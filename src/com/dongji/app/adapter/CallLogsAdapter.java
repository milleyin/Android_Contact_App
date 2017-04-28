package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.SystemSettingActivity;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.ConversationBean;
import com.dongji.app.entity.EnContact;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;

/**
 * 
 * 首页通话记录 列表 对应的Adapter
 * 
 * @author Administrator
 * 
 */
public class CallLogsAdapter extends BaseAdapter implements Filterable {

	int type ;  //通话类型:   0:全部    1:未接     2：已接
	public static final int TYPE_ALL = 0;
	public static final int TYPE_REJECT = 1;
	public static final int TYPE_ACCEPT = 2;

	private List<CallLogInfo> list = new ArrayList<CallLogInfo>(); //当前正在显示的通话列表

	//原数据
	private List<CallLogInfo> all_source = new  ArrayList<CallLogInfo>();  //全部   
	private List<CallLogInfo> rejects_source = new ArrayList<CallLogInfo>(); //未接来电 
	private List<CallLogInfo> accepts_source = new ArrayList<CallLogInfo>(); //已接来电
	
	
	private List<CallLogInfo> all = new ArrayList<CallLogInfo> ();      //全部(被过滤过)
	private List<CallLogInfo> rejects = new ArrayList<CallLogInfo>();   //未接来电 (被过滤过)
	private List<CallLogInfo> accepts = new ArrayList<CallLogInfo>();   //已接来电  (被过滤过)
	
	private List<CallLogInfo> source = new ArrayList<CallLogInfo> (); //最近通话的原始数据
	
	private List<CallLogInfo> heatSort_source = new ArrayList<CallLogInfo>();
	private List<CallLogInfo> heatSort = new ArrayList<CallLogInfo>(); //热度排序数据
	
	
	MainActivity mainActivity;
	
	MyDatabaseUtil myDatabaseUtil;

	OnClickListener onClickListener;
	
	public View menu;
	int margin_bottom;
	int original_x;

	View content_layout;
	int target_dis;
	int l_r_dis;
	int l_r;

	String number ;
//	String values;
	
	public static  String SF_NAME = "systemsetting";
	SharedPreferences sf ;
	int sort = 1;
	
	private String filterNum;
	
	List<CallLogInfo> all_list;
	ListView lv;
	int po;
	
//	List<CallLogInfo> all_list;
	
	long start;
	long end;
	
	String [] projection = new String [] {CallLog.Calls._ID,CallLog.Calls.NUMBER,CallLog.Calls.DATE,CallLog.Calls.TYPE,CallLog.Calls.DURATION};
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 0);

			final AbsoluteLayout.LayoutParams ablp = new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.FILL_PARENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0, 0);
			
			switch (msg.what) {
			case 0:
				lp.setMargins(0, 0, 0, margin_bottom);

				System.out.println(" margin_bottom  ---> " + margin_bottom);
				
				menu.setLayoutParams(lp);
				menu.postInvalidate();
				
                   lv.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							lv.smoothScrollToPosition(po);
						}
					}, 100);
				
				break;

			case 1:
				
			    ablp.x = l_r_dis;
			    content_layout.setLayoutParams(ablp);
				content_layout.postInvalidate();

				break;

			case 2:
				
				ablp.x = -l_r_dis;
				content_layout.setLayoutParams(ablp);
				content_layout.postInvalidate();
				
				break;

		    case 3:
				
				if(l_r==1)  //打电话
				{
					mainActivity.triggerCall(number);
//					System.out.println("  打电话    number ---> "+ number);
					
					content_layout.postDelayed(new Runnable() {
						public void run() {
							ablp.x = 0;
							content_layout.setLayoutParams(ablp);
							content_layout.postInvalidate();
						}
					},1000);
					
				}else{ //发短信
					
					mainActivity.triggerSms(number);
					content_layout.postDelayed(new Runnable() {
					public void run() {
						ablp.x = 0;
						content_layout.setLayoutParams(ablp);
						content_layout.postInvalidate();
					}
				},1000);
			  }
				break;
				
		    case 4: //刷新
		    	notifyDataSetChanged();
		    	mainActivity.lv_calls.setSelection(0);
		    	
		    	mainActivity.reSetCallingState();
		    	break;
		    	
		    case 5:
		    	filter();
		    	notifyDataSetChanged();
		    	mainActivity.lv_calls.setSelection(0);
		    	
		    	break;
		    	
		    case 6:
		    	filter();
		    	list = all;
				all_list = all;
		    	notifyDataSetChanged();
		    	
		    	System.out.println(" 通话记录载入 完成   ---> "  +  (System.currentTimeMillis() - start) );
		    	mainActivity.getAllContacts();
		    	break;
		    	
		    	
			default:
				break;
			}

		};
	};

	public CallLogsAdapter(MainActivity mainActivity , OnClickListener onClickListener,int type,ListView lv) {
		this.type = type;
		this.mainActivity = mainActivity;
		this.onClickListener = onClickListener;
		this.lv = lv ;
		
		myDatabaseUtil = DButil.getInstance(mainActivity);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				start = System.currentTimeMillis();
				getCallLogInfo();
		
				handler.sendEmptyMessage(6);
			}
		}).start();
		
		target_dis = mainActivity.getResources().getDimensionPixelSize(R.dimen.target_dis);
	}
	
	//切换显示类型
	public void changeToType (int type)
	{
		
		switch (type) {
		case TYPE_ALL:
			this.list = this.all;
			break;
			
		case TYPE_REJECT:
			changeToReject();
			this.list = this.rejects;
			break;
			
		case TYPE_ACCEPT:
			changeToAccept();
			this.list = this.accepts;
			break;

		default:
			break;
		}
		
		this.all_list = this.list;
		
		notifyDataSetChanged();
	}
		
	private void changeToReject() {

			rejects_source.clear();
			
			Cursor c = null;

			try {
				c = mainActivity.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE, null,CallLog.Calls.DATE + " DESC ");


				int id_column = c.getColumnIndex(CallLog.Calls._ID);
				int number_column = c.getColumnIndex(CallLog.Calls.NUMBER);
				int date_column = c.getColumnIndex(CallLog.Calls.DATE);
				int type_column = c.getColumnIndex(CallLog.Calls.TYPE);
				int duration_column = c.getColumnIndex(CallLog.Calls.DURATION);

				while (c.moveToNext()) {

					// 原始号码
					String orignal_number = c.getString(number_column);
					// 电话号码
					String NUMBER = PhoneNumberTool.cleanse(orignal_number);

					if (!NUMBER.equals("")) {
					
						long id = c.getLong(id_column);

						// 取出数据，构造bean
						CallLogInfo callLogInfo = new CallLogInfo();

						long l_date = c.getLong(date_column);
						callLogInfo.setmCall_date(getHHMM(l_date));
						
						// 通话时间
						long t = Long.parseLong(c.getString(date_column));
						callLogInfo.setDay(TimeTool.getTimeStrYYMMDDNoToday(t));

						// 通话类型
						int TYPE = c.getInt(type_column);

						String call_type = "";
						if (TYPE == CallLog.Calls.INCOMING_TYPE) {
							call_type = "呼入";
						} else if (TYPE == CallLog.Calls.OUTGOING_TYPE) {
							call_type = "呼出";
						} else if (TYPE == CallLog.Calls.MISSED_TYPE) {
							call_type = "未接";
						}

						// 设置数据
						callLogInfo.setId(id);

						callLogInfo.setType(TYPE);

						callLogInfo.setmCaller_number(orignal_number);
						callLogInfo.setOrignal_number(orignal_number);

						callLogInfo.setmCall_duration_long(c.getLong(duration_column));
						callLogInfo.setmCall_type(call_type);
						callLogInfo.setTotal(1);

						//查询通话记录对应的联系人相关信息
						String [] data = PhoneNumberTool.getContactInfo(mainActivity, orignal_number);
						String name ="";
						int contact_id = -1;
						if(data[0]!=null)
						{
							name = data[0];
						}
						
						if(data[2] !=null)
						{
							contact_id = Integer.valueOf(data[2]);
						}
						
						if(data[3] !=null)
						{
							String key = data[3];
							
							key = key.replace(" ", "");
							String array = "";
							boolean b = false;
							String capPingYin = "";

							for (int i = 0; i < key.length(); i++) {
								char cr = key.charAt(i);

								if (cr > 256) {// 汉字符号
									b = false;
								} else {
									array += cr;
									if (!b) {
										capPingYin += cr;
										b = true;
									}
								}
							}
							callLogInfo.setSork_key(key.replace(" ", "").toLowerCase());
							callLogInfo.setName_pinyin(array.replace(" ", "").toLowerCase());
							callLogInfo.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());
						}
						
						callLogInfo.setmCaller_name(name);
						callLogInfo.setPhoto_id(data[1]);
						callLogInfo.setContact_id(contact_id);

						rejects_source.add(callLogInfo);
					} 
				}

				c.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			
			filterReject();
	}
		
	void filterReject() {

		// 未接来电
		rejects.clear();

//		System.out.println(" rejects_source size  " + rejects_source.size());

		for (CallLogInfo c : rejects_source) {
			String c_number = PhoneNumberTool.cleanse(c.getmCaller_number());
			boolean isMath = MainActivity.checkIsEnContactByNumber(c_number);

//			Long id = c.getId();
//			boolean isInterceptPhone = false;
//
//			if (MainActivity.INTERCEPTPHONE.size() > 0) {
//
//				for (Long call_id : mainActivity.INTERCEPTPHONE) {
//
//					if (call_id.equals(id)) {
//
//						isInterceptPhone = true;
//						break;
//					}
//				}
//			}

			if (isMath) { // 被过滤

			} else {
				rejects.add(c);
			}
		}
	}
	
	public void changeToAccept() {
		accepts_source.clear();
		
		// 所有已接来电
		Cursor accepts_cur = mainActivity.getContentResolver().query(CallLog.Calls.CONTENT_URI,
				projection, CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE, null, null);
		
		int id_column = accepts_cur.getColumnIndex(CallLog.Calls._ID);
		int number_column = accepts_cur.getColumnIndex(CallLog.Calls.NUMBER);
		int date_column = accepts_cur.getColumnIndex(CallLog.Calls.DATE);
		int type_column = accepts_cur.getColumnIndex(CallLog.Calls.TYPE);
		int duration_column = accepts_cur.getColumnIndex(CallLog.Calls.DURATION);
		
		while (accepts_cur.moveToNext()) {

			// 原始号码
			String orignal_number = accepts_cur.getString(number_column);
			// 电话号码
			String NUMBER = orignal_number.replace("(", "").replace(") ", "")
					.replace("-", "").replace(" ", "");

			long id = accepts_cur.getLong(id_column);

			CallLogInfo callLogInfo = new CallLogInfo();

			// 通话时间
			Date date = new Date(Long.parseLong(accepts_cur
					.getString(date_column)));

			SimpleDateFormat time_sfd = new SimpleDateFormat("HH:mm");
			String time = time_sfd.format(date);
			//
			long t = Long.parseLong(accepts_cur.getString(date_column));
			callLogInfo.setDay(TimeTool.getTimeStrYYMMDDNoToday(t));

			// 通话类型
			int TYPE = accepts_cur.getInt(type_column);

			// 通话时长
			String DURATION = "";
			if (myDatabaseUtil.queryCallLogId().contains(String.valueOf(id))) {
				DURATION = "响铃一声！";
			} else {
				
				long old = Long.parseLong(accepts_cur.getString(duration_column));
				
				long hour = old / (60 * 60);
				long min = (old % (60 * 60)) / (60);
				long second = (old % (60));
				
				if ( old == 0 ) {
					
					DURATION = "未接通";
					
				} else {
					
					if (hour != 0) {
						DURATION = hour + "小时" + min + "分钟" + second + "秒";
					} else if (min != 0) {
						DURATION = min + "分钟" + second + "秒";
					} else if (second != 0) {
						DURATION = second + "秒";
					}
				}
			}

			String call_type = "";
			if (TYPE == CallLog.Calls.INCOMING_TYPE) {
				call_type = "呼入";
			} else if (TYPE == CallLog.Calls.OUTGOING_TYPE) {
				call_type = "呼出";
			} else if (TYPE == CallLog.Calls.MISSED_TYPE) {
				call_type = "未接";
			}

			// 设置数据
			callLogInfo.setId(id);

			callLogInfo.setType(TYPE);
			callLogInfo.setmCaller_number(NUMBER);
			callLogInfo.setOrignal_number(orignal_number);
			callLogInfo.setmCall_date(time);
			callLogInfo.setmCall_duration(DURATION);
			callLogInfo.setmCall_type(call_type);

			accepts_source.add(callLogInfo);
		}
		accepts_cur.close();
		
		filterAccepts();
	}
	
	void filterAccepts()
	{
		//已接来电
				accepts.clear();
				
				for(CallLogInfo c:accepts_source)
				{
					  String c_number = c.getmCaller_number();
					   boolean isMath = MainActivity.checkIsEnContactByNumber(c_number);
						
						if(isMath) { //被过滤
							
						 }else{
							 accepts.add(c);
						 }
				  }
	}
	
	//删除此条通话记录
	public void deleteById(int type , final long  call_id)
	{
		
		CallLogInfo deleteCall = null;
		
		//全部
		for(CallLogInfo c:all)
		{
			if(c.getId()==call_id)
			{
				deleteCall = c;
				break;
			}
		}
		all.remove(deleteCall);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				CallLogInfo deleteCall = null;
				
				//全部
				for(CallLogInfo c:all_source)
				{
					if(c.getId()==call_id)
					{
						deleteCall = c;
						break;
					}
				}
				all_source.remove(deleteCall);
			}
		}).start();
		
		
		if(type == TYPE_REJECT)
		{
			//未接
			for(CallLogInfo c:rejects)
			{
				if(c.getId()==call_id)
				{
					deleteCall = c;
					break;
				}
			}
			rejects.remove(deleteCall);
		}
		
       
		if(type == TYPE_ACCEPT)
		{
			 //已接
			for(CallLogInfo c:accepts)
			{
				if(c.getId()==call_id)
				{
					deleteCall = c;
					break;
				}
			}
			accepts.remove(deleteCall);
		}
	}
	
	
	//删除此号码全部的通话记录
	public void deleteAllByNumber(final String number)
	{
		
		List<CallLogInfo> deleteCalls = new ArrayList<CallLogInfo>();
		for(CallLogInfo c:all)
		{
			if(c.getOrignal_number().equals(number))
			{
				deleteCalls.add(c);
			}
		}
		all.removeAll(deleteCalls);
		
		
        new Thread(new Runnable() {
 			
			@Override
			public void run() {
				List<CallLogInfo> deleteCalls = new ArrayList<CallLogInfo>();
				
				//全部
				for(CallLogInfo c:all_source)
				{
					if(c.getOrignal_number().equals(number))
					{
						deleteCalls.add(c);
						break;
					}
				}
				all_source.remove(deleteCalls);
			}
		}).start();
		
        if( type == TYPE_REJECT )
        {
        	List<CallLogInfo> deleteRejectCalls = new ArrayList<CallLogInfo>();
    		for(CallLogInfo c:rejects)
    		{
    			if(c.getOrignal_number().equals(number))
    			{
    				deleteRejectCalls.add(c);
    			}
    		}
    		rejects.removeAll(deleteRejectCalls);
    		
        }
        
        if( type == TYPE_ACCEPT)
        {
        	
        	List<CallLogInfo> deleteAcceptsCalls = new ArrayList<CallLogInfo>();
    		for(CallLogInfo c:accepts)
    		{
    			if(c.getOrignal_number().equals(number))
    			{
    				deleteAcceptsCalls.add(c);
    			}
    		}
    		accepts.removeAll(deleteAcceptsCalls);
    		
        }
	}
	
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		System.out.println();
		
		final CallLogInfo callInfo = (CallLogInfo) list.get(position);

		final ViewHolder holder;
		if (convertView == null) {
		convertView = LayoutInflater.from(mainActivity).inflate(R.layout.top_10_callls_item, null);
		holder = new ViewHolder();
		
		holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		holder.missedNum = (TextView) convertView.findViewById(R.id.missedNum);
		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.area = (TextView) convertView.findViewById(R.id.area);
		holder.date = (TextView) convertView.findViewById(R.id.date);
		holder.type = (ImageView) convertView.findViewById(R.id.type);
		holder.duration = (TextView) convertView.findViewById(R.id.duration);
		holder.day = (TextView) convertView.findViewById(R.id.day);
		
		holder.menu_call = (Button)convertView.findViewById(R.id.menu_call);
		holder.menu_sms_detail = (Button)convertView.findViewById(R.id.menu_sms_detail);
		holder.menu_contact_detail = (Button)convertView.findViewById(R.id.menu_contact_detail);
		holder.menu_delete = (Button)convertView.findViewById(R.id.menu_delete);
		holder.menu_remind = (Button)convertView.findViewById(R.id.menu_remind);
		holder.menu_add_to = (Button)convertView.findViewById(R.id.menu_add_to);
				
		holder. content_layout =  (LinearLayout) convertView.findViewById(R.id.content_layout);
		holder.main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
		
		holder.menu = (LinearLayout)convertView.findViewById(R.id.menu);
		
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder) convertView.getTag();
		}
		
		holder.menu.setVisibility(View.GONE);//隐藏
		
		final LinearLayout content_layout = holder.content_layout;
		LinearLayout main_layout = holder.main_layout;
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						original_x = (int) event.getX();
//						System.out.println(" original_x --->  " + original_x);
						break;

					case MotionEvent.ACTION_UP:
						int destain = (int) event.getX() - original_x;
//						System.out.println(" destain ---> " + destain);

						if (destain > 60) // 向右滑
						{
							l_r = 1;
							scrollLeftOrRight(content_layout);
							System.out.println("  --- 向右滑 ");
							number = callInfo.getmCaller_number();
							
						} else if (destain < -60) // 向左滑
						{
							l_r = 2;
							scrollLeftOrRight(content_layout);
							System.out.println("  ---- 向左滑 ");
							number = callInfo.getmCaller_number();
							
						} else { // 点击事件
								popUpMenu(holder.menu);
								po= position;
						}
						break;

					default:
						break;
					}

					return true;
				}
			});
	
		
		    if(!callInfo.isNeedRequery())
		    {
		    	holder.tvNick.setText(callInfo.getmCaller_name());
				
				if (callInfo.getPhoto_id() == null)
				{
					holder.ivAvatar.setImageResource(R.drawable.default_contact);
				}
				else{
					Bitmap photo_bmp = getPhoto(callInfo.getPhoto_id());
					
					if(photo_bmp!=null)
					{
						holder.ivAvatar.setImageBitmap(photo_bmp);
					}else{
						holder.ivAvatar.setImageResource(R.drawable.default_contact);
					}
				}
				
				if(callInfo.getContact_id()== -1 || Integer.valueOf(callInfo.getContact_id())==MainActivity.MY_CONTACT_ID) //无联系人 , 或者为机主， 不显示 提醒菜单项
				{
					convertView.findViewById(R.id.l_remind).setVisibility(View.GONE);
					
				}else{
					convertView.findViewById(R.id.l_remind).setVisibility(View.VISIBLE);
				}
				
		    }else{
		    	
		    	String [] data = PhoneNumberTool.getContactInfo(mainActivity, callInfo.getmCaller_number());
				String name ="";
				String photo_id = null;
				
				if(data[2]==null) //无联系人
				{
					convertView.findViewById(R.id.l_remind).setVisibility(View.GONE);
					
				}else{
					
					name = data[0];
					photo_id = data[1];
					String contact_id = data[2];
					callInfo.setContact_id(Integer.valueOf(contact_id));
					
					if(Integer.valueOf(contact_id)==MainActivity.MY_CONTACT_ID)
					{
						convertView.findViewById(R.id.l_remind).setVisibility(View.GONE);
					}else{
						convertView.findViewById(R.id.l_remind).setVisibility(View.VISIBLE);
					}
				}
				
				
				if (photo_id == null)
				{
					holder.ivAvatar.setImageResource(R.drawable.default_contact);
				}
				else{
					Bitmap photo_bmp = getPhoto(photo_id);
					
					if(photo_bmp!=null)
					{
						holder.ivAvatar.setImageBitmap(photo_bmp);
					}else{
						holder.ivAvatar.setImageResource(R.drawable.default_contact);
					}
				}
			
				holder.tvNick.setText(name);
	            callInfo.setmCaller_name(name);
	            callInfo.setPhoto_id(photo_id);
	            callInfo.setNeedRequery(false);   
		    }
			
			
		if(type==TYPE_ALL) //全部通话记录才显示总未接次数
		{
			//根据未接次数，显示不同的颜色
			if(callInfo.getMissedNum() == 0){
				holder.missedNum.setText("");
			}else{
				int num = callInfo.getMissedNum();
				if(num>0 && num<=3)
				{
					holder.missedNum.setTextColor(mainActivity.getResources().getColor(R.color.num_color_1));
				}else if (num>3 && num<6){
					holder.missedNum.setTextColor(mainActivity.getResources().getColor(R.color.num_color_3));
				}else{
					holder.missedNum.setTextColor(mainActivity.getResources().getColor(R.color.num_color_5));
				}
				
				holder.missedNum.setText("("+String.valueOf(callInfo.getMissedNum())+")");
				System.out.println(" 未接来电次数  --->" +  callInfo.getMissedNum());
			}
		}
		
		holder.number.setText(callInfo.getmCaller_number());
	
		holder.date.setText(callInfo.getmCall_date());
		holder.day.setText(callInfo.getDay());
		
		Resources resource = (Resources) mainActivity.getResources();
		ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.my_color);
		
		
		if(callInfo.getmCall_duration()==null)
		{
			// 通话时长
			String duration_str = "";
			if (myDatabaseUtil.queryCallLogId().contains(String.valueOf(callInfo.getId()))) {
				duration_str = "响铃一声！";
			} else {
				long old = callInfo.getmCall_duration_long();
				
				long hour = old / (60 * 60);
				long min = (old % (60 * 60)) / (60);
				long second = (old % (60));
				
				if ( old == 0 ) {
					duration_str = "未接通";
				} else {
					
					if (hour != 0) {
						duration_str = hour + "小时" + min + "分钟" + second + "秒";
					} else if (min != 0) {
						duration_str = min + "分钟" + second + "秒";
					} else if (second != 0) {
						duration_str = second + "秒";
					}
				}
			}
			callInfo.setmCall_duration(duration_str);
		}
		
		if ("未接通".equals(callInfo.getmCall_duration())) {
			holder.duration.setTextColor(csl);
		}else{
			holder.duration.setTextColor(mainActivity.getResources().getColor(R.color.text_color_list_item));
		}

		String type = callInfo.getmCall_type();
		if(	type.equals("呼入"))
		{
			holder.type.setImageResource(R.drawable.dia_in_icon);
		}else if ( type.equals("呼出"))
		{
			holder.type.setImageResource(R.drawable.dia_out_icon);
		}else if(type.equals("未接")){
			holder.type.setImageResource(R.drawable.dia_in_reject);
		}
		
		holder.duration.setText(callInfo.getmCall_duration());
		
		holder.menu_call.setTag(callInfo.getmCaller_number());
		holder.menu_call.setOnClickListener(onClickListener);
		
		holder.menu_sms_detail.setTag(callInfo.getmCaller_number());
		holder.menu_sms_detail.setOnClickListener(onClickListener);

		holder.menu_contact_detail.setTag(callInfo.getmCaller_number());
		holder.menu_contact_detail.setOnClickListener(onClickListener);
		
		if (callInfo.getmCaller_name()!=null &&  ! "".equals(callInfo.getmCaller_name())  )
			holder.menu_delete.setTag(callInfo.getId()+":"+callInfo.getOrignal_number()+":"+callInfo.getSeconde_id()+":"+callInfo.getmCaller_name());
		else 
			holder.menu_delete.setTag(callInfo.getId()+":"+callInfo.getOrignal_number()+":"+callInfo.getSeconde_id()+":"+callInfo.getOrignal_number());
		
		holder.menu_delete.setOnClickListener(onClickListener);
		
		holder.menu_remind.setTag(callInfo.getmCaller_number());
		holder.menu_remind.setOnClickListener(onClickListener);
		
		holder.menu_add_to.setTag(callInfo.getmCaller_number()+":"+callInfo.getContact_id());
		holder.menu_add_to.setOnClickListener(onClickListener);
				
		
		if(MainActivity.phoneDatabaseUtil!=null && callInfo.getmCaller_area()==null)
		{
			try {
				String newNum = PhoneNumberTool.cleanse(callInfo.getmCaller_number());
				if(newNum.length()>7)
				{
					String num = newNum.substring(0, 7);
					callInfo.setmCaller_area(MainActivity.phoneDatabaseUtil.fetch(num));
				}else{
					callInfo.setmCaller_area("未知归属地");
				}
			} catch (Exception e) {
				e.printStackTrace();
				callInfo.setmCaller_area("未知归属地");
			}
		}
		 
		holder.area.setText(callInfo.getmCaller_area());
		
		if(holder.duration.getText().toString().equals("响铃一声！")){
			
			holder.duration.setClickable(true);
			holder.duration.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
					builder.setMessage("此电话可能为欺诈电话，请勿回拨！");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
		                 
		                @Override 
		                public void onClick(DialogInterface dialog, int which) { 
		                	
		                	dialog.cancel();
		                	
		                } 
		            }).create().show();
				}
			});
		}
	
		return convertView;
	}
	
	public void collapse() {
		if (menu != null) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
			menu.setVisibility(View.GONE);
		}
	}

	private void popUpMenu(View view) {
//		System.out.println("pop menu --->");
		if (menu != null && menu != view) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
			menu.setVisibility(View.GONE);
		}

		menu = view;
		final int height = menu.getHeight();
		margin_bottom = ((LinearLayout.LayoutParams) menu.getLayoutParams()).bottomMargin;

		if(margin_bottom<0)
		{
			menu.setVisibility(View.VISIBLE);
		}
		
//		System.out.println(" margin_bottom ---> " + margin_bottom);
//		System.out.println(" height ---> " + height);

		new Thread(new Runnable() {

			@Override
			public void run() {

				if (margin_bottom < -5) {
					
					while (margin_bottom < -5) {
						margin_bottom += 5;
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
					
				} else {

					while (margin_bottom > -height) {
						margin_bottom -= 5;
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
					
				}

			}
		}).start();
	}

	private void scrollLeftOrRight(View v) {
		l_r_dis = 0;
		content_layout = v;

		System.out.println("l_r  --->" + l_r);
		
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (l_r_dis < target_dis) {
					
					l_r_dis += 3;
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(l_r);
				}
				
				try {
					Thread.sleep(400);
				} catch (Exception e) {
				}
			
				handler.sendEmptyMessage(3);
			}
		}).start();
	}

	class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView missedNum;
		TextView number;
		TextView area;
		TextView date;
		TextView day;
		ImageView type;
		TextView duration;
		
		Button menu_call;
		Button menu_sms_detail;
		Button menu_contact_detail;
		Button menu_delete;
		Button menu_remind;
		Button menu_add_to;
		
		LinearLayout content_layout;
		LinearLayout main_layout;
		
		LinearLayout menu;
	}

//	private Bitmap getPhoto(String name) {
//		// 通话记录
//		Bitmap contactPhoto = null;
//		try {
//			if (name != null) {
//				
//				Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"
//								+ name);
//				Cursor cursor2 = mainActivity.getContentResolver().query(uri,
//						new String[] { "photo_id" }, null, null, null);
//				if (cursor2.moveToFirst()) {
//					String phot_IDo = cursor2.getString(0);
//					if (phot_IDo != null) {
//						Cursor cursor3 = mainActivity.getContentResolver()
//								.query(ContactsContract.Data.CONTENT_URI,
//										new String[] { "data15" },
//										"ContactsContract.Data._ID=" + phot_IDo,
//										null, null);
//						if (cursor3.moveToFirst()) {
//							byte[] photoicon = cursor3.getBlob(0);
//							ByteArrayInputStream inputStream = new ByteArrayInputStream(
//									photoicon);
//							contactPhoto = BitmapFactory.decodeStream(inputStream);
//						}
//						cursor3.close();
//					}
//				}
//				
//				cursor2.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		// cursor.close();
//		// cursor.close();
//		return contactPhoto;
//	}

	private Bitmap getPhoto(String photo_id)
	{
		Bitmap contactPhoto =null;
		Cursor cursor3 = mainActivity.getContentResolver()
				.query(ContactsContract.Data.CONTENT_URI,
						new String[] { "data15" },
						"ContactsContract.Data._ID=" + photo_id,
						null, null);
		if (cursor3.moveToFirst()) {
			byte[] photoicon = cursor3.getBlob(0);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					photoicon);
			contactPhoto = BitmapFactory.decodeStream(inputStream);
		}
		cursor3.close();
		
		return contactPhoto;
	}
	
//	private String getRingOneCall(){
//		
//		String id = "";
//		
//		RingOneCallDBHelper ringOneCallDBHelper = new RingOneCallDBHelper(mainActivity);
//		
//		SQLiteDatabase db = ringOneCallDBHelper.getReadableDatabase();
//		
//		Cursor cursor = db.query(ringOneCallDBHelper.table, new String[]{ringOneCallDBHelper.CALLLOG_ID}, null, null, null, null, null);
//		
//		if(cursor.moveToFirst()){
//			
//			do{
//				
//				String c_id = cursor.getString(cursor.getColumnIndex(ringOneCallDBHelper.CALLLOG_ID));
//				
//				if(c_id != null)
//					id=c_id+",";
//				
//			}while(cursor.moveToNext());
//		}
//		cursor.close();
//		db.close();
//		
//		return id;
//	}
	
	private void getCallLogInfo() {

		//清空
		source.clear();
		
		ContentResolver contentResolver = mainActivity.getContentResolver();
		
		sf = mainActivity.getSharedPreferences(SF_NAME, 0);
		sort = sf.getInt(SystemSettingActivity.SF_KEY_COLLOG_SORT, 1);

		Cursor c = null;

		try {
			c = mainActivity.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null,CallLog.Calls.DATE + " DESC ");

			Map<String, CallLogInfo> map = new HashMap<String, CallLogInfo>();

			int id_column = c.getColumnIndex(CallLog.Calls._ID);
			int number_column = c.getColumnIndex(CallLog.Calls.NUMBER);
			int date_column = c.getColumnIndex(CallLog.Calls.DATE);
			int type_column = c.getColumnIndex(CallLog.Calls.TYPE);
			int duration_column = c.getColumnIndex(CallLog.Calls.DURATION);
			int new_column = c.getColumnIndex(CallLog.Calls.NEW);

			while (c.moveToNext()) {

				// 原始号码
				String orignal_number = c.getString(number_column);
				// 电话号码
				String NUMBER = PhoneNumberTool.cleanse(orignal_number);


				if (!map.containsKey(NUMBER) && !NUMBER.equals("")) {
					
				
					long id = c.getLong(id_column);

					// 取出数据，构造bean
					CallLogInfo callLogInfo = new CallLogInfo();

					long l_date = c.getLong(date_column);
					callLogInfo.setmCall_date(getHHMM(l_date));
					
					// 通话时间
					long t = Long.parseLong(c.getString(date_column));
					callLogInfo.setDay(TimeTool.getTimeStrYYMMDDNoToday(t));

					// 通话类型
					int TYPE = c.getInt(type_column);


					String call_type = "";
					if (TYPE == CallLog.Calls.INCOMING_TYPE) {
						call_type = "呼入";
					} else if (TYPE == CallLog.Calls.OUTGOING_TYPE) {
						call_type = "呼出";
					} else if (TYPE == CallLog.Calls.MISSED_TYPE) {
						call_type = "未接";
					} else {  //有可能type为 4  ， 神奇了
						call_type = "呼入";
					}
					

					System.out.println("  NUMBER  ---> " + NUMBER);
					System.out.println("  TYPE  --->" + TYPE);
					
					
					// 设置数据
					callLogInfo.setId(id);

					callLogInfo.setType(TYPE);

					callLogInfo.setmCaller_number(orignal_number);
					callLogInfo.setOrignal_number(orignal_number);

					callLogInfo.setmCall_duration_long(c.getLong(duration_column));
					callLogInfo.setmCall_type(call_type);
					callLogInfo.setTotal(1);
					
//					callLogInfo.setmCaller_name(c.getString(name_column));

					//未接电话次数
					if(TYPE == CallLog.Calls.MISSED_TYPE)
					{
						Cursor miss_cur =  contentResolver.query(CallLog.Calls.CONTENT_URI, new String [] {CallLog.Calls._ID}, CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.NEW + " = 1", null	, null );
						int miss_num = miss_cur.getCount();
						miss_cur.close();
							
						callLogInfo.setMissedNum(miss_num);
					}
						
					//电话总数 
					Cursor total_cur =  contentResolver.query(CallLog.Calls.CONTENT_URI, new String [] {CallLog.Calls._ID}, CallLog.Calls.NUMBER + " = " + NUMBER , null , null );
					int totla_num = total_cur.getCount();
					total_cur.close();
					callLogInfo.setTotal(totla_num);

					//查询通话记录对应的联系人相关信息
					String [] data = PhoneNumberTool.getContactInfo(mainActivity, orignal_number);
					String name ="";
					int contact_id = -1;
					if(data[0]!=null)
					{
						name = data[0];
					}
					
					if(data[2] !=null)
					{
						contact_id = Integer.valueOf(data[2]);
					}
					
					if(data[3] !=null)
					{
						String key = data[3];
						
						key = key.replace(" ", "");
						String array = "";
						boolean b = false;
						String capPingYin = "";

						for (int i = 0; i < key.length(); i++) {
							char cr = key.charAt(i);

							if (cr > 256) {// 汉字符号
								b = false;
							} else {
								array += cr;
								if (!b) {
									capPingYin += cr;
									b = true;
								}
							}
						}
						callLogInfo.setSork_key(key.replace(" ", "").toLowerCase());
						callLogInfo.setName_pinyin(array.replace(" ", "").toLowerCase());
						callLogInfo.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());
						
					}
					callLogInfo.setmCaller_name(name);
					callLogInfo.setPhoto_id(data[1]);
					callLogInfo.setContact_id(contact_id);
					
					
					map.put(NUMBER, callLogInfo);

					source.add(callLogInfo);
					
				} 
				
			}

			c.close();

			

			if (sort == 0) { // 热度
				
				List<CallLogInfo> list = new ArrayList<CallLogInfo>();

				for (int i = 0; i < source.size(); i++) {
					CallLogInfo ci = source.get(i);

					if (list.size() == 0) {
						list.add(0, ci);
					} else {
						CallLogInfo cc = null;
						int index = -1;
						for (int j = 0; j < list.size(); j++) {
							cc = list.get(j);
							if (ci.getTotal() > cc.getTotal()) {
								index = j;
								break;
							}
							if (j == list.size() - 1) {
								index = j;
							}
						}
						list.add(index, ci);
					}
				}

				heatSort_source = list;
				
				all_source = heatSort_source;
			} else { // 时间
				all_source = source;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//获取最新的那条通话记录
	public boolean getNewestCalllog()
	{
		Cursor c = mainActivity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null , null, CallLog.Calls.DATE +" DESC");
		
		int id_column = c.getColumnIndex(CallLog.Calls._ID);
		int number_column = c.getColumnIndex(CallLog.Calls.NUMBER);
		int date_column = c.getColumnIndex(CallLog.Calls.DATE);
		int type_column = c.getColumnIndex(CallLog.Calls.TYPE);
		int duration_column = c.getColumnIndex(CallLog.Calls.DURATION);
//		int new_column = c.getColumnIndex(CallLog.Calls.NEW);
		
		if(c.moveToNext())
		{
			CallLogInfo newest_Call = new CallLogInfo();
			
			//原始号码
			String orignal_number = c.getString(number_column);
			
		    //电话号码
			String NUMBER = orignal_number.replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
			long id = c.getLong(id_column);
			
			//通话类型
			int TYPE = c.getInt(type_column);
			
			Date date = new Date(Long.parseLong(c.getString(date_column)));
			SimpleDateFormat time_sfd = new SimpleDateFormat("HH:mm");
			String time = time_sfd.format(date);
			
			
			// 通话时长
			String DURATION = "";
			if(myDatabaseUtil.queryCallLogId().contains(String.valueOf(id))){
				
				DURATION = "响铃一声！";
				
			}else{
				
				long old = Long.parseLong(c.getString(duration_column));
				long hour = old / (60 * 60);
				long min = (old % (60 * 60)) / (60);
				long second = (old % (60));
				
				if ( old == 0 ) {
					
					DURATION = "未接通";
					
				} else {
					
					if (hour != 0) {
						DURATION = hour + "小时" + min + "分钟" + second + "秒";
					} else if (min != 0) {
						DURATION = min + "分钟" + second + "秒";
					} else if (second != 0) {
						DURATION = second + "秒";
					}

				}
			}
			
			String call_type = "";
			if (TYPE == CallLog.Calls.INCOMING_TYPE) {
				call_type = "呼入";
			} else if (TYPE == CallLog.Calls.OUTGOING_TYPE) {
				call_type = "呼出";
			} else if (TYPE == CallLog.Calls.MISSED_TYPE) {
				call_type = "未接";
			}
			
			newest_Call.setId(id);
			newest_Call.setType(TYPE);
//			callLogInfo.setmCaller_name(CACHED_NAME);
			newest_Call.setmCaller_number(NUMBER);
			newest_Call.setOrignal_number(orignal_number);
			
			newest_Call.setmCall_date(time);
			newest_Call.setmCall_duration(DURATION);
			newest_Call.setmCall_type(call_type);
			
			//查询通话记录对应的联系人相关信息
			String [] data = PhoneNumberTool.getContactInfo(mainActivity, orignal_number);
			String name ="";
			int contact_id = -1;
			if(data[0]!=null)
			{
				name = data[0];
			}
			
			if(data[2] !=null)
			{
				contact_id = Integer.valueOf(data[2]);
			}
			
			if(data[3] !=null)
			{
				String key = data[3];
				
				key = key.replace(" ", "");
				String array = "";
				boolean b = false;
				String capPingYin = "";

				for (int i = 0; i < key.length(); i++) {
					char cr = key.charAt(i);

					if (cr > 256) {// 汉字符号
						b = false;
					} else {
						array += cr;
						if (!b) {
							capPingYin += cr;
							b = true;
						}
					}
				}
				newest_Call.setSork_key(key.replace(" ", "").toLowerCase());
				newest_Call.setName_pinyin(array.replace(" ", "").toLowerCase());
				newest_Call.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());
			}
			
			newest_Call.setmCaller_name(name);
			newest_Call.setPhoto_id(data[1]);
			newest_Call.setContact_id(contact_id);
			
			
			c.close();
			
			System.out.println(" newest_NUMBER  ---> " + NUMBER);
			
			
			int position = -1;
			boolean isHave = false;
			
			for(int i = 0 ;i<source.size();i++)
			{
				CallLogInfo call = source.get(i);
				
				if(call.getId()==newest_Call.getId())
				{
					isHave = true;
					break;
					
				}else{
					
//					System.out.println(" call.getmCaller_number() ---> " + call.getmCaller_number());
					
					if(call.getmCaller_number().equals(newest_Call.getmCaller_number()))
					{
						position = i;
						System.out.println(" find!!!!  position :" + position);
						break;
					}
				}
			}
			
//			System.out.println(" isHave ---> "+ isHave +" position ---> " + position);
//			System.out.println("  222222   ----->  all_original.size() :"  + all_original.size());
			
			
			if(!isHave && position!=-1)
			{
				CallLogInfo old_call = source.get(position);
				
				all_source.remove(position);
				
				if(newest_Call.getType() == CallLog.Calls.MISSED_TYPE)
				{
					int miss_mun = old_call.getMissedNum() + 1;
					newest_Call.setMissedNum(miss_mun);
				}
				newest_Call.setSeconde_id(old_call.getId());
				
				all_source.add(0, newest_Call);
				filter();
				return true;
				
			}else if (!isHave && position ==-1){
				all_source.add(0, newest_Call);
				filter();
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
//	 private String getDistanceTime(long time2) {
//		Date now = new Date(); 
//       String rs="";     
//		SimpleDateFormat format =   new SimpleDateFormat( "MM月dd日 HH:mm" );
//		String d = format.format(time2);
//		Date date = null;
//		  try {
//		   date = format.parse(d);
//		  } catch (ParseException e1) {
//		   e1.printStackTrace();
//		  }  
//		  if (now.getDate()-date.getDate()==0) {
////		   DateFormat df2 = new SimpleDateFormat("HH:mm");
////		      rs="今天  "+df2.format(time2);
//		  } else if (now.getDate()-date.getDate()==1) {
//
//			  DateFormat df2 = new SimpleDateFormat("HH:mm");
//			  rs="昨天  ";
//		 
//		  } else {
//			  
//		   DateFormat df2 = new SimpleDateFormat("MM月dd日");
//		   rs=df2.format(time2);
//		}
//		  return rs;
//	}
	
	public List<CallLogInfo> getAll()
	{
		return all;
	}

	@Override
	public Filter getFilter() {
		
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,FilterResults results) {
				
				list = (ArrayList<CallLogInfo>) results.values;
				
				if (results.count > 0) {
					
					notifyDataSetChanged();
					
				} else {
					
					notifyDataSetInvalidated();
					
				}
				
				if( constraint.equals("") ){
					
					mainActivity.search_img.setVisibility(View.VISIBLE);
					mainActivity.delete_search_info.setVisibility(View.GONE);
					
				}else{
					
					mainActivity.search_img.setVisibility(View.GONE);
					mainActivity.delete_search_info.setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence s) {
				
				filterNum = s.toString();
				
				FilterResults results = new FilterResults();
				
				ArrayList<CallLogInfo> result = new ArrayList<CallLogInfo>();
				
				if (all_list != null && all_list.size() != 0) {
					
						for (int i = 0; i < all_list.size(); i++) {
							
							if (null == filterNum) {
								
								result.add(all_list.get(i));
								
							}else{

								if(!isNumeric(s.toString())){
									
									if (all_list.get(i).getmCaller_name() != null && !all_list.get(i).getmCaller_name().equals("")){

										if (all_list.get(i).getSork_key().contains(s.toString().toLowerCase())) 
											result.add(all_list.get(i));
										else if(all_list.get(i).getName_pinyin_cap().contains(s.toString().toLowerCase())) 
											result.add(all_list.get(i));
										else if(all_list.get(i).getName_pinyin().contains(s.toString().toLowerCase())) 
											result.add(all_list.get(i));
										else if(all_list.get(i).getmCaller_name().contains(s)){
												result.add(all_list.get(i));
										}
										
									}
									
								}else{
									
									if(all_list.get(i).getmCaller_number().contains(s)){
										result.add(all_list.get(i));
									}
								}
							}
					}
						
				}
				results.values = result;
				results.count = result.size();
				return results;
			}
		};
		return filter;
	}
	
	public boolean isNumeric(String str) {
	    Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
	}
	
	/**
	 * 获取最新的通话记录， 如果有最新的通话记录，则刷新
	 */
	public void refreshNewest()
	{
		System.out.println("  is refresh  ------- > " + mainActivity.isRefreshing);
		
		if(!mainActivity.isRefreshing)
		{
			mainActivity.isRefreshing = true;
		    	
			long start = System.currentTimeMillis();
			
			if(getNewestCalllog())
			{
				type = TYPE_ALL;
				this.list = all;
				
				long end = System.currentTimeMillis();
				
//				System.out.println("通话记录刷新   耗时 ： ========  "  + (end-start) );
				
				handler.sendEmptyMessage(4);
				
			}else{
				mainActivity.reSetCallingState();
			}
		}
	}
	
	
	public void refreshCalllogSort()
	{
			long start = System.currentTimeMillis();
			
			
			sf = mainActivity.getSharedPreferences(SF_NAME, 0);
			sort = sf.getInt(SystemSettingActivity.SF_KEY_COLLOG_SORT, 1);
			
//			System.out.println(" calllog sort --- > " +sort);
			
			if(sort == 0)
			{
				
//				System.out.println(" heat sort source ---- > " + heatSort_source.size());
				
				List<CallLogInfo> list = new ArrayList<CallLogInfo>();
				if(heatSort_source.size() == 0)
				{
//					System.out.println(" source size ---- > " + source.size());
					for (int i = 0; i < source.size(); i++) {
						CallLogInfo ci = source.get(i);

						if (list.size() == 0) {
							list.add(0, ci);
						} else {
							CallLogInfo cc = null;
							int index = -1;
							for (int j = 0; j < list.size(); j++) {
								cc = list.get(j);
								if (ci.getTotal() > cc.getTotal()) {
									index = j;
									break;
								}
								if (j == list.size() - 1) {
									index = j;
								}
							}
							list.add(index, ci);
						}
					}
				}
//				all_source = heatSort_source;
				all_source = list;
			}else{
				all_source = source ;
			}
			
			//根据不同类型筛选通话记录
			if(type == TYPE_ALL)
			{
				this.list = all;
			}else if(type == TYPE_ACCEPT) {
				this.list = accepts;
			}else{
				this.list = rejects;
			}
			
			long end = System.currentTimeMillis();
			
//			System.out.println("通话记录刷新   耗时 ： ========  "  + (end-start) );
			
			handler.sendEmptyMessage(5);
	}
	
	
	//过滤
	public void filter()
	{
		all.clear();
		
		for(CallLogInfo c:all_source)
		{
			  String c_number = c.getmCaller_number();
			   boolean isMath = MainActivity.checkIsEnContactByNumber(c_number);
				
				if(isMath) { //被过滤
					
				 }else{
					all.add(c);
				 }
		  }
	 }
	
	
	//过滤所有列表
	public void fitterAll()
	{
		filter();
		filterReject();
		filterAccepts();
	}
	
	//改变排序
	public void changeToSort(int sort)
	{
//		this.list = ;
		notifyDataSetChanged();
	}
	
	public String getHHMM(long time)
	{
		Calendar car = Calendar.getInstance();
		Date date = new Date(time);
		car.setTime(date);
		int hour = car.get(Calendar.HOUR_OF_DAY);
		int min = car.get(Calendar.MINUTE);
		
		String hour_str;
		if(hour<=9)
		{
			hour_str = "0"+hour+":";
		}else{
			hour_str =hour+":";
		}
		
		String min_str;
		
		if(min<=9)
		{
			min_str = "0"+min+"";
		}else{
			min_str =min+"";
		}
		
		return hour_str + min_str;
	}
	
	public void reQueryContactName()
  	{
  		for(CallLogInfo cl:list)
  		{
  			cl.setNeedRequery(true);
  		}
  		notifyDataSetChanged();
  	}
	
	public void refreshAll(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				start = System.currentTimeMillis();
				getCallLogInfo();
		
				handler.sendEmptyMessage(6);
			}
		}).start();
	}
	
}
