package com.wedesign.mediaplayer.vo;

public abstract class UICC {
	public static final byte UICC_FREQ_LIST_TS  = (byte) 0xA0;
	public static final byte UICC_ASPS_LONG_TS   = (byte) 0xA1;
	public static final byte UICC_BAND_TS   = (byte) 0xA2;
	public static final byte UICC_ASPS_TS   = (byte) 0xA3;
	public static final byte UICC_PREV_TS   = (byte) 0xA4;
	public static final byte UICC_NEXT_TS   = (byte) 0xA5;	
	public static final byte UICC_STEP_UP_TS   = (byte) 0xA6;
	public static final byte UICC_STEP_DOWN_TS   = (byte) 0xA7;
	public static final byte UICC_SCAN_TS  = (byte) 0xA8;
	public static final byte UICC_AF_TS  = (byte) 0xA9;
	public static final byte UICC_TA_TS  = (byte) 0xAA;
	public static final byte UICC_REG_TS   = (byte) 0xAB;	
	public static final byte UICC_EON_TS   = (byte) 0xAC;
	public static final byte UICC_PTY_ENABLE_TS   = (byte) 0xAD;
	public static final byte UICC_SEL_PTY_TS   = (byte) 0xAE;
	public static final byte UICC_LOC_TS   = (byte) 0xAF;
	public static final byte UICC_HD_TAG_TS   = (byte) 0xB0;
	public static final byte UICC_PTY_SEEK_TS   = (byte) 0xB1;	
	public static final byte UICC_PREV_PRESET_TS   = (byte) 0xB2;
	public static final byte UICC_NEXT_PRESET_TS   = (byte) 0xB3;
	
	public static final byte UICC_FRONT_SRC_TS  = (byte) 0xC0;
	public static final byte UICC_REAR_SRC_TS   = (byte) 0xC1;
	public static final byte UICC_BEEP_ONLY_TS   = (byte) 0xC2;
	public static final byte UICC_BEEP_FAIL_TS   = (byte) 0xC3;
	public static final byte UICC_MONITOR_OP   = (byte) 0xC4;
	public static final byte UICC_CELEBRATION   = (byte) 0xC5;
	public static final byte UICC_REQ_VIDEO_SW   = (byte) 0xC6;
	public static final byte UICC_MPEG_RESET   = (byte) 0xC7;
	public static final byte UICC_MPEG_STATE   = (byte) 0xC8;
	public static final byte UICC_BT_PHONE_AUDIO   = (byte) 0xC9;
	public static final byte UICC_APP_TONE   = (byte) 0xCA;
	public static final byte UICC_3G_PHONE_AUDIO   = (byte) 0xCB;
	public static final byte UICC_BT_RESET   = (byte) 0xCC;
	public static final byte UICC_BT_UPDATE   = (byte) 0xCD;
	public static final byte UICC_NAVI_AUDIO   = (byte) 0xCE;
	public static final byte UICC_VR_STATE   = (byte) 0xCF;
	public static final byte UICC_SYS_RESTART   = (byte) 0xD0;
	public static final byte UICC_MUTE   = (byte) 0xD1;
	public static final byte UICC_VOLUME_DOWN  = (byte) 0xD2;
	public static final byte UICC_VOLUME_UP  = (byte) 0xD3;
}

