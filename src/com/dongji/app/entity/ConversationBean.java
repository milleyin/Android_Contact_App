package com.dongji.app.entity;

import java.util.List;

public class ConversationBean {
	long thread_id;
	long date ;
	
	int type;  //会话类型，0:普通会话（只有一个接收者），1:广播会话（多个接收者）
	
	int message_count;
	
	String recipient_ids; // 接收者（canonical_addresses表的id）列表，所有接收者以空格隔开
	String [] recipient_ids_array; 
	
	List<String> address; //此会话下所有的号码
	String address_str;
	
	boolean isNeedRequeryContacts = false; //是否需要重新检索联系人的名称
	String contacts_str; //所有号码对应的联系人名称
	
	String photo_id  ; //只显示单个联系人会话的头像，群发短信不显示头像
	
	String snippet;
	int snippet_cs;
	
	int read; // 是否有未读信息：0-未读，1-已读
	
	MmsSmsContent mmsSmsContent; //此会话下最新的消息(包括短信，彩信 ， 以及草稿)
	
	boolean isHaveDraft; //是否有草稿
	
	public long getThread_id() {
		return thread_id;
	}

	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMessage_count() {
		return message_count;
	}

	public void setMessage_count(int message_count) {
		this.message_count = message_count;
	}

	public String getRecipient_ids() {
		return recipient_ids;
	}

	public void setRecipient_ids(String recipient_ids) {
		this.recipient_ids = recipient_ids;
	}

	public String[] getRecipient_ids_array() {
		return recipient_ids_array;
	}

	public void setRecipient_ids_array(String[] recipient_ids_array) {
		this.recipient_ids_array = recipient_ids_array;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public int getSnippet_cs() {
		return snippet_cs;
	}

	public void setSnippet_cs(int snippet_cs) {
		this.snippet_cs = snippet_cs;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public List<String> getAddress() {
		return address;
	}

	public void setAddress(List<String> address) {
		this.address = address;
		
		String s  = "";
		
		for(String ad:address)
		{
			s+=ad+",";
		}
		this.address_str = s;
	}
	
	public MmsSmsContent getMmsSmsContent() {
		return mmsSmsContent;
	}

	public void setMmsSmsContent(MmsSmsContent mmsSmsContent) {
		this.mmsSmsContent = mmsSmsContent;
	}

	public boolean isHaveDraft() {
		return isHaveDraft;
	}

	public void setHaveDraft(boolean isHaveDraft) {
		this.isHaveDraft = isHaveDraft;
	}

	public String getContacts_str() {
		return contacts_str;
	}

	public void setContacts_str(String contacts_str) {
		this.contacts_str = contacts_str;
	}

	public boolean isNeedRequeryContacts() {
		return isNeedRequeryContacts;
	}

	public void setNeedRequeryContacts(boolean isNeedRequeryContacts) {
		this.isNeedRequeryContacts = isNeedRequeryContacts;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}
}

