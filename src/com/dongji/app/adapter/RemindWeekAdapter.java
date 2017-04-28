package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.R;
import com.dongji.app.entity.RemindBean;
import com.dongji.app.entity.RemindWeekBean;
import com.dongji.app.tool.TimeTool;

public class RemindWeekAdapter extends BaseAdapter {

	Context c ;
	
	List<RemindWeekBean> rwbs = new ArrayList<RemindWeekBean>();

	OnClickListener onClickListener;

	public RemindWeekAdapter(Context c, List<RemindWeekBean> rwbs,
			OnClickListener onClickListener) {
		this.c = c;
		this.rwbs = rwbs;
		this.onClickListener = onClickListener;
	}

	
	@Override
	public int getCount() {
		return rwbs.size();
	}

	
	@Override
	public Object getItem(int position) {
		return null;
	}

	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = new ViewHolder();
		convertView = LayoutInflater.from(c).inflate(R.layout.remind_week_item_list_item, null);
		holder.tv_week_day = (TextView)convertView.findViewById(R.id.tv_week_day);
		holder.ln_item_contanier = (LinearLayout)convertView.findViewById(R.id.ln_item_contanier);
		
		RemindWeekBean rwb = rwbs.get(position);
		
		
		holder.tv_week_day.setText(rwb.getWeek_day());
		
		List<RemindBean> rbs = rwb.getRbs();
		for(int i =0;i<rbs.size();i++)
		{
			RemindBean rb = rbs.get(i);
			View v  = LayoutInflater.from(c).inflate(R.layout.remind_week_item_list_item_item, null);
			
			LinearLayout l = ((LinearLayout)v.findViewById(R.id.item));
			l.setTag(rb.getId()+":"+rb.getTemp_start_time());
			l.setOnClickListener(onClickListener);
			
			((TextView)v.findViewById(R.id.tv_date)).setText(TimeTool.getTimeStrYYMMDDHHMM(rb.getTemp_start_time())+"至" + TimeTool.getTimeStrYYMMDDHHMM(rb.getTemp_end_time()));
			((TextView)v.findViewById(R.id.tv_thing)).setText(rb.getContent());
			
			holder.ln_item_contanier.addView(v);
			
			if(i!=rbs.size()-1)
			{
				TextView tv = new TextView(c);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.height = 1;
				tv.setLayoutParams(lp);
				tv.setBackgroundResource(R.color.remind_line_color);
				holder.ln_item_contanier.addView(tv);
			}
		}
		
		if(rbs.size()==0)
		{
             View v  = LayoutInflater.from(c).inflate(R.layout.remind_week_item_list_item_item, null);
			
			LinearLayout l = ((LinearLayout)v.findViewById(R.id.item));
			l.setTag(position);
//			l.setOnClickListener(onClickListener);
			l.setClickable(false);
			l.setFocusable(true);
			
			
			((TextView)v.findViewById(R.id.tv_date)).setText("");
			((TextView)v.findViewById(R.id.tv_thing)).setText("");
			
			holder.ln_item_contanier.addView(v);
		}
		
		
		return convertView;
	}
	
	class ViewHolder {
		TextView tv_week_day;
		LinearLayout ln_item_contanier;
		
	}

}
