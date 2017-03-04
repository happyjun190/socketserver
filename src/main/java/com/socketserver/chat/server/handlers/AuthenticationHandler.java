package com.socketserver.chat.server.handlers;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.VerificationCRC16;
import com.socketserver.chat.model.message.DTUDataPackage;
import com.socketserver.chat.model.message.TransmitDeviceDataToWinExeMessage;
import com.socketserver.chat.model.user.SocketUser;
import com.socketserver.chat.service.TokenCacheService;
import com.socketserver.chat.service.UserConnectionRegisterService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import redis.clients.jedis.JedisCluster;

/**
 * Handles a server-side channel.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AuthenticationHandler extends SimpleChannelInboundHandler<byte[]> {
	
	
	@Autowired
	private JedisCluster jedisCluster;
	@Autowired
	private TokenCacheService tokenCacheService;
	
	
	@Autowired
	private UserConnectionRegisterService userConnectionRegisterService;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);
	
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	
    @Override
	public void channelActive(final ChannelHandlerContext ctx) {
    	//判断是否已经有channel
		ChannelStatus channelStatus = Commons.channelStatusMap.get(ctx.channel());
		if(channelStatus==null) {
			logger.info("AuthenticationHandler-->>channelActive-->>channelStatusMap:{}",ctx.channel());
			Commons.channelStatusMap.put( ctx.channel(), new ChannelStatus( DateTime.now() ));
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
        	logger.info("read idle:" + ctx.channel());
        	//TODO
        	logger.info("read idle:{},该channel进入休眠状态", ctx.channel());
        	//ctx.close();
        }
    }
    
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
    	logger.info("channel unregistered: {}", ctx.channel());
    	userConnectionRegisterService.removeUserChannelFromThisServer(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    	logger.info( "remove connection: {}", ctx.channel());
    	userConnectionRegisterService.removeUserChannelFromThisServer(ctx.channel());
    }

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, byte[] message ) throws Exception {
		//发送  先校验再转义
		//接收  先转义再校验

		String bytesToHexString = CommonUtils.toHexString(message);
		logger.info("接收转义前,接收到的bytes 转换成 16进制字符串：{}", bytesToHexString);

		//数据报文转义
		//logger.info("接收转义前,communication package received:{}",message);
		message = CommonUtils.changeReceiveBytesDefine(message);
		bytesToHexString = CommonUtils.toHexString(message);
		//logger.info("接收转义后,communication package received:{}",message);
		logger.info("接收转义后,接收到的bytes 转换成 16进制字符串：{}", bytesToHexString);
		
		//CRC16算法校验消息是否是有效消息
		boolean verificationFlag = VerificationCRC16.doCheckByteData(message);
		
		logger.info("经过验证,verificationFlag:{},communication package received:{}",verificationFlag,message);
		
		//转换消息
		DTUDataPackage dtuDataPackage = new DTUDataPackage(message);
		//1、CRC16校验错误处理
		if(!verificationFlag) {
			logger.info("消息经过CRC16算法验证失败,返回验证失败消息,不关掉channel:{}",ctx.channel());
			//回应客户端登陆失败
			byte[] authOutBytes = sendMessage(dtuDataPackage, 
									   SocketServerConstants.ORDER_EXE_STATUS_FAILURE, 
									   dtuDataPackage.getToken());
			//发送验证(CRC16算法验证失败)失败（，完成后关闭channel--改成不关闭channel）
			ctx.writeAndFlush(authOutBytes);//
		}
		
		//2、channel库中存在channel的处理方式
		//判断是否已经有channel
		ChannelStatus channelStatus = Commons.channelStatusMap.get(ctx.channel());
		if(channelStatus==null) {
			logger.info("AuthenticationHandler-->>channelRead0-->>channelStatusMap:{}",ctx.channel());
			Commons.channelStatusMap.put( ctx.channel(), new ChannelStatus( DateTime.now() ));
		} else {
			logger.info("AuthenticationHandler-->>channelRead0-->>has channel");
		}
		
		
		//3、处理消息类型
		//DTUDataPackage dtuDataPackage = new DTUDataPackage(message);
		
		//4、只处理状态位为 0x02 的请求，服务器主动发送，也用0x02标志
		// 状态位为0x04时，需要将数据转发给windows客户端程序
		if(dtuDataPackage.getStatus()==SocketServerConstants.ORDER_EXE_STATUS_SELFSEND) {
			handlePackage(ctx, dtuDataPackage);
		}
		else {
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
		
		String machineId = CommonUtils.generateMachineIdFromBytes(dtuDataPackage.getMachineId());
		
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
				
				userConnectionRegisterService.removeUserChannelFromThisServer( userLastChannel );
				
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
			userConnectionRegisterService.removeUserChannelFromThisServer(future.channel());
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
	 * 在machine.lastconnecttime.hsetkey 这个hmap 中保存设备id对应的最近在线时间
	 * @param deviceMachineId
	 */
	private void recordMachineConnectTime(String deviceMachineId) {
		//在machine.lastconnecttime.hsetkey 这个hmap 中保存设备id对应的最近在线时间
		String systemTime = String.valueOf(System.currentTimeMillis()/1000);
		jedisCluster.hset(SocketServerConstants.MACHINE_LASTCONNECTTIME_HSETKEY, deviceMachineId, systemTime);
	}
	
	
	/**
	 * 发送验证消息
	 * @param dtuDataPackage
	 * @param status
	 * @param tokenBytes
	 */
	private static byte[] sendMessage(DTUDataPackage dtuDataPackage, byte status, byte[] tokenBytes) {
		dtuDataPackage.setToken(tokenBytes);
		dtuDataPackage.setStatus(status);
		
		byte[] authOutBytes = dtuDataPackage.getBytesFromDTUDataPackage();
		
		//发送  先校验再转义
		//接收  先转义再校验
		//发送校验
		VerificationCRC16.doCrc16CheckHighLowByte(authOutBytes);
		//发送转义
		logger.info("sendMessage 发送转义前,communication package received:{}",authOutBytes);
		authOutBytes = CommonUtils.changeSendBytesDefine(authOutBytes);
		logger.info("sendMessage 发送转义后,communication package received:{}",authOutBytes);
		
		return authOutBytes;
	}
	
	
	/**
	 * 判断是否是设备的操作
	 * @param dtuDataPackage
	 * @return
	 */
	private boolean isDeviceOption(DTUDataPackage dtuDataPackage) {
		
		//平台向设备传透数据
		if(dtuDataPackage.getOrderWord()==SocketServerConstants.ORDER_WORD_SERVER_SEND_DATA) {
			return false;
		}
		
		//终端相关操作(平台或者客户端操作设备)
		if(dtuDataPackage.getOrderType()==SocketServerConstants.ORDER_TYPE_TERMINAL) {
			return false;
		}
		
		return true;
	}
	
}