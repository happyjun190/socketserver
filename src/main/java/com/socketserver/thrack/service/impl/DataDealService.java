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
        //今日峰值功率 KW
        byte[] todayPeakPowerBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 0, 4);
        BigDecimal todayPeakPower = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(todayPeakPowerBytes, 1000);

        //历史峰值功率 KW
        byte[] historyPeakPowerBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 24, 4);
        BigDecimal historyPeakPower = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(historyPeakPowerBytes, 1000);

        logger.info("转换今日峰值功率和历史峰值功率，todayPeakPower：{}KW，historyPeakPower：{}KW", todayPeakPower, historyPeakPower);

        //BigDecimal peakPower =
    }

    @Transactional
    @Override
    public void dataDealOfAddr1616(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
        //日发电量(KWh)
        byte[] todayGeneratingCapacityBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 0, 4);
        BigDecimal todayGeneratingCapacity = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(todayGeneratingCapacityBytes, 100);

        //日省钱
        byte[] todaySaveMoneyBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 4, 4);
        BigDecimal todaySaveMoney = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(todaySaveMoneyBytes, 100);

        //日减排CO2(Kg)
        byte[] todayCO2EmissionReductionBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 8, 4);
        BigDecimal todayCO2EmissionReduction = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(todayCO2EmissionReductionBytes, 100);

        logger.info("转换今日发电量(KWh)，日省钱和日减排CO2(Kg)，todayGeneratingCapacity：{}KWh，todaySaveMoney：{}元，todayCO2EmissionReduction：{}Kg",
                                                                                        todayGeneratingCapacity, todaySaveMoney,todayCO2EmissionReduction);

    }

    @Transactional
    @Override
    public void dataDealOfAddr1652(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
        //累计发电量(KWh)
        byte[] totalGeneratingCapacityBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 0, 4);
        BigDecimal totalGeneratingCapacity = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(totalGeneratingCapacityBytes, 100);

        //累计省钱
        byte[] totalSaveMoneyBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 4, 4);
        BigDecimal totalSaveMoney = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(totalSaveMoneyBytes, 100);

        //累计减排CO2(Kg)
        byte[] totalCO2EmissionReductionBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 8, 4);
        BigDecimal totalCO2EmissionReduction = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(totalCO2EmissionReductionBytes, 100);

        logger.info("转换累计发电量(KWh)，累计省钱和累计减排CO2(Kg)，totalGeneratingCapacity：{}KWh，totalSaveMoney：{}元，totalCO2EmissionReduction：{}Kg",
                                                               totalGeneratingCapacity, totalSaveMoney, totalCO2EmissionReduction);
    }

    @Transactional
    @Override
    public void dataDealOfAddr1670(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
    }

    @Transactional
    @Override
    public void dataDealOfAddr168E(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
    }

    @Transactional
    @Override
    public void dataDealOfAddr1690(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
    }

    @Transactional
    @Override
    public void dataDealOfAddr1800(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
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