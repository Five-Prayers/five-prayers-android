package com.hbouzidi.fiveprayers.ui.splashscreen;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.BaseActivity;
import com.hbouzidi.fiveprayers.ui.MainActivity;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class SplashScreenActivity extends BaseActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int LOADING_TIME = 1200;

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
                    Log.e(TAG, "Cannot start MainActivity", e);
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