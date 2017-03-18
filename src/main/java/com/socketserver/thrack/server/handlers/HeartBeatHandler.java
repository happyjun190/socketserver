package com.socketserver.thrack.server.handlers;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by wushenjun on 2017/3/17.
 * 心跳handler
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exception", cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //心跳校验,判断心跳是都是0x3030
        String heartBeatMsg = CodeUtils.getHexStringNoBlank((byte[]) msg);
        if(heartBeatMsg.equals(Constants.HEART_BEAT_MSG)) {
            logger.info("client HeartBeat, HeartBeat_MSG:{}", heartBeatMsg);
            Client client = ClientMap.getClient(ctx.channel());
            if (client != null)
            {
                if (client.isTimeout())
                {
                    logger.info("client timeout, disconnect it");
                    ctx.close();
                }
                client.touchSession(Client.Status.ACTIVE);
                ctx.flush();
            }
            //心跳消息，不再下传
            return;
        }
        //非心跳消息,往下传
        ctx.fireChannelRead(msg);
    }
}
