package com.dongji.app.addressbook;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dongji.app.ui.ScrollLayout;

/**
 * 
 * 系统设置
 * 
 * @author Administrator
 *
 */
public class SystemSettingActivity extends Activity {

//	Context context;
	
	public View view;
	
	ScrollLayout sc_switch_hand;
	
	ScrollLayout sc_switch_panel;
	
	ScrollLayout sc_switch_contact;
	
	ScrollLayout sc_switch_sms;
	
	ScrollLayout sc_switch_callog;
	
	ScrollLayout sc_switch_sms_remind;
	
	ScrollLayout sc_switch_remind_all;
	
	ScrollLayout sc_switch_remind_group;
	
	ScrollLayout sc_switch_secretary_sex;
	
	EditText et_secretary_name;
	
	Button desktop_shortcut;
	
	public static  String SF_NAME = "systemsetting";
	SharedPreferences sf ;
	
	public static String SF_KEY_HAND = "hand"; // 1:右手   0:左手
	public static String SF_KEY_DIALING_PANEL = "dialing_panel";  //  1:默认收起  0:默认展开
	public static String SF_KEY_CONTACT_SORT = "contact_sort";    // 1:字母排序    0:热度排序
	public static String SF_KEY_SMS_SORT = "sms_sort";    //1:时间排序    0:热度排序
	public static String SF_KEY_COLLOG_SORT = "collog_sort";   // 1:时间排序    0:热度排序
	public static String SF_KEY_SMS_REMIND = "sms_remind"; //短信发送成功提醒:  1:关闭 ;   0:开启  
	public static String SF_KEY_REMIND_ALL = "remind_all";  //关闭全部提醒:  1:不启用 ;   0:开启    
	public static String SF_KEY_REMIND_GROUP = "remind_group";  //联系人组提醒:  1:不启用 ;   0:开启    
	public static String SF_KEY_SECRETARY_SEX = "secretary_sex";  //秘书性别:  1:女 ;   0:男
	public static String SF_KEY_SECRETARY_NAME = "secretary_name"; //秘书名称
	
	public static String REFRESH_CONTACT = "refresh_contact";
	public static String REFRESH_CALLLOG = "refresh_calllog";
	public static String REFRESH_SMS = "refresh_sms";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(this).inflate(R.layout.setting_item_9_system, null);
		
		setContentView(view);
		
		sf = getSharedPreferences(SF_NAME, 0);
		
		sc_switch_hand = (ScrollLayout)view.findViewById(R.id.sc_switch_hand);
		sc_switch_hand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(sc_switch_hand.getCurScreen()==0)
				{
					sc_switch_hand.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_HAND, 1);
					ed.commit();
					
//					mainActivity.bottom_layout.removeAllViews();
//					
//					mainActivity.bottom_layout.addView(mainActivity.setting_ly);
//					mainActivity.bottom_layout.addView(mainActivity.dialing_ly);
//					mainActivity.bottom_layout.addView(mainActivity.huxing_ly);
					
				}else{
					sc_switch_hand.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_HAND, 0);
					ed.commit();
					
//					mainActivity.bottom_layout.removeAllViews();
//
//					mainActivity.bottom_layout.addView(mainActivity.huxing_ly);
//					mainActivity.bottom_layout.addView(mainActivity.dialing_ly);
//					mainActivity.bottom_layout.addView(mainActivity.setting_ly);
				}
			}
		});
		
		int hand = sf.getInt(SF_KEY_HAND, 1);
		if(hand == 1)
		{
			sc_switch_hand.setToScreen(1);
		}
		
		
		sc_switch_panel = (ScrollLayout)view.findViewById(R.id.sc_switch_panel);
		sc_switch_panel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(sc_switch_panel.getCurScreen()==0)
				{
					sc_switch_panel.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_DIALING_PANEL, 1);
					ed.commit();
				}else{
					sc_switch_panel.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_DIALING_PANEL, 0);
					ed.commit();
				}
			}
		});
		
		int panel = sf.getInt(SF_KEY_DIALING_PANEL, 1);
		if(panel == 1)
		{
			sc_switch_panel.setToScreen(1);
		}
		
		//联系人排序
		sc_switch_contact = (ScrollLayout)view.findViewById(R.id.sc_switch_contact);
		sc_switch_contact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Editor sortEd = sf.edit();
				
				int sort = sf.getInt(SF_KEY_CONTACT_SORT, 1);
				
				sortEd.putInt(REFRESH_CONTACT, sort);
				sortEd.commit();
				
				if(sc_switch_contact.getCurScreen()==0)
				{
					sc_switch_contact.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_CONTACT_SORT, 1);
					ed.commit();
				}else{
					sc_switch_contact.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_CONTACT_SORT, 0);
					ed.commit();
				}
			}
		});
		
		int contact_sort = sf.getInt(SF_KEY_CONTACT_SORT, 1);
		if(contact_sort == 1)
		{
			sc_switch_contact.setToScreen(1);
		}
		//短信排序方式
		sc_switch_sms = (ScrollLayout)view.findViewById(R.id.sc_switch_sms);
		sc_switch_sms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Editor sortEd = sf.edit();
				
				int sort = sf.getInt(SF_KEY_SMS_SORT, 1);
				
				sortEd.putInt(REFRESH_SMS, sort);
				sortEd.commit();
				
				if(sc_switch_sms.getCurScreen()==0)
				{
					sc_switch_sms.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_SMS_SORT, 1);
					ed.commit();
				}else{
					sc_switch_sms.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_SMS_SORT, 0);
					ed.commit();
				}
			}
		});
		
		int sms_sort = sf.getInt(SF_KEY_SMS_SORT, 1);
		if(sms_sort == 1)
		{
			sc_switch_sms.setToScreen(1);
		}
		
		//通话记录排序方式
		sc_switch_callog = (ScrollLayout)view.findViewById(R.id.sc_switch_callog);
		sc_switch_callog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Editor sortEd = sf.edit();
				
				int sort = sf.getInt(SF_KEY_COLLOG_SORT, 1);
				
				sortEd.putInt(REFRESH_CALLLOG, sort);
				sortEd.commit();
				
				if(sc_switch_callog.getCurScreen()==0)
				{
					sc_switch_callog.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_COLLOG_SORT, 1);
					ed.commit();
				}else{
					sc_switch_callog.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_COLLOG_SORT, 0);
					ed.commit();
				}
			}
		});
		
		int callog_sort = sf.getInt(SF_KEY_COLLOG_SORT, 1);
		if(callog_sort == 1)
		{
			sc_switch_callog.setToScreen(1);
		}
		
		
		sc_switch_sms_remind = (ScrollLayout)view.findViewById(R.id.sc_switch_sms_remind);
		sc_switch_sms_remind.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sc_switch_sms_remind.getCurScreen()==0)
				{
					sc_switch_sms_remind.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_SMS_REMIND, 1);
					ed.commit();
				}else{
					sc_switch_sms_remind.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_SMS_REMIND, 0);
					ed.commit();
				}
			}
		});
		
		int sms_remind = sf.getInt(SF_KEY_SMS_REMIND, 1);
		if(sms_remind == 1)
		{
			sc_switch_sms_remind.setToScreen(1);
		}
		
		
		sc_switch_remind_all = (ScrollLayout)view.findViewById(R.id.sc_switch_remind_all);
		sc_switch_remind_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			
				
				if(sc_switch_remind_all.getCurScreen()==0)
				{
					sc_switch_remind_all.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_REMIND_ALL, 1);
					ed.commit();
					
					int remind_all = sf.getInt(SF_KEY_REMIND_ALL, 1);
					System.out.println("  remind_all  --->" + remind_all);
					
					sc_switch_remind_group.snapToScreen(1);
					Editor eds = sf.edit();
					eds.putInt(SF_KEY_REMIND_GROUP, 0);
					eds.commit();
					
				}else{
					sc_switch_remind_all.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_REMIND_ALL, 0);
					ed.commit();
					
					int remind_all = sf.getInt(SF_KEY_REMIND_ALL, 1);
					System.out.println("  remind_all  --->" + remind_all);
				}
			}
		});
		
		int remind_all = sf.getInt(SF_KEY_REMIND_ALL, 1);
		
		
		if(remind_all == 1)
		{
			sc_switch_remind_all.setToScreen(1);
		}
		//关闭联系人组提醒
		
		sc_switch_remind_group = (ScrollLayout)view.findViewById(R.id.sc_switch_remind_group);
		sc_switch_remind_group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int close_remind_all = sf.getInt(SystemSettingActivity.SF_KEY_REMIND_ALL, 1);
				if(close_remind_all==1)
				{
					sc_switch_remind_group.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_REMIND_GROUP, 1);
					ed.commit();
					
				}else
				{
					if(sc_switch_remind_group.getCurScreen()==0)
					{
						sc_switch_remind_group.snapToScreen(1);
						Editor ed = sf.edit();
						ed.putInt(SF_KEY_REMIND_GROUP, 1);
						ed.commit();
						
					}else{
						sc_switch_remind_group.snapToScreen(0);
						Editor ed = sf.edit();
						ed.putInt(SF_KEY_REMIND_GROUP, 0);
						ed.commit();
						
					}
				}
				
			}
		});
		
		int remind_group = sf.getInt(SF_KEY_REMIND_GROUP, 1);
		
		if(remind_group == 1 || remind_all==1)
		{
			sc_switch_remind_group.setToScreen(1);
		}
		
		
		sc_switch_secretary_sex = (ScrollLayout)view.findViewById(R.id.sc_switch_secretary_sex);
		sc_switch_secretary_sex.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sc_switch_secretary_sex.getCurScreen()==0)
				{
					sc_switch_secretary_sex.snapToScreen(1);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_SECRETARY_SEX, 1);
					ed.commit();
				}else{
					sc_switch_secretary_sex.snapToScreen(0);
					Editor ed = sf.edit();
					ed.putInt(SF_KEY_SECRETARY_SEX, 0);
					ed.commit();
				}
			}
		});
		
		int secretary_sex = sf.getInt(SF_KEY_SECRETARY_SEX, 1);
		if(secretary_sex == 1)
		{
			sc_switch_secretary_sex.setToScreen(1);
		}
		
		et_secretary_name = (EditText)view.findViewById(R.id.et_secretary_name);
		String s_name = sf.getString(SF_KEY_SECRETARY_NAME, "小秘书");
		et_secretary_name.setText(s_name);
		
		et_secretary_name.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				Editor ed = sf.edit();
				ed.putString(SF_KEY_SECRETARY_NAME, s.toString());
				ed.commit();
			}
		});
		
		desktop_shortcut = (Button) view.findViewById(R.id.desktop_shortcut);
		
		desktop_shortcut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				checkFirstLauncher();
				
			}
		});
		
	}
	
	private static final int CALL_PHONE_SHORTCUT = 1;
	private static final int CONTACT_SHORTCUT = 2;
	private static final int SMS_SHORTCUT = 3;
	private static final String CALL_PHONE_STRING = "打电话";
	private static final String CONTACT_STRING = "联系人";
	private static final String SMS_STRING = "短信";
	
	private void checkFirstLauncher() {
		boolean callPhoneExists=checkExistsShortcut(CALL_PHONE_SHORTCUT);
		if(!callPhoneExists) {
			createShortcut(CALL_PHONE_SHORTCUT);
		}
		boolean contactExists=checkExistsShortcut(CONTACT_SHORTCUT);
		if(!contactExists) {
			createShortcut(CONTACT_SHORTCUT);
		}
		boolean smsExists=checkExistsShortcut(SMS_SHORTCUT);
		if(!smsExists) {
			createShortcut(SMS_SHORTCUT);
		}
	}
	
	/**
	 * 创建桌面快捷方式
	 */
	private void createShortcut(int type) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		String str = null;
		
		switch(type) {
			case CALL_PHONE_SHORTCUT:
				// 指定当前的Activity为快捷方式启动的对象: com.everest.video.VideoPlayer
				// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程
				ComponentName comp = new ComponentName(getPackageName(),CallPhoneActivity.class.getName());
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
				// 快捷方式的名称
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, CALL_PHONE_STRING);
				// 快捷方式的图
				ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.call_phone_icon);
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
				
				str = "打电话";
				
				break;
			case CONTACT_SHORTCUT:
				// 指定当前的Activity为快捷方式启动的对象: com.everest.video.VideoPlayer
				// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程
				ComponentName comp2 = new ComponentName(getPackageName(),ContactActivity.class.getName());
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp2));
				// 快捷方式的名称
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, CONTACT_STRING);
				// 快捷方式的图
				ShortcutIconResource iconRes2 = Intent.ShortcutIconResource.fromContext(this, R.drawable.contact_icon);
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes2);
				
				str = "联系人";
				break;
			case SMS_SHORTCUT:
				// 指定当前的Activity为快捷方式启动的对象: com.everest.video.VideoPlayer
				// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程
				ComponentName comp3 = new ComponentName(this.getPackageName(),SMSActivity.class.getName());
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp3));
				// 快捷方式的名称
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, SMS_STRING);
				// 快捷方式的图
				ShortcutIconResource iconRes3 = Intent.ShortcutIconResource.fromContext(this, R.drawable.send_sms_icon);
				shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes3);
				
				str = "短信";
				break;
		}
		
		sendBroadcast(shortcut);
		
		Toast.makeText(this, "已生成"+str+"快捷方式！",Toast.LENGTH_SHORT).show();
		
	}

	/***
	 * 检查桌面是否存在此快捷方式
	 */
	private boolean checkExistsShortcut(int type) {
		boolean result = false;
		// 获取当前应用名称
		String title = null;
		switch(type) {
			case CALL_PHONE_SHORTCUT:
				title=CALL_PHONE_STRING;
				break;
			case CONTACT_SHORTCUT:
				title=CONTACT_STRING;
				break;
			case SMS_SHORTCUT:
				title = SMS_STRING;
				break;
		}

		final String uriStr;
		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);
		
		final Cursor c = getContentResolver().query(CONTENT_URI, null,"title=?", new String[]{title}, null);
		
		if (c != null && c.getCount() > 0) {
			result = true;
		}
		
		if (result) {
			Toast.makeText(this, "手机桌面存在"+title+"快捷方式！",Toast.LENGTH_SHORT).show();
		}
		
		return result;
	}
	
	
	
}
