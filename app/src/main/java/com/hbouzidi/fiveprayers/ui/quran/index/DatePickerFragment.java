package com.hbouzidi.fiveprayers.ui.quran.index;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.hbouzidi.fiveprayers.R;

import java.util.Calendar;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public DatePickerFragment() {
    }

    public DatePickerFragment(DatePickerDialog.OnDateSetListener callback) {
        mDateSetListener = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), R.style.CustomPickerDialogMaterialStyle,
                mDateSetListener, year, month, day);
    }
}
