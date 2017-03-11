package com.socketserver.thrack.model.userdevice;


import java.math.BigDecimal;

/**
 * @author wushenjun
 * @date:  2016年10月20日
 */
public class TabUserDevice {
	
	//开启状态(0:运行中 1:未运行 2:维护中 3:故障)
	public static final int STATUS_RUN = 0;
	public static final int STATUS_STOP = 1;
	public static final int STATUS_MAINTENANCE = 2;
	public static final int STATUS_PROBLEM = 3;
	
	private int id;//主键ID
	private int userId;//用户id(ts_user表id)
	private int deviceId;//设备id(ts_device表id)
	private String machineId;//机器mac地址
	private String deviceNum;//设备编号
	private int status;//开启状态(0:运行中 1:未运行 2:维护中 3:故障)
	private BigDecimal longitude;//初始经度
	private BigDecimal latitude;//初始纬度
	private BigDecimal lastLongitude;//最后经度
	private BigDecimal lastLatitude;//最后纬度
	private String lastStrategyValue;//设备最后的策略值
	private String softVersion;//软件版本(升级提醒用)
	private int ctime;//注册时间
	private int mtime;//修改时间
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getDeviceNum() {
		return deviceNum;
	}
	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public BigDecimal getLongitude() {
		return longitude;
	}
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	public BigDecimal getLatitude() {
		return latitude;
	}
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	public BigDecimal getLastLongitude() {
		return lastLongitude;
	}
	public void setLastLongitude(BigDecimal lastLongitude) {
		this.lastLongitude = lastLongitude;
	}
	public BigDecimal getLastLatitude() {
		return lastLatitude;
	}
	public void setLastLatitude(BigDecimal lastLatitude) {
		this.lastLatitude = lastLatitude;
	}
	public String getLastStrategyValue() {
		return lastStrategyValue;
	}
	public void setLastStrategyValue(String lastStrategyValue) {
		this.lastStrategyValue = lastStrategyValue;
	}
	public String getSoftVersion() {
		return softVersion;
	}
	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}
	public int getCtime() {
		return ctime;
	}
	public void setCtime(int ctime) {
		this.ctime = ctime;
	}
	public int getMtime() {
		return mtime;
	}
	public void setMtime(int mtime) {
		this.mtime = mtime;
	}
	
	
}
