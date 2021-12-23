package com.hbouzidi.fiveprayers.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import androidx.core.os.ConfigurationCompat;

import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class LocaleHelper {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public LocaleHelper(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public Locale getLocale() {
        if (preferencesHelper.useArabicLocale()) {
            if (preferencesHelper.getArabicNumeralsType().equals(PreferencesConstants.ARABIC_NUMERALS_TYPE_ARABIC_INDIC)) {
                return new Locale("ar");
            }
            return new Locale("ar", "MA");
        }
        return ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0);
    }

    public void refreshLocale(Context context, Activity activity) {
        Locale locale = getLocale();
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();

        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            config.setLayoutDirection(locale);

            if (activity != null) {
                if (locale.getLanguage().equals("ar")) {
                    activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                } else {
                    activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
                }
            }
        } else {
            config.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config);
        }

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
