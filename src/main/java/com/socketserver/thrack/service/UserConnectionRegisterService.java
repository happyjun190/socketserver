package com.socketserver.thrack.service;


import com.socketserver.thrack.model.user.SocketUser;

import io.netty.channel.Channel;

/**
 * 把连接到此服务器的用户记录到redis中
 * @author wushenjun
 *
 */
public interface UserConnectionRegisterService {
	
	public void registerDeviceChannelToThisServer(SocketUser user, Channel channel);
	
	public void removeChannelFromThisServer(Channel channel);
	
}
