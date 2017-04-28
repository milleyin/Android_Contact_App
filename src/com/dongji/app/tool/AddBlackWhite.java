package com.dongji.app.tool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.dongji.app.entity.ContactBean;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;

public class AddBlackWhite {

	MyDatabaseUtil myDatabaseUtil;
	Context context;
	
	public AddBlackWhite(Context context) {
		this.context = context;
		myDatabaseUtil  = DButil.getInstance(context);
	}
	
	public int saveBlack(String number) {
		
		int count = myDatabaseUtil.saveBlack(number);
		
		return count;
		
	}
	
	public int saveWhite(String number) {
		
		int count = myDatabaseUtil.saveWhite(number);
		
		return count;
		
	}
	
	public List<ContactBean> queryAllBlack(){
		
		List<ContactBean> blackList = new ArrayList<ContactBean>();
		Cursor black_cursor = myDatabaseUtil.queryAllBlack();
		if(black_cursor.moveToFirst()){
			do{
				long id = black_cursor.getLong(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_ID));
				String number = black_cursor.getString(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NUMBER));
				
				ContactBean bean = new ContactBean();
				if (id != 0) {
					
					bean = getContactInfo(id,number);
					
				} else {
					
					String[] data = PhoneNumberTool.getContactInfo(context, number);
					
					String name = "";
					String photo_id = null;
					long contact_id = -1;
					if(data[0] != null) {
						name = data[0];
					}
					if(data[1] != null) {
						photo_id = data[1];
					}
					if(data[2] !=null)
					{
						contact_id = Integer.valueOf(data[2]);
					}
					bean.setNick(name);
					bean.setPhoto_id(photo_id);
					bean.setContact_id(contact_id);
					bean.setNumber(number);
				}
				
				blackList.add(bean);
			}while(black_cursor.moveToNext());
		}
		black_cursor.close();
		
		return blackList;
	}
	
	public List<ContactBean> queryAllWhite(){
		List<ContactBean> whiteList = new ArrayList<ContactBean>();
		Cursor white_cursor = myDatabaseUtil.queryAllWhite();
		if(white_cursor.moveToFirst()){
			do{
				
				long id = white_cursor.getLong(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_ID));
				String number = white_cursor.getString(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NUMBER));
				
				ContactBean bean = new ContactBean();
				if (id != 0) {
					
					bean = getContactInfo(id,number);
					
				} else {
					
					String[] data = PhoneNumberTool.getContactInfo(context, number);
					String name = "";
					String photo_id = null;
					long contact_id = -1;
					if(data[0] != null) {
						name = data[0];
					}
					if(data[1] != null) {
						photo_id = data[1];
					}
					if(data[2] !=null)
					{
						contact_id = Integer.valueOf(data[2]);
					}
					bean.setNick(name);
					bean.setPhoto_id(photo_id);
					bean.setContact_id(contact_id);
					bean.setNumber(number);
				}
				
				whiteList.add(bean);
			}while(white_cursor.moveToNext());
		}
		white_cursor.close();
		
		return whiteList;
	}
	
	public boolean delBlack(String number){
		
		return myDatabaseUtil.deleteBlack(number);
		
	}
	
	public boolean delWhite(String number){
		
		return myDatabaseUtil.deleteWhite(number);
		
	}
	
	private ContactBean getContactInfo(Long id,String number){
		
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID +"="+id, null,ContactsContract.Contacts._ID+" desc");
		ContactBean contactBean = new ContactBean();
		if(cursor.moveToFirst()){
			
			contactBean.setContact_id(id);
			contactBean.setNick(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			contactBean.setPhoto_id(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
			contactBean.setNumber(number);
		}
		cursor.close();
		return contactBean;
	}
	
}
