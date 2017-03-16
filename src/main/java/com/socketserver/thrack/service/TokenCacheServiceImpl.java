package com.socketserver.thrack.service;

import com.socketserver.thrack.commons.SocketServerConstants;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.model.userdevice.TabUserDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;


@Service
public class TokenCacheServiceImpl implements TokenCacheService {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenCacheServiceImpl.class);
	//private static final ObjectMapper jsonMapper = new ObjectMapper();
	/*@Autowired
	private UserDeviceDAO userDeviceDAO;*/

	@Autowired
	private JedisCluster jedisCluster;
	


	@Override
	public String generateMachineToken(String machineId, String existToken) {
		TabUserDevice tabUserDevice = null;//userDeviceDAO.getUserDeviceByMachineId(machineId);
		if(tabUserDevice==null) {
			return null;
		}
		
		//机器在redis中保存的token
		String machineToke = null;
		if(StringUtil.isNotBlank(existToken)) {
			//TODO 可能需要直接返回null，因为可能是盗包上报数据
			//防止当前token存放的machineId不是数据包上报时的machineId
			String existTokensMachineId = jedisCluster.get(SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE+existToken);
			if(StringUtil.isNotBlank(existTokensMachineId)&&existTokensMachineId.equals(machineId)) {
				machineToke = existToken;
			}
			
		}
		
		//防止非法链接数据
		if(StringUtil.isBlank(machineToke)) {
			//生成8byte machineToken
			machineToke = "0";//CommonUtils.gen8ByteStringToken();
		}
		
		//机器的token前缀SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE
		jedisCluster.setex(SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE+machineToke, 
						   SocketServerConstants.TOKEN_LIFETIME_ONEDAY, 
						   machineId);
		return machineToke;
	}
	
	
}
