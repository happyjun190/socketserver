package com.socketserver.thrack.service.commons;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.DateUtils;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import com.socketserver.thrack.server.client.Constants.StartAddrAndReadSize;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Created by ziye on 2017/3/24.
 * 定时任务处理器
 */
@Service
public class ScheduleTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskService.class);

    /**
     * 用于向休眠状态下的逆变器发送请求(处于未请求/未响应状态的逆变器)
     * 每5分钟(300秒)执行一次
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void wakingSleptInverterSchedule() {
        logger.info("开始执行定时任务");
        Client client;
        Map<String, ClientInverterStats> inverterStatsMap;
        //遍历所有handler
        for(Channel channel: ClientMap.mapChannel.keySet()) {
            client = ClientMap.getClient(channel);
            inverterStatsMap = client.getInverterStatsMap();
            if(inverterStatsMap==null||inverterStatsMap.isEmpty()) {
                logger.info("这个dtu设备下没有任何逆变器设备，client：{}", client);
            } else {
                sendRequsetToSleptInverter(channel, inverterStatsMap);
            }
        }
    }


    /**
     * 向处于休眠状态的逆变器发送请求
     * @param channel
     * @param inverterStatsMap
     */
    private void sendRequsetToSleptInverter(Channel channel, Map<String, ClientInverterStats> inverterStatsMap) {
        ClientInverterStats clientInverterStats;
        String inverterId;
        byte[] inverterAddress;//逆变器地址 byte
        String readAddress;//在逆变器上读取数据区的地址
        byte[] readAddressBytes;//读取寄存器的地址
        int requestSize;
        //int index;//Constants.StartAddrAndReadSize 枚举中的index
        int nowTimeToInt = DateUtils.dateToInt();//当前时间的秒钟
        int timeinterval;
        byte[] bcrc;
        byte[] requestBytes;//请求byte   {逆变器地址(inverterAddress),请求类型(03都请求),寄存器高低位(readAddressBytes),00,寄存器个数(requestSize),crc高低位(2bytes)}
        for(String key:inverterStatsMap.keySet()) {
            clientInverterStats = inverterStatsMap.get(key);
            timeinterval = nowTimeToInt - clientInverterStats.getLastSendTime();
            //未发送请求 or 超时未收到相应(超时时间为300秒)
            if(clientInverterStats.getSendStatus()==ClientInverterStats.SEND_STATUS_0||
                    (clientInverterStats.getSendStatus()==ClientInverterStats.SEND_STATUS_1&&timeinterval>ClientInverterStats.MAX_RESPONSE_TIME)) {
                inverterId = clientInverterStats.getInverterId();
                inverterAddress = CodeUtils.hexStringToBytes(inverterId);
                readAddress = clientInverterStats.getReadAddress();
                if(StringUtil.isBlank(readAddress)) {
                    //如果逆变器没有最后的读取的寄存器的地址，则设置为最开始的寄存器地址
                    readAddress = Constants.ADDR_1600;
                }
                readAddressBytes = CodeUtils.hexStringToBytes(readAddress);
                requestSize = StartAddrAndReadSize.getSizeByAddress(readAddress);
                requestBytes = new byte[]{inverterAddress[0], 0x03, readAddressBytes[0], readAddressBytes[1], 0x00, (byte) requestSize, 0x00, 0x00};
                bcrc = CodeUtils.crc16(requestBytes, requestBytes.length-2);//length-2 因为加上了CRC高低位
                requestBytes[requestBytes.length-2] = bcrc[0];
                requestBytes[requestBytes.length-1] = bcrc[1];
                channel.writeAndFlush(requestBytes);
                //TODO 需要改变逆变器状态
                clientInverterStats.setLastSendTime(nowTimeToInt);
                clientInverterStats.setSendStatus(1);
                clientInverterStats.setReadAddress(readAddress);
                //TODO 设置到ClientMap中
                ClientMap.refreshClientInverterStats(channel, clientInverterStats);
            }
        }
    }

}
