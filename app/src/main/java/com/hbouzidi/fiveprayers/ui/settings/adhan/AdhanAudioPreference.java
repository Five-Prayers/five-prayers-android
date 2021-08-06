package com.hbouzidi.fiveprayers.ui.settings.adhan;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.hbouzidi.fiveprayers.BuildConfig;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.utils.UiUtils;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AdhanAudioPreference extends ListPreference {

    private static final int CUSTOM_RINGTONE_REQUEST_CODE = 0x9000;
    private static final int WRITE_FILES_PERMISSION_REQUEST_CODE = 0x9001;

    private final Context mContext;
    private final CharSequence[] mExtraRingtones;
    private final CharSequence[] mExtraRingtoneTitles;

    private String mValue;

    public AdhanAudioPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExtraRingtonePreference, 0, 0);

        mExtraRingtones = a.getTextArray(R.styleable.ExtraRingtonePreference_extraRingtones);
        mExtraRingtoneTitles = a.getTextArray(R.styleable.ExtraRingtonePreference_extraRingtoneTitles);

        a.recycle();

        setDefaultValue(UiUtils.uriFromRaw(PreferencesConstants.SHORT_PRAYER_CALL, getContext()).toString());
    }

    public String getValue() {
        return mValue;
    }

    public String getCustomAdhanFolderName() {
        return BuildConfig.CUSTOM_ADHAN_SOUNDS_FOLDER_NAME;
    }

    @Override
    public CharSequence getSummary() {

        String ringtoneTitle = null;

        if (mValue != null) {
            if (ringtoneTitle == null && mExtraRingtones != null && mExtraRingtoneTitles != null) {

                for (int i = 0; i < mExtraRingtones.length; i++) {

                    Uri uriExtra = UiUtils.uriFromRaw(mExtraRingtones[i].toString(), getContext());

                    if (uriExtra.equals(Uri.parse(mValue))) {
                        ringtoneTitle = mExtraRingtoneTitles[i].toString();
                        break;
                    }
                }
            }

            if (ringtoneTitle == null) {
                Ringtone ringtone = RingtoneManager.getRingtone(mContext, Uri.parse(mValue));
                String title = ringtone.getTitle(mContext);
                if (title != null && title.length() > 0)
                    ringtoneTitle = title;
            }

        }

        if (ringtoneTitle != null) {
            return ringtoneTitle;
        }

        return "";
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValueObj) {
        String value = getPersistedString((String) defaultValueObj);
        setInternalRingtone(!TextUtils.isEmpty(value) ? value : null, true);
    }

    public void setValue(String value) {
        setInternalRingtone(value, false);
    }

    private void setInternalRingtone(String value, boolean force) {

        final boolean changed = (mValue != null && !mValue.equals(value)) || (value != null && !value.equals(mValue));

        if (changed || force) {
            final boolean wasBlocking = shouldDisableDependents();

            mValue = value;
            persistString(value != null ? value : "");

            final boolean isBlocking = shouldDisableDependents();

            notifyChanged();

            if (isBlocking != wasBlocking) {
                notifyDependencyChange(isBlocking);
            }
        }
    }

    public CharSequence[] getmExtraRingtones() {
        return mExtraRingtones;
    }

    public CharSequence[] getmExtraRingtoneTitles() {
        return mExtraRingtoneTitles;
    }

    public int getCustomRingtoneRequestCode() {
        return CUSTOM_RINGTONE_REQUEST_CODE;
    }

    public int getPermissionRequestCode() {
        return WRITE_FILES_PERMISSION_REQUEST_CODE;
    }
}
