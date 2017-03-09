package com.socketserver.chat.dao.admin;


import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.socketserver.chat.model.admin.SysConfig;
import com.socketserver.chat.repositories.IndustrynetRepository;

public interface SysConfigDAO {

	@Select("select * from ts_sys_config where item=#{item}")
	public List<SysConfig> getSysConfigByItem(String item);
	
	//==================== For Cache =========================
	
	@Select("SELECT `values` FROM `ts_sys_config` WHERE item=#{item}")
	public String getValuesByItem(String item);

	
}
