package com.hbouzidi.fiveprayers.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.hbouzidi.fiveprayers.R;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AlertHelper {

    public static void displayAlertDialog(final Context context, String title, String message) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.mainStyles);
        int topColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundStartColor, ContextCompat.getColor(context, R.color.colorAccent));
        int topTitleColor = typedArray.getColor(R.styleable.mainStyles_textColorPrimary, ContextCompat.getColor(context, R.color.textColorPrimary));
        int iconTintColor = typedArray.getColor(R.styleable.mainStyles_iconsMainColor, ContextCompat.getColor(context, R.color.colorAccent));

        LovelyCustomDialog lovelyCustomDialog = new LovelyCustomDialog(context)
                .setView(R.layout.custom_dialog_view)
                .setTopColor(topColor)
                .setTitle(title)
                .setTopTitleColor(topTitleColor)
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setMessage(message)
                .setMessageGravity(Gravity.FILL)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIconTintColor(iconTintColor)
                .setCancelable(false);

        lovelyCustomDialog.setListener(R.id.btnOK, v -> lovelyCustomDialog.dismiss());
        lovelyCustomDialog.show();
    }

    public static LovelyCustomDialog createCustomInformationDialog(final Context context, String title, String message) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.mainStyles);
        int topColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundEndColor, ContextCompat.getColor(context, R.color.colorAccent));
        int topTitleColor = typedArray.getColor(R.styleable.mainStyles_textColorPrimary, ContextCompat.getColor(context, R.color.textColorPrimary));
        int iconTintColor = typedArray.getColor(R.styleable.mainStyles_iconsMainColor, ContextCompat.getColor(context, R.color.colorAccent));

        return new LovelyCustomDialog(context)
                .setView(R.layout.custom_dialog_view)
                .setTopColor(topColor)
                .setTitle(title)
                .setTopTitleColor(topTitleColor)
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setMessage(message)
                .setMessageGravity(Gravity.FILL)
                .setIcon(R.drawable.ic_information_24dp)
                .setIconTintColor(iconTintColor)
                .setCancelable(false);
    }

    public static void displayDialogError(final Context context, String message, View.OnClickListener onClickListener) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.mainStyles);
        int topColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundStartColor, ContextCompat.getColor(context, R.color.colorAccent));
        int iconTintColor = typedArray.getColor(R.styleable.mainStyles_iconsMainColor, ContextCompat.getColor(context, R.color.colorAccent));
        int topTitleColor = typedArray.getColor(R.styleable.mainStyles_textColorPrimary, ContextCompat.getColor(context, R.color.textColorPrimary));

        LovelyCustomDialog lovelyCustomDialog = new LovelyCustomDialog(context)
                .setView(R.layout.custom_dialog_view)
                .setTopColor(topColor)
                .setTitle(context.getString(R.string.common_alert))
                .setTopTitleColor(topTitleColor)
                .setTitleGravity(Gravity.CENTER_HORIZONTAL)
                .setMessage(message)
                .setMessageGravity(Gravity.FILL)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIconTintColor(iconTintColor)
                .setCancelable(false);

        if (onClickListener != null) {
            lovelyCustomDialog.setListener(R.id.btnOK, onClickListener);
        }

        lovelyCustomDialog.setListener(R.id.btnOK, v -> lovelyCustomDialog.dismiss());
        lovelyCustomDialog.show();
    }
}
