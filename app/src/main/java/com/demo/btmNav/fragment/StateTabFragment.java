package com.demo.btmNav.fragment;

import static android.app.Activity.RESULT_OK;
import static com.demo.utils.ImageUtil.getFileNameNoEx;
import static com.demo.utils.ImageUtil.getTimeStampName;
import static com.demo.utils.TakePhotoUtils.REQUEST_CODE_TAKE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.demo.oldcare.R;
import com.demo.btmNav.adapter.TabViewPagerAdapter;
import com.demo.utils.TakePhotoUtils;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StateTabFragment extends VPFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String statePhotoPath;

    private ViewPager   viewPager;
    private TabLayout  tabLayout;
    private List<ImageView> mImageViewList;
    private List<String> mTitleList;

    private Activity act;
    private TabViewPagerAdapter tabViewPagerAdapter;

    private Button btDelete;





    private String mParam1;
    private String mParam2;

    public StateTabFragment() {

    }


    // TODO: Rename and change types and number of parameters
    public static StateTabFragment newInstance(Activity Act, String param1, String param2) {
        StateTabFragment fragment = new StateTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_state_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager=view.findViewById(R.id.state_vp);
        tabLayout=view.findViewById(R.id.tab_layout);
        btDelete=view.findViewById(R.id.bt_remove);
        act=requireActivity();
        statePhotoPath = String.valueOf(act.getExternalFilesDir(""));

        setTab();
        initEvent();
    }
    private void setTab() {
        String TAG="asd";
        Log.d(TAG, "setTab: ");
        initData();
        tabViewPagerAdapter=new TabViewPagerAdapter(getChildFragmentManager(),mImageViewList,mTitleList);
        viewPager.setAdapter(tabViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void initData() {
        mImageViewList=new ArrayList<>();
        mTitleList=new ArrayList<>();
        initFile();
    }
    /*
  app文件下图片初始化
   */
    private void initFile(){
        File folder=new File(statePhotoPath);
        File[] files=folder.listFiles();
        List<File>imageFiles=new ArrayList<>();
        if(files!=null)
            for(File file :files){
                String fileName=file.getName();
                if(fileName.endsWith(".jpg")|| fileName.endsWith(".png")|| fileName.endsWith(".jpeg")){
                    imageFiles.add(file);
                }
            }
        for(File file :imageFiles){
            ImageView imageView=new ImageView(act);
            Uri iamgeUri= FileProvider.getUriForFile(act, "com.example.userprofile.fileProvider", file);
            imageView.setImageURI(iamgeUri);
            mImageViewList.add(imageView);
            String prefixName=getFileNameNoEx(file.getName());
            mTitleList.add(prefixName);
        }
    }

    public void createTab()  {
//        Toast.makeText(requireActivity(), "on crate tab" , Toast.LENGTH_SHORT).show();
        String fileName = getTimeStampName();
        File out = new File(statePhotoPath, fileName);
        if(out.exists()){
            boolean delete  = out.delete();
            if(delete){
                try {
                    out.createNewFile();
                }
                catch (Exception ignored){
                }
            }
        }
        TakePhotoUtils take=TakePhotoUtils.getInstance();
        take.dispatchTakePictureIntent(this,out);
    }

    void initEvent(){
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.bt_remove){
                    String title=null;
                    if(tabLayout!=null){
                        int selectedTabPosition = tabLayout.getSelectedTabPosition();
                        TabLayout.Tab selectedTab = tabLayout.getTabAt(selectedTabPosition);
                        if (selectedTab != null) {
                            title = Objects.requireNonNull(selectedTab.getText()).toString();
                        }
                    }
                    if(title!=null){
                        String fileName=title+".jpg";
                        File deleteFile = new File(statePhotoPath, fileName);
                        boolean delete=false;
                        if(deleteFile.exists()){
                            delete= deleteFile.delete();
                        }
                        if(delete){
                            setTab();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_TAKE){
            if(resultCode==RESULT_OK){
                setTab();
            }
        }
    }
}
