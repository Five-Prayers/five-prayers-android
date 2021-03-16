package com.hbouzidi.fiveprayers.ui.appintro;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.AlertHelper;
import com.hbouzidi.fiveprayers.ui.splashscreen.SplashScreenActivity;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.app_intro_frag_1_title),
                getResources().getString(R.string.app_intro_frag_1_description),
                R.drawable.ic_mosque_200dp,
                0xFF17C5FF,
                Color.WHITE,
                Color.WHITE
        ));

        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.app_intro_frag_2_title),
                getResources().getString(R.string.app_intro_frag_2_description),
                R.drawable.ic_data_privacy_200dp,
                0xFF17C5FF,
                Color.WHITE,
                Color.WHITE
        ));


        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.app_intro_frag_3_title),
                getResources().getString(R.string.app_intro_frag_3_description),
                R.drawable.ic_question_200dp,
                0xFF17C5FF,
                Color.WHITE,
                Color.WHITE
        ));

        addSlide(AppIntroFragment.newInstance(
                getResources().getString(R.string.app_intro_frag_4_title),
                getResources().getString(R.string.app_intro_frag_4_description),
                R.drawable.ic_hassan_mosque_20dp,
                0xFF17C5FF,
                Color.WHITE,
                Color.WHITE
        ));

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        askForPermissions(
                permissions,
                3,
                false);

        AppIntroPageTransformerType.Parallax parallax = new AppIntroPageTransformerType.Parallax(
                1.0,
                -1.0,
                2.0);
        setTransformer(parallax);

        setSystemBackButtonLocked(true);
        setWizardMode(true);
    }

    @Override
    protected void onUserDeniedPermission(@NonNull String permissionName) {
        AlertHelper.displayInformationDialog(this,
                getResources().getString(R.string.app_intro_permission_denied_dialog_title),
                getResources().getString(R.string.app_intro_permission_denied_dialog_message));
    }

    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }
}