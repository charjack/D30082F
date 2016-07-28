package com.wedesign.mediaplayer;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.Utils.Mp4MediaUtils;
import com.wedesign.mediaplayer.vo.Contents;
import com.wedesign.mediaplayer.vo.Mp4Info;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback{

    private static final String TAG = "VideoFragment";
    SurfaceView surfaceView_video;
    private SurfaceHolder holder;
    private MediaPlayer mp = null;
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
    public int fullScreen = 0;
    private View video_view;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        videoUIUpdateListener = (VideoUIUpdateListener)activity;
    }
    private volatile static VideoFragment video_instance = null;
    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment getInstance(Context context){
        if(video_instance == null){
            synchronized (VideoFragment.class) {
                if (video_instance == null){
                    video_instance = new VideoFragment();
                }else{
                    BaseUtils.mlog(TAG,"已经存在相应的实例了");
                }
            }
        }
        return video_instance;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        surfaceView_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreen = 0;
                BaseApp.pre_show = false;
                if (!BaseApp.ifFullScreenState) {
                    if (BaseApp.ifliebiaoOpen == 0) {
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
                    } else {// if(BaseApp.ifopenliebiao == 1)
                        BaseApp.ifliebiaoOpen = 0;
                        videoUIUpdateListener.onVideoLieBiaoClose();
                        if (!ishide) {
                            //hide video name and controller
                            play_video_name.setVisibility(View.GONE);
                            progress_really_layout.setVisibility(View.GONE);
                            ishide = true;
                        }
                    }
                } else {
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
        BaseUtils.mlog(TAG,"onCreateView START");
//            if(video_view != null) {
//                return null;
//            } else {
                video_view = inflater.inflate(R.layout.fragment_video, container, false);
                surfaceView_video = (SurfaceView) video_view.findViewById(R.id.surfaceView_video);
                play_video_name = (TextView) video_view.findViewById(R.id.play_video_name);

                progress_really_layout = (LinearLayout) video_view.findViewById(R.id.progress_really_layout);
                seekBar2 = (SeekBar) video_view.findViewById(R.id.seekBar2);
                seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            fullScreen = 0;
                            BaseUtils.mlog(TAG, "-onCreateView-person change the progress...");
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
                video_current_time = (TextView) video_view.findViewById(R.id.video_current_time);
                video_total_time = (TextView) video_view.findViewById(R.id.video_total_time);
                seekBar2.setProgress(0);
                seekBar2.setMax((int) currentVideoTotalTimenum);
                video_total_time.setText(currentVideoTotalTime);
                myVideoHandler = new MyVideoHandler();
                holder = surfaceView_video.getHolder();
                holder.setFormat(PixelFormat.TRANSPARENT);
                holder.addCallback(this);
                surfaceView_video.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                if (mp == null) {
                    mp = new MediaPlayer();
                    //请求音频焦点
//                    requetVideoFocus();
                }
                BaseUtils.mlog(TAG, "-onCreateView-enter videoView creat...");
                BaseUtils.mlog(TAG, "onCreateView END");
                return video_view;
//            }
    }

    public void requetVideoFocus(){
        AudioManager am = (AudioManager)BaseApp.appContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (BaseApp.playSourceManager == 0 || BaseApp.playSourceManager == 3) {//手机端切换本地和蓝牙播放出问题了,音频获得焦点自动播放了
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:  //获得焦点
                            BaseUtils.mlog(TAG, "-play-video获得焦点");
                            //bt -》MP3-》MP4-》音乐服务获取焦点，播放音乐了
                            if (BaseApp.current_fragment == 1) {
                                BaseUtils.mlog(TAG, "-play-videocurrent_fragment == 0");
                                if (mp.isPlaying()) {
                                    mp.setVolume(1.0f, 1.0f);
                                } else {
                                    mp.setVolume(1.0f, 1.0f);
                                    if (BaseApp.playSourceManager == 0) {
                                        if (BaseApp.ispauseUSB != 1) {
                                            mp.start();
                                        }
                                    } else if (BaseApp.playSourceManager == 3) {
                                        if (BaseApp.ispauseSD != 1) {
                                            mp.start();
                                        }
                                    }
                                }
                                videoUIUpdateListener.onVideoNotifyUIChange(0);
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS://长时间失去焦点
                            BaseUtils.mlog(TAG, "video长时间失去焦点");
                            //只有退出了界面才去判定长时间丢失焦点
                            if (BaseApp.current_fragment == 1) {
                                if (mp != null && BaseApp.exitUI) {
                                    if (mp.isPlaying()) {
                                        mp.pause();
                                    }
                                    videoUIUpdateListener.onVideoNotifyUIChange(1);
                                }
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去，很快重新获取，可以保留资源
                            BaseUtils.mlog(TAG, "-play-video暂时失去，很快重新获取，可以保留资源");
                            if (BaseApp.current_fragment == 1) {
                                if (mp != null) {
                                    if (mp.isPlaying()) {
                                        mp.setVolume(0.08f, 0.08f);
                                    }
                                }
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://暂时失去焦点，声音降低，但还是在播放
                            BaseUtils.mlog(TAG, "-play-video暂时失去焦点，声音降低，但还是在播放");
                            if (BaseApp.current_fragment == 1) {
                                if (mp != null) {
                                    if (mp.isPlaying()) {
                                        mp.setVolume(0.08f, 0.08f);
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        BaseUtils.mlog(TAG,"surfaceCreated-----");
        if(mp ==null){
            mp = new MediaPlayer();
        }

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setDisplay(holder);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (BaseApp.playSourceManager == 0) {
                    //当前视频播放完之后，记录播放时间为0
                    BaseApp.current_video_play_progressUSB = 0;
//                    BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).setVideo_item_progressed(0);
                    if ((BaseApp.current_video_play_numUSB + 1 >= BaseApp.mp4Infos.size()) && BaseApp.video_play_mode == 1) {
                        BaseApp.isVideopauseUSB = 2;
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
                                calulator_radom_numberNext();
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
                            playVideonext();
                        }
                    }
                    videoUIUpdateListener.onVideoNotifyUIliebiaoChange();
                } else if (BaseApp.playSourceManager == 3) {
                    //当前视频播放完之后，记录播放时间为0
                    BaseApp.current_video_play_progressSD = 0;
                    //0 随机播放 1顺序播放 2循环播放 3单曲播放

                    if ((BaseApp.current_video_play_numSD + 1 >= BaseApp.mp4InfosSD.size()) && BaseApp.video_play_mode == 1) {
                        BaseApp.isVideopauseSD = 2;
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
                                calulator_radom_numberNextSD();  //计算出随机后的数字
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
                            playVideonext();
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

        BaseUtils.mlog(TAG, "-surfaceCreated-currentVideoTotalTime----" + currentVideoTotalTime);
        //第一次进入无法显示进度条和总时长
        seekBar2.setMax((int) currentVideoTotalTimenum);
        video_total_time.setText(currentVideoTotalTime);
        play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));
        if(BaseApp.playSourceManager == 0) {
            currentVideoProgress = BaseApp.current_video_play_progressUSB;
        }else if(BaseApp.playSourceManager == 3){
            currentVideoProgress = BaseApp.current_video_play_progressSD;
        }
        if(timer!=null){
            timer.cancel();
            timer = null;
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
        BaseUtils.mlog(TAG,"surfaceDestroyed-----");
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
            currentVideoProgress = BaseApp.current_video_play_progressUSB;
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_video_play_numSD = position;
            currentVideoProgress = BaseApp.current_video_play_progressSD;
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
            BaseApp.current_video_play_progressUSB = 0;
            if(BaseApp.video_play_mode == 0  && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED){
                calulator_radom_numberPre();
            }else {
                if (BaseApp.current_video_play_numUSB - 1 < 0) {
                    BaseApp.current_video_play_numUSB = BaseApp.mp4Infos.size() - 1;
                } else {
                    BaseApp.current_video_play_numUSB = BaseApp.current_video_play_numUSB - 1;
                }
            }
            mp4Info = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_video_play_progressSD = 0;
            if(BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateSD== Contents.SD_DEVICE_STATE_SCANNER_FINISHED){
                calulator_radom_numberPreSD();
            }else {
                if (BaseApp.current_video_play_numSD - 1 < 0) {
                    BaseApp.current_video_play_numSD = BaseApp.mp4InfosSD.size() - 1;
                } else {
                    BaseApp.current_video_play_numSD = BaseApp.current_video_play_numSD - 1;
                }
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
            BaseApp.current_video_play_progressUSB = 0;
            if(BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateUSB== Contents.USB_DEVICE_STATE_SCANNER_FINISHED){
                calulator_radom_numberNext();
            }else {
                if (BaseApp.current_video_play_numUSB + 1 >= BaseApp.mp4Infos.size()) {
                    BaseApp.current_video_play_numUSB = 0;
                } else {
                    BaseApp.current_video_play_numUSB = BaseApp.current_video_play_numUSB + 1;
                }
            }
            mp4Info = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_video_play_progressSD = 0;
            if(BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateSD== Contents.SD_DEVICE_STATE_SCANNER_FINISHED){
                calulator_radom_numberNextSD();
            }else {
                if (BaseApp.current_video_play_numSD + 1 >= BaseApp.mp4InfosSD.size()) {
                    BaseApp.current_video_play_numSD = 0;
                } else {
                    BaseApp.current_video_play_numSD = BaseApp.current_video_play_numSD + 1;
                }
            }
            mp4Info = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD);
        }
        currentVideoPath = mp4Info.getData();
        play_video(currentVideoPath);
    }

    @Override
    public void onResume() {
        super.onResume();
    //    requetVideoFocus();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mp!=null && BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0){
            if(BaseApp.playSourceManager == 0) {
                BaseUtils.mlog(TAG, "enter video Onstop" + BaseApp.current_video_play_progressUSB);
            }else if(BaseApp.playSourceManager == 3){
                BaseUtils.mlog(TAG, "enter video Onstop" + BaseApp.current_video_play_progressSD);
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
        if(BaseApp.brake_flag) {  //为真播放
            if (BaseApp.playSourceManager == 0) {
                //对播放视频状态进行更改
                currentVideoTotalTimenum = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getDuration();
                seekBar2.setMax((int) currentVideoTotalTimenum);
                currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);
                video_total_time.setText(currentVideoTotalTime);
                currentVideoName = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getDisplay_name();
                play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));
                currentVideoProgress = BaseApp.current_video_play_progressUSB;
                BaseUtils.mlog(TAG, "-play_video-currentVideoProgress--------" + currentVideoProgress);
                if (!selectfromuser) {
                    try {
                        if (mp == null) {
                            mp = new MediaPlayer();
                        }
                        mp.reset();
                        mp.setDataSource(Path);//设置播放视频源
                        mp.prepare();
                        mp.seekTo(currentVideoProgress);
                        BaseApp.isVideopauseUSB = 0;
                        if (videoUIUpdateListener != null) {  //如果凡在video的开始，当视频比较大，准备时间就比较长了。还是会出现闪背景的现象
                            videoUIUpdateListener.onVideoCancelCover();
                        }
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        playVideonext();
                    }
                } else {
                    selectfromuser = false;
                    if (mp == null) {
                        mp = new MediaPlayer();
                    }
             //       mp.stop();
                    mp.reset();
                    try {
                        mp.setDataSource(Path);
                        mp.prepare();
                        mp.seekTo(currentVideoProgress);
                        BaseApp.isVideopauseUSB = 0;
                        if (videoUIUpdateListener != null) {  //如果凡在video的开始，当视频比较大，准备时间就比较长了。还是会出现闪背景的现象
                            videoUIUpdateListener.onVideoCancelCover();
                        }
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        playVideonext();
                    }
                }
                if (videoUIUpdateListener != null) {
                    videoUIUpdateListener.onVideoItemChange();
                    videoUIUpdateListener.onVideoNotifyUIChange(0);
                }
            } else if (BaseApp.playSourceManager == 3) {
                //对播放视频状态进行更改
                currentVideoTotalTimenum = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getDuration();
                seekBar2.setMax((int) currentVideoTotalTimenum);
                currentVideoTotalTime = Mp4MediaUtils.formatTime(currentVideoTotalTimenum);
                video_total_time.setText(currentVideoTotalTime);
                currentVideoName = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getDisplay_name();
                play_video_name.setText(currentVideoName.substring(0, currentVideoName.indexOf(".")));

                currentVideoProgress = BaseApp.current_video_play_progressSD;
                BaseUtils.mlog(TAG, "play_video:--------" + currentVideoProgress);
                if (!selectfromuser) {
                    try {
                        mp.reset();
                        mp.setDataSource(Path);//设置播放视频源
                        mp.prepare();
                        mp.seekTo(currentVideoProgress);
                        BaseApp.isVideopauseSD = 0;
                        if (videoUIUpdateListener != null) {  //如果凡在video的开始，当视频比较大，准备时间就比较长了。还是会出现闪背景的现象
                            videoUIUpdateListener.onVideoCancelCover();
                        }
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        playVideonext();
                    }
                } else {
                    selectfromuser = false;
               //    mp.stop();
                    if (mp == null) {
                        mp = new MediaPlayer();
                    }
                    mp.reset();
                    try {
                        mp.setDataSource(Path);
                        mp.prepare();
                        mp.seekTo(currentVideoProgress);
                        BaseApp.isVideopauseSD = 0;
                        if (videoUIUpdateListener != null) {  //如果凡在video的开始，当视频比较大，准备时间就比较长了。还是会出现闪背景的现象
                            videoUIUpdateListener.onVideoCancelCover();
                        }
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                        playVideonext();
                    }
                }
                if (videoUIUpdateListener != null) {
                    videoUIUpdateListener.onVideoItemChange();
                    videoUIUpdateListener.onVideoNotifyUIChange(0);
                }
            }
        }else{
            videoUIUpdateListener.onCoverBrake(BaseApp.brake_flag);
        }
    }

    public void start(){
        if(mp!=null && (!mp.isPlaying())){
            if (videoUIUpdateListener != null) {  //如果凡在video的开始，当视频比较大，准备时间就比较长了。还是会出现闪背景的现象
                videoUIUpdateListener.onVideoCancelCover();
            }
            mp.start();//继续播放
            if(BaseApp.playSourceManager == 0) {
                BaseApp.isVideopauseUSB = 0;
            }else if(BaseApp.playSourceManager == 3){
                BaseApp.isVideopauseSD = 0;
            }
            if (videoUIUpdateListener != null) {
                videoUIUpdateListener.onVideoNotifyUIChange(0);
            }
        }
    }


    public void pause(){
        BaseUtils.mlog(TAG, "-pause-"+ mp.getCurrentPosition());
        mp.pause();

        if(BaseApp.playSourceManager == 0) {
            if(BaseApp.isVideopauseUSB == 0) {
                BaseApp.isVideopauseUSB = 1;
            }
        }else if(BaseApp.playSourceManager == 3){
            if(BaseApp.isVideopauseSD == 0) {
                BaseApp.isVideopauseSD = 1;
            }
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
                            BaseApp.current_video_play_progressUSB = currentVideoProgress;
                            if(videoUIUpdateListener != null){
                                videoUIUpdateListener.onVideoProgressSave();
                            }
                        }else if(BaseApp.playSourceManager == 3){
                            BaseApp.current_video_play_progressSD = currentVideoProgress;
                            if(videoUIUpdateListener != null){
                                videoUIUpdateListener.onVideoProgressSave();
                            }
                        }
                        seekBar2.setProgress(currentVideoProgress);
                        video_current_time.setText(Mp4MediaUtils.formatTime(currentVideoProgress));

                        if(BaseApp.ifliebiaoOpen == 0  && !BaseApp.ifFullScreenState){//&& ishide
                            fullScreen++;
                            if(fullScreen > 8 && !BaseApp.pre_show){
                                BaseApp.pre_show = true;
                                ishide = true;
                                play_video_name.setVisibility(View.GONE);
                                progress_really_layout.setVisibility(View.GONE);
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
                            BaseApp.pre_show = false;
                        }
                    }
                    break;
            }
        }
    }

    public void calulator_radom_numberNext(){
        if( BaseApp.mp4Infos!=null &&BaseApp.mp4Infos.size()>0) {
            if (BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED) {
                int shuffle_num = 0;
                for (int i = 0; i < BaseApp.mp4Infos.size(); i++) {
                    if (BaseApp.video_shuffle_list.get(i) == BaseApp.current_video_play_numUSB) {
                        shuffle_num = i;
                        break;
                    }
                }
                if (shuffle_num == BaseApp.video_shuffle_list.size() - 1) {
                    BaseApp.current_video_play_numUSB = BaseApp.video_shuffle_list.get(0);
                } else {
                    BaseApp.current_video_play_numUSB = BaseApp.video_shuffle_list.get(shuffle_num + 1);
                }
            } else {
                if (BaseApp.current_video_play_numUSB + 1 >= BaseApp.mp4Infos.size()) {
                    BaseApp.current_video_play_numUSB = 0;
                } else {
                    BaseApp.current_video_play_numUSB++;
                }
            }
        }
    }

    public void calulator_radom_numberPre(){
        if( BaseApp.mp4Infos!=null &&BaseApp.mp4Infos.size()>0) {
            if (BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED) {
                int shuffle_num = 0;
                for (int i = 0; i < BaseApp.mp4Infos.size(); i++) {
                    if (BaseApp.video_shuffle_list.get(i) == BaseApp.current_video_play_numUSB) {
                        shuffle_num = i;
                        break;
                    }
                }
                if (shuffle_num == 0) {
                    BaseApp.current_video_play_numUSB = BaseApp.video_shuffle_list.get(BaseApp.video_shuffle_list.size() - 1);
                } else {
                    BaseApp.current_video_play_numUSB = BaseApp.video_shuffle_list.get(shuffle_num - 1);
                }
            } else {
                if (BaseApp.current_video_play_numUSB == 0) {
                    BaseApp.current_video_play_numUSB = BaseApp.mp4Infos.size() - 1;
                } else {
                    BaseApp.current_video_play_numUSB--;
                }
            }
        }
    }

    public void calulator_radom_numberNextSD(){
        if( BaseApp.mp4InfosSD!=null &&BaseApp.mp4InfosSD.size()>0) {
            if (BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED) {
                int shuffle_numSD = 0;
                for (int i = 0; i < BaseApp.mp4InfosSD.size(); i++) {
                    if (BaseApp.video_shuffle_listSD.get(i) == BaseApp.current_video_play_numSD) {
                        shuffle_numSD = i;
                        break;
                    }
                }
                if (shuffle_numSD == BaseApp.video_shuffle_listSD.size() - 1) {
                    BaseApp.current_video_play_numSD = BaseApp.video_shuffle_listSD.get(0);
                } else {
                    BaseApp.current_video_play_numSD = BaseApp.video_shuffle_listSD.get(shuffle_numSD + 1);
                }
            } else {
                if (BaseApp.current_video_play_numSD + 1 >= BaseApp.mp4InfosSD.size()) {
                    BaseApp.current_video_play_numSD = 0;
                } else {
                    BaseApp.current_video_play_numSD++;
                }
            }
        }
    }

    public void calulator_radom_numberPreSD(){
        if( BaseApp.mp4InfosSD!=null &&BaseApp.mp4InfosSD.size()>0) {
            if (BaseApp.video_play_mode == 0 && MainActivity.mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED) {
                int shuffle_numSD = 0;
                for (int i = 0; i < BaseApp.mp4InfosSD.size(); i++) {
                    if (BaseApp.video_shuffle_listSD.get(i) == BaseApp.current_video_play_numSD) {
                        shuffle_numSD = i;
                        break;
                    }
                }
                if (shuffle_numSD == 0) {
                    BaseApp.current_video_play_numSD = BaseApp.video_shuffle_listSD.get(BaseApp.video_shuffle_listSD.size() - 1);
                } else {
                    BaseApp.current_video_play_numSD = BaseApp.video_shuffle_listSD.get(shuffle_numSD - 1);
                }
            } else {
                if (BaseApp.current_video_play_numSD == 0) {
                    BaseApp.current_video_play_numSD = BaseApp.mp4InfosSD.size() - 1;
                } else {
                    BaseApp.current_video_play_numSD--;
                }
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
        public void onCoverBrake(boolean brake_flag);
    }
}