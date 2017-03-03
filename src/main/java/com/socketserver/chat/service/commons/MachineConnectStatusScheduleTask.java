/*
package com.socketserver.chat.service.commons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.dao.UserDeviceDAO;
import com.socketserver.chat.model.userdevice.TabUserDevice;

import redis.clients.jedis.JedisCluster;


@Service
public class MachineConnectStatusScheduleTask {
	
	private static final Logger logger = LoggerFactory.getLogger(MachineConnectStatusScheduleTask.class);
	private static final int MAX_NO_CONNECT_TIME = 180;//设置最大未链接时间，修改设备状态
	
	@Autowired
	private JedisCluster jedisCluster;
	@Autowired
	private UserDeviceDAO userDeviceDAO;
	
	//TODO FIXME
	// 每1分钟执行一次
	@Transactional
	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void sendSMSToProPic() {
		String systemTime = String.valueOf(System.currentTimeMillis()/1000);
		int nowTimeSeconds = Integer.parseInt(systemTime);
		Map<String, String> machineIdLastConnectTimeMap = jedisCluster.hgetAll(SocketServerConstants.MACHINE_LASTCONNECTTIME_HSETKEY);
		List<String> stopingUserDeviceList = new ArrayList<String>();
		List<String> runningUserDeviceList = new ArrayList<String>();
		//存在machineId -- lastConnectTime 的map
		if(machineIdLastConnectTimeMap!=null) {
			String lastConnectTime;
			Date date;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(String machineIdKey:machineIdLastConnectTimeMap.keySet()) {
				lastConnectTime = machineIdLastConnectTimeMap.get(machineIdKey);
				if(nowTimeSeconds-Integer.valueOf(lastConnectTime)>MAX_NO_CONNECT_TIME) {
					stopingUserDeviceList.add(machineIdKey);
					jedisCluster.hdel(SocketServerConstants.MACHINE_LASTCONNECTTIME_HSETKEY, machineIdKey);
				} else {
					runningUserDeviceList.add(machineIdKey);
				}
				date = new Date(Long.valueOf(lastConnectTime+"000"));
				logger.info("机器最后连接socket server的时间, machineId:{},lastConnectTime:{}",machineIdKey,format.format(date));
			}
			
			
			//批量更新
			if(!stopingUserDeviceList.isEmpty()) {
				userDeviceDAO.updateMachineStatus(stopingUserDeviceList, TabUserDevice.STATUS_STOP);
			}
			if(!runningUserDeviceList.isEmpty()) {
				userDeviceDAO.updateMachineStatus(runningUserDeviceList, TabUserDevice.STATUS_RUN);
			}
			
		}
		
	}
	
}
*/
