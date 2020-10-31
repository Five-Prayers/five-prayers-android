package com.hbouzidi.fiveprayers.ui;

import android.content.Context;
import android.view.View;

import com.hbouzidi.fiveprayers.R;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

public class AlertHelper {

    public static void displayAlertDialog(final Context context, String title, String message) {
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.turbo)
                .setButtonsColorRes(R.color.amaranth)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.common_ok, v -> {
                })
                .show();
    }

    public static void displayInformationDialog(final Context context, String title, String message) {
        new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.colorPrimary)
                .setButtonsColorRes(R.color.amaranth)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.common_ok, v -> {
                })
                .show();
    }

    public static void displayDialogError(final Context context, String message, View.OnClickListener onNegativeButtonClickListener) {
        LovelyStandardDialog errorDialog = new LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.scarlet)
                .setButtonsColorRes(R.color.amaranth)
                .setTitle(context.getString(R.string.common_alert))
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message);

        if(onNegativeButtonClickListener != null) {
            errorDialog
                    .setNegativeButton(R.string.common_ok, onNegativeButtonClickListener);
        }
        errorDialog.show();
    }
}
