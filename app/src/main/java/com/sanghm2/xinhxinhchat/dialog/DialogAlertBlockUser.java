package com.sanghm2.xinhxinhchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogAlertBlockUser extends Dialog {
    private final Context context;

    private final BroadcastModel broadcastModel;

    private TextView tvDeclineBlockUser;
    private TextView tvAcceptBlockUser;

    private ClickInterface clickInterfaceBlockUser;

    public DialogAlertBlockUser(@NonNull Context context, BroadcastModel broadcastModel) {
        super(context);
        this.context = context;
        this.broadcastModel = broadcastModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onInitView();
        onSetVariables();
    }

    private void onInitView() {
        tvDeclineBlockUser = findViewById(R.id.tvDeclineBlockUser);
        tvAcceptBlockUser = findViewById(R.id.tvAcceptBlockUser);
    }

    private void onSetVariables() {
        tvDeclineBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvAcceptBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> postBlockUser = new HashMap<>();
                postBlockUser.put("shopID", broadcastModel.getShopID());
                RetrofitConfigs.getClient().create(RetrofitAPI.class).postBlockUser(XinhXinhPref.getStringPreference(context, "TOKEN"), postBlockUser).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            dismiss();
                            clickInterfaceBlockUser.onSelected();
                            Toast.makeText(context, "Hoàn tất chặn người dùng " + broadcastModel.getShopName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    }
                });
            }
        });
    }

    public void setClickInterfaceBlockUser(ClickInterface clickInterfaceBlockUser) {
        this.clickInterfaceBlockUser = clickInterfaceBlockUser;
    }

    public interface ClickInterface {
        void onSelected();
    }
}
