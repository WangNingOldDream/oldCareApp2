package com.demo.alarm.Page.Activity;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.demo.alarm.Dao.AlarmDao;
import com.demo.alarm.Database.BaseRoomDatabase;
import com.demo.alarm.Database.InstanceDatabase;
import com.demo.alarm.Entity.Alarm;
import com.demo.alarm.Page.Fragment.AlarmMePagerFragment;
import com.demo.oldcare.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "test";
    ViewPager viewPager;

    Notification notification;
    private MediaPlayer mMediaPlayer;
    NotificationManager manager;
    private Vibrator vibrator;
    private PowerManager.WakeLock mWakelock;
    private String content;
    BaseRoomDatabase baseRoomDatabase;
    AlarmDao alarmDao;
    AlarmMePagerFragment alarmMePagerFragment;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
//        Log.d(TAG, "onCreate: ");
    }

    private void init() {

        initViewPager();

        baseRoomDatabase = InstanceDatabase.getInstance(this);
        alarmDao = baseRoomDatabase.getAlarmDao();

        sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("channelId", "待办通知", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
        manager.createNotificationChannel(channel);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(this,
                        0,
                        new Intent(this, MainActivity2.class),
                        FLAG_IMMUTABLE);
        notification = new NotificationCompat.Builder(this, "channelId")
                .setContentTitle("标题")
                .setContentText("内容")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();
        //系统级notification后台应用待实现。
        sharedPreferences = getSharedPreferences("alarm", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getInt("ListStyle", -1) == -1) {
            editor.putInt("ListStyle", 1);
            editor.apply();
        }
    }
    private void initViewPager() {
        List<Fragment> list = new ArrayList<>();
        alarmMePagerFragment = new AlarmMePagerFragment();
        list.add(alarmMePagerFragment);
        viewPager = findViewById(R.id.MainPager);
        myViewpagerFragment fragment = new myViewpagerFragment(getSupportFragmentManager(), 0, list);
        viewPager.setAdapter(fragment);
    }
    @Override
    protected void onResume() {
//        Log.d(TAG, "onResume: ");
        super.onResume();
        if (sharedPreferences.getBoolean("RingRing", false)) {
            Toast.makeText(this, "调用AlarmMe----------", Toast.LENGTH_SHORT).show();
            AlarmMe();
            editor.putBoolean("RingRing", false);
            editor.apply();
        }
        // 唤醒屏幕
        acquireWakeLock();

    }
    public void AlarmMe() {
        baseRoomDatabase = InstanceDatabase.getInstance(this);
        alarmDao = baseRoomDatabase.getAlarmDao();
        List<Alarm> alarms = alarmDao.selectAll();
        for (Alarm a : alarms) {
            //System.currentTimeMillis() - a.getAlarmTimeMillis() < 1000 * 60
            if ((System.currentTimeMillis() - a.getAlarmTimeMillis() < 2000 * 60)&&(a.getState()==1)) {
                mMediaPlayer = MediaPlayer.create(this, R.raw.call_of_slience);
                mMediaPlayer.setLooping(true); // 设置是否对播放的音乐进行循环播放
                mMediaPlayer.start();
                content = a.getAlarmContent();//获取闹钟内容并修改状态
                a.setState(2);
                alarmDao.updateAlarm(a);
//                Notification(a);
                startVibrator();
                createDialog();
                break;
            }
        }
    }
    private void acquireWakeLock() {

        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire(10*60*1000L /*10 minutes*/);
        }
    }





    private void startVibrator() {
        // 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {500, 1000, 500, 1000}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 0);
    }

    private void createDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("闹钟")
                .setMessage(content)
                .setNegativeButton("关闭", (dialog, whichButton) -> {
                    mMediaPlayer.stop();
                    vibrator.cancel();
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
//    private Notification createNotification(Alarm a){
//        PendingIntent pendingIntent = PendingIntent
//            .getActivity(this,
//                    0,
//                    new Intent(this, MainActivity2.class),
//                    FLAG_IMMUTABLE);
//        Notification nt = new NotificationCompat.Builder(this, "channelId")
//                .setContentTitle("待办提醒")
//                .setContentText(a.getAlarmContent())
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true).build();
//        return nt;
//    }

//    public void Notification(Alarm a) {
//        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
//            notification=createNotification(a);
//            manager.notify(1, notification);
//        } else {
//            Toast.makeText(this, "先把通知权限打开~", Toast.LENGTH_SHORT).show();
//            Uri packageURI = Uri.parse("package:" + this.getPackageName());
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
//            startActivity(intent);
//        }
//    }

    public static class myViewpagerFragment extends FragmentPagerAdapter {
        List<Fragment> mFragments;

        public myViewpagerFragment(@NonNull FragmentManager fm, int behavior, List<Fragment> fragmentList) {
            super(fm, behavior);
            this.mFragments = fragmentList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release(); // 释放掉
        }
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        // 释放锁屏
        releaseWakeLock();
        super.onPause();

    }
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }
}