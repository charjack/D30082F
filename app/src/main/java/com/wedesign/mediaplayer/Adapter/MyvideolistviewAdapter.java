package com.wedesign.mediaplayer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wedesign.mediaplayer.BaseApp;
import com.wedesign.mediaplayer.R;
import com.wedesign.mediaplayer.vo.Mp4Info;

import java.util.List;

/**
 * Created by NANA on 2016/4/21.
 */
public class MyvideolistviewAdapter extends BaseAdapter {
    public List<Mp4Info> lists;
    Context ctx;

    public MyvideolistviewAdapter(Context ctx, List<Mp4Info> lists){
        this.ctx = ctx;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  vh;
        if(convertView == null){
            vh = new ViewHolder();
            convertView = LayoutInflater.from(ctx).inflate(R.layout.videolistviewitem_layout,null);
            vh.imageView = (ImageView) convertView.findViewById(R.id.video_item_pic);
            vh.textView = (TextView) convertView.findViewById(R.id.video_item_text);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Mp4Info mp4Info = lists.get(position);
        if(BaseApp.playSourceManager == 0) {
            if (BaseApp.current_video_play_numUSB == position) {
                vh.imageView.setImageResource(R.mipmap.video_p);
                vh.textView.setText(mp4Info.getDisplay_name());
                vh.textView.setTextColor(Color.rgb(01, 66, 255));
            } else {
                vh.imageView.setImageResource(R.mipmap.video_n);
                vh.textView.setText(mp4Info.getDisplay_name());
                vh.textView.setTextColor(Color.rgb(255, 255, 255));
            }
        }else if(BaseApp.playSourceManager == 3){
            if (BaseApp.current_video_play_numSD == position) {
                vh.imageView.setImageResource(R.mipmap.video_p);
                vh.textView.setText(mp4Info.getDisplay_name());
                vh.textView.setTextColor(Color.rgb(01, 66, 255));
            } else {
                vh.imageView.setImageResource(R.mipmap.video_n);
                vh.textView.setText(mp4Info.getDisplay_name());
                vh.textView.setTextColor(Color.rgb(255, 255, 255));
            }
        }
        return convertView;
    }

    public class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

}
