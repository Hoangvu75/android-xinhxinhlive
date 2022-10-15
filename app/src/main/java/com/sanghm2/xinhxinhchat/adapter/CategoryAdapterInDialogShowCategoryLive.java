package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.CategoryModel;

import java.util.List;
import java.util.Objects;

public class CategoryAdapterInDialogShowCategoryLive extends RecyclerView.Adapter<CategoryAdapterInDialogShowCategoryLive.CategoryViewHolder> {

    private List<CategoryModel> mCategoryModelList;
    private final Context mContext;
    public  ClickInterface clickInterface ;

    public CategoryAdapterInDialogShowCategoryLive(List<CategoryModel> mCategoryModelList, Context mContext) {
        this.mCategoryModelList = mCategoryModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CategoryAdapterInDialogShowCategoryLive.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapterInDialogShowCategoryLive.CategoryViewHolder holder, int position) {
        CategoryModel categoryModel = mCategoryModelList.get(position);

        Glide.with(mContext).load(categoryModel.getImageUrl()).into(holder.ivCategoryImage);
        holder.tvCategoryName.setText(categoryModel.getName());

        if (categoryModel.isChecked()) {
            holder.vCategoryHighlight.setVisibility(View.VISIBLE);
            holder.tvCategoryName.setTextColor(Color.parseColor("#ff466d"));
        } else {
            holder.vCategoryHighlight.setVisibility(View.INVISIBLE);
            holder.tvCategoryName.setTextColor(Color.WHITE);
        }

        holder.ivCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterface.onSelected(categoryModel);
                holder.vCategoryHighlight.setVisibility(View.VISIBLE);
                holder.tvCategoryName.setTextColor(Color.parseColor("#ff466d"));
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
        private final View vCategoryHighlight;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            vCategoryHighlight = itemView.findViewById(R.id.vCategoryHighlight);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<CategoryModel> list) {
        this.mCategoryModelList = list;
        notifyDataSetChanged();
    }

    public void setClickInterface(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public interface ClickInterface {
        void onSelected(CategoryModel categoryModel);
    }
}

