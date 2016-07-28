package com.wedesign.mediaplayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.Utils.MediaUtils;
import com.wedesign.mediaplayer.vo.Contents;
import com.wedesign.mediaplayer.vo.Mp3Info;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment {
    private static final String TAG = "MusicFragment";
    public int[] music_play_mode_resource_ico = {R.mipmap.suiji_ico,R.mipmap.shunxu_ico,R.mipmap.quanbuxunhuan_ico,R.mipmap.danquxunhuan_ico};
    public int[] button_play_mode_name_ico = {R.string.suijibofang,R.string.quanbubofang,R.string.quanbuxunhuan,R.string.danqubofang};
    ImageView button_play_mode_ico;
    TextView button_play_mode_name;
    ImageView album_icon;
    TextView song_name,zhuanji_name,chuangzhe_name,num_order;
    LinearLayout song_info_layout;
    LinearLayout zhuanji_layout,singer_layout,order_layout;
    TextView bt_device_name;

    SeekBar seekBar1;
    LinearLayout progress_really_layout;
    TextView song_current_time,song_total_time;
    RelativeLayout mp3_info_ui;
//    LinearLayout come_from_layout;
//    ImageButton button_bluetooth,button_usb,button_aux,button_SDCard;

    public MusicUIUpdateListener musicUIUpdateListener;
    public View view = null;
    private volatile static MusicFragment music_instance = null;
    public MusicFragment() {
    }
    public static MusicFragment getInstance(Context context){
        if(music_instance == null){
            synchronized (MusicFragment.class) {
                if (music_instance == null){
                    music_instance = new MusicFragment();
                }else{
                    BaseUtils.mlog(TAG,"已经存在相应的实例了");
                }
            }
        }
        return music_instance;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        BaseUtils.mlog(TAG,"onAttach---");
        musicUIUpdateListener = (MusicUIUpdateListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BaseUtils.mlog(TAG,"------onCreateView-----");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_music, container, false);
        song_info_layout = (LinearLayout) view.findViewById(R.id.song_info_layout);
        bt_device_name = (TextView) view.findViewById(R.id.bt_device_name);
        zhuanji_layout = (LinearLayout) view.findViewById(R.id.zhuanji_layout);
        singer_layout = (LinearLayout) view.findViewById(R.id.singer_layout);
        order_layout = (LinearLayout) view.findViewById(R.id.order_layout);
        mp3_info_ui = (RelativeLayout) view.findViewById(R.id.mp3_info_ui);
        mp3_info_ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BaseApp.ifliebiaoOpen == 1) {
                    BaseApp.ifliebiaoOpen = 0;
                    musicUIUpdateListener.onLieBiaoClose();
                }
            }
        });
        button_play_mode_ico = (ImageView) view.findViewById(R.id.button_play_mode_ico);
        button_play_mode_name = (TextView) view.findViewById(R.id.button_play_mode_name);

        album_icon = (ImageView) view.findViewById(R.id.album_icon);

        song_name = (TextView) view.findViewById(R.id.song_name);
        song_name.requestFocus();
        zhuanji_name = (TextView) view.findViewById(R.id.zhuanji_name);
        chuangzhe_name = (TextView) view.findViewById(R.id.chuangzhe_name);
        num_order = (TextView) view.findViewById(R.id.num_order);

        progress_really_layout = (LinearLayout) view.findViewById(R.id.progress_really_layout);
        seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (BaseApp.playSourceManager == 0 && BaseApp.current_music_play_progressUSB > 0) {
                        BaseApp.current_music_play_progressUSB = progress;
                        seekBar.setProgress(progress);
                        musicUIUpdateListener.onServiceCommand(1);  //拖动
                    } else if (BaseApp.playSourceManager == 3 && BaseApp.current_music_play_progressSD > 0) {
                        BaseApp.current_music_play_progressSD = progress;
                        seekBar.setProgress(progress);
                        musicUIUpdateListener.onServiceCommand(1);  //拖动
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                musicUIUpdateListener.onServiceCommand(2);  //开始拖
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicUIUpdateListener.onServiceCommand(3);  //拖动结束
            }
        });
        song_current_time = (TextView) view.findViewById(R.id.song_current_time);
        song_total_time = (TextView) view.findViewById(R.id.song_total_time);

        initMusicFragment();

        if (musicUIUpdateListener != null) {
            musicUIUpdateListener.onMusicCancelCover();
            musicUIUpdateListener.onRequestFocus();
        }
        BaseUtils.mlog(TAG, "------onCreateView-----end");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseUtils.mlog(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseUtils.mlog(TAG, "onStop");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        BaseUtils.mlog(TAG, "onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseUtils.mlog(TAG, "onDestroy");
    }

     private void initMusicFragment(){//初始化，加载时候的文字和图片，在切换语言后，重新进入，会瞬时显示默认的如“视频名称”这样的，然后 才会跳转
         BaseUtils.mlog(TAG, "initMusicFragment-----");
         if(BaseApp.playSourceManager == 0){
             if(BaseApp.current_music_play_numUSB >=0 && BaseApp.mp3Infos!= null && BaseApp.mp3Infos.size()>0&& BaseApp.current_music_play_progressUSB >0){ //u盘数据已经读取完成了
                 Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                 Bitmap albumBitmap = MediaUtils.getArtwork(BaseApp.appContext, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
                 album_icon.setImageBitmap(albumBitmap);
                 song_name.setText(mp3Info.getTittle());
                 zhuanji_name.setText(mp3Info.getAlbum());
                 chuangzhe_name.setText(mp3Info.getArtist());
                 button_play_mode_ico.setImageResource(music_play_mode_resource_ico[BaseApp.music_play_mode]);
                 button_play_mode_name.setText(button_play_mode_name_ico[BaseApp.music_play_mode]);
                 num_order.setText((BaseApp.current_music_play_numUSB + 1) + "/" + BaseApp.mp3Infos.size());
                 song_total_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
                 seekBar1.setMax((int) mp3Info.getDuration());
             }
         }else if(BaseApp.playSourceManager == 3){
             if(BaseApp.current_music_play_numSD >=0 && BaseApp.mp3InfosSD!= null && BaseApp.mp3InfosSD.size()>0 && BaseApp.current_music_play_progressSD >0){ //u盘数据已经读取完成了
                 Mp3Info mp3InfoSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                 Bitmap albumBitmap = MediaUtils.getArtwork(BaseApp.appContext, mp3InfoSD.getId(), mp3InfoSD.getAlbumId(), true, false);
                 album_icon.setImageBitmap(albumBitmap);
                 song_name.setText(mp3InfoSD.getTittle());
                 zhuanji_name.setText(mp3InfoSD.getAlbum());
                 chuangzhe_name.setText(mp3InfoSD.getArtist());
                 button_play_mode_ico.setImageResource(music_play_mode_resource_ico[BaseApp.music_play_mode]);
                 button_play_mode_name.setText(button_play_mode_name_ico[BaseApp.music_play_mode]);
                 num_order.setText((BaseApp.current_music_play_numSD + 1) + "/" + BaseApp.mp3InfosSD.size());
                 song_total_time.setText(MediaUtils.formatTime(mp3InfoSD.getDuration()));
                 seekBar1.setMax((int) mp3InfoSD.getDuration());
             }
         }
     }

    void changeMusicPlayModeUI(int playmode){
        button_play_mode_ico.setImageResource(music_play_mode_resource_ico[playmode]);
        button_play_mode_name.setText(button_play_mode_name_ico[playmode]);
    }

    public interface MusicUIUpdateListener{
        public void onServiceCommand(int i);
        public void onLieBiaoClose();
        public void onMusicCancelCover();
        public void onRequestFocus();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        BaseUtils.mlog(TAG,"---onSaveInstanceState---");
    }
}
