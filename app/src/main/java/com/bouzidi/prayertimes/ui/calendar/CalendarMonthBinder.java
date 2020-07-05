package com.bouzidi.prayertimes.ui.calendar;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kizitonwose.calendarview.model.CalendarMonth;
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;

public class CalendarMonthBinder implements MonthHeaderFooterBinder<MonthViewContainer> {

    @Override
    public void bind(@NotNull MonthViewContainer monthViewContainer, @NotNull CalendarMonth calendarMonth) {
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

    @NotNull
    @Override
    public MonthViewContainer create(@NotNull View view) {
        return new MonthViewContainer(view);
    }
}
