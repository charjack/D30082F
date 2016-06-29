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
		void onSrcIn();

		void onSrcOut();

		void onNextTrack();

		void onPrevouseTrack();

		void onExtEventIn();

		void onExtEventOut();

		void onBrake(boolean flag);
	}

	private static final String TAG = "VideoSoruceManager";
	private static final byte appId = SMCmd.APP_ID_AUX;
	private static final byte appIdUSB = SMCmd.APP_ID_USB;
	private static final byte appIdBT = SMCmd.APP_ID_BT_MUSIC;
	private static final byte appIdSD = SMCmd.APP_ID_HDD;
	private ISourceManagerService mService = null;
	private final Context mContext;
	private static boolean mIsCurrentSource = false;
	private static boolean mIsListActivityShow = false;
	private volatile static SourceManager mInstance = null;

	private static OnSourceManagerListener mListener = null;

	private static final int CHECK_SERVICE_CONNECTION = 1110;
	private static final int SRC_IN = 1111;
	private static final int SRC_OUT = 1112;
	private static final int NEXT_TRACK = 1113;
	private static final int PREV_TRACK = 1114;
	private static final int SCMD_EXT_EVENT_IN = 1115;
	private static final int SCMD_EXT_EVENT_OUT = 1116;
	private static final int BRAKE_MSG_ON = 1117;
	private static final int BRAKE_MSG_OFF = 1118;
	private static final boolean DEBUG = false;
	
	private void LOG(String log) {
		if (DEBUG) {
			Log.d(TAG, log);
		}
	}

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
				Log.e(TAG, "return as mListener == null in Handler");
				System.out.println("return as mListener == null in Handler");
				return;
			}

			switch (msg.what) {
			case SRC_IN:
				System.out.println("SRC_IN");
				mListener.onSrcIn();
				break;
			case SRC_OUT:
				System.out.println("SRC_OUT");
				mListener.onSrcOut();
				break;
			case NEXT_TRACK:
				System.out.println("NEXT_TRACK");
				mListener.onNextTrack();
				break;
			case PREV_TRACK:
				System.out.println("PREV_TRACK");
				mListener.onPrevouseTrack();
				break;
			case SCMD_EXT_EVENT_IN:
				System.out.println("SCMD_EXT_EVENT_IN");
				mListener.onExtEventIn();
				break;
			case SCMD_EXT_EVENT_OUT:
				System.out.println("SCMD_EXT_EVENT_OUT");
				mListener.onExtEventOut();
				break;
			case BRAKE_MSG_ON:  //播放视频
				mListener.onBrake(true);
				if(BaseApp.if_debug){
					System.out.println("receive the brake message-"+ 1);
				}

				break;
			case BRAKE_MSG_OFF:
				mListener.onBrake(false);
				if(BaseApp.if_debug){
					System.out.println("receive the brake message-"+ 0);
				}
				break;

			default:
				System.out.println("default");
				break;
			}
		}
	};

	private ISrcCtrlCb.Stub mSrcCtrl = new ISrcCtrlCb.Stub() {

		@Override
		public void srcIn() throws RemoteException {
			LOG( "srcIn");
			mIsCurrentSource = true;
			mHandler.sendEmptyMessage(SRC_IN);
		}

		@Override
		public void srcOut() throws RemoteException {
			LOG( "srcOut");
			mHandler.sendEmptyMessage(SRC_OUT);
			mIsCurrentSource = false;
		}

		@Override
		public void baseCtrl(int cmd) throws RemoteException {
			if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_IN) {
				BaseUtils.mlog(TAG, "SCMD_KEY_EXT_EVENT_IN+++++++++++++++++++++");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_IN);

			} else if (cmd == SMCmd.SCMD_KEY_EXT_EVENT_OUT) {
				BaseUtils.mlog(TAG,"SCMD_KEY_EXT_EVENT_OUT+++++++++++++++++++++");
				mHandler.sendEmptyMessage(SCMD_EXT_EVENT_OUT);

			} else if (cmd == SMCmd.SCMD_KEY_SUSPEND) {
				LOG( "SCMD_KEY_SUSPEND");

				// if MainActivity is not in top. show it.
				if (!mIsListActivityShow) {
					ComponentName componentName = new ComponentName("com.wedesign.video",
							"com.wedesign.video.VideoListActivity");
					Intent intentStart = new Intent();
					intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentStart.setComponent(componentName);
					mContext.getApplicationContext().startActivity(intentStart);
				}
			}
		}

	};

	private IInputCb.Stub inputCb = new IInputCb.Stub() {

		@Override
		public void key(int key) throws RemoteException {

			if (!mIsCurrentSource) {
				return;
			}

			switch (key) {
			case KEY.NEXT_TRACK:
				mHandler.sendEmptyMessage(NEXT_TRACK);
				break;
			case KEY.PREV_TRACK:
				mHandler.sendEmptyMessage(PREV_TRACK);
				break;
			default:
				break;
			}
		}
	};

	private IDeviceCb.Stub deviceCb = new IDeviceCb.Stub(){

		@Override
		public void disc(boolean devIn) throws RemoteException {

		}

		@Override
		public void reverse(boolean on) throws RemoteException {
			System.out.println("receive the reverse message-");
		}

		@Override
		public void standby(boolean on) throws RemoteException {

		}

		@Override
		public void displayCar(boolean on) throws RemoteException {

		}

		@Override
		public void backlight(boolean on) throws RemoteException {
			System.out.println("receive the backlight message-");
		}

		@Override
		public void on3GBusy(boolean on) throws RemoteException {

		}

		@Override
		public void onParkingBrake(boolean on) throws RemoteException {
			System.out.println("receive the differ brake message-" );

		}
	};

	private void registerKeys() {
		byte[] keys = new byte[] { KEY.NEXT_TRACK, KEY.PREV_TRACK };

		try {
			mService.registerKey(appId, keys);
			mService.registerKey(appIdUSB, keys);
			mService.registerKey(appIdBT, keys);
			mService.registerKey(appIdSD, keys);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}



	public void requestSourceToVideo() { //AUX
		BaseUtils.mlog(TAG, "-------------requestSourceToVideo-------------");
//		if (!mIsCurrentSource) {
			srcTo(appId);
//		}
	}

	public void requestSourceToVideoUSB() {
		BaseUtils.mlog(TAG, "-------------requestSourceToVideoUSB-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdUSB);
//		}
	}

	public void requestSourceToVideoSD() {
		BaseUtils.mlog(TAG, "-------------requestSourceToVideoSD-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdSD);
//		}
	}

	public void requestSourceToVideoBT() {
		BaseUtils.mlog(TAG, "-------------requestSourceToVideoBT-------------");
//		if (!mIsCurrentSource) {
			srcTo(appIdBT);
//		}
	}

	private void srcTo(byte src) {
		if (mService != null) {
			try {
				LOG( "mService.sourceReq:" + src);
				mService.sourceReq(src, src);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}



	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			LOG( "onServiceDisconnected");
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			mService = ISourceManagerService.Stub.asInterface(arg1);

			LOG( "onServiceConnected");

			try {
				mService.registerSrcCtrlCb(mSrcCtrl, appId);
				mService.registerDeviceCb(deviceCb, appId);
				mService.registerInputCb(inputCb, appId);

				mService.registerSrcCtrlCb(mSrcCtrl, appIdUSB);
				mService.registerDeviceCb(deviceCb, appIdUSB);
				mService.registerInputCb(inputCb, appIdUSB);

				mService.registerSrcCtrlCb(mSrcCtrl, appIdSD);
				mService.registerDeviceCb(deviceCb, appIdSD);
				mService.registerInputCb(inputCb, appIdSD);

				mService.registerSrcCtrlCb(mSrcCtrl, appIdBT);
				mService.registerDeviceCb(deviceCb, appIdBT);
				mService.registerInputCb(inputCb, appIdBT);
				registerKeys();

				mService.sourceReq(appId, appId);
				mService.sourceReq(appIdUSB, appIdUSB);
				mService.sourceReq(appIdSD, appIdSD);
				mService.sourceReq(appIdBT, appIdBT);

			} catch (RemoteException e) {
				// TODO: handle exception
			}
		}
	};

	public void onDestory() {
		if (mService != null) {
			try {
				mService.unregisterSrcCtrlCb(mSrcCtrl, appId);
				mService.unregisterSrcCtrlCb(mSrcCtrl, appIdUSB);
				mService.unregisterSrcCtrlCb(mSrcCtrl, appIdSD);
				mService.unregisterSrcCtrlCb(mSrcCtrl, appIdBT);
			} catch (RemoteException e) {
				// TODO: handle exception
			}
		}

		mContext.unbindService(mConnection);
	}

	public static void setListActivityShow(boolean show) {
		mIsListActivityShow = show;
	}

}
