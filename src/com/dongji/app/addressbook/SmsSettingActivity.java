package com.dongji.app.addressbook;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 
 * 设置里的 短信设置
 * 
 */
public class SmsSettingActivity extends Activity {

	
	public View view;
	
	CheckBox statusbar_point;
	CheckBox pop_point;
	CheckBox sound_point;
	CheckBox vibrate_point;
	CheckBox lightscreen_point;
	
	SharedPreferences sms_checkbox_state ;
	SharedPreferences.Editor editor = null;
	
	String STATUSBAR_POINT = "statusbar_point";
	String POP_POINT = "pop_point";
	String SOUND_POINT = "sound_point";
	String VIBRATE_POINT = "vibrate_point";
	String LIGHTSCREEN_POINT = "lightscreen_point";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			view = LayoutInflater.from(this).inflate(R.layout.setting_item_8_message, null);
			
			setContentView(view);
			
			sms_checkbox_state = getSharedPreferences("com.dongji.app.addressbook.dialingsetting", 0);
			editor = sms_checkbox_state.edit();
			
			statusbar_point = (CheckBox) view.findViewById(R.id.statusbar_point);
			pop_point = (CheckBox) view.findViewById(R.id.pop_point);
			sound_point = (CheckBox) view.findViewById(R.id.sound_point);
			vibrate_point = (CheckBox) view.findViewById(R.id.vibrate_point);
			lightscreen_point = (CheckBox) view.findViewById(R.id.lightscreen_point);
			
			statusbar_point.setOnCheckedChangeListener(onCheck);
			pop_point.setOnCheckedChangeListener(onCheck);
			sound_point.setOnCheckedChangeListener(onCheck);
			vibrate_point.setOnCheckedChangeListener(onCheck);
			lightscreen_point.setOnCheckedChangeListener(onCheck);
			
			statusbar_point.setChecked(sms_checkbox_state.getBoolean(STATUSBAR_POINT, true));
			pop_point.setChecked(sms_checkbox_state.getBoolean(POP_POINT, true));
			sound_point.setChecked(sms_checkbox_state.getBoolean(SOUND_POINT, false));
			vibrate_point.setChecked(sms_checkbox_state.getBoolean(VIBRATE_POINT, false));
			lightscreen_point.setChecked(sms_checkbox_state.getBoolean(LIGHTSCREEN_POINT, true));
	}
	
	private OnCheckedChangeListener onCheck = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.statusbar_point:
				editor.putBoolean(STATUSBAR_POINT, statusbar_point.isChecked());
				editor.commit();
				break;
			case R.id.pop_point:
	            editor.putBoolean(POP_POINT, pop_point.isChecked());
				editor.commit();
				break;
			case R.id.sound_point:
				editor.putBoolean(SOUND_POINT, sound_point.isChecked());
				editor.commit();
				break;
			case R.id.vibrate_point:
				editor.putBoolean(VIBRATE_POINT, vibrate_point.isChecked());
				editor.commit();
				break;
			case R.id.lightscreen_point:
				editor.putBoolean(LIGHTSCREEN_POINT, lightscreen_point.isChecked());
				editor.commit();
				break;
			default:
				break;
			}
		}
	};
	
}
