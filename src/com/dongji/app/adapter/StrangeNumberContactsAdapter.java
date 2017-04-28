package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;

public class StrangeNumberContactsAdapter extends BaseAdapter {

	Context context;
//	private String[] mNicks;
	
	OnClickListener onClickListener;
	
	List<ContactBean> contacts = new ArrayList<ContactBean>();
	
	public StrangeNumberContactsAdapter(Context context,List<ContactBean> contacts,OnClickListener onClickListener)
	{
		this.context = context;
		this.contacts = contacts;
		this.onClickListener = onClickListener;
//		getCallerName();
	}
	
//	private void getCallerName() {
//		String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
//		Cursor c = mainActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
//				new String[] { ContactsContract.Contacts.DISPLAY_NAME }, null,null, sortOrder);
//		String[] nicks = new String[c.getCount()];
//		if (c.moveToFirst()) {
//			do {
//				String CACHED_NAME = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//				nicks[c.getPosition()] = CACHED_NAME;
//			} while (c.moveToNext());
//		}
//		
//		mNicks = nicks;
//		c.close();
//	}
	
	@Override
	public int getCount() {
		return contacts.size();
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
		
		ViewHolder holder;
		if(convertView==null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.strange_number_contact_items, null);

			holder = new ViewHolder();

			holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
			holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
			holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.area = (TextView) convertView.findViewById(R.id.area);
			
			holder.btn_select = (Button) convertView.findViewById(R.id.btn_select);
			holder.btn_select.setOnClickListener(onClickListener);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ContactBean contactBean = contacts.get(position);
		
		Long contact_id = contactBean.getContact_id();

		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Cursor photo = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
					"ContactsContract.Data._ID = " + photo_id, null, null);
			if (photo.moveToNext()) {
				byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
				ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
				Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
				if (contactPhoto == null)
					holder.ivAvatar.setImageResource(R.drawable.default_contact);
				else
					holder.ivAvatar.setImageBitmap(contactPhoto);
			}
			photo.close();
		} else {
			holder.ivAvatar.setImageResource(R.drawable.default_contact);
		}
		
//		Cursor phones = mainActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//				        new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
//						ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contact_id, null, null);
//		if (phones.moveToNext()) {
//			String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//			contactBean.setNumber(phone);
//			
//		}else{
//			holder.number.setText("");
//		}
//		phones.close();
		
		holder.number.setText(contactBean.getNumber());
		
	
		
		holder.btn_select.setTag(String.valueOf(contactBean.getContact_id()) + ":" + contactBean.getNick()+":" + contactBean.getNumber());
		
		String nick = contactBean.getNick();
		
//		String nickName = mNicks[position];
//		String catalog = converterToFirstSpell(nickName).substring(0, 1);
//		if (position == 0) {
//			holder.tvCatalog.setText(catalog);
//		} else {
//			String lastCatalog = converterToFirstSpell(mNicks[position - 1]).substring(0, 1);
//			if (catalog.equals(lastCatalog)) {
//			} else {
//				holder.tvCatalog.setText(catalog);
//			}
//		}
		holder.tvNick.setText(nick);
		
		holder.area.setText(MainActivity.CheckNumberArea(holder.number.getText().toString()));
		
	
		return convertView;
	}

//	@Override
//	public int getPositionForSection(int section) {
//		for (int i = 0; i < mNicks.length; i++) {
//			String l = converterToFirstSpell(mNicks[i]).substring(0, 1);
//			char firstChar = l.toUpperCase().charAt(0);
//			if (firstChar == section) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//	@Override
//	public int getSectionForPosition(int position) {
//		return 0;
//	}
//
//	@Override
//	public Object[] getSections() {
//		return null;
//	}

	
	class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView number;
		TextView area;
		
		Button btn_select;
	}
	
	
	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				}
//				catch (BadHanyuPinyinOutputFormatCombination e) {
//					e.printStackTrace();
//				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}
}
