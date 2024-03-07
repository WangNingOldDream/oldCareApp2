package com.demo.alarm.Page.Activity;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MidActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("RingRing", true);//标记响过
        editor.apply();
        Intent intent = new Intent(this, MainActivity2.class);
        intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("alarmMe", 1); // 设置启动的模式
        startActivity(intent);
    }
}