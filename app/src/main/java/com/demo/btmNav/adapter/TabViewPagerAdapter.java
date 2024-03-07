package com.demo.btmNav.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class TabViewPagerAdapter extends PagerAdapter {

    private List<ImageView> mImageViewList;
    private List<String> titleList;

    public TabViewPagerAdapter(@NonNull FragmentManager fm,List<ImageView> imageViewList,List<String> mTitleList) {
        mImageViewList = imageViewList;
        titleList=mTitleList;

    }

    @Override
    public int getCount() {
        return mImageViewList == null ? 0 : mImageViewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = mImageViewList.get(position);
        container.addView(imageView);
        return mImageViewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList==null?"":titleList.get(position);
    }
}
