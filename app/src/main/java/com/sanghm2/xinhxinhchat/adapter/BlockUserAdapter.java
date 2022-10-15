package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.BlockUserModel;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.BlockUserViewHolder> {

    private List<BlockUserModel> mBlockUserModelsList;
    private final Context mContext;

    public BlockUserAdapter(List<BlockUserModel> mBlockUserModelsList, Context mContext) {
        this.mBlockUserModelsList = mBlockUserModelsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BlockUserAdapter.BlockUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BlockUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_user, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BlockUserAdapter.BlockUserViewHolder holder, int position) {
        BlockUserModel blockUserModel = mBlockUserModelsList.get(position);

        Glide.with(mContext).load(blockUserModel.getAvatar()).into(holder.civAvatar);
        holder.tvUsername.setText(blockUserModel.getShopName());
        holder.cvCancelBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog alertCancelBlockDialog = new Dialog(mContext);
                alertCancelBlockDialog.setContentView(R.layout.alert_cancel_block_dialog);
                alertCancelBlockDialog.show();
                alertCancelBlockDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertCancelBlockDialog.findViewById(R.id.tvAcceptCancelBlockUser).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> delBlockUserBody = new HashMap<>();
                        delBlockUserBody.put("shopID", blockUserModel.getShopID());
                        RetrofitConfigs.getClient().create(RetrofitAPI.class).delBlockUser(XinhXinhPref.getStringPreference(mContext, "TOKEN"), delBlockUserBody).enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                if (response.isSuccessful()) {
                                    mBlockUserModelsList.remove(blockUserModel);
                                    updateAdapter(mBlockUserModelsList);
                                    alertCancelBlockDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                            }
                        });
                    }
                });
                alertCancelBlockDialog.findViewById(R.id.tvDeclineCancelBlockUser).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertCancelBlockDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBlockUserModelsList != null ? mBlockUserModelsList.size() : 0;
    }

    static class BlockUserViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView civAvatar;
        private final TextView tvUsername;
        private final CardView cvCancelBlock;

        public BlockUserViewHolder(@NonNull View itemView) {
            super(itemView);

            civAvatar = itemView.findViewById(R.id.civAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            cvCancelBlock = itemView.findViewById(R.id.cvCancelBlock);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<BlockUserModel> list) {
        this.mBlockUserModelsList = list;
        notifyDataSetChanged();
    }
}
