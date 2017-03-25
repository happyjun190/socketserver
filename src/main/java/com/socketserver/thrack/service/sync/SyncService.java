package com.socketserver.thrack.service.sync;

import com.socketserver.thrack.commons.CodeUtils;
import com.socketserver.thrack.commons.DateUtils;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.server.client.ClientMap;
import com.socketserver.thrack.server.client.Constants;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by ziye on 2017/3/26.
 * 异步处理service
 */
@Service
public class SyncService {

    /**
     * 异步处理请求数据
     * @param readAddress
     * @param inverterDeviceAddr
     * @param ctx
     * @param clientInverterStats
     */
    @Async
    public void sendRequsetToInverterDevice(String readAddress, String inverterDeviceAddr, ChannelHandlerContext ctx, ClientInverterStats clientInverterStats) {
        String reqReadAddress;
        try {
            Thread.sleep(30000);
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
            byte[] requestBytes = new byte[]{inverterAddress[0], 0x03, readAddressBytes[0], readAddressBytes[1], 0x00, (byte) requestSize, 0x00, 0x00};
            byte[] bcrc = CodeUtils.crc16(requestBytes, requestBytes.length-2);//length-2 因为加上了CRC高低位
            requestBytes[requestBytes.length-2] = bcrc[0];
            requestBytes[requestBytes.length-1] = bcrc[1];
            //TODO
            ctx.writeAndFlush(requestBytes);

            //TODO 需要改变逆变器状态
            clientInverterStats.setLastSendTime(DateUtils.dateToInt());
            clientInverterStats.setSendStatus(1);
            clientInverterStats.setReadAddress(reqReadAddress);
            //TODO 设置到ClientMap中
            ClientMap.refreshClientInverterStats(ctx.channel(), clientInverterStats);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
