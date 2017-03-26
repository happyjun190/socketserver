package com.socketserver.thrack.server;

import com.socketserver.thrack.server.client.Constants;
import com.socketserver.thrack.server.handlers.HeartBeatHandler;
import com.socketserver.thrack.server.handlers.InverterDataHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.socketserver.thrack.server.handlers.AuthenticationHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
@Component
public class InverterDataCollectServerInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired 
	private BeanFactory beanFactory; 
	
	private final NioEventLoopGroup heartBeatHandlerGroup;
    private final NioEventLoopGroup inverterDataHandlerGroup;

    public InverterDataCollectServerInitializer() throws Exception {
    	
    	heartBeatHandlerGroup = new NioEventLoopGroup();
        inverterDataHandlerGroup = new NioEventLoopGroup();
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        //接收时,将数据转换为byte数组
        pipeline.addLast(new ByteArrayDecoder());
        
        //发送时,将数据转换为byte数组
        pipeline.addLast(new ByteArrayEncoder());
        
        // and then business logic.
        //1、IDEL+验证数据(CRC16算法)+消息基本处理
        pipeline.addLast(new IdleStateHandler(Constants.READ_IDLE_TIMEOUT_IN_SECONDS,
                                             0,
                                             0),
                                              beanFactory.getBean(AuthenticationHandler.class));
        // 以下handlers包含阻塞操作，使用独立的eventGroup处理
        // use factory to get new beans
        pipeline.addLast(heartBeatHandlerGroup, beanFactory.getBean(HeartBeatHandler.class));
        pipeline.addLast(inverterDataHandlerGroup, beanFactory.getBean(InverterDataHandler.class));

        // 以下handlers包含阻塞操作，使用独立的eventGroup处理
        // use factory to get new beans
    }
}
