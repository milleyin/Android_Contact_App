package com.dongji.app.tool;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Telephony.Mms;
import android.provider.Telephony.Mms.Addr;
import android.provider.Telephony.Mms.Part;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.mms.Phone;
import com.google.android.mms.pdu.CharacterSets;
import com.google.android.mms.pdu.EncodedStringValue;
import com.google.android.mms.pdu.PduBody;
import com.google.android.mms.pdu.PduComposer;
import com.google.android.mms.pdu.PduPart;
import com.google.android.mms.pdu.SendReq;

public class MMSSender {
	private static final String TAG = "MMSSender";
	public static String mmscUrl = "http://mmsc.monternet.com";
	// public static String mmscUrl="http://mmsc.myuni.com.cn";
	
	private static final String LIANTONG_MMSC_URL = "http://mmsc.myuni.com.cn";
	private static final String YIDONG_MMSC_URL = "http://mmsc.monternet.com";
	private static final String DIANXIN_MMSC_URL = "http://mmsc.vnet.mobi";
	
	public static String mmsProxy = "10.0.0.172";
	public static int mmsProt = 80;

	private static String HDR_VALUE_ACCEPT_LANGUAGE = "";
	
	private static final String HDR_KEY_ACCEPT = "Accept";
	private static final String HDR_KEY_ACCEPT_LANGUAGE = "Accept-Language";
	private static final String HDR_VALUE_ACCEPT = "*/*, application/vnd.wap.mms-message, application/vnd.wap.sic";
	
	private static final String SMIL_TEXT_IMAGE = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"2000ms\"><text src=\"text_0.txt\" region=\"Text\"/><img src=\"%s\" region=\"Image\"/></par></body></smil>";
    private static final String IMAGE_CID = "<img_cid>";

    private static Uri imgae_part_uri  = null;
    
	public static boolean sendMMS(Context context, long threadId , String phoneNumber, String subject, String content, String imagePath)
	{
		
		System.out.println("进入sendMMS方法");
		
		
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
		String operator = telManager.getSimOperator();
		
		System.out.println("  operator  --->" + operator);
		
		// 中国移动
		if ("46000".equals(operator) || "46002".equals(operator)
				|| "46007".equals(operator)) {
			mmscUrl = YIDONG_MMSC_URL;
		} else if ("46001".equals(operator)) { // 中国联通
			mmscUrl = LIANTONG_MMSC_URL;
		} else if ("46003".equals(operator)) { // 中国电信
			mmscUrl = DIANXIN_MMSC_URL;
		}
        
		
		//尝试打开移动网络
		try {
		
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
			
			 int result =cm.startUsingNetworkFeature( ConnectivityManager.TYPE_MOBILE, "enableMMS");
			
		        switch (result) {
		            case Phone.APN_ALREADY_ACTIVE:
		            case Phone.APN_REQUEST_STARTED:
		            	
		            	ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		                int inetAddr;
		                
		                String  proxyAddr = mmsProxy;
		                inetAddr = lookupHost(proxyAddr); // Return -938825536 for IP 192.168.10.200
		                
		                if (inetAddr == -1)
		                {
		                }
		                else
		                {
		                    int[] apnTypes = new int[] {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_MOBILE_MMS, ConnectivityManager.TYPE_MOBILE_DUN, ConnectivityManager.TYPE_MOBILE_HIPRI, ConnectivityManager.TYPE_MOBILE_SUPL};
		                    
		                    for (int i=0; i<apnTypes.length; i++)
		                    {
		                        if (connMgr.requestRouteToHost(apnTypes[i], inetAddr))
		                        {
		                        }
		                    }

		                }
		            	
		                break;
		        }
		    
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		File file=new File(imagePath);
		String imageName=file.getName();
		
		
		//先写入数据库
		long msgId = insertMMSDB(context, threadId ,phoneNumber, imageName, subject, imagePath, content);
		
		
		  final SendReq sendRequest = new SendReq();
			
		    final EncodedStringValue[] sub = EncodedStringValue.extract(subject);
		    if (sub != null && sub.length > 0) {
		        sendRequest.setSubject(sub[0]);
		    }
		    
		    final EncodedStringValue[] phoneNumbers = EncodedStringValue
		            .extract(phoneNumber);
		    
		    if (phoneNumbers != null && phoneNumbers.length > 0) {
		        sendRequest.addTo(phoneNumbers[0]);
		    }
		
		    final PduBody pduBody = new PduBody();
		
		
		  //图片附件
			PduPart img_part = new PduPart();
	        img_part.setCharset(CharacterSets.UTF_8);
	        img_part.setName("附件".getBytes());
//	        part.setContentType(("image/jpg" + getTypeFromUri(uriStr)).getBytes());// "image/png"
	        img_part.setContentType("image/jpg".getBytes());
//	        part.setDataUri(Uri.parse("file://"+imagePath));
	        img_part.setDataUri(imgae_part_uri);
//	        System.out.println(" imgae_part_uri  --->" + imgae_part_uri.toString());
	        pduBody.addPart(img_part);
	        
	        if(content!=null && !"".equals(content))
	        {
	        	 //文本内容
	            PduPart text_part = new PduPart();
	            text_part.setName("文本".getBytes());
	            text_part.setContentType("text/plain".getBytes());
	            text_part.setData(content.getBytes());
	            pduBody.addPart(text_part);
	        }
	        
		    sendRequest.setBody(pduBody);
		
		    final PduComposer composer = new PduComposer(context, sendRequest);
		
            final byte[] data = composer.make();
        
        
    	HDR_VALUE_ACCEPT_LANGUAGE = HTTP.UTF_8;
		if (mmscUrl == null) {
			throw new IllegalArgumentException("URL must not be null.");
		}

		HttpClient client = null;
		
		try {

			HttpHost httpHost = new HttpHost(mmsProxy, mmsProt);
			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
			HttpConnectionParams.setConnectionTimeout(httpParams, 15000);  //十五秒

			client = new DefaultHttpClient(httpParams);

			HttpPost post = new HttpPost(mmscUrl);
			
			// mms PUD START
			ByteArrayEntity entity = new ByteArrayEntity(data);
			entity.setContentType("application/vnd.wap.mms-message");
			
			post.setEntity(entity);
			post.addHeader(HDR_KEY_ACCEPT, HDR_VALUE_ACCEPT);
			post.addHeader(HDR_KEY_ACCEPT_LANGUAGE, HDR_VALUE_ACCEPT_LANGUAGE);
			//post.addHeader("user-agent","Mozilla/5.0(Linux;U;Android 2.1-update1;zh-cn;ZTE-C_N600/ZTE-C_N600V1.0.0B02;240*320;CTC/2.0)AppleWebkit/530.17(KHTML,like Gecko) Version/4.0 Mobile Safari/530.17");
			// mms PUD END
			
			HttpParams params = client.getParams();
			HttpProtocolParams.setContentCharset(params, "UTF-8");

			System.out.println("准备执行发送");

			// PlainSocketFactory localPlainSocketFactory =
			// PlainSocketFactory.getSocketFactory();
			
			 
			HttpResponse response = client.execute(post);
			
			System.out.println("执行发送结束， 等回执。。");

			StatusLine status = response.getStatusLine();
			Log.d(TAG, "status " + status.getStatusCode());
			if (status.getStatusCode() != 200) { // HTTP 200 表服务器成功返回网页
				Log.d(TAG, "!200");
				System.out.println("status not 200!");
				throw new IOException("HTTP error: " + status.getReasonPhrase());
			}
			
			HttpEntity resentity = response.getEntity();
			byte[] body = null;
			if (resentity != null) {
				try {
					if ( resentity.getContentLength() > 0) {
						body = new byte[(int) resentity.getContentLength()];
						DataInputStream dis = new DataInputStream(resentity
								.getContent());
						try {
							dis.readFully(body);
						} finally {
//							try {
								dis.close();
//							} catch (IOException e) {
//								Log.e(TAG, "Error closing input stream: "+ e.getMessage());
//							}
						}
					}
				} finally {
					if (entity != null) {
						entity.consumeContent();
					}
				}
			}
			Log.d(TAG, "result:" + new String(body));
			System.out.println("成功！！");
			
			sendMMSDone(context, msgId);
			
			return true;
			
		}  catch (Exception e) {
			System.out.println("Exception:" + e);
			Log.e(TAG, "", e);
			
			sendMMSFailed(context, msgId);
			
			return false;
			
		} finally {
			if (client != null) {
			}
		}
		
	}
	
	
	/**
	 * 将彩信发送信息加入数据库
	 * @param context
	 * @param phoneNumber
	 * @param imageName
	 * @param subject
	 * @param imagePath
	 * @param content
	 */
	private static long insertMMSDB(Context context, long threadId ,String phoneNumber, String imageName, String subject, String imagePath, String content) {
//    	long threadId = Threads.getOrCreateThreadId(context, phoneNumber);
    	
    	// make MMS record
        ContentValues cvMain = new ContentValues();
        cvMain.put(Mms.THREAD_ID, threadId);

        cvMain.put(Mms.MESSAGE_BOX, Mms.MESSAGE_BOX_OUTBOX);   // 彩信发送类型Mms.MESSAGE_BOX_SENT   4 发送中
        cvMain.put(Mms.READ, 1);  // 发送出去则全部为已读
        cvMain.put(Mms.DATE, System.currentTimeMillis() / 1000 );   // 发送时间同短信不同   以秒为单位
        
//        System.out.println(" 彩信 时间  --->" +  TimeTool.getTimeStrYYMMDDhhmmNoTodayInSecond((System.currentTimeMillis() / 1000)));
        
        if(subject != null && !"".equals(subject))
        {
        	 try {
        		String ss = new  String(subject.getBytes(),"ISO8859_1");
//        		System.out.println("  utf-8  subject  --->" + ss);
        		
				cvMain.put(Mms.SUBJECT, ss ); // 主题
				cvMain.put(Mms.SUBJECT_CHARSET, 106);
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}   
        }
        
//      cvMain.put(Mms.SUBJECT, subject ); // 主题
//		cvMain.put(Mms.SUBJECT_CHARSET, 106);
        
        cvMain.put(Mms.CONTENT_TYPE, "application/vnd.wap.multipart.related");
        cvMain.put(Mms.MESSAGE_CLASS, "personal");   // 彩信用途
        cvMain.put(Mms.MESSAGE_TYPE, 128); // send-req
        cvMain.put(Mms.MESSAGE_SIZE, getFileSize(imagePath) + 512);  // suppose have 512 bytes extra text size
        cvMain.put(Mms.PRIORITY, String.valueOf(129));  // 此条彩信的优先级，normal 129，low 128，high 130
        cvMain.put(Mms.READ_REPORT, String.valueOf(129));  // 此条彩信的阅读报告
        cvMain.put(Mms.DELIVERY_REPORT, String.valueOf(129));  // 此条彩信的传输报告
        Random random = new Random();
        cvMain.put(Mms.MESSAGE_ID, String.valueOf(random.nextInt(100000)));   // 彩信服务器分配的消息 id
        cvMain.put(Mms.TRANSACTION_ID, String.valueOf(random.nextInt(120000)));  // 事务标识
        
        long msgId = -1;
        try {
            msgId = ContentUris.parseId(context.getContentResolver().insert(Mms.CONTENT_URI, cvMain));
        } catch (Exception e) {
            Log.e("", "insert pdu record failed", e);
            System.out.println("87 error:"+e);
            return msgId;
        }
        
        // make parts
        String smilText=String.format(SMIL_TEXT_IMAGE, imageName);
        
        ContentValues cvSmil = createPartRecord(-1, "application/smil", "smil.xml", "<siml>", "smil.xml", null, smilText);
        cvSmil.put(Part.MSG_ID, msgId);
        
    	ContentValues cvImage = createPartRecord(0, "image/jpeg", imageName, IMAGE_CID, imageName, null, null);
    	cvImage.put(Part.MSG_ID, msgId);

    	ContentValues cvText = null;
    	if(content!=null) {
    		cvText = createPartRecord(0, "text/plain", "text_0.txt", "<text_0>", "text_0.txt", null, null);
	        cvText.put(Part.MSG_ID, msgId);
	        cvText.remove(Part.TEXT);
	        cvText.put(Part.TEXT, content);
	        cvText.put(Part.CHARSET, "106");
    	}
        
        // insert parts
        Uri partUri = Uri.parse("content://mms/" + msgId + "/part");
        try{
        	context.getContentResolver().insert(partUri, cvSmil);
        	Uri dataUri = context.getContentResolver().insert(partUri, cvImage);
//        	System.out.println(" copyImage  dataUri ---->  " + dataUri.toString());
        	if(!copyImage(context, imagePath, dataUri)) {
        		System.out.println("copyImage error");
        		return msgId;
        	}
        	
        	imgae_part_uri = dataUri;
        	
        	if(cvText!=null) {
        		context.getContentResolver().insert(partUri, cvText);
        	}
        	
        }catch(Exception e) {
        	e.printStackTrace();
        	return msgId;
        }

        // to address
        ContentValues cvAddr = new ContentValues();
        cvAddr.put(Addr.MSG_ID, msgId);
        cvAddr.put(Addr.ADDRESS, phoneNumber);
        cvAddr.put(Addr.TYPE, "151");
        cvAddr.put(Addr.CHARSET, 106);
        context.getContentResolver().insert(Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);

        // from address
        cvAddr.clear();
        cvAddr.put(Addr.MSG_ID, msgId);
        cvAddr.put(Addr.ADDRESS, "insert-address-token");
        cvAddr.put(Addr.TYPE, "137");
        cvAddr.put(Addr.CHARSET, 106);
        context.getContentResolver().insert(Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);
        
        return msgId;
    }
    
    private static long getFileSize(String path) {
    	File file=new File(path);
    	if(file.exists()) {
    		return file.length();
    	}else {
    		return 0;
    	}
    }
    
    private static boolean copyImage(Context context, String path, Uri dataUri) {
    	InputStream input = null;
        OutputStream output = null;

        try {
        	
        	BitmapFactory.Options ops = new BitmapFactory.Options();
        	ops.inJustDecodeBounds = true;
        	
        	BitmapFactory.decodeFile(path,ops);
        	

        	int sample_size = ops.outWidth / (480) ;
        	 
        	ops.inSampleSize = sample_size ;
        	ops.inJustDecodeBounds = false;
        	
            System.out.println(" ******************  path :" + path  + " sample_size  :" + sample_size );
        	
            output = context.getContentResolver().openOutputStream(dataUri);
            
            Bitmap bitmap = BitmapFactory.decodeFile(path, ops);
            
            if(path.contains("jpg"))
            {
            	 bitmap.compress(CompressFormat.JPEG, 30, output);
            }else if(path.contains("png")){
            	 bitmap.compress(CompressFormat.PNG, 30, output);
            }else {
            	 bitmap.compress(CompressFormat.JPEG, 30, output);
            }
            
            output.flush();  
            output.close();
            
            if(bitmap!=null)
            {
            	bitmap.recycle();
            }
            
            return true;
            
        } catch (FileNotFoundException e) {
            Log.e("", "failed to found file?", e);
        } catch (IOException e) {
            Log.e("", "write failed..", e);
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException e) {
                Log.e("", "close failed...");
            }
        }
    	return false;
    }
    
    private static ContentValues createPartRecord(int seq, String ct, String name, String cid, String cl, String data,
            String text) {
        ContentValues cv = new ContentValues(8);
        cv.put(Part.SEQ, seq);
        cv.put(Part.CONTENT_TYPE, ct);
        cv.put(Part.NAME, name);
        cv.put(Part.CONTENT_ID, cid);
        cv.put(Part.CONTENT_LOCATION, cl);
        if (data != null)
            cv.put(Part._DATA, data);
        if (text != null)
            cv.put(Part.TEXT, text);
        return cv;
    }
    
    public static void sendMMSDone(Context context, long msgId) {
    	ContentValues values=new ContentValues();
    	values.put(Mms.MESSAGE_BOX, Mms.MESSAGE_BOX_SENT);
		context.getContentResolver().update(Mms.CONTENT_URI, values, "_id=?",
				new String[] { String.valueOf(msgId) });
    }
    
    public static void sendMMSFailed(Context context, long msgId) {
    	ContentValues values=new ContentValues();
    	values.put(Mms.MESSAGE_BOX, Mms.MESSAGE_BOX_OUTBOX);
    	values.put(Mms.RESPONSE_STATUS,"130");
		context.getContentResolver().update(Mms.CONTENT_URI, values, "_id=?",new String[] { String.valueOf(msgId) });
    }
    
    public static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn"); 
    public static final Uri APN_LIST_URI = Uri.parse("content://telephony/carriers"); 

    
    public static int updateCurrentAPN(ContentResolver resolver, String newAPN) { 
        Cursor cursor = null; 
        try { 
            //get new apn id from list 
            cursor = resolver.query(APN_LIST_URI, null, " apn = ? and current = 1", new String[]{newAPN.toLowerCase()}, null); 
            String apnId = null; 
            if (cursor != null && cursor.moveToFirst()) { 
                apnId = cursor.getString(cursor.getColumnIndex("_id")); 
            } 
            cursor.close(); 
            
            //set new apn id as chosen one 
            if (apnId != null) { 
                ContentValues values = new ContentValues(); 
                values.put("apn_id", apnId); 
                resolver.update(CURRENT_APN_URI, values, null, null); 
            } else { 
                //apn id not found, return 0. 
                return 0; 
            } 
        } catch (SQLException e) { 
//            Debug.error(e.getMessage()); 
        } finally { 
            if (cursor != null) { 
                cursor.close(); 
            } 
        } 
        //update success 
        return 1; 
   } 
    
    /**
     * Return the Accept-Language header.  Use the current locale plus
     * US if we are in a different locale than US.
     */
//    private static String getHttpAcceptLanguage() {
//        Locale locale = Locale.getDefault();
//        StringBuilder builder = new StringBuilder();
//
//        addLocaleToHttpAcceptLanguage(builder, locale);
//        if (!locale.equals(Locale.US)) {
//            if (builder.length() > 0) {
//                builder.append(", ");
//            }
//            addLocaleToHttpAcceptLanguage(builder, Locale.CHINA);
//        }
//        return builder.toString();
//    }

//    private static void addLocaleToHttpAcceptLanguage(
//            StringBuilder builder, Locale locale) {
//        String language = locale.getLanguage();
//
//        if (language != null) {
//            builder.append(language);
//
//            String country = locale.getCountry();
//
//            if (country != null) {
//                builder.append("-");
//                builder.append(country);
//            }
//        }
//    }
    
    /**   
     * 移动网络开关   
     */   
    public static void toggleMobileData(Context context, boolean enabled) {    
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);    
        Class<?> conMgrClass = null; // ConnectivityManager类    
        Field iConMgrField = null; // ConnectivityManager类中的字段    
        Object iConMgr = null; // IConnectivityManager类的引用    
        Class<?> iConMgrClass = null; // IConnectivityManager类    
        Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法    
        try {     
            // 取得ConnectivityManager类     
        conMgrClass = Class.forName(conMgr.getClass().getName());     
        // 取得ConnectivityManager类中的对象mService     
        iConMgrField = conMgrClass.getDeclaredField("mService");     
        // 设置mService可访问   iConMgrField.setAccessible(true);     
        // 取得mService的实例化类IConnectivityManager     
        iConMgr = iConMgrField.get(conMgr);     
        // 取得IConnectivityManager类     
        iConMgrClass = Class.forName(iConMgr.getClass().getName());     
        // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法     
        setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);     
        // 设置setMobileDataEnabled方法可访问     
        setMobileDataEnabledMethod.setAccessible(true);     
        // 调用setMobileDataEnabled方法     
        setMobileDataEnabledMethod.invoke(iConMgr, enabled);    
        } catch (ClassNotFoundException e) {     
            e.printStackTrace();    
        } catch (NoSuchFieldException e) {     
            e.printStackTrace();    
        } catch (SecurityException e) {     
            e.printStackTrace();    
        } catch (NoSuchMethodException e) {     
            e.printStackTrace();    
        } catch (IllegalArgumentException e) {     
            e.printStackTrace();    
        } catch (IllegalAccessException e) {     
            e.printStackTrace();    
        } catch (InvocationTargetException e) {     
            e.printStackTrace();    
        }   
    }
    
    /**
     * Look up a host name and return the result as an int. Works if the argument
     * is an IP address in dot notation. Obviously, this can only be used for IPv4
     * addresses.
     * @param hostname the name of the host (or the IP address)
     * @return the IP address as an {@code int} in network byte order
     */
    
    public static int lookupHost(String hostname)
    {
        hostname = hostname.substring(0, hostname.indexOf(":") > 0 ? hostname.indexOf(":") : hostname.length());
        String result = "";
        String[] array = hostname.split("\\.");
        if (array.length != 4) return -1;

        int[] hexArray = new int[] {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
        hexArray[0] = Integer.parseInt(array[0]) / 16;
        hexArray[1] = Integer.parseInt(array[0]) % 16;
        hexArray[2] = Integer.parseInt(array[1]) / 16;
        hexArray[3] = Integer.parseInt(array[1]) % 16;
        hexArray[4] = Integer.parseInt(array[2]) / 16;
        hexArray[5] = Integer.parseInt(array[2]) % 16;
        hexArray[6] = Integer.parseInt(array[3]) / 16;
        hexArray[7] = Integer.parseInt(array[3]) % 16;

        for (int i=0; i<8; i++)
        {
            result += Integer.toHexString( hexArray[i] );
        }

        return Long.valueOf(Long.parseLong(result, 16)).intValue();
    }
}
