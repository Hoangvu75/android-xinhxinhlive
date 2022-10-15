package com.sanghm2.xinhxinhchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sanghm2.xinhxinhchat.R;

public class DialogForgotPassword extends Dialog  {
    private final Context context;

    private TextView tvBackButton;

    public DialogForgotPassword(@NonNull Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.context = context;
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_forgot_password);

        onInitView();
        onSetVariables();
    }

    private void onInitView() {
        tvBackButton = findViewById(R.id.tvBackButton);

    }

    private void onSetVariables() {
        tvBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
