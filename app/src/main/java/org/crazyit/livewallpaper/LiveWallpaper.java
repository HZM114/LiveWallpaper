package org.crazyit.livewallpaper;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.IBinder;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.Random;

public class LiveWallpaper extends WallpaperService {

    private Bitmap heart;
    @Override
    public Engine onCreateEngine() {
        heart = BitmapFactory.decodeResource(getResources(),R.drawable.heart);
        return new MyEngine();
    }
    class MyEngine extends Engine
    {
        //记录程序界面是否可见
        private boolean mVisible;
        //记录当前用户动作事件的发生位置
        private float mTouchX = -1;
        private float mTouchY = -1;
        //记录当前需要绘制的矩形的数量
        private int count = 1 ;
        //记录第一个矩形所需坐标变换的X,Y坐标的偏移
        private int originX = 50,originY = 50;
        //定义画笔
        private Paint mPaint = new Paint();
        Handler mHandler = new Handler();
        //定义一个周期性执行的任务
        private final Runnable drawTarget = new Runnable() {
            @Override
            public void run() {
                drawFrame();
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            mPaint.setARGB(76,0,0,255);
            mPaint.setAntiAlias(true);//去锯齿
            mPaint.setStyle(Paint.Style.FILL);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            //删除回调
            mHandler.removeCallbacks(drawTarget);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible)
            {
                drawFrame();
            }
            else
            {
                mHandler.removeCallbacks(drawTarget);
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep,
                                     int xPixelOffset, int yPixelOffset) {
            drawFrame();

        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (event.getAction()==MotionEvent.ACTION_MOVE){
                mTouchX = event.getX();
                mTouchY = event.getY();
            }
            else {
                mTouchX = -1;
                mTouchY = -1;
            }
            super.onTouchEvent(event);
        }
        //定义绘图的工具方法
        private void drawFrame()
        {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try
            {
                c = holder.lockCanvas();
                if (c != null)
                {
                    c.drawColor(0xffffffff);
                    //在触碰点绘制心型
                    drawTouchPoint(c);
                    mPaint.setAlpha(76);
                    c.translate(originX,originY);
                    //采用循环绘制count个矩形
                    for (int i = 0 ; i<count; i++){
                        c.translate(80,0);
                        c.scale(0.95f,0.95f);
                        c.rotate(20f);
                        c.drawRect(0,0,150,75,mPaint);

                    }
                }
            }
            finally {
                if (c != null) holder.unlockCanvasAndPost(c);

            }
            mHandler.removeCallbacks(drawTarget);
            if (mVisible){
                count++;
                if (count >= 50)
                {
                    Random rand = new Random();
                    count = 1 ;
                    originX += (rand.nextInt(60) -30);
                    originY += (rand.nextInt(60) -30);
                    try {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
                mHandler.postDelayed(drawTarget,100);
            }
        }
        private void drawTouchPoint(Canvas c)
        {
            if (mTouchX >= 0 && mTouchY >=0)
            {
                mPaint.setAlpha(255);
                c.drawBitmap(heart,mTouchX,mTouchY,mPaint);

            }
        }
    }
}