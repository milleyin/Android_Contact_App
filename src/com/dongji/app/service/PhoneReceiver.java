package com.dongji.app.service;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.dongji.app.addressbook.CallAutoDialingActivity;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MissedMessageDBHelper;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;

/** 
 * 
 *  电话拦截监听
 * @author Administrator
 *
 */
public class PhoneReceiver extends BroadcastReceiver {

	Context context;
	
	SharedPreferences interceptmode = null;
	
	SQLiteDatabase db = null;
	
	SharedPreferences dialingVibrator = null;
	
	SharedPreferences sf = null;
	
	SharedPreferences autoNum_sf = null;
	Editor ed =  null;
	SharedPreferences incomingNum_sf = null;
	
	
	boolean isOpen = false;
	boolean answer = true;
	boolean hangup = false;
	boolean ip_setting = true;
	boolean auto_dialing = false;
	boolean flag = true;
	boolean stopHandle = false;
	boolean isRecorder = false;
	boolean isStartPlay = false;
	
	private  AudioManager mAudioManager; 
	private  ITelephony mITelephony;  
	private TelephonyManager manager;
	private MediaRecorder mediaRecorder = null;
    private File file;
    
    String localPhone = "";
	String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
	String calllog_number = "";
	
	int ringNum = 0; //为响铃一声计时
	int mode = 0;
	int tips = 0;
	
	long outGoingTime;
	long idelTime;
	
	
	SharedPreferences oneCall_sf = null;
	Editor oneCall_editor = null;
	
	
	
	/*************************** kevin  **********/
	private static final int EVENT_AUTO_ANSWER = 10;   // 自动接听
	private static final int EVENT_AUTO_CALL = 11; // 自动重拨
	private static final long AUTO_ANSWER_TIME = 24000L;
	private static boolean isListenPhone; // 是否监听电话状态
	
	
	Notification notification = null;
	AudioManager volMgr = null;
	
	boolean isInterceptNumber = false;
	
	MyDatabaseUtil myDatabaseUtil;
	
	 Handler handler = new Handler() { 
		 
	        @Override 
	        public void handleMessage(Message msg) { 
	            super.handleMessage(msg); 
	 
	            Log.d("debug", "handleMessage方法所在的线程："+ Thread.currentThread().getName()); 
	            
	            switch (msg.arg1) {
				case 1:
					if (!isRecorder){
					
						stopHandle = true;
						recordCalling();
					}
						
					break;
				case 2:
		            if (msg.what > 0) { 
		            	isStartPlay = false;
		            } else { 
		                flag = false;
						isStartPlay = true;
						answerRingingCall();
		            } 
					break;
				case 3:
				
					break;
					
				case 4:
					
					
					break; 
				
					
				default:
					break;
				}
	        } 
	    }; 
	    
	private Handler mHandler=new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what) {
            case EVENT_AUTO_ANSWER:
				flag = false;
				isStartPlay = true;
				startCall();
				break;
            case EVENT_AUTO_CALL:
            	SharedPreferences sf_time = context.getSharedPreferences("outgoing", 0);
				long outTime = sf_time.getLong("outgoingTime", 0);
				
				idelTime = System.currentTimeMillis();

				Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER,CallLog.Calls.DURATION,CallLog.Calls.TYPE},null, null, CallLog.Calls.DATE+" DESC limit 1"); // 
				
				long old = 0;
				int type = 0;
				
				if(c.moveToFirst()){
					
					calllog_number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
					old = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));
					type = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
					System.out.println("calllog_number:"+calllog_number+", "+old+", "+type);
					
				}
				
				c.close();
				
				
				int autoNum = autoNum_sf.getInt("autoNum", 0);
				
				if ( autoNum < 5 ) {
					
					if(type == CallLog.Calls.OUTGOING_TYPE){

						long hour = old / (60 * 60);
						long min = (old % (60 * 60)) / (60);
						long second = (old % (60));
						
						if(hour == 0 && min == 0 && second == 0){
							//|| ( idelTime - ( outTime + 3000 ) ) < 60000
//							System.out.println("( idelTime - ( outTime + 3000 ) ))  ----"+( idelTime - ( outTime + 3000 ) ));
							if ( ( idelTime - ( outTime + 3000 ) ) < 60000 ) {
								MainActivity.refreshCallLog();
								
								ed.putInt("autoNum", 0);
								ed.commit();
								
								return;
								
							} else {
								Intent intent = new Intent(context, CallAutoDialingActivity.class);
								
								Bundle bundle = new Bundle();
								bundle.putString("number", calllog_number);
								intent.putExtras(bundle);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
								
								context.startActivity(intent);
								
								ed.putInt("autoNum",  ++autoNum);
								ed.commit();
								
							}
						}
					}
				
				} else {
					
					ed.putInt("autoNum", 0);
					ed.commit();
					 
				}
            	break;
            }
		};
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		
		myDatabaseUtil = DButil.getInstance(context);
		
		interceptmode = context.getSharedPreferences("interceptmode", 0);
		mode = interceptmode.getInt("mode", 1);
		dialingVibrator = context.getSharedPreferences("com.dongji.app.addressbook.dialingsetting", 0);
		
		//未接留言开关
		sf = context.getSharedPreferences("missedmessage", 0);
		
		isOpen = sf.getBoolean("missedMessage", false);
		
		answer = dialingVibrator.getBoolean("answer_state_cb", true);
		
		hangup = dialingVibrator.getBoolean("hangup_state_cb", false);
		
		ip_setting = dialingVibrator.getBoolean("ip_setting_cb", false);
		
		auto_dialing = dialingVibrator.getBoolean("auto_dialing_cb", false);
		
		localPhone = dialingVibrator.getString("local_phone", "");
		
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		manager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		
		autoNum_sf = context.getSharedPreferences("autoNum_sf", 0);
		ed =  autoNum_sf.edit();
		
		incomingNum_sf = context.getSharedPreferences("incomingNum_sf", 0);

		oneCall_sf = context.getSharedPreferences("onecall_sf", 0);
		
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			// 如果是去电（拨出）
			if (auto_dialing) {
				outGoingTime = System.currentTimeMillis();
				
				SharedPreferences sf_time = context.getSharedPreferences("outgoing", 0);
				Editor editor = sf_time.edit();
				editor.putLong("outgoingTime", outGoingTime);
				editor.commit();
			}
			
			//智能IP拨号
			if(ip_setting){
				String myphone = localPhone.replace(" ", "").toString();
				String myphone_area = MainActivity.CheckNumberArea(myphone);
				String outgoingPhone = getResultData();
				String outgoingPhone_area = MainActivity.CheckNumberArea(outgoingPhone);
				
				System.out.println(" myphone_are ---- > " + myphone_area + "   outgoingphone_area ---- > " + outgoingPhone_area);
				
				if(myphone_area.equals("未知号码归属地") || outgoingPhone_area.equals("未知号码归属地"))
					return ;
				
				if(!myphone_area.substring(0, myphone_area.length()-2).equals(outgoingPhone_area.substring(0, outgoingPhone_area.length()-2))){
					
					if(outgoingPhone.contains("17951") || outgoingPhone.contains("17911") || outgoingPhone.contains("17909"))
						 return ;
					 
					setResultData(null); 
					
					 if(myphone_area.contains("移动"))
						 setResultData("17951"+ outgoingPhone.replace("+86", ""));
					 else if (myphone_area.contains("联通"))
						 setResultData("17911"+ outgoingPhone.replace("+86", ""));
					 else if (myphone_area.contains("电信"))
						 setResultData("17909"+ outgoingPhone.replace("+86", ""));
				}
			}
			
		} else {
			
			phoner();
			if(!isListenPhone) {
				manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
				isListenPhone=true;
			}
			
		}
	}

	PhoneStateListener listener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			System.out.println("state:"+state+", incomingNumber:"+incomingNumber);
			
			if (incomingNumber != null) {
				
				switch (state) {
				case TelephonyManager.CALL_STATE_OFFHOOK:

					//接听振动
					notification = new Notification();
					volMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
					
					//接听震动
					answer = dialingVibrator.getBoolean("answer_state_cb", true);
					if(answer){
						Settings.System.putInt(context.getContentResolver(),Settings.System.VIBRATE_ON,false ? 1 : 0);
						Vibrator offhook_vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE); 
						offhook_vib.vibrate(200); 
					}  else {
						Settings.System.putInt(context.getContentResolver(),Settings.System.VIBRATE_ON,true ? 0 : 1);
					}
						
					//未接留言（来电）
					String number = incomingNum_sf.getString("incomingnum", "");
					if(isOpen){
		                if(!number.equals("")){
		                	//播放提示音
							if(isStartPlay){
								startPlaying();
							} else {
								flag = false;
							}
		                }
					}
						
					break;
				case TelephonyManager.CALL_STATE_IDLE:
						
						//自动重拨功能
						
						if (TextUtils.isEmpty(incomingNumber)) { //如果电话号码为空，表示拨出电话
						
							if (auto_dialing) {
								mHandler.sendEmptyMessageDelayed(EVENT_AUTO_CALL, 1500L);
							}
						}
						
						//挂断震动
						hangup = dialingVibrator.getBoolean("hangup_state_cb", false);
						if(hangup) {
							
							Settings.System.putInt(context.getContentResolver(),Settings.System.VIBRATE_ON,false ? 1 : 0);
							Vibrator offhook_vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE); 
							offhook_vib.vibrate(200); 
						} else {
							
							Settings.System.putInt(context.getContentResolver(),Settings.System.VIBRATE_ON,true ? 0 : 1);
							
						}
						
						//未接留言
						if(isOpen){
							
							flag = false;
							
							if (isRecorder) {
								
								stopRecord();
								
								if (!isRecorder){
									
									if(!incomingNum_sf.getString("incomingnum", "").equals("")){
										
					                    MissedMessageDBHelper missedMessageDBHelper = new MissedMessageDBHelper(context);
					                    SQLiteDatabase db = missedMessageDBHelper.getWritableDatabase();
					                    
					                    ContentValues values = new ContentValues();
					                    
					                    values.put(missedMessageDBHelper.NUMBER, incomingNum_sf.getString("incomingnum", ""));
					                    values.put(missedMessageDBHelper.DIR, file.getAbsolutePath());
					                    
					                    SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
					                    Date date = new Date();
					                    
					                    values.put(missedMessageDBHelper.DATE_TIME, sf.format(date));
					                    
					                    db.insert(missedMessageDBHelper.table, missedMessageDBHelper._ID, values);
					                    
					                    db.close();
					                    
					                    Editor editor1 = incomingNum_sf.edit();
					                    editor1.putString("incomingnum", "");
					                    editor1.commit();
					                    
									}
								}
								
								
							}else {
								stopRecord();
							}
						}
						
						//响铃一声
						
						if ( !incomingNumber.equals("")) {
						
							SharedPreferences ringtime_sf = context.getSharedPreferences("ringtime_sf", 0);
							long ringTime = ringtime_sf.getLong("ringtime", 0);
							
							long currentTiem = System.currentTimeMillis();
//							System.out.println(" 时长  --------------- " + (currentTiem - ringTime));
							if((currentTiem - ringTime) < 3000){
								
//								System.out.println(" 响铃一声  ----- ");
								saveRingOneCall(PhoneNumberTool.cleanse(incomingNumber));
							}
						}
						
						Cursor coursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls._ID,CallLog.Calls.TYPE},null, null, CallLog.Calls.DATE +" DESC limit 1");
						
						int call_type1 = 0;
						long call_id = 0;
						
						if(coursor.moveToFirst()){
							
							call_type1 = coursor.getInt(coursor.getColumnIndex(CallLog.Calls.TYPE));
							call_id =  coursor.getLong(coursor.getColumnIndex(CallLog.Calls._ID));
							
						}
						coursor.close();
						
						if(call_type1 == CallLog.Calls.MISSED_TYPE ){
							
							ContentValues values = new ContentValues();
							
							values.put(CallLog.Calls.NEW, 1);
							int update_new_result  = context.getContentResolver().update(CallLog.Calls.CONTENT_URI, values, CallLog.Calls._ID+"="+call_id, null);
						}
					
						if (!isInterceptNumber)  //如果是拦截的电话，将不做刷新通话记录操作
							MainActivity.refreshCallLog();
						
						isInterceptNumber = false;
						
						//清空来电号码
						Editor editor = incomingNum_sf.edit();
						editor.putString("incomingnum", "");
						editor.commit();
						
					
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					
					// 保存来电号码，用于判断来电、去电
					Editor editor_incoming = incomingNum_sf.edit();
					editor_incoming.putString("incomingnum", incomingNumber);
					editor_incoming.commit();
					
					interceptmode = context.getSharedPreferences("interceptmode", 0);
					mode = interceptmode.getInt("mode", 1);
					
					//拦截设置功能
					if(mode == 1){//拦截黑名单、关键字信息
						
					}else if(mode == 2){//拦截陌生号码
						
						boolean isStranger = strangerIntercept(context, incomingNumber); //true 是陌生电话
						
						if(isStranger){
							settingRingModeSilent();
							saveInterceptPhone(context,incomingNumber);
						}
						
					}else if(mode == 3){//拦截白名单以外人员的信息
						
						boolean isWhite = searchWhiteList(context, incomingNumber);   //true 是白名单
						
						if(!isWhite){
							settingRingModeSilent();
							saveInterceptPhone(context,incomingNumber);
						}
						
					}else if(mode == 4){//只拦截黑名单模式
						
						boolean isBlack = searchBlackList(context, incomingNumber);  //true 是黑名单
						
						if(isBlack){
							settingRingModeSilent();
							saveInterceptPhone(context,incomingNumber);
						}
							
					}
//					System.out.println("handle EVENT_AUTO_ANSWER "+isRecorder+", isOpen:"+isOpen);
//					Log.e("PhoneReceiver", "handle EVENT_AUTO_ANSWER "+isRecorder+", isOpen:"+isOpen);
					
					//未接留言
					if(isOpen){
						
						if(isRecorder == false){
							mHandler.sendEmptyMessageDelayed(EVENT_AUTO_ANSWER, AUTO_ANSWER_TIME);
						}
					}
					
					//响铃一声 计时
					
					long ringTime1 = System.currentTimeMillis();
					
					SharedPreferences sf_time = context.getSharedPreferences("ringtime_sf", 0);
					Editor editor1 = sf_time.edit();
					editor1.putLong("ringtime", ringTime1);
					editor1.commit();
					
					
					break;
				}
				
			}
			
		}

	};
	
    //进行录音
    private void recordCalling() {
        try {
            isRecorder = true;
            baseDir= Environment.getExternalStorageDirectory().getAbsolutePath()+"/.dongji/AddressBook/";
            File directory=new File(baseDir);
            if(!directory.exists()) {
            	boolean flag=directory.mkdirs();
            }
            file = new File(baseDir, System.currentTimeMillis() + ".3gp");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 读麦克风的声音
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);// 输出格式.3gp
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 编码方式
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //停止录音
    private void stopRecord() {
    	try {
	        Log.v("TAG", "stopRecord");
	        if (isRecorder) {
	        	
	        	if (mediaRecorder != null) {
		        	mediaRecorder.stop();
		        	mediaRecorder.reset();
		            mediaRecorder.release();
		            isRecorder=false;
		            stopHandle = false;
	        	}
	        }
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    //播放提示音
    private void startPlaying() {
       MediaPlayer mPlayer = null;
        try {
            
            mPlayer = MediaPlayer.create(context, R.raw.warning_tone);
            mPlayer.start();
            
            AudioManager am=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, -2);

            mPlayer.setOnCompletionListener(new OnCompletionListener(){
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mp = null;
                    
                    new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							if (!stopHandle){
						          Message msg = new Message(); 
						          msg.arg1 = 1;
				                  handler.sendMessage(msg);
							}
						}
					}).start();
                }
             });
            
        } catch (Exception e) {
            Log.e("", "prepare() failed");
            System.out.println("startPlaying error:"+e);
        }
    }
	
    public synchronized void answerRingingCall(){
      try{

	      Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	      KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
	      localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
	      context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
	      
	      Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	      KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
	      localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
	      context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
	      
      }catch (Exception e){
    	  System.out.println("answerRingingCall error:"+e);
    	  e.printStackTrace();
      }
    }
	
	
	private void saveRingOneCall(String number){
		
		String[] data = PhoneNumberTool.getContactInfo(context, number);
		String contact_id = null;
		if(data[2] != null)
		{
			contact_id = data[2];
		}
		
		if (contact_id == null){   //陌生人号码响铃一次才进行保存

			Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls._ID}, null, null, " date desc limit 1");
			
			if(c.moveToFirst()){
				
				do{
					
					long id = c.getLong(c.getColumnIndex(CallLog.Calls._ID));
				
					myDatabaseUtil.insertRingOneCall((id+1), number);
					
				}while(c.moveToNext());
			}
			
			c.close();
		}
		
		
	}
	
	
	private void settingRingModeSilent(){
		isInterceptNumber = true;
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);    
		try  {    
	      //挂断电话    
	      mITelephony.endCall();    
		}  catch  (Exception e) {    
	      e.printStackTrace();    
		}    

	} 
	
	public void phoner(){
	     Class <TelephonyManager> c = TelephonyManager.class;  
	     Method getITelephonyMethod = null;  
	     try {  
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);  
            getITelephonyMethod.setAccessible(true);  
            mITelephony = (ITelephony) getITelephonyMethod.invoke(manager, (Object[])null);  
	     } catch (IllegalArgumentException e) {  
	           e.printStackTrace();  
	     } catch (Exception e) {  
	          e.printStackTrace();  
	
	     } 
   }


	
	private void saveInterceptPhone(final Context context,String incomingNumber){
		
		String[] data = PhoneNumberTool.getContactInfo(context, incomingNumber);
		
		String name = "";
		if (data != null) {
			name = data[0];
		}
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm");
		
		Cursor max_id = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls._ID}, null, null,CallLog.Calls._ID +" DESC limit 1");
		
		long id = 0;
		
		if (max_id.moveToFirst()) {
			
			id = max_id.getLong(max_id.getColumnIndex(CallLog.Calls._ID));
			
		}
		
		max_id.close();
		
		myDatabaseUtil.insertPhoneIntercept((id + 1), name, incomingNumber, df.format(new Date(System.currentTimeMillis())), System.currentTimeMillis());
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}
				
				Cursor max_id = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls._ID}, null, null,CallLog.Calls._ID +" DESC limit 1");
				
				if (max_id.moveToFirst()) {
					
					long id = max_id.getLong(max_id.getColumnIndex(CallLog.Calls._ID));
					context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " = " + id, null);
					
				}
				
				max_id.close();
				
			}
		}).start();
			
	}
	
	
	private boolean searchBlackList(Context context,String address){
		boolean isBlack = false;
		
		Cursor cursor = DButil.getInstance(context).queryBlack(address);
		if(cursor.getCount() > 0){
			isBlack = true;
		}
		cursor.close();
		
		return isBlack;
	}
	
	private boolean searchWhiteList(Context context,String address){
		boolean isWhite = false;
		Cursor cursor = DButil.getInstance(context).queryWhite(address);
		if(cursor.getCount() > 0){
			isWhite = true;
		}
		cursor.close();
		
		return isWhite;
	}
	
	private boolean strangerIntercept(Context context,String address){
		boolean isStranger = true;
		
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
		if (phones.moveToFirst()) {
			do{
				String phonenumber = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				if(phonenumber.equals( PhoneNumberTool.cleanse(address))){
					isStranger = false;
					break;
				}
			}while(phones.moveToNext());
		}
		phones.close();
		
		return isStranger;
	}
	
	/**
	 * 利用JAVA反射机制调用ITelephony的answerRingingCall()开始通话。
	 */
	private void startCall() {
		// 初始化iTelephony
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try {
			// 获取所有public/private/protected/默认
			// 方法的函数，如果只需要获取public方法，则可以调用getMethod.
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			// 将要执行的方法对象设置是否进行访问检查，也就是说对于public/private/protected/默认
			// 我们是否能够访问。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false
			// 则指示反射的对象应该实施 Java 语言访问检查。
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			Toast.makeText(context, "安全异常：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		} catch (NoSuchMethodException e) {
			Toast.makeText(context, "未找到方法：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		try {
			ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(
					manager, (Object[]) null);
			// 停止响铃
			iTelephony.silenceRinger();
			// 接听来电
			iTelephony.answerRingingCall();
		} catch (IllegalArgumentException e) {
			Toast.makeText(context, "参数异常：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		} catch (IllegalAccessException e) {
			Toast.makeText(context, "进入权限异常：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		} catch (InvocationTargetException e) {
			Toast.makeText(context, "目标异常：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		} catch (RemoteException e) {
			Toast.makeText(context, "Remote异常：" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
		} catch(SecurityException e) {
			System.out.println("SecurityException:"+e);
			Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");  
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);  
			intent.putExtra("android.intent.extra.KEY_EVENT",keyEvent);  
			context.sendOrderedBroadcast(intent,"android.permission.CALL_PRIVILEGED");  
			intent = new Intent("android.intent.action.MEDIA_BUTTON");  
			keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);  
			intent.putExtra("android.intent.extra.KEY_EVENT",keyEvent);  
			context.sendOrderedBroadcast(intent,"android.permission.CALL_PRIVILEGED");
		}
	}

}


	
//	public static void sendDtmf(Context context, char c,Object phone) throws IllegalArgumentException {
//
//		try{
//
//	      ClassLoader cl = context.getClassLoader(); 
//	      Class phoneClass = cl.loadClass("com.android.internal.telephony.PhoneProxy");
//	      
////	      Object[] parameterTypes= new Object[1];
////	      parameterTypes[0] = phone;
//	      
//	      System.out.println(" object  phone ------------------ > " + phone);
//	      
//	      Class[] cs= new Class[1];
//	      cs[0]= (Class) phone;
//	      
//	      Constructor phoneCon = phoneClass.getConstructor(cs);
//	      Object obj = phoneCon.newInstance(phone);
//	      
//	      //Parameters Types
//	      Class[] paramTypes= new Class[1];
//	      paramTypes[0]= char.class;
//
//	      Method get = phoneClass.getMethod("sendDtmf", paramTypes);
//
//	      //Parameters
//	      Object[] params= new Object[1];
//	      params[0]= c;
//
//	      get.invoke(obj, params);
//
//	    }catch(IllegalArgumentException iAE){
//	        iAE.printStackTrace();
//	    }catch(Exception e){
//	    	e.printStackTrace();
//	    }
//
//	}
	
//	public static Object getDefaultPhone(Context context) throws IllegalArgumentException {
//		System.out.println(" phoneFactory ---------------- > ");
//	    Object ret= null;
//
//	    try{
//	        ClassLoader cl = context.getClassLoader(); 
//	        @SuppressWarnings("rawtypes")
//	        Class PhoneFactory = cl.loadClass("com.android.internal.telephony.PhoneFactory");
//	       
//	        Method get = PhoneFactory.getDeclaredMethod("getDefaultPhone", (Class[]) null);
//	        ret= (Object)get.invoke(PhoneFactory, (Object[]) null);
//
//	        System.out.println(" phoneFactory  333333---------------- > " + ret);
//	        
//	    }catch(IllegalArgumentException iAE){
//	        throw iAE;
//	    }catch(Exception e){
////	        Log.e(TAG, "getDefaultPhone", e);
//	    	e.printStackTrace();
//	    }
//	    System.out.println(" phoneFactory ---------------- > " + ret);
//	    return ret;
//
//	}
