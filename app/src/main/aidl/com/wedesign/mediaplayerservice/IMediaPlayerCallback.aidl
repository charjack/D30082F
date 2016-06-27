// IMediaPlayerCallback.aidl
package com.wedesign.mediaplayerservice;

// Declare any non-default types here with import statements

interface IMediaPlayerCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onBlueToothConnectState(boolean ifconnected);
}
