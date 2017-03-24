package com.socketserver.thrack.server.client;

/**
 * Created by wushenjun on 2017/3/18.
 * dtu客户端下的逆变器状态信息
 */
public class ClientInverterStats {

    //发送状态(0未发送 1已发送(当发送request收到response请求后,将该状态置0))
    public static final int SEND_STATUS_0 = 0;
    public static final int SEND_STATUS_1 = 1;

    public static final int MAX_RESPONSE_TIME = 300;//响应超时时间 单位:秒钟

    private String dtuId;           //dtu设备id
    private String inverterId;      //逆变器地址
    private int lastSendTime;       //上次发送时间(second),时间戳
    private int sendStatus;         //发送状态(0未发送 1已发送(当发送request收到response请求后,将该状态置0))
                                    //TODO 由于接收到请求，处理完数据后就会发送下一个请求，则基本上不会出现置0状态
                                    //TODO 不过，由于处理数据到下一个发送请求之前会使用Threa.sleep一段时间(可能休眠后未发送)，所以要注意这段逻辑的处理
    private String readAddress;     //读取逆变器的地址

    public String getInverterId() {
        return inverterId;
    }

    public void setInverterId(String inverterId) {
        this.inverterId = inverterId;
    }

    public int getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(int lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getReadAddress() {
        return readAddress;
    }

    public void setReadAddress(String readAddress) {
        this.readAddress = readAddress;
    }

    public String getDtuId() {
        return dtuId;
    }

    public void setDtuId(String dtuId) {
        this.dtuId = dtuId;
    }

    @Override
    public String toString() {
        return "ClientInverterStats{" +
                "dtuId='" + dtuId + '\'' +
                ", inverterId='" + inverterId + '\'' +
                ", lastSendTime=" + lastSendTime +
                ", sendStatus=" + sendStatus +
                ", readAddress='" + readAddress + '\'' +
                '}';
    }
}
