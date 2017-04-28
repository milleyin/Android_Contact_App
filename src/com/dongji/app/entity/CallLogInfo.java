package com.dongji.app.entity;


public class CallLogInfo {

	private long id;
	
	private String mCaller_name;
	private String mCaller_number;
	private String mCaller_area;
	private String mCall_date;  //时间
	private String mCall_day;  //日期
	private String mCall_type;
	private String mCall_duration;//时长
	private long mCall_duration_long;
	private int missedNum;
	
	private int type;
	
	private long second_id = -1; //以时间排序，第二条通话记录的id
	
	private String orignal_number ; //原始号码

	private int contact_id = -1; //联系人id
	
	private String photo_id; //头像
	
	private boolean isNeedRequery = false; //是否需要重新(主要用于查询联系人姓名)
	
	
	//通话记录对应的联系人名字的信息(搜索通话记录时的匹配)
    String sork_key;
	String name_pinyin; //名字的全拼音
	String name_pinyin_cap;//名字的拼音首写字母
	String name_letter;
	
	
	public int getMissedNum() {
		return missedNum;
	}

	public void setMissedNum(int missedNum) {
		this.missedNum = missedNum;
	}

	int total;
	private long long_date;
	
	public String getmCaller_name() {
		return mCaller_name;
	}

	public void setmCaller_name(String mCaller_name) {
		this.mCaller_name = mCaller_name;
	}

	public String getmCaller_number() {
		return mCaller_number;
	}

	public void setmCaller_number(String mCaller_number) {
		this.mCaller_number = mCaller_number;
	}

	public String getmCaller_area() {
		return mCaller_area;
	}

	public void setmCaller_area(String mCaller_area) {
		this.mCaller_area = mCaller_area;
	}

	public String getmCall_date() {
		return mCall_date;
	}

	public void setmCall_date(String mCall_date) {
		this.mCall_date = mCall_date;
	}

	public String getmCall_type() {
		return mCall_type;
	}

	public void setmCall_type(String mCall_type) {
		this.mCall_type = mCall_type;
	}

	public String getmCall_duration() {
		return mCall_duration;
	}

	public void setmCall_duration(String mCall_duration) {
		this.mCall_duration = mCall_duration;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public long getLong_date() {
		return long_date;
	}

	public void setLong_date(long long_date) {
		this.long_date = long_date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDay() {
		return mCall_day;
	}

	public void setDay(String mCall_day) {
		this.mCall_day = mCall_day;
	}

	public long getSeconde_id() {
		return second_id;
	}

	public void setSeconde_id(long seconde_id) {
		this.second_id = seconde_id;
	}

	public String getOrignal_number() {
		return orignal_number;
	}

	public void setOrignal_number(String orignal_number) {
		this.orignal_number = orignal_number;
	}

	public int getContact_id() {
		return contact_id;
	}

	public void setContact_id(int contact_id) {
		this.contact_id = contact_id;
	}

	public String getPhoto_id() {
		return photo_id;
	}

	public void setPhoto_id(String photo_id) {
		this.photo_id = photo_id;
	}

	public boolean isNeedRequery() {
		return isNeedRequery;
	}

	public void setNeedRequery(boolean isNeedRequery) {
		this.isNeedRequery = isNeedRequery;
	}

	public long getmCall_duration_long() {
		return mCall_duration_long;
	}

	public void setmCall_duration_long(long mCall_duration_long) {
		this.mCall_duration_long = mCall_duration_long;
	}

	public String getSork_key() {
		return sork_key;
	}

	public void setSork_key(String sork_key) {
		this.sork_key = sork_key;
	}

	public String getName_pinyin() {
		return name_pinyin;
	}

	public void setName_pinyin(String name_pinyin) {
		this.name_pinyin = name_pinyin;
	}

	public String getName_pinyin_cap() {
		return name_pinyin_cap;
	}

	public void setName_pinyin_cap(String name_pinyin_cap) {
		this.name_pinyin_cap = name_pinyin_cap;
	}

	public String getName_letter() {
		return name_letter;
	}

	public void setName_letter(String name_letter) {
		this.name_letter = name_letter;
	}
	
}
