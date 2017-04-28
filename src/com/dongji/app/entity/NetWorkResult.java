package com.dongji.app.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络操作中的数据存储模型
 * @author 
 *
 */

public class NetWorkResult implements Serializable{
	private static final long serialVersionUID = 8855432315847881382L;
	/**
	 * 
	 */
	private String success;
	private int page;
	private int curpage;
    private String resultStr;//返回json的字符串
	private String download_url;//新版本下载地址
	private String version_number;//最新版本号
	private String state;
	private String send_date;
	private String cookieid;
	private ArrayList<ContactBean> contactBeans=new ArrayList<ContactBean>();
	private ArrayList<SmsContent> smsContents=new ArrayList<SmsContent>();
	private ArrayList<CallLogInfo> callLogInfos=new ArrayList<CallLogInfo>();
	private ArrayList<RemindBean> remindBeans=new ArrayList<RemindBean>();
	private ArrayList<SmsContent> smsContentFavorite=new ArrayList<SmsContent>();
	
	//无用
	private String error_message;
	private String timestamp;
	private boolean is_not_data;
	private String backUpSuccess;
	private List<String> sim = new ArrayList<String>();
	
	
	
	
	
	
	public String getCookieid() {
		return cookieid;
	}
	public void setCookieid(String cookieid) {
		this.cookieid = cookieid;
	}
	public ArrayList<ContactBean> getContactBeans() {
		return contactBeans;
	}
	public void setContactBeans(ArrayList<ContactBean> contactBeans) {
		this.contactBeans = contactBeans;
	}
	
	
	
	public ArrayList<RemindBean> getRemindBeans() {
		return remindBeans;
	}
	public void setRemindBeans(ArrayList<RemindBean> remindBeans) {
		this.remindBeans = remindBeans;
	}
	public ArrayList<SmsContent> getSmsContents() {
		return smsContents;
	}
	public void setSmsContents(ArrayList<SmsContent> smsContents) {
		this.smsContents = smsContents;
	}
	
	
	public ArrayList<SmsContent> getSmsContentFavorite() {
		return smsContentFavorite;
	}
	public void setSmsContentFavorite(ArrayList<SmsContent> smsContentFavorite) {
		this.smsContentFavorite = smsContentFavorite;
	}
	public ArrayList<CallLogInfo> getCallLogInfos() {
		return callLogInfos;
	}
	public void setCallLogInfos(ArrayList<CallLogInfo> callLogInfos) {
		this.callLogInfos = callLogInfos;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getCurpage() {
		return curpage;
	}
	public void setCurpage(int curpage) {
		this.curpage = curpage;
	}
	
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getDownload_url() {
		return download_url;
	}
	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}
	public String getVersion_number() {
		return version_number;
	}
	public void setVersion_number(String version_number) {
		this.version_number = version_number;
	}
	public List<String> getSim() {
		return sim;
	}
	public void setSim(List<String> sim) {
		this.sim = sim;
	}
	public String getResultStr() {
		return resultStr;
	}
	public void setResultStr(String resultStr) {
		this.resultStr = resultStr;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isIs_not_data() {
		return is_not_data;
	}
	public void setIs_not_data(boolean is_not_data) {
		this.is_not_data = is_not_data;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBackUpSuccess() {
		return backUpSuccess;
	}
	public void setBackUpSuccess(String backUpSuccess) {
		this.backUpSuccess = backUpSuccess;
	}
	public String getSend_date() {
		return send_date;
	}
	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}
	
}
