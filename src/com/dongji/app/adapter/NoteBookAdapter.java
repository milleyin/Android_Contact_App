package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.dongji.app.addressbook.R;
import com.dongji.app.entity.NoteBook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class NoteBookAdapter extends BaseAdapter {

	Context context;
	List<NoteBook> list = new ArrayList<NoteBook>();
	OnClickListener onClickListener;

	public NoteBookAdapter(Context context, List<NoteBook> list,OnClickListener onClickListener) {
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
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(context).inflate(
				R.layout.notebooklistitem, null);
		
		ViewHolder holder = new ViewHolder();
		holder.notebook_id = (TextView) convertView.findViewById(R.id.notebook_id);
		holder.content = (TextView) convertView.findViewById(R.id.content);
		holder.date = (TextView) convertView.findViewById(R.id.date);
		holder.delete = (Button) convertView.findViewById(R.id.delete);
		
		NoteBook noteBook = list.get(position);
		holder.notebook_id.setText(noteBook.getId());
		holder.content.setText(noteBook.getContent());
		holder.date.setText(noteBook.getDate());
		
		holder.delete.setTag(noteBook.getId());
		holder.delete.setOnClickListener(onClickListener);
		
		return convertView;
	}

	class ViewHolder {
		TextView notebook_id;
		TextView content;
		TextView date;
		Button delete;
	}
}
