package com.hbouzidi.fiveprayers.ui;

import android.content.Context;
import android.view.View;

import com.hbouzidi.fiveprayers.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AlertHelper {

    public static void displayAlertDialog(final Context context, String title, String message) {
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setCancelable(false)
                .setTitle(title)
                .setTopColorRes(R.color.turbo)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIconTintColor(R.color.black)
                .setButtonsColorRes(R.color.amaranth)
                .setMessage(message)
                .setNegativeButton(R.string.common_ok, v -> {
                })
                .show();
    }

    public static void displayInformationDialog(final Context context, String title, String message) {
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setCancelable(false)
                .setTitle(title)
                .setTopColorRes(R.color.colorPrimary)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setButtonsColorRes(R.color.amaranth)
                .setMessage(message)
                .setNegativeButton(R.string.common_ok, v -> {
                })
                .show();
    }

    public static void displayDialogError(final Context context, String message, View.OnClickListener onNegativeButtonClickListener) {
        LovelyStandardDialog errorDialog = new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setCancelable(false)
                .setTitle(context.getString(R.string.common_alert))
                .setTopColorRes(R.color.scarlet)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setIconTintColor(R.color.black)
                .setButtonsColorRes(R.color.amaranth)
                .setMessage(message);

        if(onNegativeButtonClickListener != null) {
            errorDialog
                    .setNegativeButton(R.string.common_ok, onNegativeButtonClickListener);
        }
        errorDialog.show();
    }
}
