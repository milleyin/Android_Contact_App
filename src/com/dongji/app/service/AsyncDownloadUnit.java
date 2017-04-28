package com.dongji.app.service;

import android.os.AsyncTask;
import android.widget.TextView;


/**
 * 
 * 异步下载类
 * @author 
 *
 */
public class AsyncDownloadUnit extends AsyncTask<String, Integer, Boolean> {

	TextView tv_progress;  //显示下载进度的textView

	String file_path; //文件的存储路径
	
	int type ; //0:swf文件   1：mp3文件
	public static final int TYPE_SWF=0;
	public static final int TYPE_MP3=1;
	
	private OnFinishAsyncDownload onFinishAsyncDownload; //下载完成的回调
	 
	private OnFaildAsyncDownload onFaildAsyncDownload;  //下载失败的回调
	
	public AsyncDownloadUnit(TextView tv_progress , int type)
	{
		this.tv_progress = tv_progress;
		this.type = type;
		
	}
	

	//下载结果的处理
	@Override
	protected void onPostExecute(Boolean result) {
		
		if (result) {
			onFinishAsyncDownload.onFinishDownload(file_path);
		}else{
			onFaildAsyncDownload.OnFialdDownload();
		}
	}

	// 更新进度
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(tv_progress!=null)
		{
			tv_progress.setText( values[0] + " %");
		}
	}

	
	public interface OnFinishAsyncDownload{
		public void onFinishDownload(String file_url);
	}

	public void setOnFinishAsyncDownload(OnFinishAsyncDownload onFinishAsyncDownload) {
		this.onFinishAsyncDownload = onFinishAsyncDownload;
	}
	
	public interface OnFaildAsyncDownload{
		public void OnFialdDownload();
	}


	public void setOnFaildAsyncDownload(OnFaildAsyncDownload onFaildAsyncDownload) {
		this.onFaildAsyncDownload = onFaildAsyncDownload;
	}


	@Override
	protected Boolean doInBackground(String... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
