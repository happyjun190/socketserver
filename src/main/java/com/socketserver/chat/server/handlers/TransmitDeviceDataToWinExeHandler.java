package com.socketserver.chat.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.VerificationCRC16;
import com.socketserver.chat.model.message.TransmitDeviceDataToWinExeMessage;
import com.socketserver.chat.model.user.SocketUser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 状态位为0x04时，需要将数据转发给windows客户端程序
 * @author shenjun
 * 2016/12/10
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TransmitDeviceDataToWinExeHandler extends SimpleChannelInboundHandler<TransmitDeviceDataToWinExeMessage> {
	
	private static final Logger logger = LoggerFactory.getLogger(TransmitDeviceDataToWinExeHandler.class);
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TransmitDeviceDataToWinExeMessage transmitDeviceDataToWinExeMessage) throws Exception {
		
		logger.info("将数据转发给windows客户端程序的handler,收到客户端的请求，请求类型为:{}", transmitDeviceDataToWinExeMessage.getOrderWord());
		// 往下传，查未读		
		//ctx.fireChannelRead(inputPackage);
		
		String machineId = CommonUtils.generateMachineIdFromBytes(transmitDeviceDataToWinExeMessage.getMachineId());
		
		//设备id---操作账户 map
		String terminalAccount = Commons.deviceToAccountMap.get(machineId);
		
		SocketUser terminalAccountUser = new SocketUser(
				0, 
				0,
				terminalAccount
				);
		logger.info("heart beat user: " + terminalAccountUser + ", channel: " + ctx.channel());
		
		
		//获取到被发送消息的用户channel
		Channel terminalAccountUserChannel = Commons.activeDeviceChannelMap.get( terminalAccountUser );
		
		//设置返回数据
		byte[] transmitDeviceDataToWinExeMessageBytes = transmitDeviceDataToWinExeMessage.getBytesFromTransmitDeviceDataToWinexeMessage();
		
		//转发数据
		if(terminalAccountUserChannel!=null) {
			//发送  先校验再转义
			//接收  先转义再校验
			
			//发送校验
			VerificationCRC16.doCrc16CheckHighLowByte(transmitDeviceDataToWinExeMessageBytes);
			//先转义再校验
			//发送转义
			String bytesToHexString = CommonUtils.toHexString(transmitDeviceDataToWinExeMessageBytes);
			logger.info("发送转义前1,communication package send:{}",bytesToHexString);
			transmitDeviceDataToWinExeMessageBytes = CommonUtils.changeSendBytesDefine(transmitDeviceDataToWinExeMessageBytes);
			bytesToHexString = CommonUtils.toHexString(transmitDeviceDataToWinExeMessageBytes);
			logger.info("发送转义后1,communication package send:{}",bytesToHexString);
			terminalAccountUserChannel.writeAndFlush( transmitDeviceDataToWinExeMessageBytes );
		} else {
			transmitDeviceDataToWinExeMessage.setStatus(SocketServerConstants.ORDER_EXE_STATUS_FAILURE);
			byte[] failureBytes = transmitDeviceDataToWinExeMessage.getBytesFromTransmitDeviceDataToWinexeMessage();
			//发送校验
			VerificationCRC16.doCrc16CheckHighLowByte(failureBytes);
			//先转义再校验
			//发送转义
			String bytesToHexString = CommonUtils.toHexString(failureBytes);
			logger.info("发送转义前2,communication package send:{}",bytesToHexString);
			failureBytes = CommonUtils.changeSendBytesDefine(failureBytes);
			bytesToHexString = CommonUtils.toHexString(failureBytes);
			logger.info("发送转义后2,communication package send:{}",bytesToHexString);
			ctx.channel().writeAndFlush(failureBytes);
		}
		
			
	}
}