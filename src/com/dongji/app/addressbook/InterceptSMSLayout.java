package com.dongji.app.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dongji.app.adapter.SMSInterceptAdapter;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.ui.MyDialog;

public class InterceptSMSLayout implements OnClickListener {

	Context context;
	
	public View view;
	
	ListView lvintercept;
	
	MyDatabaseUtil myDatabaseUtil;
	
	SMSInterceptAdapter smsIntercepteAdapter;
	
	LinearLayout tips_top_layout;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	TextView tv_delete_tips;
	TextView delete_id;
	
	Dialog dialog;
	String id = "";
	String sms_id = "";
	String thread_id = "";
	
	Dialog deleteDialog;
	MyDialog myDialog ;
	
	List<SmsContent> list = null;
	
	public InterceptSMSLayout(Context context){
		
		this.context = context;
		
		view  = LayoutInflater.from(context).inflate(R.layout.interceptelist, null);
		
		myDatabaseUtil = DButil.getInstance(context);
		
		lvintercept = (ListView) view.findViewById(R.id.lvintercept);
		
		tips_top_layout = (LinearLayout) view.findViewById(R.id.tips_top_layout);
		btn_top_tips_yes = (Button) view.findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_no = (Button) view.findViewById(R.id.btn_top_tips_no);
		tv_delete_tips = (TextView) view.findViewById(R.id.tv_delete_tips);
		delete_id = (TextView) view.findViewById(R.id.delete_id);
		
		btn_top_tips_yes.setOnClickListener(this);
		btn_top_tips_no.setOnClickListener(this);
		
		loadData();
		
	}
	
	public void loadData(){
		
		list = myDatabaseUtil.querySmsIntercept();
		
		smsIntercepteAdapter = new SMSInterceptAdapter(context, list, new OnMenuItemClickListener());
		lvintercept.setAdapter(smsIntercepteAdapter);
	}
	
	
	class OnMenuItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.delete:
				delete_id.setText(v.getTag().toString());
//				tv_delete_tips.setText("确定删除选中短信？");
//				tips_top_layout.setVisibility(View.VISIBLE);
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
				
				myDialog = new MyDialog(context, "确定删除选中短信？", new dialogOnClickListener());
				myDialog.normalDialog();
				
				break;
			
			case R.id.resume:
//				tv_delete_tips.setText("确定恢复选中短信？");
//				tips_top_layout.setVisibility(View.VISIBLE);
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
				String all_id = v.getTag().toString();
				
				id = all_id.substring(0, all_id.indexOf(","));
				sms_id = all_id.substring(all_id.indexOf(",")+1,all_id.indexOf("|"));
				thread_id = all_id.substring(all_id.indexOf("|")+1,all_id.length());
				
				dialog = new Dialog(context,R.style.theme_myDialog_activity);
				dialog.setContentView(R.layout.intercept_sms_resume_dialog);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				
				Button btn_resume_cancel = (Button) dialog.findViewById(R.id.btn_resume_cancel);
				Button btn_resume_yes = (Button) dialog.findViewById(R.id.btn_resume_yes);
				
				btn_resume_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						dialog.cancel();
					}
				});
				
				btn_resume_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
//						
//						Cursor cursor = db.query(smsInterceptDBHelper.table, null, smsInterceptDBHelper._ID + " = " + id, null, null, null, " _id desc");
////						
//						if (cursor.getCount() > 0){
//							
//							if ( cursor.moveToFirst()){
//								
//								
//								
//							}
//						}
						
//						cursor.close();
//						db.close();
						
						
						for ( int i = 0;i<list.size();i++) {
							
							SmsContent smsContent = list.get(i);
							
							if ( smsContent.getId() == Integer.parseInt(id)){
								
//								mainActivity.INTERCEPTSMS.remove(smsContent.getSms_number());
								
								ContentValues values = new ContentValues();

								values.put("date", smsContent.getSystemTime());
								values.put("address", smsContent.getSms_number());
								values.put("body",smsContent.getSms_body());
								
								SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm");
								
								System.out.println(" sms resume date ---- > " + df.format(smsContent.getSystemTime()));
								
								context.getContentResolver().insert(Uri.parse("content://sms/inbox/"), values);
							
								myDatabaseUtil.deleteSmsIntercept(id);
								
							}
							
						}
						
						dialog.cancel();
						
						loadData();
					}
				});
				
				break;
			
			default:
				break;
			}
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_top_tips_yes:
			
			String all_id = delete_id.getText().toString();
			
			String id = all_id.substring(0, all_id.indexOf(","));
			
			myDatabaseUtil.deleteSmsIntercept(id);
			
			loadData();
			
			tips_top_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.up_out));
			tips_top_layout.setVisibility(View.GONE);
			break;

		case R.id.btn_top_tips_no:
			tips_top_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.up_out));
			tips_top_layout.setVisibility(View.GONE);
			
			break;	
			
		default:
			break;
		}
	}
	
	class dialogOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_top_tips_yes:
				
				String all_id = delete_id.getText().toString();
				
				String id = all_id.substring(0, all_id.indexOf(","));
				
				myDatabaseUtil.deleteSmsIntercept(id);
				
				loadData();
				
				myDialog.closeDialog();
				
				break;
				
			default:
				break;
			}
		}
		
	}
	
}
