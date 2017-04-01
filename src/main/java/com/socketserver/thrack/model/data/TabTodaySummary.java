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
    private BigDecimal generateCapacity;
    private BigDecimal saveMoney;
    private BigDecimal co2Reduction;
    private String datestring;
    private Date ctime;
    private Date mtime;

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
}
