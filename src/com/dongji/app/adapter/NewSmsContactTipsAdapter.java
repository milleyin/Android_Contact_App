package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dongji.app.adapter.ContactsSearchAdapter.ViewHolder;
import com.dongji.app.addressbook.ContactLayout;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.EnContact;
import com.dongji.app.tool.PhoneNumberTool;

public class NewSmsContactTipsAdapter extends BaseAdapter  implements Filterable {

	Context mContext;
	private String[] mNicks;
	
	List<ContactBean> contacts = new ArrayList<ContactBean>();
	
	List<ContactBean> oldcontas;
	
	private String filterNum;
	
	ArrayList<ContactBean> result;

	LinearLayout ln_number_tips;
	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			
			contacts = result;
			
			notifyDataSetChanged();
			if(contacts.size()==0)
			{
				ln_number_tips.setVisibility(View.GONE);
			}else{
				if(ln_number_tips.getVisibility()!=View.VISIBLE)
				{
					ln_number_tips.setVisibility(View.VISIBLE);
				}
			}
		};
	};
	
	
	public NewSmsContactTipsAdapter(Context context, List<ContactBean> contacts)
	{
		this.mContext = context;
		
		List<ContactBean> cc = new ArrayList<ContactBean>();
		
		//过滤加密联系人
		if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
		{
			for(ContactBean cb:contacts)
			{
				boolean isMath = false;
			
					for(EnContact ec:MainActivity.EN_CONTACTS)
					{
//						System.out.println(" 加密的id:  " +ec.getContactId() + "   联系人的id ：  " + String.valueOf(cb.getContact_id()) );
						if(ec.getContactId().equals(String.valueOf(cb.getContact_id())))
						{
							isMath = true;
						}
					
						break;
					}
					
					if(!isMath)
					{
						cc.add(cb);
					}
				}
			this.contacts = cc;
			
		}else{
			this.contacts = contacts;
		}
		
		this.oldcontas = this.contacts;
		
//		getCallerName();
	}
	
//	private void getCallerName() {
//		String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
//		Cursor c = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,new String[] { ContactsContract.Contacts.DISPLAY_NAME }, null,null, sortOrder);
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
		ContactBean cb = contacts.get(position);
		return cb.getNick()+":"+cb.getNumber();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder holder;
		if (convertView == null) {
		holder = new ViewHolder();
		convertView = LayoutInflater.from(mContext).inflate(R.layout.search_contactitems, null);

		holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
		holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.area = (TextView) convertView.findViewById(R.id.area);
		holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder) convertView.getTag();
		}
		
		holder.progressBar.setVisibility(View.GONE);
		
		ContactBean contactBean = contacts.get(position);
		
		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Cursor photo = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
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
		
		String phone = contactBean.getNumber();
		holder.area.setText(MainActivity.CheckNumberArea(phone));
		
		if (null == filterNum) {
			holder.number.setText(phone);
		} else {
			try{
				holder.number.setText(Html.fromHtml(phone.replace(filterNum, "<font color='#3d8eba'>" + filterNum+ "</font>")));
			}catch(Exception e){
				
			}
		
		}
		
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
		
		ProgressBar progressBar;
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

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,FilterResults results) {
				contacts = (ArrayList<ContactBean>) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence s) {
				
				filterNum = s.toString();
				
				FilterResults results = new FilterResults();
				
				ArrayList<ContactBean> result = new ArrayList<ContactBean>();
				
				if (oldcontas != null && oldcontas.size() != 0) {
					
						for (int i = 0; i < oldcontas.size(); i++) {
							
							if(!isNumeric(s.toString())){
								
								if (oldcontas.get(i).getSork_key().toLowerCase().contains(s)) 
									result.add(oldcontas.get(i));
								if (oldcontas.get(i).getSork_key().toUpperCase().contains(s)) 
									result.add(oldcontas.get(i));
							}else{
								
								if(oldcontas.get(i).getNumber()!=null && oldcontas.get(i).getNumber().contains(s)){
									
									result.add(oldcontas.get(i));
								}
							}
					}
				}
				
				results.values = result;
				results.count = result.size();
				
				return results;
			}
		};
		return filter;
	}
	
	public boolean isNumeric(String str) {
	    Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
	}
	
	public void fiter(String key)
	{
		filterNum = key.toString();
		
//		FilterResults results = new FilterResults();
		
		result = new ArrayList<ContactBean>();
		
		if (oldcontas != null && oldcontas.size() != 0) {
			
				for (int i = 0; i < oldcontas.size(); i++) {
					
					if(!isNumeric(key.toString())){
						
						if (oldcontas.get(i).getSork_key().toLowerCase().contains(key)) 
							result.add(oldcontas.get(i));
						else if (oldcontas.get(i).getSork_key().toUpperCase().contains(key)) 
							result.add(oldcontas.get(i));
					}else{
						
						if(oldcontas.get(i).getNumber()!=null && oldcontas.get(i).getNumber().contains(key)){
							
							result.add(oldcontas.get(i));
						}
					}
			}
		}
		
		handler.sendEmptyMessage(0);
	}

	public void setLntips(LinearLayout ln_number_tips) {
		this.ln_number_tips = ln_number_tips;
	}
	
}
