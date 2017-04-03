package com.socketserver.example.securechat;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.server.client.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles a client-side channel.
 */
public class SecureChatClientHandler extends ChannelInboundHandlerAdapter {

	private final static Logger logger = LoggerFactory.getLogger(SecureChatClientHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte[] message = (byte[]) msg;
		String messageToStr = CodeUtils.getHexString(message);

		byte[] inverterAddr = {message[0]};
		String inverterAddrStr = CodeUtils.getHexStringNoBlank(inverterAddr);

		byte[] readAddressBytes = {message[2], message[3]};
		String readAddress = CodeUtils.getHexStringNoBlank(readAddressBytes);

		logger.info("客户端收到的消息为：{}, readAddress：{}", messageToStr, readAddress);

		byte[] responseBytes = null;
		switch (readAddress) {
			case Constants.ADDR_1600:
				responseBytes = M1600;
				break;
			case Constants.ADDR_1616:
				responseBytes = M1616;
				break;
			case Constants.ADDR_1652:
				responseBytes = M1652;
				break;
			case Constants.ADDR_1670:
				responseBytes = M1670;
				break;
			case Constants.ADDR_168E:
				responseBytes = M168E;
				break;
			case Constants.ADDR_1690:
				responseBytes = M1690;
				break;
			case Constants.ADDR_1800:
				responseBytes = M1800;
				break;
			default:
				break;
		}
		responseBytes[0] = message[0];
		byte[] bcrc = CodeUtils.crc16(responseBytes, responseBytes.length-2);//length-2 因为加上了CRC高低位
		responseBytes[responseBytes.length-2] = bcrc[0];
		responseBytes[responseBytes.length-1] = bcrc[1];
		ctx.writeAndFlush(responseBytes);
		logger.info("客户端发送的消息为：{}， inverterAddrStr：{}", CodeUtils.getHexString(responseBytes), inverterAddrStr);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}


	private final static byte[] M1600 = {
			0x04, 0x03, 28,
			0x16, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			(byte) 0xA4, (byte) 0xA5,(byte) 0xA6, (byte) 0xA7, (byte) 0xA8, (byte) 0xA9, (byte) 0xAA, (byte) 0xAB,
			0x00, 0x00
	};
	private final static byte[] M1616 = {
			0x04, 0x03, 20,
			0x16, 0x16, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			0x00, 0x00
	};
	private final static byte[] M1652 = {
			0x04, 0x03, 20,
            0x16, 0x52, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			0x00, 0x00
	};
	private final static byte[] M1670 = {
			0x04, 0x03, 28,
            0x16, 0x70, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x01, 0x02, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x01, 0x02, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
			0x00, 0x00
	};
	private final static byte[] M168E = {
			0x04, 0x03, 0x00,
            0x16, (byte) 0x8E, 0x02, 0x03,
			0x00, 0x00
	};
	private final static byte[] M1690 = {
			0x04, 0x03, 64,
            0x16, (byte) 0x90, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, (byte) 0xA0, (byte) 0xA1, (byte) 0xA2, (byte) 0xA3,
			0x00, 0x01, 0x02, 0x03,
			0x00, 0x00
	};
	private final static byte[] M1800 = {
			0x04, 0x03, (byte) 160,
            0x18, 0x00, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
			0x00, 0x00
	};
}