package com.socketserver.thrack.service.impl;

import com.socketserver.thrack.server.client.ClientInverterStats;
import com.socketserver.thrack.service.IDataDealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ziye on 2017/3/21.
 */
@Service
public class DataDealService implements IDataDealService {

    private static final Logger logger = LoggerFactory.getLogger(DataDealService.class);


    @Transactional
    @Override
    public void dataDealOfAddr1600(byte[] message, ClientInverterStats clientInverterStats) {

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
}