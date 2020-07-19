package com.bouzidi.prayertimes.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.job.PrayerUpdater;
import com.bouzidi.prayertimes.preferences.PreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);

        if (displaySettingsScreenFirst()) {
            navGraph.setStartDestination(R.id.navigation_notifications);
        } else {
            navGraph.setStartDestination(R.id.navigation_home);
        }

        navController.setGraph(navGraph);
        PreferencesHelper.setFirstTimeLaunch(false, this);

        PeriodicWorkRequest periodicWorkRequest =
                new PeriodicWorkRequest.Builder(PrayerUpdater.class, 60, TimeUnit.MINUTES, 50, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("Prayer updater", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    private boolean displaySettingsScreenFirst() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
                PreferencesHelper.isFirstLaunch(this);
    }
}
