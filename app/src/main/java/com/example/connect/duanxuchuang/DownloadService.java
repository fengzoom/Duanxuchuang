package com.example.connect.duanxuchuang;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/11/10.
 */

public class DownloadService extends Service {
    public static String START = "start";
    public static String STOP = "stop";
    public static String UPDATE = "UPDATE";
    public static final int STATE = 0;
    DownLoadTask downLoadTask =null;
    public static String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
    DownInfo info =null;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            if (intent.getAction().equals(START)) {
                info= (DownInfo) intent.getSerializableExtra("info");
                new InnerThread(info).start();
            } else if (intent.getAction().equals(STOP)) {
               if (info!=null){
                   downLoadTask.isPause=true;
                   Log.e("TAG", "onStartCommand:  downLoadTask.isPause=true;"+ downLoadTask.isPause );
               }
            }
            Log.e("TAG", "onStartCommand: getFilename" + info.getFilename() + "getLength" + info.getLength());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Handler handler;

    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case STATE:
                        DownInfo info = (DownInfo) msg.obj;
                        Log.e("TAG", "handleMessage: "+info.getFilename());
                       downLoadTask = new DownLoadTask(DownloadService.this, info);
                        downLoadTask.download();
                        break;
                }
            }
        };
    }

    class InnerThread extends Thread {
        private DownInfo info = null;

        public InnerThread(DownInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection httpURLConnection = null;
            RandomAccessFile raf=null;
            try {
                URL url = new URL(info.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                int lenght = -1;
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    lenght = httpURLConnection.getContentLength();
                }
                if (lenght < 0) {
                    return;
                }

                Log.e("TAG", "run: InnerThread+lenght"+lenght);
                File dir = new File(FILEPATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, info.getFilename());
                 raf = new RandomAccessFile(file, "rwd");
                raf.setLength(lenght);

                info.setLength(lenght);
                Log.e("TAG", "run: InnerThread+info.setLength(lenght);"+info.getLength());
                handler.obtainMessage(STATE,info).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {

                try {
                    httpURLConnection.disconnect();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
