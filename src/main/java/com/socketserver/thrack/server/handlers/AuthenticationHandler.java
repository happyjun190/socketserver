package com.socketserver.thrack.server.handlers;


import java.io.IOException;
import java.util.Map;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.dao.DtuDeviceDAO;
import com.socketserver.thrack.dao.InverterDeviceDAO;
import com.socketserver.thrack.model.device.TabDtuDevice;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import io.netty.channel.*;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.socketserver.thrack.commons.SocketServerConstants;
import com.socketserver.thrack.model.message.DTUDataPackage;
import com.socketserver.thrack.model.user.SocketUser;
import com.socketserver.thrack.service.TokenCacheService;
import com.socketserver.thrack.service.UserConnectionRegisterService;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import redis.clients.jedis.JedisCluster;

/**
 * Handles a server-side channel.
 * 权限认证及相关处理Handler
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AuthenticationHandler extends ChannelInboundHandlerAdapter {
	

	@Autowired
	private JedisCluster jedisCluster;
	@Autowired
	private TokenCacheService tokenCacheService;
	@Autowired
	private DtuDeviceDAO dtuDeviceDAO;
	@Autowired
	private InverterDeviceDAO inverterDeviceDAO;


	@Autowired
	private UserConnectionRegisterService userConnectionRegisterService;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	
    @Override
	public void channelActive(final ChannelHandlerContext ctx) {
    	//判断是否已经有channel
		Client client = ClientMap.getClient(ctx.channel());
		if(client==null) {
			logger.info("AuthenticationHandler-->>channelActive-->>ClientMap.addClient:{}",ctx.channel());
			ClientMap.addClient(ctx.channel(), client);
		} else {
			logger.info("AuthenticationHandler-->>channelActive-->>has channel");
		}
	}
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (!(evt instanceof IdleStateEvent)) {
            return;
        }

        IdleStateEvent e = (IdleStateEvent) evt;
        if (e.state() == IdleState.READER_IDLE) {
            // The connection was OK but there was no traffic for last period.
        	logger.info("read idle:{}, then close this channel " + ctx.channel());
			Commons.removeCloseChannel(ctx.channel());
        }
    }
    
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
    	logger.info("channel unregistered: {}", ctx.channel());
    	//userConnectionRegisterService.removeChannelFromThisServer(ctx.channel());
		Commons.removeCloseChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    	logger.info( "remove connection: {}", ctx.channel());
		Commons.removeCloseChannel(ctx.channel());
    }

	@Override
	public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
		logger.info("ChannelInboundHandlerAdapter:{}",msg.toString());

		//1、查看是否存在channel,如果存在，则进行第4步骤crc算法校验，如果不存在，查看数据库中是否存在校验串
		String authKey = CodeUtils.getHexStringNoBlank((byte[]) msg);
		Client client = ClientMap.getClient(ctx.channel());
		//channel还未认证
		if(client==null||client.getStatus()== Client.Status.INIT) {//TODO
			//auth
			TabDtuDevice tabDtuDevice = dtuDeviceDAO.getDtuDeviceByAuthKey(authKey);
			//验证不通过
			if(tabDtuDevice==null) {
				ctx.close();
			} else {
				//验证通过
				//设置dtu客户端的设备信息
				Map<String, ClientInverterStats> inverterStatsMap = inverterDeviceDAO.getInverterStatsMap(tabDtuDevice.getId());
				client = new Client(authKey, ctx.channel(), inverterStatsMap);
				client.touchSession(Client.Status.AUTH);
				ClientMap.addClient(ctx.channel(), client);
			}
			return;
		}


		//2、保存channel到双向map中


		//3、处理心跳数据 0x3030(16进制)=00(字符串)
		if(authKey.equals(Constants.HEART_BEAT_MSG)) {
			ctx.fireChannelRead(msg);
			return;
		}

		//4、crc算法校验
		//TODO 需要修改if判断，现在测试不用crc16算法
		//if (!CodeUtils.checkCRC((byte[]) msg)) {
		if (CodeUtils.checkCRC((byte[]) msg)) {
			logger.info("此处为权限认证失败，返回，不关闭channel");
			return;
		} else {
			//5、处理数据
			logger.info("此处为权限认证成功，下传数据，不关闭channel");
			ctx.fireChannelRead(msg);
			return;
		}

	}

	
	/**
	 * 处理数据包,发送给MessageHandler处理
	 * @param ctx
	 * @param dtuDataPackage
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private void handlePackage(ChannelHandlerContext ctx, DTUDataPackage dtuDataPackage) throws JsonParseException, JsonMappingException, IOException  {
		ctx.fireChannelRead(dtuDataPackage);
	}


	/**
	 * 将当前channel的设备信息注册到服务器
	 * @param ctx
	 * @param dtuDataPackage
	 */
	private void registerMachineToServer( final ChannelHandlerContext ctx, DTUDataPackage dtuDataPackage) {

		String machineId = "0";//CommonUtils.generateMachineIdFromBytes(dtuDataPackage.getMachineId());

		//1、设置登陆用户
		SocketUser authUser = new SocketUser(
				0,
				0,
				machineId
				);

		logger.info("auth received, user: {}, channel: {}", authUser, ctx.channel());


		//2、生成token
		String token = null;
		String existToken = null;
		//判断token是否存在，并且是否有效(防止有token验证时，redis中生成过多的key)
		if(dtuDataPackage.getToken()!=null) {
			existToken = new String(dtuDataPackage.getToken());
		}

		//生成token
		token = tokenCacheService.generateMachineToken(machineId, existToken);
		logger.info("连接到socketserver 的machine 为 : {}, token:{}", machineId, token);


		//3、设置返回数据
		//服务器向设备发送经过CRC16算法校验的数据
		//DTUDataPackage outDTUDataPackage = dtuDataPackage;
		byte[] authOutBytes;

		//4、判断是否登陆通过
		if(StringUtils.isNotBlank(token)){//判断是否有token
			logger.info("auth succeded, registering user: " + authUser);
			//4.1、向user原有channel发送踢掉(若不相同)
			Channel userLastChannel = Commons.activeDeviceChannelMap.get( authUser );
			if( userLastChannel != null && !userLastChannel.equals(ctx.channel())) {
				// 向原channel发送踢掉通知，不接受新channel验证
				logger.info("user has different last channel remaining : {}", userLastChannel);

				userConnectionRegisterService.removeChannelFromThisServer( userLastChannel );

				//回应客户端登陆失败
				authOutBytes = sendMessage(dtuDataPackage,
										   SocketServerConstants.ORDER_EXE_STATUS_FAILURE,
										   dtuDataPackage.getToken());
				// 发送验证失败，完成后关闭channel
				ctx.writeAndFlush(authOutBytes).addListener( closeChannelListener );
				return;
			}
			//4.2 判断通过map中是否存在当前通道
			if( Commons.activeDeviceChannelMap.containsValue( ctx.channel()) ) {
				// channel 验证用户改变
				logger.info("auth succeded but user changed.");
				Commons.activeDeviceChannelMap.inverse().remove( ctx.channel() );
			}


			//4.3、 注册这个用户
			userConnectionRegisterService.registerDeviceChannelToThisServer(authUser, ctx.channel());

			// 设置channel验证成功
			ChannelStatus channelStatus = Commons.channelStatusMap.get(ctx.channel());
			logger.info("权限验证时获取channelStatus:{}",channelStatus);
			channelStatus.setAuthenticated(true);
			//设备类型
			channelStatus.setType(1);
			channelStatus.setMachineId(machineId);


			//设置token，主动发送数据需要
			channelStatus.setToken(token);

			//4.4  返回功验证
			authOutBytes = sendMessage(dtuDataPackage,
									   SocketServerConstants.ORDER_EXE_STATUS_SUCCESS,
									   token.getBytes());

			logger.info("xxxxxxxxxx此处为登陆成功xxxxxxxxxxx，发送消息:{}",authOutBytes);
			ctx.channel().writeAndFlush(authOutBytes);

		} else {
			//回应客户端登陆失败
			authOutBytes = sendMessage(dtuDataPackage,
									   SocketServerConstants.ORDER_EXE_STATUS_FAILURE,
									   dtuDataPackage.getToken());
			// 发送验证失败，完成后关闭channel
			ctx.writeAndFlush(authOutBytes).addListener( closeChannelListener );
			return;
		}
	}



	private ChannelFutureListener closeChannelListener = new ChannelFutureListener() {
		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			userConnectionRegisterService.removeChannelFromThisServer(future.channel());
		}
	};
	
	public static class ChannelStatus {
		
		private Boolean authenticated;
		private String token;
		private String machineId;
		private int type;//0 初始值 1 设备  2 用户账号
		
		public ChannelStatus( DateTime establishTime ) {
			this.authenticated = false;
		}

		public Boolean isAuthenticated() {
			return authenticated;
		}

		public void setAuthenticated(Boolean isAuthenticated) {
			this.authenticated = isAuthenticated;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
		public String getMachineId() {
			return machineId;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
		
	}
	

	/**
	 * 发送验证消息
	 * @param dtuDataPackage
	 * @param status
	 * @param tokenBytes
	 */
	private static byte[] sendMessage(DTUDataPackage dtuDataPackage, byte status, byte[] tokenBytes) {
		return null;
	}

}