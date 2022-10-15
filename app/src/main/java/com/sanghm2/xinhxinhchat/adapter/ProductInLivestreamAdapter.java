package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.ProductInLivestreamModel;
import com.sanghm2.xinhxinhchat.utils.FormatData;

import java.util.List;

public class ProductInLivestreamAdapter extends RecyclerView.Adapter<ProductInLivestreamAdapter.ProductInLivestreamViewHolder> {

    private List<ProductInLivestreamModel> mProductInLivestreamModelList;
    private final Context mContext;

    public ProductInLivestreamAdapter(List<ProductInLivestreamModel> mProductInLivestreamModelList, Context mContext) {
        this.mProductInLivestreamModelList = mProductInLivestreamModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProductInLivestreamAdapter.ProductInLivestreamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductInLivestreamViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_in_livestream, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductInLivestreamAdapter.ProductInLivestreamViewHolder holder, int position) {
        ProductInLivestreamModel productInLivestreamModel = mProductInLivestreamModelList.get(position);

        Glide.with(mContext).load(productInLivestreamModel.getProductInfo().getImageUrls().get(0)).into(holder.ivProductImage);
        holder.tvProductName.setText(productInLivestreamModel.getProductInfo().getName());
        holder.tvProductSku.setText(productInLivestreamModel.getProductInfo().getSku());
        holder.tvProductOldPrice.setText(FormatData.formatCurrency(productInLivestreamModel.getProductInfo().getSellPrice()));
        holder.tvProductSellPrice.setText(FormatData.formatCurrency(productInLivestreamModel.getSellPrice()));
        holder.tvProductOldPrice.setPaintFlags(holder.tvProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (productInLivestreamModel.getProductInfo().getSellPrice() == productInLivestreamModel.getSellPrice()) {
            holder.tvProductOldPrice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mProductInLivestreamModelList.size();
    }

    static class ProductInLivestreamViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivProductImage;
        private final TextView tvProductName;
        private final TextView tvProductSku;
        private final TextView tvProductOldPrice;
        private final TextView tvProductSellPrice;

        public ProductInLivestreamViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductSku = itemView.findViewById(R.id.tvProductSku);
            tvProductOldPrice = itemView.findViewById(R.id.tvProductOldPrice);
            tvProductSellPrice = itemView.findViewById(R.id.tvProductSellPrice);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<ProductInLivestreamModel> list) {
        this.mProductInLivestreamModelList = list;
        notifyDataSetChanged();
    }
}
