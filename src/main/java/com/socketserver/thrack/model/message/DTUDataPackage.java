package com.socketserver.thrack.model.message;

/**
 * 接收的感知层数据
 * @author wushenjun
 * 2016/10/29
 */
public class DTUDataPackage {
	private byte packageHead;//包头
	private byte lengthLowByte;//长度低位
	private byte lengthHighByte;//长度高位
	private byte orderType;//命令类别
	private byte orderWord;//命令字
	private byte orderNumLowByte;//命令序号低位
	private byte orderNumHightByte;//命令序号高位
	private byte[] extendInfo;//扩展信息(包括序号，加密等其它扩展) 2 bytes
	private byte status;//状态
	private byte[] machineId;//设备ID(16进制设备号,不足前面补 0) 8 bytes
	private byte[] token;//Torken(8Byte 通信令牌) 8 bytes
	private byte[] data;//数据区(传输的数据) N bytes
	private byte crcLowByte;//CRC低位
	private byte crcHighByte;//CRC 高位
	private byte packageEnd;//包尾
	
	
	/**
	 * 无参构造方法-初始化数据
	 */
	public DTUDataPackage() {
		this.packageHead = 0;
		this.lengthLowByte = 0;
		this.lengthHighByte = 0;
		this.orderType = 0;
		this.orderWord = 0;
		this.orderNumLowByte = 0;
		this.orderNumHightByte = 0;
		this.extendInfo = new byte[2];
		this.status = 0;
		this.machineId = new byte[8];
		this.token = new byte[8];
		this.data = null;
		this.crcLowByte = 0;
		this.crcHighByte = 0;
		this.packageEnd = 0;
	}


	/**
	 * 转换为输入的DTU格式包
	 * @param message
	 */
	public DTUDataPackage(byte[] message) {
		int length= message.length;
		this.packageHead = message[0];
		this.lengthLowByte = message[1];
		this.lengthHighByte = message[2];
		this.orderType = message[3];
		this.orderWord = message[4];
		this.orderNumLowByte = message[5];
		this.orderNumHightByte = message[6];
		this.extendInfo = new byte[]{message[7], message[8]};
		this.status = message[9];
		this.machineId = new byte[8];
		this.token = new byte[8];
		for(int i=0;i<8;i++) {
			this.machineId[i] = message[10+i];
			this.token[i] = message[18+i];
		}
		//this.machineId = new byte[]{message[10], message[11], message[12], 0, 0, 0, 0, 0};
		if(length>29) {//至少29byte
			int dataLength = length-29;
			this.data = new byte[dataLength];
			for(int i=0; i<dataLength; i++) {
				this.data[i] = message[26+i];
			}
		}
		
		this.crcLowByte = message[length-3];
		this.crcHighByte = message[length-2];
		this.packageEnd = message[length-1];
		
	}
	
	
	/**
	 * 将数据装换为字节数组
	 * @return
	 */
	public byte[] getBytesFromDTUDataPackage() {
		//数据长度为基本长度(29)+数据区长度
		int length = 29+(this.data==null?0:this.data.length);
		byte lowByteLength = (byte) (length%256);
		byte highByteLength = (byte) (length/256);
		
		byte[] messageByte = new byte[length];
		messageByte[0] = this.packageHead;
		messageByte[1] = lowByteLength;
		messageByte[2] = highByteLength;
		messageByte[3] = this.orderType;
		messageByte[4] = this.orderWord;
		messageByte[5] = this.orderNumLowByte;
		messageByte[6] = this.orderNumHightByte;
		messageByte[7] = this.extendInfo[0];
		messageByte[8] = this.extendInfo[1];
		messageByte[9] = this.status;
		
		//machineId和token
		for(int i=0; i<8; i++) {
			messageByte[10+i] = this.machineId[i];
			messageByte[18+i] = this.token[i];
		}
		
		//this.machineId = new byte[]{message[10], message[11], message[12], 0, 0, 0, 0, 0};
		if(length>29) {//至少29byte
			int dataLength = length-29;
			for(int i=0; i<dataLength; i++) {
				messageByte[26+i] = this.data[i];
			}
		}
		
		messageByte[length-3] = this.crcLowByte;
		messageByte[length-2] = this.crcHighByte;
		messageByte[length-1] = this.packageEnd;
		
		return messageByte;
	}
	
	

	public byte getPackageHead() {
		return packageHead;
	}


	public void setPackageHead(byte packageHead) {
		this.packageHead = packageHead;
	}


	public byte getLengthLowByte() {
		return lengthLowByte;
	}


	public void setLengthLowByte(byte lengthLowByte) {
		this.lengthLowByte = lengthLowByte;
	}


	public byte getLengthHighByte() {
		return lengthHighByte;
	}


	public void setLengthHighByte(byte lengthHighByte) {
		this.lengthHighByte = lengthHighByte;
	}


	public byte getOrderType() {
		return orderType;
	}


	public void setOrderType(byte orderType) {
		this.orderType = orderType;
	}


	public byte getOrderWord() {
		return orderWord;
	}


	public void setOrderWord(byte orderWord) {
		this.orderWord = orderWord;
	}


	public byte getOrderNumLowByte() {
		return orderNumLowByte;
	}


	public void setOrderNumLowByte(byte orderNumLowByte) {
		this.orderNumLowByte = orderNumLowByte;
	}


	public byte getOrderNumHightByte() {
		return orderNumHightByte;
	}


	public void setOrderNumHightByte(byte orderNumHightByte) {
		this.orderNumHightByte = orderNumHightByte;
	}


	public byte[] getExtendInfo() {
		return extendInfo;
	}


	public void setExtendInfo(byte[] extendInfo) {
		this.extendInfo = extendInfo;
	}


	public byte getStatus() {
		return status;
	}


	public void setStatus(byte status) {
		this.status = status;
	}


	public byte[] getMachineId() {
		return machineId;
	}


	public void setMachineId(byte[] machineId) {
		this.machineId = machineId;
	}


	public byte[] getToken() {
		return token;
	}


	public void setToken(byte[] token) {
		this.token = token;
	}


	public byte[] getData() {
		return data;
	}


	public void setData(byte[] data) {
		this.data = data;
	}


	public byte getCrcLowByte() {
		return crcLowByte;
	}


	public void setCrcLowByte(byte crcLowByte) {
		this.crcLowByte = crcLowByte;
	}


	public byte getCrcHighByte() {
		return crcHighByte;
	}


	public void setCrcHighByte(byte crcHighByte) {
		this.crcHighByte = crcHighByte;
	}


	public byte getPackageEnd() {
		return packageEnd;
	}


	public void setPackageEnd(byte packageEnd) {
		this.packageEnd = packageEnd;
	}
	
	
	
	
}
