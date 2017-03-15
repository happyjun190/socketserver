package com.socketserver.thrack.service;


public interface TokenCacheService {
	

	/**
	 * 通过machineId 生成token-machineId信息
	 * @param machineId
	 * @param existToken
	 * @return
	 */
	String generateMachineToken(String machineId, String existToken);
	
	

}
