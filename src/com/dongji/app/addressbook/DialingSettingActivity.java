package com.dongji.app.addressbook;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface.OnCancelListener;

/**
 * 
 * 设置里的 拨号设置
 * 
 * @author Administrator
 *
 */
public class DialingSettingActivity extends Activity {
	
	public View view;
	
	public final static String SF_NAME = "com.dongji.app.addressbook.dialingsetting";
	SharedPreferences dialing_checkbox_state ;
	
	CheckBox press_state_cb1;
	CheckBox press_state_cb2;
	CheckBox answer_state_cb;
	CheckBox hangup_state_cb;
	CheckBox ip_setting_cb;
	CheckBox auto_dialing_cb;
	
	LinearLayout tips_add_layout;
	EditText et_telephone;
	TextView phone;
	
	Button btn_add_tips_yes;
	Button btn_add_tips_no;
	
	SharedPreferences.Editor editor = null;
	
	String PRESS_STATE_CB1 = "press_state_cb1";
	String PRESS_STATE_CB2 = "press_state_cb2";
	String ANSWER_STATE_CB = "answer_state_cb";
	String HANGUP_STATE_CB = "hangup_state_cb";
	String IP_SETTING_CB = "ip_setting_cb";
	String LOCAL_PHONE = "local_phone";
	String AUTO_DIALING_CB = "auto_dialing_cb";
	
	Dialog dialog;
	
	String number = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(this).inflate(R.layout.setting_item_7_dialing, null);
		
		setContentView(view);
		
		dialing_checkbox_state = getSharedPreferences(SF_NAME, 0);
		editor = dialing_checkbox_state.edit();
		
		press_state_cb1 = (CheckBox) view.findViewById(R.id.press_state_cb1);
		press_state_cb2 = (CheckBox) view.findViewById(R.id.press_state_cb2);
		answer_state_cb = (CheckBox) view.findViewById(R.id.answer_state_cb);
		hangup_state_cb = (CheckBox) view.findViewById(R.id.hangup_state_cb);
		ip_setting_cb = (CheckBox) view.findViewById(R.id.ip_setting_cb);
		auto_dialing_cb = (CheckBox) view.findViewById(R.id.auto_dialing_cb);
		
		tips_add_layout = (LinearLayout) view.findViewById(R.id.tips_add_layout);
		et_telephone = (EditText) view.findViewById(R.id.et_telephone);
		et_telephone.setInputType(InputType.TYPE_CLASS_NUMBER);
		phone = (TextView) view.findViewById(R.id.phone);
		
		press_state_cb1.setOnCheckedChangeListener(onCheck);
		press_state_cb2.setOnCheckedChangeListener(onCheck);
		answer_state_cb.setOnCheckedChangeListener(onCheck);
		hangup_state_cb.setOnCheckedChangeListener(onCheck);
		ip_setting_cb.setOnCheckedChangeListener(onCheck);
		auto_dialing_cb.setOnCheckedChangeListener(onCheck);
		
		
		press_state_cb1.setChecked(dialing_checkbox_state.getBoolean(PRESS_STATE_CB1, true));
		press_state_cb2.setChecked(dialing_checkbox_state.getBoolean(PRESS_STATE_CB2, false));
		answer_state_cb.setChecked(dialing_checkbox_state.getBoolean(ANSWER_STATE_CB, true));
		hangup_state_cb.setChecked(dialing_checkbox_state.getBoolean(HANGUP_STATE_CB, false));
		ip_setting_cb.setChecked(dialing_checkbox_state.getBoolean(IP_SETTING_CB, false));
		auto_dialing_cb.setChecked(dialing_checkbox_state.getBoolean(AUTO_DIALING_CB, false));
		
		
	}
	
	private OnCheckedChangeListener onCheck = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.press_state_cb1:
				editor.putBoolean(PRESS_STATE_CB1, press_state_cb1.isChecked());
				editor.commit();
				break;
			case R.id.press_state_cb2:
	            editor.putBoolean(PRESS_STATE_CB2, press_state_cb2.isChecked());
				editor.commit();
				break;
			case R.id.answer_state_cb:
				
				editor.putBoolean(ANSWER_STATE_CB, answer_state_cb.isChecked());
				editor.commit();
				break;
			case R.id.hangup_state_cb:
				editor.putBoolean(HANGUP_STATE_CB, hangup_state_cb.isChecked());
				editor.commit();
				break;
			case R.id.ip_setting_cb:
				
				SharedPreferences sf = getSharedPreferences("com.dongji.app.addressbook.dialingsetting", 0);
				String localPhoneNumber = sf.getString(LOCAL_PHONE, "");
				
//				System.out.println("localPhoneNumber ----- > "+localPhoneNumber);
				
				if(ip_setting_cb.isChecked()){
					
					phone.setText(localPhoneNumber);
					
					if (localPhoneNumber.equals("")) {
					
						dialog = new Dialog(DialingSettingActivity.this,R.style.theme_myDialog_activity);
						dialog.setContentView(R.layout.phone_dialog);
						dialog.setCanceledOnTouchOutside(true);
						dialog.show();
						
						et_telephone = (EditText) dialog.findViewById(R.id.et_telephone);
						btn_add_tips_no = (Button) dialog.findViewById(R.id.btn_add_tips_no);
						btn_add_tips_yes = (Button) dialog.findViewById(R.id.btn_add_tips_yes);
						
						btn_add_tips_yes.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								String phoneNumber = et_telephone.getText().toString();
//								
								if(phoneNumber.equals("")){
									Toast.makeText(DialingSettingActivity.this, "本机号码不能为空，请重新输入！", Toast.LENGTH_SHORT).show();
									return ;
								}
								
								dialog.cancel();
								
							}
						});
						
						btn_add_tips_no.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								et_telephone.setText("");
								
								dialog.cancel();
								
							}
						});
						
						dialog.setOnCancelListener(new OnCancelListener() {
							
							@Override
							public void onCancel(DialogInterface dialog) {
								
								String phonestr = et_telephone.getText().toString();
								
								if (phonestr.length()==0) {
									
									ip_setting_cb.setChecked(false);
									number = "";
									phone.setText("");
								} else {
									
									ip_setting_cb.setChecked(true);
									phone.setText(phonestr);
									
								}
								
								editor.putString(LOCAL_PHONE, phonestr);
								editor.putBoolean(IP_SETTING_CB, ip_setting_cb.isChecked());
								editor.commit();
								
							}
						});
					}
				} else {
					
					number = "";
					phone.setText(number);
					ip_setting_cb.setChecked(false);
					
					editor.putString(LOCAL_PHONE, number);
					editor.putBoolean(IP_SETTING_CB, ip_setting_cb.isChecked());
					editor.commit();
				}
				
				break;
			case R.id.auto_dialing_cb:
				editor.putBoolean(AUTO_DIALING_CB, auto_dialing_cb.isChecked());
				editor.commit();
				break;
			default:
				break;
			}
		}
	};
}
