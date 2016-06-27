package com.wedesign.mediaplayer.Bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import com.wedesign.bluetoothservice.IBluetoothForApksCallback;
import com.wedesign.mediaplayer.BaseApp;
import com.wedesign.mediaplayer.Utils.BaseUtils;

/*
* 这个callback中一共发送的消息包括。连接状态的消息、播放状态的信息、音乐源的数据信息(id和状态)，获取的音乐源的信息、已连接的蓝牙的设备名称、下一曲控制消息、上一曲控制消息
*
* */

/**
 * Created by NANA on 2016/5/16.
 */
public class BluetoothCallback extends IBluetoothForApksCallback.Stub{
    private final String TAG = "BluetoothCallback";

    public BluetoothCallback(Handler handler) {
    }

    @Override
    public void onConnectState(boolean hfp_state, boolean a2dp_status) throws RemoteException {
//       第一次的蓝牙状态已经从服务中获取了，这里不需要再做第一次的判断了
//        if(BaseApp.ifFirstGetState){
//            BaseUtils.mlog(TAG, " first-------------onConnectState");//获取连接状态
//            BaseApp.ifFirstGetState = false;
//            BaseApp.ifBluetoothConnected = a2dp_status;
//        }else {
            BaseUtils.mlog(TAG, "onConnectState");//获取连接状态
        if(BaseApp.ifBluetoothConnected != a2dp_status) {  //切换系统语言时候，会重新调用activity的oncreat，会弹框
            BaseApp.ifBluetoothConnected = a2dp_status;
            Handler handler = BtmusicPlay.getHandler();
            if (handler == null)
                return;
            Message msg = handler.obtainMessage(BtmusicPlay.MSG_CONNECTED_STATE, "" + BaseApp.ifBluetoothConnected);
            handler.sendMessage(msg);
        }
//        }
    }

    @Override
    public void onFinishState(boolean state) throws RemoteException {

    }

    @Override
    public void onPlaystate(boolean state) throws RemoteException {  //状态改变的时候，或者service去查询的时候回调用

            Handler handler = BtmusicPlay.getHandler();
            if (handler == null)
                return;
            BaseUtils.mlog(TAG, "onPlaystate----" + state);  //返回当前播放状态 暂停？播放
            Message msg = handler.obtainMessage(BtmusicPlay.MSG_PLAY_STATE, "" + state);
            handler.sendMessage(msg);
    }

    @Override
    public void onPinCode(String codePin) throws RemoteException {

    }

    @Override
    public void onBluetoothName(String Name) throws RemoteException {
        BaseUtils.mlog(TAG, "onBluetoothName");  //返回当前播放状态 暂停？播放

        Handler handler = BtmusicPlay.getHandler();
        if (handler == null)
            return;
        Message msg = handler.obtainMessage(BtmusicPlay.MSG_CONNECTED_NAME, ""+Name);
        handler.sendMessage(msg);
    }

    @Override
    public void onBluetoothAddr(String Addr) throws RemoteException {

    }

    /*
    * id表示源的id
    * state 表示这个源是否丢失了， true表示丢失了，false表有
    * */
    @Override
    public void onSourceMassage(boolean state, byte id) throws RemoteException {
        BaseUtils.mlog(TAG, "onSourceMassage");  //是否有了音乐源，源发生变化，或者主动去获取源的时候会被调用

        state = !state;  //服务是这样定义的
        Handler handler = BtmusicPlay.getHandler();
        if (handler == null){
            BaseUtils.mlog(TAG, "onSourceMassage：handler == null");
            return;
        }
        Message msg = handler.obtainMessage(BtmusicPlay.MSG_HAVE_SOURCE);
        Bundle bundle = new Bundle();
        bundle.putBoolean("state", state);
        bundle.putByte("id", id);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void onMusicInfo(String name, String artist, int duration, int pos, int total) throws RemoteException {

        BaseUtils.mlog(TAG, "onMusicInfo");
        Handler handler = BtmusicPlay.getHandler();  //BtmusicPlay类中的hanler
        if (handler == null)
            return;
        handler.sendMessage(handler.obtainMessage(BtmusicPlay.MSG_MUSIC_INFO,
                new MusicInfoEvent(name, artist, duration, pos, total)));
    }

    @Override
    public void onDiscoveryDone() throws RemoteException {

    }

    @Override
    public void onDiscovery(String name, String addr) throws RemoteException {

    }

    @Override
    public void onPairMode(boolean state) throws RemoteException {

    }

    @Override
    public void onCurrentAndPairList(int index, String name, String addr) throws RemoteException {

    }

    @Override
    public void onCurrentAddr(String addr) throws RemoteException {

    }

    @Override
    public void onCurrentName(String name) throws RemoteException {
        BaseUtils.mlog(TAG,"btphone_name---"+ name);
            BaseApp.btphone_name = name;
    }

    @Override
    public void onAutoConnectStats(boolean state) throws RemoteException {

    }

    @Override
    public void onAutoConnectAccept(boolean autoConnect, boolean autoAccept) throws RemoteException {

    }

    @Override
    public void onHfpStatus(int status) throws RemoteException {

    }

    @Override
    public void onHfpRemote() throws RemoteException {

    }

    @Override
    public void onHfpLocal() throws RemoteException {

    }

    @Override
    public void onCallSucceed(String number) throws RemoteException {

    }

    @Override
    public void onHangUp() throws RemoteException {

    }

    @Override
    public void onTalking(String number) throws RemoteException {

    }

    @Override
    public void onCalllog(int type, String number) throws RemoteException {

    }

    @Override
    public void onCalllogDone() throws RemoteException {

    }

    @Override
    public void onPhoneBook(String name, String number) throws RemoteException {

    }

    @Override
    public void onPhoneBookDone() throws RemoteException {

    }

    @Override
    public void onIncoming(String number) throws RemoteException {

    }

    @Override
    public void onRingStart() throws RemoteException {

    }

    @Override
    public void onRingStop() throws RemoteException {

    }

    @Override
    public void onInPairMode() throws RemoteException {

    }

    @Override
    public void onExitPairMode() throws RemoteException {

    }

    @Override
    public void onCurrentDeviceName(String name) throws RemoteException {

    }

    @Override
    public void onSimBook(String name, String number) throws RemoteException {

    }

    @Override
    public void onSimDone() throws RemoteException {

    }

    @Override
    public void onNextTrack() throws RemoteException {
        BaseUtils.mlog(TAG, "onNextTrack");

        Handler handler = BtmusicPlay.getHandler();
        if (handler == null)
            return;
        handler.sendEmptyMessage(BtmusicPlay.MSG_NEXT_TRACK);
    }

    @Override
    public void onPrevTrack() throws RemoteException {
        BaseUtils.mlog(TAG, "onPrevTrack");

        Handler handler = BtmusicPlay.getHandler();
        if (handler == null)
            return;
        handler.sendEmptyMessage(BtmusicPlay.MSG_PREV_TRACK);
    }

    @Override
    public void onExtEventIn() throws RemoteException {
        BaseUtils.mlog(TAG, "onExtEventIn");

        Handler handler = BtmusicPlay.getHandler();
        if (handler == null)
            return;
        handler.sendEmptyMessage(BtmusicPlay.MSG_EXTEVENT_IN);
    }

    @Override
    public void onExtEventOut() throws RemoteException {
        BaseUtils.mlog(TAG, "onExtEventOut");

        Handler handler = BtmusicPlay.getHandler();
        if (handler == null)
            return;
        handler.sendEmptyMessage(BtmusicPlay.MSG_EXTEVENT_OUT);
    }
}
