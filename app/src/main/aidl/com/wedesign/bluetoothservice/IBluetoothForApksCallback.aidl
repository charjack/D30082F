package com.wedesign.bluetoothservice;


interface IBluetoothForApksCallback {
   	void onConnectState(boolean hfp_state,boolean a2dp_status);
	void onFinishState(boolean state);
	void onPlaystate(boolean state);
	void onPinCode(String codePin);
	void onBluetoothName(String Name);
	void onBluetoothAddr(String Addr);
	void onSourceMassage(boolean state,byte id);
	void onMusicInfo(String name, String artist, int duration, int pos,int total); 
	void onDiscoveryDone();
	void onDiscovery(String name, String addr);
	void onPairMode(boolean state);
	void onCurrentAndPairList(int index, String name, String addr);
	void onCurrentAddr(String addr);
	void onCurrentName(String name);
	void onAutoConnectStats(boolean state);
	
	void onAutoConnectAccept (boolean autoConnect,boolean autoAccept);
	void onHfpStatus (int status);
	void onHfpRemote ();
	void onHfpLocal ();
	void onCallSucceed (String number);
	void onHangUp ();
	void onTalking (String number);
	void onCalllog (int type,String number);
	void onCalllogDone ();
	void onPhoneBook (String name,String number);
	void onPhoneBookDone ();
	void onIncoming(String number);
	void onRingStart();
	void onRingStop();
	void onInPairMode();
	void onExitPairMode();
	void onCurrentDeviceName(String name);
	void onSimBook(String name, String number) ;
	void onSimDone() ;
	
	void onNextTrack();
	void onPrevTrack();
	
	void onExtEventIn();
	void onExtEventOut();
	
	
	
}