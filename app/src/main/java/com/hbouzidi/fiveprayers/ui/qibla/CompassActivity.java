package com.hbouzidi.fiveprayers.ui.qibla;

import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;

import android.content.SharedPreferences;
import android.location.Location;
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
import com.hbouzidi.compassqibla.CompassQibla;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.ui.BaseActivity;
import com.hbouzidi.fiveprayers.utils.AlertHelper;
import com.hbouzidi.fiveprayers.utils.LocationUtils;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import javax.inject.Inject;

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

    private boolean isDialogDismissed = false;
    @Inject
    PreferencesHelper preferencesHelper;
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

        if (LocationUtils.isLocationPermissionMissing(getApplicationContext())) {
            Toast
                    .makeText(getApplicationContext(), R.string.qibla_permission_denied, Toast.LENGTH_LONG)
                    .show();
        } else if (LocationUtils.isLocationServicesDisabled(getApplicationContext())) {
            Toast
                    .makeText(getApplicationContext(), R.string.qibla_location_service_not_enabled, Toast.LENGTH_LONG)
                    .show();
        } else if (LocationUtils.isAirplaneModeOn(getApplicationContext())) {
            Toast
                    .makeText(getApplicationContext(), R.string.qibla_airplane_mode_on, Toast.LENGTH_LONG)
                    .show();
        }

        QiblaViewModel qiblaViewModel = viewModelFactory.create(QiblaViewModel.class);
        qiblaViewModel.getLocation().observe(this, this::buildCompassViews);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());
    }

    private void buildCompassViews(Location location) {
        CompassQibla.Builder builder = new CompassQibla.Builder(this);
        builder
                .setCurrentLocation(location)
                .onGetAccuracyChanged((sensor, accuracy) -> {
                    if (accuracy <= SENSOR_STATUS_ACCURACY_LOW && !isDialogDismissed) {
                        createCalibratingDialog().show();
                    }
                    return null;
                })
                .onDirectionChangeListener(qiblaDirection -> {
                    if (qiblaDirection.isFacingQibla()) {
                        tvDirection.setText(getString(R.string.facing_qibla));
                    } else {
                        String angleIndicator = (int) qiblaDirection.getNeedleAngle() + getString(R.string.degree);
                        tvDirection.setText(angleIndicator);
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
                })
                .build();

        SharedPreferences defaultSharedPreferences = PreferenceManagerFix.getDefaultSharedPreferences(this);
        String strYourLocation = getResources().getString(R.string.your_location) + " " +
                defaultSharedPreferences.getString(PreferencesConstants.LOCATION_PREFERENCE, "");

        tvLocation.setText(strYourLocation);
    }

    private LovelyCustomDialog createCalibratingDialog() {
        LovelyCustomDialog customInformationDialog = AlertHelper.createCustomInformationDialog(this,
                getString(com.hbouzidi.fiveprayers.R.string.dialog_title_sensor_not_calibrate),
                getString(com.hbouzidi.fiveprayers.R.string.dialog_message_sensor_not_calibrate)
        );

        customInformationDialog.setListener(com.hbouzidi.fiveprayers.R.id.btnOK, v -> {
            isDialogDismissed = true;
            customInformationDialog.dismiss();
        });

        return customInformationDialog;
    }
}
