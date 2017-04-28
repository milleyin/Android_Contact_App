package com.dongji.app.entity;

public class GroupInfo {
	private long group_id;//组id
	private String group_name;//组名称
	
	private String phone_name;
	private String phone_number;
	private boolean is_group;
	private boolean is_child;
	private String  person_id;//联系人id
	
	String photo_id; //头像
	
	int group_member_count ; //此分组一个有多少联系人
	
	boolean isChecked;
	
	private String area; //归属地
	
	int index;
	
	public long getGroup_id() {
		return group_id;
	}
	public void setGroup_id(long group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getPhone_name() {
		return phone_name;
	}
	public void setPhone_name(String phone_name) {
		this.phone_name = phone_name;
	}
	public String getPhone_number() {
		return phone_number;
	}
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}
	public boolean isIs_group() {
		return is_group;
	}
	public void setIs_group(boolean is_group) {
		this.is_group = is_group;
	}
	public boolean isIs_child() {
		return is_child;
	}
	public void setIs_child(boolean is_child) {
		this.is_child = is_child;
	}
	public String getPerson_id() {
		return person_id;
	}
	public void setPerson_id(String person_id) {
		this.person_id = person_id;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getPhoto_id() {
		return photo_id;
	}
	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public int getGroup_member_count() {
		return group_member_count;
	}
	public void setGroup_member_count(int group_member_count) {
		this.group_member_count = group_member_count;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
}
