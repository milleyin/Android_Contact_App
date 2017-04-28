package com.dongji.app.addressbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.ContactBean.EmailInfo;
import com.dongji.app.entity.ContactBean.PhoneInfo;
import com.dongji.app.entity.NetWorkResult;
import com.dongji.app.entity.RemindBean;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.net.NetworkUnit;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.LoginTool;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.tool.ToolUnit;

public class SystemBackUp implements OnClickListener{

	MainActivity mainActivity;
	View backupView;
	Button buttonOk;
	Button buttonCancel;
	CheckBox contactCheckBox;
	CheckBox messageCheckBox;
	CheckBox callRecordCheckBox;
	CheckBox remindCheckBox;
	CheckBox collectionCheckBox;
	ProgressBar progressBar;
	boolean isFinish;
	MyDatabaseUtil myDatabaseUtil;
//	TelephonyManager tm ;
//	PopupWindow popupWindow;
	PopupWindow recoveryWindow;
	
	ScrollView scrollView;
	/*EditText email;
	EditText password;
	EditText repassword;
	Button register;
	Button loginButton;
	Button findPassword;
	Button confirm;*/
	LinearLayout linearLayout;
	NetworkUnit networkUnit;
	SharedPreferences sPreferences;
	LinearLayout backup_recovery;
	Button onekey_backup;
	Button onekey_recovery;
	LinearLayout showContact;
	LinearLayout showLoginContact;
	EditText recovery_email;
	EditText re_confirm_passwords;
	LinearLayout confirmPasswordLinLayout;//第一次登录显示出来
	EditText recovery_password;
    Button recovery_confirm;
    String recoveryEmail;
    String recoveryPassword;
    String cookieid;
    boolean islogin=false;
    String selectBackUpRecovery="";//0 表示备份，1 表示恢复
    CheckBox recoveryContactCheckBox;
	CheckBox recoveryMessageCheckBox;
	CheckBox recoveryCallRecordCheckBox;
	CheckBox recoveryRemindCheckBox;
	CheckBox recoveryCollectionCheckBox;
	Button recoveryOk;
//	Button recoveryCancel;
	ProgressBar recoveryProgressBar;
    //恢复选择
    LinearLayout recovery_select_contact;
    
    
    //通知栏
	//通知
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	
	//通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	private RemoteViews view = null;
	NetWorkResult netWorkResult;
	private long dataIdOfPhoto;
	private ContentValues photoContentValues = new ContentValues();
	boolean b = true;
	WifiInfo info;
//	TextView backupProgress;
//	TextView recoveryProgress;
	
	
	
	ProgressDialog progressDialog;
	
	
	
	
	Button cacelBackUp;
	Button switchingAccount;
	Button accountLogin;
	Button createAcount;
	
	private Dialog backUpDialog = null;
	private Dialog localBackUpDialog = null;
	Button btn_ok = null;
	TextView tv_tips = null;
	TextView finish_tips = null;
	ProgressBar first_pro_bar = null;
	Button local_backup = null;
	Button local_recovery = null;
	LinearLayout first_backup_ly;
	TextView tips_top;
	ImageView top_img;
	
	CheckBox local_backup_contacts;
	CheckBox local_backup_message;
	CheckBox local_backup_remind;
	CheckBox local_backup_calllog;
	CheckBox local_backup_collection;
	TextView save_url;
	TextView local_tips;
	Button btn_local_backup;
	Button btn_local_recovery;
	Button btn_backup_finish;
	Button btn_backup_cancel;
	ProgressBar local_pro_bar;
	LinearLayout local_backup_recovery_ly;
	TextView tips;
//	TextView local_backup_progress;
	
	String path = null;
	int type = 0;
	int click_type = 0;
	boolean local_contacts = true;
	boolean local_message = true;
	boolean local_remind = true;
	boolean local_calllog = true;
	boolean local_collection = true;
	
	boolean isCheckedContacts = true;
	boolean isCheckedMessage = true;
	boolean isCheckedRemind = true;
	boolean isCheckedCalllog = true;
	boolean isCheckedCollection = true;
	
	boolean isTip = true;
	int select_count = 0;
	
	LocalBackUpData localBackUpData = null;
	
	SharedPreferences mSharedPreferences = null;
	Thread backupThread;
	Thread recoveryThread;
	
	boolean isBackupCancel=true;
//	boolean isrevoreyCancel=true;
	
	
	TextView tv_tips_bottom;
	TextView tv_tips_bottom_add;
	TextView tv_tips_top_backup;
	
	TextView tv_tips_bottom_recovery;
	TextView tv_tips_bottom_recovery_add;
	
	TextView tv_tips_top_recovery;
	
	ImageView finshRecovery;
	
	
	
	Button bt_recovery_finish;
	
	ScrollView local_backup_recovery;
	
	
	boolean backupFinish=false;//备份完成
	boolean backuping=false;//备份中
	boolean isLoginIng=false;//登录中
	boolean recoveryFinish=false;
	boolean recoverying=false;//备份中
	
	//本地备份，恢复状态
	boolean is_localBackUp = false; //是否在备份
	boolean is_localRecovery = false; //是否在恢复
	boolean is_localBackUp_Finish = false; //是否开始备份并且已备份完成
	boolean is_localRecoveryp_Finish = false; //是否开始恢复并且已恢复完成
	
	
	public static String onlys="#DongJiContact#";
	
	long 	MY_CONTACT_ID = -1; //机主ID
	
	
	
	
	
	    //通知栏
		//通知
		private NotificationManager cloudBackupRecoveryNotification = null;
		private Notification cloudBackupRecovery = null;
		
		//通知栏跳转Intent
		private Intent BackupRecoveryIntent = null;
		private PendingIntent BackupRecoveryPendingIntent = null;
		private RemoteViews BackupRecoveryView = null;
	   
		int click_backup_recovery=0;
		
		boolean is_tips_backup = false;
		boolean is_tips_recovery = false;
	Handler handle=new Handler()
	{
		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			switch (msg.what) {
			case -2:
				break;
			case 1:
				break;
			case 2:
				progressDialog=new ProgressDialog(mainActivity);
				progressDialog.setMessage("备份中");
				progressDialog.show();
				break;
			case 3:
				buttonOk.setVisibility(View.GONE);
				buttonCancel.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				break;
			case 4:
				progressBar.setVisibility(View.GONE);
				showContact.setVisibility(View.GONE);
				cacelBackUp.setVisibility(View.VISIBLE);
				
				confirmPasswordLinLayout.setVisibility(View.VISIBLE);
	        	accountLogin.setVisibility(View.GONE);
	        	createAcount.setVisibility(View.GONE);
	        	recovery_confirm.setVisibility(View.VISIBLE);
	        	switchingAccount.setVisibility(View.VISIBLE);
				
				showLoginContact.setVisibility(View.VISIBLE);
				SharedPreferences shPreferences=mainActivity.getSharedPreferences("login", 0);
	        	recoveryEmail=shPreferences.getString("username","");
	        	recoveryPassword=shPreferences.getString("password","");
	        	if(recoveryEmail!=null && !"".equals(recoveryEmail))
	        	{
	        		confirmPasswordLinLayout.setVisibility(View.GONE);
	        		recovery_email.setText(recoveryEmail);
	        		recovery_password.setText(recoveryPassword);
	        	}
				break;
				
			case 5:
					backUpRecoveryFinish();
					click_backup_recovery=4;
					handle.sendEmptyMessage(37);
					Toast.makeText(mainActivity, "备份完成", Toast.LENGTH_LONG).show();
					tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
					
					break;
			case 6://登录
				
				if(islogin)
				{
					backup_recovery.setVisibility(View.GONE);
			    	scrollView.setVisibility(View.VISIBLE);
			    	showContact.setVisibility(View.GONE);
			    	showLoginContact.setVisibility(View.GONE);
			    	recoveryOk.setVisibility(View.VISIBLE);
			    	recovery_select_contact.setVisibility(View.VISIBLE);
			    	
			    	recoveryContactCheckBox.setChecked(true);
			    	recoveryMessageCheckBox.setChecked(true);
			    	recoveryCallRecordCheckBox.setChecked(true);
			    	recoveryRemindCheckBox.setChecked(true);
			    	recoveryCollectionCheckBox.setChecked(true);
			    	
			    	
			    	recoveryContactCheckBox.setClickable(false);
			    	recoveryMessageCheckBox.setClickable(false);
			    	recoveryCallRecordCheckBox.setClickable(false);
			    	recoveryRemindCheckBox.setClickable(false);
			    	recoveryCollectionCheckBox.setClickable(false);
			    	
			    	
			    	
			    	   
			    	/* SharedPreferences userInfo = mainActivity.getSharedPreferences("select_backup", 0);  
			    	
			 		boolean contacts= userInfo.getBoolean("select_contact",false);
					boolean messages= userInfo.getBoolean("select_message",false);
					boolean reminds= userInfo.getBoolean("select_remind",false);
					boolean callrecords= userInfo.getBoolean("select_call_record",false);
					boolean collecttions= userInfo.getBoolean("select_collection",false);
					
	               if(contacts)
	                {
	                	recoveryContactCheckBox.setOnCheckedChangeListener(null);
	                	recoveryContactCheckBox.setChecked(true);
                        
	                }
	                else{
	                	
	                	recoveryContactCheckBox.setChecked(false);
	                	recoveryContactCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 								recoveryContactCheckBox.setChecked(false);
	 							}
	 						}
	 					});
	                }
	                if(messages)
	                {
	                	recoveryMessageCheckBox.setOnCheckedChangeListener(null);
	                	recoveryMessageCheckBox.setChecked(true);
	                	
	                }else
	                {
	                	recoveryMessageCheckBox.setChecked(false);
	                	  recoveryMessageCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								
								public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
									// TODO Auto-generated method stub
									if(isChecked)
									{
										Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
										recoveryMessageCheckBox.setChecked(false);
									}
									
								}
							});
	                }
	                if(callrecords)
	                {
	                	recoveryCallRecordCheckBox.setOnCheckedChangeListener(null);
	                	recoveryCallRecordCheckBox.setChecked(true);
	                	
	                }
	                else
	                {
	                	recoveryCallRecordCheckBox.setChecked(false);
	                	  recoveryCallRecordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	  						
	  						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	  							// TODO Auto-generated method stub
	  							if(isChecked)
	  							{
	  								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	  								recoveryCallRecordCheckBox.setChecked(false);
	  							}
	  							
	  						}
	  					});
	                }
	                if(reminds)
	                {
	                	recoveryRemindCheckBox.setOnCheckedChangeListener(null);
	                	recoveryRemindCheckBox.setChecked(true);
	                
	                }
	                else
	                {
	                	recoveryRemindCheckBox.setChecked(false);
	                	 recoveryRemindCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 								recoveryRemindCheckBox.setChecked(false);
	 							}
	 							
	 						}
	 					});
	                }
	                if(collecttions)
	                {
	                	recoveryCollectionCheckBox.setOnCheckedChangeListener(null);
	                	recoveryCollectionCheckBox.setChecked(true);
	                	
	                }
	                else
	                {
	                	recoveryCollectionCheckBox.setChecked(false);
	                	 recoveryCollectionCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 								recoveryCollectionCheckBox.setChecked(false);
	 							}
	 							
	 						}
	 					});
	                }*/
				}else
				{
					backup_recovery.setVisibility(View.GONE);
			    	scrollView.setVisibility(View.VISIBLE);
			    	showContact.setVisibility(View.GONE);
			    	showLoginContact.setVisibility(View.VISIBLE);
			    	confirmPasswordLinLayout.setVisibility(View.GONE);
			    	recovery_email.setText(recoveryEmail);
			    	recovery_password.setText(recoveryPassword);
			    	recovery_confirm.setVisibility(View.GONE);
			    	switchingAccount.setVisibility(View.GONE);
			    	accountLogin.setVisibility(View.VISIBLE);
			    	
				}
				break;
			case 7:
				if(progressBar.getVisibility()==View.VISIBLE)
				{
					progressBar.setVisibility(View.GONE);
				}
				backupFinish=true;
				backUpRecoveryFinish();
				handle.sendEmptyMessage(37);
				click_backup_recovery=4;
				Toast.makeText(mainActivity, "备份完成", Toast.LENGTH_LONG).show();
				tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				
				break;
			case 8:
    				break;
			case 9:
				backUpRecoveryFinish();
				break;
			case 10:
				backup_recovery.setVisibility(View.GONE);
		    	scrollView.setVisibility(View.VISIBLE);
		    	showContact.setVisibility(View.GONE);
		    	showLoginContact.setVisibility(View.GONE);
		    	recovery_select_contact.setVisibility(View.VISIBLE);
		    	recoveryOk.setVisibility(View.VISIBLE);
		    	tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				break;
			case 11:
				Toast.makeText(mainActivity, "连接超时，请检查网络，或稍后再试", Toast.LENGTH_LONG).show();
				break;
				
			case 12:
				recoveryOk.setVisibility(View.GONE);
//				recoveryCancel.setVisibility(View.VISIBLE);
				recoveryProgressBar.setVisibility(View.VISIBLE);
				break;
			case 13:
				Toast.makeText(mainActivity, "请选择需要备份的选项", Toast.LENGTH_SHORT).show();
				break;
			case 14:
				Toast.makeText(mainActivity, "备份失败", Toast.LENGTH_SHORT).show();
				tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				break;
			case 15:
				Toast.makeText(mainActivity, "恢复失败,请重新再试", Toast.LENGTH_SHORT).show();
				if(recoveryProgressBar.getVisibility()==View.VISIBLE)
			      {
					  recoveryProgressBar.setVisibility(View.GONE);
			      }
				  recoveryOk.setVisibility(View.VISIBLE);
				  
				   
	                recoveryContactCheckBox.setVisibility(View.VISIBLE);
	                recoveryMessageCheckBox.setVisibility(View.VISIBLE);
	                recoveryCallRecordCheckBox.setVisibility(View.VISIBLE);
	                recoveryRemindCheckBox.setVisibility(View.VISIBLE);
	                recoveryCollectionCheckBox.setVisibility(View.VISIBLE);
	                
	                recoveryContactCheckBox.setChecked(true);
	                recoveryMessageCheckBox.setChecked(true);
	                recoveryCallRecordCheckBox.setChecked(true);
	                recoveryRemindCheckBox.setChecked(true);
	                recoveryCollectionCheckBox.setChecked(true);
	                
	                recoveryContactCheckBox.setClickable(false);
	                recoveryMessageCheckBox.setClickable(false);
	                recoveryCallRecordCheckBox.setClickable(false);
	                recoveryRemindCheckBox.setClickable(false);
	                recoveryCollectionCheckBox.setClickable(false);
	                
	                
	               /* SharedPreferences userInfoReco = mainActivity.getSharedPreferences("user_backup_data", 0);  
	                boolean contactsReco= userInfoReco.getBoolean("contact", true);  
	                boolean messagesReco=userInfoReco.getBoolean("message", true);  
	                boolean remindsReco= userInfoReco.getBoolean("remind", true);  
	                boolean callrecordsReco=userInfoReco.getBoolean("callrecord", true);  
	                boolean collecttionsReco= userInfoReco.getBoolean("collecttion", true);
	                if(contactsReco)
					{
	                	recoveryContactCheckBox.setChecked(true);
					}else
					{
						recoveryContactCheckBox.setChecked(false);
	                	 recoveryContactCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 							}
	 							recoveryContactCheckBox.setChecked(false);
	 						}
	 					});
					}
					if(messagesReco)
					{
						recoveryMessageCheckBox.setChecked(true);
					}
					else
					{
						recoveryMessageCheckBox.setChecked(false);
						recoveryMessageCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 							}
	 							recoveryMessageCheckBox.setChecked(false);
	 						}
	 					});
					}
					if(callrecordsReco)
					{
						recoveryCallRecordCheckBox.setChecked(true);
					}else
					{
						recoveryCallRecordCheckBox.setChecked(false);
						recoveryCallRecordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 							}
	 							recoveryCallRecordCheckBox.setChecked(false);
	 						}
	 					});
					}
					if(remindsReco)
					{
						recoveryRemindCheckBox.setChecked(true);
					}else
					{
						recoveryRemindCheckBox.setChecked(false);
						recoveryRemindCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 							}
	 							recoveryRemindCheckBox.setChecked(false);
	 						}
	 					});
					}
					if(collecttionsReco)
					{
						recoveryCollectionCheckBox.setChecked(true);
					}
					else
					{
						recoveryCollectionCheckBox.setChecked(false);
						recoveryCollectionCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 						
	 						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	 							// TODO Auto-generated method stub
	 							if(isChecked)
	 							{
	 								Toast.makeText(mainActivity, "你没有备份此项", Toast.LENGTH_LONG).show();
	 							}
	 							recoveryCollectionCheckBox.setChecked(false);
	 						}
	 					});
					}*/
					tv_tips_top_recovery.setText(mainActivity.getString(R.string.tv_recovery_cloud).toString());
					tv_tips_bottom_recovery.setText(mainActivity.getString(R.string.tv_recovery_cloud).toString());//请选择您要恢复的部分：
					tv_tips_bottom_recovery_add.setText(mainActivity.getString(R.string.tv_recovery_cloud_add).toString());//
	                
//				  recoveryCancel.setVisibility(View.GONE);
				break;
			case 16:
				Toast.makeText(mainActivity, "恢复完成", Toast.LENGTH_SHORT).show();
				
				
				backUpRecoveryFinish();
				break;
			case 17:
				Toast.makeText(mainActivity, "用户名已存在，请修改用户名！", Toast.LENGTH_SHORT).show();
				break;
			case 18:
				Toast.makeText(mainActivity, "邮箱已注册，请重新再试！", Toast.LENGTH_SHORT).show();
				break;
			
			case 23:
		        if(progressBar.getVisibility()==View.VISIBLE)
		        {
		        	progressBar.setVisibility(View.GONE);
		        }
		        buttonOk.setVisibility(View.VISIBLE);
				buttonCancel.setVisibility(View.GONE);
				break;
			case 24:
				  if(recoveryProgressBar.getVisibility()==View.VISIBLE)
			      {
					  recoveryProgressBar.setVisibility(View.GONE);
			      }
				  recoveryOk.setVisibility(View.VISIBLE);
//				  recoveryCancel.setVisibility(View.GONE);
				break;
			case 25:
				//备份选项
				Bundle bundle2=msg.getData();
				if(bundle2!=null)
				{
					boolean contacts=bundle2.getBoolean("contactCheckBox");
					boolean messages=bundle2.getBoolean("messageCheckBox");
					boolean callrecords=bundle2.getBoolean("callRecordCheckBox");
					boolean reminds=bundle2.getBoolean("remindCheckBox");
					boolean collections=bundle2.getBoolean("collectionCheckBox");
					
					if(contacts)
					{
						contactCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						contactCheckBox.setVisibility(View.GONE);
					}
					if(messages)
					{
						messageCheckBox.setButtonDrawable(R.drawable.dot);
					}
					else
					{
						messageCheckBox.setVisibility(View.GONE);
					}
					if(callrecords)
					{
						callRecordCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						callRecordCheckBox.setVisibility(View.GONE);
					}
					if(reminds)
					{
						remindCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						remindCheckBox.setVisibility(View.GONE);
					}
					if(collections)
					{
						collectionCheckBox.setButtonDrawable(R.drawable.dot);
					}
					else
					{
						collectionCheckBox.setVisibility(View.GONE);
					}
					tv_tips_top_backup.setText(mainActivity.getString(R.string.tv_tips_top_backup).toString());
					tv_tips_bottom_add.setText(mainActivity.getString(R.string.tv_cancel_add).toString());
					tv_tips_bottom.setText(Html.fromHtml(mainActivity.getString(R.string.tv_cancel).toString().replace("\"取消\"", "<font color='#3d8eba'>" + "\"取消\"" + "</font>")));
					 SharedPreferences userInfo = mainActivity.getSharedPreferences("user_backup_data", 0);  
	                    userInfo.edit().putBoolean("contact", contacts).commit();  
	                    userInfo.edit().putBoolean("message", messages).commit();  
	                    userInfo.edit().putBoolean("remind", reminds).commit();  
	                    userInfo.edit().putBoolean("callrecord", callrecords).commit();  
	                    userInfo.edit().putBoolean("collecttion", collections).commit();  
				}
				
				
				break;
			case 26:
				//恢复选项
				Bundle bundleRecovery=msg.getData();
				if(bundleRecovery!=null)
				{
					boolean contactsRecovery=bundleRecovery.getBoolean("contactCheckBoxRecovery");
					boolean messagesRecovery=bundleRecovery.getBoolean("messageCheckBoxRecovery");
					boolean callrecordsRecovery=bundleRecovery.getBoolean("callRecordCheckBoxRecovery");
					boolean remindsRecovery=bundleRecovery.getBoolean("remindCheckBoxRecovery");
					boolean collectionsRecovery=bundleRecovery.getBoolean("collectionCheckBoxRecovery");
					
					if(contactsRecovery)
					{
						recoveryContactCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						recoveryContactCheckBox.setVisibility(View.GONE);
					}
					if(messagesRecovery)
					{
						recoveryMessageCheckBox.setButtonDrawable(R.drawable.dot);
					}
					else
					{
						recoveryMessageCheckBox.setVisibility(View.GONE);
					}
					if(callrecordsRecovery)
					{
						recoveryCallRecordCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						recoveryCallRecordCheckBox.setVisibility(View.GONE);
					}
					if(remindsRecovery)
					{
						recoveryRemindCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						recoveryRemindCheckBox.setVisibility(View.GONE);
					}
					if(collectionsRecovery)
					{
						recoveryCollectionCheckBox.setButtonDrawable(R.drawable.dot);
					}
					else
					{
						recoveryCollectionCheckBox.setVisibility(View.GONE);
					}
					
					
				}
//				recoveryContactCheckBox.setButtonDrawable(R.drawable.dot);
//				recoveryMessageCheckBox.setButtonDrawable(R.drawable.dot);
//				recoveryCallRecordCheckBox.setButtonDrawable(R.drawable.dot);
//				recoveryRemindCheckBox.setButtonDrawable(R.drawable.dot);
//				recoveryCollectionCheckBox.setButtonDrawable(R.drawable.dot);
				
				
				tv_tips_top_recovery.setText(mainActivity.getString(R.string.tv_tips_top_recovery).toString());
				tv_tips_bottom_recovery.setText("");
				tv_tips_bottom_recovery_add.setText("");
				break;
			case 27:
				contactCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				messageCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				callRecordCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				remindCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				collectionCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				break;
			case 28:
				recoveryContactCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				recoveryMessageCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				recoveryCallRecordCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				recoveryRemindCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				recoveryCollectionCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
				break;
			case 29:
				Toast.makeText(mainActivity, "请选择需要恢复的选项", Toast.LENGTH_SHORT).show();
				break;
			case 30:
				Toast.makeText(mainActivity, "临时备份成功", Toast.LENGTH_SHORT).show();
				break;
			case 31:
				recoveryProgressBar.setVisibility(View.GONE);
				finshRecovery.setImageResource(R.drawable.recovery_finish_bg);
				bt_recovery_finish.setVisibility(View.VISIBLE);
				tv_tips_top_recovery.setText(mainActivity.getString(R.string.tv_tips_top_recovery_finish).toString());
				tv_tips_bottom_recovery.setText(Html.fromHtml(mainActivity.getString(R.string.tv_recovery_cloud_finish).toString().replace("\"完成\"", "<font color='#7e93a8'>" + "\"完成\""+ "</font>")));
				
				activateAllReminds();
				
				break;
			case 32:
				Toast.makeText(mainActivity, "用户名不存在", Toast.LENGTH_SHORT).show();
				tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				break;
			case 33:
				Toast.makeText(mainActivity, "密码错误", Toast.LENGTH_SHORT).show();
				tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				break;
			case 34:
				Toast.makeText(mainActivity, "密码或用户名错误", Toast.LENGTH_SHORT).show();
				tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				break;
			case 35://恢复中
				
				
				if(recoveryFinish)
				{
					 recoveryProgressBar.setVisibility(View.GONE);
					 
					 finshRecovery.setImageResource(R.drawable.recovery_finish_bg);
					 bt_recovery_finish.setVisibility(View.VISIBLE);
				}
				else
				{
					 recoveryProgressBar.setVisibility(View.VISIBLE);
				}
				
				 backup_recovery.setVisibility(View.GONE);
				 scrollView.setVisibility(View.VISIBLE);
				 recovery_select_contact.setVisibility(View.VISIBLE);
				
				 SharedPreferences userInfoRecovery = mainActivity.getSharedPreferences("user_backup_data", 0);  
				 
				boolean contacting= userInfoRecovery.getBoolean("contact",false);
				boolean messageing= userInfoRecovery.getBoolean("message",false);
				boolean reminding= userInfoRecovery.getBoolean("remind",false);
				boolean callrecording= userInfoRecovery.getBoolean("callrecord",false);
				boolean collecttioning= userInfoRecovery.getBoolean("collecttion",false);
					if(contacting)
					{
						recoveryContactCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						recoveryContactCheckBox.setVisibility(View.GONE);
					}
					if(messageing)
					{
						recoveryMessageCheckBox.setButtonDrawable(R.drawable.dot);
					}
					else
					{
						recoveryMessageCheckBox.setVisibility(View.GONE);
					}
					if(callrecording)
					{
						recoveryCallRecordCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						recoveryCallRecordCheckBox.setVisibility(View.GONE);
					}
					if(reminding)
					{
						recoveryRemindCheckBox.setButtonDrawable(R.drawable.dot);
					}else
					{
						recoveryRemindCheckBox.setVisibility(View.GONE);
					}
					if(collecttioning)
					{
						recoveryCollectionCheckBox.setButtonDrawable(R.drawable.dot);
					}
					else
					{
						recoveryCollectionCheckBox.setVisibility(View.GONE);
					}
					tv_tips_top_recovery.setText(mainActivity.getString(R.string.tv_tips_top_recovery).toString());
					tv_tips_bottom_recovery.setText("");
					tv_tips_bottom_recovery_add.setText("");
				
				
				break;
			case 36:
				
				    SharedPreferences userInfoFailure = mainActivity.getSharedPreferences("select_backup", 0);  
					boolean contactFailure= userInfoFailure.getBoolean("select_contact",false);
					boolean messageFailure= userInfoFailure.getBoolean("select_message",false);
					boolean remindFailure= userInfoFailure.getBoolean("select_remind",false);
					boolean callrecordFailure= userInfoFailure.getBoolean("select_call_record",false);
					boolean collecttionFailure= userInfoFailure.getBoolean("select_collection",false);
					contactCheckBox.setVisibility(View.VISIBLE);
			    	messageCheckBox.setVisibility(View.VISIBLE);
			    	callRecordCheckBox.setVisibility(View.VISIBLE);
			    	remindCheckBox.setVisibility(View.VISIBLE);
			    	collectionCheckBox.setVisibility(View.VISIBLE);
			    	 contactCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
			     	messageCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
			     	callRecordCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
			     	remindCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
			     	collectionCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
					 if(contactFailure)
		                {
			    	    	contactCheckBox.setChecked(true);
		                }
		                else
		                {
		                	contactCheckBox.setChecked(false);
		                }
					 
		                if(messageFailure)
		                {
		                	messageCheckBox.setChecked(true);
		                }else
		                {
		                	messageCheckBox.setChecked(false);
		                }
		                
		                if(callrecordFailure)
		                {
		                	callRecordCheckBox.setChecked(true);
		                }
		                else
		                {
		                	callRecordCheckBox.setChecked(false);
		                }
		                
		                if(remindFailure)
		                {
		                	remindCheckBox.setChecked(true);
		                }
		                else
		                {
		                	remindCheckBox.setChecked(false);
		                }
		                if(collecttionFailure)
		                {
		                	collectionCheckBox.setChecked(true);
		                }
		                else
		                {
		                	collectionCheckBox.setChecked(false);
		                }
		                buttonOk.setVisibility(View.VISIBLE);
		                buttonCancel.setVisibility(View.GONE);
		                showContact.setVisibility(View.VISIBLE);
			    	    progressBar.setVisibility(View.GONE);
					
			    	    tv_tips_top_backup.setText(mainActivity.getString(R.string.tv_backup_cloud).toString());
						tv_tips_bottom_add.setText(mainActivity.getString(R.string.tv_backup_add).toString());
						tv_tips_bottom.setText(Html.fromHtml(mainActivity.getString(R.string.tv_backup_cloud).toString().replace("\"备份到云端\"", "<font color='#3d8eba'>" + "\"备份到云端\"" + "</font>")));
				break;
				
				
			case 37:
				
				
				
				   if(click_backup_recovery == 1)
				   {
					   cloudBackupRecovery = new Notification(R.drawable.ic_launcher, "正在云端备份", System.currentTimeMillis());
				   }else if(click_backup_recovery == 2)
				   {
					   cloudBackupRecovery = new Notification(R.drawable.ic_launcher, "正在云端恢复", System.currentTimeMillis());
				   }
				  
				    //设置下载过程中，点击通知栏，回到主界
				   BackupRecoveryIntent = new Intent(mainActivity, MainActivity.class);
				   BackupRecoveryPendingIntent = PendingIntent.getActivity(mainActivity,0,BackupRecoveryIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				   
				    //设置通知栏显示内
				   BackupRecoveryView = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
				   BackupRecoveryView.setImageViewResource(R.id.image, R.drawable.ic_launcher);
				    if ( click_backup_recovery== 1) {
				    	BackupRecoveryView.setTextViewText(R.id.tips, "正在云端备份，请稍等！");
				    } else if (click_backup_recovery == 2) {
				    	BackupRecoveryView.setTextViewText(R.id.tips, "正在云端恢复，请稍等！");
				    }else if(click_backup_recovery==3)
				    {
				    	BackupRecoveryView.setTextViewText(R.id.tips, "已取消云端备份！");
				    	click_backup_recovery=0;
				    }else if(click_backup_recovery==4)
				    {
				    	BackupRecoveryView.setTextViewText(R.id.tips, "云端备份完成！");
				    	click_backup_recovery=0;
				    }else if(click_backup_recovery==5)
				    {
				    	BackupRecoveryView.setTextViewText(R.id.tips, "云端恢复完成！");
				    	click_backup_recovery=0;
				    }

	  				// 设置通知在状态栏显示的图标
				    cloudBackupRecovery.icon = R.drawable.ic_launcher;
	  				// 通知时发出的默认声音
				    cloudBackupRecovery.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
	  				// 通过RemoteViews 设置notification中View 的属性
				    cloudBackupRecovery.contentView = BackupRecoveryView;
				    cloudBackupRecovery.contentIntent = BackupRecoveryPendingIntent;
	  				// 这个可以理解为开始执行这个通知
				    cloudBackupRecovery.flags |= Notification.FLAG_AUTO_CANCEL;
	  				cloudBackupRecoveryNotification.notify(0, cloudBackupRecovery);
	  				break;
			
			
				
			default:
				break;
			}
		}
		
	};
	
	
	Handler local_handler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 1:
				
				if (local_backup_contacts.isChecked()){
					local_backup_contacts.setButtonDrawable(R.drawable.dot);
					
				} else {
					local_backup_contacts.setVisibility(View.GONE);
				}
				if (local_backup_message.isChecked()){
					local_backup_message.setButtonDrawable(R.drawable.dot);				
				} else {
					local_backup_message.setVisibility(View.GONE);
				}
				if (local_backup_remind.isChecked()){
					local_backup_remind.setButtonDrawable(R.drawable.dot);
				} else {
					local_backup_remind.setVisibility(View.GONE);
				}
				if (local_backup_calllog.isChecked()){
					local_backup_calllog.setButtonDrawable(R.drawable.dot);
				} else {
					local_backup_calllog.setVisibility(View.GONE);
				}
				if (local_backup_collection.isChecked()){
					local_backup_collection.setButtonDrawable(R.drawable.dot);
				} else {
					local_backup_collection.setVisibility(View.GONE);
				}
				
				break;
			case 2:
				local_backup_contacts.setVisibility(View.VISIBLE);
				local_backup_message.setVisibility(View.VISIBLE);
				local_backup_remind.setVisibility(View.VISIBLE);
				local_backup_calllog.setVisibility(View.VISIBLE);
				local_backup_collection.setVisibility(View.VISIBLE);
				local_backup_contacts.setButtonDrawable(R.drawable.checkbox_selector);
				local_backup_message.setButtonDrawable(R.drawable.checkbox_selector);
				local_backup_remind.setButtonDrawable(R.drawable.checkbox_selector);
				local_backup_calllog.setButtonDrawable(R.drawable.checkbox_selector);
				local_backup_collection.setButtonDrawable(R.drawable.checkbox_selector);
				break;
			case 3:
				
				is_localBackUp = false;
				is_localBackUp_Finish = false;
				
				//设置下载过程中，点击通知栏，回到主界
				updateIntent = new Intent(mainActivity, MainActivity.class);
				updatePendingIntent = PendingIntent.getActivity(mainActivity,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				
				view = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
			    view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
			    view.setTextViewText(R.id.tips, "已取消备份...");

  				// 设置通知在状态栏显示的图标
  				updateNotification.icon = R.drawable.ic_launcher;
  				// 通知时发出的默认声音
  				updateNotification.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
  				// 通过RemoteViews 设置notification中View 的属性
  				updateNotification.contentView = view;
  				updateNotification.contentIntent = updatePendingIntent;
  				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
  				updateNotificationManager.notify(0, updateNotification);
				
				Toast.makeText(mainActivity, "已取消备份！", Toast.LENGTH_SHORT).show();
				break;
			case 19:
				tips.setVisibility(View.GONE);
				first_backup_ly.setVisibility(View.GONE);
				btn_ok.setVisibility(View.VISIBLE);
				tv_tips.setVisibility(View.VISIBLE);
				
				String str = "已成功备份至：" + path+",点击\"完成\"完成备份";
				String str1 = "\"完成\"";
				
				tv_tips.setText(Html.fromHtml(str.replace(str1, "<font color='#7e93a8'>" + str1+ "</font>")));
				
				break;
			case 20:
				break;
			case 21:
				Toast.makeText(mainActivity, "联系人本地备份失败！", Toast.LENGTH_SHORT).show();
				break;
			case 22:
				Toast.makeText(mainActivity, "短信本地备份失败！", Toast.LENGTH_SHORT).show();
				break;
			case 25:
				
				is_localRecovery = false;
				is_localRecoveryp_Finish = true;
				
				if (top_img.getVisibility() == View.GONE) 
					top_img.setVisibility(View.VISIBLE);
				
				top_img.setBackgroundResource(R.drawable.backup_recovery_finish_1);
				
				tips_top.setText("已完成对您本机的以下相关数据的恢复：");
				str = "点击'完成'完成恢复：";
				str1 = "'完成'";
				local_tips.setText(Html.fromHtml(str.replace(str1, "<font color='#7e93a8'>" + str1+ "</font>")));
				
//				local_tips.setText("点击'完成'退出恢复");
				btn_local_backup.setVisibility(View.GONE);
				btn_local_recovery.setVisibility(View.GONE);
				local_tips.setVisibility(View.VISIBLE);
				btn_backup_finish.setVisibility(View.VISIBLE);
				local_pro_bar.setVisibility(View.GONE);
				btn_backup_cancel.setVisibility(View.GONE);
				
				//设置下载过程中，点击通知栏，回到主界
				if (is_tips_recovery) {
					updateIntent = new Intent(mainActivity, MainActivity.class);
					updatePendingIntent = PendingIntent.getActivity(mainActivity,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					
					view = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
				    view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
				    view.setTextViewText(R.id.tips, "本地恢复成功...");
	
	  				// 设置通知在状态栏显示的图标
	  				updateNotification.icon = R.drawable.ic_launcher;
	  				// 通知时发出的默认声音
	  				updateNotification.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
	  				// 通过RemoteViews 设置notification中View 的属性
	  				updateNotification.contentView = view;
	  				updateNotification.contentIntent = updatePendingIntent;
	  				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	  				updateNotificationManager.notify(0, updateNotification);
	  				is_tips_recovery = false;
				}

				break;
			case 26:
				
				is_localRecovery = false;
				is_localRecoveryp_Finish = false;
				
				btn_local_recovery.setVisibility(View.GONE);
				btn_backup_cancel.setVisibility(View.GONE);
				local_pro_bar.setVisibility(View.GONE);
				local_tips.setVisibility(View.VISIBLE);
				
				backup_recovery.setVisibility(View.VISIBLE);
				local_backup_recovery.setVisibility(View.GONE);
				local_backup_recovery_ly.setVisibility(View.GONE);
				
				//设置下载过程中，点击通知栏，回到主界
				updateIntent = new Intent(mainActivity, MainActivity.class);
				updatePendingIntent = PendingIntent.getActivity(mainActivity,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				
				view = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
			    view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
			    view.setTextViewText(R.id.tips, "本地恢复失败...");

  				// 设置通知在状态栏显示的图标
  				updateNotification.icon = R.drawable.ic_launcher;
  				// 通知时发出的默认声音
  				updateNotification.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
  				// 通过RemoteViews 设置notification中View 的属性
  				updateNotification.contentView = view;
  				updateNotification.contentIntent = updatePendingIntent;
  				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
  				updateNotificationManager.notify(0, updateNotification);
				
				Toast.makeText(mainActivity, "本地恢复失败！", Toast.LENGTH_SHORT).show();
				break;
			case 28:
				updateNotificationManager = MainActivity.updateNotificationManager;
				updateNotification = new Notification(R.drawable.ic_launcher, "", System.currentTimeMillis());
			  
			    //设置下载过程中，点击通知栏，回到主界
				updateIntent = new Intent(mainActivity, MainActivity.class);
				updatePendingIntent = PendingIntent.getActivity(mainActivity,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			    
			    //设置通知栏显示内
			    view = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
			    view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
			    if (click_type == 1) {
			    	
			    	view.setTextViewText(R.id.tips, "正在本地备份，请稍等！");
			    	
			    } else if (click_type == 2) {
			    	
			    	view.setTextViewText(R.id.tips, "正在本地恢复，请稍等！");
			    }

  				// 设置通知在状态栏显示的图标
  				updateNotification.icon = R.drawable.ic_launcher;
  				// 通知时发出的默认声音
  				updateNotification.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
  				// 通过RemoteViews 设置notification中View 的属性
  				updateNotification.contentView = view;
  				updateNotification.contentIntent = updatePendingIntent;
  				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
  				updateNotificationManager.notify(0, updateNotification);
  				break;
			case 29:
				
				if (isTip) {
					
					is_localBackUp = false;
					is_localBackUp_Finish = true;
					
					if (top_img.getVisibility() == View.GONE) 
						top_img.setVisibility(View.VISIBLE);
					
					top_img.setBackgroundResource(R.drawable.backup_recovery_finish_1);
					
					tips_top.setText("已完成对您本机的以下相关数据的备份：");
					
//					save_url.setVisibility(View.VISIBLE);
//					save_url.setText("已备份至:"+path);
					
					str = "已备份至:"+path+"，点击'完成'完成备份：";
					str1 = "'完成'";
					local_tips.setText(Html.fromHtml(str.replace(str1, "<font color='#7e93a8'>" + str1+ "</font>")));
					
					btn_local_backup.setVisibility(View.GONE);
					btn_local_recovery.setVisibility(View.GONE);
					btn_backup_finish.setVisibility(View.VISIBLE);
					local_pro_bar.setVisibility(View.GONE);
					btn_backup_cancel.setVisibility(View.GONE);
					
					//设置下载过程中，点击通知栏，回到主界
					
					if (is_tips_backup) {
						
						updateIntent = new Intent(mainActivity, MainActivity.class);
						updatePendingIntent = PendingIntent.getActivity(mainActivity,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
						
						view = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
					    view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
					    view.setTextViewText(R.id.tips, "本地备份成功...");
		
		  				// 设置通知在状态栏显示的图标
		  				updateNotification.icon = R.drawable.ic_launcher;
		  				// 通知时发出的默认声音
		  				updateNotification.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
		  				// 通过RemoteViews 设置notification中View 的属性
		  				updateNotification.contentView = view;
		  				updateNotification.contentIntent = updatePendingIntent;
		  				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
		  				updateNotificationManager.notify(0, updateNotification);
		  				is_tips_backup = false;
					}
				}
				break;
			case 30:
				if (isTip) {
					
					is_localBackUp = false;
					is_localBackUp_Finish = false;
					
					btn_local_backup.setVisibility(View.VISIBLE);
					btn_backup_cancel.setVisibility(View.GONE);
					local_pro_bar.setVisibility(View.GONE);
					
					backup_recovery.setVisibility(View.VISIBLE);
					local_backup_recovery.setVisibility(View.GONE);
					local_backup_recovery_ly.setVisibility(View.GONE);
					
					//设置下载过程中，点击通知栏，回到主界
					updateIntent = new Intent(mainActivity, MainActivity.class);
					updatePendingIntent = PendingIntent.getActivity(mainActivity,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					
					view = new RemoteViews(mainActivity.getPackageName(), R.layout.backup_pro);
				    view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
				    view.setTextViewText(R.id.tips, "本地备份失败...");
	
	  				// 设置通知在状态栏显示的图标
	  				updateNotification.icon = R.drawable.ic_launcher;
	  				// 通知时发出的默认声音
	  				updateNotification.defaults = Notification.FLAG_ONLY_ALERT_ONCE;
	  				// 通过RemoteViews 设置notification中View 的属性
	  				updateNotification.contentView = view;
	  				updateNotification.contentIntent = updatePendingIntent;
	  				updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
	  				updateNotificationManager.notify(0, updateNotification);
					
					Toast.makeText(mainActivity, "本地备份失败！", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case 31:   //点击‘备份’开始备份
				
				setLocalBackUpData();
		    	   
				//local_tips.setText("点击‘备份’开始备份");
				    	   
	    	   top_img.setBackgroundResource(R.drawable.backup_recovery_start);
	    	   
	    	   tips_top.setText("请选择你需要备份的部分：");
	    	   
	    	   str = "点击\"备份到本地\"后即将对您本机的相关数据进行备份：";
	    	   str1 = "\"备份到本地\"";
	    	   
	    	   local_tips.setText(Html.fromHtml(str.replace(str1, "<font color='#3d8eba'>" + str1+ "</font>")));
	    	   
	    	   if (top_img.getVisibility() == View.GONE)
				   top_img.setVisibility(View.VISIBLE);
	
	    	   if (local_tips.getVisibility() == View.GONE)
	    		   local_tips.setVisibility(View.VISIBLE);
	    	   
				btn_local_backup.setVisibility(View.VISIBLE);
				btn_local_recovery.setVisibility(View.GONE);
				btn_backup_finish.setVisibility(View.GONE);
				
				break;
				
			case 32: //备份中
				is_localBackUp = true;
		    	   
	    	   isCheck(); //需要备份的选项
				
				 if (!local_collection && !local_calllog && !local_contacts && !local_message && !local_remind){
		    		   
		    		   Toast.makeText(mainActivity, "请选择需要备份的部分！", Toast.LENGTH_SHORT).show();
		    		   return;
		    	  }
	    	   
	    	   isTip = true;
	    	   
	    	   if (top_img.getVisibility() == View.VISIBLE)
	    	   		top_img.setVisibility(View.GONE);
	    	   
				local_pro_bar.setVisibility(View.VISIBLE);
				
				tips_top.setText("正在对您本机的以下相关数据进行备份：");
				
		    	str = "点击\"取消\"后即将终止对您本机相关数据的备份：";
		    	str1 = "\"取消\"";
		    	   
		    	local_tips.setText(Html.fromHtml(str.replace(str1, "<font color='#e89c4b'>" + str1+ "</font>")));
				
				btn_backup_cancel.setVisibility(View.VISIBLE);
				btn_local_backup.setVisibility(View.GONE);
				getCheckBoxClickable();
				type = 2;
				break;
				
			case 33:   //点击完成，返回备份主界面
				
				is_localBackUp = false;
				is_localBackUp_Finish = false;
				is_localRecovery = false;
				is_localRecoveryp_Finish = false;
				setLocalBackUpData();
				backup_recovery.setVisibility(View.VISIBLE);
				local_backup_recovery.setVisibility(View.GONE);
				local_backup_recovery_ly.setVisibility(View.GONE);
				
				break;
			case 34:  //准备恢复中
				
				is_localRecovery = false;
				is_localRecoveryp_Finish = false;
				
				if (top_img.getVisibility() == View.GONE)
	    			   top_img.setVisibility(View.VISIBLE);
	    		   
	    		   if (local_tips.getVisibility() == View.GONE)
	        		   local_tips.setVisibility(View.VISIBLE);
	    		   
	    		   top_img.setBackgroundResource(R.drawable.backup_recovery_start);
	    		   
	    		   tips_top.setText("请选择你需要恢复的部分：");
	        	   
	    		  str = "点击\"从本地恢复\"后即将对您本机的相关数据进行恢复：";
	        	  str1 = "\"从本地恢复\"";
	        	   
	        	   local_tips.setText(Html.fromHtml(str.replace(str1, "<font color='#3d8eba'>" + str1+ "</font>")));
	    	   
		    	   backup_recovery.setVisibility(View.GONE);
		    	   local_backup_recovery.setVisibility(View.VISIBLE);
		    	   local_backup_recovery_ly.setVisibility(View.VISIBLE);
		    	   
		    	   checkLocalRecoveryData();
		    	   
					btn_local_backup.setVisibility(View.GONE);
					btn_local_recovery.setVisibility(View.VISIBLE);
					btn_backup_finish.setVisibility(View.GONE);
					btn_backup_cancel.setVisibility(View.GONE);
					local_pro_bar.setVisibility(View.GONE);
					save_url.setVisibility(View.GONE);
				
				break;
			case 35: //恢复中
				
				is_localRecovery = true;
				is_localRecoveryp_Finish = false;
				
	    	   isCheck();//需要恢复的选项
				
				 if (!local_collection && !local_calllog && !local_contacts && !local_message && !local_remind){
		    		   
		    		   Toast.makeText(mainActivity, "请选择需要恢复的部分！", Toast.LENGTH_SHORT).show();
		    		   return;
		    	 }
	    	   
	    	   tips_top.setText("正在对您本机的以下相关数据进行恢复：");
	    	   
	    	   if (top_img.getVisibility() == View.VISIBLE)
	   	   			top_img.setVisibility(View.GONE);
	    	   
	    	   local_pro_bar.setVisibility(View.VISIBLE);
				btn_backup_cancel.setVisibility(View.GONE);
				btn_local_recovery.setVisibility(View.GONE);
				local_tips.setVisibility(View.GONE);
				
				getCheckBoxClickable();
				
				break;
			case 36:
				Toast.makeText(mainActivity, "本地备份完成！", Toast.LENGTH_SHORT).show();
				break;
			case 37:
				Toast.makeText(mainActivity, "本地恢复完成！", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
		
	};
	
	//备份
	public SystemBackUp(final MainActivity mainActivity)
	{
		this.mainActivity=mainActivity;
		
		networkUnit=new NetworkUnit();
		myDatabaseUtil=DButil.getInstance(mainActivity);
		cloudBackupRecoveryNotification=(NotificationManager) mainActivity.getSystemService(mainActivity.NOTIFICATION_SERVICE);
//		tm = (TelephonyManager)mainActivity.getSystemService(mainActivity.TELEPHONY_SERVICE);
		WifiManager wifiMgr = (WifiManager)mainActivity.getSystemService(mainActivity.WIFI_SERVICE);
		 info=wifiMgr.getConnectionInfo();
		backupView=LayoutInflater.from(mainActivity).inflate(R.layout.setting_item_1_backup, null);
		scrollView=(ScrollView)backupView.findViewById(R.id.show_backup_recovery);
		showContact=(LinearLayout)backupView.findViewById(R.id.show_select_contact);
		showLoginContact=(LinearLayout)backupView.findViewById(R.id.ln_set_account);
		recovery_select_contact=(LinearLayout)backupView.findViewById(R.id.recovery_select_contact);
		
		backup_recovery=(LinearLayout)backupView.findViewById(R.id.backup_recovery);
		backup_recovery.setVisibility(View.VISIBLE);
		local_backup_recovery_ly = (LinearLayout) backupView.findViewById(R.id.local_backup_recovery_ly);
		b=false;
		onekey_backup=(Button)backupView.findViewById(R.id.onekey_backup);
		
		onekey_recovery=(Button)backupView.findViewById(R.id.onekey_recovery);
		recovery_email=(EditText)backupView.findViewById(R.id.recovery_email);
		re_confirm_passwords=(EditText)backupView.findViewById(R.id.confirm_recovery_password);
		confirmPasswordLinLayout=(LinearLayout)backupView.findViewById(R.id.re_confirm_password);
		recovery_password=(EditText)backupView.findViewById(R.id.recovery_password);
		recovery_confirm=(Button)backupView.findViewById(R.id.recovery_confirm);
//		backupProgress=(TextView)backupView.findViewById(R.id.backup_progress);
//		recoveryProgress=(TextView)backupView.findViewById(R.id.recovery_progress);
		
		onekey_backup.setOnClickListener(this);
		onekey_recovery.setOnClickListener(this);
		recovery_confirm.setOnClickListener(this);
		
		buttonOk=(Button)backupView.findViewById(R.id.btn_ok);
		buttonOk.setVisibility(View.VISIBLE);
		buttonOk.setOnClickListener(this);
		buttonCancel=(Button)backupView.findViewById(R.id.btn_cancle);
		buttonCancel.setOnClickListener(this);
		contactCheckBox=(CheckBox)backupView.findViewById(R.id.backup_contacts);
		messageCheckBox=(CheckBox)backupView.findViewById(R.id.backup_message);
		callRecordCheckBox=(CheckBox)backupView.findViewById(R.id.backup_call_record);
		remindCheckBox=(CheckBox)backupView.findViewById(R.id.backup_remind);
		collectionCheckBox=(CheckBox)backupView.findViewById(R.id.backup_collection);
		progressBar=(ProgressBar)backupView.findViewById(R.id.pro_bar);
		
		recoveryContactCheckBox=(CheckBox)backupView.findViewById(R.id.recovery_contacts);
		recoveryMessageCheckBox=(CheckBox)backupView.findViewById(R.id.recovery_message);
		recoveryCallRecordCheckBox=(CheckBox)backupView.findViewById(R.id.recovery_call_record);
		recoveryRemindCheckBox=(CheckBox)backupView.findViewById(R.id.recovery_remind);
		recoveryCollectionCheckBox=(CheckBox)backupView.findViewById(R.id.recovery_collection);
		recoveryProgressBar=(ProgressBar)backupView.findViewById(R.id.recovery_pro_bar);
		recoveryOk=(Button)backupView.findViewById(R.id.recovery_ok);
//		recoveryCancel=(Button)backupView.findViewById(R.id.recovery_cancle);
		recoveryOk.setOnClickListener(this);
//		recoveryCancel.setOnClickListener(this);
		cacelBackUp=(Button)backupView.findViewById(R.id.cancel_backup);
		cacelBackUp.setOnClickListener(this);
		switchingAccount=(Button)backupView.findViewById(R.id.switching_account);
		switchingAccount.setOnClickListener(this);
		accountLogin=(Button)backupView.findViewById(R.id.account_login);
		accountLogin.setOnClickListener(this);
		createAcount=(Button)backupView.findViewById(R.id.create_account);
		createAcount.setOnClickListener(this);
		tv_tips_bottom=(TextView)backupView.findViewById(R.id.tv_tips_bottom);
		tv_tips_bottom_add=(TextView)backupView.findViewById(R.id.tv_tips_bottom_add);
		tv_tips_bottom_recovery=(TextView)backupView.findViewById(R.id.tv_tips_bottom_recovery);
		tv_tips_bottom_recovery_add=(TextView)backupView.findViewById(R.id.tv_tips_bottom_recovery_add);
		tv_tips_top_backup=(TextView)backupView.findViewById(R.id.tv_top_backup);
		tv_tips_top_recovery=(TextView)backupView.findViewById(R.id.tv_tips_top_recovery);
		finshRecovery=(ImageView)backupView.findViewById(R.id.recovery_finish);
		bt_recovery_finish=(Button)backupView.findViewById(R.id.bt_recovery_finish);
		bt_recovery_finish.setOnClickListener(this);
		local_backup = (Button)backupView.findViewById(R.id.local_backup);
		local_recovery = (Button)backupView.findViewById(R.id.local_recovery);
		
		local_backup.setOnClickListener(this);
		local_recovery.setOnClickListener(this);
		local_backup_recovery=(ScrollView)backupView.findViewById(R.id.local_backup_recovery);
		
		/********初次启动备份********/
		mSharedPreferences = mainActivity.getSharedPreferences("BACKUP_SF", 0);
		boolean isBackUp = mSharedPreferences.getBoolean("IS_BACK_UP", true);
		
		if (isBackUp) {
			
			type = 1;
			
			backUpDialog = new Dialog(mainActivity, R.style.theme_myDialog);
			backUpDialog.setContentView(R.layout.first_back_up);
			backUpDialog.show();
			backUpDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
			
			first_pro_bar = (ProgressBar) backUpDialog.findViewById(R.id.first_pro_bar);
			btn_ok = (Button) backUpDialog.findViewById(R.id.btn_ok);
//			finish_tips = (TextView) backUpDialog.findViewById(R.id.finish_tips);
			tv_tips = (TextView) backUpDialog.findViewById(R.id.tv_tips);
			
			first_backup_ly = (LinearLayout) backUpDialog.findViewById(R.id.first_backup_ly);
			tips = (TextView) backUpDialog.findViewById(R.id.tips);
//			first_pro_bar.setMax(100);
//			first_pro_bar.setProgress(10);
			
			//备份联系人、短信
			
			firstBackUpData();
			
			btn_ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					//修改初始值，释放返回键，返回首页
					mainActivity.changeIsBackUp(mSharedPreferences);
					
//					mainActivity.ver_scroller.snapToScreen(0);
//					mainActivity.top_scroller.snapToScreen(4);
//					mainActivity.l_scrolelr.snapToScreen(0);
					
//					mainActivity.state = mainActivity.STATE_HOME;
					
					//刷新首页列表数据
					mainActivity.homeCallsAdapter.notifyDataSetChanged();
					
					backUpDialog.cancel();
				}
			});
			
			
			//屏蔽返回键
			backUpDialog.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

					switch (keyCode) {
					   case KeyEvent.KEYCODE_BACK:
						   return true;
					  }
					  return false;

				}
			});
			
		}
		
		local_backup_contacts = (CheckBox) backupView.findViewById(R.id.local_backup_contacts);
		local_backup_message = (CheckBox) backupView.findViewById(R.id.local_backup_message);
		local_backup_remind = (CheckBox) backupView.findViewById(R.id.local_backup_remind);
		local_backup_calllog = (CheckBox) backupView.findViewById(R.id.local_backup_calllog);
		local_backup_collection = (CheckBox) backupView.findViewById(R.id.local_backup_collection);
		save_url = (TextView) backupView.findViewById(R.id.save_url);
		local_tips = (TextView) backupView.findViewById(R.id.local_tips);
		btn_local_backup = (Button) backupView.findViewById(R.id.btn_local_backup);
		btn_local_recovery = (Button) backupView.findViewById(R.id.btn_local_recovery);
		btn_backup_finish = (Button) backupView.findViewById(R.id.btn_backup_finish);
		btn_backup_cancel = (Button) backupView.findViewById(R.id.btn_backup_cancel);
		local_pro_bar = (ProgressBar) backupView.findViewById(R.id.local_pro_bar);
		tips_top = (TextView) backupView.findViewById(R.id.tips_top);
		top_img = (ImageView) backupView.findViewById(R.id.top_img);
		
		btn_local_backup.setOnClickListener(this);
		btn_local_recovery.setOnClickListener(this);
		btn_backup_finish.setOnClickListener(this);
		btn_backup_cancel.setOnClickListener(this);
		
		local_backup_contacts.setOnClickListener(this);
		local_backup_message.setOnClickListener(this);
		local_backup_remind.setOnClickListener(this);
		local_backup_calllog.setOnClickListener(this);
		local_backup_collection.setOnClickListener(this);
		
			
	}
	
	private void firstBackUpData() {
        AsyncTask<Integer, Integer, String> task = new AsyncTask<Integer, Integer, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Integer... params) {
                try {
                	
                    backupContacts(getContast());
                    backUpSms(getSmsInPhone());
                    
                    local_handler.sendEmptyMessage(19);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    local_handler.sendEmptyMessage(30);
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
            }

			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}
            
        };
        task.execute();
    }
	
//	private class LocalBackUpData extends AsyncTask<String, String, String>{
//
//		@Override
//		protected String doInBackground(String... params) {
//
//			 try {
//				 
//         		if (local_contacts) {
//         			if (isTip)
//         				backupContacts(getContast());
//         		}
//         		if(local_message) {
//         			if (isTip)
//         				backUpSms(getSmsInPhone());
//         		}
//         		if(local_remind) {
//         			if (isTip)
//         				backUpRemind(getAllRemind());
//         		}
//         		if (local_calllog) {
//         			if (isTip)
//         				backUpCallLog(getCallRecord());
//         		}
//         		if(local_collection) {
//         			if (isTip)
//         				backUpCollect(getFavoriteData());
//         		}
//         		
//         		local_handler.sendEmptyMessage(29);
//             }catch (Exception e) {
//                 e.printStackTrace();
//                 local_handler.sendEmptyMessage(30);
//             }
//			
//			return null;
//		}
//		
//	}
	
	private class LocalBackUpData extends Thread{

		@Override
		public void run() {
			
			try {
				 
	     		if (local_contacts) {
	     			if (isTip)
	     				backupContacts(getContast());
	     		}
	     		if(local_message) {
	     			if (isTip)
	     				backUpSms(getSmsInPhone());
	     		}
	     		if(local_remind) {
	     			if (isTip)
	     				backUpRemind(getAllRemind());
	     		}
	     		if (local_calllog) {
	     			if (isTip)
	     				backUpCallLog(getCallRecord());
	     		}
	     		if(local_collection) {
	     			if (isTip)
	     				backUpCollect(getFavoriteData());
	     		}
	     		is_tips_backup = true;
	     		local_handler.sendEmptyMessage(29);
	     		local_handler.sendEmptyMessage(36);
	         }catch (Exception e) {
	             e.printStackTrace();
	             local_handler.sendEmptyMessage(30);
	             local_handler.sendEmptyMessage(2);
	         }
		}
		
	}
	
	private void localRecoveryData() {
        AsyncTask<Integer, Integer, String> task = new AsyncTask<Integer, Integer, String>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Integer... params) {
                try {
                	
            		if (local_contacts) {
            			localRecoveryContacts(localRecoveryContactsData());
            		}
            		if(local_message) {
            			localRecoveryMessage(localRecoveryMessageData());
            		}
            		if(local_remind) {
            			localRecoveryRemind(localRecoveryRemindData());
            		}
            		if (local_calllog) {
            			localRecoveryCalllog(localRecoveryCalllogData());
            		}
            		if(local_collection) {
            			localRecoveryCollection(localRecoveryCollectionData());
            		}
            		is_tips_recovery = true;
            		local_handler.sendEmptyMessage(25);
            		
            		activateAllReminds();
            		local_handler.sendEmptyMessage(37);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    local_handler.sendEmptyMessage(2);
                    local_handler.sendEmptyMessage(26);
                }
                
                return null;
            }

            @Override
            protected void onPostExecute(String data) {
                super.onPostExecute(data);
            }

			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				super.onProgressUpdate(values);
			}
            
        };
        task.execute();
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	switch (v.getId()) {
	        case R.id.bt_recovery_finish:
	        	recoveryFinish=false;
	        	tv_tips_top_recovery.setText(mainActivity.getString(R.string.tv_recovery_cloud).toString());
				tv_tips_bottom_recovery.setText(mainActivity.getString(R.string.tv_recovery_cloud).toString());//请选择您要恢复的部分：
				tv_tips_bottom_recovery_add.setText(mainActivity.getString(R.string.tv_recovery_cloud_add).toString());//
	        	backUpRecoveryFinish();
	        	Toast.makeText(mainActivity, "恢复完成", Toast.LENGTH_SHORT).show();
	        	break;
	        case R.id.account_login:
	        	//判断之前有没有登录过
//	        	SharedPreferences shPreferencesLogin=mainActivity.getSharedPreferences("login", 0);
//	        	recoveryEmail=shPreferencesLogin.getString("username","");
//	        	recoveryPassword=shPreferencesLogin.getString("password","");
//	        	
//	        	recovery_email.setText(recoveryEmail);
//	        	recovery_password.setText(recoveryPassword);
	        	
	        	final String passwordRegisterLogin=recovery_password.getText().toString();
    			final String emailRegisterLogin=recovery_email.getText().toString();
        		isLoginCookie();
        		if("".equals(emailRegisterLogin))
    	    	{
    		    	Toast.makeText(mainActivity, "请输入用户名", Toast.LENGTH_SHORT).show();
    	    	}
        		else 
        		{
        			if("".equals(passwordRegisterLogin))
	    	    	{
	    	    		Toast.makeText(mainActivity, "请输入密码", Toast.LENGTH_SHORT).show();
	    	    	}
        			else
        			{
        				if(!LoginTool.isEmail(emailRegisterLogin))
        				{
        					Toast.makeText(mainActivity, "邮箱格式有问题，请重新输入", Toast.LENGTH_SHORT).show();
        				}
        				else
        				{
        					
        					try {
    	    	    			new Thread(new Runnable() {
    								
    								@Override
    								public void run() {
    									// TODO Auto-generated method stub
    									NetWorkResult netWorkResult;
    									try {
    										netWorkResult = networkUnit.login(emailRegisterLogin, passwordRegisterLogin,info.getMacAddress());
    										sPreferences=mainActivity.getSharedPreferences("login", 0);
    				    					if("10000".equals(netWorkResult.getSuccess()))
    				    					{
    				    						Editor editor=sPreferences.edit();
    				    						editor.putString("username", emailRegisterLogin);
    				    						editor.putString("password", passwordRegisterLogin);
    				    						editor.putString("cookie", netWorkResult.getCookieid());
    				    						editor.commit();
    				    						
    				    						if(selectBackUpRecovery.equals("0"))
    				    						{
    				    							if("1".equals(netWorkResult.getBackUpSuccess()))
		    										{
    				    								handle.sendEmptyMessage(7);
		    										}
		    										else if("-1".equals(netWorkResult.getBackUpSuccess()))
		    										{
		    											handle.sendEmptyMessage(14);
		    										}
		    										else if("-2".equals(netWorkResult.getBackUpSuccess()))
		    										{
		    											handle.sendEmptyMessage(7);
		    										}
		    										else if("0".equals(netWorkResult.getBackUpSuccess()))
		    										{
		    											handle.sendEmptyMessage(14);
		    										}
    				    							
    				    							
    				    						}
    				    						else if(selectBackUpRecovery.equals("1"))
    				    						{
    				    							handle.sendEmptyMessage(10);
    				    						}
    				    						
    				    					}
    				    					else if("-1".equals(netWorkResult.getSuccess()))
    				    					{
    				    						//用户不存在
    				    						handle.sendEmptyMessage(32);
    				    					}
    				    					else if("-2".equals(netWorkResult.getSuccess()))
    				    					{
    				    						handle.sendEmptyMessage(33);
    				    						//密码错误
    				    					}
    				    					else if("-3".equals(netWorkResult.getSuccess()))
    				    					{
    				    						//登录失败
    				    						handle.sendEmptyMessage(34);
    				    					}
    				    					else{
    				    						//登录失败
    				    						handle.sendEmptyMessage(34);
    				    					}
    									} catch (Exception e) {
    										// TODO Auto-generated catch block
    										e.printStackTrace();
    										handle.sendEmptyMessage(34);
    									}
    			    					
    								}
    							}).start();
    	    					
    	    				} catch (Exception e) {
    	    					// TODO Auto-generated catch block
    	    					e.printStackTrace();
    	    				}
        				}
        			}
        		}
	        	
	        	break;
	        case R.id.cancel_backup://取消备份
	        		backUpRecoveryFinish();
	        		 tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
	        	break;
	        case R.id.switching_account:
	        	confirmPasswordLinLayout.setVisibility(View.GONE);
	        	accountLogin.setVisibility(View.VISIBLE);
	        	createAcount.setVisibility(View.VISIBLE);
	        	recovery_confirm.setVisibility(View.GONE);
	        	switchingAccount.setVisibility(View.GONE);
	        	break;
	        case R.id.create_account:
	        	confirmPasswordLinLayout.setVisibility(View.VISIBLE);
	        	recovery_email.setText("");
	        	re_confirm_passwords.setText("");
	        	recovery_password.setText("");
	        	accountLogin.setVisibility(View.GONE);
	        	createAcount.setVisibility(View.GONE);
	        	recovery_confirm.setVisibility(View.VISIBLE);
	        	switchingAccount.setVisibility(View.VISIBLE);
	        	break;
	        	
	        case R.id.recovery_confirm://登录
	        	
	        	final String passwordRegister=recovery_password.getText().toString();
    			final String rePasswordRegister=re_confirm_passwords.getText().toString();
    			final String emailRegister=recovery_email.getText().toString();
	        	//判断之前有没有登录过
    			isLoginCookie();
	        		if("".equals(recoveryEmail) || "".equals(recoveryPassword))//注册
	        		{
	        			if(!"".equals(emailRegister)&& !"".equals(passwordRegister) && !"".equals(rePasswordRegister))
		    			{
		    				if(passwordRegister.length()<6)
		    				{
		    					Toast.makeText(mainActivity, "密码最少6位", Toast.LENGTH_SHORT).show();
		    				}
		    				else
		    				{
		    					if(passwordRegister.equals(rePasswordRegister))
			    				{
		    						if(LoginTool.isEmail(emailRegister))
		    						{
		    							try {
				    						new Thread(new Runnable() {
				    							
				    							@Override
				    							public void run() {
				    								// TODO Auto-generated method stub
				    								NetWorkResult netWorkResult;
				    								try {
				    									netWorkResult = networkUnit.register(emailRegister, passwordRegister, rePasswordRegister,info.getMacAddress());
				    									if(netWorkResult!=null&&!"".equals(netWorkResult.getSuccess()))
				    									{
					    									sPreferences=mainActivity.getSharedPreferences("login", 0);
					    									
					    									if("10000".equals(netWorkResult.getSuccess()))
					    									{
					    										Editor editor=sPreferences.edit();
					    										editor.putString("username", emailRegister);
					    										editor.putString("password", passwordRegister);
					    										editor.putString("cookie", netWorkResult.getCookieid());
					    										editor.commit();
					    										if("1".equals(netWorkResult.getBackUpSuccess()))
					    										{
					    											handle.sendEmptyMessage(5);
					    											isFinish=true;
//					    											handle.sendEmptyMessage(8);
					    										}
					    										else if("-1".equals(netWorkResult.getBackUpSuccess()))
					    										{
					    											handle.sendEmptyMessage(14);
					    										}
					    										else if("0".equals(netWorkResult.getBackUpSuccess()))
					    										{
					    											handle.sendEmptyMessage(14);
					    										}
					    										
					    									}else
					    									{
					    										if("-3".equals(netWorkResult.getSuccess()))
					    										{
					    											handle.sendEmptyMessage(17);
					    										}
					    										else if("-6".equals(netWorkResult.getSuccess()))
					    										{
					    											handle.sendEmptyMessage(17);
					    										}
					    										else
					    										handle.sendEmptyMessage(11);
					    									}
				    									}
				    									else
				    									{
				    										handle.sendEmptyMessage(11);
				    									}
				    								} catch (Exception e) {
				    									// TODO Auto-generated catch block
				    									e.printStackTrace();
				    									handle.sendEmptyMessage(11);
				    								}
				    								
				    							}
				    						}).start();
				    						
				    						
				    					} catch (Exception e) {
				    						// TODO Auto-generated catch block
				    						e.printStackTrace();
				    						handle.sendEmptyMessage(11);
				    					}
		    						}
		    						else
		    						{
		    							Toast.makeText(mainActivity, "邮箱格式不对，请重新输入", Toast.LENGTH_SHORT).show();
		    						}
			    					
			    				}
			    				else
			    				{
			    					Toast.makeText(mainActivity, "密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
			    				}
		    				}
		    				
		    			}
		    			else
		    			{
		    				Toast.makeText(mainActivity, "用户名和密码必须填写", Toast.LENGTH_SHORT).show();
		    			}
	        		}
	        		//登录
	        		else
	        		{
	    	        		recovery_email.setText(recoveryEmail);
	    		        	recovery_password.setText(recoveryPassword);
	    	        		isLoginCookie();
	    	        		final String loginEmail=recovery_email.getText().toString();
	    	        		final String loginPassword=recovery_password.getText().toString();
	    	        		if("".equals(loginEmail))
	    	    	    	{
	    	    		    	Toast.makeText(mainActivity, "请输入用户名", Toast.LENGTH_SHORT).show();
	    	    	    	}
	    	        		else 
	    	        		{
	    	        			if("".equals(loginPassword))
	    		    	    	{
	    		    	    		Toast.makeText(mainActivity, "请输入密码", Toast.LENGTH_SHORT).show();
	    		    	    	}
	    	        			else
	    	        			{
	    	        				if(!LoginTool.isEmail(loginEmail))
	    	        				{
	    	        					Toast.makeText(mainActivity, "邮箱格式有问题，请重新输入", Toast.LENGTH_SHORT).show();
	    	        				}
	    	        				else
	    	        				{
	    	        					
	    	        					try {
	    	    	    	    			new Thread(new Runnable() {
	    	    								
	    	    								@Override
	    	    								public void run() {
	    	    									// TODO Auto-generated method stub
	    	    									NetWorkResult netWorkResult;
	    	    									try {
	    	    										netWorkResult = networkUnit.login(loginEmail, loginPassword,info.getMacAddress());
	    	    										sPreferences=mainActivity.getSharedPreferences("login", 0);
	    	    				    					if("10000".equals(netWorkResult.getSuccess()))
	    	    				    					{
	    	    				    						Editor editor=sPreferences.edit();
	    	    				    						editor.putString("username", emailRegister);
	    	    				    						editor.putString("password", passwordRegister);
	    	    				    						editor.putString("cookie", netWorkResult.getCookieid());
	    	    				    						editor.commit();
	    	    				    						
	    	    				    						if(selectBackUpRecovery.equals("0"))
	    	    				    						{
	    	    				    							if("1".equals(netWorkResult.getBackUpSuccess()))
	    			    										{
	    	    				    								handle.sendEmptyMessage(7);
	    			    										}
	    			    										else if("-1".equals(netWorkResult.getBackUpSuccess()))
	    			    										{
	    			    											handle.sendEmptyMessage(14);
	    			    										}
	    			    										else if("-2".equals(netWorkResult.getBackUpSuccess()))
	    			    										{
	    			    											handle.sendEmptyMessage(7);
	    			    										}
	    			    										else if("0".equals(netWorkResult.getBackUpSuccess()))
	    			    										{
	    			    											handle.sendEmptyMessage(14);
	    			    										}
	    	    				    							
	    	    				    							
	    	    				    						}
	    	    				    						else if(selectBackUpRecovery.equals("1"))
	    	    				    						{
	    	    				    							handle.sendEmptyMessage(10);
	    	    				    						}
	    	    				    						
	    	    				    					}
	    	    				    					else if("-1".equals(netWorkResult.getSuccess()))
	    	    				    					{
	    	    				    						//用户不存在
	    	    				    						handle.sendEmptyMessage(32);
	    	    				    					}
	    	    				    					else if("-2".equals(netWorkResult.getSuccess()))
	    	    				    					{
	    	    				    						handle.sendEmptyMessage(33);
	    	    				    						//密码错误
	    	    				    					}
	    	    				    					else if("-3".equals(netWorkResult.getSuccess()))
	    	    				    					{
	    	    				    						//登录失败
	    	    				    						handle.sendEmptyMessage(34);
	    	    				    					}
	    	    				    					else{
	    	    				    						//登录失败
	    	    				    						handle.sendEmptyMessage(34);
	    	    				    					}
	    	    				    					
	    	    									} catch (Exception e) {
	    	    										// TODO Auto-generated catch block
	    	    										e.printStackTrace();
	    	    									}
	    	    			    					
	    	    								}
	    	    							}).start();
	    	    	    					
	    	    	    				} catch (Exception e) {
	    	    	    					// TODO Auto-generated catch block
	    	    	    					e.printStackTrace();
	    	    	    				}
	    	        				}
	    	        			}
	    	        		}
	        		}
	    			
	    			break;
		case R.id.confirm_password://找回密码确认
			
			break;
	    case R.id.login_register://注册
			
		
	    case R.id.find_password://找回密码
	    	
	    	
	    	break;
	    	
	    case R.id.onekey_backup:
	    	if(ToolUnit.checkNetWork(mainActivity))
	    	{
	    		selectBackUpRecovery="0";
				backup_recovery.setVisibility(View.GONE);
		    	scrollView.setVisibility(View.VISIBLE);
		    	finshRecovery.setBackgroundResource(R.drawable.back_cloud);
		    	recoveryProgressBar.setVisibility(View.GONE);
		    	if(isLoginIng)
		    	{
		    		showLoginContact.setVisibility(View.VISIBLE);
		    		cacelBackUp.setVisibility(View.VISIBLE);
		    		isLoginIng=false;
		    		b=true;
		    	}
		    	else if(backuping)
		    	{
		    		b=true;
		    		SharedPreferences sPreferencesss=	mainActivity.getSharedPreferences("select_backup", 0);
		    		
		    	    boolean selectConatct=	sPreferencesss.getBoolean("select_contact", false);
		    	    boolean selectMessage=	sPreferencesss.getBoolean("select_message", false);
		    	    boolean selectCallRecord=	sPreferencesss.getBoolean("select_call_record", false);
		    	    boolean selectRemind=	sPreferencesss.getBoolean("select_remind", false);
		    	    boolean selectCollection=	sPreferencesss.getBoolean("select_collection", false);
		    	    
		    	    if(selectConatct)
	                {
		    	    	contactCheckBox.setButtonDrawable(R.drawable.dot);
	                }
	                else
	                {
	                	contactCheckBox.setVisibility(View.GONE);
	                	
	                }
	                if(selectMessage)
	                {
	                	messageCheckBox.setButtonDrawable(R.drawable.dot);
	                }else
	                {
	                	messageCheckBox.setVisibility(View.GONE);
	                }
	                if(selectCallRecord)
	                {
	                	callRecordCheckBox.setButtonDrawable(R.drawable.dot);
	                }
	                else
	                {
	                	callRecordCheckBox.setVisibility(View.GONE);
	                }
	                if(selectRemind)
	                {
	                	remindCheckBox.setButtonDrawable(R.drawable.dot);
	                }
	                else
	                {
	                	remindCheckBox.setVisibility(View.GONE);
	                }
	                if(selectCollection)
	                {
	                	collectionCheckBox.setButtonDrawable(R.drawable.dot);
	                }
	                else
	                {
	                	collectionCheckBox.setVisibility(View.GONE);
	                }
	                buttonOk.setVisibility(View.GONE);
	                buttonCancel.setVisibility(View.VISIBLE);
	                showContact.setVisibility(View.VISIBLE);
		    	    progressBar.setVisibility(View.VISIBLE);
		    	}
		    	else
		    	{
		    		showLoginContact.setVisibility(View.GONE);
		    		showContact.setVisibility(View.VISIBLE);
			    	tv_tips_bottom.setText(Html.fromHtml(mainActivity.getString(R.string.tv_backup_cloud).replace("\"备份到云端\"", "<font color='#3d8eba'>" + "\"备份到云端\"" + "</font>"))); 
			    	tv_tips_bottom_add.setText(mainActivity.getString(R.string.tv_backup_add));
					contactCheckBox.setVisibility(View.VISIBLE);
					messageCheckBox.setVisibility(View.VISIBLE);
					callRecordCheckBox.setVisibility(View.VISIBLE);
					remindCheckBox.setVisibility(View.VISIBLE);
					collectionCheckBox.setVisibility(View.VISIBLE);
					local_backup_recovery.setVisibility(View.GONE);
				 	b=true;
				 	contactCheckBox.setChecked(true);
			    	messageCheckBox.setChecked(true);
			    	callRecordCheckBox.setChecked(true);
			    	remindCheckBox.setChecked(true);
			    	collectionCheckBox.setChecked(true);
			    	
			    	
//			    	contactCheckBox.setClickable(false);
//			    	messageCheckBox.setClickable(false);
//			    	callRecordCheckBox.setClickable(false);
//			    	remindCheckBox.setClickable(false);
//			    	collectionCheckBox.setClickable(false);
		    	}
		   
	    	}else
	    	{
	    		Toast.makeText(mainActivity, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
	    	}
	    	
	    	break;
	    case R.id.onekey_recovery:
	    	if(ToolUnit.checkNetWork(mainActivity))
	    	{
	    		
	    		if(recoverying)
	    		{
	    			
	    			handle.sendEmptyMessage(35);
	    			b=true;
	    		}
	    		else if(recoveryFinish)
	    		{
	    			handle.sendEmptyMessage(35);
	    		}
	    		else
	    		{
	    			tv_tips_bottom_recovery.setText(Html.fromHtml(mainActivity.getString(R.string.tv_recovery_cloud).replace("\"从云端恢复\"", "<font color='#3d8eba'>" + "\"从云端恢复\"" + "</font>"))); 
		    		tv_tips_bottom_recovery_add.setText(mainActivity.getString(R.string.tv_recovery_cloud_add));
			    	notLogin();
			    	selectBackUpRecovery="1";
			    	b=true;
			    	local_backup_recovery.setVisibility(View.GONE);//本地备份隐藏
	    		}
	    		
	    	}
	    	else
	    	{
	    		Toast.makeText(mainActivity, "网络连接失败，请检查网络", Toast.LENGTH_SHORT).show();
	    	}
	    	break;
	  /*  case R.id.recovery_cancle:
	    	try {
	    		if(  recoveryProgressBar.getVisibility()==View.VISIBLE)
		    	{
		    		recoveryProgressBar.setVisibility(View.GONE);
		    	}
		    	backUpRecoveryFinish();
		    	recoveryThread.interrupt();
			} catch (Exception e) {
				// TODO: handle exception
			}
	    	
	    	break;*/
	    	//恢复按钮确认
	    case R.id.recovery_ok:
	    	
	    recoveryThread=	new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if(recoveryContactCheckBox.isChecked() ||recoveryMessageCheckBox.isChecked() || recoveryCallRecordCheckBox.isChecked() ||recoveryRemindCheckBox.isChecked() ||recoveryCollectionCheckBox.isChecked())
					{
						recoverying=true;
						handle.sendEmptyMessage(12);
						
						handle.sendEmptyMessage(37);
						click_backup_recovery=2;
						
						NetWorkResult netWorkResult;
					    boolean contactSelect=false;
						boolean messageSelect=false;
						boolean remindSelect=false;
						boolean callRecordSelect=false;
						boolean favoriteSelect=false;
						String selectMode="";
						if(recoveryContactCheckBox.isChecked())
						{
							selectMode="contact";
							contactSelect=true;
						}
						if(recoveryMessageCheckBox.isChecked())
						{
							if("".equals(selectMode))
							{
								selectMode="message";
							}else
							{
								selectMode+="&message";
							}
							messageSelect=true;
						}
						if(recoveryRemindCheckBox.isChecked())
						{
							if("".equals(selectMode))
							{
								selectMode="remind";
							}
							else
							{
								selectMode+="&remind";
							}
							remindSelect=true;
						}
						if(recoveryCallRecordCheckBox.isChecked())
						{
							if("".equals(selectMode))
							{
								selectMode="callrecord";
							}
							else
							{
								selectMode+="&callrecord";
							}
							callRecordSelect=true;
						}
						
						if(recoveryCollectionCheckBox.isChecked())
						{
							if("".equals(selectMode))
							{
								selectMode="favorite";
							}
							else
							{
								selectMode+="&favorite";
							}
							favoriteSelect=true;
						}
						
						Message message=new Message();
						Bundle bundle=new Bundle();
						bundle.putBoolean("contactCheckBoxRecovery", contactSelect);
						bundle.putBoolean("messageCheckBoxRecovery", messageSelect);
						bundle.putBoolean("callRecordCheckBoxRecovery", callRecordSelect);
						bundle.putBoolean("remindCheckBoxRecovery", remindSelect);
						bundle.putBoolean("collectionCheckBoxRecovery", favoriteSelect);
						message.what=26;
						message.setData(bundle);
						handle.sendMessage(message);
						
						isLoginCookie();
						
							try {
									//恢复处理 暂定
								
								    netWorkResult= networkUnit.postCloundContactRecovery(cookieid,selectMode,contactSelect,messageSelect,remindSelect,callRecordSelect,favoriteSelect);
									
								    if(netWorkResult!=null && "1".equals(netWorkResult.getSuccess()))
									{
										final  ArrayList<ContactBean> contactBeanss=netWorkResult.getContactBeans();
										final  ArrayList<SmsContent> smsContextBeanss=netWorkResult.getSmsContents();
										final  ArrayList<RemindBean> remindBeanss=netWorkResult.getRemindBeans();
										final  ArrayList<CallLogInfo> callRecordBeanss=netWorkResult.getCallLogInfos();
										final  ArrayList<SmsContent> smsFavoriteBeanss=netWorkResult.getSmsContentFavorite();
										boolean is;
										final boolean contactOperat=contactSelect;
										final boolean messageOperat=messageSelect;
										final boolean remindOperat=remindSelect;
										final boolean callRecordOperat=callRecordSelect;
										final boolean favoriteOperat=favoriteSelect;
										
											 new Thread(new Runnable() {
												@Override
												public void run() {
													// TODO Auto-generated method stub
													
													if(contactOperat)
													{
														if(contactBeanss!=null && contactBeanss.size()>0)
														{
															delContact();
															for(int i=0;i<contactBeanss.size();i++)
															{
																//插入通讯录
																addContact(contactBeanss.get(i));
															}
														}
														
													}
													
													if(messageOperat)
													{
														if(smsContextBeanss!=null && smsContextBeanss.size()>0)
														{
															deleteMessage();
															for(int i=0;i<smsContextBeanss.size();i++)
															{
																//插入短信
																writeToDataBase(smsContextBeanss.get(i));
															}
														}
														
													}
													if(remindOperat)
													{
														if(remindBeanss!=null && remindBeanss.size()>0)
														{
															myDatabaseUtil.deleteAllRemind();
															for(int i=0;i<remindBeanss.size();i++)
															{
																RemindBean remindBean=remindBeanss.get(i);
																//插入提醒
																myDatabaseUtil.insertRemind(remindBean.getContent(), remindBean.getContacts(), remindBean.getParticipants(), remindBean.getStart_time(), remindBean.getEnd_time(), remindBean.getRemind_type(), remindBean.getRemind_num(), remindBean.getRemind_time(), remindBean.getRepeat_type(), remindBean.getRepeat_fre(), remindBean.getRepeat_condition(), remindBean.getRepeat_start_time(), remindBean.getRepeat_end_time());
															}
														}
													}
													if(callRecordOperat)
													{
														if(callRecordBeanss!=null && callRecordBeanss.size()>0)
														{
															deleteCallRecord();
															for(int i=0;i<callRecordBeanss.size();i++)
															{
																//插入通话记录
																insertCallLog(callRecordBeanss.get(i));
															}
														}
													}
													if(favoriteOperat)
													{
														if(smsFavoriteBeanss!=null && smsFavoriteBeanss.size()>0)
														{
															myDatabaseUtil.deleteFavorite(null);
															for(int i=0;i<smsFavoriteBeanss.size();i++)
															{
																//插入短信收藏
																SmsContent smsContent=smsFavoriteBeanss.get(i);
																myDatabaseUtil.insertDataFavorite(String.valueOf(smsContent.getThread_id()), String.valueOf(smsContent.getId()), smsContent.getSms_body(), smsContent.getDate(), smsContent.getSend_type().equals("我") ?"我":getNameByNumber(PhoneNumberTool.cleanse(smsContent.getSms_number()))!=null?getNameByNumber(PhoneNumberTool.cleanse(smsContent.getSms_number())):PhoneNumberTool.cleanse(smsContent.getSms_number()), PhoneNumberTool.cleanse(smsContent.getSms_number()));
															}
														}
													}
													
//													handle.sendEmptyMessage(16);
//													handle.sendEmptyMessage(24);
													isFinish=true;
//													handle.sendEmptyMessage(28);
													handle.sendEmptyMessage(31);
													recoverying=false;
													recoveryFinish=true;
													handle.sendEmptyMessage(37);
													click_backup_recovery=5;
												}
											}).start();
										}
//								        else if(netWorkResult!=null && "0".equals(netWorkResult.getSuccess()))
//										{
//											handle.sendEmptyMessage(6);
//											handle.sendEmptyMessage(28);
//										}
										else
										{
											//恢复失败
											handle.sendEmptyMessage(15);
											handle.sendEmptyMessage(28);
										}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								handle.sendEmptyMessage(15);
								handle.sendEmptyMessage(28);
							}
					
				 }
					else
					{
						handle.sendEmptyMessage(29);
					}
				}
			});
	        recoveryThread.start();
	    	break;
		case R.id.btn_ok:
			handle.sendEmptyMessage(37);
			click_backup_recovery=1;
			backuping=true;
		//备份确认
			
			isLoginCookie();
			backupThread=	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if(contactCheckBox.isChecked() ||messageCheckBox.isChecked() || callRecordCheckBox.isChecked() ||remindCheckBox.isChecked() ||collectionCheckBox.isChecked())
				{
					
					handle.sendEmptyMessage(3);
					boolean contactCheck=false;
					boolean messageCheck=false;
					boolean callRecordCheck=false;
					boolean remindCheck=false;
					boolean collectionCheck=false;
					ArrayList<ContactBean> arrayListContact=new ArrayList<ContactBean>();
					ArrayList<SmsContent> contentsMessage=new ArrayList<SmsContent>();
					ArrayList<RemindBean> arrayListRemind=new ArrayList<RemindBean>();
					ArrayList<CallLogInfo> logInfosRecord=new ArrayList<CallLogInfo>();
					if(contactCheckBox.isChecked())
					{
						contactCheck=true;
					}
					if(messageCheckBox.isChecked())
					{
						messageCheck=true;
					}
					if(callRecordCheckBox.isChecked())
					{
						callRecordCheck=true;
					}
					if(remindCheckBox.isChecked())
					{
						remindCheck=true;
					}
					if(collectionCheckBox.isChecked())
					{
						collectionCheck=true;
					}
					
					Message message=new Message();
					Bundle bundle=new Bundle();
					bundle.putBoolean("contactCheckBox", contactCheck);
					bundle.putBoolean("messageCheckBox", messageCheck);
					bundle.putBoolean("callRecordCheckBox", callRecordCheck);
					bundle.putBoolean("remindCheckBox", remindCheck);
					bundle.putBoolean("collectionCheckBox", collectionCheck);
					message.what=25;
					message.setData(bundle);
					handle.sendMessage(message);
					
					SharedPreferences sPreferences=mainActivity.getSharedPreferences("select_backup", 0);
					
//					boolean backupContact=sPreferences.getBoolean("select_contact", false);
//					
//					boolean backupMessage=sPreferences.getBoolean("select_message", false);
//					boolean backupCall=sPreferences.getBoolean("select_call_record", false);
//					boolean backupRemind=sPreferences.getBoolean("select_remind", false);
//					boolean backupCollect=sPreferences.getBoolean("select_collection", false);
					
					if(contactCheck)
					{
						sPreferences.edit().putBoolean("select_contact", contactCheck).commit();
					}
					if(messageCheck)
					{
						sPreferences.edit().putBoolean("select_message", messageCheck).commit();
					}
					if(callRecordCheck)
					{
						sPreferences.edit().putBoolean("select_call_record", callRecordCheck).commit();
					}
					if(remindCheck)
					{
						sPreferences.edit().putBoolean("select_remind", remindCheck).commit();
					}
					if(collectionCheck)
					{
						sPreferences.edit().putBoolean("select_collection", collectionCheck).commit();
					}
					
					
					
					isLoginCookie();
						try {
							if(!isBackupCancel)
							{
							    	isBackupCancel=true;
							}else
							       {
									netWorkResult=networkUnit.postCloundContactBackup(contactCheck?getContast():arrayListContact,messageCheck?getSmsInPhone():contentsMessage,remindCheck?getAllRemind():arrayListRemind,callRecordCheck?getCallRecord():logInfosRecord,collectionCheck?getFavoriteData():contentsMessage, cookieid,info.getMacAddress());
								   if(netWorkResult!=null)
									{
										String success=netWorkResult.getSuccess();
										String state=netWorkResult.getState();
										
										isFinish=true;
										//1表示成功,0表示没有登录
										if(!isBackupCancel)
										{
									    	isBackupCancel=true;
										}else
										{
											if("1".equals(success) && "0".equals(state))//临时备份成功，马上进入登录
											{
											   handle.sendEmptyMessage(4);
											   handle.sendEmptyMessage(27)	;
											}
											else if("1".equals(success)&& "1".equals(state))
											{
												handle.sendEmptyMessage(7);
												handle.sendEmptyMessage(27)	;
											}else
											{
												
											}
										}
										backupFinish=true;
								
										backuping=false;

									}
								    if(!isBackupCancel)
								    {
//								    	handle.sendEmptyMessage(9);
								    	isBackupCancel=true;
								    	handle.sendEmptyMessage(27)	;
								    }
									
							
							
							} 
							
						}catch (Exception e) {
								// TODO Auto-generated catch block
								//正在备份的时候出错
							 e.printStackTrace();
								backuping=true;
								handle.sendEmptyMessage(36)	;
								handle.sendEmptyMessage(11);
								 if(!isBackupCancel)
								    {
								    	handle.sendEmptyMessage(9);
								    	isBackupCancel=true;
								    }
								
							}
					
					
			 }
				else{
					handle.sendEmptyMessage(13);
				}
			}
		});
			backupThread.start();	
	
		break;
		
		
       case R.id.btn_cancle:
    	   try {
    	   isBackupCancel=false;
    		   backUpRecoveryFinish();
    		   backupThread.interrupt();
    		   handle.sendEmptyMessage(37);
    		   click_backup_recovery=3;
    		   tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
    	        backuping=false;//备份中
    			
    		   
		} catch (Exception e) {
			// TODO: handle exception
		}
    	   
    	   break;
    	   
       case R.id.local_backup:  //本地备份
    	   
    	    if (!is_localRecovery && !is_localRecoveryp_Finish) {
    	   
    	    	backup_recovery.setVisibility(View.GONE);
    	    	local_backup_recovery.setVisibility(View.VISIBLE);
    	    	local_backup_recovery_ly.setVisibility(View.VISIBLE);
    	    	click_type = 1;
	//    	   localBackUpDialog();
	    	   
	    	   if(!is_localBackUp && !is_localBackUp_Finish) {
	    		   
					local_handler.sendEmptyMessage(31);
					
	    	   } else if (is_localBackUp && !is_localBackUp_Finish) {
	    		   
	    		   local_handler.sendEmptyMessage(32);
	    		   local_handler.sendEmptyMessage(1);
	    		   
	    	   } else if (!is_localBackUp && is_localBackUp_Finish) {
	    		   
	    		   local_handler.sendEmptyMessage(29);
	    		   
	    	   }
	    	   b=true;
    	    } else {
    	    	
    	    	
    	    	Toast.makeText(mainActivity, "正在进行本地恢复，请稍后在进行本地备份操作！", Toast.LENGTH_SHORT).show();
    	    	
    	    }
    	   
    	   break;
    	   
       case R.id.local_recovery:   //本地恢复
    	   
    	   if (!is_localBackUp && !is_localBackUp_Finish) {
    		  
    		   System.out.println("isHaveData ----- > " + isHaveData());
    		   
	    	   if (!isHaveData()) {
	    		   
	    		   Toast.makeText(mainActivity, "本地无备份数据，请先进行备份", Toast.LENGTH_SHORT).show();
	    		   
	    	   } else {
	    		   
	    		   local_backup_recovery.setVisibility(View.VISIBLE);
	    		   backup_recovery.setVisibility(View.GONE);
	    		   local_backup_recovery_ly.setVisibility(View.VISIBLE);
	    		   
	    		   click_type = 2;
	    		   
	    		   if ( !is_localRecovery && !is_localRecoveryp_Finish)
	    			   local_handler.sendEmptyMessage(34);
	    		   else if (!is_localRecovery && is_localRecoveryp_Finish)
	    			   local_handler.sendEmptyMessage(25);
	    		   else if (is_localRecovery && !is_localRecoveryp_Finish) {
	    			   local_handler.sendEmptyMessage(35);
	    		   		local_handler.sendEmptyMessage(1);
	    		   }
	    		   
	    	   }
	    	   b=true;
    	   } else {
    		   Toast.makeText(mainActivity, "正在进行本地备份，请稍后在进行本地恢复操作！", Toast.LENGTH_SHORT).show();
    	   }
    	   
    	   break;
       case R.id.btn_local_backup:   //备份到本地
    	   
    	   
			local_handler.sendEmptyMessage(32);
			local_handler.sendEmptyMessage(1);
			local_handler.sendEmptyMessage(28);
//			firstBackUpData();
			localBackUpData = new LocalBackUpData();
			
			localBackUpData.start();
			
			break;   
       case R.id.btn_local_recovery:  //从本地恢复
    	   
    	   	local_handler.sendEmptyMessage(35);
			local_handler.sendEmptyMessage(1);
			local_handler.sendEmptyMessage(28);
			localRecoveryData();
			
			break;
       case R.id.btn_backup_finish:
    	   
    	   if (click_type == 2) {
    		   
    		   if (mainActivity.homeCallsAdapter != null)
    			   mainActivity.homeCallsAdapter.refreshAll();
    	   }
    	   
    	   local_handler.sendEmptyMessage(33);
    	   local_handler.sendEmptyMessage(2);
    	   
    	   break;
       case R.id.btn_backup_cancel:
    	   
//    	   setCheckBoxClickable();
    	   
    	   is_localBackUp = false;
    	   is_localBackUp_Finish = false;
    	   
    	   setLocalBackUpData();
    	   
    	   backup_recovery.setVisibility(View.VISIBLE);
    	   local_backup_recovery.setVisibility(View.GONE);
    	   local_backup_recovery_ly.setVisibility(View.GONE);
    	   
    	   local_pro_bar.setVisibility(View.GONE);
    	   btn_backup_cancel.setVisibility(View.GONE);
    	   
    	   localBackUpData.interrupt();
    	   
    	   isTip = false;
    	   local_handler.sendEmptyMessage(2);
    	   local_handler.sendEmptyMessage(3);
    	   
    	   break;
    	   
       case R.id.local_backup_contacts:
			
    	   if (click_type == 2) {
    		   
				if (!isCheckedContacts) {
					local_backup_contacts.setChecked(false);
					Toast.makeText(mainActivity, "无此备份数据", Toast.LENGTH_SHORT).show();
				}
    	   }

			break;
		case R.id.local_backup_calllog:
			
			if (click_type == 2){ 
				
				if (!isCheckedCalllog) {
					local_backup_calllog.setChecked(false);
					Toast.makeText(mainActivity, "无此备份数据", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.local_backup_collection:
			
			if (click_type == 2){ 
				
				if (!isCheckedCollection) {
					local_backup_collection.setChecked(false);
					Toast.makeText(mainActivity, "无此备份数据", Toast.LENGTH_SHORT).show();
				}
			}
			
			break;
		case R.id.local_backup_message:
			if (click_type == 2){ 
				
				if (!isCheckedMessage) {
					local_backup_message.setChecked(false);
					Toast.makeText(mainActivity, "无此备份数据", Toast.LENGTH_SHORT).show();
				}
			}
			
			break;
		case R.id.local_backup_remind:
			if (click_type == 2){ 
				
				if (!isCheckedRemind) {
					local_backup_remind.setChecked(false);
					Toast.makeText(mainActivity, "无此备份数据", Toast.LENGTH_SHORT).show();
				}
			}
			break;
	default:
		break;
	}
	}
	
	private boolean isHaveData(){
		
		String filePath = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
		
		File file = new File(filePath);
		
		if (file.exists() && file.isDirectory()) {    
			if(file.list().length > 0) {        
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private void isCheck(){
		local_contacts = local_backup_contacts.isChecked();
		local_message = local_backup_message.isChecked();
		local_remind = local_backup_remind.isChecked();
		local_calllog = local_backup_calllog.isChecked();
		local_collection = local_backup_collection.isChecked();
	}
	
	private void setLocalBackUpData(){
		
		local_backup_contacts.setChecked(true);
		local_backup_contacts.setClickable(true);
	
		local_backup_message.setChecked(true);
		local_backup_message.setClickable(true);
	
		local_backup_collection.setChecked(true);
		local_backup_collection.setClickable(true);
	
		local_backup_remind.setChecked(true);
		local_backup_remind.setClickable(true);
	
		local_backup_calllog.setChecked(true);
		local_backup_calllog.setClickable(true);
	}
	
	private void checkLocalRecoveryData(){
		
		if (!isFileExist("contacts.txt")) {
			local_backup_contacts.setChecked(false);
			isCheckedContacts = false;
		} else {
			local_backup_contacts.setChecked(true);
			isCheckedContacts = true;
		}
		
		if (!isFileExist("message.txt")) {
			local_backup_message.setChecked(false);
			isCheckedMessage = false;
		}  else {
			local_backup_message.setChecked(true);
			isCheckedMessage = true;
		}
		
		if (!isFileExist("collect.txt")) {
			local_backup_collection.setChecked(false);
			isCheckedCollection = false;
		}  else {
			local_backup_collection.setChecked(true);
			isCheckedCollection = true;
		}
		
		if (!isFileExist("remind.txt")) {
			local_backup_remind.setChecked(false);
			isCheckedRemind = false;
		} else {
			local_backup_remind.setChecked(true);
			isCheckedRemind = true;
		}
		
		if (!isFileExist("calllog.txt")) {
			local_backup_calllog.setChecked(false);
			isCheckedCalllog = false;
		} else {
			local_backup_calllog.setChecked(true);
			isCheckedCalllog = true;
		}
	}
	
	private boolean isFileExist(String fileName) {
		
		String filePath = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
		
		File file = new File(filePath + fileName);
    	
    	if (!file.exists()) {// 文件存在
    		return false;
    	}
    	
    	return true;
		
	}
	
	private void getCheckBoxClickable(){
		local_backup_contacts.setClickable(false);
		local_backup_message.setClickable(false);
		local_backup_remind.setClickable(false);
		local_backup_calllog.setClickable(false);
		local_backup_collection.setClickable(false);
	}
	
//	private void setCheckBoxClickable(){
//		local_backup_contacts.setClickable(true);
//		local_backup_message.setClickable(true);
//		local_backup_remind.setClickable(true);
//		local_backup_calllog.setClickable(true);
//		local_backup_collection.setClickable(true);
//	}
	
	public void backUpRecoveryFinish()
	{
		backup_recovery.setVisibility(View.VISIBLE);
    	scrollView.setVisibility(View.GONE);
    	showContact.setVisibility(View.GONE);
    	showLoginContact.setVisibility(View.GONE);
    	contactCheckBox.setChecked(true);
    	messageCheckBox.setChecked(true);
    	callRecordCheckBox.setChecked(true);
    	remindCheckBox.setChecked(true);
    	collectionCheckBox.setChecked(true);
    	contactCheckBox.setClickable(false);
    	messageCheckBox.setClickable(false);
    	callRecordCheckBox.setClickable(false);
    	remindCheckBox.setClickable(false);
    	collectionCheckBox.setClickable(false);
    	
    	buttonOk.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.GONE);
        recoveryOk.setVisibility(View.GONE);
        recovery_select_contact.setVisibility(View.GONE);
        
        
        
        
        recoveryContactCheckBox.setVisibility(View.VISIBLE);
        recoveryMessageCheckBox.setVisibility(View.VISIBLE);
        recoveryCallRecordCheckBox.setVisibility(View.VISIBLE);
        recoveryRemindCheckBox.setVisibility(View.VISIBLE);
        recoveryCollectionCheckBox.setVisibility(View.VISIBLE);
        
        contactCheckBox.setVisibility(View.VISIBLE);
    	messageCheckBox.setVisibility(View.VISIBLE);
    	callRecordCheckBox.setVisibility(View.VISIBLE);
    	remindCheckBox.setVisibility(View.VISIBLE);
    	collectionCheckBox.setVisibility(View.VISIBLE);
    	
    	 contactCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
     	messageCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
     	callRecordCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
     	remindCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
     	collectionCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
        
    	recoveryContactCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
    	recoveryMessageCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
    	recoveryCallRecordCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
    	recoveryRemindCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
    	recoveryCollectionCheckBox.setButtonDrawable(R.drawable.checkbox_selector);
    	
    	
    	cacelBackUp.setVisibility(View.GONE);
    	
    	
    	recoveryProgressBar.setVisibility(View.GONE);
		finshRecovery.setImageResource(R.drawable.back_cloud);
		bt_recovery_finish.setVisibility(View.GONE);
		
		progressBar.setVisibility(View.GONE);
		
//    	recoveryProgress.setText("");
//    	backupProgress.setText("");
	}
	
	public void deleteCallRecord()
	{
		mainActivity.getContentResolver().delete(CallLog.Calls.CONTENT_URI,null, null);
	}
	private void insertCallLog(CallLogInfo callLogInfo)
	{
	    // TODO Auto-generated method stub
	    ContentValues values = new ContentValues();
	    values.put(CallLog.Calls.NUMBER, callLogInfo.getmCaller_number());
	    values.put(CallLog.Calls.DATE, callLogInfo.getLong_date());
	    values.put(CallLog.Calls.DURATION, callLogInfo.getmCall_duration());//通话时长
	    values.put(CallLog.Calls.TYPE,Integer.valueOf(callLogInfo.getmCall_type()));//未接
	     values.put(CallLog.Calls.NEW, 0);//0已看1未看
	    
	    mainActivity.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
	}
	
	public void deleteMessage()
	{
		mainActivity.getContentResolver().delete(Uri.parse("content://sms/" ),null, null);
	}
	private void writeToDataBase(SmsContent smsContent)  
    {  
        ContentValues values = new ContentValues();
        values.put("address", smsContent.getSms_number());
        values.put("body", smsContent.getSms_body());  
        values.put("type", smsContent.getTypeId()); //1为收，2为发 
        values.put("date", smsContent.getDate());
        values.put("read", "1");//"1"means has read ,1表示已读  
        mainActivity.getContentResolver().insert(Uri.parse("content://sms/"), values);  
        
//        Cursor cursor = mainActivity.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"_id"}, null, null, " _id desc limit 1");
//        
//        if (cursor.getCount() > 0) {
//        	
//	        if (cursor.moveToFirst()) {
//	        	
//	        	do {
//	        		
//	        		long id = cursor.getLong(cursor.getColumnIndex("_id"));
//	        		System.out.println(" id ======= > " + id);
//	        		ContentValues v = new ContentValues();
//	        		v.put("date", smsContent.getDate());
//	        		
//	        		mainActivity.getContentResolver().update(Uri.parse("content://sms/"), v, " _id = " +id, null);
//	        		
//	        	}while(cursor.moveToNext());
//	        }
//        }
//        cursor.close();
        
    }  
	//删除全部联系人
	private void delContact() {
		try {
//			int contact=mainActivity.getContentResolver().delete(Contacts.CONTENT_URI, null, null);
			
			
			/*getContentResolver().delete(
					ContentUris.withAppendedId(
							Contacts.CONTENT_URI,
							Long.valueOf(contactId)), null,
					null);*/
			
		int s=	mainActivity.getContentResolver().delete(Uri.parse(ContactsContract.RawContacts.CONTENT_URI.toString() +"?" + ContactsContract.CALLER_IS_SYNCADAPTER+"=true"), ContactsContract.RawContacts._ID + ">0", null);
			
			System.out.println(s);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//增加联系人
//		public void addContact(ContactBean contactBean)
//		{
//			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//			
//			//插入联系人
//			ContentResolver cr = mainActivity.getContentResolver();
//			
//			ContentValues  values=new ContentValues();
//			Uri  rawcontacturi=cr.insert(RawContacts.CONTENT_URI, values);
//			long  rawcontactid=ContentUris.parseId(rawcontacturi);
//			
//			System.out.println("恢复中  ----  contactBean.getNick() " + contactBean.getNick());
//			System.out.println("    ----  contactBean.getJob() " + contactBean.getJob());
//			
//			//联系人姓名
//		    if(!"".equals(contactBean.getNick() )&& null !=contactBean.getNick())
//				{
//					
//					if(contactBean.getNick().contains(onlys))  //机主
//					{
//						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
//			//			builder.withValueBackReference(Data.RAW_CONTACT_ID, (int)backRef); 
//						builder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//						builder.withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
//						builder.withValue(StructuredName.DISPLAY_NAME,contactBean.getNick().replace(onlys, ""));
//						builder.withYieldAllowed(true);
//						ops.add(builder.build());
//						
//						ContentProviderOperation.Builder builder1 = ContentProviderOperation.newInsert(Data.CONTENT_URI);
//						builder1.withValue(Data.RAW_CONTACT_ID, rawcontactid);
//						builder1.withValue(Data.MIMETYPE, MainActivity.MY_CONTACT_MIME_TYPE);
//						builder1.withValue("data2", "我是机主");
//						
//						ops.add(builder1.build());
//						
//						Cursor contactIdCursor = mainActivity.getContentResolver().query(RawContacts.CONTENT_URI,
//								new String[] { RawContacts.CONTACT_ID },RawContacts._ID + "=" + rawcontactid, null,
//								null);
//
//						if (contactIdCursor != null&& contactIdCursor.moveToFirst()) {
//							long contactId = contactIdCursor.getLong(0);
//							SharedPreferences ss = mainActivity.getSharedPreferences("myNumberContactId", 0);
//							Editor ed = ss.edit();
//							ed.putLong("myContactId", contactId);
//							ed.commit();
//
//							mainActivity.MY_CONTACT_ID = contactId;
//						}
//						
//					}else{ //普通联系人
//						
//						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
//			//			builder.withValueBackReference(Data.RAW_CONTACT_ID, (int)backRef); 
//						builder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//						builder.withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
//						builder.withValue(StructuredName.DISPLAY_NAME,contactBean.getNick());
//						builder.withYieldAllowed(true);
//						ops.add(builder.build());
//					}
//				}
//			
//			
//			    if(!"".equals(contactBean.getPhoto()) && contactBean.getPhoto()!=null)
//			    {
//			    	ContentProviderOperation.Builder b_photo = ContentProviderOperation.newInsert(Data.CONTENT_URI);
////					b_photo.withValueBackReference(Data.RAW_CONTACT_ID, backRef);
//					b_photo.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//					b_photo.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
//					//
//					b_photo.withValue(Photo.IS_SUPER_PRIMARY, 1);
//					
////					ByteArrayOutputStream out = new ByteArrayOutputStream();
//					
//					photoContentValues.put(Photo.PHOTO, contactBean.getPhoto());
//					b_photo.withValues(photoContentValues);
//					b_photo.withYieldAllowed(true);
//					ops.add(b_photo.build());
//			    }
//			
//		    //生日
//		    if(!"".equals(contactBean.getBrithday()) && null !=contactBean.getBrithday())
//			{
//		    	ContentProviderOperation.Builder bb = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
//				bb.withValue(Data.RAW_CONTACT_ID,rawcontactid);
//					 
//				ContentValues cv = new ContentValues();
//				cv.put(ContactsContract.CommonDataKinds.Event.START_DATE,contactBean.getBrithday());
//				bb.withValues(cv);
//				bb.withYieldAllowed(true);
//				 ops.add(bb.build());
//			}
//		   
//		
//			if(!"".equals(contactBean.getNumber()) && null !=contactBean.getNumber())
//			{
//				ContentProviderOperation.Builder numberss = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
//				numberss.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//				ContentValues	cvs = new ContentValues();
//				cvs.put(Phone.TYPE, Phone.TYPE_MOBILE);
//				cvs.put(Phone.NUMBER, contactBean.getNumber());
//				numberss.withValues(cvs);
//				numberss.withYieldAllowed(true);
//				ops.add(numberss.build());
//			}
//	 		    
//				if(!"".equals(contactBean.getOrganizations()) && null !=contactBean.getOrganizations())
//				{
//					ContentProviderOperation.Builder b_company = ContentProviderOperation.newInsert(Data.CONTENT_URI);
//			    	b_company.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//					b_company.withValue(Data.MIMETYPE,  Organization.CONTENT_ITEM_TYPE);
//					b_company.withValue(Organization.COMPANY,contactBean.getOrganizations());
//					b_company.withYieldAllowed(true);
//					ops.add(b_company.build());
//				}
//				
//		    	if(!"".equals(contactBean.getJob()) && null !=contactBean.getJob())
//		    	{
//	    		   //添加职业信息
////			   	     ContentProviderOperation.Builder b_job = ContentProviderOperation.newInsert(Data.CONTENT_URI);
////			   	     b_job.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
////			   		 b_job.withValue(Data.MIMETYPE,  Organization.CONTENT_ITEM_TYPE);
////			   		 b_job.withValue(Organization.JOB_DESCRIPTION,contactBean.getJob());
////			   		 b_job.withYieldAllowed(true);
////			   		 ops.add(b_job.build());
//			   		 
//			   		 ContentProviderOperation.Builder bb = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Organization.CONTENT_ITEM_TYPE);
//					 bb.withValue(Data.RAW_CONTACT_ID,rawcontactid);
//					 ContentValues cv = new ContentValues();
//					 cv.put(Organization.JOB_DESCRIPTION, contactBean.getJob());
//					 bb.withValues(cv);
//					 bb.withYieldAllowed(true);
//					 ops.add(bb.build());
//		    	}
//		 
//		 ContentProviderOperation.Builder contentbuilder = null;
//		 
//    	 ContentValues cv= null;
//    	 if(!"".equals(contactBean.getEmail()) && null !=contactBean.getEmail())
//    	 {
//    		 contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
// 			contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
// 			cv = new ContentValues();
// 	 		cv.put(Email.DATA,contactBean.getEmail());
// 	 		cv.put(Email.TYPE,Email.TYPE_WORK);
// 			contentbuilder.withValues(cv);
// 			contentbuilder.withYieldAllowed(true);
// 			 
//    	 }
//    	 if(!"".equals(contactBean.getAddress()) && null !=contactBean.getAddress())
//    	 {
//    		 contentbuilder = ContentProviderOperation
// 					.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
// 			contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
// 			cv = new ContentValues();
// 	 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET,contactBean.getAddress());
// 	 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
// 			contentbuilder.withValues(cv);
// 			contentbuilder.withYieldAllowed(true);
//    	 }
//		
//    	 if(!"".equals(contactBean.getWebsite()) && null !=contactBean.getWebsite())
//    	 {
//    		 contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Website.CONTENT_ITEM_TYPE);
// 			contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
// 			cv = new ContentValues();
// 			cv.put(Website.URL, contactBean.getWebsite());
// 			cv.put(Website.TYPE, Website.TYPE_WORK);
// 			contentbuilder.withValues(cv);
// 			contentbuilder.withYieldAllowed(true);
//    	 }
//    	 
//		if(!"".equals(contactBean.getNotes()) && null != contactBean.getNotes())	
//		{
//			contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Note.CONTENT_ITEM_TYPE);
//			contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//			cv = new ContentValues();
//			cv.put(Note.NOTE, contactBean.getNotes());
//			contentbuilder.withValues(cv);
//			contentbuilder.withYieldAllowed(true);
//		}
//
//	    	 if(!"".equals(contactBean.getWebsite()) && null !=contactBean.getWebsite())
//	    	 {
//	    		 contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Website.CONTENT_ITEM_TYPE);
//	 			contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//	 			cv = new ContentValues();
//	 			cv.put(Website.URL, contactBean.getWebsite());
//	 			cv.put(Website.TYPE, Website.TYPE_WORK);
//	 			contentbuilder.withValues(cv);
//	 			contentbuilder.withYieldAllowed(true);
//	    	 }
//			if(!"".equals(contactBean.getNotes()) && null != contactBean.getNotes())	
//			{
//				contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Note.CONTENT_ITEM_TYPE);
//				contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//				cv = new ContentValues();
//				cv.put(Note.NOTE, contactBean.getNotes());
//				contentbuilder.withValues(cv);
//				contentbuilder.withYieldAllowed(true);
//			}
//				
//			
//				contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
//				contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
//				cv = new ContentValues();
//		 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET,contactBean.getAddress());
//		 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
//				contentbuilder.withValues(cv);
//				contentbuilder.withYieldAllowed(true);
//			
//				if(contentbuilder!=null)
//				{
//					 ops.add(contentbuilder.build());
//				}
//				
//				
//				 try {
//					mainActivity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (OperationApplicationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		}
		
	//判断cookie 有没有过期
	public void isLoginCookie()
	{
		SharedPreferences shPreferences=mainActivity.getSharedPreferences("login", 0);
    	recoveryEmail=shPreferences.getString("username","");
        recoveryPassword=shPreferences.getString("password","");
        cookieid=shPreferences.getString("cookie", "");
        
	}
	public void notLogin()
	{
		SharedPreferences shPreferences=mainActivity.getSharedPreferences("login", 0);
    	recoveryEmail=shPreferences.getString("username","");
        recoveryPassword=shPreferences.getString("password","");
        cookieid=shPreferences.getString("cookie", "");
        if(!"".equals(cookieid))
        {
        	new Thread(new Runnable() {
				public void run() {
					try {
						NetWorkResult	netWorkResult=networkUnit.isLoginCookie(cookieid);
						//0表示cookie未过期，1表示cookie过期
						if("0".equals(netWorkResult.getSuccess()))
						{
							islogin=true;
						}
						else if("1".equals(netWorkResult.getSuccess()))
						{
							islogin=false;
						}
						handle.sendEmptyMessage(6);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handle.sendEmptyMessage(11);
					}
				}
			}).start();
        }
        else
        {
        	Toast.makeText(mainActivity, "你还没有备份，请先备份", Toast.LENGTH_SHORT).show();
        }
        
	}
	
	//获取全部联系人
	public ArrayList<ContactBean> getContast() {
		
		checkLord();
		
		ArrayList<ContactBean> arrayList = new ArrayList<ContactBean>();
		/*
		 * 获取联系人信息，将联系人的名字存入nameList,将联系人的号码存入numberList
		 */
		Uri uri = Contacts.CONTENT_URI;
		String[] projection = new String[] { Contacts._ID, // 0
				Contacts.DISPLAY_NAME, // 1
				Contacts.STARRED, // 2
				Contacts.TIMES_CONTACTED, // 3
				Contacts.CONTACT_PRESENCE, // 4
				Contacts.PHOTO_ID, // 5
				Contacts.LOOKUP_KEY, // 6
				Contacts.HAS_PHONE_NUMBER, // 7
				Contacts.IN_VISIBLE_GROUP,// 8

		};
		
		ContentResolver content = mainActivity.getContentResolver();
		String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
		Cursor cursor = content.query(uri, projection, null, null, sortOrder);

		
		while (cursor.moveToNext()) {
			ContactBean contactBean = new ContactBean();
			// 获取联系人的姓名
			String name = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			contactBean.setPhoto_id(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
			// 获取联系人的ID，并根据ID来获取电话号码
			// 注意：一个联系人可以有多个电话号码，按照这种方式储存的话，nameList与numberList中的数据将失去联系
			long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));

//			System.out.println(" MY_CONTACT_ID ---> " + MY_CONTACT_ID+ " contactId  ---> " + contactId);

			if (contactId == MY_CONTACT_ID) {
				contactBean.setNick(onlys + name);
			} else {
				contactBean.setNick(name);
			}

//			System.out.println("  contactBean.getNick()  --->"+ contactBean.getNick());

			contactBean.setContact_id(contactId);

			// /////////////////////////////////////////////
			// 查看联系人有多少电话号码, 如果没有返回0
			int phoneCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (phoneCount > 0) {

				Cursor phonesCursor = mainActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+ contactId, null, null);

				if (phonesCursor.moveToFirst()) {
					List<ContactBean.PhoneInfo> phoneNumberList = new ArrayList<ContactBean.PhoneInfo>();
					do {
						// 遍历所有电话号码
						String phoneNumber = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						// 对应的联系人类型
						int type = phonesCursor.getInt(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

						// 初始化联系人电话信息
						ContactBean.PhoneInfo phoneInfo = new ContactBean.PhoneInfo();
						phoneInfo.type = type;
						phoneInfo.number = phoneNumber;

						phoneNumberList.add(phoneInfo);
					} while (phonesCursor.moveToNext());
					// 设置联系人电话信息
					contactBean.setPhoneList(phoneNumberList);
				}
				phonesCursor.close();
			}

			// /////////////////////////////////////////////

			// 头像
			Cursor cursorphoto = content.query(Contacts.CONTENT_URI,new String[] { Contacts.PHOTO_ID }, Contacts._ID + " = ? ",
					new String[] { String.valueOf(contactId) }, null);
			if (cursorphoto.moveToFirst()) {// 查到了数据
				// 如果没有头像photoId将被赋值为0,更新时注意判断photoId是否大于0
				long photoId = cursorphoto.getLong(0);
				// 保存photo在data表中_id的值,更新时使用
				dataIdOfPhoto = photoId;
				if (photoId > 0) {
					String[] projections = new String[] { Photo.PHOTO };
					String photoSelection = Data._ID + " = ? ";
					String[] selectionArgs = new String[] { String	.valueOf(photoId) };
					Cursor photoCursor = content.query(Data.CONTENT_URI,projections, photoSelection, selectionArgs, null);
					if (photoCursor.moveToFirst()) {// 用户设置了头像
						byte[] photo = photoCursor.getBlob(0);
						contactBean.setPhoto(photo);
						// MyLog.i("用户设置了头像");
					} else {// 该联系人没有头像，使用默认的图片
						// MyLog.i("用户没有设置头像,使用默认图片");
					}
					photoCursor.close();
				} else {
					// 没有头像,使用默认图片
				}
			}
			if (cursorphoto != null) {
				cursorphoto.close();
			}

			Cursor organizations = content.query(Data.CONTENT_URI,	new String[] { Data._ID, Organization.COMPANY,Organization.TITLE, Organization.JOB_DESCRIPTION },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Organization.CONTENT_ITEM_TYPE + "'",
					new String[] { String.valueOf(contactId) }, null);
			while (organizations.moveToNext()) {

				String company = organizations.getString(organizations.getColumnIndex(Organization.COMPANY));

				String job = organizations.getString(organizations.getColumnIndex(Organization.JOB_DESCRIPTION));

				if (company != null)// 公司 组织名称
				{
					contactBean.setOrganizations(company);
				}
				if (job != null) // 职业
				{
					contactBean.setJob(job);
					System.out.println("job --->" + job);
				}
			}
			;
			organizations.close();

			Cursor birthrdayCursor = content.query(Data.CONTENT_URI,new String[] {Data._ID,
									ContactsContract.CommonDataKinds.Event.START_DATE },Data.CONTACT_ID
									+ "=?"+ " AND "+ Data.MIMETYPE+ "='"+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE+ "'",
							new String[] { String.valueOf(contactId) }, null);

			while (birthrdayCursor.moveToNext()) { // 获取第一个生日

				String bir = birthrdayCursor.getString(birthrdayCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
				contactBean.setBrithday(bir);
				break;
			}
			birthrdayCursor.close();


			// 获得联系人的EMAIL
			Cursor emailCur = mainActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + "="+ contactId, null, null);

			if (emailCur.moveToFirst()) {
				List<ContactBean.EmailInfo> emailList = new ArrayList<ContactBean.EmailInfo>();
				do {
					// 遍历所有的email
					String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
					int type = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

					// 初始化联系人邮箱信息
					ContactBean.EmailInfo emailInfo = new ContactBean.EmailInfo();
					emailInfo.type = type; // 设置邮箱类型
					emailInfo.email = email; // 设置邮箱地址

					emailList.add(emailInfo);
				} while (emailCur.moveToNext());

				contactBean.setEmailList(emailList);
			}

			emailCur.close();

			// 获取该联系人地址
			Cursor addressCur = content
					.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
			while (addressCur.moveToNext()) {
				// 遍历所有的地址
				String street = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//				contactBean.setAddress(street);
				contactBean.getAddressList().add(street);
			}
			addressCur.close();

			// 获取网址信息
			Cursor websitesCur = content.query(Data.CONTENT_URI, new String[] {Data._ID, Website.URL }, Data.CONTACT_ID + "=?" + " AND "
					+ Data.MIMETYPE + "='" + Website.CONTENT_ITEM_TYPE + "'",
					new String[] { String.valueOf(contactId) }, null);
			while (websitesCur.moveToNext()) {
				String website = websitesCur.getString(websitesCur.getColumnIndex(Website.URL));
//				contactBean.setWebsite(website);
				contactBean.getWebSiteList().add(website);
			}
			websitesCur.close();

			// 获取备注信息
			Cursor notesCur = content.query(Data.CONTENT_URI, new String[] {Data._ID, Note.NOTE }, Data.CONTACT_ID + "=?" + " AND "
					+ Data.MIMETYPE + "='" + Note.CONTENT_ITEM_TYPE + "'",new String[] { String.valueOf(contactId) }, null);

			while (notesCur.moveToNext()) {
				String noteinfo = notesCur.getString(notesCur.getColumnIndex(Note.NOTE));
//				contactBean.setNotes(noteinfo);
				contactBean.getNoteList().add(noteinfo);
			}
			;
			notesCur.close();

			arrayList.add(contactBean);
		}
		cursor.close();
		return arrayList;
	}
	
		
		//获取通话记录
		public ArrayList<CallLogInfo> getCallRecord()
		{
			ArrayList<CallLogInfo> logInfos=new ArrayList<CallLogInfo>();
			String phoneNumber = "";
			String phoneName="";
			int type;
			long callTime;
			Date date;
			long _id;
			ContentResolver cr = mainActivity.getContentResolver();
			final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.CACHED_NAME,CallLog.Calls.TYPE, CallLog.Calls.DATE,CallLog.Calls._ID,CallLog.Calls.DURATION}, null, null,CallLog.Calls.DEFAULT_SORT_ORDER);
	        while(cursor.moveToNext())
	        {
	        	CallLogInfo callLogInfo=new CallLogInfo();
        	    
        	    phoneNumber = cursor.getString(0);
	            phoneName = cursor.getString(1);
	            type = cursor.getInt(2);
	            date = new Date(Long.parseLong(cursor.getString(3)));
	            _id=cursor.getLong(4);
	            String duration=cursor.getString(5);
	            callLogInfo.setmCaller_name(phoneName);
	            callLogInfo.setmCaller_number(phoneNumber);
	            callLogInfo.setmCall_type(String.valueOf(type));
	            callLogInfo.setLong_date(Long.parseLong(cursor.getString(3)));
	            callLogInfo.setId(_id);
	            callLogInfo.setmCall_duration(duration);
	            logInfos.add(callLogInfo);
	        }
	        cursor.close();
			return logInfos;
		}
		//获取所有短信
		
		public ArrayList<SmsContent> getSmsInPhone()   
		{   
			ArrayList<SmsContent> contents=new ArrayList<SmsContent>();
		    final String SMS_URI_ALL   = "content://sms/";     
		    try{   
		        ContentResolver cr = mainActivity.getContentResolver();   
		        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};   
		        Uri uri = Uri.parse(SMS_URI_ALL);   
		        Cursor cur = cr.query(uri, projection, null, null, null);   
		        if (cur.moveToFirst()) {   
		            String name;    
		            String phoneNumber;          
		            String smsbody;   
		            String date;   
		            String type;   
		            int nameColumn = cur.getColumnIndex("person");   
		            int phoneNumberColumn = cur.getColumnIndex("address");   
		            int smsbodyColumn = cur.getColumnIndex("body");   
		            int dateColumn = cur.getColumnIndex("date");   
		            int typeColumn = cur.getColumnIndex("type");   
		            
		            do{   
		                name = cur.getString(nameColumn);                
		                phoneNumber = cur.getString(phoneNumberColumn);   
		                smsbody = cur.getString(smsbodyColumn);   
		                
		                int typeId = cur.getInt(typeColumn);   
		                if(typeId == 1){   
		                    type = "接收";   
		                } else if(typeId == 2){   
		                    type = "发送";   
		                } else {   
		                    type = "";   
		                }   
		                SmsContent smsContent=new SmsContent();
		                smsContent.setSms_name(name);
		                smsContent.setSms_number(phoneNumber);
		                smsContent.setSms_body(smsbody);
		                smsContent.setDate(cur.getLong(dateColumn));
		                smsContent.setSend_type(type);
		                smsContent.setTypeId(typeId);
		                if(phoneNumber!=null)
		                {
		                	contents.add(smsContent);
		                }
		            }while(cur.moveToNext());   
		        } else {   
		        }   
		        
		        cur.close();
		            
		    } catch(SQLiteException ex) {   
		    }   
		    return contents;   
		} 
		//通过电话号码获取名字
		public String getNameByNumber(String numbers)
		{
			
			String info [] = PhoneNumberTool.getContactInfo(mainActivity, numbers);
			
		return 	info [0];
		
			
		}
	    //获取所有短信收藏
		
		public ArrayList<SmsContent> getFavoriteData()
		{
			ArrayList<SmsContent> arrayListSms=new ArrayList<SmsContent>();
			myDatabaseUtil=DButil.getInstance(mainActivity);
			Cursor cursor=myDatabaseUtil.fetchMessageFavorite();
			while(cursor.moveToNext())
			{
			    SmsContent smsContent=new SmsContent();
				//threadid
			    smsContent.setThread_id(Long.valueOf(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.THREAD_ID))));
				//CONTENT_ID
			    smsContent.setId(Long.valueOf(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.CONTENT_ID))));
				//FAVORITE_CONTENT
			    smsContent.setSms_body(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.FAVORITE_CONTENT)));
				//CONTENT_TIME
			    smsContent.setDate(cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.CONTENT_TIME)));
				//FAVORITE_NUMBER
			    smsContent.setSms_number(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.FAVORITE_NUMBER)));
				//FAVORITE_SENDER
			    smsContent.setSend_type(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.FAVORITE_SENDER)));
			    arrayListSms.add(smsContent);
			}
			cursor.close();
			return arrayListSms;
		}
		//获取提醒
		public ArrayList<RemindBean> getAllRemind()
		{
			ArrayList<RemindBean> arrayListRemind=new ArrayList<RemindBean>();
			myDatabaseUtil=DButil.getInstance(mainActivity);
			Cursor cursor=myDatabaseUtil.queryAllRemind();
			
			if (cursor.getCount() > 0) {
				
				if (cursor.moveToFirst()) {
					 do{
						 	RemindBean remindBean=new RemindBean();
							remindBean.setId(cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_ID)));
							remindBean.setContent(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT)));
							remindBean.setContacts(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT)));
							remindBean.setParticipants(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.REMIND_PARTICIPANT)));
							remindBean.setStart_time(cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REMIND_START)));
							remindBean.setEnd_time(cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REMIND_END)));
							
							remindBean.setRemind_type(cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_TYPE)));
							remindBean.setRemind_num(cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_NUM)));
							remindBean.setRemind_time(cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_TIME)));
							
							remindBean.setRepeat_fre(cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ)));
							remindBean.setRepeat_type(cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE)));
							remindBean.setRepeat_condition(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION)));
							remindBean.setRepeat_start_time(cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME)));
							remindBean.setRepeat_end_time(cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME)));
							remindBean.setCount(cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.HAS_REMIND_TIME)));
							arrayListRemind.add(remindBean);
					 }while(cursor.moveToNext());
				}
			}
			cursor.close();
			return arrayListRemind;
		}
		
	    
		boolean onBackPress()
		{
			if(backup_recovery.getVisibility()==View.VISIBLE)
			{
				b=false;
			}
			if(recoveryFinish)
			{
                backUpRecoveryFinish();
				return b;
			}
			if(!backupFinish)
			{
				backUpRecoveryFinish();
				
				return b;
			}
			
			if(b)
			{
				if(showLoginContact.getVisibility()==View.VISIBLE)
				{
					isLoginIng=true;
				}
				backUpRecoveryFinish();
//				isBackupCancel=false;
				tv_tips_top_backup.setText("即将对你本机的以下数据进行备份");
				
				return b;
			}
			
			
			return b;
		}
		
		 /**
         * 备份联系人
         */
        private void backupContacts(List<ContactBean> infos){
            
        	JSONObject jObject = new JSONObject();
    		JSONArray jaonContact = new JSONArray();
        	
    		try {
	               
                for (int i=0;i<infos.size();i++) {
                	
                	if (isTip) {
                		
	                	ContactBean contactBean = infos.get(i);
	                	
	                	JSONObject j = new JSONObject();
	        			j.put("contact_id", contactBean.getContact_id()!=null?contactBean.getContact_id():"");
	        			j.put("contact_name", contactBean.getNick()!=null?contactBean.getNick():"");
	        			j.put("contact_organizations", contactBean.getOrganizations()!=null?contactBean.getOrganizations():"");
	        			j.put("contact_job", contactBean.getJob()!=null?contactBean.getJob():"");
	        			j.put("contact_birthrday", contactBean.getBrithday()!=null?contactBean.getBrithday():"");
	        			//联系人头像
	        			String images=null;
	        			try {
		        			if(contactBean.getPhoto()!=null)
		        			{
								images = new String(contactBean.getPhoto(),"ISO-8859-1");
		        			}
	        			} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
	        			j.put("contact_image", images!=null?images:"");
	        			//联系人电话
	        			JSONArray jsonNumber = new JSONArray();
	        			for (int n =0;n<contactBean.getPhoneList().size();n++) {
	        				PhoneInfo phoneInfo = contactBean.getPhoneList().get(n);
	        				
	        				JSONObject phone = new JSONObject();
	        				
	        				phone.put("type", phoneInfo.type);
	        				phone.put("number", phoneInfo.number);
	        				
	        				jsonNumber.put(phone);
	        			}
	        			j.put("contact_number", jsonNumber);
	        			//联系人email
	        			JSONArray jsonEmail = new JSONArray();
	        			for (int e =0;e<contactBean.getEmailList().size();e++) {
	        				EmailInfo emailInfo = contactBean.getEmailList().get(e);
	        				
	        				JSONObject email = new JSONObject();
	        				email.put("type", emailInfo.type);
	        				email.put("email", emailInfo.email);
	        				jsonEmail.put(email);
	        			}
	        			j.put("contact_email", jsonEmail);
	        			//联系人地址
	        			JSONArray jsonAddress = new JSONArray();
	        			for(int a=0;a<contactBean.getAddressList().size();a++) {
	        				
	        				JSONObject address = new JSONObject();
	        				address.put("address", contactBean.getAddressList().get(a));
	        				jsonAddress.put(address);
	        				
	        			}
	        			j.put("contact_address",jsonAddress);
	        			//联系人网站
	        			JSONArray jsonWebSite = new JSONArray();
	        			for(int a=0;a<contactBean.getWebSiteList().size();a++) {
	        				
	        				JSONObject webSite = new JSONObject();
	        				webSite.put("webSite", contactBean.getWebSiteList().get(a));
	        				jsonWebSite.put(webSite);
	        				
	        			}
	        			j.put("contact_website",jsonWebSite);
	        			//联系人备注
	        			JSONArray jsonNote = new JSONArray();
	        			for(int a=0;a<contactBean.getNoteList().size();a++) {
	        				
	        				JSONObject note = new JSONObject();
	        				note.put("note", contactBean.getNoteList().get(a));
	        				jsonNote.put(note);
	        				
	        			}
	        			j.put("contact_notes",jsonNote);
	        			jaonContact.put(j);
                	}
        		}
        		
        		jObject.put("contact", jaonContact);
            } catch (JSONException e) {
				e.printStackTrace();
			}
    		
    		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        		return;
        	}
        	
        	File file = new File(Environment.getExternalStorageDirectory() + "/DJ_AddressBook/contacts.txt");
        	
        	path = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
        	
        	if (!file.getParentFile().exists()) {// 文件不存在
        		file.getParentFile().mkdirs();// 创建文件夹
        	}
    		
        	OutputStreamWriter writer = null;
            try {
                
            	 writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            	 
            	 writer.write(jObject.toString());
 				 writer.flush();
                
//                handle.sendEmptyMessage(19);
                
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
				try {
	        		if (writer != null) {
	        			writer.close();
	        		}
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
            
        }
        
        /**
         * 备份短信信息
         * @param cArrayListMessage
         */
        private void backUpSms(List<SmsContent> cArrayListMessage){
        	
        	JSONObject jObject = new JSONObject();
        	JSONArray jaonMessage= new JSONArray();
        	
        	try {
        		
	    		for(int i=0;i<cArrayListMessage.size();i++) {
	    			
	    			if (isTip) {
		    			SmsContent smsContent=cArrayListMessage.get(i);
		    			JSONObject j = new JSONObject();
		    			
						j.put("message_id", smsContent.getId());
						j.put("message_thread_id", smsContent.getThread_id());
		    			j.put("message_number", smsContent.getSms_number()!=null ?smsContent.getSms_number():"");
		    			j.put("message_time", smsContent.getDate()!= -1 ?smsContent.getDate():"");
		    			j.put("message_type", smsContent.getTypeId());
		    			j.put("message_content", smsContent.getSms_body()!=null ?smsContent.getSms_body():"");
		    			
		    			jaonMessage.put(j );
		    			
//	        			try{
//	        				localBackUpData.sleep(1000);
//	        			}catch(InterruptedException ex){
//	        				ex.printStackTrace();
//	        			}
	    			}
	    		}
	    		jObject.put("message", jaonMessage);
	    		
    		} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        		return;
        	}
        	
        	File file = new File(Environment.getExternalStorageDirectory() + "/DJ_AddressBook/message.txt");
        	
        	path = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
        	
        	if (!file.getParentFile().exists()) {// 文件不存在
        		file.getParentFile().mkdirs();// 创建文件夹
        	}
        	
        	OutputStreamWriter writer = null;
        	try {
				writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
				writer.write(jObject.toString());
				writer.flush();
				
//				handle.sendEmptyMessage(20);
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
	        		if (writer != null) {
	        			writer.close();
	        		}
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        }
        
        /**
         * 备份通话记录信息
         * 
         */
        private void backUpCallLog(List<CallLogInfo> infos){
        	
        	JSONObject jObject = new JSONObject();
        	JSONArray jaonCallRecord= new JSONArray();
        	
        	try {
        		
	    		for(int i=0;i<infos.size();i++) {
	    			if (isTip) {
		    			CallLogInfo callLogInfo = infos.get(i);
		    			
		    			JSONObject j = new JSONObject();
		    			j.put("call_record_id", callLogInfo.getId());
		    			j.put("call_record_name", callLogInfo.getmCaller_name()!=null?callLogInfo.getmCaller_name():"");
		    			j.put("call_record_number", callLogInfo.getmCaller_number()!=null?callLogInfo.getmCaller_number():"");
		    			j.put("call_record_type", callLogInfo.getmCall_type()!=null?callLogInfo.getmCall_type():"");
		    			j.put("call_record_time", callLogInfo.getLong_date()!=-1?callLogInfo.getLong_date():"");
		    			j.put("call_record_duration", callLogInfo.getmCall_duration()!=null?callLogInfo.getmCall_duration():"");
		    			jaonCallRecord.put(j );
		    			
//	        			try{
//	        				localBackUpData.sleep(1000);
//	        			}catch(InterruptedException ex){
//	        				ex.printStackTrace();
//	        			}
	    			}
	    		}
	    		jObject.put("callrecord", jaonCallRecord);
	    		
    		} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        		return;
        	}
        	
        	File file = new File(Environment.getExternalStorageDirectory() + "/DJ_AddressBook/calllog.txt");
        	
        	path = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
        	
        	if (!file.getParentFile().exists()) {// 文件不存在
        		file.getParentFile().mkdirs();// 创建文件夹
        	}
        	
        	OutputStreamWriter writer = null;
        	try {
				writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
				writer.write(jObject.toString());
				writer.flush();
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
	        		if (writer != null) {
	        			writer.close();
	        		}
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        }
        
        /**
         * 备份提醒信息
         * 
         */
        private void backUpRemind(List<RemindBean> infos){
        	JSONObject jObject = new JSONObject();
        	JSONArray jaonRemind= new JSONArray();
        	
        	try {
        		
	    		for(int i=0;i<infos.size();i++) {
	    			if (isTip) {
		    			RemindBean remindBean = infos.get(i);
		    			
		    			JSONObject j = new JSONObject();
		    			j.put("remind_id", remindBean.getId());
		    			j.put("remind_content", remindBean.getContent()!=null?remindBean.getContent():"");
		    			j.put("remind_contact", remindBean.getContacts()!=null?remindBean.getContacts():"");
		    			j.put("remind_participants", remindBean.getParticipants()!=null?remindBean.getParticipants():"");
		    			j.put("remind_start_time", remindBean.getStart_time());
		    			j.put("remind_end_time", remindBean.getEnd_time());
		    			j.put("remind_remind_type", remindBean.getRemind_type());
		    			j.put("remind_remind_num", remindBean.getRemind_num());
		    			j.put("remind_remind_time", remindBean.getRemind_time());
		    			j.put("remind_repeat_type", remindBean.getRepeat_type());
		    			j.put("remind_repeat_fre", remindBean.getRepeat_fre());
		    			j.put("remind_repeat_start_time", remindBean.getRepeat_start_time());
		    			j.put("remind_repeat_end_time", remindBean.getRepeat_end_time());
		    			j.put("remind_repeat_count", remindBean.getCount()!=null?remindBean.getCount():"");
		    			jaonRemind.put(j );
		    			
//	        			try{
//	        				localBackUpData.sleep(1000);
//	        			}catch(InterruptedException ex){
//	        				ex.printStackTrace();
//	        			}
	    			}
	    		}
	    		jObject.put("remind", jaonRemind);
	    		
    		} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        		return;
        	}
        	
        	File file = new File(Environment.getExternalStorageDirectory() + "/DJ_AddressBook/remind.txt");
        	
        	path = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
        	
        	if (!file.getParentFile().exists()) {// 文件不存在
        		file.getParentFile().mkdirs();// 创建文件夹
        	}
        	
        	OutputStreamWriter writer = null;
        	try {
				writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
				writer.write(jObject.toString());
				writer.flush();
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
	        		if (writer != null) {
	        			writer.close();
	        		}
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        }
        
        /**
         * 备份短信收藏箱信息
         * 
         */
        private void backUpCollect(List<SmsContent> infos){
        	JSONObject jObject = new JSONObject();
        	JSONArray jaonCallFavorite= new JSONArray();
        	
        	try {
        		
	    		for(int i=0;i<infos.size();i++) {
	    			if (isTip) {
		    			SmsContent sContent = infos.get(i);
		    			
		    			JSONObject j = new JSONObject();
		    			j.put("message_thread_id", sContent.getThread_id());
		    			j.put("message_content_id", sContent.getId());
		    			j.put("message_body", sContent.getSms_body()!=null?sContent.getSms_body():"");
		    			j.put("message_date", sContent.getDate()!=-1?sContent.getDate():"");
		    			j.put("message_number", sContent.getSms_number()!=null?sContent.getSms_number():"");
		    			jaonCallFavorite.put(j);
		    			
//	        			try{
//	        				localBackUpData.sleep(1000);
//	        			}catch(InterruptedException ex){
//	        				ex.printStackTrace();
//	        			}
	    			}
	    		}
	    		jObject.put("favorite", jaonCallFavorite);
	    		
    		} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        		return;
        	}
        	
        	File file = new File(Environment.getExternalStorageDirectory() + "/DJ_AddressBook/collect.txt");
        	
        	path = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/";
        	
        	if (!file.getParentFile().exists()) {// 文件不存在
        		file.getParentFile().mkdirs();// 创建文件夹
        	}
        	
        	OutputStreamWriter writer = null;
        	try {
				writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
				writer.write(jObject.toString());
				writer.flush();
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
	        		if (writer != null) {
	        			writer.close();
	        		}
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        }
        
        /*
         * 恢复联系人信息
         */
        private void localRecoveryContacts(List<ContactBean> list) {
			try {
				
				if(list!=null && list.size()>0)
				{
					delContact();
					for(int i=0;i<list.size();i++)
					{
						//插入通讯录
						addContact(list.get(i));
						
					}
				}
			
			} catch(Exception ex) {
				ex.printStackTrace();
			}
        }
        
        private List<ContactBean> localRecoveryContactsData() {
        	List<ContactBean> list = new ArrayList<ContactBean>();
			try {
			
				String data = readFile("contacts.txt");
				
				JSONObject jsonObject2 = new JSONObject(data);
	            JSONArray jsonArray = jsonObject2.getJSONArray("contact");
	            
	            if(jsonArray != null && jsonArray.length() > 0) {
					for(int i=0; i<jsonArray.length(); i++) {
						ContactBean contactBean=new ContactBean();
						
						JSONObject aObj = jsonArray.getJSONObject(i);
						
						contactBean.setContact_id(Long.valueOf(aObj.getString("contact_id")));
						contactBean.setNick(aObj.getString("contact_name"));
						contactBean.setOrganizations(aObj.getString("contact_organizations"));
						contactBean.setBrithday(aObj.getString("contact_birthrday"));
						contactBean.setJob(aObj.getString("contact_job"));
						//头像
						String imageRecovery=aObj.getString("contact_image");
						byte[] imageRe = imageRecovery.getBytes("ISO-8859-1");
						contactBean.setPhoto(imageRe);
						//电话
						JSONArray number = aObj.getJSONArray("contact_number");
						for (int n =0;n<number.length();n++) {
							
							ContactBean.PhoneInfo phoneInfo = new ContactBean.PhoneInfo();
							
							JSONObject object = number.getJSONObject(n);
							phoneInfo.type = object.getInt("type");
							phoneInfo.number = object.getString("number");
							
							contactBean.phoneList.add(phoneInfo);
							
						}
						//email
						JSONArray email = aObj.getJSONArray("contact_email");
						for(int e = 0; e < email.length(); e++) {
							
							ContactBean.EmailInfo emailInfo = new ContactBean.EmailInfo();
							
							JSONObject object = email.getJSONObject(e);
							emailInfo.type = object.getInt("type");
							emailInfo.email = object.getString("email");
							
							contactBean.emailList.add(emailInfo);
							
						}
						//地址
						JSONArray address = aObj.getJSONArray("contact_address");
						for (int a = 0; a < address.length(); a++) {
							
							JSONObject object = address.getJSONObject(a);
							contactBean.getAddressList().add(object.getString("address"));
						}
						//网址
						JSONArray webSite = aObj.getJSONArray("contact_website");
						for (int w = 0 ; w < webSite.length(); w++) {
							
							JSONObject object = webSite.getJSONObject(w);
							contactBean.getWebSiteList().add(object.getString("webSite"));
						}
						//备注
						JSONArray note = aObj.getJSONArray("contact_notes");
						for (int n = 0 ; n < note.length(); n++) {
							
							JSONObject object = note.getJSONObject(n);
							contactBean.getNoteList().add(object.getString("note"));
						}
						
						list.add(contactBean);
					}
				}
			
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return list;
        }
        
        /*
         * 恢复短信信息
         */
        private void localRecoveryMessage(List<SmsContent> list) {
        	
			try {
			
				if(list!=null && list.size()>0)
				{
					deleteMessage();
					for(int i=0;i<list.size();i++)
					{
						//插入短信
						writeToDataBase(list.get(i));
					}
				}
				
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
        }
        
        private List<SmsContent> localRecoveryMessageData() {
        	
        	List<SmsContent> list = new ArrayList<SmsContent>();
			try {
				
				String data = readFile("message.txt");
	        	
				JSONObject jsonObject2 = new JSONObject(data);
	            JSONArray jsonArray = jsonObject2.getJSONArray("message");
	            
	            if(jsonArray != null && !"".equals(jsonArray)&& jsonArray.length() > 0) {
					for(int i=0; i<jsonArray.length(); i++) {
						SmsContent smsContent=new SmsContent();
						JSONObject aObj = jsonArray.getJSONObject(i);
						smsContent.setId(Long.valueOf(aObj.getString("message_id")));
						smsContent.setSms_number((aObj.getString("message_number")));
						smsContent.setDate(aObj.getLong("message_time"));
						smsContent.setTypeId(Integer.valueOf(aObj.getString("message_type")));
						smsContent.setSms_body((aObj.getString("message_content")));
						list.add(smsContent);
					}
				}
	            
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return list;
        }
        
        /*
         * 恢复通话记录信息
         */
        private void localRecoveryCalllog(List<CallLogInfo> list) {
			try {
			
				if(list!=null && list.size()>0)
				{
					deleteCallRecord();
					for(int i=0;i<list.size();i++)
					{
						insertCallLog(list.get(i));
					}
				}
			
			} catch(Exception ex) {
				ex.printStackTrace();
			}
        }
        
        private List<CallLogInfo> localRecoveryCalllogData() {
        	List<CallLogInfo> list = new ArrayList<CallLogInfo>();
			try {
				String data = readFile("calllog.txt");
	        	
				JSONObject jsonObject2 = new JSONObject(data);
	            JSONArray jsonArray = jsonObject2.getJSONArray("callrecord");
	            
	            if(jsonArray != null && jsonArray.length() > 0) {
					for(int i=0; i<jsonArray.length(); i++) {
						CallLogInfo callLogInfo=new CallLogInfo();
						JSONObject aObj = jsonArray.getJSONObject(i);
						callLogInfo.setId(Long.valueOf(aObj.getString("call_record_id")));
						callLogInfo.setmCaller_name((aObj.getString("call_record_name")));
						callLogInfo.setmCaller_number((aObj.getString("call_record_number")));
						callLogInfo.setmCall_type((aObj.getString("call_record_type")));
						callLogInfo.setLong_date((aObj.getLong("call_record_time")));
						callLogInfo.setmCall_duration(aObj.getString("call_record_duration"));
						list.add(callLogInfo);
					}
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return list;
        }
        
        /*
         * 恢复提醒信息
         */
        private void localRecoveryRemind(List<RemindBean> list) {
			try {
				if(list!=null && list.size()>0)
				{
					myDatabaseUtil.deleteAllRemind();
					for(int i=0;i<list.size();i++)
					{
						RemindBean remindBean=list.get(i);
						//插入短信收藏
						myDatabaseUtil.insertRemind(remindBean.getContent(), remindBean.getContacts(), remindBean.getParticipants(), remindBean.getStart_time(), remindBean.getEnd_time(), remindBean.getRemind_type(), remindBean.getRemind_num(), remindBean.getRemind_time(), remindBean.getRepeat_type(), remindBean.getRepeat_fre(), remindBean.getRepeat_condition(), remindBean.getRepeat_start_time(), remindBean.getRepeat_end_time());
					}
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
        }
        
        private List<RemindBean> localRecoveryRemindData() {
        	List<RemindBean> list = new ArrayList<RemindBean>();
			try {
				
				String data = readFile("remind.txt");
	        	
				JSONObject jsonObject2 = new JSONObject(data);
	            JSONArray jsonArray = jsonObject2.getJSONArray("remind");
	            
	            if(jsonArray != null && jsonArray.length() > 0) {
					for(int i=0; i<jsonArray.length(); i++) {
						RemindBean remindBean=new RemindBean();
						JSONObject aObj = jsonArray.getJSONObject(i);
						remindBean.setId(Integer.valueOf(aObj.getString("remind_id")));
						remindBean.setContent(aObj.getString("remind_content"));
						remindBean.setContacts(aObj.getString("remind_contact"));
						remindBean.setParticipants(aObj.getString("remind_participants"));
						remindBean.setStart_time(Long.valueOf(aObj.getString("remind_start_time")));
						remindBean.setEnd_time(Long.valueOf(aObj.getString("remind_end_time")));
						remindBean.setRemind_type(Integer.valueOf(aObj.getString("remind_remind_type")));
						remindBean.setRemind_num(Integer.valueOf(aObj.getString("remind_remind_num")));
						remindBean.setRemind_time(Integer.valueOf(aObj.getString("remind_remind_time")));
						remindBean.setRepeat_type(Integer.valueOf(aObj.getString("remind_repeat_type")));
						remindBean.setRepeat_fre(Integer.valueOf(aObj.getString("remind_repeat_fre")));
						remindBean.setRepeat_start_time(Long.valueOf(aObj.getString("remind_repeat_start_time")));
						remindBean.setRepeat_end_time(Long.valueOf(aObj.getString("remind_repeat_end_time")));
						remindBean.setCount(aObj.getString("remind_repeat_count"));
						list.add(remindBean);
					}
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return list;
        }
        
        /*
         * 恢复短信收藏箱信息
         */
        private void localRecoveryCollection(List<SmsContent> list) {
			try {
				if(list!=null && list.size()>0)
				{
					myDatabaseUtil.deleteFavorite(null);
					for(int i=0;i<list.size();i++)
					{
						SmsContent smsContent=list.get(i);
						myDatabaseUtil.insertDataFavorite(String.valueOf(smsContent.getThread_id()), String.valueOf(smsContent.getId()), smsContent.getSms_body(), smsContent.getDate(), smsContent.getSend_type().equals("1") ?smsContent.getSms_name():"我", smsContent.getSms_number().replace("(", "").replace(") ", "").replace("-", "").replace(" ", "").replace("+86", "").replace("17951", ""));
					}
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
        }
        
        private List<SmsContent> localRecoveryCollectionData() {
        	
        	List<SmsContent> list = new ArrayList<SmsContent>();
			try {
				String data = readFile("collect.txt");
				
				JSONObject jsonObject2 = new JSONObject(data);
	            JSONArray jsonArray = jsonObject2.getJSONArray("favorite");
	            
	            if(jsonArray != null && jsonArray.length() > 0) {
					for(int i=0; i<jsonArray.length(); i++) {
						SmsContent smsContent=new SmsContent();
						JSONObject aObj = jsonArray.getJSONObject(i);
						smsContent.setThread_id(Long.valueOf(aObj.getString("message_thread_id")));
						smsContent.setId(Long.valueOf((aObj.getString("message_content_id"))));
						smsContent.setSms_body((aObj.getString("message_body")));
						smsContent.setSend_type((aObj.getString("message_date")));
						smsContent.setSms_number((aObj.getString("message_number")));
						list.add(smsContent);
					}
				}
			
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return list;
        }
        
	    private String readFile(String fileName){
	    	try {
	    		
	    		String filePath = Environment.getExternalStorageDirectory() + "/DJ_AddressBook/" + fileName;
	    		
		        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
		        FileInputStream inputStream = new FileInputStream(filePath);
		        int len = 0;
		        byte[] buffer = new byte[1024];
		        
		        while((len = inputStream.read(buffer)) != -1){
		                outputStream.write(buffer, 0, len);
		        }
		        
		        outputStream.close();
		        inputStream.close();
		        
		        byte[] data = outputStream.toByteArray();
		        
		        return new String(data);
		        
	    	} catch (IOException ex) {
	    		ex.printStackTrace();
	    		return null;
	    	}
	        
	    }


	 /**
	  * 恢复成功后 重新激活所有提醒
	  */
	 void activateAllReminds()
	 {
		 Cursor cursor = DButil.getInstance(mainActivity).queryAllRemind();
		 
		 while (cursor.moveToNext()) {
			 
			 int remind_id = cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_ID));
			 long start_time = cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REMIND_START));
			 int remind_type = cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_TYPE));
			 int remind_num = cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REMIND_NUM));
			 int repeat_type = cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE));
			 String repeat_condition = cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION));
			 int repeat_freq = cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ));
			 long repeat_start_time = cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME));
			 long repeat_end_time = cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME));
			 String time_filter = cursor.getString(cursor.getColumnIndex(MyDatabaseUtil.TIME_FILTER));
			 
			  long next_time = TimeTool.getNextTime(start_time, remind_type, remind_num, repeat_type, repeat_condition,repeat_freq,repeat_start_time, repeat_end_time,time_filter);
		       
				//保留
		        if(next_time!=-1)
		        {
		        	Intent it = new Intent(mainActivity, AlarmReceiver.class);
		    		it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);		
		    		PendingIntent pit = PendingIntent.getBroadcast(mainActivity, (int)remind_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
		    		AlarmManager amr = (AlarmManager) mainActivity.getSystemService(Activity.ALARM_SERVICE);
//		    		amr.cancel(pit);//先取消 ？
		    		amr.set(AlarmManager.RTC_WAKEUP, next_time ,pit);
		    		
		        }else{
		        	//没有下一次的提醒了
		        	Intent it = new Intent(mainActivity, AlarmReceiver.class);
		    		it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);		
		    		PendingIntent pit = PendingIntent.getBroadcast(mainActivity, (int)remind_id, it, PendingIntent.FLAG_UPDATE_CURRENT);
		    		AlarmManager amr = (AlarmManager) mainActivity.getSystemService(Activity.ALARM_SERVICE);
		    		amr.cancel(pit);//取消
		        }
		        
		 }
		 cursor.close();
		 
	 }
	 
	//增加联系人
		public void addContact(ContactBean contactBean)
		{
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			
			/***************添加联系人***************************/
			ContentResolver cr = mainActivity.getContentResolver();
			ContentValues  values=new ContentValues();
			Uri  rawcontacturi=cr.insert(RawContacts.CONTENT_URI, values);
			long  rawcontactid=ContentUris.parseId(rawcontacturi);
//			}
			
			//联系人姓名
		    if(!"".equals(contactBean.getNick() )&& null !=contactBean.getNick()){
					
					if(contactBean.getNick().contains(onlys))  //机主
					{
						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			//			builder.withValueBackReference(Data.RAW_CONTACT_ID, (int)backRef); 
						builder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
						builder.withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
						builder.withValue(StructuredName.DISPLAY_NAME,contactBean.getNick().replace(onlys, ""));
						builder.withYieldAllowed(true);
						ops.add(builder.build());
						
						ContentProviderOperation.Builder builder1 = ContentProviderOperation.newInsert(Data.CONTENT_URI);
						builder1.withValue(Data.RAW_CONTACT_ID, rawcontactid);
						builder1.withValue(Data.MIMETYPE, MainActivity.MY_CONTACT_MIME_TYPE);
						builder1.withValue("data2", "我是机主");
						
						ops.add(builder1.build());
						
						Cursor contactIdCursor = mainActivity.getContentResolver().query(RawContacts.CONTENT_URI,new String[] { RawContacts.CONTACT_ID },RawContacts._ID + "=" + rawcontactid, null,null);

						if (contactIdCursor != null&& contactIdCursor.moveToFirst()) {
							long contactId = contactIdCursor.getLong(0);
							SharedPreferences ss = mainActivity.getSharedPreferences("myNumberContactId", 0);
							Editor ed = ss.edit();
							ed.putLong("myContactId", contactId);
							ed.commit();

							mainActivity.MY_CONTACT_ID = contactId;
						}
						
					}else{ //普通联系人
						
						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
			//			builder.withValueBackReference(Data.RAW_CONTACT_ID, (int)backRef); 
						builder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
						builder.withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
						builder.withValue(StructuredName.FAMILY_NAME, "");
						builder.withValue(StructuredName.GIVEN_NAME, contactBean.getNick());
						builder.withValue(StructuredName.MIDDLE_NAME, "");
//						builder.withValue(StructuredName.DISPLAY_NAME,contactBean.getNick());
						builder.withYieldAllowed(true);
						ops.add(builder.build());
					}
				}
			
			//头像
			if (!contactBean.getPhoto().equals("") && contactBean.getPhoto() != null) {
				ContentProviderOperation.Builder b_photo = ContentProviderOperation.newInsert(Data.CONTENT_URI);
				b_photo.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
				b_photo.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
				b_photo.withValue(Photo.IS_SUPER_PRIMARY, 1);
				photoContentValues.put(Photo.PHOTO, contactBean.getPhoto());
				b_photo.withValues(photoContentValues);
				b_photo.withYieldAllowed(true);
				ops.add(b_photo.build());
			}
			//组织
			if (!contactBean.getOrganizations().equals("") && contactBean.getOrganizations() != null) {
		    	ContentProviderOperation.Builder b_company = ContentProviderOperation.newInsert(Data.CONTENT_URI);
		    	b_company.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
				b_company.withValue(Data.MIMETYPE,  Organization.CONTENT_ITEM_TYPE);
				b_company.withValue(Organization.COMPANY,contactBean.getOrganizations());
				b_company.withYieldAllowed(true);
				ops.add(b_company.build());
			}
		     //添加职业信息
			if (!contactBean.getJob().equals("") && contactBean.getJob() != null) {
				ContentProviderOperation.Builder b_job = ContentProviderOperation.newInsert(Data.CONTENT_URI);
				b_job.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
				b_job.withValue(Data.MIMETYPE,  Organization.CONTENT_ITEM_TYPE);
				b_job.withValue(Organization.JOB_DESCRIPTION,contactBean.getJob());
				b_job.withYieldAllowed(true);
				ops.add(b_job.build());
			}
			 //生日
			if (!contactBean.getBrithday().equals("") && contactBean.getBrithday() != null) {
				ContentProviderOperation.Builder bb = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
				bb.withValue(Data.RAW_CONTACT_ID,rawcontactid);
				ContentValues cs = new ContentValues();
				cs.put(ContactsContract.CommonDataKinds.Event.START_DATE,contactBean.getBrithday());
				bb.withValues(cs);
				bb.withYieldAllowed(true);
				ops.add(bb.build());
			}
			 //电话号码
			for (int n = 0; n < contactBean.getPhoneList().size(); n++) {
				ContactBean.PhoneInfo phoneInfo = contactBean.getPhoneList().get(n);
				
				if (phoneInfo != null) {
					ContentProviderOperation.Builder numberss = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
					numberss.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
					ContentValues	cvs = new ContentValues();
					cvs.put(Phone.TYPE, phoneInfo.type);
					cvs.put(Phone.NUMBER, phoneInfo.number);
					numberss.withValues(cvs);
					numberss.withYieldAllowed(true);
					ops.add(numberss.build());
				}
			}
			
			//email
			for (int e = 0; e < contactBean.getEmailList().size(); e++) {
				ContactBean.EmailInfo emailInfo = contactBean.getEmailList().get(e);
				if (emailInfo != null) {
					ContentProviderOperation.Builder contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
					contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
					ContentValues cv = new ContentValues();
			 		cv.put(Email.DATA,emailInfo.email);
			 		cv.put(Email.TYPE,Email.TYPE_WORK);
					contentbuilder.withValues(cv);
					contentbuilder.withYieldAllowed(true);
					ops.add(contentbuilder.build());
				}
			}
				//地址
			for (int a = 0; a < contactBean.getAddressList().size(); a++) {
				String street = contactBean.getAddressList().get(a);
				if (!street.equals("") && street != null){
					ContentProviderOperation.Builder contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
					contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
					ContentValues cv = new ContentValues();
			 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET,street);
			 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
					contentbuilder.withValues(cv);
					contentbuilder.withYieldAllowed(true);
					ops.add(contentbuilder.build());
				}
			}
				//网站
			for (int w = 0; w < contactBean.getWebSiteList().size(); w++) {
				String url = contactBean.getWebSiteList().get(w);
				if (!url.equals("") && url != null) {
					ContentProviderOperation.Builder contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Website.CONTENT_ITEM_TYPE);
					contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
					ContentValues cv = new ContentValues();
					cv.put(Website.URL, url);
					cv.put(Website.TYPE, Website.TYPE_WORK);
					contentbuilder.withValues(cv);
					contentbuilder.withYieldAllowed(true);
					ops.add(contentbuilder.build());
				}
			}
				//备注
			for (int n = 0; n < contactBean.getNoteList().size(); n++) {
				String note = contactBean.getNoteList().get(n);
				if (!note.equals("") && note != null) {
					ContentProviderOperation.Builder contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Note.CONTENT_ITEM_TYPE);
					contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
					ContentValues cv = new ContentValues();
					cv.put(Note.NOTE, note);
					contentbuilder.withValues(cv);
					contentbuilder.withYieldAllowed(true);
					ops.add(contentbuilder.build());
				}
			}
			try {
				mainActivity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		void checkLord() {

			SharedPreferences ss = mainActivity.getSharedPreferences("myNumberContactId", 0);
			long contactId = ss.getLong("myContactId", -1);

			// 不存在机主（第一次安装 或 数据已被清空）
			if (contactId == -1) {
				Cursor myCursor = mainActivity.getContentResolver().query(Data.CONTENT_URI,
						new String[] { Data._ID, Data.CONTACT_ID },
						Data.MIMETYPE + "='" + mainActivity.MY_CONTACT_MIME_TYPE + "'", null,
						null);

				if (myCursor.moveToNext()) {
					contactId = myCursor.getLong(myCursor
							.getColumnIndex(Data.CONTACT_ID));
				}

				myCursor.close();

				if (contactId != -1) // 保存起来
				{
					Editor editor = ss.edit();
					editor.putLong("myContactId", contactId);
					editor.commit();
				}

			}

			Cursor c = mainActivity.getContentResolver().query(RawContacts.CONTENT_URI, null,RawContacts.CONTACT_ID + " = " + contactId, null, null);
			int size = c.getCount();

			if (size > 0) {
				c.moveToNext();
				String nn = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				System.out.println("  DISPLAY_NAME  --->" + nn);
			}

			c.close();

			System.out.println("  size   --->" + size);

			// 不存在本机联系人id，则创建机主联系人
			if (contactId == -1 || size == 0) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

						ContentValues values = new ContentValues();
						Uri rawcontacturi = mainActivity.getContentResolver().insert(RawContacts.CONTENT_URI, values);

						long rawcontactid = ContentUris.parseId(rawcontacturi);

						// 联系人姓名
						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);

						// builder.withValueBackReference(Data.RAW_CONTACT_ID,
						// (int)backRef);
						builder.withValue(Data.RAW_CONTACT_ID, rawcontactid);
						builder.withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
						builder.withValue(StructuredName.DISPLAY_NAME, "机主");
						builder.withYieldAllowed(true);

						// builder.withValueBackReference(Data.RAW_CONTACT_ID,
						// (int)backRef);
						ContentProviderOperation.Builder builder1 = ContentProviderOperation
								.newInsert(Data.CONTENT_URI);
						builder1.withValue(Data.RAW_CONTACT_ID, rawcontactid);
						builder1.withValue(Data.MIMETYPE, mainActivity.MY_CONTACT_MIME_TYPE);
						builder1.withValue("data2", "我是机主");
						// builder1.withYieldAllowed(true);

						ops.add(builder.build());
						ops.add(builder1.build());

						try {
							mainActivity.getContentResolver().applyBatch(
									ContactsContract.AUTHORITY, ops);
							// Toast.makeText(MainActivity.this, "机主联系人信息已创建",
							// Toast.LENGTH_SHORT).show();

							Cursor contactIdCursor = mainActivity.getContentResolver().query(
									RawContacts.CONTENT_URI,
									new String[] { RawContacts.CONTACT_ID },
									RawContacts._ID + "=" + rawcontactid, null,
									null);

							if (contactIdCursor != null
									&& contactIdCursor.moveToFirst()) {
								long contactId = contactIdCursor.getLong(0);
								SharedPreferences ss = mainActivity.getSharedPreferences(
										"myNumberContactId", 0);
								Editor ed = ss.edit();
								ed.putLong("myContactId", contactId);
								ed.commit();

								mainActivity.MY_CONTACT_ID = contactId;
								MY_CONTACT_ID = contactId;
							}

							contactIdCursor.close();

							System.out.println(" rawcontactid --->" + rawcontactid);
							System.out.println(" 机主联系人信息已创建  ---");

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			} else {

				mainActivity.MY_CONTACT_ID = contactId;
				MY_CONTACT_ID = contactId;
			}
		}
}
