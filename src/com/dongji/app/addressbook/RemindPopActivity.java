package com.dongji.app.addressbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.PopRemindNumberListAdapter;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.TimeTool;

/**
 * 
 * 提醒弹窗
 * 
 * @author Administrator
 *
 */
public class RemindPopActivity extends Activity implements OnClickListener {
	
	int remind_id;
	
	ImageView img_head;
	TextView tv_top_title; 
	ImageView btn_close;
	
	TextView tv_name;
	TextView tv_partener;
	TextView tv_content;
	
	Button btn_call;
	Button btn_sms;
	Button btn_cancle_remind;
	
	Dialog dialog; //选择号码
	ListView lv;
	PopRemindNumberListAdapter popRemindNumberListAdapter;
	List<CallLogInfo> list = new ArrayList<CallLogInfo>();
	
	Button btn_pick_ok;
	Button btn_pick_cancle;
	
	long start_time = -1;  //提醒的开始时间
	long end_time = -1;    //提醒的结束时间
	
	int remind_type;
	int remind_num;  //提醒数值
	
	int remind_time; //体系次数
	int has_remind_time;
	
	String contact_str = "" ; //联系人:   #id#:name:p,p,p
	int c_id = -1;
	String partner_str = "";
	int [] p_ids;
	String partner_names = "";
	
	int repeat_type;
	long repeat_start_time = -1;
	long repeat_end_time = -1;
	
	String repeat_condition;
	
	int repeat_freq;
	
	int aciton_type;  //0:打电话  1:发短信
//	MyDatabaseUtil myDatabaseUtil;
	private int REMIND_GAP_TIME = 5*60*1000 ; //每次提醒的间隔时间，毫秒 ， 5分钟 
	
	String time_filter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remind);
		img_head = (ImageView)findViewById(R.id.img_head);
		tv_top_title = (TextView)findViewById(R.id.tv_top_title);
		
		SharedPreferences sf = getSharedPreferences(SystemSettingActivity.SF_NAME, 0);;
		
		String name = sf.getString(SystemSettingActivity.SF_KEY_SECRETARY_NAME, "小秘书"); //秘书的名字
		String title = name+" 提醒您";
		tv_top_title.setText(Html.fromHtml(title.replace(name, "<font color='#3d8eba'>" + name+ "</font>")));
		
		int sex = sf.getInt(SystemSettingActivity.SF_KEY_SECRETARY_SEX, 1); //秘书性别:  1:女 ;   0:男
		
		if(sex == 0)
		{
			img_head.setImageResource(R.drawable.male_secretary);
		}
		
		btn_close = (ImageView)findViewById(R.id.btn_close); 
		btn_close.setOnClickListener(this);
		
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_partener = (TextView)findViewById(R.id.tv_partener); 
		tv_content = (TextView)findViewById(R.id.tv_content);
		
		btn_call = (Button)findViewById(R.id.btn_call);
		btn_call.setOnClickListener(this);
		
		btn_sms = (Button)findViewById(R.id.btn_sms);
		btn_sms.setOnClickListener(this);
		
		btn_cancle_remind = (Button)findViewById(R.id.btn_cancle_remind);
		btn_cancle_remind.setOnClickListener(this);
		
		remind_id = getIntent().getIntExtra(MyDatabaseUtil.REMIND_ID, -1);
		System.out.println(" RemindPopActivity  remind_id --->" + remind_id);
		query();
	}
	
	
	/**
	 * 
	 * 查询出当前这条提醒的信息
	 * 
	 */
	void query()
	{
		Cursor c = DButil.getInstance(this).queryRemind(remind_id);
		if(c.moveToNext())
		{
			String content = c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT));
			contact_str = c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT));
			String [] contact_ss = contact_str.split(":");
			c_id = Integer.valueOf(contact_ss[0].replace("#", ""));
			String [] pp = contact_ss[2].split(",");
			
			
			
			for(String p : pp)
			{
				if(c_id != MainActivity.MY_CONTACT_ID)
				{
					CallLogInfo ca = new CallLogInfo();
					ca.setId(c_id);
					ca.setmCaller_name(contact_ss[1]);
					ca.setmCaller_number(p);	
					list.add(ca);
				}
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
					String [] phons = c_s[2].split(",");
					
					for(String p : phons)
					{
						CallLogInfo ca = new CallLogInfo();
						ca.setId(p_ids[i]);
						ca.setmCaller_name(c_s[1]);
						ca.setmCaller_number(p);	
						list.add(ca);
					}
					
				}
				partner_names = sb_name.substring(0, sb_name.length()-1);
			}
			
			start_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START));
			end_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_END));
			
			remind_num = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM));
			
			remind_time = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TIME));
			
			has_remind_time = c.getInt(c.getColumnIndex(MyDatabaseUtil.HAS_REMIND_TIME));
			has_remind_time++; //默认为0，提醒一次 + 1
			
			remind_type = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE));
			
			
			//重复
			repeat_type = c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE));
			
			repeat_condition = c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION));
			
		    repeat_freq = c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ));
			
			repeat_start_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME));
			repeat_end_time =  c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME));
			
			time_filter  = c.getString(c.getColumnIndex(MyDatabaseUtil.TIME_FILTER));
			c.close();
			
			System.out.println("总提醒次数: " +remind_time);
			System.out.println("已经提醒的次数 :  " + has_remind_time);
			
			
			DButil.getInstance(this).updateHasRemindNum(remind_id, has_remind_time);
			
			tv_name.setText(contact_ss[1]);
			tv_content.setText(content);
			tv_partener.setText(partner_names.replace(";", "  "));
		}else{
			this.finish();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_close:
			handleBack();
			break;
			
			
		case R.id.btn_call:
			aciton_type =0;
			showDilaog();
			break;
			
			
		case R.id.btn_sms:
			aciton_type =1;
			showDilaog();
			break;
			
			
		case R.id.btn_cancle_remind:
			triggerNext();
			break;

		default:
			break;
		}
	}
	
	
	void handleBack()
	{
		if(has_remind_time < remind_time) //还没完成总提醒次数,五分钟后提醒
		{
			Intent it = new Intent(RemindPopActivity.this, AlarmReceiver.class);
    		it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);		
    		PendingIntent pit = PendingIntent.getBroadcast(RemindPopActivity.this, (int)remind_id, it, 0);
    		AlarmManager amr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
    		amr.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+ REMIND_GAP_TIME),pit);
    		finish();
		}else{
			triggerNext();
		}
	}
	
	
	/**
	 * 
	 * 触发下一次提醒
	 * 
	 */
	void triggerNext(){
		
		DButil.getInstance(this).updateHasRemindNum(remind_id, 0);
		
		long next_time = TimeTool.getNextTime(start_time, remind_type, remind_num, repeat_type, repeat_condition,repeat_freq,repeat_start_time, repeat_end_time,time_filter);
	       
		//还有下次提醒
        if(next_time!=-1)
        {
        	Intent it = new Intent(RemindPopActivity.this, AlarmReceiver.class);
    		it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);		
    		PendingIntent pit = PendingIntent.getBroadcast(RemindPopActivity.this, (int)remind_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
    		AlarmManager amr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
//    		amr.cancel(pit);//先取消 ？
    		amr.set(AlarmManager.RTC_WAKEUP, next_time ,pit);
    		
        }else{
        	//没有下一次的提醒了
        	Intent it = new Intent(RemindPopActivity.this, AlarmReceiver.class);
    		it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);		
    		PendingIntent pit = PendingIntent.getBroadcast(RemindPopActivity.this, (int)remind_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
    		AlarmManager amr = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
    		
    		amr.cancel(pit);//取消
        }
        
        finish();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	@Override
	public void onBackPressed() {
		handleBack();
	}
	
	
	void showDilaog(){
		if(dialog==null)
		{
			dialog = new Dialog(this, R.style.theme_myDialog);
			View view = LayoutInflater.from(this).inflate(R.layout.dialog_remind_number_pick_list, null);
			dialog.setCanceledOnTouchOutside(true);
			dialog.setContentView(view);
			
			lv = (ListView)view.findViewById(R.id.lv);
			LinearLayout.LayoutParams mParams=(LinearLayout.LayoutParams)lv.getLayoutParams();
			DisplayMetrics dm = new DisplayMetrics();  
			getWindowManager().getDefaultDisplay().getMetrics(dm);  
			mParams.height=(int)Math.round(dm.heightPixels*0.7);
			lv.setLayoutParams(mParams);

			popRemindNumberListAdapter = new PopRemindNumberListAdapter(this,list);
			lv.setAdapter(popRemindNumberListAdapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					if(popRemindNumberListAdapter.cb!=null)
					{
						popRemindNumberListAdapter.cb.setChecked(false);
					}
					
					CheckBox check = (CheckBox)view.findViewById(R.id.cb);
					check.setChecked(true);
					
					popRemindNumberListAdapter.cb = check;
					popRemindNumberListAdapter.select_positon = position;
				}
			});
			
			btn_pick_ok = (Button)view.findViewById(R.id.btn_pick_ok);
			btn_pick_cancle = (Button)view.findViewById(R.id.btn_pick_cancle);
			btn_pick_cancle.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
		
		btn_pick_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				CallLogInfo call = null;
				if(popRemindNumberListAdapter.select_positon!=-1)
				{
					call = list.get(popRemindNumberListAdapter.select_positon);
				}
				
				if(call!=null)
				{
					
					if(aciton_type ==0) //打电话
					{
						if (!call.getmCaller_number().equals("") ) {
						
							Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+list.get(popRemindNumberListAdapter.select_positon).getmCaller_number()));
							startActivity(intent);
							
						} else {
							 
							 Toast.makeText(RemindPopActivity.this, "号码为空，请设置电话号码！", Toast.LENGTH_SHORT).show();
							 return ;
						 }
						
					}else{ //发短信
						
						if (!call.getmCaller_number().equals("") ) {
							
							String thread_id = NewMessageActivity.queryThreadIdByNumber(getApplicationContext(),call.getmCaller_number());
							Intent intent = new Intent(getApplicationContext(), NewMessageActivity.class);
							intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
							intent.putExtra(NewMessageActivity.DATA_NUMBER, call.getmCaller_number());

							startActivity(intent);
							
						} else {
							 
							 Toast.makeText(RemindPopActivity.this, "号码为空，请设置电话号码！", Toast.LENGTH_SHORT).show();
							 return ;
						 }	
						
					}
					
				}else{
					Toast.makeText(RemindPopActivity.this, "请选择联系人", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		dialog.show();
	}
	
}
