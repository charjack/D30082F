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
        BaseUtils.mlog(TAG, "onConnectState-----"+a2dp_status);//获取连接状态
        if(BaseApp.ifBluetoothConnected != a2dp_status) {  //切换系统语言时候，会重新调用activity的oncreat，会弹框
            BaseUtils.mlog(TAG, "onConnectState-----不同的连接状态");//获取连接状态
            BaseApp.ifBluetoothConnected = a2dp_status;
            Handler handler = BtmusicPlay.getHandler();
            if (handler == null)
                return;
            Message msg = handler.obtainMessage(BtmusicPlay.MSG_CONNECTED_STATE, "" + BaseApp.ifBluetoothConnected);
            handler.sendMessage(msg);
        }else{
            BaseUtils.mlog(TAG, "onConnectState-----相同的连接状态");//获取连接状态
        }
    }

    @Override
    public void onInitState(boolean state) throws RemoteException {

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
        BaseUtils.mlog(TAG, "onBluetoothName----"+Name);  //
    }

    @Override
    public void onBluetoothAddr(String Addr) throws RemoteException {

    }

    @Override
    public void onMusicInfo(String name, String artist, int duration, int pos, int total) throws RemoteException {

        BaseUtils.mlog(TAG, "onMusicInfo  name"+name+ "----"+artist);
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
    public void onCurrentAndPairList(int index, String name, String addr) throws RemoteException {

    }

    @Override
    public void onCurrentAddr(String addr) throws RemoteException {

    }

    @Override
    public void onCurrentName(String name) throws RemoteException {
        BaseUtils.mlog(TAG,"btphone_name---"+ name);
        BaseApp.btphone_name = name;
        Handler handler = BtmusicPlay.getHandler();  //BtmusicPlay类中的hanler
        if (handler == null)
            return;
        handler.sendEmptyMessage(BtmusicPlay.MSG_CONNECTED_NAME);
    }

    @Override
    public void onAutoConnectAccept(boolean autoConnect, boolean autoAccept) throws RemoteException {

    }

    @Override
    public void onHfpRemote() throws RemoteException {

    }

    @Override
    public void onHfpLocal() throws RemoteException {

    }

    @Override
    public void onCallSucceeded(String number) throws RemoteException {

    }

    @Override
    public void onHangUp() throws RemoteException {

    }

    @Override
    public void onTalking(String number) throws RemoteException {

    }

    @Override
    public void onCallHistory(int type, String number) throws RemoteException {

    }

    @Override
    public void onCallHistoryDone() throws RemoteException {

    }

    @Override
    public void onPhoneBook(String name, String number) throws RemoteException {

    }

    @Override
    public void onPhoneBookDone() throws RemoteException {

    }

    @Override
    public void onIncomingCall(String number) throws RemoteException {

    }


    @Override
    public void onInPairMode() throws RemoteException {

    }

    @Override
    public void onExitPairMode() throws RemoteException {

    }

    @Override
    public void onFinishCallActivity() throws RemoteException {

    }

    @Override
    public void onSdkVersion(String version) throws RemoteException {

    }

    @Override
    public void onConnectingStatus(boolean isConnecting) throws RemoteException {

    }

    @Override
    public void onContactSyncState(int state) throws RemoteException {

    }

    @Override
    public void onContactSyncData(String data) throws RemoteException {

    }

    @Override
    public void onCallKeyInput() throws RemoteException {

    }

    @Override
    public void onSimBook(String name, String number) throws RemoteException {

    }

    @Override
    public void onSimDone() throws RemoteException {

    }

}
