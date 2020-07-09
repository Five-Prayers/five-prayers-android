package com.bouzidi.prayertimes.ui.appintro;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.bouzidi.prayertimes.preferences.PreferencesHelper;
import com.bouzidi.prayertimes.ui.splashscreen.SplashScreenActivity;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(
                "Welcome...",
                "This is the first slide of the example"
        ));

        addSlide(AppIntroFragment.newInstance(
                "...Let's get started!",
                "In order to calculation prayer timing," +
                        "this application need your permission to use your localisation capabilities"

        ));

        addSlide(AppIntroFragment.newInstance(
                "...Let's get started!",
                "The end !"

        ));

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        askForPermissions(
                permissions,
                2,
                true);


        AppIntroPageTransformerType.Parallax parallax = new AppIntroPageTransformerType.Parallax(
                1.0,
                -1.0,
                2.0);
        setTransformer(parallax);
        setImmersiveMode();

        setWizardMode(true);
    }

    @Override
    protected void onUserDeniedPermission(String permissionName) {
        // User pressed "Deny" on the permission dialog
    }

    @Override
    protected void onUserDisabledPermission(String permissionName) {
        // User pressed "Deny" + "Don't ask again" on the permission dialog
    }

    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        PreferencesHelper.setFirstTimeLaunch(false, this);

        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }
}