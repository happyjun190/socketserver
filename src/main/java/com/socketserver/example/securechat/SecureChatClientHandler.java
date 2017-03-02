package com.socketserver.example.securechat;

import com.socketserver.chat.model.message.DTUDataPackage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a client-side channel.
 */
public class SecureChatClientHandler extends SimpleChannelInboundHandler<byte[]> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg)
			throws Exception {
		DTUDataPackage dtuDataPackage = new DTUDataPackage(msg);
		System.err.println(new String(dtuDataPackage.getData()));
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}