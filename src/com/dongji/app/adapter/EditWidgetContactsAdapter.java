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
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.WidgetContact;
import com.dongji.app.tool.PhoneNumberTool;

public class EditWidgetContactsAdapter extends BaseAdapter  implements SectionIndexer {

	List<ContactBean> contacts = new ArrayList<ContactBean>();
	List<ContactBean> oldcontacts;
	
	Context context;
	
	public CheckBox cb;
	int position = -1;
	int c_id;
	
	public EditWidgetContactsAdapter(Context context,List<ContactBean> contacts)
	{
		this.context = context;
		this.contacts = contacts;
		this.oldcontacts = this.contacts;
	}
	
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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.remind_contact_items, null);

		final ViewHolder holder = new ViewHolder();

		holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
		holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.area = (TextView) convertView.findViewById(R.id.area);
		
		holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
		
		holder.cb.setVisibility(View.GONE);
		
		final int the_position  = position;
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				System.out.println(" onCheckedChanged --->" + isChecked );
				if(isChecked)
				{
					EditWidgetContactsAdapter.this.position = the_position;
					
					if(cb!=null && cb != holder.cb)
					{
						cb.setChecked(false);
					}
					
					cb = holder.cb;
					
				}else{
					if(EditWidgetContactsAdapter.this.position == the_position)
					{
						holder.cb.setChecked(true);
					}
				}
			}
		});
		
		
		if(position==this.position)
		{
			cb = holder.cb;
			cb.setChecked(true);
		}
		
//		holder.btn_select.setOnClickListener(onClickListener);
		
		ContactBean contactBean = contacts.get(position);
		
		WidgetContact wt = new WidgetContact();
		
		wt.setName(contactBean.getNick());
		
		Long contact_id = contactBean.getContact_id();

		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Cursor photo = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
					"ContactsContract.Data._ID = " + photo_id, null, null);
			if (photo.moveToNext()) {
				byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
				
				wt.setPhotoicon(photoicon);
				
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
		
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				        new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contact_id, null, null);
		if (phones.moveToNext()) {
			String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contactBean.setNumber(phone);
			wt.setNumber(PhoneNumberTool.cleanse(phone));
			holder.number.setText(phone);
		}
		phones.close();
		
		
		String nick = contactBean.getNick();
		
		holder.tvNick.setText(nick);
		
		holder.area.setText(MainActivity.CheckNumberArea(holder.number.getText().toString()));
		
		holder.cb.setTag(wt);
		
		holder.number.setTag(wt);
		
		return convertView;
	}

	@Override
	public int getPositionForSection(int section) {
		char b = '#';
		if(section == b )
		{
			return 0;
		}else{
			System.out.println("section  ---->  "+section);
			for (int i = 0; i < oldcontacts.size(); i++) {
				String l = oldcontacts.get(i).getName_pinyin_cap();
				char firstChar = l.toUpperCase().charAt(0);

				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}
		
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	
	class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView number;
		TextView area;
		
//		Button btn_select;
		CheckBox cb;
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
