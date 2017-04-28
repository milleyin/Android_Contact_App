package com.dongji.app.entity;

import com.dongji.app.addressbook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactEditableBean  {

	int type ;
	public static final int CONTACT_EDITABLE_TYPE_MOBILE = 0;  //手机
	public static final int CONTACT_EDITABLE_TYPE_HOME_PHONE = 1; //固话
	public static final int CONTACT_EDITABLE_TYPE_EMAIL = 2; //邮箱 
	public static final int CONTACT_EDITABLE_TYPE_ADDRESS = 3; //地址 
	public static final int CONTACT_EDITABLE_TYPE_WEBSITE = 4; //网页
	public static final int CONTACT_EDITABLE_TYPE_NOTE = 5; //备注
	
	long data_id;
	
	String title ;
	String content;
	
	int phone_type;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getData_id() {
		return data_id;
	}
	public void setData_id(long data_id) {
		this.data_id = data_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getPhone_type() {
		return phone_type;
	}
	public void setPhone_type(int phone_type) {
		this.phone_type = phone_type;
	}
}
