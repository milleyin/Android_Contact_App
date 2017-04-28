package com.dongji.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 被加密的联系人信息
 * @author Administrator
 *
 */
public class EnContact {
	
	String contactId; //id
	
	List<String> numbers = new ArrayList<String>(); //所有号码

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public List<String> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<String> numbers) {
		this.numbers = numbers;
	}
}
