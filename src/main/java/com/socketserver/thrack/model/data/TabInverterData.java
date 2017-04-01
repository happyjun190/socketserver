package com.socketserver.thrack.model.data;

import java.util.Date;

/**
 * Created by wushenjun on 2017/4/1.
 */
public class TabInverterData {
    private int id;
    private int dtuId;
    private String inverterId;
    private int dataLength;
    private String data;
    private Date ctime;
    private String startReadAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDtuId() {
        return dtuId;
    }

    public void setDtuId(int dtuId) {
        this.dtuId = dtuId;
    }

    public String getInverterId() {
        return inverterId;
    }

    public void setInverterId(String inverterId) {
        this.inverterId = inverterId;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public String getStartReadAddress() {
        return startReadAddress;
    }

    public void setStartReadAddress(String startReadAddress) {
        this.startReadAddress = startReadAddress;
    }
}
