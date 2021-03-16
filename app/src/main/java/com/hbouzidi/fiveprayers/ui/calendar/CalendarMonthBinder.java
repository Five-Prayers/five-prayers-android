package com.hbouzidi.fiveprayers.ui.calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;


import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class CalendarMonthBinder implements MonthHeaderFooterBinder<MonthViewContainer> {

    @Override
    public void bind(@NonNull MonthViewContainer monthViewContainer, @NonNull CalendarMonth calendarMonth) {
        LinearLayout legendLayout = monthViewContainer.getLegendLayout();
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        if (legendLayout.getTag() == null) {
            legendLayout.setTag(calendarMonth.getYearMonth());
            for (int i = 0; i < legendLayout.getChildCount(); i++) {
                TextView child = (TextView) legendLayout.getChildAt(i);
                child.setText(firstDayOfWeek.plus(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            }
        }
    }

    @NonNull
    @Override
    public MonthViewContainer create(@NonNull View view) {
        return new MonthViewContainer(view);
    }
}
