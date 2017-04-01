package com.socketserver.thrack.dao;

import com.socketserver.thrack.model.data.TabInverterData;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by wushenjun on 2017/4/1.
 * 逆变器原始数据表
 */
public interface InverterDataDAO {

    /**
     * 采集数据时入库
     * @param tabInverterData
     */
    @Insert("INSERT INTO \"ts_inverter_data\" (\"id\", \"dtu_id\", \"inverter_id\", \"data_length\", \"data\", \"ctime\", \"start_read_address\") " +
            "                                   VALUES (seq_on_inverterdata.nextval, #{dtuId}, #{inverterId}, #{dataLength}, #{data}, SYSDATE, #{startReadAddress}) ")
    void insertInverterData(TabInverterData tabInverterData);

}
