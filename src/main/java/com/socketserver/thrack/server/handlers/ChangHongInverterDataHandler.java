package com.socketserver.thrack.server.handlers;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by wushenjun on 2017/3/29.
 * 铁塔(长虹)逆变器数据处理handler
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChangHongInverterDataHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ChangHongInverterDataHandler.class);

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

        //1长虹逆变器
        if(clientInverterStats.getInverterType()==ClientInverterStats.INVERTER_TYPE_1) {

        } else {
            //非英威腾逆变器消息,往下传
            ctx.fireChannelRead(msg);
        }

    }


    /**
     * 获取逆变器地址
     * @param message
     * @return
     */
    private String getInverterDeviceAddr(byte[] message) {
        byte[] addrBytes = {message[0]};
        return CodeUtils.getHexStringNoBlank(addrBytes);
    }

}
