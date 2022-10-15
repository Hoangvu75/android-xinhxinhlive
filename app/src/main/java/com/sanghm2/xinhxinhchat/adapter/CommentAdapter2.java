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
import com.sanghm2.xinhxinhchat.utils.FormatData;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.CommentModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter2 extends RecyclerView.Adapter<CommentAdapter2.CommentViewHolder> {

    private List<CommentModel> mCommentModelList;
    private final Context mContext;

    public CommentAdapter2(List<CommentModel> mCommentModelList, Context mContext) {
        this.mCommentModelList = mCommentModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CommentAdapter2.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_2, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter2.CommentViewHolder holder, int position) {
        CommentModel commentModel = mCommentModelList.get(position);

        Glide.with(mContext).load(commentModel.getFrom().getAvatar()).into(holder.civAvatar);
        holder.tvUsername.setText(commentModel.getFrom().getName());
        holder.tvTime.setText(
                FormatData.formatTime(Long.parseLong(commentModel.getCreatedTime()))
        );
        holder.tvCommentContent.setText(commentModel.getMessage());
    }

    @Override
    public int getItemCount() {
        return mCommentModelList != null ? mCommentModelList.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView civAvatar;
        private final TextView tvUsername;
        private final TextView tvTime;
        private final TextView tvCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            civAvatar = itemView.findViewById(R.id.civAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<CommentModel> list) {
        this.mCommentModelList = list;
        notifyDataSetChanged();
    }
}
