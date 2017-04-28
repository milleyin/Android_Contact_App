package com.dongji.app.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Administrator
 *
 */
public class ContactBean {

	Long contact_id;
	
	String photo_id;
	
	String nick;
	
	String number;
	
	String sork_key;
	
	String name_pinyin; //名字的全拼音
	
	String name_pinyin_cap;//名字的拼音首写字母
	
	String name_letter;
	
	byte[] photo;
	
	int heat_progress; //热度
	
	private String organizations;//组织
	private String job;//职业
	private String brithday;//生日
	private String email;//邮箱
	private String address;//地址
	private String website;//网址
	private String notes;//备注
	List<String> numberlist = new ArrayList<String>();
	
	
	int gourp_id ; //联系人所在的分组id : -1 为未分组
	
	String area; //归属地
	
	public List<String> getNumberlist() {
		return numberlist;
	}

	public void setNumberlist(List<String> numberlist) {
		this.numberlist = numberlist;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getName_letter() {
		return name_letter;
	}

	public void setName_letter(String name_letter) {
		this.name_letter = name_letter;
	}

	public String getName_pinyin() {
		return name_pinyin;
	}

	public void setName_pinyin(String name_pinyin) {
		this.name_pinyin = name_pinyin;
	}

	String lookup_key;
	
	public String getSork_key() {
		return sork_key;
	}

	public void setSork_key(String sork_key) {
		this.sork_key = sork_key;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Long getContact_id() {
		return contact_id;
	}

	public void setContact_id(Long contact_id) {
		this.contact_id = contact_id;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getLookup_key() {
		return lookup_key;
	}

	public void setLookup_key(String lookup_key) {
		this.lookup_key = lookup_key;
	}

	public String getName_pinyin_cap() {
		return name_pinyin_cap;
	}

	public void setName_pinyin_cap(String name_pinyin_cap) {
		this.name_pinyin_cap = name_pinyin_cap;
	}

	public String getOrganizations() {
		return organizations;
	}

	public void setOrganizations(String organizations) {
		this.organizations = organizations;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getBrithday() {
		return brithday;
	}

	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getGourp_id() {
		return gourp_id;
	}

	public void setGourp_id(int gourp_id) {
		this.gourp_id = gourp_id;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
	public int getProgress() {
		return heat_progress;
	}

	public void setProgress(int progress) {
		this.heat_progress = progress;
	}
	
	public static class PhoneInfo{
		
		public int type ;
		public String number;
		
		public PhoneInfo(){}
		
		public PhoneInfo(int type,String number){
			this.type = type;
			this.number = number;
		}
	}
	
	public static class EmailInfo {
		public int type;
		public String email;
		
		public EmailInfo(){}
		
		public EmailInfo(String email)
		{
			this.email = email;
		}
	}
	
	public List<PhoneInfo> phoneList = new ArrayList<ContactBean.PhoneInfo>();
	public List<EmailInfo> emailList = new ArrayList<ContactBean.EmailInfo>();
	public List<String> addressList = new ArrayList<String>();
	public List<String> webSiteList = new ArrayList<String>();
	public List<String> noteList = new ArrayList<String>();
	
	public List<String> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<String> addressList) {
		this.addressList = addressList;
	}

	public List<String> getWebSiteList() {
		return webSiteList;
	}

	public void setWebSiteList(List<String> webSiteList) {
		this.webSiteList = webSiteList;
	}

	public List<String> getNoteList() {
		return noteList;
	}

	public void setNoteList(List<String> noteList) {
		this.noteList = noteList;
	}

	public List<PhoneInfo> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<PhoneInfo> phoneList) {
		this.phoneList = phoneList;
	}

	public List<EmailInfo> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<EmailInfo> emailList) {
		this.emailList = emailList;
	}
}
