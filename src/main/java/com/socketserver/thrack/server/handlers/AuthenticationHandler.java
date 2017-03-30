package com.socketserver.thrack.server.handlers;


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
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
	private DtuDeviceDAO dtuDeviceDAO;
	@Autowired
	private InverterDeviceDAO inverterDeviceDAO;

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
		if (!CodeUtils.checkCRC((byte[]) msg)) {
		//if (CodeUtils.checkCRC((byte[]) msg)) {
			//TODO 存在粘包问题
			logger.info("此处为CRC认证失败，消息为:{}", CodeUtils.getHexString((byte[]) msg));
			return;
		} else {
			//5、处理数据
			//logger.info("此处为CRC认证成功，下传数据，不关闭channel");
			ctx.fireChannelRead(msg);
			return;
		}

	}



}