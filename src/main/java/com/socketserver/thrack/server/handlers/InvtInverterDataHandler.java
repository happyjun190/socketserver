package com.socketserver.thrack.server.handlers;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.StringUtil;
import com.socketserver.thrack.dao.InverterDataDAO;
import com.socketserver.thrack.model.data.TabInverterData;
import com.socketserver.thrack.server.ExecutorGroupFactory;
import com.socketserver.thrack.server.client.Client;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import com.socketserver.thrack.server.client.Constants.StartAddrAndReadSize;
import com.socketserver.thrack.service.IDataDealService;
import com.socketserver.thrack.service.ISendReqToInverterService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wushenjun on 2017/3/17.
 * 光伏(英威腾)逆变器数据处理handler
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class InvtInverterDataHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(InvtInverterDataHandler.class);

    @Autowired
    private IDataDealService dataDealService;
    @Autowired
    private InverterDataDAO inverterDataDAO;
    //@Autowired
    //private SyncService syncService;

    @Autowired
    private ISendReqToInverterService sendReqToInvtInverterDevice;

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


        String messageToStr = CodeUtils.getHexString(this.getDataBytes(message));
        Client client = ClientMap.getClient(ctx.channel());
        logger.info("client:{}, and the response data is :{}", client, messageToStr);

        Map<String, ClientInverterStats> inverterStatsMap = client.getInverterStatsMap();
        //逆变器地址
        String inverterDeviceAddr = this.getInverterDeviceAddr(message);
        //逆变器信息
        ClientInverterStats clientInverterStats = inverterStatsMap==null?null:inverterStatsMap.get(inverterDeviceAddr);
        if(inverterStatsMap==null||inverterStatsMap.isEmpty()||clientInverterStats==null) {//null and empty判断
            logger.info("dtu逆变器设备: {} 不存在,请于管理后台配置逆变器设备信息并重启设备！", client);
            return;
        }


        //TODO 只在当前handler处理，其他handler不用处理
        //数据入库
        TabInverterData tabInverterData = new TabInverterData();
        tabInverterData.setData(messageToStr);
        tabInverterData.setDataLength(message.length-5);
        tabInverterData.setDtuId(client.getDtuId());
        tabInverterData.setInverterAddr(inverterDeviceAddr);
        tabInverterData.setInverterId(clientInverterStats.getInverterId());
        tabInverterData.setStartReadAddress(clientInverterStats.getReadAddress());

        //使用线程处理-原始数据入库
        ExecutorGroupFactory.getInstance().getWritingDBTaskGroup().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        inverterDataDAO.insertInverterData(tabInverterData);
                    }
                }, 1, TimeUnit.MICROSECONDS
        );


        //0英威腾逆变器
        if(clientInverterStats.getInverterType()==ClientInverterStats.INVERTER_TYPE_0) {
            //数据处理
            String readAddress = clientInverterStats.getReadAddress();

            if(StringUtil.isBlank(readAddress)) {
                return;
            }


            //判断当前消息长度是否符合需要的长度，如果不符合，则抛弃消息
            int messageDataSize = StartAddrAndReadSize.getSizeByAddress(readAddress);//数据长度为请求寄存器个数*2
            messageDataSize = messageDataSize*2;

            //TODO 后续可能需要使用java反射机制执行方法(并结合java8的新特性),还需结合spring
            logger.info("now deal with the data with the startaddress : {}, msg length:{}, data length:{} ", readAddress, message.length, messageDataSize);

            if((message.length-5)!= messageDataSize) {//repeat send
                //如果消息长度与期望长度不一致，则再次请求
                //异步发送request消息
                logger.info("如果消息长度与期望长度不一致，则再次请求");
                //TODO 长度不一致时，不用等待30S，等待5s
                ExecutorGroupFactory.getInstance().getAyncReqInvtTaskGroup().schedule(
                        new Runnable() {
                            @Override
                            public void run() {
                                sendReqToInvtInverterDevice.sendReqToInvtInverterDevice(true, readAddress, inverterDeviceAddr, ctx, clientInverterStats);
                            }
                        }, 5, TimeUnit.SECONDS
                );
                return;
            }

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
                case Constants.ADDR_16A0:
                    dataDealService.dataDealOfAddr16A0(message, clientInverterStats);
                    break;
                case Constants.ADDR_1800:
                    dataDealService.dataDealOfAddr1800(message, clientInverterStats);
                    break;
                default:
                    break;
            }

            //异步发送request消息
            ExecutorGroupFactory.getInstance().getAyncReqInvtTaskGroup().schedule(
                    new Runnable() {
                        @Override
                        public void run() {
                            sendReqToInvtInverterDevice.sendReqToInvtInverterDevice(false, readAddress, inverterDeviceAddr, ctx, clientInverterStats);
                        }
                    }, 30, TimeUnit.SECONDS
            );

            //异步处理
            //syncService.sendRequsetToInvtInverterDevice(readAddress, inverterDeviceAddr, ctx, clientInverterStats);

            //service处理完成后再重置逆变器信息，如sendStatus、readAddress
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


    //获取数据区数据byte
    private static byte[] getDataBytes(byte[] message) {
        int length = message.length;
        byte[] dataBytes = new byte[length-5];
        int index = 0;
        for(int i=3; i<length-2; i++) {
            dataBytes[index++] = message[i];
        }
        return dataBytes;
    }

}
