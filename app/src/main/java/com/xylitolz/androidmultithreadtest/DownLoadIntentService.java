package com.xylitolz.androidmultithreadtest;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc
 * @date 2018-04-23 09:51
 */

public class DownLoadIntentService extends IntentService {

    public static final String TAG = "DownLoadIntentService";

    public static final String DOWNLOAD_FINISH = "DOWNLOAD_FINISH";

    public DownLoadIntentService() {
        this("DownLoadIntentService");
    }

    public DownLoadIntentService(String name) {
        super(name);
    }

    /**
     * 工作线程执行，其原理还是Handler，详细细节见源码
     * @param intent
     */
    @WorkerThread
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getExtras();
        String fileName = bundle.getString("file_name");
        Log.e(TAG,"当前下载"+fileName+"当前线程id"+Thread.currentThread().getId());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent resultIntent = new Intent(DOWNLOAD_FINISH);
        resultIntent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.e(TAG,"当前serviceId"+startId);
        Log.e(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Log.e(TAG,"onStart");
        super.onStart(intent, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");
        super.onDestroy();
    }
}
