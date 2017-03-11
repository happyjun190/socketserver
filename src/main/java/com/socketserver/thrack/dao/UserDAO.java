package com.socketserver.thrack.dao;



import org.apache.ibatis.annotations.Param;

import com.socketserver.thrack.model.user.TabUser;

public interface UserDAO {
	
	/**
	 * 获取用户信息
	 * @param userAccount
	 * @return
	 */
	public TabUser getUserInfo(@Param("userAccount")String userAccount);
}
