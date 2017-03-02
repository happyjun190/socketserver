package com.socketserver.chat.service;


import com.socketserver.chat.model.user.SocketUser;

import io.netty.channel.Channel;

/**
 * 把连接到此服务器的用户记录到redis中
 * @author wushenjun
 *
 */
public interface UserConnectionRegisterService {
	
	public void registerDeviceChannelToThisServer(SocketUser user, Channel channel);
	
	public void removeUserChannelFromThisServer(Channel channel);
	
	public String getUserConnectingAddress(SocketUser user);
	
	/**
	 * Set user version at local and redis
	 * @param user
	 * @param version
	 */
	public void setVersion(SocketUser user, String version);
	
	/**
	 * Get user version from local or redis by {@link SocketUser}
	 * @param user
	 * @return
	 */
	public String getVersion(SocketUser user);
	
	/**
	 * User login
	 * NOTE: different from register
	 * @param user
	 */
	public void login(SocketUser user);
	
	/**
	 * User logout
	 * @param user
	 */
	public void logout(SocketUser user);
	
	/**
	 * If not logout
	 * @param user
	 * @param False if logout
	 */
	public boolean isOnline(SocketUser user);
}
