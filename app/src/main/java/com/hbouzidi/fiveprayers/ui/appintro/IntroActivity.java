package com.hbouzidi.fiveprayers.ui.appintro;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.ui.splashscreen.SplashScreenActivity;
import com.hbouzidi.fiveprayers.utils.AlertHelper;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.createInstance(
                getResources().getString(R.string.app_intro_frag_1_title),
                getResources().getString(R.string.app_intro_frag_1_description),
                R.drawable.ic_mosque_200dp,
                R.color.dodger_blue,
                R.color.white,
                R.color.white
        ));

        addSlide(AppIntroFragment.createInstance(
                getResources().getString(R.string.app_intro_frag_2_title),
                getResources().getString(R.string.app_intro_frag_2_description),
                R.drawable.ic_data_privacy_200dp,
                R.color.dodger_blue,
                R.color.white,
                R.color.white
        ));


        addSlide(AppIntroFragment.createInstance(
                getResources().getString(R.string.app_intro_frag_3_title),
                getResources().getString(R.string.app_intro_frag_3_description),
                R.drawable.ic_question_200dp,
                R.color.dodger_blue,
                R.color.white,
                R.color.white
        ));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addSlide(AppIntroFragment.createInstance(
                    getResources().getString(R.string.app_intro_frag_3_title),
                    getResources().getString(R.string.app_intro_frag_3_5_description),
                    R.drawable.ic_question_200dp,
                    R.color.dodger_blue,
                    R.color.white,
                    R.color.white
            ));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            addSlide(AppIntroFragment.createInstance(
                    getResources().getString(R.string.app_intro_frag_4_title),
                    getResources().getString(R.string.app_intro_frag_4_description),
                    R.drawable.ic_question_200dp,
                    R.color.dodger_blue,
                    R.color.white,
                    R.color.white
            ));
        }

        addSlide(AppIntroFragment.createInstance(
                getResources().getString(R.string.app_intro_frag_5_title),
                getResources().getString(R.string.app_intro_frag_5_description),
                R.drawable.ic_hassan_mosque_20dp,
                R.color.dodger_blue,
                R.color.white,
                R.color.white
        ));

        askForPermissions(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                3,
                false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            askForPermissions(
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    4,
                    false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            askForPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    5,
                    false);
        }

        AppIntroPageTransformerType.Parallax parallax = new AppIntroPageTransformerType.Parallax(
                1.0,
                -1.0,
                2.0);
        setTransformer(parallax);

        setSystemBackButtonLocked(true);
        setWizardMode(true);
    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
