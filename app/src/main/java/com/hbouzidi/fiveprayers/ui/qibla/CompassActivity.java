package com.hbouzidi.fiveprayers.ui.qibla;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManagerFix;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;

import java.util.Locale;

import javax.inject.Inject;

import static android.view.View.INVISIBLE;

/**
 * @author Hassaan Jamil
 * @link https://github.com/hassaanjamil/hj-android-lib-qibla-direction
 * Updated By Hicham Bouzidi Idrissi
 */
public class CompassActivity extends AppCompatActivity {
    private static final String TAG = CompassActivity.class.getSimpleName();

    private Compass compass;
    private ImageView qiblatIndicator;
    private ImageView imageDial;
    private TextView tvAngle;
    private TextView tvYourLocation;

    private float currentAzimuth;
    SharedPreferences prefs;
    private Location location;

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

        QiblaViewModel qiblaViewModel = viewModelFactory.create(QiblaViewModel.class);

        prefs = getSharedPreferences("", MODE_PRIVATE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        qiblatIndicator = findViewById(R.id.qibla_indicator);
        imageDial = findViewById(R.id.dial);
        tvAngle = findViewById(R.id.angle);
        tvYourLocation = findViewById(R.id.your_location);

        qiblatIndicator.setVisibility(INVISIBLE);
        qiblatIndicator.setVisibility(View.GONE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        qiblaViewModel.getLocation().observe(this, location -> {
            this.location = location;
            setUpViews();
            setupCompass();
            compass.start(this);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "start compass");
        if (compass != null) {
            compass.start(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (compass != null) {
            compass.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (compass != null) {
            compass.start(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        if (compass != null) {
            compass.stop();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void setUpViews() {
        // Footer Image
        findViewById(R.id.footer_image).setVisibility(View.VISIBLE);

        // Your Location TextView
        findViewById(R.id.your_location).setVisibility(View.VISIBLE);
    }

    private void setupCompass() {
        fetch_GPS();
        getBearing();

        compass = new Compass(this);
        Compass.CompassListener compassListener = azimuth -> {
            adjustGambarDial(azimuth);
            adjustArrowQiblat(azimuth);
        };
        compass.setListener(compassListener);
    }

    public void adjustGambarDial(float azimuth) {
        Animation an = new RotateAnimation(-currentAzimuth, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        imageDial.startAnimation(an);
    }

    public void adjustArrowQiblat(float azimuth) {
        float qiblaDegree = GetFloat("qibla_degree");
        Animation an = new RotateAnimation(-(currentAzimuth) + qiblaDegree, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        currentAzimuth = (azimuth);
        an.setDuration(500);
        an.setRepeatCount(0);
        an.setFillAfter(true);
        qiblatIndicator.startAnimation(an);
        if (qiblaDegree > 0) {
            qiblatIndicator.setVisibility(View.VISIBLE);
        } else {
            qiblatIndicator.setVisibility(INVISIBLE);
            qiblatIndicator.setVisibility(View.GONE);
        }
    }

    public void getBearing() {
        float kaabaDegs = GetFloat("qibla_degree");
        if (kaabaDegs > 0.0001) {
            String strYourLocation;

            if (location != null) {
                SharedPreferences defaultSharedPreferences = PreferenceManagerFix.getDefaultSharedPreferences(this);
                strYourLocation = getResources().getString(R.string.your_location) + " " +
                        defaultSharedPreferences.getString(PreferencesConstants.LOCATION_PREFERENCE, "");
            } else
                strYourLocation = getResources().getString(R.string.unable_to_get_your_location);
            tvYourLocation.setText(strYourLocation);
            String strKaabaDirection = String.format(Locale.ENGLISH, "%.0f", kaabaDegs)
                    + " " + getResources().getString(R.string.degree) + " " + getDirectionString(kaabaDegs);
            tvAngle.setText(strKaabaDirection);
            qiblatIndicator.setVisibility(View.VISIBLE);
        } else {
            fetch_GPS();
        }
    }

    private String getDirectionString(float azimuthDegrees) {
        String where = "NW";

        if (azimuthDegrees >= 350 || azimuthDegrees <= 10)
            where = "N";
        if (azimuthDegrees < 350 && azimuthDegrees > 280)
            where = "NW";
        if (azimuthDegrees <= 280 && azimuthDegrees > 260)
            where = "W";
        if (azimuthDegrees <= 260 && azimuthDegrees > 190)
            where = "SW";
        if (azimuthDegrees <= 190 && azimuthDegrees > 170)
            where = "S";
        if (azimuthDegrees <= 170 && azimuthDegrees > 100)
            where = "SE";
        if (azimuthDegrees <= 100 && azimuthDegrees > 80)
            where = "E";
        if (azimuthDegrees <= 80 && azimuthDegrees > 10)
            where = "NE";

        return where;
    }

    public void SaveFloat(String Judul, Float bbb) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putFloat(Judul, bbb);
        edit.apply();
    }

    public Float GetFloat(String Judul) {
        return prefs.getFloat(Judul, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void fetch_GPS() {
        double result;

        if (location != null) {
            double myLat = location.getLatitude();
            double myLng = location.getLongitude();

            String strYourLocation = getResources().getString(R.string.your_location)
                    + " " + myLat + ", " + myLng;
            tvYourLocation.setText(strYourLocation);

            Log.e("TAG", "GPS is on");

            if (myLat < 0.001 && myLng < 0.001) {
                qiblatIndicator.setVisibility(INVISIBLE);
                qiblatIndicator.setVisibility(View.GONE);
                tvAngle.setText(getResources().getString(R.string.location_not_ready));
                tvYourLocation.setText(getResources().getString(R.string.location_not_ready));
            } else {
                double kaabaLng = 39.826206; // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double kaabaLat = Math.toRadians(21.422487); // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                double myLatRad = Math.toRadians(myLat);
                double longDiff = Math.toRadians(kaabaLng - myLng);
                double y = Math.sin(longDiff) * Math.cos(kaabaLat);
                double x = Math.cos(myLatRad) * Math.sin(kaabaLat) - Math.sin(myLatRad) * Math.cos(kaabaLat) * Math.cos(longDiff);
                result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;

                SaveFloat("qibla_degree", (float) result);
                String strKaabaDirection = String.format(Locale.ENGLISH, "%.0f", (float) result)
                        + " " + getResources().getString(R.string.degree) + " " + getDirectionString((float) result);
                tvAngle.setText(strKaabaDirection);
                qiblatIndicator.setVisibility(View.VISIBLE);
            }
        } else {
            qiblatIndicator.setVisibility(INVISIBLE);
            qiblatIndicator.setVisibility(View.GONE);
            tvAngle.setText(getResources().getString(R.string.pls_enable_location));
            tvYourLocation.setText(getResources().getString(R.string.pls_enable_location));
        }
    }
}
