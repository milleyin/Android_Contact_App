package com.dongji.app.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.Data;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dongji.app.adapter.ContactDetailRemindAdapter;
import com.dongji.app.adapter.PartnerDetailAdapter;
import com.dongji.app.adapter.PhoneDetailAdapter;
import com.dongji.app.adapter.PickWeekAdapter;
import com.dongji.app.adapter.RemindWeekAdapter;
import com.dongji.app.addressbook.AddEditRmindLayout.OnFinishEditRemindListener;
import com.dongji.app.entity.RemindBean;
import com.dongji.app.entity.RemindWeekBean;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.ui.ScrollLayout;
import com.dongji.app.ui.ScrollLayoutRemind;
import com.dongji.app.ui.ScrollLayoutRemind.OnScrollerFinish;


/**
 * 
 * 设置里的  提醒设置
 * 
 * @author Administrator
 *
 */
public class RemindsActivity extends Activity implements OnClickListener {
	
	ImageView change_view ; //切换视图
	Button add_remind ; // 新建提醒
	
	//周模式 (默认显示)
	LinearLayout week_layout;
	ScrollLayoutRemind week_scroller;
	LinearLayout btn_pre_week;
	LinearLayout btn_next_week;
	TextView tv_week_title;
	
	Dialog week_show_remind;
	
	
	//列表模式
	LinearLayout list_layout; 
	LinearLayout add_ln;
	LinearLayout contact_data;
	TextView data_title;
	TextView data_vocation;
	ListView phone_list;
	ListView remind_info;
	
	LinearLayout tips_top_layout;
	TextView tv_top_tips;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	
	
	int change = 0; // 0 按周显示 ， 1列表显示
	
	List<RemindBean> contactList = new ArrayList<RemindBean>();
	List<RemindBean> list = new ArrayList<RemindBean>();
	
	Button showAdapterButton;
	
	ContactDetailRemindAdapter contactDetailRemindAdapter;
	
	Dialog pop_partner; //参与人详情
	
	String contact_id = "";
	
	public int selected_position  = -1; //被选中的位置,  -1表示未选中
	
	int week_gap = 0; // 0 表示本周， 正表示： 上几周   负表示： 下几周
	int cur_contact_id; //当前被选中的联系人
	
	
	ListView lv_cur_week_remind;
	
	Button btn_back_to_this_week; //返回本周
	
	long cur_week_start_time;
	long cur_week_end_time;
	Dialog pop_pick_week;
	TextView tv_month_title;
	ListView lv_month_week;
	
	List<Long> times;
	
	ScrollLayout month_scroller;
	
	
	int selected_remind_id;
	int selected_repeat_type;
	long delete_remind_temp_start_time;
	Dialog dialog_delete_remind;

	
	Button btn_one;
	Button btn_all;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting_item_11_remind_list);
        
		change_view = (ImageView)findViewById(R.id.change_view);
		change_view.setOnClickListener(this);
		
		add_remind = (Button) findViewById(R.id.add_remind);
		add_remind.setOnClickListener(this);
		
		week_layout =(LinearLayout) findViewById(R.id.week_layout);
		week_scroller = (ScrollLayoutRemind)  findViewById(R.id.week_scroller);
		week_scroller.setRemindLayout(this);
		btn_pre_week = (LinearLayout) findViewById(R.id.btn_pre_week); 
		btn_next_week = (LinearLayout) findViewById(R.id.btn_next_week);
		tv_week_title = (TextView) findViewById(R.id.tv_week_title);
		btn_back_to_this_week = (Button)  findViewById(R.id.btn_back_to_this_week);
		
		list_layout = (LinearLayout)  findViewById(R.id.list_layout);
		add_ln = (LinearLayout)  findViewById(R.id.add_all);
		contact_data = (LinearLayout)  findViewById(R.id.contact_data);
		data_title = (TextView)  findViewById(R.id.data_title);
		data_vocation = (TextView)  findViewById(R.id.data_vocation);
		phone_list = (ListView)  findViewById(R.id.phone_list);
		remind_info = (ListView)  findViewById(R.id.remind_info);
		
		tips_top_layout = (LinearLayout)  findViewById(R.id.tips_top_layout);
		tv_top_tips = (TextView)  findViewById(R.id.tv_top_tips);
		btn_top_tips_yes = (Button)  findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_yes.setOnClickListener(this);
		btn_top_tips_no = (Button)  findViewById(R.id.btn_top_tips_no);
		btn_top_tips_no.setOnClickListener(this);
	
		
		dialog_delete_remind = new Dialog(RemindsActivity.this,R.style.theme_myDialog);
		dialog_delete_remind.setCanceledOnTouchOutside(true);
		dialog_delete_remind.setContentView(R.layout.dialog_delete_remind);
		
		
		btn_one = (Button) dialog_delete_remind.findViewById(R.id.btn_one);
		btn_all = (Button) dialog_delete_remind.findViewById(R.id.btn_all);
		btn_one.setOnClickListener(this);
		btn_all.setOnClickListener(this);
		
		
		loadeDataAndLayoutLeftBar(); //加载数据闭并初始化左侧栏
		
		if(contactList.size()>0)
		{
			selected_position = 0;
			updateSelected();
			init();
			layout();
		}else{
			init();
			layoutWhenNoDate();
		}
		
	}
	
	
	void init()
	{
		List<RemindWeekBean> rwbs_n = new ArrayList<RemindWeekBean>();
		for(int k = 0;k<7;k++)
		{
			String sd = "";
			
			if(k==0)
			{
				sd = "周\n一";
			}else if(k==1)
			{
				sd = "周\n二";
			}else if(k==2)
			{
				sd = "周\n三";
			}else if(k==3)
			{
				sd = "周\n四";
			}else if(k==4)
			{
				sd = "周\n五";
			}else if(k==5)
			{
				sd = "周\n六";
			}else if(k==6)
			{
				sd = "周\n日";
			}
			
//			System.out.println(" k  --->" + k);
//			System.out.println(" s : --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(s));
//			System.out.println(" e : --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(e));
			
			RemindWeekBean rwbn = new RemindWeekBean(sd, 1, 1);
			
			rwbs_n.add(rwbn);
		}
		
		View week_item = LayoutInflater.from(RemindsActivity.this).inflate(R.layout.remind_week_item, null);
		
		btn_back_to_this_week.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Calendar cal = Calendar.getInstance();
		    	
		    	//获取今天是星期几
		    	Date date = new Date(System.currentTimeMillis());
		    	cal.setTime(date);
		    	
		    	int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
		    	
		    	if(day_of_week == 1)
		    	{
		    		day_of_week = 7;
		    	}else{
		    		day_of_week --;
		    	}
		    	
		    	int day_gap = 1 - day_of_week; //与周一差几天
		    	
		    	long time = System.currentTimeMillis() + day_gap * 24 * 60 * 60 *1000;
		    	
		    	Date dd = new Date(time);
		    	
		    	cal.clear();
		    	cal.setTime(dd);
		    	
		    	int year_start = cal.get(Calendar.YEAR);
		    	int month_start = cal.get(Calendar.MONTH);
		    	int day_start = cal.get(Calendar.DAY_OF_MONTH);
		    	
		    	Date d_start = new Date(year_start-1900, month_start, day_start, 0, 0);
		    	
		    	//本周  周一 
		    	long week_start_time =  d_start.getTime();
		    	//本周  周日
		    	long week_end_time = week_start_time + (long)7 * (long)24 * (long)60 * (long)60 *(long)1000 - (long)1;
		    	
				if (week_gap < 0) {
//					tv_next_week_title.setText(TimeCounter.getTimeStrYYMMDD(week_start_time)+ " 至   "+ TimeCounter.getTimeStrYYMMDD(week_end_time));
					week_scroller.setOnScrollerFinish(new OnScrollerFinish() {

						@Override
						public void onScrollerFinish() {
							week_gap = 0;
							if(contactList.size()>0)
							{
								layout();
							}else{
								layoutWhenNoDate();
							}
						}
					});
					week_scroller.snapToScreen(2);

				} else {
//					tv_pre_week_title.setText(TimeCounter.getTimeStrYYMMDD(week_start_time)+ " 至   "+ TimeCounter.getTimeStrYYMMDD(week_end_time));
					week_scroller.setOnScrollerFinish(new OnScrollerFinish() {
						@Override
						public void onScrollerFinish() {
							week_gap = 0;
							if(contactList.size()>0)
							{
								layout();
							}else{
								layoutWhenNoDate();
							}
						}
					});
					week_scroller.snapToScreen(0);
				}
			}
		});
		
		tv_week_title.setClickable(true);
		tv_week_title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popPickWeek();
			}
		});
		lv_cur_week_remind = (ListView)week_item.findViewById(R.id.lv_remind_week);
		
		btn_pre_week.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToPreWeek();
			}
		});
		
		btn_next_week.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				goToNextWeek();
			}
		});
		
		
		
		View week_item_pre = LayoutInflater.from(RemindsActivity.this).inflate(R.layout.remind_week_item, null);
		ListView lv_remind_week_pre = (ListView)week_item_pre.findViewById(R.id.lv_remind_week);
		lv_remind_week_pre.setAdapter(new RemindWeekAdapter(RemindsActivity.this, rwbs_n, new WeekItemClickListener()));
		
		
		View week_item_next = LayoutInflater.from(RemindsActivity.this).inflate(R.layout.remind_week_item, null);
		ListView lv_remind_week_next = (ListView)week_item_next.findViewById(R.id.lv_remind_week);
		lv_remind_week_next.setAdapter(new RemindWeekAdapter(RemindsActivity.this, rwbs_n, new WeekItemClickListener()));
		
		week_scroller.addView(week_item_pre);
		week_scroller.addView(week_item);
		week_scroller.addView(week_item_next);
		
	}
	
	// 切换视图布局
	public void change() {
		switch (change) {
		case 0:
			week_layout.setVisibility(View.GONE);
			list_layout.setVisibility(View.VISIBLE);

			change = 1;// 切换至列表显示

			if (contactList.size() > 0) {
				
				String tag = (String) add_ln.getChildAt(selected_position).findViewById(R.id.button_item).getTag();
				String[] str = tag.split(":");
				String sender = str[0];
				String contactid = str[3];
//				System.out.println(" contactid --->" + contactid + " sender  --->" + sender);
//				System.out.println(" change  --->" + change);

				layoutListMode(contactid, sender);
				
			}else{
				
				contact_data.setVisibility(View.GONE);
				
				contactDetailRemindAdapter = new ContactDetailRemindAdapter(RemindsActivity.this,RemindsActivity.this, "-1", new RemindMenuItemClickListener());
				remind_info.setAdapter(contactDetailRemindAdapter);
			}

			break;

		case 1:

			week_layout.setVisibility(View.VISIBLE);
			list_layout.setVisibility(View.GONE);
			change = 0;// 切换至周显示

			week_gap = 0;
			if (contactList.size() > 0) {
				layout();
			}else{
				layoutWhenNoDate();
			}
			break;

		default:
			break;
		}
	}
	
	public void refresh()
	{
		String tag = (String)add_ln.getChildAt(selected_position).findViewById(R.id.button_item).getTag();
		String[] str = tag.split(":");
		String sender = str[0];
		String contactid = str[3];
		
		switch (change) {
		case 0:
			layout();
			break;

		case 1:
			layoutListMode(contactid, sender);
			break;
			
		default:
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			
		   case R.id.change_view: // 提醒界面 切换视图

			change();
			if (change == 0) {
				v.setBackgroundResource(R.drawable.btn_remind_list);
			} else {
				v.setBackgroundResource(R.drawable.btn_remind_week);
			}

			break;

		   case R.id.add_remind: // 新建提醒

			   new AddEditRmindLayout(RemindsActivity.this, -1, -1,
						new OnFinishEditRemindListener() {

							@Override
							public void OnFinishEditRemind() { // 添加成功的回调刷新
								loadeDataAndLayoutLeftBar();
								selected_position = 0; // 默认显示第一个
								updateSelected();
								refresh();
							}
						});
			
			break;
		
			case R.id.button_item:
				
				String allInfo = v.getTag().toString();
				
				String[] str = allInfo.split(":");
				String sender = str[0];
				String count = str[1];
				int position = Integer.parseInt(str[2]);
				String contactid = str[3];
				
				selected_position = position;
				
				if(change == 0 ) //周视图 点击刷新
				{
					layout();
				}else{ //列表视图 点击刷新
					layoutListMode(contactid,sender);
				}
			
				
				//根据选中的位置，刷新背景
				for(int i = 0;i<add_ln.getChildCount();i++)
				{
					if(i==position)
					{
						View firstView = add_ln.getChildAt(i);
				        firstView.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
				        Button button = (Button) firstView.findViewById(R.id.button_item);
//				        button.setText(sender+"("+count+")");
				        
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
				
				break;
		
			case R.id.btn_top_tips_yes:
				
				tips_top_layout.setVisibility(View.GONE);
				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(RemindsActivity.this, R.anim.up_out));
				
				break;
				
			case R.id.btn_top_tips_no:
				tips_top_layout.setVisibility(View.GONE);
				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(RemindsActivity.this, R.anim.up_out));
				break;
			
			case R.id.btn_one:
				
				if (selected_repeat_type!=MyDatabaseUtil.REPEAT_TYPE_ONE) {
				
				 long one =  DButil.getInstance(RemindsActivity.this).updateRemindTimeFilter(selected_remind_id,delete_remind_temp_start_time);
				
				 System.out.println("  result  ---> " + one);
				 
				} else {
					
					long result = DButil.getInstance(RemindsActivity.this).delete(selected_remind_id);
					
					System.out.println("  result  ---> " + result);
					
					//取消闹钟服务
					Intent it = new Intent(RemindsActivity.this, AlarmReceiver.class);
		    		it.putExtra(MyDatabaseUtil.REMIND_ID, selected_remind_id);		
		    		PendingIntent pit = PendingIntent.getBroadcast(RemindsActivity.this, (int)selected_remind_id, it, 0);
		    		AlarmManager amr = (AlarmManager) RemindsActivity.this.getSystemService(Activity.ALARM_SERVICE);
		    		amr.cancel(pit);
					
				}
				updateAfterDelete();
				
				dialog_delete_remind.dismiss();
				week_show_remind.dismiss();
				
				break;
				
			case R.id.btn_all:
				
				
				long result = DButil.getInstance(RemindsActivity.this).delete(selected_remind_id);
				
				System.out.println("  result  ---> " + result);
				
				//取消闹钟服务
				Intent it = new Intent(RemindsActivity.this, AlarmReceiver.class);
	    		it.putExtra(MyDatabaseUtil.REMIND_ID, selected_remind_id);		
	    		PendingIntent pit = PendingIntent.getBroadcast(RemindsActivity.this, (int)selected_remind_id, it, 0);
	    		AlarmManager amr = (AlarmManager) RemindsActivity.this.getSystemService(Activity.ALARM_SERVICE);
	    		amr.cancel(pit);
				
				updateAfterDelete();
				
				dialog_delete_remind.dismiss();
				week_show_remind.dismiss();
				
				break;
				
			default:
				break;
		}
	}
	
	
	class WeekItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			String [] tag_ss = ((String)v.getTag()).split(":");
			
			final int id = Integer.valueOf(tag_ss[0]);
			delete_remind_temp_start_time = Long.valueOf(tag_ss[1]);
			
			selected_remind_id = id;
			
			RemindBean rb = new RemindBean();
			Cursor c = DButil.getInstance(RemindsActivity.this).queryRemind(id);
			while (c.moveToNext()) {
				
				rb.setId(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_ID)));
				
				rb.setContent(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT)));
				
				rb.setContacts(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT)));
				rb.setParticipants(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_PARTICIPANT)));
				
				rb.setStart_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START)));
				rb.setEnd_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_END)));
				
				rb.setRemind_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE)));
				rb.setRemind_num(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM)));
				rb.setRemind_time(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TIME)));
				
				rb.setRepeat_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE)));
				rb.setRepeat_fre(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ)));
				
				rb.setRepeat_condition(c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION)));
				
				rb.setRepeat_start_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME)));
				rb.setRepeat_end_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME)));
			}
			c.close();
			
			selected_repeat_type = rb.getRepeat_type();
			
			View view = LayoutInflater.from(RemindsActivity.this).inflate(R.layout.pop_remind_item, null);
			week_show_remind = new Dialog(RemindsActivity.this,R.style.theme_myDialog_activity);
			week_show_remind.setContentView(view);
			week_show_remind.setCanceledOnTouchOutside(true);
	    	
			TextView tv_contact = (TextView) view.findViewById(R.id.tv_contact);
			tv_contact.setOnClickListener(new CotanctNameOnClick(rb.getContacts()));
			TextView tv_partners = (TextView) view.findViewById(R.id.tv_partners);
			
	    	TextView tv_event_time = (TextView) view.findViewById(R.id.tv_event_time);
			TextView tv_remind_num = (TextView) view.findViewById(R.id.tv_remind_num);
			
			TextView tv_repeat_type = (TextView) view.findViewById(R.id.tv_repeat_type);
			
			TextView tv_repeat_freq = (TextView) view.findViewById(R.id.tv_repeat_freq);
			TextView t_repeat_codition = (TextView) view.findViewById(R.id.t_repeat_codition); 
			TextView tv_repeat_time = (TextView) view.findViewById(R.id.tv_repeat_time);  
			
			TextView tv_remind_time = (TextView) view.findViewById(R.id.tv_remind_time);
			
			TextView content = (TextView) view.findViewById(R.id.content);
	    	
			tv_event_time.setText(TimeTool.getTimeStrYYMMDDHHMM(rb.getStart_time())+"~" + TimeTool.getTimeStrYYMMDDHHMM(rb.getEnd_time()));
			String remind_unti = "";
			
			int remind_type = rb.getRemind_type();
			switch (remind_type) {
			case MyDatabaseUtil.REMIND_TYPE_MIN:
				remind_unti="分钟";
				break;
			case MyDatabaseUtil.REMIND_TYPE_HOUR:
				remind_unti="小时";
				break;
			case MyDatabaseUtil.REMIND_TYPE_DAY:
				remind_unti="天";
				break;
			case MyDatabaseUtil.REMIND_TYPE_WEEK:
				remind_unti="星期";
				break;
				
			default:
				break;
			}
			
			tv_remind_num.setText("提醒:"+rb.getRemind_num() + " " +remind_unti);
			
			String repeat_unti = "";
			int repeat_type = rb.getRepeat_type();
			
			switch (repeat_type) {
			case MyDatabaseUtil.REPEAT_TYPE_ONE:
				repeat_unti="一次性";
				break;
			case MyDatabaseUtil.REPEAT_TYPE_DAY:
				repeat_unti="天";
				break;
			case MyDatabaseUtil.REPEAT_TYPE_WEEK:
				repeat_unti="周";
				break;
			case MyDatabaseUtil.REPEAT_TYPE_MONTH:
				repeat_unti="月";
				break;
				
			case MyDatabaseUtil.REPEAT_TYPE_YEAR:
				repeat_unti="年";
				break;	
			default:
				break;
			}
			int repeat_freq = rb.getRepeat_fre();
			
			if(repeat_unti.equals("一次性"))
			{
				 tv_repeat_type.setText("重复： "+repeat_unti);
				 tv_repeat_freq.setText("重复频率: 无");
				 tv_repeat_time.setVisibility(View.GONE);
			}else{
				 tv_repeat_type.setText("重复： 每"+repeat_unti);
				 tv_repeat_freq.setText("重复频率: "+repeat_freq+repeat_unti );
				 System.out.println(" rb.getRepeat_start_time()  ---> " + rb.getRepeat_start_time());
				 tv_repeat_time.setText("重复开始时间:" + TimeTool.getTimeStrYYMMDD(rb.getRepeat_start_time())+"  重复结束时间:" + TimeTool.getTimeStrYYMMDD(rb.getRepeat_end_time()));
			}
			
			String repeat_condition = rb.getRepeat_condition();
			
			if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_WEEK)
			{
				StringBuffer sb = new StringBuffer();
				
				if(repeat_condition.contains("1"))
				{
					sb.append("周一 ");
				}
				
				if(repeat_condition.contains("2"))
				{
					sb.append("周二 ");
				}
				
				if(repeat_condition.contains("3"))
				{
					sb.append("周三 ");
				}
				
				if(repeat_condition.contains("4"))
				{
					sb.append("周四 ");
				}
				
				if(repeat_condition.contains("5"))
				{
					sb.append("周五 ");
				}
				
				if(repeat_condition.contains("6"))
				{
					sb.append("周六 ");
				}
				
				if(repeat_condition.contains("7"))
				{
					sb.append("周日 ");
				}
				
				 t_repeat_codition.setText("重复时间: "+sb.toString());
			}else if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_MONTH){
				if(repeat_condition.equals("1")){
					Calendar car = Calendar.getInstance();
					Date date = new Date(rb.getStart_time());
					car.setTime(date);
					int day = car.get(Calendar.DATE);
					 t_repeat_codition.setText("重复时间: 每月 的 第"+day+"天");
				}else{
					Calendar car = Calendar.getInstance();
					Date date = new Date(rb.getStart_time());
					car.setTime(date);
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
			         t_repeat_codition.setText("重复时间: 每月 的 第"+week_of_month+"个星期的周"+dd);
				}
			}else{
				 t_repeat_codition.setVisibility(View.GONE);
			}
			
			 tv_remind_time.setText("提醒次数: " + rb.getRemind_time() + "次");
			 content.setText(rb.getContent());
			 
			 
			try {
				 String [] ss = rb.getContacts().split(":");
				 tv_contact.setText(ss[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 
			
			 tv_partners.setMovementMethod(LinkMovementMethod.getInstance());  

				int [] p_ids;
				String [] p_ss; //参与人的名字数组
				List<String[]> phone_list = new ArrayList<String[]>();
				String partner_names = ""; //全部参与人
				String partner_str = rb.getParticipants();
				
				if(partner_str!=null && !partner_str.equals("")){
					String [] partner_ss = partner_str.split(";");
					
					p_ids = new int[partner_ss.length];
					p_ss = new String[partner_ss.length];
					
					StringBuffer sb_name = new StringBuffer();
					
					for(int i =0;i<partner_ss.length;i++)
					{
						String [] c_s = partner_ss[i].split(":");
						p_ids[i] = Integer.valueOf(c_s[0].replace("#", ""));
						p_ss[i] = c_s[1];
						sb_name.append(c_s[1]+"   ");
						
						String [] phons = c_s[2].split(","); //电话号码
						phone_list.add(phons);
					}
					partner_names = sb_name.toString();
					
					SpannableString spannableString = new SpannableString("参与人： "+partner_names);
					
					int index = 4;
					for(int j =0;j<partner_ss.length;j++)
					{
						spannableString.setSpan(new MyclickSpan(p_ids[j],p_ss[j],phone_list.get(j)), index, index+p_ss[j].length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						//设置颜色 
						spannableString.setSpan(new ForegroundColorSpan(RemindsActivity.this.getResources().getColor(R.color.text_color_base)), index, index+p_ss[j].length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						index=index + p_ss[j].length()+3;
						
						System.out.println("index -->" + index);
					}
					
					tv_partners.setText(spannableString);
				}else{
					tv_partners.setText("参与人： 无");
				}
				
			 
			 Button btn_pop_edit = (Button)view.findViewById(R.id.btn_pop_edit);
			 btn_pop_edit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					new AddEditRmindLayout(RemindsActivity.this, id, -1,new OnFinishEditRemindListener() {
						   
						@Override
						public void OnFinishEditRemind() {
							loadeDataAndLayoutLeftBar();
							updateSelected();
							updatePopRemind(id);
							layout();
							
						}
					});
					
				}
			});
			 
			Button btn_pop_delete = (Button)view.findViewById(R.id.btn_pop_delete);
			btn_pop_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						week_show_remind.dismiss();
						
						dialog_delete_remind.show();
					}
			});
			
			week_show_remind.show();
		}
	}
	
	
	
	class CotanctNameOnClick implements OnClickListener{
		
		String source;
		
		public CotanctNameOnClick(String source)
		{
			this.source = source;
		}
		
		@Override
		public void onClick(View v) {
			
			String [] ss = source.split(":");
			int contactId = Integer.valueOf(ss[0].replace("#", ""));
			String name = ss[1];
			String [] phone_list = ss[2].split(",");
			
			showPartnerDetail(1, contactId, name, phone_list);
			 week_show_remind.dismiss();
		}
	}
	
	
	/**
	 * 
	 * 查询提醒数据  并  初始化左侧栏
	 * 
	 */
	public void loadeDataAndLayoutLeftBar(){
		
		contactList.clear();
		
		add_ln.removeAllViews();
		
		
		Cursor cursor = DButil.getInstance(RemindsActivity.this).queryRemindContact();
		
		Map<String , RemindBean> map = new HashMap<String , RemindBean>();
		
		
		if(cursor.moveToFirst()){
			
			do{
				String remindContact = cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT));
			
//				System.out.println("  remindContact  ---> " + remindContact);
				
				String[] ss = remindContact.split(":");
				String contactId = ss[0].replace("#", "");
				
//				String name = ss[1];
//				System.out.println("contactId  --->" + contactId);
				
//				remindContact = ss[0]+":"+name+":"+ss[2]; //新数据
//				System.out.println("  remindContact   --->  " + remindContact);
							
				if(!map.containsKey(contactId))
				  {
					RemindBean remindBean = new RemindBean();
					remindBean.setContacts(remindContact);
					remindBean.setCount("1");
						
					map.put(contactId, remindBean);
					contactList.add(remindBean);
					
				  }else{
					  
					RemindBean remindBean = map.get(contactId);
					int c = Integer.valueOf(remindBean.getCount());
					int a = c+1;
					remindBean.setCount(String.valueOf(a));
				  }
				
			}while(cursor.moveToNext());
			
		}
		
		cursor.close();
		
		for(int i=0;i<contactList.size();i++){
			
			FrameLayout view = (FrameLayout)LayoutInflater.from(RemindsActivity.this).inflate(R.layout.add_button_item, null);
			Button showAdapterButton = (Button) view.findViewById(R.id.button_item);
			showAdapterButton.setOnClickListener(this);
			
			String contactInfo = contactList.get(i).getContacts();
			
			String [] str = contactInfo.split(":");
			
			String contactId = str[0].substring(1, str[0].length()-1);
			
			showAdapterButton.setText(str[1]);
			showAdapterButton.setTag(str[1]+":"+contactList.get(i).getCount()+":"+i+":"+contactId);
			
			add_ln.addView(view);
		}
		
	}
	
	
	/**
	 * 
	 * 
	 * 将左侧栏选中的条目  背景 和 字体颜色 设为选中的状态
	 * 
	 * 
	 */
	public void updateSelected()
	{
		View firstView = add_ln.getChildAt(selected_position);
        firstView.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
        
        Button btn = (Button)firstView.findViewById(R.id.button_item);
        
        String[] ss = contactList.get(selected_position).getContacts().split(":");
		
		btn.setText(Html.fromHtml("<font color='#44687d'>"+ss[1]+"("+contactList.get(selected_position).getCount()+")"+"</font>"));
	}
	
	
	/**
	 * 
	 * 加载列表模式 
	 * @param id 联系人id
	 * @param name  联系人姓名
	 */
	private void layoutListMode(String id,String name){

			Cursor phone = RemindsActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = '"+id+"'", null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" desc");
			
			String[] phonelist = new String[phone.getCount()];
			
			if ( phone.moveToFirst() ){
				
				do{
					
					String number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					
					phonelist[phone.getPosition()] = number;
					
				}while(phone.moveToNext());
				
			}
				
			phone.close();
		
		if(!name.contains("我")){
			
			contact_data.setVisibility(View.VISIBLE);
			
			String job = "无";
			// 获取该联系人组织,, 只取第一个 有内容的公司
			Cursor organizations = RemindsActivity.this.getContentResolver().query(
								Data.CONTENT_URI,new String[] { Data._ID, Organization.COMPANY,Organization.TITLE,Organization.JOB_DESCRIPTION },
								Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Organization.CONTENT_ITEM_TYPE + "'",
								new String[] { String.valueOf(id) }, null);
							while (organizations.moveToNext()) {
								String job_str = organizations.getString(organizations.getColumnIndex(Organization.JOB_DESCRIPTION));
								
								if(job_str!=null) //职业
								{
									job = job_str;
									break;
								}
							} ;
			organizations.close();
			
			data_vocation.setText("职业："+job);
			
			phone_list.setAdapter(new PhoneDetailAdapter(RemindsActivity.this, RemindsActivity.this, name, phonelist));
			
		}else{
			contact_data.setVisibility(View.GONE);
		}
		
		contactDetailRemindAdapter = new ContactDetailRemindAdapter(RemindsActivity.this,RemindsActivity.this, id, new RemindMenuItemClickListener());
		remind_info.setAdapter(contactDetailRemindAdapter);
		
	}
	
	class RemindMenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if(contactDetailRemindAdapter.menu!=null)
			{
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -contactDetailRemindAdapter.menu.getHeight());
				
				contactDetailRemindAdapter.menu.setLayoutParams(lp);
			}
			
			switch (v.getId()) {
			case R.id.menu_edit_remind:
				
				String tag = v.getTag().toString();
				
				String[] s = tag.split(",");
				
				int remind_id = Integer.parseInt(s[0]);
				contact_id = s[1];
				
				new AddEditRmindLayout(RemindsActivity.this, remind_id, -1,new OnFinishEditRemindListener() {
					@Override
					public void OnFinishEditRemind() {  //修改成功的回调刷新
						loadeDataAndLayoutLeftBar();
						updateSelected();
						
						contactDetailRemindAdapter = new ContactDetailRemindAdapter(RemindsActivity.this,RemindsActivity.this, contact_id, new RemindMenuItemClickListener());
						remind_info.setAdapter(contactDetailRemindAdapter);
						
					}
				});
				
				break;
				
			case R.id.menu_delete_remind: //删除指定的一条提醒
				
				String[] ss = ((String)v.getTag()).split(":");
				
				final int id = Integer.valueOf(ss[0]);
				final int position = Integer.valueOf(ss[1]);
				
				new AlertDialog.Builder(RemindsActivity.this).setTitle("提示").setIcon(
						android.R.drawable.ic_dialog_info).setMessage("确定删除选中的提醒?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									
									long result = DButil.getInstance(RemindsActivity.this).delete(id);
									
									contactDetailRemindAdapter.remove(position);

									//取消闹钟服务
									Intent it = new Intent(RemindsActivity.this, AlarmReceiver.class);
						    		it.putExtra(MyDatabaseUtil.REMIND_ID, id);		
						    		PendingIntent pit = PendingIntent.getBroadcast(RemindsActivity.this, (int)id, it, 0);
						    		AlarmManager amr = (AlarmManager) RemindsActivity.this.getSystemService(Activity.ALARM_SERVICE);
						    		amr.cancel(pit);
						    	
						    		updateAfterDelete();
								}
							}).setNegativeButton("取消", null).show();
				break;

			default:
				break;
			}
		}
	}
	
	
	public void trigglerPhoneCall(String number)
	{
		if(pop_partner!=null)
		{
			pop_partner.dismiss();
		}
		
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
		RemindsActivity.this.startActivity(intent);
		
	}
	
	
	public void trigglerSms(String number,String name)
	{
		if(pop_partner!=null)
		{
			pop_partner.dismiss();
		}
		
		String thread_id = NewMessageActivity.queryThreadIdByNumber(RemindsActivity.this, number);

		Intent intent = new Intent(RemindsActivity.this, NewMessageActivity.class);
		intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
		intent.putExtra(NewMessageActivity.DATA_NUMBER, number);
				
		RemindsActivity.this.startActivity(intent);
		
	}
	
		
	  /**
	   * 显示提醒里面 涉及到的  联系人 或 参与人的 详细信息
	   * @param type 0参与人 ； 1联系人
	   * @param contactId
	   * @param name
	   * @param phone_list
	   */
		public void showPartnerDetail(int type,int contactId, String name,
				String[] phone_list) {
			
			View v = LayoutInflater.from(RemindsActivity.this).inflate(R.layout.dialog_partner_detail, null);
			pop_partner = new Dialog(RemindsActivity.this,R.style.theme_myDialog);
			pop_partner.setContentView(v);
			pop_partner.setCanceledOnTouchOutside(true);
			
			TextView tv_title = (TextView)v.findViewById(R.id.tv_title);
			
			String source="";
			if(type==0)
			{
				source = "参与人: "+name+" 的资料";
			}else{
				source = "联系人: "+name+" 的资料";
			}
	
			tv_title.setText(Html.fromHtml(source.replace(name, "<font color='#3d8eba'>" + name+ "</font>")));
			
			Button btn_close = (Button)v.findViewById(R.id.btn_close);
			btn_close.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					pop_partner.dismiss();
				}
			});
			
			TextView tv_job = (TextView)v.findViewById(R.id.tv_job);
			String job = "无";
			// 获取该联系人组织,, 只取第一个 有内容的公司
			Cursor organizations = RemindsActivity.this.getContentResolver().query(
								Data.CONTENT_URI,new String[] { Data._ID, Organization.COMPANY,Organization.TITLE,Organization.JOB_DESCRIPTION },
								Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Organization.CONTENT_ITEM_TYPE + "'",
								new String[] { String.valueOf(contactId) }, null);
							while (organizations.moveToNext()) {
								String job_str = organizations.getString(organizations.getColumnIndex(Organization.JOB_DESCRIPTION));
								
								if(job_str!=null) //职业
								{
									job = job_str;
									break;
								}
							} ;
			organizations.close();
			tv_job.setText(job);
			
			
			ListView lv_partner_numbers = (ListView)v.findViewById(R.id.lv_partner_numbers);
			lv_partner_numbers.setAdapter(new PartnerDetailAdapter(RemindsActivity.this, RemindsActivity.this, contactId ,name, phone_list));
			
			pop_partner.show();
		}
		
		/**
		 * 
		 * 周视图相关    计算当前被算中的联系人 下的哪些提醒  是在  下列周时间范围内
		 * 
		 */
	    public void layout()
	    {
	    	Calendar cal = Calendar.getInstance();
	    	
	    	//获取今天是星期几
	    	Date date = new Date(System.currentTimeMillis());
	    	cal.setTime(date);
	    	
	    	int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
	    	
	    	if(day_of_week == 1)
	    	{
	    		day_of_week = 7;
	    	}else{
	    		day_of_week --;
	    	}
	    	
	    	int day_gap = 1 - day_of_week; //与周一差几天
	    	
	    	long time = System.currentTimeMillis() + day_gap * 24 * 60 * 60 *1000;
	    	
	    	Date dd = new Date(time);
	    	
	    	cal.clear();
	    	cal.setTime(dd);
	    	
	    	int year_start = cal.get(Calendar.YEAR);
	    	int month_start = cal.get(Calendar.MONTH);
	    	int day_start = cal.get(Calendar.DAY_OF_MONTH);
	    	
	    	Date d_start = new Date(year_start-1900, month_start, day_start, 0, 0);
	    	
	    	//本周  周一 
	    	long week_start_time =  d_start.getTime();
//	    	System.out.println(" 本周  周一的时间为  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_start_time));
	    	
	    	//本周  周日
	    	long week_end_time = week_start_time + (long)7 * (long)24 * (long)60 * (long)60 *(long)1000 - (long)1;
//	    	System.out.println(" 本周  周日的时间为  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_end_time));
	    	
	    	
	    	//算出当前的周的  周一  00:00   和  周日的  23:59 
	    	week_start_time = week_start_time + ( (long)week_gap * (long)7 * (long)24 * (long)60 * (long)60 * (long)1000);
	    	cur_week_start_time = week_start_time;
	    	week_end_time =  week_end_time + ((long)week_gap * (long)7 * (long)24 * (long)60 * (long)60 * (long)1000);
	    	cur_week_end_time = week_end_time;
	    	
	    	System.out.println(" 当前周  周一的时间为  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_start_time));
	    	System.out.println(" 当前周  周日的时间为  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_end_time));
	    	
	    	//查询当前被选中的联系人  所有有关的    提醒
	    	String tag = (String)add_ln.getChildAt(selected_position).findViewById(R.id.button_item).getTag();
			String[] str = tag.split(":");
			String contactid = str[3];
	    	
			List<RemindBean> rbs = new ArrayList<RemindBean>();
			Cursor c = DButil.getInstance(RemindsActivity.this).queryRemindByContactId(contactid);
			
			System.out.println(" c.count()  ---> " + c.getCount());
			while (c.moveToNext()) {
				
				RemindBean rb = new RemindBean();
				
				rb.setId(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_ID)));
				
				rb.setContent(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT)));
				
				rb.setContacts(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT)));
				rb.setParticipants(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_PARTICIPANT)));
				
				rb.setStart_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START)));
				rb.setEnd_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_END)));
				
				rb.setRemind_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE)));
				rb.setRemind_num(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM)));
				rb.setRemind_time(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TIME)));
				
				rb.setRepeat_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE)));
				rb.setRepeat_fre(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ)));
				
				rb.setRepeat_condition(c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION)));
				
				rb.setRepeat_start_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME)));
				rb.setRepeat_end_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME)));
				
				rb.setTime_filter(c.getString(c.getColumnIndex(MyDatabaseUtil.TIME_FILTER)));
				
				rbs.add(rb);
			}
			
			c.close(); //关闭游标
			
			
			//在本周内的提醒
			List<RemindBean> weekBeans = new ArrayList<RemindBean>(); 
			
			for(int i = 0 ;i <rbs.size();i++)
			{
				RemindBean rb = rbs.get(i);
				
				long event_start_time = rb.getStart_time();  //活动开始时间
				long event_end_time = rb.getEnd_time();      //活动结束时间
				int freq = rb.getRepeat_fre(); //频率
				
				int repeat_type = rb.getRepeat_type(); //重复类型
				
				long repeat_end_time = rb.getRepeat_end_time(); //重复结束时间
				
				boolean isMatch = false;
				
				String time_filter = rb.getTime_filter();
				System.out.println( " time_filter  ---> " + time_filter);
				
//				System.out.println(" event_start_time is --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(event_start_time) );
				
				switch (repeat_type) {
				
				case MyDatabaseUtil.REPEAT_TYPE_ONE: //一次性
					
					 
					  
					   if( event_start_time >week_start_time && event_start_time <week_end_time )
					   {
					      isMatch = true;
					   }

					   if( event_end_time > week_start_time && event_end_time < week_end_time )
					   {
					      isMatch = true;
					   }
					   
					   if(event_start_time < week_start_time && event_end_time > week_end_time) //事件持续包含整周
					   {
						  isMatch = true;
					   }

					   if(time_filter.contains(String.valueOf(event_start_time))) //已被过滤掉
					   {
						   isMatch = false;
					   }
					   
					    if(isMatch) //此活动在本周内
					   {
					    	long temp_start_time = event_start_time;
						    long temp_end_time = event_end_time;
					    	
//					    	if(event_start_time<week_start_time) //超过本周开始时间
//				    	    {
//				    	    	temp_start_time = week_start_time;
//				    	    }else{
//				    	    	temp_start_time = event_start_time;
//				    	    }
//				    	    
//				    	    if(event_end_time>week_end_time)    //超过本周结束时间
//				    	    {
//				    	    	temp_end_time = week_end_time;
//				    	    }else{
//				    	    	temp_end_time = event_end_time;
//				    	    }
				    	    
					    	rb.setTemp_start_time(temp_start_time);
					    	rb.setTemp_end_time(temp_end_time);
					    	weekBeans.add(rb);
					    	
//					    	System.out.println(" 一次性 : from " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_start_time) +" to " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_end_time));
					   }
					break;
					
				case MyDatabaseUtil.REPEAT_TYPE_DAY: //天重复
					
//					System.out.println(" 天重复  ----");
					
					if(repeat_end_time>week_start_time)
					{
						long next_start_time = event_start_time;
					    long next_end_time = event_end_time;
					    
					    while(next_start_time < week_end_time)
					    {
					    
					    System.out.println(" next_start_time: " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(next_start_time) +" week_end_time:  " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_end_time));
					    if(next_start_time > repeat_end_time ) //超过重复结束时间
					    {
					    	break;
					    }
					    	
					     if(next_start_time > week_start_time && next_start_time < week_end_time){
					        isMatch  = true;
					      }
					      
					      if(next_end_time > week_start_time && next_end_time < week_end_time){
					        isMatch  = true;
					      } 
					      
					      if(next_start_time < week_start_time && event_end_time > next_end_time) //事件持续包含整周
							{
								  isMatch = true;
							}
					      
					      if(time_filter.contains(String.valueOf(next_start_time))) //已被过滤掉
						   {
							   isMatch = false;
						   }
					      
					      if(isMatch) //此活动在本周内
						   {
					    	  long temp_start_time = next_start_time;
						      long temp_end_time = next_end_time;
					    	    
//					    	    if(next_start_time < week_start_time) //超过本周开始时间
//					    	    {
//					    	    	temp_start_time = week_start_time;
//					    	    }else{
//					    	    	temp_start_time = next_start_time;
//					    	    }
//					    	    
//					    	    if(next_end_time > week_end_time)    //超过本周结束时间
//					    	    {
//					    	    	temp_end_time = week_end_time;
//					    	    }else{
//					    	    	temp_end_time = next_end_time;
//					    	    }
					    	    RemindBean new_rb = rb.copy();
					    	    new_rb.setTemp_start_time(temp_start_time);
					    	    new_rb.setTemp_end_time(temp_end_time);
						    	
						    	System.out.println(" 天重复: from " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_start_time) +" to " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_end_time));
						    	
						    	weekBeans.add(new_rb);
						   }
					      
					      next_start_time = next_start_time + (long)freq  * (long)24 * (long)60*(long)60*(long)1000;
					      next_end_time = next_end_time + (long)freq  * (long)24 *(long)60*(long)60*(long)1000;
					      
					      isMatch = false;
					   }
					}
					
					break;
					
				case MyDatabaseUtil.REPEAT_TYPE_WEEK: //周重复
					
					System.out.println(" 周重复  ----");
					
					if(repeat_end_time>week_start_time)
					{
						int repeat_time =0 ; //重复次数
						
						String repeat_condition = rb.getRepeat_condition();
				        String []ss = repeat_condition.split(",");
				        
				        int [] wds; //周重复  的详情  :    {1,3,5}
				        if(repeat_condition.length()==1)
				        {
				        	wds = new int [1];
				        	wds[0] = Integer.valueOf(repeat_condition);
				        }else{
				        	wds = new int [ss.length];
				        	
				        	for(int j = 0;j<ss.length;j++)
				        	{
				        		wds[j] = Integer.valueOf(ss[j]);
				        	}
				        }
				        
						
						long next_start_time = event_start_time;
					    long next_end_time = event_end_time;
					    
					    
					    while(next_start_time<week_end_time)
					    {
					    	if(next_start_time > repeat_end_time ) //超过重复结束时间
					    	{
					    		break;
					    	}
					    	
					    	System.out.println(" next_start_time: " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(next_start_time) +" next_end_time:  " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(next_end_time));
					    	
					    	if(next_start_time > week_start_time && next_start_time < week_end_time){
					            isMatch  = true;
					         }
					          
					        if(next_end_time > week_start_time && next_end_time < week_end_time){
					            isMatch  = true;
					         }
					        
					        if(next_start_time < week_start_time && event_end_time > next_end_time) //事件持续包含整周
							  {
								  isMatch = true;
							  }
					        
					        if(time_filter.contains(String.valueOf(next_start_time))) //已被过滤掉
							   {
								   isMatch = false;
							   }
					        
					        if(isMatch) //此活动在本周内
					        {
					        	long temp_start_time = next_start_time;
						    	long temp_end_time = next_end_time;
					        	
//					        	 long temp_start_time;
//						    	 long temp_end_time;
//						    	  
//						    	 if(next_start_time < week_start_time) //超过本周开始时间
//						    	 {
//						    	    temp_start_time = week_start_time;
//						    	 }else{
//						    	    temp_start_time = next_start_time;
//						    	 }
//						    	    
//						    	 if(next_end_time > week_end_time)    //超过本周结束时间
//						    	 {
//						    	    temp_end_time = week_end_time;
//						    	 }else{
//						    	    temp_end_time = next_end_time;
//						    	 }
						    	    
						    	RemindBean new_rb = rb.copy();
					    	    new_rb.setTemp_start_time(temp_start_time);
					    	    new_rb.setTemp_end_time(temp_end_time);
							    	
							    System.out.println(" 周重复: from " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_start_time) +" to " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_end_time));
							    weekBeans.add(new_rb);
					        }
					        
					        //获取已经重复到星期几
					        Date d = new Date(next_start_time);
					        Calendar calendar = Calendar.getInstance();
					        calendar.setTime(d);
					        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
					        
					        if(weekDay==1) //周日
					        {
					        	weekDay = 7;
					        }else{
					        	weekDay -- ;
					        }
					        
					        
					        int index = -1;
					        for(int k = 0 ; k<wds.length;k++)
					        {
					        	if(weekDay==wds[k])
					        	{
					        		index = k;
					        		break;
					        	}
					        }
					        
					        if(index == wds.length-1) //本周重复结束
					        {
					        	 repeat_time++;
					             next_start_time =  event_start_time + (long)repeat_time*(long)(freq)*(long)7*(long)24*(long)60*(long)60*(long)1000;
					             next_end_time = event_end_time + (long)repeat_time*(long)(freq)*(long)7*(long)24*(long)60*(long)60*(long)1000;
					             
					             Date ddd = new Date(next_start_time);
							     Calendar calendars = Calendar.getInstance();
							     calendars.setTime(ddd);
							     int w = calendars.get(Calendar.DAY_OF_WEEK); 
							     if(w==1) //周日
							     {
							        w = 7;
							     }else{
							        w -- ;
							     }
					             
							     int first_week_day = wds[0];
							     
							     next_start_time = next_start_time + (long)(first_week_day - w) * (long)24 *(long)60 *(long)60 * (long)1000;
							     next_end_time = next_end_time + (long)(first_week_day - w) * (long)24 *(long)60 *(long)60 * (long)1000;
							     
					        }else{
					        	int next_week_day =  wds[index+1];
					            int day_crap =  next_week_day - weekDay; //相差多少天
					            next_start_time = next_start_time + (long)day_crap * (long)24*(long)60*(long)60*(long)1000;
					            next_end_time  = next_end_time + (long)day_crap * (long)24*(long)60*(long)60*(long)1000;
					        }
					        
					        isMatch = false;
					    }
					}
					break;
					
					
				case MyDatabaseUtil.REPEAT_TYPE_MONTH:  //月重复
					if(repeat_end_time>week_start_time)
					{
					   long next_start_time = event_start_time;
					   long next_end_time = event_end_time;
					     
					   Date date1 = new Date(event_start_time);
					   Calendar cc = Calendar.getInstance();
					   cc.setTime(date1);
					   
					   int day = cc.get(Calendar.DATE);
					   int year = cc.get(Calendar.YEAR);
					   int month = cc.get(Calendar.MONTH);
					   int hour = cc.get(Calendar.HOUR_OF_DAY);
				       int weekOfMonth = cc.get(Calendar.WEEK_OF_MONTH);
					   int dayOfWeek = cc.get(Calendar.DAY_OF_WEEK);
					   int min = cc.get(Calendar.MINUTE);
					   
					   while(next_start_time < week_end_time)
					   {
						   if(next_start_time > repeat_end_time )//超过重复结束时间
					    	{
					    		break;
					    	}
						   
						   if(next_start_time > week_start_time && next_start_time < week_end_time){
					            isMatch  = true;
					         }
					          
					        if(next_end_time > week_start_time && next_end_time < week_end_time){
					            isMatch  = true;
					         }
					        
					        if(next_start_time < week_start_time && event_end_time > next_end_time) //事件持续包含整周
							{
								  isMatch = true;
							}
					        
					        if(time_filter.contains(String.valueOf(next_start_time))) //已被过滤掉
							   {
								   isMatch = false;
							   }
					        
					        if(isMatch) //此活动在本周内
					        {
					        	long temp_start_time = next_start_time;
						    	long temp_end_time = next_end_time;
						    	  
//						    	 if(next_start_time < week_start_time) //超过本周开始时间
//						    	 {
//						    	    temp_start_time = week_start_time;
//						    	 }else{
//						    	    temp_start_time = next_start_time;
//						    	 }
//						    	    
//						    	 if(next_end_time > week_end_time)    //超过本周结束时间
//						    	 {
//						    	    temp_end_time = week_end_time;
//						    	 }else{
//						    	    temp_end_time = next_end_time;
//						    	 }
						    	    
						    	RemindBean new_rb = rb.copy();
					    	    new_rb.setTemp_start_time(temp_start_time);
					    	    new_rb.setTemp_end_time(temp_end_time);
							    
							    System.out.println(" 月重复: from " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_start_time) +" to " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_end_time));
							     
							    weekBeans.add(new_rb);
					        }
					        
					        int repeat_condition = Integer.valueOf(rb.getRepeat_condition());
					        
					        if(repeat_condition==1)   //计算下个月的第几天的开始时间 和结束时间
					        {
					        	month = month + freq;
								
								if(month >11)
								{
									 year = year+(month/11);
						    		 month=(month%11)-1;
								}
								
								Date date2 = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(min));
								next_start_time =  date2.getTime();
								next_end_time = next_start_time +(event_end_time-event_start_time);
								
					        }else{  //计算下个月的第几个星期的周几的 开始时间 和结束时间
					        	month= month + freq;
								
								next_start_time =  TimeTool.weekdatetodata(year, month, weekOfMonth, dayOfWeek, hour, min);
								next_end_time = next_start_time +(event_end_time-event_start_time);
					        }  
					        
					        isMatch = false;
					   }
					}
					break;
					
				case MyDatabaseUtil.REPEAT_TYPE_YEAR: //年重复
					if(repeat_end_time>week_start_time)
					{
						long next_start_time = event_start_time;
						long next_end_time = event_end_time;
						
						Date date1 = new Date(event_start_time);
					    Calendar cc = Calendar.getInstance();
						cc.setTime(date1);
						   
						int day = cc.get(Calendar.DATE);
						int year = cc.get(Calendar.YEAR);
						int month = cc.get(Calendar.MONTH);
						int hour = cc.get(Calendar.HOUR_OF_DAY);
						int min = cc.get(Calendar.MINUTE);
						   
						while(next_start_time < week_end_time)
						{
							if(next_start_time > repeat_end_time ) //超过重复结束时间
					    	{
					    		break;
					    	}
							
							if(next_start_time > week_start_time && next_start_time < week_end_time){
						         isMatch  = true;
						    }
						          
						    if(next_end_time > week_start_time && next_end_time < week_end_time){
						         isMatch  = true;
						    }
						        
						    if(next_start_time < week_start_time && event_end_time > next_end_time) //事件持续包含整周
							  {
								  isMatch = true;
							  }
						       
						    if(time_filter.contains(String.valueOf(next_start_time))) //已被过滤掉
							   {
								   isMatch = false;
							   }
						    
						    if(isMatch) //此活动在本周内
						    {
						    	long temp_start_time = next_start_time;
						    	long temp_end_time = next_end_time;
							    	  
//							    if(next_start_time < week_start_time) //超过本周开始时间
//							    {
//							    	  temp_start_time = week_start_time;
//							    }else{
//							    	  temp_start_time = next_start_time;
//							    }
//							    	    
//							    if(next_end_time > week_end_time)    //超过本周结束时间
//							    {
//							    	  temp_end_time = week_end_time;
//							    }else{
//							    	 temp_end_time = next_end_time;
//							    }
							    	    
						    	RemindBean new_rb = rb.copy();
					    	    new_rb.setTemp_start_time(temp_start_time);
					    	    new_rb.setTemp_end_time(temp_end_time);
								    	
								System.out.println(" 年重复: from " +  new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_start_time) +" to " + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(temp_end_time));
								
								weekBeans.add(new_rb);
						        }
						        
						        year = year + freq;
								Date date3 = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(min));
								next_start_time =  date3.getTime();
								next_end_time = next_start_time +(event_end_time-event_start_time);
						        
						        isMatch = false;
						}
					}
					break;
				
				default:
					break;
				}
				
			}
			
			//排序
			List<RemindWeekBean> rwbs = new ArrayList<RemindWeekBean>();
			
			for(int k = 0;k<7;k++)
			{
				long s = week_start_time+ (long)k *(long)24 * (long)60 * (long)60 *(long)1000;
				long e = week_start_time+ (long)(k+1) *(long)24 * (long)60 * (long)60 *(long)1000 - (long)1;
				
				String sd = "";
				
				if(k==0)
				{
					sd = "周\n一";
				}else if(k==1)
				{
					sd = "周\n二";
				}else if(k==2)
				{
					sd = "周\n三";
				}else if(k==3)
				{
					sd = "周\n四";
				}else if(k==4)
				{
					sd = "周\n五";
				}else if(k==5)
				{
					sd = "周\n六";
				}else if(k==6)
				{
					sd = "周\n日";
				}
				
//				System.out.println(" k  --->" + k);
//				System.out.println(" s : --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(s));
//				System.out.println(" e : --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(e));
				
				RemindWeekBean rwb =  new RemindWeekBean(sd,s,e);
				rwbs.add(rwb);
			}
			
			//标记
			for(int j = 0 ; j<weekBeans.size() ;j++)
			{
				RemindBean rb = weekBeans.get(j);
				long  temp_start_time = rb.getTemp_start_time();
				long  temp_end_time = rb.getTemp_end_time();
				
				for(int k = 0;k<rwbs.size();k++)
				{
					RemindWeekBean rwb = rwbs.get(k);
					long week_day_start_time = rwb.getWeek_day_start_time();
					long week_day_end_time = rwb.getWeek_day_end_time();
					
					if( !(temp_end_time < week_day_start_time)  &&  !(temp_start_time > week_day_end_time))
					{
						rwb.getRbs().add(rb);
					}
				}
			}

			tv_week_title.setText(TimeTool.getTimeStrYYMMDD(week_start_time)+ " 至 "+ TimeTool.getTimeStrYYMMDD(week_end_time));
//			tv_pre_week_title.setText(TimeCounter.getTimeStrYYMMDD(week_start_time - (long)7* (long)24 *(long)60 *(long)60 *(long)1000)+ " 至   "+ TimeCounter.getTimeStrYYMMDD(week_end_time - 7* 24 *60 *60 *1000));
//			tv_next_week_title.setText(TimeCounter.getTimeStrYYMMDD(week_start_time + (long)7* (long)24 *(long)60 *(long)60 *(long)1000)+ " 至   "+ TimeCounter.getTimeStrYYMMDD(week_end_time + 7* 24 *60 *60 *1000));
			
			RemindWeekAdapter remindWeekAdapter = new RemindWeekAdapter(RemindsActivity.this, rwbs, new WeekItemClickListener());
			lv_cur_week_remind.setAdapter(remindWeekAdapter);
			
			week_scroller.setToScreen(1); //显示中间的屏幕
			
			//是否显示返回本周按钮
			if(week_gap==0)
			{
				btn_back_to_this_week.setVisibility(View.GONE);
			}else{
				btn_back_to_this_week.setVisibility(View.VISIBLE);
			}
			
	    }
	    
	    
	    /**
	     * 
	     * 当没有任何提醒数据时  加载的界面
	     * 
	     */
	    void layoutWhenNoDate()
	    {
            Calendar cal = Calendar.getInstance();
	    	
	    	//获取今天是星期几
	    	Date date = new Date(System.currentTimeMillis());
	    	cal.setTime(date);
	    	
	    	int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
	    	
	    	if(day_of_week == 1)
	    	{
	    		day_of_week = 7;
	    	}else{
	    		day_of_week --;
	    	}
	    	
	    	int day_gap = 1 - day_of_week; //与周一差几天
	    	
	    	long time = System.currentTimeMillis() + day_gap * 24 * 60 * 60 *1000;
	    	
	    	Date dd = new Date(time);
	    	
	    	cal.clear();
	    	cal.setTime(dd);
	    	
	    	int year_start = cal.get(Calendar.YEAR);
	    	int month_start = cal.get(Calendar.MONTH);
	    	int day_start = cal.get(Calendar.DAY_OF_MONTH);
	    	
	    	Date d_start = new Date(year_start-1900, month_start, day_start, 0, 0);
	    	
	    	//本周  周一 
	    	long week_start_time =  d_start.getTime();
//	    	System.out.println(" 本周  周一的时间为  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_start_time));
	    	
	    	//本周  周日
	    	long week_end_time = week_start_time + (long)7 * (long)24 * (long)60 * (long)60 *(long)1000 - (long)1;
//	    	System.out.println(" 本周  周日的时间为  --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(week_end_time));
	    	
	    	
	    	//算出当前的周的  周一  00:00   和  周日的  23:59 
	    	week_start_time = week_start_time + ( (long)week_gap * (long)7 * (long)24 * (long)60 * (long)60 * (long)1000);
	    	cur_week_start_time = week_start_time;
	    	week_end_time =  week_end_time + ((long)week_gap * (long)7 * (long)24 * (long)60 * (long)60 * (long)1000);
	    	cur_week_end_time = week_end_time;
	    	tv_week_title.setText(TimeTool.getTimeStrYYMMDD(week_start_time)+ " 至 "+ TimeTool.getTimeStrYYMMDD(week_end_time));
	    	
	    	List<RemindWeekBean> rwbs_n = new ArrayList<RemindWeekBean>();
			for(int k = 0;k<7;k++)
			{
				String sd = "";
				
				if(k==0)
				{
					sd = "周\n一";
				}else if(k==1)
				{
					sd = "周\n二";
				}else if(k==2)
				{
					sd = "周\n三";
				}else if(k==3)
				{
					sd = "周\n四";
				}else if(k==4)
				{
					sd = "周\n五";
				}else if(k==5)
				{
					sd = "周\n六";
				}else if(k==6)
				{
					sd = "周\n日";
				}
				
//				System.out.println(" k  --->" + k);
//				System.out.println(" s : --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(s));
//				System.out.println(" e : --->" + new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).format(e));
				
				RemindWeekBean rwbn = new RemindWeekBean(sd, 1, 1);
				
				rwbs_n.add(rwbn);
			}
			RemindWeekAdapter remindWeekAdapter = new RemindWeekAdapter(RemindsActivity.this, rwbs_n, new WeekItemClickListener());
			lv_cur_week_remind.setAdapter(remindWeekAdapter);
            week_scroller.setToScreen(1); //显示中间的屏幕
			
	    	//是否显示返回本周按钮
			if(week_gap==0)
			{
				btn_back_to_this_week.setVisibility(View.GONE);
			}else{
				btn_back_to_this_week.setVisibility(View.VISIBLE);
			}
	    }
	    
	    public void goToPreWeek()
	    {
	    	week_scroller.setOnScrollerFinish(new OnScrollerFinish() { 
				
				@Override
				public void onScrollerFinish() {
					activatePreWeek();
				}
			});
	    	week_scroller.snapToScreen(0);
	    	
	    }
	    
	    public void goToNextWeek(){
                week_scroller.setOnScrollerFinish(new OnScrollerFinish() { 
				
				@Override
				public void onScrollerFinish() {
					activateNextWeek();
				}
			});
           week_scroller.snapToScreen(2);
	    }
	    
	    
	    void popPickWeek()
	    {
	    	View view = LayoutInflater.from(RemindsActivity.this).inflate(R.layout.popup_pick_week, null);
	    	pop_pick_week = new Dialog(RemindsActivity.this,R.style.theme_myDialog);
	    	pop_pick_week.setCanceledOnTouchOutside(true);
	    	pop_pick_week.setContentView(view);
	    	
	    	LinearLayout btn_pre_month = (LinearLayout) view.findViewById(R.id.btn_pre_month);
	    	btn_pre_month.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//先滑动，后加载
					
					month_scroller.setOnScrollerFinish(new ScrollLayout.OnScrollerFinish() { 
						
						@Override
						public void onScrollerFinish() {
							long t = times.get(0);
							
//							int s_month = TimeCounter.getMonth(t- (long)7* (long)24 *(long)60 *(long)60 *(long)1000);
//							int e_month = TimeCounter.getMonth(t );
							
//							System.out.println("  t :  " + TimeCounter.getTimeStrYYMMDD(t));
//							System.out.println("  t _ s :  " + TimeCounter.getTimeStrYYMMDD(t - (long)14* (long)24 *(long)60 *(long)60 *(long)1000));
//							System.out.println("  t _ e:  " + TimeCounter.getTimeStrYYMMDD(t - (long)7* (long)24 *(long)60 *(long)60 *(long)1000 - (long)1));
							
							countWeekOfMonth(t - (long)14* (long)24 *(long)60 *(long)60 *(long)1000 ,t - (long)7* (long)24 *(long)60 *(long)60 *(long)1000 - (long)1 );
							
							lv_month_week.setAdapter(new PickWeekAdapter(RemindsActivity.this, times));
					    	month_scroller.setToScreen(1);
						}
					});
					
					month_scroller.snapToScreen(0);
				}
			});
	    	
	    	LinearLayout btn_next_month = (LinearLayout) view.findViewById(R.id.btn_next_month);
	    	btn_next_month.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					month_scroller.setOnScrollerFinish(new ScrollLayout.OnScrollerFinish() { 
						
						@Override
						public void onScrollerFinish() {
							int last = times.size() - 1;
							long t = times.get(last);
							
//							System.out.println("  t :  " + TimeCounter.getTimeStrYYMMDD(t));
//							System.out.println("  t _ s :  " + TimeCounter.getTimeStrYYMMDD(t + (long)7* (long)24 *(long)60 *(long)60 *(long)1000));
//							System.out.println("  t _ e:  " + TimeCounter.getTimeStrYYMMDD(t + (long)14* (long)24 *(long)60 *(long)60 *(long)1000 - (long)1));
							
							countWeekOfMonth(t + (long)7* (long)24 *(long)60 *(long)60 *(long)1000, t + (long)14* (long)24 *(long)60 *(long)60 *(long)1000 - (long)1 );
							
							lv_month_week.setAdapter(new PickWeekAdapter(RemindsActivity.this, times));
					    	month_scroller.setToScreen(1);
						}
					});
					month_scroller.snapToScreen(2);
				}
			});
	    	
	    	
	    	tv_month_title = (TextView) view.findViewById(R.id.tv_month_title);
	    	lv_month_week = (ListView)view.findViewById(R.id.lv_month_week);
	    	countWeekOfMonth(cur_week_start_time,cur_week_end_time);
	    	lv_month_week.setAdapter(new PickWeekAdapter(RemindsActivity.this, times));
	    	
	    	lv_month_week.setOnItemClickListener(new OnItemClickListener() {  //点击跳转到指定的周

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					pop_pick_week.dismiss();
					
					long t = times.get(position);
					
					Calendar cal = Calendar.getInstance();
			    	
			    	//获取今天是星期几
			    	Date date = new Date(System.currentTimeMillis());
			    	cal.setTime(date);
			    	
			    	int day_of_week = cal.get(Calendar.DAY_OF_WEEK);
			    	
			    	if(day_of_week == 1)
			    	{
			    		day_of_week = 7;
			    	}else{
			    		day_of_week --;
			    	}
			    	
			    	int day_gap = 1 - day_of_week; //与周一差几天
			    	
			    	long time = System.currentTimeMillis() + day_gap * 24 * 60 * 60 *1000;
			    	
			    	Date dd = new Date(time);
			    	
			    	cal.clear();
			    	cal.setTime(dd);
			    	
			    	int year_start = cal.get(Calendar.YEAR);
			    	int month_start = cal.get(Calendar.MONTH);
			    	int day_start = cal.get(Calendar.DAY_OF_MONTH);
			    	
			    	Date d_start = new Date(year_start-1900, month_start, day_start, 0, 0);
			    	
			    	//本周  周一 
			    	long week_start_time =  d_start.getTime();
			    	
			    	
			    	long t_gap = t - week_start_time;
			    	
			    	week_gap = (int)(t_gap /((long)7* (long)24 *(long)60 *(long)60 *(long)1000));
			    	if(contactList.size()>0)
			    	{
			    		layout();
			    	}else{
			    		layoutWhenNoDate();
			    	}
			    	
				}
			});
	    	
	    	month_scroller = (ScrollLayout)view.findViewById(R.id.month_scroller);
	    	month_scroller.setToScreen(1);
	    	
	    	pop_pick_week.show();
	    }
	    
	    void countWeekOfMonth(long s , long e)
	    {
            times = new ArrayList<Long>();
	    	
	    	long s_time = s;
	    	long e_time = e;
	    	
	    	
	    	//获取当前在哪个月份
	    	
	    	int s_month = TimeTool.getMonth(s_time);
	    	int e_month = TimeTool.getMonth(e_time);
	    	
	    	tv_month_title.setText(TimeTool.getYear(e_time)+"年" + e_month +"月");
	    	
	    	if(s_month!=e_month) // 跨月份    以结束时间月份为准
	    	{
	    		times.add(Long.valueOf(s_time));
	    		
	    		int end_day ;
	    		int month = TimeTool.getMonth(s_time);
	    		int year = TimeTool.getYear(s_time);
	    		
	    		String s1 = "1,3,5,7,8,10,12";
	    		
	    		if(month == 2)
	    		{
	    			if ((year % 100 == 0 && year % 400 == 0) || year % 4 == 0) //闰年
	    			{
	    				end_day = 29;
	    			}else{
	    				end_day = 28;
	    			}
	    		}else if(s1.contains(String.valueOf(month))){
	    			end_day = 31;
	    		}else{
	    			end_day = 30;
	    		}
	    		
	    		while(true)
	    		{
	    			s_time = s_time +  (long)7* (long)24 *(long)60 *(long)60 *(long)1000;
	    			e_time = e_time + (long)7* (long)24 *(long)60 *(long)60 *(long)1000;
	    			
	    			times.add(Long.valueOf(s_time));
	    			
	    			if(TimeTool.getMonth(s_time) != TimeTool.getMonth(e_time) || TimeTool.getDay(e_time) == end_day)
	    			{
	    				break;
	    			}
	    		}
	    		
	    	}else{ //没跨月份
	    		
	    		boolean isHave = false;
	    		
	    		long ss_time = s_time;
	    		long ee_time = e_time;
	    		
	    		List<Long> s_times = new ArrayList<Long>();
	    		
	    		while(true) //往前推
	    		{
	    			if(TimeTool.getDay(ss_time)==1)
	    			{
	    				s_times.add(Long.valueOf(ss_time));
	    				isHave = true;
	    				break;
	    			}
	    			
	    			ss_time =  ss_time  -  (long)7* (long)24 *(long)60 *(long)60 *(long)1000;
	    			ee_time =  ee_time  -  (long)7* (long)24 *(long)60 *(long)60 *(long)1000;
	    			
	    			s_times.add(Long.valueOf(ss_time));
	    			
	    			if(TimeTool.getDay(ss_time)==1 || TimeTool.getMonth(ss_time) != TimeTool.getMonth(ee_time))
	    			{
	    				break;
	    			}
	    		}
	    		
	    		
	    		ss_time = s_time;
	    		ee_time = e_time;
	    		
	    		int end_day ;
	    		int month = TimeTool.getMonth(ss_time);
	    		int year = TimeTool.getYear(ss_time);
	    		
	    		String s1 = "1,3,5,7,8,10,12";
	    		
	    		if(month == 2)
	    		{
	    			if ((year % 100 == 0 && year % 400 == 0) || year % 4 == 0) //闰年年
	    			{
	    				end_day = 29;
	    			}else{
	    				end_day = 28;
	    			}
	    		}else if(s1.contains(String.valueOf(month))){
	    			end_day = 31;
	    		}else{
	    			end_day = 30;
	    		}
	    		
	    		List<Long> e_times = new ArrayList<Long>();
	    		while(true) //往后推
	    		{
	    			if(TimeTool.getDay(ee_time)==end_day)
	    			{
	    				e_times.add(Long.valueOf(ee_time));
	    				isHave = true;
	    				break;
	    			}
	    			
	    			ss_time =  ss_time  + (long)7* (long)24 *(long)60 *(long)60 *(long)1000;
	    			ee_time =  ee_time  +  (long)7* (long)24 *(long)60 *(long)60 *(long)1000;
	    			
	    			e_times.add(Long.valueOf(ss_time));
	    			
	    			if(TimeTool.getDay(ss_time)==end_day || TimeTool.getMonth(ss_time) != TimeTool.getMonth(ee_time))
	    			{
	    				break;
	    			}
	    		}
	    		
	    		for(int i = s_times.size()-1; i>=0 ;i--)
	    		{
	    			times.add(s_times.get(i));
	    		}
	    		if(!isHave)
	    		{
	    			times.add(s_time);
	    		}
	    		
	    		for(Long l : e_times)
	    		{
	    			times.add(l);
	    		}
	    		
	    		for(Long l : times)
	    		{
	    			System.out.println("  月 周 详情:    " + TimeTool.getTimeStrYYMMDD(l));
	    		}
	    	}
	    }
	    
	    class MyclickSpan extends ClickableSpan{

			int contactId;
			String name;
			String[] phone_list;
			
			
			public MyclickSpan(int contactId, String name, String[] phone_list) {
				this.contactId = contactId;
				this.name = name;
				this.phone_list = phone_list;
			}

			 @Override
			    public void updateDrawState(TextPaint ds) {
			        ds.setColor(ds.linkColor);
			        ds.setUnderlineText(false);
			    }

			@Override
			public void onClick(View widget) {
				System.out.println(" name ---> " + name);
			    showPartnerDetail(0,contactId, name, phone_list);
			    week_show_remind.dismiss();
			} 
		}
	    
	    void updatePopRemind(int id)
	    {
	    	RemindBean rb = new RemindBean();
			Cursor c = DButil.getInstance(RemindsActivity.this).queryRemind(id);
			while (c.moveToNext()) {
				
				rb.setId(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_ID)));
				
				rb.setContent(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT)));
				
				rb.setContacts(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT)));
				rb.setParticipants(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_PARTICIPANT)));
				
				rb.setStart_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START)));
				rb.setEnd_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_END)));
				
				rb.setRemind_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE)));
				rb.setRemind_num(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM)));
				rb.setRemind_time(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TIME)));
				
				rb.setRepeat_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE)));
				rb.setRepeat_fre(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ)));
				
				rb.setRepeat_condition(c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION)));
				
				rb.setRepeat_start_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME)));
				rb.setRepeat_end_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME)));
			}
			c.close();
			
			
			TextView tv_contact = (TextView) week_show_remind.findViewById(R.id.tv_contact);
			TextView tv_partners = (TextView) week_show_remind.findViewById(R.id.tv_partners);
			
	    	TextView tv_event_time = (TextView) week_show_remind.findViewById(R.id.tv_event_time);
			TextView tv_remind_num = (TextView) week_show_remind.findViewById(R.id.tv_remind_num);
			
			TextView tv_repeat_type = (TextView) week_show_remind.findViewById(R.id.tv_repeat_type);
			
			TextView tv_repeat_freq = (TextView) week_show_remind.findViewById(R.id.tv_repeat_freq);
			TextView t_repeat_codition = (TextView) week_show_remind.findViewById(R.id.t_repeat_codition); 
			TextView tv_repeat_time = (TextView) week_show_remind.findViewById(R.id.tv_repeat_time);  
			
			TextView tv_remind_time = (TextView) week_show_remind.findViewById(R.id.tv_remind_time);
			
			TextView content = (TextView) week_show_remind.findViewById(R.id.content);
	    	
			tv_event_time.setText(TimeTool.getTimeStrYYMMDDHHMM(rb.getStart_time())+" 至 " + TimeTool.getTimeStrYYMMDDHHMM(rb.getEnd_time()));
			String remind_unti = "";
			
			int remind_type = rb.getRemind_type();
			switch (remind_type) {
			case MyDatabaseUtil.REMIND_TYPE_MIN:
				remind_unti="分钟";
				break;
			case MyDatabaseUtil.REMIND_TYPE_HOUR:
				remind_unti="小时";
				break;
			case MyDatabaseUtil.REMIND_TYPE_DAY:
				remind_unti="天";
				break;
			case MyDatabaseUtil.REMIND_TYPE_WEEK:
				remind_unti="星期";
				break;
				
			default:
				break;
			}
			
			tv_remind_num.setText("提醒:"+rb.getRemind_num() + " " +remind_unti);
			
			String repeat_unti = "";
			int repeat_type = rb.getRepeat_type();
			
			switch (repeat_type) {
			case MyDatabaseUtil.REPEAT_TYPE_ONE:
				repeat_unti="一次性";
				break;
			case MyDatabaseUtil.REPEAT_TYPE_DAY:
				repeat_unti="天";
				break;
			case MyDatabaseUtil.REPEAT_TYPE_WEEK:
				repeat_unti="周";
				break;
			case MyDatabaseUtil.REPEAT_TYPE_MONTH:
				repeat_unti="月";
				break;
				
			case MyDatabaseUtil.REPEAT_TYPE_YEAR:
				repeat_unti="年";
				break;	
			default:
				break;
			}
			int repeat_freq = rb.getRepeat_fre();
			
			if(repeat_unti.equals("一次性"))
			{
				 tv_repeat_type.setText("重复： "+repeat_unti);
				 tv_repeat_freq.setText("重复频率: 无");
				 tv_repeat_time.setVisibility(View.GONE);
			}else{
				 tv_repeat_type.setText("重复： 每"+repeat_unti);
				 tv_repeat_freq.setText("重复频率: "+repeat_freq+repeat_unti );
				 tv_repeat_time.setText("重复开始时间:" + TimeTool.getTimeStrYYMMDD(rb.getRepeat_start_time())+"  重复结束时间:" + TimeTool.getTimeStrYYMMDD(rb.getRepeat_end_time()));
			}
			
			String repeat_condition = rb.getRepeat_condition();
			
			if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_WEEK)
			{
				StringBuffer sb = new StringBuffer();
				
				if(repeat_condition.contains("1"))
				{
					sb.append("周一 ");
				}
				
				if(repeat_condition.contains("2"))
				{
					sb.append("周二 ");
				}
				
				if(repeat_condition.contains("3"))
				{
					sb.append("周三 ");
				}
				
				if(repeat_condition.contains("4"))
				{
					sb.append("周四 ");
				}
				
				if(repeat_condition.contains("5"))
				{
					sb.append("周五 ");
				}
				
				if(repeat_condition.contains("6"))
				{
					sb.append("周六 ");
				}
				
				if(repeat_condition.contains("7"))
				{
					sb.append("周日 ");
				}
				
				 t_repeat_codition.setText("重复时间: "+sb.toString());
			}else if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_MONTH){
				if(repeat_condition.equals("1")){
					Calendar car = Calendar.getInstance();
					Date date = new Date(rb.getStart_time());
					car.setTime(date);
					int day = car.get(Calendar.DATE);
					 t_repeat_codition.setText("重复时间: 每月 的 第"+day+"天");
				}else{
					Calendar car = Calendar.getInstance();
					Date date = new Date(rb.getStart_time());
					car.setTime(date);
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
			         t_repeat_codition.setText("重复时间: 每月 的 第"+week_of_month+"个星期的周"+dd);
				}
			}else{
				 t_repeat_codition.setVisibility(View.GONE);
			}
			
			 tv_remind_time.setText("提醒次数: " + rb.getRemind_time() + "次");
			 content.setText(rb.getContent());
			 
			 
			 String [] ss = rb.getContacts().split(":");
			 tv_contact.setText(ss[1]);
			 
			 
			 tv_partners.setMovementMethod(LinkMovementMethod.getInstance());  

				int [] p_ids;
				String [] p_ss; //参与人的名字数组
				List<String[]> phone_list = new ArrayList<String[]>();
				String partner_names = ""; //全部参与人
				String partner_str = rb.getParticipants();
				
				if(partner_str!=null && !partner_str.equals("")){
					String [] partner_ss = partner_str.split(";");
					
					p_ids = new int[partner_ss.length];
					p_ss = new String[partner_ss.length];
					
					StringBuffer sb_name = new StringBuffer();
					
					for(int i =0;i<partner_ss.length;i++)
					{
						String [] c_s = partner_ss[i].split(":");
						p_ids[i] = Integer.valueOf(c_s[0].replace("#", ""));
						p_ss[i] = c_s[1];
						sb_name.append(c_s[1]+"   ");
						
						String [] phons = c_s[2].split(","); //电话号码
						phone_list.add(phons);
					}
					partner_names = sb_name.toString();
					
					SpannableString spannableString = new SpannableString("参与人： "+partner_names);
					
					int index = 4;
					for(int j =0;j<partner_ss.length;j++)
					{
						spannableString.setSpan(new MyclickSpan(p_ids[j],p_ss[j],phone_list.get(j)), index, index+p_ss[j].length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						//设置颜色 
						spannableString.setSpan(new ForegroundColorSpan(Color.RED), index, index+p_ss[j].length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						index=index + p_ss[j].length()+3;
						
						System.out.println("index -->" + index);
					}
					
					tv_partners.setText(spannableString);
				}else{
					tv_partners.setText("参与人： 无");
				}
	    }
	    
	    
	    private  void updateAfterDelete()
	    {
	    	
	    	String old_id = (((String) add_ln.getChildAt(selected_position).findViewById(R.id.button_item).getTag()).split(":"))[3];
	    	loadeDataAndLayoutLeftBar();
            
    		if(add_ln.getChildCount()>0)
    		{
    			View v = add_ln.getChildAt(selected_position);
    			
    			if(v==null)
    			{
    				selected_position = 0;
    			}
    			
	    		String new_id = (((String) add_ln.getChildAt(selected_position).findViewById(R.id.button_item).getTag()).split(":"))[3];
	    			
	    		if(!old_id.equals(new_id))
	    		{
	    			selected_position = 0;
	    		}
	    		
    			String tag = (String)add_ln.getChildAt(selected_position).findViewById(R.id.button_item).getTag();
				String[] str = tag.split(":");
				String name = str[0];
				String contactid = str[3];
				
				if(change == 0)
				{
					layout();
				}else{
					layoutListMode(contactid, name);
				}
				
				updateSelected();
				
    		}else{
    			
    			if(change == 0)
				{
    				layoutWhenNoDate();
    				
				}else{
					remind_info.setAdapter(null);
					contact_data.setVisibility(View.GONE);
				}
    		}
	    }
	    
	    public void activateNextWeek()
	    {
	    	week_gap++;
			if(contactList.size()>0)
			{
				layout();
			}else{
				layoutWhenNoDate();
			}
	    }
	    
	    
	    public  void activatePreWeek()
	    {
	    	week_gap--;
			
			if(contactList.size()>0)
			{
				layout();
			}else{
				layoutWhenNoDate();
			}
	    }
	    
}
