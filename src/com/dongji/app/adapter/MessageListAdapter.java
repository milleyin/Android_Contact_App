package com.dongji.app.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dongji.app.addressbook.NewMessageActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.MessageLibrary;

public class MessageListAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<MessageLibrary> libraries;
	LayoutInflater aInflater;
	String selectMessage="";
	
	CheckBox checkB;
	int checkPosition = -1;
    public MessageListAdapter(Context context,ArrayList<MessageLibrary> libraries)
    {
    	this.context=context;
    	this.libraries=libraries;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return libraries.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return libraries.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView=aInflater.from(context).inflate(R.layout.message_item, null);
		
		final CheckBox checkBox=(CheckBox)convertView.findViewById(R.id.message_checkbox);
		TextView textView=(TextView)convertView.findViewById(R.id.message_context);
		
		
		
		MessageLibrary mLibrary=libraries.get(position);
		final String msg = mLibrary.getMessage_context();
		
		if(mLibrary!=null)
		{
			textView.setText(mLibrary.getMessage_context());
		}
		
		if(checkPosition==position)
		{
			checkBox.setChecked(true);
		}
		
		checkBox.setTag(position);
		checkBox.setOnTouchListener( new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int position = (Integer)checkBox.getTag();
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					if(v!=checkB)
					{
						if(checkB!=null)
						{
							
							checkB.setChecked(false);
						}
						
						checkPosition = position ;
						NewMessageActivity.selectMessage = msg;
						 checkB = checkBox;
						checkBox.setChecked(true);
					}else
					{
						if(checkB!=null)
						{
							checkBox.setChecked(false);
							NewMessageActivity.selectMessage="";
						}
						else
						{
							checkPosition = position ;
							NewMessageActivity.selectMessage = msg;
							 checkB = checkBox;
							checkBox.setChecked(true);
						}
					}
					break;

				default:
					break;
				}
				
				
				
				
				return true;
			}
		} );
		
		return convertView;
	}
	
//     public String getCheckedMessage()
//     {
//    	 return selectMessage;
//     }
     
     public String setCheck(View v,int position)
     {
    	 CheckBox checkBox = (CheckBox)v.findViewById(R.id.message_checkbox);
    	 
    	 MessageLibrary mLibrary=libraries.get(position);
    	 
    	 if(position!=checkPosition)
    	 {
    		 checkBox.setChecked(true);
    		 selectMessage=mLibrary.getMessage_context();
				checkPosition = position ;
				
				if(checkB!=null)
				{
					checkB.setChecked(false);
				}
				
				checkB = checkBox;
    	 }
    	 return selectMessage;
     }
}
