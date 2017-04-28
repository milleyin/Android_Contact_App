package com.google.android.mms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.mms.Phone;
import com.google.android.mms.Telephony;

public class APNHelper {
	
	private Context context;
	
public class APN {
    public String MMSCenterUrl = "";
    public String MMSPort = "";
    public String MMSProxy = ""; 
}


public APNHelper(final Context context) {
    this.context = context;
}   

public List<APN> getMMSApns() {     
    final Cursor apnCursor = this.context.getContentResolver().query(Uri.withAppendedPath(Telephony.Carriers.CONTENT_URI, "current"), null, null, null, null);
if ( apnCursor == null ) {
        return Collections.EMPTY_LIST;
    } else {
        final List<APN> results = new ArrayList<APN>(); 
            if ( apnCursor.moveToFirst() ) {
        do {
            final String type = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.TYPE));
            if ( !TextUtils.isEmpty(type) && ( type.equalsIgnoreCase(Phone.APN_TYPE_ALL) || type.equalsIgnoreCase(Phone.APN_TYPE_MMS) ) ) {
                final String mmsc = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSC));
                final String mmsProxy = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSPROXY));
                final String port = apnCursor.getString(apnCursor.getColumnIndex(Telephony.Carriers.MMSPORT));                  
                final APN apn = new APN();
                apn.MMSCenterUrl = mmsc;
                apn.MMSProxy = mmsProxy;
                apn.MMSPort = port;
                results.add(apn);
            }
        } while ( apnCursor.moveToNext() ); 
             }              
        apnCursor.close();
        return results;
    }
}

}
