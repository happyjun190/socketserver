package com.socketserver.example.securechat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Simple SSL thrack client modified from {@link}.
 */
public final class SecureChatClient {

	static final String HOST = System.getProperty("host", "127.0.0.1");
	//static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    private static final Logger logger = LoggerFactory.getLogger(SecureChatClient.class);
    
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        //final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new SecureChatClientInitializer());
             //.handler(new SecureChatClientInitializer(sslCtx));

            // Start the connection attempt.
            Channel ch = b.connect(HOST, PORT).sync().channel();
            // { "comtype":6, "i":9999, "t":1, "os":"i", "v":"1.0.0",  "data":{ "token":"73b7ca1477ab4a3481727c24bc4fb0a5" } }
//            ch.writeAndFlush(
//            		"{ \"comtype\":1, \"i\":10, \"si\":43, \"di\":\"b7b9eb51a9c6ea4db352af33bb2385ec267243f5\", \"t\":3, \"data\":{ \"token\":\"d0a03554470e43269390d4670897c87a\" } } \r\n");
//            
//            ch.writeAndFlush(
//            		"{\"comtype\":3,\"i\":10,\"t\":3,\"si\":43,\"di\":\"b7b9eb51a9c6ea4db352af33bb2385ec267243f5\",\"data\":{\"msgtype\":15,\"msgid\":\"AF3ABE6E-75E8-4B52-9CF8-C49FDAA3C47D\",\"on\":1,\"s\":1,\"c\":1}} \r\n");
//            
            // Read commands from the stdin.
            
            /*ch.writeAndFlush("0x010x0f0x090x0f0x0f0x0e0e000a0b\r\n");
            ch.writeAndFlush("0x020x0f0x090x0f0x0f0x0e0e000a0b\r\n");
            ch.writeAndFlush("0x030x0f0x090x0f0x0f0x0e0e000a0b\r\n");
            ch.writeAndFlush("0x040x0f0x090x0f0x0f0x0e0e000a0b\r\n");
            ch.writeAndFlush("0x050x0f0x090x0f0x0f0x0e0e000a0b\r\n");
            ch.writeAndFlush("0x060x0f0x090x0f0x0f0x0e0e000a0b\r\n");*/

            //服务器主动推送消息
            /*byte[] message = {85, 35, 0, (byte) 0xAA, (byte) 0xFF, -66, 116, 0, 0, (byte)0x02,
            				  //0, 0, 0, 12, 41, 97, 99, -45, 80, 87,
            				  0, 0, 0, 12, 41, 97, 99, -45,
            				  0, 0, 0, 0, 0, 0, 0, 0,
            				  0, 0, -86};*/
            //登陆消息
            byte[] message1 = {(byte)0xAA, (byte)0xBB, (byte)0xCC};
            byte[] message2 = {(byte)0x30, (byte)0x30};
            byte[] message3 = {(byte)0x01, (byte)0x03, (byte)0x01, (byte)0x03, (byte)0x01, (byte)0x03};


            int index = 0;

			//System.out.println(serverReceiveOutMessageBytes);

            ChannelFuture lastWriteFuture = ch.writeAndFlush(message1);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                if(index%3==1) {
                    lastWriteFuture = ch.writeAndFlush(message1);
                } else if(index%2==3) {
                    lastWriteFuture = ch.writeAndFlush(message2);
                } else {
                    lastWriteFuture = ch.writeAndFlush(message3);
                }

                index++;
                logger.info("this is the client test for heartbeat and auth, and index is {}", index);
                // Sends the received line to the server.
                //lastWriteFuture = ch.writeAndFlush(line + "\0");

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }
            
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
            
            /*ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // Sends the received line to the server.
                //lastWriteFuture = ch.writeAndFlush(line + "\0");
                lastWriteFuture = ch.writeAndFlush(line);
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    ch.closeFuture().sync();
                    break;
                }
            }
             
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
            */
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }
}
