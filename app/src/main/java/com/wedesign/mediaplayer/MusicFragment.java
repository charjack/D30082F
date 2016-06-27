package com.wedesign.mediaplayer;

import android.app.Activity;
import android.app.Fragment;
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
import com.wedesign.mediaplayer.vo.Contents;

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
    ImageButton button_bluetooth,button_usb,button_aux,button_SDCard;

    public MusicUIUpdateListener musicUIUpdateListener;

    public MusicFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        musicUIUpdateListener = (MusicUIUpdateListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BaseUtils.mlog(TAG,"------onCreateView-----");
        // Inflate the layout for this fragment
        View view  =inflater.inflate(R.layout.fragment_music, container, false);
        song_info_layout = (LinearLayout) view.findViewById(R.id.song_info_layout);
        bt_device_name  = (TextView) view.findViewById(R.id.bt_device_name);
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

        button_usb = (ImageButton) view.findViewById(R.id.button_usb);
        button_bluetooth = (ImageButton) view.findViewById(R.id.button_bluetooth);
        button_aux = (ImageButton) view.findViewById(R.id.button_aux);
        button_SDCard = (ImageButton) view.findViewById(R.id.button_SDCard);

        button_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG,"button_usb----CLICK");
                if(BaseApp.ifhaveUSBdevice && BaseApp.playSourceManager != 0) {  //u盘拔出后，点击无效了 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED
                    musicUIUpdateListener.onSavePlaySource(0);  //sp中保存当前播放的source
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                    BaseApp.playSourceManager = 0;
                    button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                    button_aux.setBackgroundResource(R.mipmap.touming_b);
                    button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                    button_usb.setBackgroundResource(R.mipmap.yinyuan_p);
                    musicUIUpdateListener.onYinyuanChangeToUSB();
                }
            }
        });

        button_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG,"button_bluetooth----CLICK");
                if(BaseApp.ifBluetoothConnected && BaseApp.playSourceManager != 1){
                    musicUIUpdateListener.onSavePlaySource(1);
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager ;
                    BaseApp.playSourceManager = 1;
                    button_usb.setBackgroundResource(R.mipmap.touming_b);
                    button_bluetooth.setBackgroundResource(R.mipmap.yinyuan_p);
                    button_aux.setBackgroundResource(R.mipmap.touming_b);
                    button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                    musicUIUpdateListener.onYinyuanChangeToBT();
                }
            }
        });

        button_aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG, "button_aux----CLICK");
                if(BaseApp.playSourceManager != 2) {
                    musicUIUpdateListener.onSavePlaySource(2);
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager ;
                    BaseApp.playSourceManager = 2;
                    button_usb.setBackgroundResource(R.mipmap.touming_b);
                    button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                    button_aux.setBackgroundResource(R.mipmap.yinyuan_p);
                    button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                    musicUIUpdateListener.onAUXEent();
                }
            }
        });

        button_SDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG,"button_SDCard----CLICK");
                if(BaseApp.ifhavaSDdevice && BaseApp.playSourceManager != 3) {
                    musicUIUpdateListener.onSavePlaySource(3);
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager ;
                    BaseApp.playSourceManager = 3;
                    button_usb.setBackgroundResource(R.mipmap.touming_b);
                    button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                    button_aux.setBackgroundResource(R.mipmap.touming_b);
                    button_SDCard.setBackgroundResource(R.mipmap.yinyuan_p);
                    musicUIUpdateListener.onYinyuanChangeToSD();
                }
            }
        });

        progress_really_layout = (LinearLayout) view.findViewById(R.id.progress_really_layout);
        seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(BaseApp.playSourceManager == 0) {
                        BaseApp.current_music_play_progressUSB = progress;
                        seekBar.setProgress(progress);
                        musicUIUpdateListener.onServiceCommand(1);  //拖动
                    }else if(BaseApp.playSourceManager == 3){
                        BaseApp.current_music_play_progressSD  = progress;
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

        song_current_time=(TextView) view.findViewById(R.id.song_current_time);
        song_total_time=(TextView) view.findViewById(R.id.song_total_time);

        if(musicUIUpdateListener!=null){
            musicUIUpdateListener.onMusicCancelCover();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BaseApp.current_fragment == 0) {
            switch (BaseApp.playSourceManager) {
                case 0:
                    button_usb.setBackgroundResource(R.mipmap.yinyuan_p);
                    break;
                case 1:
                    button_bluetooth.setBackgroundResource(R.mipmap.yinyuan_p);
                    break;
                case 2:
                    button_aux.setBackgroundResource(R.mipmap.yinyuan_p);
                    break;
                case 3:
                    button_SDCard.setBackgroundResource(R.mipmap.yinyuan_p);
                    break;
            }

            if (BaseApp.ifhaveUSBdevice) {
                button_usb.setImageResource(R.mipmap.usb_n);
            } else {
                button_usb.setImageResource(R.mipmap.usb_p);
            }
            if (BaseApp.ifhavaSDdevice) {
                button_SDCard.setImageResource(R.mipmap.sd_ico_p);
            } else {
                button_SDCard.setImageResource(R.mipmap.sd_ico_n);
            }
            if (BaseApp.ifBluetoothConnected) {
                button_bluetooth.setImageResource(R.mipmap.bt_n);
            } else {
                button_bluetooth.setImageResource(R.mipmap.bt_p);
            }
            button_aux.setImageResource(R.mipmap.aux_n);
        }



    }

    void changeMusicPlayModeUI(int playmode){
        button_play_mode_ico.setImageResource(music_play_mode_resource_ico[playmode]);
        button_play_mode_name.setText(button_play_mode_name_ico[playmode]);
    }

    public interface MusicUIUpdateListener{
        public void onServiceCommand(int i);
        public void onLieBiaoClose();
        public void onYinyuanChangeToSD();
        public void onYinyuanChangeToUSB();
        public void onYinyuanChangeToBT();//进入BT
        public void onAUXEent();//进入aux
        public void onSavePlaySource(int sourceID);
        public void onMusicCancelCover();
    }


}
