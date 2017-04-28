package com.dongji.app.addressbook;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.entity.NetWorkResult;
import com.dongji.app.net.NetworkUnit;
import com.dongji.app.service.UpdateVersionService;
import com.dongji.app.tool.AndroidUtils;
import com.umeng.fb.UMFeedbackService;

/**
 * 
 * 九宫格 设置界面
 * 
 * @author Administrator
 *
 */
public class SettingLayout implements OnClickListener {
	
	MainActivity mainActivity;
	
	public View view;
	
	LinearLayout setting_item_1; //  备份/恢复 
	SystemBackUp systemBackUp;
	
	
//	LinearLayout setting_item_2; // 通话录音
//	View view_2;
	
	
	LinearLayout setting_item_3; // 记事本
	NoteBookActivity noteBookLayout;
	View view_3;
	
	
	LinearLayout setting_item_4; // 拦截设置
	View view_4;
	
	
	/*LinearLayout setting_item_5; // 未接留言
	MissedMessageLayout missedMessageLayout;
	View view_5;*/
	
	
	LinearLayout setting_item_6; // 加密内容
	View view_6;
	
	
	LinearLayout setting_item_7; // 拨号设置
	View view_7;
	
	
	LinearLayout setting_item_8; // 短信设置
	View view_8;
	
	LinearLayout setting_item_9; // 系统设置
	View view_9;
	
	LinearLayout setting_item_10; // 短信收藏箱
	View view_10;
	
	LinearLayout setting_item_11; // 提醒
	int change = 0;
	
	LinearLayout setting_item_13; // 意见发聩
	
	
	LinearLayout setting_item_12;
	ImageView open;
	TextView update_time;
	TextView update_version;
	Button exit;
	Button check_version;
	Dialog dialog;
	
	int cur_layout;
	String update_url;
	String version_info = "1.0";
	String version_date = "2012-12-28";
	public static String feedURL = "http://bbs.91dongji.com/forum-57-1.html";
	
	private MyHandler myHandler = new MyHandler();
	public SettingLayout(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;
		
		view = mainActivity.findViewById(R.id.layout_setting);
		
		setting_item_1 = (LinearLayout)view.findViewById(R.id.setting_item_1);
		setting_item_1.setOnClickListener(this);
//		setting_item_2 = (LinearLayout)view.findViewById(R.id.setting_item_2);
//		setting_item_2.setOnClickListener(this);
		
		setting_item_3 = (LinearLayout)view.findViewById(R.id.setting_item_3);
		setting_item_3.setOnClickListener(this);
		
		setting_item_4 = (LinearLayout)view.findViewById(R.id.setting_item_4);
		setting_item_4.setOnClickListener(this);
		
//		setting_item_5 = (LinearLayout)view.findViewById(R.id.setting_item_5);
//		setting_item_5.setOnClickListener(this);
		
		setting_item_6 = (LinearLayout)view.findViewById(R.id.setting_item_6);
		setting_item_6.setOnClickListener(this);
		
		setting_item_7 = (LinearLayout)view.findViewById(R.id.setting_item_7);
		setting_item_7.setOnClickListener(this);
	
		setting_item_8 = (LinearLayout)view.findViewById(R.id.setting_item_8);
		setting_item_8.setOnClickListener(this);
		
		setting_item_9 = (LinearLayout)view.findViewById(R.id.setting_item_9);
		setting_item_9.setOnClickListener(this);
		
		setting_item_10 = (LinearLayout)view.findViewById(R.id.setting_item_10);
		setting_item_10.setOnClickListener(this);
		
		setting_item_11 = (LinearLayout)view.findViewById(R.id.setting_item_11);
		setting_item_11.setOnClickListener(this);
		
		setting_item_12 = (LinearLayout)view.findViewById(R.id.setting_item_12);
		setting_item_12.setOnClickListener(new AboutUsOnClickListener());
		
		setting_item_13=(LinearLayout)view.findViewById(R.id.setting_item_13);
		setting_item_13.setOnClickListener(this);
		
//		CookieSyncManager csm = CookieSyncManager.createInstance(mainActivity);
//		CookieManager cookieManager = CookieManager.getInstance();
//		csm.sync();
//		cookieManager.removeAllCookie();
	}
	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		if(v.getId()==R.id.setting_item_13) {
			UMFeedbackService.openUmengFeedbackSDK(mainActivity); // 意见反馈
			return;
		}

		switch (v.getId()) {
//		case R.id.setting_item_1:
//			cur_layout = R.id.setting_item_1;
//			mainActivity.thrid_layout.removeAllViews();
//			mainActivity.top_third.removeAllViews();
//			
//			if(systemBackUp==null)
//			{
//				systemBackUp=new SystemBackUp(mainActivity);
//			}
//			
//			mainActivity.thrid_layout.addView(systemBackUp.backupView);
//			
//			view = LayoutInflater.from(mainActivity).inflate(R.layout.item_top, null);
//			
//			img_top = (ImageView)view.findViewById(R.id.img_top);
//			tv_top = (TextView)view.findViewById(R.id.tv_top);
//			tv_top.setText("备份/恢复");
//			
//			mainActivity.top_third.addView(view);
//			
//			break;
//
	   case R.id.setting_item_3:
		   
		   cur_layout = R.id.setting_item_3; 
			
		   intent = new Intent(mainActivity, NoteBookActivity.class);
		   mainActivity.startActivity(intent);
		   
		   break;
		   
		   
	     case R.id.setting_item_4:
			
		   cur_layout = R.id.setting_item_4;
		   
		   intent = new Intent(mainActivity, InterceptSettingActivity.class);
		   mainActivity.startActivity(intent);
			
		   break;
			
	   case R.id.setting_item_6:
			
		   cur_layout = R.id.setting_item_6;
		   
		   intent = new Intent(mainActivity, EncryptionActivity.class);
		   mainActivity.startActivity(intent);
			
		   break;
			
			
	   case R.id.setting_item_7:
			
		   cur_layout = R.id.setting_item_7;
		   
		   intent = new Intent(mainActivity, DialingSettingActivity.class);
		   mainActivity.startActivity(intent);
		   
		   break;
				
	   case R.id.setting_item_8:
			
		   cur_layout = R.id.setting_item_8;
		   
		   intent = new Intent(mainActivity, SmsSettingActivity.class);
		   mainActivity.startActivity(intent);
			
			break;
			
	   case R.id.setting_item_9:
			
		   cur_layout = R.id.setting_item_9;
		   
		   intent = new Intent(mainActivity, SystemSettingActivity.class);
		   mainActivity.startActivity(intent);
		   
		   
			break;
	   case R.id.setting_item_10:
		   
		   cur_layout = R.id.setting_item_10;

		   intent = new Intent(mainActivity, SmsCollectActivity.class);
		   mainActivity.startActivity(intent);
		   
			break;
		
	   case R.id.setting_item_11:
		   
		    cur_layout = R.id.setting_item_11;

		    intent = new Intent(mainActivity, RemindsActivity.class);
		    mainActivity.startActivity(intent);
		    
			break;
		   
		default:
			break;
		}	
		
	}
	
	class AboutUsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			
			   cur_layout = R.id.setting_item_12;

			   dialog = new Dialog(mainActivity,R.style.theme_myDialog_activity);
			   dialog.setContentView(R.layout.aboutus);
			   dialog.setCanceledOnTouchOutside(true);
			   dialog.show();
			   
			   open = (ImageView) dialog.findViewById(R.id.open);
				
				update_time = (TextView) dialog.findViewById(R.id.update_time);
				update_version = (TextView) dialog.findViewById(R.id.update_version);
				
				check_version = (Button) dialog.findViewById(R.id.check_version);
				exit = (Button) dialog.findViewById(R.id.exit);
				
				String packageName = mainActivity.getPackageName();
				PackageManager pm = mainActivity.getPackageManager();
				PackageInfo packageInfo;
//					http://192.168.1.200/cms/index.php?g=api&m=Message&a=Opt
					try {
						packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
						update_version.setText("版本信息:V"+packageInfo.versionName);
					} catch (NameNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				open.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
//						   Uri uri = Uri.parse("http://bbs.91dongji.com/forum-57-1.html");
//						   Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//						   mainActivity.startActivity(intent);
						   
//						   final Handler mHandler = new Handler(); 
//							WebSettings webSettings = open.getSettings();             
//							webSettings.setJavaScriptEnabled(true);            
//							open.addJavascriptInterface(new Object() {                 
//							   public void clickOnAndroid() {                     
//								   mHandler.post(new Runnable() {                         
//									   public void run() {                             
//										   open.loadUrl("javascript:wave()");                         
//										   }                     
//									   });                 
//								   }             
//							   }, "demo");             
//							open.loadUrl("http://bbs.91dongji.com/forum-57-1.html");
						
						
						/*Intent it = new Intent(Intent.ACTION_VIEW,
								Uri.parse(feedURL));
						it.setClassName("com.android.browser",
								"com.android.browser.BrowserActivity");
						mainActivity.startActivity(it);*/
						  Intent intent= new Intent();        
						    intent.setAction("android.intent.action.VIEW");    
						    Uri content_url = Uri.parse(feedURL);   
						    intent.setData(content_url);  
						    mainActivity.startActivity(intent);
					}
				});
				
				check_version.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//版本更新
						
						Toast.makeText(mainActivity, "正在检测最新版本",Toast.LENGTH_SHORT).show();
						
						try {
							if (AndroidUtils.isNetworkAvailable(mainActivity))
							{
								/*ArrayList<String> strings = AndroidUtils
										.checkAppUpdate(mainActivity);
								if (!(strings.size() == 0)) {
									String download_url = strings.get(0);
									if (download_url != null
											&& !"".equals(download_url)) {
										update_url = download_url;
										version_info = strings.get(1);
										version_date = strings.get(2);
										myHandler.sendEmptyMessage(6);
									}
								} else {
									myHandler.sendEmptyMessage(9);
								}*/
								
                            NetworkUnit networkUnit=new NetworkUnit();
								
								String packageName = mainActivity.getPackageName();
								PackageManager pm = mainActivity.getPackageManager();
								PackageInfo packageInfo;
//									http://192.168.1.200/cms/index.php?g=api&m=Message&a=Opt
									packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
									int versionCode = packageInfo.versionCode == 0 ? 1 : packageInfo.versionCode;
								
									NetWorkResult net=	networkUnit.updateVersion(packageName, String.valueOf(versionCode));
									
											if(net!=null)
											{
												String success=net.getSuccess();
												if("1".equals(success))
												{
													String versionInfo=net.getVersion_number();
													String sendDate=net.getSend_date();
												    update_url=	net.getDownload_url();
												    if(update_url!=null)
												    {
												    	myHandler.sendEmptyMessage(7);
												    }
												}else
												{
													myHandler.sendEmptyMessage(9);
												}
											}
							}else {

								myHandler.sendEmptyMessage(10);
							}
							

						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
					}
				});
				
				exit.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				
		}
		
	}
	
	class RemindTopClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			
		}
	}
	
	public void downloadUpdatedApp() {
		if (AndroidUtils.isSdcardExists()) // sd卡是否可用
		{
			Intent updateIntent = new Intent(mainActivity,UpdateVersionService.class);
					
			updateIntent.putExtra("update_url", update_url);

			mainActivity.startService(updateIntent);
		} else {
			Toast.makeText(mainActivity, "sd card faild", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 6: //
					if (!UpdateVersionService.IS_DOWNLOAD) {
						Dialog dialog = new AlertDialog.Builder(mainActivity)
								.setIcon(android.R.drawable.ic_dialog_info)
								.setTitle("发现新版本")
								.setPositiveButton("马上更新",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												downloadUpdatedApp();
											}
										}).create();

						dialog.show();
					} else {
						Toast.makeText(mainActivity, "动机通讯录正在后台下载，请稍后",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case 7: // 如果有版本更新
					if (!UpdateVersionService.IS_DOWNLOAD) {
						downloadUpdatedApp();
					} else {
						Toast.makeText(mainActivity, "动机通讯录正在后台下载，请稍后",
								Toast.LENGTH_SHORT).show();
					}
					break;

				case 9: // 没有版本更新
					Toast.makeText(mainActivity, "已是最新版本", Toast.LENGTH_LONG)
							.show();
					break;
				case 10: // 没有版本更新
					Toast.makeText(mainActivity, "网络异常，请检查网络",
							Toast.LENGTH_LONG).show();
					break;
				case 11: // 版本更新出现问题
					/*
					 * if (updateDialog.isShowing()) { updateDialog.dismiss(); }
					 * Toast.makeText(Launcher.this, "下载失败，请重试",
					 * Toast.LENGTH_LONG).show(); break;
					 */
				default:
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
