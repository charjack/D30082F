package com.wedesign.bluetoothservice;


interface IBluetoothForApksCallback {
   	void onConnectState(boolean hfp_state,boolean a2dp_status);
	void onInitState(boolean state);
	void onPlaystate(boolean state);
	void onPinCode(String codePin);
	void onBluetoothName(String Name);
	void onBluetoothAddr(String Addr);
	void onMusicInfo(String name, String artist, int duration, int pos,int total); 
	void onDiscoveryDone();
	void onDiscovery(String name, String addr);
	void onCurrentAndPairList(int index, String name, String addr);
	void onCurrentAddr(String addr);
	void onCurrentName(String name);
	
	void onAutoConnectAccept (boolean autoConnect,boolean autoAccept);
	void onHfpRemote ();
	void onHfpLocal ();
	void onCallSucceeded (String number);
	void onHangUp ();
	void onTalking (String number);
	void onCallHistory (int type,String number);
	void onCallHistoryDone ();
	void onPhoneBook (String name,String number);
	void onPhoneBookDone ();
	void onIncomingCall(String number);
	void onSimBook(String name, String number) ;
	void onSimDone() ;	
	
	void onInPairMode();
	void onExitPairMode();
	
	void onFinishCallActivity();
	void onSdkVersion(String version);
	void onConnectingStatus(boolean isConnecting);
	
	void onContactSyncState(int state);
	void onContactSyncData(String data);
	void onCallKeyInput();
}