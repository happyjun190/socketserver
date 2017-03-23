package com.socketserver.thrack.service.impl;

import com.socketserver.thrack.commons.DataTransformUtils;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.service.IDataDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by ziye on 2017/3/21.
 */
@Service
public class DataDealService implements IDataDealService {

    private static final Logger logger = LoggerFactory.getLogger(DataDealService.class);


    @Transactional
    @Override
    public void dataDealOfAddr1600(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
        //峰值功率 KW
        //byte[] peakPowerBytes = {dataBytes[0], dataBytes[1], dataBytes[2], dataBytes[3]};
        byte[] peakPowerBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 0, 4);
        BigDecimal peakPower = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(peakPowerBytes, 1000);
        //BigDecimal peakPower =
    }

    @Transactional
    @Override
    public void dataDealOfAddr1616(byte[] message, ClientInverterStats clientInverterStats) {

    }

    @Transactional
    @Override
    public void dataDealOfAddr1652(byte[] message, ClientInverterStats clientInverterStats) {

    }

    @Transactional
    @Override
    public void dataDealOfAddr1670(byte[] message, ClientInverterStats clientInverterStats) {

    }

    @Transactional
    @Override
    public void dataDealOfAddr168E(byte[] message, ClientInverterStats clientInverterStats) {

    }

    @Transactional
    @Override
    public void dataDealOfAddr1690(byte[] message, ClientInverterStats clientInverterStats) {

    }

    @Transactional
    @Override
    public void dataDealOfAddr1800(byte[] message, ClientInverterStats clientInverterStats) {

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