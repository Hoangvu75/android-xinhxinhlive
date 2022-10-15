package com.sanghm2.xinhxinhchat.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.SlidePhotoAdapter;
import com.sanghm2.xinhxinhchat.model.SlidePhoto;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class ViewFragment extends Fragment {
    private Context mContext;

    ViewPager viewPager;
    CircleIndicator circleIndicator;
    List<SlidePhoto> slidePhotoList;
    SlidePhotoAdapter slidePhotoAdapter;
    Timer timer;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        mContext = getActivity();

        onInitView(view);
        onSetVariables();

        return view;
    }

    private void onInitView(View view) {
        viewPager = view.findViewById(R.id.vp_photo);
        circleIndicator = view.findViewById(R.id.circle);
    }

    private void onSetVariables() {
        slidePhotoList = getListSlidePhoto();
        setDataSlidePhotoAdapter();
    }

    private List<SlidePhoto> getListSlidePhoto() {
        List<SlidePhoto> list = new ArrayList<>();
        list.add(new SlidePhoto(R.drawable.ic_banner_auto_view_1_2x));
        list.add(new SlidePhoto(R.drawable.ic_banner_auto_view_2_2x));
        list.add(new SlidePhoto(R.drawable.ic_banner_auto_view_3_2x));
        list.add(new SlidePhoto(R.drawable.ic_banner_auto_view_4_2x));
        list.add(new SlidePhoto(R.drawable.ic_banner_auto_view_5_2x));
        return list;
    }
    private void setDataSlidePhotoAdapter() {
        slidePhotoAdapter = new SlidePhotoAdapter(slidePhotoList,this);
        viewPager.setAdapter(slidePhotoAdapter);
        circleIndicator.setViewPager(viewPager);
        slidePhotoAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        autoTransaction();
    }
    private void autoTransaction() {
        if(slidePhotoList == null || slidePhotoList.isEmpty()||viewPager == null){
            return;
        }
        if(timer == null){
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        int totalItem = slidePhotoList.size() - 1;
                        if(currentItem < totalItem){
                            currentItem++;
                            viewPager.setCurrentItem(currentItem);
                        }else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        },500,3000);
    }
}
