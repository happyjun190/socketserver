package com.socketserver.thrack.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.socketserver.thrack.model.userdevice.TabUserDevice;

public interface UserDeviceDAO {
	
	/**
	 * 通过machine_id 获取到一条有效数据(正常情况下只有一条有效)
	 * @param machineId
	 * @return
	 */
	public TabUserDevice getUserDeviceByMachineId(@Param("machineId")String machineId);
	
	
	/**
	 * 批量更新设备状态
	 * @param machineIdList
	 */
	public void updateMachineStatus(@Param("machineIdList")List<String> machineIdList, @Param("status")int status);
	
}
