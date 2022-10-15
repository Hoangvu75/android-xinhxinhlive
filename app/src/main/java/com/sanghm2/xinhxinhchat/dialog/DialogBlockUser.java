package com.sanghm2.xinhxinhchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.BlockUserAdapter;
import com.sanghm2.xinhxinhchat.adapter.CategoryNameAdapter;
import com.sanghm2.xinhxinhchat.adapter.ProductAdapter;
import com.sanghm2.xinhxinhchat.model.BlockUserModel;
import com.sanghm2.xinhxinhchat.model.ProductModel;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogBlockUser extends Dialog {
    private final Context context;

    private TextView tvBackButton;

    private List<BlockUserModel> blockUserModelsList;
    private RecyclerView rvBlockUser;
    private BlockUserAdapter blockUserAdapter;
    private RecyclerView.LayoutManager blockUserLayout;

    public DialogBlockUser(@NonNull Context context) {
        super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);

        this.context = context;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_block_user);

        onInitView();
        onSetVariables();
        onLoadData();
    }

    private void onInitView() {
        tvBackButton = findViewById(R.id.tvBackButton);

        blockUserModelsList = new ArrayList<BlockUserModel>();
        rvBlockUser = findViewById(R.id.rvBlockUser);
        blockUserAdapter = new BlockUserAdapter(blockUserModelsList, context);
        blockUserLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
    }

    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        rvBlockUser.setAdapter(blockUserAdapter);
        rvBlockUser.setLayoutManager(blockUserLayout);
    }

    private void onLoadData() {
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBlockUser(XinhXinhPref.getStringPreference(context, "TOKEN")).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        blockUserModelsList = BlockUserModel.FromJson(response.body().toString());
                        blockUserAdapter.updateAdapter(blockUserModelsList);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
    }
}
