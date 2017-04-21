package com.socketserver.thrack.service;

import com.socketserver.thrack.server.client.ClientInverterStats;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wushenjun on 2017/3/30.
 */
public interface ISendReqToInverterService {

    /**
     * 给指定channel的指定逆变器发送读请求
     * @param readAddress
     * @param isRepeat 是否重发 ture:是 false:否
     * @param inverterDeviceAddr
     * @param ctx
     * @param clientInverterStats
     */
    void sendReqToInvtInverterDevice(boolean isRepeat, String readAddress, String inverterDeviceAddr, ChannelHandlerContext ctx, ClientInverterStats clientInverterStats);
}
