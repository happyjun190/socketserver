package com.socketserver.thrack.server.interactive;


import com.socketserver.thrack.commons.CodeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class InverterRequest
{
	byte address;
	byte cmd;
	byte[] register = new byte[2];
	byte[] cnt = new byte[2];
	byte[] crc = new byte[2];

	public InverterRequest()
	{
		address = 0x01;
		cmd = 0x03;
		register = new byte[] { 0x16, 0x70 }; // TODO
		cnt = new byte[] { 0x00, 0x0E };
	}

	public InverterRequest(byte address, byte cmd, short register, short cnt)
	{
		this.address = address;
		this.cmd = cmd;
		this.register[0] = (byte) (register >> 8);
		this.register[1] = (byte) (register & 0x00FF);
		this.cnt[0] = (byte) (cnt >> 8);
		this.cnt[1] = (byte) (cnt & 0x00FF);
	}

	public byte[] encode() throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(new byte[] { address });
		bos.write(new byte[] { cmd });
		bos.write(register);
		bos.write(cnt);
		byte[] all = bos.toByteArray();
		byte[] crc = CodeUtils.crc16(all, all.length);

		bos.write(crc);

		return bos.toByteArray();
	}

	/*public static void main(String[] args) throws IOException
	{
		InverterRequest mb = new InverterRequest();
		byte[] bytes = mb.encode();

		for (int i = 0; i < bytes.length; i++)
		{
			System.out.print(String.format("%02X ", bytes[i]));
		}
		System.out.println();
	}*/
}
