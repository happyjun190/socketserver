package com.socketserver.chat.model.user;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 包含machineID与类型
 * @author wushenjun
 *
 */
public class SocketUser implements Comparable<SocketUser>, Serializable {

	private static final long serialVersionUID = -5959203587135357559L;
	
	private Integer userId;	// type = 3时，SCM版用户，这个userId 实际上是 providerId
	private Integer userType;
	private String deviceCustomId;	// 识别设备的Id, 用于支持SCM版多设备同时登录的需求
	
	private static Pattern toStringPattern = Pattern.compile("i(\\d+)t(\\d+)scm(\\d+)dCI(\\S*)");
	
	public SocketUser(Integer userId, Integer userType, String deviceCustomId) {
		this.userId = userId;
		this.userType = userType;
		this.deviceCustomId = deviceCustomId == null ? "" : deviceCustomId;
	}
	
	/**
	 * 从map生成User, map中的键值必须为"toid"、"totype"
	 * @param map
	 * @return
	 */
	public static SocketUser fromMap(Map<String, Object> map){
		return new SocketUser (
				Integer.valueOf( String.valueOf( map.get("toid"))),
				(Integer) map.get("totype"),
				(String) map.get("todi")
				);
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public Integer getUserType() {
		return userType;
	}
	
	
	public String getDeviceCustomId() {
		return deviceCustomId;
	}
	
	@Override
	public String toString() {
		return "i" + userId + "t" + userType + "dCI" + deviceCustomId;
	}
	
	public static SocketUser fromString(String userString) {
		Matcher matcher = toStringPattern.matcher(userString);
		matcher.matches();
		return new SocketUser(
				Integer.valueOf( matcher.group(1) ),
				Integer.valueOf( matcher.group(2) ),
				String.valueOf( matcher.group(3) )
		);
	}

	@Override
	public boolean equals( Object obj ) {
		if (obj == null) {
	        return false;
	    }
	    if (getClass() != obj.getClass()) {
	        return false;
	    }
	    final SocketUser that = (SocketUser) obj;
		return this.userId.equals( that.userId )
				&& this.userType.equals( that.userType )
				&& this.deviceCustomId.equals( that.deviceCustomId );
	}
	
	@Override
	public int hashCode() {
//		return (int)(userId * 17) + userType * 31 + scmUserId;
		int hash = 3;
		hash = 7 * hash + userId.intValue();
		hash = 7 * hash + userType;
		hash = 7 * hash + deviceCustomId.hashCode();
		return hash;
	}

	@Override
	public int compareTo(SocketUser that) {
		return Integer.valueOf( this.hashCode() ).compareTo(
				Integer.valueOf( that.hashCode() ) );
	}
	
	/**
	 * 自定义序列化方法，以缩短cacheable生成的key长度
	 * @param oos
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
	    oos.writeLong(this.userId);
	    oos.writeInt(this.userType);
	    oos.writeUTF(this.deviceCustomId);
	}	
	/**
	 * 自定义序列化方法，以缩短cacheable生成的key长度
	 * @param oos
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		userId = ois.readInt();
		userType = ois.readInt();
		deviceCustomId = ois.readUTF();
	}
	
	/*static public void main(String[] args){
		System.out.println("ok");
		Pattern pattern = Pattern.compile("i(\\d+)t(\\d+)dCI(\\S*)");
		Matcher matcher = pattern.matcher("i10t3dCI111");
		System.out.println(matcher.matches());
		System.out.println(matcher.group(1));
		System.out.println(matcher.group(2));
		System.out.println(matcher.group(3));
		
		
		System.out.println(fromString("i10t3dCIbd0abd8274afaa1dfef5c6dda9e849c7f1fc0241"));
	}
*/
	
}
