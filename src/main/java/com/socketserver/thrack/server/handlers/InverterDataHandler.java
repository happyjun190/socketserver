package com.socketserver.thrack.server.handlers;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.DateUtils;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import com.socketserver.thrack.server.client.Constants.StartAddrAndReadSize;
import com.socketserver.thrack.service.IDataDealService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by wushenjun on 2017/3/17.
 * 逆变器数据处理handler
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InverterDataHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Autowired
    private IDataDealService dataDealService;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exception", cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] message = (byte[])msg;
        if(message.length<=5) {//读响应一般5+2*N, 写响应一般8字节，读写响应出错5字节
            return;
        }
        String messageToStr = CodeUtils.getHexString(message);
        Client client = ClientMap.getClient(ctx.channel());
        logger.info("client:{}, and the response is :{}", client, messageToStr);

        Map<String, ClientInverterStats> inverterStatsMap = client.getInverterStatsMap();
        //逆变器地址
        String inverterDeviceAddr = this.getInverterDeviceAddr(message);
        //逆变器信息
        ClientInverterStats clientInverterStats = inverterStatsMap==null?null:inverterStatsMap.get(inverterDeviceAddr);
        if(inverterStatsMap==null||inverterStatsMap.isEmpty()||clientInverterStats==null) {//null and empty判断
            logger.info("dtu逆变器设备: {} 不存在,请于管理后台配置逆变器设备信息并重启设备！", client);
            return;
        }

        //数据处理
        String readAddress = clientInverterStats.getReadAddress();

        if(StringUtil.isBlank(readAddress)) {
            return;
        }

        //TODO 后续可能需要使用java反射机制执行方法(并结合java8的新特性),还需结合spring
        logger.info("now deal with the data with the startaddress : {} ", readAddress);

        switch (readAddress) {
            case Constants.ADDR_1600:
                dataDealService.dataDealOfAddr1600(message, clientInverterStats);
                break;
            case Constants.ADDR_1616:
                dataDealService.dataDealOfAddr1616(message, clientInverterStats);
                break;
            case Constants.ADDR_1652:
                dataDealService.dataDealOfAddr1652(message, clientInverterStats);
                break;
            case Constants.ADDR_1670:
                dataDealService.dataDealOfAddr1670(message, clientInverterStats);
                break;
            case Constants.ADDR_168E:
                dataDealService.dataDealOfAddr168E(message, clientInverterStats);
                break;
            case Constants.ADDR_1690:
                dataDealService.dataDealOfAddr1690(message, clientInverterStats);
                break;
            case Constants.ADDR_1800:
                dataDealService.dataDealOfAddr1800(message, clientInverterStats);
                break;
            default:
                break;
        }

        String reqReadAddress;
        try {
            Thread.sleep(30000);
            int index = StartAddrAndReadSize.getIndexByAddress(readAddress);
            if(index==Constants.MAX_INDEX_OF_ADDRESS) {//已经是最大的index
                reqReadAddress = Constants.ADDR_1600;
            } else {
                //获取下一个请求地址
                reqReadAddress = StartAddrAndReadSize.getAddressByIndex(index+1);
            }
            byte[] inverterAddress = CodeUtils.hexStringToBytes(inverterDeviceAddr);
            byte[] readAddressBytes = CodeUtils.hexStringToBytes(reqReadAddress);
            int requestSize = StartAddrAndReadSize.getSizeByAddress(readAddress);
            byte[] requestBytes = new byte[]{inverterAddress[0], 0x03, readAddressBytes[0], readAddressBytes[1], 0x00, (byte) requestSize, 0x00, 0x00};
            byte[] bcrc = CodeUtils.crc16(requestBytes, requestBytes.length-2);//length-2 因为加上了CRC高低位
            requestBytes[requestBytes.length-2] = bcrc[0];
            requestBytes[requestBytes.length-1] = bcrc[1];
            //TODO
            ctx.writeAndFlush(requestBytes);

            //TODO 需要改变逆变器状态
            clientInverterStats.setLastSendTime(DateUtils.dateToInt());
            clientInverterStats.setSendStatus(1);
            clientInverterStats.setReadAddress(readAddress);
            //TODO 设置到ClientMap中
            ClientMap.refreshClientInverterStats(ctx.channel(), clientInverterStats);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //service处理完成后再重置逆变器信息，如sendStatus、readAddress
    }


    /**
     * 获取逆变器地址
     * @param message
     * @return
     */
    private String getInverterDeviceAddr(byte[] message) {
        byte[] addrBytes = {message[0], message[1]};
        return CodeUtils.getHexStringNoBlank(addrBytes);
    }


    /*public  static  void main(String args[]) {
        for(int i=0; i<10; i++) {
            try {
                Thread.sleep(10000);
                System.out.println(DateUtils.dateToInt());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/

}
