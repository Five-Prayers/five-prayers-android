package com.hbouzidi.fiveprayers.ui.settings.adhan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hbouzidi.fiveprayers.R;
import com.travijuu.numberpicker.library.NumberPicker;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class AdhanReminderView extends ConstraintLayout {

    private final NumberPicker numberPicker;

    public AdhanReminderView(Context context) {
        this(context, null);
    }

    public AdhanReminderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.adhan_reminder_number_pref, this);

        numberPicker = findViewById(R.id.AdhanReminderNumberPicker);
    }

    public void setNumberPickerValue(int adjustment) {
        numberPicker.setValue(adjustment);
    }

    public int getNumberPickerValue() {
        return numberPicker.getValue();
    }
}
