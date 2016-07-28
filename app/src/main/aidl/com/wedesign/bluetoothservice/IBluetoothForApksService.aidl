package com.wedesign.bluetoothservice;
import com.wedesign.bluetoothservice.IBluetoothForApksCallback;

interface IBluetoothForApksService{
	void registerCallback(IBluetoothForApksCallback callback);		//---->
	void unregisterCallback(IBluetoothForApksCallback callback);	//---->
	void getConnectState();											//---->onConnectState(boolean state);
	void getInitState();											//---->onFinishState(boolean state);
	boolean getPlaystate();											//----> onPlaystate(boolean state);
	void getPinCode();												//---->onPinCode(String codePin);
	void getLocalDeviceName();										//---->onBluetoothName(String Name);
	void getLocalDeviceAddr();										//----->onBluetoothAddr(String addr);
	void musicInitStart();						
	void musicNext();												//---->onMusicInfo(....); 
	void musicPrevious();											//---->onMusicInfo(...); 
	void getMusicInfo();											//---->onMusicInfo(...); 
	void musicPlayPause();										//---->onPlaystate(boolean state);
	void registerBtMusicClassName(String name);
	
	
	
	//Settings
	void stopSearchDecive();											//---->onDiscoveryDone()
	void searchDevice();											//---->onDiscovery()  //---->onDiscoveryDone()
	void cancelPairMode();										
	void setPairMode();												
	void setDevicename(String name);									//---->onBluetoothName(String Name);
	void getPairList();												//---->onCurrentAndPairList()
	void deletePair(String index);									
	void getConnectDeviceAddr();									//---->onCurrentAddr()
	void getConnectDeviceName();									//---->onCurrentName()
	void disconnect();												//---->onConnectState(boolean state);
	void connectDevice(String addr);			//---->onConnectState(boolean state);
	
	//Phone
	void cancelAutoAnswer();										//---->onAutoConnectAccept()
	void phoneTransfer();											//---->onHfpRemote()
	void phoneTransferBack();										//---->onHfpLocal()
	void phoneDail(String number);									//---->onCallSucceed()
	void phoneHangUp();												//---->onHangUp()
	void phoneAnswer();												//---->onTalking()
	void phoneTransmitDTMFCode(char code);							
	void callHistoryStartUpdate(int type);								//---->onCalllog(int type, String number) onCalllogDone() 
	void phoneBookStartUpdate();									//---->onPhoneBook() onPhoneBookDone()
	void simBookStartUpdata();
	
	void getBluetoothVersion();
	void setMicMute(boolean mute);
	
	//<<pdi only
	void setDialActivityStatus(boolean show);
	void setCallActivityStatus(boolean show);
	//>>end
	void getConnectingStatus();
	
	void getContactSyncState();
	void getContactSyncData();
	void setContactSyncState(boolean isOpen);
}