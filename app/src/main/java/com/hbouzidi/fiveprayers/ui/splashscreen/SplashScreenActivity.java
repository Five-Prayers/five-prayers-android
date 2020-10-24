package com.hbouzidi.fiveprayers.ui.splashscreen;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private final static int LOADING_TIME = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = findViewById(R.id.splash_progressbar);
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100);
        objectAnimator.setDuration(LOADING_TIME);
        objectAnimator.start();

        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1200);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}