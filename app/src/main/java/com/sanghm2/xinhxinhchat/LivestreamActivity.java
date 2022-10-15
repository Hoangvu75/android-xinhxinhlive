package com.sanghm2.xinhxinhchat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.adapter.VideoAdapter;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LivestreamActivity extends AppCompatActivity {

    private List<BroadcastModel> broadcastModelList;
    private int initialPosition;
    private String type;

    private VideoAdapter videoAdapter;
    public ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestream);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.slide_lr_enter, R.anim.slide_lr_exit);

        setIntentData(getIntent());
        onInitView();
        onSetVariables();
        onLoadData();
    }

    private void setIntentData(Intent intent) {
        broadcastModelList = (ArrayList<BroadcastModel>) intent.getSerializableExtra("broadcastModelList");
        initialPosition = intent.getIntExtra("initialPosition", 0);
        type = intent.getStringExtra("type");
    }

    private void onInitView() {
        List<BroadcastModel> pseudoBroadcastModelList = new ArrayList<BroadcastModel>();
        for (int i = 0; i <= initialPosition; i++) {
            pseudoBroadcastModelList.add(broadcastModelList.get(i));
        }
        videoAdapter = new VideoAdapter(pseudoBroadcastModelList, LivestreamActivity.this, this);
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
                    if (position == videoAdapter.getItemCount() - 1 || (position%10 == 0 && position > 0)) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RetrofitConfigs.getClient().create(RetrofitAPI.class)
                                        .getBroadcast(
                                                XinhXinhPref.getStringPreference(LivestreamActivity.this, "TOKEN"),
                                                10, (position/10 + 2), 10, type)
                                        .enqueue(new Callback<JsonElement>() {
                                            @Override
                                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                                if (response.isSuccessful()) {
                                                    assert response.body() != null;
                                                    List<BroadcastModel> broadcastModelListAPI = BroadcastModel.FromJson(response.body().toString());
                                                    broadcastModelList.addAll(broadcastModelListAPI);
                                                    videoAdapter.updateAdapter(broadcastModelList);
                                                    Log.d("Hoang", "size: " + videoAdapter.getItemCount());
                                                    viewPager2.setVisibility(View.VISIBLE);
                                                } else {
                                                    ErrorBody.Error401(LivestreamActivity.this, response);
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
        videoAdapter.setClickInterMinimize(new VideoAdapter.ClickInterface() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSelected() {
                minimize();
            }
        });
    }

    private void onLoadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager2.setCurrentItem(initialPosition, true);
            }
        }, 100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_rl_enter, R.anim.slide_rl_exit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager2.setAdapter(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //called when user presses Home button, enter in PIP mode, requires Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            if (!isInPictureInPictureMode()){
                minimize();
            } else {

            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void minimize() {
        Rational aspectRation = new Rational(9, 16);
        PictureInPictureParams.Builder pictureInPictureParams = new PictureInPictureParams.Builder();
        pictureInPictureParams.setAspectRatio(aspectRation).build();
        enterPictureInPictureMode(pictureInPictureParams.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode){
            findViewById(R.id.llAllViewOfController).setVisibility(View.GONE);
        } else {
            findViewById(R.id.llAllViewOfController).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntentData(intent);
        onInitView();
        onSetVariables();
        onLoadData();
    }
}