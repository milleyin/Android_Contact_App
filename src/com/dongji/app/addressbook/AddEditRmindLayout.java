package com.dongji.app.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.RemindContactsAdapter;
import com.dongji.app.adapter.RemindContactsMutilAdapter;
import com.dongji.app.adapter.SmsExpandableListAdapter;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.GroupInfo;
import com.dongji.app.entity.WheelMain;
import com.dongji.app.entity.WheelMainRepeatRange;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.ui.SideBar;

/**
 * 
 * 新建  或  编辑提醒  (非activity 实际的载体是对话框dialog)
 * 
 * @author zy_wu
 *
 */
public class AddEditRmindLayout implements OnClickListener {
	
	int id;
	
	Context context;
	
	public View v ;
	
	TextView tv_top_title; //标题栏
	
	EditText et_content;
	
	Button btn_pick_contact; //选择联系人
	CheckBox cb_me;
	
	Button btn_pick_partner; //选择参与人
	
	Button btn_start_time;
	Button btn_end_time;
	CheckBox cb_all_day;
	
	
	Button save;
	Button cancel;
	
	int remind_type = MyDatabaseUtil.REMIND_TYPE_MIN; //新建时，默认提醒类型为min
	Button btn_remind_type; //提醒类型选择
	EditText et_remind_num; //提醒数值
	EditText et_remind_time;
	
	int repeat_type  = MyDatabaseUtil.REPEAT_TYPE_ONE ;
	Button btn_repeat_type; //重复类型选择
	LinearLayout repeat_rate; //重复频率
	EditText et_repeat_freq;
	
	TextView repeat_rate_type;
	
	LinearLayout repeat_time; //重复时间
	LinearLayout ln_repeat_time_week;
	LinearLayout ln_repeat_time_month;
	
	Dialog pic_repeat_range_dialog;
	WheelMainRepeatRange wheelMainRepeatRange;
	LinearLayout ln_repeat_time_range; //重复的开始时间 和 结束时间
	
	int dialog_type = 0;
	LinearLayout ln_remind_type;
	LinearLayout ln_repeat_type;
	RadioGroup remind_type_rg;
	RadioGroup repeat_type_rg;
	
	String repeat_week_condition = ""; //周重复条件
	CheckBox cb_monday;    //周一
	CheckBox cb_tuesday;   //周二
	CheckBox cb_wednesday; //周三
	CheckBox cb_thursday;  //周四
	CheckBox cb_friday;    //周五
	CheckBox cb_saturday;  //周六
	CheckBox cb_sunday;    //周日
	
	int repeat_month_condition = 1;   //月重复条件  :   1: 每月的第几天   ;   2:每月的第几周的周几
	RadioGroup repeat_time_month_rg;
	RadioButton b_day_in_month;
	RadioButton b_day_in_week_in_month;
	
	OnFinishEditRemindListener onFinishEditRemindListener;
	
	Dialog dialog = null;
	
	//时间开始时间 和 结束时间的选择框
	Dialog pic_date_dialog;
	int  pick_time_type ;   //0:选择开始时间  ,  1:选择结束时间
	long start_time = -1;  //提醒的开始时间
	long end_time = -1;    //提醒的结束时间
	WheelMain wheelMain;
	Button buttonsure;
	Button buttoncancle;
	
	
	Dialog choose_remind_type;
	Dialog choose_repeat_type;
	
	Calendar car;
	
	String contact_str = "" ; //相关信息联系人:   #id#:name:p,p,p
	int c_id = -1;
	
	String partner_str = ""; //参与人相关信息 (以分号隔开) :  #id#:name:p,p,p ;  #id#:name:p,p,p  ;
	int [] p_ids;
	String partner_names = "";
	
	List<ContactBean> contacts;
	boolean isQuerying = false;
	
	Dialog contact_dialog;
	ListView lv_contact;
	RemindContactsAdapter remindContactsAdapter;
	
	SideBar sideBar;
	
	int partner_type = 0;  //  0:多选联系人     1:分组内选联系人
	Dialog partake_dialog ;
	
	LinearLayout contact_ln;
	ListView lv_partake_contact;
	RemindContactsMutilAdapter remindContactsMutilAdapter;
	ExpandableListView group_expandableListView;
	SmsExpandableListAdapter smsExpandableListAdapter;
	ArrayList<ArrayList<GroupInfo>> childInfos = new ArrayList<ArrayList<GroupInfo>>();
	ArrayList<GroupInfo> aGroupInfos ;
	
	Button btn_repeat_start_time;
	Button btn_repeat_end_time;
	
	int  repeat_range_type = 0;
	long repeat_start_time = -1;
	long repeat_end_time = -1;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				remindContactsAdapter = new RemindContactsAdapter(context, contacts);
				lv_contact.setAdapter(remindContactsAdapter);
				
				isQuerying = false;
				break;
				
			case 1:
				remindContactsMutilAdapter = new RemindContactsMutilAdapter(context, contacts);
	 			lv_partake_contact.setAdapter(remindContactsMutilAdapter);
	 			  
				isQuerying = false;
				break;
				
			case 2:
				
				smsExpandableListAdapter = new SmsExpandableListAdapter(context,aGroupInfos,childInfos);
        		
				group_expandableListView.setAdapter(smsExpandableListAdapter);
				
				break;

			default:
				break;
			}
			
		};
	};
	
	
	/**
	 * 
	 * @param context
	 * @param id  -1 为新建  ; 反之为编辑
	 * @param contactId 联系人id ： 不为 -1  则表示创建与某某联系人的提醒
	 * @param onFinishEditRemindListener
	 * 
	 */
	public AddEditRmindLayout(Context context,int id ,int contactId ,OnFinishEditRemindListener onFinishEditRemindListener)
	{
		this.context = context;
		this.onFinishEditRemindListener = onFinishEditRemindListener;
		this.id = id;
		
		car  = Calendar.getInstance();
		
		v = LayoutInflater.from(context).inflate(R.layout.add_remind, null);

		dialog = new Dialog(context,R.style.theme_myDialog);
		dialog.setContentView(v);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		
		tv_top_title = (TextView)dialog.findViewById(R.id.tv_top_title);
		
		et_content = (EditText)dialog.findViewById(R.id.et_content);
		
		btn_pick_contact = (Button)dialog.findViewById(R.id.btn_pick_contact);
		btn_pick_contact.setOnClickListener(this);
		cb_me = (CheckBox)dialog.findViewById(R.id.cb_me);
		cb_me.setOnCheckedChangeListener(new MeCheckBoxOnCheckChangeListener());
		
		btn_pick_partner = (Button)dialog.findViewById(R.id.btn_pick_partner);
		btn_pick_partner.setOnClickListener(this);
		
		btn_start_time = (Button)dialog.findViewById(R.id.btn_start_time);
		btn_start_time.setOnClickListener(this);
		btn_end_time = (Button)dialog.findViewById(R.id.btn_end_time);
		btn_end_time.setOnClickListener(this);
		cb_all_day = (CheckBox)dialog.findViewById(R.id.cb_all_day);
		cb_all_day.setOnCheckedChangeListener(new AllDayCheckBoxOnCheckChangeListener());
		
		et_remind_num = (EditText)dialog.findViewById(R.id.et_remind_num);
		et_remind_time = (EditText)dialog.findViewById(R.id.et_remind_time);
		
		et_repeat_freq = (EditText)dialog.findViewById(R.id.et_repeat_freq);
		
		save = (Button) dialog.findViewById(R.id.save);
		cancel = (Button) dialog.findViewById(R.id.cancel);
		repeat_rate = (LinearLayout) dialog.findViewById(R.id.repeat_rate);
		repeat_rate_type = (TextView) dialog.findViewById(R.id.repeat_rate_type);
		repeat_time = (LinearLayout) dialog.findViewById(R.id.repeat_time); 
		ln_repeat_time_week = (LinearLayout) dialog.findViewById(R.id.ln_repeat_time_week);
		ln_repeat_time_month = (LinearLayout) dialog.findViewById(R.id.ln_repeat_time_month);
		
		ln_repeat_time_range = (LinearLayout) dialog.findViewById(R.id.ln_repeat_time_range);
		
		btn_remind_type = (Button) dialog.findViewById(R.id.remind_type);
		btn_repeat_type = (Button) dialog.findViewById(R.id.repeat_type);
		
		WeekOnCheckChangeListener weekOnCheckChangeListener = new WeekOnCheckChangeListener();
		
		cb_monday = (CheckBox )dialog.findViewById(R.id.cb_monday);
		cb_monday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		cb_tuesday= (CheckBox )dialog.findViewById(R.id.cb_tuesday);
		cb_tuesday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		cb_wednesday= (CheckBox )dialog.findViewById(R.id.cb_wednesday);
		cb_wednesday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		cb_thursday= (CheckBox )dialog.findViewById(R.id.cb_thursday);
		cb_thursday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		cb_friday= (CheckBox )dialog.findViewById(R.id.cb_friday);
		cb_friday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		cb_saturday= (CheckBox )dialog.findViewById(R.id.cb_saturday);
		cb_saturday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		cb_sunday= (CheckBox )dialog.findViewById(R.id.cb_sunday);
		cb_sunday.setOnCheckedChangeListener(weekOnCheckChangeListener);
		
		
		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		btn_remind_type.setOnClickListener(this);
		btn_repeat_type.setOnClickListener(this);
		
		
		//时间选择框
		pic_date_dialog = new Dialog(context, R.style.theme_myDialog);
		pic_date_dialog.setCanceledOnTouchOutside(true);
		
		View layoutss = LayoutInflater.from(context).inflate(R.layout.pick_remind_start_end_time, null);
		View timePicker1 = layoutss.findViewById(R.id.timePicker1);
		wheelMain = new WheelMain(timePicker1);
		wheelMain.initDateTimePicker();
		buttonsure = (Button) layoutss.findViewById(R.id.buttonsure);
		buttonsure.setOnClickListener(new PickDateClickListener());
		buttoncancle = (Button) layoutss.findViewById(R.id.buttoncancle);
		buttoncancle.setOnClickListener(new PickDateClickListener());
		
		pic_date_dialog.setContentView(layoutss);
		
		
		//重复范围时间选择对话框
		pic_repeat_range_dialog = new Dialog(context, R.style.theme_myDialog);
		pic_repeat_range_dialog.setCanceledOnTouchOutside(true);

		
		View lay = LayoutInflater.from(context).inflate(R.layout.pick_remind_repeat_start_end_time, null);
		View timePicker = lay.findViewById(R.id.timePicker1);
		wheelMainRepeatRange = new WheelMainRepeatRange(timePicker);
		wheelMainRepeatRange.initDateTimePicker();
		buttonsure = (Button) lay.findViewById(R.id.buttonsure);
		buttonsure.setOnClickListener(new RepeatRangeClickListener());
		buttoncancle = (Button) lay.findViewById(R.id.buttoncancle);
		buttoncancle.setOnClickListener(new RepeatRangeClickListener());

		pic_repeat_range_dialog.setContentView(lay);
		
		repeat_time_month_rg = (RadioGroup)dialog.findViewById(R.id.repeat_time_month_rg);
		repeat_time_month_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId==R.id.b_day_in_month){ //每月的第几天
					repeat_month_condition = 1;
				}else{
					repeat_month_condition = 2;
				}
//				System.out.println("  repeat_month_condition  ----> " + repeat_month_condition);
			}
		});
		
		b_day_in_month = (RadioButton)repeat_time_month_rg.findViewById(R.id.b_day_in_month);
		b_day_in_week_in_month = (RadioButton)repeat_time_month_rg.findViewById(R.id.b_day_in_week_in_month);
		
		
		//提醒类型
		choose_remind_type = new Dialog(context,R.style.theme_myDialog);
		choose_remind_type.setCanceledOnTouchOutside(true);
		choose_remind_type.setContentView(R.layout.remind_dialog);
		remind_type_rg = (RadioGroup) choose_remind_type.findViewById(R.id.remind_type_rg);
		remind_type_rg.setOnCheckedChangeListener(onChecked);
		
		
		//重复类型
		choose_repeat_type = new Dialog(context,R.style.theme_myDialog);
		choose_repeat_type.setCanceledOnTouchOutside(true);
		choose_repeat_type.setContentView(R.layout.remind_repeat_dialog);
		repeat_type_rg = (RadioGroup) choose_repeat_type.findViewById(R.id.repeat_type_rg);
		repeat_type_rg.setOnCheckedChangeListener(onChecked);
		
		
		btn_repeat_start_time = (Button) dialog.findViewById(R.id.btn_repeat_start_time);
		btn_repeat_start_time.setOnClickListener(this);
		btn_repeat_end_time = (Button) dialog.findViewById(R.id.btn_repeat_end_time);
		btn_repeat_end_time.setOnClickListener(this);
		
		
		
		if(id==-1) //////////////////////////////新建提醒
		{
			loadAddRemind(contactId);
			
		}else{  ////////////////////////////////////编辑提醒
			
			loadRemindDetail();
			
		}
		
	}
	
	/**
	 * 新建提醒
	 * 
	 * @param contactId 联系人id
	 * 
	 */
	void loadAddRemind(int contactId)
	{
		//初始开始时间
		start_time = System.currentTimeMillis()+(10*60*1000);
		btn_start_time.setText(getTimeStrYYMMDDHHMM(start_time));
		
		//初始结束时间
		end_time = start_time+(24*60*60*1000);
		btn_end_time.setText(getTimeStrYYMMDDHHMM(end_time));
		
		if(contactId!=-1) //新建与某某人提醒
		{
			//获取联系人姓名
			
			String disPlayName="";
		    String selection = RawContacts.CONTACT_ID + "=" + contactId;
		    Cursor mCursor = context.getContentResolver().query(RawContactsEntity.CONTENT_URI,null, selection, null, null);
		 	int count = mCursor.getCount();
			String itemMimeType;
			mCursor.moveToFirst();
			for (int i = 0; i < count; i++) {
				itemMimeType = mCursor.getString(mCursor.getColumnIndex(RawContactsEntity.MIMETYPE));
				if (itemMimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
					// 先保存在data表中_id的值
					disPlayName = mCursor.getString(mCursor.getColumnIndex(StructuredName.DISPLAY_NAME));
				}
				mCursor.moveToNext();
			}
			mCursor.close();
			String pp =" ";
			StringBuffer sb_phones = new StringBuffer();
			// 获得联系人的电话号码
			Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
			while (phones.moveToNext()) {
				// 遍历所有的电话号码
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				sb_phones.append(phoneNumber+",");
				};
			phones.close();
			
			if(sb_phones.toString().length()>0)
			{
				pp= sb_phones.substring(0, sb_phones.length()-1);
			}
			contact_str="#"+contactId+"#:"+disPlayName+":"+pp;
			
			btn_pick_contact.setText(disPlayName);
			
			String str = "新建与 "+disPlayName+" 的提醒";
					
			tv_top_title.setText(Html.fromHtml(str.replace(disPlayName, "<font color='#3d8eba'>" + disPlayName+ "</font>")));
			
		}else{
			tv_top_title.setText("新建提醒");
		}
	}
	
	
	/**
	 * 
	 * 编辑提醒
	 * 
	 */
	void loadRemindDetail()
	{
		tv_top_title.setText("编辑提醒");
		
		
		Cursor c = DButil.getInstance(context).queryRemind(id);
		c.moveToNext();
		String content = c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT));
		contact_str = c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT));
		String [] contact_ss = contact_str.split(":");
		
		c_id = Integer.valueOf(contact_ss[0].replace("#", ""));
		
		btn_pick_contact.setText(contact_ss[1]);
		
		SharedPreferences ss = context.getSharedPreferences("myNumberContactId", 0);
		long myContactId = ss.getLong("myContactId", -1); //机主联系人id
		if(c_id==myContactId){
			cb_me.setChecked(true);
			btn_pick_contact.setText("我");
		}
		
		partner_str = c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_PARTICIPANT));
		if(!partner_str.equals("")){
			String [] partner_ss = partner_str.split(";");
			p_ids = new int[partner_ss.length];
			StringBuffer sb_name = new StringBuffer();
			for(int i =0;i<partner_ss.length;i++)
			{
				String [] c_s = partner_ss[i].split(":");
				p_ids[i] = Integer.valueOf(c_s[0].replace("#", ""));
				sb_name.append(c_s[1]+";");
			}
			partner_names = sb_name.substring(0, sb_name.length()-1);
			btn_pick_partner.setText(partner_names);
		}
		
		et_content.setText(content);
		
		start_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START));
		btn_start_time.setText(getTimeStrYYMMDDHHMM(start_time));
		end_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_END));
		btn_end_time.setText(getTimeStrYYMMDDHHMM(end_time));
		
		
		int remind_num = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM));
		et_remind_num.setText(String.valueOf(remind_num));
		
		RadioButton r ;
		remind_type = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE));
		switch (remind_type) {
		case MyDatabaseUtil.REMIND_TYPE_MIN:
			r = (RadioButton)remind_type_rg.findViewById(R.id.remind_type_min);
			r.setChecked(true);
			break;

		case MyDatabaseUtil.REMIND_TYPE_HOUR:
			r = (RadioButton)remind_type_rg.findViewById(R.id.remind_type_hour);
			r.setChecked(true);
			break;

		case MyDatabaseUtil.REMIND_TYPE_DAY:
			r = (RadioButton)remind_type_rg.findViewById(R.id.remind_type_day);
			r.setChecked(true);
			break;
		case MyDatabaseUtil.REMIND_TYPE_WEEK:
			r = (RadioButton)remind_type_rg.findViewById(R.id.remind_type_week);
			r.setChecked(true);
			break;

		default:
			break;
		}
		
		
		int remind_time = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TIME));
		et_remind_time.setText(String.valueOf(remind_time));
		
		//重复
		repeat_type = c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE));
		
		String repeat_condition = c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION));
		
		RadioButton rr;
		
		switch (repeat_type) {
		
		case MyDatabaseUtil.REPEAT_TYPE_ONE:
			rr = (RadioButton)repeat_type_rg.findViewById(R.id.once);
			rr.setChecked(true);
			break;

		case MyDatabaseUtil.REPEAT_TYPE_DAY:
			rr = (RadioButton)repeat_type_rg.findViewById(R.id.day);
			rr.setChecked(true);
			break;

		case MyDatabaseUtil.REPEAT_TYPE_WEEK:
			rr = (RadioButton)repeat_type_rg.findViewById(R.id.week);
			rr.setChecked(true);
			
			this.repeat_week_condition = repeat_condition;
			
			if(repeat_condition.contains("1"))
			{
				cb_monday.setChecked(true);
			}
			
			if(repeat_condition.contains("2"))
			{
				cb_tuesday.setChecked(true);
			}
			
			if(repeat_condition.contains("3"))
			{
				cb_wednesday.setChecked(true);
			}
			
			if(repeat_condition.contains("4"))
			{
				cb_thursday.setChecked(true);
			}
			
			if(repeat_condition.contains("5"))
			{
				cb_friday.setChecked(true);
			}
			
			if(repeat_condition.contains("6"))
			{
				cb_saturday.setChecked(true);
			}
			
			if(repeat_condition.contains("7"))
			{
				cb_sunday.setChecked(true);
			}
			
			break;

		case MyDatabaseUtil.REPEAT_TYPE_MONTH:
			rr = (RadioButton)repeat_type_rg.findViewById(R.id.month);
			rr.setChecked(true);
			
			this.repeat_month_condition = Integer.valueOf(repeat_condition);
			if(repeat_month_condition==1)
			{
				b_day_in_month.setChecked(true);
				
			}else{
				b_day_in_week_in_month.setChecked(true);
			}
			
			Calendar car = Calendar.getInstance();
			Date date = new Date(start_time);
			car.setTime(date);
			int day = car.get(Calendar.DATE);
			b_day_in_month.setText("每月 的 第"+day+"天");
			
	        int week_of_month = car.get(Calendar.WEEK_OF_MONTH);
	        int day_of_week = car.get(Calendar.DAY_OF_WEEK);
	        
	        String dd ="";
	        
	        switch (day_of_week) {
			case 1:
				dd="日";
				break;
            case 2:
            	dd="一";
				break;
				
            case 3:
            	dd="二";
                break;
            case 4:
            	dd="三";
                break;
            case 5:
            	dd="四";
                break;

            case 6:
            	dd="五";
                break;
            case 7:
            	dd="六";
                break;
			default:
				break;
			}
	        b_day_in_week_in_month.setText("每月 的 第"+week_of_month+"个星期的周"+dd);
			
			break;

		case MyDatabaseUtil.REPEAT_TYPE_YEAR:
			rr = (RadioButton)repeat_type_rg.findViewById(R.id.year);
			rr.setChecked(true);
			break;

		default:
			break;
		}
		
		int repeat_freq = c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ));
		et_repeat_freq.setText(String.valueOf(repeat_freq));
		
		repeat_start_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME));
		
		if(repeat_start_time==-1 || repeat_start_time==0)
		{
			btn_repeat_start_time.setText("设置");
		}else{
			btn_repeat_start_time.setText(getTimeStrYYMMDD(repeat_start_time));
		}
		
		repeat_end_time =  c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME));
		
		if(repeat_end_time==-1 || repeat_end_time==0)
		{
			btn_repeat_end_time.setText("设置");
		}else{
			btn_repeat_end_time.setText(getTimeStrYYMMDD(repeat_end_time));
		}
		
//		System.out.println("start_time ---> " + start_time);
//		System.out.println(" end_time  --->" + end_time);
//		System.out.println(" content  --->" + content);
//		System.out.println(" contact_str  --->" + contact_str);
//		System.out.println(" partner_str  --->" + partner_str);
//		
//		System.out.println(" remind_type  --->" + remind_type); 
//		System.out.println(" remind_num  --->" + remind_num);
//		
//		System.out.println(" repeat_type  --->" + repeat_type); 
//		System.out.println(" repeat_freq  --->" + repeat_freq);
//		
//		System.out.println(" repeat_condition  --->" + repeat_condition);
//		
//		System.out.println(" repeat_start_time  --->" + repeat_start_time);
//		System.out.println(" repeat_end_time  --->" + repeat_end_time);
		
		c.close();
		
	}
	
	
	public interface OnFinishEditRemindListener{ 
		
		public void OnFinishEditRemind();
	}
	
	
	public void setRemindContent(String content)
	{
		et_content.setText(content);
	}

	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		    case R.id.btn_pick_contact:
		    	if(cb_me.isChecked())
		    	{
		    		contact_str = "";
					btn_pick_contact.setText("选择联系人");
					cb_me.setChecked(false);
		    	}
		    	showContactDilaog();
			    break;
			
		    case R.id.btn_pick_partner:
		    	showPartnerDialog();
		    	break;
		    	
		    case R.id.btn_start_time:
		    	pick_time_type = 0;
		    	if(start_time!=-1)
		    	{
		    		Date date = new Date(start_time);
			    	car.setTime(date);
			    	wheelMain.setToTime(car.get(Calendar.YEAR), car.get(Calendar.MONTH), car.get(Calendar.DATE), car.get(Calendar.HOUR_OF_DAY), car.get(Calendar.MINUTE));
		    	}
		    	
		    	pic_date_dialog.show();
		    	break;
		    	
		    case R.id.btn_end_time:
		    	pick_time_type =1;
		    	if(end_time!=-1)
		    	{
		    		Date date = new Date(end_time);
			    	car.setTime(date);
			    	wheelMain.setToTime(car.get(Calendar.YEAR), car.get(Calendar.MONTH), car.get(Calendar.DATE), car.get(Calendar.HOUR_OF_DAY), car.get(Calendar.MINUTE));
		    	}
		    	pic_date_dialog.show();
		    	break;
		    	
			case R.id.save:
				
				if(check())
				{
					if(id==-1) //添加
					{
						save();
					}else{     //修改编辑
						update();
					}
					
					dialog.cancel();
					
					if(onFinishEditRemindListener!=null)
					{
						onFinishEditRemindListener.OnFinishEditRemind();
					}
					
					MainActivity.isChangeRemindData = true;
				}
				
				break;
				
			case R.id.cancel:
				
				dialog.cancel();
				
				break;
			
			case R.id.remind_type:
				
				choose_remind_type.show();
				
				break;
				
			case R.id.repeat_type:
				
				choose_repeat_type.show();
				
				break;	
				
			case R.id.btn_repeat_start_time:
				repeat_range_type =0;
		    	if(repeat_start_time!=-1)
		    	{
		    		Date date = new Date(repeat_start_time);
			    	car.setTime(date);
			    	wheelMainRepeatRange.setToTime(car.get(Calendar.YEAR), car.get(Calendar.MONTH), car.get(Calendar.DATE));
		    	}
		    	pic_repeat_range_dialog.show();
				break;
				
			case R.id.btn_repeat_end_time:
				repeat_range_type =1;
		    	if(repeat_end_time!=-1)
		    	{
		    		Date date = new Date(repeat_end_time);
			    	car.setTime(date);
			    	wheelMainRepeatRange.setToTime(car.get(Calendar.YEAR), car.get(Calendar.MONTH), car.get(Calendar.DATE));
		    	}
		    	pic_repeat_range_dialog.show();
				break;
				
			default:
				break;
		}
	}
	
	
	/**
	 * 
	 * 新建提醒后保存
	 * 
	 */
	void save()
	{
		String repeat_condition = "0";
		
		if(repeat_type==MyDatabaseUtil.REPEAT_TYPE_WEEK)
		{
			repeat_condition = repeat_week_condition;
		}else if (repeat_type==MyDatabaseUtil.REPEAT_TYPE_MONTH)
		{
			repeat_condition = String.valueOf(repeat_month_condition);
		}
		
//		System.out.println("before ---  ----");
//		System.out.println(" start_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(start_time));
//		System.out.println(" end_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(end_time));
		
		if(cb_all_day.isChecked())
		{
			Date start_date = new Date(start_time);
			car.setTime(start_date);
			Date s_date = new Date(car.get(Calendar.YEAR)-1900, car.get(Calendar.MONTH),car.get(Calendar.DATE), 0, 0);
			start_time = s_date.getTime();
			
			Date end_date = new Date(end_time);
			car.setTime(end_date);
			Date e_date = new Date(car.get(Calendar.YEAR)-1900, car.get(Calendar.MONTH),car.get(Calendar.DATE), 0, 0);
			end_time = e_date.getTime();
			
//			System.out.println("after ---  ----");
//			System.out.println(" start_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(start_time));
//			System.out.println(" end_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(end_time));
		}else {
			long time_gap = end_time - start_time;
			if(time_gap == 24*60*60*1000){
				end_time -=10000;
			}
		}
		
		// 周重复的时间判断
		if (repeat_type == MyDatabaseUtil.REPEAT_TYPE_WEEK) {
			Calendar c = Calendar.getInstance();
			Date dd = new Date(start_time);
			c.setTime(dd);

			System.out.println(" 当前设定的事件开始时间  --->"+ new SimpleDateFormat("yyyy-MM-dd HH:mm").format(start_time));

			int cur_week_day = c.get(Calendar.DAY_OF_WEEK);
			if (cur_week_day == 1) {
				cur_week_day = 7;
			} else {
				cur_week_day = cur_week_day - 1;
			}

			String[] w_ss = repeat_week_condition.split(",");

			if (w_ss.length == 0) {
				w_ss = new String[] { repeat_week_condition };
			}
			int index = -1;

			for (int i = 0; i < w_ss.length; i++) {
				if (w_ss[i].equals(String.valueOf(cur_week_day))) {
					index = i;
					break;
				}
			}

			if (index == -1) // 说明没找到
			{
				int jj = -1;
				for (int i = 0; i < w_ss.length; i++) {
					if (Integer.valueOf(w_ss[i]) > cur_week_day) {
						jj = Integer.valueOf(w_ss[i]);
						break;
					}
				}

				int time_crap = 0; //时间差
				
				if (jj == -1) // 直接跳到下次的重复开始周几去
				{
					int day_crap = 7 - cur_week_day + Integer.valueOf(w_ss[0]);
                    time_crap = day_crap * 24 * 60 * 60 * 1000;
					// System.out.println(" 下周的第一个重复  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm").format(start_time));
				} else {
					int day_crap = jj - cur_week_day; // 相差几天
					time_crap = day_crap * 24 * 60 * 60 * 1000;
					// System.out.println(" 本周内 ， 非今天重复  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm").format(start_time));
				}
				
				start_time = start_time + time_crap;
				end_time = end_time + time_crap;
				repeat_start_time = repeat_start_time + time_crap;
				repeat_end_time = repeat_end_time+ time_crap;

			} else { // 找到则不管
			       // System.out.println(" 本周内 ， 当天重复  --->" + new SimpleDateFormat("yyyy-MM-dd HH:mm" ).format(start_time));
			}
		}
		
		//设置时间随机值，避免同一时间的多个提醒无法弹出
		long gap = (long) (10000*Math.random());
//		System.out.println("  new gap ---> " + gap);
//		System.out.println("  start_time  ---->" + start_time);
//		System.out.println("  total   ---> " + (start_time+gap));
		
		try {
			//保留
			long id = DButil.getInstance(context).insertRemind(et_content.getText().toString(),contact_str, partner_str, start_time+gap, end_time, remind_type, Integer.valueOf(et_remind_num.getText().toString()),Integer.valueOf(et_remind_time.getText().toString()), repeat_type, Integer.valueOf(et_repeat_freq.getText().toString()), repeat_condition,repeat_start_time,repeat_end_time);
			System.out.println(" new remind_id ---> " + id);
			//保存成功,触发第一次提醒
			triggerFirstRemind(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * 触发第一次提醒
	 * 
	 * @param remind_id
	 */
	void triggerFirstRemind(long remind_id)
	{
		long before_time = 0 ;
		
		switch (remind_type) {
		case MyDatabaseUtil.REMIND_TYPE_MIN:
			before_time = Long.valueOf(et_remind_num.getText().toString()) * 60*1000;  //必须为长整型   Long.valueOf(et_remind_num.getText().toString())
			break;
			
		case MyDatabaseUtil.REMIND_TYPE_HOUR:
			before_time = Long.valueOf(et_remind_num.getText().toString()) * 60*60*1000;
			break;
			
		case MyDatabaseUtil.REMIND_TYPE_DAY:
			before_time = Long.valueOf(et_remind_num.getText().toString()) * 24 *60*60*1000;
			System.out.println("Long.valueOf(et_remind_num.getText().toString()) -->" +Long.valueOf(et_remind_num.getText().toString()));
			break;
			
		case MyDatabaseUtil.REMIND_TYPE_WEEK:
			before_time = Long.valueOf(et_remind_num.getText().toString()) * 7*24*60*60*1000;
			break;

		default:
			break;
		}
		
//		System.out.println(" start_time --->" + start_time);
//		System.out.println(" before_time --->" + before_time);
		
		long next_time = start_time - before_time;
		
		
		//保留
		Intent it = new Intent(context, AlarmReceiver.class);
		it.putExtra(MyDatabaseUtil.REMIND_ID, Integer.valueOf(String.valueOf(remind_id)));
		
		int r_id = Integer.valueOf(String.valueOf(remind_id));
		
		System.out.println(" next_time  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(next_time) + "  提醒id --->  " + r_id );
		
		PendingIntent pit = PendingIntent.getBroadcast(context, r_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager amr = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		amr.set(AlarmManager.RTC_WAKEUP, next_time ,pit);
		Toast.makeText(context, "添加提醒成功", Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 
	 * 编辑提醒后 保存
	 * 
	 */
	void update()
	{
		
        String repeat_condition = "0";
		if(repeat_type==MyDatabaseUtil.REPEAT_TYPE_WEEK)
		{
			repeat_condition = repeat_week_condition;
		}else if (repeat_type==MyDatabaseUtil.REPEAT_TYPE_MONTH)
		{
			repeat_condition = String.valueOf(repeat_month_condition);
		}
		
//		System.out.println("before ---  ----");
//		System.out.println(" start_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(start_time));
//		System.out.println(" end_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(end_time));
		
		if(cb_all_day.isChecked())
		{
			Date start_date = new Date(start_time);
			car.setTime(start_date);
			Date s_date = new Date(car.get(Calendar.YEAR)-1900, car.get(Calendar.MONTH),car.get(Calendar.DATE), 0, 0);
			start_time = s_date.getTime();
			
			Date end_date = new Date(end_time);
			car.setTime(end_date);
			Date e_date = new Date(car.get(Calendar.YEAR)-1900, car.get(Calendar.MONTH),car.get(Calendar.DATE), 0, 0);
			end_time = e_date.getTime();
			
//			System.out.println("after ---  ----");
//			System.out.println(" start_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(start_time));
//			System.out.println(" end_time ---> " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(end_time));
		}else {
			long time_gap = end_time - start_time;
			if(time_gap == 24*60*60*1000){
				end_time -=10000;
			}
		}
		
		
		//周重复判断
	    if (repeat_type == MyDatabaseUtil.REPEAT_TYPE_WEEK) {
				Calendar c = Calendar.getInstance();
				Date dd = new Date(start_time);
				c.setTime(dd);

				System.out.println(" 当前设定的事件开始时间  --->"+ new SimpleDateFormat("yyyy-MM-dd HH:mm").format(start_time));

				int cur_week_day = c.get(Calendar.DAY_OF_WEEK);
				if (cur_week_day == 1) {
					cur_week_day = 7;
				} else {
					cur_week_day = cur_week_day - 1;
				}

//				System.out.println(" cur_week_day ---> " + cur_week_day);
//				System.out.println("repeat_week_condition --->" + repeat_week_condition);
				

				String[] w_ss = repeat_week_condition.split(",");

				if (w_ss.length == 0) {
					w_ss = new String[] { repeat_week_condition };
				}
				int index = -1;

				for (int i = 0; i < w_ss.length; i++) {
					if (w_ss[i].equals(String.valueOf(cur_week_day))) {
						index = i;
						break;
					}
				}

//				System.out.println(" index --->" + index);

				if (index == -1) // 说明没找到
				{
					int jj = -1;
					for (int i = 0; i < w_ss.length; i++) {
						if (Integer.valueOf(w_ss[i]) > cur_week_day) {
							jj = Integer.valueOf(w_ss[i]);
							break;
						}
					}

					int time_crap = 0; //时间差
						
					if (jj == -1) // 直接跳到下次的重复开始周几去
					{
						int day_crap = 7 - cur_week_day + Integer.valueOf(w_ss[0]);
		                time_crap = day_crap * 24 * 60 * 60 * 1000;
						// System.out.println(" 下周的第一个重复  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm").format(start_time));
					} else {
						int day_crap = jj - cur_week_day; // 相差几天
						time_crap = day_crap * 24 * 60 * 60 * 1000;
						// System.out.println(" 本周内 ， 非今天重复  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm").format(start_time));
					}
						
					start_time = start_time + time_crap;
					end_time = end_time + time_crap;
					repeat_start_time = repeat_start_time + time_crap;
					repeat_end_time = repeat_end_time+ time_crap;

				} else { // 找到则不管
					     // System.out.println(" 本周内 ， 当天重复  --->" + new SimpleDateFormat("yyyy-MM-dd HH:mm" ).format(start_time));
				}
			}
	    
	  //设置时间随机值，避免同一时间的多个提醒无法弹出
	  	long gap = (long) (10000*Math.random());
		
		long upadte_id = DButil.getInstance(context).updateRemind(this.id, et_content.getText().toString(),contact_str, partner_str, start_time+gap, end_time, remind_type, Integer.valueOf(et_remind_num.getText().toString()),Integer.valueOf(et_remind_time.getText().toString()), repeat_type, Integer.valueOf(et_repeat_freq.getText().toString()), repeat_condition,repeat_start_time,repeat_end_time);
		
		
		long next_time = TimeTool.getNextTime(start_time+gap, remind_type, Integer.valueOf(et_remind_num.getText().toString()), repeat_type, repeat_condition, Integer.valueOf(et_repeat_freq.getText().toString()),repeat_start_time, repeat_end_time,"");
       
		//保留
        if(next_time!=-1)
        {
        	Intent it = new Intent(context, AlarmReceiver.class);
    		it.putExtra(MyDatabaseUtil.REMIND_ID, Integer.valueOf(String.valueOf(upadte_id)));		
    		PendingIntent pit = PendingIntent.getBroadcast(context, (int)upadte_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
    		AlarmManager amr = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
    		amr.cancel(pit);//先取消 ？
    		amr.set(AlarmManager.RTC_WAKEUP, next_time ,pit);
    		
        }else{ //取消提醒
        	Intent it = new Intent(context, AlarmReceiver.class);
    		it.putExtra(MyDatabaseUtil.REMIND_ID, upadte_id);		
    		PendingIntent pit = PendingIntent.getBroadcast(context, (int)upadte_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
    		AlarmManager amr = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
    		amr.cancel(pit);
        }
	}
	
	/**
	 * 
	 * 检查
	 * @return
	 */
	public boolean check()
	{
			
		if(et_content.getText().toString().length()==0)
		{
			Toast.makeText(context, "提醒内容不能为空", 	Toast.LENGTH_SHORT).show();
			return false;
		}
//			
		if(contact_str.equals(""))
		{
			Toast.makeText(context, "请选择联系人", Toast.LENGTH_SHORT).show();
			return false;
		}
			
		if(start_time==-1)
		{
			Toast.makeText(context, "请选择开始时间", Toast.LENGTH_SHORT).show();
			return false;
		}
			
		if(end_time==-1)
		{
			Toast.makeText(context, "请选择结束时间", Toast.LENGTH_SHORT).show();
			return false;
		}
			
		if(id==-1 && start_time < System.currentTimeMillis())
		{
			Toast.makeText(context, "开始时间必须大于当前系统时间", Toast.LENGTH_SHORT).show();
			return false;
		}
			
		if(end_time<=start_time)
		{
			Toast.makeText(context, "结束时间必须大于开始时间", Toast.LENGTH_SHORT).show();
			return false;
		}
			
			
		if(et_remind_num.getText().toString().equals("") || et_remind_num.getText().toString().equals("0"))
		{
			Toast.makeText(context, "请输入大于0的提醒数值", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			try {
				int  i = Integer.valueOf(et_remind_num.getText().toString());
				if(i==0)
				{
					Toast.makeText(context, "请输入大于0的提醒数值", Toast.LENGTH_SHORT).show();
					return false;
				}
					
			} catch (Exception e) {
					Toast.makeText(context, "请输入正确的提醒数值", Toast.LENGTH_SHORT).show();
					return false;
			}
		}
			
		if(et_remind_time.getText().toString().equals("") || et_remind_time.getText().toString().equals("0"))
		{
			Toast.makeText(context, "请输入大于0的提醒次数", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			try {
				int  i = Integer.valueOf(et_remind_time.getText().toString());
					
				if(i==0)
				{
					Toast.makeText(context, "请输入大于0的提醒次数", Toast.LENGTH_SHORT).show();
					return false;
				}
					
			} catch (Exception e) {
					Toast.makeText(context, "请输入正确的提醒次数", Toast.LENGTH_SHORT).show();
					return false;
			}
		}
			
			if(repeat_type != MyDatabaseUtil.REPEAT_TYPE_ONE && (et_repeat_freq.getText().toString().equals("") || et_repeat_freq.getText().toString().equals("0")))
			{
				Toast.makeText(context, "请输入大于0的重复频率", Toast.LENGTH_SHORT).show();
				return false;
			}else{
				try {
					int i = Integer.valueOf(et_repeat_freq.getText().toString());
					if(i == 0)
					{
						Toast.makeText(context, "请输入大于0的重复频率", Toast.LENGTH_SHORT).show();
						return false;
					}
				} catch (Exception e) {
					Toast.makeText(context, "请输入正确的重复频率", Toast.LENGTH_SHORT).show();
					return false;
				}
			}
			
			if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_WEEK && repeat_week_condition.equals(""))
			{
				Toast.makeText(context, "请选择重复时间", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			//自动校正重复开始时间
			if( repeat_type != MyDatabaseUtil.REPEAT_TYPE_ONE &&  repeat_start_time<start_time)
			{
				Date d = new Date(start_time);
				car.setTime(d);
				int y = car.get(Calendar.YEAR);
				int m = car.get(Calendar.MONTH)+1;
				int da = car.get(Calendar.DATE);
				
				
				btn_repeat_start_time.setText(y+"/"+m+"/"+da);
				repeat_start_time = start_time;
				
				Toast.makeText(context, "重复开始时间不得小于事件开始时间,已修正", Toast.LENGTH_SHORT).show();
				return false;
			}
			
			
			//自动校正重复结束时间
			long t = 0;
			String s = (btn_start_time.getText().toString().split(" "))[0];
			String [] ss = s.split("/");
			int year = Integer.valueOf(ss[0]);
			int month = Integer.valueOf(ss[1]);
			int day = Integer.valueOf(ss[2]);
			switch (repeat_type) {
			case 1: //天
				t = 24*60*60*1000;
				if ( repeat_end_time<start_time+t*Long.valueOf(et_repeat_freq.getText().toString()))
				{
					Date d = new Date(start_time+t*Long.valueOf(et_repeat_freq.getText().toString()));
					
					car.setTime(d);
					int y = car.get(Calendar.YEAR);
					int m = car.get(Calendar.MONTH)+1;
					int da = car.get(Calendar.DATE);
			
//
//					car.clear();
//					car.set(y, m, da, 23, 59, 59);
					
					btn_repeat_end_time.setText(y+"/"+m+"/"+da);
					repeat_end_time =  start_time+t*Long.valueOf(et_repeat_freq.getText().toString());
					
					Toast.makeText(context, "重复结束时间不得小于最小重复时间,已修正", Toast.LENGTH_SHORT).show();
					return false;
				}
				break;
				
			case 2://周
				t = 24*60*60*1000*7;
				if ( repeat_end_time < start_time+t*Long.valueOf(et_repeat_freq.getText().toString()))
				{
					Date d = new Date(start_time+t*Long.valueOf(et_repeat_freq.getText().toString()));
					
					car.setTime(d);
					int y = car.get(Calendar.YEAR);
					int m = car.get(Calendar.MONTH)+1;
					int da = car.get(Calendar.DATE);
			
					btn_repeat_end_time.setText(y+"/"+m+"/"+da);
					repeat_end_time =  start_time + t*Long.valueOf(et_repeat_freq.getText().toString());
					
					Toast.makeText(context, "重复结束时间不得小于最小重复时间,已修正", Toast.LENGTH_SHORT).show();
					return false;
				}
				break;
				
			case 3://月
//				t = 24*60*60*1000*30;
				
				System.out.println("btn_start_time ---> "+year+"/"+month+"/"+day);
				System.out.println("et_repeat_freq.getText().toString() ---> " + et_repeat_freq.getText().toString());
				
				int new_year;
				int new_month;
				if(month+Integer.valueOf(et_repeat_freq.getText().toString())>12)
				{
					new_year = year+(month+Integer.valueOf(et_repeat_freq.getText().toString()))/12;
					new_month = (month+Integer.valueOf(et_repeat_freq.getText().toString()))%12+1;
					
				}else{
					new_year = year;
					new_month = month+Integer.valueOf(et_repeat_freq.getText().toString());
				}
				
				System.out.println("new_year  --->"+new_year+"/"+new_month+"/"+day);
				
				Date date = new Date(Integer.valueOf(new_year)/1900, Integer.valueOf(new_month)-1, Integer.valueOf(day), Integer.valueOf(23), Integer.valueOf(59));
				
				if(repeat_end_time<date.getTime())
				{
					repeat_end_time = date.getTime();
					btn_repeat_end_time.setText(new_year+"/"+new_month+"/"+day);
					Toast.makeText(context, "重复结束时间不得小于最小重复时间,已修正", Toast.LENGTH_SHORT).show();
					return false;
				}
				
				break;
				
			case 4://年
				Date date_year = new Date(Integer.valueOf(year+Integer.valueOf(et_repeat_freq.getText().toString()))-1900, Integer.valueOf(month)-1, Integer.valueOf(day), Integer.valueOf(23), Integer.valueOf(59));
				if(repeat_end_time<date_year.getTime())
				{
					repeat_end_time = date_year.getTime();
					btn_repeat_end_time.setText(year+Integer.valueOf(et_repeat_freq.getText().toString())+"/"+month+"/"+day);
					Toast.makeText(context, "重复结束时间不得小于最小重复时间,已修正", Toast.LENGTH_SHORT).show();
					return false;
				}
				break;

			default:
				break;
			}
			
			return true;
		}
	
	
	void showContactDilaog()
	{
 	      if(contact_dialog==null)
 	      {
 	    	 contact_dialog = new Dialog(context,R.style.theme_myDialog);
 	    	 View v = LayoutInflater.from(context).inflate(R.layout.remind_contact_dialog, null);
 	    	 contact_dialog.setCanceledOnTouchOutside(true);
 	    	 contact_dialog.setContentView(v);
 	    	 lv_contact = (ListView)v.findViewById(R.id.lv_contact);
// 	    	 lv_contact.setEmptyView(LayoutInflater.from(mainActivity).inflate(R.layout.empty_view, null));
 	    	 
// 	    	sideBar = (SideBar) contact_dialog.findViewById(R.id.sideBar);
// 	    	sideBar.setListView(lv_contact);
 	    	 
 	    	 Button btn_pick_contact_ok = (Button) v.findViewById(R.id.btn_pick_contact_ok);
 	    	 btn_pick_contact_ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(remindContactsAdapter!=null && remindContactsAdapter.cb!=null )
					{
						String tag = (String) remindContactsAdapter.cb.getTag();
						String [] ss = tag.split(":");
						
						Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + ss[0].replace("#", ""), null, null);
						String pp = " ";
						StringBuffer sb_phones = new StringBuffer();
						
						while (phones.moveToNext()) { //其他的号码
							String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
							sb_phones.append(ph+",");
						}
				        phones.close();
						if(sb_phones.toString().length()>0)
						{
							pp= sb_phones.substring(0, sb_phones.length()-1);
						}
						
						c_id = Integer.valueOf(ss[0].replace("#", ""));
						contact_str = tag+":"+pp;
						btn_pick_contact.setText(ss[1]);
					}
					
					contact_dialog.dismiss();
				}
				
			});
 	    	 
 	    	Button btn_pick_contact_cancle = (Button) v.findViewById(R.id.btn_pick_contact_cancle);
 	    	btn_pick_contact_cancle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					contact_dialog.dismiss();
				}
			});
 	    	
 	       if(contacts==null && !isQuerying)
 	       {
 	    	  Toast.makeText(context, "正在载入联系人", Toast.LENGTH_SHORT).show();
 	 	       new Thread(new Runnable() {
 	 				
 	 				@Override
 	 				public void run() {
 	 					contacts = new ArrayList<ContactBean>() ;
 						String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
 						Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
 						
 						int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
 						int photot_id_column = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
 						int nick_column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
 						int has_phone_column = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
 						int sort_key_column = cursor.getColumnIndex("sort_key");
 						
 						while(cursor.moveToNext())
 						{
 							
 							int phone_column = cursor.getInt(has_phone_column);
 							
 							String sort_key = cursor.getString(sort_key_column);
 							
 							if(phone_column>0 && sort_key!=null && !sort_key.equals(""))
 							{
 								ContactBean contactBean = new ContactBean(); ;
 	 							contactBean.setContact_id(cursor.getLong(id_column));
 	 							contactBean.setPhoto_id(cursor.getString(photot_id_column));
 	 							contactBean.setNick(cursor.getString(nick_column));
 	 							
 	 							 boolean b = false;
 	 							 String capPingYin = "";
 	 							 
 	 							String key = cursor.getString(cursor.getColumnIndex("sort_key")).replace(" ", "");
 	 							 
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
 	    		  remindContactsAdapter = new RemindContactsAdapter(context, contacts);
 				  lv_contact.setAdapter(remindContactsAdapter);
 	    	   }else{
 	    		  Toast.makeText(context, "正在载入联系人", Toast.LENGTH_SHORT).show();
 	    	   }
 	       }
 	     }
 	     contact_dialog.show();
 	     
	}
	
	
	void showPartnerDialog()
	{
		if(partake_dialog==null)
		{
			partake_dialog = new Dialog(context,R.style.theme_myDialog);
			partake_dialog.setCanceledOnTouchOutside(true);
			partake_dialog.setContentView(R.layout.remind_partake);
			
			final Button btn_all_contact = (Button) partake_dialog.findViewById(R.id.btn_all_contact);
			final Button btn_group = (Button) partake_dialog.findViewById(R.id.btn_group);
			Button btn_partake_yes = (Button) partake_dialog.findViewById(R.id.btn_partake_yes);
			Button btn_partake_cancel = (Button) partake_dialog.findViewById(R.id.btn_partake_cancel);
			
			contact_ln = (LinearLayout) partake_dialog.findViewById(R.id.contact_ln);
			
			lv_partake_contact = (ListView) partake_dialog.findViewById(R.id.lv_partake_contact);
			group_expandableListView = (ExpandableListView) partake_dialog.findViewById(R.id.group_expandableListView);
			
			
			if(contacts==null && !isQuerying)
	 	       {
	 	    	  Toast.makeText(context, "正在载入联系人", Toast.LENGTH_SHORT).show();
	 	    	  
	 	 	       new Thread(new Runnable() {
	 	 				
	 	 				@Override
	 	 				public void run() {
	 	 					contacts = new ArrayList<ContactBean>() ;
	 						String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
	 						Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
	 						System.out.println(cursor.getCount() + "===========================contact=count");
	 						
	 						int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
	 						int photoColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
	 						int nickColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	 						int sortKeyColumn = cursor.getColumnIndex("sort_key");
	 						
	 						while(cursor.moveToNext())
	 						{
	 							
	 							String key = cursor.getString(sortKeyColumn);
	 							
	 							if(key!=null) //过滤没有名字的联系人
	 							{
	 								ContactBean contactBean = new ContactBean(); ;
		 							contactBean.setContact_id(cursor.getLong(idColumn));
		 							contactBean.setPhoto_id(cursor.getString(photoColumn));
		 							contactBean.setNick(cursor.getString(nickColumn));
		 							
		 							
		 							contacts.add(contactBean);
	 							}
	 							
	 						}
	 						cursor.close();
	 						
	 						handler.sendEmptyMessage(1);
	 	 				}
	 	 			}).start();
	 	 	       isQuerying = true;
	 	 	       
	 	       }else{
	 	    	   if(contacts!=null)
	 	    	   {
	 	    		  remindContactsMutilAdapter = new RemindContactsMutilAdapter(context, contacts);
	 	 			  lv_partake_contact.setAdapter(remindContactsMutilAdapter);
	 	 			  
	 	    	   }else{
	 	    		  Toast.makeText(context, "正在载入联系人", Toast.LENGTH_SHORT).show();
	 	    	   }
	 	       }
			
			btn_all_contact.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					contact_ln.setVisibility(View.VISIBLE);
					group_expandableListView.setVisibility(View.GONE);
					
					btn_all_contact.setBackgroundResource(R.drawable.remind_partake_select);
					btn_all_contact.setTextColor(Color.WHITE);
					
					btn_group.setBackgroundResource(R.drawable.remind_partake_normal);
					btn_group.setTextColor(context.getResources().getColor(R.color.text_color_base));
					
					partner_type = 0;
				}
			});
			
			
			btn_group.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(aGroupInfos==null)
					{
						Toast.makeText(context, "正在载入分组", Toast.LENGTH_SHORT).show();
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								long start = System.currentTimeMillis();
								
								aGroupInfos = getContactGroup();
								
								//查全部联系人
								String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
								
								ContentResolver contentResolver =  context.getContentResolver();
								Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
								
								
								int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
								int photo_column = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
								int nick_column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
								int sort_key_column = cursor.getColumnIndex("sort_key");
								int has_phone_column = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
								
								List<GroupInfo> all_group_infos = new ArrayList<GroupInfo>();
								
								while(cursor.moveToNext())
								{
									
									int phone_count  = cursor.getInt(has_phone_column);
									String sort_key = cursor.getString(sort_key_column);
									
									if(phone_count>0 && sort_key!=null && !sort_key.equals(""))
									{
										long contact_id = cursor.getLong(id_column);
										
										GroupInfo groupInfo = new GroupInfo();
										
										
										 //查询联系人的分组信息
										 String[] groups= new String[]{GroupMembership.GROUP_ROW_ID};
										 String where=GroupMembership.CONTACT_ID+"="+ contact_id +" AND " +Data.MIMETYPE + "=" + " '" + GroupMembership.CONTENT_ITEM_TYPE+"'";
										 Cursor groupCursor=contentResolver.query(Data.CONTENT_URI, groups, where, null, null);
										   
										 if(groupCursor.moveToNext()){
										  long gourp_id=groupCursor.getLong(groupCursor.getColumnIndex(GroupMembership.GROUP_ROW_ID));
										  groupInfo.setGroup_id(gourp_id);
										 }else{
										  groupInfo.setGroup_id(-1);
										 }
										 
										 groupInfo.setPerson_id(String.valueOf(contact_id));
										 groupInfo.setPhone_name(cursor.getString(nick_column));
										 groupInfo.setPhoto_id(cursor.getString(photo_column));
										 
										 groupCursor.close();
										 
										 all_group_infos.add(groupInfo);
									}
								}
								cursor.close();
				        		
								for(GroupInfo parent_g:aGroupInfos)
								{
									long g_id = parent_g.getGroup_id();
									
									ArrayList<GroupInfo> childs = new ArrayList<GroupInfo>();
									for(GroupInfo child_g:all_group_infos)
									{
										if(g_id==child_g.getGroup_id())
										{
											childs.add(child_g);
										}
									}
									childInfos.add(childs);
								}
								
								long end = System.currentTimeMillis();
								
								System.out.println(" 载入分组完成 ---> 耗时 :" + (end - start));
								
				        		handler.sendEmptyMessage(2);
							}
						}).start();
					}
					
					btn_all_contact.setBackgroundResource(R.drawable.remind_partake_normal);
					btn_all_contact.setTextColor(context.getResources().getColor(R.color.text_color_base));
					
					btn_group.setBackgroundResource(R.drawable.remind_partake_select);
					btn_group.setTextColor(Color.WHITE);
					
					contact_ln.setVisibility(View.GONE);
					group_expandableListView.setVisibility(View.VISIBLE);
//					
					partner_type = 1;
					
				}
			});
			
			
			btn_partake_yes.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(partner_type==0 && remindContactsMutilAdapter!=null)
					{
						int [] selecteds =  remindContactsMutilAdapter.getSelectedItemIndexes();
						
						if(selecteds.length>0)
						{
							StringBuffer sb_ids = new StringBuffer();
							StringBuffer sb_name = new StringBuffer();
							
							for(int i=0;i<selecteds.length;i++)
							{
								ContactBean c = contacts.get(selecteds[i]);
								
								Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + c.getContact_id(), null, null);
								String pp = " ";
								StringBuffer sb_phones = new StringBuffer();
								while (phones.moveToNext()) { //其他的号码
									String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
									sb_phones.append(ph+",");
								}
						        phones.close();
								if(sb_phones.toString().length()>0)
								{
									pp= sb_phones.substring(0, sb_phones.length()-1);
								}
								System.out.println("pp --->" + pp);
								sb_ids.append("#"+c.getContact_id()+"#:"+c.getNick()+":"+pp+";");
								sb_name.append(c.getNick()+";");
							}
							
							
							partner_str = sb_ids.substring(0, sb_ids.length()-1);
							partner_names = sb_name.substring(0, sb_name.length()-1);
							
							System.out.println("  partner_ids  -->" + partner_str);
							
							btn_pick_partner.setText(partner_names);
						}else{
							partner_str = "";
							partner_names = "";
							
							btn_pick_partner.setText("请选择参与人");
						}
						
					}else{
						
						if(smsExpandableListAdapter!=null)
						{
							List<GroupInfo> selected_info = smsExpandableListAdapter.getSelected_info();
							
							if(selected_info.size()>0)
							{
								
								StringBuffer sb_ids = new StringBuffer();
								StringBuffer sb_name = new StringBuffer();
								
								for(int i=0;i<selected_info.size();i++)
								{
									GroupInfo g = selected_info.get(i);
									
									Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + g.getPerson_id(), null, null);
									String pp = " ";
									StringBuffer sb_phones = new StringBuffer();
									while (phones.moveToNext()) { //其他的号码
										String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
										sb_phones.append(ph+",");
									}
							        phones.close();
									if(sb_phones.toString().length()>0)
									{
										pp= sb_phones.substring(0, sb_phones.length()-1);
									}
									System.out.println("pp --->" + pp);
									
									sb_ids.append("#"+g.getPerson_id()+"#:"+g.getPhone_name()+":"+pp+";");
									sb_name.append(g.getPhone_name()+";");
								}
								
								partner_str = sb_ids.substring(0, sb_ids.length()-1);
								partner_names = sb_name.substring(0, sb_name.length()-1);
								
								System.out.println("  partner_ids  -->" + partner_str);
								
								btn_pick_partner.setText(partner_names);
								
							}else{
								partner_str = "";
								partner_names = "";
								
								btn_pick_partner.setText("请选择参与人");
							}
						}
						
					}
					
					partake_dialog.cancel();
				}
			});
			
			
			btn_partake_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					partake_dialog.cancel();
				}
			});
		}
		
		partake_dialog.show();
		
	}
	
	
	private OnCheckedChangeListener onChecked = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			switch (group.getId()) {
			
				case R.id.remind_type_rg:
					
					RadioButton rb1 = (RadioButton) group.findViewById(checkedId);
					
					btn_remind_type.setText(rb1.getText());
					
				switch (checkedId) {
				case R.id.remind_type_min:
					remind_type = MyDatabaseUtil.REMIND_TYPE_MIN;
					break;
				case R.id.remind_type_hour:
					remind_type = MyDatabaseUtil.REMIND_TYPE_HOUR;
					break;
				case R.id.remind_type_day:
					remind_type = MyDatabaseUtil.REMIND_TYPE_DAY;
					break;
				case R.id.remind_type_week:
					remind_type = MyDatabaseUtil.REMIND_TYPE_WEEK;
					break;
				default:
					break;
				}
				
				System.out.println(" remind_type --->" + remind_type);
				choose_remind_type.dismiss();
					break;
					
					
				case R.id.repeat_type_rg:
					
					RadioButton rb2 = (RadioButton) group.findViewById(checkedId);
					
					String str = rb2.getText().toString();
					
					btn_repeat_type.setText(str);
					
					if(rb2.getId() == R.id.day){
						
						repeat_type = MyDatabaseUtil.REPEAT_TYPE_DAY;
						
						repeat_rate.setVisibility(View.VISIBLE);
						repeat_rate_type.setText("天");
						repeat_time.setVisibility(View.GONE);
						
						ln_repeat_time_range.setVisibility(View.VISIBLE);
						
					}else if(rb2.getId() == R.id.week){
						
						repeat_type = MyDatabaseUtil.REPEAT_TYPE_WEEK;
						
						repeat_rate.setVisibility(View.VISIBLE);
						repeat_rate_type.setText("周");
						repeat_time.setVisibility(View.VISIBLE);
						ln_repeat_time_week.setVisibility(View.VISIBLE);
						ln_repeat_time_month.setVisibility(View.GONE);
						ln_repeat_time_range.setVisibility(View.VISIBLE);
						
					}else if(rb2.getId() == R.id.month){
						
						repeat_type = MyDatabaseUtil.REPEAT_TYPE_MONTH;
						
						repeat_rate.setVisibility(View.VISIBLE);
						repeat_rate_type.setText("月");
						repeat_time.setVisibility(View.VISIBLE);
						ln_repeat_time_month.setVisibility(View.VISIBLE);
						ln_repeat_time_week.setVisibility(View.GONE);
						
						ln_repeat_time_range.setVisibility(View.VISIBLE);
						
						//刷新
						Calendar car = Calendar.getInstance();
						Date date = new Date(start_time);
						car.setTime(date);
						int day = car.get(Calendar.DATE);
						b_day_in_month.setText("每月 的 第"+day+"天");
						
				        int week_of_month = car.get(Calendar.WEEK_OF_MONTH);
				        int day_of_week = car.get(Calendar.DAY_OF_WEEK);
				        
				        String dd ="";
				        
				        switch (day_of_week) {
						case 1:
							dd="日";
							break;
		                case 2:
		                	dd="一";
							break;
							
		                case 3:
		                	dd="二";
		                    break;
		                case 4:
		                	dd="三";
		                    break;
		                case 5:
		                	dd="四";
		                    break;

		                case 6:
		                	dd="五";
		                    break;
		                case 7:
		                	dd="六";
		                    break;
						default:
							break;
						}
				        b_day_in_week_in_month.setText("每月 的 第"+week_of_month+"个星期的周"+dd);
						
					}else if(rb2.getId() == R.id.year){
						
						repeat_type = MyDatabaseUtil.REPEAT_TYPE_YEAR;
						
						repeat_rate.setVisibility(View.VISIBLE);
						repeat_rate_type.setText("年");
						repeat_time.setVisibility(View.GONE);
						
						ln_repeat_time_range.setVisibility(View.VISIBLE);
					}else{
						
						repeat_type = MyDatabaseUtil.REPEAT_TYPE_ONE;
						
						repeat_rate.setVisibility(View.GONE);
						repeat_time.setVisibility(View.GONE);
						ln_repeat_time_range.setVisibility(View.GONE);
					}
					
					choose_repeat_type.dismiss();
					System.out.println(" repeat_type --->" + repeat_type);
					
					break;
					
				default:
					break;
			}
		}
	};
	
	
	class MeCheckBoxOnCheckChangeListener implements android.widget.CompoundButton.OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(isChecked)
			{
				SharedPreferences ss = context.getSharedPreferences("myNumberContactId", 0);
				long contactId = ss.getLong("myContactId", -1);
				
				Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId, null, null);
				String pp = " ";
				StringBuffer sb_phones = new StringBuffer();
				while (phones.moveToNext()) { 
					String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
					sb_phones.append(ph+",");
				}
		        phones.close();
				
				contact_str = "#"+String.valueOf(contactId)+"#:"+"我:"+pp;
				btn_pick_contact.setText("我");
			}else{
				contact_str = "";
				btn_pick_contact.setText("选择联系人");
			}
		}
	}
	
	
	//全天 checkbox
	class AllDayCheckBoxOnCheckChangeListener implements android.widget.CompoundButton.OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if(isChecked)
			{
				String [] ss = btn_start_time.getText().toString().split(" ");
				btn_start_time.setText(ss[0]);
				
				String [] sss = btn_end_time.getText().toString().split(" ");
				btn_end_time.setText(sss[0]);
				
			}else{
				if(start_time!=-1)
				{
					Date data = new Date(start_time);
					btn_start_time.setText((data.getYear()+1900)+"/"+(data.getMonth()+1)+"/"+data.getDate()+" "+data.getHours()+":"+data.getMinutes());
				}
				
				if(end_time!=-1)
				{
					Date end_data = new Date(end_time);
					btn_end_time.setText((end_data.getYear()+1900)+"/"+(end_data.getMonth()+1)+"/"+end_data.getDate()+" "+end_data.getHours()+":"+end_data.getMinutes());
				}
			}
		}
	}
	
	
	class WeekOnCheckChangeListener implements android.widget.CompoundButton.OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			 
			StringBuffer sb = new StringBuffer();

			if (cb_monday.isChecked()) {
				sb.append("1,");
			}

			if (cb_tuesday.isChecked()) {
				sb.append("2,");
			}

			if (cb_wednesday.isChecked()) {
				sb.append("3,");
			}

			if (cb_thursday.isChecked()) {
				sb.append("4,");
			}

			if (cb_friday.isChecked()) {
				sb.append("5,");
			}

			if (cb_saturday.isChecked()) {
				sb.append("6,");
			}

			if (cb_sunday.isChecked()) {
				sb.append("7,");
			}
			   
			if (sb.toString().equals("")) {  //一个都没选
				repeat_week_condition="";
		    	
			} else {
				repeat_week_condition = sb.substring(0, sb.length() - 1);
			}

//			 System.out.println(" repeat_week_condition ---> " + repeat_week_condition);
		}
	}
	
	
	
	class PickDateClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonsure:
				
//				System.out.println( " wheelMain.getTimemil()  ---> " + wheelMain.getTimemilInFormat());
				
				String time = wheelMain.getTimemilInFormat();
				String [] tt = time.split("/");
				String year =tt[0];
				String month = tt[1];
				String day = tt[2];
				String hour = tt[3];
				String minute = tt[4];
				
				String hourStr = hour;
				if(Integer.valueOf(hour)<=9)
				{
					hourStr="0"+hour;
				}
				
				String minuteStr = minute;
				if(Integer.valueOf(minute)<=9)
				{
					minuteStr="0"+minute;
				}
				
				System.out.println("date: " + year +"-"+ month +"-"+day+" "+hour+":"+minute);
				
				Date date = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minute));
//				System.out.println(" date.getTime();  ----> " + date.getTime());
				
				
				switch (pick_time_type) {
				case 0:
					start_time =  date.getTime();
					if(cb_all_day.isChecked())
					{
						btn_start_time.setText(year +"/"+ month +"/"+day);
					}else{
						btn_start_time.setText(year +"/"+ month +"/"+day+" "+hourStr+":"+minuteStr);
					}
					break;
					
				case 1:
					end_time =  date.getTime();
					if(cb_all_day.isChecked())
					{
						btn_end_time.setText(year +"/"+ month +"/"+day);
					}else{
						btn_end_time.setText(year +"/"+ month +"/"+day+" "+hourStr+":"+minuteStr);
					}
					break;
					
				default:
					break;
				}
				
				
				if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_MONTH)
				{
					//刷新
					Calendar car = Calendar.getInstance();
					car.setTime(date);
					int d = car.get(Calendar.DATE);
					b_day_in_month.setText("每月 的 第"+d+"天");
					
			        int week_of_month = car.get(Calendar.WEEK_OF_MONTH);
			        int day_of_week = car.get(Calendar.DAY_OF_WEEK);
			        
			        String dd ="";
			        
			        switch (day_of_week) {
					case 1:
						dd="日";
						break;
	                case 2:
	                	dd="一";
						break;
						
	                case 3:
	                	dd="二";
	                    break;
	                case 4:
	                	dd="三";
	                    break;
	                case 5:
	                	dd="四";
	                    break;

	                case 6:
	                	dd="五";
	                    break;
	                case 7:
	                	dd="六";
	                    break;
					default:
						break;
					}
			        b_day_in_week_in_month.setText("每月 的 第"+week_of_month+"个星期的周"+dd);
				}
				
				pic_date_dialog.dismiss();
				
				break;
				
            case R.id.buttoncancle:
            	
            	pic_date_dialog.dismiss();
            	
				break;

			default:
				break;
			}
		}
	}
	
	
	class RepeatRangeClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonsure:
				
				System.out.println( " wheelMain.getTimemil()  ---> " + wheelMainRepeatRange.getTimemilInFormat());
				
				String time = wheelMainRepeatRange.getTimemilInFormat();
				String [] tt = time.split("/");
				String year =tt[0];
				String month = tt[1];
				String day = tt[2];
				
				
				System.out.println("date: " + year +"-"+ month +"-"+day);
				
				Date date = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day), Integer.valueOf(23), Integer.valueOf(59));
				System.out.println(" date.getTime();  ----> " + date.getTime());
				
				switch (repeat_range_type) {
					
				case 0:		
					repeat_start_time =  date.getTime();
					btn_repeat_start_time.setText(year +"/"+ month +"/"+day);

				
					break;
					
				case 1:
					
					repeat_end_time =  date.getTime();
					btn_repeat_end_time.setText(year +"/"+ month +"/"+day);
					break;

				default:
					break;
				}
				
				pic_repeat_range_dialog.dismiss();
				
				break;
				
            case R.id.buttoncancle:
            	
            	pic_repeat_range_dialog.dismiss();
            	
				break;

			default:
				break;
			}
		}
	}
	
	
	public ArrayList<GroupInfo> getContactGroup()
	{
		// 我们要得到分组的id 分组的名字
		String[] RAW_PROJECTION = new String[] { ContactsContract.Groups._ID,ContactsContract.Groups.TITLE, };
	    // 查询条件是Groups.DELETED=0
		String RAW_CONTACTS_WHERE = ContactsContract.Groups.DELETED + " = ? ";
		// 条用内容提供者查询 new String[] { "" + 0 } 是给Groups.DELETED赋值
		Cursor cursor = context.getContentResolver().
		        		query( ContactsContract.Groups.CONTENT_URI, RAW_PROJECTION, RAW_CONTACTS_WHERE, new String[] { "" + 0 }, null);
		 
		// 存放分组信息
		ArrayList<GroupInfo> islist = new ArrayList<GroupInfo>();
		// 默认放一个未分组
		GroupInfo ginfo = new GroupInfo();
		ginfo.setGroup_name("未分组");
		ginfo.setGroup_id((-1));
		islist.add(ginfo);
		GroupInfo groupInfo = null;
		        
		while (cursor.moveToNext()) {

			// 分组的实体类
			groupInfo = new GroupInfo();
			groupInfo.setGroup_id(cursor.getInt(cursor.getColumnIndex("_id")));
			groupInfo.setGroup_name(cursor.getString(cursor.getColumnIndex("title")));

			// 把分组放到集合里去
			islist.add(groupInfo);
		}
		 
		cursor.close();
		return islist;
	}
	
	
	private String getTimeStrYYMMDDHHMM(long time)
	{
		Calendar car = Calendar.getInstance();
		Date date = new Date(time);
		car.setTime(date);
		int year = car.get(Calendar.YEAR);
		int month = car.get(Calendar.MONTH)+1;
		int day = car.get(Calendar.DATE);
		int hour = car.get(Calendar.HOUR_OF_DAY);
		int min = car.get(Calendar.MINUTE);
		String hourStr = String.valueOf(hour);
		if(Integer.valueOf(hour)<=9)
		{
			hourStr="0"+hour;
		}
		String minuteStr = String.valueOf(min);
		if(Integer.valueOf(min)<=9)
		{
			minuteStr="0"+min;
		}
		return year+"/"+month+"/"+day+" "+hourStr+":"+minuteStr;
	}
	
	
	private String getTimeStrYYMMDD(long time)
	{
		Calendar car = Calendar.getInstance();
		Date date = new Date(time);
		car.setTime(date);
		int year = car.get(Calendar.YEAR);
		int month = car.get(Calendar.MONTH)+1;
		int day = car.get(Calendar.DATE);
		return year+"/"+month+"/"+day;
	}
}
