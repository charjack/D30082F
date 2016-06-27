package com.wedesign.mediaplayer.MediaPlayerService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.wedesign.mediaplayer.BaseApp;
import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayerservice.IMediaPlayerCallback;
import com.wedesign.mediaplayerservice.IMediaPlayerService;

/**
 * Created by NANA on 2016/6/21.
 */
public class MPbtState {
    private static final String TAG = "MPbtState";
    private static IMediaPlayerCallback callbackMP = null;
    public static IMediaPlayerService serviceMP = null;
    private Context mainContext = null;
    private volatile static MPbtState instanceMP = null;


    private MPbtState(Context context, Handler handler){
        mainContext = context;
        callbackMP = new MediaPlayerCallback(handler);
    }

    public static MPbtState getInstance(Context context, Handler handler){
        if(instanceMP == null){
            synchronized (MPbtState.class) {
                if (instanceMP == null){
                    instanceMP = new MPbtState(context,handler);
                }
            }
        }
        instanceMP.mainContext = context;
        return instanceMP;
    }

    public void initBTMP(){
        if(!BaseApp.isbindBTserviceMP) {
            BaseUtils.mlog(TAG, "initBTMP");
            Intent intentMP = new Intent("com.wedesign.mediaplayerservice.MediaPlayerService");
            mainContext.startService(intentMP);

            mainContext.bindService(intentMP, connectionMP, Context.BIND_AUTO_CREATE);
        }
    }


    private ServiceConnection connectionMP = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serv) {
            BaseUtils.mlog(TAG, "onServiceConnected");
            serviceMP = IMediaPlayerService.Stub.asInterface(serv);

            if(serviceMP == null){
                BaseUtils.mlog(TAG, "serviceMP is null");
            }
            if(callbackMP == null){
                BaseUtils.mlog(TAG, "callbackMP is null");
            }
            try {
                serviceMP.registerCallbackMP(callbackMP);
                getBlueToothConnectState();  //绑定结束后，判定蓝牙连接状态
            } catch (RemoteException e) {
                e.printStackTrace();
                serviceMP = null;
            }
            BaseApp.isbindBTserviceMP = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BaseUtils.mlog(TAG, "onServiceDisconnected");
            BaseApp.isbindBTserviceMP = false;
        }
    };


    public void getBlueToothConnectState() {
        try {
            BaseUtils.mlog(TAG, "getBlueToothConnectState");
            serviceMP.getBlueToothConnectState();  //触发callback回调
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
