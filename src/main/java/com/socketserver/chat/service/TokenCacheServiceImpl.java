package com.socketserver.chat.service;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socketserver.chat.commons.CommonUtils;
import com.socketserver.chat.commons.EncryptUtils;
import com.socketserver.chat.commons.SocketServerConstants;
import com.socketserver.chat.commons.StringUtil;
import com.socketserver.chat.dao.UserDAO;
import com.socketserver.chat.dao.UserDeviceDAO;
import com.socketserver.chat.model.message.DTUDataPackage;
import com.socketserver.chat.model.user.SocketUser;
import com.socketserver.chat.model.user.TabUser;
import com.socketserver.chat.model.userdevice.TabUserDevice;

import redis.clients.jedis.JedisCluster;


@Service
public class TokenCacheServiceImpl implements TokenCacheService {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenCacheServiceImpl.class);
	//private static final ObjectMapper jsonMapper = new ObjectMapper();
	@Autowired
	private UserDeviceDAO userDeviceDAO;
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private JedisCluster jedisCluster;
	

	@Override
	public Boolean checkServerToken(SocketUser server, String token) {
		
		// 应用服务器token的key的命名规则(例)：i0t9serverToken
		// usertype定义：9是大众版服务器，8是专业版服务器
		
		String tokenByServer = jedisCluster.get( "i" + server.getUserId() + "t" + server.getUserType() + "serverToken" );
		
		return token.equals( tokenByServer );
		
	}

	@Override
	public Boolean checkUserToken(SocketUser user, String token) {
		// 用户token验证：根据token取userid，与传入userid对比（与服务器验证逻辑不同）
		
		String tokenPrefix = null;
		
		String idByToken = jedisCluster.get( tokenPrefix + token );
			
		logger.info("got id by token: " + (idByToken != null ? idByToken : "null") + ", input user: " + user);
		
		if(idByToken == null) {
			return false;
		} else {
			return user.getUserId().equals( Integer.valueOf( idByToken ));
		}
	}

	@Override
	public String generateMachineToken(String machineId, String existToken) {
		TabUserDevice tabUserDevice = userDeviceDAO.getUserDeviceByMachineId(machineId);
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
			machineToke = CommonUtils.gen8ByteStringToken();
		}
		
		//机器的token前缀SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE
		jedisCluster.setex(SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE+machineToke, 
						   SocketServerConstants.TOKEN_LIFETIME_ONEDAY, 
						   machineId);
		return machineToke;
	}
	
	
	@Override
	public String generateClientUserToken(String userAccount, String password) {
		TabUser user = userDAO.getUserInfo(userAccount);
		if(user==null) {
			return null;
		}
		
		int userId = user.getId();
		
		//验证密码是否正确
		if (!user.getPassword().equals(EncryptUtils.MD5Str(password + user.getLoginSalt()))) {// 前端送过来的是md5编码后的密码，小写
			return null;
		}
		
		//生成8byte machineToken
		String clientUserToken = CommonUtils.gen8ByteStringToken();
		
		//机器的token前缀
		String tokenPrefix = SocketServerConstants.TOEKN_PRIFIX_TO_WEBUSER;
		
		//保存用户id到redis中
		jedisCluster.setex(tokenPrefix+clientUserToken, SocketServerConstants.TOKEN_LIFETIME_ONEDAY, String.valueOf(userId));
		return clientUserToken;
	}
	

	@Override
	public boolean checkMachineToken(DTUDataPackage dtuDataPackage) {
		String token = new String(dtuDataPackage.getToken());
		//机器的token前缀
		String tokenPrefix = SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE;
		String tokenMachineId = jedisCluster.get(tokenPrefix+token);
		if(tokenMachineId==null) {
			return false;
		} else {
			return tokenMachineId.equals(CommonUtils.generateMachineIdFromBytes(dtuDataPackage.getMachineId()));
		}
	}

	@Override
	public boolean checkTokenIsValid(DTUDataPackage dtuDataPackage) throws JsonParseException, JsonMappingException, IOException {
		//此处用于处理token验证，防止无token的channel发送数据
		String token = new String(dtuDataPackage.getToken());
		//设备登录
		String tokenMachineId = jedisCluster.get(SocketServerConstants.TOKEN_PRIFIX_TO_MACHINE+token);
		//客户端用户登陆(非web平台)
		String tokenUserId = jedisCluster.get(SocketServerConstants.TOEKN_PRIFIX_TO_WEBUSER+token);
		
		//先判断token区是否存在有效token，不存在则到data区去获取token再判断
		//都是取redis数据，不会影响运行效率
		if(StringUtil.isNotBlank(tokenMachineId)||StringUtil.isNotBlank(tokenUserId)){
			return true;
		} else {
			//验证web平台推送消息时的token有效性
			if(dtuDataPackage.getData()!=null) {
//				String data = new String(dtuDataPackage.getData());
//				Map<String, Object> dataMap = jsonMapper.readValue(data, Map.class);
//				token = (String) dataMap.get("token");
//				String userId = jedisCluster.get(SocketServerConstants.TOEKN_PRIFIX_TO_WEBUSER+token);
				token = new String(dtuDataPackage.getToken());
				String userId = jedisCluster.get(SocketServerConstants.TOEKN_PRIFIX_TO_WEBUSER+token);
				if(userId!=null) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
