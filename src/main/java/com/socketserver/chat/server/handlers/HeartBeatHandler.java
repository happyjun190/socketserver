package com.socketserver.chat.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.VerificationCRC16;
import com.socketserver.chat.model.message.HeartBeatMessage;
import com.socketserver.chat.model.user.SocketUser;
import com.socketserver.chat.service.UserConnectionRegisterService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HeartBeatHandler extends SimpleChannelInboundHandler<HeartBeatMessage> {
	
	@Autowired
	private UserConnectionRegisterService userConnectionRegisterService;
	
	private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HeartBeatMessage heartBeatMessage) throws Exception {
		
		logger.info("此处为处理心跳的handler,收到客户端的请求，请求类型为:{}", heartBeatMessage.getOrderWord());
		// 往下传，查未读		
		//ctx.fireChannelRead(inputPackage);
		
		if ( heartBeatMessage.getOrderWord()==SocketServerConstants.ORDER_WORD_HEARTBEAT || 
				heartBeatMessage.getOrderWord()==SocketServerConstants.ORDER_WORD_WINDOWSCLIENT_HEARTBEAT ) {// heart beat
			String machineId = CommonUtils.generateMachineIdFromBytes(heartBeatMessage.getMachineId());
			SocketUser heartBeatingUser = new SocketUser(
					0, 
					0,
					machineId
					);
			logger.info("heart beat user: " + heartBeatingUser + ", channel: " + ctx.channel());
			
			
			//设置返回数据
			//服务器向设备发送经过CRC16算法校验的数据
			heartBeatMessage.setStatus(SocketServerConstants.ORDER_EXE_STATUS_SUCCESS);
			byte[] heartBeatMessageBytes = heartBeatMessage.getBytesFromHeartBeatMessage();
			
			//发送  先校验再转义
			//接收  先转义再校验
			
			//发送校验
			VerificationCRC16.doCrc16CheckHighLowByte(heartBeatMessageBytes);
			
			//先转义再校验
			//发送转义
			String bytesToHexString = CommonUtils.toHexString(heartBeatMessageBytes);
			logger.info("发送转义前,communication package send:{}",bytesToHexString);
			heartBeatMessageBytes = CommonUtils.changeSendBytesDefine(heartBeatMessageBytes);
			bytesToHexString = CommonUtils.toHexString(heartBeatMessageBytes);
			logger.info("发送转义后,communication package send:{}",bytesToHexString);
			
			
			// 发送server心跳
			ctx.channel().writeAndFlush( heartBeatMessageBytes );
			//只有设备发送心跳才会记录
			if(heartBeatMessage.getOrderWord()==SocketServerConstants.ORDER_WORD_HEARTBEAT) {
				userConnectionRegisterService.registerDeviceChannelToThisServer(heartBeatingUser, ctx.channel());
			}
			
			
		}
	}
}