package com.xylitolz.androidmultithreadtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc IntentService使用例子
 * @date 2018-04-23 09:27
 */

public class IntentServiceDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvResult;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service);
        tvResult = findViewById(R.id.tv_result);
        findViewById(R.id.btn_download).setOnClickListener(this);
        intentFilter = new IntentFilter(DownLoadIntentService.DOWNLOAD_FINISH);
        receiver = new DownLoadBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,intentFilter);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this,DownLoadIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString("file_name","巴啦啦小魔仙.mp4");
        intent.putExtras(bundle);
        startService(intent);

        Intent intent2 = new Intent(this,DownLoadIntentService.class);
        Bundle bundle2 = new Bundle();
        bundle2.putString("file_name","金刚葫芦娃.mp4");
        intent2.putExtras(bundle2);
        startService(intent2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context,IntentServiceDemoActivity.class);
        context.startActivity(intent);
    }

    private class DownLoadBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(TextUtils.equals(intent.getAction(),DownLoadIntentService.DOWNLOAD_FINISH)) {
                Bundle bundle = intent.getExtras();
                String fileName = bundle.getString("file_name");
                String result = tvResult.getText().toString();
                result += "名为"+fileName+"的文件下载完成！\n";
                tvResult.setText(result);
                Toast.makeText(IntentServiceDemoActivity.this,"下载完成，当前下载文件名"+fileName,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
