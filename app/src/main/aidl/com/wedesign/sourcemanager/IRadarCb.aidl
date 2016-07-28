package com.wedesign.sourcemanager;


interface IRadarCb
{
	void onData(in byte[] buff);
	void onFrontData(in byte[] buff);
}