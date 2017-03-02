package com.socketserver.chat.server.cleaners;

import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.server.handlers.AuthenticationHandler.ChannelStatus;
import com.socketserver.chat.server.handlers.Commons;

import io.netty.channel.Channel;

/**
 * 已经使用read_idle代替
 * @author wushenjun
 *
 */
public class CleanUnactiveChannels implements Runnable {

	
	private static final Logger logger = LoggerFactory.getLogger(CleanUnactiveChannels.class);

	@Override
	public void run() {
		
		try { // 启动延迟
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Set<Entry<Channel, ChannelStatus>> channelStatusEntries = Commons.channelStatusMap.entrySet();
		
		while(true) {
			try {
				// 清扫间隔
				Thread.sleep( SocketServerConstants.INTEVAL_UNACTIVE_CHANNEL_CLEANING );
				
				logger.info("cleaning begins, channels alive: " + channelStatusEntries.size());

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}
	}
}
