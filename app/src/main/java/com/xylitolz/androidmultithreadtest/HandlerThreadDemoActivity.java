package com.xylitolz.androidmultithreadtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc HandlerThread使用demo
 * @date 2018-04-19 11:34
 */

public class HandlerThreadDemoActivity extends AppCompatActivity {

    private TextView tvProgressValue;
    private ProgressBar pbLoading;

    private Handler workerHandler,mainHandler;//工作线程绑定的Handler和主线程Handler
    private HandlerThread workerThread;//工作线程
    public boolean isRunning = true;//Activity运行状态

    public static final int DOWN_LOAD_TASK = 1001;//开启下载任务
    public static final int UPDATE_PROGRESS = 1002;//更新进度条
    public static final int PROGRESS_MAX_VALUE = 10000;
    private String downloadUrl = "http://www.riceeater.info/uploads/shadowsocks--universal-4.5.1.apk";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread_demo);
        initView();
        initThread();
    }

    private void initView() {
        //获取写入文件权限
        verifyStoragePermissions(this);

        tvProgressValue = findViewById(R.id.tv_progress_value);
        pbLoading = findViewById(R.id.pb_loading);

        pbLoading.setMax(PROGRESS_MAX_VALUE);
        updateProgress(0);
    }

    private void initThread() {
        //初始化工作线程
        workerThread = new HandlerThread("down-load-thread");
        //启动工作线程
        workerThread.start();
        //初始化工作线程handler,传入Looper实现与工作线程的绑定
        workerHandler = new DownLoadHandler(workerThread.getLooper(),new WeakReference<HandlerThreadDemoActivity>(this));
        //初始化主线程
        mainHandler = new MainHandler(new WeakReference<HandlerThreadDemoActivity>(this));
    }

    /**
     * Button点击事件，模拟收到推送消息，开始下载
     * @param view
     */
    protected void getPushMessage(View view) {
        //模拟收到推送消息，请求下载
        Message message = Message.obtain();
        message.what = DOWN_LOAD_TASK;
        message.obj = downloadUrl;
        //工作线程消息处理器发出消息，消息会进入工作线程的MessageQueue中，
        // 并被工作线程Looper取出并重新分发给workerHandler
        workerHandler.sendMessage(message);
    }

    /**
     * 发送更新进度条消息，在子线程中执行
     * @param message 消息体
     */
    public void sendProgressMessage(Message message) {
        Log.e("THREAD", "sendProgressMessage"+Thread.currentThread().getId()+"");
        mainHandler.sendMessage(message);//子线程将消息发给主线程
    }

    /**
     * 更新UI操作，主线程进行
     * @param percent
     */
    public void updateProgress(float percent) {
        updateProgress(percent,null);
    }

    public void updateProgress(float percent,String filePath) {
        Log.e("THREAD", "updateProgress"+Thread.currentThread().getId()+"");
        tvProgressValue.setText("当前进度:"+String.format("%.2f%%",percent * 100));
        pbLoading.setProgress((int) (percent * PROGRESS_MAX_VALUE));
        if(percent == 1) {
            Toast.makeText(this,"下载完成",Toast.LENGTH_SHORT).show();
            install(filePath);
        }
    }

    /**
     * 安装应用
     * @param filePath
     */
    public void install(String filePath) {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    this
                    , "com.github.shadowsocks.fileprovider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.e("THREAD", "URI"+apkFile.getAbsolutePath());
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        isRunning = false;//设置运行状态为false
        if(workerThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                workerThread.quitSafely();//线程安全
            } else {
                workerThread.quit();//非线程安全
            }
        }
        if(workerHandler != null) {
            workerHandler.removeCallbacksAndMessages(null);
        }
        if(mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context,HandlerThreadDemoActivity.class);
        context.startActivity(intent);
    }

    //使用静态内部类防止内存泄漏
    static class MainHandler extends Handler {
        private WeakReference<HandlerThreadDemoActivity> activityWeakReference;

        public MainHandler(WeakReference<HandlerThreadDemoActivity> activityWeakReference) {
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("THREAD", "MainHandler handleMessage"+Thread.currentThread().getId()+"");
            if(msg.what == UPDATE_PROGRESS) {
                //获取到更新进度条的消息
                float percent = (float) msg.obj;
                if(percent != 1) {
                    activityWeakReference.get().updateProgress(percent);
                } else {
                    Bundle bundle = msg.getData();
                    String filePath = bundle.getString("file");
                    activityWeakReference.get().updateProgress(percent,filePath);
                }
            }
        }
    }

    static class DownLoadHandler extends Handler {
        private WeakReference<HandlerThreadDemoActivity> activityWeakReference;
        public DownLoadHandler(Looper looper,WeakReference<HandlerThreadDemoActivity> activityWeakReference) {
            super(looper);
            this.activityWeakReference = activityWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("THREAD", "DownLoadHandler handleMessage"+Thread.currentThread().getId()+"");
            if(msg.what == DOWN_LOAD_TASK) {
                //判断收到消息为要求下载
                String url = (String) msg.obj;
                if( !TextUtils.isEmpty(url)) {
                    download(url);
                }
            }
        }

        private void download(String url) {
            try{
                //下载路径
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                //文件名
                String filename=url.substring(url.lastIndexOf("/") + 1);
                //获取文件名
                URL myURL = new URL(url);
                URLConnection conn = myURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                int fileSize = conn.getContentLength();//根据响应获取文件大小
                if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
                if (is == null) throw new RuntimeException("stream is null");
                File file = new File(path+"/"+filename);
                if(!file.exists()){
                    file.mkdirs();
                }
                //把数据存入路径+文件名
                FileOutputStream fos = new FileOutputStream(file);
                byte buf[] = new byte[1024];
                int downLoadFileSize = 0;//记录已下载大小
                int numRead = is.read(buf);
                while(numRead != -1 && activityWeakReference.get().isRunning) {
                    fos.write(buf, 0, numRead);
                    downLoadFileSize += numRead;
                    numRead = is.read(buf);
                    //更新进度条
                    float percent = downLoadFileSize / (fileSize * 1f);
                    Message message = Message.obtain();
                    message.what = UPDATE_PROGRESS;
                    if(percent == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putString("file", file.getAbsolutePath());
                        message.setData(bundle);
                    }
                    message.obj = percent;
                    activityWeakReference.get().sendProgressMessage(message);
                }
                is.close();
                fos.close();
            } catch (Exception ex) {
                Log.e("DOWNLOAD", "error: " + ex.getMessage(), ex);
            }
        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
