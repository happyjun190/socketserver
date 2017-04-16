package com.socketserver.thrack.model.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ziye on 2017/4/1.
 */
public class TabPeakPowerData {
    private long id;
    private BigDecimal todayPeakPower;
    private BigDecimal historyPeakPower;
    private Date ctime;
    private String dtuId;
    private String inverterId;//逆变器id
    private String inverterAddr;//逆变器地址

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getTodayPeakPower() {
        return todayPeakPower;
    }

    public void setTodayPeakPower(BigDecimal todayPeakPower) {
        this.todayPeakPower = todayPeakPower;
    }

    public BigDecimal getHistoryPeakPower() {
        return historyPeakPower;
    }

    public void setHistoryPeakPower(BigDecimal historyPeakPower) {
        this.historyPeakPower = historyPeakPower;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getDtuId() {
        return dtuId;
    }

    public void setDtuId(String dtuId) {
        this.dtuId = dtuId;
    }

    public String getInverterId() {
        return inverterId;
    }

    public void setInverterId(String inverterId) {
        this.inverterId = inverterId;
    }

    public String getInverterAddr() {
        return inverterAddr;
    }

    public void setInverterAddr(String inverterAddr) {
        this.inverterAddr = inverterAddr;
    }
}
