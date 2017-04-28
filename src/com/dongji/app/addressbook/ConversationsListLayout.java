package com.dongji.app.addressbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dongji.app.adapter.ConversationAdapter;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.entity.ConversationBean;

/**
 * 
 * 会话列表
 * 
 * @author Administrator
 *
 */
public class ConversationsListLayout implements OnClickListener, OnScrollListener {
	
	boolean isNeedRefresh = false; //是否需要刷新
	
	//所有的短信
	public static final String SMS_URI_ALL = "content://sms/";
	// 收件箱短信
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	// 发件箱短信
	public static final String SMS_URI_SEND = "content://sms/sent";
	// 草稿箱短信
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	
	
	public MainActivity mainActivity;

	public View view;
	
	LinearLayout tips_layout;
	private View mBottomView;
	
	Button btn_new_message; //新建短信
	
	
	Button btn_del_mode;  //批量删除
	Button btn_del_yes;
	Button btn_del_no;
	
	
	public ListView lv_sms;
	ConversationAdapter smssAdapter;
	
	View menu;
	int margin_bottom;
	
	NewMessageActivity newMessageLayout;
	
	ProgressDialog progressDialog;
	 
    String selected_thread_id; //删除按钮选中的短信会话id
    
    public static String SF_KEY_SMS_SORT = "sms_sort";
    public static  String SF_NAME = "systemsetting";
	SharedPreferences sf ;
	
	List<SmsContent> all_sms;
	
	Dialog dialog;
	
	boolean isDeletering = false; //正在批量删除
	
	private int pos;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, margin_bottom);
			
			menu.setLayoutParams(lp);
			menu.postInvalidate();
			
			System.out.println("====================="+pos);
			
			lv_sms.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					lv_sms.smoothScrollToPosition(pos);
//					System.out.println("滑动--->");
				}
			}, 100);
		};
	};
	
	Handler handler_delete = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		
    		switch (msg.what) {
			case 1:
				
				if(progressDialog!=null && progressDialog.isShowing())
				{
					progressDialog.dismiss();
				}
				
				smssAdapter.refresh(false);
				
				isDeletering = false;
				smssAdapter.setEditMode(false);
				
				break;

			default:
				break;
			}
    		
    	};
    };
	
    Handler handler_load = new Handler()
    {
    	public void handleMessage(android.os.Message msg) {
    		init();
    	};
    };
    
	public ConversationsListLayout(MainActivity mainActivity)
	{
		this.mainActivity = mainActivity;
//		this.height = mainActivity.getResources().getDimensionPixelSize(R.dimen.menu_height);
//		view = LayoutInflater.from(mainActivity).inflate(R.layout.message_list, null);
		
		view = mainActivity.findViewById(R.id.layout_message_list);
		
		sf = mainActivity.getSharedPreferences(SF_NAME, 0);
		
		mBottomView=view.findViewById(R.id.bottomlayout);
		
		tips_layout = (LinearLayout)view.findViewById(R.id.tips_layout);
		
		btn_new_message = (Button) view.findViewById(R.id.btn_new_message);
		btn_new_message.setOnClickListener(this);
		
		btn_del_mode = (Button)view.findViewById(R.id.btn_del_mode);
		btn_del_mode.setOnClickListener(this);
		
		lv_sms = (ListView)view.findViewById(R.id.lv);
		lv_sms.setOnScrollListener(this);
		
		this.mainActivity.state = mainActivity.STATE_MESSAGE_LIST;
		
		init();
	}
	
	//加载数据
	public void init(){
		
		OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener();
//		smsListAdapter = new SmsListAdapter(mainActivity,getSmsList(),true,onMenuItemClickListener);
//		lv_sms.setAdapter(smsListAdapter);
		
		smssAdapter = new ConversationAdapter(mainActivity, this,onMenuItemClickListener);
		lv_sms.setAdapter(smssAdapter);
		
		lv_sms.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (smssAdapter != null && !smssAdapter.isEditMode()) {
					
					ConversationBean smsThread = (ConversationBean) smssAdapter.getItem(arg2);
					
					if (smsThread.getRead() == 0) {
						
						if (arg1.findViewById(R.id.read).getVisibility() == View.VISIBLE) {
							arg1.findViewById(R.id.read).setVisibility(View.GONE);
						}
						smsThread.setRead(1);
					}
					

					Intent intent = new Intent(mainActivity, NewMessageActivity.class);
					
					intent.putExtra(NewMessageActivity.DATA_THREAD_ID, String.valueOf(smsThread.getThread_id()));
							
					mainActivity.startActivity(intent);
					
				}
			}
		});
		
		lv_sms.setOnItemLongClickListener(new OnItemLongClickListener() { //长按监听


			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
//				po  = arg2;
				
				if(smssAdapter.isEditMode())
				{
					return true;
				}
				
//				System.out.println(" onItemLongClick ");
				if(menu!=null && menu!=arg1.findViewById(R.id.menu))
				{
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
					lp.setMargins(0, 0, 0, -menu.getHeight());
					
					menu.setLayoutParams(lp);
//					menu.setVisibility(View.GONE);
				}
			
//				if(margin_bottom<0)
//				{
//					menu.setVisibility(View.VISIBLE);
//				}
				
				menu = arg1.findViewById(R.id.menu);
				final int height = menu.getHeight();
				margin_bottom = ((LinearLayout.LayoutParams)menu.getLayoutParams()).bottomMargin;
				
//				System.out.println(" margin_bottom ---> " + margin_bottom);
//				System.out.println(" height ---> " + height);
				pos=arg2;
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						if(margin_bottom<-5)
						{
							while(margin_bottom<-5)
							{
								margin_bottom+=5;
								try {
									Thread.sleep(10);
								} catch (Exception e) {
									e.printStackTrace();
								}
								handler.sendEmptyMessage(0);
								
								System.out.println("  run() ---> " + margin_bottom);
							}
						}else{
							
							
							while(margin_bottom>-height)
							{
								margin_bottom-=5;
								try {
									Thread.sleep(10);
								} catch (Exception e) {
									e.printStackTrace();
								}
								handler.sendEmptyMessage(0);
								
								System.out.println("  run() ---> " + margin_bottom);
							}
						}
					
						
					}
				}).start();
				return true;
			}
		});
		
	}
	
	public void showTips()
	{
		mBottomView.setVisibility(View.VISIBLE);
		tips_layout.setVisibility(View.VISIBLE);
		Animation animation_in = AnimationUtils.loadAnimation( mainActivity , R.anim.dialing_in);
		animation_in.setDuration(200);
		tips_layout.startAnimation(animation_in);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_del_mode:
			Animation anim_out = AnimationUtils.loadAnimation(mainActivity, R.anim.fade_out);
			anim_out.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					tips_layout.removeAllViews();
					View view = LayoutInflater.from(mainActivity).inflate(R.layout.tip_yes_no, null);
					btn_del_yes = (Button)view.findViewById(R.id.btn_del_yes);
					btn_del_yes.setOnClickListener(ConversationsListLayout.this);
					btn_del_no = (Button)view.findViewById(R.id.btn_del_no);
					btn_del_no.setOnClickListener(ConversationsListLayout.this);
					
					tips_layout.addView(view);
					Animation anim_in = AnimationUtils.loadAnimation(mainActivity, R.anim.fade_in);
					tips_layout.startAnimation(anim_in);
				}
			});
			tips_layout.startAnimation(anim_out);
			
			smssAdapter.setEditMode(true);
			
			break;
			
		case R.id.btn_del_yes:
            int [] indexs = smssAdapter.getSelectedItemIndexes();
			
			if(indexs.length>0)
			{
				tips_layout.removeAllViews();
				tips_layout.addView(btn_del_mode);
				tips_layout.setVisibility(View.GONE);
				mBottomView.setVisibility(View.GONE);
				
//				tips_top_layout.setVisibility(View.VISIBLE);
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
				
//				myDialog = new MyDialog(mainActivity, "确定删除选中的会话?", new DialogOnClickListener());
//				myDialog.normalDialog();
				
				getDialog();
				
			}else{
//				Toast.makeText(mainActivity, "请选择至少一条记录 ", Toast.LENGTH_SHORT).show();
			}
			
			break;
			
		case R.id.btn_del_no:
			tips_layout.removeAllViews();
			tips_layout.addView(btn_del_mode);
			tips_layout.setVisibility(View.GONE);
			mBottomView.setVisibility(View.GONE);
			
//			mainActivity.l_scrolelr.snapToScreen(0);
			
			smssAdapter.setEditMode(false);
			break;

		case R.id.btn_new_message: //新建短信
			
			Intent intent = new Intent(mainActivity, NewMessageActivity.class);
			mainActivity.startActivity(intent);
			break;
			
		default:
			break;
		}
	}
	
	void delete()
	{
		if(smssAdapter.isEditMode())
		{
			isDeletering = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					long [] threadIds = smssAdapter.getSelectedSmsThreadId();
					int size = threadIds.length;
					
					for(int i = 0; i<size;i++)
					{
						  Uri mUri = Uri.parse("content://mms-sms/conversations/" + threadIds[i]);
						  mainActivity.getContentResolver().delete(mUri, null, null);
					}
					handler_delete.sendEmptyMessage(1);
				}
			}).start();
			
		}else{
			
			isDeletering = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					
					int delete = mainActivity.getContentResolver().delete(Uri.parse("content://mms-sms/conversations/" + selected_thread_id),null, null);
					System.out.println(" delete --->" + delete);
					handler_delete.sendEmptyMessage(1);
				}
			}).start();
		}
	}
	
	class OnMenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {

			if(menu!=null)
			{
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -menu.getHeight());
				
				menu.setLayoutParams(lp);
			}

			
			switch (v.getId()) {
			case R.id.menu_call:
				
				String phone_number = (String) v.getTag();
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone_number));
				mainActivity.startActivity(intent);
				
				break;
				
				
			case R.id.menu_sms_detail:
                
				
				String n = (String)v.getTag();

				Intent m_intent = new Intent(mainActivity, NewMessageActivity.class);
				m_intent.putExtra(NewMessageActivity.DATA_THREAD_ID, n);
				mainActivity.startActivity(m_intent); 
			    
				break;
				
				
			case R.id.menu_delete:
				
				selected_thread_id = (String) v.getTag();
				
				System.out.println(" selected_number is --->" + selected_thread_id);
				
//				tv_top_tips.setText("确定删除选中的会话?");
				
//				myDialog = new MyDialog(mainActivity, "确定删除选中的会话?", new DialogOnClickListener());
//				myDialog.normalDialog();
				
				getDialog();
				
//				tips_top_layout.setVisibility(View.VISIBLE);
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
				
				break;

			default:
				break;
			}
		}
	}
	
	private void getDialog(){
		
		dialog = new Dialog(mainActivity,R.style.theme_myDialog_activity);
		dialog.setContentView(R.layout.mydialog);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		
		dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
//                mainActivity.l_scrolelr.snapToScreen(0);
			}
		});
		
		TextView tv_tips = (TextView)dialog.findViewById(R.id.tv_tips);
//		tv_top_tips = (TextView) dialog.findViewById(R.id.tv_tips);
		Button btn_top_tips_yes = (Button) dialog.findViewById(R.id.btn_top_tips_yes);
		Button btn_top_tips_no = (Button) dialog.findViewById(R.id.btn_top_tips_no);
//		
		tv_tips.setText("确定删除选中的会话?");
//		
		btn_top_tips_yes.setOnClickListener(new DialogOnClickListener());
		btn_top_tips_no.setOnClickListener(new DialogOnClickListener());
		
	}
	
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_top_tips_no:
//				tips_top_layout.setVisibility(View.GONE);
				
				dialog.cancel();
				
//				mainActivity.l_scrolelr.snapToScreen(0);
				
				smssAdapter.setEditMode(false);
				break;
				
			case R.id.btn_top_tips_yes:
//				tips_top_layout.setVisibility(View.GONE);
				dialog.cancel();
//				mainActivity.l_scrolelr.snapToScreen(0);
				
				progressDialog = new ProgressDialog(mainActivity);
				progressDialog.setMessage("正在删除,请稍后");
				progressDialog.show();
				
				delete();
				
				break;

			default:
				break;
			}
			
			
		}
		
	}
	
	
	//删除短信
	public void deleteSMS(String delete_id) {
		try {
			// Delete SMS
			String split = delete_id;
			Long id = Long.parseLong(delete_id.substring(0,delete_id.indexOf(",")));
			Long threadId = Long.parseLong(delete_id.substring(delete_id.indexOf(",") + 1, delete_id.length()));
			mainActivity.getContentResolver().delete(Uri.parse("content://sms/conversations/" + threadId),"_id =" + id, null);
			Log.d("deleteSMS", "threadId:: " + threadId);
		} catch (Exception e) {
			Log.d("deleteSMS", "Exception:: " + e);
		}
	}
    
    public void updateDeleteNum(int selected)
	{
		btn_del_yes.setText("删除("+selected+")");
		
//		if(btn_top_tips_yes.getVisibility()==View.VISIBLE)
//		{
//			tv_top_tips.setText("确定删除选中的会话？");
//		}
	}
    
    public void refresh(boolean is2Top)
    {
    	if(smssAdapter!=null && !isDeletering)
    	{
    		System.out.println("列表 刷新  --- >");
    		try {
    			smssAdapter.refresh(is2Top);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	isNeedRefresh = false;
    }
    
    //当联系人数据库gia百年
    public void updateWhenContactChange()
    {
    	try {
    		if(smssAdapter!=null)
        	{
        		smssAdapter.reQueryContactName();
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void refreshSort()
    {
    	if(smssAdapter!=null)
    	{
    		System.out.println("列表 刷新  --- >");
    		smssAdapter.refreshSort();
    	} 
    }
    
    
    public void updateAfterChangeEncryption()
    {
    	if(smssAdapter!=null)
    	{
    		smssAdapter.updateAfterChangeEncryption();
    	}
    }

	public boolean  onBackPress() {
		
		if(tips_layout.getVisibility()==View.VISIBLE)
		{
			tips_layout.removeAllViews();
			tips_layout.addView(btn_del_mode);
			tips_layout.setVisibility(View.GONE);
			mBottomView.setVisibility(View.GONE);
//			mainActivity.l_scrolelr.snapToScreen(0);
			
			smssAdapter.setEditMode(false);
			
			return true;
		}
		
		return false;
	}

	boolean isT;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if(pos < firstVisibleItem || pos >firstVisibleItem+visibleItemCount) {
			/*if(menu!=null && !isT)
			{
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -menu.getHeight());
				
				menu.setLayoutParams(lp);
				final int height = menu.getHeight();
				margin_bottom = ((LinearLayout.LayoutParams)menu.getLayoutParams()).bottomMargin;
				
				isT=true;
//				System.out.println(" margin_bottom ---> " + margin_bottom);
//				System.out.println(" height ---> " + height);
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {

						while (margin_bottom > -height) {
							margin_bottom -= 5;
							try {
								Thread.sleep(10);
							} catch (Exception e) {
								e.printStackTrace();
							}
							handler.sendEmptyMessage(0);

							System.out.println("  run() ---> " + margin_bottom);
						}
					}
					
						
				}).start();
			}*/
			
			if(menu!=null) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -menu.getHeight());
				
				menu.setLayoutParams(lp);
				menu.postInvalidate();
			}
			
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	public void collapse() {
		if (smssAdapter!=null && menu != null) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
		}
	}

	/**
	 * 刷新定时短信的时间
	 */
	public void onResume() {
		
		if(smssAdapter!=null)
		{
			smssAdapter.notifyDataSetChanged();
		}
	}
}
