package com.sanghm2.xinhxinhchat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.sanghm2.xinhxinhchat.R;

import java.util.concurrent.TimeUnit;

public class TabLayoutHelper {
    private Context context;
    private LinearLayout tabLayout, tab1, tab2, tab3, tab4;
    private SelectedTabInterface selectedTabInterface;
    private TextView tv_title1, tv_title2, tv_title3, tv_title4;
    private ImageView iv_icon1, iv_icon2, iv_icon3, iv_icon4;

    public void setSelectedTabInterface(SelectedTabInterface selectedTabInterface) {
        this.selectedTabInterface = selectedTabInterface;
    }

    public TabLayoutHelper(Context context) {
        this.context = context;
    }

    public View addTabLayout() {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.tab_layout, null);
        tabLayout = view.findViewById(R.id.ln_tab);
        tab1 = view.findViewById(R.id.tab1);
        tab2 = view.findViewById(R.id.tab2);
        tab3 = view.findViewById(R.id.tab3);
        tab4 = view.findViewById(R.id.tab4);
        //1
        tv_title1 = (TextView) view.findViewById(R.id.tv_title1);
        iv_icon1 = view.findViewById(R.id.iv_icon1);
        //2
        tv_title2 = (TextView) view.findViewById(R.id.tv_title2);
        iv_icon2 = view.findViewById(R.id.iv_icon2);
        //3
        tv_title3 = (TextView) view.findViewById(R.id.tv_title3);
        iv_icon3 = view.findViewById(R.id.iv_icon3);
        // 4
        tv_title4 = (TextView) view.findViewById(R.id.tv_title4);
        iv_icon4 = view.findViewById(R.id.iv_icon4);
        //
        selectedTab(1);
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab(1);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab(2);
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab(3);
            }
        });
        tab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab(4);
            }
        });
        setWeight(view);
        return view;
    }

    public void setWeight(View view) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f
        );
        view.setLayoutParams(param);
    }


    public interface SelectedTabInterface {
        void onSelected(int tab);
    }

    @SuppressLint("ResourceAsColor")
    private void selectedTab(int tab) {
        if (selectedTabInterface != null) {
            selectedTabInterface.onSelected(tab);
        }
        switch (tab) {
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv_title1.setTextColor(context.getColor(R.color.watermelon));
                    iv_icon1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.watermelon));
                    tv_title2.setTextColor(context.getColor(R.color.white));
                    iv_icon2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title3.setTextColor(context.getColor(R.color.white));
                    iv_icon3.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title4.setTextColor(context.getColor(R.color.white));
                    iv_icon4.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                }
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv_title1.setTextColor(context.getColor(R.color.white));
                    iv_icon1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title2.setTextColor(context.getColor(R.color.watermelon));
                    iv_icon2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.watermelon));
                    tv_title3.setTextColor(context.getColor(R.color.white));
                    iv_icon3.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title4.setTextColor(context.getColor(R.color.white));
                    iv_icon4.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                }
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv_title1.setTextColor(context.getColor(R.color.white));
                    iv_icon1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title2.setTextColor(context.getColor(R.color.white));
                    iv_icon2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title3.setTextColor(context.getColor(R.color.watermelon));
                    iv_icon3.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.watermelon));
                    tv_title4.setTextColor(context.getColor(R.color.white));
                    iv_icon4.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                }
                break;
            case 4:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv_title1.setTextColor(context.getColor(R.color.white));
                    iv_icon1.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title2.setTextColor(context.getColor(R.color.white));
                    iv_icon2.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title3.setTextColor(context.getColor(R.color.white));
                    iv_icon3.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
                    tv_title4.setTextColor(context.getColor(R.color.watermelon));
                    iv_icon4.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.watermelon));
                }
                break;
        }
    }
}
