package com.dongji.app.addressbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.EditWidgetContactsAdapter;
import com.dongji.app.adapter.StrangeNumberContactsAdapter;
import com.dongji.app.adapter.WidgetContactAdapter;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.WidgetContact;
import com.dongji.app.sqllite.ContactLauncherDBHelper;

public class EditWidgetContact extends Activity implements OnClickListener{

	LinearLayout ly;
	ListView lv;
	
	ListView lv_contacts;
	Button btn_top_tips_no;
	Button btn_top_tips_yes;
	
	Button btn_edit_cancel;
	Button btn_edit_ok;
	
	WidgetContactAdapter widgetContactAdapter = null;
	StrangeNumberContactsAdapter strangeNumberContactsAdapter = null; 
	ContactLauncherDBHelper contactLauncher = null;
	SQLiteDatabase db = null;
	
	Dialog contact_dialog = null;
	
	List<ContactBean> contacts;
	boolean isQuerying = false;
	EditWidgetContactsAdapter editWidgetContactsAdapter;
	String tag = null;
	
	Context context;
	
//	LinearLayout edit_ly;
	
	List<WidgetContact> wcs = new ArrayList<WidgetContact>();
	
	int type = 1;
	
	String id = "";
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				editWidgetContactsAdapter = new EditWidgetContactsAdapter(context, contacts);
				lv_contacts.setAdapter(editWidgetContactsAdapter);
				
				isQuerying = false;
				break;
			default:
				break;
			}
			
		};
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.context = EditWidgetContact.this;
		
		setContentView(R.layout.edit_widget_contact);
		
		ly = (LinearLayout) findViewById(R.id.edit_ly);
		lv = (ListView) findViewById(R.id.contact_lv);
		
		btn_edit_cancel = (Button) findViewById(R.id.btn_edit_cancel);
		btn_edit_ok = (Button) findViewById(R.id.btn_edit_ok);
		
//		edit_ly = (LinearLayout) findViewById(R.id.edit_ly);
		
		btn_edit_cancel.setOnClickListener(this);
		btn_edit_ok.setOnClickListener(this);
//		edit_ly.setOnClickListener(this);
		
		loadData();
		
	}
	
	private void loadData() {
		
		contactLauncher = new ContactLauncherDBHelper(this);

		db = contactLauncher.getReadableDatabase();

		if(wcs==null || wcs.size()==0) {
		Cursor cr = db.query(contactLauncher.table, null, null, null, null,null, null);
	
		if (cr.moveToFirst()) {

			do {
				
				WidgetContact w = new WidgetContact();
				
				String number = cr.getString(cr.getColumnIndex(contactLauncher.NUMBER));
				String name = cr.getString(cr.getColumnIndex(contactLauncher.CONTACTNAME));
				String id = cr.getString(cr.getColumnIndex(contactLauncher._ID));
//				
//				if (numbers != null) {
//				
//					String[] str = numbers.split(",");
//					
//					System.out.println("cr --- > "+ cr.getCount() +"     str ----- > " + str.length);
//					
//					for (int i=0;i<str.length;i++) {
//					
//						System.out.println("  str ----- > " + str[i]);
//						
//						if (str[i] != number) {
//						
//							if ( name == null)
//								w.setName(number);
//							
//							w.setNumber(number);
//							w.setName(name);
//						
//						
//							byte[] photoicon = cr.getBlob(cr.getColumnIndex(contactLauncher.PHOTO));
//							if(photoicon != null){
//								w.setPhotoicon(photoicon);
//							}
//							
//							wcs.add(w);
//						}
//					}
//					
//				} else {
//					
					if ( name == null)
						w.setName(number);
					
					w.setNumber(number);
					w.setName(name);
					w.setId(id);
				
					byte[] photoicon = cr.getBlob(cr.getColumnIndex(contactLauncher.PHOTO));
					if(photoicon != null){
						w.setPhotoicon(photoicon);
					}
					
					System.out.println(" w number ----- > " + number);
					
					wcs.add(w);
//				}

			} while (cr.moveToNext());
	
		}
		db.close();
		cr.close();
		}
		if ( wcs.size() < 6) {
			
			int size = 6 - wcs.size();
			
			for (int i = 0; i<size;i++ ) {

				WidgetContact w = new WidgetContact();
				
				wcs.add(w);
				
			}
			
		}
				
		System.out.println(" wcs size ----- > " + wcs.size());
		
		widgetContactAdapter = new WidgetContactAdapter(this, wcs, new OnMenuClickListener());
		lv.setAdapter(widgetContactAdapter);
		
	}
	
	
	private class OnMenuClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			tag = (String) v.getTag();
			
			switch (v.getId()) {
			
			case R.id.change_widget_contact:
				
				type = 1;
				
				showContactDilaog();
				
				break;
				
			case R.id.delete_widget_contact:
				
				type = 3;
				
//				db = contactLauncher.getWritableDatabase();
//				
//				ContentValues values = new ContentValues();
//				values.put(contactLauncher.TAG, 1);
//				
//				db.update(contactLauncher.table,values, contactLauncher.NUMBER + " = '" + tag + "'", null);
//				
//				db.close();
//				
//				loadData();
				
				String[] str = tag.split(",");
				
				int pt = Integer.parseInt(str[0]);
				
				WidgetContact wContact = new WidgetContact();
				
				wcs.set(pt, wContact);
				
				id += str[1] + ",";
				
				System.out.println(" id id id ----- > " + id);
				
				loadData();
				
				break;
			
			case R.id.add_new_contact:
				
				type = 2;
				
				showContactDilaog();
				
				break;

			default:
				break;
			}
			
		}
		
	}
	
	void showContactDilaog()
	{
 	      if(contact_dialog==null)
 	      {
 	    	 contact_dialog = new Dialog(EditWidgetContact.this,R.style.theme_myDialog);
 	    	 View v = LayoutInflater.from(this).inflate(R.layout.remind_contact_dialog, null);
 	    	 contact_dialog.setCanceledOnTouchOutside(true);
 	    	 contact_dialog.setContentView(v);
 	    	 lv_contacts = (ListView)v.findViewById(R.id.lv_contact);
 	    	 
 	    	 Button btn_pick_contact_ok = (Button) v.findViewById(R.id.btn_pick_contact_ok);
 	    	 Button btn_pick_contact_cancle = (Button) v.findViewById(R.id.btn_pick_contact_cancle);
 	    	 btn_pick_contact_ok.setVisibility(View.GONE);
 	    	 btn_pick_contact_cancle.setVisibility(View.GONE);
 	    	 
 	    	 
 	    	 lv_contacts.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					TextView phone = (TextView) view.findViewById(R.id.number);
					
					WidgetContact wt = (WidgetContact) phone.getTag();
					
					String name = wt.getName();
					
					System.out.println(" number ---- > " + name+", "+wt.getNumber());
					
					if( wt.getNumber() != null) {
					
						if (type == 2) { 
					
							int i=0;
							for (;i<wcs.size();i++) {
								
								WidgetContact w = (WidgetContact) wcs.get(i);
								
								if (w.getName() == null){
									int pt = Integer.parseInt(tag);
									wcs.set(pt, wt);
									break;
								}
								
								if (w.getName().equals(name)) {
									
									Toast.makeText(EditWidgetContact.this, "此联系人已添加到桌面，不能重复添加！", Toast.LENGTH_SHORT).show();
									return ;
									
								} 
							}
							
						} else if (type == 1) {
							for (int i =0;i<wcs.size();i++) {
								
								WidgetContact w = (WidgetContact)wcs.get(i);
								
								if (w.getName().equals(name)) {
									Toast.makeText(EditWidgetContact.this, "此联系人已添加，不能重复添加！", Toast.LENGTH_SHORT).show();
									return ;
								} else {
									if (w.getNumber() == tag) {
										wcs.set(i, wt);
										break;
										
									}
									
									
								}
								
							}
							
						}
					
					} else {
						
						Toast.makeText(EditWidgetContact.this, "号码为空的联系人无法添加至桌面！", Toast.LENGTH_SHORT).show();
						return ;
						
					}
					
					contact_dialog.dismiss();
					
					loadData();
					
				}
			});
 	    	 
// 	    	 btn_pick_contact_ok.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if(editWidgetContactsAdapter!=null)
//					{
//						
//						WidgetContact wt = (WidgetContact) editWidgetContactsAdapter.cb.getTag();
//						
//						db = contactLauncher.getReadableDatabase();
//						
////						Cursor launcher = db.query(contactlauncher.table, null, contactlauncher.CONTACTNAME+"= '"+contactBean.getNick()+"'", null, null, null, null);
//						Cursor launcher = db.query(contactLauncher.table, null, null, null, null, null, null);
//						
////						if (launcher.getCount()  == 6) {
//							
////							Toast.makeText(EditWidgetContact.this, "超过最大添加数量，无法进行此操作！", Toast.LENGTH_SHORT).show();
////							return ;
////						} else {
//							
//						if (type == 1) {
//							
//							Cursor launchercursor = db.query(contactLauncher.table, null, contactLauncher.CONTACTNAME+"= '"+wt.getName()+"'", null, null, null, null);
//							
//							if(launchercursor.getCount() == 0){
//							
//								db = contactLauncher.getWritableDatabase();
//								
//								ContentValues values = new ContentValues();
//								values.put(contactLauncher.CONTACTNAME, wt.getName());
//								values.put(contactLauncher.NUMBER, wt.getNumber());
//								values.put(contactLauncher.PHOTO, wt.getPhotoicon());
//								values.put(contactLauncher.TAG, 0);
//								
//								db.update(contactLauncher.table, values, contactLauncher.NUMBER + " = '"+ tag +"'", null);
//							} else {
//								
//								Toast.makeText(EditWidgetContact.this, "此联系人已添加到桌面，不能重复添加！", Toast.LENGTH_SHORT).show();
//								return ;
//								
//							}
//							
//							launchercursor.close();
//							
//						} else {
//							
//							
//							if (launcher.getCount()  == 6) {
//								
//								Toast.makeText(EditWidgetContact.this, "超过最大添加数量，无法进行此操作！", Toast.LENGTH_SHORT).show();
//								return ;
//							} 
//						
//							Cursor launchercursor = db.query(contactLauncher.table, null, contactLauncher.CONTACTNAME+"= '"+wt.getName()+"'", null, null, null, null);
//							
//							if(launchercursor.getCount() == 0){
//								
//								db = contactLauncher.getWritableDatabase();
//								
//								ContentValues values = new ContentValues();
//								values.put(contactLauncher.CONTACTNAME, wt.getName());
//								values.put(contactLauncher.NUMBER, wt.getNumber());
//								values.put(contactLauncher.PHOTO, wt.getPhotoicon());
//								values.put(contactLauncher.TAG, 0);
//								
//								db.insert(contactLauncher.table, contactLauncher._ID, values);
//							}  else {
//								
//								Toast.makeText(EditWidgetContact.this, "此联系人已添加到桌面，不能重复添加！", Toast.LENGTH_SHORT).show();
//								return ;
//								
//							}
//							
//							launchercursor.close();
//						}
//							
//							
////						}
//						
//						launcher.close();
//						
//						db.close();
//						
//						Intent widget = new Intent();
//						widget.setAction("com.dongji.app.ui.appwidget.refresh");
//						sendBroadcast(widget);
//						
//						
//					}
//					
//					contact_dialog.dismiss();
//					
//					loadData();
//				}
//				
//			});
// 	    	 
// 	    	btn_pick_contact_cancle.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					contact_dialog.dismiss();
//				}
//			});
 	    	
 	       if(contacts==null && !isQuerying)
 	       {
 	    	  Toast.makeText(this, "正在载入联系人", Toast.LENGTH_SHORT).show();
 	 	       new Thread(new Runnable() {
 	 				
 	 				@Override
 	 				public void run() {
 	 					contacts = new ArrayList<ContactBean>() ;
 						String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
 						Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
 						while(cursor.moveToNext())
 						{
 							ContactBean contactBean = new ContactBean(); ;
 							contactBean.setContact_id(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
 							contactBean.setPhoto_id(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
 							contactBean.setNick(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
 							
 							 boolean b = false;
 							 String capPingYin = "";
 							 
 							String key = cursor.getString(cursor.getColumnIndex("sort_key"));
 							int phone_count = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
 							
 							int phone_num = cursor.getInt(phone_count); //是否有号码

 							boolean isCheck = true; //此联系人是否有效
 							
 							long c_id  = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
 							
 							if(phone_num ==0 &&  c_id != MainActivity.MY_CONTACT_ID)  //没有电话号码，并且不是机主
 							{
 								isCheck = false;
 							}
 							
 							if (key != null && isCheck ) // 没有sortkey ，则为空的联系人 
 							{
	 							 for(int i = 0;i<key.length();i++){
	 								 char c = key.charAt(i);
	 								 
	 								 if(c>256){//汉字符号 
	 									 b=false;
	 								 }else{
	 									 if(!b){
	 										 capPingYin += c;
	 										 b=true;
	 									 }
	 								 }
	 								}
	 							
	 							contactBean.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());
	 							
	 							contacts.add(contactBean);
 							}
 						}
 						cursor.close();
 						
 						handler.sendEmptyMessage(0);
 	 				}
 	 			}).start();
 	 	       isQuerying = true;
 	       }else{
 	    	   if(contacts!=null)
 	    	   {
 	    		  editWidgetContactsAdapter = new EditWidgetContactsAdapter(context, contacts);
 				  lv_contacts.setAdapter(editWidgetContactsAdapter);
 	    	   }else{
 	    		  Toast.makeText(this, "正在载入联系人", Toast.LENGTH_SHORT).show();
 	    	   }
 	       }
 	     }
 	     contact_dialog.show();
 	     
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_edit_cancel:
			
//			db = contactLauncher.getWritableDatabase();
//			ContentValues values = new ContentValues();
//			values.put(contactLauncher.TAG, 0);
//			
//			db.update(contactLauncher.table,values, null, null);
//			
//			db.close();
			this.finish();
			
			break;

		case R.id.btn_edit_ok:
			
			db = contactLauncher.getWritableDatabase();
			
			if ( type == 3) {
				
				String[] str = id.split(",");
				
				for (int s = 0;s<str.length;s++) {
					
					System.out.println(" str s  ---- > " + str[s]);
					
					String i = str[s];
					
					db.delete(contactLauncher.table, contactLauncher.NUMBER + " = '" + i + "'" , null);
					
				}
				
				
			} else {
				
				for (int i =0;i<wcs.size();i++) {
					
					WidgetContact wt = (WidgetContact) wcs.get(i);
					
					if (wt.getName() != null) {
						
						Cursor launchercursor = db.query(contactLauncher.table, null, contactLauncher.CONTACTNAME+" = '"+wt.getName()+"'", null, null, null, null);
						
						if(launchercursor.getCount() == 0){
							
							ContentValues v1 = new ContentValues();
							v1.put(contactLauncher.CONTACTNAME, wt.getName());
							v1.put(contactLauncher.NUMBER, wt.getNumber());
							v1.put(contactLauncher.PHOTO, wt.getPhotoicon());
							
							db.insert(contactLauncher.table, contactLauncher._ID, v1);
							
						}
						
						launchercursor.close();
					} 
				}
				
			}
			
			db.close();
			
			this.finish();
			
			Intent widget = new Intent();
			widget.setAction("com.dongji.app.ui.appwidget.refresh");
			sendBroadcast(widget);
			
			break;
		
			
		default:
			break;
		}
		
	}

	

}
