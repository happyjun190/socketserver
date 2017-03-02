package com.socketserver.chat.commons;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 各种工具方法类
 * @author wushenjun
 * 16/10/30
 */
public class CommonUtils {
	//随机String生成的基本数据
	private static final char[] charData = {'A', 'B', 'C', 'D', 'E', 'F',
			  'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			  'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
			  '4', '5', '6', '7', '8', '9', '@', '!', '#', '$', '%', '&',
			  '*','+',',','-','.',':','=','?','|','{','}','~'};
	
	/**
	 * 数据接收报文转义
	 *  数据报文中，除包头包尾外，其它任何字节出现 0x55 都需要进行转义
	 *	接收流程:
	 *		0x54 0x01 -> 0x55
	 *		0x54 0x02 -> 0x54
	 *		0xA9 0x01 -> 0xAA
	 *		0xA9 0x02 -> 0xA9
	 * 该map为接收的转义map，key:接收到发送时转义的第一个byte，value:下标0、2为接收到发送时转义的第二个byte，下标1、3为最终接收转义的字符
	 */
	@SuppressWarnings("serial")
	private static final Map<Object, Object> receiveChangeDefineByte = new HashMap<Object, Object>(){{
		put((byte)0x54, new byte[] { 0x01, 0x55});
		put((byte)0x54, new byte[] { 0x02, 0x54});
		put((byte)0xA9, new byte[] { 0x01, (byte)0xAA, 0x02, (byte)0xA9});
	}};
	
	
	/**
	 * 数据发送报文转义
	 *  数据报文中，除包头包尾外，其它任何字节出现 0x55 都需要进行转义
	 *	发送流程: 
	 *  	0x55 -> 0x54 0x01
	 *		0x54 -> 0x54 0x02
	 *		0xAA -> 0xA9 0x01
	 *		0xA9 -> 0xA9 0x02
	 */
	@SuppressWarnings("serial")
	private static final Map<Object, Object> sendChangeDefineByte = new HashMap<Object, Object>(){{
		put((byte)0x55, new byte[] { 0x54, 0x01});
		put((byte)0x54, new byte[] { 0x54, 0x02});
		put((byte)0xAA, new byte[] { (byte)0xA9, 0x01});
		put((byte)0xA9, new byte[] { (byte)0xA9, 0x02});
	}};
	
	
	//char数组长度
	private static final int  charDataLength = 54;
	
	/**
	 * 生成16进制通过'-'连接的mac-address
	 * @param machineIdBytes
	 * @return
	 */
	public static String generateMachineIdFromBytes(byte[] machineIdBytes) {
		int index=0;
		String machineId = "";
		for (byte b:machineIdBytes) { 
			index++;
		    String hex = toHexString(b);
		    if(index==machineIdBytes.length) {
		    	machineId = machineId+hex;
		    } else {
		    	machineId = machineId+hex+"-";
		    }
		}
		return machineId;
	}
	
	
	/*public static void main(String[] args) {
		byte[] machineIdBytes = new byte[] {0, 0, 0, 12, 41, 97, 99, -45};
		String machineId = getMachineIdFromBytes(machineIdBytes);
		System.out.println(machineId);
	}*/
	
	/**
	 * 将字节数据转换为hex(16进制数据),不足补0
	 * @return
	 */
	public static String toHexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF); 
	    if (hex.length() == 1) { 
	    	hex = '0' + hex; 
	    }
		return hex;
	}
	
	
	/**
	 * 将byte数组转换为16进制字符串
	 * @param bytes
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for(byte b:bytes) {
			sb.append(toHexString(b));
		}
		return sb.toString();
	}
	
	
	/**
	 * 生成8个字节的随机字符串
	 * @return
	 */
	public static String gen8ByteStringToken(){
		int i;
		int count = 0;
		
        StringBuffer pwd = new StringBuffer("");
        Random r = new Random();
        while(count < 8){
        	i = Math.abs(r.nextInt(charDataLength));
        	if (i >= 0 && i < charDataLength) {
        		pwd.append(charData[i]);
        		count ++;
        	}
        }
        return pwd.toString();
	}
	
	
	/**
	 * 数据报文转义(接收转义)
	 * @param receiveMessage
	 *  数据报文中，除包头包尾外，其它任何字节出现 0x55 都需要进行转义
	 *	发送流程: 
	 *  	0x55 -> 0x54 0x01
	 *		0x54 -> 0x54 0x02
	 *		0xAA -> 0xA9 0x01
	 *		0xA9 -> 0xA9 0x02
	 *
	 *	接收流程:
	 *		0x54 0x01 -> 0x55
	 *		0x54 0x02 -> 0x54
	 *		0xA9 0x01 -> 0xAA
	 *		0xA9 0x02 -> 0xA9
	 * @return
	 */
	public static byte[] changeReceiveBytesDefine(byte[] receiveMessage) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(2800);
		
		int length = receiveMessage.length;
		//包头包尾不需要转义
		byteBuffer.put(receiveMessage[0]);
		byte[] receiveMapByte;
		for(int i=1; i<length-1; i++) {
			receiveMapByte = (byte[]) receiveChangeDefineByte.get(receiveMessage[i]);
			//判断是否可能存在转义，如果不存在，直接put到byteBuffer
			if(receiveMapByte!=null) {
				//当前下标之后的一个字节
				byte nextByte = receiveMessage[i+1];
				//具体看receiveChangeDefineByte的组成
				if(nextByte==receiveMapByte[0]) {
					byteBuffer.put(receiveMapByte[1]);
					i++;//下标+1
				} else {
					if(receiveMapByte.length==4&&nextByte==receiveMapByte[2]) {
						byteBuffer.put(receiveMapByte[3]);
						i++;//下标+1
					} else {
						byteBuffer.put(receiveMessage[i]);
					}
				}
			} else {
				byteBuffer.put(receiveMessage[i]);
			}
		}
		//包头包尾不需要转义
		byteBuffer.put(receiveMessage[length-1]);
		
		//byteBuffer长度
		int byteBufferLength = byteBuffer.position();
		byte[] resultBytes = new byte[byteBufferLength];
		for(int i=0; i<byteBufferLength; i++) {
			resultBytes[i] = byteBuffer.get(i);
		}
		return resultBytes;
	}
	
	
	/**
	 * 数据报文转义(发送转义)
	 * @param sendMessage
	 *  数据报文中，除包头包尾外，其它任何字节出现 0x55 都需要进行转义
	 *	发送流程: 
	 *  	0x55 -> 0x54 0x01
	 *		0x54 -> 0x54 0x02
	 *		0xAA -> 0xA9 0x01
	 *		0xA9 -> 0xA9 0x02
	 *
	 *	接收流程:
	 *		0x54 0x01 -> 0x55
	 *		0x54 0x02 -> 0x54
	 *		0xA9 0x01 -> 0xAA
	 *		0xA9 0x02 -> 0xA9
	 * @return
	 */
	public static byte[] changeSendBytesDefine(byte[] sendMessage) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(2800);
		
		int length = sendMessage.length;
		//包头包尾不需要转义
		byteBuffer.put(sendMessage[0]);
		byte[] sendMapByte;
		for(int i=1; i<length-1; i++) {
			sendMapByte = (byte[]) sendChangeDefineByte.get(sendMessage[i]);
			//判断是否可能存在转义，如果不存在，直接put到byteBuffer
			if(sendMapByte!=null) {
				byteBuffer.put(sendMapByte[0]);
				byteBuffer.put(sendMapByte[1]);
			} else {
				byteBuffer.put(sendMessage[i]);
			}
		}
		//包头包尾不需要转义
		byteBuffer.put(sendMessage[length-1]);
		
		//byteBuffer长度
		int byteBufferLength = byteBuffer.position();
		byte[] resultBytes = new byte[byteBufferLength];
		for(int i=0; i<byteBufferLength; i++) {
			resultBytes[i] = byteBuffer.get(i);
		}
		return resultBytes;
	}
	
	
	
	/*public static void main(String[] args) {
		byte[] resultBytes = changeReceiveBytesDefine(new byte[]{85, 35, 0, -87, 1, -18, 53, 0, 0, 0, 2, 0, 0, 0, 12, 41, 97, 99, -45, 72, 49, 73, 78, 124, 43, 43, 51, 49, 50, 51, 52, 53, 54, -58, 56, -86});
		System.out.println(Arrays.toString(resultBytes));
	}*/
}
