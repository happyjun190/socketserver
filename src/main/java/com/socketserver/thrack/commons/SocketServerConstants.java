package com.socketserver.thrack.commons;

/**
 * socket server 常量
 * @author wushenjun
 *
 */
public class SocketServerConstants {

	
	//在machine.lastconnecttime.hsetkey 这个hmap 中保存设备id对应的最近在线时间
	public static final String MACHINE_LASTCONNECTTIME_HSETKEY = "machine.lastconnecttime.hsetkey";
	
	
	//通信协议 start
	//1、命令类别
	public static final byte ORDER_TYPE_LINK = (byte) 0xA0;//链路操作
	public static final byte ORDER_TYPE_DATATRANSFORM = (byte) 0xAA;//数据传输
	public static final byte ORDER_TYPE_TERMINAL = (byte) 0xF0;//终端相关操作
	
	//2、命令字
	//通信协议 end
	public static final byte ORDER_WORD_AUTH = 0x00;//链路操作-注册登录
	public static final byte ORDER_WORD_HEARTBEAT = 0x01;//链路操作-心跳数据包
	public static final byte ORDER_WORD_WINDOWSCLIENT_AUTH = 0x02;//链路操作-windows-注册登录
	public static final byte ORDER_WORD_WINDOWSCLIENT_HEARTBEAT = 0x03;//链路操作-windows-心跳数据包
	
	public static final byte ORDER_WORD_SERVER_REVEIVE_DATA = (byte) 0xEE;//数据传输-设备向平台透传数据
	public static final byte ORDER_WORD_SERVER_SEND_DATA = (byte) 0xFF;//数据传输-平台向设备透传数据
	
	public static final byte ORDER_WORD_CLIENTRESTART = 0x55;//终端相关操作-重启设备
	public static final byte ORDER_WORD_SENDTIME = (byte) 0x88;//终端相关操作-下发时钟
	public static final byte ORDER_WORD_SETBAUDRATE = (byte) 0x88;//终端相关操作-设置串口波特率
	
	//token的最大保存时间
	public static final int TOKEN_LIFETIME_ONEDAY = 24*3600;
	//token 前缀
	public static final String TOKEN_PRIFIX_TO_MACHINE = "token.to.machine:";
	public static final String TOEKN_PRIFIX_TO_WEBUSER = "web.token.to.userid:";
	
	
	//命令执行状态, 0x00 成功  0x01 失败  0x02 主动发送 0x03 不支持 0x04表示数据透传数据接收到了（设备向服务器回复或向服务器主动发送填写这里）
	//设备向服务器回复时 命令类别 命令字 命令序号 和接收到的那条一样 按要求填写状态 数据区不用写
	public static final byte ORDER_EXE_STATUS_SUCCESS = 0x00;//成功
	public static final byte ORDER_EXE_STATUS_FAILURE = 0x01;//失败
	public static final byte ORDER_EXE_STATUS_SELFSEND = 0x02;//主动发送，如果是服务端发送，也用这个状态给客户端
	public static final byte ORDER_EXE_STATUS_NOSUPPORT = 0x03;//不支持
	//public static final byte ORDER_EXE_STATUS_HASRECEIVE = 0x04;//表示数据透传数据接收到了（设备向服务器回复或向服务器主动发送填写这里）
	
	
	// 触发IDLE_STATE_HANDLER时间
	public static final int READ_IDLE_TIMEOUT_IN_SECONDS = 150;
//	public static final int READ_IDLE_TIMEOUT_IN_SECONDS = 1500;  // for test
	
	// 用户连接记录在redis中的保存时间
	public static final int USER_TO_SOCKET_SERVER_REDIS_LIFETIME_SECONDS = 60;

	
	public static final int INTEVAL_UNACTIVE_CHANNEL_CLEANING = 5000;
}
