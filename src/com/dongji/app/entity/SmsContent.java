package com.dongji.app.entity;

public class SmsContent extends MmsSmsContent {
	private long thread_id;
	private String person_id;//发送人在联系人列表中的id，如果为空表示为未保存到联系人列表
	private int read;
	private String sms_name;//名字
	private String sms_number;//电话号码
	private String sms_area;//区域
	private int sms_type;//类型
	private String sms_duration;//
	private String send_type;//发送类型，0，自己发送，1接收
	private String sms_body;//信息内容
	private int status;
	private String subject;
	
	private int position; //短信在list中的位置
	private int long_position;
	
	private int typeId;
	
	private byte[] photo;
	

	public int session_count = -1 ;  //同一会话下的短信总数
	
	private Long SystemTime;
	
	int seen;
	
	public Long getSystemTime() {
		return SystemTime;
	}


	public void setSystemTime(Long systemTime) {
		SystemTime = systemTime;
	}


	private int timeing_id;
	public byte[] getPhoto() {
		return photo;
	}


	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}


	public SmsContent()
	{
		
	}

	public int getLong_position() {
		return long_position;
	}


	public void setLong_position(int long_position) {
		this.long_position = long_position;
	}

	public long getThread_id() {
		return thread_id;
	}
	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}
	public String getPerson_id() {
		return person_id;
	}
	public void setPerson_id(String person_id) {
		this.person_id = person_id;
	}
	
	
	public int getRead() {
		return read;
	}


	public void setRead(int read) {
		this.read = read;
	}


	public SmsContent(String send_type,String sms_body)
	{
		this.send_type=send_type;
		this.sms_body=sms_body;
	}
	
	public String getSend_type() {
		return send_type;
	}
	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}
	
	public String getSms_body() {
		return sms_body;
	}
	public void setSms_body(String sms_body) {
		this.sms_body = sms_body;
	}
	
	public String getSms_name() {
		return sms_name;
	}
	public void setSms_name(String sms_name) {
		this.sms_name = sms_name;
	}
	
	public String getSms_number() {
		return sms_number;
	}
	public void setSms_number(String sms_number) {
		this.sms_number = sms_number;
	}
	
	public String getSms_area() {
		return sms_area;
	}
	public void setSms_area(String sms_area) {
		this.sms_area = sms_area;
	}
	
	public int getSms_type() {
		return sms_type;
	}
	public void setSms_type(int sms_type) {
		this.sms_type = sms_type;
	}
	
	public String getSms_duration() {
		return sms_duration;
	}
	public void setSms_duration(String sms_duration) {
		this.sms_duration = sms_duration;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public int getSession_count() {
		return session_count;
	}
	public void setSession_count(int session_count) {
		this.session_count = session_count;
	}

	public int getTimeing_id() {
		return timeing_id;
	}
	public void setTimeing_id(int timeing_id) {
		this.timeing_id = timeing_id;
	}

	public int getSeen() {
		return seen;
	}

	public void setSeen(int seen) {
		this.seen = seen;
	}
	
}
