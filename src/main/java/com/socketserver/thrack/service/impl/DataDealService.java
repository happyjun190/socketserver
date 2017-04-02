package com.socketserver.thrack.service.impl;

import com.socketserver.thrack.commons.DataTransformUtils;
import com.socketserver.thrack.commons.DateUtils;
import com.socketserver.thrack.dao.InverterDataDAO;
import com.socketserver.thrack.model.data.TabInverterOperParams;
import com.socketserver.thrack.model.data.TabPeakPowerData;
import com.socketserver.thrack.model.data.TabTodaySummary;
import com.socketserver.thrack.server.ExecutorGroupFactory;
import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.service.IDataDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by ziye on 2017/3/21.
 */
@Service
public class DataDealService implements IDataDealService {

    private static final Logger logger = LoggerFactory.getLogger(DataDealService.class);

    @Autowired
    private InverterDataDAO inverterDataDAO;


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

        TabPeakPowerData tabPeakPowerData = new TabPeakPowerData();
        tabPeakPowerData.setHistoryPeakPower(historyPeakPower);
        tabPeakPowerData.setTodayPeakPower(todayPeakPower);
        tabPeakPowerData.setDtuId(clientInverterStats.getDtuId());
        tabPeakPowerData.setInverterId(clientInverterStats.getInverterId());

        //使用线程处理-峰值功率数据入库(已解析)
        ExecutorGroupFactory.getInstance().getWritingDBTaskGroup().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        inverterDataDAO.insertPowerData(tabPeakPowerData);
                    }
                }, 1, TimeUnit.MICROSECONDS
        );
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

        TabTodaySummary tabTodaySummary = new TabTodaySummary();
        tabTodaySummary.setDtuId(clientInverterStats.getDtuId());
        tabTodaySummary.setInverterId(clientInverterStats.getInverterId());
        tabTodaySummary.setCo2Reduction(todayCO2EmissionReduction);
        tabTodaySummary.setSaveMoney(todaySaveMoney);
        tabTodaySummary.setGenerateCapacity(todayGeneratingCapacity);
        //设置日期yyyymmdd
        tabTodaySummary.setDatestring(DateUtils.getNowTime(DateUtils.DATE_DAY_STR));

        //使用线程处理-今日统计数据入库(已解析)
        ExecutorGroupFactory.getInstance().getWritingDBTaskGroup().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        inverterDataDAO.insertTodaySummary(tabTodaySummary);
                    }
                }, 1, TimeUnit.MICROSECONDS
        );

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

        TabTodaySummary tabTodaySummary = new TabTodaySummary();
        tabTodaySummary.setDtuId(clientInverterStats.getDtuId());
        tabTodaySummary.setInverterId(clientInverterStats.getInverterId());
        tabTodaySummary.setTotalCo2Reduction(totalCO2EmissionReduction);
        tabTodaySummary.setTotalSaveMoney(totalSaveMoney);
        tabTodaySummary.setTotalGenerateCapacity(totalGeneratingCapacity);
        //设置日期yyyymmdd
        tabTodaySummary.setDatestring(DateUtils.getNowTime(DateUtils.DATE_DAY_STR));

        //使用线程处理-累计统计数据入库(已解析)
        ExecutorGroupFactory.getInstance().getWritingDBTaskGroup().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        inverterDataDAO.insertTotalSummary(tabTodaySummary);
                    }
                }, 1, TimeUnit.MICROSECONDS
        );

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
        //无功功率(VA)，逆变器发出无功时为负，吸收无功时为正
        byte[] reactivePowerBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 0, 4);
        BigDecimal reactivePower = DataTransformUtils.tranfrom4ByteAndMulToSignedRealValue(reactivePowerBytes, 1000);
        logger.info("当前无功功率reactivePower:{}", reactivePower);
    }

    @Transactional
    @Override
    public void dataDealOfAddr1690(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
        TabInverterOperParams tabInverterOperParams = new TabInverterOperParams();
        //PV1电压
        byte[] pv1VoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 0, 2);
        BigDecimal pv1Voltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv1VoltageBytes, 10);
        //PV1电流
        byte[] pv1ElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 2, 2);
        BigDecimal pv1ElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv1ElectricCurrentBytes, 10);
        //PV2电压
        byte[] pv2VoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 4, 2);
        BigDecimal pv2Voltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv2VoltageBytes, 10);
        //PV2电流
        byte[] pv2ElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 6, 2);
        BigDecimal pv2ElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv2ElectricCurrentBytes, 10);
        //PV3电压
        byte[] pv3VoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 8, 2);
        BigDecimal pv3Voltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv3VoltageBytes, 10);
        //PV3电流
        byte[] pv3ElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 10, 2);
        BigDecimal pv3ElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv3ElectricCurrentBytes, 10);
        //PV4电压
        byte[] pv4VoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 12, 2);
        BigDecimal pv4Voltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv4VoltageBytes, 10);
        //PV4电流
        byte[] pv4ElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 14, 2);
        BigDecimal pv4ElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(pv4ElectricCurrentBytes, 10);

        logger.info("PV1~PV4电压分别为:{},{},{},{}", pv1Voltage, pv2Voltage, pv3Voltage, pv4Voltage);
        logger.info("PV1~PV4电流分别为:{},{},{},{}", pv1ElectricCurrent, pv2ElectricCurrent, pv3ElectricCurrent, pv4ElectricCurrent);
        tabInverterOperParams.setPv1Voltage(pv1Voltage);
        tabInverterOperParams.setPv2Voltage(pv2Voltage);
        tabInverterOperParams.setPv3Voltage(pv3Voltage);
        tabInverterOperParams.setPv4Voltage(pv4Voltage);
        tabInverterOperParams.setPv1ElectricCurrent(pv1ElectricCurrent);
        tabInverterOperParams.setPv2ElectricCurrent(pv2ElectricCurrent);
        tabInverterOperParams.setPv3ElectricCurrent(pv3ElectricCurrent);
        tabInverterOperParams.setPv4ElectricCurrent(pv4ElectricCurrent);

        //U相电压
        byte[] uPhaseVoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 16, 2);
        BigDecimal uPhaseVoltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(uPhaseVoltageBytes, 10);
        //U相电流
        byte[] uPhaseElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 18, 2);
        BigDecimal uPhaseElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(uPhaseElectricCurrentBytes, 10);
        //V相电压
        byte[] vPhaseVoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 20, 2);
        BigDecimal vPhaseVoltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(vPhaseVoltageBytes, 10);
        //V相电流
        byte[] vPhaseElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 22, 2);
        BigDecimal vPhaseElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(vPhaseElectricCurrentBytes, 10);
        //W相电压
        byte[] wPhaseVoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 24, 2);
        BigDecimal wPhaseVoltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(wPhaseVoltageBytes, 10);
        //W相电流
        byte[] wPhaseElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 26, 2);
        BigDecimal wPhaseElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(wPhaseElectricCurrentBytes, 10);

        //BUS电压
        byte[] busVoltageBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 28, 2);
        BigDecimal busVoltage = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(busVoltageBytes, 10);
        //BUS电流
        byte[] busElectricCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 30, 2);
        BigDecimal busElectricCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(busElectricCurrentBytes, 10);

        logger.info("U、V、W相电压及BUS电压分别为:{},{},{},{}",uPhaseVoltage, vPhaseVoltage, wPhaseVoltage, busVoltage);
        logger.info("U、V、W相电流及BUS电流分别为:{},{},{},{}",uPhaseElectricCurrent, vPhaseElectricCurrent, wPhaseElectricCurrent, busElectricCurrent);
        tabInverterOperParams.setuPhaseVoltage(uPhaseVoltage);
        tabInverterOperParams.setvPhaseVoltage(vPhaseVoltage);
        tabInverterOperParams.setwPhaseVoltage(wPhaseVoltage);
        tabInverterOperParams.setBusPhaseVoltage(busVoltage);
        tabInverterOperParams.setuPhaseElectricCurrent(uPhaseElectricCurrent);
        tabInverterOperParams.setvPhaseElectricCurrent(vPhaseElectricCurrent);
        tabInverterOperParams.setwPhaseElectricCurrent(wPhaseElectricCurrent);
        tabInverterOperParams.setBusPhaseElectricCurrent(busElectricCurrent);

        //电网频率
        byte[] gridFrequencyBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 32, 2);
        BigDecimal gridFrequency = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(gridFrequencyBytes, 100);
        //功率因数(有符号)
        byte[] powerFactorBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 34, 2);
        BigDecimal powerFactor = DataTransformUtils.tranfrom2ByteAndMulToSignedRealValue(powerFactorBytes, 1000);
        //输入功率(KW)
        byte[] inputPowerBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 36, 4);
        BigDecimal inputPower = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(inputPowerBytes, 1000);
        //输出功率(KW)
        byte[] outputPowerBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 40, 4);
        BigDecimal outputPower = DataTransformUtils.tranfrom4ByteAndMulToUnsignedRealValue(outputPowerBytes, 1000);

        logger.info("电网频率、功率因数(有符号)、输入功率(KW)及输出功率(KW)分别为:{},{},{},{}",gridFrequency, powerFactor, inputPower, outputPower);
        tabInverterOperParams.setGridFrequency(gridFrequency);
        tabInverterOperParams.setPowerFactor(powerFactor);
        tabInverterOperParams.setInputPower(inputPower);
        tabInverterOperParams.setOutputPower(outputPower);

        //温度1(℃)
        byte[] temperature1Bytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 44, 2);
        BigDecimal temperature1 = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(temperature1Bytes, 100);
        //温度2(℃)
        byte[] temperature2Bytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 46, 2);
        BigDecimal temperature2 = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(temperature2Bytes, 100);
        //温度3(℃)
        byte[] temperature3Bytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 48, 2);
        BigDecimal temperature3 = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(temperature3Bytes, 100);

        logger.info("温度1(℃)、温度2(℃)、温度3(℃)分别为:{},{},{}",temperature1, temperature2, temperature3);
        tabInverterOperParams.setTemperature1(temperature1);
        tabInverterOperParams.setTemperature2(temperature2);
        tabInverterOperParams.setTemperature3(temperature3);

        //接地电阻(M)
        byte[] groundingResistanceBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 58, 2);
        BigDecimal groundingResistance = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(groundingResistanceBytes, 100);
        //漏电流(mA)
        byte[] leakageCurrentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 60, 2);
        BigDecimal leakageCurrent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(leakageCurrentBytes, 100);
        //直流分量(mA) 0~65535，对应0~65535mA
        byte[] dcComponentBytes = DataTransformUtils.getBytesArrFromOffsetAndLength(dataBytes, 62, 2);
        BigDecimal dcComponent = DataTransformUtils.tranfrom2ByteAndMulToUnsignedRealValue(dcComponentBytes, 1);
        logger.info("接地电阻(M)、漏电流(mA)、直流分量(mA)分别为:{},{},{}",groundingResistance, leakageCurrent, dcComponent);
        tabInverterOperParams.setGroundingResistance(groundingResistance);
        tabInverterOperParams.setLeakageCurrent(leakageCurrent);
        tabInverterOperParams.setDcComponent(dcComponent);

        tabInverterOperParams.setDtuId(clientInverterStats.getDtuId());
        tabInverterOperParams.setInverterId(clientInverterStats.getInverterId());


        //使用线程处理-运行参数信息(已解析)
        ExecutorGroupFactory.getInstance().getWritingDBTaskGroup().schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        inverterDataDAO.insertInverterOperParams(tabInverterOperParams);
                    }
                }, 1, TimeUnit.MICROSECONDS
        );


    }

    @Transactional
    @Override
    public void dataDealOfAddr1800(byte[] message, ClientInverterStats clientInverterStats) {
        byte[] dataBytes = this.getDataBytes(message);
    }


    @Override
    public void dataDealOfChangHongInverter(byte[] message, ClientInverterStats clientInverterStats) {

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