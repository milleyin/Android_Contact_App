package com.dongji.app.entity;

public class WhiteListEntity {

	long black_id;
	String contact_id;
	String contact_name;
	String number;
	byte photo;

	public long getBlack_id() {
		return black_id;
	}

	public void setBlack_id(long black_id) {
		this.black_id = black_id;
	}

	public String getContact_id() {
		return contact_id;
	}

	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public byte getPhoto() {
		return photo;
	}

	public void setPhoto(byte photo) {
		this.photo = photo;
	}

}
