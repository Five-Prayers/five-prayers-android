package com.hbouzidi.fiveprayers.ui.calendar;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.R;
import com.kizitonwose.calendarview.ui.ViewContainer;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class MonthViewContainer extends ViewContainer {

    private final LinearLayout legendLayout;

    public MonthViewContainer(@NonNull View view) {
        super(view);
        legendLayout = view.findViewById(R.id.legendLayout);
    }

    public LinearLayout getLegendLayout() {
        return legendLayout;
    }
}
