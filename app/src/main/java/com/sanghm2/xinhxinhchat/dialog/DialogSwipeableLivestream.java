package com.sanghm2.xinhxinhchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.VideoAdapter;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogSwipeableLivestream extends Dialog {
    private final Context context;

    private final List<BroadcastModel> broadcastModelList;
    private final int initialPosition;
    private final String type;
    private VideoAdapter videoAdapter;
    public ViewPager2 viewPager2;

    public DialogSwipeableLivestream(@NonNull Context context,
                                     List<BroadcastModel> broadcastModelList,
                                     int initialPosition,
                                     String type) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        this.context = context;
        this.broadcastModelList = broadcastModelList;
        this.initialPosition = initialPosition;
        this.type = type;
        setContentView(R.layout.dialog_swipeable_livestream);
        getWindow().getAttributes().windowAnimations = R.style.DialogSwipeAnimationLR;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onLoadData();
        onInitView();
        onSetVariables();
    }

    private void onInitView() {
        List<BroadcastModel> pseudoBroadcastModelList = new ArrayList<BroadcastModel>();
        for (int i = 0; i <= initialPosition; i++) {
            pseudoBroadcastModelList.add(broadcastModelList.get(i));
        }
//        videoAdapter = new VideoAdapter(pseudoBroadcastModelList, context);
        viewPager2 = findViewById(R.id.viewPagerVideos);
    }

    private void onSetVariables() {
        viewPager2.setAdapter(videoAdapter);
        if (Objects.equals(type, "trending")) {
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (videoAdapter.getItemCount() < broadcastModelList.size()) {
                                videoAdapter.updateAdapter(broadcastModelList);
                            }
                            handler.removeCallbacksAndMessages(null);
                            viewPager2.setVisibility(View.VISIBLE);
                        }
                    }, 400);
                }
            });
        }
        if (Objects.equals(type, "newest") || Objects.equals(type, "top_views")) {
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    Log.d("Hoang", "position: " + position + " page: " + (position/10 + 1));
                    if (position == videoAdapter.getItemCount() - 1) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RetrofitConfigs.getClient().create(RetrofitAPI.class)
                                        .getBroadcast(
                                                XinhXinhPref.getStringPreference(context, "TOKEN"),
                                                10, (position/10 + 2), 10, type)
                                        .enqueue(new Callback<JsonElement>() {
                                            @Override
                                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                                if (response.isSuccessful()) {
                                                    assert response.body() != null;
                                                    List<BroadcastModel> broadcastModelListAPI = BroadcastModel.FromJson(response.body().toString());
                                                    List<BroadcastModel> updateBroadcastModelList = new ArrayList<BroadcastModel>();
                                                    updateBroadcastModelList.addAll(broadcastModelList);
                                                    updateBroadcastModelList.addAll(broadcastModelListAPI);
                                                    videoAdapter.updateAdapter(updateBroadcastModelList);

                                                    viewPager2.setVisibility(View.VISIBLE);
                                                } else {
                                                    ErrorBody.Error401(context, response);
                                                    String error = ErrorBody.getErrorMessage(response);
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                            }
                                        });
                                handler.removeCallbacksAndMessages(null);
                            }
                        }, 400);
                    }
                }
            });
        }
    }

    private void onLoadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager2.setCurrentItem(initialPosition, true);
            }
        }, 100);
    }
}
