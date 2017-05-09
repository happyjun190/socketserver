package com.socketserver.thrack.dao;

import com.socketserver.thrack.model.data.TabInverterData;
import com.socketserver.thrack.model.data.TabInverterRealtimeData;
import com.socketserver.thrack.model.data.TabPeakPowerData;
import com.socketserver.thrack.model.data.TabTodaySummary;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * Created by wushenjun on 2017/4/1.
 * 逆变器原始数据表
 */
public interface InverterDataDAO {

    /**
     * 采集数据时入库--原始数据
     * @param tabInverterData
     */
    @Insert("INSERT INTO \"TS_INVERTER_DATA\" (\"ID\", \"DTU_ID\", \"INVERTER_ADDR\", \"INVERTER_ID\", \"DATA_LENGTH\", \"DATA\", \"CTIME\", \"START_READ_ADDRESS\") " +
            "                          VALUES (SEQ_ON_INVERTERDATA.NEXTVAL, #{dtuId}, #{inverterAddr}, #{inverterId}, #{dataLength}, #{data}, SYSDATE, #{startReadAddress}) ")
    void insertInverterData(TabInverterData tabInverterData);


    /**
     * 峰值功率入库(今日峰值功率和历史峰值功率)
     * @param tabPeakPowerData
     */
    @Insert("INSERT INTO \"TS_PEAK_POWER_DATA\" (\"ID\", \"TODAY_PEAK_POWER\", \"HISTORY_PEAK_POWER\", \"DTU_ID\", \"INVERTER_ADDR\", \"INVERTER_ID\", \"CTIME\") " +
            "                            VALUES (SEQ_ON_PEAKPOWERDATA.NEXTVAL, #{todayPeakPower}, #{historyPeakPower}, #{dtuId}, #{inverterAddr}, #{inverterId}, SYSDATE)")
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
     * 插入/更新当前无功功率
     * @param tabTodaySummary
     */
    void insertReactivePowerToTodaySummary(TabTodaySummary tabTodaySummary);


    /**
     * 插入运行参数信息
     * @param tabInverterOperParams
     */
    void insertInverterRealtimeData(TabInverterRealtimeData tabInverterOperParams);


    /**
     * 更新逆变器的一些状态(状态、逆变器时间、异常状态)
     * @param tabTodaySummary
     */
    void insertInverterStatus(TabTodaySummary tabTodaySummary);


    /**
     * 获取最近一条逆变器上报的数据
     * @param dtuId
     * @param inverterId
     * @return
     */
    TabInverterRealtimeData getInverterLastInsertRealTimeData(@Param("dtuId") String dtuId, @Param("inverterId") String inverterId);

}
