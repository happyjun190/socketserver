package com.socketserver.thrack.server.client;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ClientMap
{
	private static final ConcurrentHashMap<Channel, Client> mapCha = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, Client> mapKey = new ConcurrentHashMap<>();

	public synchronized static void addClient(Channel channel, Client client)
	{
		if (channel == null || client == null)
			return;

		String authKey = client.getAuthKey();
		Client old = mapKey.get(authKey);
		if (old != null)
		{
			mapKey.remove(authKey);
			mapCha.remove(old.getChannel());
		}

		mapKey.put(authKey, client);
		mapCha.put(channel, client);
	}

	public static Client getClient(Channel channel)
	{
		return mapCha.get(channel);
	}

	public static Client getClient(String authKey)
	{
		return mapKey.get(authKey);
	}

	public synchronized static void removeClient(Channel channel)
	{
		Client client = mapCha.get(channel);
		if (client != null)
		{
			mapKey.remove(client.getAuthKey());
			mapCha.remove(channel);
		}
	}

	public synchronized static void removeClient(String authKey)
	{
		Client client = mapKey.get(authKey);
		if (client != null)
		{
			mapCha.remove(client.getChannel());
			mapKey.remove(authKey);
		}
	}
}
