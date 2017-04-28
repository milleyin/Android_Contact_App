package com.dongji.app.adapter;

import java.util.List;

import com.dongji.app.addressbook.R;
import com.dongji.app.entity.KeywordEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class KeywordAdapter extends BaseAdapter {

	Context context;
	List<KeywordEntity> list = null;
	OnClickListener onClickListener;
	
	public KeywordAdapter(Context context,List<KeywordEntity> list,OnClickListener onClickListener){
		this.context = context;
		this.list = list;
		this.onClickListener = onClickListener;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(R.layout.intercept_keyword_item, null);
		
		ViewHolder viewHolder= new ViewHolder();
		
		viewHolder.content = (TextView) convertView.findViewById(R.id.keyword_content);
		viewHolder.delete = (Button) convertView.findViewById(R.id.keyword_delete);
		
		KeywordEntity keywordEntity = list.get(position);
		
		viewHolder.content.setText(keywordEntity.getContent());
		viewHolder.delete.setTag(keywordEntity.getId()+","+keywordEntity.getContent());
		viewHolder.delete.setOnClickListener(onClickListener);
		return convertView;
	}
	
	class ViewHolder{
		TextView content;
		Button delete;
	}

}
