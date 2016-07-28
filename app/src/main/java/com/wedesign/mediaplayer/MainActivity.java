package com.wedesign.mediaplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wedesign.mediaplayer.Adapter.MyGridViewAdapter2;
import com.wedesign.mediaplayer.Adapter.MymusiclistviewAdapter;
import com.wedesign.mediaplayer.Adapter.MyvideolistviewAdapter;
import com.wedesign.mediaplayer.Bluetooth.BtmusicPlay;
import com.wedesign.mediaplayer.MediaService.MPbtState;
import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.Utils.MediaUtils;
import com.wedesign.mediaplayer.Utils.Mp4MediaUtils;
import com.wedesign.mediaplayer.Utils.PicMediaUtils;
import com.wedesign.mediaplayer.vo.Contents;
import com.wedesign.mediaplayer.vo.Mp3Info;
import com.wedesign.mediaplayer.vo.Mp4Info;
import com.wedesign.mediaplayer.vo.PicInfo;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.AbsListView.OnScrollListener;

public class MainActivity extends Activity implements View.OnClickListener,
        MusicFragment.MusicUIUpdateListener,
        VideoFragment.VideoUIUpdateListener,
        PictureFragment.PicUIUpdateListener,
       SourceManager.OnSourceManagerListener{

    private static final String TAG = "MediaPlayer";
    public PlayMusicService playMusicService;
    public boolean islocalmusicbound = false;

    RelativeLayout mRLayout;  //总布局
    FrameLayout frame_content; //fragment 布局

    LinearLayout button_layout_dilan;  //底部的总布局 包括button_layout和button_layout_bluetooth
    ImageButton button_shangqu,button_bofang,button_xiaqu,button_play_mode,button_liebiao;
    ImageButton button_fangda,button_suoxiao;
    LinearLayout button_layout;   //本地播放的底栏

    ImageButton button_shangqu_bluetooth,button_bofang_bluetooth,button_xiaqu_bluetooth;
    LinearLayout button_layout_bluetooth;  //蓝牙播放的底栏

    Button button_music,button_video,button_pic;    //列表中的控件

    MusicFragment musicFragment;
    VideoFragment videoFragment;
    PictureFragment pictureFragment;
    static List<Fragment> fragments = new ArrayList<>();
    String[] fragment_tags = {"musicFragment","videoFragment","pictureFragment"};
    RelativeLayout leibieliebiao;
    ListView musiclistview_id;
    ListView videolistview_id;
    GridView gridview_id;
    LinearLayout loading_layout;
    ImageView loading_image;
    TextView loading_text;
    MymusiclistviewAdapter mymusiclistviewAdapter;
    MyvideolistviewAdapter myvideolistviewAdapter;
    MyGridViewAdapter2 myGridViewAdapter2;
    private BtmusicPlay mBtmusictPlay = null;
    ImageView media_fragment_image;
    RelativeLayout brake_layout;

    public static int mDeviceStateUSB = 0;
    public int usbState = 2;
    public static int mDeviceStateSD = 0;
    public int sdState = 2;
    //用于获取sp中存储的数据

    String last_path_usb;
    String last_path_sd;
    String last_video_path_usb;
    String last_video_path_sd;
    String last_pic_path_usb;
    String last_pic_path_sd;
    Timer scan_timerUSB = new Timer();
    Timer scan_timerSD = new Timer();
    public static Mp3Info mp3Info_temp = new Mp3Info();  //记录点击的数据
    public static Mp3Info mp3Info_tempSD = new Mp3Info();

    public static Mp4Info mp4Info_temp = new Mp4Info();  //记录点击的数据
    public static Mp4Info mp4Info_tempSD = new Mp4Info();

    public static PicInfo PicInfo_temp = new PicInfo();  //记录点击的数据
    public static PicInfo PicInfo_tempSD = new PicInfo();

    public static Mp3Info mp3Info_default_temp = new Mp3Info();  //记录默认的数据
    public static Mp3Info mp3Info_default_tempSD = new Mp3Info();

    private static MyHandler myHandler;
    private AnimationDrawable frameAnim;
    public static SourceManager mSourceManager;
    private BroadcastReceiver mMediaReceiver;
    private BroadcastReceiver mDeviceReceiver;
    public int[] music_play_mode_resource = {R.mipmap.suiji,R.mipmap.shunxu,R.mipmap.quanbuxunhuan,R.mipmap.danquxunhuan};
    public int[] music_play_mode_resource_ico = {R.mipmap.suiji_ico,R.mipmap.shunxu_ico,R.mipmap.quanbuxunhuan_ico,R.mipmap.danquxunhuan_ico};
    public int[] button_play_mode_name_ico = {R.string.suijibofang,R.string.quanbubofang,R.string.quanbuxunhuan,R.string.danqubofang};

    int last_sd_total_num = 0;
    int last_sd_video_total_num =0;
    int last_sd_pic_total_num =0;

    int last_usb_total_num = 0;
    int last_usb_video_total_num =0;
    int last_usb_pic_total_num =0;

    int last_usb_num = -1;
    int last_usb_video_num = -1;
    int last_usb_pic_num = -1;

    int last_sd_num = -1;
    int last_sd_video_num =-1;
    int last_sd_pic_num =-1;

    Timer pic_timer = null;

    private MPbtState mMPbtState = null;

    private Uri mUri;
    public static boolean is_out_for_play = false;

    Timer timer_main;
    AlertDialog dlg;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    LinearLayout come_from_layout;  //全屏和列表显示的时候需要隐藏
    ImageButton button_bluetooth,button_usb,button_aux,button_SDCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseUtils.mlog(TAG, "onCreate-----start");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_main);

        //无论蓝牙是否连接上了，这里都需要去绑定蓝牙服务，去监听蓝牙的连接状态
        mBtmusictPlay = BtmusicPlay.getInstance(getApplicationContext(), myHandler);
        mBtmusictPlay.initBT();

        mMPbtState = MPbtState.getInstance(getApplicationContext(),myHandler);
        mMPbtState.initBTMP();

        mSourceManager = SourceManager.getInstance(getApplicationContext());
        sharedPreferences = getSharedPreferences("DongfengDataSave", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        final Intent intent_out = getIntent();
        if(intent_out != null) {
            BaseUtils.mlog(TAG,"intent is not null...");
            try {
                mUri = intent_out.getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mUri != null && !mUri.toString().trim().equals("")) {
                BaseUtils.mlog(TAG,"mUri----------"+mUri.toString());
            }else{
                BaseUtils.mlog(TAG,"intent is not null=======但是文件的格式不正确");
                is_out_for_play = false;
            }
        }else{
            is_out_for_play = false;
        }

        initUI();  //切换语言导致视频重音问题，音频、图片都存在问题。主要是fragment被attach了两次，
        initComeFromUI();
        if(savedInstanceState == null) {
            initFragment();
        }else {
            FragmentManager fm = getFragmentManager();
            musicFragment = (MusicFragment)fm.findFragmentByTag("musicFragment");
            videoFragment = (VideoFragment)fm.findFragmentByTag("videoFragment");
            pictureFragment = (PictureFragment)fm.findFragmentByTag("pictureFragment");
            if(fragments.size() == 0){
                fragments.add(musicFragment);
                fragments.add(videoFragment);
                fragments.add(pictureFragment);
            }else {
                fragments.set(0, musicFragment);
                fragments.set(1, videoFragment);
                fragments.set(2, pictureFragment);
            }
            BaseUtils.mlog(TAG,"fragments--"+fragments.size());
        }
        myHandler = new MyHandler(this);
        getDevicesState();//主要是给mDeviceStateSD赋值
        //这里主要是获取了四个设备的状态，其中usb和sd卡的状态用mDeviceStateUSB和mDeviceStateSD来标示，而aux和蓝牙只用有木有和是否连接上来标示

        //判断上一次是哪个播放源
        BaseApp.playSourceManager = sharedPreferences.getInt("PLAYSOURCE", 0);
        BaseUtils.mlog(TAG, "BaseApp.playSourceManager" + BaseApp.playSourceManager);

        if (BaseApp.playSourceManager == 3) {   //上次播放的是sd卡音乐
            if (BaseApp.ifhavaSDdevice) {
                BaseApp.playSourceManager = 3;
            } else if (BaseApp.ifhaveUSBdevice) {
                BaseApp.playSourceManager = 0;
            } else if (BaseApp.ifBluetoothConnected) {
                BaseApp.playSourceManager = 1;
            } else if (BaseApp.ifhaveAUXdevice) {  //第三个必定为真
                BaseApp.playSourceManager = 2;
            }
        } else if (BaseApp.playSourceManager == 0) {  //蓝牙和usb、aux一起考虑
            if (BaseApp.ifhaveUSBdevice) {
                BaseApp.playSourceManager = 0;
            } else if (BaseApp.ifhavaSDdevice) {
                BaseApp.playSourceManager = 3;
            } else if (BaseApp.ifBluetoothConnected) {
                BaseApp.playSourceManager = 1;
            } else if (BaseApp.ifhaveAUXdevice) {  //第三个必定为真
                BaseApp.playSourceManager = 2;
            }
        } else if(BaseApp.playSourceManager == 1){
           if (BaseApp.ifBluetoothConnected) {
                BaseApp.playSourceManager = 1;
            }else if (BaseApp.ifhaveUSBdevice) {
                BaseApp.playSourceManager = 0;
            } else if (BaseApp.ifhavaSDdevice) {
                BaseApp.playSourceManager = 3;
            }  else if (BaseApp.ifhaveAUXdevice) {  //第三个必定为真
                BaseApp.playSourceManager = 2;
            }
        }else{
            BaseApp.playSourceManager = 2;
        }

        BaseUtils.mlog(TAG, "BaseApp.playSourceManager" + BaseApp.playSourceManager);
        //当判断设备有存在或者处于连接状态了，这个时候就需要去查询多媒体信息和绑定蓝牙服务了,当前的playSourceManager就是真实的可用于播放的源了
        myHandler.sendEmptyMessage(Contents.MSG_LOCAL_FIRST_CHANGE_UI);   //修改播放源的ui蓝色背景

        if (BaseApp.ifhaveUSBdevice) {
            BaseUtils.mlog(TAG, "mDeviceStateUSB----" + mDeviceStateUSB);
            //点亮图标放在了fragment
            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED) {
                BaseUtils.mlog(TAG, "进入的时候，USB设备加载成功。。。");
                new MyAsyncTaskUSB1().execute();

            } else if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED || mDeviceStateUSB == Contents.USB_DEVICE_STATE_MOUNTED) {
                BaseUtils.mlog(TAG, "进入的时候，USB设备正在加载中。。。");
                myHandler.sendEmptyMessage(Contents.USB_MSG_STATE_SCANNER_STARTED);
            }
        }else{
            BaseApp.ifmusicReadFinishUSB = false;
        }

        if (BaseApp.ifhavaSDdevice) {
            BaseUtils.mlog(TAG, "mDeviceStateSD----" + mDeviceStateSD);
            BaseApp.ifmusicReadFinishSD = false;
            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED) {
                BaseUtils.mlog(TAG, "进入的时候，SD设备加载成功。。。");
                new MyAsyncTaskSD1().execute();
            } else if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED || mDeviceStateSD == Contents.SD_DEVICE_STATE_MOUNTED) {
                BaseUtils.mlog(TAG, "进入的时候，SD设备正在加载中。。。");
                myHandler.sendEmptyMessage(Contents.SD_MSG_STATE_SCANNER_STARTED);
            }
        }else{
            BaseApp.ifmusicReadFinishSD = false;
        }
//        //无论蓝牙是否连接上了，这里都需要去绑定蓝牙服务，去监听蓝牙的连接状态
//        mBtmusictPlay = BtmusicPlay.getInstance(getApplicationContext(),myHandler);
//        mBtmusictPlay.initBT();

        if(BaseApp.ifBluetoothConnected){
            //点亮图标放在了fragment
            BaseUtils.mlog(TAG, "进入的时候，蓝牙已经连接了。。。");
            if(BaseApp.playSourceManager == 1){
                //如果当前playsourcemanager为1，那么需要请求源，去播放音乐
                myHandler.sendEmptyMessage(Contents.BLUETOOTH_PLAY);
            }
        }

        if(BaseApp.ifhaveAUXdevice){
            if(BaseApp.playSourceManager == 2){
                BaseUtils.mlog(TAG, "进入的时候，没有任何设备存在，进入了AUX功能。。。");
                //发送消息去改变ui画面,前面的usb和sd由于是默认画面，所以不需要发送消息去改变画面了
                myHandler.sendEmptyMessage(Contents.MSG_AUX_FIRST_CHANGE_UI);
            }
        }

        myHandler.sendEmptyMessage(Contents.MSG_DEVICE_STATE_UI);
        registMediaBroadcast();
        registDeviceBroadcase();

        Intent intent = new Intent(this, PlayMusicService.class);
        startService(intent); //启动服务
        if(timer_main==null) {
            timer_main = new Timer();
            timer_main.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (BaseApp.ifliebiaoOpen == 1) {
                        BaseApp.liebiaoOpenNum++;
                        if (BaseApp.liebiaoOpenNum > 5) {//3s自动隐藏
                            BaseApp.liebiaoOpenNum = 0;
                            myHandler.sendEmptyMessage(Contents.MSG_DISMISS_LIEBIAO_AUTO);
                        }
                    } else {
                        BaseApp.liebiaoOpenNum = 0;
                    }
                }
            }, 0, 1000);
        }
        BaseUtils.mlog2(TAG, "onCreate", "end");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseUtils.mlog(TAG, "--------onResume--------");
        BaseUtils.mlog(TAG, "------current_video_play_progressUSB--------" + BaseApp.current_video_play_progressUSB);
        BaseUtils.mlog(TAG, "-------current_video_play_progressSD--------" + BaseApp.current_video_play_progressSD);
        mSourceManager.setListener(this);//无论当前是否有播放资源，都需要去请求源
        bindPlayMusicService();
        BaseApp.exitUI = false;

        BaseUtils.mlog(TAG, "--------onResume--------"+BaseApp.playSourceManager);
        myHandler.sendEmptyMessage(998);  //申请源

        BaseApp.dibuheight = 66;  //默认的高度
        if(BaseApp.current_fragment == 0){
            BaseApp.ifinPPT = false;
            BaseApp.ifFullScreenState = false;
//            BaseApp.statebarheight = 0;
            exitFullScreen();
        }else if(BaseApp.current_fragment == 1){
            if(BaseApp.ifFullScreenState){
                myHandler.sendEmptyMessage(999);//全屏
            }
        }else if(BaseApp.current_fragment == 2){
            if(BaseApp.ifinPPT){
                myHandler.sendEmptyMessage(999);//全屏
            }
        }

        choosedilanAndFragmentShow(BaseApp.current_fragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseUtils.mlog(TAG, "--------onPause--------");
        unbindPlayMusicService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseUtils.mlog(TAG, "--------onStop--------");
        BaseApp.exitUI = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseUtils.mlog(TAG, "--------onDestroy--------" + BaseApp.playSourceManager);
        if(timer_main!=null){
            timer_main.cancel();
            timer_main = null;
        }
        if(pic_timer!=null){
            pic_timer.cancel();
            pic_timer = null;
        }
        if(scan_timerUSB !=null){
            scan_timerUSB.cancel();
            scan_timerUSB = null;
        }

        if(scan_timerSD!=null){
            scan_timerSD.cancel();
            scan_timerSD = null;
        }

        unregisterReceiver(mMediaReceiver);
        unregisterReceiver(mDeviceReceiver);
//        mSourceManager.onDestory();
    }

    private void initUI(){
        BaseUtils.mlog2(TAG, "initUI", "start");
        BaseApp.music_play_mode = sharedPreferences.getInt("MUSICPLAYMODE", 0);
        BaseApp.dibuheight = 0;  //全屏状态后，再次切换语言，然后回来会导致statebarheight不被清空出问题
        BaseApp.statebarheight = 0;
        //主布局以及两大核心布局块
        mRLayout = (RelativeLayout) findViewById(R.id.content);
        frame_content = (FrameLayout) findViewById(R.id.frame_content);
        button_layout_dilan  = (LinearLayout) findViewById(R.id.button_layout_dilan);
        //底部local
        button_shangqu = (ImageButton) findViewById(R.id.button_shangqu);
        BaseUtils.mlog2(TAG, "initUI", "ispausesdSD"+BaseApp.ispauseSD+"; ispausesdUSB"+BaseApp.ispauseUSB);
        button_bofang = (ImageButton) findViewById(R.id.button_bofang);
        if(BaseApp.ispauseUSB == 0 || BaseApp.ispauseSD ==0){
            button_bofang.setImageResource(R.mipmap.bofang);
        }else {
            button_bofang.setImageResource(R.mipmap.zanting);
        }
        button_xiaqu = (ImageButton) findViewById(R.id.button_xiaqu);
        button_play_mode = (ImageButton) findViewById(R.id.button_play_mode);
        button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);   //这里不加入在切换语言的时候会闪一下播放的mode
        button_liebiao = (ImageButton) findViewById(R.id.button_liebiao);
        //local以及图片模块增加的布局
        button_fangda = (ImageButton) findViewById(R.id.button_fangda);
        button_suoxiao = (ImageButton) findViewById(R.id.button_suoxiao);
        button_layout = (LinearLayout) findViewById(R.id.button_layout);
        //底部蓝牙
        button_shangqu_bluetooth = (ImageButton) findViewById(R.id.button_shangqu_bluetooth);
        button_bofang_bluetooth = (ImageButton) findViewById(R.id.button_bofang_bluetooth);
        button_xiaqu_bluetooth = (ImageButton) findViewById(R.id.button_xiaqu_bluetooth);
        button_layout_bluetooth = (LinearLayout) findViewById(R.id.button_layout_bluetooth);

        //列表
        leibieliebiao = (RelativeLayout) findViewById(R.id.leibieliebiao);
        button_music = (Button) findViewById(R.id.button_music);
        button_music.setBackgroundResource(R.mipmap.liebiao_p);
        button_video = (Button) findViewById(R.id.button_video);
        button_pic = (Button) findViewById(R.id.button_pic);

        loading_layout = (LinearLayout) findViewById(R.id.loading_layout);
        loading_image = (ImageView) findViewById(R.id.loading_image);
        loading_text = (TextView) findViewById(R.id.loading_text);
        musiclistview_id = (ListView) findViewById(R.id.musiclistview_id);
        musiclistview_id.setSelector(new ColorDrawable(Color.TRANSPARENT));  //防止出现黄色的背景
        videolistview_id = (ListView) findViewById(R.id.videolistview_id);
        videolistview_id.setSelector(new ColorDrawable(Color.TRANSPARENT));  //防止出现黄色的背景
        gridview_id = (GridView) findViewById(R.id.gridview_id);
        gridview_id.setSelector(new ColorDrawable(Color.TRANSPARENT));  //防止出现黄色的背景

        brake_layout = (RelativeLayout) findViewById(R.id.brake_layout);
        media_fragment_image = (ImageView) findViewById(R.id.media_fragment_image);

        //播放源
        come_from_layout = (LinearLayout)findViewById(R.id.come_from_layout);
        button_usb = (ImageButton) findViewById(R.id.button_usb);
        button_bluetooth = (ImageButton) findViewById(R.id.button_bluetooth);
        button_aux = (ImageButton) findViewById(R.id.button_aux);
        button_SDCard = (ImageButton) findViewById(R.id.button_SDCard);

        musiclistview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseUtils.mlog(TAG, "------music-------onItemClick-------------");

                choosedilanAndFragmentShow(0);

                if(!BaseApp.brake_flag){
                    brake_layout.setVisibility(View.GONE);
                }

                BaseApp.liebiaoOpenNum = 0;

                if(BaseApp.current_media == 0) {
                    if(BaseApp.playSourceManager == 0) {
                        if(BaseApp.current_fragment == 0 && BaseApp.current_music_play_numUSB == position){
                            //已经在播放了点击了相同的曲目
                        }else {
                            if(BaseApp.current_music_play_numUSB == position) {   //在图片界面的时候点击相同的曲目还是不应该改变
                            }else{
                                BaseApp.current_music_play_progressUSB = 0;
                            }
                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                            BaseApp.current_music_play_numUSB = position;
                            //每次点击之后，都需要重新赋值，否则会一直在列表中刷新第一次扫描出来的歌曲
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED) {
                                mp3Info_temp = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                                BaseApp.when_scan_click = true;
                            }
                            if(BaseApp.current_fragment!=0) {
                                changeFragment(0);
                            }
                            BaseUtils.mlog(TAG, "MainActivity-onItemClick-music_play_mode:" + BaseApp.music_play_mode);
                            if (BaseApp.current_music_play_numUSB >= 0) {
                                if(BaseApp.current_music_play_progressUSB == 0) {
                                    BaseApp.ispauseUSB = 2;
                                    playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                                }else{
                                    playMusicService.musicUpdateListener.onLocalMusicChangeItem(BaseApp.current_music_play_numUSB);
                                    BaseApp.ispauseUSB = 2;
                                    playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                                }
                                BaseUtils.mlog(TAG, "onItemClick----" + BaseApp.current_music_play_numUSB);
                            }
                        }
                    } else if (BaseApp.playSourceManager == 3) {
                        if (BaseApp.current_fragment == 0 && BaseApp.current_music_play_numSD == position) {

                        } else {
                            if(BaseApp.current_music_play_numSD == position) {   //在图片界面的时候点击相同的曲目还是不应该改变
                            }else {
                                BaseApp.current_music_play_progressSD = 0;
                            }
                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                            BaseApp.current_music_play_numSD = position;
                            //每次点击之后，都需要重新赋值，否则会一直在列表中刷新第一次扫描出来的歌曲
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                                mp3Info_tempSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                                BaseApp.when_scan_clickSD = true;
                            }
                            if(BaseApp.current_fragment!=0) {
                                changeFragment(0);
                            }
                            BaseUtils.mlog(TAG, "MainActivity-onItemClick-music_play_mode:" + BaseApp.music_play_mode);
                            if (BaseApp.current_music_play_numSD >= 0) {
                                if(BaseApp.current_music_play_progressSD == 0) {
                                    BaseApp.ispauseSD = 2;
                                    playMusicService.playSD(BaseApp.current_music_play_numSD);  //防止点击相同曲目的卡顿
                                }else{
                                    playMusicService.musicUpdateListener.onLocalMusicChangeItem(BaseApp.current_music_play_numSD);
                                    BaseApp.ispauseSD = 2;
                                    playMusicService.playSD(BaseApp.current_music_play_numSD);
                                }
                                BaseUtils.mlog(TAG, "onItemClick----" + BaseApp.current_music_play_numSD);
                            }
                        }
                    }
                }
            }
        });

        musiclistview_id.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState!= SCROLL_STATE_IDLE){
                    BaseApp.liebiaoOpenNum = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        videolistview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseUtils.mlog(TAG, "-------VIDEO------onItemClick-------------");
                //在current_media是2的情况下，list列表就会被隐藏了。所以不可能跳转
                //改变底栏
                choosedilanAndFragmentShow(1);
                BaseApp.liebiaoOpenNum = 0;

                if (BaseApp.current_media == 1) {
                    button_play_mode.setImageResource(music_play_mode_resource[BaseApp.video_play_mode]);
                    if (BaseApp.playSourceManager == 0) {
                        if (BaseApp.current_fragment == 1 && BaseApp.current_video_play_numUSB == position) {
                            //点击了相同的曲目，不做任何操作
                        } else {
                            BaseUtils.mlog(TAG, "-onItemClick-enter the video play...");
                            if (BaseApp.current_video_play_numUSB != position) {
                                BaseApp.current_video_play_progressUSB = 0;
                            }

                            BaseApp.current_video_play_numUSB = position;
                            myvideolistviewAdapter.notifyDataSetChanged();
                            //每次点击之后，都需要重新赋值，否则会一直在列表中刷新第一次扫描出来的歌曲
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED) {
                                mp4Info_temp = BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB);
                                BaseApp.when_scan_click_video = true;
                            }

                            if (playMusicService != null && (BaseApp.current_fragment == 0 || playMusicService.isPlaying())) {
                                BaseApp.ispauseUSB = 2;
                                playMusicService.pause();
                            }
                            if (BaseApp.current_fragment != 1) {
                                changeFragment(1);
                                BaseUtils.mlog(TAG, BaseApp.mp4Infos.get(position).getData());
                                button_bofang.setImageResource(R.mipmap.bofang);
                                videoFragment.playVideoFromMainactivity(position, 0);

                            } else {//如果就是在视频这个界面,这种做法貌似行不通
                                BaseUtils.mlog(TAG, "-onItemClick-Already in videofragment...");
                                videoFragment.playVideoFromUser(position);
                            }
                        }
                    } else if (BaseApp.playSourceManager == 3) {
                        BaseUtils.mlog(TAG, "-onItemClick-enter the video play...");
                        if (BaseApp.current_fragment == 1 && BaseApp.current_video_play_numSD == position) {
                            //点击了相同的曲目，不做任何操作
                        } else {
                            if (BaseApp.current_video_play_numSD != position) {
                                BaseApp.current_video_play_progressSD = 0;
                            }
                            BaseApp.current_video_play_numSD = position;
                            myvideolistviewAdapter.notifyDataSetChanged();
                            //每次点击之后，都需要重新赋值，否则会一直在列表中刷新第一次扫描出来的歌曲
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                                mp4Info_tempSD = BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD);
                                BaseApp.when_scan_click_videoSD = true;
                            }

                            if (playMusicService != null && (BaseApp.current_fragment == 0 || playMusicService.isPlaying())) {
                                BaseApp.ispauseSD = 2;
                                playMusicService.pause();
                            }
                            if (BaseApp.current_fragment != 1) {
                                changeFragment(1);
                                BaseUtils.mlog(TAG, BaseApp.mp4InfosSD.get(position).getData());
                                button_bofang.setImageResource(R.mipmap.bofang);
                                videoFragment.playVideoFromMainactivity(position, 0);
                            } else {//如果就是在视频这个界面,这种做法貌似行不通
                                BaseUtils.mlog(TAG, "-onItemClick-Already in videofragment...");
                                videoFragment.playVideoFromUser(position);
                            }
                        }
                    }
                }
            }
        });
        videolistview_id.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState!= SCROLL_STATE_IDLE){
                    BaseApp.liebiaoOpenNum = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        gridview_id.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseUtils.mlog(TAG, "--------gridview_id-----click---");
                choosedilanAndFragmentShow(2);
                BaseApp.liebiaoOpenNum = 0;

                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (position == i) {//当前选中的Item改变背景颜色
                        view.setBackgroundResource(R.mipmap.tupian_p);
                    } else {
                        if (v != null)  //在当前页面不会进行刷新，需要自己手动设置背景隐藏
                            v.setBackgroundResource(0);
                    }
                }
                if(!BaseApp.brake_flag){  //可以继续播放音乐浏览图片
                    brake_layout.setVisibility(View.GONE);
                }
                if (BaseApp.playSourceManager == 0) {
                    //点击就发送消息
                    BaseApp.current_pic_play_numUSB = position;
                    if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED) {
                        PicInfo_temp = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB);
                        BaseApp.when_scan_click_pic = true;
                    }
                    myHandler.sendEmptyMessage(Contents.IMAGE_ITEM_CLICK);
                } else if (BaseApp.playSourceManager == 3) {
                    //点击就发送消息
                    BaseApp.current_pic_play_numSD = position;
                    if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                        PicInfo_tempSD = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD);
                        BaseApp.when_scan_click_picSD = true;
                    }
                    if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                        mp3Info_tempSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                        BaseApp.when_scan_clickSD = true;
                    }
                    myHandler.sendEmptyMessage(Contents.IMAGE_ITEM_CLICK);
                }
            }
        });

        gridview_id.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState!= SCROLL_STATE_IDLE){
                    BaseApp.liebiaoOpenNum = 0;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        button_shangqu_bluetooth.setOnClickListener(this);
        button_xiaqu_bluetooth.setOnClickListener(this);
        button_bofang_bluetooth.setOnClickListener(this);

        button_shangqu.setOnClickListener(this);
        button_bofang.setOnClickListener(this);
        button_xiaqu.setOnClickListener(this);
        button_play_mode.setOnClickListener(this);
        button_liebiao.setOnClickListener(this);
        button_music.setOnClickListener(this);
        button_video.setOnClickListener(this);
        button_pic.setOnClickListener(this);
        button_fangda.setOnClickListener(this);
        button_suoxiao.setOnClickListener(this);

        BaseUtils.mlog2(TAG, "initUI", "end");
    }

    private void initFragment(){

            BaseUtils.mlog2(TAG, "initFragment", "start");
            BaseUtils.mlog(TAG, "-fragments SIZE:------" + fragments.size());
            fragments = new ArrayList<Fragment>();
            if (musicFragment == null) {
                musicFragment = MusicFragment.getInstance(this);
                fragments.add(musicFragment);
            }
            if (videoFragment == null) {
                videoFragment = VideoFragment.getInstance(this);
                fragments.add(videoFragment);
            }
            if (pictureFragment == null) {
                pictureFragment = PictureFragment.getInstance(this);
                fragments.add(pictureFragment);
            }
    //    if(BaseApp.ifStartOnce) {
            if (fragments != null && fragments.size() == 3) {
                BaseUtils.mlog(TAG, "-addFragmentLayout-current_fragment is:------" + BaseApp.current_fragment);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (fragments.get(BaseApp.current_fragment).isAdded()) {
                    BaseUtils.mlog(TAG, "-addFragmentLayout-current_fragment is:------start");
                    fragments.get(BaseApp.current_fragment).onStart();
                } else {
                        ft.add(R.id.media_fragment, fragments.get(BaseApp.current_fragment),fragment_tags[BaseApp.current_fragment]);
                        ft.commit();
                    BaseUtils.mlog(TAG, "-addFragmentLayout-current_fragment is:------commit");
                }
                BaseUtils.mlog2(TAG, "initFragment", "end");
            }
    }

    private void getDevicesState(){
        BaseUtils.mlog2(TAG, "getDevicesState", "start");
        //判断当前的usb的设备状态
        if(IsPathMounts(Contents.USB_PATH)){
            BaseUtils.mlog(TAG, "usb卡设备存在");
            BaseApp.ifhaveUSBdevice = true;
            usbState = Contents.USB_STATE_MOUNTED;
            mDeviceStateUSB = Contents.USB_DEVICE_STATE_MOUNTED;

            usbState = getUsbState(this);
            switch(usbState){
                case Contents.USB_STATE_MOUNTED:
                    mDeviceStateUSB = Contents.USB_DEVICE_STATE_MOUNTED;
                    break;
                case Contents.USB_STATE_SCANNER_STARTED:
                    mDeviceStateUSB = Contents.USB_DEVICE_STATE_SCANNER_STARTED;
                    break;
                case Contents.USB_STATE_SCANNER_FINISHED:
                    mDeviceStateUSB = Contents.USB_DEVICE_STATE_SCANNER_FINISHED;
                    break;
            }
        }else{
            BaseUtils.mlog(TAG, "usb卡设备不存在");
            BaseApp.ifhaveUSBdevice = false;
            usbState = Contents.USB_STATE_UNMOUNTED;
            mDeviceStateUSB = Contents.USB_DEVICE_STATE_UNMOUNTED;
        }
        BaseUtils.mlog(TAG, "usbState:" + usbState + " ---mDeviceStateUSB:" + mDeviceStateUSB + "---BaseApp.ifhaveUSBdevice:" + BaseApp.ifhaveUSBdevice);

        //判断当前蓝牙
        int temp_blue_state = getBluetoothConnectState(this);
        if(temp_blue_state == 0){
            BaseApp.ifBluetoothConnected = false;
        }else if(temp_blue_state == 1){
            BaseApp.ifBluetoothConnected = true;
        }
        BaseUtils.mlog(TAG, "onConnectState-----BaseApp.ifBluetoothConnected----" + BaseApp.ifBluetoothConnected);
        //判断当前的aux的设备状态
        BaseApp.ifhaveAUXdevice = true;   //目前硬件设备，AUX常在
        //判断当前的sd的设备状态
        if(IsPathMounts(Contents.SDCARD_PATH)){
            BaseUtils.mlog(TAG, "sd卡设备存在");
            BaseApp.ifhavaSDdevice = true;
            sdState = Contents.SD_STATE_MOUNTED;
            mDeviceStateSD = Contents.SD_DEVICE_STATE_MOUNTED;

            sdState = getSDCardState(this);
            BaseUtils.mlog(TAG, "sdState---" + sdState);
            switch(sdState){
                case Contents.SD_STATE_MOUNTED:
                    mDeviceStateSD = Contents.SD_DEVICE_STATE_MOUNTED;
                    break;
                case Contents.SD_STATE_UNMOUNTED:    //正常的话，应该不会执行
                    mDeviceStateSD = Contents.SD_DEVICE_STATE_UNMOUNTED;
                    break;
                case Contents.SD_STATE_SCANNER_STARTED:
                    mDeviceStateSD = Contents.SD_DEVICE_STATE_SCANNER_STARTED;
                    break;
                case Contents.SD_STATE_SCANNER_FINISHED:
                    mDeviceStateSD = Contents.SD_DEVICE_STATE_SCANNER_FINISHED;
                    break;
            }
        }else{
            BaseUtils.mlog(TAG, "sd卡设备不存在");
            BaseApp.ifhavaSDdevice = false;
            sdState = Contents.SD_STATE_UNMOUNTED;
            mDeviceStateSD = Contents.SD_DEVICE_STATE_UNMOUNTED;
        }

        BaseUtils.mlog2(TAG, "getDevicesState", "end");
    }


    public boolean IsPathMounts(String strPath) {   //判断路径是否存在
        BaseUtils.mlog(TAG, "-------------IsPathMounts-------------");
        String filenameTemp = strPath + "/tmp" + ".txt";
        File file = new File(filenameTemp);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                return false;
            }
        }
        if (file.exists()) {
            file.delete();
            return true;
        } else {
            return false;
        }
    }

    public static int getUsbState(Context c) {
        BaseUtils.mlog(TAG, "-------------getUsbState-------------");
        int value = Contents.USB_STATE_UNMOUNTED;
        try {
            Context otherContext = c.createPackageContext(
                    "com.wedesign.sourcemanager",
                    Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = otherContext.getSharedPreferences(
                    "com.wedesign.sourcemanager", Context.MODE_WORLD_READABLE
                            + Context.MODE_WORLD_WRITEABLE
                            + Context.MODE_MULTI_PROCESS);
            value = sp.getInt("UsbState", Contents.USB_STATE_UNMOUNTED);
        } catch (Exception e) {
            // TODO: handle exception
        }

        BaseUtils.mlog(TAG, "getUsbState: value = " + value);
        return value;
    }

    public int getBluetoothConnectState(Context c){
        BaseUtils.mlog(TAG, "-------------getBluetoothConnectState-------------");
        int ifblueConn = 0;
        try {
            Context otherContext = c.createPackageContext(
                    "com.wedesign.mediaservice",
                    Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = otherContext.getSharedPreferences(
                    "MediaPlayerDeviceStateSave", Context.MODE_WORLD_READABLE
                            + Context.MODE_WORLD_WRITEABLE
                            + Context.MODE_MULTI_PROCESS);
            ifblueConn = sp.getInt("BLUESTATE", 0);   //
        } catch (Exception e) {
            // TODO: handle exception
        }
        BaseUtils.mlog(TAG, "getBluetoothConnectState: ifblueConn = " + ifblueConn);
        return ifblueConn;
    }

    public static int getSDCardState(Context c) {
        BaseUtils.mlog(TAG, "-------------getSDCardState-------------");
        int value = Contents.SD_STATE_UNMOUNTED;
        try {
            Context otherContext = c.createPackageContext(
                    "com.wedesign.sourcemanager",
                    Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = otherContext.getSharedPreferences(
                    "com.wedesign.sourcemanager", Context.MODE_WORLD_READABLE
                            + Context.MODE_WORLD_WRITEABLE
                            + Context.MODE_MULTI_PROCESS);
            value = sp.getInt("SDState", Contents.SD_STATE_UNMOUNTED);   //等c哥修改之后，改变UsbState这个字符串
        } catch (Exception e) {
            // TODO: handle exception
        }

        BaseUtils.mlog(TAG, "getSDCardState: value = " + value);
        return value;
    }

    @Override
    public void onServiceCommand(int i) {
        BaseUtils.mlog(TAG, "-------------onServiceCommand-------------");
        if(BaseApp.playSourceManager == 0) {
            if(BaseApp.ispauseUSB == 2){  //第一次启动就拖动进度条有问题
                BaseApp.ispauseUSB = 1;
                playMusicService.playUSB(BaseApp.current_music_play_numUSB);
            }
            switch (i) {
                case 1:
                    BaseUtils.mlog(TAG, "-------------onServiceCommand----------USB---1");
                    playMusicService.seek(BaseApp.current_music_play_progressUSB);
                    break;
                case 2:
                    BaseUtils.mlog(TAG, "-------------onServiceCommand----------USB---2");
                    playMusicService.pause();
                    break;
                case 3:
                    BaseUtils.mlog(TAG, "-------------onServiceCommand----------USB---3");
                    playMusicService.start_play();
                    break;
            }
        }else if(BaseApp.playSourceManager == 3){
            BaseUtils.mlog(TAG, "-------------onServiceCommand----------SD---");
            if(BaseApp.ispauseSD == 2){  //第一次启动就拖动进度条有问题
                BaseApp.ispauseSD = 1;
                playMusicService.playSD(BaseApp.current_music_play_numSD);
            }
            switch (i) {
                case 1:
                    playMusicService.seek(BaseApp.current_music_play_progressSD);
                    break;
                case 2:
                    playMusicService.pauseSD();
                    break;
                case 3:
                    playMusicService.start_play();
                    break;
            }
        }
    }

    @Override
    public void onLieBiaoClose() {
        BaseUtils.mlog(TAG, "-------------onLieBiaoClose-------------");
        leibieliebiao.setVisibility(View.GONE);
        BaseApp.ifliebiaoOpen = 0;
        BaseApp.liebiaoOpenNum = 0;

    }

    public void YinyuanChangeToBT() {//切换到BT，处理之前的状态
        BaseUtils.mlog(TAG, "--------onYinyuanChangeToBT-------");
        BaseUtils.mlog(TAG, "last_playSourceManager---------" + BaseApp.last_playSourceManager);
//        mSourceManager.requestSourceToBT();
        BaseApp.app_inner_source = true;  //用于判断是内部源的切换
        musicFragment.song_name.setVisibility(View.VISIBLE);
        musicFragment.singer_layout.setVisibility(View.VISIBLE);
        musicFragment.song_name.setText(null);
        musicFragment.chuangzhe_name.setText(null);

        switch(BaseApp.last_playSourceManager){
            case 0:
                if(playMusicService!=null && playMusicService.isPlaying()) {
                    BaseApp.ispauseUSB = 2;
                    playMusicService.pause();
                    playMusicService.resetMusic();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }

                BaseUtils.mlog(TAG, "yinyuan change from usb...请求蓝牙源");

                choosedilanAndFragmentShow(0);
                mBtmusictPlay.getDeviceName();
                mBtmusictPlay.sourceCtrl();
                mBtmusictPlay.getMusicInfo();
                break;
            case 2:
                BaseUtils.mlog(TAG, "yinyuan changeAUX...请求蓝牙源");
                choosedilanAndFragmentShow(0);
                mBtmusictPlay.getDeviceName();
                mBtmusictPlay.sourceCtrl();
                mBtmusictPlay.getMusicInfo();
                break;
            case 3:
                if(playMusicService !=null && playMusicService.isPlaying()) {
                    BaseApp.ispauseSD = 2;
                    playMusicService.pauseSD();
                    playMusicService.resetMusic();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                BaseUtils.mlog(TAG, "yinyuan change from sd...请求蓝牙源 ");
                choosedilanAndFragmentShow(0);
                mBtmusictPlay.getDeviceName();
                mBtmusictPlay.sourceCtrl();
                mBtmusictPlay.getMusicInfo();
                break;
        }
    }

    public void YinyuanChangeToUSB() {
        BaseUtils.mlog(TAG, "onYinyuanChangeToUSB---------");
        BaseUtils.mlog(TAG, "last_playSourceManager---------"+BaseApp.last_playSourceManager);
        BaseApp.app_inner_source = true;  //用于判断是内部源的切换
        mSourceManager.requestSourceToMUSIC();
        switch(BaseApp.last_playSourceManager){
            case 1:
                mBtmusictPlay.outSoundisPlay =0;  //切掉音源
                if(mBtmusictPlay!=null && mBtmusictPlay.mPlayStatus){//源出去了，直接停止播放mPlayStatus同时改变状态
                    mBtmusictPlay.musicPlayOrPause();
                }
                choosedilanAndFragmentShow(0);

                if (BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0) {
                    if (BaseApp.current_music_play_numUSB < BaseApp.mp3Infos.size() && BaseApp.current_music_play_numUSB >= 0 ) {
                        Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                        setMusicFragmentContent(mp3Info);

                        if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED && (playMusicService != null && (BaseApp.ispauseUSB ==0 || BaseApp.ispauseUSB ==2))) {
                            BaseApp.ispauseUSB =0;
                            playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }else if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (playMusicService != null && (BaseApp.ispauseUSB ==0 || BaseApp.ispauseUSB ==2))){ //还没有扫描完，就切了过来还是要进行播放啊
                            BaseApp.ispauseUSB =0;
                            button_bofang.setImageResource(R.mipmap.bofang);
                            if (BaseApp.current_music_play_numUSB >= 0) {
                                playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                            }
                        }
                    } else { //加载的时候切换usb和蓝牙的时候出现了bug
                        setDefaultMusicFragmentContent();
                        setDefaultMusicProgress();
                        button_bofang.setImageResource(R.mipmap.zanting);
                    }
                }else{
                    setDefaultMusicFragmentContent();
                    setDefaultMusicProgress();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                break;
            case 2:
                choosedilanAndFragmentShow(0);

                if (BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0) {
                    if (BaseApp.current_music_play_numUSB < BaseApp.mp3Infos.size() && BaseApp.current_music_play_numUSB >= 0 ) {
                        Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                        setMusicFragmentContent(mp3Info);
                        setMusicProgress();

                        if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED && (playMusicService != null && (BaseApp.ispauseUSB ==0 || BaseApp.ispauseUSB ==2))) {
                            BaseApp.ispauseUSB =0;
                            playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }else if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (playMusicService != null && (BaseApp.ispauseUSB ==0 || BaseApp.ispauseUSB ==2))){ //还没有扫描完，就切了过来还是要进行播放啊
                            BaseApp.ispauseUSB =0;
                            button_bofang.setImageResource(R.mipmap.bofang);
                            if (BaseApp.current_music_play_numUSB >= 0) {
                                playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                            }
                        }
                    } else { //加载的时候切换usb和蓝牙的时候出现了bug
                        setDefaultMusicFragmentContent();
                        setDefaultMusicProgress();
                        button_bofang.setImageResource(R.mipmap.zanting);
                    }
                }else { //加载的时候切换usb和蓝牙的时候出现了bug
                    setDefaultMusicFragmentContent();
                    setDefaultMusicProgress();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                break;
            case 3:
                //上一个源就是sd卡，变成usb的时候不需要修改UI
                if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size() >0 && BaseApp.current_music_play_numUSB >=0) {
                    if (BaseApp.current_music_play_numUSB >= 0 && BaseApp.current_music_play_numUSB < BaseApp.mp3Infos.size() ) {
                    }else{
                        BaseApp.current_music_play_numUSB = 0;
                    }
                    if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED && (playMusicService != null && (BaseApp.ispauseUSB ==0 || BaseApp.ispauseUSB ==2))) {
                        BaseApp.ispauseUSB = 0;
                        playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                    }else if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (playMusicService != null && (BaseApp.ispauseUSB ==0 || BaseApp.ispauseUSB ==2))){ //还没有扫描完，就切了过来还是要进行播放啊
                        BaseApp.ispauseUSB =0;
                        button_bofang.setImageResource(R.mipmap.bofang);
                        if (BaseApp.current_music_play_numUSB >= 0) {
                            playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                        }
                    }
                    BaseUtils.mlog(TAG, "onChangePlayfromSDToUSB----");
                    musicFragment.changeMusicPlayModeUI(BaseApp.music_play_mode);
                    Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                    setMusicFragmentContent(mp3Info);
                    setMusicProgress();
                    if(BaseApp.ispauseUSB == 0 || BaseApp.ispauseUSB == 2) {
                        button_bofang.setImageResource(R.mipmap.bofang);
                    }else{
                        button_bofang.setImageResource(R.mipmap.zanting);
                    }
                }else{
                    BaseUtils.mlog(TAG, "---怎么是-------");
                    if(BaseApp.mp3InfosSD!=null && playMusicService!=null && playMusicService.isPlaying()){
                        playMusicService.pauseSD();
                    }
                    setDefaultMusicFragmentContent();
                    setDefaultMusicProgress();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                if(BaseApp.mp3InfosSD!=null && playMusicService!=null){
                    BaseUtils.mlog(TAG, "---to sd----BaseApp.ispauseSD-------" + BaseApp.ispauseSD);
                    BaseApp.ispauseSD = 2;
                    BaseUtils.mlog(TAG, "---to sd-----BaseApp.ispauseSD-------" + BaseApp.ispauseSD);
//                    playMusicService.pauseSD();
                }
       //         BaseApp.current_music_play_progressUSB = 0;
                break;
        }
    }

    public void YinyuanChangeToSD() {
        BaseUtils.mlog(TAG, "onYinyuanChangeToSD---------");
        BaseUtils.mlog(TAG, "last_playSourceManager---------" + BaseApp.last_playSourceManager);
        mSourceManager.requestSourceToVIDEO();
        BaseApp.app_inner_source = true;  //用于判断是内部源的切换
        switch(BaseApp.last_playSourceManager){
            case 1:
                mBtmusictPlay.outSoundisPlay =0;  //切掉音源
                if(mBtmusictPlay!=null && mBtmusictPlay.mPlayStatus){//源出去了，直接停止播放,同时改变了mPlayStatus状态
                    mBtmusictPlay.musicPlayOrPause();
                }
                choosedilanAndFragmentShow(0);

                if (BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0) {
                    if (BaseApp.current_music_play_numSD < BaseApp.mp3InfosSD.size() && BaseApp.current_music_play_numSD >= 0 ) {
                        Mp3Info mp3InfoSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                        setMusicFragmentContent(mp3InfoSD);
                        setMusicProgress();
                        if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED && (playMusicService != null && (BaseApp.ispauseSD ==0 || BaseApp.ispauseSD ==2))) {
                            BaseApp.ispauseSD =0;
                            playMusicService.playSD(BaseApp.current_music_play_numSD);
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }else if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (playMusicService != null && (BaseApp.ispauseSD ==0 || BaseApp.ispauseSD ==2))){ //还没有扫描完，就切了过来还是要进行播放啊
                            BaseApp.ispauseSD =0;
                            button_bofang.setImageResource(R.mipmap.bofang);
                            if (BaseApp.current_music_play_numSD >= 0) {
                                playMusicService.playSD(BaseApp.current_music_play_numSD);
                            }
                        }
                    } else { //加载的时候切换usb和蓝牙的时候出现了bug
                        setDefaultMusicFragmentContent();
                        setDefaultMusicProgress();
                        button_bofang.setImageResource(R.mipmap.zanting);
                    }
                }else{
                    setDefaultMusicFragmentContent();
                    setDefaultMusicProgress();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                break;
            case 2:
                choosedilanAndFragmentShow(0);
                if (BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0) {
                    if (BaseApp.current_music_play_numSD < BaseApp.mp3InfosSD.size() && BaseApp.current_music_play_numSD >= 0 ) {
                        Mp3Info mp3InfoSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                        setMusicFragmentContent(mp3InfoSD);
                        setMusicProgress();
                        if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED && (playMusicService != null && (BaseApp.ispauseSD ==0 || BaseApp.ispauseSD ==2))) {
                            BaseApp.ispauseSD =0;
                            playMusicService.playSD(BaseApp.current_music_play_numSD);
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }else if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (playMusicService != null && (BaseApp.ispauseSD ==0 || BaseApp.ispauseSD ==2))){ //还没有扫描完，就切了过来还是要进行播放啊
                            BaseApp.ispauseSD =0;
                            button_bofang.setImageResource(R.mipmap.bofang);
                            if (BaseApp.current_music_play_numSD >= 0) {
                                playMusicService.playSD(BaseApp.current_music_play_numSD);
                            }
                        }
                    } else { //加载的时候切换usb和蓝牙的时候出现了bug
                        setDefaultMusicFragmentContent();
                        setDefaultMusicProgress();
                        button_bofang.setImageResource(R.mipmap.zanting);
                    }
                }else { //加载的时候切换usb和蓝牙的时候出现了bug
                    setDefaultMusicFragmentContent();
                    setDefaultMusicProgress();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                break;
            case 0:
            //    上一个源是usb，切换到sd不需要切换ui
                //BaseApp.current_music_play_numSD >0加上这个判断条件，是防止在加载的过程中，没有找到对应的曲目的问题
                if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size() >0  && BaseApp.current_music_play_numSD >=0) {
                    if (BaseApp.current_music_play_numSD >= 0 && BaseApp.current_music_play_numSD < BaseApp.mp3InfosSD.size() ) {
                    }else{
                        BaseApp.current_music_play_numSD = 0;
                    }
                    if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED &&(playMusicService != null && (BaseApp.ispauseSD ==0 || BaseApp.ispauseSD ==2))) {
                        BaseApp.ispauseSD = 0;
                        playMusicService.playSD(BaseApp.current_music_play_numSD);
                    }else if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (playMusicService != null && (BaseApp.ispauseSD ==0 || BaseApp.ispauseSD ==2))){ //还没有扫描完，就切了过来还是要进行播放啊
                        BaseApp.ispauseSD =0;
                        button_bofang.setImageResource(R.mipmap.bofang);
                        if (BaseApp.current_music_play_numSD >= 0) {
                            playMusicService.playSD(BaseApp.current_music_play_numSD);
                        }
                    }
                    BaseUtils.mlog(TAG, "onChangePlayfromSDToUSB----");
                    musicFragment.changeMusicPlayModeUI(BaseApp.music_play_mode);
                    Mp3Info mp3InfoSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                    setMusicFragmentContent(mp3InfoSD);
                    setMusicProgress();
                    if(BaseApp.ispauseSD == 0 || BaseApp.ispauseSD == 2) {
                        button_bofang.setImageResource(R.mipmap.bofang);
                    }else{
                        button_bofang.setImageResource(R.mipmap.zanting);
                    }
                }else{
                    setDefaultMusicFragmentContent();
                    setDefaultMusicProgress();
                    button_bofang.setImageResource(R.mipmap.zanting);
                    if((BaseApp.mp3Infos!=null && playMusicService!=null && playMusicService.isPlaying())){
                        BaseApp.ispauseUSB = 2;
                        playMusicService.pause();
                    }
                }

                if(BaseApp.mp3InfosSD!=null && playMusicService!=null){
                    BaseUtils.mlog(TAG,"-----BaseApp.ispauseSD-----"+BaseApp.ispauseUSB);
                    BaseApp.ispauseUSB = 2;
                    BaseUtils.mlog(TAG, "-----BaseApp.ispauseSD-----" + BaseApp.ispauseUSB);
//                    playMusicService.pause();
                }
            //    BaseApp.current_music_play_progressSD = 0;

                break;
        }
    }

    public void AUXEent() {   //切换到AUX，处理之前的状态
        BaseUtils.mlog(TAG, "-------------onAUXEent-------------");
        BaseUtils.mlog(TAG, "-------------BaseApp.last_playSourceManager-------------" + BaseApp.last_playSourceManager);
        mSourceManager.requestSourceToAUX();
        BaseApp.app_inner_source = true;  //用于判断是内部源的切换
        button_layout_dilan.setBackgroundResource(R.mipmap.aux_dilan);
        switch(BaseApp.last_playSourceManager) {
            case 0:
                if(playMusicService != null && playMusicService.isPlaying()) {
                    BaseApp.ispauseUSB = 2;  //如果想下次进入的时候继续播放，在前面加上这句话就行了
                    playMusicService.pause();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                choosedilanAndFragmentShow(0);
                break;
            case 1:
                if(mBtmusictPlay.mPlayStatus){//源出去了，直接停止播放
                    mBtmusictPlay.musicPlayOrPause();
                }
                mBtmusictPlay.outSoundisPlay =0;  //切掉音源
                mBtmusictPlay.mPlayStatus = false;//一定要设置，不然快速切换的时候，可能状态还没有改变，就有切回来了
                choosedilanAndFragmentShow(0);
                break;
            case 3:
                if(playMusicService != null && playMusicService.isPlaying()) {
                    BaseApp.ispauseSD = 2;  //如果想下次进入的时候继续播放，在前面加上这句话就行了
                    playMusicService.pauseSD();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                choosedilanAndFragmentShow(0);
                break;

        }
    }

    public void changeUIToAUX() {   //切换到AUX，处理之前的状态
        BaseUtils.mlog(TAG, "-------------changeUIToAUX-------------");
        BaseUtils.mlog(TAG, "-------------last_playSourceManager-------------"+BaseApp.last_playSourceManager);
        mSourceManager.requestSourceToAUX();
        BaseApp.app_inner_source = true;  //用于判断是内部源的切换
        button_layout_dilan.setBackgroundResource(R.mipmap.aux_dilan);
        switch(BaseApp.last_playSourceManager) {
            case 0:
                if(playMusicService != null && playMusicService.isPlaying()) {
                    BaseApp.ispauseUSB = 2;  //如果想下次进入的时候继续播放，在前面加上这句话就行了
                    playMusicService.pause();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                choosedilanAndFragmentShow(0);
                break;
            case 1:
                if(mBtmusictPlay.mPlayStatus){//源出去了，直接停止播放
                    mBtmusictPlay.musicPlayOrPause();
                }
                choosedilanAndFragmentShow(0);
                break;
            case 3:
                if(playMusicService != null && playMusicService.isPlaying()) {
                    BaseApp.ispauseSD = 2;  //如果想下次进入的时候继续播放，在前面加上这句话就行了
                    playMusicService.pauseSD();
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
                choosedilanAndFragmentShow(0);
                break;
        }
    }


    public void SavePlaySource(int sourceID) {
        editor.putInt("PLAYSOURCE", sourceID);
        editor.commit();
    }

    @Override
    public void onMusicCancelCover() {
        if (media_fragment_image.getVisibility() == View.VISIBLE) {
            myHandler.sendEmptyMessageDelayed(Contents.MSG_CANCEL_COVER, 500);
        }
    }

    @Override
    public void onRequestFocus() {
        BaseUtils.mlog(TAG, "-------------每次进入申请focus-------------");
//        playMusicService.requestMusicFocus();  //如果是从图片进来怎么办？
    }

    @Override
    public void onPicLieBiaoClose() {
        leibieliebiao.setVisibility(View.GONE);
        BaseApp.ifliebiaoOpen = 0;
        BaseApp.liebiaoOpenNum = 0;
    }

    @Override
    public void onCancelPPT() {
        BaseUtils.mlog(TAG, "-------------onCancelPPT-------------");

        BaseApp.ifinPPT = false;
        button_bofang.setImageResource(R.mipmap.zanting_pic);
        if(pic_timer!=null){
            pic_timer.cancel();
            pic_timer = null;
        }
        exitFullScreen();

        pictureFragment.pic_shanglan_textview.setVisibility(View.GONE);
        pictureFragment.imageView_PPT.setVisibility(View.GONE);
        pictureFragment.big_pic_show.setVisibility(View.VISIBLE);

        if(BaseApp.playSourceManager == 0) {
            if(BaseApp.picInfos!=null && BaseApp.picInfos.size()> 0) {
                if (BaseApp.current_pic_play_numUSB == BaseApp.picInfos.size() - 1) {
                } else {
                    //每次退出时，都往前突一个，显得不是很好
                    BaseApp.current_pic_play_numUSB = BaseApp.current_pic_play_numUSB - 1;
                    if(BaseApp.current_pic_play_numUSB <0){
                        BaseApp.current_pic_play_numUSB = 0;
                    }
                }
                pictureFragment.changeImageShow(BaseApp.current_pic_play_numUSB);
            }
        }else if(BaseApp.playSourceManager == 3){
            if(BaseApp.picInfosSD!=null && BaseApp.picInfosSD.size() > 0) {
                if (BaseApp.current_pic_play_numSD == BaseApp.picInfosSD.size() - 1) {
                } else {
                    //每次退出时，都往前突一个，显得不是很好
                    BaseApp.current_pic_play_numSD = BaseApp.current_pic_play_numSD - 1;
                    if(BaseApp.current_pic_play_numSD <0){
                        BaseApp.current_pic_play_numSD = 0;
                    }
                }
                pictureFragment.changeImageShow(BaseApp.current_pic_play_numSD);
            }
        }
    }

    @Override
    public void onSavePicProgress() {
        BaseUtils.mlog(TAG, "--------onSavePicProgress-----change-------------");
        if (BaseApp.playSourceManager == 0) {
            if (BaseApp.picInfos.size() > 0 && BaseApp.current_pic_play_numUSB >= 0) {
                editor.putString("LASTPICPATHUSB", BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData());
                editor.apply();

                if (BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 2) {
                    myGridViewAdapter2.notifyDataSetChanged();
                }
            }
        } else if (BaseApp.playSourceManager == 3) {
            if (BaseApp.picInfosSD.size() > 0 && BaseApp.current_pic_play_numSD >= 0) {
                editor.putString("LASTPICPATHSD", BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData());
                editor.apply();

                if (BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 2) {
                    myGridViewAdapter2.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onPicCancelCover() {
        if (media_fragment_image.getVisibility() == View.VISIBLE) {
            myHandler.sendEmptyMessageDelayed(Contents.MSG_CANCEL_COVER, 500);
        }
    }

    @Override
    public void onVideoProgressSave() {
        if (BaseApp.playSourceManager == 0) {
            if (BaseApp.mp4Infos.size() > 0 && BaseApp.current_video_play_numUSB >= 0) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("LASTVIDEOPLAYTIMEUSB", BaseApp.current_video_play_progressUSB);//
                editor.apply();
            }
        } else if (BaseApp.playSourceManager == 3) {
            if (BaseApp.mp4InfosSD.size() > 0 && BaseApp.current_video_play_numSD >= 0) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("LASTVIDEOPLAYTIMESD", BaseApp.current_video_play_progressSD);
                editor.apply();
            }
        }
    }

    @Override
    public void onVideoStateChange() {
        BaseUtils.mlog(TAG, "-------------onVideoStateChange-------------");
        //如果不加入判断，导致第一从视频切过来，播放按钮过大。显示出bug
        if(BaseApp.current_media == 2 && BaseApp.current_fragment == 2){

        }else {
            button_bofang.setImageResource(R.mipmap.zanting);
        }
    }

    @Override
    public void onVideoScreenChangepre(int progress) {
        BaseUtils.mlog(TAG, "-------------onVideoScreenChangepre-------------");
    }

    @Override
    public void onVideoScreenChange(int progress) {
        BaseUtils.mlog(TAG, "-------------onVideoScreenChange-------------");
        //改变屏幕大小
        if(BaseApp.ifFullScreenState) {
                enterFullScreen();
        }else {
            if (BaseApp.ifliebiaoOpen == 0) {
                exitFullScreen();
//            }
            }
        }
    }

    @Override
    public void onVideoLieBiaoClose() {
        BaseUtils.mlog(TAG, "-------------onVideoLieBiaoClose-------------");
        leibieliebiao.setVisibility(View.GONE);
        BaseApp.ifliebiaoOpen = 0;
        BaseApp.liebiaoOpenNum = 0;
    }

    @Override
    public void onVideoNotifyUIChange(int ifstop) {
        BaseUtils.mlog(TAG, "-------------onVideoNotifyUIChange-------------");
        if(ifstop == 1){
            button_bofang.setImageResource(R.mipmap.zanting);
        }else{
            button_bofang.setImageResource(R.mipmap.bofang);
        }
        if(BaseApp.playSourceManager == 0) {
            if (BaseApp.current_fragment == 1 && BaseApp.video_play_mode == 1 && BaseApp.current_video_play_numUSB + 1 >= BaseApp.mp4Infos.size()) { //视频+顺序播放
                //还需要考虑全屏和小屏两种情况
                if (!BaseApp.ifFullScreenState && ifstop == 1) {  //不是全屏
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
            }
        }else if(BaseApp.playSourceManager == 3){
            if (BaseApp.current_fragment == 1 && BaseApp.video_play_mode == 1 && BaseApp.current_video_play_numSD + 1 >= BaseApp.mp4InfosSD.size()) { //视频+顺序播放
                //还需要考虑全屏和小屏两种情况
                if (!BaseApp.ifFullScreenState && ifstop == 1) {  //不是全屏
                    button_bofang.setImageResource(R.mipmap.zanting);
                }
            }
        }
    }

    @Override
    public void onVideoNotifyUIliebiaoChange() {
        BaseUtils.mlog(TAG, "-------------onVideoNotifyUIliebiaoChange-------------");
        //当列表打开的时候，需要重新刷新
        if(BaseApp.ifliebiaoOpen == 1 && myvideolistviewAdapter != null){
            myvideolistviewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onVideoItemChange() {
        BaseUtils.mlog(TAG, "-------------change-------------");
        if (BaseApp.playSourceManager == 0) {
            if (BaseApp.mp4Infos.size() > 0 && BaseApp.current_video_play_numUSB >= 0) {
                editor.putString("LASTVIDEOPATHUSB", BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getData());
                editor.apply();

                if (BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 1 && myvideolistviewAdapter!=null) {
                    myvideolistviewAdapter.notifyDataSetChanged();
                }
            }
        }else if (BaseApp.playSourceManager == 3) {
            if (BaseApp.mp4InfosSD.size() > 0 && BaseApp.current_video_play_numSD >= 0) {
                editor.putString("LASTVIDEOPATHSD", BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getData());
                editor.apply();

                if (BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 1 && myvideolistviewAdapter!=null) {
                    myvideolistviewAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onVideoCancelCover() {
        BaseUtils.mlog(TAG, "-------------onVideoCancelCover-------------");
        if(media_fragment_image.getVisibility() == View.VISIBLE) {  //解决手刹背景被取消的bug
            myHandler.sendEmptyMessageDelayed(Contents.MSG_CANCEL_COVER, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        BaseUtils.mlog(TAG, "-------------onClick-------------");
        BaseApp.liebiaoOpenNum = 0;
        switch(v.getId()){
            case R.id.button_shangqu:
                BaseUtils.mlog(TAG, "-------------button_shangqu-------------");
                if(BaseApp.current_fragment == 1){
                    videoFragment.fullScreen = 0;
                }

                BaseApp.ifliebiaoOpen = 0;
                leibieliebiao.setVisibility(View.GONE);

                if(BaseApp.playSourceManager == 0) {
                    if (BaseApp.current_fragment == 0 && BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0) {
                        if (BaseApp.current_music_play_numUSB >= 0) {  //刷新的过程中，点击无效
                            playMusicService.prev();
                        }
                    } else if (BaseApp.current_fragment == 1 && BaseApp.mp4Infos != null && BaseApp.mp4Infos.size() > 0) {
                        if(BaseApp.brake_flag) {
                            videoFragment.playVideopre();
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }
                    } else if (BaseApp.current_fragment == 2 && BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                        pictureFragment.playPicpre();
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (BaseApp.current_fragment == 0 && BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0) {
                        if (BaseApp.current_music_play_numSD >= 0) {  //刷新的过程中，点击无效
                            playMusicService.prevSD();
                        }
                    } else if (BaseApp.current_fragment == 1 && BaseApp.mp4InfosSD != null && BaseApp.mp4InfosSD.size() > 0) {
                        if(BaseApp.brake_flag) {
                            videoFragment.playVideopre();
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }
                    } else if (BaseApp.current_fragment == 2 && BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                        pictureFragment.playPicpre();
                    }
                }
                break;

            case R.id.button_bofang:
                BaseUtils.mlog(TAG, "-------------button_bofang-------------");
                if(BaseApp.current_fragment == 1){
                    videoFragment.fullScreen = 0;
                }

                BaseApp.ifliebiaoOpen = 0;
                leibieliebiao.setVisibility(View.GONE);

                if(BaseApp.playSourceManager == 0) {
                    BaseUtils.mlog(TAG,"-------BaseApp.ispauseUSB-------"+BaseApp.ispauseUSB);

                    if (BaseApp.current_fragment == 0 && BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0) {
                        if (playMusicService.isPlaying()) {
                            button_bofang.setImageResource(R.mipmap.zanting);
                            playMusicService.pause();
                        } else {
                            if (BaseApp.ispauseUSB == 1) {
                                button_bofang.setImageResource(R.mipmap.bofang);
                                playMusicService.start_play();
                            } else {
                                if (BaseApp.current_music_play_numUSB >= 0) {
                                    playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                                    button_bofang.setImageResource(R.mipmap.bofang);
                                }
                            }
                        }
                    } else if (BaseApp.current_fragment == 1 && BaseApp.mp4Infos != null && BaseApp.mp4Infos.size() > 0) {
                        if(BaseApp.brake_flag) {
                            if (BaseApp.isVideopauseUSB == 0) {
                                BaseUtils.mlog(TAG, "-onClick-----------1111-----------");
                                button_bofang.setImageResource(R.mipmap.zanting);   //三角形
                                videoFragment.pause();
                                BaseApp.isVideopauseUSB = 1;
                            } else {
                                if (BaseApp.isVideopauseUSB == 1) {
                                    BaseApp.isVideopauseUSB = 0;
                                    BaseUtils.mlog(TAG, "-onClick-----------2222-----------");
                                    button_bofang.setImageResource(R.mipmap.bofang);
                                    videoFragment.start();
                                } else {
                                    BaseApp.isVideopauseUSB = 0;
                                    BaseUtils.mlog(TAG, "-onClick----------3333-----------");
                                    button_bofang.setImageResource(R.mipmap.bofang);
                                    videoFragment.play_video(BaseApp.mp4Infos.get(BaseApp.current_video_play_numUSB).getData());
                                }
                            }
                        }
                    } else if (BaseApp.current_fragment == 2 && BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                        button_bofang.setImageResource(R.mipmap.bofang_pic);
                        //开启幻灯片Activity
                        BaseApp.ifinPPT = true;
                        pictureFragment.big_pic_show.setVisibility(View.GONE);
                        pictureFragment.pic_shanglan_textview.setVisibility(View.GONE);
                        button_layout_dilan.setBackgroundResource(0);
                        pictureFragment.imageView_PPT.setImageURI(Uri.parse(BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData()));
                        pictureFragment.imageView_PPT.setVisibility(View.VISIBLE);

                        enterFullScreen();
                        pic_timer = new Timer();
                        pic_timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                myHandler.sendEmptyMessage(Contents.PPT_COMEBAKC);
                            }
                        }, 0, 3000);
                    }
                }else if(BaseApp.playSourceManager == 3){
                    BaseUtils.mlog(TAG,"-------BaseApp.ispauseSD-------"+BaseApp.ispauseSD);
                    if (BaseApp.current_fragment == 0 && BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0) {
                        if (playMusicService.isPlaying()) {
                            button_bofang.setImageResource(R.mipmap.zanting);
                            playMusicService.pauseSD();
                        } else {
                            if (BaseApp.ispauseSD == 1) {
                                BaseApp.ispauseSD = 0;
                                button_bofang.setImageResource(R.mipmap.bofang);
                                playMusicService.start_play();
                            } else {
                                if (BaseApp.current_music_play_numSD >= 0) {
                                    playMusicService.playSD(BaseApp.current_music_play_numSD);
                                    button_bofang.setImageResource(R.mipmap.bofang);
                                }
                            }
                        }
                    } else if (BaseApp.current_fragment == 1 && BaseApp.mp4InfosSD != null && BaseApp.mp4InfosSD.size() > 0) {
                        if(BaseApp.brake_flag) {
                            if (videoFragment.isPlaying()) {
                                BaseUtils.mlog(TAG, "-onClick-" + "----------1111-----------");
                                button_bofang.setImageResource(R.mipmap.zanting);   //三角形
                                videoFragment.pause();
                                BaseApp.isVideopauseSD = 1;
                            } else {
                                if ((BaseApp.isVideopauseSD == 1)) {
                                    BaseUtils.mlog(TAG, "-onClick-" + "----------2222-----------");
                                    BaseApp.isVideopauseSD = 0;
                                    button_bofang.setImageResource(R.mipmap.bofang);
                                    videoFragment.start();
                                } else {
                                    BaseApp.isVideopauseSD = 0;
                                    BaseUtils.mlog(TAG, "-onClick-" + "----------3333-----------");
                                    button_bofang.setImageResource(R.mipmap.bofang);
                                    videoFragment.play_video(BaseApp.mp4InfosSD.get(BaseApp.current_video_play_numSD).getData());
                                }
                            }
                        }
                    } else if (BaseApp.current_fragment == 2 && BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                        button_bofang.setImageResource(R.mipmap.bofang_pic);
                        //开启幻灯片Activity
                        BaseApp.ifinPPT = true;

                        button_layout_dilan.setBackgroundResource(0);
                        pictureFragment.big_pic_show.setVisibility(View.GONE);
                        pictureFragment.pic_shanglan_textview.setVisibility(View.GONE);
                        pictureFragment.imageView_PPT.setImageURI(Uri.parse(BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData()));
                        pictureFragment.imageView_PPT.setVisibility(View.VISIBLE);

                        enterFullScreen();
                        pic_timer = new Timer();
                        pic_timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if(BaseApp.current_fragment == 2) {
                                    myHandler.sendEmptyMessage(Contents.PPT_COMEBAKC);
                                }
                            }
                        }, 0, 3000);
                    }
                }
                break;
            case R.id.button_xiaqu:
                BaseUtils.mlog(TAG, "-------------button_xiaqu-------------");
                if(BaseApp.current_fragment == 1){
                    videoFragment.fullScreen = 0;
                }
                BaseApp.ifliebiaoOpen = 0;
                leibieliebiao.setVisibility(View.GONE);

                if(BaseApp.playSourceManager == 0){
                    if(BaseApp.current_fragment == 0 && BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0){
                        if(BaseApp.current_music_play_numUSB >=0) {
                            playMusicService.next();
                        }
                    }else if(BaseApp.current_fragment == 1&& BaseApp.mp4Infos != null && BaseApp.mp4Infos.size() > 0){
                        if(BaseApp.brake_flag) {
                            if (BaseApp.current_video_play_numUSB >= 0) {
                                videoFragment.playVideonext();
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                    }else if(BaseApp.current_fragment == 2 && BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                        if(BaseApp.current_pic_play_numUSB>=0) {
                            pictureFragment.playPicnext();
                        }
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if(BaseApp.current_fragment == 0 && BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0){
                        if(BaseApp.current_music_play_numSD >=0) {
                            playMusicService.nextSD();
                        }
                    }else if(BaseApp.current_fragment == 1&& BaseApp.mp4InfosSD != null && BaseApp.mp4InfosSD.size() > 0){
                        if(BaseApp.brake_flag) {
                            if (BaseApp.current_video_play_numSD >= 0) {
                                videoFragment.playVideonext();
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                    }else if(BaseApp.current_fragment == 2 && BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                        if(BaseApp.current_pic_play_numSD>=0) {
                            pictureFragment.playPicnext();
                        }
                    }
                }
                break;
            case R.id.button_play_mode:
                BaseUtils.mlog(TAG, "-------------button_play_mode-------------");
                if(BaseApp.current_fragment == 1){
                    videoFragment.fullScreen = 0;
                }
                BaseApp.ifliebiaoOpen = 0;
                leibieliebiao.setVisibility(View.GONE);

                if(BaseApp.playSourceManager == 0) {
                    if (BaseApp.current_fragment == 0 && BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0) {
                        BaseApp.music_play_mode++;
                        if (BaseApp.music_play_mode >= 4) {
                            BaseApp.music_play_mode = 0;
                        }
                        editor.putInt("MUSICPLAYMODE", BaseApp.music_play_mode);
                        editor.apply();

                        button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                        musicFragment.changeMusicPlayModeUI(BaseApp.music_play_mode);
                    } else if (BaseApp.current_fragment == 1 && BaseApp.mp4Infos != null && BaseApp.mp4Infos.size() > 0) {
                        if(BaseApp.brake_flag) {
                            BaseApp.video_play_mode++;
                            if (BaseApp.video_play_mode >= 4) {
                                BaseApp.video_play_mode = 0;
                            }
                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.video_play_mode]);
                            editor.putInt("VIDEOPLAYMODE", BaseApp.video_play_mode);
                            editor.apply();
                        }
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (BaseApp.current_fragment == 0 && BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0) {
                        BaseApp.music_play_mode++;
                        if (BaseApp.music_play_mode >= 4) {
                            BaseApp.music_play_mode = 0;
                        }
                        editor.putInt("MUSICPLAYMODE", BaseApp.music_play_mode);
                        editor.apply();

                        button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                        musicFragment.changeMusicPlayModeUI(BaseApp.music_play_mode);
                    } else if (BaseApp.current_fragment == 1 && BaseApp.mp4InfosSD != null && BaseApp.mp4InfosSD.size() > 0) {
                        if(BaseApp.brake_flag) {
                            BaseApp.video_play_mode++;
                            if (BaseApp.video_play_mode >= 4) {
                                BaseApp.video_play_mode = 0;
                            }
                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.video_play_mode]);
                            editor.putInt("VIDEOPLAYMODE", BaseApp.video_play_mode);
                            editor.apply();
                        }
                    }
                }
                break;
            case R.id.button_fangda:
                BaseUtils.mlog(TAG, "-------------button_fangda-------------");
                if(BaseApp.ifliebiaoOpen ==1){
                    BaseApp.ifliebiaoOpen = 0;
                    leibieliebiao.setVisibility(View.GONE);

                }
                if(BaseApp.playSourceManager == 0) {
                    if (BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                        pictureFragment.pic_play_fangda();
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (BaseApp.picInfos != null && BaseApp.picInfosSD.size() > 0) {
                        pictureFragment.pic_play_fangda();
                    }
                }
                break;
            case R.id.button_suoxiao:
                if(BaseApp.ifliebiaoOpen ==1){
                    BaseApp.ifliebiaoOpen = 0;
                    leibieliebiao.setVisibility(View.GONE);

                }
                if(BaseApp.playSourceManager == 0) {
                    if (BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                        pictureFragment.pic_play_suoxiao();
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                        pictureFragment.pic_play_suoxiao();
                    }
                }
                break;
            case R.id.button_liebiao:
                BaseUtils.mlog(TAG, "-------------button_liebiao-------------");
                if(BaseApp.ifFullScreenState){  //当在处理这个的时候onVideoScreenChange，发生了点击事件全屏了但是列表还在
                    if(BaseApp.ifliebiaoOpen == 0){
                        leibieliebiao.setVisibility(View.GONE);
                    }
                    break;
                }
                BaseApp.liebiaoOpenNum = 0;
                if(BaseApp.current_fragment == 1){
                    videoFragment.fullScreen = 0;
                }
                if(BaseApp.ifliebiaoOpen == 0) {  //列表出现的时候，点击还是有几率点到后面的布局
                    BaseApp.current_media = BaseApp.current_fragment;//客户要求点击之后出来的列表与当前fragment一致
                    BaseApp.ifliebiaoOpen =1;
                    leibieliebiao.setVisibility(View.VISIBLE);

                    switch(BaseApp.current_media){
                        case 0:
                            button_music.setBackgroundResource(R.mipmap.liebiao_p);
                            button_video.setBackgroundResource(0);
                            button_pic.setBackgroundResource(0);
                            break;
                        case 1:
                            button_music.setBackgroundResource(0);
                            button_video.setBackgroundResource(R.mipmap.liebiao_p);
                            button_pic.setBackgroundResource(0);
                            break;
                        case 2:
                            button_music.setBackgroundResource(0);
                            button_video.setBackgroundResource(0);
                            button_pic.setBackgroundResource(R.mipmap.liebiao_p);
                            break;
                    }


                    if(BaseApp.playSourceManager == 0) {
                        if (BaseApp.current_media == 0) {  //打开的是音乐列表
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                                BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if (BaseApp.music_media_state_scan == 0) {
                                        BaseApp.music_media_state_scan = 1;
                                        liebiaoShowNOorScan(1);
                                    }
                                } else if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if(BaseApp.ifmusicReadFinishUSB) {
                                        BaseApp.music_media_state_scan = 0;
                                        liebiaoShowNOorScan(0);
                                    }else{
                                        if (BaseApp.music_media_state_scan == 0) {
                                            BaseApp.music_media_state_scan = 1;
                                            liebiaoShowNOorScan(1);
                                        }
                                    }
                                } else {
                                    chooseListViewToShow(0);
                                    BaseApp.music_media_state_scan = 0;
                                    if (mymusiclistviewAdapter != null) {
                                        mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                        musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                    } else {
                                        mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                        musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                    }
                                    if (BaseApp.current_music_play_numUSB >= 0) {
                                        musiclistview_id.setSelection(BaseApp.current_music_play_numUSB);
                                    }
//                                    mymusiclistviewAdapter.notifyDataSetChanged();  //setselection 之后不需要再用这个了
                                }
                            }
                        } else if (BaseApp.current_media == 1) {   //打开视频列表
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                                BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if (BaseApp.video_media_state_scan == 0) {
                                        BaseApp.video_media_state_scan = 1;
                                        liebiaoShowNOorScan(1);
                                    }
                                } else if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if(BaseApp.ifmusicReadFinishUSB) {
                                        BaseApp.music_media_state_scan = 0;
                                        liebiaoShowNOorScan(0);
                                    }else{
                                        if (BaseApp.music_media_state_scan == 0) {
                                            BaseApp.music_media_state_scan = 1;
                                            liebiaoShowNOorScan(1);
                                        }
                                    }
                                } else {
                                    chooseListViewToShow(1);

                                    BaseApp.video_media_state_scan = 0;
                                    //   musicvideolist.requestFocusFromTouch();
                                    if (myvideolistviewAdapter != null) {
                                        myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                        videolistview_id.setAdapter(myvideolistviewAdapter);
                                    } else {
                                        myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                        videolistview_id.setAdapter(myvideolistviewAdapter);
                                    }
                                    if (BaseApp.current_video_play_numUSB >= 0) {
                                        videolistview_id.setSelection(BaseApp.current_video_play_numUSB);
                                    }
//                                    myvideolistviewAdapter.notifyDataSetChanged();
                                }
                            }
                        } else if (BaseApp.current_media == 2) {
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                                BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                                liebiaoShowNOorScan(0);
                            } else {
                                if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfos == null || BaseApp.picInfos.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if (BaseApp.pic_media_state_scan == 0) {
                                        BaseApp.pic_media_state_scan = 1;
                                        liebiaoShowNOorScan(1);
                                    }
                                } else if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfos == null || BaseApp.picInfos.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if(BaseApp.ifmusicReadFinishUSB) {
                                        BaseApp.music_media_state_scan = 0;
                                        liebiaoShowNOorScan(0);
                                    }else{
                                        if (BaseApp.music_media_state_scan == 0) {
                                            BaseApp.music_media_state_scan = 1;
                                            liebiaoShowNOorScan(1);
                                        }
                                    }
                                } else {
                                    chooseListViewToShow(2);
                                    BaseApp.pic_media_state_scan = 0;

                                    if (myGridViewAdapter2 != null) {
                                        myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                        gridview_id.setAdapter(myGridViewAdapter2);
                                    } else {
                                        BaseUtils.mlog(TAG, "-onClick-" + "-onClick-" + "picInfos is OK,come to update the gridview...");
                                        myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                        gridview_id.setAdapter(myGridViewAdapter2);
                                    }
                                    if (BaseApp.current_pic_play_numUSB >= 0) {
                                        gridview_id.setSelection(BaseApp.current_pic_play_numUSB);
                                    }
//                                    myGridViewAdapter2.notifyDataSetChanged();
                                }
                            }
                        }
                    }else if(BaseApp.playSourceManager == 3){  //SD 卡模式
                        if (BaseApp.current_media == 0) {  //打开的是音乐列表
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_UNMOUNTED) {
                                BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if (BaseApp.music_media_state_scanSD == 0) {
                                        BaseApp.music_media_state_scanSD = 1;
                                        liebiaoShowNOorScan(1);
                                    }
                                } else if (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if(BaseApp.ifmusicReadFinishSD) {
                                        BaseApp.music_media_state_scan = 0;
                                        liebiaoShowNOorScan(0);
                                    }else{
                                        if (BaseApp.music_media_state_scanSD == 0) {
                                            BaseApp.music_media_state_scanSD = 1;
                                            liebiaoShowNOorScan(1);
                                        }
                                    }
                                } else {
                                    chooseListViewToShow(0);
                                    BaseApp.music_media_state_scanSD = 0;
                                    //
                                    if (mymusiclistviewAdapter != null) {
                                        mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                        musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                    } else {
                                        mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                        musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                    }
                                    if (BaseApp.current_music_play_numSD >= 0) {
                                        musiclistview_id.setSelection(BaseApp.current_music_play_numSD);
                                    }
//                                    mymusiclistviewAdapter.notifyDataSetChanged();
                                }
                            }
                        } else if (BaseApp.current_media == 1) {   //打开视频列表
                            if (mDeviceStateSD == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                                BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if (BaseApp.video_media_state_scanSD == 0) {
                                        BaseApp.video_media_state_scanSD = 1;
                                        liebiaoShowNOorScan(1);
                                    }
                                } else if (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if(BaseApp.ifmusicReadFinishSD) {
                                        BaseApp.music_media_state_scan = 0;
                                        liebiaoShowNOorScan(0);
                                    }else{
                                        if (BaseApp.music_media_state_scanSD == 0) {
                                            BaseApp.music_media_state_scanSD = 1;
                                            liebiaoShowNOorScan(1);
                                        }
                                    }
                                } else {
                                    chooseListViewToShow(1);

                                    BaseApp.video_media_state_scanSD = 0;
                                    //   musicvideolist.requestFocusFromTouch();
                                    if (myvideolistviewAdapter != null) {
                                        myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                        videolistview_id.setAdapter(myvideolistviewAdapter);
                                    } else {
                                        myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                        videolistview_id.setAdapter(myvideolistviewAdapter);
                                    }
                                    if (BaseApp.current_video_play_numSD >= 0) {
                                        videolistview_id.setSelection(BaseApp.current_video_play_numSD);
                                    }
//                                    myvideolistviewAdapter.notifyDataSetChanged();
                                }
                            }
                        } else if (BaseApp.current_media == 2) {
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_UNMOUNTED) {
                                BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if (BaseApp.pic_media_state_scanSD == 0) {
                                        BaseApp.pic_media_state_scanSD = 1;
                                        liebiaoShowNOorScan(1);
                                    }
                                } else if (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0)) {
                                    chooseListViewToShow(3);
                                    if(BaseApp.ifmusicReadFinishSD) {
                                        BaseApp.music_media_state_scan = 0;
                                        liebiaoShowNOorScan(0);
                                    }else{
                                        if (BaseApp.music_media_state_scanSD == 0) {
                                            BaseApp.music_media_state_scanSD = 1;
                                            liebiaoShowNOorScan(1);
                                        }
                                    }
                                } else {
                                    chooseListViewToShow(2);
                                    BaseApp.pic_media_state_scanSD = 0;

                                    if (myGridViewAdapter2 != null) {
                                        myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                        gridview_id.setAdapter(myGridViewAdapter2);
                                    } else {
                                        BaseUtils.mlog(TAG, "-onClick-" + "-onClick-" + "picInfos is OK,come to update the gridview...");
                                        myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                        gridview_id.setAdapter(myGridViewAdapter2);
                                    }
                                    if (BaseApp.current_pic_play_numSD >= 0) {
                                        gridview_id.setSelection(BaseApp.current_pic_play_numSD);
                                    }
//                                    myGridViewAdapter2.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }else{
                    BaseApp.ifliebiaoOpen = 0;
                    leibieliebiao.setVisibility(View.GONE);

                }
                break;
            case R.id.button_music:
                BaseUtils.mlog(TAG, "-------------button_music-------------");
                BaseApp.liebiaoOpenNum = 0;
                BaseApp.current_media = 0;
                button_music.setBackgroundResource(R.mipmap.liebiao_p);
                button_video.setBackground(null);
                button_pic.setBackground(null);
                if(BaseApp.playSourceManager == 0) {
                    if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                        BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                        chooseListViewToShow(3);
                        liebiaoShowNOorScan(0);
                    } else {
                        if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0)) {
                            chooseListViewToShow(3);
                            if (BaseApp.music_media_state_scan == 0) {
                                BaseApp.music_media_state_scan = 1;
                                liebiaoShowNOorScan(1);
                            }
                        } else if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0)) {
                            chooseListViewToShow(3);
                            if(BaseApp.ifmusicReadFinishSD) {
                                BaseApp.music_media_state_scan = 0;
                                liebiaoShowNOorScan(0);
                            }else{
                                if (BaseApp.music_media_state_scan == 0) {
                                    BaseApp.music_media_state_scan = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            }
                        } else  if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size()>0){
                            chooseListViewToShow(0);
                            BaseApp.music_media_state_scan = 0;
                            if (mymusiclistviewAdapter != null) {
                                mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                musiclistview_id.setAdapter(mymusiclistviewAdapter);
                            } else {
                                mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                musiclistview_id.setAdapter(mymusiclistviewAdapter);
                            }

                            if (BaseApp.current_music_play_numUSB >= 0) {
                                musiclistview_id.setSelection(BaseApp.current_music_play_numUSB);
                            }
//                            mymusiclistviewAdapter.notifyDataSetChanged();
                        }
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (mDeviceStateSD == Contents.SD_DEVICE_STATE_UNMOUNTED) {
                        BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                        chooseListViewToShow(3);
                        liebiaoShowNOorScan(0);
                    } else {
                        if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0)) {
                            chooseListViewToShow(3);
                            if (BaseApp.music_media_state_scanSD == 0) {
                                BaseApp.music_media_state_scanSD = 1;
                                liebiaoShowNOorScan(1);
                            }
                        } else if (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0)) {
                            chooseListViewToShow(3);
                            if(BaseApp.ifmusicReadFinishSD) {
                                BaseApp.music_media_state_scanSD = 0;
                                liebiaoShowNOorScan(0);
                            }else{
                                if (BaseApp.music_media_state_scanSD == 0) {
                                    BaseApp.music_media_state_scanSD = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            }
                        } else if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size()>0){
                            chooseListViewToShow(0);
                            BaseApp.music_media_state_scanSD = 0;
                            //    musicvideolist.requestFocusFromTouch();
                            if (mymusiclistviewAdapter != null) {
                                mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                musiclistview_id.setAdapter(mymusiclistviewAdapter);
                            } else {
                                mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                musiclistview_id.setAdapter(mymusiclistviewAdapter);
                            }

                            if (BaseApp.current_music_play_numSD >= 0) {
                                musiclistview_id.setSelection(BaseApp.current_music_play_numSD);
                            }
//                            mymusiclistviewAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
            case R.id.button_video:
                BaseUtils.mlog(TAG, "-------------button_video-------------");
                BaseApp.liebiaoOpenNum = 0;
                BaseApp.current_media = 1;
                button_video.setBackgroundResource(R.mipmap.liebiao_p);
                button_music.setBackground(null);
                button_pic.setBackground(null);
                if(BaseApp.playSourceManager == 0) {
                    if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                        BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                        chooseListViewToShow(3);
                        liebiaoShowNOorScan(0);
                    } else {
                        if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0)) {
                            chooseListViewToShow(3);
                            if (BaseApp.video_media_state_scan == 0) {
                                BaseApp.video_media_state_scan = 1;
                                liebiaoShowNOorScan(1);
                            }
                        } else if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0)) {
                            chooseListViewToShow(3);
                            if(BaseApp.ifvideoReadFinishUSB) {
                                BaseApp.video_media_state_scan = 0;
                                liebiaoShowNOorScan(0);
                            }else{
                                if (BaseApp.video_media_state_scan == 0) {
                                    BaseApp.video_media_state_scan = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            }
                        } else if(BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0){
                            chooseListViewToShow(1);
                            BaseApp.video_media_state_scan = 0;
                            if (myvideolistviewAdapter != null) {
                                myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                videolistview_id.setAdapter(myvideolistviewAdapter);
                            } else {
                                myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                videolistview_id.setAdapter(myvideolistviewAdapter);
                            }

                            if (BaseApp.current_video_play_numUSB >= 0) {
                                videolistview_id.setSelection(BaseApp.current_video_play_numUSB);
                            }
//                            myvideolistviewAdapter.notifyDataSetChanged();
                        }
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (mDeviceStateSD == Contents.SD_DEVICE_STATE_UNMOUNTED) {
                        BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                        chooseListViewToShow(3);
                        liebiaoShowNOorScan(0);
                    } else {
                        if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0)) {
                            chooseListViewToShow(3);
                            if (BaseApp.video_media_state_scanSD == 0) {
                                BaseApp.video_media_state_scanSD = 1;
                                liebiaoShowNOorScan(1);
                            }
                        } else if (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0)) {
                            chooseListViewToShow(3);
                            if(BaseApp.ifvideoReadFinishSD) {
                                BaseApp.video_media_state_scanSD = 0;
                                liebiaoShowNOorScan(0);
                            }else{
                                if (BaseApp.video_media_state_scanSD == 0) {
                                    BaseApp.video_media_state_scanSD = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            }
                        } else if(BaseApp.mp4InfosSD!=null && BaseApp.mp4InfosSD.size()>0){
                            chooseListViewToShow(1);
                            BaseApp.video_media_state_scanSD = 0;
                            if (myvideolistviewAdapter != null) {
                                myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                videolistview_id.setAdapter(myvideolistviewAdapter);
                            } else {
                                myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                videolistview_id.setAdapter(myvideolistviewAdapter);
                            }

                            if (BaseApp.current_video_play_numSD >= 0) {
                                videolistview_id.setSelection(BaseApp.current_video_play_numSD);
                            }
//                            myvideolistviewAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
            case R.id.button_pic:
                BaseApp.liebiaoOpenNum = 0;
                BaseUtils.mlog(TAG, "-------------button_pic-------------");
                BaseApp.current_media = 2;
                button_pic.setBackgroundResource(R.mipmap.liebiao_p);
                button_music.setBackground(null);
                button_video.setBackground(null);
                if(BaseApp.playSourceManager == 0) {
                    if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_UNMOUNTED) {
                        BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                        chooseListViewToShow(3);
                        liebiaoShowNOorScan(0);
                    } else {
                        if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfos == null || BaseApp.picInfos.size() == 0)) {
                            chooseListViewToShow(3);
                            if (BaseApp.pic_media_state_scan == 0) {
                                BaseApp.pic_media_state_scan = 1;
                                liebiaoShowNOorScan(1);
                            }
                        } else if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED &&(BaseApp.picInfos == null || BaseApp.picInfos.size() == 0)) {
                            chooseListViewToShow(3);
                            if(BaseApp.ifvideoReadFinishUSB) {
                                BaseApp.video_media_state_scan = 0;
                                liebiaoShowNOorScan(0);
                            }else{
                                if (BaseApp.video_media_state_scan == 0) {
                                    BaseApp.video_media_state_scan = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            }
                        } else if(BaseApp.picInfos!=null && BaseApp.picInfos.size()>0){
                            chooseListViewToShow(2);
                            BaseApp.pic_media_state_scan = 0;
                            if (myGridViewAdapter2 != null) {
                                myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                gridview_id.setAdapter(myGridViewAdapter2);
                            } else {
                                BaseUtils.mlog(TAG, "-onClick-" + "picInfos is OK,come to update the gridview...");
                                myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                gridview_id.setAdapter(myGridViewAdapter2);
                            }
                            if (BaseApp.current_pic_play_numUSB >= 0) {
                                gridview_id.setSelection(BaseApp.current_pic_play_numUSB);
                            }
                        }
                    }
                }else if(BaseApp.playSourceManager == 3){
                    if (mDeviceStateSD == Contents.SD_DEVICE_STATE_UNMOUNTED) {
                        BaseUtils.mlog(TAG, "-onClick-" + "unmount....");
                        chooseListViewToShow(3);
                        liebiaoShowNOorScan(0);
                    } else {
                        if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0)) {
                            chooseListViewToShow(3);
                            if (BaseApp.pic_media_state_scanSD == 0) {
                                BaseApp.pic_media_state_scanSD = 1;
                                liebiaoShowNOorScan(1);
                            }
                        } else if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED  && (BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0)) {
                            chooseListViewToShow(3);
                            if(BaseApp.ifvideoReadFinishSD) {
                                BaseApp.video_media_state_scanSD = 0;
                                liebiaoShowNOorScan(0);
                            }else{
                                if (BaseApp.video_media_state_scanSD == 0) {
                                    BaseApp.video_media_state_scanSD = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            }
                        } else if(BaseApp.picInfosSD!=null && BaseApp.picInfosSD.size()>0){
                            chooseListViewToShow(2);
                            BaseApp.pic_media_state_scanSD = 0;
                            if(BaseApp.picInfosSD!=null && BaseApp.picInfosSD.size()>0) {
                                if (myGridViewAdapter2 != null) {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                } else {
                                    BaseUtils.mlog(TAG, "-onClick-" + "picInfos is OK,come to update the gridview...");
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                }
                            }
                            if (BaseApp.current_pic_play_numSD >= 0) {
                                gridview_id.setSelection(BaseApp.current_pic_play_numSD);
                            }
                        }
                    }
                }
                break;
            case R.id.button_shangqu_bluetooth:
                BaseUtils.mlog(TAG, "-onClick-" + "点击了蓝牙的上曲按钮。。。。");
                mBtmusictPlay.musicPrevious();
//                mBtmusictPlay.getMusicInfo();
                break;
            case R.id.button_bofang_bluetooth:
                BaseUtils.mlog(TAG, "-onClick-" + "点击了蓝牙的播放按钮。。。。"+mBtmusictPlay.mPlayStatus);
                if(mBtmusictPlay!=null) {
                    mBtmusictPlay.musicPlayOrPause();//状态等改变之后，回调改变按钮状态
                }
                break;
            case R.id.button_xiaqu_bluetooth:
                BaseUtils.mlog(TAG, "-onClick-" + "点击了蓝牙的下曲按钮。。。。");
                mBtmusictPlay.musicNext();
//                mBtmusictPlay.getMusicInfo();
                break;
        }
    }

    @Override
    public void onSrcIn(int n) {
        BaseUtils.mlog(TAG,"---onSrcIn----"+n);
//        BaseApp.app_inner_source = false;
        if(BaseApp.app_out_source){
            BaseApp.app_out_source = false;
            if(playMusicService!=null && BaseApp.current_fragment!=1){  //非视频界面
                playMusicService.start_play();
            }
        }
    }
    @Override
    public void onSrcOut(int n) {
        if(BaseApp.app_inner_source) {
            BaseApp.app_inner_source = false;
            BaseUtils.mlog(TAG,"---onSrcOut---inner change source---"+n);
            switch (BaseApp.playSourceManager) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }else{
            BaseUtils.mlog(TAG,"---onSrcOut---out change source---"+n);
            BaseApp.app_out_source  = true;
            if(playMusicService!=null && BaseApp.current_fragment!=1){
                if(playMusicService.isPlaying()){
                    if(BaseApp.playSourceManager == 0) {
                        playMusicService.pause();
                        BaseApp.ispauseUSB = 2;
                    }else if(BaseApp.playSourceManager == 3){
                        playMusicService.pauseSD();
                        BaseApp.ispauseSD = 2;
                    }
                }
            }
        }
    }

    @Override
    public void onNextTrack(int n) {
        BaseUtils.mlog(TAG,"---onNextTrack---out change source---"+n);
        switch(n){
            case 0:
                if(BaseApp.current_fragment == 0 && BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0){
                    if(BaseApp.current_music_play_numUSB >=0) {
                        playMusicService.next();
                    }
                }else if(BaseApp.current_fragment == 1&& BaseApp.mp4Infos != null && BaseApp.mp4Infos.size() > 0){
                    if(BaseApp.brake_flag) {
                        if (BaseApp.current_video_play_numUSB >= 0) {
                            videoFragment.playVideonext();
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }
                    }
                }else if(BaseApp.current_fragment == 2 && BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                    if(BaseApp.current_pic_play_numUSB>=0) {
                        pictureFragment.playPicnext();
                    }
                }
                break;
            case 1:
                mBtmusictPlay.musicNext();
                break;
            case 3:
                if(BaseApp.current_fragment == 0 && BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0){
                    if(BaseApp.current_music_play_numSD >=0) {
                        playMusicService.nextSD();
                    }
                }else if(BaseApp.current_fragment == 1&& BaseApp.mp4InfosSD != null && BaseApp.mp4InfosSD.size() > 0){
                    if(BaseApp.brake_flag) {
                        if (BaseApp.current_video_play_numSD >= 0) {
                            videoFragment.playVideonext();
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }
                    }
                }else if(BaseApp.current_fragment == 2 && BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                    if(BaseApp.current_pic_play_numSD>=0) {
                        pictureFragment.playPicnext();
                    }
                }
                break;
        }
    }
    @Override
    public void onPrevouseTrack(int n) {
        BaseUtils.mlog(TAG,"---onPrevouseTrack---out change source---"+n);
        switch(n){
            case 0:
                if (BaseApp.current_fragment == 0 && BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() > 0) {
                    if (BaseApp.current_music_play_numUSB >= 0) {  //刷新的过程中，点击无效
                        playMusicService.prev();
                    }
                } else if (BaseApp.current_fragment == 1 && BaseApp.mp4Infos != null && BaseApp.mp4Infos.size() > 0) {
                    if(BaseApp.brake_flag) {
                        videoFragment.playVideopre();
                        button_bofang.setImageResource(R.mipmap.bofang);
                    }
                } else if (BaseApp.current_fragment == 2 && BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                    pictureFragment.playPicpre();
                }
                break;
            case 1:
                mBtmusictPlay.musicPrevious();
                break;
            case 3:
                if (BaseApp.current_fragment == 0 && BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() > 0) {
                    if (BaseApp.current_music_play_numSD >= 0) {  //刷新的过程中，点击无效
                        playMusicService.prevSD();
                    }
                } else if (BaseApp.current_fragment == 1 && BaseApp.mp4InfosSD != null && BaseApp.mp4InfosSD.size() > 0) {
                    if(BaseApp.brake_flag) {
                        videoFragment.playVideopre();
                        button_bofang.setImageResource(R.mipmap.bofang);
                    }
                } else if (BaseApp.current_fragment == 2 && BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                    pictureFragment.playPicpre();
                }
                break;
        }
    }
    @Override
    public void onExtEventIn(int n) {
        BaseUtils.mlog(TAG,"---onExtEventIn---out change source---"+n);
        if(playMusicService!=null && BaseApp.current_fragment!=1){
            if(playMusicService.isPlaying()){
                if(BaseApp.playSourceManager == 0) {
                    playMusicService.pause();
                    BaseApp.ispauseUSB = 2;
                }else if(BaseApp.playSourceManager == 3){
                    playMusicService.pauseSD();
                    BaseApp.ispauseSD = 2;
                }
            }
        }
    }
    @Override
    public void onExtEventOut(int n) {
        BaseUtils.mlog(TAG,"---onExtEventOut------"+n);
//        myHandler.sendEmptyMessageDelayed(998, 500);

        if(playMusicService!=null && BaseApp.current_fragment!=1){
            playMusicService.start_play();
        }
    }
    @Override
    public void onBrake(boolean flag,int n) {
        if(BaseApp.current_fragment == 1) {
            if (flag) {//true --》播放視頻  灯亮
                BaseUtils.mlog(TAG,"手刹关闭1----"+BaseApp.isVideopauseSD+"----BaseApp.current_video_play_numSD"+BaseApp.current_video_play_numSD);
                media_fragment_image.setVisibility(View.GONE);
                brake_layout.setVisibility(View.GONE);
                if(videoFragment!=null && !videoFragment.isPlaying()){//
                    if(BaseApp.playSourceManager == 0){
                        if(BaseApp.isVideopauseUSB == 1){  //在别的页面把手刹打开，进入视频界面，关闭手刹，会出问题video的初始值要为0，防止第一次进入手打打开的问题
                            videoFragment.start();
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }else if(BaseApp.isVideopauseUSB == 2) {
                            if(isTopActivity()) {
                                media_fragment_image.setVisibility(View.VISIBLE);
                                videoFragment.playVideoFromUser(BaseApp.current_video_play_numUSB);
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                    }else if(BaseApp.playSourceManager == 3){
                        if(BaseApp.isVideopauseSD == 1){
                            videoFragment.start();
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }else if(BaseApp.isVideopauseSD == 2) {
                            if(isTopActivity()) {
                                media_fragment_image.setVisibility(View.VISIBLE);
                                videoFragment.playVideoFromUser(BaseApp.current_video_play_numSD);
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                    }
                }
                BaseUtils.mlog(TAG,"手刹关闭2----"+BaseApp.isVideopauseSD+"----BaseApp.current_video_play_numSD"+BaseApp.current_video_play_numSD);
            }else{//手刹打开的时候点击无效，需要设置一下
                BaseUtils.mlog(TAG, "手刹打开1--------"+BaseApp.isVideopauseSD+"----BaseApp.current_video_play_numSD"+BaseApp.current_video_play_numSD);
//                media_fragment_image.setVisibility(View.VISIBLE);
//                brake_layout.setVisibility(View.VISIBLE);
                if(videoFragment!=null){//手刹的打开分两种情况，在video界面和不在video界面
                    if(videoFragment.isPlaying()) {
                        videoFragment.pause();
                    }else{
                        if (BaseApp.playSourceManager == 0) {
                            BaseApp.isVideopauseUSB = 2;
                        } else if (BaseApp.playSourceManager == 3) {
                            BaseApp.isVideopauseSD = 2;
                        }
//                        videoFragment.pause();  //在音乐界面点击，手刹，然后再到video中关闭手刹，这里播放会报错，直接导致进入下一曲
                    }
                }
                media_fragment_image.setVisibility(View.VISIBLE);
                brake_layout.setVisibility(View.VISIBLE);
                BaseUtils.mlog(TAG, "手刹打开2--------"+BaseApp.isVideopauseSD+"----BaseApp.current_video_play_numSD"+BaseApp.current_video_play_numSD);
            }
        }
    }

    @Override
    public void onCoverBrake(boolean flag) {
        if(videoFragment.isPlaying()) {
            button_bofang.setImageResource(R.mipmap.zanting);
        }else{
            button_bofang.setImageResource(R.mipmap.bofang);
        }

        BaseUtils.mlog(TAG,"onCoverBrake--------");
        onBrake(flag,0);
    }
    //////////////////////////////////////////////

    class MyHandler extends Handler {
        WeakReference<Activity > mActivityReference;
        MyHandler(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final Activity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    //998 不能放在onresume中，否则请求源不不会成功，蓝牙播放重启，进入了sd播放，一会蓝牙连接上之后，自动播放蓝牙音乐了。
                    case 998: //进入的时候，需要蓝牙音乐自动播放 BaseUtils.mlog(TAG,"进入自动播放蓝牙音乐");
                        switch(BaseApp.playSourceManager){
                            case 0:
                                mSourceManager.requestSourceToMUSIC();
                                break;
                            case 1:
                                mBtmusictPlay.sourceCtrl();
                                break;
                            case 2:
                                mSourceManager.requestSourceToAUX();  //请求aux源
                                break;
                            case 3:
                                mSourceManager.requestSourceToVIDEO();
                                break;
                        }
                        break;
                    case 999:  //全屏状态下来电话，导致不能全屏问题
                        BaseUtils.mlog(TAG,"进入自动全屏");
                        enterFullScreen();
                        break;
//                    case 456:
//                        if(dlg!=null){
//                            dlg.dismiss();
//                        }
//                        break;
                    case Contents.MSG_DISMISS_LIEBIAO_AUTO:
                        leibieliebiao.setVisibility(View.GONE);
                        BaseApp.ifliebiaoOpen = 0;
                        break;
                    case Contents.MSG_CANCEL_COVER:
                        BaseUtils.mlog(TAG, "-------------MSG_CANCEL_COVER-------------");
                        media_fragment_image.setVisibility(View.GONE);
                        break;
                    case Contents.MSG_DO_COVER:
                        BaseUtils.mlog(TAG, "-------------MSG_DO_COVER-------------");
                        media_fragment_image.setVisibility(View.VISIBLE);
                        break;
                    case Contents.DIRECT_CHANGE_PIC_TO_AUX: //图片把u盘回到aux的解决
                        BaseUtils.mlog(TAG, "-------------DIRECT_CHANGE_PIC_TO_AUX-------------");
                        changeUIToAUX();
                        break;
                    case Contents.PPT_COMEBAKC:  // 处理PPT进入的图片的切换
                        BaseUtils.mlog(TAG, "-------------PPT_COMEBAKC-------------");
                        if (BaseApp.playSourceManager == 0) {
                            if (BaseApp.picInfos != null && BaseApp.picInfos.size() > 0) {
                                pictureFragment.imageView_PPT.setImageURI(Uri.parse(BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData()));
                                if (BaseApp.current_pic_play_numUSB + 1 >= BaseApp.picInfos.size()) {
                                    BaseApp.current_pic_play_numUSB = 0;
                                } else {
                                    BaseApp.current_pic_play_numUSB++;
                                }
                            }
                            pictureFragment.picUIUpdateListener.onSavePicProgress();
                        } else if (BaseApp.playSourceManager == 3) {
                            if (BaseApp.picInfosSD != null && BaseApp.picInfosSD.size() > 0) {
                                pictureFragment.imageView_PPT.setImageURI(Uri.parse(BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData()));
                                if (BaseApp.current_pic_play_numSD + 1 >= BaseApp.picInfosSD.size()) {
                                    BaseApp.current_pic_play_numSD = 0;
                                } else {
                                    BaseApp.current_pic_play_numSD++;
                                }
                            }
                            pictureFragment.picUIUpdateListener.onSavePicProgress();
                        }
                        break;
                    case Contents.MSG_DEVICE_STATE_UI:
                        BaseUtils.mlog(TAG, "-------------MSG_DEVICE_STATE_UI-------------");
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
                        break;
                    case Contents.MUSIC_PROGRESS:
//                        BaseUtils.mlog(TAG, "-------------MUSIC_PROGRESS-------------");
                        if(musicFragment!=null) {
                            if (BaseApp.playSourceManager == 0 || BaseApp.playSourceManager == 3) {
                                musicFragment.seekBar1.setProgress(msg.arg1);
                                musicFragment.song_current_time.setText(MediaUtils.formatTime(msg.arg1));
                            }
                        }
                        break;
                    case Contents.MUSIC_REFRESH_INFO_UI:
                        BaseUtils.mlog(TAG, "-------------MUSIC_REFRESH_INFO_UI-------------");
                        if (BaseApp.playSourceManager == 0) {
                            Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                            setMusicFragmentContent(mp3Info);
                            setMusicProgress();

                            if (playMusicService.isPlaying()) {
                                button_bofang.setImageResource(R.mipmap.bofang);
                            } else {
                                button_bofang.setImageResource(R.mipmap.zanting);
                            }
                        } else if (BaseApp.playSourceManager == 3) {
//                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                            Mp3Info mp3Info = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                            setMusicFragmentContent(mp3Info);
                            setMusicProgress();
                            if (playMusicService.isPlaying()) {
                                button_bofang.setImageResource(R.mipmap.bofang);
                            } else {
                                button_bofang.setImageResource(R.mipmap.zanting);
                            }
                        }
                        break;
                    case Contents.MSG_LOCAL_FIRST_CHANGE_UI:
                        BaseUtils.mlog(TAG, "-------------MSG_LOCAL_FIRST_CHANGE_UI-------------");
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
                        break;
                    case Contents.MSG_AUX_FIRST_CHANGE_UI:
                        BaseUtils.mlog(TAG, "-------------MSG_AUX_FIRST_CHANGE_UI-------------");
                        mSourceManager.requestSourceToAUX();
                        button_aux.setBackgroundResource(R.mipmap.yinyuan_p);
                        button_layout_dilan.setBackgroundResource(R.mipmap.aux_dilan);
                        musicFragment.bt_device_name.setVisibility(View.VISIBLE);
                        musicFragment.zhuanji_layout.setVisibility(View.INVISIBLE);
                        musicFragment.singer_layout.setVisibility(View.INVISIBLE);
                        musicFragment.order_layout.setVisibility(View.INVISIBLE);
                        musicFragment.song_name.setVisibility(View.INVISIBLE);
                        musicFragment.progress_really_layout.setVisibility(View.INVISIBLE);
                        musicFragment.bt_device_name.setText("AUX");
                        musicFragment.album_icon.setImageResource(R.mipmap.aux_ico);
                        button_layout.setVisibility(View.GONE);
                        button_layout_bluetooth.setVisibility(View.GONE);
                        break;
                    case Contents.USB_MUSIC_INIT_CHANGE:
                        BaseUtils.mlog(TAG, "-------------USB_MUSIC_INIT_CHANGE-------------");
                        if (BaseApp.playSourceManager == 0 && BaseApp.current_fragment == 0 && musicFragment!=null) {
                            BaseUtils.mlog(TAG, "-------------MUSIC_INIT_CHANGE-------------USB" + BaseApp.current_music_play_numUSB);
                            Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                            setMusicFragmentContent(mp3Info);
                            setMusicProgress();
                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                            myHandler.sendEmptyMessageDelayed(Contents.USB_MUSIC_INIT_CHANGE_TO_PLAY, 500);
                        }
                        break;

                    case Contents.USB_MUSIC_INIT_CHANGE_TO_PLAY://802
                        BaseUtils.mlog(TAG, "-------------USB_MUSIC_INIT_CHANGE_TO_PLAY-------------");
                        if (playMusicService != null) {
                            BaseApp.ispauseUSB = 0;
                            playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }
                        break;
                    case Contents.SD_MUSIC_INIT_CHANGE:
                        BaseUtils.mlog(TAG, "-------------SD_MUSIC_INIT_CHANGE-------------");
                        if (BaseApp.playSourceManager == 3 && BaseApp.current_fragment == 0 && musicFragment!=null) {  //第一次进入，如果是sd卡的东西，但是在usb的时候就收到了消息，就会导致mp3InfosSD没有数据了
                            BaseUtils.mlog(TAG, "-------------MUSIC_INIT_CHANGE-------------SD");
                            Mp3Info mp3Info = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                            setMusicFragmentContent(mp3Info);
                            setMusicProgress();
                            button_play_mode.setImageResource(music_play_mode_resource[BaseApp.music_play_mode]);
                            myHandler.sendEmptyMessageDelayed(Contents.SD_MUSIC_INIT_CHANGE_TO_PLAY, 500);
                        }
                        break;
                    case Contents.SD_MUSIC_INIT_CHANGE_TO_PLAY:
                        BaseUtils.mlog(TAG, "-------------SD_MUSIC_INIT_CHANGE_TO_PLAY-------------");
                        if (playMusicService != null) {
                            BaseApp.ispauseSD = 0;
                            playMusicService.playSD(BaseApp.current_music_play_numSD);
                            button_bofang.setImageResource(R.mipmap.bofang);
                        }
                        break;
                    case Contents.USB_MUSIC_INIT_CHANGE_LIST:
                        BaseUtils.mlog(TAG, "-------------USB_MUSIC_INIT_CHANGE_LIST----------------");
                        if (BaseApp.playSourceManager == 0 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 0) {
                            if (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0) {
                                chooseListViewToShow(3);
                                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                                loading_text.setText(R.string.wuwenjian);
                            } else {
                                chooseListViewToShow(0);
                                if (mymusiclistviewAdapter != null) {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                } else {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.USB_VIDEO_INIT_CHANGE:
                        BaseUtils.mlog(TAG, "-------------USB_VIDEO_INIT_CHANGE----------------");
                        if (BaseApp.playSourceManager == 0 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 1) {
                            if (BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0) {
                                chooseListViewToShow(3);
                                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                                loading_text.setText(R.string.wuwenjian);
                            } else {
                                chooseListViewToShow(1);
                                if (myvideolistviewAdapter != null) {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                } else {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.SD_MUSIC_INIT_CHANGE_LIST:
                        BaseUtils.mlog(TAG, "-------------SD_MUSIC_INIT_CHANGE_LIST----------------");
                        if (BaseApp.playSourceManager == 3 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 0) {
                            if (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0) {
                                chooseListViewToShow(3);
                                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                                loading_text.setText(R.string.wuwenjian);
                            } else {
                                chooseListViewToShow(0);
                                if (mymusiclistviewAdapter != null) {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                } else {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.SD_VIDEO_INIT_CHANGE:
                        BaseUtils.mlog(TAG, "-------------SD_VIDEO_INIT_CHANGE----------------");
                        if (BaseApp.playSourceManager == 3 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 1) {
                            if (BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0) {
                                chooseListViewToShow(3);
                                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                                loading_text.setText(R.string.wuwenjian);
                            } else {
                                chooseListViewToShow(1);
                                if (myvideolistviewAdapter != null) {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                } else {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.USB_PIC_INIT_CHANGE:
                        BaseUtils.mlog(TAG, "-------------USB_PIC_INIT_CHANGE----------------");
                        if (BaseApp.playSourceManager == 0 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 2) {

                            if (BaseApp.picInfos == null || BaseApp.picInfos.size() == 0) {
                                chooseListViewToShow(3);
                                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                                loading_text.setText(R.string.wuwenjian);
                            } else {
                                chooseListViewToShow(2);
                                if (myGridViewAdapter2 != null) {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                } else {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                }
                            }
                        }
                        break;
                    case Contents.SD_PIC_INIT_CHANGE:
                        BaseUtils.mlog(TAG, "-------------SD_PIC_INIT_CHANGE----------------");
                        if (BaseApp.playSourceManager == 3 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 2) {
                            if (BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0) {
                                chooseListViewToShow(3);
                                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                                loading_text.setText(R.string.wuwenjian);
                            } else {
                                chooseListViewToShow(2);
                                if (myGridViewAdapter2 != null) {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                } else {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                }
                            }
                        }
                        break;
                    case Contents.IMAGE_ITEM_CLICK:
                        BaseUtils.mlog(TAG, "-------------IMAGE_ITEM_CLICK----------------");
                        if (BaseApp.playSourceManager == 0) {
                            //显示页面进行控制

//                            if (BaseApp.current_fragment == 0) {  //若果不切掉，会带来两个问题，1、从音乐到图片，再到音乐，点击之后会重新播放，需要的是继续播放
//                                BaseApp.ispauseUSB = 2;
//                                playMusicService.pause();                                  //     2、从音乐到图片到音乐，音频关不掉
//                            }
                            if (BaseApp.current_fragment != 2) {
                                changeFragment(2);
                                BaseUtils.mlog(TAG, "-MyHandler-" + "current picture num " + BaseApp.current_pic_play_numUSB);
                                pictureFragment.setImageShow(BaseApp.current_pic_play_numUSB);
                            } else {
                                BaseUtils.mlog(TAG, "current picture num " + BaseApp.current_pic_play_numUSB);
                                pictureFragment.changeImageShow(BaseApp.current_pic_play_numUSB);
                            }
                        } else if (BaseApp.playSourceManager == 3) {
//                            if (BaseApp.current_fragment == 0) {  //若果不切掉，会带来两个问题，1、从音乐到图片，再到音乐，点击之后会重新不放，需要的是继续播放
//                                BaseApp.ispauseSD = 2;
//                                playMusicService.pause();                                  //     2、从音乐到图片到音乐，音频关不掉
//                            }
                            if (BaseApp.current_fragment != 2) {
                                changeFragment(2);
                                BaseUtils.mlog(TAG, "-MyHandler-" + "current picture num " + BaseApp.current_pic_play_numSD);
                                pictureFragment.setImageShow(BaseApp.current_pic_play_numSD);
                            } else {
                                BaseUtils.mlog(TAG, "current picture num " + BaseApp.current_pic_play_numSD);
                                pictureFragment.changeImageShow(BaseApp.current_pic_play_numSD);
                            }
                        }
                        break;
                    case Contents.USB_MSG_STATE_MOUNTED:
                        BaseUtils.mlog(TAG, "-------------USB_MSG_STATE_MOUNTED----------------");
                        button_usb.setImageResource(R.mipmap.usb_n);
                        if (BaseApp.current_fragment == 0) {
                            if (BaseApp.playSourceManager != 0) {  //当前不是播放的usb的音乐，弹窗提示是否切换到usb
                                BaseApp.detect_device_num = 0;
                            }
                        }else if(BaseApp.current_fragment == 1){ //当前播放sd的视频的时候插入了u盘
                            if(BaseApp.playSourceManager != 0){
                                BaseApp.detect_device_num = 0;
                            }
                        }else if(BaseApp.current_fragment == 2){//当前在sd图片的界面的时候插入了u盘
                            if(BaseApp.playSourceManager != 0){
                                BaseApp.detect_device_num = 0;
                            }
                        }

                        break;
                    case Contents.USB_MSG_STATE_UNMOUNTED:
                        BaseUtils.mlog(TAG, "USB_MSG_STATE_UNMOUNTED----");
                        //需要将这些数组赋值为空
                        BaseApp.mp3Infos = null;
                        BaseApp.mp4Infos = null;
                        BaseApp.picInfos = null;
                        BaseApp.current_music_play_numUSB = -1;
                        BaseApp.current_video_play_numUSB = -1;
                        BaseApp.current_pic_play_numUSB = -1;

                        last_usb_total_num = 0;
                        last_usb_video_total_num = 0;
                        last_usb_pic_total_num = 0;
                        BaseApp.ispauseUSB = 2;

                        if (BaseApp.ifliebiaoOpen == 1) {
                            leibieliebiao.setVisibility(View.GONE);
                            BaseApp.ifliebiaoOpen = 0;

                        }
                        if (BaseApp.playSourceManager == 0) { //正在播放usb的多媒体
//                        if (isTopActivity()) {
                            if (BaseApp.current_fragment == 2) {
                                if (pic_timer != null) {
                                    pic_timer.cancel();
                                    pic_timer = null;
                                }
                                button_layout.setBackgroundResource(R.mipmap.dilan);
                                button_fangda.setVisibility(View.GONE);
                                button_suoxiao.setVisibility(View.GONE);
                                button_play_mode.setVisibility(View.VISIBLE);
                                button_shangqu.setImageResource(R.mipmap.shangqu);
                                button_bofang.setImageResource(R.mipmap.bofang);
                                button_xiaqu.setImageResource(R.mipmap.xiaqu);
                                button_liebiao.setImageResource(R.mipmap.liebiao);
                                if (BaseApp.ifinPPT) {
                                    BaseApp.ifinPPT = false;
                                    exitFullScreen();
                                }
                            }

                            if (BaseApp.current_fragment == 1 && BaseApp.ifFullScreenState) {
                                BaseApp.ifFullScreenState = false;
                                exitFullScreen();
                            }

                            if (BaseApp.current_fragment != 0) { //当前不在音乐播放的界面
                                changeFragment(0);
                            } else {//正在播放usb音乐
                                if (playMusicService != null) {
                                    playMusicService.resetMusic();
                                }
                            }

                            button_usb.setImageResource(R.mipmap.usb_p);
                            switch (BaseApp.last_playSourceManager) {
                                case 3://相当于点击了fragment的sd卡按钮
                                    if (BaseApp.ifhavaSDdevice) {
                                        SavePlaySource(3);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 3;
                                        setDeviceBackground(3);
                                        BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                        YinyuanChangeToSD();
                                    } else if (BaseApp.ifBluetoothConnected) {
                                        SavePlaySource(1);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 1;
                                        setDeviceBackground(1);
                                        YinyuanChangeToBT();
                                    } else {//回到aux
                                        SavePlaySource(2);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 2;
                                        setDeviceBackground(2);
                                        myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);
                                    }
                                    break;
                                case 1://蓝牙
                                    if (BaseApp.ifBluetoothConnected) {
                                        SavePlaySource(1);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 1;
                                        setDeviceBackground(1);
                                        YinyuanChangeToBT();
                                    } else if (BaseApp.ifhavaSDdevice) {
                                        SavePlaySource(3);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 3;
                                        setDeviceBackground(3);
                                        BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                        YinyuanChangeToSD();
                                    } else {//回到aux
                                        SavePlaySource(2);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 2;
                                        setDeviceBackground(2);
                                        myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);
                                    }
                                    break;
                                default:  //第一次插入，然后就拔掉
                                    SavePlaySource(2);
                                    int tempp = BaseApp.playSourceManager;
                                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                    BaseApp.playSourceManager = 2;
                                    setDeviceBackground(2);
                                    BaseUtils.mlog(TAG, "last is aux.....");
                                    myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);
                                    break;
                            }
//                        } else {//当前的界面被覆盖了，这个时候被拔掉
//
//                        }
                        } else {//当前播放的不是usb多媒体文件
                            button_usb.setImageResource(R.mipmap.usb_p);
                        }
                        break;
                    case Contents.USB_MSG_STATE_SCANNER_STARTED:
                        BaseUtils.mlog(TAG, "-------------USB_MSG_STATE_SCANNER_STARTED----------------");
                        //扫描开始，读取之前存储的音乐信息
                        last_path_usb = sharedPreferences.getString("LASTPATHUSB", "0");
                        BaseApp.music_play_mode = sharedPreferences.getInt("MUSICPLAYMODE", 0);

                        //开启定时器
                        BaseApp.usb_scan_source = true;
                        scan_timerUSB = new Timer();
                        scan_timerUSB.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED) {
                                    BaseUtils.mlog(TAG, "BaseApp.timer_numm-------------");
                                    new MyAsyncTaskUSB2().execute();
                                }
                            }
                        }, 0, 2000);  //2s中执行一次查询
                        break;
                    case Contents.USB_MSG_STATE_SCANNER_FINISHED:
                        BaseUtils.mlog(TAG, "-------------USB_MSG_STATE_SCANNER_FINISHED----------------");
                        if (scan_timerUSB != null) {
                            //scan_timer
                            scan_timerUSB.cancel();
                            scan_timerUSB = null;
                        }

                        BaseUtils.mlog(TAG, "-------------最后一次查询了-------------");
                        new MyAsyncTaskUSB2().execute();
                        break;
                    case Contents.SD_MSG_STATE_MOUNTED:
                        BaseUtils.mlog(TAG, "-------------SD_MSG_STATE_MOUNTED----------------");
                        button_SDCard.setImageResource(R.mipmap.sd_ico_p);
                        if (BaseApp.current_fragment == 0) {
                            if (BaseApp.playSourceManager != 3) {
                                //当前不是播放的usb的音乐，弹窗提示是否切换到usb
                                BaseApp.detect_device_num = 3;
                            }
                        }else if(BaseApp.current_fragment == 1){ //当前播放sd的视频的时候插入了u盘
                            if(BaseApp.playSourceManager != 3){
                                BaseApp.detect_device_num = 3;
                            }
                        }else if(BaseApp.current_fragment == 2){//当前在sd图片的界面的时候插入了u盘
                            if(BaseApp.playSourceManager != 3){
                                BaseApp.detect_device_num = 3;
                            }
                        }
                        break;
                    case Contents.SD_MSG_STATE_UNMOUNTED:
                        BaseUtils.mlog(TAG, "-------------SD_MSG_STATE_UNMOUNTED----------------");
                        //需要考虑跳转问题  暂时不做处理
                        BaseUtils.mlog(TAG, "USB_MSG_STATE_UNMOUNTED----");
                        BaseApp.mp3InfosSD = null;
                        BaseApp.mp4InfosSD = null;
                        BaseApp.picInfosSD = null;

                        BaseApp.current_music_play_numSD = -1;
                        BaseApp.current_video_play_numSD = -1;
                        BaseApp.current_pic_play_numSD = -1;
                        last_sd_total_num = 0;
                        last_sd_video_total_num = 0;
                        last_sd_pic_total_num = 0;
                        BaseApp.ispauseSD = 2;
                        if (BaseApp.ifliebiaoOpen == 1) {
                            leibieliebiao.setVisibility(View.GONE);
                            BaseApp.ifliebiaoOpen = 0;

                        }
                        button_layout.setBackgroundResource(R.mipmap.dilan);
                        button_fangda.setVisibility(View.GONE);
                        button_suoxiao.setVisibility(View.GONE);
                        button_play_mode.setVisibility(View.VISIBLE);
                        button_shangqu.setImageResource(R.mipmap.shangqu);
                        button_bofang.setImageResource(R.mipmap.bofang);
                        button_xiaqu.setImageResource(R.mipmap.xiaqu);
                        button_liebiao.setImageResource(R.mipmap.liebiao);
                        if (BaseApp.playSourceManager == 3) { //正在播放sd的多媒体
//                        if (isTopActivity()) {
                            if (BaseApp.current_fragment == 2) {
                                if (pic_timer != null) {
                                    pic_timer.cancel();
                                    pic_timer = null;
                                }
                                if (BaseApp.ifinPPT) {
                                    BaseApp.ifinPPT = false;
                                    exitFullScreen();
                                }
                            }
                            if (BaseApp.current_fragment == 1 && BaseApp.ifFullScreenState) {
                                BaseApp.ifFullScreenState = false;
                                exitFullScreen();
                            }
                            if (BaseApp.current_fragment != 0) { //当前在音乐播放的界面
                                changeFragment(0);
                            } else {//正在播放usb音乐
                                if (playMusicService != null) {//防止突然中断导致音乐服务崩溃，出现audioflinger蹦了，在播放蓝牙的时候，会突然停一下
                                    playMusicService.resetMusic();
                                }
                            }

                            button_SDCard.setImageResource(R.mipmap.sd_ico_n);
                            switch (BaseApp.last_playSourceManager) {
                                case 0://相当于点击了fragment的sd卡按钮
                                    if (BaseApp.ifhaveUSBdevice) {
                                        SavePlaySource(0);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 0;
                                        setDeviceBackground(0);
                                        BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                        YinyuanChangeToUSB();
                                    } else if (BaseApp.ifBluetoothConnected) {
                                        SavePlaySource(1);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 1;
                                        setDeviceBackground(1);
                                        YinyuanChangeToBT();
                                    } else {//回到aux
                                        SavePlaySource(2);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 2;
                                        setDeviceBackground(2);
                                        myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);
                                    }
                                    break;
                                case 1://蓝牙
                                    if (BaseApp.ifBluetoothConnected) {
                                        SavePlaySource(1);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 1;
                                        setDeviceBackground(1);
                                        YinyuanChangeToBT();
                                    } else if (BaseApp.ifhaveUSBdevice) {
                                        SavePlaySource(0);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 0;
                                        setDeviceBackground(0);
                                        BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                        YinyuanChangeToUSB();
                                    } else {//回到aux
                                        SavePlaySource(2);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 2;
                                        setDeviceBackground(2);
                                        myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);
                                    }
                                    break;
                                default:  //第一次插入，然后就拔掉
                                    SavePlaySource(2);
                                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                    BaseApp.playSourceManager = 2;
                                    setDeviceBackground(2);
                                    myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);
                                    break;
                            }
//                        } else {//当前的界面被覆盖了，这个时候被拔掉
//
//                        }
                        } else {//当前播放的不是sd多媒体文件
                            button_SDCard.setImageResource(R.mipmap.sd_ico_n);
                        }

                        break;
                    case Contents.SD_MSG_STATE_SCANNER_STARTED:
                        BaseUtils.mlog(TAG, "-------------SD_MSG_STATE_SCANNER_STARTED----------------");
                        //开启定时器
                        last_path_sd = sharedPreferences.getString("LASTPATHSD", "0");
                        BaseApp.music_play_mode = sharedPreferences.getInt("MUSICPLAYMODE", 0);

                        BaseApp.sd_scan_source = true;
                        scan_timerSD = new Timer();
                        scan_timerSD.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                                    BaseUtils.mlog(TAG, "BaseApp.timer_numm-------------");
                                    new MyAsyncTaskSD2().execute();
                                }
                            }
                        }, 0, 2000);  //2s中执行一次查询
                        break;
                    case Contents.SD_MSG_STATE_SCANNER_FINISHED:
                        BaseUtils.mlog(TAG, "-------------SD_MSG_STATE_SCANNER_FINISHED----------------");
                        if (scan_timerSD != null) {
                            //scan_timer
                            BaseUtils.mlog(TAG, "-------------清空scan_timer-------------");
                            scan_timerSD.cancel();
                            scan_timerSD = null;
                        }
                        BaseUtils.mlog(TAG, "-------------最后一次查询了-------------");
                        new MyAsyncTaskSD2().execute();
                        break;

                    case Contents.MUSIC_REFRESH_TOTAL_NUM:  //没有点击事件的时候刷新总数目
                        BaseUtils.mlog(TAG, "-------------MUSIC_REFRESH_TOTAL_NUM----------------");
                        if (BaseApp.playSourceManager == 0 && BaseApp.usb_scan_source) {
                            BaseUtils.mlog(TAG, "-------------MUSIC_REFRESH_TOTAL_NUM-------------USB");
                            if (BaseApp.mp3Infos != null && BaseApp.mp3Infos.size() >= 0) {
                                if (BaseApp.current_music_play_numUSB < 0) {
                                    musicFragment.num_order.setText("" + BaseApp.mp3Infos.size());
                                } else {
                                    musicFragment.num_order.setText((BaseApp.current_music_play_numUSB + 1) + "/" + BaseApp.mp3Infos.size());
                                }
                            }
                        } else if (BaseApp.playSourceManager == 3 && BaseApp.sd_scan_source) {
                            BaseUtils.mlog(TAG, "-------------MUSIC_REFRESH_TOTAL_NUM-------------SD");
                            if (BaseApp.mp3InfosSD != null && BaseApp.mp3InfosSD.size() >= 0) {
                                if (BaseApp.current_music_play_numSD < 0) {
                                    musicFragment.num_order.setText("" + BaseApp.mp3InfosSD.size());
                                } else {
                                    musicFragment.num_order.setText((BaseApp.current_music_play_numSD + 1) + "/" + BaseApp.mp3InfosSD.size());
                                }
                            }
                        }
                        break;
                    case Contents.USB_MUSIC_LOAD_CHANGE:  //内容和init_change消息一样
                        BaseUtils.mlog(TAG, "-------------USB_MUSIC_LOAD_CHANGE----------------");
                        if (BaseApp.playSourceManager == 0) {
                            BaseUtils.mlog(TAG, "-------------MUSIC_load_CHANGE-------------USB");
                            Mp3Info mp3Info = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                            setMusicFragmentContent(mp3Info);
                            setMusicProgress();

                            if (playMusicService != null && (BaseApp.ispauseUSB == 0 || BaseApp.ispauseUSB == 2)) {
                                BaseApp.ispauseUSB = 0;
                                playMusicService.playUSB(BaseApp.current_music_play_numUSB);
                                if(BaseApp.current_fragment == 0) //图片界面不需要修改
                                    button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                        break;
                    case Contents.SD_MUSIC_LOAD_CHANGE:
                        BaseUtils.mlog(TAG, "-------------SD_MUSIC_LOAD_CHANGE----------------");
                        if (BaseApp.playSourceManager == 3) {
                            BaseUtils.mlog(TAG, "-------------MUSIC_INIT_CHANGE-------------SD");
                            Mp3Info mp3Info = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                            setMusicFragmentContent(mp3Info);
                            setMusicProgress();

                            if (playMusicService != null && (BaseApp.ispauseSD == 0 || BaseApp.ispauseSD == 2)) {
                                BaseApp.ispauseSD = 0;
                                playMusicService.playSD(BaseApp.current_music_play_numSD);
                                if(BaseApp.current_fragment == 0) //图片界面不需要修改
                                    button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                        break;
                    case Contents.USB_MUSIC_LOAD_REFRESH_LIST:
                        BaseUtils.mlog(TAG, "-------------USB_MUSIC_LOAD_REFRESH_LIST----------------");
                        if (BaseApp.playSourceManager == 0 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 0) {
                            BaseUtils.mlog(TAG, "-MyHandler-" + "mp3Infos is OK,come to update the list...");
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_MOUNTED ||
                                    (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0))) {
                                if (BaseApp.music_media_state_scan == 0) {  //保持住当前的刷新动态图
                                    chooseListViewToShow(3);
                                    BaseApp.music_media_state_scan = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            } else if (mDeviceStateUSB == Contents.USB_STATE_SCANNER_FINISHED && (BaseApp.mp3Infos == null || BaseApp.mp3Infos.size() == 0)) {
                                BaseApp.music_media_state_scan = 0;
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                chooseListViewToShow(0);
                                BaseApp.music_media_state_scan = 0;
                                if (mymusiclistviewAdapter != null) {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                } else {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3Infos);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.SD_MUSIC_LOAD_REFRESH_LIST:
                        BaseUtils.mlog(TAG, "-------------SD_MUSIC_LOAD_REFRESH_LIST----------------");
                        if (BaseApp.playSourceManager == 3 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 0) {
                            BaseUtils.mlog(TAG, "-MyHandler-" + "mp3Infos is OK,come to update the list...");
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_MOUNTED ||
                                    (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0))) {
                                if (BaseApp.music_media_state_scanSD == 0) {  //保持住当前的刷新动态图
                                    chooseListViewToShow(3);
                                    BaseApp.music_media_state_scanSD = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            } else if (mDeviceStateSD == Contents.SD_STATE_SCANNER_FINISHED && (BaseApp.mp3InfosSD == null || BaseApp.mp3InfosSD.size() == 0)) {
                                BaseApp.music_media_state_scanSD = 0;
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                chooseListViewToShow(0);
                                BaseApp.music_media_state_scanSD = 0;
                                if (mymusiclistviewAdapter != null) {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                } else {
                                    mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this, BaseApp.mp3InfosSD);
                                    musiclistview_id.setAdapter(mymusiclistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.USB_VIDEO_LOAD_REFRESH_LIST:
                        BaseUtils.mlog(TAG, "-------------VIDEO_LOAD_REFRESH_LIST-------------USB");
                        if (BaseApp.playSourceManager == 0 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 1) {
                            BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...");
                            //数据获取结束准备刷新
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_MOUNTED ||
                                    (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && ((BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0))
                                    )) {
                                BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...1");
                                chooseListViewToShow(3);
                                if (BaseApp.video_media_state_scan == 0) {
                                    BaseApp.video_media_state_scan = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            } else if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED && (BaseApp.mp4Infos == null || BaseApp.mp4Infos.size() == 0)) {
                                BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...2");
                                BaseApp.video_media_state_scan = 0;
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...3");
                                chooseListViewToShow(1);
                                BaseApp.video_media_state_scan = 0;

                                if (myvideolistviewAdapter != null) {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                } else {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4Infos);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.SD_VIDEO_LOAD_REFRESH_LIST:
                        BaseUtils.mlog(TAG, "-------------SD_VIDEO_LOAD_REFRESH_LIST----------------");
                        if (BaseApp.playSourceManager == 3 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 1) {
                            BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...");
                            //数据获取结束准备刷新
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_MOUNTED ||
                                    (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && ((BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0)) || BaseApp.video_media_state_scan == 0)) {
                                BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...2");
                                chooseListViewToShow(3);
                                if (BaseApp.video_media_state_scanSD == 0) {
                                    BaseApp.video_media_state_scanSD = 1;
                                    loading_image.setBackgroundResource(0);
                                    liebiaoShowNOorScan(1);
                                }
                            } else if (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED && (BaseApp.mp4InfosSD == null || BaseApp.mp4InfosSD.size() == 0)) {
                                BaseUtils.mlog(TAG, "-MyHandler-" + "mp4Infos is OK,come to update the list...3");
                                BaseApp.video_media_state_scanSD = 0;
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                chooseListViewToShow(1);
                                BaseApp.video_media_state_scanSD = 0;

                                if (myvideolistviewAdapter != null) {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                } else {
                                    myvideolistviewAdapter = new MyvideolistviewAdapter(MainActivity.this, BaseApp.mp4InfosSD);
                                    videolistview_id.setAdapter(myvideolistviewAdapter);
                                }
                            }
                        }
                        break;
                    case Contents.USB_IMAGE_LOAD_REFRESH_LIST:
                        BaseUtils.mlog(TAG, "-------------IMAGE_LOAD_REFRESH_LIST-------------USB");
                        if (BaseApp.playSourceManager == 0 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 2) {
                            if (mDeviceStateUSB == Contents.USB_DEVICE_STATE_MOUNTED ||
                                    (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED && ((BaseApp.picInfos == null || BaseApp.picInfos.size() == 0))
                                    )) {
                                chooseListViewToShow(3);
                                if (BaseApp.pic_media_state_scan == 0) {
                                    BaseApp.pic_media_state_scan = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            } else if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfos == null || BaseApp.picInfos.size() == 0)) {
                                BaseApp.pic_media_state_scan = 0;
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                chooseListViewToShow(2);
                                BaseApp.pic_media_state_scan = 0;
                                if (myGridViewAdapter2 != null) {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                } else {
                                    BaseUtils.mlog(TAG, "-MyHandler-" + "picInfos is OK,come to update the gridview...");
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfos);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                }
                            }
                        }
                        break;
                    case Contents.SD_IMAGE_LOAD_REFRESH_LIST:
                        BaseUtils.mlog(TAG, "-------------IMAGE_LOAD_FINISH-------------USB");
                        if (BaseApp.playSourceManager == 3 && BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 2) {
                            if (mDeviceStateSD == Contents.SD_DEVICE_STATE_MOUNTED ||
                                    (mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED && ((BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0))
                                    )) {
                                chooseListViewToShow(3);
                                if (BaseApp.pic_media_state_scanSD == 0) {
                                    BaseApp.pic_media_state_scanSD = 1;
                                    liebiaoShowNOorScan(1);
                                }
                            } else if (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_STARTED && (BaseApp.picInfosSD == null || BaseApp.picInfosSD.size() == 0)) {
                                BaseApp.pic_media_state_scanSD = 0;
                                chooseListViewToShow(3);
                                liebiaoShowNOorScan(0);
                            } else {
                                chooseListViewToShow(2);
                                BaseApp.pic_media_state_scanSD = 0;

                                if (myGridViewAdapter2 != null) {
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                } else {
                                    BaseUtils.mlog(TAG, "-MyHandler-" + "picInfos is OK,come to update the gridview...");
                                    myGridViewAdapter2 = new MyGridViewAdapter2(MainActivity.this, BaseApp.picInfosSD);
                                    gridview_id.setAdapter(myGridViewAdapter2);
                                }
                            }
                        }
                        break;
                    case BtmusicPlay.MSG_ACTIVITY_CHANGE:
                        BaseUtils.mlog(TAG, "-------------MSG_ACTIVITY_CHANGE------蓝牙fragment刷新-------");
                        if (BaseApp.playSourceManager == 1) {
                            musicFragment.zhuanji_layout.setVisibility(View.GONE);
                            musicFragment.bt_device_name.setVisibility(View.VISIBLE);
                            musicFragment.song_name.setVisibility(View.VISIBLE);
                            musicFragment.singer_layout.setVisibility(View.VISIBLE);
                            musicFragment.order_layout.setVisibility(View.INVISIBLE);
                            musicFragment.progress_really_layout.setVisibility(View.INVISIBLE);
//                            musicFragment.song_name.setVisibility(View.VISIBLE);
//                            musicFragment.singer_layout.setVisibility(View.VISIBLE);
                            musicFragment.song_name.setText(getString(R.string.gequmingcheng));
                            musicFragment.chuangzhe_name.setText(getString(R.string.geshou));
                            musicFragment.bt_device_name.setText(getString(R.string.weizhi));
                            musicFragment.song_name.setText(mBtmusictPlay.txtName == null ? getString(R.string.gequmingcheng) : mBtmusictPlay.txtName);
                            musicFragment.chuangzhe_name.setText(mBtmusictPlay.txtArtists ==null ? getString(R.string.geshou) : mBtmusictPlay.txtArtists);
                            musicFragment.bt_device_name.setText(BaseApp.btphone_name == null ? getString(R.string.weizhi) : BaseApp.btphone_name);
                        }
                        break;
                    case BtmusicPlay.MSG_UPDATE_BT_NAME:
                        musicFragment.bt_device_name.setText(BaseApp.btphone_name);  //放在btmusicinfo中显示太慢了
                        break;
                    case Contents.BLUETOOTH_PLAY:  //连接上蓝牙，就改变状态   666
                        BaseUtils.mlog(TAG, "-------------BLUETOOTH_PLAY-------------");
                        YinyuanChangeToBT();
                        break;
                    case Contents.BLUETOOTH_CONNECTED:
                        BaseUtils.mlog(TAG, "-------------BLUETOOTH_CONNECTED-------------");
                        //启动的时候，如果蓝牙连接上了，我再去绑定服务，会获得连接的状态。
                        //点亮图标，如果当前的playsourcemanager是1的话，
//                        BaseApp.ifBluetoothConnected = true;
                        button_bluetooth.setImageResource(R.mipmap.bt_n);
                        if (BaseApp.current_fragment == 0) {
                            if (BaseApp.playSourceManager != 1) {
                                BaseApp.detect_device_num = 1;
                            }
                        }else if(BaseApp.current_fragment == 1){ //当前播放sd的视频的时候插入了u盘
                            if(BaseApp.playSourceManager != 1){
                                BaseApp.detect_device_num = 1;
                            }
                        }else if(BaseApp.current_fragment == 2){//当前在sd图片的界面的时候插入了u盘
                            if(BaseApp.playSourceManager != 1){
                                BaseApp.detect_device_num = 1;
                            }
                        }
                        break;
                    case Contents.BLUETOOTH_DISCONNECTED:
                        BaseUtils.mlog(TAG, "-------------BLUETOOTH_DISCONNECTED-------------");
                        //图标变暗并且如果是在蓝牙界面还需要切换
//                        BaseApp.ifBluetoothConnected = false;
                        button_bluetooth.setImageResource(R.mipmap.bt_p);
                        if (BaseApp.playSourceManager == 1) { //正在播放蓝牙音乐
                            switch (BaseApp.last_playSourceManager) {
                                case 0://相当于点击了fragment的sd卡按钮
                                    if (BaseApp.ifhaveUSBdevice) {
                                        SavePlaySource(0);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 0;
                                        setDeviceBackground(0);
                                        BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                        YinyuanChangeToUSB();
                                    } else if (BaseApp.ifhavaSDdevice) {
                                        SavePlaySource(3);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 3;
                                        setDeviceBackground(3);
                                        BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                        YinyuanChangeToSD();
                                    } else {//回到aux
                                        SavePlaySource(2);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 2;
                                        setDeviceBackground(2);
                                        AUXEent();
                                    }
                                    break;
                                case 3://蓝牙
                                    if (BaseApp.ifhavaSDdevice) {
                                        SavePlaySource(3);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 3;
                                        setDeviceBackground(3);
                                        BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                        YinyuanChangeToSD();
                                    } else if (BaseApp.ifhaveUSBdevice) {
                                        SavePlaySource(0);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 0;
                                        setDeviceBackground(0);
                                        BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                        YinyuanChangeToUSB();
                                    } else {//回到aux
                                        SavePlaySource(2);
                                        BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                        BaseApp.playSourceManager = 2;
                                        setDeviceBackground(2);
                                        AUXEent();
                                    }
                                    break;
                                default:  //第一次插入，然后就拔掉
                                    SavePlaySource(2);
                                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                    BaseApp.playSourceManager = 2;
                                    setDeviceBackground(2);
                                    AUXEent();
                                    break;
                            }
                        } else {//当前播放的不是蓝牙音乐
                            if (BaseApp.current_fragment == 0) {
                                button_bluetooth.setImageResource(R.mipmap.bt_p);
                            }
                        }
                        break;
                    case Contents.IMAGE_PPT_COMEBACK://处理PPTactivity返回来的消息显示   31
                        BaseUtils.mlog(TAG, "-------------IMAGE_PPT_COMEBACK-------------");
                        String mybigPicPath = null;
                        if (BaseApp.playSourceManager == 0) {
                            mybigPicPath = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
                        } else if (BaseApp.playSourceManager == 3) {
                            mybigPicPath = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
                        }
                        Bitmap bm = pictureFragment.convertToBitmap(mybigPicPath, 800, 350);
                        pictureFragment.big_pic_show.setImageBitmap(bm);
                        button_bofang.setImageResource(R.mipmap.zanting_pic);
                        break;
                }
            }
        }
    }

    //程序开启的时候，数据就已经在了。
    private class MyAsyncTaskUSB1 extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {
            int last_usb_num = -1;
            int last_usb_video_num = -1;
            int last_usb_pic_num = -1;
            BaseUtils.mlog(TAG, "MyAsyncTaskUSB1-------------doInBackground-------------");
            BaseApp.mp3Infos = MediaUtils.getMp3Infos(MainActivity.this);
            if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size() > 0){
                last_path_usb = sharedPreferences.getString("LASTPATHUSB", "0");
                BaseApp.music_play_mode = sharedPreferences.getInt("MUSICPLAYMODE", 0);
                if(last_path_usb == null || last_path_usb.equals("0")){
                    BaseApp.current_music_play_numUSB = 0;
                    Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_INIT_CHANGE);//114  刷新ui界面
                    myHandler.sendMessage(msg);
                }else{
                    Mp3Info mp3Info_first = MediaUtils.getMp3Infobypath(MainActivity.this, last_path_usb);
                    if (mp3Info_first != null && mp3Info_first.getDuration()>0) {
                        for(int i=0;i<BaseApp.mp3Infos.size();i++){
                            if(mp3Info_first.getUrl().equals(BaseApp.mp3Infos.get(i).getUrl())){
                                last_usb_num = i;
                                int progress_temp = sharedPreferences.getInt("LASTMUSICPLAYTIMEUSB", 0);
                                BaseApp.current_music_play_progressUSB = progress_temp;
                                BaseUtils.mlog(TAG,"BaseApp.current_music_play_progressUSB____"+BaseApp.current_music_play_progressUSB);
                                break;
                            }
                        }
                        if(last_usb_num == -1){
                            BaseApp.current_music_play_numUSB = 0 ;
                            Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_INIT_CHANGE);//114  刷新ui界面
                            myHandler.sendMessage(msg);
                        }else{
                            BaseApp.current_music_play_numUSB = last_usb_num ;
                            Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_INIT_CHANGE);//114  刷新ui界面
                            myHandler.sendMessage(msg);
                        }
                    }else{//把第一首歌的信息填写上去
                        BaseApp.current_music_play_numUSB = 0 ;
                        Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_INIT_CHANGE);//114
                        myHandler.sendMessage(msg);
                    }
                }
            }
            BaseApp.ifmusicReadFinishUSB= true;

            //产生随机播放列表
            if(BaseApp.music_shuffle_list!=null){
                BaseApp.music_shuffle_list.clear();
                BaseApp.music_shuffle_list = null;
                BaseApp.music_shuffle_list = new ArrayList<>();
            }
            if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size()>0){
                for(int i=0;i<BaseApp.mp3Infos.size();i++){
                    BaseApp.music_shuffle_list.add(i);
                }
                Collections.shuffle(BaseApp.music_shuffle_list);
            }
            myHandler.sendEmptyMessage(Contents.USB_MUSIC_INIT_CHANGE_LIST);

            //视频  USB
            BaseApp.mp4Infos = Mp4MediaUtils.getMp4Infos(MainActivity.this);
            if(BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0) {
                //获取上次播放的视频
                last_video_path_usb = sharedPreferences.getString("LASTVIDEOPATHUSB", "0");
                BaseApp.video_play_mode = sharedPreferences.getInt("VIDEOPLAYMODE", 0);
                BaseUtils.mlog(TAG,"last_video_path--"+last_video_path_usb);
                if(last_video_path_usb ==null || last_video_path_usb.equals("0")){
                    BaseApp.current_video_play_numUSB = 0;
                }else{
                    Mp4Info mp4Infos_first = Mp4MediaUtils.getMp4Infobypath(MainActivity.this, last_video_path_usb);
                    if (mp4Infos_first != null && mp4Infos_first.getDuration()>0) {
                        for(int i=0;i<BaseApp.mp4Infos.size();i++){
                            if(mp4Infos_first.getData().equals(BaseApp.mp4Infos.get(i).getData())){
                                last_usb_video_num = i;
                                BaseApp.current_video_play_progressUSB = sharedPreferences.getInt("LASTVIDEOPLAYTIMEUSB",0);
                                break;
                            }
                        }
                        if(last_usb_video_num == -1){
                            BaseApp.current_video_play_numUSB = -1;
                        }else{
                            BaseApp.current_video_play_numUSB = last_usb_video_num;
                        }
                    }else{//把第一个视频的信息填写上去
                        BaseApp.current_video_play_numUSB = -1;
                    }
                }
                //产生随机播放列表
                if(BaseApp.video_shuffle_list!=null){
                    BaseApp.video_shuffle_list.clear();
                    BaseApp.video_shuffle_list = null;
                    BaseApp.video_shuffle_list = new ArrayList<>();
                }
                if(BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0){
                    for(int i=0;i<BaseApp.mp4Infos.size();i++){
                        BaseApp.video_shuffle_list.add(i);
                    }
                    Collections.shuffle(BaseApp.video_shuffle_list);
                }
            }
            myHandler.sendEmptyMessage(Contents.USB_VIDEO_INIT_CHANGE);
            BaseApp.ifvideoReadFinishUSB = true;

            //图片  USB
            BaseApp.picInfos = PicMediaUtils.getPicInfos(MainActivity.this);
            if(BaseApp.picInfos!=null && BaseApp.picInfos.size()>0){
                //获取上次播放的视频
                last_pic_path_usb = sharedPreferences.getString("LASTPICPATHUSB", "0");
                if(last_pic_path_usb ==null || last_pic_path_usb.equals("0")){
                    BaseApp.current_pic_play_numUSB = 0;
                }else{
                    PicInfo pic_first = PicMediaUtils.getPicInfobypath(MainActivity.this, last_pic_path_usb);
                    if (pic_first != null && pic_first.getData().length()>0) {
                        for(int i=0;i<BaseApp.picInfos.size();i++){
                            if(pic_first.getData().equals(BaseApp.picInfos.get(i).getData())){
                                last_usb_pic_num = i;
                                break;
                            }
                        }
                        if(last_usb_pic_num == -1){
                            BaseApp.current_pic_play_numUSB = -1;
                        }else{
                            BaseApp.current_pic_play_numUSB = last_usb_pic_num;
                        }
                    }else{//把第一首歌的信息填写上去
                        BaseApp.current_pic_play_numUSB = -1 ;
                    }
                }
            }
            myHandler.sendEmptyMessage(Contents.USB_PIC_INIT_CHANGE);
            BaseApp.ifpicReadFinishUSB = true;
            return null;
        }
    }

    //程序开启的时候，数据就已经在了。
    private class MyAsyncTaskSD1 extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {
            BaseUtils.mlog(TAG, "MyAsyncTaskSD1-------------doInBackground-------------");
            BaseUtils.mlog(TAG, "sd进入数据已经准备好了");
            int last_sd_num = -1;
            int last_sd_video_num = -1;
            int last_sd_pic_num = -1;
            BaseApp.mp3InfosSD = MediaUtils.getMp3InfosSD(MainActivity.this);
            if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size() > 0){
                last_path_sd = sharedPreferences.getString("LASTPATHSD", "0");
                BaseApp.music_play_mode = sharedPreferences.getInt("MUSICPLAYMODE", 0);

                if(last_path_sd ==null || last_path_sd.equals("0")){
                    BaseApp.current_music_play_numSD = 0;
                    Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_INIT_CHANGE);//114  刷新ui界面
                    myHandler.sendMessage(msg);
                    BaseUtils.mlog(TAG,"SD卡的路径为0----");
                }else{
                    Mp3Info mp3Info_first = MediaUtils.getMp3Infobypath(MainActivity.this,last_path_sd);
                    if (mp3Info_first != null && mp3Info_first.getDuration()>0) {
                        for(int i=0;i<BaseApp.mp3InfosSD.size();i++){
                            if(mp3Info_first.getUrl().equals(BaseApp.mp3InfosSD.get(i).getUrl())){
                                last_sd_num = i;
                                int progress_temp = sharedPreferences.getInt("LASTMUSICPLAYTIMESD",0);
                                BaseApp.current_music_play_progressSD = progress_temp;
                                break;
                            }
                        }
                        if(last_sd_num == -1){  //曲目可能被删除了
                            BaseUtils.mlog(TAG,"SD卡没有找到对应的曲目0----");
                            BaseApp.current_music_play_numSD = 0 ;
                            Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_INIT_CHANGE);//114  刷新ui界面
                            myHandler.sendMessage(msg);
                        }else{
                            BaseUtils.mlog(TAG,"SD卡找到对应的曲目----"+last_sd_num);
                            BaseApp.current_music_play_numSD = last_sd_num ;
                            Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_INIT_CHANGE);//114  刷新ui界面
                            myHandler.sendMessage(msg);
                        }
                    }else{//把第一首歌的信息填写上去
                        BaseUtils.mlog(TAG,"SD卡的数据为0----");
                        BaseApp.current_music_play_numSD = 0 ;
                        Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_INIT_CHANGE);//114
                        myHandler.sendMessage(msg);
                    }
                }
            }
            BaseUtils.mlog(TAG,"BaseApp.current_music_play_numSD---"+BaseApp.current_music_play_numSD);
            BaseApp.ifmusicReadFinishSD = true;
            //产生随机播放列表
            if(BaseApp.music_shuffle_listSD!=null){
                BaseApp.music_shuffle_listSD.clear();
                BaseApp.music_shuffle_listSD = null;
                BaseApp.music_shuffle_listSD = new ArrayList<>();
            }
            if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size()>0){
                for(int i=0;i<BaseApp.mp3InfosSD.size();i++){
                    BaseApp.music_shuffle_listSD.add(i);   //切换语言后，怎么自动增加了？
                }
                Collections.shuffle(BaseApp.music_shuffle_listSD);
            }
            myHandler.sendEmptyMessage(Contents.SD_MUSIC_INIT_CHANGE_LIST);

            //视频  SD
            BaseApp.mp4InfosSD = Mp4MediaUtils.getMp4InfosSD(MainActivity.this);
            if(BaseApp.mp4InfosSD!=null && BaseApp.mp4InfosSD.size()>0) {
                //获取上次播放的视频
                last_video_path_sd = sharedPreferences.getString("LASTVIDEOPATHSD", "0");
                BaseApp.video_play_mode = sharedPreferences.getInt("VIDEOPLAYMODE",0);
                if(last_video_path_sd ==null || last_video_path_sd.equals("0")){
                    BaseApp.current_video_play_numSD = 0;
                }else{
                    Mp4Info mp4Infos_first = Mp4MediaUtils.getMp4Infobypath(MainActivity.this, last_video_path_sd);
                    if (mp4Infos_first != null && mp4Infos_first.getDuration()>0) {
                        for(int i=0;i<BaseApp.mp4InfosSD.size();i++){
                            if(mp4Infos_first.getData().equals(BaseApp.mp4InfosSD.get(i).getData())){
                                last_sd_video_num = i;  //找到对应的曲目之后，给这个曲目赋对应的播放时长
                                BaseApp.current_video_play_progressSD =  sharedPreferences.getInt("LASTVIDEOPLAYTIMESD", 0);
//                                BaseApp.mp4InfosSD.get(last_sd_video_num).setVideo_item_progressed(progress_temp);
                                break;
                            }
                        }
                        if(last_sd_video_num == -1){  //曲目可能被删除了
                            BaseApp.current_video_play_numSD = -1 ;
                        }else{
                            BaseApp.current_video_play_numSD = last_sd_video_num;
                        }
                    }else{
                        BaseApp.current_music_play_numSD = -1 ;
                    }
                }
                //产生随机播放列表
                if(BaseApp.video_shuffle_listSD!=null){
                    BaseApp.video_shuffle_listSD.clear();
                    BaseApp.video_shuffle_listSD = null;
                    BaseApp.video_shuffle_listSD = new ArrayList<>();
                }
                if(BaseApp.mp4InfosSD!=null && BaseApp.mp4InfosSD.size()>0){
                    for(int i=0;i<BaseApp.mp4InfosSD.size();i++){
                        BaseApp.video_shuffle_listSD.add(i);
                    }
                    Collections.shuffle(BaseApp.video_shuffle_listSD);
                }
            }
            myHandler.sendEmptyMessage(Contents.SD_VIDEO_INIT_CHANGE);
            BaseApp.ifvideoReadFinishSD = true;

            //图片  SD
            BaseApp.picInfosSD = PicMediaUtils.getPicInfosSD(MainActivity.this);
            if(BaseApp.picInfosSD!=null && BaseApp.picInfosSD.size()>0){

                last_pic_path_sd = sharedPreferences.getString("LASTPICPATHSD", "0");
                if(last_pic_path_sd ==null || last_pic_path_sd.equals("0")){
                    BaseApp.current_pic_play_numSD = 0;
                }else{
                    PicInfo pic_first = PicMediaUtils.getPicInfobypath(MainActivity.this, last_pic_path_sd);
                    if (pic_first != null && pic_first.getData().length()>0) {
                        for(int i=0;i<BaseApp.picInfosSD.size();i++){
                            if(pic_first.getData().equals(BaseApp.picInfosSD.get(i).getData())){
                                    last_sd_pic_num = i;
                                    break;
                            }
                        }
                        if(last_sd_pic_num == -1){
                            BaseApp.current_pic_play_numSD = -1;
                        }else{
                            BaseApp.current_pic_play_numSD = last_sd_pic_num;
                        }
                    }else{//把第一首歌的信息填写上去
                        BaseApp.current_pic_play_numSD = -1;
                    }
                }
            }
            myHandler.sendEmptyMessage(Contents.SD_PIC_INIT_CHANGE);
            BaseApp.ifpicReadFinishSD = true;
            return null;
        }
    }

    //开启的时候正在扫描，或者程序起来之后，再进行扫描
    private class MyAsyncTaskUSB2 extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {

            BaseUtils.mlog(TAG, "MyAsyncTaskUSB2-------------doInBackground-------------");
            BaseApp.mp3Infos = MediaUtils.getMp3Infos(MainActivity.this);
            //每次查询结束就要马上计算当前的音乐在第几个  别忘了最后一次
            //处理查询过程中的点击播放事件
            if(!BaseApp.ifmusicReadFinishUSB && (BaseApp.when_scan_click ) ) {
                if (mp3Info_temp != null && mp3Info_temp.getId() > 0) {
                    for (int i = 0; i < BaseApp.mp3Infos.size(); i++) {
                        if (mp3Info_temp.getUrl().equals(BaseApp.mp3Infos.get(i).getUrl())) {
                            BaseApp.current_music_play_numUSB = i;
                            last_usb_num = i;
                            break;
                        }
                    }
                }
            }

            if(!BaseApp.ifmusicReadFinishUSB && (last_usb_num != -1 && !BaseApp.when_scan_click) ){
                if (mp3Info_default_temp != null && mp3Info_default_temp.getId() > 0) {
                    for (int i = 0; i < BaseApp.mp3Infos.size(); i++) {
                        if (mp3Info_default_temp.getUrl().equals(BaseApp.mp3Infos.get(i).getUrl())) {
                            BaseApp.current_music_play_numUSB = i;
                            last_usb_num = i;
                            break;
                        }
                    }
                }
            }

            //刷新完成了，还是没有发生点击事件
            if(last_usb_num == -1 && BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size()>0 && !BaseApp.when_scan_click
                    && (mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED || mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED)
                    ){
                //改为开始扫描的时候去读取一次就行了
                if (last_path_usb ==null || last_path_usb.equals("0")) {//当前没有记录的时候，直接指定播放第一首
                    BaseApp.current_music_play_numUSB = 0;
                    last_usb_num = 0;//sp无法找到对应曲目的时候，下次也不进入了扫描了
                    mp3Info_default_temp = BaseApp.mp3Infos.get(0);
                    Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_LOAD_CHANGE);//114  刷新ui界面
                    myHandler.sendMessage(msg);
                } else {
                    Mp3Info mp3Info_first = MediaUtils.getMp3Infobypath(MainActivity.this,last_path_usb);
                    if (mp3Info_first != null && mp3Info_first.getDuration()>0) {
                        for (int i = 0; i < BaseApp.mp3Infos.size(); i++) {
                            if (mp3Info_first.getUrl().equals(BaseApp.mp3Infos.get(i).getUrl())) {
                                    last_usb_num = i;
                                    int progress_temp = sharedPreferences.getInt("LASTMUSICPLAYTIMEUSB", 0);
                                    BaseApp.current_music_play_progressUSB = progress_temp;
                                break;
                            }
                        }
                        if(last_usb_num == -1){
                            if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED) {
                                //当没有扫描完，而且又没有获取到对应的曲目的时候，只是界默认的初始化界面
                            }else if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED){
                                BaseApp.current_music_play_numUSB = 0;
                                Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_LOAD_CHANGE);//114
                                myHandler.sendMessage(msg);
                            }
                        }else{
                            BaseApp.current_music_play_numUSB = last_usb_num ;
                            mp3Info_default_temp = BaseApp.mp3Infos.get(BaseApp.current_music_play_numUSB);
                            Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_LOAD_CHANGE);//114  刷新ui界面
                            myHandler.sendMessage(msg);
                        }
                    }else{  //在数据库中没有找到对应的曲目，直接默认
                        if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED) {
                                //当没有扫描完，而且又没有获取到对应的曲目的时候，只是界默认的初始化界面
                        }else if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED){
                            BaseApp.current_music_play_numUSB = 0;
                            last_usb_num = 0;//sp无法找到对应曲目的时候，下次也不进入了扫描了
                            mp3Info_default_temp = BaseApp.mp3Infos.get(0);
                            Message msg = myHandler.obtainMessage(Contents.USB_MUSIC_LOAD_CHANGE);//114
                            myHandler.sendMessage(msg);
                        }
                    }
                }
            }

            //无论是否点击了，刷新总数目
            if((mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED || mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_STARTED)) {
                myHandler.sendEmptyMessage(Contents.MUSIC_REFRESH_TOTAL_NUM);
                if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED){  //防止拔出u盘再插入的情况
                    BaseApp.when_scan_click = false;
                    BaseApp.usb_scan_source = false;  //主要是检测，是否在扫描，扫描的消息开始的时候设置为true，扫描完成的时候设置为false
                    BaseApp.ifmusicReadFinishUSB = true;//主要是表示数据是否读入了arraylist中，读入了为true


                    if(BaseApp.music_shuffle_list!=null){
                        BaseApp.music_shuffle_list.clear();
                        BaseApp.music_shuffle_list = null;
                        BaseApp.music_shuffle_list = new ArrayList<>();
                    }
                    //产生随机播放列表
                    if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size()>0){
                        for(int i=0;i<BaseApp.mp3Infos.size();i++){
                         BaseApp.music_shuffle_list.add(i);
                        }
                        Collections.shuffle(BaseApp.music_shuffle_list);
                    }
                }
            }
            if(BaseApp.mp3Infos!=null && last_usb_total_num < BaseApp.mp3Infos.size()) {  //当刷新的数目没有变化是，不刷新list
                last_usb_total_num = BaseApp.mp3Infos.size();
                myHandler.sendEmptyMessage(Contents.USB_MUSIC_LOAD_REFRESH_LIST);//12  刷新music的list
            }


            //视频  只有当finish的时候，显示条目
            BaseApp.mp4Infos = Mp4MediaUtils.getMp4Infos(MainActivity.this);

            if(BaseApp.when_scan_click_video ) {
                if (mp4Info_temp != null && mp4Info_temp.getId() > 0) {
                    for (int i = 0; i < BaseApp.mp4Infos.size(); i++) {
                        if (mp4Info_temp.getData().equals(BaseApp.mp4Infos.get(i).getData())) {
                            BaseApp.current_video_play_numUSB = i;
                            break;
                        }
                    }
                }
            }

            if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED && BaseApp.current_video_play_numUSB == -1) {
                if(BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0) {
                    //获取上次播放的视频
                    last_video_path_usb= sharedPreferences.getString("LASTVIDEOPATHUSB", "0");
                    BaseApp.video_play_mode = sharedPreferences.getInt("VIDEOPLAYMODE", 0);
                    if(last_video_path_usb ==null || last_video_path_usb.equals("0")){
                        BaseApp.current_video_play_numUSB = 0;
                    }else{
                        Mp4Info mp4Infos_first = Mp4MediaUtils.getMp4Infobypath(MainActivity.this, last_video_path_usb);
                        if (mp4Infos_first != null && mp4Infos_first.getDuration()>0) {
                            for(int i=0;i<BaseApp.mp4Infos.size();i++){
                                if(mp4Infos_first.getData().equals(BaseApp.mp4Infos.get(i).getData())){
                                    last_usb_video_num = i;
                                    BaseApp.current_video_play_progressUSB = sharedPreferences.getInt("LASTVIDEOPLAYTIMEUSB",0);
//                                    BaseApp.mp4Infos.get(last_usb_video_num).setVideo_item_progressed(progress_temp);
                                    break;
                                }
                            }
                            if(last_usb_video_num == -1){
                                BaseApp.current_video_play_numUSB = -1;
                            }else{
                                BaseApp.current_video_play_numUSB = last_usb_video_num;
                            }
                        }else{//把第一首歌的信息填写上去
                            BaseApp.current_video_play_numUSB = -1 ;
                        }
                    }
                    //产生随机播放列表
                    if(BaseApp.video_shuffle_list!=null){
                        BaseApp.video_shuffle_list.clear();
                        BaseApp.video_shuffle_list = null;
                        BaseApp.video_shuffle_list = new ArrayList<>();
                    }
                    if(BaseApp.mp4Infos!=null && BaseApp.mp4Infos.size()>0){
                        for(int i=0;i<BaseApp.mp4Infos.size();i++){
                            BaseApp.video_shuffle_list.add(i);
                        }
                        Collections.shuffle(BaseApp.video_shuffle_list);
                    }
                }
                BaseApp.ifvideoReadFinishUSB = true;
            }
            //无论是否刷新完，消息还是要不断的发送的
            if(BaseApp.mp4Infos!=null && last_usb_video_total_num < BaseApp.mp4Infos.size()) {
                last_usb_video_total_num = BaseApp.mp4Infos.size();
                myHandler.sendEmptyMessage(Contents.USB_VIDEO_LOAD_REFRESH_LIST);//22
            }
            //图片
            BaseApp.picInfos = PicMediaUtils.getPicInfos(MainActivity.this);
            if(BaseApp.when_scan_click_pic ) {
                if (PicInfo_temp != null && PicInfo_temp.getId() > 0) {
                    for (int i = 0; i < BaseApp.picInfos.size(); i++) {
                        if (PicInfo_temp.getData().equals(BaseApp.picInfos.get(i).getData())) {
                            BaseApp.current_pic_play_numUSB = i;
                            break;
                        }
                    }
                }
            }
            if(mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED && BaseApp.current_pic_play_numUSB==-1) {
                if(BaseApp.picInfos!=null && BaseApp.picInfos.size()>0){
                    last_pic_path_usb = sharedPreferences.getString("LASTPICPATHUSB", "0");
                    if(last_pic_path_usb ==null || last_pic_path_usb.equals("0")){
                        BaseApp.current_pic_play_numUSB = 0;
                    }else{
                        PicInfo pic_first = PicMediaUtils.getPicInfobypath(MainActivity.this, last_pic_path_usb);
                        if (pic_first != null && pic_first.getData().length()>0) {
                            for(int i=0;i<BaseApp.picInfos.size();i++){
                                if(pic_first.getData().equals(BaseApp.picInfos.get(i).getData())){
                                    last_usb_pic_num = i;
                                    break;
                                }
                            }
                            if(last_usb_pic_num ==-1){
                                BaseApp.current_pic_play_numUSB = -1;
                            }else{
                                BaseApp.current_pic_play_numUSB = last_usb_pic_num;
                            }
                        }else{//把第一首歌的信息填写上去
                            BaseApp.current_pic_play_numUSB = -1;
                        }
                    }
                }
                BaseApp.ifpicReadFinishUSB = true;
            }
            if(BaseApp.picInfos!=null && last_usb_pic_total_num < BaseApp.picInfos.size()) {
                last_usb_pic_total_num = BaseApp.picInfos.size();
                myHandler.sendEmptyMessage(Contents.USB_IMAGE_LOAD_REFRESH_LIST);//22
            }
            return null;
        }
    }

    //开启的时候正在扫描，或者程序起来之后，再进行扫描
    private class MyAsyncTaskSD2 extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {
            BaseUtils.mlog(TAG, "-------------doInBackground--------SD-----");

            BaseApp.mp3InfosSD = MediaUtils.getMp3InfosSD(MainActivity.this);
            //每次查询结束就要马上计算当前的音乐在第几个  别忘了最后一次
            //处理查询过程中的点击播放事件
            if(!BaseApp.ifmusicReadFinishSD && (BaseApp.when_scan_clickSD)) {
                if (mp3Info_tempSD != null && mp3Info_tempSD.getId() > 0) {
                    for (int i = 0; i < BaseApp.mp3InfosSD.size(); i++) {
                        if (mp3Info_tempSD.getUrl().equals(BaseApp.mp3InfosSD.get(i).getUrl())) {
                            BaseApp.current_music_play_numSD = i;
                            last_sd_num = i;
                            break;
                        }
                    }
                }
            }

            if(!BaseApp.ifmusicReadFinishSD && (last_sd_num != -1 && !BaseApp.when_scan_clickSD) ){
                if (mp3Info_default_tempSD != null && mp3Info_default_tempSD.getId() > 0) {
                    for (int i = 0; i < BaseApp.mp3InfosSD.size(); i++) {
                        if (mp3Info_default_tempSD.getUrl().equals(BaseApp.mp3InfosSD.get(i).getUrl())) {
                            BaseApp.current_music_play_numSD = i;
                            last_sd_num = i;
                            break;
                        }
                    }
                }
            }

            //刷新完成了，还是没有发生点击事件
            if(last_sd_num == -1 && BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size()>0 && !BaseApp.when_scan_clickSD
                    &&(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED || mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED)){
                if (last_path_sd ==null || last_path_sd.equals("0")) {
                    BaseApp.current_music_play_numSD = 0;
                    last_sd_num = 0;
                    mp3Info_default_tempSD = BaseApp.mp3InfosSD.get(0);
                    Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_LOAD_CHANGE);//114  刷新ui界面
                    myHandler.sendMessage(msg);
                } else {
                    Mp3Info mp3Info_first = MediaUtils.getMp3Infobypath(MainActivity.this,last_path_sd);
                    if (mp3Info_first != null && mp3Info_first.getDuration()>0) {
                        for (int i = 0; i < BaseApp.mp3InfosSD.size(); i++) {
                            if (mp3Info_first.getUrl().equals(BaseApp.mp3InfosSD.get(i).getUrl())) {
                                if (last_sd_num == -1) {  //当前条目没有被改变了，这个时候采取赋值
                                    last_sd_num = i;
                                    int progress_temp = sharedPreferences.getInt("LASTMUSICPLAYTIMESD",0);
                                    BaseApp.current_music_play_progressSD = progress_temp;
                                }
                                break;
                            }
                        }
                        if(last_sd_num == -1){  //曲目可能还没有被扫描到
                            if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                                //当没有扫描完，而且又没有获取到对应的曲目的时候，只是界默认的初始化界面
                            }else if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED){
                                BaseApp.current_music_play_numSD = 0;
                                mp3Info_default_tempSD = BaseApp.mp3InfosSD.get(0);
                                Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_LOAD_CHANGE);//114
                                myHandler.sendMessage(msg);
                            }
                        }else{
                            BaseApp.current_music_play_numSD = last_sd_num ;
                            mp3Info_default_tempSD = BaseApp.mp3InfosSD.get(BaseApp.current_music_play_numSD);
                            Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_LOAD_CHANGE);//114  刷新ui界面
                            myHandler.sendMessage(msg);
                        }
                    }else{
                        if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED) {
                            //当没有扫描完，而且又没有获取到对应的曲目的时候，只是界默认的初始化界面
                        }else if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED){
                            BaseApp.current_music_play_numSD = 0;
                            last_sd_num = 0;
                            mp3Info_default_tempSD = BaseApp.mp3InfosSD.get(0);
                            Message msg = myHandler.obtainMessage(Contents.SD_MUSIC_LOAD_CHANGE);//114
                            myHandler.sendMessage(msg);
                        }
                    }
                }
            }

            //无论是否点击了，刷新总数目
            if((mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED || mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_STARTED)) {
                myHandler.sendEmptyMessage(Contents.MUSIC_REFRESH_TOTAL_NUM);
                if(mDeviceStateSD== Contents.SD_DEVICE_STATE_SCANNER_FINISHED){  //防止拔出u盘再插入的情况
                    BaseApp.when_scan_clickSD = false;
                    BaseApp.sd_scan_source =false;
                    BaseApp.ifmusicReadFinishSD = true;

                    //产生随机播放列表
                    if(BaseApp.music_shuffle_listSD!=null){
                        BaseApp.music_shuffle_listSD.clear();
                        BaseApp.music_shuffle_listSD = null;
                        BaseApp.music_shuffle_listSD = new ArrayList<>();
                    }

                    if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size()>0){
                        for(int i=0;i<BaseApp.mp3InfosSD.size();i++){
                            BaseApp.music_shuffle_listSD.add(i);
                        }
                        Collections.shuffle(BaseApp.music_shuffle_listSD);
                    }
                }
            }
            if(BaseApp.mp3InfosSD!=null && (BaseApp.mp3InfosSD.size() > last_sd_total_num)) {  //数目不变时，不刷新
                last_sd_total_num = BaseApp.mp3InfosSD.size();
                myHandler.sendEmptyMessage(Contents.SD_MUSIC_LOAD_REFRESH_LIST);//12  刷新music的list
            }

            //视频  只有当finish的时候，显示条目
            BaseApp.mp4InfosSD = Mp4MediaUtils.getMp4InfosSD(MainActivity.this);
            if(BaseApp.when_scan_click_videoSD ) {
                if (mp4Info_tempSD != null && mp4Info_tempSD.getId() > 0) {
                    for (int i = 0; i < BaseApp.mp4InfosSD.size(); i++) {
                        if (mp4Info_tempSD.getData().equals(BaseApp.mp4InfosSD.get(i).getData())) {
                            BaseApp.current_video_play_numSD = i;
                            break;
                        }
                    }
                }
            }
            if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED && BaseApp.current_video_play_numSD == -1) {
                if(BaseApp.mp4InfosSD!=null && BaseApp.mp4InfosSD.size()>0) {
                    //获取上次播放的视频
                    last_video_path_sd = sharedPreferences.getString("LASTVIDEOPATHSD", "0");
                    BaseApp.video_play_mode = sharedPreferences.getInt("VIDEOPLAYMODE", 0);
                    if(last_video_path_sd ==null || last_video_path_sd.equals("0")){
                    BaseApp.current_video_play_numSD = 0;
                    }else{
                        Mp4Info mp4Infos_first = Mp4MediaUtils.getMp4Infobypath(MainActivity.this, last_video_path_sd);
                        if (mp4Infos_first != null && mp4Infos_first.getDuration()>0) {
                            for(int i=0;i<BaseApp.mp4InfosSD.size();i++){
                                if(mp4Infos_first.getData().equals(BaseApp.mp4InfosSD.get(i).getData())){
                                    last_sd_video_num = i;
                                    BaseApp.current_video_play_progressSD = sharedPreferences.getInt("LASTVIDEOPLAYTIMESD", 0);
//                                    BaseApp.mp4InfosSD.get(last_sd_video_num).setVideo_item_progressed(progress_temp);
                                    break;
                                }
                            }
                            if(last_sd_video_num == -1){
                                BaseApp.current_video_play_numSD = -1;
                            }else{
                                BaseApp.current_video_play_numSD = last_sd_video_num;
                            }
                        }else{//把第一首歌的信息填写上去
                            BaseApp.current_video_play_numSD = -1 ;
                        }
                    }
                    //产生随机播放列表
                    if(BaseApp.video_shuffle_listSD!=null){
                        BaseApp.video_shuffle_listSD.clear();
                        BaseApp.video_shuffle_listSD = null;
                        BaseApp.video_shuffle_listSD = new ArrayList<>();
                    }
                    if(BaseApp.mp4InfosSD!=null && BaseApp.mp4InfosSD.size()>0){
                        for(int i=0;i<BaseApp.mp4InfosSD.size();i++){
                            BaseApp.video_shuffle_listSD.add(i);
                        }
                        Collections.shuffle(BaseApp.video_shuffle_listSD);
                    }
                }
                BaseApp.ifvideoReadFinishSD = true;
            }
            //无论是否刷新完，消息还是要不断的发送的
            if(BaseApp.mp4InfosSD !=null && last_sd_video_total_num < BaseApp.mp4InfosSD.size()) {
                last_sd_video_total_num = BaseApp.mp4InfosSD.size();
                myHandler.sendEmptyMessage(Contents.SD_VIDEO_LOAD_REFRESH_LIST);//22
            }
            //图片
            BaseApp.picInfosSD = PicMediaUtils.getPicInfosSD(MainActivity.this);
            if(BaseApp.when_scan_click_picSD ) {
                if (PicInfo_tempSD != null && PicInfo_tempSD.getId() > 0) {
                    for (int i = 0; i < BaseApp.picInfosSD.size(); i++) {
                        if (PicInfo_tempSD.getData().equals(BaseApp.picInfosSD.get(i).getData())) {
                            BaseApp.current_pic_play_numSD = i;
                            break;
                        }
                    }
                }
            }
            if(mDeviceStateSD == Contents.SD_DEVICE_STATE_SCANNER_FINISHED && BaseApp.current_pic_play_numSD == -1) {
                if(BaseApp.picInfosSD!=null && BaseApp.picInfosSD.size()>0){
                    last_pic_path_sd = sharedPreferences.getString("LASTPICPATHSD", "0");
                    if(last_pic_path_sd ==null || last_pic_path_sd.equals("0")){
                        BaseApp.current_pic_play_numSD = 0;
                    }else{
                        PicInfo pic_first = PicMediaUtils.getPicInfobypath(MainActivity.this, last_pic_path_sd);
                        if (pic_first != null && pic_first.getData().length()>0) {
                            for(int i=0;i<BaseApp.picInfosSD.size();i++){
                                if(pic_first.getData().equals(BaseApp.picInfosSD.get(i).getData())){
                                    last_sd_pic_num = i;
                                    break;
                                }
                            }
                            if(last_sd_pic_num == -1){
                                BaseApp.current_pic_play_numSD = -1;
                            }else{
                                BaseApp.current_pic_play_numSD = last_sd_pic_num;
                            }
                        }else{//把第一首歌的信息填写上去
                            BaseApp.current_pic_play_numSD = -1 ;
                        }
                    }
                }
                BaseApp.ifpicReadFinishSD = true;
            }
            if(BaseApp.picInfosSD != null && last_sd_pic_total_num < BaseApp.picInfosSD.size() ) {
                last_sd_pic_total_num = BaseApp.picInfosSD.size();
                myHandler.sendEmptyMessage(Contents.SD_IMAGE_LOAD_REFRESH_LIST);//22
            }
            return null;
        }
    }

    //注册广播监听，usb事件
    private void registMediaBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        mMediaReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BaseUtils.mlog(TAG, "-------------onReceive-------------");

                Uri uri = intent.getData();
                String path = uri.getPath();
                if (path.equals(Contents.USB_PATH)) {

                    String action = intent.getAction();

                    BaseUtils.mlog(TAG, "------USB--------------------action1:" + action);
                    if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED) && (mDeviceStateUSB != Contents.USB_DEVICE_STATE_SCANNER_FINISHED)) {
                        if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_UNMOUNTED) {
                            mDeviceStateUSB = Contents.USB_DEVICE_STATE_SCANNER_FINISHED;
                            BaseUtils.mlog(TAG, "------USB--------------------action:" + action);
                            myHandler.sendEmptyMessage(Contents.USB_MSG_STATE_SCANNER_FINISHED);
                        }
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        BaseApp.ifhaveUSBdevice = true;
                        mDeviceStateUSB = Contents.USB_DEVICE_STATE_MOUNTED;
                        BaseUtils.mlog(TAG, "------USB--------------------action:" + action);
                        myHandler.sendEmptyMessage(Contents.USB_MSG_STATE_MOUNTED);

                    } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                        mDeviceStateUSB = Contents.USB_DEVICE_STATE_UNMOUNTED;
                        BaseUtils.mlog(TAG, "------USB--------------------action:" + action);
                        BaseApp.ifhaveUSBdevice = false;
                        BaseApp.ifmusicReadFinishUSB = false;
                        BaseApp.ifvideoReadFinishUSB = false;
                        BaseApp.ifpicReadFinishUSB = false;
                        last_usb_num = -1;
                        last_usb_video_num = -1;
                        last_usb_pic_num = -1;
                        BaseApp.current_media = 0;
                        mp3Info_default_temp = null;
                        myHandler.sendEmptyMessage(Contents.USB_MSG_STATE_UNMOUNTED);

                    } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                        if (mDeviceStateUSB != Contents.USB_DEVICE_STATE_UNMOUNTED) {
                            mDeviceStateUSB = Contents.USB_DEVICE_STATE_SCANNER_STARTED;
                            BaseUtils.mlog(TAG, "------USB--------------------action:" + action);
                            myHandler.sendEmptyMessage(Contents.USB_MSG_STATE_SCANNER_STARTED);
                        }
                    }
                }else if(path.equals(Contents.SDCARD_PATH)){
                    String action = intent.getAction();
                    BaseUtils.mlog(TAG, "------SDCARD--------------------action1:" + action);
                    if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED) && (mDeviceStateSD != Contents.SD_DEVICE_STATE_SCANNER_FINISHED)) {
                        if (mDeviceStateSD != Contents.SD_DEVICE_STATE_UNMOUNTED) {
                            mDeviceStateSD = Contents.SD_DEVICE_STATE_SCANNER_FINISHED;
                            BaseUtils.mlog(TAG, "------SDCARD--------------------action:" + action);
                            myHandler.sendEmptyMessage(Contents.SD_MSG_STATE_SCANNER_FINISHED);
                        }
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        mDeviceStateSD = Contents.SD_DEVICE_STATE_MOUNTED;
                        BaseApp.ifhavaSDdevice = true;
                        BaseUtils.mlog(TAG, "------SDCARD--------------------action:" + action);
                        myHandler.sendEmptyMessage(Contents.SD_MSG_STATE_MOUNTED);

                    } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                        mDeviceStateSD = Contents.SD_DEVICE_STATE_UNMOUNTED;
                        BaseApp.ifhavaSDdevice = false;
                        BaseApp.ifmusicReadFinishSD = false;
                        BaseApp.ifvideoReadFinishSD = false;
                        BaseApp.ifpicReadFinishSD = false;
                        last_sd_num = -1;
                        last_sd_video_num = -1;
                        last_sd_pic_num = -1;

                        mp3Info_tempSD = null;
                        BaseUtils.mlog(TAG, "------SDCARD--------------------action:" + action);
                        myHandler.sendEmptyMessage(Contents.SD_MSG_STATE_UNMOUNTED);

                    } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                        if (mDeviceStateSD != Contents.SD_DEVICE_STATE_UNMOUNTED) {
                            mDeviceStateSD = Contents.SD_DEVICE_STATE_SCANNER_STARTED;
                            BaseUtils.mlog(TAG, "------SDCARD--------------------action:" + action);
                            myHandler.sendEmptyMessage(Contents.SD_MSG_STATE_SCANNER_STARTED);
                        }
                    }
                }
            }
        };
        registerReceiver(mMediaReceiver, new IntentFilter(intentFilter));
    }

    private void registDeviceBroadcase(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.wedesign.mediaservice.DEVICE_STATE");

        mDeviceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BaseApp.deviceNUM = intent.getByteExtra("connDeviceNum",(byte)0x05);
                BaseUtils.mlog(TAG,"connDeviceNum---------"+BaseApp.deviceNUM);

                if(BaseApp.current_fragment == 0){
                    switch (BaseApp.detect_device_num) {
                        case 0:   //检测到U盘插入
                            if (BaseApp.ifhaveUSBdevice && BaseApp.playSourceManager != 0) {  //u盘拔出后，点击无效了 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED
                                SavePlaySource(0);  //sp中保存当前播放的source
                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                BaseApp.playSourceManager = 0;
                                setDeviceBackground(0);
                                BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                YinyuanChangeToUSB();
                            }
                            break;
                        case 3:    //检测到SD卡插入
                            if (BaseApp.ifhavaSDdevice && BaseApp.playSourceManager != 3) {
                                SavePlaySource(3);
                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                BaseApp.playSourceManager = 3;
                                setDeviceBackground(3);
                                BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                YinyuanChangeToSD();
                            }
                            break;
                        case 1: //蓝牙不提示
//                            if (BaseApp.ifBluetoothConnected && BaseApp.playSourceManager != 1) {
//                                SavePlaySource(1);
//                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
//                                BaseApp.playSourceManager = 1;
//                                setDeviceBackground(1);
//                                YinyuanChangeToBT();
//                            }
                            break;
                    }
                }else if(BaseApp.current_fragment == 1){
                    if (BaseApp.current_fragment == 1 && BaseApp.ifFullScreenState) {
                        BaseApp.ifFullScreenState = false;
                        exitFullScreen();
                    }
                    if(BaseApp.current_fragment!=0) {
                        changeFragment(0);
                    }
                    switch (BaseApp.detect_device_num) {
                        case 0:   //检测到U盘插入
                            if (BaseApp.ifhaveUSBdevice && BaseApp.playSourceManager != 0) {  //u盘拔出后，点击无效了 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED
                                SavePlaySource(0);  //sp中保存当前播放的source
                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                BaseApp.playSourceManager = 0;
                                setDeviceBackground(0);
                                BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                YinyuanChangeToUSB();
                            }
                            break;
                        case 3:    //检测到SD卡插入
                            if (BaseApp.ifhavaSDdevice && BaseApp.playSourceManager != 3) {
                                SavePlaySource(3);
                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                BaseApp.playSourceManager = 3;
                                setDeviceBackground(3);
                                BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                YinyuanChangeToSD();
                            }
                            break;
                        case 1:
//                            if (BaseApp.ifBluetoothConnected && BaseApp.playSourceManager != 1) {
//                                SavePlaySource(1);
//                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
//                                BaseApp.playSourceManager = 1;
//                                setDeviceBackground(1);
//                                YinyuanChangeToBT();
//                            }
                            break;
                    }
                }else if(BaseApp.current_fragment == 2){
                    if (BaseApp.current_fragment == 2 && BaseApp.ifinPPT) {
                        BaseApp.ifinPPT = false;
                        exitFullScreen();
                    }
                    if(BaseApp.current_fragment!=0) {
                        changeFragment(0); //切到music
                    }
                    switch (BaseApp.detect_device_num) {
                        case 0:   //检测到U盘插入
                            if (BaseApp.ifhaveUSBdevice && BaseApp.playSourceManager != 0) {  //u盘拔出后，点击无效了 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED
                                SavePlaySource(0);  //sp中保存当前播放的source
                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                BaseApp.playSourceManager = 0;
                                setDeviceBackground(0);
                                BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                                YinyuanChangeToUSB();
                            }
                            break;
                        case 3:    //检测到SD卡插入
                            if (BaseApp.ifhavaSDdevice && BaseApp.playSourceManager != 3) {
                                SavePlaySource(3);
                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                                BaseApp.playSourceManager = 3;
                                setDeviceBackground(3);
                                BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                                YinyuanChangeToSD();
                            }
                            break;
                        case 1:
//                            if (BaseApp.ifBluetoothConnected && BaseApp.playSourceManager != 1) {
//                                SavePlaySource(1);
//                                BaseApp.last_playSourceManager = BaseApp.playSourceManager;
//                                BaseApp.playSourceManager = 1;
//                                setDeviceBackground(1);
//                                YinyuanChangeToBT();
//                            }
                            break;
                    }
                }

            }
        };

        registerReceiver(mDeviceReceiver, filter);
    }

    public void bindPlayMusicService(){
        if(!islocalmusicbound){
            Intent intent = new Intent(this, PlayMusicService.class);
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
            islocalmusicbound = true;
        }
    }
    public void unbindPlayMusicService(){
        if(islocalmusicbound)
        {
            unbindService(conn);
            islocalmusicbound = false;
        }
    }
    //绑定音乐服务
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            PlayMusicService.PlayMusicBinder playMusicBinder = (PlayMusicService.PlayMusicBinder) service;
            playMusicService = playMusicBinder.getPlayService();
            playMusicService.setMusicUpdateListener(musicUpdateListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if(playMusicService!=null) {
                playMusicService = null;
            }
        }
    };

    private PlayMusicService.MusicUpdateListener musicUpdateListener = new PlayMusicService.MusicUpdateListener(){
        @Override
        public void onLocalMusicRefreshProgress(int progress) {

            if (BaseApp.playSourceManager == 0) {
                if(BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size() > 0 && BaseApp.current_music_play_numUSB >= 0) {
                    editor.putInt("LASTMUSICPLAYTIMEUSB", progress);
                    editor.apply();
                }
            } else if (BaseApp.playSourceManager == 3) {
                if(BaseApp.mp3InfosSD!=null && BaseApp.mp3InfosSD.size() > 0 && BaseApp.current_music_play_numSD >= 0) {
                    editor.putInt("LASTMUSICPLAYTIMESD", progress);
                    editor.apply();
                }
            }
            if(BaseApp.current_fragment == 0){
//                BaseUtils.mlog(TAG, "-------------publish-------------");
                Message msg = myHandler.obtainMessage(Contents.MUSIC_PROGRESS);//1
                msg.arg1 = progress;
                myHandler.sendMessage(msg);
            }
        }

        @Override
        public void onLocalMusicChangeItem(int position) {
            BaseUtils.mlog(TAG, "-------------change--music item-----------");
            if (BaseApp.playSourceManager == 0) {
                //随机播放模式，当曲目改变的时候，记录50首歌曲的数字
                if (BaseApp.mp3Infos!=null && BaseApp.mp3Infos.size() > 0 && position >= 0 && BaseApp.mp3Infos.size()>position) {
                    BaseApp.current_music_play_numUSB = position;

                    editor.putString("LASTPATHUSB", BaseApp.mp3Infos.get(position).getUrl());
                    editor.apply();

                    if (BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 0 && BaseApp.mp3Infos!=null &&BaseApp.mp3Infos.size() > 0) {
                        if(mymusiclistviewAdapter != null) {
                            mymusiclistviewAdapter.notifyDataSetChanged();
                        }else{
                            mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this,BaseApp.mp3Infos);
                            musiclistview_id.setAdapter(mymusiclistviewAdapter);
                        }
                    }
                    if (BaseApp.current_fragment == 0) {
                        //不能再回调函数中处理，从video界面退出，再进入，切到music界面会出现闪退现象
                        Message msg = myHandler.obtainMessage(Contents.MUSIC_REFRESH_INFO_UI);//2
                        myHandler.sendMessage(msg);
                    }
                }
            }else if (BaseApp.playSourceManager == 3) {
                if (BaseApp.mp3InfosSD!=null &&BaseApp.mp3InfosSD.size() > 0 && position >= 0 && BaseApp.mp3InfosSD.size()>position) {
                    BaseApp.current_music_play_numSD = position;
                    editor.putString("LASTPATHSD", BaseApp.mp3InfosSD.get(position).getUrl());
                    editor.apply();

                    if (BaseApp.ifliebiaoOpen == 1 && BaseApp.current_media == 0 && BaseApp.mp3InfosSD!=null &&BaseApp.mp3InfosSD.size() > 0) {
                        if(mymusiclistviewAdapter != null) {
                            mymusiclistviewAdapter.notifyDataSetChanged();
                        }else{
                            mymusiclistviewAdapter = new MymusiclistviewAdapter(MainActivity.this,BaseApp.mp3InfosSD);
                            musiclistview_id.setAdapter(mymusiclistviewAdapter);
                        }
                    }
                    if (BaseApp.current_fragment == 0) {
                        //不能再回调函数中处理，从video界面退出，再进入，切到music界面会出现闪退现象
                        Message msg = myHandler.obtainMessage(Contents.MUSIC_REFRESH_INFO_UI);//2
                        myHandler.sendMessage(msg);
                    }
                }
            }
        }

        @Override
        public void onLocalMusicStop(int isstop) {
            musicFragment.seekBar1.setProgress(0);
            musicFragment.song_current_time.setText(MediaUtils.formatTime(0));
            button_bofang.setImageResource(R.mipmap.zanting);
            if(BaseApp.playSourceManager == 0){
                BaseApp.current_music_play_progressUSB = 0;
            }else if(BaseApp.playSourceManager == 3){
                BaseApp.current_music_play_progressSD = 0;
            }

        }

        @Override
        public void onLocalMusicChangeBofangIcon(boolean isstop) {
            if(isstop){
                button_bofang.setImageResource(R.mipmap.zanting);
            }else{
                button_bofang.setImageResource(R.mipmap.bofang);
            }
        }
    };

    public static Handler getHandler() {
        return myHandler;
    }

    private boolean isTopActivity() {
//        BaseUtils.mlog(TAG,"----isTopActivity----");
        boolean isTop = false;
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains("MainActivity")){
            isTop = true;
        }
        BaseUtils.mlog(TAG,"----isTopActivity----"+isTop);
        return isTop;
    }
    public void exitFullScreen(){
        BaseUtils.mlog(TAG, "----exitFullScreen----");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mRLayout.setSystemUiVisibility(View.VISIBLE);
        MainActivity.this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        mRLayout.setFocusable(true);
        button_layout.setVisibility(View.VISIBLE);
        come_from_layout.setVisibility(View.VISIBLE);

        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        BaseUtils.mlog(TAG, "-onVideoScreenChange-" + "状态栏的高度2:----" + statusBarHeight);
        BaseUtils.mlog(TAG, "-onVideoScreenChange-" + "dibuheight:----" + BaseApp.dibuheight);
        BaseUtils.mlog(TAG, "-onVideoScreenChange-" + " BaseApp.statebarheight:----" + BaseApp.statebarheight);

        //系统默认去掉了标题栏，只是保留了状态栏，状态栏的高度是63dp，但是返回后获取的高度为0,但在图片模式快速点击有可能出现为63的情况
//        if (statusBarHeight == 0) {
            RelativeLayout.LayoutParams mFramlayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mFramlayout.setMargins(0, BaseApp.statebarheight, 0, BaseApp.dibuheight);
            frame_content.setLayoutParams(mFramlayout);
//        }
    }
    public void enterFullScreen(){
        BaseUtils.mlog(TAG,"----enterFullScreen----");
        if (BaseApp.statebarheight == 0) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            BaseApp.statebarheight = frame.top;   //获取状态栏的高度
            BaseUtils.mlog(TAG, "Mainactivity-onVideoScreenChange-statebarheight" + BaseApp.statebarheight);
        }
        if (BaseApp.dibuheight == 0) {
            BaseApp.dibuheight = button_layout.getHeight();  //获取底栏的高度

            BaseUtils.mlog(TAG, "Mainactivity-onVideoScreenChange-dibuheight" + BaseApp.dibuheight);
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRLayout.setSystemUiVisibility(View.INVISIBLE);

        button_layout_dilan.setBackgroundResource(0);
        button_layout.setVisibility(View.GONE);
        come_from_layout.setVisibility(View.GONE);
        //mFramlayout是整个布局，包括了底栏
        RelativeLayout.LayoutParams mFramlayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        mFramlayout.setMargins(0, 0, 0,1);
        frame_content.setLayoutParams(mFramlayout);
    }
    public void changeFragment(int fragmentNUM){
        BaseUtils.mlog(TAG,"----changeFragment----");
        switch(fragmentNUM){
            case 0:
                if (BaseApp.current_fragment != 0) {
                    media_fragment_image.setVisibility(View.VISIBLE);  //activity被设置成了透明了，在fragment切换的时候，fragment会被镂空，出现闪屏现象
                    BaseUtils.mlog(TAG, "设备插入---返回到音乐界面");
                    if(musicFragment == null ){
                        musicFragment = MusicFragment.getInstance(MainActivity.this);
                    }
                    FragmentManager fm1 = getFragmentManager();
                    FragmentTransaction ft1 = fm1.beginTransaction();
//                    fragments.get(BaseApp.current_fragment).onStop();//停止当前的fragment

                    if (musicFragment.isAdded()) {
                        musicFragment.onStart();
                    } else {
                        ft1.add(R.id.media_fragment, musicFragment, "musicFragment");
                        ft1.commitAllowingStateLoss();
                    }
                    if(fragments.size() >= 3) {
                        if (BaseApp.current_fragment == 1) {
                            if (fragments.get(BaseApp.current_fragment) == null) {
                                fragments.set(1, VideoFragment.getInstance(MainActivity.this));
                            }
                        } else if (BaseApp.current_fragment == 2) {
                            if (fragments.get(BaseApp.current_fragment) == null) {
                                fragments.set(2, PictureFragment.getInstance(MainActivity.this));
                            }
                        }
                    }
                    fragments.get(BaseApp.current_fragment).onStop();//停止当前的fragment
                    ft1.hide(fragments.get(BaseApp.current_fragment));
                    ft1.remove(fragments.get(BaseApp.current_fragment));
                    ft1.show(musicFragment);
                    BaseApp.current_fragment = 0;
                }
                break;
            case 1:
                BaseUtils.mlog(TAG, "-onItemClick-create new videofragment...");
                media_fragment_image.setVisibility(View.VISIBLE);  //activity被设置成了透明了，在fragment切换的时候，fragment会被镂空，出现闪屏现象
                videoFragment = (VideoFragment) fragments.get(1);
                if(videoFragment == null ){
                    videoFragment = VideoFragment.getInstance(MainActivity.this);
                }
                FragmentManager fm2 = getFragmentManager();
                FragmentTransaction ft2 = fm2.beginTransaction();

                if (videoFragment.isAdded())   //判断videofragment是否在栈中
                    videoFragment.onStart();
                else {
                    ft2.add(R.id.media_fragment, videoFragment,"videoFragment");
                    ft2.commitAllowingStateLoss();
                }
                if(fragments.size() >= 3) {
                    if (BaseApp.current_fragment == 0) {
                        if (fragments.get(BaseApp.current_fragment) == null) {
                            fragments.set(0, MusicFragment.getInstance(MainActivity.this));
                        }
                    } else if (BaseApp.current_fragment == 2) {
                        if (fragments.get(BaseApp.current_fragment) == null) {
                            fragments.set(2, PictureFragment.getInstance(MainActivity.this));
                        }
                    }
                }
                fragments.get(BaseApp.current_fragment).onStop();//停止当前的fragment
                ft2.hide(fragments.get(BaseApp.current_fragment));   //隐藏music
                ft2.show(videoFragment);
                ft2.remove(fragments.get(BaseApp.current_fragment));
                BaseApp.current_fragment = 1;
                break;
            case 2:
                BaseUtils.mlog(TAG, "-MyHandler-" + "create new picture fragment...");
                media_fragment_image.setVisibility(View.VISIBLE);  //activity被设置成了透明了，在fragment切换的时候，fragment会被镂空，出现闪屏现象
                pictureFragment = (PictureFragment) fragments.get(2);
                if(pictureFragment == null ){
                    pictureFragment = PictureFragment.getInstance(MainActivity.this);
                }
                FragmentManager fm3 = getFragmentManager();
                FragmentTransaction ft3 = fm3.beginTransaction();
                if (pictureFragment.isAdded())   //判断PICfragment是否在栈中
                    pictureFragment.onStart();
                else {
                    ft3.add(R.id.media_fragment, pictureFragment,"pictureFragment");
                    ft3.commitAllowingStateLoss();
                }
                if(fragments.size() >= 3) {
                    if (BaseApp.current_fragment == 0) {
                        if (fragments.get(BaseApp.current_fragment) == null) {
                            fragments.set(0, MusicFragment.getInstance(MainActivity.this));
                        }
                    } else if (BaseApp.current_fragment == 1) {
                        if (fragments.get(BaseApp.current_fragment) == null) {
                            fragments.set(1, VideoFragment.getInstance(MainActivity.this));
                        }
                    }
                }
                ft3.hide(fragments.get(BaseApp.current_fragment));   //隐藏
                ft3.show(pictureFragment);
                fragments.get(BaseApp.current_fragment).onStop();//停止当前的fragment
                ft3.remove(fragments.get(BaseApp.current_fragment));
                BaseApp.current_fragment = 2;
                break;
        }
    }
    public void setDeviceBackground(int num){
        BaseUtils.mlog(TAG,"----setDeviceBackground----");
        switch(num){
            case 0:
                button_usb.setBackgroundResource(R.mipmap.yinyuan_p);
                button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                button_aux.setBackgroundResource(R.mipmap.touming_b);
                button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                break;
            case 1:
                button_usb.setBackgroundResource(R.mipmap.touming_b);
                button_bluetooth.setBackgroundResource(R.mipmap.yinyuan_p);
                button_aux.setBackgroundResource(R.mipmap.touming_b);
                button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                break;
            case 2:
                button_usb.setBackgroundResource(R.mipmap.touming_b);
                button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                button_aux.setBackgroundResource(R.mipmap.yinyuan_p);
                button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                break;
            case 3:
                button_usb.setBackgroundResource(R.mipmap.touming_b);
                button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                button_aux.setBackgroundResource(R.mipmap.touming_b);
                button_SDCard.setBackgroundResource(R.mipmap.yinyuan_p);
                break;
        }
    }
    public void setMusicFragmentContent(Mp3Info mp3Info){  //设置fragment的填充界面
        BaseUtils.mlog(TAG, "---setMusicFragmentContent----");
        if(BaseApp.playSourceManager == 0) {
            Bitmap albumBitmap = MediaUtils.getArtwork(getApplicationContext(), mp3Info.getId(), mp3Info.getAlbumId(), true, false);
            musicFragment.bt_device_name.setText(null);   //进入蓝牙会闪一下aux
            musicFragment.album_icon.setImageBitmap(albumBitmap);
            musicFragment.song_name.setText(mp3Info.getTittle());
            musicFragment.zhuanji_name.setText(mp3Info.getAlbum());
            musicFragment.chuangzhe_name.setText(mp3Info.getArtist());
            musicFragment.button_play_mode_ico.setImageResource(music_play_mode_resource_ico[BaseApp.music_play_mode]);
            musicFragment.button_play_mode_name.setText(button_play_mode_name_ico[BaseApp.music_play_mode]);
            musicFragment.num_order.setText((BaseApp.current_music_play_numUSB + 1) + "/" + BaseApp.mp3Infos.size());
            musicFragment.song_total_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
            musicFragment.seekBar1.setMax((int) mp3Info.getDuration());
        }else if(BaseApp.playSourceManager == 3){
            Bitmap albumBitmap = MediaUtils.getArtwork(getApplicationContext(), mp3Info.getId(), mp3Info.getAlbumId(), true, false);
            musicFragment.bt_device_name.setText(null);
            musicFragment.album_icon.setImageBitmap(albumBitmap);
            musicFragment.song_name.setText(mp3Info.getTittle());
            musicFragment.zhuanji_name.setText(mp3Info.getAlbum());
            musicFragment.chuangzhe_name.setText(mp3Info.getArtist());
            musicFragment.button_play_mode_ico.setImageResource(music_play_mode_resource_ico[BaseApp.music_play_mode]);
            musicFragment.button_play_mode_name.setText(button_play_mode_name_ico[BaseApp.music_play_mode]);
            musicFragment.num_order.setText((BaseApp.current_music_play_numSD + 1) + "/" + BaseApp.mp3InfosSD.size());
            musicFragment.song_total_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
            musicFragment.seekBar1.setMax((int) mp3Info.getDuration());
        }
    }
    public void setDefaultMusicFragmentContent() {  //设置默认的fragment的填充界面
        BaseUtils.mlog(TAG,"---setDefaultMusicFragmentContent----");
        musicFragment.bt_device_name.setText(null);
        musicFragment.album_icon.setImageResource(R.mipmap.yinyue);
        musicFragment.song_name.setText(R.string.gequmingcheng);
        musicFragment.zhuanji_name.setText(R.string.zhuanji);
        musicFragment.chuangzhe_name.setText(R.string.geshou);
        musicFragment.num_order.setText("" + 0);
        musicFragment.song_current_time.setText(MediaUtils.formatTime(0));
        musicFragment.song_total_time.setText(MediaUtils.formatTime(0));
    }
    public void setMusicProgress(){
        BaseUtils.mlog(TAG, "---setMusicProgress----");
        if(BaseApp.playSourceManager == 0){
            Message msg = myHandler.obtainMessage(Contents.MUSIC_PROGRESS);//设置进度条，直接在这里设置容易不起作用
            msg.arg1 = BaseApp.current_music_play_progressUSB;
            myHandler.sendMessage(msg);
        }else if(BaseApp.playSourceManager == 3){
            Message msg = myHandler.obtainMessage(Contents.MUSIC_PROGRESS);//设置进度条，直接在这里设置容易不起作用
            msg.arg1 = BaseApp.current_music_play_progressSD;
            myHandler.sendMessage(msg);
        }
    }
    public void setDefaultMusicProgress(){
        BaseUtils.mlog(TAG,"---setDefaultMusicProgress----");
        Message msg = myHandler.obtainMessage(Contents.MUSIC_PROGRESS);//设置进度条，直接在这里设置容易不起作用
        msg.arg1 = 0;
        myHandler.sendMessage(msg);
    }
    public void chooseListViewToShow(int num){
        BaseUtils.mlog(TAG, "---chooseListViewToShow----");
        switch(num){
            case 0:  //music
                musiclistview_id.setVisibility(View.VISIBLE);
                videolistview_id.setVisibility(View.GONE);
                gridview_id.setVisibility(View.GONE);
                loading_layout.setVisibility(View.GONE);
                break;
            case 1:  //video
                musiclistview_id.setVisibility(View.GONE);
                videolistview_id.setVisibility(View.VISIBLE);
                gridview_id.setVisibility(View.GONE);
                loading_layout.setVisibility(View.GONE);
                break;
            case 2:  //pic
                musiclistview_id.setVisibility(View.GONE);
                videolistview_id.setVisibility(View.GONE);
                gridview_id.setVisibility(View.VISIBLE);
                loading_layout.setVisibility(View.GONE);
                break;
            case 3:  //扫描状态，包含无文件和扫描中
                musiclistview_id.setVisibility(View.GONE);
                videolistview_id.setVisibility(View.GONE);
                gridview_id.setVisibility(View.GONE);
                loading_layout.setVisibility(View.VISIBLE);
                break;
            case 4:
                musiclistview_id.setVisibility(View.GONE);
                videolistview_id.setVisibility(View.GONE);
                gridview_id.setVisibility(View.GONE);
                loading_layout.setVisibility(View.GONE);
                break;
        }
    }
    public void liebiaoShowNOorScan(int state){
        switch(state){
            case 0:  //无文件
                loading_image.setBackgroundResource(0);
                loading_image.setBackgroundResource(R.mipmap.jinggao_ico);
                loading_text.setText(R.string.wuwenjian);
                break;
            case 1: //动画
                loading_image.setBackgroundResource(0);
                loading_image.setBackgroundResource(R.drawable.loading_ico);
                frameAnim = (AnimationDrawable) loading_image.getBackground();
                frameAnim.start();
                loading_text.setText(R.string.jiazaizhong);
                break;
        }

    }
    public void initComeFromUI(){
        button_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG, "button_usb----CLICK");
                if(!BaseApp.brake_flag){   //视频手刹界面切过来，需要去掉手刹标志
                    brake_layout.setVisibility(View.GONE);
                }
                if (BaseApp.ifhaveUSBdevice && BaseApp.playSourceManager != 0) {  //u盘拔出后，点击无效了 && MainActivity.mDeviceStateUSB == Contents.USB_DEVICE_STATE_SCANNER_FINISHED
                    SavePlaySource(0);  //sp中保存当前播放的source
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                    BaseApp.playSourceManager = 0;
                    button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                    button_aux.setBackgroundResource(R.mipmap.touming_b);
                    button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                    button_usb.setBackgroundResource(R.mipmap.yinyuan_p);
                    if(BaseApp.current_fragment!=0){
                        changeFragment(0);
                    }
                    BaseApp.ispauseUSB = 2; //只要是切源总是自动播放
                    YinyuanChangeToUSB();
                }
            }
        });

        button_bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG, "button_bluetooth----CLICK");
                if(!BaseApp.brake_flag ){
                    brake_layout.setVisibility(View.GONE);
                }
                if (BaseApp.ifBluetoothConnected && BaseApp.playSourceManager != 1) {
                    SavePlaySource(1);
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                    BaseApp.playSourceManager = 1;
                    if(BaseApp.current_fragment!=0){
                        changeFragment(0);
                    }
                    button_usb.setBackgroundResource(R.mipmap.touming_b);
                    button_bluetooth.setBackgroundResource(R.mipmap.yinyuan_p);
                    button_aux.setBackgroundResource(R.mipmap.touming_b);
                    button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                    YinyuanChangeToBT();
                }
            }
        });

        button_aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG, "button_aux----CLICK");
                if(!BaseApp.brake_flag ){
                    brake_layout.setVisibility(View.GONE);
                }
                if (BaseApp.playSourceManager != 2) {
                    SavePlaySource(2);
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                    BaseApp.playSourceManager = 2;
                    if(BaseApp.current_fragment!=0){
                        changeFragment(0);
                        myHandler.sendEmptyMessage(Contents.DIRECT_CHANGE_PIC_TO_AUX);  //如果直接使用onAUXEvent，界面出不来
                    }else{
                        AUXEent();
                    }
                    button_usb.setBackgroundResource(R.mipmap.touming_b);
                    button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                    button_aux.setBackgroundResource(R.mipmap.yinyuan_p);
                    button_SDCard.setBackgroundResource(R.mipmap.touming_b);
                }
            }
        });

        button_SDCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseUtils.mlog(TAG, "button_SDCard----CLICK");
                if(!BaseApp.brake_flag){
                    brake_layout.setVisibility(View.GONE);
                }
                if (BaseApp.ifhavaSDdevice && BaseApp.playSourceManager != 3) {
                    SavePlaySource(3);
                    BaseApp.last_playSourceManager = BaseApp.playSourceManager;
                    BaseApp.playSourceManager = 3;
                    if(BaseApp.current_fragment!=0){
                        changeFragment(0);
                    }
                    button_usb.setBackgroundResource(R.mipmap.touming_b);
                    button_bluetooth.setBackgroundResource(R.mipmap.touming_b);
                    button_aux.setBackgroundResource(R.mipmap.touming_b);
                    button_SDCard.setBackgroundResource(R.mipmap.yinyuan_p);
                    BaseApp.ispauseSD = 2; //只要是切源总是自动播放
                    YinyuanChangeToSD();
                }
            }
        });
    }
    public void choosedilanAndFragmentShow(int incomeFragment){
        BaseUtils.mlog(TAG,"choosedilanAndFragmentShow---"+incomeFragment);
        BaseUtils.mlog(TAG,"choosedilanAndFragmentShow---"+BaseApp.playSourceManager);
        switch(BaseApp.playSourceManager){
            case 0://usb
            case 3://sd
                button_layout.setVisibility(View.VISIBLE);
                button_layout_bluetooth.setVisibility(View.GONE);
                if(incomeFragment == 2){  //图片
                    button_layout.setBackgroundResource(R.mipmap.dilan_pic);
                    button_fangda.setVisibility(View.VISIBLE);
                    button_suoxiao.setVisibility(View.VISIBLE);
                    button_play_mode.setVisibility(View.GONE);
                    button_shangqu.setImageResource(R.mipmap.shangqu_pic);
                    button_bofang.setImageResource(R.mipmap.zanting_pic);
                    button_xiaqu.setImageResource(R.mipmap.xiaqu_pic);
                    button_liebiao.setImageResource(R.mipmap.liebiao_pic);
                    button_layout_dilan.setBackgroundResource(0); //去掉aux的底栏背景，免得全屏显示出来了
                }else{ //视频和音乐公用一个底部
                    button_layout.setBackgroundResource(R.mipmap.dilan);
                    button_fangda.setVisibility(View.GONE);
                    button_suoxiao.setVisibility(View.GONE);
                    button_play_mode.setVisibility(View.VISIBLE);
                    button_shangqu.setImageResource(R.mipmap.shangqu);
                    if(BaseApp.playSourceManager == 0){
                        if(incomeFragment == 0){
                            if(BaseApp.ispauseUSB == 1){
                                button_bofang.setImageResource(R.mipmap.zanting);
                            }else{
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }else if(incomeFragment == 1){
                            if(BaseApp.isVideopauseUSB == 1){
                                button_bofang.setImageResource(R.mipmap.zanting);
                            }else{
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                    }else if(BaseApp.playSourceManager == 3){
                        if(incomeFragment == 0){
                            if(BaseApp.ispauseUSB == 1){
                                button_bofang.setImageResource(R.mipmap.zanting);
                            }else{
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }else if(incomeFragment == 1){
                            if(BaseApp.isVideopauseUSB == 1){
                                button_bofang.setImageResource(R.mipmap.zanting);
                            }else{
                                button_bofang.setImageResource(R.mipmap.bofang);
                            }
                        }
                    }
                    button_xiaqu.setImageResource(R.mipmap.xiaqu);
                    button_liebiao.setImageResource(R.mipmap.liebiao);
                    button_layout_dilan.setBackgroundResource(0); //去掉aux的底栏背景，免得全屏显示出来了
                    if(BaseApp.current_fragment == 0 && musicFragment!=null) {
                        musicFragment.progress_really_layout.setVisibility(View.VISIBLE);
                        musicFragment.bt_device_name.setVisibility(View.INVISIBLE);
                        musicFragment.zhuanji_layout.setVisibility(View.VISIBLE);
                        musicFragment.order_layout.setVisibility(View.VISIBLE);
                        musicFragment.song_name.setVisibility(View.VISIBLE);
                        musicFragment.singer_layout.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case 1:  //bt
                button_layout.setVisibility(View.GONE);
                button_layout_bluetooth.setVisibility(View.VISIBLE);
                button_shangqu_bluetooth.setVisibility(View.VISIBLE);
                button_bofang_bluetooth.setVisibility(View.VISIBLE);
                button_xiaqu_bluetooth.setVisibility(View.VISIBLE);
                if(musicFragment!=null) {
                    BaseUtils.mlog(TAG,"musicFragment---choosedilanAndFragmentShow");
                    musicFragment.bt_device_name.setVisibility(View.VISIBLE);
                    musicFragment.singer_layout.setVisibility(View.VISIBLE);
                    musicFragment.song_name.setVisibility(View.VISIBLE);
                    musicFragment.zhuanji_layout.setVisibility(View.GONE);
                    musicFragment.order_layout.setVisibility(View.INVISIBLE);
                    musicFragment.album_icon.setImageResource(R.mipmap.bt);
                    musicFragment.progress_really_layout.setVisibility(View.INVISIBLE);
                }
                break;
            case 2:  //aux
                button_layout.setVisibility(View.GONE);
                button_layout_dilan.setBackgroundResource(R.mipmap.aux_dilan);
                button_layout_bluetooth.setVisibility(View.GONE);
                if(musicFragment!=null) {
                    musicFragment.bt_device_name.setVisibility(View.VISIBLE);
                    musicFragment.zhuanji_layout.setVisibility(View.INVISIBLE);
                    musicFragment.singer_layout.setVisibility(View.INVISIBLE);
                    musicFragment.order_layout.setVisibility(View.INVISIBLE);
                    musicFragment.song_name.setVisibility(View.INVISIBLE);
                    musicFragment.progress_really_layout.setVisibility(View.INVISIBLE);
                    musicFragment.bt_device_name.setText("AUX");
                    musicFragment.album_icon.setImageResource(R.mipmap.aux_ico);
                }
                break;
        }
    } //根据不同的底部设置不同的底栏
}
