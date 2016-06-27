package com.wedesign.sourcemanager;


import  com.wedesign.sourcemanager.ISrcCtrlCb;
import  com.wedesign.sourcemanager.IVolumeCb;
import  com.wedesign.sourcemanager.IRadioCb;
import  com.wedesign.sourcemanager.IInputCb;
import  com.wedesign.sourcemanager.IDeviceCb;
import  com.wedesign.sourcemanager.ISettingCb;
import  com.wedesign.sourcemanager.IRadarCb;
import  com.wedesign.sourcemanager.IDVRCb;
import  com.wedesign.sourcemanager.IPanoCameraCb;

interface ISourceManagerService
{
	void registerApp(byte appId);
	void unregisterApp(byte appId);
	void registerKey(byte appId, in byte[] keys);
	void unregisterKey(byte appId);
	
	void baseCmd(byte appId, byte subCmd);
	void sourceReq(byte appId, byte appIdReq);
	void uiccCmd(byte appId, byte uicc, byte par);
	void usrInput(byte appId, byte sCmd, byte par);
	
	void sendRadioData(byte appId, in byte[] data);
	void setRadioFreq(byte appId, int freq);
	void sendSettingData(byte appId, in byte[] data);
	void setAudio(byte appId, byte idx, byte par);
	void sendSysData(byte appId, in byte[] data);
	void sendTouch(byte appId, char x, char y, byte op);


	int getWorkMode();
	byte[] getVolume();
	byte[] getMcuVersion();


	int getData(byte appId, int idx);
	byte[] getDataBuff(int idx);

	byte getBrightness();
	void setBrightness(byte value);

	byte[] getDeviceStatus();

	void registerSrcCtrlCb(ISrcCtrlCb cb, byte appId);
	void unregisterSrcCtrlCb(ISrcCtrlCb cb, byte appId);

	void registerVolumeCb(IVolumeCb cb, byte appId);
	void unregisterVolumeCb(IVolumeCb cb, byte appId);

	void registerRadioCb(IRadioCb cb, byte appId);
	void unregisterRadioCb(IRadioCb cb, byte appId);

	void registerInputCb(IInputCb cb, byte appId);
	void unregisterInputCb(IInputCb cb, byte appId);

	void registerDeviceCb(IDeviceCb cb, byte appId);
	void unregisterDeviceCb(IDeviceCb cb, byte appId);

	void registerSettingCb(ISettingCb cb, byte appId);
	void unregisterSettingCb(ISettingCb cb, byte appId);

	void registerRadarCb(IRadarCb cb, byte appId);
	void unregisterRadarCb(IRadarCb cb, byte appId);
	
	void registerDVRCb(IDVRCb cb, byte appId);
	void unregisterDVRCb(IDVRCb cb, byte appId);
	void writeDVR(in byte[] data);
	
	void registerPanoCameraCb(IPanoCameraCb cb, byte appId);
	void unregisterPanoCameraCb(IPanoCameraCb cb, byte appId);
	void writePanoCamera(in byte[] data);
}

