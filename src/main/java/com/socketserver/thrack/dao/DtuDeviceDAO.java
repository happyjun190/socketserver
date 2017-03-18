package com.socketserver.thrack.dao;

import com.socketserver.thrack.model.device.TabDtuDevice;
import org.apache.ibatis.annotations.Param;

/**
 * Created by wushenjun on 2017/3/16.
 * dtu设备DAO
 */
public interface DtuDeviceDAO {

    /**
     * 通过dtuId获取dtu基本信息
     * @param dtuId
     * @return
     */
    TabDtuDevice getDtuDeviceById(@Param("dtuId")int dtuId);


    /**
     * 通过dtuId获取dtu基本信息
     * @param authKey
     * @return
     */
    TabDtuDevice getDtuDeviceByAuthKey(@Param("authKey")String authKey);

}
