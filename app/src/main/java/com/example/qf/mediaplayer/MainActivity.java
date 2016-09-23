package com.example.qf.mediaplayer;

import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    public static final String path= Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator+"netease"+File.separator+
            "cloudmusic"+ File.separator+"Music";
    public File[] files;
    private int currentProgress;
    private String fileName;
    private SimpleDateFormat sdf=new SimpleDateFormat("mm:ss");
    private TextView currentTime,totalTime,name;
    private ServiceConnection serviceConnection;
    private List<String> list=new ArrayList<>();
    private int currentPosition=0;
    private Button btn;
    private ListView lv;
    private boolean isDestroy=false;
    public Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:

                    break;
                case 2:

                    break;
                case 3:
                    break;
                case 4:
                    if(!isDestroy) {
                        mHandler.sendEmptyMessageDelayed(4, 1000);
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        currentTime.setText(sdf.format(new Date(mediaPlayer.getCurrentPosition())));
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayer=new MediaPlayer();
        files = new File(path).listFiles();
        for(File f:files){
            list.add(f.getName());
        }
   
        btn= (Button) findViewById(R.id.play_pause);
        lv= (ListView) findViewById(R.id.lv);
        name= (TextView) findViewById(R.id.name);
        fileName=list.get(0);
        name.setText(fileName);
        initMediaPlayer();
        setData(list);
        currentTime= (TextView) findViewById(R.id.currentTime);
        totalTime= (TextView) findViewById(R.id.totalTime);
        seekBar= (SeekBar) findViewById(R.id.seekBar);
        int duration=mediaPlayer.getDuration();
        seekBar.setMax(duration);
        currentTime.setText(sdf.format(new Date(0)));
        totalTime.setText(sdf.format(new Date(duration)));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProgress=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(currentProgress);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(currentPosition!=list.size()-1) {
                    currentPosition += 1;
                    mediaPlayer.reset();
                    choose(currentPosition);
                }
            }
        });

    }

    private void initMediaPlayer() {

        try {
            mediaPlayer.setDataSource(this, Uri.fromFile(new File(path,fileName)));
            mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void setData(final List<String> list){
        lv.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,list));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mediaPlayer.isPlaying()){
                    choose(position);
                }else {
                    mediaPlayer.reset();
                    choose(position);
                }
                currentPosition=position;
                btn.setBackgroundResource(R.drawable.player_toolbar_pause_normal);
            }
        });



    }
    public void choose(int position) {
        fileName = list.get(position);
        name.setText(fileName);
        if (mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }
            seekBar.setProgress(0);
            currentPosition = position;
            currentTime.setText(sdf.format(new Date(0)));
            initMediaPlayer();
            mediaPlayer.start();
            int duration = mediaPlayer.getDuration();
            seekBar.setMax(duration);
            totalTime.setText(sdf.format(new Date(duration)));
            mHandler.sendEmptyMessage(4);
    }


    public void last(View view) {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }
        if(currentPosition!=0){
            currentPosition-=1;
            choose(currentPosition);
        }else {
            Toast.makeText(MainActivity.this, "现在已是第一首了", Toast.LENGTH_SHORT).show();
        }
    }

    public void start_pause(View view) {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            mHandler.sendEmptyMessage(4);
            btn.setBackgroundResource(R.drawable.player_toolbar_pause_normal);
        }else{
            mediaPlayer.pause();
            btn.setBackgroundResource(R.drawable.player_toolbar_play_normal);
        }

    }

    public void next(View view) {
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }
        if(currentPosition!=list.size()-1) {
            currentPosition += 1;
            choose(currentPosition);
        }else{
            Toast.makeText(MainActivity.this, "已经是最后一首了", Toast.LENGTH_SHORT).show();
        }
    }

    public void stop(View view) {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            seekBar.setProgress(0);
            currentTime.setText(sdf.format(new Date(0)));
            btn.setBackgroundResource(R.drawable.player_toolbar_play_normal);
            initMediaPlayer();
        }else {
            seekBar.setProgress(0);
            currentTime.setText(sdf.format(new Date(0)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy=true;
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            Log.d("jzjz", "onDestroy: ");
        }
    }
}
