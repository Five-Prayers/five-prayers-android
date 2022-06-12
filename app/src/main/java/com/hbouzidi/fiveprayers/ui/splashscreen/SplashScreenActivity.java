package com.hbouzidi.fiveprayers.ui.splashscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.splashscreen.SplashScreen;

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
    private static final int LOADING_TIME = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SplashScreen.installSplashScreen(this);
            super.onCreate(savedInstanceState);

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);

            Thread myThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(LOADING_TIME);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}