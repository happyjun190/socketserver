package com.socketserver.thrack.service.impl;

import com.socketserver.thrack.cache.IRedisOperator;
import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.DateUtils;
import com.socketserver.thrack.commons.RedisConstants;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import com.socketserver.thrack.service.ISendReqToInverterService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wushenjun on 2017/3/30.
 */
@Service
public class SendReqToInverterService implements ISendReqToInverterService {

    private static final Logger logger = LoggerFactory.getLogger(SendReqToInverterService.class);

    @Autowired
    private IRedisOperator redisOperator;

    public void sendReqToInvtInverterDevice(String readAddress, String inverterDeviceAddr, ChannelHandlerContext ctx, ClientInverterStats clientInverterStats) {
        String reqReadAddress;
        int index = Constants.StartAddrAndReadSize.getIndexByAddress(readAddress);
        if(index==Constants.MAX_INDEX_OF_ADDRESS) {//已经是最大的index
            reqReadAddress = Constants.ADDR_1600;
        } else {
            //获取下一个请求地址
            reqReadAddress = Constants.StartAddrAndReadSize.getAddressByIndex(index+1);
        }
        byte[] inverterAddress = CodeUtils.hexStringToBytes(inverterDeviceAddr);
        byte[] readAddressBytes = CodeUtils.hexStringToBytes(reqReadAddress);
        int requestSize = Constants.StartAddrAndReadSize.getSizeByAddress(readAddress);
        //读数据
        byte[] requestBytes = new byte[]{inverterAddress[0], 0x03, readAddressBytes[0], readAddressBytes[1], 0x00, (byte) requestSize, 0x00, 0x00};
        byte[] bcrc = CodeUtils.crc16(requestBytes, requestBytes.length-2);//length-2 因为加上了CRC高低位
        requestBytes[requestBytes.length-2] = bcrc[0];
        requestBytes[requestBytes.length-1] = bcrc[1];

        //TODO 如果最近发送的时间与当前发送的时间差在5s以内，则sleep(5s)
        //查看当前channel最近发送消息的时间
        int nowTime = DateUtils.dateToInt();//获取系统当前时间
        //获取当前channel的authKey
        Client client = ClientMap.getClient(ctx.channel());
        String authKey = client.getAuthKey();
        String channelLastSendTimeStr = redisOperator.get(RedisConstants.Prefix.CHANNEL_LAST_SEND_TIME+authKey);
        int channelLastSendTime = channelLastSendTimeStr==null?0:Integer.parseInt(channelLastSendTimeStr);

        //如果当前channel在5s 内已经发送了消息，则休眠5s 再发送消息
        if(nowTime-channelLastSendTime<Constants.MAX_SEND_TIME_INTERVAL) {
            try {
                //休息6秒(wait/sleep)
                nowTime+= Constants.MAX_WAIT_TIME_INTERVAL;
                Thread.sleep((long)(Constants.MAX_WAIT_TIME_INTERVAL*1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        redisOperator.set(RedisConstants.Prefix.CHANNEL_LAST_SEND_TIME+authKey, String.valueOf(nowTime));
        //发送消息
        ctx.writeAndFlush(requestBytes);

        //改变逆变器状态
        clientInverterStats.setLastSendTime(DateUtils.dateToInt());
        clientInverterStats.setSendStatus(1);
        clientInverterStats.setReadAddress(reqReadAddress);
        //设置到ClientMap中--更新最新dtu下逆变器状态
        ClientMap.refreshClientInverterStats(ctx.channel(), clientInverterStats);

    }

}
