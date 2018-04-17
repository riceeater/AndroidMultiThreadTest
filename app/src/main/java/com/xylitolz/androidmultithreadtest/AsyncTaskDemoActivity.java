package com.xylitolz.androidmultithreadtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc AsyncTask使用简单演示
 * @date 2018-04-17 14:26
 */

public class AsyncTaskDemoActivity extends AppCompatActivity{

    /**
     * 这里定义AsyncTask切记不要使用多态，不然会报错，因为不能正确识别三个泛型参数
     */
    private DownloadImageTask asyncTask;
    private ImageView ivContainer;
    private ProgressBar pbLoading;
    public static String IMAGE_URL = "http://p6z0jdp7l.bkt.clouddn.com/avatar.jpg";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_demo);
        init();
    }

    private void init() {
        ivContainer = findViewById(R.id.iv_container);
        pbLoading = findViewById(R.id.pb_loading);

    }

    protected void loadImage(View view) {
        asyncTask = new DownloadImageTask();
        asyncTask.execute(IMAGE_URL);//可以接受多个参数
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context,AsyncTaskDemoActivity.class);
        context.startActivity(intent);
    }

    class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {

        /**
         * 主线程中执行，调用execute方法后立即执行
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
            ivContainer.setImageBitmap(null);
        }

        /**
         * 工作线程中执行，会在执行完onPreExecute后执行方法
         * @param strings 接受参数
         * @return 返回结果，传递给onPostExecute
         */
        @Override
        protected Bitmap doInBackground(String... strings) {
            //下载图片，返回bitmap对象
            String url = strings[0];
            URLConnection connection;
            InputStream inputStream;
            Bitmap result = null;
            try {
                connection = new URL(url).openConnection();
                inputStream = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                result = BitmapFactory.decodeStream(bis);
                bis.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        /**
         * 主线程执行，在手动调用publishProgress后被自动调用
         * @param values 异步任务进度
         */
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        /**
         * 主线程执行，工作任务完成后调用该方法
         * @param bitmap 返回结果
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pbLoading.setVisibility(View.GONE);
            ivContainer.setImageBitmap(bitmap);
        }
    }
}
