package com.socketserver.chat.server;

import com.socketserver.chat.server.cleaners.CleanUnactiveChannels;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SecureChatServer {
	
	static final int PORT = 8992;
	
//	@Autowired
//	private ApnsPushService apnsPushService;
	
	@Autowired
	private SecureChatServerInitializer serverInitializer;
	
	private void start(String[] args) throws Exception {

//		apnsPushService.push("hello world", "69ce15ef0e00f176abdbc1ddd2c0ff864f95d42089f163a3dcea7ab74978b438", false, 3, "0");
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			
			new Thread(new CleanUnactiveChannels()).start();
			
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler( serverInitializer );

			b.bind(PORT).sync().channel().closeFuture().sync();
			
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
    }
	
	// start server via Spring ApplicationContext to enable Spring IoC
	public static void main(String[] args) throws Exception {

		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		SecureChatServer server = context.getBean(SecureChatServer.class);
		server.start(args);
		
		context.close();
		
	}
}
