package com.demo.utils;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TakePhotoUtils {
    private static final TakePhotoUtils TAKE_PHOTO_UTILS = new TakePhotoUtils();
    public static final int REQUEST_PERMISSION_CODE = 10010;
    private String authority="com.example.userprofile.fileProvider";
    private String imgPath;
    public static final int REQUEST_CODE_TAKE=007;

    private TakePhotoUtils() {
    }
    public static TakePhotoUtils getInstance() {
        return TAKE_PHOTO_UTILS;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void dispatchTakePictureIntent(Activity activity,File file) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)) {
            //拍照方法
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                if (file != null) {
                    Uri photoUri = FileProvider.getUriForFile(activity, authority, file);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    activity.startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE);
                }
            }
        } else {
            String[] perms;
            //提示用户开户权限   拍照和读写sd卡权限
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                perms = new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.CAMERA};
            }else{
                perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

            }
            ActivityCompat.requestPermissions(activity, perms, 10010);
        }
    }


    public void dispatchTakePictureIntent(Fragment fragment, File file) {
        //requireContext赋予权限
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(fragment.requireContext(), android.Manifest.permission.CAMERA)) {
            //拍照方法
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (takePictureIntent.resolveActivity(fragment.requireActivity().getPackageManager()) != null) {
                if (file != null) {
                    Uri photoUri = FileProvider.getUriForFile(fragment.requireContext(), "com.example.userprofile.fileProvider", file);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//在fragment页面拍照需要使用fragment中的startActivityForResult，不然不会走fragment中的回调
                    fragment.startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE);
                }
//            }
        } else {
            String[] perms;
            //提示用户开户权限   拍照和读写sd卡权限
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
                perms = new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.CAMERA};
            }else{
                perms = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            }
            //在fragment中申请权限时候需要使用fragment中的requestPermissions方法不然不会走fragment中的回调
            fragment.requestPermissions(perms, REQUEST_PERMISSION_CODE);
        }
    }
    private File createImageFile(Activity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir("");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imgPath = image.getAbsolutePath();
        Log.e("打印图片路径", imgPath);
        return image;
    }
}

