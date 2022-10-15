package com.sanghm2.xinhxinhchat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.BroadcastAdapter;
import com.sanghm2.xinhxinhchat.adapter.ResultTextAdapter;
import com.sanghm2.xinhxinhchat.adapter.SearchShopAdapter;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.model.ResultTextModel;
import com.sanghm2.xinhxinhchat.model.SearchShopModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogSearch extends Dialog {
    private final Context context;

    private TextView tvBackButton;
    private EditText etSearch;
    private TextView tvSearchShop;

    private List<ResultTextModel> resultTextModelList;
    private RecyclerView resultTextModelRecyclerView;
    private ResultTextAdapter resultTextAdapter;

    private int loadCountOfSearchShop;
    private List<SearchShopModel> searchShopModelList;
    private RecyclerView searchShopModelRecyclerView;
    private SearchShopAdapter searchShopAdapter;

    private List<BroadcastModel> searchBroadcastModelList;
    private RecyclerView rvSearchBroadcast;
    private BroadcastAdapter broadcastAdapter;

    public DialogSearch(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        this.context = context;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search);

        onInitView();
        onSetVariables();
        onLoadData();
    }

    private void onInitView() {
        tvBackButton = findViewById(R.id.tvBackButton);
        etSearch = findViewById(R.id.etSearch);
        tvSearchShop = findViewById(R.id.tvSearchShop);
        resultTextModelRecyclerView = findViewById(R.id.rvResultText);
        searchShopModelRecyclerView = findViewById(R.id.rvSearchShop);
        rvSearchBroadcast = findViewById(R.id.rvSearchBroadcast);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility", "NewApi"})
    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Drawable icSearch = getContext().getResources().getDrawable(R.drawable.ic_baseline_search_24);
        Drawable icClear = getContext().getResources().getDrawable(R.drawable.ic_baseline_clear_24);
        etSearch.setCompoundDrawablesWithIntrinsicBounds(icSearch, null, null, null);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (etSearch.getText().toString().length() >= 3 ) {
                    onLoadData();
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(icSearch, null, icClear, null);
                    tvSearchShop.setVisibility(View.VISIBLE);
                    resultTextModelRecyclerView.setVisibility(View.VISIBLE);
                    tvSearchShop.setText(String.format("  Tìm shop \"%s\"", etSearch.getText().toString()));
                    etSearch.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            final int DRAWABLE_LEFT = 0;
                            final int DRAWABLE_TOP = 1;
                            final int DRAWABLE_RIGHT = 2;
                            final int DRAWABLE_BOTTOM = 3;

                            if(event.getAction() == MotionEvent.ACTION_UP) {
                                if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                    etSearch.setText("");
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                } else {
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(icSearch, null, null, null);
                    tvSearchShop.setVisibility(View.GONE);
                    resultTextModelRecyclerView.setVisibility(View.GONE);
                    etSearch.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    searchShopModelRecyclerView.setVisibility(View.GONE);
                }
            }
        });
        tvSearchShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextModelRecyclerView.setVisibility(View.GONE);
                tvSearchShop.setVisibility(View.GONE);
                rvSearchBroadcast.setVisibility(View.GONE);
                searchShopModelRecyclerView.setVisibility(View.VISIBLE);

                RetrofitConfigs.getClient().create(RetrofitAPI.class).getSearchUser(1, 20, etSearch.getText().toString()).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            searchShopModelList = SearchShopModel.FromJson(response.body().toString());
                            searchShopAdapter.updateAdapter(searchShopModelList);
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
        tvSearchShop.setVisibility(View.GONE);
        resultTextModelRecyclerView.setVisibility(View.GONE);
        searchShopModelRecyclerView.setVisibility(View.GONE);
        rvSearchBroadcast.setVisibility(View.GONE);

        resultTextModelList = new ArrayList<ResultTextModel>();
        RecyclerView.LayoutManager layoutManagerOfResultText = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        resultTextAdapter = new ResultTextAdapter(resultTextModelList, context);
        resultTextAdapter.setClickInterface(new ResultTextAdapter.ClickInterface() {
            @Override
            public void onSelected(ResultTextModel resultTextModel) {
                etSearch.setText(resultTextModel.getText());
                tvSearchShop.setVisibility(View.GONE);
                resultTextModelRecyclerView.setVisibility(View.GONE);
                searchShopModelRecyclerView.setVisibility(View.GONE);
                rvSearchBroadcast.setVisibility(View.VISIBLE);
                RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcastByTitle(XinhXinhPref.getStringPreference(context, Constants.TOKEN), 1, 20, etSearch.getText().toString()).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            searchBroadcastModelList = BroadcastModel.FromJson2(response.body().toString());
                            broadcastAdapter.updateAdapter(searchBroadcastModelList);
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
        resultTextModelRecyclerView.setAdapter(resultTextAdapter);
        resultTextModelRecyclerView.setLayoutManager(layoutManagerOfResultText);

        loadCountOfSearchShop = 2;
        searchShopModelList = new ArrayList<SearchShopModel>();
        RecyclerView.LayoutManager layoutManagerOfSearchShop = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        searchShopAdapter = new SearchShopAdapter(searchShopModelList, context);
        searchShopModelRecyclerView.setAdapter(searchShopAdapter);
        searchShopModelRecyclerView.setLayoutManager(layoutManagerOfSearchShop);
        searchShopModelRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!view.canScrollVertically(1)) {
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).getSearchUser(loadCountOfSearchShop, 10, etSearch.getText().toString()).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                List<SearchShopModel> searchShopModelListAPI = SearchShopModel.FromJson(response.body().toString());
                                searchShopModelList.addAll(searchShopModelListAPI);
                                searchShopAdapter.updateAdapter(searchShopModelList);
                                loadCountOfSearchShop++;
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

        searchBroadcastModelList = new ArrayList<BroadcastModel>();
        RecyclerView.LayoutManager layoutManagerOfBroadcast = new GridLayoutManager(context, 2);
        broadcastAdapter = new BroadcastAdapter(searchBroadcastModelList, context);
        rvSearchBroadcast.setAdapter(broadcastAdapter);
        rvSearchBroadcast.setLayoutManager(layoutManagerOfBroadcast);
    }

    private void onLoadData() {
        resultTextModelList.clear();
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcastByTitle(XinhXinhPref.getStringPreference(context, Constants.TOKEN), 1, 20, etSearch.getText().toString()).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<BroadcastModel> broadcastModelListAPI = BroadcastModel.FromJson2(response.body().toString());
                    for (int i = 0; i < broadcastModelListAPI.size(); i++) {
                        String shopTitle = broadcastModelListAPI.get(i).getTitle();
                        ResultTextModel resultTextModel = new ResultTextModel(shopTitle);
                        resultTextModelList.add(resultTextModel);
                    }
                    resultTextAdapter.updateAdapter(resultTextModelList);
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
