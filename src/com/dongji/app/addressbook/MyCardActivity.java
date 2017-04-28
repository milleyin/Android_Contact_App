package com.dongji.app.addressbook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongji.app.entity.ContactEditableBean;
import com.dongji.app.tool.Constellations;
import com.dongji.app.tool.PhoneNumberTool;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 
 * 机主名片
 * 
 * @author Administrator
 *
 */
public class MyCardActivity extends Activity {

	Context mainActivity;
    ContactEditableBean companyCeb = new ContactEditableBean();
	
	List<ContactEditableBean> mobiles = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> homePhones = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> emails = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> address = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> websites = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> notes = new ArrayList<ContactEditableBean>();
	
	public String contactId;
	public long rawContactId;
	
	String disPlayName ; //联系人姓名
	private long dataIdOfName;
	
	String job_str =  "无"; //职业信息
	long job_id = -1;
	
	String birthday_str = "无"; //生日信息
	long  birthdayId = -1; // 生日对应的id
	
	public static final String MY_CONTACT_ID = "my_contact_id" ; //机主的联系人id
	
	ImageView img_code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.my_card_dialog);
		
		mainActivity = this;
		contactId = getIntent().getStringExtra(MY_CONTACT_ID);
		
		img_code = (ImageView)findViewById(R.id.img_code);
		
		loadContactData();
		layoutDetail();
		show();
	}
	
	/**
	 * 
	 * 加载机主联系人的信息
	 * 
	 */
   private void loadContactData(){
		
		mobiles.clear();
		homePhones.clear();
		emails.clear();
		address.clear();
		websites.clear();
		notes.clear();
		
	     ContentResolver resolver = mainActivity.getContentResolver();
	     
	     Cursor tmpCursor = resolver.query(
					RawContacts.CONTENT_URI, new String[] { RawContacts._ID },
					RawContacts.CONTACT_ID + "=?",
					new String[] { String.valueOf(contactId) }, null);
	     
			if (tmpCursor.moveToFirst())rawContactId = tmpCursor.getLong(0);
			tmpCursor.close();
			
	     
	    //获取联系人姓名
	    String selection = RawContacts.CONTACT_ID + "=" + contactId;
	    Cursor mCursor = resolver.query(RawContactsEntity.CONTENT_URI,null, selection, null, null);
	 	int count = mCursor.getCount();
		String itemMimeType;
		mCursor.moveToFirst();
		for (int i = 0; i < count; i++) {
			itemMimeType = mCursor.getString(mCursor.getColumnIndex(RawContactsEntity.MIMETYPE));
			if (itemMimeType!=null && itemMimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
				// 先保存在data表中_id的值
				long id = mCursor.getLong(mCursor.getColumnIndex(RawContactsEntity.DATA_ID));
				dataIdOfName = id;
				disPlayName = mCursor.getString(mCursor.getColumnIndex(StructuredName.DISPLAY_NAME));
			}
			mCursor.moveToNext();
		}
		mCursor.close();
		
		//以后可能会用到
		//获取联系人头像
//		Cursor cursor = resolver.query(Contacts.CONTENT_URI,new String[] { Contacts.PHOTO_ID }, Contacts._ID + " = ? ",
//				new String[] { String.valueOf(contactId) }, null);
//		if (cursor.moveToFirst()) {// 查到了数据
//			// 如果没有头像photoId将被赋值为0,更新时注意判断photoId是否大于0
//			long photoId = cursor.getLong(0);
//			// 保存photo在data表中_id的值,更新时使用
//			dataIdOfPhoto = photoId;
//			if (photoId > 0) {
//				String[] projection = new String[] { Photo.PHOTO };
//				String photoSelection = Data._ID + " = ? ";
//				String[] selectionArgs = new String[] { String.valueOf(photoId) };
//				Cursor photoCursor = resolver.query(
//						Data.CONTENT_URI, projection, photoSelection,
//						selectionArgs, null);
//				if (photoCursor.moveToFirst()) {// 用户设置了头像
//					byte[] photo = photoCursor.getBlob(0);
//					Bitmap bitmapPhoto = BitmapFactory.decodeByteArray(photo,0, photo.length);
//					img_add_contact_photo.setImageBitmap(bitmapPhoto);
//					img_photo.setImageBitmap(bitmapPhoto);
//					cur_photo = bitmapPhoto;
//					old_photo = bitmapPhoto;
//					// 我们将头像存储在自定义的一个ContentValues对象中,在更新时使用
//					photoContentValues.put(Photo.PHOTO, photo);
//					hasPhoto = true;
//					//MyLog.i("用户设置了头像");
//				} else {// 该联系人没有头像，使用默认的图片
//					hasPhoto = false;
//					img_photo.setImageResource(R.drawable.default_contact);
//					img_add_contact_photo.setImageResource(R.drawable.default_contact);
//					//MyLog.i("用户没有设置头像,使用默认图片");
//				}
//				photoCursor.close();
//			} else {
//				// 没有头像,使用默认图片
//				img_photo.setImageResource(R.drawable.default_contact);
//				img_add_contact_photo.setImageResource(R.drawable.default_contact);
//			}
//		}
//		cursor.close();// 注意Cursor对象的关闭
		
//		System.out.println(" dataIdOfPhoto ---> " + dataIdOfPhoto);
//		System.out.println(" hasPhoto ---> " + hasPhoto);
		
		// 获取该联系人组织,, 只取第一个 有内容的公司
		Cursor organizations = resolver.query(
					Data.CONTENT_URI,new String[] { Data._ID, Organization.COMPANY,Organization.TITLE,Organization.JOB_DESCRIPTION },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Organization.CONTENT_ITEM_TYPE + "'",
					new String[] { contactId }, null);
				while (organizations.moveToNext()) {
					
					String company = organizations.getString(organizations.getColumnIndex(Organization.COMPANY));
					long data_id = organizations.getLong(organizations.getColumnIndex(Data._ID));
					
					String job = organizations.getString(organizations.getColumnIndex(Organization.JOB_DESCRIPTION));
					
					if(company!=null)//公司 组织名称
					{
						companyCeb.setContent(company);
						companyCeb.setData_id(data_id);
					}
					
					if(job!=null) //职业
					{
						job_str = job;
						job_id = data_id;
//						System.out.println("  job_id  ---> " + job_id);
					}
				} ;
		organizations.close();
		
		//获取联系人生日
		Cursor birthrdayCursor = resolver.query(
				Data.CONTENT_URI,new String[] { Data._ID, ContactsContract.CommonDataKinds.Event.START_DATE},
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "'",
				new String[] { contactId }, null);
		
			while (birthrdayCursor.moveToNext()) { //获取第一个生日
				
				String bir = birthrdayCursor.getString(birthrdayCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
				int index = bir.indexOf("-");
				birthday_str = bir.substring(index+1);//不显示年份
				birthdayId  = birthrdayCursor.getLong(birthrdayCursor.getColumnIndex(Data._ID));
				
//				System.out.println(" birthday_str ---> " + birthday_str + " birthdayId --->" + birthdayId);
				break;
			} ;
	    birthrdayCursor.close();
	    
		
		// 获得联系人的电话号码
		Cursor phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
					while (phones.moveToNext()) {
						// 遍历所有的电话号码
						String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						long data_id = phones.getLong(phones.getColumnIndex(Data._ID));
						
						
						if(phoneType==Phone.TYPE_MOBILE) //手机
						{
							ContactEditableBean ceb = new ContactEditableBean();
							ceb.setData_id(data_id);
							ceb.setTitle("手机"+ String.valueOf(mobiles.size()+1));

							ceb.setContent(PhoneNumberTool.cleanse(phoneNumber));
							ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_MOBILE);
							mobiles.add(ceb);
							
						}else{ //其他均归为固话
							ContactEditableBean ceb = new ContactEditableBean();
							ceb.setData_id(data_id);
							ceb.setTitle("固话"+String.valueOf(homePhones.size()+1));
							ceb.setContent(PhoneNumberTool.cleanse(phoneNumber));
							ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_HOME_PHONE);
							homePhones.add(ceb);
						}
					};
		phones.close();

		
		// 获取该联系人邮箱
		Cursor emailsCur = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
							+ " = " + contactId, null, null);
				while (emailsCur.moveToNext()) {
					String emailValue = emailsCur.getString(emailsCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					long data_id = emailsCur.getLong(emailsCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("邮箱"+String.valueOf(emails.size()+1));
					ceb.setContent(emailValue);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_EMAIL);
					emails.add(ceb);
					
				} ;
		emailsCur.close();
			
		
		// 获取该联系人地址  
		Cursor addressCur = resolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
				while (addressCur.moveToNext()) {
					// 遍历所有的地址
					String street = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    long data_id = addressCur.getLong(addressCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("地址"+String.valueOf(address.size()+1));
					ceb.setContent(street);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_ADDRESS);
					address.add(ceb);
				} ;
		addressCur.close();
				
		
		//获取网址信息
		Cursor websitesCur =  resolver.query(
					Data.CONTENT_URI,new String[] { Data._ID, Website.URL },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Website.CONTENT_ITEM_TYPE + "'",new String[] { contactId }, null);
			
				while (websitesCur.moveToNext())
				{
					String website = websitesCur.getString(websitesCur.getColumnIndex(Website.URL));
					long data_id = websitesCur.getLong(websitesCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("网站"+String.valueOf(websites.size()+1));
					ceb.setContent(website);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_WEBSITE);
				    websites.add(ceb);
				}
		websitesCur.close();
			
		
		// 获取备注信息
		Cursor notesCur = resolver.query(
					Data.CONTENT_URI,new String[] { Data._ID, Note.NOTE },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Note.CONTENT_ITEM_TYPE + "'",new String[] { contactId }, null);
			
			while (notesCur.moveToNext()) {
					String noteinfo = notesCur.getString(notesCur.getColumnIndex(Note.NOTE));
					
                    long data_id = notesCur.getLong(notesCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("备注"+String.valueOf(notes.size()+1));
					ceb.setContent(noteinfo);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_NOTE);
				    notes.add(ceb);
				};
	   notesCur.close();
	   
	   
	}

   
   /**
    * 
    * 显示
    * 
    */
    public void show()
    {
    	 
 	   DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int screenWidth = metric.widthPixels;
        int screenHeight = metric.heightPixels;
        
        int content_width = screenWidth-(screenWidth/5);
        int content_height = screenHeight - (screenHeight/5);
        
        System.out.println(" content_width ---> " + content_width);
        System.out.println(" content_height ---> " + content_height);
         
 		
 		LinearLayout base_ln = (LinearLayout)findViewById(R.id.base_ln);
 		LayoutParams base_lp = base_ln.getLayoutParams();
 		base_lp.width = content_width;
 		base_lp.height = content_height;
 		base_ln.setOrientation(LinearLayout.HORIZONTAL);
 		
 		LinearLayout view = (LinearLayout)findViewById(R.id.ln_content);
 		
 		Button bn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 		bn.setText(disPlayName );
 		bn.setTextSize(24f);
 		view.addView(bn);
 		
 		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
 		lp.height = 1;
 		
 		
 		if(!birthday_str.equals("无"))
 		{
 			if(Constellations.check(birthday_str)!=null)
 			{
 				Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 				btn.setText("生日： " + birthday_str + "("+Constellations.check(birthday_str)+")");
 				
 				view.addView(btn);
 			}
 		}
 		
 		
 		if(companyCeb.getContent()!=null && !companyCeb.getContent().equals(""))
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText("公司： " + companyCeb.getContent());
 			
 			view.addView(btn);
 		}
 		
 		if(!job_str.equals("无"))
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText("职业： " + job_str);
 			
 			view.addView(btn);
 		}
 		
 		//显示职业信息
// 		tv_content_job.setText(job_str);
 		
 		for(ContactEditableBean ceb:mobiles)
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText(ceb.getTitle()+": " + ceb.getContent());
 			
 			view.addView(btn);
 		}
 		
 		for(ContactEditableBean ceb:homePhones)
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText(ceb.getTitle()+": " + ceb.getContent());
 			
 			view.addView(btn);
 		}
 		
 		for(ContactEditableBean ceb:emails)
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText(ceb.getTitle()+": " + ceb.getContent());
 			
 			view.addView(btn);
 		}
 		
 		for(ContactEditableBean ceb:address)
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText(ceb.getTitle()+": " + ceb.getContent());
 			
 			view.addView(btn);
 		}
 		
 		for(ContactEditableBean ceb:websites)
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText(ceb.getTitle()+": " + ceb.getContent());
 			
 			view.addView(btn);
 		}
 		
 		for(ContactEditableBean ceb:notes)
 		{
 			Button btn = (Button)LayoutInflater.from(mainActivity).inflate(R.layout.card_btn, null);
 			btn.setText(ceb.getTitle()+": " + ceb.getContent());
 			
 			view.addView(btn);	
 		}

    }
    
    
    /**
     * 
     * 布局
     * 
     */
    private void layoutDetail()
	{
		
		final StringBuffer sf = new StringBuffer();  //VCard二维码格式  字符串
		final StringBuffer ssf = new StringBuffer();
		
		
		sf.append("BEGIN:VCARD"+"\n");
		sf.append("N:"+disPlayName+"\n");
		ssf.append("联系人:"+disPlayName+"\n");
		
		
		if(!birthday_str.equals("无"))
		{
			if(Constellations.check(birthday_str)!=null)
			{
				sf.append("BDAY:"+birthday_str+"\n");
				ssf.append("生日:"+birthday_str+"\n");
			}else{
				birthday_str="无";
			}
		}
		
		
		if(companyCeb.getContent()!=null && !companyCeb.getContent().equals(""))
		{
			sf.append("ORG:"+companyCeb.getContent()+"\n");
			ssf.append("公司:"+companyCeb.getContent()+"\n");
		}
		
		
		//显示职业信息
		View view_job = LayoutInflater.from(mainActivity).inflate(R.layout.contact_detail_item, null);
		TextView tv_job = (TextView)view_job.findViewById(R.id.tv_title);
		tv_job.setText("职业");
		TextView tv_content_job = (TextView)view_job.findViewById(R.id.tv_content);
		tv_content_job.setText(job_str);
				
		
		if(!job_str.equals("无"))
		{
			sf.append("TITLE:"+job_str+"\n");
			ssf.append("职业:"+job_str+"\n");
		}
		
		for(ContactEditableBean ceb:mobiles)
		{
			sf.append("TEL:"+ceb.getContent()+"\n");
			ssf.append("电话:"+ceb.getContent()+"\n");
		}
		
		for(ContactEditableBean ceb:homePhones)
		{
			sf.append("TEL:"+ceb.getContent()+"\n");
			ssf.append("电话:"+ceb.getContent()+"\n");
		}
		
		for(ContactEditableBean ceb:emails)
		{
			sf.append("EMAIL:"+ceb.getContent()+"\n");
			ssf.append("邮箱:"+ceb.getContent()+"\n");
		}
		
		for(ContactEditableBean ceb:address)
		{
			sf.append("ADR:"+ceb.getContent()+"\n");
			ssf.append("地址:"+ceb.getContent()+"\n");
		}
		
		for(ContactEditableBean ceb:websites)
		{
			sf.append("URL:"+ceb.getContent()+"\n");
			ssf.append("网址:"+ceb.getContent()+"\n");
		}
		
		for(ContactEditableBean ceb:notes)
		{
			sf.append("NOTE:"+ceb.getContent()+"\n");
			ssf.append("备注:"+ceb.getContent()+"\n");
		}
		
		
		
		//生成二维码图片
		
		sf.append("END:VCARD ");
//		System.out.println(" crcode str --->" + sf.toString());
		
		try {
			img_code.setBackgroundDrawable(new BitmapDrawable(create2DCode(sf.toString())));
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
    
    /**
	 * 生成二维码图片
	 * 
	 * @param str  内容  
	 * @return
	 * @throws WriterException
	 */
	public Bitmap create2DCode(String str) throws WriterException {
		
		int size = 0;
		
	
		size = mainActivity.getResources().getDimensionPixelSize(R.dimen.crcode_size_big);
		
		
        //生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE,size,size);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        
        return bitmap;
    }
}
