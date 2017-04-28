package com.dongji.app.entity;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.addressbook.AddContactActivity;
import com.dongji.app.addressbook.R;

public class ContactEditableItem implements OnClickListener {
	
	Context context;
	
	int type ;
	public static final int CONTACT_EDITABLE_TYPE_MOBILE = 0;  //手机
	public static final int CONTACT_EDITABLE_TYPE_HOME_PHONE = 1; //固话
	public static final int CONTACT_EDITABLE_TYPE_EMAIL = 2; //邮箱 
	public static final int CONTACT_EDITABLE_TYPE_ADDRESS = 3; //地址 
	public static final int CONTACT_EDITABLE_TYPE_WEBSITE = 4; //网页
	public static final int CONTACT_EDITABLE_TYPE_NOTE = 5; //备注
	
	long data_id = -1;
	
	LinearLayout parent;
	
	public View view;
	public TextView textView;
	public EditText editText;
	public Button btn_delete; //删除
	
	public LinearLayout ln;
	
	boolean isContentChanged = false; //内容是否被改变了
	
	AddContactActivity addContactLayout;
	
	public ContactEditableItem(Context context,AddContactActivity addContactLayout,ContactEditableBean ceb)
	{
		this.context = context;
		this.addContactLayout = addContactLayout;
		this.type = ceb.getType();
		this.data_id = ceb.getData_id();
		
		view = LayoutInflater.from(context).inflate(R.layout.contact_detail_add_item, null);
		
		ln = (LinearLayout)view.findViewById(R.id.ln);
		
		textView = (TextView)view.findViewById(R.id.tv);
		textView.setText(ceb.getTitle());
		
		editText = (EditText)view.findViewById(R.id.et);
		editText.setText(ceb.getContent());
		editText.addTextChangedListener(new MyTextWatch());
		
		btn_delete = (Button)view.findViewById(R.id.btn);
		btn_delete.setOnClickListener(this);
		
		if(type==CONTACT_EDITABLE_TYPE_MOBILE || type==CONTACT_EDITABLE_TYPE_HOME_PHONE) //固话 和手机 只能输入电话号码
		{
			editText.setInputType(InputType.TYPE_CLASS_PHONE);
		}
	}
	
	public ContactEditableItem(Context context,AddContactActivity addContactLayout,int type,String title)
	{
		this.context = context;
		this.addContactLayout = addContactLayout;
		this.type = type;
		view = LayoutInflater.from(context).inflate(R.layout.contact_detail_add_item, null);
		
		textView = (TextView)view.findViewById(R.id.tv);
		textView.setText(title);
		
		editText = (EditText)view.findViewById(R.id.et);
		editText.addTextChangedListener(new MyTextWatch());
		
		btn_delete = (Button)view.findViewById(R.id.btn);
		btn_delete.setOnClickListener(this);
		
		if(type==CONTACT_EDITABLE_TYPE_MOBILE || type==CONTACT_EDITABLE_TYPE_HOME_PHONE) //固话 和手机 只能输入电话号码
		{
			editText.setInputType(InputType.TYPE_CLASS_PHONE);
		}
	}
	
	@Override
	public void onClick(View v) {

		boolean b = true;
		
		if( (type == CONTACT_EDITABLE_TYPE_MOBILE|| type == CONTACT_EDITABLE_TYPE_MOBILE) && addContactLayout.isLastNumber()) //只有一个号码不允许删除
		{
			b = false;
		}
		
		if(b)
		{
			if(data_id!=-1)
			{
//				int i = context.getContentResolver().delete(Data.CONTENT_URI,Data._ID + "=?",new String[] { String.valueOf(data_id) });
//				if(i>0)
//				{
//					System.out.println(" 删除成功  ----> ");
//				}
				
				//不直接删除，讲date_id保存起来
				addContactLayout.addDeleteDateId(data_id);
			}
			
			addContactLayout.ceiList.remove(this);
		
		switch (type) {
			case CONTACT_EDITABLE_TYPE_MOBILE:

				addContactLayout.mobileContainer.removeView(view);
				break;

				
			case CONTACT_EDITABLE_TYPE_HOME_PHONE:
				addContactLayout.homePhoneContainer.removeView(view);
				break;
				
				
			case CONTACT_EDITABLE_TYPE_EMAIL:
				addContactLayout.emailContainer.removeView(view);
				break;
				
				
			case CONTACT_EDITABLE_TYPE_ADDRESS:
				addContactLayout.addressContainer.removeView(view);
				break;
				
				
			case CONTACT_EDITABLE_TYPE_WEBSITE:
				addContactLayout.websitesContainer.removeView(view);
				break;
				
				
			case CONTACT_EDITABLE_TYPE_NOTE:
				addContactLayout.noteContainer.removeView(view);
				break;
				

			default:
				break;
			}
		
		addContactLayout.reLayoutEditMode();
		}else{
			Toast.makeText(context, "至少保留联系人的一个号码", Toast.LENGTH_SHORT).show();
		}
	}
	
	public ContentProviderOperation getContentProviderOperation()
	{
		ContentProviderOperation.Builder builder = null ;
		
		if(editText.getText().toString().equals("") || !isContentChanged) //没有内容 或  文本未被改变过
		{
			return null;
		}
		
		switch (type) {
		case CONTACT_EDITABLE_TYPE_MOBILE:
			if (data_id == -1) { //添加
				 builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
				builder.withValue(Data.RAW_CONTACT_ID,addContactLayout.rawContactId);
				ContentValues cv = new ContentValues();
		 		cv.put(Phone.TYPE, Phone.TYPE_MOBILE);
		 		cv.put(Phone.NUMBER, editText.getText().toString());
				builder.withValues(cv);
				builder.withYieldAllowed(true);
				builder.build();
//				System.out.println(" 添加手机号码 ---> " + editText.getText().toString() +" data_id:"+ data_id+"contact id --->" + addContactLayout.contactId );
				
			} else { //更新
				builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
				builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(data_id) });	 
		 		ContentValues cv = new ContentValues();
		 		cv.put(Phone.NUMBER, editText.getText().toString());
		 		builder.withValues(cv);
		 		builder.withYieldAllowed(true);
			}

			break;

			
		case CONTACT_EDITABLE_TYPE_HOME_PHONE:
			if (data_id == -1) { //添加

				 builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
					builder.withValue(Data.RAW_CONTACT_ID,addContactLayout.rawContactId);
					ContentValues cv = new ContentValues();
			 		cv.put(Phone.TYPE, Phone.TYPE_HOME);
			 		cv.put(Phone.NUMBER, editText.getText().toString());
					builder.withValues(cv);
					builder.withYieldAllowed(true);
					builder.build();
			} else { //更新

				builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
				builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(data_id) });	
		 		ContentValues cv = new ContentValues();
		 		cv.put(Phone.NUMBER,editText.getText().toString());
		 		builder.withValues(cv);
		 		builder.withYieldAllowed(true);
			}

			break;

			
		case CONTACT_EDITABLE_TYPE_EMAIL: 
			if (data_id == -1) { //添加
				builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
				builder.withValue(Data.RAW_CONTACT_ID,addContactLayout.rawContactId);
				ContentValues cv = new ContentValues();
		 		cv.put(Email.DATA,editText.getText().toString());
		 		cv.put(Email.TYPE,Email.TYPE_WORK);
				builder.withValues(cv);
				builder.withYieldAllowed(true);
				
			} else { //更新

				builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
				builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(data_id) });	
		 		ContentValues cv = new ContentValues();
		 		cv.put(ContactsContract.CommonDataKinds.Email.DATA,editText.getText().toString());
		 		builder.withValues(cv);
		 		builder.withYieldAllowed(true);
			}

			break;

			
		case CONTACT_EDITABLE_TYPE_ADDRESS:
			if (data_id == -1) { //添加
				builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
				builder.withValue(Data.RAW_CONTACT_ID,addContactLayout.rawContactId);
				ContentValues cv = new ContentValues();
		 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET,editText.getText().toString());
		 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
				builder.withValues(cv);
				builder.withYieldAllowed(true);

			} else { //更新
				builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
				builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(data_id) });	 
		 		ContentValues cv = new ContentValues();
		 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, editText.getText().toString());
		 		builder.withValues(cv);
		 		builder.withYieldAllowed(true);
			}

			break;

			
		case CONTACT_EDITABLE_TYPE_WEBSITE:
			if (data_id == -1) { //添加

				builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Website.CONTENT_ITEM_TYPE);
				builder.withValue(Data.RAW_CONTACT_ID,addContactLayout.rawContactId);
				ContentValues cv = new ContentValues();
				cv.put(Website.URL, editText.getText().toString());
				cv.put(Website.TYPE, Website.TYPE_WORK);
				builder.withValues(cv);
				builder.withYieldAllowed(true);
			} else { //更新
				builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
				builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(data_id) });	 // 更新时的条件
				ContentValues cv = new ContentValues();
				cv.put(Website.URL, editText.getText().toString());
				builder.withValues(cv);
				builder.withYieldAllowed(true);
			}

			break;

			
		case CONTACT_EDITABLE_TYPE_NOTE:
			if (data_id == -1) { //添加

				builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Note.CONTENT_ITEM_TYPE);
				builder.withValue(Data.RAW_CONTACT_ID,addContactLayout.rawContactId);
				ContentValues cv = new ContentValues();
				cv.put(Note.NOTE, editText.getText().toString());
				builder.withValues(cv);
				builder.withYieldAllowed(true);
			} else { //更新

				builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
				builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(data_id) });	 // 更新时的条件
				ContentValues cv = new ContentValues();
				cv.put(Note.NOTE, editText.getText().toString());
				builder.withValues(cv);
				builder.withYieldAllowed(true);
				
			}

			break;

			
		default:
			break;
		}
		
		return builder.build();
	}
	
	
	public boolean isContentChanged() {
		return isContentChanged;
	}

	public void setContentChanged(boolean isContentChanged) {
		this.isContentChanged = isContentChanged;
	}



	class MyTextWatch implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			isContentChanged = true;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public EditText getEditText() {
		return editText;
	}

	public void setEditText(EditText editText) {
		this.editText = editText;
	}
	
}
