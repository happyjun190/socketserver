package com.socketserver.thrack.server.client;

/**
 * Created by wushenjun on 2017/3/18.
 * dtu客户端下的逆变器状态信息
 */
public class ClientInverterStats {
    private String inverterId;//逆变器地址
    private int lastSendTime;//上次发送时间(second),时间戳
    private int sendStatus;//发送状态(0未发送 1已发送(当发送request收到response请求后,将该状态置0))
    private String readAddress;//读取逆变器的地址

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


    @Override
    public String toString() {
        return "ClientInverterStats{" +
                "inverterId='" + inverterId + '\'' +
                ", lastSendTime=" + lastSendTime +
                ", sendStatus=" + sendStatus +
                ", readAddress='" + readAddress + '\'' +
                '}';
    }
}
