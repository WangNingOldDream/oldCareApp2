package com.demo.utils;

import static com.demo.userProfile.EditProfileActivity.REQUEST_CODE_CHOOSE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtil {
    public static void openPhotoAlbum(Activity activity) {
        Intent intent;
        //安卓13以上打开相册.
        intent = new Intent("android.provider.action.PICK_IMAGES");
        intent.setType("images/*");//设置只显示图片
        /**
         * 设置选择照片的个数,默认1张时可不添加该属性,大于1的时候再设置.
         * 可指定图片数量上限为最大数字,调用 MediaStore.getPickImagesMaxLimit().
         */
        //intent.putExtra("android.provider.extra.PICK_IMAGES_MAX", MediaStore.getPickImagesMaxLimit());
        activity.startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }
    public static String imageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] buffer = byteArrayOutputStream.toByteArray();
        String baseStr = Base64.encodeToString(buffer, Base64.DEFAULT);
        return baseStr;
    }

    public static Bitmap base64ToImage(String bitmap64) {
        byte[] bytes = Base64.decode(bitmap64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
    @NonNull
    public static String getTimeStampName() {
        String timeStamp = new SimpleDateFormat("yyyy年MM月dd日HH时mm分").format(new Date());
        String fileName = timeStamp + ".jpg";// 照片命名
        return fileName;
    }

}
