package com.socketserver.thrack.server.client;

public class Constants
{
	public static final int LOGIN_KEY_LENGTH = 16;
	public static final long CLIENT_TIMEOUT = 120 * 1000l;
	public static final long CLIENT_CACHE_TIMEOUT = 3600 * 1000l; // 一小时的缓存

	public static final String HEART_BEAT_MSG = "3030";//心跳消息


	// 触发IDLE_STATE_HANDLER时间
	public static final int READ_IDLE_TIMEOUT_IN_SECONDS = 150;

	public static final int INTEVAL_UNACTIVE_CHANNEL_CLEANING = 10000;//10000毫秒



	//起始读取地址
	public static final String ADDR_1600 = "1600";
	public static final String ADDR_1616 = "1616";
	public static final String ADDR_1652 = "1652";
	public static final String ADDR_1670 = "1670";
	public static final String ADDR_168E = "168E";
	public static final String ADDR_1690 = "1690";
	public static final String ADDR_1800 = "1800";

	//StartAddrAndReadSize中最大的index
	public static final int MAX_INDEX_OF_ADDRESS = 7;


	//16进制 0 1 2 3 4 5 6 7 8 9 A B C D E F
	//起始地址和读取寄存器个数
	public enum StartAddrAndReadSize{
		ADDR_1600("1600",  14,  new byte[]{ 0x16,       0x00},  1),		//读取0x1600~0x160D段位14个寄存器数据，共 28byte
		ADDR_1616("1616",  10,  new byte[]{ 0x16,       0x16},  2),		//读取0x1616~0x161F段位10个寄存器数据，共 20byte
		ADDR_1652("1652",  10,  new byte[]{ 0x16,       0x52},  3),		//读取0x1652~0x165B段位10个寄存器数据，共 20byte
		ADDR_1670("1670",  14,  new byte[]{ 0x16,       0x70},  4),		//读取0x1670~0x167D段位14个寄存器数据，共 28byte
		ADDR_168E("168E",  2 ,  new byte[]{ 0x16, (byte)0x8E},  5),		//读取0x168E~0x168F段位 2个寄存器数据，共  4byte
		ADDR_1690("1690",  32,  new byte[]{ 0x16, (byte)0x90},  6),		//读取0x1690~0x16AF段位32个寄存器数据，共 64byte
		ADDR_1800("1800",  80,  new byte[]{ 0x18,       0x00},  7),		//读取0x1800~0x184F段位80个寄存器数据，共160byte
		;

		private final String address;
		private final int size;
		private final byte[] requestBytes;
		private final int index;

		StartAddrAndReadSize(String address, int size, byte[] requestBytes, int index) {
			this.address = address;
			this.size = size;
			this.requestBytes = requestBytes;
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

		public byte[] getRequestBytes() {
			return requestBytes;
		}

		//根据地址获取index
		public static int getIndexByAddress(String address) {
			for (StartAddrAndReadSize s : StartAddrAndReadSize.values()) {
				if(s.getAddress().equals(address)) {
					return s.getIndex();
				}
			}
			return -1;
		}

		//根据地址获取请求开始地址
		public static byte[] getRequestBytesByAddress(String address) {
			for (StartAddrAndReadSize s : StartAddrAndReadSize.values()) {
				if(s.getAddress().equals(address)) {
					return s.getRequestBytes();
				}
			}
			return null;
		}

		//根据地址获取请求寄存器个数
		public static int getSizeByAddress(String address) {
			for (StartAddrAndReadSize s : StartAddrAndReadSize.values()) {
				if(s.getAddress().equals(address)) {
					return s.getSize();
				}
			}
			return 0;
		}

		//根据index获取地址
		public static String getAddressByIndex(int index) {
			for (StartAddrAndReadSize s : StartAddrAndReadSize.values()) {
				if(s.getIndex()==index) {
					return s.getAddress();
				}
			}
			return null;
		}

	}


}
