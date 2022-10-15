package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.utils.FormatData;


import java.util.List;

public class BroadcastAdapter extends RecyclerView.Adapter<BroadcastAdapter.BroadcastViewHolder> {

    private List<BroadcastModel> mBroadcastModelsList;
    private final Context mContext;

    private ClickInterface clickInterShowLivestream;

    public BroadcastAdapter(List<BroadcastModel> mBroadcastModelsList, Context mContext) {
        this.mBroadcastModelsList = mBroadcastModelsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BroadcastAdapter.BroadcastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BroadcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_broadcast, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BroadcastAdapter.BroadcastViewHolder holder, int position) {
        BroadcastModel broadcastModel = mBroadcastModelsList.get(position);

        String linkLive;
        if (broadcastModel.getStatus().equals("LIVE")) {
            if (broadcastModel.getListLive().size() > 1) {
                linkLive = broadcastModel.getListLive().get(1).getLinkLive();
            } else {
                linkLive = broadcastModel.getListLive().get(0).getLinkLive();
            }
        } else {
            linkLive = broadcastModel.getLinkVideo();
        }

        holder.simpleExoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        holder.pvBroadcastThumbnail.setPlayer(holder.simpleExoPlayer);
        if (linkLive != null && !linkLive.isEmpty()) {
            MediaItem mediaItem = MediaItem.fromUri(linkLive);
            holder.simpleExoPlayer.addMediaItem(mediaItem);
        }
        holder.simpleExoPlayer.prepare();
        holder.simpleExoPlayer.setVolume(0f);
        holder.simpleExoPlayer.seekTo(0);

        Glide.with(mContext).load(broadcastModel.getThumbnail()).into(holder.ivBroadcastThumbnail);
        holder.tvBroadcastTitle.setText(broadcastModel.getTitle());
        holder.tvBroadcastMaxCountViews.setText(FormatData.format(broadcastModel.getMaxCountViews()));
        holder.tvBroadcastCountReactions.setText(FormatData.format(broadcastModel.getCountReactions()));

        if (broadcastModel.getStatus().equals("LIVE")) {
            holder.llBroadcastStatus.setVisibility(View.VISIBLE);
        } else {
            holder.llBroadcastStatus.setVisibility(View.GONE);
        }

        holder.cvBroadcastImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterShowLivestream.onSelected(broadcastModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBroadcastModelsList != null ? mBroadcastModelsList.size() : 0;
    }

    static class BroadcastViewHolder extends RecyclerView.ViewHolder {
        private SimpleExoPlayer simpleExoPlayer;
        private Handler handler;

        private final CardView cvBroadcastImage;
        private final LinearLayout llBroadcastStatus;
        private final StyledPlayerView pvBroadcastThumbnail;
        private final ImageView ivBroadcastThumbnail;
        private final TextView tvBroadcastTitle;
        private final TextView tvBroadcastMaxCountViews;
        private final TextView tvBroadcastCountReactions;

        public BroadcastViewHolder(@NonNull View itemView) {
            super(itemView);

            cvBroadcastImage = itemView.findViewById(R.id.cvBroadcastImage);
            llBroadcastStatus = itemView.findViewById(R.id.llBroadcastStatus);
            pvBroadcastThumbnail = itemView.findViewById(R.id.pvBroadcastThumbnail);
            ivBroadcastThumbnail = itemView.findViewById(R.id.ivBroadcastThumbnail);
            tvBroadcastTitle = itemView.findViewById(R.id.tvBroadcastTitle);
            tvBroadcastMaxCountViews = itemView.findViewById(R.id.tvBroadcastMaxCountViews);
            tvBroadcastCountReactions = itemView.findViewById(R.id.tvBroadcastCountReactions);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<BroadcastModel> list) {
        this.mBroadcastModelsList = list;
        notifyDataSetChanged();
    }

    public void setClickInterShowLivestream(ClickInterface clickInterShowLivestream) {
        this.clickInterShowLivestream = clickInterShowLivestream;
    }

    public interface ClickInterface {
        void onSelected(BroadcastModel broadcastModel);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BroadcastViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        holder.simpleExoPlayer.setPlayWhenReady(true);
        holder.simpleExoPlayer.play();

        holder.handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!holder.simpleExoPlayer.isPlaying()) {
                    holder.simpleExoPlayer.setPlayWhenReady(true);
                    holder.pvBroadcastThumbnail.setVisibility(View.INVISIBLE);
                } else if (holder.simpleExoPlayer.isPlaying()) {
                    holder.pvBroadcastThumbnail.setVisibility(View.VISIBLE);
                }
                holder.handler.postDelayed(this, 100);
            }
        };
        holder.handler.postDelayed(runnable, 100);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BroadcastViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.simpleExoPlayer.setPlayWhenReady(false);
        holder.simpleExoPlayer.release();
        holder.handler.removeCallbacksAndMessages(null);
    }
}

