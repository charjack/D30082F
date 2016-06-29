package com.wedesign.mediaplayer.vo;

/**
 * Created by NANA on 2016/5/7.
 */
public class Contents {
    public static final int MSG_DISMISS_LIEBIAO_AUTO= 616;        //改变蓝牙的播放按钮
    public static final int MSG_DEVICE_STATE_UI= 99;        //改变蓝牙的播放按钮
    public static final int MUSIC_PROGRESS = 1;                         //刷新音乐的进度条和时间
    public static final int USB_MUSIC_LOAD_REFRESH_LIST= 12;                      //音乐数据加载完之后的消息
    public static final int SD_MUSIC_LOAD_REFRESH_LIST= 112;                      //音乐数据加载完之后的消息
    public static final int USB_MUSIC_INIT_CHANGE_TO_PLAY = 802;                          //程序退出后，再次进入数据没有改变
    public static final int USB_MUSIC_INIT_CHANGE= 114;                          //程序退出后，再次进入数据没有改变,这个时候自动播放
    public static final int USB_MUSIC_INIT_CHANGE_LIST= 111;                    //进入的时候已经有了数据，刷洗list
    public static final int SD_MUSIC_INIT_CHANGE_TO_PLAY= 801;                          //程序退出后，再次进入数据没有改变
    public static final int SD_MUSIC_INIT_CHANGE= 194;                          //程序退出后，再次进入数据没有改变
    public static final int SD_MUSIC_INIT_CHANGE_LIST= 191;                     //进入的时候已经有了数据，刷洗list
    public static final int USB_MUSIC_LOAD_CHANGE= 115;                          //程序退出后，再次进入数据没有改变
    public static final int SD_MUSIC_LOAD_CHANGE= 195;                          //程序退出后，再次进入数据没有改变
    public static final int MUSIC_REFRESH_INFO_UI= 2;                   //刷新音乐Fragment界面
    public static final int USB_VIDEO_LOAD_REFRESH_LIST= 22;                      //视频数据加载完成
    public static final int SD_VIDEO_LOAD_REFRESH_LIST= 212;                      //视频数据加载完成
    public static final int IMAGE_ITEM_CLICK= 30;                       //点击gridview的条目消息
    public static final int IMAGE_PPT_COMEBACK= 31;                     //ppt结束返回消息
    public static final int USB_IMAGE_LOAD_REFRESH_LIST= 32;                      //图片加载完成
    public static final int SD_IMAGE_LOAD_REFRESH_LIST= 312;                      //图片加载完成

    public static final int USB_STATE_MOUNTED = 1;                      //u盘未挂载
    public static final int USB_STATE_UNMOUNTED = 2;                    //u盘挂载了
    public static final int USB_STATE_SCANNER_STARTED = 3;              //开始扫描U盘，把信息写入contentprovider中
    public static final int USB_STATE_SCANNER_FINISHED = 4;             //U盘扫描结束

    public static final int SD_STATE_MOUNTED = 1;                      //u盘未挂载
    public static final int SD_STATE_UNMOUNTED = 2;                    //u盘挂载了
    public static final int SD_STATE_SCANNER_STARTED = 3;              //开始扫描U盘，把信息写入contentprovider中
    public static final int SD_STATE_SCANNER_FINISHED = 4;             //U盘扫描结束

    public static final int USB_DEVICE_STATE_UNMOUNTED = 0;                 //设备状态
    public static final int USB_DEVICE_STATE_MOUNTED = 1;
    public static final int USB_DEVICE_STATE_SCANNER_STARTED = 2;
    public static final int USB_DEVICE_STATE_SCANNER_FINISHED = 3;

    public static final int SD_DEVICE_STATE_UNMOUNTED = 0;                 //设备状态
    public static final int SD_DEVICE_STATE_MOUNTED = 1;
    public static final int SD_DEVICE_STATE_SCANNER_STARTED = 2;
    public static final int SD_DEVICE_STATE_SCANNER_FINISHED = 3;

    public static final int USB_MSG_STATE_UNMOUNTED = 100;                  //判断出设备状态后，发送message，进行相应的处理
    public static final int USB_MSG_STATE_MOUNTED = 200;
    public static final int USB_MSG_STATE_SCANNER_STARTED = 300;
    public static final int USB_MSG_STATE_SCANNER_FINISHED = 400;

    public static final int SD_MSG_STATE_UNMOUNTED = 190;                  //判断出设备状态后，发送message，进行相应的处理
    public static final int SD_MSG_STATE_MOUNTED = 290;
    public static final int SD_MSG_STATE_SCANNER_STARTED = 390;
    public static final int SD_MSG_STATE_SCANNER_FINISHED = 490;

    public static final int MUSIC_REFRESH_TOTAL_NUM = 51;                //当没有点击item，并且没有扫描结束的时候，发送消息改变ui界面的总数

    public static final int BLUETOOTH_PLAY = 666;                   //蓝牙连接上了，发送消息，改变musicfragment
    public static final int BLUETOOTH_CONNECTED = 776;                //蓝牙断开了，发送消息，改变musicfragment
    public static final int BLUETOOTH_DISCONNECTED = 777;                //蓝牙断开了，发送消息，改变musicfragment
    public static final int BLUETOORH_CHANGE_BOFANG_BUTTON = 888;        //改变蓝牙的播放按钮

    public static final int MSG_LOCAL_FIRST_CHANGE_UI= 730;        //改变蓝牙的播放按钮
    public static final int MSG_AUX_FIRST_CHANGE_UI= 731;        //改变蓝牙的播放按钮

    public static final int USB_VIDEO_INIT_CHANGE = 214;
    public static final int SD_VIDEO_INIT_CHANGE = 215;

    public static final int USB_PIC_INIT_CHANGE = 314;
    public static final int SD_PIC_INIT_CHANGE = 315;

    public static final int PPT_COMEBAKC = 995;   //处理PPT进入的图片的切换
    public static final int DIRECT_CHANGE_PIC_TO_AUX = 996;   //处理从图片拔掉u盘的情况
    public static final int MSG_CANCEL_COVER = 303;   //把activity主题设置成透明，解决视频卡顿问题，这样导致的透明背景问题，通过盖一层黑色的背景色解决掉

    public static final String USB_PATH = "/mnt/usb_storage";
    public static final String SDCARD_PATH = "/mnt/external_sd0";
}
