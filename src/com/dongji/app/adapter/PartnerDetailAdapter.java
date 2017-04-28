package com.dongji.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dongji.app.addressbook.AddContactActivity;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.RemindsActivity;

public class PartnerDetailAdapter extends BaseAdapter {

	AddContactActivity addContactLayout;
	Context context ;
	String name;
	String[] numbers;
	RemindsActivity remindLayout;
	
	int contact_id;
	
	public PartnerDetailAdapter(AddContactActivity addContactLayout,Context context,int contact_id, String name,String[] numbers) {
		this.addContactLayout = addContactLayout;
		this.context = context;
		this.name = name;
		this.numbers = numbers;
		this.contact_id  = contact_id;
	}
	
	public PartnerDetailAdapter(RemindsActivity remindLayout,Context context,int contact_id, String name,String[] numbers) {
		
		this.remindLayout = remindLayout;
		this.context = context;
		this.name = name;
		this.numbers = numbers;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return numbers.length;
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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.partner_detail_list_item, null);
		
		TextView tv_head = (TextView)convertView.findViewById(R.id.tv_head);
		tv_head.setText("电话"+(position+1));
		
		TextView tv_number = (TextView)convertView.findViewById(R.id.tv_number);
		tv_number.setText(numbers[position]);
		
		
		Button btn_call = (Button)convertView.findViewById(R.id.btn_call);
		btn_call.setTag(numbers[position]);
		btn_call.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String number = (String) v.getTag();
				
				if(remindLayout != null){
					
					remindLayout.trigglerPhoneCall(number);
					
				}else{
					addContactLayout.trigglerPhoneCall(number);
				}
			}
		});
		
		
		Button btn_send_sms = (Button)convertView.findViewById(R.id.btn_send_sms);
		btn_send_sms.setTag(numbers[position]+":"+name);
		btn_send_sms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String s = (String) v.getTag();
				String [] ss = s.split(":");
				if(remindLayout != null){
					
					remindLayout.trigglerSms(ss[0],ss[1]);
					
				}else{
					addContactLayout.trigglerSms(ss[0],ss[1]);
				}
			}
		});
		
		//机主不显示打电话 发短信
		if(contact_id == MainActivity.MY_CONTACT_ID)
		{
			btn_send_sms.setVisibility(View.GONE);
			btn_call.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
