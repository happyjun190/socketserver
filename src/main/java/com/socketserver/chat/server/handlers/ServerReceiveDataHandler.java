package com.socketserver.chat.server.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.VerificationCRC16;
import com.socketserver.chat.model.message.ServerReceiveDataMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ServerReceiveDataHandler extends SimpleChannelInboundHandler<ServerReceiveDataMessage> {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerReceiveDataMessage.class);
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	logger.error("exception", cause);
    }

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ServerReceiveDataMessage serverReceiveDataMessage) throws Exception {
		
		logger.info("此处为socket server 接收处理 client 发送数据 的handler,收到客户端的请求，请求类型为:{}", serverReceiveDataMessage.getOrderWord());
		// 往下传，查未读		
		//ctx.fireChannelRead(inputPackage);
		
		if ( serverReceiveDataMessage.getOrderWord()==SocketServerConstants.ORDER_WORD_SERVER_REVEIVE_DATA ) {// heart beat
			//String machineId = CommonUtils.generateMachineIdFromBytes(serverReceiveDataMessage.getMachineId());
			/*SocketUser heartBeatingUser = new SocketUser(
					0, 
					0,
					machineId
					);*/
			//logger.info("heart beat user: " + heartBeatingUser + ", channel: " + ctx.channel());
			
			if(serverReceiveDataMessage.getData()!=null) {
				String data = new String(serverReceiveDataMessage.getData());
				logger.info("*****************接收到客户端发过来的数据,数据内容为:{},byte数据为:{}",data,serverReceiveDataMessage.getData());
			}
			
			//logger.info("*****************接收到客户端发过来的数据,所有消息内容为:{}",serverReceiveDataMessage.getBytesFromServerReceiveDataMessage());;
			//设置返回数据
			//服务器向设备发送经过CRC16算法校验的数据
			serverReceiveDataMessage.setStatus(SocketServerConstants.ORDER_EXE_STATUS_SUCCESS);
			byte[] serverReceiveOutMessageBytes = serverReceiveDataMessage.getBytesFromServerReceiveDataMessage();
			
			//发送  先校验再转义
			//接收  先转义再校验
			
			//发送校验
			VerificationCRC16.doCrc16CheckHighLowByte(serverReceiveOutMessageBytes);
			
			//发送转义
			logger.info("发送转义前,communication package received:{}",serverReceiveOutMessageBytes);
			serverReceiveOutMessageBytes = CommonUtils.changeSendBytesDefine(serverReceiveOutMessageBytes);
			logger.info("发送转义后,communication package received:{}",serverReceiveOutMessageBytes);
			
			
			// 发送接收数据成功的消息
			ctx.channel().writeAndFlush( serverReceiveOutMessageBytes );
			
		}
	}
}