package com.wedesign.mediaplayer.vo;




public class SMCmd {
	public static final int PORT_NUM = 6001;
	public static final int FRAME_DATA_MAX = 255;
	public static final int PAYLOAD_DATA_MAX = 220;
	
	public static final byte APP_ID_INVALID = (byte) 0xFF;
	public static final byte APP_ID_RADIO = 0;
	public static final byte APP_ID_DISC = 1;
	public static final byte APP_ID_USB = 2;	//USB
	public static final byte APP_ID_HDD = 3;	//SD
	public static final byte APP_ID_DTV = 4;
	public static final byte APP_ID_AUX = 5;     // AUX
	public static final byte APP_ID_IPOD = 6;
	public static final byte APP_ID_BT_MUSIC = 7; //BT
	public static final byte APP_ID_BT_PHONE = 8;
	public static final byte APP_ID_LAUNCHER = 9;
	public static final byte APP_ID_CAMERA = 10;
	public static final byte APP_ID_NEWSTTS = 11;
	public static final byte APP_ID_YDT = 12;	//yidiantong
	public static final byte APP_ID_NAVI = 13;
	public static final byte APP_ID_VOICEASS = 14;
	public static final byte APP_ID_MOBILEINTER = 15;	//xx link with phone, e.g mirror link.
	public static final byte APP_ID_ECO = 16;
	public static final byte APP_ID_VOL = 17;
	public static final byte APP_ID_SOS = 18;
	public static final byte APP_ID_SRROUND = 19;
	public static final byte APP_ID_FADEZONE = 20;
	public static final byte APP_ID_SOURCE_MANAGER = 21;
	public static final byte APP_ID_STANDBY = 22;
	public static final byte APP_ID_SYSTEM_UI = 23;
	public static final byte APP_ID_UPDATE = 24;
	public static final byte APP_ID_ALL = 25;





	
	public static final byte START1 = (byte) 0xFF;
	public static final byte START2 = (byte) 0xFE;
	public static final byte END1 = START2;
	public static final byte END2 = START1;
	
	
	
	//cmd from source manager:
	public static final byte CMDF_BASE_CTRL = 0x01;
	public static final byte CMDF_RADIO_DATA = 0x02;
	public static final byte CMDF_SETTING_DATA = 0x03;
	public static final byte CMDF_HEART_BEAT = 0x04;
	public static final byte CMDF_SYS_DATA = 0x05;
	public static final byte CMDF_DTV_DATA = 0x06;
	public static final byte CMDF_SWC_DATA = 0x07;
	public static final byte CMDF_CAN_INFO = 0x08;
	public static final byte CMDF_RET_DATA = 0x09;
	public static final byte CMDF_KEY = 	0x0A;
	public static final byte CMDF_MCU_CMD = 0x0B;
	public static final byte CMDF_MUTE_STATE = 	0x0C;
	
	//sub cmd of CMDF_SYS_DATA
	public static final byte SCMD_RET_MCU_VOLUME = 0x01;
	public static final byte SCMD_RET_MCU_VER = 0x03;
	public static final byte SCMD_RET_MCU_DEVICE_STATUS = 0x04;
	public static final byte SCMD_RET_MCU_VOLUME_ALL = 0x09;



	//sub cmd of CMDF_SETTING_DATA
	public static final byte SCMDF_SETTING_M2A_SET = 0x20;


	//sub cmd of CMDF_MUTE_STATE
	public static final byte SCMD_MUTE_STATE_OFF = 0x00;
	public static final byte SCMD_MUTE_STATE_ON = 0x01;
	
	//sub cmd of CMDF_RET_DATA
	public static final byte SCMD_RET_WORK_MODE = 0x01;
	public static final byte SCMD_RET_MCU_UPDATE_FILE = 0x02;
	public static final byte SCMD_RET_MCU_UPDATE_PROGRESS = 0x03;
	public static final byte SCMD_RET_MCU_UPDATE_STATE = 0x04;
	public static final byte SCMD_RET_DATA_FRONT_SRC = 0x05;
	public static final byte SCMD_RET_DATA_MCU_VERSION = 0x08;
	public static final byte SCMD_RET_DATA_BRIGHTNESS = 0x20;

	public static final byte SCMD_RET_DISC_STATUS = 0x30;
//	public static final byte SCMD_RET_DISC_EJECT_REQUEST = 0x31;
	public static final byte SCMD_RET_DATA_CAN_CLOCK = 0x40;
	public static final byte SCMD_RET_DATA_CAN_TEMPERATURE = 0x41;
	
	public static final byte SCMD_RET_AUTH_STATUS = (byte) 0xD0;
	public static final byte SCMD_RET_POWER_STATUS = (byte) 0xD1;


	//cmd to source manager.
	public static final byte CMDT_BASE_STATE = (byte) 0x81;
	public static final byte CMDT_REG_INPUT_KEYS = (byte) 0x82;
	public static final byte CMDT_HEART_BEAT = (byte) 0x83;
	public static final byte CMDT_SRC_REQ = (byte) 0x84;
	public static final byte CMDT_RADIO_DATA = (byte) 0x85;
	public static final byte CMDT_SETTING_DATA = (byte) 0x86;
	public static final byte CMDT_DTV_DATA = (byte) 0x87;
	public static final byte CMDT_TX_CMD = (byte) 0x88;
	public static final byte CMDT_TX_CAN_INFO = (byte) 0x89;
	public static final byte CMDT_TX_SWC_CTL = (byte) 0x8A;
	public static final byte CMDT_USR_INPUT = (byte) 0x8B;
	public static final byte CMDT_GET_DATA = (byte) 0x8C;
	public static final byte CMDT_SYS_INFO = (byte) 0x8D;
	public static final byte CMDT_REG_APP = (byte) 0x8E;
	
	public static final byte CMDT_TEST_CMD = (byte) 0xA0;
	
	
	//sub cmd of CMDF_BASE_CTRL
	public static final byte SCMD_KEY_START	= 0x01;
	public static final byte SCMD_KEY_SUSPEND	= 0x02;
	public static final byte SCMD_KEY_HIDE	= 0x03;
	public static final byte SCMD_KEY_STOP	= 0x04;
	public static final byte SCMD_KEY_EXT_EVENT_IN	= 0x05;
	public static final byte SCMD_KEY_EXT_EVENT_OUT	= 0x06;

	public static final byte SCMD_KEY_EJECT_REQUEST	= (byte)0x90;
	
	
	//sub cmd of CMDT_BASE_STATE
	public static final byte SCMD_KEY_OK = 0x01;
	public static final byte SCMD_KEY_PROCESS = 0x02;
	public static final byte SCMD_KEY_FAIL = 0x03;
	//...
	public static final byte SCMD_KEY_QUIT = (byte) 0x80;

	public static final byte SCMD_KEY_EJECT_READY = (byte)0x91;
	
	
	//sub cmd of CMDT_USR_INPUT
	public static final byte SCMD_WARNING_OK_PRESSED = 0x01;
	public static final byte SCMD_SCAN_MCU_UPDATE_FILE = 0x02;
	public static final byte SCMD_REBOOT_SYSTEM = 0x03;
	public static final byte SCMD_EXIT_ARM_DISPLAY = 0x06;
	public static final byte SCMD_USB_ID_CONTROL = 0x07;
	public static final byte SCMD_SHUTDOWN_SYSTEM = 0x08;


	//sub id of CMDT_SETTING_DATA.
	public static final byte SCMD_SET_AUDIO = 0x01;
	public static final byte SCMD_SET_SCREEN = 0x09;
	public static final byte SCMD_SET_TUNER_FREQ = 0x0A;
	
	
	//sub cmd of CMDT_GET_DATA
	public static final byte SCMD_GET_WORK_MODE = 0x01;	
	
	//no block.
	public static final byte SCMD_GET_MCU_VERSION = (byte) 0xA0;	
	
	//sub cmd of CMDT_SYS_INFO
	public static final byte SCMD_TX_TOUCH_INFO = 0x03;




	public static final byte SCMD_INVALID = (byte) 0xFF;
	
	//for APP_ID_SOURCE_MANAGER:
	//use CMDF_BASE_CTRL + sub cmd + app id to run app id.
	
	
	
	//sub cmd of CMDT_TEST_CMD
	public static final byte SCMD_SYS_INIT = 0x01;
	public static final byte SCMD_SYS_REQ_LINK = 0x02;
	public static final byte SCMD_OS_READY = 0x03;
	public static final byte SCMD_SYS_ENTER = 0x04;
	
	
	public static final int MAX_KEYS_REGISTER = 50;
	
	public static final int SOCKET_CONNECT_SUCCESS = 8000;
	public static final int AUTH_FAILED = 8001;
	public static final int TIMER_1_SEC = 8002;
	public static final int RX_MSG = 8888;
}


//static const uint WM_NOT_INIT = 0xFF;
//static const uint WM_INIT = 0;
//static const uint WM_WARNING = 1;
//static const uint WM_NORMAL = 2;
//static const uint WM_REVERSE = 3;
//static const uint WM_PHONE = 4;
//static const uint WM_TBOX = 5;
//static const uint WM_STANDBY = 6;	//not really system standby, close audio and display clock menu.
//static const uint WM_SCREEN_OFF = 7;
//static const uint WM_SRROUND = 8;	//全景
//static const uint WM_FADEZONE = 9;	//盲区

