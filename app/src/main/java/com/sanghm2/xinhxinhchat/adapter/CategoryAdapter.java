package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.dialog.DialogShowCategoryLive;
import com.sanghm2.xinhxinhchat.model.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryModel> mCategoryModelList;
    private final Context mContext;

    public CategoryAdapter(List<CategoryModel> mCategoryModelList, Context mContext) {
        this.mCategoryModelList = mCategoryModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        CategoryModel categoryModel = mCategoryModelList.get(position);

        Glide.with(mContext).load(categoryModel.getImageUrl()).into(holder.ivCategoryImage);
        holder.tvCategoryName.setText(categoryModel.getName());

        holder.ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogShowCategoryLive dialogShowCategoryLive = new DialogShowCategoryLive(mContext, categoryModel.get_id());
                dialogShowCategoryLive.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryModelList != null ? mCategoryModelList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryImage;
        private final TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<CategoryModel> list) {
        this.mCategoryModelList = list;
        notifyDataSetChanged();
    }
}

