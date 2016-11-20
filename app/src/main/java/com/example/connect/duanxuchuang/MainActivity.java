package com.example.connect.duanxuchuang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;
    private ProgressBar pg;
    private Button btStart;
    private Button btStop;
    private String path="http://downmini.kugou.com/kugou8100.exe";
    DownInfo downInfo;
    MBroadcastReceive mBroadcastReceive=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView= (TextView) findViewById(R.id.textView);
        pg= (ProgressBar) findViewById(R.id.progressBar);
        btStart= (Button) findViewById(R.id.btstart);
        btStop= (Button) findViewById(R.id.btstop);
         downInfo=new DownInfo(1,path,0,"kugou.exe",0);
        pg.setMax(100);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText(downInfo.getFilename());
                Intent intent=new Intent(MainActivity.this,DownloadService.class);
                intent.putExtra("info",downInfo);
                intent.setAction(DownloadService.START);
                startService(intent);
            }
        });
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,DownloadService.class);

                intent.setAction(DownloadService.STOP);
                startService(intent);
            }
        });
        mBroadcastReceive=new MBroadcastReceive();
        IntentFilter filter=new IntentFilter();
        filter.addAction(DownloadService.UPDATE);
        registerReceiver(mBroadcastReceive,filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceive);
    }

    class MBroadcastReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadService.UPDATE)){
                int finished = intent.getIntExtra("finished", 0);
                pg.setProgress(finished);
            }

        }
    }
}
