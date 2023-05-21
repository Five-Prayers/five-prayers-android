package com.hbouzidi.fiveprayers.ui.quran.index;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import com.hbouzidi.fiveprayers.R;

import java.util.Calendar;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public TimePickerFragment() {
    }

    public TimePickerFragment(TimePickerDialog.OnTimeSetListener callback) {
        onTimeSetListener = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(requireContext(), R.style.CustomPickerDialogMaterialStyle,
                onTimeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

}
