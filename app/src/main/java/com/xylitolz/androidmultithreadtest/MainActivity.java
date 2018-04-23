package com.xylitolz.androidmultithreadtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_handler_demo).setOnClickListener(this);
        findViewById(R.id.btn_handler_thread_demo).setOnClickListener(this);
        findViewById(R.id.btn_async_task_demo).setOnClickListener(this);
        findViewById(R.id.btn_intent_service_demo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_handler_demo:
                HandlerDownloadActivity.start(this);
                break;
            case R.id.btn_async_task_demo:
                AsyncTaskDemoActivity.start(this);
                break;
            case R.id.btn_handler_thread_demo:
                HandlerThreadDemoActivity.start(this);
                break;
            case R.id.btn_intent_service_demo:
                IntentServiceDemoActivity.start(this);
                break;
        }
    }
}
