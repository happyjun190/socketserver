package com.socketserver.example.securechat;

import com.socketserver.chat.commons.SocketServerConstants;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SecureChatClientInitializer extends ChannelInitializer<SocketChannel> {

    //private final SslContext sslCtx;

    //public SecureChatClientInitializer(SslContext sslCtx) {
	public SecureChatClientInitializer() {
        //this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        //pipeline.addLast(sslCtx.newHandler(ch.alloc(), SecureChatClient.HOST, SecureChatClient.PORT));

        // On top of the SSL handler, add the text line codec.
        /*pipeline.addLast(new DelimiterBasedFrameDecoder(SocketServerConstants.MAX_ACCEPTED_FRAME_LENGTH, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());*/

      //接收时,将数据转换为byte数组
        pipeline.addLast(new ByteArrayDecoder());
        
        //发送时,将数据转换为byte数组
        pipeline.addLast(new ByteArrayEncoder());
        
        // and then business logic.
        pipeline.addLast(new SecureChatClientHandler());
    }
}