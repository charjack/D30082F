package com.wedesign.mediaplayer.Bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;


import com.wedesign.bluetoothservice.IBluetoothForApksCallback;
import com.wedesign.bluetoothservice.IBluetoothForApksService;
import com.wedesign.mediaplayer.BaseApp;
import com.wedesign.mediaplayer.MainActivity;
import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.vo.Contents;


/**
 * Created by NANA on 2016/5/16.
 */


public class BtmusicPlay {
    private static final String TAG = "BtmusicPlay";
    private IBluetoothForApksCallback callback = null;
    public static IBluetoothForApksService service = null;
    public static Handler BTmusicPlayhand = null;
    //call back 使用的msg id ,回调函数不是这边的函数主动调用的，而是服务端调用的
    //具体作用参看callback类中的说明
    public static final int MSG_MUSIC_INFO = 401;
    public static final int MSG_CONNECTED_STATE = 402;
    public static final int MSG_PLAY_STATE = 403;
    public static final int MSG_HAVE_SOURCE = 404;
    public static final int MSG_NEXT_TRACK = 429;
    public static final int MSG_PREV_TRACK = 430;
    public static final int MSG_EXTEVENT_IN = 431;
    public static final int MSG_EXTEVENT_OUT = 432;
    public static final int MSG_CONNECTED_NAME = 417;
    //////////////////////////////////////////////////////

    private Context mainContext = null;
//    private Handler mainhandler = null;

    private BtmusicPlay(Context context, Handler handler){
        mainContext = context;
//        mainhandler = handler; //存储mainActivity的handler   ,传递过来，没有用到
        BTmusicPlayhand = this.BTmusicPlayhandler;
        callback = new BluetoothCallback(BTmusicPlayhand);
    }
    public static Handler getHandler() {
        return BTmusicPlayhand;
    }

    public static final int MSG_ACTIVITY_CHANGE = 420;
    public static final byte SOURCE_IN_BTMUSIC = 7;
    //记录播放状态,这个播放状态的记录，是自己维护的，只要保证第一次获取到了正确的状态后，以后的播放状态不要让服务去控制，
    //因为有的时候，我调用一次控制命令，那边给我发过来两条callback，导致状态记录不准确。
    public boolean mPlayStatus = false;
    public String txtName, txtArtists;
    public int bt_position = 1 ,bt_totalnum = 1;

    public int outSoundisPlay = 0;
    public boolean connectStatus = false;

    private volatile static BtmusicPlay instance = null;
    public static BtmusicPlay getInstance(Context context, Handler handler){
        if(instance == null){
            synchronized (BtmusicPlay.class) {
                if (instance == null){
                    instance = new BtmusicPlay(context,handler);
                }
            }
        }
        instance.mainContext = context;
//        instance.mainhandler = handler;
        return instance;
    }

    private static IBluetoothForApksService getService(){
        return service;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serv) {
            service = IBluetoothForApksService.Stub.asInterface(serv);
            try {
                service.registerCallback(callback);
                BaseUtils.mlog(TAG, "IBluetoothForApksService onServiceConnected !!!! finished!!!!!!!!!!!!!!!!! ");
             //   getConnectState();  //绑定结束后，判定蓝牙连接状态
            } catch (RemoteException e) {
                e.printStackTrace();
                service = null;
            }
            BaseApp.isbindBTservice = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BaseUtils.mlog(TAG, "IBluetoothForApksService onServiceDisconnected-----------------------------finished ");
            BaseApp.isbindBTservice = false;
        }
    };

    public void musicPlayOrPause() {
        BaseUtils.mlog(TAG, "musicPlayOrPause");
        try {
            service.musicPlayOrPause();
            mPlayStatus = !mPlayStatus;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void musicNext() {
        BaseUtils.mlog(TAG, "musicNext");
        try {
            service.musicNext();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void musicPrevious() {
        try {
            service.musicPrevious();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getMusicInfo() {
        try {
            BaseUtils.mlog(TAG, "getMusicInfo");
            service.getCurrentDeviceName();
            service.getMusicInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sourceCtrl(String action, byte from) {
        try {
            BaseUtils.mlog(TAG, "sourceCtrl");
            service.sourceCtrl(action, from);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getConnectState() {
        try {
            BaseUtils.mlog(TAG, "getConnectState");
            service.getConnectState();  //触发callback回调
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    Handler BTmusicPlayhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CONNECTED_STATE:  //蓝牙状态获取结束后才会调用
                    BaseUtils.mlog(TAG, "MSG_CONNECTED_STATE");
                    String status1 = (String) msg.obj;
                    connectStatus = status1.equals("" + true);
                    if(connectStatus) {  //连接上了
                        BaseApp.ifBluetoothConnected = true;
                        Handler mainhandler = MainActivity.getHandler();
                        mainhandler.sendEmptyMessage(Contents.BLUETOOTH_CONNECTED);  //   776
                    }else{
                        BaseApp.ifBluetoothConnected = false;
                        Handler mainhandler = MainActivity.getHandler();
                        //将图标的按钮变暗，并且判断当前是否播放的蓝牙，如果是则需要切换fragment
                        mainhandler.sendEmptyMessage(Contents.BLUETOOTH_DISCONNECTED);  //   777
                    }
                    break;

                case MSG_HAVE_SOURCE:  //源的获取已经结束了
                    BaseUtils.mlog(TAG, "MSG_HAVE_SOURCE");
                    Bundle bundle = (Bundle) msg.getData();
                    byte id = bundle.getByte("id");
                    if (SOURCE_IN_BTMUSIC == id) {
                        outSoundisPlay = 1;//表示这个消息是针对btmusic的，btmusic会收到两个消息，一个是源进来了，一个是源出去了。
                        if(!bundle.getBoolean("state")){
                            outSoundisPlay = 2;//表示获取了蓝牙源，并且没有丢失，也就是enable了的意思,false表示打开了闸门，可以播放音乐了
                        }else{
                            outSoundisPlay = 3;
                            //现在是调试阶段，后期处理
//                            if(mPlayStatus){//源出去了，直接停止播放
//                                musicPlayOrPause();
//                            }
                        }
                    }else{
                        outSoundisPlay = 0;
                        //现在是调试阶段，后期处理
//                        if(mPlayStatus){//切换到usb状态下，丢失源断开音频
//                            musicPlayOrPause();
//                        }
                    }
                    BaseUtils.mlog(TAG, "outSoundisPlay---------" + outSoundisPlay+"---------mPlayStatus--------"+mPlayStatus);
                    if(outSoundisPlay == 2 ){
                        getMusicInfo();
                    }
                    break;

                case MSG_MUSIC_INFO:  //音乐信息已经获取了
                    BaseUtils.mlog(TAG, "MSG_MUSIC_INFO");
                    txtName = ((MusicInfoEvent) msg.obj).name;
                    txtArtists = ((MusicInfoEvent) msg.obj).artist;
                    bt_position = ((MusicInfoEvent) msg.obj).pos;
                    bt_totalnum = ((MusicInfoEvent) msg.obj).total;
                    BaseUtils.mlog(TAG,txtName+"--"+txtArtists+"---");
                    Handler mainhandler = MainActivity.getHandler();
                    if (mainhandler != null) {
                        mainhandler.sendEmptyMessage(MSG_ACTIVITY_CHANGE);
                    }

//                    if(!mPlayStatus){//切换带蓝牙状态，直接播放
//                        BaseUtils.mlog(TAG, "MSG_MUSIC_INFO-----mPlayStatus---为真播放--------------" + mPlayStatus);
//                        mPlayStatus = true;
//                        musicPlayOrPause();
//                        musicPlayOrPause();
//                    }
                    try {
                        //这里获取的蓝牙状态有可能是假的
//                        mPlayStatus = service.getPlaystate();  //这里的调用根本触发不了，getplaystate的callback函数,所以程序不会继续往下跑了，但是可以获取手机端的播放状态
                        BaseUtils.mlog(TAG, "MSG_MUSIC_INFO-----mPlayStatus---原始播放状态--------------"+mPlayStatus);
                        if(BaseApp.playSourceManager == 1&&!mPlayStatus) {  //原始是暂停状态，进行自动播放
                            mPlayStatus = !mPlayStatus;
                            service.musicPlayOrPause();
                            BaseUtils.mlog(TAG, "获取音乐信息后，请求播放。。。");
                        }
                        Handler mainhandler2 = MainActivity.getHandler();
                        mainhandler2.sendEmptyMessage(Contents.BLUETOORH_CHANGE_BOFANG_BUTTON);//改变暂停播放按钮 888

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;

                case MSG_PLAY_STATE:
                    String status = (String) msg.obj;
                    mPlayStatus = status.equals("" + true);
                    BaseUtils.mlog(TAG,"MSG_PLAY_STATE-------mPlayStatus-----"+mPlayStatus);

                    Handler mainhandler3 = MainActivity.getHandler();
                    mainhandler3.sendEmptyMessage(Contents.BLUETOORH_CHANGE_BOFANG_BUTTON);//改变暂停播放按钮 888

                    if ((mPlayStatus) && (outSoundisPlay == 2)) {  //只要音源在，并且是播放的状态就行
                        Intent intent4 = new Intent("Wedesign.action.ACTION_IN_AUDIO_TRACK");
                        mainContext.sendBroadcast(intent4);
                        Intent intent1 = new Intent("Wedesign.action.ACTION_IN_AUDIO_PLAY");
                        mainContext.sendBroadcast(intent1);
                    } else{
                        Intent intent3 = new Intent("Wedesign.action.ACTION_OUT_AUDIO_TRACK");
                        mainContext.sendBroadcast(intent3);
                    }
                    break;

                case MSG_NEXT_TRACK:
                    musicNext();
                    break;
                case MSG_PREV_TRACK:
                    musicPrevious();
                    break;
                case MSG_EXTEVENT_IN:  //表示源申请成功
                    BaseUtils.mlog(TAG, "MSG_EXTEVENT_IN");
                    break;
                case MSG_EXTEVENT_OUT: //表示源被抢了
                    BaseUtils.mlog(TAG, "MSG_EXTEVENT_OUT");
                    break;
                default:break;
            }
        }
    };

    public void initBT(){
        if(!BaseApp.isbindBTservice) {
            BaseUtils.mlog(TAG, "未绑定，初始化蓝牙。。。");
            Intent intent = new Intent("com.wedesign.bluetoothservice.BluetoothForApksService");
            mainContext.startService(intent);
            mainContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }else{
            BaseUtils.mlog(TAG, "已经绑定，初始化蓝牙。。。");
            getConnectState();  //绑定结束后，判定蓝牙连接状态
        }
    }
}
