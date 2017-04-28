package com.dongji.app.addressbook;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 加密内容
 * 
 * @author Administrator
 *
 */
public class EncryptionActivity extends Activity {

	public View view;
	
	LinearLayout pwd1_layout;
	LinearLayout pwd2_layout;
	LinearLayout setting_pwd_layout;
	LinearLayout setting_answer_layout;
	LinearLayout setting_over_layout;
	
	TextView tips;
	
	EditText et_pwd_1;
	EditText et_pwd_2;
	
	TextView tv_ask;
	EditText et_answer;
	
	EditText et_for_1;
	EditText et_for_2;
	
	EditText et_new_answer;
	EditText et_new_ask;
	
	Button submit;
	Button forget_pwd;
	Button btn_findpwd_no;
	Button btn_findpwd_yes;
	Button btn_yes;
	
	Button re_setting_ask;
	Button re_setting_pwd;
	Button return_btn;
	
	SharedPreferences encryption = null;
	SharedPreferences.Editor editor = null;
	
    public static final String SF_NAME = "com.dongji.app.addressbook.encryption";
	
	public static final String KEY_ISENCRYPTION = "isEncryption";
	public static final String KEY_PWD = "pwd";
	public static final String KEY_PIN = "pin";
	public static final String KEY_ISREINPUTPWD = "isReInputPwd";
	public static final String KEY_DECRYPTION = "decryption";
	
	private final String KEY_ASK = "ask";
	private final String KEY_ANSWER= "answer" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(this).inflate(R.layout.setting_item_6_encryption, null);
		
		setContentView(view);
		
		encryption = getSharedPreferences(SF_NAME, 0);
		
		pwd1_layout = (LinearLayout) view.findViewById(R.id.pwd1_layout);
		pwd2_layout = (LinearLayout) view.findViewById(R.id.pwd2_layout);
		setting_pwd_layout = (LinearLayout) view.findViewById(R.id.setting_pwd_layout);
		setting_answer_layout = (LinearLayout) view.findViewById(R.id.setting_answer_layout);
		setting_over_layout = (LinearLayout) view.findViewById(R.id.setting_over_layout); 
		
		tips = (TextView) view.findViewById(R.id.tips);
		et_pwd_1 = (EditText) view.findViewById(R.id.et_pwd_1);
		et_pwd_2 = (EditText) view.findViewById(R.id.et_pwd_2);
		et_for_1 = (EditText) view.findViewById(R.id.et_for_1);
		et_for_2 = (EditText) view.findViewById(R.id.et_for_2);
		
		btn_yes = (Button) view.findViewById(R.id.btn_yes);
		forget_pwd = (Button) view.findViewById(R.id.forgetpwd);
		submit = (Button) view.findViewById(R.id.submit);
		re_setting_ask = (Button) view.findViewById(R.id.re_setting_ask);
		re_setting_pwd = (Button) view.findViewById(R.id.re_setting_pwd);
		return_btn = (Button) view.findViewById(R.id.return_btn);
		
		submit.setOnClickListener(onclik);
		forget_pwd.setOnClickListener(onclik);
		btn_yes.setOnClickListener(onclik);
		re_setting_ask.setOnClickListener(onclik);
		re_setting_pwd.setOnClickListener(onclik);
		return_btn.setOnClickListener(onclik);
		
		loadPwdData();
		
	}
	
	public void loadPwdData(){
		String pwd = encryption.getString(KEY_PWD, KEY_PWD);
		String answer = encryption.getString(KEY_ANSWER, "");
		boolean isReInput = encryption.getBoolean(KEY_ISREINPUTPWD, false);
		boolean isDecryption = encryption.getBoolean(KEY_DECRYPTION, false);

		if(!pwd.equals(KEY_PWD)){  //设置密码、问题后，再次进入解密操作
			
			if (!answer.equals("")) { //设置问题
				
				if (isDecryption) {  //判断是否解密，如果已解密，那么在未退出程序，都不需要再次解密，否则需要再次解密
					
					setting_pwd_layout.setVisibility(View.GONE);
					setting_answer_layout.setVisibility(View.GONE);
					setting_over_layout.setVisibility(View.VISIBLE);
					
				} else {
					
					setting_pwd_layout.setVisibility(View.VISIBLE);
					setting_over_layout.setVisibility(View.GONE);
				
					tips.setText(getResources().getString(R.string.searchpwd));
					et_pwd_1.setText("");
					forget_pwd.setVisibility(View.VISIBLE);
					pwd2_layout.setVisibility(View.GONE);
					((TextView)view.findViewById(R.id.tv_pw_text)).setVisibility(View.GONE);
					setting_answer_layout.setVisibility(View.GONE);
				}
			} else { //如果设置了密码，但是没有设置问题的话，进入加密界面后要求设置问题
				
				setting_pwd_layout.setVisibility(View.GONE);
				setting_answer_layout.setVisibility(View.VISIBLE);
				setting_over_layout.setVisibility(View.GONE);
			}
			
		}else{ //设置加密密码
			
			tips.setText(getResources().getString(R.string.settingpwd));
			forget_pwd.setVisibility(View.GONE);
			pwd2_layout.setVisibility(View.VISIBLE);
			setting_answer_layout.setVisibility(View.GONE);
			
		}
		
	}
	
	private OnClickListener onclik = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.submit:
				String pwd1 = et_pwd_1.getText().toString();
				String pwd2 = et_pwd_2.getText().toString();
				
				if(pwd2_layout.getVisibility() == View.VISIBLE){    //第一次设置密码操作
					if(pwd1.equals("")||pwd2.equals(""))
					{
						Toast.makeText(EncryptionActivity.this, "请先输入密码", Toast.LENGTH_SHORT).show();
					}else{
						
						if (!pwd1.equals(pwd2)) {
							Toast.makeText(EncryptionActivity.this,getResources().getString(R.string.pwd_tips) , Toast.LENGTH_SHORT).show();
							et_pwd_2.setText("");
							return;
						}
						
						savePwd(pwd1,pwd2);
						
						setting_pwd_layout.setVisibility(View.GONE);
						setting_answer_layout.setVisibility(View.VISIBLE);
					}
					
				}else{                      //设置密码、问题后，输入密码进行解密操作
					if(!pwd1.equals("")){
						
						String pwd = encryption.getString(KEY_PWD,KEY_PWD);
						if(pwd1.equals(pwd)){
							
							editor = encryption.edit();
//							editor.putBoolean(mainActivity.ISENCRYPTION, false);
							editor.putBoolean(KEY_ISREINPUTPWD, false);
							editor.putBoolean(KEY_DECRYPTION, true); //是否解密
							editor.commit();
							
							MainActivity.isEncryption = false;
							
							setting_over_layout.setVisibility(View.VISIBLE);
							setting_pwd_layout.setVisibility(View.GONE);
							setting_answer_layout.setVisibility(View.GONE);
							
							
							MainActivity.isChangeEnData = true;
							MainActivity.isEncryption = false;
							
							
						}else{
							Toast.makeText(EncryptionActivity.this,getResources().getString(R.string.pwd_error) , Toast.LENGTH_SHORT).show();
							et_pwd_1.setText("");
						}
					}else{
						Toast.makeText(EncryptionActivity.this,getResources().getString(R.string.pwd_null) , Toast.LENGTH_SHORT).show();
					}
				}
				break;
				
			case R.id.forgetpwd:        //忘记密码操作
				final Dialog dialog = new Dialog(EncryptionActivity.this,R.style.theme_myDialog_activity);
				dialog.setContentView(R.layout.forget_pwd_dialog);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				
				tv_ask = (TextView) dialog.findViewById(R.id.tv_ask);
				et_answer = (EditText) dialog.findViewById(R.id.et_answer);
				
				String sf_ask =  encryption.getString(KEY_ASK,"");
				final String sf_answer = encryption.getString(KEY_ANSWER,"");
				
				tv_ask.setText(sf_ask);
				
				btn_findpwd_no = (Button) dialog.findViewById(R.id.btn_no);
				btn_findpwd_yes = (Button) dialog.findViewById(R.id.btn_yes);
				
				btn_findpwd_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String answer = et_answer.getText().toString();
						
						if(!answer.equals("")) {
							
							if(sf_answer.equals(answer)){
								dialog.cancel();
								settingNewPwdDialog();
							} else {
								
								et_answer.setText("");
								Toast.makeText(EncryptionActivity.this,"问题答案输入不正确，请重新输入", Toast.LENGTH_SHORT).show();
								return;
								
							}
							
						} else {
							
							Toast.makeText(EncryptionActivity.this,"请输入安全问题答案", Toast.LENGTH_SHORT).show();
							return;
							
						}
						
					}
				});
				
				btn_findpwd_no.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				
				
				break;
			case R.id.btn_yes:       //第一次问题设置操作
				
				String ask = et_for_1.getText().toString();
				String answer_for = et_for_2.getText().toString();
				
				if(ask.equals("") || answer_for.equals(""))
					Toast.makeText(EncryptionActivity.this,"问题与答案不能为空，请重新输入", Toast.LENGTH_SHORT).show();
				else
					saveAsk(ask,answer_for);
				
				break;
			
			case R.id.re_setting_ask:          //修改问题操作
				
				final Dialog new_ask_dialog = new Dialog(EncryptionActivity.this,R.style.theme_myDialog_activity);
				new_ask_dialog.setContentView(R.layout.setting_new_answer_dialog);
//				new_ask_dialog.setTitle("修改安全问题");
				new_ask_dialog.setCanceledOnTouchOutside(true);
				new_ask_dialog.show();
				
				Button btn_no =  (Button) new_ask_dialog.findViewById(R.id.btn_new_cancel);
				Button btn_yes = (Button) new_ask_dialog.findViewById(R.id.btn_new_yes);
				
				et_new_answer = (EditText) new_ask_dialog.findViewById(R.id.et_new_answer);
				et_new_ask = (EditText) new_ask_dialog.findViewById(R.id.et_new_ask);
				
				btn_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						String new_answer = et_new_answer.getText().toString();
						String new_ask = et_new_ask.getText().toString();
						
						if(new_answer.equals("") || new_ask.equals("")) {
							Toast.makeText(EncryptionActivity.this,"新问题与答案不能为空，请重新输入", Toast.LENGTH_SHORT).show();
							return;
						} else {
						
							editor = encryption.edit();
							editor.putString(KEY_ASK, new_ask);
							editor.putString(KEY_ANSWER, new_answer);
							editor.commit();
						}
						
						new_ask_dialog.cancel();
					}
				});
				
				btn_no.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new_ask_dialog.cancel();
					}
				});
			
				
				break;
			case R.id.re_setting_pwd:     //重设密码操作
				settingNewPwdDialog();
				break;
				
			case R.id.return_btn:      //返回键操作
				
				finish();
				
				break;
			default:
				break;
			}
		}
	};
	
	private void settingNewPwdDialog(){
		final Dialog new_pwd_dialog = new Dialog(EncryptionActivity.this,R.style.theme_myDialog_activity);
		new_pwd_dialog.setContentView(R.layout.setting_new_pwd_dialog);
		new_pwd_dialog.setCanceledOnTouchOutside(true);
		new_pwd_dialog.show();
		
		Button btn_no =  (Button) new_pwd_dialog.findViewById(R.id.btn_newpwd_cancel);
		Button btn_yes = (Button) new_pwd_dialog.findViewById(R.id.btn_newpwd_yes);
		
		btn_yes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText new_pwd_1 = (EditText) new_pwd_dialog.findViewById(R.id.et_new_pwd);
				EditText new_pwd_2 = (EditText) new_pwd_dialog.findViewById(R.id.et_new_repwd);
				
				String new_pwd1 = new_pwd_1.getText().toString();
				String new_pwd2 = new_pwd_2.getText().toString();
				
				if(new_pwd1.equals("") || new_pwd2.equals("")) {
					Toast.makeText(EncryptionActivity.this,"新密码与确认密码不能为空，请重新输入", Toast.LENGTH_SHORT).show();
					return;
				}
				else {
					
					if (!new_pwd1.equals(new_pwd2)) {
						Toast.makeText(EncryptionActivity.this,getResources().getString(R.string.pwd_tips) , Toast.LENGTH_SHORT).show();
						et_pwd_2.setText("");
						return;
					}
					
					savePwd(new_pwd_1.getText().toString(), new_pwd_2.getText().toString());
					
				}
					
				new_pwd_dialog.cancel();
			}
		});
		
		btn_no.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new_pwd_dialog.cancel();
			}
		});
	}
	
	private void savePwd(String pwd1,String pwd2){
		if(pwd1.equals(pwd2)){
			editor = encryption.edit();
			editor.putString(KEY_PWD, pwd2);
			editor.commit();
			
			MainActivity.isEncryption = true;
			
			editor = encryption.edit();
			editor.putBoolean(KEY_ISENCRYPTION, true);
			editor.commit();
			
		}else{
			Toast.makeText(EncryptionActivity.this,getResources().getString(R.string.pwd_tips) , Toast.LENGTH_SHORT).show();
			et_pwd_2.setText("");
		}
	}
	
	private void saveAsk(String ask,String answer){
		
		editor = encryption.edit();
		editor.putString(KEY_ASK, ask);
		editor.putString(KEY_ANSWER, answer);
		editor.putBoolean(KEY_ISREINPUTPWD, true);
		editor.commit();
		
		setting_over_layout.setVisibility(View.VISIBLE);
		setting_pwd_layout.setVisibility(View.GONE);
		setting_answer_layout.setVisibility(View.GONE);
		
	}
	
}
