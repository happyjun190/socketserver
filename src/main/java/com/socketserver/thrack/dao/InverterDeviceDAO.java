package com.socketserver.thrack.dao;

import com.socketserver.thrack.server.client.ClientInverterStats;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * Created by wushenjun on 2017/3/18.
 * 逆变器DAO
 */
public interface InverterDeviceDAO {

    /**
     * 通过dtuId 获取dtu下关联的有效设备,并返回inverterId(逆变器地址)-ClientInverterStats的map集合
     * @param dtuId
     * @return
     */
    @MapKey("inverterAddr")
    Map<String, ClientInverterStats> getInverterStatsMap(@Param("dtuId")int dtuId);
}
