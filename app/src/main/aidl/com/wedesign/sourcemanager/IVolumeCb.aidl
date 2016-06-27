package com.wedesign.sourcemanager;


interface IVolumeCb {
	void muteState(boolean mute);
	void volUpdate(byte mode, byte value);
}
