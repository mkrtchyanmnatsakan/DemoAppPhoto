package com.mno.example.copyart;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback, Camera.PreviewCallback, Camera.PictureCallback,Camera.AutoFocusCallback  {

    FloatingActionButton photoFab;
    FloatingActionButton albumFab;
    FloatingActionButton cameraFab;
    FloatingActionButton fleshFab;
    FloatingActionMenu menu;

    ImageView currentImg;

    MapView preview;
    private Bitmap bmp;
    private boolean isSingleCameraMode = true;

    private Camera.Parameters cameraParameters;
    private boolean isOpenFlesh = true;
    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private Bitmap alertBitmap;
    private Canvas canvas;
    private ArrayList<Picture> pictureArrayList;
    private CopyArtDatabaseHandler db;
    private int countOfScans = 0;

    private static final String SAVE_SCAN_PATH = "new_scan_list";


    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImagePicker.setMinQuality(600,600);






      //  cameraFab = (FloatingActionButton) findViewById(R.id.camera_fab);
      //  cancelFab = (FloatingActionButton) findViewById(R.id.cancel_fab);
       // fleshFab = (FloatingActionButton) findViewById(R.id.flesh_fab);
      //  fleshFab.setOnClickListener(this);
//        cancelFab.setOnClickListener(this);


    }

    private void saveAllPictures(ArrayList<Picture> pictures){

        if (pictures != null) {
            for (Picture picter : pictures) {
                db.addPictures(picter);
            }
        }
    }


    public static void saveCurrentPictureInfo(ArrayList<Picture> pictureList, Context context) {

        Log.e("pictureList", pictureList.get(0).getName() +" ++++");

        try {
            FileOutputStream fos = context.openFileOutput(SAVE_SCAN_PATH, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(pictureList);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Picture> getCurrentPictureInfo(Context context) {
        ArrayList<Picture> pictureList = null;
        try {
            FileInputStream fis = context.openFileInput(SAVE_SCAN_PATH);
            ObjectInputStream is = new ObjectInputStream(fis);
            pictureList = (ArrayList<Picture>) is.readObject();
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Picture scan : pictureList) {
            //showLog("SCANUS", scan.getLoacalImageUri());
        }
        return pictureList;
    }

    public void requestForCameraPermission() {
        final String permission = android.Manifest.permission.CAMERA;
        final String permissionR = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        final String permissionW = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionRationaleDialog("Test", permission, permissionR, permissionW);
            } else {
                requestForPermission(permission, permissionR, permissionW);
            }
        } else {

        }
    }




    private void showPermissionRationaleDialog(final String message, final String permission, final String permissionR, final String permissionW) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestForPermission(permission, permissionR, permissionW);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void requestForPermission(final String permission, final String permissionR, final String permissionW) {
        ActivityCompat.requestPermissions(this, new String[]{permission, permissionR, permissionW}, 1);
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

//                Intent intentAlbum = new Intent();
//                intentAlbum.setType("image/*");
//                intentAlbum.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                HomeActivity.this.startActivity(intentAlbum);

                ImagePicker.pickImage(this, "Select your image:");

                break;

            case R.id.camera_fab:
                saveCurrentPictureInfo(pictureArrayList,this);
                saveAllPictures(getCurrentPictureInfo(this));

            break;



        //    case R.id.cancel_fab:
              //  menu.close(true);
        //        break;

       //     case R.id.flesh_fab:
                //cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
         //       break;


        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {

            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;

        ViewGroup.LayoutParams lp = preview.getLayoutParams();


        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(90);
        } else {
            camera.setDisplayOrientation(0);
        }

        preview.setLayoutParams(lp);
        camera.startPreview();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    protected void onResume() {



        super.onResume();

        final String permission = android.Manifest.permission.CAMERA;
        final String permissionR = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        final String permissionW = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permission)) {
                showPermissionRationaleDialog("Test", permission, permissionR, permissionW);
            } else {
                requestForPermission(permission, permissionR, permissionW);
            }
        } else {
            launch();
        }


    }

    private void changeModeCamera(boolean isSingleMode) {
        if (isSingleMode) {
            isSingleCameraMode = true;


            pictureArrayList = new ArrayList<>();





        } else {
            isSingleCameraMode = false;
            pictureArrayList = new ArrayList<>();

        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if (isSingleCameraMode) {
            pictureArrayList = null;
            pictureArrayList = new ArrayList<>();
        }




        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];
        bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        bmp = rotateBitmap(bmp, 90);
        saveToInternalStorage(bmp);
        currentImg.setImageBitmap(bmp);

        //////////////////////


        alertBitmap = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),bmp.getConfig());

        canvas = new Canvas(alertBitmap);
        Paint paint = new Paint();
        paint.setAlpha(42);

        canvas.drawBitmap(bmp,0,0, paint);



        Bitmap b = Bitmap.createBitmap(alertBitmap);
        Canvas c = new Canvas(b);
        preview.draw(c);


        ///////////////////


        camera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.e("onPause","true");
/*
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }*/
    }

    private void launch() {

        preview = (MapView) findViewById(R.id.surfaceView);
        getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        requestForCameraPermission();

        camera = Camera.open();
        cameraParameters = camera.getParameters();

        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();

        bestSize = sizeList.get(0);
        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
            /*if (sizeList.get(i).height > bestSize.height) {
                bestSize = sizeList.get(i);
            }*/
        }

        List<Integer> supportedPreviewFormats = cameraParameters.getSupportedPreviewFormats();
        Iterator<Integer> supportedPreviewFormatsIterator = supportedPreviewFormats.iterator();
        while (supportedPreviewFormatsIterator.hasNext()) {
            Integer previewFormat = supportedPreviewFormatsIterator.next();
            if (previewFormat == ImageFormat.YV12) {
                cameraParameters.setPreviewFormat(previewFormat);
            }

        }

        cameraParameters.setPreviewSize(bestSize.width, bestSize.height);

        cameraParameters.setPictureSize(bestSize.width, bestSize.height);
        // param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);// to do
        try {
            camera.setParameters(cameraParameters);
            isOpenFlesh = true;

        } catch (Exception e) {
        }



        photoFab = (FloatingActionButton) findViewById(R.id.photo_fab);
        photoFab.setOnClickListener(this);

        cameraFab = (FloatingActionButton) findViewById(R.id.camera_fab);
        cameraFab.setOnClickListener(this);



        albumFab = (FloatingActionButton) findViewById(R.id.my_album_fab);
        albumFab.setOnClickListener(this);

        db = new CopyArtDatabaseHandler(this);
        countOfScans = db.getCountofPictures();



        menu = (FloatingActionMenu) findViewById(R.id.menu);
        menu.setOnClickListener(this);
        //  galleryFab = (FloatingActionButton) findViewById(R.id.gallery_fab);
        currentImg = (ImageView) findViewById(R.id.current_scan_img);

        currentImg.setOnClickListener(this);


    }

    private void saveToInternalStorage(Bitmap bitmapImage) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/CameraAndroid");
        myDir.mkdirs();
        String path = "CurrentImg";
        String fname = path + ".jpg";
        File file = new File(myDir, fname);
        setResult(Const.update, new Intent().putExtra(Const.bitmapImage, file.toString()));
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Picture currentPicture = new Picture(path, Const.STANDART_IMAGE_NAME + (db.getCountofPictures() + pictureArrayList.size()), file.toURI().toASCIIString());// to do
        Log.e("currentPic", currentPicture.getPath());
        pictureArrayList.add(currentPicture);




    }


    private void soundCamera() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mp = MediaPlayer.create(HomeActivity.this, R.raw.camera_click);
                mp.start();
            }
        });
        thread.run();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("onActvityReasa","true +++");

        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);



        if(bitmap!=null && canvas!=null && alertBitmap!=null){
            preview.setImage(bitmap);
            Log.e("bitmap",bitmap.getGenerationId() + "+++");
            Log.e("bitmap", "notNull+++");
            Bitmap transparentBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.naruto);/** to do*/
            Paint paint = new Paint();
            paint.setAlpha(42);

            canvas.drawBitmap(bitmap, 0,0, paint);

            saveToInternalStorage(alertBitmap);

            currentImg.setImageBitmap(alertBitmap);



        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        soundCamera();
        if (success) {

            Toast.makeText(this, "ok Check", Toast.LENGTH_SHORT).show();
            camera.takePicture(null, null, this);

            if (!isSingleCameraMode) {

            }
        }
    }
}
