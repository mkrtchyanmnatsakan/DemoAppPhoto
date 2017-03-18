package com.mno.example.copyart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by m-dev on 3/17/17.
 */

public class CustomView extends View {


    Bitmap image;
    int screenHeight;
    int screenWidth;
    Paint paint;
    GestureDetector gestures;
    ScaleGestureDetector scaleGesture;
    float scale = 1.0f;
    float horizontalOffset, verticalOffset;

    int NORMAL = 0;
    int ZOOM = 1;
    int DRAG = 2;
    boolean isScaling = false;
    float touchX, touchY;
    int mode = NORMAL;

    public CustomView(Context context, int r) {
        super(context);
        //initializing variables
        image = BitmapFactory.decodeResource(getResources(),
                r);
        //This is a full screen view
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);

        scaleGesture = new ScaleGestureDetector(getContext(),new ScaleListener());
        gestures = new GestureDetector(getContext(), new GestureListener());
        mode = NORMAL;
        initialize();
    }

    //Best fit image display on canvas
    private void initialize() {
        float imgPartRatio = image.getWidth() / (float) image.getHeight();
        float screenRatio = (float) screenWidth / (float) screenHeight;

        if (screenRatio > imgPartRatio) {
            scale = ((float) screenHeight) / (float) (image.getHeight()); // fit height
            horizontalOffset = ((float) screenWidth - scale
                    * (float) (image.getWidth())) / 2.0f;
            verticalOffset = 0;
        } else {
            scale = ((float) screenWidth) / (float) (image.getWidth()); // fit width
            horizontalOffset = 0;
            verticalOffset = ((float) screenHeight - scale
                    * (float) (image.getHeight())) / 2.0f;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.WHITE);
        if(mode == DRAG || mode == NORMAL) {
            //This works perfectly as expected
            canvas.translate(horizontalOffset, verticalOffset);
            canvas.scale(scale, scale);
            canvas.drawBitmap(image, getMatrix(), paint);
        }
        else if (mode == ZOOM) {
            //PROBLEM AREA - when applying pinch zoom,
            //the image jumps to a position abruptly
            canvas.scale(scale, scale, touchX, touchY);
            canvas.drawBitmap(image, getMatrix(), paint);
        }
        canvas.restore();
    }

    public class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactorNew = detector.getScaleFactor();
            if (detector.isInProgress()) {
                touchX = detector.getFocusX();
                touchY = detector.getFocusY();
                scale *= scaleFactorNew;
                invalidate(0, 0, screenWidth, screenHeight);
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            isScaling = true;
            mode=ZOOM;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mode = NORMAL;
            isScaling = false;
        }

    }

    public class GestureListener implements GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onDown(MotionEvent e) {
            isScaling = false;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (!isScaling) {
                mode = DRAG;
                isScaling = false;
                horizontalOffset -= distanceX;
                verticalOffset -= distanceY;
                invalidate(0, 0, screenWidth, screenHeight);
            } else {
                mode = ZOOM;
                isScaling = true;
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGesture.onTouchEvent(event);
        gestures.onTouchEvent(event);
        return true;
    }
}
