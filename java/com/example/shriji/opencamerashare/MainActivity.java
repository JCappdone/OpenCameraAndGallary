package com.example.shriji.opencamerashare;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1001;
    private static final int SELECT_IMAGE = 1003;
    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.btnCapture)
    Button mBtnCapture;
    @BindView(R.id.btnsaveInExternal)
    Button mBtnsaveInExternal;
    @BindView(R.id.btnShare)
    Button mBtnShare;
    private Bitmap mPhoto;
    private int MY_PERMISSIONS_REQUEST = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Toast.makeText(this, "You have Permission", Toast.LENGTH_SHORT).show();
        } else {
            checkRuntimePermission();
        }
    }

    private void checkRuntimePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick({R.id.btnCapture, R.id.btnsaveInExternal, R.id.btnShare})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCapture:
                showCameraIntentToCapturePhoto();
            /*    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                break;
            case R.id.btnsaveInExternal:
                String partFilename = currentDateFormat();
                storeCameraPhotoInSDCard(mPhoto, partFilename);

                break;
            case R.id.btnShare:

/*

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                sharingIntent.setType("image*/
/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
                startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));

*/
/*

                //open gallery
                Intent intent = new Intent();
                intent.setType("image*/
/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
*/


                partFilename = currentDateFormat();
                String storeFilename = "photo_" + partFilename + ".jpg";
                Bitmap mBitmap = getImageFileFromSDCard(storeFilename);
                File imageFile = new File(storeFilename);
                Uri imageUri = Uri.fromFile(imageFile);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, PATH);

                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share images to.."));
                break;
        }
    }


    private void showCameraIntentToCapturePhoto() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());
       // cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private Uri getImageUri() {
        File file = new File(creteFolderPath("photo.jpg"));
        return Uri.fromFile(file);
    }
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.temp/";


    // create folder if not created
    public static String creteFolderPath(String filename) {
        File folder = new File(PATH);
        boolean success = false;
        if (!folder.exists()) {
            Log.e("Global", "creteFolderPath folder not exists");
            success = folder.mkdir();
            Log.e("Global", "status==" + success);
        }
        if (!success) {
            Log.e("Global", "creteFolderPath false");
            return PATH + filename;

        } else {
            Log.e("Global", "Folder created");
        }
        return PATH;
    }






    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            mPhoto = (Bitmap) data.getExtras().get("data");

/*            String partFilename = currentDateFormat();
            storeCameraPhotoInSDCard(mPhoto, partFilename);


            // display the image from SD Card to ImageView Control
            String storeFilename = "photo_" + partFilename + ".jpg";
            Bitmap mBitmap = getImageFileFromSDCard(storeFilename);*/
            mImageView.setImageBitmap(mPhoto);
        }


        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED)        {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "photo_" + currentDate + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
