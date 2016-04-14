package com.yang.download.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yang.download.AppConfig;
import com.yang.download.entity.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yuy on 2016/4/14.
 */
public class DownloadService extends Service {

	private DownloadTask mTask = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (AppConfig.ACTION_START.equals(intent.getAction())) {
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.d("yang", "start fileInfo-->" + fileInfo.toString());

			new InitThread(fileInfo).start();
		} else if (AppConfig.ACTION_STOP.equals(intent.getAction())) {
			FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
			Log.d("yang", "stop fileInfo-->" + fileInfo.toString());
			if (mTask != null) {
				mTask.isPause = true;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case AppConfig.MSG_INIT:
					FileInfo fileInfo = (FileInfo) msg.obj;
					Log.d("yang", "AppConfig.MSG_INIT ----fileInfo-->" + fileInfo);
					mTask = new DownloadTask(DownloadService.this, fileInfo);
					mTask.download();
					break;
			}
			return false;
		}
	});

	/**
	 * 初始化子线程
	 */
	class InitThread extends Thread {
		private FileInfo mFileInfo = null;

		public InitThread(FileInfo fileInfo) {
			this.mFileInfo = fileInfo;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			try {
				//连接网络文件
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				int length = -1;
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					//获得文件长度
					length = conn.getContentLength();
				}

				if (length < 0) {
					return;
				}

				//在本地创建文件
				File dir = new File(AppConfig.DOWNLOAD_PATH);
				if (!dir.exists()) {
					dir.mkdir();
				}

				File file = new File(dir, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				//设置文件长度
				raf.setLength(length);
				mFileInfo.setLength(length);
				mHandler.obtainMessage(AppConfig.MSG_INIT, mFileInfo).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				try {
					conn.disconnect();
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
