package com.dongji.app.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dongji.app.ui.EmotionHelper;

public class EmotionListAdapter extends BaseAdapter {
	
	private Context context;
	
	public EmotionListAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return EmotionHelper.emotionResID.length;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView image = new ImageView(context);
		image.setFocusable(false);
		image.setImageDrawable(context.getResources().getDrawable(EmotionHelper.emotionResID[position]));
		return image;
	}
}
