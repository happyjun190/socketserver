package com.socketserver.thrack.server.client;

public class Constants
{
	public static final int LOGIN_KEY_LENGTH = 16;
	public static final long CLIENT_TIMEOUT = 120 * 1000l;
	public static final long CLIENT_CACHE_TIMEOUT = 3600 * 1000l; // 一小时的缓存

	public static final String HEART_BEAT_MSG = "3030";//心跳消息


	//起始读取地址
	public static final String ADDR_1600 = "1600";
	public static final String ADDR_1616 = "1616";
	public static final String ADDR_1652 = "1652";
	public static final String ADDR_1670 = "1670";
	public static final String ADDR_168E = "168E";
	public static final String ADDR_1690 = "1690";
	public static final String ADDR_1800 = "1800";


	//16进制 0 1 2 3 4 5 6 7 8 9 A B C D E F
	//起始地址和读取寄存器个数
	public enum StartAddrAndReadSize{
		ADDR_1600("1600", 14,  1),		//读取0x1600~0x160D段位14个寄存器数据，共 28byte
		ADDR_1616("1616", 10,  2),		//读取0x1616~0x161F段位10个寄存器数据，共 20byte
		ADDR_1652("1652", 10,  3),		//读取0x1652~0x165B段位10个寄存器数据，共 20byte
		ADDR_1670("1670", 14,  4),		//读取0x1670~0x167D段位14个寄存器数据，共 28byte
		ADDR_168E("168E", 2 ,  5),		//读取0x168E~0x168F段位 2个寄存器数据，共  4byte
		ADDR_1690("1690", 32,  6),		//读取0x1690~0x16AF段位32个寄存器数据，共 64byte
		ADDR_1800("1800", 80,  7),		//读取0x1800~0x184F段位80个寄存器数据，共160byte
		;

		private final String address;
		private final int size;
		private final int index;

		StartAddrAndReadSize(String address, int size, int index) {
			this.address = address;
			this.size = size;
			this.index = index;
		}

		public String getAddress() {
			return address;
		}

		public int getSize() {
			return size;
		}

		public int getIndex() {
			return index;
		}
		public int getIndex(String address) {
			for (StartAddrAndReadSize s : StartAddrAndReadSize.values()) {
				if(s.getAddress().equals(address)) {
					return s.getIndex();
				}
			}
			return index;
		}
	}


}
