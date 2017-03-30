package com.socketserver.thrack.server.client;

import com.socketserver.thrack.server.handlers.ChangHongInverterDataHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientMap
{
	public static final ConcurrentHashMap<Channel, Client> mapChannel = new ConcurrentHashMap<>();
	public static final ConcurrentHashMap<String, Client> mapKey = new ConcurrentHashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ChangHongInverterDataHandler.class);

	public synchronized static void addClient(Channel channel, Client client) {
		if (channel == null || client == null) {
			return;
		}

		String authKey = client.getAuthKey();
		Client old = mapKey.get(authKey);
		if (old != null) {
			mapKey.remove(authKey);
			mapChannel.remove(old.getChannel());
		}

		mapKey.put(authKey, client);
		mapChannel.put(channel, client);
	}

	/**
	 * 刷新client中逆变器的数据请求状态等信息
	 * @param channel
	 * @param clientInverterStats
	 */
	public synchronized static void refreshClientInverterStats(Channel channel, ClientInverterStats clientInverterStats) {
		if (channel == null || clientInverterStats == null) {
			return;
		}
		Client client = mapChannel.get(channel);
		Map<String, ClientInverterStats> inverterStatsMap = client.getInverterStatsMap();
		if(inverterStatsMap==null) {
			inverterStatsMap = new HashMap<>();
		}
		//logger.info("refreshClientInverterStats:{}", clientInverterStats);
		inverterStatsMap.put(clientInverterStats.getInverterId(), clientInverterStats);
		client.setInverterStatsMap(inverterStatsMap);
		mapChannel.put(channel, client);
	}

	public static Client getClient(Channel channel)
	{
		return mapChannel.get(channel);
	}

	public static Client getClient(String authKey)
	{
		return mapKey.get(authKey);
	}

	public synchronized static void removeClient(Channel channel)
	{
		Client client = mapChannel.get(channel);
		if (client != null) {
			mapKey.remove(client.getAuthKey());
			mapChannel.remove(channel);
		}
	}

	public synchronized static void removeClient(String authKey)
	{
		Client client = mapKey.get(authKey);
		if (client != null)
		{
			mapChannel.remove(client.getChannel());
			mapKey.remove(authKey);
		}
	}
}
