package com.dongji.app.entity;

/**
 * 
 * @author Administrator
 *
 */
public class MmsSmsContent   {
	
	public static final int M_TYPE_SMS = 0;
	public static final int M_TYPE_MMS = 1;
	
	int message_type  = -1; // 类型： 0 为短信  ； 1为 彩信

	long id;
	
	long date = -1; //时间
	
	public int getMessage_type() {
		return message_type;
	}

	public void setMessage_type(int message_type) {
		this.message_type = message_type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
}
