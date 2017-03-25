package com.socketserver.thrack.server.cleaners;

import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
		while(true) {
			try {
				// 清扫间隔
				Thread.sleep( Constants.INTEVAL_UNACTIVE_CHANNEL_CLEANING );
				
				logger.info("cleaning begins, channels alive: " + ClientMap.mapKey.size());

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}
	}
}
