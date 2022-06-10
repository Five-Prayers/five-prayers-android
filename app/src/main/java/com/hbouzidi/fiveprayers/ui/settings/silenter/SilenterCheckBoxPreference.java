package com.hbouzidi.fiveprayers.ui.settings.silenter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;

import com.hbouzidi.fiveprayers.R;

public class SilenterCheckBoxPreference extends CheckBoxPreference {

    private final Context mContext;

    public SilenterCheckBoxPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        updateSummary();
    }

    public SilenterCheckBoxPreference(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        updateSummary();
    }

    public SilenterCheckBoxPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        updateSummary();
    }

    public SilenterCheckBoxPreference(@NonNull Context context) {
        super(context);

        mContext = context;
        updateSummary();
    }

    @Override
    protected void onClick() {
        super.onClick();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !isNotificationPolicyAccessGranted(mContext)) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            mContext.startActivity(intent);

            setChecked(false);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(isNotificationPolicyAccessGranted(mContext) && checked);
    }

    private void updateSummary() {
        if (!isNotificationPolicyAccessGranted(mContext)) {
            setSummary(mContext.getString(R.string.error_summary_silenter_preference_section));
        }
    }

    private boolean isNotificationPolicyAccessGranted(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return notificationManager.isNotificationPolicyAccessGranted();
        } else {
            return true;
        }
    }
}
