package com.hbouzidi.fiveprayers.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.job.PeriodicWorkCreator;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class MainActivity extends BaseActivity {

    @Inject
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((FivePrayerApplication) getApplicationContext())
                .defaultComponent
                .inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);

        if (displaySettingsScreenFirst()) {
            navGraph.setStartDestination(R.id.navigation_settings);
        } else {
            navGraph.setStartDestination(R.id.navigation_home);
        }

        navController.setGraph(navGraph);
        preferencesHelper.setFirstTimeLaunch(false);

        PeriodicWorkCreator.schedulePrayerUpdater(this);
    }

    @Override
    public void onBackPressed() {
        minimizeApp();
    }

    private boolean displaySettingsScreenFirst() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) &&
                preferencesHelper.isFirstLaunch();
    }

    private void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
