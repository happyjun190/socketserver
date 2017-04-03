package com.socketserver.thrack.model.data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ziye on 2017/4/1.
 */
public class TabTodaySummary {
    private long id;
    private String dtuId;
    private String inverterId;
    private BigDecimal generateCapacity;//当日发电量
    private BigDecimal saveMoney;//当日省钱量
    private BigDecimal co2Reduction;//当日CO2减排量
    private BigDecimal totalGenerateCapacity;//累计发电量
    private BigDecimal totalSaveMoney;//累计省钱量
    private BigDecimal totalCo2Reduction;//累计CO2减排量(KG)
    private BigDecimal reactivePower;//无功功率
    private String datestring;
    private Date ctime;
    private Date mtime;
    private String exception1;
    private String exception2;
    private String exception3;
    private String exception4;
    private String exception5;
    private String exception6;
    private String exception7;
    private String exception8;
    private Date inverterTime;
    private int inverterStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public BigDecimal getGenerateCapacity() {
        return generateCapacity;
    }

    public void setGenerateCapacity(BigDecimal generateCapacity) {
        this.generateCapacity = generateCapacity;
    }

    public BigDecimal getSaveMoney() {
        return saveMoney;
    }

    public void setSaveMoney(BigDecimal saveMoney) {
        this.saveMoney = saveMoney;
    }

    public BigDecimal getCo2Reduction() {
        return co2Reduction;
    }

    public void setCo2Reduction(BigDecimal co2Reduction) {
        this.co2Reduction = co2Reduction;
    }

    public String getDatestring() {
        return datestring;
    }

    public void setDatestring(String datestring) {
        this.datestring = datestring;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    public BigDecimal getTotalGenerateCapacity() {
        return totalGenerateCapacity;
    }

    public void setTotalGenerateCapacity(BigDecimal totalGenerateCapacity) {
        this.totalGenerateCapacity = totalGenerateCapacity;
    }

    public BigDecimal getTotalSaveMoney() {
        return totalSaveMoney;
    }

    public void setTotalSaveMoney(BigDecimal totalSaveMoney) {
        this.totalSaveMoney = totalSaveMoney;
    }

    public BigDecimal getTotalCo2Reduction() {
        return totalCo2Reduction;
    }

    public void setTotalCo2Reduction(BigDecimal totalCo2Reduction) {
        this.totalCo2Reduction = totalCo2Reduction;
    }

    public BigDecimal getReactivePower() {
        return reactivePower;
    }

    public void setReactivePower(BigDecimal reactivePower) {
        this.reactivePower = reactivePower;
    }

    public String getException1() {
        return exception1;
    }

    public void setException1(String exception1) {
        this.exception1 = exception1;
    }

    public String getException2() {
        return exception2;
    }

    public void setException2(String exception2) {
        this.exception2 = exception2;
    }

    public String getException3() {
        return exception3;
    }

    public void setException3(String exception3) {
        this.exception3 = exception3;
    }

    public String getException4() {
        return exception4;
    }

    public void setException4(String exception4) {
        this.exception4 = exception4;
    }

    public String getException5() {
        return exception5;
    }

    public void setException5(String exception5) {
        this.exception5 = exception5;
    }

    public String getException6() {
        return exception6;
    }

    public void setException6(String exception6) {
        this.exception6 = exception6;
    }

    public String getException7() {
        return exception7;
    }

    public void setException7(String exception7) {
        this.exception7 = exception7;
    }

    public String getException8() {
        return exception8;
    }

    public void setException8(String exception8) {
        this.exception8 = exception8;
    }

    public Date getInverterTime() {
        return inverterTime;
    }

    public void setInverterTime(Date inverterTime) {
        this.inverterTime = inverterTime;
    }

    public int getInverterStatus() {
        return inverterStatus;
    }

    public void setInverterStatus(int inverterStatus) {
        this.inverterStatus = inverterStatus;
    }
}
