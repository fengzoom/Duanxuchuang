package com.example.connect.duanxuchuang;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class DownLoadTask {
    private  ThreadDAOimpl threadDAOimpl=null;
    private Context mContext;
    private DownInfo downInfo = null;
    private long finished=0;
    public  boolean isPause=false;

    public DownLoadTask(Context mContext, DownInfo downInfo) {
        this.mContext = mContext;
        this.downInfo = downInfo;
        threadDAOimpl = new ThreadDAOimpl(mContext);
    }

    public void download(){

        List<ThreadInfo> threadsInfos = threadDAOimpl.getThreads(downInfo.getUrl());
        ThreadInfo threadInfo=null;
        Log.e("TAG", "download: threadsInfos.size()qqqqq"+threadsInfos.size() );
        if (threadsInfos.size()==0){
            threadInfo = new ThreadInfo(0,downInfo.getUrl(),0,downInfo.getLength(),0);
        }else {
            threadInfo=threadsInfos.get(0);
        }
        Log.e("TAG", "download: threadsInfos.size()"+threadsInfos.size()+ threadInfo.getFinished());
        new DownLoadThread(threadInfo).start();
    }

    class DownLoadThread extends Thread {
        private ThreadInfo info = null;

        public DownLoadThread(ThreadInfo info) {
            this.info = info;
        }

        @Override
        public void run() {
            super.run();
            Log.e("TAG", "run: info.getUrl()"+info.getUrl()+"info.getId() "+info.getId() );
            if (!threadDAOimpl.isExists(info.getUrl(),info.getId())){
                threadDAOimpl.insertThread(info);
                Log.e("TAG", "run:重新来过 "+info.getFinished() );
            }
            HttpURLConnection httpURLConnection=null;
            InputStream inputStream=null;
            RandomAccessFile raf=null;
            try {
                URL url = new URL(info.getUrl());
                httpURLConnection = ((HttpURLConnection) url.openConnection());
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setConnectTimeout(3000);

                long start =info.getStart()+info.getFinished();
                httpURLConnection.setRequestProperty("Range","bytes="+start+"-"+info.getEnd());
                File file=new File(DownloadService.FILEPATH,downInfo.getFilename());
                Intent intent=new Intent(DownloadService.UPDATE);
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);
                long time=System.currentTimeMillis();
                finished+=info.getFinished();
//                Log.e("TAG", "run: httpURLConnection.getResponseCode()"+httpURLConnection.getResponseCode() );
                if (httpURLConnection.getResponseCode()==206){
                    inputStream = httpURLConnection.getInputStream();
                    byte[] buffer=new byte[4*1024];
                    int length=-1;
                    while ((length=inputStream.read(buffer))!=-1){
                        raf.write(buffer,0,length);
                        finished+=length;

                        if (System.currentTimeMillis()-time>1000){
                            time=System.currentTimeMillis();
                            intent.putExtra("finished",(int)((finished*100)/downInfo.getLength()));
//                            Log.e("TAG", "run: %"+(int)((finished*100)/downInfo.getLength()));
                            mContext.sendBroadcast(intent);
                            Log.e("TAG", "run: isPause"+isPause );
                            info.setFinished(finished);
                        }

                        if (isPause){
                            threadDAOimpl.updataThread(info.getUrl(),info.getId(),finished);
                            return;
                        }
                    }
                    intent.putExtra("finished",100);
                    mContext.sendBroadcast(intent);
                    threadDAOimpl.deleteThread(info.getUrl(),info.getId());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    httpURLConnection.disconnect();
                    inputStream.close();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
