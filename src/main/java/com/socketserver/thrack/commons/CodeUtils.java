package com.socketserver.thrack.commons;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class CodeUtils
{
	private static final Log logger = LogFactory.getLog(CodeUtils.class.getName());

	public static final byte charToByte(char c)
	{
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static final byte[] hexStringToBytes(String hexString, int ext)
	{
		if (hexString == null || hexString.equals(""))
		{
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length + ext];
		for (int i = 0; i < length; i++)
		{
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	public static final byte[] hexStringToBytes(String hexString)
	{
		return hexStringToBytes(hexString, 0);
	}

	public static final byte[] crc16(byte[] buf, int len)
	{
		int crc = 0x0000FFFF;
		int poly = 0x0000A001;
		int i, j;

		if (buf == null || len <= 0)
			return null;

		for (i = 0; i < len; i++)
		{
			crc ^= ((int) buf[i] & 0x000000FF);
			for (j = 0; j < 8; j++)
			{
				if ((crc & 0x00000001) != 0)
				{
					crc >>= 1;
					crc ^= poly;
				} else
				{
					crc >>= 1;
				}
			}
		}

		// byte lo = (byte) (crc & 0x000000FF);
		// byte hi = (byte) (crc >> 8);

		byte[] bcrc = new byte[2];
		bcrc[0] = (byte) (crc & 0x000000FF);
		bcrc[1] = (byte) (crc >> 8);
		return bcrc;
	}

	public static boolean checkCRC(byte[] pdu)
	{
		boolean crcOK = false;
		try
		{
			byte[] ccrc = CodeUtils.crc16(pdu, pdu.length - 2);
			crcOK = (ccrc[0] == pdu[pdu.length - 2] && ccrc[1] == pdu[pdu.length - 1]);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		//logger.info("crcOK=" + crcOK);
		return crcOK;
	}

	public static final String getHexString(byte[] buf, int start, int len)
	{
		StringBuffer sb = new StringBuffer(1024);
		for (int i = start; i < len; i++)
		{
			sb.append(String.format("%02X ", buf[i]));
		}
		return sb.toString();
	}


	public static final String getHexStringNoBlank(byte[] buf, int start, int len)
	{
		StringBuffer sb = new StringBuffer(1024);
		for (int i = start; i < len; i++)
		{
			sb.append(String.format("%02X", buf[i]));
		}
		return sb.toString();
	}


	/**
	 * 有空格的String  如  XX AA OA
	 * @param buf
	 * @return
	 */
	public static String getHexString(byte[] buf)
	{
		return getHexString(buf, 0, buf.length);
	}

	/**
	 * 无空格string  如  XXAAOA
	 * @param buf
	 * @return
	 */
	public static String getHexStringNoBlank(byte[] buf)
	{
		return getHexStringNoBlank(buf, 0, buf.length);
	}


	public static void main(String[] args)
	{
		// byte[] bytes = { 0x02, 0x07 };
		// byte[] bytes = hexStringToBytes("0207");
		// byte[] bytes = hexStringToBytes("01031670000E");
		// byte[] bytes =
		// hexStringToBytes("01031CA01100000000000000000000000000000F01010001231A0000040000");
		byte[] bytes = hexStringToBytes("010316780001", 0);
		byte[] bcrc = crc16(bytes, bytes.length);
		byte lo = bcrc[0];
		byte hi = bcrc[1];
		System.out.println("low = " + String.format("%02X", lo));
		System.out.println("high = " + String.format("%02X", hi));
	}

	public final static byte[] genMBADU(String reqData)
	{
		byte[] buf = hexStringToBytes(reqData, 2);
		byte[] bcrc = crc16(buf, buf.length - 2);
		buf[buf.length - 2] = bcrc[0];
		buf[buf.length - 1] = bcrc[1];
		return buf;
	}

}
