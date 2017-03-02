package com.socketserver.chat.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.VerificationCRC16;
import com.socketserver.chat.model.message.ServerSendDataMessage;
import com.socketserver.chat.model.user.SocketUser;
import com.socketserver.chat.server.handlers.AuthenticationHandler.ChannelStatus;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ServerSendDataHandler extends SimpleChannelInboundHandler<ServerSendDataMessage> {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerSendDataMessage.class);
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ServerSendDataMessage serverSendDataMessage) throws Exception {
		
		logger.info("此处为socket server发送数据到client的handler,请求类型为:{}", serverSendDataMessage.getOrderWord());
		// 往下传，查未读
		//ctx.fireChannelRead(inputPackage);
		
		if ( serverSendDataMessage.getOrderWord()==SocketServerConstants.ORDER_WORD_SERVER_SEND_DATA ) {// heart beat
			String terminalMachineId = CommonUtils.generateMachineIdFromBytes(serverSendDataMessage.getMachineId());
			SocketUser terminalUser = new SocketUser(
					0, 
					0,
					terminalMachineId
					);
			
			//TODO
			//获取到被发送消息的用户channel
			Channel terminalUserChannel = Commons.activeDeviceChannelMap.get( terminalUser );
			
			logger.info("socket server send data to user: {}, terminal channel: {}, orgin chanel:{} ",terminalUser, terminalUserChannel,ctx.channel());
			
			//设置转发数据到目标设备
			//服务器向设备发送经过CRC16算法校验的数据
			//设置为服务器主动像client端发送消息
			byte[] dataByte = serverSendDataMessage.getData();
			serverSendDataMessage.setData(dataByte);
			serverSendDataMessage.setStatus(SocketServerConstants.ORDER_EXE_STATUS_SELFSEND);
			
			// 设置channel验证成功
			ChannelStatus terminalChannelStatus = Commons.channelStatusMap.get(terminalUserChannel)!=null?Commons.channelStatusMap.get(terminalUserChannel):null;
			
			if(terminalChannelStatus!=null&&terminalChannelStatus.getToken()!=null) {
				String terminalUserToken = terminalChannelStatus.getToken();
				serverSendDataMessage.setToken(terminalUserToken.getBytes());
			} else {
				logger.info("************没有找到目标设备channel的ChannelStatus信息************");
				return;
			}
			
			ChannelStatus currentChannelStatus = Commons.channelStatusMap.get(ctx.channel());
			
			//设备id---操作账户 map
			Commons.deviceToAccountMap.put(terminalChannelStatus.getMachineId(), currentChannelStatus.getMachineId());
			logger.info("后台（win exe）发送数据到设备，发送账号为:{}， 设备id为：{}",currentChannelStatus.getMachineId(),terminalChannelStatus.getMachineId());
			 
			byte[] serverSendDataMessageBytes = serverSendDataMessage.getBytesServerSendDataMessage();
			
			//发送消息
			if(terminalUserChannel!=null) {
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
				terminalUserChannel.writeAndFlush(serverSendDataMessageBytes);
			} else {
				logger.info("************没有找到目标设备terminalUserChannel************");
				//TODO
				//ctx.channel().writeAndFlush("some message");
			}
			
			// 发送server心跳
			//ctx.channel().writeAndFlush( serverSendDataMessageBytes );
			//userConnectionRegisterService.registerUserChannelToThisServer(heartBeatingUser, ctx.channel());
			
		}
	}
}