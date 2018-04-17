package com.xylitolz.androidmultithreadtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc 使用多线程与Handler模拟下载进度条
 * @date 2018-04-17 14:12
 */

public class HandlerDownloadActivity extends AppCompatActivity {
    /**
     * 第一步，主线程中创建Handler并在handleMessage方法中处理相应逻辑
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //这里完成更新UI的操作
            if(msg != null && msg.what == MESSAGE_WHAT_UPDATE_PROGRESS) {
                int progress = (int) msg.obj;
                String value = "";
                if(progress <= MAX_PROGRESS) {
                    progressBar.setProgress(progress);
                    value += "第一进度条:"+progress+"%";
                }
                if(progress*2 <= MAX_PROGRESS) {
                    progressBar.setSecondaryProgress(progress*2);
                    value += " 第二进度条:"+progress*2+"%";
                } else {
                    value += " 第二进度条:"+MAX_PROGRESS+"%";
                }
                tvProgressValue.setText(value);
            }
        }
    };
    /**
     * 执行下载操作的线程
     */
    private Thread downloadThread;
    private boolean destroyThread = false;

    private ProgressBar progressBar;
    private TextView tvProgressValue;

    private int progress = 0;

    private static final int MAX_PROGRESS = 100;
    private static final int MESSAGE_WHAT_UPDATE_PROGRESS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_download);

        progressBar = findViewById(R.id.progress_bar);
        tvProgressValue = findViewById(R.id.tv_progress_value);

        progressBar.setProgress(progress);
        progressBar.setSecondaryProgress(progress);
        tvProgressValue.setText("第一进度条:0%  第二进度条:0%");
        progressBar.setMax(MAX_PROGRESS);
    }

    protected void testDownLoad(View view) {
        if(downloadThread != null) {
            Toast.makeText(this,"下载任务已经启动！",Toast.LENGTH_SHORT).show();
            return;
        }
        //初始化
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        //创建子线程
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //线程结束标志，activity销毁后不再执行线程内代码
                if(!destroyThread) {
                    //do something
                    for(int i = 0;i < 100;i++) {
                        progress++;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Message message = Message.obtain();
                        message.what = MESSAGE_WHAT_UPDATE_PROGRESS;
                        message.obj = progress;
                        handler.sendMessage(message);
                    }
                }
            }
        });
        //启动子线程
        downloadThread.start();
    }


    @Override
    protected void onDestroy() {
        //设置线程结束标志
        destroyThread = true;
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context,HandlerDownloadActivity.class);
        context.startActivity(intent);
    }
}
