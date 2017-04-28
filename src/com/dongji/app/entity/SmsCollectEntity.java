package com.dongji.app.entity;

public class SmsCollectEntity {

	int favorite_id;// 自增id
	String thread_id;// 会话id
	String content_id;// 某条短信的具体id
	String favorite_content;// 短信内容
	String content_time;// 短信时间
	String favorite_sender;// 短信发送者
	String favorite_number;// 电话号码
	String favorite_del1;// 备用字段
	String favorite_del2;// 备用字段
	String favorite_del3;// 备用字段

	public int getFavorite_id() {
		return favorite_id;
	}

	public void setFavorite_id(int favorite_id) {
		this.favorite_id = favorite_id;
	}

	public String getThread_id() {
		return thread_id;
	}

	public void setThread_id(String thread_id) {
		this.thread_id = thread_id;
	}

	public String getContent_id() {
		return content_id;
	}

	public void setContent_id(String content_id) {
		this.content_id = content_id;
	}

	public String getFavorite_content() {
		return favorite_content;
	}

	public void setFavorite_content(String favorite_content) {
		this.favorite_content = favorite_content;
	}

	public String getContent_time() {
		return content_time;
	}

	public void setContent_time(String content_time) {
		this.content_time = content_time;
	}

	public String getFavorite_sender() {
		return favorite_sender;
	}

	public void setFavorite_sender(String favorite_sender) {
		this.favorite_sender = favorite_sender;
	}

	public String getFavorite_number() {
		return favorite_number;
	}

	public void setFavorite_number(String favorite_number) {
		this.favorite_number = favorite_number;
	}

	public String getFavorite_del1() {
		return favorite_del1;
	}

	public void setFavorite_del1(String favorite_del1) {
		this.favorite_del1 = favorite_del1;
	}

	public String getFavorite_del2() {
		return favorite_del2;
	}

	public void setFavorite_del2(String favorite_del2) {
		this.favorite_del2 = favorite_del2;
	}

	public String getFavorite_del3() {
		return favorite_del3;
	}

	public void setFavorite_del3(String favorite_del3) {
		this.favorite_del3 = favorite_del3;
	}

}
