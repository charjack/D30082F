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
import com.wedesign.mediaplayer.vo.Mp3Info;

import java.util.List;


/**
 * Created by NANA on 2016/4/21.
 */
public class MymusiclistviewAdapter extends BaseAdapter {
    public List<Mp3Info> lists;
    Context ctx;

    public MymusiclistviewAdapter(Context ctx, List<Mp3Info> lists){
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
            convertView = LayoutInflater.from(ctx).inflate(R.layout.listviewitem_layout,null);
            vh.imageView = (ImageView) convertView.findViewById(R.id.music_item_pic);
            vh.textView = (TextView) convertView.findViewById(R.id.music_item_text);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        Mp3Info mp3Info = lists.get(position);
        if(BaseApp.playSourceManager == 0) {
            if (BaseApp.current_music_play_numUSB == position) {
                vh.imageView.setImageResource(R.mipmap.yinyue_p);
                vh.textView.setText(mp3Info.getTittle() + " - " + mp3Info.getArtist());
                vh.textView.setTextColor(Color.rgb(01, 66, 255));
            } else {
                vh.imageView.setImageResource(R.mipmap.yinyue_n);
                vh.textView.setText(mp3Info.getTittle() + " - " + mp3Info.getArtist());
                vh.textView.setTextColor(Color.rgb(255, 255, 255));
            }
        }else if(BaseApp.playSourceManager == 3){
            if (BaseApp.current_music_play_numSD == position) {
                vh.imageView.setImageResource(R.mipmap.yinyue_p);
                vh.textView.setText(mp3Info.getTittle() + " - " + mp3Info.getArtist());
                vh.textView.setTextColor(Color.rgb(01, 66, 255));
            } else {
                vh.imageView.setImageResource(R.mipmap.yinyue_n);
                vh.textView.setText(mp3Info.getTittle() + " - " + mp3Info.getArtist());
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
