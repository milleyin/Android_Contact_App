package com.dongji.app.ui;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.dongji.app.addressbook.EditWidgetContact;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.WidgetContact;
import com.dongji.app.sqllite.ContactLauncherDBHelper;

public class widgetProvider extends AppWidgetProvider {
	private static final String CLICK_NAME_ACTION = "com.dongji.app.ui.widget.click";
	private static final String EDIT_CONTACT = "com.dongji.app.ui.widget.editcontact";

	private static final String NUMBER ="number";
	
	private static RemoteViews rv;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		System.out.println("===========onUpdate==============");

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		if (rv == null) {
			rv = new RemoteViews(context.getPackageName(),R.layout.contactlauncher);
		}
		
		if (intent.getAction().equals(CLICK_NAME_ACTION)) {

			String number  = intent.getStringExtra(NUMBER);
			
			System.out.println(" number  ---> " + number);
			Uri uri = intent.getData();
			Intent num = new Intent(Intent.ACTION_CALL, uri);
			num.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(num);
			
		} else if(intent.getAction().equals("com.dongji.app.ui.appwidget.refresh")){
			
			AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
			int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(context, widgetProvider.class));
			
			for (int i = 0; i < appIds.length; i++) {
				int appWidgetId = appIds[i];
				updateAppWidget(context, appWidgetManger, appWidgetId);
			}
		} else if (intent.getAction().equals(EDIT_CONTACT)) {
			
			Intent edit_intent = new Intent(context, EditWidgetContact.class);
			edit_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(edit_intent);
			
		}
		
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgeManger, int appWidgetId) {

		rv = new RemoteViews(context.getPackageName(), R.layout.contactlauncher);

		try {
			rv.removeAllViews(R.id.vertical_layout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ContactLauncherDBHelper contactLauncher = new ContactLauncherDBHelper(context);

		SQLiteDatabase db = contactLauncher.getReadableDatabase();

		Cursor cr = db.query(contactLauncher.table, null, null, null, null,null, null);
	
		List<WidgetContact> wcs = new ArrayList<WidgetContact>();
		
		if (cr.moveToFirst()) {

			do {
				
				WidgetContact w = new WidgetContact();
				
				String number = cr.getString(cr.getColumnIndex(contactLauncher.NUMBER));
				String name = cr.getString(cr.getColumnIndex(contactLauncher.CONTACTNAME));
				
				if ( name == null)
					w.setName(number);
				
				w.setNumber(number);
				w.setName(name);
				
				byte[] photoicon = cr.getBlob(cr.getColumnIndex(contactLauncher.PHOTO));
				if(photoicon != null){
					w.setPhotoicon(photoicon);
				}
				
				wcs.add(w);

			} while (cr.moveToNext());
	
		}
		db.close();
		cr.close();

		RemoteViews curRemoteView = new RemoteViews(context.getPackageName(), R.layout.contact_widget_line_layout);
		rv.addView(R.id.vertical_layout, curRemoteView);
		
		Intent click = new Intent(EDIT_CONTACT);
		PendingIntent edit = PendingIntent.getBroadcast(context, 0, click, 0);
		curRemoteView.setOnClickPendingIntent(R.id.edit_contact, edit);
		
		
		int count = 0;
		
		System.out.println(" wcs.size()  --->" + wcs.size());
		
		for(int i =0;i<wcs.size();i++)
		{
			final WidgetContact w = wcs.get(i);
			
			System.out.println(" w.getName() --->" + w.getName());
			
			if(count<3)
			{
				RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.launcherimg);
				views.setTextViewText(R.id.tvname, w.getName());
				
				byte[] photoicon = w.getPhotoicon();
				if(photoicon != null){
					w.setPhotoicon(photoicon);
					
					ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
					Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
					if (contactPhoto == null)
						views.setImageViewResource(R.id.img, R.drawable.default_contact);
					else
						views.setImageViewBitmap(R.id.img, contactPhoto);
				}else{
					views.setImageViewResource(R.id.img, R.drawable.default_contact);
				}
				
				System.out.println(" w.getNumber() --->" + w.getNumber());
				
				Uri uri = Uri.parse("tel:" + w.getNumber());
				final Intent intentClick = new Intent(CLICK_NAME_ACTION, uri);

				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,intentClick, 0);
				
				views.setOnClickPendingIntent(R.id.img, pendingIntent);
				
				curRemoteView.addView(R.id.ln, views);
				
				count ++;
				System.out.println(" add  item  --->" + i + "    " + count);
				
			}else
			{
				count =1;
				curRemoteView = new RemoteViews(context.getPackageName(), R.layout.contact_widget_line_layout);
				rv.addView(R.id.vertical_layout, curRemoteView);
				
				curRemoteView.setViewVisibility(R.id.title_ly, View.GONE);
				
				RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.launcherimg);
				views.setTextViewText(R.id.tvname, w.getName());
				
				byte[] photoicon = w.getPhotoicon();
				if(photoicon != null){
					w.setPhotoicon(photoicon);
					
					ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
					Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
					if (contactPhoto == null)
						views.setImageViewResource(R.id.img, R.drawable.default_contact);
					else
						views.setImageViewBitmap(R.id.img, contactPhoto);
				}else{
					views.setImageViewResource(R.id.img, R.drawable.default_contact);
				}
				
				
				Uri uri = Uri.parse("tel:" + w.getNumber());
				final Intent intentClick = new Intent(CLICK_NAME_ACTION, uri);

				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,intentClick, 0);
				
				views.setOnClickPendingIntent(R.id.img, pendingIntent);
				
				curRemoteView.addView(R.id.ln, views);
				
				System.out.println(" add  line  --->" + i + "    " + count);
			}
		}
		
		appWidgeManger.updateAppWidget(appWidgetId, rv);
	}

}