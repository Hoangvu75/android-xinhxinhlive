package com.sanghm2.xinhxinhchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sanghm2.xinhxinhchat.fragment.ContactFragment;
import com.sanghm2.xinhxinhchat.fragment.LiveFragment;
import com.sanghm2.xinhxinhchat.fragment.LoginFragment;
import com.sanghm2.xinhxinhchat.fragment.ViewFragment;
import com.sanghm2.xinhxinhchat.utils.TabLayoutHelper;
import com.sanghm2.xinhxinhchat.utils.ThemeHelper;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private int selected = 0;
    private LinearLayout tabLayout;
    private TabLayoutHelper tabLayoutHelper;
    private FrameLayout fl_container;
    private Fragment fragmentLive, fragmentView, fragmentContact, fragmentLogin;
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = fragmentLive;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThemeHelper.setColorSatusbar(getWindow(), R.color.watermelon);
        initView();
        initFragment();
        initTab();
        onAction();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private void initView() {
        fl_container = findViewById(R.id.fl_container);
        tabLayout = findViewById(R.id.ln_tab);
        tabLayoutHelper = new TabLayoutHelper(mContext);
    }

    private void initFragment() {
        fragmentLive = new LiveFragment();
        fragmentView = new ViewFragment();
        fragmentContact = new ContactFragment();
        fragmentLogin = new LoginFragment();
        fm.beginTransaction().add(R.id.fl_container, fragmentLogin, "4").hide(fragmentLogin).commit();
        fm.beginTransaction().add(R.id.fl_container, fragmentContact, "3").hide(fragmentContact).commit();
        fm.beginTransaction().add(R.id.fl_container,fragmentView, "2").hide(fragmentView).commit();
        fm.beginTransaction().add(R.id.fl_container, fragmentLive, "1").commit();
        active = fragmentLive;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.hide(active).show(fragment).commit();
        active = fragment;
    }

    private void initTab() {
        View viewtab = tabLayoutHelper.addTabLayout();
        tabLayout.addView(viewtab);
        openFragment(fragmentLive);
        tabLayoutHelper.setSelectedTabInterface(new TabLayoutHelper.SelectedTabInterface() {
            @Override
            public void onSelected(int tab) {
                selected = tab;
                Fragment fragment;
                switch (tab) {
                    case 1:
                        fragment = fragmentLive;
                        type = "";
                        loadData();
                        break;
                    case 2:
                        fragment = fragmentView;
                        type = "";
                        loadData();
                        break;
                    case 3:
                        fragment = fragmentContact;
                        type = "";
                        loadData();
                        break;
                    case 4:
                        fragment = fragmentLogin;
                        type = "";
                        loadData();
                        if (!Objects.equals(XinhXinhPref.getStringPreference(mContext, "TOKEN"), "") && XinhXinhPref.getStringPreference(mContext, "TOKEN") != null) {
                            fragment.onResume();
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + tab);
                }
                openFragment(fragment);
            }
        });
    }


    @SuppressLint("CheckResult")
    private void onAction() {

    }

    @Override
    public void onBackPressed() {
    }

    private void loadData() {
//        switch (type) {
//            case "":
//                ((AllFragment) fragmentAll).getConversations(selectPageID, type, 10, null, "pending");
//                break;
//            case "message":
//                ((MessageFragment) fragmentMessager).getConversations(selectPageID, type, 10, null, "pending");
//                break;
//            case "comment":
//                ((CommentFragment) fragmentComment).getConversations(selectPageID, type, 10, null, "pending");
//                break;
//        }
    }
}