package com.dongji.app.net;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

/**
 * HttpClient 多线程安全，单例模式
 * @author 
 *
 */
public class NetworkService {
	
	private static DefaultHttpClient myHttpClient;
	
	private static final String CHARSET = HTTP.UTF_8; //编码

	private static long TIME_OUT = 3000; // 超时设置 /* 从连接池中取连接的超时时�?*/
	private static int CONNETCTION_TIME_OUT = 5000; /* 连接超时 */
	private static int SO_TIME_OUT = 10000; /* 请求超时 */
	
	private NetworkService() {
		
	}

	public static synchronized DefaultHttpClient getHttpClient() {
		if (null == myHttpClient) {
			HttpParams params = new BasicHttpParams();
			
			// 设置�?��基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, CHARSET);
			HttpProtocolParams.setUseExpectContinue(params, true);
//			HttpProtocolParams.setUserAgent(params,"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			
			//设置超时
			ConnManagerParams.setTimeout(params, TIME_OUT);
			HttpConnectionParams.setConnectionTimeout(params, CONNETCTION_TIME_OUT);
			HttpConnectionParams.setSoTimeout(params, SO_TIME_OUT);
			
			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			
			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
			myHttpClient = new DefaultHttpClient(conMgr, params);
		}
		return myHttpClient;
	}
	
	/**
	 * 关闭连接
	 */
	public static void shutDown()
	{
		if(myHttpClient!=null)
		{
			myHttpClient.getConnectionManager().shutdown();
		}
	}
	
}
