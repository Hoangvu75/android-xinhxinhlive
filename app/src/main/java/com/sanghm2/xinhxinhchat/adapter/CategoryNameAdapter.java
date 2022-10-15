package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.dialog.DialogShowCategoryLive;
import com.sanghm2.xinhxinhchat.model.CategoryModel;

import java.util.List;

public class CategoryNameAdapter extends RecyclerView.Adapter<CategoryNameAdapter.CategoryViewHolder> {

    private List<CategoryModel> mCategoryModelList;
    private final Context mContext;
    public ClickInterface clickInterface ;

    public CategoryNameAdapter(List<CategoryModel> mCategoryModelList, Context mContext) {
        this.mCategoryModelList = mCategoryModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CategoryNameAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_name, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryNameAdapter.CategoryViewHolder holder, int position) {
        CategoryModel categoryModel = mCategoryModelList.get(position);

        holder.tvCategoryName.setText(categoryModel.getName());

        if (categoryModel.isChecked()) {
            holder.vCategoryHighlight.setVisibility(View.VISIBLE);
            holder.tvCategoryName.setTextColor(Color.parseColor("#ff466d"));
        } else {
            holder.vCategoryHighlight.setVisibility(View.INVISIBLE);
            holder.tvCategoryName.setTextColor(Color.WHITE);
        }

        holder.tvCategoryName.setOnClickListener(new View.OnClickListener() {
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
        private final TextView tvCategoryName;
        private final View vCategoryHighlight;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            vCategoryHighlight = itemView.findViewById(R.id.vCategoryHighlight);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<CategoryModel> list) {
        this.mCategoryModelList = list;
        notifyDataSetChanged();
    }

    public void setClickInterface(CategoryNameAdapter.ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    public interface ClickInterface {
        void onSelected(CategoryModel categoryModel);
    }
}

