package com.hbouzidi.fiveprayers.ui.qibla;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManagerFix;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.ui.BaseActivity;

import javax.inject.Inject;

import io.github.derysudrajat.compassqibla.CompassQibla;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class CompassActivity extends BaseActivity {

    private float currentCompassDegree = 0f;
    private float currentNeedleDegree = 0f;
    private ImageView ivCompass;
    private ImageView ivNeedle;
    private TextView tvDirection;
    private TextView tvLocation;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((FivePrayerApplication) getApplicationContext())
                .appComponent
                .qiblaComponent()
                .create()
                .inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compass);

        ivCompass = findViewById(R.id.ivCompass);
        ivNeedle = findViewById(R.id.ivNeedle);
        tvDirection = findViewById(R.id.tvDirection);
        tvLocation = findViewById(R.id.tvLocation);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        CompassQibla.Builder builder = new CompassQibla.Builder(this);
        builder
                .onPermissionDenied(() -> {
                    Toast
                            .makeText(getApplicationContext(), R.string.quibla_permission_denied, Toast.LENGTH_LONG)
                            .show();
                    return null;
                })
                .onGetLocationAddress(address -> {
                    if (address.hasLatitude() && address.hasLongitude()) {
                        SharedPreferences defaultSharedPreferences = PreferenceManagerFix.getDefaultSharedPreferences(this);
                        String strYourLocation = getResources().getString(R.string.your_location) + " " +
                                defaultSharedPreferences.getString(PreferencesConstants.LOCATION_PREFERENCE, "");

                        tvLocation.setText(strYourLocation);
                    }
                    return null;
                })
                .onDirectionChangeListener(qiblaDirection -> {
                    if (qiblaDirection.isFacingQibla()) {
                        tvDirection.setText(getString(R.string.facing_qibla));
                    } else {
                        tvDirection.setText((int) qiblaDirection.getNeedleAngle() + getString(R.string.degree));
                    }

                    RotateAnimation rotateCompass = new RotateAnimation(currentCompassDegree,
                            qiblaDirection.getCompassAngle(),
                            Animation.RELATIVE_TO_SELF,
                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                    );

                    rotateCompass.setDuration(1000);
                    currentCompassDegree = qiblaDirection.getCompassAngle();
                    ivCompass.startAnimation(rotateCompass);

                    RotateAnimation rotateNeedle = new RotateAnimation(currentNeedleDegree,
                            qiblaDirection.getNeedleAngle(),
                            Animation.RELATIVE_TO_SELF,
                            0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                    );
                    rotateNeedle.setDuration(1000);
                    currentNeedleDegree = qiblaDirection.getNeedleAngle();
                    ivNeedle.startAnimation(rotateNeedle);

                    return null;
                }).build();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
