package com.bouzidi.prayertimes.ui.splashscreen;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bouzidi.prayertimes.MainActivity;
import com.bouzidi.prayertimes.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.splash_progressbar);
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100);
        objectAnimator.setDuration(2000);
        objectAnimator.start();

        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
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
}