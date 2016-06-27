// IMediaPlayerService.aidl
package com.wedesign.mediaplayerservice;
import com.wedesign.mediaplayerservice.IMediaPlayerCallback;
// Declare any non-default types here with import statements

interface IMediaPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallbackMP(IMediaPlayerCallback callback);		//---->
    void unregisterCallbackMP(IMediaPlayerCallback callback);	//---->
    void getBlueToothConnectState();											//---->onConnectState(boolean state);
}
