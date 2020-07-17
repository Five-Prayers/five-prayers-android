package com.bouzidi.prayertimes.ui;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class AlertHelper {

    public static void displayAlertDialog(final Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(com.bouzidi.prayertimes.R.string.common_ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void displayInformationDialog(final Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(com.bouzidi.prayertimes.R.string.common_ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}
