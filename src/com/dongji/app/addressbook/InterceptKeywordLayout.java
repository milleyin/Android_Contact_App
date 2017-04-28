package com.dongji.app.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.KeywordAdapter;
import com.dongji.app.entity.KeywordEntity;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.ui.MyDialog;

public class InterceptKeywordLayout implements OnClickListener {

	InterceptSettingActivity context;
	
	public View view;
	
	ListView lvkeyword;
	
	Button refresh;
	Button add;
	EditText et_keyword_content;
	
	Button keyword_delete;
	TextView keyword_content;
	
	LinearLayout tips_add_layout;
	Button btn_add_tips_yes;
	Button btn_add_tips_no;
	LinearLayout tips_top_layout;
	TextView delete_id;
	TextView tv_delete_tips;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	
	MyDialog myDialog;
	Dialog dialog;
	
	MyDatabaseUtil myDatabaseUtil = null;
	
	Animation an_in;
	Animation an_out;
	
	LinearLayout lv_container;
	
	SharedPreferences sf = null;
	Editor editor = null;
	
	public InterceptKeywordLayout(InterceptSettingActivity context){
		
		this.context = context;
		
		view  = LayoutInflater.from(context).inflate(R.layout.intercepte_keyword, null);
		
		myDatabaseUtil = DButil.getInstance(context);
		
		//第一次默认添加两条数据
		sf = context.getSharedPreferences("keyword_add", 0);
		editor = sf.edit();
		
		boolean isAdd = sf.getBoolean("isadd", false);
		
		if (!isAdd) {
			
			myDatabaseUtil.firstAddKeyWord();
			
			editor.putBoolean("isadd", true);
			editor.commit();
			
		}
		
		lvkeyword = (ListView) view.findViewById(R.id.lvkeyword);
		
		refresh = (Button) view.findViewById(R.id.refresh);
		add = (Button) view.findViewById(R.id.add);
		
		tips_add_layout = (LinearLayout) view.findViewById(R.id.tips_add_layout);
//		btn_add_tips_yes = (Button) view.findViewById(R.id.btn_add_tips_yes);
//		btn_add_tips_no = (Button) view.findViewById(R.id.btn_add_tips_no);
//		et_keyword_content = (EditText) view.findViewById(R.id.et_keyword_content);
		
		tips_top_layout = (LinearLayout) view.findViewById(R.id.tips_top_layout);
		delete_id = (TextView) view.findViewById(R.id.delete_id);
		tv_delete_tips = (TextView) view.findViewById(R.id.tv_delete_tips);
		btn_top_tips_yes = (Button) view.findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_no = (Button) view.findViewById(R.id.btn_top_tips_no);
		
		add.setOnClickListener(this);
		refresh.setOnClickListener(this);
//		btn_add_tips_yes.setOnClickListener(this);
//		btn_add_tips_no.setOnClickListener(this);
		btn_top_tips_yes.setOnClickListener(new OnMenuItemClickListener());
		btn_top_tips_no.setOnClickListener(new OnMenuItemClickListener());
		
		an_in = AnimationUtils.loadAnimation(context, R.anim.up_in);
		an_out = AnimationUtils.loadAnimation(context, R.anim.up_out);
		an_out.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				tips_add_layout.setVisibility(View.GONE);
				tips_top_layout.setVisibility(View.GONE);
			}
		});
		
		lv_container = (LinearLayout)view.findViewById(R.id.lv_container);
		
		loadData();
	}
	
	public void loadData(){
		
		List<KeywordEntity> keywordlist = myDatabaseUtil.queryKeyWord();
		
		KeywordAdapter keywordAdapter = new KeywordAdapter(context, keywordlist,new OnMenuItemClickListener());
		lvkeyword.setAdapter(keywordAdapter);
		
		
        lv_container.removeAllViews();
		
		//查询收藏联系人列表，初始化
		
		DisplayMetrics metric = new DisplayMetrics();
	    context.getWindowManager().getDefaultDisplay().getMetrics(metric);
	    int item_width = metric.widthPixels / 3;     // 每个按钮的宽度

	    LinearLayout.LayoutParams item_lp = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.FILL_PARENT);
	    item_lp.width = item_width;
	    
		int count = 0;
		
		LinearLayout curentLn = null ;
		LinearLayout  first_l = new LinearLayout(context);
		
		curentLn = first_l;
		
		LinearLayout.LayoutParams lp_first = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		lp_first.weight = 1;
		lp_first.topMargin = 10;
		lp_first.height = context.getResources().getDimensionPixelSize(R.dimen.dialing_panel_line_height);
		first_l.setLayoutParams(lp_first);
		
		lv_container.addView(first_l);
		
		for(KeywordEntity k:keywordlist){
				if(count<3)
				{
					View v = LayoutInflater.from(context).inflate(R.layout.key_word, null);
					
					LinearLayout item_ln = (LinearLayout)v;
					item_ln.setLayoutParams(item_lp);
					
					Button btn_delete_keyword = (Button)v.findViewById(R.id.btn_delete_keyword);
					btn_delete_keyword.setTag(String.valueOf(k.getId())+","+k.getContent());
					btn_delete_keyword.setOnClickListener(new KeyWordDeleteClickListener());
					
					TextView tv_name = (TextView)v.findViewById(R.id.tv_keyword);
					tv_name.setText(k.getContent());
					
					curentLn.addView(v);
					
					count ++;
					
				}else{
					count = 1;
					
					LinearLayout  l = new LinearLayout(context);
					curentLn = l;
					
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.weight = 1;
					lp.topMargin = 10;
					lp.height = context.getResources().getDimensionPixelSize(R.dimen.dialing_panel_line_height);
					l.setLayoutParams(lp);
					
                    View v = LayoutInflater.from(context).inflate(R.layout.key_word, null);
					
					LinearLayout item_ln = (LinearLayout)v;
					item_ln.setLayoutParams(item_lp);
					
					Button btn_delete_keyword = (Button)v.findViewById(R.id.btn_delete_keyword);
					btn_delete_keyword.setTag(String.valueOf(k.getId())+","+k.getContent());
					btn_delete_keyword.setOnClickListener(new KeyWordDeleteClickListener());
					
					TextView tv_name = (TextView)v.findViewById(R.id.tv_keyword);
					tv_name.setText(k.getContent());
					
					l.addView(v);
					
					lv_container.addView(l);
				}
				
				System.out.println(" count ---:>" + count);
			}
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			
			break;
		case R.id.add:
			
//			tips_add_layout.setVisibility(View.VISIBLE);
//			tips_add_layout.startAnimation(an_in);
			
			
			dialog = new Dialog(context,R.style.theme_myDialog_activity);
			dialog.setContentView(R.layout.add_info_dialog);
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			
			TextView tv_top_tips = (TextView) dialog.findViewById(R.id.tv_top_tips);
//			TextView content_tips = (TextView) dialog.findViewById(R.id.content_tips);
			et_keyword_content = (EditText) dialog.findViewById(R.id.content);
			btn_add_tips_yes = (Button) dialog.findViewById(R.id.btn_add_tips_yes);
			btn_add_tips_no = (Button) dialog.findViewById(R.id.btn_add_tips_no);
			
			tv_top_tips.setText("添加关键字");
			et_keyword_content.setHint("请输入关键字");
//			content_tips.setText("关键字");
			
			btn_add_tips_yes.setOnClickListener(this);
			btn_add_tips_no.setOnClickListener(this);
			
			break;
		case R.id.btn_add_tips_yes:
			
			String keyword = et_keyword_content.getText().toString();
			
			if(!keyword.equals(""))
			{
				SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
				
				long add = myDatabaseUtil.addKeyWord(keyword, df.format(new Date()), 0);
				dialog.cancel();
				
				if ( add > 0) 
					loadData();
				
				et_keyword_content.setText("");
				
			}else{
				Toast.makeText(context, "请输入关键字", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.btn_add_tips_no:
			dialog.cancel();
//			tips_add_layout.startAnimation(an_out);
			break;

		default:
			break;
		}
	}
	
	
	class OnMenuItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_top_tips_yes:
					
					int keyword = Integer.parseInt(delete_id.getText().toString());
					
					myDatabaseUtil.deleteKeyWord(keyword);
					
					tips_top_layout.startAnimation(an_out);
					loadData();
					
					break;
				case R.id.btn_top_tips_no:
					
					tips_top_layout.startAnimation(an_out);
					
					break;	
				default:
					break;
			}
		}
	}
	
	class KeyWordDeleteClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String tag = v.getTag().toString();
			String content = tag.substring(tag.indexOf(",")+1, tag.length());
			String id = tag.substring(0, tag.indexOf(","));
			
			String new_tip = "确定要删除关键字:"+""+content+"?";
			tv_delete_tips.setText(new_tip);
//			tips_top_layout.setVisibility(View.VISIBLE);
//			tips_top_layout.startAnimation(an_in);
			delete_id.setText(id);
			
			myDialog = new MyDialog(context, new_tip, new DialogOnClickListener());
			myDialog.normalDialog();
			
		}
	}
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_top_tips_yes:
				
				int keyword = Integer.parseInt(delete_id.getText().toString());
				long delete = myDatabaseUtil.deleteKeyWord(keyword);
				
				if (delete > 0)
					loadData();
				
				myDialog.closeDialog();
				
				break;
			default:
				break;
		}
		}
	}
	
}
