package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.ProductModel;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductModel> mProductModelList;
    private final Context mContext;
    public ClickInterface clickInterface;

    public ProductAdapter(List<ProductModel> mProductModelList, Context mContext) {
        this.mProductModelList = mProductModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        ProductModel productModel = mProductModelList.get(position);

        Glide.with(mContext).load(productModel.getImageUrls().get(0)).into(holder.ivProductImage);
        holder.tvProductName.setText(productModel.getName());
        holder.tvProductSku.setText(productModel.getSku());
        holder.tvProductStatus.setText(productModel.getStatus());
        if (Objects.equals(productModel.getStatus(), "selling")) {
            holder.tvProductStatus.setText("● Đang bán");
            holder.tvProductStatus.setTextColor(Color.parseColor("#00FF88"));
        } else {
            holder.tvProductStatus.setText("● Ngưng bán");
            holder.tvProductStatus.setTextColor(Color.parseColor("#FF4747"));
        }
        DecimalFormat currencyFormat = new DecimalFormat("###,###,###");
        holder.tvProductSellPrice.setText(currencyFormat.format(productModel.getSellPrice()) + "đ");

        holder.llItemProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterface.onSelected(productModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProductModelList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout llItemProduct;
        private final ImageView ivProductImage;
        private final TextView tvProductName;
        private final TextView tvProductSku;
        private final TextView tvProductStatus;
        private final TextView tvProductSellPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            llItemProduct = itemView.findViewById(R.id.llItemProduct);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductSku = itemView.findViewById(R.id.tvProductSku);
            tvProductStatus = itemView.findViewById(R.id.tvProductStatus);
            tvProductSellPrice = itemView.findViewById(R.id.tvProductSellPrice);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<ProductModel> list) {
        this.mProductModelList = list;
        notifyDataSetChanged();
    }

    public void setClickInterface(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public interface ClickInterface {
        void onSelected(ProductModel productModel);
    }
}
