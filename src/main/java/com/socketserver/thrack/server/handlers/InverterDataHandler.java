package com.socketserver.thrack.server.handlers;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
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

        //TODO 后续使用java反射机制执行方法(可能需要结合java8的新特性),结合spring
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

        try {
            Thread.sleep(30000);
            //TODO
            ctx.writeAndFlush(new byte[]{0, 0, 0, 0});
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

}
