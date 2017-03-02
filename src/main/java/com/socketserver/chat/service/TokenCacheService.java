package com.socketserver.chat.service;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.socketserver.chat.model.message.DTUDataPackage;
import com.socketserver.chat.model.user.SocketUser;

public interface TokenCacheService {
	
	/**
	 * 检查服务端token是否正确
	 * @param server
	 * @param token
	 * @return
	 */
	public Boolean checkServerToken(SocketUser server, String token);
	
	/**
	 * 检查用户端token是否正确
	 * @param user
	 * @param token
	 * @return
	 */
	public Boolean checkUserToken(SocketUser user, String token);
	
	/**
	 * 通过machineId 生成token-machineId信息
	 * @param machineId
	 * @param existToken
	 * @return
	 */
	public String generateMachineToken(String machineId, String existToken);
	
	
	/**
	 * 验证用户名密码并生存token
	 * @param userAccount
	 * @param password
	 * @return
	 */
	public String generateClientUserToken(String userAccount, String password);
	
	/**
	 * 验证token中存储的machineId是否和数据包中一致
	 * @param machineId
	 * @return
	 */
	public boolean checkMachineToken(DTUDataPackage dtuDataPackage);
	
	
	/**
	 * 验证数据包中的token是否有效
	 * 1、可能在token区，而且可能是设备或者终端的token
	 * 2、可能在data区，用于web登陆用户的16byte token
	 * @param dtuDataPackage
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public boolean checkTokenIsValid(DTUDataPackage dtuDataPackage) throws JsonParseException, JsonMappingException, IOException;

}
