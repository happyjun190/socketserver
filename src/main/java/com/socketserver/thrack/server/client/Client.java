package com.socketserver.thrack.server.client;

import io.netty.channel.Channel;

public class Client
{
	private String authKey;
	private Status status = Status.INIT;
	private Channel channel;

	private long lastAccess = -1l;

	public enum Status
	{
		INIT, AUTH, ACTIVE, TIMEOUT, BLOCK, BLACKLIST
	}

	public Client(String authKey, Channel channel)
	{
		this.authKey = authKey;
		this.channel = channel;
	}

	public String getAuthKey()
	{
		return authKey;
	}

	public void setAuthKey(String authKey)
	{
		this.authKey = authKey;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public void touchSession(Status status)
	{
		lastAccess = System.currentTimeMillis();
		this.status = status;
	}

	public boolean isTimeout()
	{
		long now = System.currentTimeMillis();
		long elapse = now - lastAccess;
		return elapse > Constants.CLIENT_TIMEOUT;
	}

	public long getTimeout()
	{
		long now = System.currentTimeMillis();
		long elapse = now - lastAccess;
		return elapse;
	}

	public long getLastAccess()
	{
		return lastAccess;
	}

	public void setLastAccess(long lastAccess)
	{
		this.lastAccess = lastAccess;
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel = channel;
	}


	@Override
	public String toString() {
		return "Client{" +
				"authKey='" + authKey + '\'' +
				", status=" + status +
				", channel=" + channel +
				", lastAccess=" + lastAccess +
				'}';
	}
}
