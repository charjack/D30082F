package com.wedesign.mediaplayer;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.Utils.Mp4MediaUtils;
import com.wedesign.mediaplayer.vo.Mp4Info;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback{

    private static final String TAG = "VideoFragment";
    SurfaceView surfaceView_video;
    private SurfaceHolder holder;
    private MediaPlayer mp;
    private boolean ishide = true;
    private TextView play_video_name;

    private String currentVideoPath = null;
    private int currentVideoProgress = 0;
    private String currentVideoName = null;
    private long currentVideoTotalTimenum =0;
    private String currentVideoTotalTime = null;
    public VideoUIUpdateListener videoUIUpdateListener;

    SeekBar seekBar2;
    LinearLayout progress_really_layout;
    TextView video_current_time,video_total_time;
    Timer timer;
    MyVideoHandler myVideoHandler;
    private boolean selectfromuser = false;
    private boolean selectfromactivity = false;
    private int fullScreen = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        videoUIUpdateListener = (VideoUIUpdateListener)activity;
    }

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        surfaceView_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!BaseApp.ifFullScreenState) {
                    if(BaseApp.ifliebiaoOpen == 0) {
                        if (ishide) {
                            //show video name and controller
                            play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));
                            play_video_name.setVisibility(View.VISIBLE);
                            progress_really_layout.setVisibility(View.VISIBLE);
                            ishide = false;
                        } else {
                            //hide video name and controller
                            play_video_name.setVisibility(View.GONE);
                            progress_really_layout.setVisibility(View.GONE);
                            ishide = true;
                        }
                    }else{// if(BaseApp.ifopenliebiao == 1)
                        BaseApp.ifliebiaoOpen = 0;
                        videoUIUpdateListener.onVideoLieBiaoClose();
                        if(!ishide){
                            //hide video name and controller
                            play_video_name.setVisibility(View.GONE);
                            progress_really_layout.setVisibility(View.GONE);
                            ishide = true;
                        }
                    }
                }
                else{
                    BaseApp.ifFullScreenState = false;
                    //显示小屏幕
                    videoUIUpdateListener.onVideoScreenChange(mp.getCurrentPosition());
                    //show
                    play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));
                    play_video_name.setVisibility(View.VISIBLE);
                    progress_really_layout.setVisibility(View.VISIBLE);
                    ishide = false;
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View video_view  =inflater.inflate(R.layout.fragment_video, container, false);
        surfaceView_video = (SurfaceView) video_view.findViewById(R.id.surfaceView_video);
        play_video_name = (TextView) video_view.findViewById(R.id.play_video_name);

        progress_really_layout = (LinearLayout) video_view.findViewById(R.id.progress_really_layout);
        seekBar2 = (SeekBar) video_view.findViewById(R.id.seekBar2);
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {

                    BaseUtils.mlog(TAG, "-onCreateView-" + "person change the progress...");
                    if (BaseApp.playSourceManager == 0) {
                        BaseApp.current_video_play_progressUSB = progress;
                    } else if (BaseApp.playSourceManager == 3) {
                        BaseApp.current_video_play_progressSD = progress;
                    }
                    seekBar.setProgress(progress);
                    mp.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mp.pause();
                if (videoUIUpdateListener != null) {
                    videoUIUpdateListener.onVideoNotifyUIChange(1);  //1表示暂停 0 表示开始按钮
                }  //改变暂停按钮
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.start();//改变暂停按钮
                if (videoUIUpdateListener != null) {
                    videoUIUpdateListener.onVideoNotifyUIChange(0);  //1表示暂停 0 表示开始按钮
                }

            }
        });
        video_current_time=(TextView) video_view.findViewById(R.id.video_current_time);
        video_total_time=(TextView) video_view.findViewById(R.id.video_total_time);
        seekBar2.setProgress(0);
        seekBar2.setMax((int) currentVideoTotalTimenum);
        video_total_time.setText(currentVideoTotalTime);
        myVideoHandler = new MyVideoHandler();
        holder = surfaceView_video.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);
        surfaceView_video.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mp = new MediaPlayer();

        BaseUtils.mlog(TAG, "-onCreateView-" + "enter videoView creat...");
        if(videoUIUpdateListener!=null){
            videoUIUpdateListener.onVideoCancelCover();
        }
        return video_view;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if(mp ==null){
            mp = new MediaPlayer();
        }
//        currentVideoIndexToPlay = BaseApp.current_video_play_num;
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setDisplay(holder);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (BaseApp.playSourceManager == 0) {
                    //当前视频播放完之后，记录播放时间为0
                    BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).setVideo_item_progressed(0);
                    if ((BaseApp.current_video_play_numUSB + 1 >= BaseApp.mp4Infos.size()) && BaseApp.video_play_mode == 1) {
                        mediaPlayer.pause();   //结束后不能停止，可能用户还会拖动播放
                        videoUIUpdateListener.onVideoNotifyUIChange(1);  //1表示暂停 0 表示开始按钮
                    } else {
                        mediaPlayer.reset();
                        try {
                            BaseApp.current_video_play_numUSB++;
                            if (BaseApp.video_play_mode == 3)
                                BaseApp.current_video_play_numUSB--;  //单曲播放 保持数目不变
                            else if (BaseApp.video_play_mode == 2) {
                                if (BaseApp.current_video_play_numUSB >= BaseApp.mp4Infos.size()) {
                                    BaseApp.current_video_play_numUSB = 0;
                                } else {  //曲目加1即可
                                }
                            } else if (BaseApp.video_play_mode == 1) {//曲目加1即可，特殊情况，之前已经处理了
                            } else if (BaseApp.video_play_mode == 0) {  //随机播放
                                BaseApp.current_video_play_numUSB--;  //先恢复
                                Random random = new Random();
                                int current_video_play_num_temp = random.nextInt(BaseApp.mp4Infos.size());  //总数是4  范围0~3
                                if (BaseApp.current_video_play_numUSB == current_video_play_num_temp) {
                                    BaseApp.current_video_play_numUSB++;
                                    if (BaseApp.current_video_play_numUSB >= BaseApp.mp4Infos.size()) {
                                        BaseApp.current_video_play_numUSB = 0;
                                    }
                                } else {
                                    BaseApp.current_video_play_numUSB = current_video_play_num_temp;
                                }

                            }

                            mediaPlayer.setDataSource(BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getData());//设置播放视频源
                            mediaPlayer.prepare();
                            //                        System.out.println("start play ...");
                            BaseUtils.mlog(TAG, "start play ...");
                            mediaPlayer.start();

                            currentVideoTotalTimenum = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getDuration();
                            seekBar2.setMax((int) currentVideoTotalTimenum);
                            currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);
                            video_total_time.setText(currentVideoTotalTime);
                            currentVideoName = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getDisplay_name();
                            play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    videoUIUpdateListener.onVideoNotifyUIliebiaoChange();
                } else if (BaseApp.playSourceManager == 3) {
                    //当前视频播放完之后，记录播放时间为0
                    BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).setVideo_item_progressed(0);
                    //0 随机播放 1顺序播放 2循环播放 3单曲播放

                    if ((BaseApp.current_video_play_numSD + 1 >= BaseApp.mp4InfosSD.size()) && BaseApp.video_play_mode == 1) {
                        mediaPlayer.pause();   //结束后不能停止，可能用户还会拖动播放
                        videoUIUpdateListener.onVideoNotifyUIChange(1);  //1表示暂停 0 表示开始按钮
                    } else {
                        mediaPlayer.reset();
                        try {
                            BaseApp.current_video_play_numSD++;
                            if (BaseApp.video_play_mode == 3)
                                BaseApp.current_video_play_numSD--;  //单曲播放 保持数目不变
                            else if (BaseApp.video_play_mode == 2) {
                                if (BaseApp.current_video_play_numSD >= BaseApp.mp4InfosSD.size()) {
                                    BaseApp.current_video_play_numSD = 0;
                                } else {  //曲目加1即可
                                }
                            } else if (BaseApp.video_play_mode == 1) {//曲目加1即可，特殊情况，之前已经处理了
                            } else if (BaseApp.video_play_mode == 0) {  //随机播放
                                BaseApp.current_video_play_numSD--;  //先恢复
                                Random random = new Random();
                                int current_video_play_num_temp = random.nextInt(BaseApp.mp4InfosSD.size());  //总数是4  范围0~3
                                if (BaseApp.current_video_play_numSD == current_video_play_num_temp) {
                                    BaseApp.current_video_play_numSD++;
                                    if (BaseApp.current_video_play_numSD >= BaseApp.mp4InfosSD.size()) {
                                        BaseApp.current_video_play_numSD = 0;
                                    }
                                } else {
                                    BaseApp.current_video_play_numSD = current_video_play_num_temp;
                                }

                            }

                            mediaPlayer.setDataSource(BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getData());//设置播放视频源
                            mediaPlayer.prepare();

                            BaseUtils.mlog(TAG, "start play ...");
                            mediaPlayer.start();

                            currentVideoTotalTimenum = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getDuration();
                            seekBar2.setMax((int) currentVideoTotalTimenum);
                            currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);
                            video_total_time.setText(currentVideoTotalTime);
                            currentVideoName = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getDisplay_name();
                            play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    videoUIUpdateListener.onVideoNotifyUIliebiaoChange();
                }
                if(videoUIUpdateListener!=null){
                    videoUIUpdateListener.onVideoItemChange();
                }
            }
        });
        Mp4Info mp4Info = new Mp4Info();
        if(BaseApp.playSourceManager == 0 ) {
            mp4Info = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
        }else if(BaseApp.playSourceManager == 3){
            mp4Info = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD);
        }
        currentVideoPath = mp4Info.getData();
        currentVideoName  = mp4Info.getDisplay_name();
        currentVideoTotalTimenum = mp4Info.getDuration();   //
        currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);

        BaseUtils.mlog(TAG, "-surfaceCreated-" + "video start-----" + currentVideoTotalTime);
        //第一次进入无法显示进度条和总时长
        seekBar2.setMax((int) currentVideoTotalTimenum);
        video_total_time.setText(currentVideoTotalTime);
        play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));
        if(BaseApp.playSourceManager == 0) {
            currentVideoProgress = (int) BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getVideo_item_progressed();
        }else if(BaseApp.playSourceManager == 3){
            currentVideoProgress = (int) BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getVideo_item_progressed();
        }
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mp != null) {
                    myVideoHandler.sendEmptyMessage(1);
                }
            }
        }, 0, 500);
 //       if(selectfromactivity) {
            BaseUtils.mlog(TAG, "surfaceview create...");
            selectfromactivity = false;
            play_video(currentVideoPath);
//        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        BaseUtils.mlog(TAG, "----surfaceChanged----");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mp!=null)
        {
            if (mp.isPlaying()){
                mp.stop();
                mp.release();
                mp=null;
            }
        }
        ishide = true;  //不加上会出现第一次无法全屏
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
        videoUIUpdateListener.onVideoStateChange();
    }

    public void playVideoFromMainactivity(int position,int progress){  //不在videoFragment这个界面
        selectfromactivity = true;
        if(BaseApp.playSourceManager == 0) {
            BaseApp.current_video_play_numUSB = position;
            currentVideoProgress = (int) BaseApp.mp4Infos.get(position).getVideo_item_progressed();
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_video_play_numSD = position;
            currentVideoProgress = (int) BaseApp.mp4InfosSD.get(position).getVideo_item_progressed();
        }
    }

    public void playVideoFromUser(int position){  //已经在videoFragment这个界面了
        selectfromuser = true;
        if(BaseApp.playSourceManager == 0) {
            BaseApp.current_video_play_numUSB = position;
            Mp4Info mp4Info = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
            currentVideoPath = mp4Info.getData();
            play_video(currentVideoPath);
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_video_play_numSD = position;
            Mp4Info mp4Info = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD);
            currentVideoPath = mp4Info.getData();
            play_video(currentVideoPath);
        }
    }

    public void playVideopre(){
        selectfromuser = true;
        Mp4Info mp4Info = new Mp4Info();
        if(BaseApp.playSourceManager == 0) {
            if (BaseApp.current_video_play_numUSB - 1 < 0) {
                BaseApp.current_video_play_numUSB = BaseApp.mp4Infos.size() - 1;
            } else {
                BaseApp.current_video_play_numUSB = BaseApp.current_video_play_numUSB - 1;
            }
            mp4Info = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
        }else if(BaseApp.playSourceManager == 3){
            if (BaseApp.current_video_play_numSD - 1 < 0) {
                BaseApp.current_video_play_numSD = BaseApp.mp4InfosSD.size() - 1;
            } else {
                BaseApp.current_video_play_numSD = BaseApp.current_video_play_numSD - 1;
            }
            mp4Info = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD);
        }
        currentVideoPath = mp4Info.getData();
        play_video(currentVideoPath);
    }
    public void playVideonext(){
        selectfromuser = true;
        Mp4Info mp4Info = new Mp4Info();
        if(BaseApp.playSourceManager == 0) {
            if (BaseApp.current_video_play_numUSB + 1 >= BaseApp.mp4Infos.size()) {
                BaseApp.current_video_play_numUSB = 0;
            } else {
                BaseApp.current_video_play_numUSB = BaseApp.current_video_play_numUSB + 1;
            }
            mp4Info = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
        }else if(BaseApp.playSourceManager == 3){
            if (BaseApp.current_video_play_numSD + 1 >= BaseApp.mp4InfosSD.size()) {
                BaseApp.current_video_play_numSD = 0;
            } else {
                BaseApp.current_video_play_numSD = BaseApp.current_video_play_numSD + 1;
            }
            mp4Info = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD);
        }
        currentVideoPath = mp4Info.getData();
        play_video(currentVideoPath);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mp!=null && BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0){
            if(BaseApp.playSourceManager == 0) {
                BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).setVideo_item_progressed(mp.getCurrentPosition());
                BaseUtils.mlog(TAG, "-onStop-" + "enter video Onstop" + BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getVideo_item_progressed());
            }else if(BaseApp.playSourceManager == 3){
                BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).setVideo_item_progressed(mp.getCurrentPosition());
                BaseUtils.mlog(TAG, "-onStop-" + "enter video Onstop" + BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getVideo_item_progressed());
            }
            if (mp.isPlaying()){
                mp.stop();
            }
            mp.release();
            mp=null;
        }
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
    }

    public void play_video(String Path) {

        if(BaseApp.playSourceManager == 0) {
            //对播放视频状态进行更改
            currentVideoTotalTimenum = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getDuration();
            seekBar2.setMax((int) currentVideoTotalTimenum);
            currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);
            video_total_time.setText(currentVideoTotalTime);
            currentVideoName = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getDisplay_name();
            play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));
//        BaseApp.current_video_play_num  = currentVideoIndexToPlay;

            currentVideoProgress = (int) BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getVideo_item_progressed();
            BaseUtils.mlog(TAG, "-play_video-" + "play_video:--------" + currentVideoProgress);
            if (!selectfromuser) {
                try {
                    mp.reset();
                    mp.setDataSource(Path);//设置播放视频源
                    mp.prepare();
                    mp.seekTo(currentVideoProgress);
                    BaseApp.isVideopauseUSB = 0;
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                selectfromuser = false;
                mp.stop();
                mp.reset();
                try {
                    mp.setDataSource(Path);
                    mp.prepare();
                    mp.seekTo(currentVideoProgress);
                    BaseApp.isVideopauseUSB = 0;
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(videoUIUpdateListener!=null){
                videoUIUpdateListener.onVideoItemChange();
                videoUIUpdateListener.onVideoNotifyUIChange(0);
            }
        }else if(BaseApp.playSourceManager == 3){
            //对播放视频状态进行更改
            currentVideoTotalTimenum = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getDuration();
            seekBar2.setMax((int) currentVideoTotalTimenum);
            currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);
            video_total_time.setText(currentVideoTotalTime);
            currentVideoName = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getDisplay_name();
            play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));

            currentVideoProgress = (int) BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getVideo_item_progressed();
            BaseUtils.mlog(TAG, "-play_video-" + "play_video:--------" + currentVideoProgress);
            if (!selectfromuser) {
                try {
                    mp.reset();
                    mp.setDataSource(Path);//设置播放视频源
                    mp.prepare();
                    mp.seekTo(currentVideoProgress);
                    BaseApp.isVideopauseSD = 0;
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                selectfromuser = false;
                mp.stop();
                mp.reset();
                try {
                    mp.setDataSource(Path);
                    mp.prepare();
                    mp.seekTo(currentVideoProgress);
                    BaseApp.isVideopauseSD  = 0;
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(videoUIUpdateListener!=null){
                videoUIUpdateListener.onVideoItemChange();
                videoUIUpdateListener.onVideoNotifyUIChange(0);
            }
        }
//        surfaceView_image.setVisibility(View.GONE);
    }

    public void start(){
        if(mp!=null && (!mp.isPlaying())){
            mp.start();//继续播放
            if(BaseApp.playSourceManager == 0) {
                BaseApp.isVideopauseUSB = 0;
            }else if(BaseApp.playSourceManager == 3){
                BaseApp.isVideopauseSD = 0;
            }
        }
    }
    public void pause(){
        BaseUtils.mlog(TAG, "-pause-" + "pause" + mp.getCurrentPosition());
        mp.pause();

        if(BaseApp.playSourceManager == 0) {
            BaseApp.isVideopauseUSB = 1;
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.isVideopauseSD = 1;
        }
    }

    public boolean isPlaying(){
        if(mp !=null){
            return mp.isPlaying();
        }
        return false;
    }

    class MyVideoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 1:
                    if(mp!=null && isPlaying()){
                        currentVideoProgress = mp.getCurrentPosition();
                        if(BaseApp.playSourceManager == 0) {
                            BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).setVideo_item_progressed(currentVideoProgress);
                            if(videoUIUpdateListener != null){
                                videoUIUpdateListener.onVideoProgressSave();
                            }
                        }else if(BaseApp.playSourceManager == 3){
                            BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).setVideo_item_progressed(currentVideoProgress);
                            if(videoUIUpdateListener != null){
                                videoUIUpdateListener.onVideoProgressSave();
                            }
                        }
                        seekBar2.setProgress(currentVideoProgress);
                        video_current_time.setText(Mp4MediaUtils.formatTime(currentVideoProgress));

                        if(BaseApp.ifliebiaoOpen == 0 && ishide && !BaseApp.ifFullScreenState){
                            fullScreen++;
                            if(fullScreen > 4 && !BaseApp.pre_show){
                                BaseApp.pre_show = true;
                                videoUIUpdateListener.onVideoScreenChangepre(mp.getCurrentPosition());
                            }
                            if(fullScreen >= 12){  //6s再去全屏
                                fullScreen = 0;
                                BaseApp.pre_show = false;
                                BaseApp.ifFullScreenState = true;
                                videoUIUpdateListener.onVideoScreenChange(mp.getCurrentPosition());
                            }
                        }else{
                            fullScreen = 0;
                        }
                    }
                    break;
            }
        }
    }



    public interface VideoUIUpdateListener{
        public void onVideoProgressSave();
        public void onVideoStateChange();
        public void onVideoScreenChange(int progress);
        public void onVideoScreenChangepre(int progress);
        public void onVideoLieBiaoClose();
        public void onVideoNotifyUIChange(int ifstop);
        public void onVideoNotifyUIliebiaoChange();
        public void onVideoItemChange();
        public void onVideoCancelCover();
    }
}