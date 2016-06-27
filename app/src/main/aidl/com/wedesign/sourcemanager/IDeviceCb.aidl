package com.wedesign.sourcemanager;


interface IDeviceCb {
    void disc(boolean devIn);
    void reverse(boolean on);
    void standby(boolean on);
    void displayCar(boolean on);	//true is CAR, false is ARM(Android)
    	void backlight(boolean on);	//true is off, false is on.
    	void on3GBusy(boolean on);
    	void onParkingBrake(boolean on);
}
