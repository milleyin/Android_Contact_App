package com.dongji.app.addressbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;

public class InterceptModeLayout implements OnClickListener {

	Context context;
	
	public View view;
	
	RadioButton radioBtn1;
	RadioButton radioBtn2;
	RadioButton radioBtn3;
	RadioButton radioBtn4;
	
	SharedPreferences interceptmode;
	String name = "interceptmode";
	SharedPreferences.Editor editor;
	
	public InterceptModeLayout(Context context){
		
		this.context = context;
		
		view = LayoutInflater.from(context).inflate(R.layout.interceptmode, null);
		
		radioBtn1 = (RadioButton) view.findViewById(R.id.mode1);
		radioBtn2 = (RadioButton) view.findViewById(R.id.mode2);
		radioBtn3 = (RadioButton) view.findViewById(R.id.mode3);
		radioBtn4 = (RadioButton) view.findViewById(R.id.mode4);
		
		radioBtn1.setOnClickListener(this);
		radioBtn2.setOnClickListener(this);
		radioBtn3.setOnClickListener(this);
		radioBtn4.setOnClickListener(this);
		
		interceptmode = context.getSharedPreferences(name, 0);
		editor = interceptmode.edit();
				
		loadData();
	}
	
	public void loadData(){
		int mode = interceptmode.getInt("mode", 1);
		
		System.out.println(" intercept mode ---- > " + mode);
		
		if(mode == 1)
			radioBtn1.setChecked(true);
		else if (mode == 2)
			radioBtn2.setChecked(true);
		else if (mode == 3)
			radioBtn3.setChecked(true);
		else if (mode == 4)
			radioBtn4.setChecked(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.mode1:
				radioBtn1.setChecked(true);
				radioBtn2.setChecked(false);
				radioBtn3.setChecked(false);
				radioBtn4.setChecked(false);
				getSaveModeStatus(1);
				break;
			case R.id.mode2:
				radioBtn1.setChecked(false);
				radioBtn2.setChecked(true);
				radioBtn3.setChecked(false);
				radioBtn4.setChecked(false);
				getSaveModeStatus(2);
				break;
			case R.id.mode3:
				radioBtn1.setChecked(false);
				radioBtn2.setChecked(false);
				radioBtn3.setChecked(true);
				radioBtn4.setChecked(false);
				getSaveModeStatus(3);
				break;
	
			case R.id.mode4:
				radioBtn1.setChecked(false);
				radioBtn2.setChecked(false);
				radioBtn3.setChecked(false);
				radioBtn4.setChecked(true);
				getSaveModeStatus(4);
				break;
	
			default:
				break;
		}
	}
	
	private void getSaveModeStatus(int mode){
		
		editor = interceptmode.edit();
		editor.putInt("mode", mode);
		editor.commit();
		
		System.out.println(mode + "  =========  " + interceptmode.getInt("mode", 1));
	}
	
}
