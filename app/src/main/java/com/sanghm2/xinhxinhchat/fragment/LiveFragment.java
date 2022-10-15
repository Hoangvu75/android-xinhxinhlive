package com.sanghm2.xinhxinhchat.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.LivestreamActivity;
import com.sanghm2.xinhxinhchat.MainActivity;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.BroadcastAdapter;
import com.sanghm2.xinhxinhchat.adapter.CategoryAdapter;
import com.sanghm2.xinhxinhchat.adapter.SlidePhotoAdapter;
import com.sanghm2.xinhxinhchat.dialog.DialogSearch;
import com.sanghm2.xinhxinhchat.dialog.DialogSwipeableLivestream;
import com.sanghm2.xinhxinhchat.model.BlockUserModel;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.model.CategoryModel;
import com.sanghm2.xinhxinhchat.model.SlidePhoto;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveFragment extends Fragment {
    private Context mContext;

    private SwipeRefreshLayout srlRefresh;

    private NestedScrollView nsvFragmentLive;
    private ConstraintLayout clOfSearchButton;

    static int loadCountOfNewest = 2;
    static int loadCountOfTopViews = 2;

    private List<CategoryModel> categoryModelList;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;

    private List<BroadcastModel> broadcastModelListTrending;
    private RecyclerView broadcastRecyclerViewTrending;
    private BroadcastAdapter broadcastAdapterTrending;

    private List<BroadcastModel> broadcastModelListNewest;
    private RecyclerView broadcastRecyclerViewNewest;
    private BroadcastAdapter broadcastAdapterNewest;

    private List<BroadcastModel> broadcastModelListTopViews;
    private RecyclerView broadcastRecyclerViewTopViews;
    private BroadcastAdapter broadcastAdapterTopViews;

    private DialogSwipeableLivestream dialogSwipeableLivestream;

    private ImageView ivSearchButton;

    private ViewPager viewPager ;
    private CircleIndicator circleIndicator ;
    private List<SlidePhoto> slidePhotoList  ;
    private SlidePhotoAdapter slidePhotoAdapter;
    private Timer timer ;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        mContext = getActivity();
        initView(view);
        onSetVariables();
        onLoadData();
        return view;
    }

    private void initView(View view) {
        srlRefresh = view.findViewById(R.id.srlRefresh);

        nsvFragmentLive = view.findViewById(R.id.nsvFragmentLive);
        clOfSearchButton = view.findViewById(R.id.clOfSearchButton);

        categoryRecyclerView = view.findViewById(R.id.rvCategoryItems);
        broadcastRecyclerViewTrending = view.findViewById(R.id.rvBroadcastItemsTrending);
        broadcastRecyclerViewNewest = view.findViewById(R.id.rvBroadcastItemsNewest);
        broadcastRecyclerViewTopViews = view.findViewById(R.id.rvBroadcastItemsTopViews);

        ivSearchButton = view.findViewById(R.id.ivSearchButton);
        viewPager = view.findViewById(R.id.vp_photo);
        circleIndicator = view.findViewById(R.id.circle);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onSetVariables() {
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadData();
                loadCountOfNewest = 2;
                loadCountOfTopViews = 2;
                srlRefresh.setRefreshing(false);
                broadcastRecyclerViewTrending.scrollToPosition(0);
                broadcastRecyclerViewNewest.scrollToPosition(0);
                broadcastRecyclerViewTopViews.scrollToPosition(0);
                Toast.makeText(mContext, "Page Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        nsvFragmentLive.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY >= 1000) {
                    clOfSearchButton.setBackgroundColor(Color.BLACK);
                } else {
                    clOfSearchButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                }
            }
        });

        categoryModelList = new ArrayList<CategoryModel>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        categoryAdapter = new CategoryAdapter(categoryModelList, mContext);
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(layoutManager);

        broadcastModelListTrending = new ArrayList<BroadcastModel>();
        RecyclerView.LayoutManager layoutManagerBroadcastAdapterTrending = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        broadcastAdapterTrending = new BroadcastAdapter(broadcastModelListTrending, mContext);
        broadcastRecyclerViewTrending.setAdapter(broadcastAdapterTrending);
        broadcastRecyclerViewTrending.setLayoutManager(layoutManagerBroadcastAdapterTrending);
        broadcastAdapterTrending.setClickInterShowLivestream(new BroadcastAdapter.ClickInterface() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSelected(BroadcastModel broadcastModel) {
                int initialPosition = broadcastModelListTrending.indexOf(broadcastModel);
//                dialogSwipeableLivestream = new DialogSwipeableLivestream(mContext, broadcastModelListTrending, initialPosition, "trending");
//                dialogSwipeableLivestream.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        dialogSwipeableLivestream.viewPager2.setAdapter(null);
//                        onResume();
//                    }
//                });
//                dialogSwipeableLivestream.show();

                Intent intent = new Intent(mContext, LivestreamActivity.class);
                intent.putExtra("broadcastModelList", (ArrayList<BroadcastModel>) broadcastModelListTrending);
                intent.putExtra("initialPosition", initialPosition);
                intent.putExtra("type", "trending");
                startActivity(intent);
            }
        });

        broadcastModelListNewest = new ArrayList<BroadcastModel>();
        RecyclerView.LayoutManager layoutManagerBroadcastAdapterNewest = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        broadcastAdapterNewest = new BroadcastAdapter(broadcastModelListNewest, mContext);
        broadcastRecyclerViewNewest.setAdapter(broadcastAdapterNewest);
        broadcastRecyclerViewNewest.setLayoutManager(layoutManagerBroadcastAdapterNewest);
        broadcastRecyclerViewNewest.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!view.canScrollHorizontally(1)) {
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcast(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN), 10, loadCountOfNewest, 10, "newest").enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                List<BroadcastModel> broadcastModelListAPI = BroadcastModel.FromJson(response.body().toString());
                                broadcastModelListNewest.addAll(broadcastModelListAPI);
                                broadcastAdapterNewest.updateAdapter(broadcastModelListNewest);
                                loadCountOfNewest++;
                                Log.d("DUNG", "onResponse: " + loadCountOfNewest);
                            } else {
                                ErrorBody.Error401(mContext, response);
                                String error = ErrorBody.getErrorMessage(response);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
                }
            }
        });
        broadcastAdapterNewest.setClickInterShowLivestream(new BroadcastAdapter.ClickInterface() {
            @Override
            public void onSelected(BroadcastModel broadcastModel) {
                int initialPosition = broadcastModelListNewest.indexOf(broadcastModel);
//                dialogSwipeableLivestream = new DialogSwipeableLivestream(mContext, broadcastModelListNewest, initialPosition, "newest");
//                dialogSwipeableLivestream.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        dialogSwipeableLivestream.viewPager2.setAdapter(null);
//                        onResume();
//                    }
//                });
//                dialogSwipeableLivestream.show();

                Intent intent = new Intent(mContext, LivestreamActivity.class);
                intent.putExtra("broadcastModelList", (ArrayList<BroadcastModel>) broadcastModelListNewest);
                intent.putExtra("initialPosition", initialPosition);
                intent.putExtra("type", "newest");
                startActivity(intent);
            }
        });

        broadcastModelListTopViews = new ArrayList<BroadcastModel>();
        RecyclerView.LayoutManager layoutManagerBroadcastAdapterTopViews = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        broadcastAdapterTopViews = new BroadcastAdapter(broadcastModelListTopViews, mContext);
        broadcastRecyclerViewTopViews.setAdapter(broadcastAdapterTopViews);
        broadcastRecyclerViewTopViews.setLayoutManager(layoutManagerBroadcastAdapterTopViews);
        broadcastRecyclerViewTopViews.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!view.canScrollHorizontally(1)) {
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcast(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN), 10, loadCountOfTopViews, 10, "top_views").enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                List<BroadcastModel> broadcastModelListAPI = BroadcastModel.FromJson(response.body().toString());
                                broadcastModelListTopViews.addAll(broadcastModelListAPI);
                                broadcastAdapterTopViews.updateAdapter(broadcastModelListTopViews);
                                loadCountOfTopViews++;
                            } else {
                                ErrorBody.Error401(mContext, response);
                                String error = ErrorBody.getErrorMessage(response);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
                }
            }
        });
        broadcastAdapterTopViews.setClickInterShowLivestream(new BroadcastAdapter.ClickInterface() {
            @Override
            public void onSelected(BroadcastModel broadcastModel) {
//                int initialPosition = broadcastModelListTopViews.indexOf(broadcastModel);
//                dialogSwipeableLivestream = new DialogSwipeableLivestream(mContext, broadcastModelListTopViews, initialPosition, "top_views");
//                dialogSwipeableLivestream.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        dialogSwipeableLivestream.viewPager2.setAdapter(null);
//                        onResume();
//                    }
//                });
//                dialogSwipeableLivestream.show();
            }
        });

        ivSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSearch dialogSearch = new DialogSearch(mContext);
                dialogSearch.show();
            }
        });

        slidePhotoList = getListSlidePhoto();
        setDataSlidePhotoAdapter();
    }

    private void onLoadData() {
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getCategory(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    categoryModelList = CategoryModel.FromJson(response.body().toString());
                    categoryAdapter.updateAdapter(categoryModelList);
                } else {
                    ErrorBody.Error401(mContext, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
            }
        });
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcast(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN), 10, 1, 10, "newest").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    broadcastModelListTrending = BroadcastModel.FromJson3(response.body().toString());
                    broadcastAdapterTrending.updateAdapter(broadcastModelListTrending);
                } else {
                    ErrorBody.Error401(mContext, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcast(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN), 10, 1, 10, "newest").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    broadcastModelListNewest = BroadcastModel.FromJson(response.body().toString());
                    broadcastAdapterNewest.updateAdapter(broadcastModelListNewest);
                } else {
                    ErrorBody.Error401(mContext, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcast(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN), 10, 1, 10, "top_views").enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    broadcastModelListTopViews = BroadcastModel.FromJson(response.body().toString());
                    broadcastAdapterTopViews.updateAdapter(broadcastModelListTopViews);
                } else {
                    ErrorBody.Error401(mContext, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
    }

    private List<SlidePhoto> getListSlidePhoto() {
        List<SlidePhoto> list = new ArrayList<>();
        list.add(new SlidePhoto(R.drawable.slide1));
        list.add(new SlidePhoto(R.drawable.slide2));
        list.add(new SlidePhoto(R.drawable.slide3));
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

    @Override
    public void onResume() {
        super.onResume();
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBlockUser(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<BlockUserModel> blockUserModelsList = BlockUserModel.FromJson(response.body().toString());

                        for (int i = 0; i < blockUserModelsList.size(); i++) {
                            for (int j = 0; j < broadcastModelListTrending.size(); j++) {
                                if (Objects.equals(broadcastModelListTrending.get(j).getShopID(), blockUserModelsList.get(i).getShopID())) {
                                    broadcastModelListTrending.remove(j);
                                    broadcastAdapterTrending.updateAdapter(broadcastModelListTrending);
                                }
                            }
                            for (int j = 0; j < broadcastModelListNewest.size(); j++) {
                                if (Objects.equals(broadcastModelListNewest.get(j).getShopID(), blockUserModelsList.get(i).getShopID())) {
                                    broadcastModelListNewest.remove(j);
                                    broadcastAdapterNewest.updateAdapter(broadcastModelListNewest);
                                }
                            }
                            for (int j = 0; j < broadcastModelListTopViews.size(); j++) {
                                if (Objects.equals(broadcastModelListTopViews.get(j).getShopID(), blockUserModelsList.get(i).getShopID())) {
                                    broadcastModelListTopViews.remove(j);
                                    broadcastAdapterTopViews.updateAdapter(broadcastModelListTopViews);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
    }
}


