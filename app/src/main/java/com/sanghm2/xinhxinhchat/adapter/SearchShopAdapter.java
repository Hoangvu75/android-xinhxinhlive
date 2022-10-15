package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.SearchShopModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchShopAdapter extends RecyclerView.Adapter<SearchShopAdapter.SearchShopViewHolder> {

    private List<SearchShopModel> mSearchShopModelList;
    private final Context mContext;

    public SearchShopAdapter(List<SearchShopModel> mSearchShopModelList, Context mContext) {
        this.mSearchShopModelList = mSearchShopModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SearchShopAdapter.SearchShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchShopAdapter.SearchShopViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchShopAdapter.SearchShopViewHolder holder, int position) {
        SearchShopModel searchShopModel = mSearchShopModelList.get(position);
        Glide.with(mContext).load(searchShopModel.getAvatar()).into(holder.civAvatar);
        holder.tvShopName.setText(searchShopModel.getShopName());
        holder.tvFullName.setText(searchShopModel.getFullName());
    }

    @Override
    public int getItemCount() {
        return mSearchShopModelList.size();
    }

    static class SearchShopViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView civAvatar;
        private final TextView tvShopName;
        private final TextView tvFullName;

        public SearchShopViewHolder(@NonNull View itemView) {
            super(itemView);

            civAvatar = itemView.findViewById(R.id.civAvatar);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvFullName = itemView.findViewById(R.id.tvFullName);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<SearchShopModel> list) {
        this.mSearchShopModelList = list;
        notifyDataSetChanged();
    }
}

