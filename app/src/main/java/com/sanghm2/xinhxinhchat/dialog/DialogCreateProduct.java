package com.sanghm2.xinhxinhchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.CategoryModel;
import com.sanghm2.xinhxinhchat.model.PostProductModel;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogCreateProduct extends Dialog {

    private final Context context;
    private List<CategoryModel> categoryModelList;

    private TextView tvBackButton;

    private EditText etSetProductName;
    private EditText etSetProductDescription;
    private TextView tvChooseCategory;
    private EditText etSetProductSellPrice;
    private TextView tvChooseUnit;
    private TextView tvSetStatus;
    private EditText etSetProductSku;
    private SwitchCompat scSetProductPreOrderState;

    private String productName = null;
    private String productDescription = null;
    private String productCategoryID = null;
    private Integer productSellPrice = 0;
    private String productUnitID = null;
    private String productStatus = null;
    private String productSku = null;
    private Boolean productOrderState = false;

    private CardView cvPostProductButton;

    public ClickInterface clickInterfaceForCreateProduct;

    public DialogCreateProduct(@NonNull Context context, List<CategoryModel> categoryModelList) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        this.categoryModelList = categoryModelList;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create_product);

        onInitView();
        onSetVariables();
    }

    private void onInitView() {
        tvBackButton = findViewById(R.id.tvBackButton);

        etSetProductName = findViewById(R.id.etSetProductName);
        etSetProductDescription = findViewById(R.id.etSetProductDescription);
        tvChooseCategory = findViewById(R.id.tvChooseCategory);
        etSetProductSellPrice = findViewById(R.id.etSetProductSellPrice);
        tvChooseUnit = findViewById(R.id.tvChooseUnit);
        tvSetStatus = findViewById(R.id.tvSetStatus);
        etSetProductSku = findViewById(R.id.etSetProductSku);
        scSetProductPreOrderState = findViewById(R.id.scSetProductPreOrderState);

        cvPostProductButton = findViewById(R.id.cvPostProductButton);
    }

    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogProductManagement dialogProductManagement = new DialogProductManagement(context);
                dialogProductManagement.show();
                dismiss();
            }
        });

        tvChooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseCategoryDialog chooseCategoryDialog = new ChooseCategoryDialog(context);
                chooseCategoryDialog.setContentView(R.layout.bottom_sheet_choose_category);
                chooseCategoryDialog.show();
            }
        });
        tvChooseUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseUnitDialog chooseUnitDialog = new ChooseUnitDialog(context);
                chooseUnitDialog.setContentView(R.layout.bottom_sheet_choose_unit);
                chooseUnitDialog.show();
            }
        });
        tvSetStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvSetStatus.getText().toString().equals("Đang bán")) {
                    tvSetStatus.setText("Ngưng bán");
                } else {
                    tvSetStatus.setText("Đang bán");
                }
            }
        });
        scSetProductPreOrderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productOrderState = !productOrderState;
            }
        });

        cvPostProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etSetProductName.getText().toString().equals("") && !etSetProductSellPrice.getText().toString().equals(""))
                {
                    productName = etSetProductName.getText().toString().trim();
                    productDescription = etSetProductDescription.getText().toString().trim();
                    for (int i = 0; i < categoryModelList.size(); i++) {
                        if (Objects.equals(categoryModelList.get(i).getName(), tvChooseCategory.getText().toString())) {
                            productCategoryID = categoryModelList.get(i).get_id();
                        }
                    }
                    productSellPrice = Integer.parseInt(etSetProductSellPrice.getText().toString());
                    switch(tvChooseUnit.getText().toString()) {
                        case "Phần":
                            productUnitID = "61af81dc73b94400080fa64c";
                            break;
                        case "Đôi":
                            productUnitID = "606589c9a062bb0bdcbd20ee";
                            break;
                        case "Túi":
                            productUnitID = "606589c9a062bb0bdcbd20f0";
                            break;
                        case "Chiếc":
                            productUnitID = "606589c9a062bb0bdcbd20ed";
                            break;
                        case "Thùng":
                            productUnitID = "606589c9a062bb0bdcbd20ef";
                            break;
                        case "Cái":
                            productUnitID = "606589c9a062bb0bdcbd20ec";
                            break;
                    }
                    if (tvSetStatus.getText().toString().equals("Đang bán")) {
                        productStatus = "selling";
                    } else {
                        productStatus = "stop_selling";
                    }
                    if (!etSetProductSku.getText().toString().equals("")) {
                        productSku = etSetProductSku.getText().toString().trim().toUpperCase(Locale.ROOT);
                    } else {
                        productSku = null;
                    }

                    PostProductModel postProductModel = new PostProductModel(
                            productSellPrice,
                            productStatus,
                            productOrderState,
                            productName.trim(),
                            productDescription.trim(),
                            productSku,
                            productUnitID,
                            productCategoryID
                    );

                    if (productSku != null && productSku.contains("PRO")) {
                        Toast.makeText(context, "Mã sản phẩm không được trùng mã hệ thống", Toast.LENGTH_SHORT).show();
                    } else {
                        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                                .PostProduct(XinhXinhPref.getStringPreference(context, "TOKEN"), postProductModel)
                                .enqueue(new Callback<JsonElement>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                        if (response.isSuccessful()) {
                                            DialogProductManagement dialogProductManagement = new DialogProductManagement(context);
                                            dialogProductManagement.show();
                                            dismiss();
                                            clickInterfaceForCreateProduct.onSelected();
                                        } else {
                                            if (response.code() == 422) {
                                                try {
                                                    JSONObject errors = null;
                                                    if (response.errorBody() != null) {
                                                        errors = new JSONObject(response.errorBody().string()).getJSONObject("errors");
                                                    }
                                                    String message = null;
                                                    if (errors != null) {
                                                        message = new JSONObject(errors.toString()).getString("sku");
                                                    }
                                                    Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();

                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    String message = null;
                                                    if (response.errorBody() != null) {
                                                        message = new JSONObject(response.errorBody().string()).getString("message");
                                                    }
                                                    Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                                                } catch (JSONException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                    }
                                });
                    }
                } else {
                    Toast.makeText(context, "Hãy điền vào thông tin cần thiết", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class ChooseCategoryDialog extends BottomSheetDialog {
        private final Context context;

        private RadioButton rbBtn0, rbBtn1, rbBtn2, rbBtn3, rbBtn4, rbBtn5;
        private CardView cvFinishButton;

        public ChooseCategoryDialog(@NonNull Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            rbBtn0 = findViewById(R.id.rbBtn0);
            rbBtn1 = findViewById(R.id.rbBtn1);
            rbBtn2 = findViewById(R.id.rbBtn2);
            rbBtn3 = findViewById(R.id.rbBtn3);
            rbBtn4 = findViewById(R.id.rbBtn4);
            rbBtn5 = findViewById(R.id.rbBtn5);
            cvFinishButton = findViewById(R.id.cvFinishButton);
        }

        private void onSetVariables() {
            rbBtn0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseCategory.setText("Thời trang");
                }
            });
            rbBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseCategory.setText("Mỹ phẩm");
                }
            });
            rbBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseCategory.setText("Điện tử");
                }
            });
            rbBtn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseCategory.setText("Ẩm thực");
                }
            });
            rbBtn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseCategory.setText("Đồ gia dụng");
                }
            });
            rbBtn5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseCategory.setText("Khác");
                }
            });
            cvFinishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    class ChooseUnitDialog extends BottomSheetDialog {
        private final Context context;

        private RadioButton rbBtn0, rbBtn1, rbBtn2, rbBtn3, rbBtn4, rbBtn5;
        private CardView cvFinishButton;

        public ChooseUnitDialog(@NonNull Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            rbBtn0 = findViewById(R.id.rbBtn0);
            rbBtn1 = findViewById(R.id.rbBtn1);
            rbBtn2 = findViewById(R.id.rbBtn2);
            rbBtn3 = findViewById(R.id.rbBtn3);
            rbBtn4 = findViewById(R.id.rbBtn4);
            rbBtn5 = findViewById(R.id.rbBtn5);
            cvFinishButton = findViewById(R.id.cvFinishButton);
        }

        private void onSetVariables() {
            rbBtn0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseUnit.setText("Phần");
                }
            });
            rbBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseUnit.setText("Đôi");
                }
            });
            rbBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseUnit.setText("Túi");
                }
            });
            rbBtn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseUnit.setText("Chiếc");
                }
            });
            rbBtn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseUnit.setText("Thùng");
                }
            });
            rbBtn5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvFinishButton.setVisibility(View.VISIBLE);
                    tvChooseUnit.setText("Cái");
                }
            });
            cvFinishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    public void setClickInterfaceForCreateProduct(ClickInterface clickInterfaceForCreateProduct) {
        this.clickInterfaceForCreateProduct = clickInterfaceForCreateProduct;
    }

    public interface ClickInterface {
        void onSelected();
    }

    @Override
    public void onBackPressed() {
        DialogProductManagement dialogProductManagement = new DialogProductManagement(context);
        dialogProductManagement.show();
        dismiss();
    }
}