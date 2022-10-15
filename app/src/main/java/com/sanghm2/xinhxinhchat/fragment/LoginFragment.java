package com.sanghm2.xinhxinhchat.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.dialog.DialogBlockUser;
import com.sanghm2.xinhxinhchat.dialog.DialogForgotPassword;
import com.sanghm2.xinhxinhchat.dialog.DialogProductManagement;
import com.sanghm2.xinhxinhchat.model.LoginDataModel;
import com.sanghm2.xinhxinhchat.model.ProfileModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private Context mContext;

    private LinearLayout llLogin1;
    private TextView tvMoveToLogin;

    private ConstraintLayout csLogin2;
    private TextView tvBackButton;
    private EditText etLoginPhoneNumber;
    private EditText etLoginPassword;
    private TextView tvForgotPassword;
    private CardView cvLoginButton;

    private CircleImageView civAvatar;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvLivestreamCount;
    private TextView tvFollowersCount;
    private TextView tvLikeCount;
    private ConstraintLayout cslProductManagement;
    private ConstraintLayout cslBlockUser;
    private ConstraintLayout cslLogoutButton;

    public DialogProductManagement dialogProductManagement;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mContext = getActivity();
        onInitView(view);
        onSetVariables();
        onLoadData();
        return view;
    }

    private void onInitView(View view) {
        llLogin1 = view.findViewById(R.id.llLogin1);
        tvMoveToLogin = view.findViewById(R.id.tvMoveToLogin);

        csLogin2 = view.findViewById(R.id.csLogin2);
        tvBackButton = view.findViewById(R.id.tvBackButton);
        etLoginPhoneNumber = view.findViewById(R.id.etLoginPhoneNumber);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        cvLoginButton = view.findViewById(R.id.cvLoginButton);

        civAvatar = view.findViewById(R.id.civAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvLivestreamCount = view.findViewById(R.id.tvLivestreamCount);
        tvFollowersCount = view.findViewById(R.id.tvFollowersCount);
        tvLikeCount = view.findViewById(R.id.tvLikeCount);
        cslProductManagement = view.findViewById(R.id.cslProductManagement);
        cslBlockUser = view.findViewById(R.id.cslBlockUser);
        cslLogoutButton = view.findViewById(R.id.cslLogoutButton);
    }

    private void onSetVariables() {
        llLogin1.setVisibility(View.VISIBLE);
        tvMoveToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLogin1.setVisibility(View.GONE);
                csLogin2.setVisibility(View.VISIBLE);
            }
        });

        csLogin2.setVisibility(View.VISIBLE);
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llLogin1.setVisibility(View.VISIBLE);
                csLogin2.setVisibility(View.GONE);
            }
        });
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForgotPassword dialogForgotPassword = new DialogForgotPassword(mContext);
                dialogForgotPassword.show();
            }
        });
        cvLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etLoginPhoneNumber.getText().toString().equals("") && !etLoginPassword.getText().toString().equals("")) {
                    HashMap<String, String> loginBody = new HashMap<>();
                    loginBody.put("phone", etLoginPhoneNumber.getText().toString());
                    loginBody.put("password", etLoginPassword.getText().toString());
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).LoginAccount(loginBody).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                LoginDataModel loginData = LoginDataModel.FromJson(response.body().toString());
                                llLogin1.setVisibility(View.GONE);
                                csLogin2.setVisibility(View.GONE);
                                XinhXinhPref.setStringPreference(mContext, Constants.TOKEN, "Bearer " + loginData.getAccessToken());
                                RetrofitConfigs.getClient().create(RetrofitAPI.class).getProfile(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            ProfileModel profileModel = ProfileModel.FromJson(response.body().toString());
                                            tvLivestreamCount.setText("  " + profileModel.getCampaign().getTotal());
                                            tvFollowersCount.setText("  " + profileModel.getFollowerTotal());
                                            tvLikeCount.setText(" " + profileModel.getLikeTotal());
                                            Glide.with(mContext).load(profileModel.getUserInfo().getAvatar()).into(civAvatar);
                                            tvName.setText(profileModel.getUserInfo().getShopName());
                                            tvPhone.setText(profileModel.getUserInfo().getPhone());
                                        } else {
                                            ErrorBody.Error401(mContext, response);
                                            String error = ErrorBody.getErrorMessage(response);

                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonElement> call, Throwable t) {

                                    }
                                });
                            } else {
                                ErrorBody.Error401(mContext, response);
                                String error = ErrorBody.getErrorMessage(response);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
                } else {
                    Toast.makeText(mContext, "Vui lòng nhập thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cslProductManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogProductManagement = new DialogProductManagement(mContext);
                dialogProductManagement.show();
            }
        });
        cslBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBlockUser dialogBlockUser = new DialogBlockUser(mContext);
                dialogBlockUser.show();
            }
        });
        cslLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llLogin1.setVisibility(View.VISIBLE);
                csLogin2.setVisibility(View.VISIBLE);
                XinhXinhPref.setStringPreference(mContext, Constants.TOKEN, "");
            }
        });
    }

    private void onLoadData() {
        if (XinhXinhPref.getStringPreference(mContext, Constants.TOKEN) != null && !Objects.equals(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN), "")) {
            llLogin1.setVisibility(View.GONE);
            csLogin2.setVisibility(View.GONE);
            RetrofitConfigs.getClient().create(RetrofitAPI.class).getProfile(XinhXinhPref.getStringPreference(mContext, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        ProfileModel profileModel = ProfileModel.FromJson(response.body().toString());
                        tvLivestreamCount.setText("  " + profileModel.getCampaign().getTotal());
                        tvFollowersCount.setText("  " + profileModel.getFollowerTotal());
                        tvLikeCount.setText(" " + profileModel.getLikeTotal());
                        Glide.with(mContext).load(profileModel.getUserInfo().getAvatar()).into(civAvatar);
                        tvName.setText(profileModel.getUserInfo().getShopName());
                        tvPhone.setText(profileModel.getUserInfo().getPhone());
                    } else {
                        ErrorBody.Error401(mContext, response);
                        String error = ErrorBody.getErrorMessage(response);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, Throwable t) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onLoadData();
    }
}
