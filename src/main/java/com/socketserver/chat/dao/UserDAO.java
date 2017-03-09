package com.socketserver.chat.dao;



import org.apache.ibatis.annotations.Param;

import com.socketserver.chat.model.user.TabUser;
import com.socketserver.chat.repositories.IndustrynetRepository;

public interface UserDAO {
	
	/**
	 * 获取用户信息
	 * @param userAccount
	 * @return
	 */
	public TabUser getUserInfo(@Param("userAccount")String userAccount);
}
