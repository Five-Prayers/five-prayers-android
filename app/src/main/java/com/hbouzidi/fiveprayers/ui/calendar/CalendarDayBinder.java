package com.hbouzidi.fiveprayers.ui.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.HijriHoliday;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDateType;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.ui.DayBinder;

import java.time.LocalDate;
import java.util.List;


public class CalendarDayBinder implements DayBinder<DayViewContainer> {

    @Override
    public void bind(@NonNull DayViewContainer dayViewContainer, @NonNull CalendarDay calendarDay) {
        dayViewContainer.setCalendarDay(calendarDay);
        TextView dayTextView = dayViewContainer.getDayTextView();
        TextView hijriDayTextView = dayViewContainer.getHijriDayTextView();
        ImageView hijriCalendarDateMonthTextView = dayViewContainer.getHijriCalendarDateMonthTextView();
        ImageView hijriHolidayDateMonthTextView = dayViewContainer.getHijriHolidayDate();
        hijriCalendarDateMonthTextView.setVisibility(View.INVISIBLE);
        hijriHolidayDateMonthTextView.setVisibility(View.INVISIBLE);
        View calendarDayLayout = dayViewContainer.getCalendarDayLayout();

        LocalDate selectedDate = CalendarActivity.getSelectedDate();
        List<AladhanDate> aladhanDates = CalendarActivity.getHijriDates();

        LocalDate today = LocalDate.now();
        if (calendarDay.getOwner() == DayOwner.THIS_MONTH) {
            if (calendarDay.getDate().equals(today)) {
                updateHijriDate(calendarDay, hijriDayTextView, hijriCalendarDateMonthTextView, hijriHolidayDateMonthTextView, aladhanDates);
                calendarDayLayout.setBackgroundResource(R.drawable.calendar_today_bg);
                dayTextView.setVisibility(View.VISIBLE);
            } else if (calendarDay.getDate().equals(selectedDate)) {
                updateHijriDate(calendarDay, hijriDayTextView, hijriCalendarDateMonthTextView, hijriHolidayDateMonthTextView, aladhanDates);
                calendarDayLayout.setBackgroundResource(R.drawable.calendar_selected_bg);
                dayTextView.setVisibility(View.VISIBLE);
            } else {
                updateHijriDate(calendarDay, hijriDayTextView, hijriCalendarDateMonthTextView, hijriHolidayDateMonthTextView, aladhanDates);
                calendarDayLayout.setBackground(null);
                dayTextView.setVisibility(View.VISIBLE);
            }
        } else {
            dayTextView.setVisibility(View.INVISIBLE);
            hijriDayTextView.setVisibility(View.INVISIBLE);
            hijriHolidayDateMonthTextView.setVisibility(View.INVISIBLE);
            calendarDayLayout.setBackground(null);
        }
        dayTextView.setText(String.valueOf(calendarDay.getDay()));
    }

    private void updateHijriDate(@NonNull CalendarDay calendarDay, TextView hijriDayTextView, ImageView hijriCalendarDateMonthTextView, ImageView hijriHolidayDateMonthTextView, List<AladhanDate> aladhanDates) {
        if (aladhanDates != null) {
            String gregorianMonth = aladhanDates.get(0).getGregorian().getMonth().getNumber();

            if (Integer.valueOf(gregorianMonth).equals(calendarDay.getDate().getMonthValue())) {
                AladhanDateType hijriDate = aladhanDates.get(calendarDay.getDay() - 1).getHijri();
                String hijriDayNumber = hijriDate.getDay();
                hijriDayTextView.setText(hijriDayNumber);
                hijriDayTextView.setVisibility(View.VISIBLE);

                if (hijriDayNumber.equals("01")) {
                    hijriCalendarDateMonthTextView.setVisibility(View.VISIBLE);
                }

                HijriHoliday holiday = HijriHoliday.getHoliday(Integer.parseInt(hijriDate.getDay()), Integer.parseInt(hijriDate.getMonth().getNumber()));

                if (holiday != null) {
                    hijriHolidayDateMonthTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @NonNull
    @Override
    public DayViewContainer create(@NonNull View view) {
        return new DayViewContainer(view);
    }
}
