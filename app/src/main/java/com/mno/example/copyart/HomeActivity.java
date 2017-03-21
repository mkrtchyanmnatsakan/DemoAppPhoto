package com.mno.example.copyart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.flurgle.camerakit.CameraListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mno.example.copyart.SQLite.CopyArtDatabaseHandler;
import com.mno.example.copyart.model.Picture;
import com.mvc.imagepicker.ImagePicker;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    ImageView currentImg;

    MapView preview;
    private boolean isOpenFlesh = true;
    private SurfaceHolder surfaceHolder;
    private Bitmap alertBitmap;
    private Canvas canvas;
    private ArrayList<Picture> pictureArrayList;
    private CopyArtDatabaseHandler db;
    ImageView transparentEffectImg;
    FloatingActionButton photoFab;
    FloatingActionButton albumFab;
    FloatingActionButton cameraFab;
    FloatingActionButton automaticRotationFab;
    FloatingActionButton rotationBy5Fab;
    FloatingActionButton replayBy5Fab;
    FloatingActionButton plusFab;
    FloatingActionButton minusFab;
    FloatingActionButton fleshFab;
    FloatingActionMenu menu;
    private Bitmap dinoBMP;

    PhotoViewAttacher mAttacher;
    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    private Toast mCurrentToast;

    private final Handler handler = new Handler();
    private boolean rotating = false;
    private int i = 127;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        transparentEffectImg = (ImageView) findViewById(R.id.transparet_efect_imageView);


        dinoBMP = BitmapFactory.decodeResource(getResources(), R.drawable.dino);

        Bitmap scaledBitmap = scaleDown(dinoBMP, 1100, true);


//        narutoBMP.setWidth(200);
//        narutoBMP.setHeight(200);

        transparentEffectImg.setImageBitmap(scaledBitmap);

        transparentEffectImg.getLayoutParams().height = getWindowManager().getDefaultDisplay().getHeight();
        transparentEffectImg.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth();
        transparentEffectImg.setScaleType(ImageView.ScaleType.FIT_START);
        mAttacher = new PhotoViewAttacher(transparentEffectImg);
        // mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
        mAttacher.setOnSingleFlingListener(new SingleFlingListener());
        mAttacher.setScaleType(ImageView.ScaleType.CENTER);
        mAttacher.setZoomable(true);


        //mAttacher.setScaleLevels(10f,30f,80f);


        ImagePicker.setMinQuality(800, 600);
        preview = (MapView) findViewById(R.id.surfaceView);
        preview.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                // Create a bitmap
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                Log.e("BBBBB", "VVV" + result);
                onPic(result);

            }
        });

//        photoFab = (FloatingActionButton) findViewById(R.id.photo_fab);
//        photoFab.setOnClickListener(this);
        cameraFab = (FloatingActionButton) findViewById(R.id.camera_fab);
        cameraFab.setOnClickListener(this);
        albumFab = (FloatingActionButton) findViewById(R.id.my_album_fab);
        albumFab.setOnClickListener(this);

        automaticRotationFab = (FloatingActionButton) findViewById(R.id.rotation_fab);
        automaticRotationFab.setOnTouchListener(this);

        rotationBy5Fab = (FloatingActionButton) findViewById(R.id.rotation_by_5_fab);
        rotationBy5Fab.setOnClickListener(this);

        replayBy5Fab = (FloatingActionButton) findViewById(R.id.replay_by_5_fab);
        replayBy5Fab.setOnClickListener(this);

        plusFab = (FloatingActionButton) findViewById(R.id.plus_fab);
        plusFab.setOnClickListener(this);

        minusFab = (FloatingActionButton) findViewById(R.id.minus_fab);
        minusFab.setOnClickListener(this);


        db = new CopyArtDatabaseHandler(this);
        // countOfScans = db.getCountofPictures();
        menu = (FloatingActionMenu) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        //  galleryFab = (FloatingActionButton) findViewById(R.id.gallery_fab);
        currentImg = (ImageView) findViewById(R.id.current_scan_img);
        currentImg.setOnClickListener(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.photo_fab:
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,
//                        "Select Picture"), 1);
//                break;

            case R.id.rotation_fab:
                toggleRotation();
                break;

            case R.id.rotation_by_5_fab:
                mAttacher.setRotationBy(-5);
                break;

            case R.id.replay_by_5_fab:
                mAttacher.setRotationBy(5);
                break;
            case R.id.my_album_fab:
                ImagePicker.pickImage(this, "Select your image:");
                break;

            case R.id.minus_fab:

                i = i-10;

                transparentEffectImg.setAlpha((float)i/255);



//
//                AlphaAnimation alpha = new AlphaAnimation(-0.5F, -0.01F); // change values as you want
//                alpha.setDuration(0); // Make animation instant
//                alpha.setFillAfter(true); // Tell it to persist after the animation ends
//// And then on your imageview
//                transparentEffectImg.startAnimation(alpha);
//
//                mAttacher = new PhotoViewAttacher(transparentEffectImg);
                break;

            case R.id.plus_fab:

                i =i+10;

                transparentEffectImg.setAlpha((float)i/255);



//                AlphaAnimation alphaPlus = new AlphaAnimation(0.5F, 0.01F); // change values as you want
//                alphaPlus.setDuration(0); // Make animation instant
//                alphaPlus.setFillAfter(true); // Tell it to persist after the animation ends
//// And then on your imageview
//                transparentEffectImg.startAnimation(alphaPlus);
                break;

            case R.id.camera_fab:
                v.setAlpha(0.5f);
                preview.captureImage();
                // to do pic

                break;

        }

    }


    @Override
    protected void onResume() {
        preview.start();
        super.onResume();

    }


    public void onPic(Bitmap bitmap) {
        currentImg.setImageBitmap(bitmap);
        //////////////////////
        alertBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());// on pic
        canvas = new Canvas(alertBitmap);
        Paint paint = new Paint();
        paint.setAlpha(42);

        canvas.drawBitmap(alertBitmap, 0, 0, paint);


        Bitmap b = Bitmap.createBitmap(alertBitmap);
        Canvas c = new Canvas(b);
        preview.draw(c);


        ///////////////////

    }

    @Override
    protected void onPause() {
        preview.stop();
        super.onPause();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActvityReasa", "true +++");
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);// Galery
        if (bitmap != null) {
            transparentEffectImg.setImageBitmap(bitmap);
            Log.e("bitmap", bitmap.getGenerationId() + "+++");
            Log.e("bitmap", "notNull+++");
            /*Bitmap transparentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.naruto);
            Paint paint = new Paint();
            paint.setAlpha(42);

            canvas.drawBitmap(bitmap, 0, 0, paint);


            currentImg.setImageBitmap(bitmap);*/


        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rotating = false;
                toggleRotation();
                break;
            case MotionEvent.ACTION_UP:
                rotating = true;
                toggleRotation();

        }
        return false;
    }






    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }

        @Override
        public void onOutsidePhotoTap() {
            showToast("You have a tap event on the place where out of the photo.");
        }
    }

    private void showToast(CharSequence text) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

    private void toggleRotation() {
        if (rotating) {
            handler.removeCallbacksAndMessages(null);
        } else {
            rotateLoop();
        }
        rotating = !rotating;
    }

    private void rotateLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAttacher.setRotationBy(1);
                rotateLoop();
            }
        }, 15);
    }


    private class MatrixChangeListener implements PhotoViewAttacher.OnMatrixChangedListener {


        @Override
        public void onMatrixChanged(RectF rect) {
            // mCurrMatrixTv.setText(rect.toString());
        }
    }

    private class SingleFlingListener implements PhotoViewAttacher.OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (BuildConfig.DEBUG) {

//                transparentEffectImg.setMaxWidth((int) velocityX);
//                transparentEffectImg.setMaxHeight((int) velocityY);
//                mAttacher.setMaximumScale(velocityX);
                // Log.d("PhotoView", String.format(FLING_LOG_STRING, velocityX, velocityY));
            }
            return true;
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

}
