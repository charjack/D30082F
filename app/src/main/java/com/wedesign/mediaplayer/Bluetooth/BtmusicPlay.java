package com.wedesign.mediaplayer.Bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
    public static final int MSG_UPDATE_BT_NAME = 421;
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
                service.registerBtMusicClassName("com.wedesign.mediaplayer");
                BaseApp.ifbtServiceBind = true;
                BaseUtils.mlog(TAG, "IBluetoothForApksService onServiceConnected !!!! finished!!!!!!!!!!!!!!!!! ");
             //   getConnectState();  //绑定结束后，判定蓝牙连接状态
            } catch (RemoteException e) {
                e.printStackTrace();
                service = null;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BaseApp.ifbtServiceBind = false;
            BaseUtils.mlog(TAG, "IBluetoothForApksService onServiceDisconnected-----------------------------finished ");
        }
    };

    public void musicPlayOrPause() {
        BaseUtils.mlog(TAG, "musicPlayOrPause");
        try {
            service.musicPlayPause();
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
            service.getConnectDeviceName();
            service.getMusicInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getDeviceName(){
        try {
            BaseUtils.mlog(TAG, "getDeviceName");
            service.getMusicInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sourceCtrl() {
        if(service == null){
            return;
        }
        try {
            BaseUtils.mlog(TAG,"sourceCtrl");
            service.musicInitStart();  //播放
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
                        BaseUtils.mlog(TAG, "BLUETOOTH_CONNECTED");
//                        BaseApp.ifBluetoothConnected = true;
                        Handler mainhandler = MainActivity.getHandler();
                        mainhandler.sendEmptyMessage(Contents.BLUETOOTH_CONNECTED);  //   776
                    }else{
                        BaseUtils.mlog(TAG, "BLUETOOTH_DISCONNECTED");
//                        BaseApp.ifBluetoothConnected = false;
                        Handler mainhandler = MainActivity.getHandler();
                        //将图标的按钮变暗，并且判断当前是否播放的蓝牙，如果是则需要切换fragment
                        mainhandler.sendEmptyMessage(Contents.BLUETOOTH_DISCONNECTED);  //   777
                    }
                    break;
                case MSG_MUSIC_INFO:  //音乐信息已经获取了
                    BaseUtils.mlog(TAG, "MSG_MUSIC_INFO");
                    txtName = ((MusicInfoEvent) msg.obj).name;
                    txtArtists = ((MusicInfoEvent) msg.obj).artist;
                    bt_position = ((MusicInfoEvent) msg.obj).pos;
                    bt_totalnum = ((MusicInfoEvent) msg.obj).total;
                    BaseUtils.mlog(TAG,"txtName:"+txtName+"--txtArtists:"+txtArtists);
                    Handler mainhandler = MainActivity.getHandler();
                    if (mainhandler != null) {
                        mainhandler.sendEmptyMessage(MSG_ACTIVITY_CHANGE);
                    }
                    break;
                case MSG_PLAY_STATE:
                    String status = (String) msg.obj;
                    mPlayStatus = status.equals("" + true);
                    BaseUtils.mlog(TAG,"MSG_PLAY_STATE-------mPlayStatus-----"+mPlayStatus);
                    break;
                case MSG_CONNECTED_NAME:
                    Handler mainhandler2 = MainActivity.getHandler();
                    if (mainhandler2 != null) {
                        mainhandler2.sendEmptyMessage(MSG_UPDATE_BT_NAME);
                    }
                    break;
                default:break;
            }
        }
    };

    public void initBT(){
        if(!BaseApp.ifbtServiceBind) {
            BaseUtils.mlog(TAG, "initBT。。。");
            BaseUtils.mlog(TAG, "初始化蓝牙。。。");
            Intent intent = new Intent("com.wedesign.bluetoothservice.BluetoothForApksService");
            intent.setPackage("com.wedesign.bluetoothservice");
            mainContext.startService(intent);
            mainContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }
}
