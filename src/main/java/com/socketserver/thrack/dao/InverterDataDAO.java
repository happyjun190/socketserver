package com.socketserver.thrack.dao;

import com.socketserver.thrack.model.data.TabInverterData;
import com.socketserver.thrack.model.data.TabInverterOperParams;
import com.socketserver.thrack.model.data.TabPeakPowerData;
import com.socketserver.thrack.model.data.TabTodaySummary;
import org.apache.ibatis.annotations.Insert;

/**
 * Created by wushenjun on 2017/4/1.
 * 逆变器原始数据表
 */
public interface InverterDataDAO {

    /**
     * 采集数据时入库--原始数据
     * @param tabInverterData
     */
    @Insert("INSERT INTO \"ts_inverter_data\" (\"id\", \"dtu_id\", \"inverter_id\", \"data_length\", \"data\", \"ctime\", \"start_read_address\") " +
            "                          VALUES (seq_on_inverterdata.nextval, #{dtuId}, #{inverterId}, #{dataLength}, #{data}, SYSDATE, #{startReadAddress}) ")
    void insertInverterData(TabInverterData tabInverterData);


    /**
     * 峰值功率入库(今日峰值功率和历史峰值功率)
     * @param tabPeakPowerData
     */
    @Insert("INSERT INTO \"ts_peak_power_data\" (\"id\", \"today_peak_power\", \"history_peak_power\", \"dtu_id\", \"inverter_id\", \"ctime\") " +
            "                            VALUES (seq_on_peakpowerdata.nextval, #{todayPeakPower}, #{historyPeakPower}, #{dtuId}, #{inverterId}, SYSDATE)")
    void insertPowerData(TabPeakPowerData tabPeakPowerData);


    /**
     * 插入/更新今日统计数据(今日发电量/省钱量/co2减排量)
     * @param tabTodaySummary
     */
    void insertTodaySummary(TabTodaySummary tabTodaySummary);

    /**
     * 插入/更新累计统计数据(累计发电量/省钱量/co2减排量)
     * @param tabTodaySummary
     */
    void insertTotalSummary(TabTodaySummary tabTodaySummary);


    /**
     * 插入运行参数信息
     * @param tabInverterOperParams
     */
    void insertInverterOperParams(TabInverterOperParams tabInverterOperParams);

}
