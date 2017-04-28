package com.dongji.app.addressbook;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.SmsCollectAdapter;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.SmsCollectEntity;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.ui.MyDialog;

/**
 * 
 * 短信收藏箱
 * 
 * @author Administrator
 *
 */
public class SmsCollectActivity extends Activity implements OnClickListener {

	public View view;
	
	MyDatabaseUtil myDatabaseUtil;
	
	SmsCollectAdapter smsCollectAdapter;
	List<SmsCollectEntity> senderList = new ArrayList<SmsCollectEntity>();
	List<SmsCollectEntity> list = new ArrayList<SmsCollectEntity>();
	
	SharedPreferences spf;
	SharedPreferences.Editor editor;
	
	LinearLayout add_ln;
	TextView contact_title;
	ListView contact_info;
	
	Button showAdapterButton;
	
	String f_id = "";
	
	MyDialog myDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		view = LayoutInflater.from(this).inflate(R.layout.setting_item_10_smscollect, null);
		
		setContentView(view);
		
		add_ln = (LinearLayout) view.findViewById(R.id.add_all);
		contact_title = (TextView) view.findViewById(R.id.contact_title);
		contact_info = (ListView) view.findViewById(R.id.contact_info);
		
		spf = getSharedPreferences("position", 0);
		editor = spf.edit();
		
		loadData();
		
	}
	
	public void loadData(){
		
		senderList.clear();
		add_ln.removeAllViews();
		list.clear();
		
		myDatabaseUtil = DButil.getInstance(this);
		Cursor sender_cursor = myDatabaseUtil.queryMessageSender();
		
		if (sender_cursor.getCount() > 0){
		
			if(sender_cursor.moveToFirst()){
				
				do{
					
					String favorite_sender = sender_cursor.getString(sender_cursor.getColumnIndex(MyDatabaseUtil.FAVORITE_SENDER));
					
					System.out.println(" favorite sender ---- > " + favorite_sender);
					
					String sendNum = sender_cursor.getString(sender_cursor.getColumnIndex("sendNum"));
					String number = PhoneNumberTool.cleanse(sender_cursor.getString(sender_cursor.getColumnIndex(MyDatabaseUtil.FAVORITE_NUMBER)));
					
					
					 boolean isMath = MainActivity.checkIsEnContactByNumber(number);
				       
					
					if (!isMath) {
						
						SmsCollectEntity smsCollectEntity = new SmsCollectEntity();
						
						//联系人被修改后，获取联系人最新名称
						
						if(isNumeric(favorite_sender)){
							
							smsCollectEntity.setFavorite_sender(favorite_sender);
							
						}else{
							
							Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME+"='"+favorite_sender+"'", null, null);
							
							if(cursor.getCount() > 0)
								
								smsCollectEntity.setFavorite_sender(favorite_sender);
							
							else{
								
								Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.CONTACT_ID}, null, null, null);
								
								if(phone.moveToFirst()){
									
									do{
										String tel = PhoneNumberTool.cleanse(phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
										
										long c_id = 0;
										if(number.equals(tel))
											c_id  = phone.getLong(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
										
										if(c_id == 0){
											
											smsCollectEntity.setFavorite_sender(favorite_sender);
										}else{
											
											Cursor contact =  getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, ContactsContract.Contacts._ID+"="+c_id, null, null);
											
											if(contact.moveToFirst()){
												
												do{
													
													String displayname = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
													
													smsCollectEntity.setFavorite_sender(displayname);
													
												}while(contact.moveToNext());
											}
											
											
											contact.close();
										}
										
											
										
									}while(phone.moveToNext());
								}
								
								phone.close();
							}
								
							cursor.close();
							
						}
						
						smsCollectEntity.setFavorite_del1(sendNum);
						
						senderList.add(smsCollectEntity);
					}
					
				}while(sender_cursor.moveToNext());
			}
			
			sender_cursor.close();
			
			for(int i=0;i<senderList.size();i++){
				
				FrameLayout view = (FrameLayout)LayoutInflater.from(this).inflate(R.layout.add_button_item, null);
				showAdapterButton = (Button) view.findViewById(R.id.button_item);
				showAdapterButton.setOnClickListener(this);
				
				String sender = senderList.get(i).getFavorite_sender();
				String num = senderList.get(i).getFavorite_del1();
				
				showAdapterButton.setText(sender);
	    		showAdapterButton.setTag(sender+","+num+"|"+i);
				add_ln.addView(view);
			}
			
			int initposition = spf.getInt("positionvalue", 0);
			
			if(senderList.size() > 0){
				
				View firstView = add_ln.getChildAt(initposition);
				
				if(firstView!=null)
				{
					firstView.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
			        
			        Button btn = (Button)firstView.findViewById(R.id.button_item);
			        
			        String name = senderList.get(initposition).getFavorite_sender();
			        String count = senderList.get(initposition).getFavorite_del1();
			        
		//	        Spanned title = Html.fromHtml("<font color='#106c96'>"+name+"</font>"+" 的收藏列表 (共 "+count+"条 )");
					contact_title.setText(Html.fromHtml("<font color='#3d8eba'>"+name+"</font>"+" 的收藏列表 (共"+count+"条 )"));
					
					btn.setText(Html.fromHtml("<font color='#44687d'>"+name+"("+count+")"+"</font>"));
					btn.setText(name+"("+count+")");
			        
			        setAdapter(name);
				}
			}
		}
		
		if (sender_cursor.getCount() == 0 ){
			contact_title.setText(" 收藏列表 ");
		}
	}
	
	public static boolean isNumeric(String str){ 
		
		Pattern pattern = Pattern.compile("[0-9]*"); 

		System.out.println("str  --->" + str);
		try {
			return pattern.matcher(str).matches(); 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	} 

	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.button_item:
			
			list.clear();
			
			String allInfo = v.getTag().toString();
			
			String sender = allInfo.substring(0, allInfo.indexOf(","));
			String count = allInfo.substring(allInfo.indexOf(",")+1,allInfo.indexOf("|"));
			int position = Integer.parseInt(allInfo.substring(allInfo.indexOf("|")+1,allInfo.length()));
			
//			String title = sender +" 的收藏列表 (共 "+count+"条 )";
			contact_title.setText(Html.fromHtml("<font color='#3d8eba'>"+sender+"</font>"+" 的收藏列表 (共"+count+"条 )"));
			
			setAdapter(sender);
			
			//根据选中的位置，刷新背景
			for(int i = 0;i<add_ln.getChildCount();i++)
			{
				if(i==position)
				{
					View firstView = add_ln.getChildAt(i);
			        firstView.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
			        Button button = (Button) firstView.findViewById(R.id.button_item);
			        button.setText(Html.fromHtml("<font color='#44687d'>"+sender+"("+count+")"+"</font>"));
			        
				}else{
					
					View firstView = add_ln.getChildAt(i);
			        firstView.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg);
			        Button btn = (Button) firstView.findViewById(R.id.button_item);
			        
			        String clearOldInfo = btn.getText().toString();
			        
			        if(clearOldInfo.contains("(")){
			        	
			        	btn.setText(clearOldInfo.substring(0, clearOldInfo.indexOf("(")));
			        }
				}
			}
	 		
			editor.putInt("positionvalue", position);
			editor.commit();
			
			break;
			
		default:
			break;
		}
		
	}
	
	class OnMenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			
			case R.id.menu_search:
				
				String sms_id = (String)v.getTag();
				
				System.out.println(" sms_id  ---->" + sms_id);
				
				//原短信是否存在
				String thread_id =null;
				Cursor c =  getContentResolver().query(Uri.parse("content://sms/"), new String[]{"thread_id"}, "_id =? ", new String [] {sms_id}, null);
				if(c.moveToNext())
				{
					thread_id = c.getString(c.getColumnIndex("thread_id"));
				}
				c.close();
				
				if(thread_id!=null)
				{
					Intent intent = new Intent(getApplicationContext(), NewMessageActivity.class);
					intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
					intent.putExtra(NewMessageActivity.DATA_OTHER, sms_id);
							
					 startActivity(intent);
					
				}else{
					Toast.makeText(getApplicationContext(), "原短信已删除", Toast.LENGTH_SHORT).show();
				}
				
				break;
				
				//转发
			case R.id.menu_send:
				
				//newMark
                String content = v.getTag().toString();

				Intent intent = new Intent(getApplicationContext(), NewMessageActivity.class);
				intent.putExtra(NewMessageActivity.DATA_OTHER, content);
						
				 startActivity(intent);
				
				
				break;
				
			case R.id.menu_delete:
				
				f_id = (String)v.getTag();
				
				myDialog = new MyDialog(SmsCollectActivity.this, "确定删除该条收藏", new DialogOnClickListener());
				myDialog.normalDialog();
				break;

			default:
				break;
			}
			
		}
	}
	
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_top_tips_yes:
				
				if(!f_id.equals("")){
					
					boolean isDelete = myDatabaseUtil.deleteByIdFavorite(f_id);
					
					if(isDelete)
						loadData();
				}
				
				myDialog.closeDialog();
				break;

			default:
				break;
			}
			
		}
		
	}

	
	private void setAdapter(String sender){
		
		list.clear();
		
		myDatabaseUtil = DButil.getInstance(getApplicationContext());
		Cursor smscollect = myDatabaseUtil.queryMessageFavorite(sender);
		
			if(smscollect.moveToFirst()){
				
	 			do{
	 				
	 				int id = smscollect.getInt(smscollect.getColumnIndex(MyDatabaseUtil.FAVORITE_ID));
	 				long thread_id = smscollect.getLong(smscollect.getColumnIndex(MyDatabaseUtil.THREAD_ID));
	 				long sms_id = smscollect.getLong(smscollect.getColumnIndex(MyDatabaseUtil.CONTENT_ID));
	 				String favorite_content = smscollect.getString(smscollect.getColumnIndex(MyDatabaseUtil.FAVORITE_CONTENT));
	 				long time = smscollect.getLong(smscollect.getColumnIndex(MyDatabaseUtil.CONTENT_TIME));
	 				String favorite_sender = smscollect.getString(smscollect.getColumnIndex(MyDatabaseUtil.FAVORITE_SENDER));
	 				String favorite_number = smscollect.getString(smscollect.getColumnIndex(MyDatabaseUtil.FAVORITE_NUMBER));
	 				
	 				SmsCollectEntity smsCollectEntity = new SmsCollectEntity();

	 				smsCollectEntity.setFavorite_id(id);
	 				smsCollectEntity.setThread_id(String.valueOf(thread_id));
	 				smsCollectEntity.setContent_id(String.valueOf(sms_id));
	 				smsCollectEntity.setFavorite_content(favorite_content);
	 				
	 				smsCollectEntity.setContent_time(TimeTool.getTimeStrYYMMDD(time));
	 				
	 				smsCollectEntity.setFavorite_sender(favorite_sender);
	 				smsCollectEntity.setFavorite_number(favorite_number);
	 				
	 				list.add(smsCollectEntity);
	 				
	 			}while(smscollect.moveToNext());
			}
			
			smscollect.close();
	
		smscollect.close();
 		smsCollectAdapter = new SmsCollectAdapter(getApplicationContext(), list, new OnMenuItemClickListener());
 		contact_info.setAdapter(smsCollectAdapter);
	}
	
}