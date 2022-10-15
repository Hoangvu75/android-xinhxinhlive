package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Rational;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bosphere.fadingedgelayout.FadingEdgeLayout;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.LivestreamActivity;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.dialog.DialogAlertBlockUser;
import com.sanghm2.xinhxinhchat.model.BlockUserModel;
import com.sanghm2.xinhxinhchat.model.BroadcastModel;
import com.sanghm2.xinhxinhchat.model.CommentModel;
import com.sanghm2.xinhxinhchat.model.LoginDataModel;
import com.sanghm2.xinhxinhchat.model.ProductInLivestreamModel;
import com.sanghm2.xinhxinhchat.retrofit.ErrorBody;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitAPI;
import com.sanghm2.xinhxinhchat.retrofit.RetrofitConfigs;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.DoubleClickListener;
import com.sanghm2.xinhxinhchat.utils.FormatData;
import com.sanghm2.xinhxinhchat.utils.SocketConfig;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<BroadcastModel> broadcastModelList;
    private final Context context;
    private final LivestreamActivity livestreamActivity;

    private ClickInterface clickInterMinimize;

    public VideoAdapter(List<BroadcastModel> broadcastModelList,
                        Context context,
                        LivestreamActivity livestreamActivity) {
        this.broadcastModelList = broadcastModelList;
        this.context = context;
        this.livestreamActivity = livestreamActivity;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.broadcastModel = broadcastModelList.get(position);
        // onSetVariables()

        if (holder.broadcastModel.getStatus().equals("LIVE")) {
            if (holder.broadcastModel.getListLive().size() > 1) {
                holder.linkLive = holder.broadcastModel.getListLive().get(1).getLinkLive();
            } else {
                holder.linkLive = holder.broadcastModel.getListLive().get(0).getLinkLive();
            }
        } else {
            holder.linkLive = holder.broadcastModel.getLinkVideo();
        }
        holder.linkFacebook = holder.broadcastModel.getListLive().get(0).getLinkFacebook();

        holder.pvLivestreamVideo.setControllerShowTimeoutMs(0);
        holder.pvLivestreamVideo.setControllerHideOnTouch(false);
        holder.pvLivestreamVideo.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                if (holder.llAllViewOfController.getVisibility() == View.VISIBLE) {
                    holder.llAllViewOfController.setVisibility(View.GONE);
                } else {
                    holder.llAllViewOfController.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.llMinimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterMinimize.onSelected();
            }
        });

        holder.llCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                livestreamActivity.onBackPressed();
            }
        });

        Glide.with(context).load(holder.broadcastModel.getAvatarOwner()).into(holder.civAvatar);
        holder.tvUsername.setText(holder.broadcastModel.getShopName());
        holder.llFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(XinhXinhPref.getStringPreference(context, Constants.TOKEN), "") && XinhXinhPref.getStringPreference(context, Constants.TOKEN) != null) {
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).getCheckFollow(XinhXinhPref.getStringPreference(context, Constants.TOKEN), holder.broadcastModel.getShopID()).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                HashMap<String, String> postUnFollow = new HashMap<>();
                                postUnFollow.put("followee", holder.broadcastModel.getShopID());
                                AlertUnfollowDialog alertUnfollowDialog = new AlertUnfollowDialog(context, postUnFollow, holder);
                                alertUnfollowDialog.show();
                            } else {
                                HashMap<String, String> postFollow = new HashMap<>();
                                postFollow.put("followee", holder.broadcastModel.getShopID());
                                RetrofitConfigs.getClient().create(RetrofitAPI.class).postFollow(XinhXinhPref.getStringPreference(context, Constants.TOKEN), postFollow).enqueue(new Callback<JsonElement>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                        Toast.makeText(context, "Đã theo dõi", Toast.LENGTH_SHORT).show();
                                        holder.llFollowButton.setBackgroundResource(R.drawable.bgr_border_10);
                                        holder.ivFollowSymbol.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                                        holder.tvFollow.setText("Đã theo dõi");
                                        holder.tvFollow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
                } else {
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(context);
                    alertLoginDialog.show();
                }
            }
        });
        holder.llReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(XinhXinhPref.getStringPreference(context, Constants.TOKEN), "") && XinhXinhPref.getStringPreference(context, Constants.TOKEN) != null) {
                    ReportDialog reportDialog = new ReportDialog(context, holder.broadcastModel, broadcastModelList);
                    reportDialog.show();
                } else {
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(context);
                    alertLoginDialog.show();
                }
            }
        });

        holder.tvBroadcastDescription.setText(holder.broadcastModel.getDescription());
        holder.tvCountViews.setText(FormatData.format(holder.broadcastModel.getMaxCountViews()));
        holder.tvCountReactions.setText(FormatData.format(holder.broadcastModel.getCountReactions()));

        holder.rvCommentList.setAdapter(holder.commentAdapter);

        holder.tvCountComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentDialog commentDialog = new CommentDialog(context, holder.broadcastModel, holder.countComments);
                commentDialog.show();
            }
        });
        holder.civShowProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowProductDialog showProductDialog = new ShowProductDialog(context, holder.broadcastModel);
                showProductDialog.show();
            }
        });
        holder.tvWriteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(XinhXinhPref.getStringPreference(context, Constants.TOKEN), "") && XinhXinhPref.getStringPreference(context, Constants.TOKEN) != null) {
                    WriteCommentDialog writeCommentDialog = new WriteCommentDialog(context, holder.broadcastModel);
                    writeCommentDialog.show();
                } else {
                    AlertLoginDialog alertLoginDialog = new AlertLoginDialog(context);
                    alertLoginDialog.show();
                }
            }
        });
        holder.llShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLivestreamDialog shareLivestreamDialog = new ShareLivestreamDialog(context, holder.linkFacebook);
                shareLivestreamDialog.show();
            }
        });
        holder.llReactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.llPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.simpleExoPlayer.isPlaying()) {
                    onPause();
                } else {
                    onStart();
                }
            }

            private void onPause() {
                holder.simpleExoPlayer.setPlayWhenReady(false);
                holder.simpleExoPlayer.getPlaybackState();
                Toast.makeText(context, "Paused", Toast.LENGTH_SHORT).show();
                holder.ivIconController.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_resume));
            }

            private void onStart(){
                holder.simpleExoPlayer.setPlayWhenReady(true);
                holder.simpleExoPlayer.getPlaybackState();
                Toast.makeText(context, "Resumed", Toast.LENGTH_SHORT).show();
                holder.ivIconController.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_pause));
            }
        });
    }

    @Override
    public int getItemCount() {
        return broadcastModelList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements SocketConfig.EventSocket
    {
        SocketConfig socketConfig;

        BroadcastModel broadcastModel;
        String linkLive;
        String linkFacebook;

        Handler handler;

        PlayerView pvLivestreamVideo;
        SimpleExoPlayer simpleExoPlayer;
        MediaItem mediaItem;
        LinearLayout llAllViewOfController;

        LinearLayout llMinimizeButton;
        LinearLayout llCloseButton;
        CircleImageView civAvatar;
        TextView tvUsername;
        LinearLayout llFollowButton;
        ImageView ivFollowSymbol;
        TextView tvFollow;
        LinearLayout llReportButton;
        TextView tvTimeOfLive;

        TextView tvBroadcastDescription;
        TextView tvCountViews;
        TextView tvCountReactions;

        List<CommentModel> commentModelList;
        RecyclerView rvCommentList;
        CommentAdapter commentAdapter;
        FadingEdgeLayout felCommentLayout;

        TextView tvProductCount;
        TextView tvCountComments;
        int countComments;
        CircleImageView civShowProductList;
        TextView tvWriteComment;
        LinearLayout llShareButton;
        LinearLayout llReactButton;

        LinearLayout llPlayControl;
        CardView cvLive;
        LinearLayout llVideoController;
        ImageView ivIconController;

        public VideoViewHolder(@NonNull View itemView){
            super(itemView);
            pvLivestreamVideo = itemView.findViewById(R.id.pvLivestreamVideo);
            simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
            llAllViewOfController = itemView.findViewById(R.id.llAllViewOfController);

            llMinimizeButton = itemView.findViewById(R.id.llMinimizeButton);
            llCloseButton = itemView.findViewById(R.id.llCloseButton);
            civAvatar = itemView.findViewById(R.id.civAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            llFollowButton = itemView.findViewById(R.id.llFollowButton);
            ivFollowSymbol = itemView.findViewById(R.id.ivFollowSymbol);
            tvFollow = itemView.findViewById(R.id.tvFollow);
            llReportButton = itemView.findViewById(R.id.llReportButton);
            tvTimeOfLive = itemView.findViewById(R.id.tvTimeOfLive);

            tvBroadcastDescription = itemView.findViewById(R.id.tvBroadcastDescription);
            tvCountViews = itemView.findViewById(R.id.tvCountViews);
            tvCountReactions = itemView.findViewById(R.id.tvCountReactions);

            commentModelList = new ArrayList<CommentModel>();
            rvCommentList = itemView.findViewById(R.id.rvCommentList);
            commentAdapter = new CommentAdapter(commentModelList, context);
            felCommentLayout = itemView.findViewById(R.id.felCommentLayout);

            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            tvCountComments = itemView.findViewById(R.id.tvCountComments);
            civShowProductList = itemView.findViewById(R.id.civShowProductList);
            tvWriteComment = itemView.findViewById(R.id.tvWriteComment);
            llShareButton = itemView.findViewById(R.id.llShareButton);
            llReactButton = itemView.findViewById(R.id.llReactButton);

            llPlayControl = itemView.findViewById(R.id.llPlayControl);
            cvLive = itemView.findViewById(R.id.cvLive);
            llVideoController = itemView.findViewById(R.id.llVideoController);
            ivIconController = itemView.findViewById(R.id.ivIconController);
        }

        private List<String> getLstKey() {
            List<String> lstKey = new ArrayList<>();
            lstKey.add(Constants.AUTHENTICATED);
            lstKey.add(Constants.COMMENT_FB);
            lstKey.add(Constants.VIEW_CAMPAIGN);
            return lstKey;
        }

        private void initSocket() {
            socketConfig = new SocketConfig(this, context, getLstKey());
        }

        private void stopSocket() {
            if (socketConfig != null) {
                socketConfig.onDisconnect();
            }
        }

        @Override
        public void onConnect() {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onDisconnect() {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onConnectError() {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onReceiver(String key, String data) {
            getReceiver(key, data);
        }

        private void getReceiver(String key, String data) {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @SuppressLint("RtlHardcoded")
                @Override
                public void run() {
                    switch (key) {
                        case Constants.AUTHENTICATED:
                            socketConfig.requestCampaign(broadcastModel.get_id());
                            break;
                        case Constants.COMMENT_FB:
                            getComment(data);
                            break;
                        case Constants.VIEW_CAMPAIGN:
                            Transition transition = new Slide(Gravity.LEFT);
                            transition.setDuration(750);
                            transition.addTarget(R.id.llToastRoot);
                            TransitionManager.beginDelayedTransition(llAllViewOfController, transition);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    itemView.findViewById(R.id.llToastRoot).setVisibility(View.VISIBLE);
                                }
                            }, 0);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    itemView.findViewById(R.id.llToastRoot).animate().alpha(0.0f).setDuration(1000);
                                }
                            }, 3000);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    itemView.findViewById(R.id.llToastRoot).setVisibility(View.INVISIBLE);
                                    itemView.findViewById(R.id.llToastRoot).animate().alpha(1.0f).setDuration(0);
                                    handler.removeCallbacksAndMessages(null);
                                }
                            }, 4000);

                            break;
                    }
                }
            });
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        private void getComment(String data) {
            CommentModel commentModel = CommentModel.fromJsonSocket(data);

            if (commentModel != null) {
                commentModelList.add(0, commentModel);
                countComments++;
                if (commentModelList.size() > 25) {
                    commentModelList.remove(25);
                }
            }
            commentAdapter.updateAdapter(commentModelList);
            tvCountComments.setText("+" + countComments + " bình luận");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<BroadcastModel> list) {
        this.broadcastModelList = list;
        notifyDataSetChanged();
    }

    static class AlertLoginDialog extends BottomSheetDialog {
        private final Context context;

        private LinearLayout llLogin1;
        private TextView tvMoveToLogin;
        private ConstraintLayout cslLogin2;
        private TextView tvBackButton;
        private CardView cvLoginButton;
        private EditText etLoginPhoneNumber;
        private EditText etLoginPassword;

        public AlertLoginDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            setContentView(R.layout.bottom_sheet_report_login_dialog);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            llLogin1 = findViewById(R.id.llLogin1);
            tvMoveToLogin = findViewById(R.id.tvMoveToLogin);
            cslLogin2 = findViewById(R.id.cslLogin2);
            tvBackButton = findViewById(R.id.tvBackButton);
            cvLoginButton = findViewById(R.id.cvLoginButton);
            etLoginPhoneNumber = findViewById(R.id.etLoginPhoneNumber);
            etLoginPassword = findViewById(R.id.etLoginPassword);
        }

        private void onSetVariables() {
            llLogin1.setVisibility(View.VISIBLE);
            tvMoveToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llLogin1.setVisibility(View.GONE);
                    cslLogin2.setVisibility(View.VISIBLE);
                }
            });
            tvBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llLogin1.setVisibility(View.VISIBLE);
                    cslLogin2.setVisibility(View.GONE);
                }
            });
            cvLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assert etLoginPhoneNumber != null;
                    assert etLoginPassword != null;
                    if (!etLoginPhoneNumber.getText().toString().equals("") && !etLoginPassword.getText().toString().equals("")) {
                        HashMap<String, String> loginBody = new HashMap<>();
                        loginBody.put("phone", etLoginPhoneNumber.getText().toString());
                        loginBody.put("password", etLoginPassword.getText().toString());
                        RetrofitConfigs.getClient().create(RetrofitAPI.class).LoginAccount(loginBody).enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    LoginDataModel loginData = LoginDataModel.FromJson(response.body().toString());
                                    Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                    XinhXinhPref.setStringPreference(context, Constants.TOKEN, "Bearer " + loginData.getAccessToken());
                                    dismiss();
                                } else {
                                    ErrorBody.Error401(context, response);
                                    String error = ErrorBody.getErrorMessage(response);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                            }
                        });
                    } else {
                        Toast.makeText(context, "Vui lòng nhập thông tin đăng nhập", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    static class AlertUnfollowDialog extends Dialog {
        private final Context context;
        private final HashMap<String, String> postUnFollow;
        private final VideoViewHolder holder;

        private TextView tvAcceptUnfollow;
        private TextView tvDeclineUnfollow;

        public AlertUnfollowDialog(@NonNull Context context, HashMap<String, String> postUnFollow, VideoViewHolder holder) {
            super(context);
            this.context = context;
            this.postUnFollow = postUnFollow;
            this.holder = holder;
            setContentView(R.layout.alert_unfollow_dialog);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            tvAcceptUnfollow = findViewById(R.id.tvAcceptUnfollow);
            tvDeclineUnfollow = findViewById(R.id.tvDeclineUnfollow);
        }

        private void onSetVariables() {
            tvAcceptUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RetrofitConfigs.getClient().create(RetrofitAPI.class).postUnFollow(XinhXinhPref.getStringPreference(context, Constants.TOKEN), postUnFollow).enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            Toast.makeText(context, "Hủy theo dõi", Toast.LENGTH_SHORT).show();
                            holder.llFollowButton.setBackgroundResource(R.drawable.bgr_border_10_corner);
                            holder.ivFollowSymbol.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                            holder.tvFollow.setText("Theo dõi");
                            holder.tvFollow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                            dismiss();
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
                }
            });
            tvDeclineUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    class ReportDialog extends Dialog {
        private final Context context;
        private final BroadcastModel broadcastModel;
        private final List<BroadcastModel> broadcastModelList;

        private TextView tvCloseButton;
        private LinearLayout llReportLiveContent;
        private LinearLayout llBlockUser;

        public ReportDialog(@NonNull Context context, BroadcastModel broadcastModel, List<BroadcastModel> broadcastModelList) {
            super(context);
            this.context = context;
            this.broadcastModel = broadcastModel;
            this.broadcastModelList = broadcastModelList;
            setContentView(R.layout.bottom_sheet_report_dialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            getWindow().setAttributes(lp);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            tvCloseButton = findViewById(R.id.tvCloseButton);
            llReportLiveContent = findViewById(R.id.llReportLiveContent);
            llBlockUser = findViewById(R.id.llBlockUser);
        }

        private void onSetVariables() {
            tvCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            llReportLiveContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ReportLiveContentDialog reportLiveContentDialog = new ReportLiveContentDialog(context, broadcastModel);
                    dismiss();
                    reportLiveContentDialog.show();
                }
            });
            llBlockUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogAlertBlockUser alertBlockUserDialog = new DialogAlertBlockUser(context, broadcastModel);
                    alertBlockUserDialog.setContentView(R.layout.alert_block_user_dialog);
                    alertBlockUserDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertBlockUserDialog.setClickInterfaceBlockUser(new DialogAlertBlockUser.ClickInterface() {
                        @Override
                        public void onSelected() {
                            RetrofitConfigs.getClient().create(RetrofitAPI.class).getBroadcast(XinhXinhPref.getStringPreference(context, Constants.TOKEN), 10, 1, 10, "newest").enqueue(new Callback<JsonElement>() {
                                @Override
                                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        broadcastModelList.remove(broadcastModel);
                                        updateAdapter(broadcastModelList);
                                    } else {
                                        ErrorBody.Error401(context, response);
                                        String error = ErrorBody.getErrorMessage(response);
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                }
                            });
                        }
                    });
                    alertBlockUserDialog.show();
                    dismiss();
                }
            });
        }
    }

    static class CommentDialog extends Dialog {
        private final Context context;
        private final BroadcastModel broadcastModel;
        private final int countComments;

        private TextView tvCommentTitle;
        private TextView tvCloseButton;

        private List<CommentModel> commentModelList;
        private RecyclerView rvCommentList;
        private CommentAdapter2 commentAdapter;
        private RecyclerView.LayoutManager commentLayout;

        private static int loadCountOfComments;

        private LinearLayout llCommentPart;
        private CircleImageView civAvatar;
        private EditText etWriteComment;
        private LinearLayout llPostCommentButton;

        public CommentDialog(@NonNull Context context, BroadcastModel broadcastModel, int countComments) {
            super(context);
            this.context = context;
            this.broadcastModel = broadcastModel;
            this.countComments = countComments + 4;
            setContentView(R.layout.bottom_sheet_comment_dialog);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            getWindow().setAttributes(lp);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
            onLoadData();
        }

        private void onInitView() {
            tvCommentTitle = findViewById(R.id.tvCommentTitle);
            tvCloseButton = findViewById(R.id.tvCloseButton);

            commentModelList = new ArrayList<CommentModel>();
            rvCommentList = findViewById(R.id.rvCommentList);
            commentAdapter = new CommentAdapter2(commentModelList, context);
            commentLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

            loadCountOfComments = 2;

            llCommentPart = findViewById(R.id.llCommentPart);
            civAvatar = findViewById(R.id.civAvatar);
            etWriteComment = findViewById(R.id.etWriteComment);
            llPostCommentButton = findViewById(R.id.llPostCommentButton);
        }

        @SuppressLint("SetTextI18n")
        @RequiresApi(api = Build.VERSION_CODES.M)
        private void onSetVariables() {
            tvCommentTitle.setText("Bình luận (" + countComments + ")");
            tvCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            if (!Objects.equals(XinhXinhPref.getStringPreference(context, Constants.TOKEN), "") && XinhXinhPref.getStringPreference(context, Constants.TOKEN) != null) {
                llCommentPart.setVisibility(View.VISIBLE);
            } else {
                llCommentPart.setVisibility(View.GONE);
            }
            rvCommentList.setAdapter(commentAdapter);
            rvCommentList.setLayoutManager(commentLayout);
            rvCommentList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (!view.canScrollVertically(1)) {
                        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                                .getCommentOfBroadcast(
                                        XinhXinhPref.getStringPreference(context, Constants.TOKEN),
                                        broadcastModel.get_id(),
                                        loadCountOfComments, 10, -1).enqueue(new Callback<JsonElement>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            List<CommentModel> newCommentList = CommentModel.FromJson(response.body().toString());
                                            commentModelList.addAll(newCommentList);
                                            commentAdapter.updateAdapter(commentModelList);

                                            loadCountOfComments++;
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                    }
                                });
                    }
                }
            });

            llPostCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!etWriteComment.getText().toString().trim().equals("")) {
                        HashMap<String, String> comment = new HashMap<>();
                        comment.put("message", etWriteComment.getText().toString());
                        comment.put("campaignID", broadcastModel.get_id());

                        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                                .postCommentViewer(
                                        XinhXinhPref.getStringPreference(context, Constants.TOKEN),
                                        comment).enqueue(new Callback<JsonElement>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Đã đăng bình luận", Toast.LENGTH_SHORT).show();
                                            etWriteComment.setText("");
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                    }
                                });
                    } else {
                        Toast.makeText(context, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void onLoadData() {
            RetrofitConfigs.getClient().create(RetrofitAPI.class)
                    .getCommentOfBroadcast(
                            XinhXinhPref.getStringPreference(context, Constants.TOKEN),
                            broadcastModel.get_id(),
                            1, 10, -1).
                    enqueue(new Callback<JsonElement>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                commentModelList = CommentModel.FromJson(response.body().toString());
                                commentAdapter.updateAdapter(commentModelList);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });

            RetrofitConfigs.getClient().create(RetrofitAPI.class)
                    .getProfile(XinhXinhPref.getStringPreference(context, Constants.TOKEN))
                    .enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                try {
                                    assert response.body() != null;
                                    JSONObject data = new JSONObject(response.body().toString()).getJSONObject("data");
                                    String avatar = data.getJSONObject("userInfo").getString("avatar");
                                    Glide.with(context).load(avatar).into(civAvatar);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
        }
    }

    static class WriteCommentDialog extends BottomSheetDialog {
        private final Context context;
        private final BroadcastModel broadcastModel;

        private CircleImageView civAvatar;
        public EditText etWriteComment;
        private LinearLayout llPostCommentButton;

        public WriteCommentDialog(@NonNull Context context, BroadcastModel broadcastModel) {
            super(context);
            this.context = context;
            this.broadcastModel = broadcastModel;
            setContentView(R.layout.bottom_sheet_write_comment_dialog);
            setCanceledOnTouchOutside(true);
            setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    closeKeyboard();
                }
            });
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
            onLoadData();
        }

        private void onInitView() {
            civAvatar = findViewById(R.id.civAvatar);
            etWriteComment = findViewById(R.id.etWriteComment);
            llPostCommentButton = findViewById(R.id.llPostCommentButton);
        }

        private void onSetVariables() {
            etWriteComment.requestFocus();
            openKeyboard();
            llPostCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!etWriteComment.getText().toString().trim().equals("")) {
                        HashMap<String, String> comment = new HashMap<>();
                        comment.put("message", etWriteComment.getText().toString());
                        comment.put("campaignID", broadcastModel.get_id());

                        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                                .postCommentViewer(
                                        XinhXinhPref.getStringPreference(context, Constants.TOKEN),
                                        comment).enqueue(new Callback<JsonElement>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Đã đăng bình luận", Toast.LENGTH_SHORT).show();
                                            etWriteComment.setText("");
                                            dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                    }
                                });
                    } else {
                        Toast.makeText(context, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void onLoadData() {
            RetrofitConfigs.getClient().create(RetrofitAPI.class)
                    .getProfile(XinhXinhPref.getStringPreference(context, Constants.TOKEN))
                    .enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                try {
                                    assert response.body() != null;
                                    JSONObject data = new JSONObject(response.body().toString()).getJSONObject("data");
                                    String avatar = data.getJSONObject("userInfo").getString("avatar");
                                    Glide.with(context).load(avatar).into(civAvatar);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
        }

        private void openKeyboard() {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        private void closeKeyboard() {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    static class ReportLiveContentDialog extends Dialog {
        private final Context context;
        private final BroadcastModel broadcastModel;

        private LinearLayout llBackButton;
        private RadioButton rbBtn0, rbBtn1, rbBtn2, rbBtn3, rbBtn4;
        private EditText etWriteReport;
        private TextView tvSendReport;

        public ReportLiveContentDialog(@NonNull Context context, BroadcastModel broadcastModel) {
            super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
            this.context = context;
            this.broadcastModel = broadcastModel;
            getWindow().getAttributes().windowAnimations = R.style.DialogSwipeAnimationLR;
            setContentView(R.layout.dialog_report_live_content);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            llBackButton = findViewById(R.id.llBackButton);
            rbBtn0 = findViewById(R.id.rbBtn0);
            rbBtn1 = findViewById(R.id.rbBtn1);
            rbBtn2 = findViewById(R.id.rbBtn2);
            rbBtn3 = findViewById(R.id.rbBtn3);
            rbBtn4 = findViewById(R.id.rbBtn4);
            etWriteReport = findViewById(R.id.etWriteReport);
            tvSendReport = findViewById(R.id.tvSendReport);
        }

        private void onSetVariables() {
            llBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            rbBtn0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        rbBtn0.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        rbBtn0.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                    } else {
                        rbBtn0.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        rbBtn0.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                    }
                }
            });
            rbBtn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        rbBtn1.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        rbBtn1.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                    } else {
                        rbBtn1.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        rbBtn1.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                    }
                }
            });
            rbBtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        rbBtn2.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        rbBtn2.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                    } else {
                        rbBtn2.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        rbBtn2.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                    }
                }
            });
            rbBtn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        rbBtn3.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        rbBtn3.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                    } else {
                        rbBtn3.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        rbBtn3.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                    }
                }
            });
            rbBtn4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        rbBtn4.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        rbBtn4.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        etWriteReport.setVisibility(View.VISIBLE);
                    } else {
                        rbBtn4.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        rbBtn4.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        etWriteReport.setVisibility(View.GONE);
                    }
                }
            });
            tvSendReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog alertReportDialog = new Dialog(context);
                    alertReportDialog.setContentView(R.layout.alert_report_dialog);
                    alertReportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertReportDialog.findViewById(R.id.tvDeclineReport).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertReportDialog.dismiss();
                        }
                    });
                    alertReportDialog.findViewById(R.id.tvAcceptReport).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String, String> body = new HashMap<String, String>();
                            body.put("livestreamId", broadcastModel.get_id());
                            if (rbBtn0.isChecked()) {
                                body.put("description", "Bạo lực");
                            } else if (rbBtn1.isChecked()) {
                                body.put("description", "Quấy rối");
                            } else if (rbBtn2.isChecked()) {
                                body.put("description", "Ảnh khỏa thân");
                            } else if (rbBtn3.isChecked()) {
                                body.put("description", "Nội dung nhạy cảm");
                            } else if (rbBtn4.isChecked()) {
                                if (!etWriteReport.getText().toString().equals("")) {
                                    body.put("description", etWriteReport.getText().toString());
                                } else {
                                    Toast.makeText(context, "Hãy nhập nội dung cần báo cáo", Toast.LENGTH_SHORT).show();
                                    alertReportDialog.dismiss();
                                    etWriteReport.requestFocus();
                                }
                            }
                            if (body.get("description") != null) {
                                RetrofitConfigs.getClient().create(RetrofitAPI.class).
                                        postReport(XinhXinhPref.getStringPreference(context, Constants.TOKEN), body).
                                        enqueue(new Callback<JsonElement>() {
                                            @Override
                                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                                if (response.isSuccessful()) {
                                                    Toast.makeText(context, "Gửi báo cáo thành công", Toast.LENGTH_SHORT).show();
                                                    alertReportDialog.dismiss();
                                                    dismiss();
                                                } else {
                                                    try {
                                                        assert response.errorBody() != null;
                                                        Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                                            }
                                        });
                            }
                        }
                    });
                    alertReportDialog.show();
                }
            });
        }
    }

    static class ShareLivestreamDialog extends Dialog {
        private final Context context;
        private final String linkFacebook;

        private TextView tvCloseButton;
        private CircleImageView civFacebookShare,
                civInstagramShare,
                civMessengerShare,
                civViberShare,
                civZaloShare,
                civOtherShare;
        private TextView tvCopyButton;

        public ShareLivestreamDialog(@NonNull Context context, String linkFacebook) {
            super(context);
            this.context = context;
            this.linkFacebook = linkFacebook;
            setContentView(R.layout.dialog_share_livestream);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            getWindow().setAttributes(lp);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
        }

        private void onInitView() {
            tvCloseButton = findViewById(R.id.tvCloseButton);
            civFacebookShare = findViewById(R.id.civFacebookShare);
            civInstagramShare = findViewById(R.id.civInstagramShare);
            civMessengerShare = findViewById(R.id.civMessengerShare);
            civViberShare = findViewById(R.id.civViberShare);
            civZaloShare = findViewById(R.id.civZaloShare);
            civOtherShare = findViewById(R.id.civOtherShare);
            tvCopyButton = findViewById(R.id.tvCopyButton);
        }

        private void onSetVariables() {
            tvCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            civFacebookShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    shareAppLinkViaFacebook(linkFacebook, context);
                }
            });
            civInstagramShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    shareAppLinkViaInstagram(linkFacebook, context);
                }
            });
            civMessengerShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    shareAppLinkViaMessenger(linkFacebook, context);
                }
            });
            civViberShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    shareAppLinkViaViber(linkFacebook, context);
                }
            });
            civZaloShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    shareAppLinkViaZalo(linkFacebook, context);
                }
            });
            civOtherShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    shareAppLink(linkFacebook, context);
                }
            });
            tvCopyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied shared-link", linkFacebook);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Đã copy link livestream", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void shareAppLinkViaFacebook(String urlToShare, Context context) {
            try {
                Intent intent1 = new Intent();
                intent1.setClassName("com.facebook.katana", "com.facebook.katana.activity.composer.ImplicitShareIntentHandler");
                intent1.setAction("android.intent.action.SEND");
                intent1.setType("text/plain");
                intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                context.startActivity(intent1);
            } catch (Exception e) {
                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                context.startActivity(intent);
            }
        }

        private void shareAppLinkViaInstagram(String urlToShare, Context context) {
            try {
                Intent intent1 = new Intent();
                intent1.setPackage("com.instagram.android");
                intent1.setAction("android.intent.action.SEND");
                intent1.setType("text/plain");
                intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                context.startActivity(intent1);
            } catch (Exception e) {
                String sharerUrl = "https://www.instagram.com/sharer.php?u=" + urlToShare;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                context.startActivity(intent);
            }
        }

        private void shareAppLinkViaMessenger(String urlToShare, Context context) {
            try {
                Intent intent1 = new Intent();
                intent1.setPackage("com.facebook.orca");
                intent1.setAction("android.intent.action.SEND");
                intent1.setType("text/plain");
                intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                context.startActivity(intent1);
            } catch (Exception e) {
                Toast.makeText(context, "Hãy cài đặt Messenger", Toast.LENGTH_SHORT).show();
            }
        }

        private void shareAppLinkViaViber(String urlToShare, Context context) {
            try {
                Intent intent1 = new Intent();
                intent1.setPackage("com.viber.voip");
                intent1.setAction("android.intent.action.SEND");
                intent1.setType("text/plain");
                intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                context.startActivity(intent1);
            } catch (Exception e) {
                Toast.makeText(context, "Hãy cài đặt Viber", Toast.LENGTH_SHORT).show();
            }
        }

        private void shareAppLinkViaZalo(String urlToShare, Context context) {
            try {
                Intent intent1 = new Intent();
                intent1.setPackage("com.zing.zalo");
                intent1.setAction("android.intent.action.SEND");
                intent1.setType("text/plain");
                intent1.putExtra("android.intent.extra.TEXT", urlToShare);
                context.startActivity(intent1);
            } catch (Exception e) {
                Toast.makeText(context, "Hãy cài đặt Zalo", Toast.LENGTH_SHORT).show();
            }
        }

        private void shareAppLink(String urlToShare, Context context) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ ngay");
                shareIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);
                context.startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        }
    }

    static class ShowProductDialog extends Dialog {
        private final Context context;
        private final BroadcastModel broadcastModel;

        private TextView tvCloseButton;
        private TextView tvNoProductNotification;

        private List<ProductInLivestreamModel> productInLivestreamModelList;
        private RecyclerView rvProductInLive;
        private ProductInLivestreamAdapter productInLivestreamAdapter;
        private RecyclerView.LayoutManager productInLivestreamLayout;

        public ShowProductDialog(@NonNull Context context, BroadcastModel broadcastModel) {
            super(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
            this.context = context;
            this.broadcastModel = broadcastModel;
            getWindow().getAttributes().windowAnimations = R.style.DialogSwipeAnimationLR;
            setContentView(R.layout.dialog_show_product);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            onInitView();
            onSetVariables();
            onLoadData();
        }

        private void onInitView() {
            tvCloseButton = findViewById(R.id.tvCloseButton);
            tvNoProductNotification = findViewById(R.id.tvNoProductNotification);

            productInLivestreamModelList = new ArrayList<ProductInLivestreamModel>();
            rvProductInLive = findViewById(R.id.rvProductInLive);
            productInLivestreamAdapter = new ProductInLivestreamAdapter(productInLivestreamModelList, context);
            productInLivestreamLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        }

        private void onSetVariables() {
            tvCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            rvProductInLive.setAdapter(productInLivestreamAdapter);
            rvProductInLive.setLayoutManager(productInLivestreamLayout);
        }

        private void onLoadData() {
            RetrofitConfigs.getClient().create(RetrofitAPI.class)
                    .getProductInLivestream(broadcastModel.get_id())
                    .enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    productInLivestreamModelList = ProductInLivestreamModel.FromJson(response.body().toString());
                                    productInLivestreamAdapter.updateAdapter(productInLivestreamModelList);
                                    if (productInLivestreamModelList.isEmpty()) {
                                        tvNoProductNotification.setVisibility(View.VISIBLE);
                                    } else {
                                        rvProductInLive.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                        }
                    });
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull VideoViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.simpleExoPlayer.setPlayWhenReady(false);
        holder.simpleExoPlayer.release();

        if (holder.handler != null) {
            holder.handler.removeCallbacksAndMessages(null);
        }

        holder.stopSocket();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewAttachedToWindow(@NonNull VideoViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        RetrofitConfigs.getClient().create(RetrofitAPI.class).getBlockUser(XinhXinhPref.getStringPreference(context, "TOKEN")).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<BlockUserModel> blockUserModelsList = BlockUserModel.FromJson(response.body().toString());
                        for (BlockUserModel blockUserModel : blockUserModelsList) {
                            if (Objects.equals(blockUserModel.getShopID(), holder.broadcastModel.getShopID())) {
                                broadcastModelList.remove(holder.broadcastModel);
                                updateAdapter(broadcastModelList);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

            }
        });

        holder.initSocket();

        holder.simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
        holder.mediaItem = MediaItem.fromUri(holder.linkLive);
        holder.pvLivestreamVideo.setPlayer(holder.simpleExoPlayer);
        holder.simpleExoPlayer.addMediaItem(holder.mediaItem);
        holder.simpleExoPlayer.prepare();
        holder.simpleExoPlayer.setPlayWhenReady(true);

        Handler handlerPlay = new Handler();
        Runnable runnablePlay = new Runnable() {
            @Override
            public void run() {
                if (!holder.simpleExoPlayer.isPlaying()) {
                    holder.simpleExoPlayer.setPlayWhenReady(true);
                    holder.simpleExoPlayer.play();
                } else if (holder.simpleExoPlayer.isPlaying()) {
                    handlerPlay.removeCallbacksAndMessages(null);
                }
                handlerPlay.postDelayed(this, 100);
            }
        };
        handlerPlay.postDelayed(runnablePlay, 100);

        if (holder.broadcastModel.getStatus().equals("LIVE")) {
            holder.handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    long unix = holder.broadcastModel.getStartTime() * 1000;
                    Date dt = new Date(unix);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");
                    df.setTimeZone(TimeZone.getTimeZone("GMT+7"));

                    String createdTime = df.format(dt);
                    String currentTime = df.format(new Date());

                    Date createdTimeDate = null;
                    Date currentTimeDate = null;
                    try {
                        createdTimeDate = df.parse(createdTime);
                        currentTimeDate = df.parse(currentTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    assert createdTimeDate != null;
                    long createdTimeMillis = createdTimeDate.getTime();
                    assert currentTimeDate != null;
                    long currentTimeMillis = currentTimeDate.getTime();

                    holder.tvTimeOfLive.setText(FormatData.formatDuration(currentTimeMillis - createdTimeMillis));

                    holder.handler.postDelayed(this, 1000);
                }
            };
            holder.handler.postDelayed(runnable, 1000);
            holder.llVideoController.setVisibility(View.GONE);
        } else {
            holder.tvTimeOfLive.setVisibility(View.GONE);
            holder.cvLive.setVisibility(View.GONE);
            holder.llVideoController.setVisibility(View.VISIBLE);
        }

        // onLoadData()
        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                .getCheckFollow(
                        XinhXinhPref.getStringPreference(context, Constants.TOKEN),
                        holder.broadcastModel.getShopID()).enqueue(new Callback<JsonElement>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            holder.llFollowButton.setBackgroundResource(R.drawable.bgr_border_10);
                            holder.ivFollowSymbol.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                            holder.tvFollow.setText("Đã theo dõi");
                            holder.tvFollow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.white)));
                        } else {
                            holder.llFollowButton.setBackgroundResource(R.drawable.bgr_border_10_corner);
                            holder.ivFollowSymbol.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                            holder.tvFollow.setText("Theo dõi");
                            holder.tvFollow.setTextColor(ColorStateList.valueOf(context.getResources().getColor(R.color.watermelon)));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    }
                });
        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                .getCommentOfBroadcast(
                        XinhXinhPref.getStringPreference(context, Constants.TOKEN),
                        holder.broadcastModel.get_id(),
                        1, 25, -1).enqueue(new Callback<JsonElement>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            holder.commentModelList = CommentModel.FromJson(response.body().toString());
                            holder.commentAdapter.updateAdapter(holder.commentModelList);

                            if (holder.commentModelList.size() >= 5) {
                                holder.felCommentLayout.setFadeEdges(true, false, false, false);
                                holder.felCommentLayout.setFadeSizes(200, 0, 0, 0);
                            } else {
                                holder.felCommentLayout.setFadeEdges(false, false, false, false);
                                holder.felCommentLayout.setFadeSizes(0, 0, 0, 0);
                            }

                            try {
                                JSONObject data = new JSONObject(response.body().toString()).getJSONObject("data");
                                holder.countComments = data.getInt("total") - 4;
                                if (holder.countComments > 0) {
                                    holder.tvCountComments.setVisibility(View.VISIBLE);
                                    holder.tvCountComments.setText("+" + holder.countComments + " bình luận");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            holder.rvCommentList.scrollToPosition(0);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    }
                });
        RetrofitConfigs.getClient().create(RetrofitAPI.class)
                .getProductInLivestream(holder.broadcastModel.get_id())
                .enqueue(new Callback<JsonElement>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                List<ProductInLivestreamModel> productInLivestreamModelList = ProductInLivestreamModel.FromJson(response.body().toString());
                                holder.tvProductCount.setText(productInLivestreamModelList.size() + "");
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {

                    }
                });

    }

    public void setClickInterMinimize(ClickInterface clickInterMinimize) {
        this.clickInterMinimize = clickInterMinimize;
    }

    public interface ClickInterface {
        void onSelected();
    }
}