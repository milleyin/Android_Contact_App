package com.dongji.app.entity;

import android.graphics.Bitmap;

/**
 * 彩信
 * @author Administrator
 *
 */
public class MmsContent extends MmsSmsContent{

	String subject ; //主题
	
	String address; //号码
	
	long exp; //有效期

	int m_size; //大小
	
	int msg_box ; //此条彩信属于哪个信箱，all为0，inbox为1，sent为2，draft为3，outbox为4，failed为5
	
	int st = -1; // 该彩信的下载状态，未启动-128，下载中-129，传输失败-130，保存失败-135
	
	boolean isHavePicPart; //是否有图片附件
	boolean isHaveTextPart; //是否有文字附件
	String part_pic_id;
	String part_text_id;
	String part_text;
	
	String content ; //显示的内容:  有主题则显示 <主题>+附件  ；  没主题则显示附件
	
	Bitmap part_bmp;

	int retr_st;
	
	int resp_st;
	
	int d_rpt;
	
	int thread_id;
	
	int seen;
	
	long d_tm;
	
	int read; //0-未读，1-已读 
	
	String contact_name; //联系人的名字
	
	String resp_txt;
	
	int read_status;
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getMsg_box() {
		return msg_box;
	}

	public void setMsg_box(int msg_box) {
		this.msg_box = msg_box;
	}

	public int getSt() {
		return st;
	}

	public void setSt(int st) {
		this.st = st;
	}

	public String getPart_pic_id() {
		return part_pic_id;
	}

	public void setPart_pic_id(String part_pic_id) {
		this.part_pic_id = part_pic_id;
	}

	public String getPart_text_id() {
		return part_text_id;
	}

	public void setPart_text_id(String part_text_id) {
		this.part_text_id = part_text_id;
	}

	public String getPart_text() {
		return part_text;
	}

	public void setPart_text(String part_text) {
		this.part_text = part_text;
	}

	public Bitmap getPart_bmp() {
		return part_bmp;
	}

	public void setPart_bmp(Bitmap part_bmp) {
		this.part_bmp = part_bmp;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public int getM_size() {
		return m_size;
	}

	public void setM_size(int m_size) {
		this.m_size = m_size;
	}

	public int getRetr_st() {
		return retr_st;
	}

	public void setRetr_st(int retr_st) {
		this.retr_st = retr_st;
	}

	public int getResp_st() {
		return resp_st;
	}

	public void setResp_st(int resp_st) {
		this.resp_st = resp_st;
	}

	public int getD_rpt() {
		return d_rpt;
	}

	public void setD_rpt(int d_rpt) {
		this.d_rpt = d_rpt;
	}

	public boolean isHavePicPart() {
		return isHavePicPart;
	}

	public void setHavePicPart(boolean isHavePicPart) {
		this.isHavePicPart = isHavePicPart;
	}

	public boolean isHaveTextPart() {
		return isHaveTextPart;
	}

	public void setHaveTextPart(boolean isHaveTextPart) {
		this.isHaveTextPart = isHaveTextPart;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getThread_id() {
		return thread_id;
	}

	public void setThread_id(int thread_id) {
		this.thread_id = thread_id;
	}

	public int getSeen() {
		return seen;
	}

	public void setSeen(int seen) {
		this.seen = seen;
	}

	public long getD_tm() {
		return d_tm;
	}

	public void setD_tm(long d_tm) {
		this.d_tm = d_tm;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	
	
	
	public String getResp_txt() {
		return resp_txt;
	}

	public void setResp_txt(String resp_txt) {
		this.resp_txt = resp_txt;
	}
	
	
	public int getRead_status() {
		return read_status;
	}

	public void setRead_status(int read_status) {
		this.read_status = read_status;
	}

	public  String  toString()
	{
		return "msg_box:" + msg_box + "  st: " + st + "  retr_st : " + retr_st + "  resp_st: " + resp_st + "  d_rpt : +" + d_rpt + "  d_tm:" + d_tm + " resp_txt:" + resp_txt + " read_status:" + read_status;
	}
	
	
}
