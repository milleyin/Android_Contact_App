package com.dongji.app.addressbook;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.dongji.app.adapter.MissedMessageAdapter;
import com.dongji.app.entity.MissedMessageEntity;
import com.dongji.app.sqllite.MissedMessageDBHelper;
import com.dongji.app.ui.MyDialog;
import com.dongji.app.ui.ScrollLayout;

/**
 * 
 * 未接留言
 * 
 * @author Administrator
 *
 */
public class MissedMessageLayout implements OnClickListener {
	
	MainActivity mainActivity;
	
	public View view;
	
	ScrollLayout sc_switch;
	Button delete_all;
	
	ListView lvmissedmessage;
	
	SharedPreferences sf = null;
	
	SharedPreferences.Editor editor = null;
	
	SQLiteDatabase db = null;
	
	MissedMessageDBHelper missedMessageDBHelper = null;
	
	List<MissedMessageEntity> list = null;
	
	MissedMessageAdapter missMessageAdapter = null;
	
	LinearLayout tips_top_layout;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	TextView tv_delete_tips;
	
	int type = 0;
	
	int id = 0;
	String directory = "";
	
	MyDialog myDialog;
	
	public MissedMessageLayout(MainActivity mainActivity){
		
		this.mainActivity = mainActivity;
		
		view = LayoutInflater.from(mainActivity).inflate(R.layout.setting_item_5_miss_message, null);
		
		sf = mainActivity.getSharedPreferences("missedmessage", 0);
		
		sc_switch = (ScrollLayout) view.findViewById(R.id.sc_switch);
		
//		boolean isOpen = sf.getBoolean("missedMessage", false);
//		
//		if(isOpen){
//			sc_switch.snapToScreen(0);
//		}else{
//			sc_switch.snapToScreen(1);
//		}
		
		sc_switch.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					editor = sf.edit();
					
					System.out.println(" is open ---- > " + sf.getBoolean("missedMessage", false));
					
					boolean isOpen = sf.getBoolean("missedMessage", false);
					
					if(isOpen){
						editor.putBoolean("missedMessage", false);
						editor.commit();
						sc_switch.snapToScreen(1);
					}else{
						editor.putBoolean("missedMessage", true);
						editor.commit();
						sc_switch.snapToScreen(0);
					}
					
					System.out.println(" 1 is open ---- > " + sf.getBoolean("missedMessage", false));
				}
				return true;
			}
		});
		
		delete_all = (Button) view.findViewById(R.id.delete_all);
		lvmissedmessage = (ListView) view.findViewById(R.id.lvmissedmessage);
		
		tips_top_layout = (LinearLayout) view.findViewById(R.id.tips_top_layout);
		btn_top_tips_yes = (Button) view.findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_no = (Button) view.findViewById(R.id.btn_top_tips_no);
		tv_delete_tips = (TextView) view.findViewById(R.id.tv_delete_tips);
		
		
		delete_all.setOnClickListener(this);
		btn_top_tips_yes.setOnClickListener(this);
		btn_top_tips_no.setOnClickListener(this);
		
		loadData();
	}
	
	public void loadData(){
		
		boolean isOpen = sf.getBoolean("missedMessage", false);
		
		if(isOpen){
			sc_switch.setToScreen(0);
		}else{
			sc_switch.setToScreen(1);
		}
		
		System.out.println(" sf  missMessage ---- > " + sf.getBoolean("missedMessage", false));

		list = new ArrayList<MissedMessageEntity>();
		
		missedMessageDBHelper = new MissedMessageDBHelper(mainActivity);
		
		db = missedMessageDBHelper.getReadableDatabase();
		
		Cursor c = db.query(missedMessageDBHelper.table, null, null, null, null, null, missedMessageDBHelper._ID + " desc");
		
		if(c.moveToFirst()){
			do{
				
				MissedMessageEntity missedMessage = new MissedMessageEntity();
				
				missedMessage.setId(c.getLong(c.getColumnIndex(missedMessageDBHelper._ID)));
				missedMessage.setNumber(c.getString(c.getColumnIndex(missedMessageDBHelper.NUMBER)));
				missedMessage.setDir(c.getString(c.getColumnIndex(missedMessageDBHelper.DIR)));
				missedMessage.setDate(c.getString(c.getColumnIndex(missedMessageDBHelper.DATE_TIME)));
				
				list.add(missedMessage);
			}while(c.moveToNext());
		}
		
		c.close();
		db.close();

		missMessageAdapter = new MissedMessageAdapter(mainActivity, list,new OnMenuItemClickListener());
		lvmissedmessage.setAdapter(missMessageAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_all:
			type = 2;
//			tv_delete_tips.setText("确定删除全部留言文件");
//			tips_top_layout.setVisibility(View.VISIBLE);
//			tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
			
			myDialog = new MyDialog(mainActivity, "确定删除全部留言文件", new DialogOnClickListener());
			myDialog.normalDialog();
			
			break;
		case R.id.btn_top_tips_yes:
		
			db = missedMessageDBHelper.getWritableDatabase();
			
			if(type == 1 && id != 0){
				
				db.delete(missedMessageDBHelper.table, missedMessageDBHelper._ID+"="+id, null);
				
				File file = new File(directory);
				file.delete();
				
			}else{
				
				db.delete(missedMessageDBHelper.table, null, null);
				
				File f = new File(Environment.getExternalStorageDirectory().toString());
       			
       			File[] fl = f.listFiles();
       			for (int i=0; i<fl.length; i++)
       			{
       				if(fl[i].toString().endsWith(".3gp"))
       				{
       					fl[i].delete();
       				}
       			}
				
			}
			
			db.close();
			
			loadData();
			
			tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_out));
			tips_top_layout.setVisibility(View.GONE);
			
			break;
		
		case R.id.btn_top_tips_no:
			
			tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_out));
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
				
//				tv_delete_tips.setText("确定删除选中的留言文件");
//				tips_top_layout.setVisibility(View.VISIBLE);
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_in));
				
				type = 1;
				String tag = v.getTag().toString();
				id = Integer.parseInt(tag.substring(0, tag.indexOf(",")));
				directory = tag.substring(tag.indexOf(",")+1,tag.length());
				
				myDialog = new MyDialog(mainActivity, "确定删除选中的留言文件", new DialogOnClickListener());
				myDialog.normalDialog();
				
				break;

			case R.id.play:
				
				String dir = v.getTag().toString();
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + dir), "video/mp4");
                mainActivity.startActivity(intent);            
                
				break;
				
			case R.id.call:
	
				String number = v.getTag().toString();
				
				Uri uri = Uri.parse("tel:"+number); 
				Intent it = new Intent(Intent.ACTION_CALL, uri); 
				it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mainActivity.startActivity(it); 
				break;
			default:
				break;
			}
		}
	}
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			
			case R.id.btn_top_tips_yes:
			
				db = missedMessageDBHelper.getWritableDatabase();
				
				if(type == 1 && id != 0){
					
					db.delete(missedMessageDBHelper.table, missedMessageDBHelper._ID+"="+id, null);
					
					File file = new File(directory);
					file.delete();
					
				}else{
					
					db.delete(missedMessageDBHelper.table, null, null);
					
					File f = new File(Environment.getExternalStorageDirectory().toString());
	       			
	       			File[] fl = f.listFiles();
	       			for (int i=0; i<fl.length; i++)
	       			{
	       				if(fl[i].toString().endsWith(".3gp"))
	       				{
	       					fl[i].delete();
	       				}
	       			}
					
				}
				
				db.close();
				
				loadData();
				
//				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(mainActivity, R.anim.up_out));
//				tips_top_layout.setVisibility(View.GONE);
				
				myDialog.closeDialog();
				
				break;
			
			default:
				break;
			}
			
		}
		
	}
	
}
