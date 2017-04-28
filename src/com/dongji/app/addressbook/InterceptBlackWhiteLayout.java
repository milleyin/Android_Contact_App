package com.dongji.app.addressbook;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.BlackAdapter;
import com.dongji.app.adapter.WhiteAdapter;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.tool.AddBlackWhite;
import com.dongji.app.ui.MyDialog;

public class InterceptBlackWhiteLayout implements OnClickListener {
	
	Context context;
	
	public View view;
	
	int index = 1;
	Button btn_switch_to_blacklist;
	Button btn_switch_to_whitelist;
	
	Button btn_add_in;

	ListView lv;
	
	LinearLayout tips_add_layout;
	LinearLayout tips_top_layout;
	
	TextView tv_add_tips;
	TextView add_type;
//	TextView delete_type;
//	TextView delete_id;
	TextView tv_delete_tips;
	EditText et_telephone;
	
	Button btn_add_tips_yes;
	Button btn_add_tips_no;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
//	MyDatabaseUtil myDatabaseUtil;
	AddBlackWhite addBlackWhite;
	
	BlackAdapter blackAdapter;
	WhiteAdapter whiteAdapter;
	
	MyDialog myDialog ;
	Dialog dialog;
	
	String type = null;
	
	String contact_id = null;
	String type1 = null;
	String name = null;
	String number = null;
	
	
	
	public InterceptBlackWhiteLayout(Context context){
		
		this.context = context;
		
		view  = LayoutInflater.from(context).inflate(R.layout.intercepte_blackwhitelist, null);
		
		addBlackWhite = new AddBlackWhite(context);
		
//		myDatabaseUtil  = DButil.getInstance(mainActivity);
		
//		black_add = (Button) view.findViewById(R.id.black_add);
//		white_add = (Button) view.findViewById(R.id.white_add);
//		
//		lvblack = (ListView) view.findViewById(R.id.lvblack);
//		lvwhite = (ListView) view.findViewById(R.id.lvwhite);
		
		lv = (ListView)view.findViewById(R.id.lv);
		
		btn_switch_to_blacklist = (Button) view.findViewById(R.id.btn_switch_to_blacklist);
		btn_switch_to_blacklist.setOnClickListener(this);
		btn_switch_to_whitelist = (Button) view.findViewById(R.id.btn_switch_to_whitelist);
		btn_switch_to_whitelist.setOnClickListener(this);
		
		btn_add_in = (Button) view.findViewById(R.id.btn_add_in);
		btn_add_in.setOnClickListener(this);
		
		tips_add_layout = (LinearLayout) view.findViewById(R.id.tips_add_layout);
		tips_top_layout = (LinearLayout) view.findViewById(R.id.tips_top_layout);
		
		tv_add_tips = (TextView) view.findViewById(R.id.tv_add_tips);
		et_telephone = (EditText) view.findViewById(R.id.et_telephone);
		et_telephone.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		btn_add_tips_yes = (Button) view.findViewById(R.id.btn_add_tips_yes);
		btn_add_tips_no = (Button) view.findViewById(R.id.btn_add_tips_no);
		
		btn_top_tips_yes = (Button) view.findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_no = (Button) view.findViewById(R.id.btn_top_tips_no);
		
		add_type = (TextView) view.findViewById(R.id.add_type);
//		delete_type = (TextView) view.findViewById(R.id.delete_type);
//		delete_id = (TextView) view.findViewById(R.id.delete_id);
		tv_delete_tips = (TextView) view.findViewById(R.id.tv_delete_tips);
		
//		black_add.setOnClickListener(this);
//		white_add.setOnClickListener(this);
		btn_add_tips_yes.setOnClickListener(this);
		btn_add_tips_no.setOnClickListener(this);
		btn_top_tips_yes.setOnClickListener(this);
		btn_top_tips_no.setOnClickListener(this);
		
		loadBlackList();
	}
	
	public void loadBlackList(){
		
		btn_switch_to_blacklist.setBackgroundResource(R.drawable.black_white_top_left_selected);
		btn_switch_to_whitelist.setBackgroundResource(R.drawable.black_white_top_right_normal);
		index =1;
		
//		List<ContactBean> blackList = new ArrayList<ContactBean>();
//		Cursor black_cursor = myDatabaseUtil.queryAllBlack();
//		if(black_cursor.moveToFirst()){
//			do{
//				ContactBean bean = new ContactBean();
//				
////				bean.setContact_id(black_cursor.getLong(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_ID)));
////				bean.setNick(black_cursor.getString(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NAME)));
////				bean.setNumber(black_cursor.getString(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NUMBER)));
////				bean.setPhoto(black_cursor.getBlob(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_PHOTO)));
//				
//				long id = black_cursor.getLong(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_ID));
//				String number = black_cursor.getString(black_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NUMBER));
//				
//				if (id != 0) {
//					
//					bean = getContactInfo(id,number);
//					
//				} else {
//					
//					String[] data = PhoneNumberTool.getContactInfo(mainActivity, number);
//					
//					String name = "";
//					String photo_id = null;
//					long contact_id = -1;
//					if(data[0] != null) {
//						name = data[0];
//					}
//					if(data[1] != null) {
//						photo_id = data[1];
//					}
//					if(data[2] !=null)
//					{
//						contact_id = Integer.valueOf(data[2]);
//					}
//					bean.setNick(name);
//					bean.setPhoto_id(photo_id);
//					bean.setContact_id(contact_id);
//					bean.setNumber(number);
//				}
//				
//				blackList.add(bean);
//			}while(black_cursor.moveToNext());
//		}
//		black_cursor.close();
		
		List<ContactBean> blackList = addBlackWhite.queryAllBlack();
		blackAdapter = new BlackAdapter(context, blackList, new OnMenuItemClickListener());
		lv.setAdapter(blackAdapter);
	}
	
	private void loadWhiteData(){
//		List<ContactBean> whiteList = new ArrayList<ContactBean>();
//		Cursor white_cursor = myDatabaseUtil.queryAllWhite();
//		if(white_cursor.moveToFirst()){
//			do{
//				ContactBean bean = new ContactBean();
//				
////				bean.setContact_id(white_cursor.getLong(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_ID)));
////				bean.setNick(white_cursor.getString(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NAME)));
////				bean.setNumber(white_cursor.getString(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NUMBER)));
////				bean.setPhoto(white_cursor.getBlob(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_PHOTO)));
//				
//				long id = white_cursor.getLong(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_ID));
//				String number = white_cursor.getString(white_cursor.getColumnIndex(myDatabaseUtil.CONTACT_NUMBER));
//				
//				if (id != 0) {
//					
//					bean = getContactInfo(id,number);
//					
//				} else {
//					
//					String[] data = PhoneNumberTool.getContactInfo(mainActivity, number);
//					String name = "";
//					String photo_id = null;
//					long contact_id = -1;
//					if(data[0] != null) {
//						name = data[0];
//					}
//					if(data[1] != null) {
//						photo_id = data[1];
//					}
//					if(data[2] !=null)
//					{
//						contact_id = Integer.valueOf(data[2]);
//					}
//					bean.setNick(name);
//					bean.setPhoto_id(photo_id);
//					bean.setContact_id(contact_id);
//					bean.setNumber(number);
//				}
//				
//				WhiteList.add(bean);
//			}while(white_cursor.moveToNext());
//		}
//		white_cursor.close();
		
		List<ContactBean> whiteList = addBlackWhite.queryAllWhite();
		
		whiteAdapter = new WhiteAdapter(context, whiteList, new OnMenuItemClickListener());
		lv.setAdapter(whiteAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btn_switch_to_blacklist:
			if(index!=1)
			{
				btn_switch_to_blacklist.setBackgroundResource(R.drawable.black_white_top_left_selected);
				btn_switch_to_whitelist.setBackgroundResource(R.drawable.black_white_top_right_normal);
				loadBlackList();
				index =1;
			}
			break;
			
		case R.id.btn_switch_to_whitelist:
			if(index!=2)
			{
				btn_switch_to_blacklist.setBackgroundResource(R.drawable.black_white_top_left_normal);
				btn_switch_to_whitelist.setBackgroundResource(R.drawable.black_white_top_right_selected);
				loadWhiteData();
				index =2;
			}
			break;
		
			case R.id.btn_add_in:
				
				String tips = null;
					if(index==1)
					{

						type = "1";
						tips = "添加号码到黑名单";
						
					} else {
						
						type = "2";
						tips = "添加号码到白名单";
					}
					
					dialog = new Dialog(context,R.style.theme_myDialog_activity);
					dialog.setContentView(R.layout.add_info_dialog);
					dialog.setCanceledOnTouchOutside(true);
					dialog.show();
					
					TextView tv_top_tips = (TextView) dialog.findViewById(R.id.tv_top_tips);
					et_telephone = (EditText) dialog.findViewById(R.id.content);
					btn_add_tips_yes = (Button) dialog.findViewById(R.id.btn_add_tips_yes);
					btn_add_tips_no = (Button) dialog.findViewById(R.id.btn_add_tips_no);
					
					tv_top_tips.setText(tips);
					et_telephone.setHint("请输入手机号码");
					
					btn_add_tips_yes.setOnClickListener(this);
					btn_add_tips_no.setOnClickListener(this);
				
				break;
			
			case R.id.btn_add_tips_yes:

				String keyword = et_telephone.getText().toString();
				
				if(!keyword.equals(""))
				{
//					Long id = getContactId(keyword);
					
					if(type.equals("1")){
//						saveBlack(keyword);
						int count = addBlackWhite.saveBlack(keyword);
						if (count == 0 ) {
							loadBlackList();
						} else {
							Toast.makeText(context, "黑名单中存在该联系人！", Toast.LENGTH_SHORT).show();
						}
					  //添加黑白名单数据之后，刷新联系人列表
//						if(mainActivity.contactLayout!=null)
//						{
//							mainActivity.contactLayout.refresh();
//						}
//						
//						try {
//							mainActivity.getAllContacts();
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
					    
					} else {
						
						int count = addBlackWhite.saveWhite(keyword);
						
						if (count == 0 ) {
							loadWhiteData();
						} else {
							Toast.makeText(context, "白名单中存在该联系人！", Toast.LENGTH_SHORT).show();
						}
				
					}
						dialog.cancel();
					
				}else{
					Toast.makeText(context, "请输入电话号码", Toast.LENGTH_SHORT).show();
				}
				
				
				break;
				
			case R.id.btn_add_tips_no:
				
				dialog.cancel();
				
				break;
			
			case R.id.btn_top_tips_yes:
				
				SQLiteDatabase db = null;
				if(type1.equals("1")){
					
//					if(myDatabaseUtil.deleteBlack(number))
					if(addBlackWhite.delBlack(number))
						loadBlackList();
					
				}else{
					
//					if (myDatabaseUtil.deleteWhite(number)) 
					if(addBlackWhite.delWhite(number))
						loadWhiteData();
				}
				
				
				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.up_out));
				tips_top_layout.setVisibility(View.GONE);
				
				break;
			
			case R.id.btn_top_tips_no:
				
				tips_top_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.up_out));
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
				String tag = v.getTag().toString();
				
				String[] str = tag.split(",");
				name = str[0];
				type1 = str[1];
				contact_id = str[2];
				number = str[3];
				
				
				String tips = null;
				
				String s = null;
				if (name.equals("")) {
					s = number;
				} else {
					s = name;
				}
				
				if(type1.equals("1")){
					tips = "确定将"+s+"移除黑名单？";
				}else{
					tips = "确定将"+s+"移除白名单？";
				}
				
				myDialog = new MyDialog(context, tips, new DialogOnClickListener());
				myDialog.normalDialog();
				
				break;
			default:
				break;
			}
		}
	}
	
//	private Long getContactId(String number){
//		List<ContactBean> list = new ArrayList<ContactBean>();
//		
//		Long id = null;
//		Cursor phones = mainActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
//		if (phones.moveToFirst()) {
//			do{
//				
//
//				String phonenumber = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//				Long contact_id = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//				
//				ContactBean contactBean = new ContactBean();
//				contactBean.setNumber(phonenumber);
//				contactBean.setContact_id(contact_id);
//				list.add(contactBean);
//				
//			}while(phones.moveToNext());
//		}
//		phones.close();
//		
//		for(int i=0;i<list.size();i++){
//			ContactBean contactBean = (ContactBean) list.get(i);
//			
//			String telephone = contactBean.getNumber();
////			String sub = telephone.substring((telephone.length()-number.length()), telephone.length());
//			if(telephone.equals(number))
//				id = contactBean.getContact_id();
//		}
//		return id;
//	}
	
//	private ContactBean getContactInfo(Long id,String number){
//		
//		Cursor cursor = mainActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID +"="+id, null,ContactsContract.Contacts._ID+" desc");
//		ContactBean contactBean = new ContactBean();
//		if(cursor.moveToFirst()){
//			
//			contactBean.setContact_id(id);
//			contactBean.setNick(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
//			contactBean.setPhoto_id(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
//			contactBean.setNumber(number);
			
//			String photo_id = contactBean.getPhoto_id();
//			if (photo_id != null) {
//				Cursor photo = mainActivity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
//						"ContactsContract.Data._ID = " + photo_id, null, null);
//				if (photo.moveToNext()) {
//					byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
//					if (photoicon != null)
//						contactBean.setPhoto(photoicon);
//				}
//				photo.close();
//			}
//		}
//		cursor.close();
//		return contactBean;
//	}
	
	
//	private void saveBlack(String number){
//		
//		Cursor contact = myDatabaseUtil.queryBlack(number);
//		
//		if(contact.getCount() == 0){
//			
//			Cursor white = myDatabaseUtil.queryWhite(number);
//			
//			if (white.getCount() > 0) {
//				
//				myDatabaseUtil.deleteWhite(number);
//			}
//			
//			white.close();
//			
//			myDatabaseUtil.insertBlack(number);
//			
//								
//		} else {
//			
//			Toast.makeText(mainActivity, "黑名单中存在该联系人！", Toast.LENGTH_SHORT).show();
//		}
//		contact.close();
//			
//	}
	
//	private void saveWhite(String number){
//		
//		Cursor contact = myDatabaseUtil.queryWhite(number);
//		
//		if( contact.getCount() == 0){
//			
//			Cursor black = myDatabaseUtil.queryBlack(number);
//			
//			if (black.getCount() > 0) {
//				
//				myDatabaseUtil.deleteBlack(number);
//				
//			}
//			
//			black.close();
//		
//			myDatabaseUtil.insertWhite(number);
//			
//		}  else {
//			
//			Toast.makeText(mainActivity, "白名单中存在该联系人！", Toast.LENGTH_SHORT).show();
//		}
//		
//		contact.close();
//			
//	}
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_top_tips_yes:
				
				if(type1.equals("1")){
					
//					myDatabaseUtil.deleteBlack(number);
					addBlackWhite.delBlack(number);
					
					loadBlackList();

					
					//删除黑名单数据之后，刷新联系人列表
//					if(context.contactLayout!=null)
//					{
//						context.contactLayout.refresh();
//					}
//					
//					try {
//						context.getAllContacts();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					
				}else{
					
//					myDatabaseUtil.deleteWhite(number);
					
					addBlackWhite.delWhite(number);
					
					loadWhiteData();
				}
				
				myDialog.closeDialog();
				break;

			default:
				break;
			}
		}
		
	}
	
}
