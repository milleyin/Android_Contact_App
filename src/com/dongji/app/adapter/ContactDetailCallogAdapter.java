package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.AddContactActivity;
import com.dongji.app.addressbook.ContactLayout;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.entity.EnContact;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;

/**
 * 
 * 最近通话 列表 对应的Adapter
 * 
 * @author Administrator
 * 
 */
public class ContactDetailCallogAdapter  extends BaseAdapter {
	
    Context context;
    
    AddContactActivity addContactLayout;
    MyDatabaseUtil myDatabaseUtil ;
    OnClickListener onClickListener;
    
	private List<CallLogInfo> list = new ArrayList<CallLogInfo>();

	public View menu;
	int margin_bottom;
	int original_x;
//
//	View content_layout;
//	int target_dis = 50;
//	int l_r_dis;
//	int l_r;

//	List<CheckBox> cbs ;
	
    boolean[] itemStatus = new boolean[20];
	
	boolean isEditMode = false; //是否为多选模式
	
	String number ;
	
	public static  String SF_NAME = "systemsetting";
	public static String SF_KEY_COLLOG_SORT = "collog_sort";
	SharedPreferences sf ;
	int sort = 1;
	
//	private static HashMap<Integer,Boolean> isSelected;
	
//	public static HashMap<Integer, Boolean> getIsSelected() {
//		return isSelected;
//	}
//
//	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
//		ContactDetailCallogAdapter.isSelected = isSelected;
//	}


	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);

			switch (msg.what) {
			case 0:
				lp.setMargins(0, 0, 0, margin_bottom);

				menu.setLayoutParams(lp);
				menu.postInvalidate();

				break;
				
			default:
				break;
			}

		};
	};
	
	public ContactDetailCallogAdapter(Context context,AddContactActivity addContactLayout, String number , OnClickListener onClickListener)
	{
		this.context = context;
		this.addContactLayout = addContactLayout;
		this.onClickListener = onClickListener;
		this.list = getCallLogInfoByNumber(number);
		
		itemStatus = new boolean[list.size()];
		
//		cbs = new ArrayList<CheckBox>();
//		for(int i = 0;i<list.size();i++)
//		{
//			cbs.add(null);
//		}
//		
//		 isSelected = new HashMap<Integer,Boolean>();
//		 initDate();
	}
	
//	 private void initDate(){        
//		 for(int i=0; i<list.size();i++) {            
//			 	getIsSelected().put(i,false);        
//			 }    
//		 }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
	    final CallLogInfo callInfo = (CallLogInfo) list.get(position);
	    
		final ViewHolder viewHolder = new ViewHolder();
		
		convertView = LayoutInflater.from(context).inflate(R.layout.contact_history_callls_item, null);
		
//		viewHolder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		viewHolder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
//		viewHolder.missedNum = (TextView) convertView.findViewById(R.id.missedNum);
		viewHolder.number = (TextView) convertView.findViewById(R.id.number);
		viewHolder.area = (TextView) convertView.findViewById(R.id.area);
		viewHolder.date = (TextView) convertView.findViewById(R.id.date);
		viewHolder.type = (ImageView) convertView.findViewById(R.id.type);
		viewHolder.duration = (TextView) convertView.findViewById(R.id.duration);
		viewHolder.day = (TextView) convertView.findViewById(R.id.day);
		
		viewHolder.menu_call = (Button)convertView.findViewById(R.id.menu_call);
		viewHolder.menu_call.setTag(callInfo.getmCaller_number());
		viewHolder.menu_call.setOnClickListener(onClickListener);
		
		viewHolder.menu_sms_detail = (Button)convertView.findViewById(R.id.menu_sms_detail);
		viewHolder.menu_sms_detail.setTag(callInfo.getmCaller_number());
		viewHolder.menu_sms_detail.setOnClickListener(onClickListener);

		viewHolder.menu_delete = (Button)convertView.findViewById(R.id.menu_delete);
		viewHolder.menu_delete.setTag(position+","+String.valueOf(callInfo.getId()));
		viewHolder.menu_delete.setOnClickListener(onClickListener);
		
		viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxEdit);
		viewHolder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
		
//		if(cbs.get(position)!=null)
//		{
//			cbs.remove(position);
//		}
//		cbs.add(position, viewHolder.checkBox);
//		if(cbs.size()>list.size())
//		{
//			cbs.remove(cbs.size()-1);
//		}
		
		if(isEditMode)
		{
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			
			if (itemStatus[position] == true) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
		}else  {
			viewHolder.checkBox.setVisibility(View.GONE);
		}
				
//		viewHolder.checkBox.setChecked(getIsSelected().get(position));
		
		LinearLayout main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						if(!isEditMode)
						{
							popUpMenu(v);
						}
						break;

					default:
						break;
						
					}
					return false;
				}
			});
		
		viewHolder.number.setText(callInfo.getmCaller_number());
		viewHolder.date.setText(callInfo.getmCall_date());
		viewHolder.day.setText(callInfo.getDay());

		Resources resource = (Resources) context.getResources();
		ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.my_color);
		
		if ("未接通".equals(callInfo.getmCall_duration())) {
			viewHolder.duration.setTextColor(csl);
		}
		viewHolder.duration.setText(callInfo.getmCall_duration());
		
		String type = callInfo.getmCall_type();
		if(	type.equals("呼入"))
		{
			viewHolder.type.setImageResource(R.drawable.dia_in_icon);
		}else if ( type.equals("呼出"))
		{
			viewHolder.type.setImageResource(R.drawable.dia_out_icon);
		}else{
			viewHolder.type.setImageResource(R.drawable.dia_in_reject);
		}
		viewHolder.tvNick.setText(callInfo.getmCaller_name());
		
		
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
		
		viewHolder.area.setText(callInfo.getmCaller_area());
		
		return convertView;

}
	
	private void popUpMenu(View view) {
//		System.out.println(" onItemLongClick ");
		if (menu != null && menu != view.findViewById(R.id.menu)) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
		}

		menu = view.findViewById(R.id.menu);
		final int height = menu.getHeight();
		margin_bottom = ((LinearLayout.LayoutParams) menu.getLayoutParams()).bottomMargin;


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
	
	
	class ViewHolder {
//		TextView tvCatalog;// 目录
//		ImageView ivAvatar;// 头像
//		TextView missedNum;
		TextView tvNick;// 昵称
		TextView number;
		TextView area;
		TextView date;
		TextView day;
		ImageView type;
		TextView duration;
		
		Button menu_call;
		Button menu_sms_detail;
		Button menu_delete;
		
		CheckBox checkBox;
	}
	
//	private String getRingOneCall(){
//		String id = "";
//		
//		RingOneCallDBHelper ringOneCallDBHelper = new RingOneCallDBHelper(context);
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
	
	private List<CallLogInfo> getCallLogInfoByNumber(String n) {
		myDatabaseUtil = DButil.getInstance(context);
		Cursor c = null;
		List<CallLogInfo> list = null;
		
		try {
			c = PhoneNumberTool.getCallLogByNumber(context, n);
			list = new ArrayList<CallLogInfo>();
			
			if (c.moveToFirst()) {
				do {
						CallLogInfo callLogInfo = new CallLogInfo();
						callLogInfo.setId(c.getInt(c.getColumnIndex(CallLog.Calls._ID)));
						
						String NUMBER = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER)).replace("(", "").replace(") ", "").replace("-", "").replace(" ", "");
						
						 boolean isMath = MainActivity.checkIsEnContactByNumber(NUMBER);
						
						 long id = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
//							boolean isInterceptPhone = false;
//							if(MainActivity.INTERCEPTPHONE.size() > 0){
//								
//								for(long call_id:MainActivity.INTERCEPTPHONE){
//									
//									if ( call_id == id){
//										
//										isInterceptPhone = true;
//										break;
//									}
//								}
//							}
							

//							if(!isInterceptPhone){
						 
								if(!isMath)
								{
									// 联系人名称
									String CACHED_NAME = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
									
									// 通话时间
									Date date = new Date(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
									
									SimpleDateFormat time_sfd = new SimpleDateFormat("HH:mm");
									String time = time_sfd.format(date);
									
									long t = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE)));
									callLogInfo.setDay(TimeTool.getTimeStrYYMMDDNoToday(t));
									
									int TYPE = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
									
									// 通话时长
									String DURATION = "";
									if(myDatabaseUtil.queryCallLogId().contains(String.valueOf(id))){
										
										DURATION = "响铃一声！";
										
									}else{
//										long old = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));
//										long hour = old / (60 * 60);
//										long min = (old % (60 * 60)) / (60);
//										long second = (old % (60));
//										
//										if(hour!=0)
//										{
//											DURATION = hour + "小时" + min + "分钟" + second + "秒";
//										}else if(min!=0)
//										{
//											DURATION =  min + "分钟" + second + "秒";
//										}else{
//											DURATION =  second + "秒";
//										}
//								
//										
//										if ((TYPE == CallLog.Calls.INCOMING_TYPE)
//												&&  hour == 0 && min ==0 && second == 0) {
//											DURATION = "未接通";
//										} else if ((TYPE == CallLog.Calls.OUTGOING_TYPE)
//												&& hour == 0 && min ==0 && second == 0 ) {
//											DURATION = "未接通";
//										}
										
										long old = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));
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

//											if ((TYPE == CallLog.Calls.INCOMING_TYPE)
//													&&  hour == 0 && min ==0 && second == 0) {
//												
//												System.out.println(" ====incoming type ----- > " );
//												
//												DURATION = "未接通";
//											} else if ((TYPE == CallLog.Calls.OUTGOING_TYPE)
//													&& hour == 0 && min ==0 && second == 0 ) {
//												
//												System.out.println(" ====outgoing type ----- > " );
//												
//												DURATION = "未接通";
//											}
										}
										
//									}
									
									// 通话类型
									String call_type = "";
									
									if (TYPE == CallLog.Calls.INCOMING_TYPE) {
										
										call_type = "呼入";
									} else if (TYPE == CallLog.Calls.OUTGOING_TYPE) {
										
										call_type = "呼出";
									} else if (TYPE == CallLog.Calls.MISSED_TYPE) {
										
										call_type = "未接";
									}
									if ((TYPE == CallLog.Calls.INCOMING_TYPE)&& (("时长：0小时0分钟0秒").equals(DURATION))) {
										
										DURATION = "未接通";
									} else if ((TYPE == CallLog.Calls.OUTGOING_TYPE)&& (("时长：0小时0分钟0秒").equals(DURATION))) {
										
										DURATION = "未接通";
									}
		
									callLogInfo.setmCaller_name(CACHED_NAME);
									callLogInfo.setmCaller_number(NUMBER);
									callLogInfo.setmCall_date(time);
									callLogInfo.setmCall_duration(DURATION);
									callLogInfo.setmCall_type(call_type);
									
									list.add(callLogInfo);
								}
							}
				} while (c.moveToNext());
				
				c.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
		
		System.out.println(" list size --->"+ list.size());
		
		return list;
	}
	
	public void  remove(int position)
	{
		list.remove(list.get(position));
		itemStatus = new boolean[list.size()];
		notifyDataSetChanged();
	}
	
//	private String getDistanceTime(long time2) {
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
	
	//获取所有被选中的  通话记录的id
	public long [] getSelectedCallogId()
	{
		int [] positon =  getSelectedItemIndexes();
		
		
		if(positon.length==0)
		{
			return null;
		}
		
		long [] callog_ids = new long [positon.length];
		
		for(int i =0;i<positon.length;i++)
		{
			callog_ids[i] = list.get(positon[i]).getId();
		}
		
		return callog_ids;
	}
	
	public void selectALL(boolean isSelectAll) {
		
		for (int i = 0; i < itemStatus.length; i++) {
			itemStatus[i] = isSelectAll;
		}
	}
	
	public int[] getSelectedItemIndexes() {

		if (itemStatus == null || itemStatus.length == 0) {
			return new int[0];
		} else {
			int size = itemStatus.length;
			int counter = 0;
			for (int i = 0; i < size; i++) {
				if (itemStatus[i] == true)
					++counter;
			}
			int[] selectedIndexes = new int[counter];
			int index = 0;
			for (int i = 0; i < size; i++) {
				if (itemStatus[i] == true)
					selectedIndexes[index++] = i;
			}
			return selectedIndexes;
		}
	};
	
	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
		
		//重置所有状态
		for(int i =0;i<itemStatus.length;i++) 
		{
			itemStatus[i] = false;
		}
		
		this.notifyDataSetChanged();
		
//		if(isEditMode)
//		{
//			for(CheckBox cb:cbs)
//			{
//				if(cb!=null)
//				{
//					cb.setVisibility(View.VISIBLE);
//					cb.setChecked(false);
//				}
//			}
//		}else{
//			for(CheckBox cb:cbs)
//			{
//				if(cb!=null)
//				{
//					cb.setVisibility(View.GONE);
//					cb.setChecked(false);
//				}
//			}
//		}
	}

	public boolean isEditMode() {
		return isEditMode;
	}
	
	
	class MyCheckBoxChangedListener implements OnCheckedChangeListener {
		int position;

		MyCheckBoxChangedListener(int position) {
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			System.out.println("" + position + "Checked?:" + isChecked);
			if (isChecked)
				itemStatus[position] = true;
			else
				itemStatus[position] = false;
			addContactLayout.updateDeleteNum(getSelectedItemIndexes().length);
		}
	}
}
