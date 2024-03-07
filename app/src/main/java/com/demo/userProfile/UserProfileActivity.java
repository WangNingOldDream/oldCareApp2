package com.demo.userProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.utils.ImageUtil;
import com.demo.oldcare.R;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvNickName,tvAccount,tvAge,tvGender,tvCity,tvHome,tvSchool,tvSign,tvBirthdayTime;


    private ImageView ivAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profilectivity);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }


    private void initData() {
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

        String age = getAgeByBirthDay(birthDayTime);

        tvAccount.setText(account);
        tvNickName.setText(nickName);
        tvAge.setText(age);
        tvHome.setText(city);
        tvSign.setText(sign);
        tvBirthdayTime.setText(birthDayTime);
        tvGender.setText(gender);
        tvCity.setText(city);
        ivAvatar.setImageBitmap(ImageUtil.base64ToImage(image64));

    }

    private String getAgeByBirthDay(String birthDayTime) {

        if (TextUtils.isEmpty(birthDayTime)) {
            return "";
        }

        try {
            int index = birthDayTime.indexOf("年");
            String result = birthDayTime.substring(0, index);

            int parseInt = Integer.parseInt(result);
            return String.valueOf(2024 - parseInt);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }


    private void initView() {
        tvAccount = findViewById(R.id.tv_account_text);
        tvNickName = findViewById(R.id.tv_nick_name);
        tvAge = findViewById(R.id.tv_age);
        tvHome = findViewById(R.id.tv_home_text);
        tvSign = findViewById(R.id.tv_sign_text);
        tvBirthdayTime = findViewById(R.id.tv_birth_time_text);
        tvGender = findViewById(R.id.tv_gender);
        tvCity = findViewById(R.id.tv_city);
        ivAvatar = findViewById(R.id.iv_avatar);
    }

    public void toEdit(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        SharedPreferences.Editor edit = spf.edit();
        edit.putBoolean("isLogin", false);
        edit.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}