package com.wedesign.sourcemanager;


interface ISettingCb {
    void settingData(in byte[] buff);
    void mcuVersion(in byte[] buff);
    void mcuUpdateState(int state);
    void mcuUpdateProgress(int percent);
}
