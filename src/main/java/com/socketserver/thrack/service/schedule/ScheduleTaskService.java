package com.socketserver.thrack.service.schedule;

import com.socketserver.thrack.commons.ByteUtil;
import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.DateUtils;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import com.socketserver.thrack.server.client.Constants.StartAddrAndReadSize;
import com.socketserver.thrack.server.interactive.InverterRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


/**
 * Created by ziye on 2017/3/24.
 * 定时任务处理器
 */
@Service
public class ScheduleTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskService.class);

    /**
     * 用于向休眠状态下的(英威腾-光伏)逆变器发送请求(处于未请求/未响应状态的逆变器)
     * 每5分钟(300秒)执行一次
     */
    //@Scheduled(fixedDelay = 5 * 60 * 1000)
    @Scheduled(fixedDelay = 5 * 1000)
    public void wakingSleptInvtInverterSchedule() {
        //logger.info("开始执行定时任务：{}", "wakingSleptInvtInverterSchedule");
        Client client;
        Map<String, ClientInverterStats> inverterStatsMap;
        //遍历所有handler
        for(Channel channel: ClientMap.mapChannel.keySet()) {
            client = ClientMap.getClient(channel);
            inverterStatsMap = client.getInverterStatsMap();
            if(inverterStatsMap==null||inverterStatsMap.isEmpty()) {
                logger.info("这个dtu设备下没有任何逆变器设备，client：{}", client);
            } else {
                //验证通过或者是活跃状态
                if(client.getStatus()==Client.Status.AUTH||client.getStatus()==Client.Status.ACTIVE) {
                    sendRequsetToSleptInvtInverter(channel, inverterStatsMap);
                } else {
                    logger.info("这个dtu设备channel未鉴权通过或处于不活跃状态，client：{}", client);
                }

            }
        }
    }


    /**
     * 用于向休眠状态下的(长虹-铁塔)逆变器发送请求(处于未请求/未响应状态的逆变器)
     * 每5分钟(300秒)执行一次
     */
    //@Scheduled(fixedDelay = 5 * 60 * 1000)
    @Scheduled(fixedDelay = 6 * 1000)
    public void wakingSleptChanghongnverterSchedule() {

        //logger.info("开始执行定时任务：{}", "wakingSleptChanghongnverterSchedule");
        Client client;
        Map<String, ClientInverterStats> inverterStatsMap;
        //遍历所有handler
        for(Channel channel: ClientMap.mapChannel.keySet()) {
            client = ClientMap.getClient(channel);
            inverterStatsMap = client.getInverterStatsMap();
            if(inverterStatsMap==null||inverterStatsMap.isEmpty()) {
                logger.info("这个dtu设备下没有任何逆变器设备，client：{}", client);
            } else {
                //验证通过或者是活跃状态
                if(client.getStatus()==Client.Status.AUTH||client.getStatus()==Client.Status.ACTIVE) {
                    sendRequsetToSleptChangHongInverter(channel, inverterStatsMap);
                } else {
                    logger.info("这个dtu设备channel未鉴权通过或处于不活跃状态，client：{}", client);
                }

            }
        }

    }


    /**
     * 向处于休眠状态的(英威腾-光伏)逆变器发送请求
     * @param channel
     * @param inverterStatsMap
     */
    private void sendRequsetToSleptInvtInverter(Channel channel, Map<String, ClientInverterStats> inverterStatsMap) {
        ClientInverterStats clientInverterStats;
        String inverterAddr;
        byte[] inverterAddress;//逆变器地址 byte
        String readAddress;//在逆变器上读取数据区的地址
        byte[] readAddressBytes;//读取寄存器的地址
        int requestSize;
        //int index;//Constants.StartAddrAndReadSize 枚举中的index
        int nowTimeToInt = DateUtils.dateToInt();//当前时间的秒钟
        int timeinterval;
        byte[] bcrc;
        byte[] requestBytes = null;//请求byte   {逆变器地址(inverterAddress),请求类型(03都请求),寄存器高低位(readAddressBytes),00,寄存器个数(requestSize),crc高低位(2bytes)}
        for(String key:inverterStatsMap.keySet()) {
            clientInverterStats = inverterStatsMap.get(key);
            timeinterval = nowTimeToInt - clientInverterStats.getLastSendTime();
            //未发送请求 or 超时未收到相应(超时时间为300秒) //TODO 并且是英威腾-光伏逆变器 inverterType=0
            if(clientInverterStats.getInverterType()==ClientInverterStats.INVERTER_TYPE_0&&(clientInverterStats.getSendStatus()==ClientInverterStats.SEND_STATUS_0||
                    (clientInverterStats.getSendStatus()==ClientInverterStats.SEND_STATUS_1&&timeinterval>ClientInverterStats.MAX_RESPONSE_TIME))) {
                logger.info("inverterType:{}, sendStatus:{}, timeinterval:{}", clientInverterStats.getInverterType(), clientInverterStats.getSendStatus(), timeinterval);
                inverterAddr = clientInverterStats.getInverterAddr();
                inverterAddress = CodeUtils.hexStringToBytes(inverterAddr);
                readAddress = clientInverterStats.getReadAddress();
                if(StringUtil.isBlank(readAddress)) {
                    //如果逆变器没有最后的读取的寄存器的地址，则设置为最开始的寄存器地址
                    readAddress = Constants.ADDR_1600;
                }
                readAddressBytes = CodeUtils.hexStringToBytes(readAddress);
                requestSize = StartAddrAndReadSize.getSizeByAddress(readAddress);

                //请求数据
                byte[] requestInverterAddr = new byte[] {readAddressBytes[0], readAddressBytes[1]};
                byte[] requestLength = new byte[] {0x00, (byte) requestSize};
                InverterRequest request = new InverterRequest(inverterAddress[0], (byte) 0x03, ByteUtil.getShort(requestInverterAddr, 0), ByteUtil.getShort(requestLength, 0));

                try {
                    requestBytes = request.encode();
                    logger.info("请求逆变器的数据为:{}" + CodeUtils.getHexString(requestBytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteBuf encoded = channel.alloc().buffer();
                encoded.writeBytes(requestBytes);
                channel.writeAndFlush(encoded);

                /*requestBytes = new byte[]{inverterAddress[0], 0x03, readAddressBytes[0], readAddressBytes[1], 0x00, (byte) requestSize, 0x00, 0x00};
                bcrc = CodeUtils.crc16(requestBytes, requestBytes.length-2);//length-2 因为加上了CRC高低位
                requestBytes[requestBytes.length-2] = bcrc[0];
                requestBytes[requestBytes.length-1] = bcrc[1];
                logger.info("schedule task requestBytes is : {}", CodeUtils.getHexString(requestBytes));
                channel.writeAndFlush(requestBytes);*/



                //TODO 需要改变逆变器状态
                clientInverterStats.setLastSendTime(nowTimeToInt);
                clientInverterStats.setSendStatus(1);
                clientInverterStats.setReadAddress(readAddress);
                //TODO 设置到ClientMap中
                ClientMap.refreshClientInverterStats(channel, clientInverterStats);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 向处于休眠状态的(英威腾-铁塔)逆变器发送请求
     * @param channel
     * @param inverterStatsMap
     */
    private void sendRequsetToSleptChangHongInverter(Channel channel, Map<String, ClientInverterStats> inverterStatsMap) {
        ClientInverterStats clientInverterStats;
        String inverterAddr;
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
            //未发送请求 or 超时未收到相应(超时时间为300秒) //TODO 并且是铁塔-长虹逆变器 inverterType=1
            if(clientInverterStats.getInverterType()==ClientInverterStats.INVERTER_TYPE_1&&(clientInverterStats.getSendStatus()==ClientInverterStats.SEND_STATUS_0||
                    (clientInverterStats.getSendStatus()==ClientInverterStats.SEND_STATUS_1&&timeinterval>ClientInverterStats.MAX_RESPONSE_TIME))) {
                inverterAddr = clientInverterStats.getInverterAddr();
                inverterAddress = CodeUtils.hexStringToBytes(inverterAddr);
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
