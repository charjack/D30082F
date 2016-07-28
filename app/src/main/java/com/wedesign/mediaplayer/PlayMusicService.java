package com.wedesign.mediaplayer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.vo.Contents;
import com.wedesign.mediaplayer.vo.Mp3Info;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayMusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener{
    private static final String TAG = "PlayMusicService";
    private MediaPlayer mPlayer;

    public static final int RANDOM_PLAY = 0;  //随机
    public static final int ORDER_PLAY = 1;   //顺序
    public static final int RECELY_PLAY = 2;  //全部循环
    public static final int SINGLE_PLAY = 3;  //单曲循环

    public MusicUpdateListener musicUpdateListener;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    public PlayMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        requestMusicFocus();
        es.execute(updateStatusRunnable);
    }

    public void requestMusicFocus(){
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (BaseApp.playSourceManager == 0 || BaseApp.playSourceManager == 3) {//手机端切换本地和蓝牙播放出问题了,音频获得焦点自动播放了
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:  //获得焦点
                            BaseUtils.mlog(TAG, "-play-music获得焦点");
                            //bt -》MP3-》MP4-》音乐服务获取焦点，播放音乐了
                            if (BaseApp.current_fragment == 0 || BaseApp.current_fragment == 2) {
                                BaseUtils.mlog(TAG, "-play-music_current_fragment == 0");
                                if (mPlayer.isPlaying()) {
                                    mPlayer.setVolume(1.0f, 1.0f);
                                } else {
                                    mPlayer.setVolume(1.0f, 1.0f);
                                    if (BaseApp.playSourceManager == 0) {
                                        if (BaseApp.ispauseUSB != 1) {
                                            mPlayer.start();
                                            musicUpdateListener.onLocalMusicChangeBofangIcon(false);  //只有不等于1，采取改变图标。
                                        }
                                    } else if (BaseApp.playSourceManager == 3) {
                                        if (BaseApp.ispauseSD != 1) {
                                            mPlayer.start();
                                            musicUpdateListener.onLocalMusicChangeBofangIcon(false);
                                        }
                                    }
                                }

                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS://长时间失去焦点
                            BaseUtils.mlog(TAG, "music长时间失去焦点");
                            if (BaseApp.current_fragment == 0 || BaseApp.current_fragment == 2){
                                //只有退出了界面才去判定长时间丢失焦点
                                if (mPlayer != null && BaseApp.exitUI) {
                                    if (mPlayer.isPlaying()) {
                                        mPlayer.pause();
                                    }
                                    musicUpdateListener.onLocalMusicChangeBofangIcon(true);
                                }
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://暂时失去，很快重新获取，可以保留资源
                            BaseUtils.mlog(TAG, "-play-music暂时失去，很快重新获取，可以保留资源");
                            float mixVoice = (float)(getMixVoice(BaseApp.appContext)/100.0);
                            if (BaseApp.current_fragment == 0 || BaseApp.current_fragment == 2) {
                                if (mPlayer != null) {
                                    if (mPlayer.isPlaying()) {
                                        mPlayer.setVolume(mixVoice, mixVoice);
                                    }
                                }
                            }
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://暂时失去焦点，声音降低，但还是在播放
                            BaseUtils.mlog(TAG, "-play-music暂时失去焦点，声音降低，但还是在播放");
                            float mixVoice2 = (float)(getMixVoice(BaseApp.appContext)/100.0);
                            if (BaseApp.current_fragment == 0 || BaseApp.current_fragment == 2) {
                                if (mPlayer != null) {
                                    if (mPlayer.isPlaying()) {
                                        mPlayer.setVolume(mixVoice2, mixVoice2);
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }


    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                if(musicUpdateListener!=null &&mPlayer!=null&& mPlayer.isPlaying()){
                    if(BaseApp.playSourceManager == 0){
                        BaseApp.current_music_play_progressUSB = getcurrentProgress();
                    }else if(BaseApp.playSourceManager == 3){
                        BaseApp.current_music_play_progressSD = getcurrentProgress();
                    }
                    musicUpdateListener.onLocalMusicRefreshProgress(getcurrentProgress());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void seek (int msec){
        if(mPlayer != null)
            mPlayer.seekTo(msec);
    }

    public boolean isPlaying(){
        if(mPlayer !=null){
            return mPlayer.isPlaying();
        }
        return false;
    }

    public void resetMusic(){
        if(mPlayer !=null){
             mPlayer.reset();
        }
    }

    public void pause(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            if(BaseApp.ispauseUSB == 2){
            }else{
                //被暂停了
                BaseApp.ispauseUSB = 1;
            }
            BaseApp.current_music_play_progressUSB= mPlayer.getCurrentPosition();
            BaseUtils.mlog(TAG,"pauseUSB"+BaseApp.ispauseUSB);
        }
    }
    public void pauseSD(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
            if(BaseApp.ispauseSD == 2){
            }else{
                BaseApp.ispauseSD = 1;
            }
            BaseApp.current_music_play_progressSD = mPlayer.getCurrentPosition();
            BaseUtils.mlog(TAG,"pauseSD"+BaseApp.ispauseSD);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(es!=null&& !es.isShutdown()){
            es.shutdown();
            es=null;
        }
    }

    class PlayMusicBinder extends Binder {
        public PlayMusicService getPlayService(){
            return PlayMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayMusicBinder();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(BaseApp.playSourceManager == 0) {
            BaseApp.current_music_play_progressUSB = 0;
            BaseApp.ispauseUSB = 2;
            if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size()>0) {
                switch (BaseApp.music_play_mode) {
                    case RANDOM_PLAY:
                        BaseApp.current_music_play_progressUSB = 0;
                        //10  4   0~3    0~4+5
                        if (BaseApp.music_play_mode == 0 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED) {
                            int shuffle_num = 0;
                            for (int i = 0; i < BaseApp.mp3Infos.size(); i++) {
                                if (BaseApp.music_shuffle_list.get(i) == BaseApp.current_music_play_numUSB) {
                                    shuffle_num = i;
                                    break;
                                }
                            }
                            if (shuffle_num == BaseApp.music_shuffle_list.size() - 1) {
                                BaseApp.current_music_play_numUSB = BaseApp.music_shuffle_list.get(0);
                            } else {
                                BaseApp.current_music_play_numUSB = BaseApp.music_shuffle_list.get(shuffle_num + 1);
                            }
                            playUSB(BaseApp.current_music_play_numUSB);
                        } else {
                            if (BaseApp.current_music_play_numUSB + 1 >= BaseApp.mp3Infos.size()) {
                                BaseApp.current_music_play_numUSB = 0;
                            } else {
                                BaseApp.current_music_play_numUSB++;
                            }
                            playUSB(BaseApp.current_music_play_numUSB);
                        }
                        break;
                    case ORDER_PLAY:
                        nextOrder(); //判断最后一首歌的时候停止
                        break;
                    case RECELY_PLAY:
                        next();
                        break;
                    case SINGLE_PLAY:
                        playUSB(BaseApp.current_music_play_numUSB);
                        break;
                    default:
                        break;
                }
            }
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_music_play_progressSD = 0;
            BaseApp.ispauseSD = 2;
            if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size()>0) {
                switch (BaseApp.music_play_mode) {
                    case RANDOM_PLAY:
                        BaseApp.current_music_play_progressSD = 0;
                        if (MainActivity.mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED) {
                            int shuffle_numSD = 0;
                            for (int i = 0; i < BaseApp.mp3InfosSD.size(); i++) {
                                if (BaseApp.music_shuffle_listSD.get(i) == BaseApp.current_music_play_numSD) {
                                    shuffle_numSD = i;
                                    break;
                                }
                            }
                            if (shuffle_numSD == BaseApp.music_shuffle_listSD.size() - 1) {
                                BaseApp.current_music_play_numSD = BaseApp.music_shuffle_listSD.get(0);
                            } else {
                                BaseApp.current_music_play_numSD = BaseApp.music_shuffle_listSD.get(shuffle_numSD + 1);
                            }
                            playSD(BaseApp.current_music_play_numSD);
                        } else {
                            if (BaseApp.current_music_play_numSD + 1 >= BaseApp.mp3InfosSD.size()) {
                                BaseApp.current_music_play_numSD = 0;
                            } else {
                                BaseApp.current_music_play_numSD++;
                            }
                            playSD(BaseApp.current_music_play_numSD);
                        }
                        break;
                    case ORDER_PLAY:
                        nextOrderSD(); //判断最后一首歌的时候停止
                        break;
                    case RECELY_PLAY:
                        nextSD();
                        break;
                    case SINGLE_PLAY:
                        playSD(BaseApp.current_music_play_numSD);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void playUSB(int position){
        if(BaseApp.current_fragment!=1) {  //扫描u盘，点击视频播放，有音乐声音
            mPlayer.setVolume(1.0f, 1.0f);
            BaseUtils.mlog(TAG, "当前的播放状态----" + BaseApp.ispauseUSB);
            BaseUtils.mlog(TAG, "当前的播放时长--" + BaseApp.current_music_play_progressUSB);
            Mp3Info mp3Info = null;
            mp3Info = BaseApp.mp3Infos.get(position);
            try {
                mPlayer.reset();
                mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mPlayer.prepare();
                mPlayer.seekTo(BaseApp.current_music_play_progressUSB);
                if (BaseApp.ispauseUSB == 0 || BaseApp.ispauseUSB == 2) {
                    mPlayer.start();
                    BaseApp.ispauseUSB = 0;
                } else {
                    mPlayer.start();
                    mPlayer.pause();
                }
                BaseApp.current_music_play_numUSB = position;
            } catch (IOException e) {
                BaseUtils.mlog(TAG, "USB的音乐播放出错了");
                e.printStackTrace();
                next();
            }

            if (musicUpdateListener != null) {
                BaseUtils.mlog(TAG, "currentposition-----" + BaseApp.current_music_play_numUSB);
                musicUpdateListener.onLocalMusicChangeItem(BaseApp.current_music_play_numUSB);
            }
         //   requestMusicFocus();//最终还是不能在这里使用这个函数，当不在多媒体界面的时候，歌曲切换回自动的丢失焦点了，由此而来的后果是视频无法做音频焦点处理
        }
    }
//一个appID只有一个音频焦点，当切换至蓝牙后，当前本地的音频焦点就被覆盖了，重启返回来的时候需要重新申请焦点
    public void playSD(int position){
        BaseUtils.mlog(TAG, "ispauseSD===="+BaseApp.ispauseSD);
        if(BaseApp.current_fragment!=1) {
            mPlayer.setVolume(1.0f, 1.0f);  //这样在内部处理，无法监控蓝牙声音，最好的处理时通过外部常驻服务控制音量大小
            Mp3Info mp3Info = null;
            mp3Info = BaseApp.mp3InfosSD.get(position);
            try {
                mPlayer.reset();
                mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mPlayer.prepare();
                mPlayer.seekTo(BaseApp.current_music_play_progressSD);
                if (BaseApp.ispauseSD == 0 || BaseApp.ispauseSD == 2) {
                    mPlayer.start();
                    BaseApp.ispauseSD = 0;
                } else {
                    mPlayer.start();
                    mPlayer.pause();
                }
                BaseApp.current_music_play_numSD = position;
            } catch (IOException e) {
                BaseUtils.mlog(TAG, "SD的音乐播放出错了");
                e.printStackTrace();
                nextSD();
            }
            if (musicUpdateListener != null) {
                BaseUtils.mlog(TAG, "currentposition-----" + BaseApp.current_music_play_numSD);
                musicUpdateListener.onLocalMusicChangeItem(BaseApp.current_music_play_numSD);
            }
      //      requestMusicFocus();
        }
    }

    public void next(){
        BaseUtils.mlog(TAG, "next-----" + BaseApp.music_shuffle_list.toString());
        BaseUtils.mlog(TAG, "next-----" + BaseApp.music_shuffle_list.size());
        BaseApp.current_music_play_progressUSB = 0;
        if(BaseApp.music_play_mode == 0 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED){
            int shuffle_num=0;
            for(int i = 0;i<BaseApp.mp3Infos.size();i++){
                if(BaseApp.music_shuffle_list.get(i) == BaseApp.current_music_play_numUSB){
                    shuffle_num = i;
                    break;
                }
            }
            if(shuffle_num == BaseApp.music_shuffle_list.size() - 1){
                BaseApp.current_music_play_numUSB = BaseApp.music_shuffle_list.get(0);
            }else {
                BaseApp.current_music_play_numUSB = BaseApp.music_shuffle_list.get(shuffle_num + 1);
            }
            playUSB(BaseApp.current_music_play_numUSB);
        }else {
            if (BaseApp.current_music_play_numUSB + 1 >= BaseApp.mp3Infos.size()) {
                BaseApp.current_music_play_numUSB = 0;
            } else {
                BaseApp.current_music_play_numUSB++;
            }
            playUSB(BaseApp.current_music_play_numUSB);
        }
    }
    public void nextSD(){
        BaseUtils.mlog(TAG, "nextSD-----" + BaseApp.music_shuffle_listSD.toString()+"---BaseApp.mp3InfosSD.size()"+BaseApp.mp3InfosSD.size());
        BaseApp.current_music_play_progressSD = 0;
        BaseApp.current_music_play_progressSD = 0;
        if(BaseApp.music_play_mode == 0 && MainActivity.mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED){
            int shuffle_numSD=0;
            for(int i = 0;i<BaseApp.mp3InfosSD.size();i++){
                if(BaseApp.music_shuffle_listSD.get(i) == BaseApp.current_music_play_numSD){
                    shuffle_numSD = i;
                    break;
                }
            }
            if(shuffle_numSD == BaseApp.music_shuffle_listSD.size()-1){
                BaseApp.current_music_play_numSD = BaseApp.music_shuffle_listSD.get(0);
            }else {
                BaseApp.current_music_play_numSD = BaseApp.music_shuffle_listSD.get(shuffle_numSD + 1);
            }
            playSD(BaseApp.current_music_play_numSD);
        }else {
            if (BaseApp.current_music_play_numSD + 1 >= BaseApp.mp3InfosSD.size()) {
                BaseApp.current_music_play_numSD = 0;
            } else {
                BaseApp.current_music_play_numSD++;
            }
            playSD(BaseApp.current_music_play_numSD);
        }
    }

    public void nextOrder(){
        BaseApp.current_music_play_progressUSB = 0;
        if(BaseApp.current_music_play_numUSB+1 >= BaseApp.mp3Infos.size()){
            mPlayer.pause();
            musicUpdateListener.onLocalMusicStop(1);  //顺序播放，到最后一首暂停，不能停止，否则重新开始播放会出问题
        }else
        {
            BaseApp.current_music_play_numUSB++;
            playUSB(BaseApp.current_music_play_numUSB);
        }
    }

    public void nextOrderSD(){
        BaseApp.current_music_play_progressSD = 0;
        if(BaseApp.current_music_play_numSD+1 >= BaseApp.mp3InfosSD.size()){
            mPlayer.pause();
            musicUpdateListener.onLocalMusicStop(1);  //顺序播放，到最后一首暂停，不能停止，否则重新开始播放会出问题
        }else
        {
            BaseApp.current_music_play_numSD++;
            playSD(BaseApp.current_music_play_numSD);
        }
    }

    public void prev(){
        BaseUtils.mlog(TAG, "prev-----" + BaseApp.music_shuffle_list.toString());
        BaseApp.current_music_play_progressUSB = 0;
        if(BaseApp.music_play_mode == 0 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED){
            int shuffle_num=0;
            for(int i = 0;i<BaseApp.mp3Infos.size();i++){
                if(BaseApp.music_shuffle_list.get(i) == BaseApp.current_music_play_numUSB){
                    shuffle_num = i;
                    break;
                }
            }
            if(shuffle_num == 0){
                BaseApp.current_music_play_numUSB = BaseApp.music_shuffle_list.get(BaseApp.music_shuffle_list.size() - 1);
            }else {
                BaseApp.current_music_play_numUSB = BaseApp.music_shuffle_list.get(shuffle_num - 1);
            }
            playUSB(BaseApp.current_music_play_numUSB);
        }else{
            if(BaseApp.current_music_play_numUSB-1 < 0){
                BaseApp.current_music_play_numUSB = BaseApp.mp3Infos.size()-1;
            }else
            {
                BaseApp.current_music_play_numUSB--;
            }
        }
        playUSB(BaseApp.current_music_play_numUSB);
    }

    public void prevSD(){
        BaseUtils.mlog(TAG, "prevSD-----" + BaseApp.music_shuffle_listSD.toString());
        BaseApp.current_music_play_progressSD = 0;
        if(BaseApp.music_play_mode == 0 && MainActivity.mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED){
            int shuffle_numSD=0;
            for(int i = 0;i<BaseApp.mp3InfosSD.size();i++){
                if(BaseApp.music_shuffle_listSD.get(i) == BaseApp.current_music_play_numSD){
                    shuffle_numSD = i;
                    break;
                }
            }
            if(shuffle_numSD == 0){
                BaseApp.current_music_play_numSD = BaseApp.music_shuffle_listSD.get(BaseApp.music_shuffle_listSD.size() - 1);
            }else {
                BaseApp.current_music_play_numSD= BaseApp.music_shuffle_listSD.get(shuffle_numSD - 1);
            }
            playSD(BaseApp.current_music_play_numSD);
        }else {
            if (BaseApp.current_music_play_numSD - 1 < 0) {
                BaseApp.current_music_play_numSD = BaseApp.mp3InfosSD.size() - 1;
            } else {
                BaseApp.current_music_play_numSD--;
            }
            playSD(BaseApp.current_music_play_numSD);
        }
    }

    public void start_play(){
        if(mPlayer!=null && (!mPlayer.isPlaying())){
            BaseUtils.mlog(TAG, "start_play");

            if(BaseApp.playSourceManager == 0 && BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size()>0) {
                BaseApp.ispauseUSB = 0;
                mPlayer.seekTo(BaseApp.current_music_play_progressUSB);
                mPlayer.start();//继续播放
            }else if(BaseApp.playSourceManager == 3 && BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size()>0){
                BaseApp.ispauseSD = 0;
                mPlayer.seekTo(BaseApp.current_music_play_progressSD);
                mPlayer.start();//继续播放
            }
        }
    }
    public int getcurrentProgress(){  //返回当前的播放进度
        if(mPlayer!=null && mPlayer.isPlaying()){
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration(){
        if(mPlayer!=null && mPlayer.isPlaying()){
            return mPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        BaseUtils.mlog(TAG, "onError音乐播放出错了");
        mediaPlayer.reset();
        return false;
    }

    public int getMixVoice(Context c){
        BaseUtils.mlog(TAG, "-------------getMixVoice-------------");
        int value = Contents.USB_STATE_UNMOUNTED;
        try {
            Context otherContext = c.createPackageContext(
                    "com.wedesign.sourcemanager",
                    Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = otherContext.getSharedPreferences(
                    "com.wedesign.sourcemanager", Context.MODE_WORLD_READABLE
                            + Context.MODE_WORLD_WRITEABLE
                            + Context.MODE_MULTI_PROCESS);
            value = sp.getInt("mix_settings_voice", 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        BaseUtils.mlog(TAG, "getMixVoice: value = " + value);
        return value;
    }


    public interface MusicUpdateListener{
        public void onLocalMusicRefreshProgress(int progress);
        public void onLocalMusicChangeItem(int position);
        public void onLocalMusicStop(int isstop);
        public void onLocalMusicChangeBofangIcon(boolean isstop);  //isstop为真 暂停
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener){
        this.musicUpdateListener = musicUpdateListener;
    }
}
