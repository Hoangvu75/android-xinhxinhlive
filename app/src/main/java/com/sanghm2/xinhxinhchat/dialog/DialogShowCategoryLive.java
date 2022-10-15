package com.sanghm2.xinhxinhchat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.BroadcastAdapter;
import com.sanghm2.xinhxinhchat.adapter.CategoryAdapterInDialogShowCategoryLive;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.model.CategoryModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogShowCategoryLive extends Dialog {
    private TextView tvBackButton;

    private final Context context;
    private String categoryID;
    private static int loadCount;

    private List<CategoryModel> categoryModelList;
    private RecyclerView categoryRecyclerView;
    private RecyclerView.LayoutManager layoutManagerOfCategory;
    private CategoryAdapterInDialogShowCategoryLive categoryAdapter;

    private List<BroadcastModel> broadcastModelList;
    private RecyclerView broadcastRecyclerView;
    private RecyclerView.LayoutManager layoutManagerOfBroadcast;
    private BroadcastAdapter broadcastAdapter;

    public DialogShowCategoryLive(@NonNull Context context, String categoryID) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.categoryID = categoryID;

        getWindow().getAttributes().windowAnimations = R.style.DialogSwipeAnimationLR;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_category_live_layout);

        onInitView();
        onSetVariables();
        onLoadData();
    }

    private void onInitView() {
        tvBackButton = findViewById(R.id.tvBackButton);

        loadCount = 2;

        categoryRecyclerView = findViewById(R.id.rvCategoryItems);
        categoryModelList = new ArrayList<CategoryModel>();
        layoutManagerOfCategory = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        categoryAdapter = new CategoryAdapterInDialogShowCategoryLive(categoryModelList, context);

        broadcastModelList = new ArrayList<BroadcastModel>();
        broadcastRecyclerView = findViewById(R.id.rvBroadcastItemsSetByCategory);
        layoutManagerOfBroadcast = new GridLayoutManager(context, 2);
        broadcastAdapter = new BroadcastAdapter(broadcastModelList, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(layoutManagerOfCategory);
        categoryAdapter.setClickInterface(new CategoryAdapterInDialogShowCategoryLive.ClickInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSelected(CategoryModel categoryModel) {
                for (int i = 0; i < categoryModelList.size(); i++) {
                    categoryModelList.get(i).setChecked(false);
                }
                categoryModel.setChecked(true);
                categoryAdapter.notifyDataSetChanged();
                categoryID = categoryModel.get_id();
                RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcastByCategoryID(XinhXinhPref.getStringPreference(context, Constants.TOKEN), 1, 10, categoryID).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            broadcastModelList = BroadcastModel.FromJson2(response.body().toString());
                            broadcastAdapter.updateAdapter(broadcastModelList);
                        } else {
                            ErrorBody.Error401(context, response);
                            String error = ErrorBody.getErrorMessage(response);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    }
                });
            }
        });

        broadcastRecyclerView.setLayoutManager(layoutManagerOfBroadcast);
        broadcastRecyclerView.setAdapter(broadcastAdapter);
        broadcastRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!view.canScrollVertically(1)) {
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcastByCategoryID(XinhXinhPref.getStringPreference(context, Constants.TOKEN), loadCount, 10, categoryID).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                List<BroadcastModel> broadcastModelListAPI = BroadcastModel.FromJson2(response.body().toString());
                                broadcastModelList.addAll(broadcastModelListAPI);
                                broadcastAdapter.updateAdapter(broadcastModelList);
                                loadCount++;
                            } else {
                                ErrorBody.Error401(context, response);
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
        broadcastAdapter.setClickInterShowLivestream(new BroadcastAdapter.ClickInterface() {
            @Override
            public void onSelected(BroadcastModel broadcastModel) {
                int initialPosition = broadcastModelList.indexOf(broadcastModel);
                DialogSwipeableLivestream dialogSwipeableLivestream = new DialogSwipeableLivestream(context, broadcastModelList, initialPosition, "category");
                dialogSwipeableLivestream.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        dialogSwipeableLivestream.viewPager2.setAdapter(null);
                    }
                });
                dialogSwipeableLivestream.show();
            }
        });
    }

    private void onLoadData() {
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getCategory(XinhXinhPref.getStringPreference(context, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    categoryModelList = CategoryModel.FromJson(response.body().toString());
                    categoryAdapter.updateAdapter(categoryModelList);

                    for (int i = 0; i < categoryModelList.size(); i++) {
                        if (Objects.equals(categoryModelList.get(i).get_id(), categoryID)) {
                            categoryModelList.get(i).setChecked(true);
                        }
                    }
                } else {
                    ErrorBody.Error401(context, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
            }
        });
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcastByCategoryID(XinhXinhPref.getStringPreference(context, Constants.TOKEN), 1, 10, categoryID).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    broadcastModelList = BroadcastModel.FromJson2(response.body().toString());
                    broadcastAdapter.updateAdapter(broadcastModelList);
                } else {
                    ErrorBody.Error401(context, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
    }
}
