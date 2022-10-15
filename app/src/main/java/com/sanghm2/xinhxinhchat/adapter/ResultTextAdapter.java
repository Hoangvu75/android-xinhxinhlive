package com.sanghm2.xinhxinhchat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.model.ResultTextModel;

import java.util.List;

public class ResultTextAdapter extends RecyclerView.Adapter<ResultTextAdapter.ResultTextViewHolder>{

    private List<ResultTextModel> mResultTextModelList;
    private final Context mContext;
    public ClickInterface clickInterface ;

    public ResultTextAdapter(List<ResultTextModel> mResultTextModelList, Context mContext) {
        this.mResultTextModelList = mResultTextModelList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ResultTextAdapter.ResultTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResultTextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_results_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultTextAdapter.ResultTextViewHolder holder, int position) {
        ResultTextModel resultTextModel = mResultTextModelList.get(position);
        holder.tvResultText.setText(resultTextModel.getText());

        holder.tvResultText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInterface.onSelected(resultTextModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResultTextModelList.size();
    }

    static class ResultTextViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvResultText;

        public ResultTextViewHolder(@NonNull View itemView) {
            super(itemView);

            tvResultText = itemView.findViewById(R.id.tvResultText);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapter(List<ResultTextModel> list) {
        this.mResultTextModelList = list;
        notifyDataSetChanged();
    }

    public void setClickInterface(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }
    public interface ClickInterface {
        void onSelected(ResultTextModel resultTextModel);
    }
}
