package com.socketserver.thrack.service;

import com.socketserver.thrack.server.client.ClientInverterStats;

/**
 * Created by ziye on 2017/3/21.
 * 数据处理interface
 */
public interface IDataDealService {

    /**
     * 读取0x1600~0x160D段位14个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr1600(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x1616~0x161F段位10个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr1616(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x1652~0x165B段位10个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr1652(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x1670~0x167D段位14个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr1670(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x168E~0x168F段位 2个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr168E(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x1690~0x169F段位16个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr1690(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x16A0~0x16AF段位16个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr16A0(byte[] message, ClientInverterStats clientInverterStats);

    /**
     * 读取0x1800~0x184F段位32个寄存器数据
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfAddr1800(byte[] message, ClientInverterStats clientInverterStats);


    /**
     * 长虹逆变器数据处理
     * @param message
     * @param clientInverterStats
     */
    void dataDealOfChangHongInverter(byte[] message, ClientInverterStats clientInverterStats);

}