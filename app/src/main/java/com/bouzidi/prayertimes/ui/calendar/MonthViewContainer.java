package com.bouzidi.prayertimes.ui.calendar;

import android.view.View;
import android.widget.LinearLayout;

import com.bouzidi.prayertimes.R;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.jetbrains.annotations.NotNull;

public class MonthViewContainer extends ViewContainer {

    private final LinearLayout legendLayout;

    public MonthViewContainer(@NotNull View view) {
        super(view);
        legendLayout = view.findViewById(R.id.legendLayout);
    }

    public LinearLayout getLegendLayout() {
        return legendLayout;
    }
}
