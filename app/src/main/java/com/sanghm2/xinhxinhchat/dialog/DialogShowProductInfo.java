package com.sanghm2.xinhxinhchat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.ProductAdapter;
import com.sanghm2.xinhxinhchat.fragment.LoginFragment;
import com.sanghm2.xinhxinhchat.model.CategoryModel;
import com.sanghm2.xinhxinhchat.model.ProductModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogShowProductInfo extends Dialog {

    private final Context context;
    public ProductModel productModel;
    private List<CategoryModel> categoryModelList;

    private TextView tvBackButton;

    private ImageView ivProductImage;
    private TextView tvProductName;
    private TextView tvProductSellPrice;
    private TextView tvProductCategory;
    private TextView tvProductSku;
    private TextView tvProductStatus;
    private TextView tvProductPreOrderState;
    private TextView tvProductDescription;

    private CardView cvEditProductButton;
    private CardView cvDeleteProductButton;

    public ClickInterface clickInterfaceForDeleteProduct;
    ClickInterfaceTurnBack clickInterfaceForTurnBack;

    public DialogShowProductInfo(@NonNull Context context, ProductModel productModel) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.productModel = productModel;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_product_info);

        onInitView();
        onSetVariables();
    }

    private void onInitView() {
        categoryModelList = new ArrayList<CategoryModel> ();

        tvBackButton = findViewById(R.id.tvBackButton);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductSellPrice = findViewById(R.id.tvProductSellPrice);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        tvProductSku = findViewById(R.id.tvProductSku);
        tvProductStatus = findViewById(R.id.tvProductStatus);
        tvProductPreOrderState = findViewById(R.id.tvProductPreOrderState);
        tvProductDescription = findViewById(R.id.tvProductDescription);

        cvEditProductButton = findViewById(R.id.cvEditProductButton);
        cvDeleteProductButton = findViewById(R.id.cvDeleteProductButton);
    }

    @SuppressLint("SetTextI18n")
    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                clickInterfaceForTurnBack.onSelected();
            }
        });

        Glide.with(context).load(productModel.getImageUrls().get(0)).into(ivProductImage);
        tvProductName.setText(productModel.getName().trim());
        DecimalFormat currencyFormat = new DecimalFormat("###,###,###");
        tvProductSellPrice.setText(currencyFormat.format(productModel.getSellPrice()) + "đ");
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getCategory(XinhXinhPref.getStringPreference(context, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    categoryModelList = CategoryModel.FromJson(response.body().toString());
                    for (int i = 0; i < categoryModelList.size(); i++) {
                        if (Objects.equals(productModel.getCategoryID(), categoryModelList.get(i).get_id())) {
                            tvProductCategory.setText(categoryModelList.get(i).getName());
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
        tvProductSku.setText(productModel.getSku());
        tvProductStatus.setText(productModel.getStatus());
        if (Objects.equals(productModel.getStatus(), "selling")) {
            tvProductStatus.setText("● Đang bán");
            tvProductStatus.setTextColor(Color.parseColor("#00FF88"));
        } else {
            tvProductStatus.setText("● Ngưng bán");
            tvProductStatus.setTextColor(Color.parseColor("#FF4747"));
        }
        tvProductPreOrderState.setText(productModel.getOrder().toString());
        if (productModel.getOrder().toString().equals("true")) {
            tvProductPreOrderState.setText("Có");
        } else {
            tvProductPreOrderState.setText("Không");
        }
        tvProductDescription.setText(productModel.getDescription().trim());

        cvEditProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogEditProduct dialogEditProduct = new DialogEditProduct(context, productModel, categoryModelList);
                dialogEditProduct.setClickInterfaceForEditProduct(new DialogEditProduct.ClickInterface() {
                    @Override
                    public void onSelected() {
                        productModel = dialogEditProduct.newProductModel;
                        Glide.with(context).load(productModel.getImageUrls().get(0)).into(ivProductImage);
                        tvProductName.setText(productModel.getName().trim());
                        DecimalFormat currencyFormat = new DecimalFormat("###,###,###");
                        tvProductSellPrice.setText(currencyFormat.format(productModel.getSellPrice()) + "đ");
                        RetrofitConfigs.getClient().create(RetrofitAPI.class).getCategory(XinhXinhPref.getStringPreference(context, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    categoryModelList = CategoryModel.FromJson(response.body().toString());
                                    for (int i = 0; i < categoryModelList.size(); i++) {
                                        if (Objects.equals(productModel.getCategoryID(), categoryModelList.get(i).get_id())) {
                                            tvProductCategory.setText(categoryModelList.get(i).getName());
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
                        tvProductSku.setText(productModel.getSku());
                        tvProductStatus.setText(productModel.getStatus());
                        if (Objects.equals(productModel.getStatus(), "selling")) {
                            tvProductStatus.setText("● Đang bán");
                            tvProductStatus.setTextColor(Color.parseColor("#00FF88"));
                        } else {
                            tvProductStatus.setText("● Ngưng bán");
                            tvProductStatus.setTextColor(Color.parseColor("#FF4747"));
                        }
                        tvProductPreOrderState.setText(productModel.getOrder().toString());
                        if (productModel.getOrder().toString().equals("true")) {
                            tvProductPreOrderState.setText("Có");
                        } else {
                            tvProductPreOrderState.setText("Không");
                        }
                        tvProductDescription.setText(productModel.getDescription().trim());
                    }
                });
                dialogEditProduct.show();
            }
        });
        cvDeleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitConfigs.getClient().create(RetrofitAPI.class)
                        .DeleteProduct(XinhXinhPref.getStringPreference(context, "TOKEN"), productModel.getId())
                        .enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                dismiss();
                                clickInterfaceForDeleteProduct.onSelected();
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                            }
                        });
            }
        });
    }

    public void setClickInterfaceForDeleteProduct(ClickInterface clickInterfaceForDeleteProduct) {
        this.clickInterfaceForDeleteProduct = clickInterfaceForDeleteProduct;
    }

    public interface ClickInterface {
        void onSelected();
    }

    public void setClickInterfaceForTurnBack(ClickInterfaceTurnBack clickInterfaceForTurnBack) {
        this.clickInterfaceForTurnBack = clickInterfaceForTurnBack;
    }

    public interface ClickInterfaceTurnBack {
        void onSelected();
    }

    @Override
    public void onBackPressed() {
        dismiss();
        clickInterfaceForTurnBack.onSelected();
    }
}
