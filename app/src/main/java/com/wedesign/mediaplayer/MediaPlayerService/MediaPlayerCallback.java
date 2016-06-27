package com.wedesign.mediaplayer.MediaPlayerService;

import android.os.Handler;
import android.os.RemoteException;

import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayerservice.IMediaPlayerCallback;

/**
 * Created by NANA on 2016/6/21.
 */
public class MediaPlayerCallback  extends IMediaPlayerCallback.Stub{

    public MediaPlayerCallback(Handler handler) {
    }
    private static final String TAG = "MediaPlayerCallback";

    @Override
    public void onBlueToothConnectState(boolean ifconnected) throws RemoteException {
//        BaseApp.ifBluetoothConnected = ifconnected;
        BaseUtils.mlog(TAG,"onBlueToothConnectState----ifconnected----"+ifconnected);
    }
}
