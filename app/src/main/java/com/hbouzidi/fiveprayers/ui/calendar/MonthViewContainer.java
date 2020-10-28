package com.hbouzidi.fiveprayers.ui.calendar;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.R;
import com.kizitonwose.calendarview.ui.ViewContainer;


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
