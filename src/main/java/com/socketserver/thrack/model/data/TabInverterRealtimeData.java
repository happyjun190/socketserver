package com.socketserver.thrack.model.data;

import java.math.BigDecimal;

/**
 * Created by ziye on 2017/4/2.
 * 设备运行过程的参数信息
 */
public class TabInverterRealtimeData {
    private int id;//运行参数id
    private String dtuId;//dtu设备id
    private String inverterId;//逆变器id
    private String inverterAddr;//逆变器地址
    private BigDecimal pv1Voltage;//pv1电压
    private BigDecimal pv2Voltage;//pv2电压
    private BigDecimal pv3Voltage;//pv3电压
    private BigDecimal pv4Voltage;//pv4电压
    private BigDecimal pv1ElectricCurrent;//pv1电流
    private BigDecimal pv2ElectricCurrent;//pv2电流
    private BigDecimal pv3ElectricCurrent;//pv3电流
    private BigDecimal pv4ElectricCurrent;//pv4电流
    private BigDecimal uPhaseVoltage;//U相电压
    private BigDecimal vPhaseVoltage;//V相电压
    private BigDecimal wPhaseVoltage;//W相电压
    private BigDecimal busPhaseVoltage;//BUS相电压
    private BigDecimal uPhaseElectricCurrent;//U相电流
    private BigDecimal vPhaseElectricCurrent;//V相电流
    private BigDecimal wPhaseElectricCurrent;//W相电流
    private BigDecimal busPhaseElectricCurrent;//BUS相电流
    private BigDecimal gridFrequency;//电网频率
    private BigDecimal powerFactor;//功率因数(有符号)
    private BigDecimal inputPower;//输入功率(KW)
    private BigDecimal outputPower;//输出功率(KW)
    private BigDecimal temperature1;//温度1(℃)
    private BigDecimal temperature2;//温度2(℃)
    private BigDecimal temperature3;//温度3(℃)
    private BigDecimal groundingResistance;//接地电阻(M)
    private BigDecimal leakageCurrent;//漏电流(mA)
    private BigDecimal dcComponent;//直流分量(mA)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDtuId() {
        return dtuId;
    }

    public void setDtuId(String dtuId) {
        this.dtuId = dtuId;
    }

    public String getInverterId() {
        return inverterId;
    }

    public void setInverterId(String inverterId) {
        this.inverterId = inverterId;
    }

    public String getInverterAddr() {
        return inverterAddr;
    }

    public void setInverterAddr(String inverterAddr) {
        this.inverterAddr = inverterAddr;
    }

    public BigDecimal getPv1Voltage() {
        return pv1Voltage;
    }

    public void setPv1Voltage(BigDecimal pv1Voltage) {
        this.pv1Voltage = pv1Voltage;
    }

    public BigDecimal getPv2Voltage() {
        return pv2Voltage;
    }

    public void setPv2Voltage(BigDecimal pv2Voltage) {
        this.pv2Voltage = pv2Voltage;
    }

    public BigDecimal getPv3Voltage() {
        return pv3Voltage;
    }

    public void setPv3Voltage(BigDecimal pv3Voltage) {
        this.pv3Voltage = pv3Voltage;
    }

    public BigDecimal getPv4Voltage() {
        return pv4Voltage;
    }

    public void setPv4Voltage(BigDecimal pv4Voltage) {
        this.pv4Voltage = pv4Voltage;
    }

    public BigDecimal getPv1ElectricCurrent() {
        return pv1ElectricCurrent;
    }

    public void setPv1ElectricCurrent(BigDecimal pv1ElectricCurrent) {
        this.pv1ElectricCurrent = pv1ElectricCurrent;
    }

    public BigDecimal getPv2ElectricCurrent() {
        return pv2ElectricCurrent;
    }

    public void setPv2ElectricCurrent(BigDecimal pv2ElectricCurrent) {
        this.pv2ElectricCurrent = pv2ElectricCurrent;
    }

    public BigDecimal getPv3ElectricCurrent() {
        return pv3ElectricCurrent;
    }

    public void setPv3ElectricCurrent(BigDecimal pv3ElectricCurrent) {
        this.pv3ElectricCurrent = pv3ElectricCurrent;
    }

    public BigDecimal getPv4ElectricCurrent() {
        return pv4ElectricCurrent;
    }

    public void setPv4ElectricCurrent(BigDecimal pv4ElectricCurrent) {
        this.pv4ElectricCurrent = pv4ElectricCurrent;
    }

    public BigDecimal getuPhaseVoltage() {
        return uPhaseVoltage;
    }

    public void setuPhaseVoltage(BigDecimal uPhaseVoltage) {
        this.uPhaseVoltage = uPhaseVoltage;
    }

    public BigDecimal getvPhaseVoltage() {
        return vPhaseVoltage;
    }

    public void setvPhaseVoltage(BigDecimal vPhaseVoltage) {
        this.vPhaseVoltage = vPhaseVoltage;
    }

    public BigDecimal getwPhaseVoltage() {
        return wPhaseVoltage;
    }

    public void setwPhaseVoltage(BigDecimal wPhaseVoltage) {
        this.wPhaseVoltage = wPhaseVoltage;
    }

    public BigDecimal getBusPhaseVoltage() {
        return busPhaseVoltage;
    }

    public void setBusPhaseVoltage(BigDecimal busPhaseVoltage) {
        this.busPhaseVoltage = busPhaseVoltage;
    }

    public BigDecimal getuPhaseElectricCurrent() {
        return uPhaseElectricCurrent;
    }

    public void setuPhaseElectricCurrent(BigDecimal uPhaseElectricCurrent) {
        this.uPhaseElectricCurrent = uPhaseElectricCurrent;
    }

    public BigDecimal getvPhaseElectricCurrent() {
        return vPhaseElectricCurrent;
    }

    public void setvPhaseElectricCurrent(BigDecimal vPhaseElectricCurrent) {
        this.vPhaseElectricCurrent = vPhaseElectricCurrent;
    }

    public BigDecimal getwPhaseElectricCurrent() {
        return wPhaseElectricCurrent;
    }

    public void setwPhaseElectricCurrent(BigDecimal wPhaseElectricCurrent) {
        this.wPhaseElectricCurrent = wPhaseElectricCurrent;
    }

    public BigDecimal getBusPhaseElectricCurrent() {
        return busPhaseElectricCurrent;
    }

    public void setBusPhaseElectricCurrent(BigDecimal busPhaseElectricCurrent) {
        this.busPhaseElectricCurrent = busPhaseElectricCurrent;
    }

    public BigDecimal getGridFrequency() {
        return gridFrequency;
    }

    public void setGridFrequency(BigDecimal gridFrequency) {
        this.gridFrequency = gridFrequency;
    }

    public BigDecimal getPowerFactor() {
        return powerFactor;
    }

    public void setPowerFactor(BigDecimal powerFactor) {
        this.powerFactor = powerFactor;
    }

    public BigDecimal getInputPower() {
        return inputPower;
    }

    public void setInputPower(BigDecimal inputPower) {
        this.inputPower = inputPower;
    }

    public BigDecimal getOutputPower() {
        return outputPower;
    }

    public void setOutputPower(BigDecimal outputPower) {
        this.outputPower = outputPower;
    }

    public BigDecimal getTemperature1() {
        return temperature1;
    }

    public void setTemperature1(BigDecimal temperature1) {
        this.temperature1 = temperature1;
    }

    public BigDecimal getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(BigDecimal temperature2) {
        this.temperature2 = temperature2;
    }

    public BigDecimal getTemperature3() {
        return temperature3;
    }

    public void setTemperature3(BigDecimal temperature3) {
        this.temperature3 = temperature3;
    }

    public BigDecimal getGroundingResistance() {
        return groundingResistance;
    }

    public void setGroundingResistance(BigDecimal groundingResistance) {
        this.groundingResistance = groundingResistance;
    }

    public BigDecimal getLeakageCurrent() {
        return leakageCurrent;
    }

    public void setLeakageCurrent(BigDecimal leakageCurrent) {
        this.leakageCurrent = leakageCurrent;
    }

    public BigDecimal getDcComponent() {
        return dcComponent;
    }

    public void setDcComponent(BigDecimal dcComponent) {
        this.dcComponent = dcComponent;
    }
}
