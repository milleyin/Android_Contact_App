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
import android.text.SpannableString;
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
import com.dongji.app.entity.MessageFavorite;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.ExpressionUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;

/**
 * 
 * 最近通话 列表 对应的Adapter
 * 
 * @author Administrator
 * 
 */
public class ContactDetailCollectSmsAdapter  extends BaseAdapter {
	
    Context context;
    
    AddContactActivity addContactLayout;
    
    OnClickListener onClickListener;
    
    private List<MessageFavorite> list = new ArrayList<MessageFavorite>();

	public View menu;
	int margin_bottom;
	int original_x;

	List<CheckBox> cbs ;
	
    boolean[] itemStatus = new boolean[20];
	
	boolean isEditMode = false; //是否为多选模式
	
	
	public static  String SF_NAME = "systemsetting";
	public static String SF_KEY_COLLOG_SORT = "collog_sort";
	SharedPreferences sf ;
	int sort = 1;
	
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
	
	public ContactDetailCollectSmsAdapter(Context context,AddContactActivity addContactLayout, List<String> all_numbers , OnClickListener onClickListener)
	{
		this.context = context;
		this.addContactLayout = addContactLayout;
		this.onClickListener = onClickListener;
		
//		//测试数据
//		for(int i =0;i<10;i++)
//		{
//			CallLogInfo c = new CallLogInfo();
//			list.add(c);
//		}
		
//		System.out.println("all_numbers   ---> "+all_numbers);
		
		this.list = getFavorites(all_numbers);
//		
		itemStatus = new boolean[list.size()];
		
		cbs = new ArrayList<CheckBox>();
		for(int i = 0;i<list.size();i++)
		{
			cbs.add(null);
		}
	}

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

	private List<MessageFavorite> getFavorites(List<String> all_numbers)
	{
		List<String > new_all = new ArrayList<String>();
		
		for(String n:all_numbers)
		{
			new_all.add(n);
			new_all.add("+86"+n);
			new_all.add("17951"+n);
			new_all.add("12593"+n);
			new_all.add(PhoneNumberTool.cleanse(n));
		}
		List<MessageFavorite> l = new ArrayList<MessageFavorite>();
		Cursor c = DButil.getInstance(context).queryFavorite(new_all);
		
		while (c.moveToNext()) {
			MessageFavorite mf = new MessageFavorite();
//			FAVORITE_ID,THREAD_ID,CONTENT_ID,FAVORITE_CONTENT,CONTENT_TIME,FAVORITE_SENDER,FAVORITE_NUMBER
			
			mf.setId(c.getInt(c.getColumnIndex(MyDatabaseUtil.FAVORITE_ID)));
			mf.setThreadId(c.getString(c.getColumnIndex(MyDatabaseUtil.THREAD_ID)));
			mf.setContentId(c.getString(c.getColumnIndex(MyDatabaseUtil.CONTENT_ID)));
			mf.setContent(c.getString(c.getColumnIndex(MyDatabaseUtil.FAVORITE_CONTENT)));
			
//			System.out.println("time long ---> " + c.getLong(c.getColumnIndex(MyDatabaseUtil.CONTENT_TIME)));
			
			mf.setTime(TimeTool.getTimeStrYYMMDD(c.getLong(c.getColumnIndex(MyDatabaseUtil.CONTENT_TIME))));
			mf.setNumber(c.getString(c.getColumnIndex(MyDatabaseUtil.FAVORITE_NUMBER)));
			mf.setSender(c.getString(c.getColumnIndex(MyDatabaseUtil.FAVORITE_SENDER)));
			l.add(mf);
		}
		
		c.close();
		
		return l;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder = new ViewHolder();
		
		MessageFavorite mf = list.get(position);
		
		convertView = LayoutInflater.from(context).inflate(R.layout.contact_collect_msg_item, null);
		viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		viewHolder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
		viewHolder.tv_body = (TextView) convertView.findViewById(R.id.tv_body);
		
		System.out.println("mf.getTime() --->" + mf.getTime());
		viewHolder.tv_name.setText("发件人:"+mf.getSender());
		viewHolder.tv_date.setText(mf.getTime());
		
		String str = mf.getContent();					
		SpannableString spannableString = ExpressionUtil.getExpressionString(context, str);
		try {
			viewHolder.tv_body.setText(spannableString);
		} catch (Exception e) {
		}
		
		viewHolder.menu_check = (Button)convertView.findViewById(R.id.menu_check);
//		viewHolder.menu_check.setTag(callInfo.getmCaller_number());
		viewHolder.menu_check.setTag(mf.getContentId()+":"+mf.getContent());
		viewHolder.menu_check.setOnClickListener(onClickListener);
		
		viewHolder.menu_re_send = (Button)convertView.findViewById(R.id.menu_re_send);
//		viewHolder.menu_re_send.setTag(callInfo.getmCaller_number());
		viewHolder.menu_re_send.setTag(mf.getContent());
		viewHolder.menu_re_send.setOnClickListener(onClickListener);
		
		viewHolder.menu_delete = (Button)convertView.findViewById(R.id.menu_delete);
		viewHolder.menu_delete.setTag(mf.getId()+":"+position);
		viewHolder.menu_delete.setOnClickListener(onClickListener);
		
		viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxEdit);
		viewHolder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
		
		//替换更新存储的checkBoxs 索引
		if(cbs.get(position)!=null)
		{
			cbs.remove(position);
		}
		cbs.add(position, viewHolder.checkBox);
		if(cbs.size()>list.size())
		{
			cbs.remove(cbs.size()-1);
		}
		
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
		TextView tv_name;
		TextView tv_date;
		TextView tv_body;
		
		Button menu_check;
		Button menu_re_send;
		Button menu_delete;
		
		CheckBox checkBox;
	}
	
	
	
	public void  remove(int position)
	{
		list.remove(position);
		itemStatus = new boolean[list.size()];
		notifyDataSetChanged();
	}
	
	
	//获取所有被选中的  通话记录的id
	public long [] getSelectedIds()
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
		
		if(isEditMode)
		{
			for(CheckBox cb:cbs)
			{
				if(cb!=null)
				{
					cb.setVisibility(View.VISIBLE);
					cb.setChecked(false);
				}
			}
		}else{
			for(CheckBox cb:cbs)
			{
				if(cb!=null)
				{
					cb.setVisibility(View.GONE);
					cb.setChecked(false);
				}
			}
		}
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
