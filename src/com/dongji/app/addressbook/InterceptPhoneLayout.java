package com.dongji.app.addressbook;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dongji.app.adapter.PhoneInterceptAdapter;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.ui.MyDialog;

public class InterceptPhoneLayout implements OnClickListener{

	Context context;
	
	public View view;
	
	ListView lvintercept;
	LinearLayout tips_top_layout;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	TextView tv_delete_tips;
//	TextView delete_id;
	
	MyDatabaseUtil myDatabaseUtil;
	PhoneInterceptAdapter phoneInterceptAdapter;
	MyDialog myDialog ;
	
	String[] tag = null;
	
	public InterceptPhoneLayout(Context context) {
		this.context = context;
		
		view = LayoutInflater.from(context).inflate(R.layout.interceptelist, null);
		
		myDatabaseUtil = DButil.getInstance(context);
		
		lvintercept = (ListView) view.findViewById(R.id.lvintercept);
		
		tips_top_layout = (LinearLayout) view.findViewById(R.id.tips_top_layout);
		btn_top_tips_yes = (Button) view.findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_no = (Button) view.findViewById(R.id.btn_top_tips_no);
		tv_delete_tips = (TextView) view.findViewById(R.id.tv_delete_tips);
//		delete_id = (TextView) view.findViewById(R.id.delete_id);
		
		btn_top_tips_yes.setOnClickListener(this);
		btn_top_tips_no.setOnClickListener(this);
		
		loadData();
		
		
	}
	
	public void loadData(){
		
		List<CallLogInfo> list = myDatabaseUtil.queryPhoneIntercept();
		
		phoneInterceptAdapter = new PhoneInterceptAdapter(context, list, new OnMenuItemClickListener());
		lvintercept.setAdapter(phoneInterceptAdapter);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_top_tips_yes:
			
			String _id = tag[0];
			String calllog_id = tag[1];
			
			myDatabaseUtil.deletePhoneIntercept(_id);
//			mainActivity.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID+"="+calllog_id, null);
			
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
	
	
	class OnMenuItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.delete:
				tv_delete_tips.setText("确定删除选中的拦截历史？");
				
				tag = v.getTag().toString().split(",");
				
//				delete_id.setText(v.getTag().toString());
//				tips_top_layout.setVisibility(View.VISIBLE);
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
				
				myDialog = new MyDialog(context, "确定删除选中的拦截历史？", new dialogOnClickListener());
				myDialog.normalDialog();
				
				break;
			
			default:
				break;
			}
		}
	}
	
	class dialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_top_tips_yes:
				
				String _id = tag[0];
				String calllog_id = tag[1];
				
				myDatabaseUtil.deletePhoneIntercept(_id);
				
				loadData();
//				mainActivity.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID+"="+calllog_id, null);
				
				myDialog.closeDialog();
				
				break;
				
			default:
				break;
			}
		}
		
	}
	
}
