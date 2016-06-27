package com.wedesign.bluetoothservice;
import com.wedesign.bluetoothservice.IBluetoothForApksCallback;

interface IBluetoothForApksService{
	void registerCallback(IBluetoothForApksCallback callback);		//---->
	void unregisterCallback(IBluetoothForApksCallback callback);	//---->
	void getConnectState();											//---->onConnectState(boolean state);
	void getFinishState();											//---->onFinishState(boolean state);
	boolean getPlaystate();											//----> onPlaystate(boolean state);
	void getPinCode();												//---->onPinCode(String codePin);

	void getBluetoothName();										//---->onBluetoothName(String Name);
	void getBluetoothAddr();										//----->onBluetoothAddr(String addr);
	void sourceCtrl(String action, byte from);						//---->onSourceMassage(boolean state,byte id);
	void musicNext();												//---->onMusicInfo(....); 
	void musicPrevious();											//---->onMusicInfo(...); 
	void getMusicInfo();											//---->onMusicInfo(...); 
	void musicPlayOrPause();										//---->onPlaystate(boolean state);
	
	
	
	//Settings
	void stopSearchDecive();											//---->onDiscoveryDone()
	void searchDevice();											//---->onDiscovery()
	void cancelPairMode();											//---->onPairMode(boolean state)
	void setPairMode();												//---->onPairMode(boolean state)
	void setDevicename(String name);									//---->onBluetoothName(String Name);
	void getPairList();												//---->onCurrentAndPairList()
	void deletePair(String index);									
	void getCurrentDeviceAddr();									//---->onCurrentAddr()
	void getCurrentDeviceName();									//---->onCurrentName()
	void disconnect();												//---->onConnectState(boolean state);
	void getAutoConnectAnswer();									//---->onAutoConnectStats();
	void setAutoConnect();											//---->onAutoConnectStats();
	void cancelAutoConnect();										//---->onAutoConnectStats();
	void connectDevice(String addr);
	//Phone
	void cancelAutoAnswer();										//---->onAutoConnectAccept()
	void inqueryHfpStatus();										//---->onHfpStatus()
	void phoneTransfer();											//---->onHfpRemote()
	void phoneTransferBack();										//---->onHfpLocal()
	void phoneDail(String number);									//---->onCallSucceed()
	void phoneHangUp();												//---->onHangUp()
	void phoneAnswer();												//---->onTalking()
	void phoneTransmitDTMFCode(char code);							
	void callLogstartUpdate(int type);								//---->onCalllog(int type, String number) onCalllogDone() 
	void phoneBookStartUpdate();									//---->onPhoneBook() onPhoneBookDone()
	
	void showNotification(String name,String artists) ;
	void cancelNotification();
}