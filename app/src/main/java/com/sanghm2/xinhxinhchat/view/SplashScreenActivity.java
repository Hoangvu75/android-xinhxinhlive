package com.sanghm2.xinhxinhchat.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.gms.security.ProviderInstaller;
import com.sanghm2.xinhxinhchat.MainActivity;
import com.sanghm2.xinhxinhchat.R;
import com.sanghm2.xinhxinhchat.utils.Constants;
import com.sanghm2.xinhxinhchat.utils.ThemeHelper;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

public class SplashScreenActivity extends AppCompatActivity {
    Context context = this;
    String token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ThemeHelper.setFullScreen(getWindow());
        ThemeHelper.setColorSatusbar(getWindow(), R.color.watermelon);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);

        installIfNeeded();
    }

    private void installIfNeeded() {
        ProviderInstaller.installIfNeededAsync(context, new ProviderInstaller.ProviderInstallListener() {
            @Override
            public void onProviderInstallFailed(int i, @Nullable Intent intent) {
                Log.e("Sang", "onProviderInstallFailed");
            }

            @Override
            public void onProviderInstalled() {
                Log.e("Sang", "onProviderInstalled");
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}