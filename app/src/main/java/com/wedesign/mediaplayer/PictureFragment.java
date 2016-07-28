package com.wedesign.mediaplayer;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.wedesign.mediaplayer.Utils.BaseUtils;
import com.wedesign.mediaplayer.Utils.PicMediaUtils;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment {
    private static final String TAG = "PictureFragment";
    ImageView big_pic_show,imageView_PPT;
    MyPicHandler myPicHandler;
    TextView pic_shanglan_textview;
    boolean isshowtitle = false;
    Context ctx;
    private Bitmap myBitmap;
    private boolean ifzoom = false;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();  //记录两个触点的中间点
    private PointF end = new PointF();
    private PointF point_one_start = new PointF();
    private PointF point_one_end = new PointF();

    DisplayMetrics dm;
    float minScaleR=0.5f;
    float MAX_SCALE = 2f;
    float bigSize = 1.25f;
    float smallSize = 0.8f;
    private long firstTime = 0;  //记录第一次点击的时间

    PicUIUpdateListener picUIUpdateListener;
    private volatile static PictureFragment pic_instance = null;
    public PictureFragment() {
    }

    public static PictureFragment getInstance(Context context){
        if(pic_instance == null){
            synchronized (PictureFragment.class) {
                if (pic_instance == null){
                    pic_instance = new PictureFragment();
                }else{
                    BaseUtils.mlog(TAG,"已经存在相应的实例了");
                }
            }
        }
        return pic_instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ctx = (Context) activity;
        picUIUpdateListener =(PicUIUpdateListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View pic_view = inflater.inflate(R.layout.fragment_picture, container, false);
        big_pic_show = (ImageView) pic_view.findViewById(R.id.big_pic_show);
        pic_shanglan_textview = (TextView) pic_view.findViewById(R.id.pic_shanglan_textview);
        imageView_PPT = (ImageView) pic_view.findViewById(R.id.imageView_PPT);

        imageView_PPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picUIUpdateListener.onCancelPPT();
            }
        });

        myPicHandler = new MyPicHandler();

        dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();

        String mybigPicPath = null;
        if(BaseApp.playSourceManager == 0) {
            mybigPicPath = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
        }else if(BaseApp.playSourceManager == 3){
            mybigPicPath = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
        }

        Bitmap bm = convertToBitmap(mybigPicPath,800, 360);
        big_pic_show.setImageBitmap(bm);
        float deltaX = 0,deltaY = 0;
        Matrix newmatrix = new Matrix();
        deltaY = (180-bm.getHeight()/2);
        deltaX = 400 - bm.getWidth()/2;
        newmatrix.postTranslate(deltaX, deltaY);
//        big_pic_show.setImageMatrix(newmatrix);
        setImage(newmatrix);
        big_pic_show.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ImageView view = (ImageView) v;
                if (BaseApp.ifliebiaoOpen == 1) {
                    BaseApp.ifliebiaoOpen = 0;
                    picUIUpdateListener.onPicLieBiaoClose();
                }else{
                    BaseUtils.mlog(TAG, "触摸事件发生了");
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            matrix.set(view.getImageMatrix());
                            savedMatrix.set(matrix);
                            start.set(event.getX(), event.getY());
                            mode = DRAG;
                            break;
                        case MotionEvent.ACTION_UP:
                            BaseUtils.mlog(TAG, "单点的触摸起来了");
                            mode = NONE;
                            end.set(event.getX(),event.getY());
                            BaseUtils.mlog(TAG, "X轴---" + (start.x - end.x)+"   Y轴---"+(start.y-end.y));
                            if(Math.abs(start.x-end.x)<10 && Math.abs(start.y-end.y)<10){//判断为点击事件
                                long secondTime = System.currentTimeMillis();
                                if (secondTime - firstTime < 1000) {  //小于2秒判断为双击事件，返回原图大小
                                    playPic();
                                }else {
                                    firstTime = secondTime;//更新firstTime
                                    if (isshowtitle) {//标题显示了，需要隐藏
                                        pic_shanglan_textview.setVisibility(View.GONE);
                                        myPicHandler.removeMessages(2);  //取消发送的消息
                                        isshowtitle = false;
                                    } else {
                                        String path = null;
                                        if (BaseApp.playSourceManager == 0) {
                                            path = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
                                        } else if (BaseApp.playSourceManager == 3) {
                                            path = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
                                        }
                                        String parentName = new File(path).getParentFile().getName();
                                        pic_shanglan_textview.setVisibility(View.VISIBLE);
                                        pic_shanglan_textview.setText(parentName);
                                        System.out.println(parentName);
                                        isshowtitle = true;
                                        myPicHandler.sendEmptyMessageDelayed(2, 5000);
                                    }
                                }
                            }else if(end.x-start.x > 50 && (Math.abs(end.y - start.y) < 100) && mode!=ZOOM && !ifzoom){
                                playPicpre();
                            }else if(start.x - end.x > 50 && (Math.abs(start.y - end.y) < 100) && mode!=ZOOM && !ifzoom){
                                playPicnext();
                            }

                            float p1[] = new float[9];
                            float p2[] = new float[9];
                            matrix.getValues(p1);
                            savedMatrix.getValues(p2);
                            if(p2[0]>1 && 1>p1[0]){  //从放大状态到了缩小状态,将图片居中显示
                                playPicZoom(p1[0]);
                            }
                            break;

                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = spacing(event);   //两点按下的距离
                            if (oldDist > 10f) {
                                matrix.set(view.getImageMatrix());
                                savedMatrix.set(matrix);
                                midPoint(mid, event);  //设置mid为两个触点的中间点坐标
                                mode = ZOOM;
                            }
                            point_one_start.set(event.getX(),event.getY());
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            BaseUtils.mlog(TAG, "多点的触摸起来了");
                            point_one_end.set(event.getX(),event.getY());
                            mode = NONE;
                            if(Math.abs(point_one_start.x-point_one_end.x)<10 && Math.abs(point_one_start.y-point_one_end.y)<10){//判断为点击事件
                                if (isshowtitle) {//标题显示了，需要隐藏
                                    pic_shanglan_textview.setVisibility(View.GONE);
                                    myPicHandler.removeMessages(2);  //取消发送的消息
                                    isshowtitle = false;
                                } else {
                                    String path = null;
                                    if(BaseApp.playSourceManager ==0) {
                                        path = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
                                    }else if(BaseApp.playSourceManager == 3){
                                        path = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
                                    }
                                    String parentName = new File(path).getParentFile().getName();
                                    pic_shanglan_textview.setVisibility(View.VISIBLE);
                                    pic_shanglan_textview.setText(parentName);
                                    System.out.println(parentName);
                                    isshowtitle = true;
                                    myPicHandler.sendEmptyMessageDelayed(2, 5000);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {
                                if(ifzoom) { //只有缩放之后，才能移动
                                    matrix.set(savedMatrix);
                                    float p[] = new float[9];
                                    matrix.getValues(p);    //p会每次都变化
                                    if(p[0] > 1) {
                                        BaseUtils.mlog(TAG, view.getTop() + "----");
                                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);  //
                                    }
                                }
                                view.setImageMatrix(matrix);
                            }else if (mode == ZOOM) {
                                float newDist = spacing(event);
                                if (newDist > 20f) {
                                    ifzoom = true;  //双指缩放
                                    matrix.set(savedMatrix);
                                    float scale = newDist / oldDist;
                                    float p[] = new float[9];
                                    matrix.getValues(p);    //p会每次都变化

                                    BaseUtils.mlog(TAG, "P[0]------:" + p[0] + "scale---:" + scale);
                                    if((p[0] < minScaleR) && scale < 1){  //限制最小和最大视图
//                                        matrix.postScale(1, 1, mid.x, mid.y);
                                        matrix.postScale(1, 1, 400, 180);
                                    }else if((p[0] > MAX_SCALE) && scale > 1){
//                                        matrix.postScale(1, 1, mid.x, mid.y);
                                        matrix.postScale(1, 1, 400, 180);
                                    }else {
//                                      matrix.postScale(scale, scale, mid.x, mid.y);
                                        matrix.postScale(scale, scale, 400, 180);
                                    }
                                    view.setImageMatrix(matrix);
                                }else{
                                    view.setImageMatrix(matrix);
                                }

                            }else {
                                view.setImageMatrix(matrix);
                            }

                            break;
                    }
                }
                return true;
            }

            private float spacing(MotionEvent event)
            {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float)(Math.sqrt(x * x + y * y));
            }

            private void midPoint(PointF point, MotionEvent event)
            {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }

        });

        if(picUIUpdateListener!=null){
            picUIUpdateListener.onPicCancelCover();
        }
        return pic_view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Message picture_msg = myPicHandler.obtainMessage(1);
        if(BaseApp.playSourceManager == 0) {
            picture_msg.arg1 = BaseApp.current_pic_play_numUSB;
            if(BaseApp.ifhaveUSBdevice){
                myPicHandler.sendMessage(picture_msg);
            }
        }else if(BaseApp.playSourceManager == 3){
            picture_msg.arg1 = BaseApp.current_pic_play_numSD;
            if(BaseApp.ifhavaSDdevice){
                myPicHandler.sendMessage(picture_msg);
            }
        }

    }

    public void changeImageShow(int position){   //已经在图片的fragment了
        ifzoom = false;
//        size = 1f;
        String mybigPicPath =null ;
        if(BaseApp.playSourceManager == 0) {
            mybigPicPath = BaseApp.picInfos.get(position).getData();
        }else if(BaseApp.playSourceManager == 3) {
            mybigPicPath = BaseApp.picInfosSD.get(position).getData();
        }
        Bitmap bm = convertToBitmap(mybigPicPath, 800, 360);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 180-bm.getHeight()/2;  //view的高度就是350

        BaseUtils.mlog(TAG, "view Height:360" + "-----bitmap Height:" + bm.getHeight());
        float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800

        BaseUtils.mlog(TAG, "view width:400" + "-----bitmap width:" + bm.getWidth());
        newmatrix.postTranslate(deltaX, deltaY);
//        big_pic_show.setImageMatrix(newmatrix);
        setImage(newmatrix);
    }

    public void setImage(Matrix mx){
        big_pic_show.setImageMatrix(mx);
        if(picUIUpdateListener!=null){
            picUIUpdateListener.onSavePicProgress();
        }
    }

    public void setImageShow(int position){  //从列表点击显示图片
        ifzoom = false;
        if(BaseApp.playSourceManager == 0) {
            BaseApp.current_pic_play_numUSB = position;
        }else if(BaseApp.playSourceManager == 3){
            BaseApp.current_pic_play_numSD = position;
        }
    }

    public void playPicpre(){
        ifzoom = false;
        String mybigPicPath = null;
        if(BaseApp.playSourceManager == 0) {
            if (BaseApp.current_pic_play_numUSB - 1 < 0) {
                BaseApp.current_pic_play_numUSB = BaseApp.picInfos.size() - 1;
            } else {
                BaseApp.current_pic_play_numUSB = BaseApp.current_pic_play_numUSB - 1;
            }
            mybigPicPath = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
        }else if(BaseApp.playSourceManager == 3){
            if (BaseApp.current_pic_play_numSD - 1 < 0) {
                BaseApp.current_pic_play_numSD = BaseApp.picInfosSD.size() - 1;
            } else {
                BaseApp.current_pic_play_numSD = BaseApp.current_pic_play_numSD - 1;
            }
            mybigPicPath = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
        }
        Bitmap bm = convertToBitmap(mybigPicPath, 800, 360);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 180-bm.getHeight()/2;  //view的高度就是350

        BaseUtils.mlog(TAG,"view Height:360" + "-----bitmap Height:" + bm.getHeight());
        float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800

        BaseUtils.mlog(TAG, "view width:800" + "-----bitmap width:" + bm.getWidth());
        newmatrix.postTranslate(deltaX, deltaY);
//        big_pic_show.setImageMatrix(newmatrix);
        setImage(newmatrix);
    }

    public void playPicZoom(float p0){  //在放大至缩小的时候还原图片于中心位置
        String mybigPicPath = null;
        if(BaseApp.playSourceManager == 0) {
            mybigPicPath = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
        }else if(BaseApp.playSourceManager == 3){
            mybigPicPath = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
        }
        Bitmap bm = convertToBitmap(mybigPicPath, 800, 360);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 180-p0*bm.getHeight()/2;  //view的高度就是350

        BaseUtils.mlog(TAG,"view Height:360" + "-----bitmap Height:" + (180 - deltaY));
        float deltaX = 400 - p0*bm.getWidth()/2;  // view的宽度就是800

        BaseUtils.mlog(TAG, "view width:800" + "-----bitmap width:" + (180 -deltaX));
        newmatrix.postScale(p0, p0);
        newmatrix.postTranslate(deltaX, deltaY);
        setImage(newmatrix);
    }

    public void playPic(){
        ifzoom = false;
        String mybigPicPath = null;
        if(BaseApp.playSourceManager == 0) {
            mybigPicPath = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
        }else if(BaseApp.playSourceManager == 3){
            mybigPicPath = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
        }
        Bitmap bm = convertToBitmap(mybigPicPath, 800, 360);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 180-bm.getHeight()/2;  //view的高度就是350

        BaseUtils.mlog(TAG,"view Height:360" + "-----bitmap Height:" + bm.getHeight());
        float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800

        BaseUtils.mlog(TAG, "view width:800" + "-----bitmap width:" + bm.getWidth());
        newmatrix.postTranslate(deltaX, deltaY);
        setImage(newmatrix);
    }


    public void playPicnext(){
        String mybigPicPath = null;
        if(BaseApp.playSourceManager == 0) {
            BaseUtils.mlog(TAG, "-playPicnext-" + "current_pic_play_num: " + BaseApp.current_pic_play_numUSB + "picInfos.size()" + PicMediaUtils.getPicCounts(ctx));
            ifzoom = false;
            if (BaseApp.current_pic_play_numUSB + 1 > BaseApp.picInfos.size() - 1) {
                BaseApp.current_pic_play_numUSB = 0;
            } else {
                BaseApp.current_pic_play_numUSB++;
            }
            BaseUtils.mlog(TAG, "-playPicnext-" + "current_pic_play_num: " + BaseApp.current_pic_play_numUSB);
            mybigPicPath = BaseApp.picInfos.get(BaseApp.current_pic_play_numUSB).getData();
        }else if(BaseApp.playSourceManager == 3){
            BaseUtils.mlog(TAG, "-playPicnext-" + "current_pic_play_num: " + BaseApp.current_pic_play_numSD + "picInfos.size()" + PicMediaUtils.getPicCounts(ctx));
            ifzoom = false;
            if (BaseApp.current_pic_play_numSD + 1 > BaseApp.picInfosSD.size() - 1) {
                BaseApp.current_pic_play_numSD = 0;
            } else {
                BaseApp.current_pic_play_numSD++;
            }
            BaseUtils.mlog(TAG, "-playPicnext-" + "current_pic_play_num: " + BaseApp.current_pic_play_numSD);
            mybigPicPath = BaseApp.picInfosSD.get(BaseApp.current_pic_play_numSD).getData();
        }

        Bitmap bm = convertToBitmap(mybigPicPath, 800, 360);
        big_pic_show.setImageBitmap(bm);
        Matrix newmatrix = new Matrix();
        float deltaY = 180-bm.getHeight()/2;  //view的高度就是350

        BaseUtils.mlog(TAG,"view Height:360" + "-----bitmap Height:" + bm.getHeight());
        float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800

        BaseUtils.mlog(TAG, "view width:400" + "-----bitmap width:" + bm.getWidth());
        newmatrix.postTranslate(deltaX, deltaY);

        setImage(newmatrix);
    }

    public Bitmap convertToBitmap(String path, int w, int h) {
        Bitmap bmOrg = BitmapFactory.decodeFile(path);

        float scaleWidth =  (w / (float)bmOrg.getWidth());
        float scaleHeight =  (h / (float)bmOrg.getHeight());
        int newW = 0;
        int newH = 0;
        if(scaleWidth > scaleHeight){
            newW = (int) (bmOrg.getWidth() * scaleHeight);
            newH = (int) (bmOrg.getHeight() * scaleHeight);
        }else if(scaleWidth <= scaleHeight){
            newW = (int) (bmOrg.getWidth() * scaleWidth);
            newH = (int) (bmOrg.getHeight() * scaleWidth);
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmOrg, newW, newH, true);
        return resizedBitmap;
    }

    public void pic_play_fangda(){
        BaseUtils.mlog(TAG,"-pic_play_fangda-"+"come in fangda...");
        ifzoom = true;
        myBitmap = ((BitmapDrawable) big_pic_show.getDrawable()).getBitmap();
        big_pic_show.setImageBitmap(myBitmap);
        Matrix newMatrix = big_pic_show.getImageMatrix();
        float p[] = new float[9];
        newMatrix.getValues(p);

        BaseUtils.mlog(TAG,"P[0]------:" + p[0]);
        if(p[0] >MAX_SCALE){
            newMatrix.postScale(1, 1, 400, 180);
        }else {
            newMatrix.postScale(bigSize, bigSize, 400, 180);
        }
        big_pic_show.setImageMatrix(newMatrix);
    }
    public void pic_play_suoxiao(){

        BaseUtils.mlog(TAG,"-pic_play_suoxiao-"+"come in suoxiao...");
        ifzoom = true;
        myBitmap = ((BitmapDrawable) big_pic_show.getDrawable()).getBitmap();
        big_pic_show.setImageBitmap(myBitmap);
        Matrix newMatrix = big_pic_show.getImageMatrix();
        float p[] = new float[9];
        newMatrix.getValues(p);    //p会每次都变化

        BaseUtils.mlog(TAG,"P[0]------:" + p[0]);
        if(p[0] < minScaleR){
            newMatrix.postScale(1, 1, 400, 180);
        }else {
            newMatrix.postScale(smallSize, smallSize, 400, 180);
        }
        big_pic_show.setImageMatrix(newMatrix);
    }

    class MyPicHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1://重新返回后，显示大图片
                    String mybigPicPath = null;
                    if(BaseApp.playSourceManager == 0 && BaseApp.picInfos!=null && BaseApp.picInfos.size() > 0) {
                        mybigPicPath = BaseApp.picInfos.get(msg.arg1).getData();
                    }else if(BaseApp.playSourceManager == 3  && BaseApp.picInfosSD!=null && BaseApp.picInfosSD.size() > 0){
                        mybigPicPath = BaseApp.picInfosSD.get(msg.arg1).getData();
                    }
                    Bitmap bm = convertToBitmap(mybigPicPath, 800, 360);
                    big_pic_show.setImageBitmap(bm);
                    Matrix newmatrix = new Matrix();
                    float deltaY = 180-bm.getHeight()/2;  //view的高度就是350

                    BaseUtils.mlog(TAG,"view Height:350" + "-----bitmap Height:" + bm.getHeight());
                    float deltaX = 400 - bm.getWidth()/2;  // view的宽度就是800

                    BaseUtils.mlog(TAG, "view width:400" + "-----bitmap width:" + bm.getWidth());
                    newmatrix.postTranslate(deltaX,deltaY);
                    setImage(newmatrix);
//                    big_pic_show.setImageMatrix(newmatrix);
                    break;
                case 2://隐藏标题
                    pic_shanglan_textview.setVisibility(View.GONE);
                    isshowtitle = false;
                    break;
            }
        }
    }

    public interface PicUIUpdateListener{
        public void onPicLieBiaoClose();
        public void onCancelPPT();
        public void onSavePicProgress();
        public void onPicCancelCover();
    }
}
