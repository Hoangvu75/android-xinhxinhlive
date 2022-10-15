package com.sanghm2.xinhxinhchat.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.adapter.CategoryNameAdapter;
import com.sanghm2.xinhxinhchat.adapter.ProductAdapter;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.model.CategoryModel;
import com.sanghm2.xinhxinhchat.model.ProductModel;
import com.sanghm2.xinhxinhchat.model.ProfileModel;
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

public class DialogProductManagement extends Dialog {

    private final Context context;

    private TextView tvBackButton;
    private CardView btnCreateProduct;

    private List<CategoryModel> categoryModelList;
    private RecyclerView rvCategoryName;
    private CategoryNameAdapter categoryNameAdapter;
    private RecyclerView.LayoutManager categoryNameLayout;

    private List<ProductModel> productModelList;
    private RecyclerView rvProduct;
    private ProductAdapter productAdapter;
    private RecyclerView.LayoutManager productLayout;
    private Integer loadCountOfProduct;

    public DialogProductManagement(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_product_management);

        onInitView();
        onSetVariables();
        onLoadData();
    }

    private void onInitView() {
        tvBackButton = findViewById(R.id.tvBackButton);
        btnCreateProduct = findViewById(R.id.btnCreateProduct);

        categoryModelList = new ArrayList<CategoryModel>();
        rvCategoryName = findViewById(R.id.rvCategoryName);
        categoryNameAdapter = new CategoryNameAdapter(categoryModelList, context);
        categoryNameLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        productModelList = new ArrayList<ProductModel>();
        rvProduct = findViewById(R.id.rvProduct);
        productAdapter = new ProductAdapter(productModelList, context);
        productLayout = new GridLayoutManager(context, 2);
        loadCountOfProduct = 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogCreateProduct dialogCreateProduct = new DialogCreateProduct(context, categoryModelList);
                dialogCreateProduct.setClickInterfaceForCreateProduct(new DialogCreateProduct.ClickInterface() {
                    @Override
                    public void onSelected() {
                        Toast.makeText(context, "Hoàn tất tạo sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogCreateProduct.show();
                dismiss();
            }
        });

        rvCategoryName.setAdapter(categoryNameAdapter);
        rvCategoryName.setLayoutManager(categoryNameLayout);
        categoryNameAdapter.setClickInterface(new CategoryNameAdapter.ClickInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSelected(CategoryModel categoryModel) {
                for (int i = 0; i < categoryModelList.size(); i++) {
                    categoryModelList.get(i).setChecked(false);
                }
                categoryModel.setChecked(true);
                categoryNameAdapter.notifyDataSetChanged();

                productModelList.clear();
                RetrofitConfigs.getClient().create(RetrofitAPI.class).getProductByCategory(XinhXinhPref.getStringPreference(context, "TOKEN"), 1, 10, categoryModel.get_id()).enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        assert response.body() != null;
                        productModelList = ProductModel.FromJson(response.body().toString());
                        productAdapter.updateAdapter(productModelList);
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    }
                });
                loadCountOfProduct = 2;
            }
        });

        rvProduct.setAdapter(productAdapter);
        rvProduct.setLayoutManager(productLayout);
        rvProduct.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (!view.canScrollVertically(1)) {
                    for (int j = 0; j < categoryModelList.size(); j++) {
                        if (categoryModelList.get(j).isChecked()) {
                            RetrofitConfigs.getClient().create(RetrofitAPI.class).getProductByCategory(XinhXinhPref.getStringPreference(context, "TOKEN"), loadCountOfProduct, 10, categoryModelList.get(j).get_id()).enqueue(new Callback<JsonElement>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                    assert response.body() != null;
                                    List<ProductModel> productModelResList = ProductModel.FromJson(response.body().toString());
                                    productModelList.addAll(productModelResList);
                                    productAdapter.updateAdapter(productModelList);
                                    loadCountOfProduct++;
                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                }
                            });
                        }
                    }
                }
            }
        });
        productAdapter.setClickInterface(new ProductAdapter.ClickInterface() {
            @Override
            public void onSelected(ProductModel selectedProductModel) {
                Toast.makeText(context, "Bạn đang xem sản phẩm " + selectedProductModel.getName(), Toast.LENGTH_SHORT).show();
                DialogShowProductInfo dialogShowProductInfo = new DialogShowProductInfo(context, selectedProductModel);
                dialogShowProductInfo.setClickInterfaceForDeleteProduct(new DialogShowProductInfo.ClickInterface() {
                    @Override
                    public void onSelected() {
                        productModelList.remove(selectedProductModel);
                        productAdapter.updateAdapter(productModelList);
                        Toast.makeText(context, "Đã xóa sản phẩm " + selectedProductModel.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialogShowProductInfo.setClickInterfaceForTurnBack(new DialogShowProductInfo.ClickInterfaceTurnBack() {
                    @Override
                    public void onSelected() {
                        selectedProductModel.setId(dialogShowProductInfo.productModel.getId());
                        selectedProductModel.setSellPrice(dialogShowProductInfo.productModel.getSellPrice());
                        selectedProductModel.setStatus(dialogShowProductInfo.productModel.getStatus());
                        selectedProductModel.setOrder(dialogShowProductInfo.productModel.getOrder());
                        selectedProductModel.setName(dialogShowProductInfo.productModel.getName());
                        selectedProductModel.setDescription(dialogShowProductInfo.productModel.getDescription());
                        selectedProductModel.setSku(dialogShowProductInfo.productModel.getSku());
                        selectedProductModel.setUnitID(dialogShowProductInfo.productModel.getUnitID());
                        selectedProductModel.setCategoryID(dialogShowProductInfo.productModel.getCategoryID());
                        selectedProductModel.setImageUrls(dialogShowProductInfo.productModel.getImageUrls());
                        productAdapter.updateAdapter(productModelList);
                    }
                });
                dialogShowProductInfo.show();
            }
        });
    }

    private void onLoadData() {
        RetrofitConfigs.getClient().create(RetrofitAPI.class).getCategory(XinhXinhPref.getStringPreference(context, Constants.TOKEN)).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<CategoryModel> categoryModelResList = CategoryModel.FromJson(response.body().toString());
                    categoryModelList.addAll(categoryModelResList);
                    CategoryModel categoryModel = new CategoryModel("", "Tất cả", "", true);
                    categoryModelList.add(0, categoryModel);
                    categoryNameAdapter.updateAdapter(categoryModelList);
                } else {
                    ErrorBody.Error401(context, response);
                    String error = ErrorBody.getErrorMessage(response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
            }
        });

        RetrofitConfigs.getClient().create(RetrofitAPI.class).getProductList(XinhXinhPref.getStringPreference(context, "TOKEN"), 1, 10).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                productModelList = ProductModel.FromJson(response.body().toString());
                productAdapter.updateAdapter(productModelList);
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });
    }
}