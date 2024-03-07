package com.demo.alarm.BroadcastRec;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.demo.alarm.Page.Activity.MainActivity2;
import com.demo.oldcare.R;

public class AlarmBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String content=intent.getStringExtra("content");
        System.out.println("闹钟响了");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences sharedPreferences = context.getSharedPreferences("alarm", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("RingRing", true);//标记响过
        editor.apply();
        // 取消闹钟
        am.cancel(PendingIntent.getBroadcast(context, getResultCode(), new Intent(context, AlarmBroadcast.class), FLAG_IMMUTABLE));
        triggerNotification(context,content);
//        Intent i = new Intent(context, MidActivity.class); // 要启动的类
//        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);
    }




    @SuppressLint("MissingPermission")
    private void triggerNotification(Context context, String content) {
        // 创建通知渠道
        createNotificationChannel(context);
        Intent intent =new Intent(context, MainActivity2.class);
        intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("alarmMe", 1); // 设置启动的模式
        // 构建通知
        @SuppressLint("LaunchActivityFromNotification")
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context,
                        0,intent
                        ,
                        FLAG_IMMUTABLE);
        @SuppressLint("LaunchActivityFromNotification")
        Notification nt = new NotificationCompat.Builder(context, "channelId")
                .setContentTitle("待办提醒")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH).build();

        // 发送通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()){
            notificationManager.notify(1, nt);
        } else {
            Toast.makeText(context, "先把通知权限打开~", Toast.LENGTH_SHORT).show();
            Uri packageURI = Uri.parse("package:" + context.getPackageName());
            Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(context,intent2,null);
        }
    }

    private void createNotificationChannel(Context context) {

        CharSequence name = "channel_name";
        String description = "channel_description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


}
