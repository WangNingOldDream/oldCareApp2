package com.demo.btmNav.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

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
import com.demo.alarm.Page.Activity.MainActivity2;
import com.demo.alarm.Page.Fragment.AlarmMePagerFragment;
import com.demo.oldcare.R;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.demo.oldcare.R;
import com.demo.btmNav.adapter.MyFragmentStateVPAdapter;
import com.demo.btmNav.fragment.StateTabFragment;
import com.demo.btmNav.fragment.UserProfileFragment;
import com.demo.btmNav.fragment.VPFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class BotNavActivity extends AppCompatActivity {
    private static final String TAG = "BotNavActivity";
    private static final String[] title={"近态","定时","体检","个人"};
    public static final int REQUSEST_CODE = 1;
    public static final int NAV0=0,NAV1=1,NAV2=2,NAV3=3;
    private ViewPager mViewPager;
    private int navIndex=0;
    private BottomNavigationView mBottomNavigationView;
    private MyFragmentStateVPAdapter mStateVPAdapter;
    private List<Fragment> mFragmentList;
    private ActionBar actionBar;
    private Menu mMenu;
    private MenuItem capture;
    private MenuItem clock;
    private StateTabFragment fragmentState;
    private ViewPager viewPager;

    private Notification notification;
    private MediaPlayer mMediaPlayer;
    private NotificationManager manager;
    private Vibrator vibrator;
    private PowerManager.WakeLock mWakelock;
    private String content;
    private BaseRoomDatabase baseRoomDatabase;
    private AlarmDao alarmDao;
    private AlarmMePagerFragment alarmMePagerFragment;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v_p_fragment_botoom_nav);

        mViewPager = findViewById(R.id.vp);
        mBottomNavigationView = findViewById(R.id.bottom_menu);
        initData();

        mStateVPAdapter = new MyFragmentStateVPAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mStateVPAdapter);
        mViewPager.setCurrentItem(navIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
//                navIndex=position;
                Log.d(TAG, "onPageSelected: "+navIndex);
                onResponseSelected(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: ");
                int itemId = item.getItemId();
                if (itemId == R.id.menu_state) {
                    navIndex=0;
                    mViewPager.setCurrentItem(0);
                } else if (itemId == R.id.menu_time) {
                    navIndex=1;
                    mViewPager.setCurrentItem(1);
                } else if (itemId == R.id.menu_exam) {
                    navIndex=2;
                    mViewPager.setCurrentItem(2);
                } else if (itemId == R.id.menu_people) {
                    navIndex=3;
                    mViewPager.setCurrentItem(3);
                }
                return true;
            }
        });
        navIndex=getIntent().getIntExtra("nav",NAV0);
//        mViewPager.setCurrentItem(navIndex);
        Log.d(TAG, "onCreate: "+navIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        mMenu=menu;
        Log.d(TAG, "onCreateOptionsMenu: ");
        changeMenu(navIndex);
        mViewPager.setCurrentItem(navIndex);
        return super.onCreateOptionsMenu(menu);
    }


    private void changeMenu(Menu menu, boolean menu1, boolean menu2, boolean menu3){
        Log.d(TAG, "changeMenu: ");
        capture= mMenu.findItem(R.id.camera);
        capture.setVisible(menu1);
        clock= mMenu.findItem(R.id.clock);
        clock.setVisible(menu2);
        MenuItem item3= mMenu.findItem(R.id.file);
        item3.setVisible(menu3);
        initMenuEvent();
    }

    private void initMenuEvent() {
        capture.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.camera){
                    if(fragmentState!=null){
                        fragmentState.createTab();
                    }
                }
                return false;
            }
        });
        clock.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.clock){
                    Toast.makeText(BotNavActivity.this, "clock", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void changeMenu(int nav){

        if(nav==NAV0){
            changeMenu(mMenu,true,false,false);
        }
        if(nav==NAV1){
            changeMenu(mMenu,false,true,false);
        }
        if(nav==NAV2){
            changeMenu(mMenu,false,false,true);
        }
        if(nav==NAV3){
            changeMenu(mMenu,false,false,false);
        }
        Log.d(TAG, "decideChangeMenu: ");
    }
    private void onResponseSelected(int position) {
        Log.d(TAG, "onResponseSelected: ");
        switch (position) {
            case 0:
                actionBar.setTitle("近态");
                    mBottomNavigationView.setSelectedItemId(R.id.menu_state);
                changeMenu(mMenu,true,false,false);
                break;
            case 1:
                actionBar.setTitle("定时");
                    mBottomNavigationView.setSelectedItemId(R.id.menu_time);
                changeMenu(mMenu,false,true,false);
                break;
            case 2:
                actionBar.setTitle("体检报告录入");
                     mBottomNavigationView.setSelectedItemId(R.id.menu_exam);
                changeMenu(mMenu,false,false,true);
                break;
            case 3:
                actionBar.setTitle("个人");
                    mBottomNavigationView.setSelectedItemId(R.id.menu_people);
                changeMenu(mMenu,false,false,false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume: ");
    }


    private void initData() {
        mFragmentList = new ArrayList<>();
        fragmentState = StateTabFragment.newInstance(this,"近态", "");
        VPFragment fragmentTime = VPFragment.newInstance("定时", "");
        VPFragment fragmentExam = VPFragment.newInstance("体检", "");
        VPFragment fragmentPeople = UserProfileFragment.newInstance("我的", "");
        mFragmentList.add(fragmentState);
        mFragmentList.add(fragmentTime);
        mFragmentList.add(fragmentExam);
        mFragmentList.add(fragmentPeople);
        actionBar=getSupportActionBar();
    }

    private void initViewPager() {
        List<Fragment> list = new ArrayList<>();
        alarmMePagerFragment = new AlarmMePagerFragment();
        list.add(alarmMePagerFragment);
        viewPager = findViewById(R.id.MainPager);
        MainActivity2.myViewpagerFragment fragment = new MainActivity2.myViewpagerFragment(getSupportFragmentManager(), 0, list);
        viewPager.setAdapter(fragment);
    }


}