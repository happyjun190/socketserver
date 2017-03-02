package com.socketserver.chat.server.handlers;

import com.socketserver.chat.model.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.VerificationCRC16;
import com.socketserver.chat.model.user.SocketUser;
import com.socketserver.chat.server.handlers.AuthenticationHandler.ChannelStatus;
import com.socketserver.chat.service.TokenCacheService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * message集中处理器
 * @author shenjun
 * 2016/10/31
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MessageHandler extends SimpleChannelInboundHandler<DTUDataPackage> {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
	
	@Autowired
	private TokenCacheService tokenCacheService;
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    }

	@Override
	protected void channelRead0( ChannelHandlerContext ctx, DTUDataPackage dtuDataPackage) throws Exception {
		//验证token有效性
		boolean isValid = tokenCacheService.checkTokenIsValid(dtuDataPackage);
		
		logger.info("*****************MessageHandler接收到客户端发过来的数据,token验证:{},所有消息内容为:{}",isValid,dtuDataPackage.getBytesFromDTUDataPackage());
		
		switch(dtuDataPackage.getOrderType()) {
			case SocketServerConstants.ORDER_TYPE_LINK://链路操作
				handleLinkPackage(ctx, dtuDataPackage);
				break;
			case SocketServerConstants.ORDER_TYPE_DATATRANSFORM://数据传输
				handleDataTransformPackage(ctx, dtuDataPackage);
				break;
			case SocketServerConstants.ORDER_TYPE_TERMINAL://终端相关操作
				handleTerminalPackage(ctx, dtuDataPackage);
				break;
			default:
				return;
		}
	}
	
	
	/**
	 * 处理链路操作
	 */
	private void handleLinkPackage(ChannelHandlerContext ctx, DTUDataPackage dtuDataPackage) {
		if(dtuDataPackage.getOrderWord()==SocketServerConstants.ORDER_WORD_HEARTBEAT||
				dtuDataPackage.getOrderWord()==SocketServerConstants.ORDER_WORD_WINDOWSCLIENT_HEARTBEAT) {//链路操作-心跳数据包
			byte[] heartBeatMessageBytes = dtuDataPackage.getBytesFromDTUDataPackage();
			HeartBeatMessage heartBeatMessage = new HeartBeatMessage(heartBeatMessageBytes);
			ctx.fireChannelRead( heartBeatMessage );
		} else {
			//TODO
			return;
		}
		
	}
	
	
	/**
	 * 处理数据传输
	 */
	private void handleDataTransformPackage(ChannelHandlerContext ctx, DTUDataPackage dtuDataPackage) {
		byte[] serverReceiveDataMessageBytes = dtuDataPackage.getBytesFromDTUDataPackage();
		if(dtuDataPackage.getOrderWord()==SocketServerConstants.ORDER_WORD_SERVER_REVEIVE_DATA) {//数据传输-设备向平台透传数据
			//此处转发设备接收到数据透传后发出来的信息
			TransmitDeviceDataToWinExeMessage transmitDeviceDataToWinexeMessage = new TransmitDeviceDataToWinExeMessage(serverReceiveDataMessageBytes);
			ctx.fireChannelRead(transmitDeviceDataToWinexeMessage);
			/*ServerReceiveDataMessage serverReceiveDataMessage = new ServerReceiveDataMessage(serverReceiveDataMessageBytes);
			ctx.fireChannelRead( serverReceiveDataMessage );*/
		} else if(dtuDataPackage.getOrderWord()==SocketServerConstants.ORDER_WORD_SERVER_SEND_DATA) {//数据传输-平台向设备透传数据
			ServerSendDataMessage serverSendDataMessage = new ServerSendDataMessage(serverReceiveDataMessageBytes);
			ctx.fireChannelRead( serverSendDataMessage );
		} else {
			//TODO
			return;
		}
	}
	
	/**
	 * 处理终端相关操作
	 */
	private void handleTerminalPackage(ChannelHandlerContext ctx, DTUDataPackage dtuDataPackage) {
		
		String terminalMachineId = CommonUtils.generateMachineIdFromBytes(dtuDataPackage.getMachineId());
		SocketUser terminalUser = new SocketUser(
				0, 
				0,
				terminalMachineId
				);
		
		//TODO
		//获取到被发送消息的用户channel
		Channel terminalUserChannel = Commons.activeDeviceChannelMap.get( terminalUser );
		
		logger.info("socket server send data to user: {}, terminal channel: {}, orgin chanel:{} ",terminalUser, terminalUserChannel,ctx.channel());
		
		//设置返回数据
		//服务器向设备发送经过CRC16算法校验的数据
		//设置为服务器主动像client端发送消息
		//byte[] dataByte = {49, 58, (byte)0xAA, 51, 52, (byte)0xA9,53, 54,(byte)0xAA};
		//dtuDataPackage.setData(dataByte);
		dtuDataPackage.setData(null);
		dtuDataPackage.setStatus(SocketServerConstants.ORDER_EXE_STATUS_SELFSEND);
		
		// 设置channel验证成功
		ChannelStatus channelStatus = Commons.channelStatusMap.get(terminalUserChannel);
		
		if(channelStatus!=null&&channelStatus.getToken()!=null) {
			String terminalUserToken = channelStatus.getToken();
			dtuDataPackage.setToken(terminalUserToken.getBytes());
		} else {
			return;
		}
		
		byte[] serverSendDataMessageBytes = dtuDataPackage.getBytesFromDTUDataPackage();
		
		//发送  先校验再转义
		//接收  先转义再校验
		//发送校验
		VerificationCRC16.doCrc16CheckHighLowByte(serverSendDataMessageBytes);
		//发送转义
		String bytesToHexString = CommonUtils.toHexString(serverSendDataMessageBytes);
		logger.info("发送转义前,communication package send:{}",bytesToHexString);
		serverSendDataMessageBytes = CommonUtils.changeSendBytesDefine(serverSendDataMessageBytes);
		bytesToHexString = CommonUtils.toHexString(serverSendDataMessageBytes);
		logger.info("发送转义后,communication package send:{}",bytesToHexString);
		
		//发送消息
		if(terminalUserChannel!=null) {
			terminalUserChannel.writeAndFlush(serverSendDataMessageBytes);
		} else {
			//TODO
			//ctx.channel().writeAndFlush("some message");
		}
		
		
	}
	
}
