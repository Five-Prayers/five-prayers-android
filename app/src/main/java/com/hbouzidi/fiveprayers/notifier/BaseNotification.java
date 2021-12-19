package com.hbouzidi.fiveprayers.notifier;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class BaseNotification {

    protected final Context context;
    protected final PreferencesHelper preferencesHelper;

    public BaseNotification(PreferencesHelper preferencesHelper, Context context) {
        this.preferencesHelper = preferencesHelper;
        this.context = context;
    }

    @DrawableRes
    protected int getActionIcon() {
        return R.drawable.ic_notifications_on_24dp_blue;
    }

    @DrawableRes
    protected int getNotificationIcon() {
        return R.drawable.ic_mosque_24dp_blue;
    }

    @ColorInt
    protected int getNotificationColor() {
        switch (preferencesHelper.getThemePreference()) {
            case PreferencesConstants.THEME_PREFERENCE_NAME_THEME_DARK_ORANGE:
                return context.getResources().getColor(R.color.orange);
            case PreferencesConstants.THEME_PREFERENCE_NAME_THEME_DARK_TURQUOISE:
                return context.getResources().getColor(R.color.turquoise_blue);
            case PreferencesConstants.THEME_PREFERENCE_NAME_THEME_WHITE_BLUE:
            default:
                return context.getResources().getColor(R.color.dodger_blue);
        }
    }
}
