package com.wedesign.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.vo.KEY;
import com.wedesign.mediaplayer.vo.SMCmd;
import com.wedesign.sourcemanager.IDeviceCb;
import com.wedesign.sourcemanager.IInputCb;
import com.wedesign.sourcemanager.ISourceManagerService;
import com.wedesign.sourcemanager.ISrcCtrlCb;


public class SourceManager {
	public interface OnSourceManagerListener {
		void onSrcIn(int n);  //n 用来判断是哪个设备
		void onSrcOut(int n);
		void onNextTrack(int n);
		void onPrevouseTrack(int n);
		void onExtEventIn(int n);
		void onExtEventOut(int n);
		void onBrake(boolean flag,int n);
	}

	private static final String TAG = "SoruceManager";
	private static final byte appIdAUX = SMCmd.APP_ID_AUX;
	private static final byte appIdMUSIC= SMCmd.APP_ID_USB;
	private static final byte appIdBT = SMCmd.APP_ID_BT_MUSIC;
	private static final byte appIdVIDEO = SMCmd.APP_ID_HDD;
	private ISourceManagerService mService = null;
	private final Context mContext;
	private volatile static SourceManager mInstance = null;

	private static OnSourceManagerListener mListener = null;

	private static final int CHECK_SERVICE_CONNECTION = 1110;
	private static final int SRC_IN_MUSIC = 1101;
	private static final int SRC_OUT_MUSIC = 1102;
	private static final int NEXT_TRACK_MUSIC = 1103;
	private static final int PREV_TRACK_MUSIC = 1104;
	private static final int SCMD_EXT_EVENT_IN_MUSIC = 1105;
	private static final int SCMD_EXT_EVENT_OUT_MUSIC = 1106;
	private static final int BRAKE_MSG_ON_MUSIC = 1107;
	private static final int BRAKE_MSG_OFF_MUSIC = 1108;

	private static final int SRC_IN_BT = 1111;
	private static final int SRC_OUT_BT = 1112;
	private static final int NEXT_TRACK_BT = 1113;
	private static final int PREV_TRACK_BT = 1114;
	private static final int SCMD_EXT_EVENT_IN_BT = 1115;
	private static final int SCMD_EXT_EVENT_OUT_BT = 1116;
	private static final int BRAKE_MSG_ON_BT = 1117;
	private static final int BRAKE_MSG_OFF_BT = 1118;

	private static final int SRC_IN_AUX = 1121;
	private static final int SRC_OUT_AUX = 1122;
	private static final int NEXT_TRACK_AUX = 1123;
	private static final int PREV_TRACK_AUX = 1124;
	private static final int SCMD_EXT_EVENT_IN_AUX = 1125;
	private static final int SCMD_EXT_EVENT_OUT_AUX = 1126;
	private static final int BRAKE_MSG_ON_AUX = 1127;
	private static final int BRAKE_MSG_OFF_AUX = 1128;

	private static final int SRC_IN_VIDEO = 1131;
	private static final int SRC_OUT_VIDEO = 1132;
	private static final int NEXT_TRACK_VIDEO = 1133;
	private static final int PREV_TRACK_VIDEO = 1134;
	private static final int SCMD_EXT_EVENT_IN_VIDEO = 1135;
	private static final int SCMD_EXT_EVENT_OUT_VIDEO = 1136;
	private static final int BRAKE_MSG_ON_VIDEO = 1137;
	private static final int BRAKE_MSG_OFF_VIDEO = 1138;

	private SourceManager(Context context) {
		this.mContext = context;
		this.bindService();
	}

	public static SourceManager getInstance(Context context) {
		if (mInstance == null) {
			synchronized (SourceManager.class) {
				if (mInstance == null) {
					mInstance = new SourceManager(context);
				}
			}
		}
		return mInstance;
	}

	public void setListener(OnSourceManagerListener listener) {
		mListener = listener;
	}

	public void bindService() {
		Intent intent = new Intent("com.wedesign.sourcemanager.ISourceManagerService");
		intent.setPackage("com.wedesign.sourcemanager");
		mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		mHandler.sendEmptyMessageDelayed(CHECK_SERVICE_CONNECTION, 500);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case CHECK_SERVICE_CONNECTION:
				if(mService == null){
					bindService();
				}
				break;
			default:
				break;
			}

			if (mListener == null) {
				BaseUtils.mlog(TAG, "return as mListener == null in Handler");
				return;
			}

			switch (msg.what) {
//usb
				case SRC_IN_MUSIC:
					BaseUtils.mlog(TAG, "SRC_IN_MUSIC");
					mListener.onSrcIn(0);
					break;
				case SRC_OUT_MUSIC:
					BaseUtils.mlog(TAG, "SRC_OUT_MUSIC");
					mListener.onSrcOut(0);
					break;
				case NEXT_TRACK_MUSIC:
					BaseUtils.mlog(TAG, "NEXT_TRACK_MUSIC");
					mListener.onNextTrack(0);
					break;
				case PREV_TRACK_MUSIC:
					BaseUtils.mlog(TAG, "PREV_TRACK_MUSIC");
					mListener.onPrevouseTrack(0);
					break;
				case SCMD_EXT_EVENT_IN_MUSIC:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_IN_MUSIC");
					mListener.onExtEventIn(0);
					break;
				case SCMD_EXT_EVENT_OUT_MUSIC:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_OUT_MUSIC");
					mListener.onExtEventOut(0);
					break;
				case BRAKE_MSG_ON_MUSIC:  //播放视频
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_ON_MUSIC message");
					mListener.onBrake(true, 0);
					break;
				case BRAKE_MSG_OFF_MUSIC:
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_OFF_MUSIC message");
					mListener.onBrake(false, 0);
					break;

//BT
				case SRC_IN_BT:
					BaseUtils.mlog(TAG, "SRC_IN_BT");
					mListener.onSrcIn(1);
					break;
				case SRC_OUT_BT:
					BaseUtils.mlog(TAG, "SRC_OUT_BT");
					mListener.onSrcOut(1);
					break;
				case NEXT_TRACK_BT:
					BaseUtils.mlog(TAG, "NEXT_TRACK_BT");
					mListener.onNextTrack(1);
					break;
				case PREV_TRACK_BT:
					BaseUtils.mlog(TAG, "PREV_TRACK_BT");
					mListener.onPrevouseTrack(1);
					break;
				case SCMD_EXT_EVENT_IN_BT:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_IN_BT");
					mListener.onExtEventIn(1);
					break;
				case SCMD_EXT_EVENT_OUT_BT:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_OUT_BT");
					mListener.onExtEventOut(1);
					break;
				case BRAKE_MSG_ON_BT:  //播放视频
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_ON_BT message");
					mListener.onBrake(true, 1);
					break;
				case BRAKE_MSG_OFF_BT:
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_OFF_BT message");
					mListener.onBrake(false, 1);
					break;

//AUX
				case SRC_IN_AUX:
					BaseUtils.mlog(TAG, "SRC_IN_AUX");
					mListener.onSrcIn(2);
					break;
				case SRC_OUT_AUX:
					BaseUtils.mlog(TAG, "SRC_OUT_AUX");
					mListener.onSrcOut(2);
					break;
				case NEXT_TRACK_AUX:
					BaseUtils.mlog(TAG, "NEXT_TRACK_AUX");
					mListener.onNextTrack(2);
					break;
				case PREV_TRACK_AUX:
					BaseUtils.mlog(TAG, "PREV_TRACK_AUX");
					mListener.onPrevouseTrack(2);
					break;
				case SCMD_EXT_EVENT_IN_AUX:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_IN_AUX");
					mListener.onExtEventIn(2);
					break;
				case SCMD_EXT_EVENT_OUT_AUX:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_OUT_AUX");
					mListener.onExtEventOut(2);
					break;
				case BRAKE_MSG_ON_AUX:  //播放视频
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_ON_AUX message");
					mListener.onBrake(true, 2);
					break;
				case BRAKE_MSG_OFF_AUX:
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_OFF_AUX message");
					mListener.onBrake(false, 2);
					break;

//SD
				case SRC_IN_VIDEO:
					BaseUtils.mlog(TAG, "SRC_IN_VIDEO");
					mListener.onSrcIn(3);
					break;
				case SRC_OUT_VIDEO:
					BaseUtils.mlog(TAG, "SRC_OUT_VIDEO");
					mListener.onSrcOut(3);
					break;
				case NEXT_TRACK_VIDEO:
					BaseUtils.mlog(TAG, "NEXT_TRACK_VIDEO");
					mListener.onNextTrack(3);
					break;
				case PREV_TRACK_VIDEO:
					BaseUtils.mlog(TAG, "PREV_TRACK_VIDEO");
					mListener.onPrevouseTrack(3);
					break;
				case SCMD_EXT_EVENT_IN_VIDEO:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_IN_VIDEO");
					mListener.onExtEventIn(3);
					break;
				case SCMD_EXT_EVENT_OUT_VIDEO:
					BaseUtils.mlog(TAG, "SCMD_EXT_EVENT_OUT_VIDEO");
					mListener.onExtEventOut(3);
					break;
				case BRAKE_MSG_ON_VIDEO:  //播放视频
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_ON_VIDEO message");
					mListener.onBrake(true, 3);
					break;
				case BRAKE_MSG_OFF_VIDEO:
					BaseUtils.mlog(TAG, "-----receive the BRAKE_MSG_OFF_VIDEO message");
					mListener.onBrake(false, 3);
					break;
				default:
					BaseUtils.mlog(TAG, "-----receive default message");
					break;
			}
		}
	};

	private ISrcCtrlCb.Stub mSrcCtrl_MUSIC = new ISrcCtrlCb.Stub() {

		@Override
		public void srcIn() throws RemoteException {
			BaseUtils.mlog(TAG, "srcIn_MUSIC");
			mHandler.sendEmptyMessage(SRC_IN_MUSIC);
		}

		@Override
		public void srcOut() throws RemoteException {
			BaseUtils.mlog(TAG, "srcOut_MUSIC");
			mHandler.sendEmptyMessage(SRC_OUT_MUSIC);
		}

		@Override
		public void baseCtrl(int cmd) throws RemoteException {
			if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_IN) {
				BaseUtils.mlog(TAG, "SCMD_KEY_EXT_EVENT_IN+++++++++++++++++++++_MUSIC");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_IN_MUSIC);
			} else if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_OUT) {
				BaseUtils.mlog(TAG,"SCMD_KEY_EXT_EVENT_OUT+++++++++++++++++++++_MUSIC");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_OUT_MUSIC);
			} else if (cmd == SMCmd.SCMD_KEY_SUSPEND) {
				BaseUtils.mlog(TAG, "SCMD_KEY_SUSPEND_MUSIC");
			}
		}

	};

	private IInputCb.Stub inputCb_MUSIC = new IInputCb.Stub() {

		@Override
		public void key(int key) throws RemoteException {
			if(BaseApp.playSourceManager == 0) {
				switch (key) {
					case KEY.NEXT_TRACK:
						BaseUtils.mlog(TAG, "NEXT_TRACK_MUSIC");
						mHandler.sendEmptyMessage(NEXT_TRACK_MUSIC);
						break;
					case KEY.PREV_TRACK:
						BaseUtils.mlog(TAG, "PREV_TRACK_MUSIC");
						mHandler.sendEmptyMessage(PREV_TRACK_MUSIC);
						break;
					default:
						break;
				}
			}
		}
	};

	private IDeviceCb.Stub deviceCb_MUSIC = new IDeviceCb.Stub(){

		@Override
		public void disc(boolean devIn) throws RemoteException {

		}

		@Override
		public void reverse(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the reverse message-_MUSIC");
		}

		@Override
		public void standby(boolean on) throws RemoteException {

		}

		@Override
		public void displayCar(boolean on) throws RemoteException {

		}

		@Override
		public void backlight(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the backlight message-_MUSIC");
		}

		@Override
		public void on3GBusy(boolean on) throws RemoteException {

		}

		@Override
		public void onParkingBrake(boolean on) throws RemoteException {
			if(BaseApp.playSourceManager == 0) {
				BaseUtils.mlog(TAG, "onParkingBrakeUSB");
				if (BaseApp.brake_flag != on) {
					BaseApp.brake_flag = on;
					if (BaseApp.brake_flag) {
						mHandler.sendEmptyMessage(BRAKE_MSG_ON_MUSIC);
					} else {
						mHandler.sendEmptyMessage(BRAKE_MSG_OFF_MUSIC);
					}
				}
			}
		}

		@Override
		public void carLightState(int num) throws RemoteException {

		}

		@Override
		public void carSpeed(int num) throws RemoteException {

		}

		@Override
		public void carAngle(int num) throws RemoteException {

		}
	};

	private ISrcCtrlCb.Stub mSrcCtrl_BT = new ISrcCtrlCb.Stub() {

		@Override
		public void srcIn() throws RemoteException {
			BaseUtils.mlog(TAG, "srcIn_BT");
			mHandler.sendEmptyMessage(SRC_IN_BT);
		}

		@Override
		public void srcOut() throws RemoteException {
			BaseUtils.mlog(TAG, "srcOut_BT");
			mHandler.sendEmptyMessage(SRC_OUT_BT);
		}

		@Override
		public void baseCtrl(int cmd) throws RemoteException {
			if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_IN) {
				BaseUtils.mlog(TAG, "SCMD_KEY_EXT_EVENT_IN+++++++++++++++++++++_BT");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_IN_BT);
			} else if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_OUT) {
				BaseUtils.mlog(TAG,"SCMD_KEY_EXT_EVENT_OUT+++++++++++++++++++++_BT");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_OUT_BT);
			} else if (cmd == SMCmd.SCMD_KEY_SUSPEND) {
				BaseUtils.mlog(TAG, "SCMD_KEY_SUSPEND_BT");
			}
		}

	};

	private IInputCb.Stub inputCb_BT = new IInputCb.Stub() {

		@Override
		public void key(int key) throws RemoteException {
			if(BaseApp.playSourceManager == 1) {
				switch (key) {
					case KEY.NEXT_TRACK:
						mHandler.sendEmptyMessage(NEXT_TRACK_BT);
						break;
					case KEY.PREV_TRACK:
						mHandler.sendEmptyMessage(PREV_TRACK_BT);
						break;
					default:
						break;
				}
			}
		}
	};

	private IDeviceCb.Stub deviceCb_BT = new IDeviceCb.Stub(){

		@Override
		public void disc(boolean devIn) throws RemoteException {

		}

		@Override
		public void reverse(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the reverse message-_BT");
		}

		@Override
		public void standby(boolean on) throws RemoteException {

		}

		@Override
		public void displayCar(boolean on) throws RemoteException {

		}

		@Override
		public void backlight(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the backlight message-_BT");
		}

		@Override
		public void on3GBusy(boolean on) throws RemoteException {

		}

		@Override
		public void onParkingBrake(boolean on) throws RemoteException {
			if(BaseApp.playSourceManager == 1) {
				BaseUtils.mlog(TAG, "onParkingBrakebt");
				if (BaseApp.brake_flag != on) {
					BaseApp.brake_flag = on;
					if (BaseApp.brake_flag) {
						mHandler.sendEmptyMessage(BRAKE_MSG_ON_BT);
					} else {
						mHandler.sendEmptyMessage(BRAKE_MSG_OFF_BT);
					}
				}
			}
		}

		@Override
		public void carLightState(int num) throws RemoteException {

		}

		@Override
		public void carSpeed(int num) throws RemoteException {

		}

		@Override
		public void carAngle(int num) throws RemoteException {

		}
	};

	private ISrcCtrlCb.Stub mSrcCtrl_AUX = new ISrcCtrlCb.Stub() {

		@Override
		public void srcIn() throws RemoteException {
			BaseUtils.mlog(TAG, "srcIn_AUX");
			mHandler.sendEmptyMessage(SRC_IN_AUX);
		}

		@Override
		public void srcOut() throws RemoteException {
			BaseUtils.mlog(TAG, "srcOut_AUX");
			mHandler.sendEmptyMessage(SRC_OUT_AUX);
		}

		@Override
		public void baseCtrl(int cmd) throws RemoteException {
			if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_IN) {
				BaseUtils.mlog(TAG, "SCMD_KEY_EXT_EVENT_IN+++++++++++++++++++++_AUX");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_IN_AUX);
			} else if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_OUT) {
				BaseUtils.mlog(TAG,"SCMD_KEY_EXT_EVENT_OUT+++++++++++++++++++++_AUX");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_OUT_AUX);
			} else if (cmd == SMCmd.SCMD_KEY_SUSPEND) {
				BaseUtils.mlog(TAG, "SCMD_KEY_SUSPEND_AUX");
			}
		}

	};

	private IInputCb.Stub inputCb_AUX = new IInputCb.Stub() {
		@Override
		public void key(int key) throws RemoteException {
			if(BaseApp.playSourceManager == 2) {
				switch (key) {
					case KEY.NEXT_TRACK:
						mHandler.sendEmptyMessage(NEXT_TRACK_AUX);
						break;
					case KEY.PREV_TRACK:
						mHandler.sendEmptyMessage(PREV_TRACK_AUX);
						break;
					default:
						break;
				}
			}
		}
	};

	private IDeviceCb.Stub deviceCb_AUX = new IDeviceCb.Stub(){

		@Override
		public void disc(boolean devIn) throws RemoteException {

		}

		@Override
		public void reverse(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the reverse message-_AUX");
		}

		@Override
		public void standby(boolean on) throws RemoteException {

		}

		@Override
		public void displayCar(boolean on) throws RemoteException {

		}

		@Override
		public void backlight(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the backlight message-_AUX");
		}

		@Override
		public void on3GBusy(boolean on) throws RemoteException {

		}

		@Override
		public void onParkingBrake(boolean on) throws RemoteException {
			if(BaseApp.playSourceManager == 2) {
				BaseUtils.mlog(TAG, "onParkingBrakeAUX");
				if (BaseApp.brake_flag != on) {
					BaseApp.brake_flag = on;
					if (BaseApp.brake_flag) {
						mHandler.sendEmptyMessage(BRAKE_MSG_ON_AUX);
					} else {
						mHandler.sendEmptyMessage(BRAKE_MSG_OFF_AUX);
					}
				}
			}
		}

		@Override
		public void carLightState(int num) throws RemoteException {

		}

		@Override
		public void carSpeed(int num) throws RemoteException {

		}

		@Override
		public void carAngle(int num) throws RemoteException {

		}
	};

	private ISrcCtrlCb.Stub mSrcCtrl_VIDEO = new ISrcCtrlCb.Stub() {

		@Override
		public void srcIn() throws RemoteException {
			BaseUtils.mlog(TAG, "srcIn_VIDEO");
			mHandler.sendEmptyMessage(SRC_IN_VIDEO);
		}

		@Override
		public void srcOut() throws RemoteException {
			BaseUtils.mlog(TAG, "srcOut_VIDEO");
			mHandler.sendEmptyMessage(SRC_OUT_VIDEO);
		}

		@Override
		public void baseCtrl(int cmd) throws RemoteException {
			if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_IN) {
				BaseUtils.mlog(TAG, "SCMD_KEY_EXT_EVENT_IN+++++++++++++++++++++_VIDEO");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_IN_VIDEO);
			} else if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_OUT) {
				BaseUtils.mlog(TAG,"SCMD_KEY_EXT_EVENT_OUT+++++++++++++++++++++_VIDEO");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_OUT_VIDEO);
			} else if (cmd == SMCmd.SCMD_KEY_SUSPEND) {
				BaseUtils.mlog(TAG, "SCMD_KEY_SUSPEND_VIDEO");
			}
		}

	};

	private IInputCb.Stub inputCb_VIDEO = new IInputCb.Stub() {

		@Override
		public void key(int key) throws RemoteException {
			if(BaseApp.playSourceManager == 3) {
				switch (key) {
					case KEY.NEXT_TRACK:
						mHandler.sendEmptyMessage(NEXT_TRACK_VIDEO);
						break;
					case KEY.PREV_TRACK:
						mHandler.sendEmptyMessage(PREV_TRACK_VIDEO);
						break;
					default:
						break;
				}
			}
		}
	};

	private IDeviceCb.Stub deviceCb_VIDEO = new IDeviceCb.Stub(){

		@Override
		public void disc(boolean devIn) throws RemoteException {

		}

		@Override
		public void reverse(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the reverse message-_VIDEO");
		}

		@Override
		public void standby(boolean on) throws RemoteException {

		}

		@Override
		public void displayCar(boolean on) throws RemoteException {

		}

		@Override
		public void backlight(boolean on) throws RemoteException {
			BaseUtils.mlog(TAG, "receive the backlight message-_VIDEO");
		}

		@Override
		public void on3GBusy(boolean on) throws RemoteException {

		}

		@Override
		public void onParkingBrake(boolean on) throws RemoteException {
			if(BaseApp.playSourceManager == 3) {
				BaseUtils.mlog(TAG, "onParkingBrakeSD");
				if (BaseApp.brake_flag != on) {
					BaseApp.brake_flag = on;
					if (BaseApp.brake_flag) {
						mHandler.sendEmptyMessage(BRAKE_MSG_ON_VIDEO);
					} else {
						mHandler.sendEmptyMessage(BRAKE_MSG_OFF_VIDEO);
					}
				}
			}
		}

		@Override
		public void carLightState(int num) throws RemoteException {

		}

		@Override
		public void carSpeed(int num) throws RemoteException {

		}

		@Override
		public void carAngle(int num) throws RemoteException {

		}
	};
	private void registerKeys() {
		byte[] keys = new byte[] { KEY.NEXT_TRACK, KEY.PREV_TRACK };

		try {
			mService.registerKey(appIdAUX, keys);
			mService.registerKey(appIdMUSIC, keys);
			mService.registerKey(appIdBT, keys);
			mService.registerKey(appIdVIDEO, keys);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}



	public void requestSourceToAUX() { //AUX
		BaseUtils.mlog(TAG, "-------------requestSourceToAUX-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdAUX);
//		}
	}

	public void requestSourceToMUSIC() {
		BaseUtils.mlog(TAG, "-------------requestSourceToMUSIC-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdMUSIC);
//		}
	}

	public void requestSourceToVIDEO() {
		BaseUtils.mlog(TAG, "-------------requestSourceToVideo-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdVIDEO);
//		}
	}

	public void requestSourceToBT() {
		BaseUtils.mlog(TAG, "-------------requestSourceToBT-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdBT);
//		}
	}

	private void srcTo(byte src) {
		if (mService != null) {
			try {
				BaseUtils.mlog(TAG, "src-----:" + src);
				mService.sourceReq(src, src);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}else{
			BaseUtils.mlog(TAG,"mService IS NULL");
		}

	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			BaseUtils.mlog(TAG, "onServiceDisconnected");
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			mService = ISourceManagerService.Stub.asInterface(arg1);

			BaseUtils.mlog(TAG, "onServiceConnected");

			try {
				mService.registerSrcCtrlCb(mSrcCtrl_MUSIC, appIdMUSIC);
				mService.registerDeviceCb(deviceCb_MUSIC, appIdMUSIC);
				mService.registerInputCb(inputCb_MUSIC, appIdMUSIC);

				mService.registerSrcCtrlCb(mSrcCtrl_BT, appIdBT);
				mService.registerDeviceCb(deviceCb_BT, appIdBT);
				mService.registerInputCb(inputCb_BT, appIdBT);

				mService.registerSrcCtrlCb(mSrcCtrl_AUX, appIdAUX);
				mService.registerDeviceCb(deviceCb_AUX, appIdAUX);
				mService.registerInputCb(inputCb_AUX, appIdAUX);

				mService.registerSrcCtrlCb(mSrcCtrl_VIDEO, appIdVIDEO);
				mService.registerDeviceCb(deviceCb_VIDEO, appIdVIDEO);
				mService.registerInputCb(inputCb_VIDEO, appIdVIDEO);
				registerKeys();
			} catch (RemoteException e) {
				// TODO: handle exception
			}
		}
	};

	public void onDestory() {
		if (mService != null) {
			try {
				mService.unregisterSrcCtrlCb(mSrcCtrl_MUSIC, appIdMUSIC);
				mService.unregisterDeviceCb(deviceCb_MUSIC, appIdMUSIC);
				mService.unregisterInputCb(inputCb_MUSIC, appIdMUSIC);

//				mService.unregisterSrcCtrlCb(mSrcCtrl_BT, appIdBT);
				mService.unregisterDeviceCb(deviceCb_BT, appIdBT);
				mService.unregisterInputCb(inputCb_BT, appIdBT);

				mService.unregisterSrcCtrlCb(mSrcCtrl_AUX, appIdAUX);
				mService.unregisterDeviceCb(deviceCb_AUX, appIdAUX);
				mService.unregisterInputCb(inputCb_AUX, appIdAUX);

				mService.unregisterSrcCtrlCb(mSrcCtrl_VIDEO, appIdVIDEO);
				mService.unregisterDeviceCb(deviceCb_VIDEO, appIdVIDEO);
				mService.unregisterInputCb(inputCb_VIDEO, appIdVIDEO);
			} catch (RemoteException e) {
				// TODO: handle exception
			}
		}

		mContext.unbindService(mConnection);
	}

}
