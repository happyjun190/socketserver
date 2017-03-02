package com.socketserver.chat.model.user;


public class TabUser {
	private int id;
	private String login;
	private String password;
	private String phone;
	private String email;
	private int state;
	private int ctime;
	private int mtime;
	private String login_salt;
	private int type;
	private String role_name;
	private String role_id;
	private int is_bindphone;
	private String uname;//用户名
	private String address;//地址

	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getIs_bindphone() {
		return is_bindphone;
	}
	public void setIs_bindphone(int is_bindphone) {
		this.is_bindphone = is_bindphone;
	}
	public String getRole_name() {
		return role_name;
	}
	public void setRole_name(String role_name) {
		this.role_name = role_name;
	}
	public String getLogin_salt() {
		return login_salt;
	}
	public void setLogin_salt(String login_salt) {
		this.login_salt = login_salt;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	private String head_url; // 头像地址
	public String getRole_id() {
		return role_id;
	}
	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}
	private String real_name;
	private String loginSalt;// 用户密码加密干盐值
	private String qq;// qq
	private int isBindphone;// 是否绑定手机
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getCtime() {
		return ctime;
	}
	public void setCtime(int ctime) {
		this.ctime = ctime;
	}
	public int getMtime() {
		return mtime;
	}
	public void setMtime(int mtime) {
		this.mtime = mtime;
	}
	public String getHead_url() {
		return head_url;
	}
	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getLoginSalt() {
		return loginSalt;
	}
	public void setLoginSalt(String loginSalt) {
		this.loginSalt = loginSalt;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public int getIsBindphone() {
		return isBindphone;
	}
	public void setIsBindphone(int isBindphone) {
		this.isBindphone = isBindphone;
	}
	
}
