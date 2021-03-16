package com.hbouzidi.fiveprayers.ui.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.R;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.ViewContainer;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class DayViewContainer extends ViewContainer {

    private TextView hijriDayTextView;
    private TextView dayTextView;
    private ImageView hijriCalendarDateMonthTextView;
    private ImageView hijriHolidayDate;
    private CalendarDay calendarDay;
    private final View calendarDayLayout;

    public DayViewContainer(@NonNull View view) {
        super(view);

        calendarDayLayout = view.findViewById(R.id.calendarDayLayout);
        dayTextView = view.findViewById(R.id.calendarDayText);
        hijriDayTextView = view.findViewById(R.id.hijri_calendar_date_text_view);
        hijriCalendarDateMonthTextView = view.findViewById(R.id.hijri_calendar_date_month_text_view);
        hijriHolidayDate = view.findViewById(R.id.hijri_calendar_holiday_date);

        view.setOnClickListener(v -> {
            if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
                EventBus.getDefault().post(new CalendarActivity.MessageEvent(calendarDay.getDate()));
            }
        });
    }

    public TextView getDayTextView() {
        return dayTextView;
    }

    public void setCalendarDay(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    public TextView getHijriDayTextView() {
        return hijriDayTextView;
    }

    public View getCalendarDayLayout() {
        return calendarDayLayout;
    }

    public ImageView getHijriCalendarDateMonthTextView() {
        return hijriCalendarDateMonthTextView;
    }

    public ImageView getHijriHolidayDate() {
        return hijriHolidayDate;
    }
}
