package com.socketserver.thrack.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestEncoder extends ChannelOutboundHandlerAdapter {
	private static final Log logger = LogFactory.getLog(RequestEncoder.class.getName());

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		logger.info("RequestEncoder write , msg=" + msg);
		ctx.write(msg, promise);
	}
}