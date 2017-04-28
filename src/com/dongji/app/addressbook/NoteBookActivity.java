package com.dongji.app.addressbook;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.NoteBookAdapter;
import com.dongji.app.adapter.NoteBookContentProvider;
import com.dongji.app.adapter.NoteBookDBHepler;
import com.dongji.app.addressbook.ContactLayout.OnMenuItemClickListener;
import com.dongji.app.entity.NoteBook;
import com.dongji.app.ui.MyDialog;

/**
 * 
 * 设置里的  记事本
 * 
 * @author Administrator
 *
 */
public class NoteBookActivity extends Activity implements OnClickListener {

	public View view;

	EditText content_et;
	ListView noteBooklv;
	LinearLayout noteBookDelete;
	LinearLayout add_notebook;
	

	NoteBookAdapter noteBookAdapter;

	String notebook_id = "0";

	MyDialog myDialog;
	
	String tagId = null;
	
	Dialog dialog = null;
	EditText et_keyword_content;
	Button btn_add_tips_yes;
	Button btn_add_tips_no;
	
	int type = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = LayoutInflater.from(this).inflate(R.layout.setting_item_3_notepad, null);
		
		setContentView(view);

		noteBookDelete = (LinearLayout) view.findViewById(R.id.notebook_delete);
		noteBookDelete.setOnClickListener(this);
		content_et = (EditText) view.findViewById(R.id.content);
		noteBooklv = (ListView) view.findViewById(R.id.notebook_lv);
		add_notebook = (LinearLayout) view.findViewById(R.id.add_notebook);

		add_notebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				type = 0;
				
				dialog = new Dialog(NoteBookActivity.this,R.style.theme_myDialog_activity);
				dialog.setContentView(R.layout.add_notebook_dialog);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				
				et_keyword_content = (EditText) dialog.findViewById(R.id.content);
				btn_add_tips_yes = (Button) dialog.findViewById(R.id.btn_add_tips_yes);
				btn_add_tips_no = (Button) dialog.findViewById(R.id.btn_add_tips_no);
				
				
				btn_add_tips_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String content = et_keyword_content.getText().toString();
						
						if (!content.equals("")) {
						
							if (type == 0) {
							
								addMessage(content);
								loadData();
							}
							
							dialog.cancel();
							
						} else {
							
							Toast.makeText(NoteBookActivity.this, "记事本内容不能为空！", Toast.LENGTH_SHORT).show();
							return;
							
						}
						
					}
				});
				
				btn_add_tips_no.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				
			}
		});
		
		noteBooklv.requestFocus();
		noteBooklv.setFocusable(true);
		noteBooklv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
			
				TextView n_id = (TextView) view.findViewById(R.id.notebook_id);
				TextView content = (TextView) view.findViewById(R.id.content);
				
				final String str = content.getText().toString();
				notebook_id = n_id.getText().toString();
		
				
				type = 1;
				
				dialog = new Dialog(NoteBookActivity.this,R.style.theme_myDialog_activity);
				dialog.setContentView(R.layout.add_notebook_dialog);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				
				TextView tv_top_tips = (TextView) dialog.findViewById(R.id.tv_top_tips);
			
				et_keyword_content = (EditText) dialog.findViewById(R.id.content);
				btn_add_tips_yes = (Button) dialog.findViewById(R.id.btn_add_tips_yes);
				btn_add_tips_no = (Button) dialog.findViewById(R.id.btn_add_tips_no);
				
				
				et_keyword_content.setText(str);
				
				btn_add_tips_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						String ed_content = et_keyword_content.getText().toString();
						
						updateMessage(ed_content, notebook_id);
						
						
						loadData();
						
						dialog.cancel();
						
					}
				});
				
				btn_add_tips_no.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				
			}
		});
		
		loadData();
		
	}
	

	public void loadData() {
		Cursor c = null;
		List<NoteBook> list = null;
		try {
			c = getContentResolver().query(
					NoteBookContentProvider.URIS, null, null, null,
					" _id desc");
			list = new ArrayList<NoteBook>();
			if (c.moveToFirst()) {
				do {
					NoteBook noteBook = new NoteBook();

					String id = c.getString(c
							.getColumnIndex(NoteBookDBHepler._ID));
					String content = c.getString(c
							.getColumnIndex(NoteBookDBHepler.CONTENT));
					String date = c.getString(c
							.getColumnIndex(NoteBookDBHepler.DATE_TIME));

					noteBook.setId(id);
					noteBook.setContent(content);
					noteBook.setDate(date);

					list.add(noteBook);
				} while (c.moveToNext());
			}
			noteBookAdapter = new NoteBookAdapter(NoteBookActivity.this, list,
					new OnMenuItemClickListener());
			noteBooklv.setAdapter(noteBookAdapter);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			c.close();
		}
	}

	private void addMessage(final String content) {
		AsyncTask<String, Object, Uri> task = new AsyncTask<String, Object, Uri>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Uri doInBackground(String... params) {
				ContentValues values = new ContentValues();
				values.put(NoteBookDBHepler.CONTENT, content);

				SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
				values.put(NoteBookDBHepler.DATE_TIME, df.format(new Date()));

				Uri uri = null;
				try {
					uri = getContentResolver().insert(
							NoteBookContentProvider.URIS, values);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return uri;
			}

			@Override
			protected void onPostExecute(Uri uri) {
				loadData();
				noteBooklv.requestFocus();
				noteBooklv.setFocusable(true);
			}
		};
		task.execute();
	}

	private void updateMessage(final String content, final String id) {
		AsyncTask<String, Object, Integer> task = new AsyncTask<String, Object, Integer>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Integer doInBackground(String... params) {
				ContentValues values = new ContentValues();
				values.put(NoteBookDBHepler.CONTENT, content);

				SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
				values.put(NoteBookDBHepler.DATE_TIME, df.format(new Date()));

				int count = 0;
				try {
					count = getContentResolver().update(
							NoteBookContentProvider.URIS, values,
							NoteBookDBHepler._ID + "=" + id, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return count;
			}

			@Override
			protected void onPostExecute(Integer count) {
				loadData();
				noteBooklv.requestFocus();
				noteBooklv.setFocusable(true);
			}
		};
		task.execute();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		

		case R.id.notebook_delete:
			
			myDialog = new MyDialog(NoteBookActivity.this, "确定清空记事本", new DialogOnClickListener());
			myDialog.normalDialog();
			
			break;


		case R.id.btn_add_tips_no:
			
			dialog.cancel();
			
			break;
			
		case R.id.btn_add_tips_yes:
			
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
					
					tagId = v.getTag().toString();
					
					myDialog = new MyDialog(NoteBookActivity.this, "确定删除选中记事本", new DialogOnClickListener());
					myDialog.normalDialog();
					
					break;
				default:
					break;
			}
		}
	}
	
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_top_tips_yes:
				
				int count = 0;
				if(tagId != null){
					count = getContentResolver().delete(NoteBookContentProvider.URIS, "_ID = "+tagId, null);
					tagId = null;
					content_et.setText("");
				}else{
					count = getContentResolver().delete(NoteBookContentProvider.URIS, null, null);
				}
				if(count>0)
					loadData();
				
				myDialog.closeDialog();
				
				break;

			default:
				break;
			}
			
		}
		
	}

}
