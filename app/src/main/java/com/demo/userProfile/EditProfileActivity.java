package com.demo.userProfile;

import static com.demo.btmNav.activity.BotNavActivity.NAV3;
import static com.demo.utils.ImageUtil.openPhotoAlbum;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.demo.btmNav.activity.BotNavActivity;
import com.demo.utils.ImageUtil;
import com.demo.oldcare.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    public static final int REQUEST_CODE_TAKE = 1;
    public static final int REQUEST_CODE_CHOOSE = 0;
    private EditText etNickName, etAccount, etSign;
    private TextView tvBirthDayTime;
    private RadioButton rbBoy, rbGirl;
    private AppCompatSpinner spinnerCity;
    private ImageView ivAvatar;

    private String[] cities;

    private int selectedCityPosition;
    private String selectedCity;

    private String birthDay;
    private String birthDayTime;

    private Uri imageUri;
    private String imageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initView();
        initData();
        initEvent();
    }
    private void initView() {

        etAccount = findViewById(R.id.et_account_text);
        etNickName = findViewById(R.id.et_nick_name);
        etSign = findViewById(R.id.et_sign_text);

        tvBirthDayTime = findViewById(R.id.tv_birth_time_text);
        rbBoy = findViewById(R.id.rb_boy);
        rbGirl = findViewById(R.id.rb_girl);
        spinnerCity = findViewById(R.id.sp_city);
        ivAvatar = findViewById(R.id.iv_avatar);
    }

    private void initData() {
        cities = getResources().getStringArray(R.array.cities);
        getDataFromSpf();

    }
    private void getDataFromSpf() {
        SharedPreferences spfRecord = getSharedPreferences("spfRecord", MODE_PRIVATE);
        String account = spfRecord.getString("account", "");
        String nickName = spfRecord.getString("nick_name", "");
        String city = spfRecord.getString("city", "");
        String gender = spfRecord.getString("gender", "");
        String birthDayTime = spfRecord.getString("birth_day_time", "");
        String sign = spfRecord.getString("sign", "");
        String image64 = spfRecord.getString("image_64", "");

        etAccount.setText(account);
        etNickName.setText(nickName);
        etSign.setText(sign);
        tvBirthDayTime.setText(birthDayTime);
        ivAvatar.setImageBitmap(ImageUtil.base64ToImage(image64));

        if (TextUtils.equals("男", gender)) {
            rbBoy.setChecked(true);
        }

        if (TextUtils.equals("女", gender)) {
            rbGirl.setChecked(true);
        }

        for (int i = 0; i < cities.length; i++) {
            if (TextUtils.equals(cities[i], city)) {
                selectedCityPosition = i;
                break;
            }
        }

        spinnerCity.setSelection(selectedCityPosition);

    }
    private void initEvent() {


        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCityPosition = position;
                selectedCity = cities[position];
                Log.d(TAG, "onItemSelected: --------position--------" + position);
                Log.d(TAG, "onItemSelected: ---------selectedCity-------" + selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvBirthDayTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        int realMonth = month + 1;
                        birthDay = year + "年" + realMonth + "月" + dayOfMonth + "日";
                        Log.d(TAG, "onItemSelected: --------birthDay--------" + birthDay);

                        popTimePick();

                    }
                }, 2000, 10, 23).show();
            }
        });

    }
    private void popTimePick() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                birthDayTime = birthDay + hourOfDay + "时" + minute + "分";
                Log.d(TAG, "onItemSelected: --------birthDayTime--------" + birthDayTime);
                tvBirthDayTime.setText(birthDayTime);
            }
        }, 12, 36, true).show();
    }

    public void save(View view) {

        String account = etAccount.getText().toString();
        String sign = etSign.getText().toString();
        String nickName = etNickName.getText().toString();
        String gender = "男";
        if (rbBoy.isChecked()) {
            gender = "男";
        }
        if (rbGirl.isChecked()) {
            gender = "女";
        }

        SharedPreferences spfRecord = getSharedPreferences("spfRecord", MODE_PRIVATE);
        SharedPreferences.Editor edit = spfRecord.edit();
        edit.putString("account", account);
        edit.putString("nick_name", nickName);
        edit.putString("sign", sign);
        if(birthDayTime!=null)
            edit.putString("birth_day_time", birthDayTime);
        edit.putString("city", selectedCity);
        edit.putString("gender", gender);
        if(imageBase64!=null)
            edit.putString("image_64", imageBase64);
        edit.apply();
        Intent intent =new Intent(EditProfileActivity.this, BotNavActivity.class);
        intent.putExtra("nav",NAV3);
        startActivity(intent);
        this.finish();
    }

    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // 真正的执行去拍照
            doTake();
        } else {
            // 去申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
    public void choosePhoto(){
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
//            // 真正的执行去拍照
//            doChoose();
//        } else {
            // 去申请权限
//            Toast.makeText(this, "获取相册权限", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
//       }
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    private void doChoose() {
       openPhotoAlbum(this);
    }

    private void handleSelectedImage(ImageView img  ,Intent data) {
        if (data == null) return;
        Uri uri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            img.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doTake();
            } else {
                Toast.makeText(this, "你没有获得摄像头权限~", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void doTake() {
        File imageTemp = new File(getExternalCacheDir(), "imageOut.jpeg");
        if (imageTemp.exists()) {
            imageTemp.delete();
        }
        try {
            imageTemp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // contentProvider
        imageUri = FileProvider.getUriForFile(this, "com.example.userprofile.fileProvider", imageTemp);
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_TAKE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE) {
            if (resultCode == RESULT_OK) {
                // 获取拍摄的照片
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ivAvatar.setImageBitmap(bitmap);
                    String imageToBase64 = ImageUtil.imageToBase64(bitmap);
                    imageBase64 = imageToBase64;
                } catch (FileNotFoundException e) {

                }
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE) {
            if (resultCode == RESULT_OK) {
                if (data == null) return;
                Uri uri = data.getData();
                // 获取拍摄的照片
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ivAvatar.setImageBitmap(bitmap);
                    String imageToBase64 = ImageUtil.imageToBase64(bitmap);
                    imageBase64 = imageToBase64;
                } catch (FileNotFoundException e) {
                }
            }
        }
    }
}