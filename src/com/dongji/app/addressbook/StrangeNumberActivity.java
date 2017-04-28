package com.dongji.app.addressbook;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.StrangeNumberContactsAdapter;
import com.dongji.app.adapter.StrangeNumberListAdapter;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.StrangeNumberCalllogBean;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.AddBlackWhite;
import com.dongji.app.tool.PhoneNumberTool;

/**
 * 
 * 陌生号码详情
 * 
 * @author Administrator
 *
 */
public class StrangeNumberActivity extends Activity implements OnClickListener {

	
	public View view;
	
	String number;
	public static String STRANGE_NUMBER = "strange_number";
	
	TextView tv_strange_number;
	ListView lv_strange_callog;
	Button btn_add;
	
	Dialog dialog_more;
	Button btn_new_contact;  //新建联系人
	Button btn_add_contact_in; //添加到已有联系人
	Button btn_add_black_list; //加入黑名单
	Button btn_add_white_list; //加入白名单
	
	Dialog dialog_tip;
	
	TextView tv_top_tips;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	
	ListView lv_contacts; //选择联系人
	StrangeNumberContactsAdapter strangeNumberContactsAdapter;
	
	Button btn_send_sms; //发短信
	Button btn_make_call; //打电话
	
	NewMessageActivity newMessageLayout;
	
	AddBlackWhite addBlackWhite;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(StrangeNumberActivity.this).inflate(R.layout.strange_number, null);
		setContentView(view);
		
		this.number = getIntent().getStringExtra(STRANGE_NUMBER);
		
		addBlackWhite = new AddBlackWhite(StrangeNumberActivity.this);
		tv_strange_number = (TextView)view.findViewById(R.id.tv_strange_number);
		tv_strange_number.setText(number);
		
		btn_add = (Button)view.findViewById(R.id.btn_add);
		btn_add.setOnClickListener(this);
		
		lv_strange_callog = (ListView)view.findViewById(R.id.lv_strange_callog);
		
		dialog_tip = new Dialog(StrangeNumberActivity.this,R.style.theme_myDialog);
			
		dialog_tip.setContentView(LayoutInflater.from(StrangeNumberActivity.this).inflate(R.layout.dialog_tip_popup, null));
			
		tv_top_tips = (TextView)dialog_tip.findViewById(R.id.tv_top_tips);
		btn_top_tips_yes = (Button)dialog_tip.findViewById(R.id.btn_top_tips_yes); 
		btn_top_tips_yes.setOnClickListener(this);
		btn_top_tips_no = (Button)dialog_tip.findViewById(R.id.btn_top_tips_no);
		btn_top_tips_no.setOnClickListener(this);
		
		
		lv_contacts = (ListView)view.findViewById(R.id.lv_contacts);
		
		btn_send_sms = (Button)view.findViewById(R.id.btn_send_sms);
		btn_send_sms.setOnClickListener(this);
		btn_make_call = (Button)view.findViewById(R.id.btn_make_call);
		btn_make_call.setOnClickListener(this);
		
		loadData();
	}
	
	void loadData(){
		Cursor c = StrangeNumberActivity.this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "number=?", new String[]{number}, null);
		
		List<StrangeNumberCalllogBean> callogs = new ArrayList<StrangeNumberCalllogBean>();
		
		final List<StrangeNumberCalllogBean> misscallogs = new ArrayList<StrangeNumberCalllogBean>();
		
		
		while (c.moveToNext()) {
			
			long id = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
			
				
				StrangeNumberCalllogBean s = new StrangeNumberCalllogBean();
				
				s.setId(c.getLong(c.getColumnIndex(CallLog.Calls._ID)));
				
				SimpleDateFormat sfd = new SimpleDateFormat("MM/dd hh:mm:ss");
				Date date = new Date(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
				String DATE = sfd.format(date);// 格式化的效果:例如01/08 // 09:10:11
				s.setDate(DATE);
				
				// 通话时长
				long old = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));
				long hour = old / (60 * 60);
				long min = (old % (60 * 60)) / (60);
				long second = (old % (60));
				
				String DURATION ;
				
				int TYPE = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
				
				if(hour!=0)
				{
					DURATION = hour + "小时" + min + "分钟" + second + "秒";
				}else if(min!=0)
				{
					DURATION =  min + "分钟" + second + "秒";
				}else{
					DURATION =  second + "秒";
				}
		
				
				if ((TYPE == CallLog.Calls.INCOMING_TYPE)
						&&  hour == 0 && min ==0 && second == 0) {
					DURATION = "未接通";
				} else if ((TYPE == CallLog.Calls.OUTGOING_TYPE)
						&& hour == 0 && min ==0 && second == 0 ) {
					DURATION = "未接通";
				}
				
				// 通话类型
		
				String call_type = "";
				
				if (TYPE == CallLog.Calls.INCOMING_TYPE) {
					
					call_type = "呼入";
				} else if (TYPE == CallLog.Calls.OUTGOING_TYPE) {
					
					call_type = "呼出";
				} else if (TYPE == CallLog.Calls.MISSED_TYPE) {
					if(c.getInt(c.getColumnIndex(CallLog.Calls.NEW)) == 1)
					{
						misscallogs.add(s);
					}
					call_type = "未接";
				}
				
				s.setDuration(DURATION);
				s.setType(call_type);
				
				callogs.add(s);
			}
		c.close();
		
		//清空未接留言记录
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//清空所有未接来电的标示符
				for(StrangeNumberCalllogBean s:misscallogs)
				{
					ContentValues cv = new ContentValues();
					cv.put(CallLog.Calls.NEW,0);
					int result = StrangeNumberActivity.this.getContentResolver().update(CallLog.Calls.CONTENT_URI, cv, CallLog.Calls._ID + " = " +s.getId(), null);
					System.out.println( result +"  s _id --->" + s.getId());
				}
			}
		}).start();
		
		
		lv_strange_callog.setAdapter(new StrangeNumberListAdapter(StrangeNumberActivity.this,callogs));
	}

	@Override
	public void onBackPressed() {
		
		if(!handleBackPress())
		{
			super.onBackPressed();
		}
		
	}
	
	boolean handleBackPress()
	{
		if(lv_contacts.getVisibility() == View.VISIBLE)
		{
			Animation out = AnimationUtils.loadAnimation(StrangeNumberActivity.this, R.anim.dialing_out);
			out.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					lv_contacts.setVisibility(View.GONE);
				}
			});
			
			lv_contacts.startAnimation(out);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
		case R.id.btn_add:
			if(dialog_more==null)
			{
				dialog_more = new Dialog(StrangeNumberActivity.this, R.style.theme_myDialog);
				dialog_more.setCanceledOnTouchOutside(true);
				dialog_more.setContentView(LayoutInflater.from(StrangeNumberActivity.this).inflate(R.layout.dialog_strange_more, null));
				btn_new_contact = (Button)dialog_more.findViewById(R.id.btn_new_contact);
				btn_new_contact.setOnClickListener(this);
				btn_add_contact_in = (Button)dialog_more.findViewById(R.id.btn_add_contact_in);
				btn_add_contact_in.setOnClickListener(this);
				btn_add_black_list = (Button)dialog_more.findViewById(R.id.btn_add_black_list);
				btn_add_black_list.setOnClickListener(this);
				btn_add_white_list = (Button)dialog_more.findViewById(R.id.btn_add_white_list);
				btn_add_white_list.setOnClickListener(this);
				
				dialog_more.setCanceledOnTouchOutside(true);
			}
			dialog_more.show();
			break;

			
		case R.id.btn_top_tips_no:
//			tips_top_layout.setVisibility(View.GONE);
//			StrangeNumberActivity.this.l_scrolelr.snapToScreen(0);
			break;
			
		case R.id.btn_top_tips_yes:
//			tips_top_layout.setVisibility(View.GONE);
//			StrangeNumberActivity.this.l_scrolelr.snapToScreen(0);
			break;
			
		case R.id.btn_new_contact:

			dialog_more.dismiss();
			
			Intent intent = new Intent(StrangeNumberActivity.this, AddContactActivity.class);
			intent.putExtra(AddContactActivity.DATA_TYPE, AddContactActivity.TYPE_ADD_CONTACT);
			intent.putExtra(AddContactActivity.DATA_STRANGE_NUMBER, number);
			startActivity(intent);
			
			break;
			
		case R.id.btn_add_contact_in:
			dialog_more.dismiss();
			
			if(strangeNumberContactsAdapter==null)
			{
				List<ContactBean> contacts = new ArrayList<ContactBean>() ;
				String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
				Cursor cursor = StrangeNumberActivity.this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
				System.out.println(cursor.getCount() + "===========================contact=count");
				while(cursor.moveToNext())
				{
					ContactBean contactBean = new ContactBean(); ;
					contactBean.setContact_id(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
					contactBean.setPhoto_id(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
					contactBean.setNick(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
					
					contacts.add(contactBean);
				}
				cursor.close();
				
				strangeNumberContactsAdapter = new StrangeNumberContactsAdapter(StrangeNumberActivity.this, contacts, new selectContactClickListener());
				lv_contacts.setAdapter(strangeNumberContactsAdapter);
			}
			
			lv_contacts.setVisibility(View.VISIBLE);
			lv_contacts.startAnimation(AnimationUtils.loadAnimation(StrangeNumberActivity.this, R.anim.dialing_in));
			
			break;
			
		case R.id.btn_add_black_list:
			dialog_more.dismiss();
			
			btn_top_tips_yes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					saveBlack(number);
					
					int count = addBlackWhite.saveBlack(number);
					if (count  > 0 ) {

						Toast.makeText(StrangeNumberActivity.this, "黑名单中存在该联系人！", Toast.LENGTH_SHORT).show();
					}
					
					dialog_tip.dismiss();
				}
			});
			btn_top_tips_no.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog_tip.dismiss();
				}
			});
			String source = "将  "+number+" 添加到黑名单";
			tv_top_tips.setText(Html.fromHtml(source.replace(number, "<font color='#3d8eba'>" + number+ "</font>")));
			
			dialog_tip.show();
			break;
			
		case R.id.btn_add_white_list:
			dialog_more.dismiss();
			
			btn_top_tips_yes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					saveWhite(number);
					
					int count1 = addBlackWhite.saveWhite(number);
					if (count1  > 0 ) {

						Toast.makeText(StrangeNumberActivity.this, "白名单中存在该联系人！", Toast.LENGTH_SHORT).show();
					}
					
					dialog_tip.dismiss();
				}
			});
			btn_top_tips_no.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog_tip.dismiss();
				}
			});
			String source1 = "将  "+number+"  添加到白名单";
			tv_top_tips.setText(Html.fromHtml(source1.replace(number, "<font color='#3d8eba'>" + number+ "</font>")));
			
			dialog_tip.show();
			break;
			
		case R.id.btn_send_sms:


			Intent m_intent = new Intent(this, NewMessageActivity.class);
			m_intent.putExtra(NewMessageActivity.DATA_NUMBER, number);
					
			startActivity(m_intent);
			
			break;
			
		case R.id.btn_make_call:
			Intent call_intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
			StrangeNumberActivity.this.startActivity(call_intent);
			break;
			
		default:
			break;
		}
	}
	
	
	class selectContactClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String s = (String) v.getTag();
			String [] ss = s.split(":");
			String contactId = ss[0];
			lv_contacts.setVisibility(View.GONE);
			
			System.out.println(" contactId  ---> " + ss[0]);
			
			Intent intent = new Intent(StrangeNumberActivity.this, AddContactActivity.class);
			intent.putExtra(AddContactActivity.DATA_TYPE, AddContactActivity.TYPE_ADD_NUMBER_TO_CURENT_CONTACT);
			intent.putExtra(AddContactActivity.DATA_CONTACT_ID, contactId );
			intent.putExtra(AddContactActivity.DATA_STRANGE_NUMBER, number);
			startActivity(intent);
			
		}
	}
	
}
