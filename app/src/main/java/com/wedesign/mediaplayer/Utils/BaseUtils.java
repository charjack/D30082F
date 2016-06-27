package com.wedesign.mediaplayer.Utils;

import android.util.Log;

import com.wedesign.mediaplayer.BaseApp;


/**
 * Created by NANA on 2016/5/18.
 */
public class BaseUtils {
    public static void sout(String str){
        if(BaseApp.if_debug){
            System.out.println(str);
        }
    }

    public static void mlog(String tag,String str){
        if(BaseApp.if_debug){
            Log.d(tag, str);
        }
    }

    public static void mlog2(String tag,String funcName,String str){
        if(BaseApp.if_debug){
            Log.d(tag,funcName+"---"+str);
        }
    }
}