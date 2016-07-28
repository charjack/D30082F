package com.wedesign.mediaplayer.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.wedesign.mediaplayer.vo.Contents;
import com.wedesign.mediaplayer.vo.Mp4Info;

import java.util.ArrayList;


/**
 * Created by NANA on 2016/4/27.
 */
public class Mp4MediaUtils {
    //查询所有的mp4文件信息
    public static ArrayList<Mp4Info> getMp4Infos(Context context){
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Video.Media.DURATION+">=10000",null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER
        );
        String temp_name = null;
        ArrayList<Mp4Info> mp4Infos = new ArrayList<Mp4Info>();
        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();
            temp_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            if (temp_name.contains(Contents.USB_PATH)) {
                Mp4Info mp4Info = new Mp4Info();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                mp4Info.setId(id);
                mp4Info.setDisplay_name(display_name);
                mp4Info.setData(data);
                mp4Info.setDuration(duration);
                mp4Infos.add(mp4Info);
            }
        }
        cursor.close();
        return mp4Infos;
    }

    public static ArrayList<Mp4Info> getMp4InfosSD(Context context){
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Video.Media.DURATION+">=10000",null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER
        );

        String temp_name = null;
        ArrayList<Mp4Info> mp4Infos = new ArrayList<Mp4Info>();
        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToNext();
            temp_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            if (temp_name.contains(Contents.SDCARD_PATH)) {
                Mp4Info mp4Info = new Mp4Info();
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                mp4Info.setId(id);
                mp4Info.setDisplay_name(display_name);
                mp4Info.setData(data);
                mp4Info.setDuration(duration);
                mp4Infos.add(mp4Info);
            }
        }
        cursor.close();
        return mp4Infos;
    }

    public static Mp4Info getMp4Infobypath(Context context,String path){
//        System.out.println(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,null,
                MediaStore.Video.Media.DATA+" = ?",new String[]{path},
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);

        Mp4Info mp4Info = null;
        if(cursor.moveToNext()){
            mp4Info = new Mp4Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
            String display_name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            long duration =cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

            mp4Info.setId(id);
            mp4Info.setDisplay_name(display_name);
            mp4Info.setData(data);
            mp4Info.setDuration(duration);
        }

        cursor.close();
        return mp4Info;
    }

    public static String formatTime(long time){
        String hour = time/(1000*60*60)+"";
        String min = (time%(1000*60*60))/(1000*60)+"";//
        String sec = time%(1000*60)+"";

        if(hour.length()<1){
            hour ="00";
        } else if(hour.length()<2){
            hour = "0"+time/(1000*60*60);
        }else{
            hour = time/(1000*60*60)+"";
        }


        if(min.length()<2){
            min = "0"+(time%(1000*60*60))/(1000*60);
        }else{
            min = (time%(1000*60*60))/(1000*60)+"";
        }

        if(sec.length() == 4) {
            sec = "0"+(time%(1000*60)+"");
        }else if(sec.length() ==3){
            sec = "00"+(time%(1000*60)+"");
        }else if(sec.length() == 2){
            sec = "000"+(time%(1000*60)+"");
        }else if(sec.length() == 1){
            sec = "0000"+(time%(1000*60)+"");
        }
        return hour+":"+min+":"+sec.trim().substring(0,2);
    }

}
