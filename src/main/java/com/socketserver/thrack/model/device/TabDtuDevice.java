package com.socketserver.thrack.model.device;

import java.util.Date;

/**
 * Created by wushenjun on 2017/3/16.
 */
public class TabDtuDevice {
    private int id;//主键
    private String powerstationId;//电厂ID
    private String model;//dtu型号
    private String authKey;//校验串
    private int status;//dtu设备状态 0正常 1废弃 其他状态待需求完善
    private String parity;//有无奇偶校验位 0有 1无
    private String seriesRate;//串口速率bps 可以在一个区间,暂时一个字段表示 如:110bps ~ 230400bps
    private Date mtime;//修改时间
    private Date ctime;//创建时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPowerstationId() {
        return powerstationId;
    }

    public void setPowerstationId(String powerstationId) {
        this.powerstationId = powerstationId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getSeriesRate() {
        return seriesRate;
    }

    public void setSeriesRate(String seriesRate) {
        this.seriesRate = seriesRate;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }
}
