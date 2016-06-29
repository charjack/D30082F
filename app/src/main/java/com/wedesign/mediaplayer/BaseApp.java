package com.wedesign.mediaplayer;

import android.app.Application;
import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.wedesign.mediaplayer.vo.Mp3Info;
import com.wedesign.mediaplayer.vo.Mp4Info;
import com.wedesign.mediaplayer.vo.PicInfo;

import java.util.ArrayList;

/**
 * Created by NANA on 2016/6/6.
 */
public class BaseApp extends Application {
    public static boolean if_debug = true;                //调试模式是否打开
    public static Context appContext;                     //全局应用的上下文
    public static DbUtils dbUtils;                        //全局的数据库
    public static DbUtils dbUtilsSD;                        //全局的数据库

    public static int current_music_play_numUSB = -1;       //当前的播放曲目，开始时赋值为-1，表示没有选中的曲目，这点很重要，不能设置为0
    public static int current_music_play_progressUSB = 0;   //记录当前音乐播放的进度
    public static int current_video_play_numUSB = -1;       //当前视频播放的曲目
    public static int current_video_play_progressUSB = 0;   //记录当前视频播放的进度
    public static int current_pic_play_numUSB = -1;         //记录当前图片的number

    public static int current_music_play_numSD = -1;       //当前的播放曲目，开始时赋值为-1，表示没有选中的曲目，这点很重要，不能设置为0
    public static int current_music_play_progressSD = 0;   //记录当前音乐播放的进度
    public static int current_video_play_numSD = -1;       //当前视频播放的曲目
    public static int current_video_play_progressSD = 0;   //记录当前视频播放的进度
    public static int current_pic_play_numSD = -1;         //记录当前图片的number


    static ArrayList<Mp3Info> mp3Infos = new ArrayList<>();//进行异步加载
    static ArrayList<Mp3Info> mp3InfosSD = new ArrayList<>();
    static ArrayList<Mp4Info> mp4Infos = new ArrayList<>();
    static ArrayList<Mp4Info> mp4InfosSD = new ArrayList<>();
    static ArrayList<PicInfo> picInfos = new ArrayList<>();
    static ArrayList<PicInfo> picInfosSD = new ArrayList<>();

    public static byte current_fragment = 0;              //当前fragment是哪个  0:音乐 1:视频 2:图片
    public static byte current_media = 0;                 //0:music 1: video 2:picture
    public static byte ifliebiaoOpen = 0;                 //判断列表是否打开了0表示没有打开 1表示打开了
    public static byte liebiaoOpenNum = 0;                //列表打开之后，定时消失

    public static boolean ifhaveUSBdevice = false;
    public static boolean ifhavaSDdevice = false;
    public static boolean ifBluetoothConnected = false;
    public static boolean ifhaveAUXdevice = true;         //目前车机的状态是aux一定常在

    public static boolean isbindBTservice = false;        //判定服务是否绑定了
    public static int playSourceManager = 0;              //0表示上次播放的源来自usb，1表示上次播放的源来自蓝牙，2表示上次状态是aux，3表示上次播放的源是sd卡，只记录usb和sd

    public static boolean when_scan_click = false;       //判断scan的时候，是否点击了item
    public static boolean when_scan_clickSD = false;       //判断scan的时候，是否点击了item

    public static byte music_media_state_scan = 0;        //记录扫描的状态
    public static byte music_media_state_scanSD = 0;        //记录扫描的状态
    public static byte video_media_state_scan = 0;
    public static byte video_media_state_scanSD = 0;
    public static byte pic_media_state_scan = 0;
    public static byte pic_media_state_scanSD = 0;

    public static int music_play_mode = 0;
    public static int video_play_mode = 0;

    public static byte ispauseUSB = 2;                    //0 表示不暂停 1表示暂停，下次进来不需要播放 2表示暂停，但是下次进来需要播放
    public static byte ispauseSD = 2;
    public static byte isVideopauseUSB = 2;
    public static byte isVideopauseSD = 2;

    public static boolean exitUI =false;
    public static boolean ifFullScreenState = false;

    public static int statebarheight = 0;
    public static int dibuheight = 0;

    public static boolean ifmusicReadFinishUSB = false;
    public static boolean ifvideoReadFinishUSB = false;
    public static boolean ifpicReadFinishUSB = false;
    public static boolean ifmusicReadFinishSD = false;
    public static boolean ifvideoReadFinishSD = false;
    public static boolean ifpicReadFinishSD = false;

    public static boolean usb_scan_source = false;   //当前usb是否在扫描
    public static boolean sd_scan_source = false;   //

    public static String btphone_name = null;

    public static int last_playSourceManager = 0;   //记录上一次播放源，0表示usb，1表示蓝牙，2表示aux，3表示sd卡

    public static boolean ifinPPT = false;   //在设备拔出的时候需要做一个判断

    public static byte detect_device_num = -1;

    public static boolean isbindBTserviceMP = false;

    public static boolean pre_show =false;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        dbUtils = DbUtils.create(getApplicationContext(), "DongfengVideoSave.db");
        dbUtilsSD = DbUtils.create(getApplicationContext(), "DongfengVideoSaveSD.db");
    }
}
