package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.R;
import com.dongji.app.entity.SmsCollectEntity;
import com.dongji.app.tool.ExpressionUtil;

public class SmsCollectAdapter extends BaseAdapter {

	Context context;
	
	List<SmsCollectEntity> collectList = new ArrayList<SmsCollectEntity>();
	
	OnClickListener onClickListener;
	
	public View menu;
	int margin_bottom;
	
	int height;
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);

			switch (msg.what) {
			case 0:
				lp.setMargins(0, 0, 0, margin_bottom);

				menu.setLayoutParams(lp);
				menu.postInvalidate();

//				System.out.println(" margin_bottom ---> " + margin_bottom);
				
				if(margin_bottom <= -height)
				{
					System.out.println(" set gone");
					menu.setVisibility(View.GONE);
				}
				break;
		    	
			default:
				break;
			}
		}
	};
	
	public SmsCollectAdapter(Context context,List<SmsCollectEntity> collectList,OnClickListener onClickListener) {
		
		this.context = context;
		this.collectList = collectList;
		this.onClickListener = onClickListener;
		this.height = context.getResources().getDimensionPixelSize(R.dimen.menu_height);
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return collectList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return collectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = LayoutInflater.from(context).inflate(R.layout.smscollectitems, null);
		
		ViewHolder viewHolder = new ViewHolder();
		
		viewHolder.collectInfo = (TextView) convertView.findViewById(R.id.collect_info);
		viewHolder.time = (TextView) convertView.findViewById(R.id.collect_time);
		viewHolder.menu_search = (Button) convertView.findViewById(R.id.menu_search);
		viewHolder.menu_send = (Button) convertView.findViewById(R.id.menu_send);
		viewHolder.menu_delete = (Button) convertView.findViewById(R.id.menu_delete);
		
		LinearLayout main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						
							popUpMenu(v);

						break;

					default:
						break;
					}

					return false;
				}
			});
		
		SmsCollectEntity smsCollectEntity = collectList.get(position);
		
		
		String str = smsCollectEntity.getFavorite_content();					
		SpannableString spannableString = ExpressionUtil.getExpressionString(context, str);
		
		try {
			viewHolder.collectInfo.setText(spannableString);
		} catch (Exception e) {
		}
		
		viewHolder.time.setText("收藏时间："+smsCollectEntity.getContent_time());
		
		
		viewHolder.menu_search.setOnClickListener(onClickListener);
		viewHolder.menu_send.setOnClickListener(onClickListener);
		viewHolder.menu_delete.setOnClickListener(onClickListener);
		viewHolder.menu_search.setTag(String.valueOf(smsCollectEntity.getContent_id()));
		viewHolder.menu_send.setTag(smsCollectEntity.getFavorite_content());
		viewHolder.menu_delete.setTag(String.valueOf(smsCollectEntity.getFavorite_id()));
		
		return convertView;
	}
	
	class ViewHolder{
		TextView collectInfo;
		TextView time;
		
		Button menu_search;
		Button menu_send;
		Button menu_delete;
	}
	
	private void popUpMenu(View view) {
		
		System.out.println("  popUpMenu  --->");
		
		
		//隐藏    同一时间只能有一个下来菜单被展开
		if (menu != null && menu != view.findViewById(R.id.menu)) {  
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
			menu.setVisibility(View.GONE);
		}

		menu = view.findViewById(R.id.menu);
		margin_bottom = ((LinearLayout.LayoutParams) menu.getLayoutParams()).bottomMargin;
		
		if(margin_bottom<0)
		{
			menu.setVisibility(View.VISIBLE);
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (margin_bottom < -5) {
					while (margin_bottom < -5) {
						margin_bottom += 5;
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				} else {

					while (margin_bottom > -height) {
						margin_bottom -= 5;
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}

			}
		}).start();
	}

}
