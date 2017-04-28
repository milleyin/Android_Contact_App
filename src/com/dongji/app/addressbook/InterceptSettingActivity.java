package com.dongji.app.addressbook;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 
 * 拦截模式
 * 
 * @author Administrator
 *
 */
public class InterceptSettingActivity extends Activity implements OnClickListener {
	
	public View view;
	
	Button phoneIntercept;
	Button smsIntercept;
	Button namelist;
	Button keyword;
	Button interceptmode;
	
	LinearLayout content_layout;
	View view1;
	InterceptPhoneLayout interceptPhoneLayout;
	InterceptModeLayout interceptModeLayout;
	InterceptKeywordLayout interceptKeywordLayout;
	InterceptBlackWhiteLayout interceptBlackWhiteLayout;
	InterceptSMSLayout interceptSMSLayout;
	
	int cur_index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(this).inflate(R.layout.setting_item_4_intercept, null);
		
		setContentView(view);
		
		phoneIntercept = (Button) view.findViewById(R.id.phoneIntercept);
		smsIntercept = (Button) view.findViewById(R.id.smsIntercept);
		namelist = (Button) view.findViewById(R.id.namelist);
		keyword = (Button) view.findViewById(R.id.keyword);
		interceptmode = (Button) view.findViewById(R.id.interceptmode);
		
		content_layout = (LinearLayout) view.findViewById(R.id.content_layout);
		
		phoneIntercept.setOnClickListener(this);
		smsIntercept.setOnClickListener(this);
		namelist.setOnClickListener(this);
		keyword.setOnClickListener(this);
		interceptmode.setOnClickListener(this);
		
		loadData();
	}

	public void loadData(){
		
		content_layout.removeAllViews();
		
		if(interceptPhoneLayout == null)
			interceptPhoneLayout = new InterceptPhoneLayout(InterceptSettingActivity.this);
		else
			interceptPhoneLayout.loadData();
		
		content_layout.addView(interceptPhoneLayout.view);
		
		if ( cur_index != 0 ) {
			
			setSelectedState(0,cur_index);
			
		} else {
			
			phoneIntercept.setBackgroundResource(R.drawable.intercept_top_selected_bg);
		}
		
		cur_index = 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.phoneIntercept:
			
			if(cur_index!=0)
			{
				content_layout.removeAllViews();
				
				if(interceptPhoneLayout == null)
					interceptPhoneLayout = new InterceptPhoneLayout(InterceptSettingActivity.this);
				else
					interceptPhoneLayout.loadData();
				
				content_layout.addView(interceptPhoneLayout.view);
				
				setSelectedState(0,cur_index);
				cur_index = 0;
			}
			
			
			
			break;
		case R.id.smsIntercept:
			
			if(cur_index!=1)
			{
				content_layout.removeAllViews();
				
				if(interceptSMSLayout == null)
					interceptSMSLayout = new InterceptSMSLayout(InterceptSettingActivity.this);
				else
					interceptSMSLayout.loadData();
				
				content_layout.addView(interceptSMSLayout.view);
				
				setSelectedState(1,cur_index);
				cur_index = 1;
			}
			
			
			break;
		case R.id.namelist:
			
			if(cur_index!=2)
			{
				content_layout.removeAllViews();
				
				if(interceptBlackWhiteLayout == null)
					interceptBlackWhiteLayout = new InterceptBlackWhiteLayout(InterceptSettingActivity.this);
				else
					interceptBlackWhiteLayout.loadBlackList();
				
				content_layout.addView(interceptBlackWhiteLayout.view);
				
				setSelectedState(2,cur_index);
				cur_index = 2;
			}
			
			break;
		case R.id.keyword:
			
			if(cur_index!=3)
			{
				content_layout.removeAllViews();
				
				if(interceptKeywordLayout == null)
					interceptKeywordLayout = new InterceptKeywordLayout(InterceptSettingActivity.this);
				else
					interceptKeywordLayout.loadData();
				
				content_layout.addView(interceptKeywordLayout.view);
				
				setSelectedState(3,cur_index);
				cur_index = 3;
			}
			
			break;
		case R.id.interceptmode:
			
			if(cur_index!=4)
			{
				content_layout.removeAllViews();
				
				if(interceptModeLayout == null)
					interceptModeLayout = new InterceptModeLayout(InterceptSettingActivity.this);
				else
					interceptModeLayout.loadData();
				
				content_layout.addView(interceptModeLayout.view);
				
				setSelectedState(4,cur_index);
				cur_index = 4;
			}
			
			break;

		default:
			break;
		}
	}
	
	void setSelectedState(int cur,int old)
	{
		switch (cur) {
		case 0:
			phoneIntercept.setBackgroundResource(R.drawable.intercept_top_selected_bg);
			phoneIntercept.setTextColor(Color.WHITE);
			break;

		case 1:
			smsIntercept.setBackgroundResource(R.drawable.intercept_top_selected_bg);
			smsIntercept.setTextColor(Color.WHITE);
			break;

		case 2:
			namelist.setBackgroundResource(R.drawable.intercept_top_selected_bg);
			namelist.setTextColor(Color.WHITE);
			break;

		case 3:
			keyword.setBackgroundResource(R.drawable.intercept_top_selected_bg);
			keyword.setTextColor(Color.WHITE);
			break;

		case 4:
			interceptmode.setBackgroundResource(R.drawable.intercept_top_selected_bg);
			interceptmode.setTextColor(Color.WHITE);
			break;

		default:
			break;
		}
		
		
		switch (old) {
		case 0:
			phoneIntercept.setBackgroundResource(R.drawable.intercept_top_normal_bg);
			phoneIntercept.setTextColor(getResources().getColor(R.color.text_color_base));
			break;

		case 1:
			smsIntercept.setBackgroundResource(R.drawable.intercept_top_normal_bg);
			smsIntercept.setTextColor(getResources().getColor(R.color.text_color_base));
			break;

		case 2:
			namelist.setBackgroundResource(R.drawable.intercept_top_normal_bg);
			namelist.setTextColor(getResources().getColor(R.color.text_color_base));
			break;

		case 3:
			keyword.setBackgroundResource(R.drawable.intercept_top_normal_bg);
			keyword.setTextColor(getResources().getColor(R.color.text_color_base));
			break;

		case 4:
			interceptmode.setBackgroundResource(R.drawable.intercept_top_normal_bg);
			interceptmode.setTextColor(getResources().getColor(R.color.text_color_base));
			break;

		default:
			break;
		}
		
	}
}
