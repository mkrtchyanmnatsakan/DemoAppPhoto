package com.mno.example.copyart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.flurgle.camerakit.CameraListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mno.example.copyart.SQLite.CopyArtDatabaseHandler;
import com.mno.example.copyart.model.Picture;
import com.mvc.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
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
    FloatingActionButton fleshFab;
    FloatingActionMenu menu;
    private Bitmap narutoBMP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        transparentEffectImg = (ImageView) findViewById(R.id.transparet_efect_imageView);
        narutoBMP = BitmapFactory.decodeResource(getResources(), R.drawable.naruto);
        transparentEffectImg.setImageBitmap(narutoBMP);
        ImagePicker.setMinQuality(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
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

        photoFab = (FloatingActionButton) findViewById(R.id.photo_fab);
        photoFab.setOnClickListener(this);

        cameraFab = (FloatingActionButton) findViewById(R.id.camera_fab);
        cameraFab.setOnClickListener(this);


        albumFab = (FloatingActionButton) findViewById(R.id.my_album_fab);
        albumFab.setOnClickListener(this);

        db = new CopyArtDatabaseHandler(this);
        // countOfScans = db.getCountofPictures();


        menu = (FloatingActionMenu) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        //  galleryFab = (FloatingActionButton) findViewById(R.id.gallery_fab);
        currentImg = (ImageView) findViewById(R.id.current_scan_img);

        currentImg.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.photo_fab:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);
                break;

            case R.id.my_album_fab:
                ImagePicker.pickImage(this, "Select your image:");

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
        if (bitmap != null ) {
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

}
