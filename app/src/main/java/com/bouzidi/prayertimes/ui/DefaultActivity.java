package com.bouzidi.prayertimes.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bouzidi.prayertimes.preferences.PreferencesHelper;
import com.bouzidi.prayertimes.ui.appintro.IntroActivity;
import com.bouzidi.prayertimes.ui.splashscreen.SplashScreenActivity;

public class DefaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PreferencesHelper.isFirstLaunch(this)) {
            Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            startActivity(intent);
        }
    }
}