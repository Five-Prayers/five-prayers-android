package com.bouzidi.prayertimes.ui.calendar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.timings.HijriHoliday;
import com.bouzidi.prayertimes.timings.PrayerHelper;
import com.bouzidi.prayertimes.timings.aladhan.AladhanDate;
import com.bouzidi.prayertimes.timings.aladhan.AladhanDateType;
import com.bouzidi.prayertimes.utils.UiUtils;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarMonth;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

public class CalendarActivity extends AppCompatActivity {

    private static final int MIN_MONTH_COUNT = 6;
    private static final int MAX_MONTH_COUNT = 12;

    private CalendarView calendarView;
    private CompositeDisposable compositeDisposable;
    private Toolbar toolbar;
    private TextView selectedDateTextView;
    private RecyclerView holidayRecyclerView;

    private static LocalDate selectedDate;
    private static List<AladhanDate> hijriDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        selectedDateTextView = findViewById(R.id.selected_date_text_view);
        compositeDisposable = new CompositeDisposable();

        initHolidayRecyclerView();

        initToolbar();

        initCalendarView();

        calendarView.setMonthScrollListener(calendarMonth -> {
            selectedDateTextView.setVisibility(View.INVISIBLE);
            holidayRecyclerView.setVisibility(View.INVISIBLE);

            compositeDisposable.add(
                    PrayerHelper.getHijriCalendar(calendarMonth.getMonth(), calendarMonth.getYear(), this)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<AladhanDate>>() {
                                @Override
                                public void onSuccess(List<AladhanDate> dates) {
                                    hijriDates = dates;

                                    updateToolbarSubtitle();

                                    LocalDate firstDateOfCurrentMonth = calendarMonth.getYearMonth().atDay(1);
                                    selectDate(firstDateOfCurrentMonth);

                                    calendarView.notifyCalendarChanged();
                                }

                                @Override
                                public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                }
                            }));


            updateToolbarTitle(calendarMonth);
            return Unit.INSTANCE;
        });
    }

    public static LocalDate getSelectedDate() {
        return selectedDate;
    }

    public static List<AladhanDate> getHijriDates() {
        return hijriDates;
    }

    public static class MessageEvent {
        LocalDate date;

        public MessageEvent(LocalDate date) {
            this.date = date;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        selectDate(event.date);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        compositeDisposable.dispose();
    }

    private void updateToolbarTitle(CalendarMonth calendarMonth) {
        DateTimeFormatter toolbarTitleFormatter = DateTimeFormatter.ofPattern(UiUtils.GREGORIAN_MONTH_YEAR_FORMAT);

        String title = toolbarTitleFormatter.format(calendarMonth.getYearMonth());
        toolbar.setTitle(StringUtils.capitalize(title));
    }

    private void initCalendarView() {
        calendarView = findViewById(R.id.calendarView);
        calendarView.setDayBinder(new CalendarDayBinder());
        calendarView.setMonthHeaderBinder(new CalendarMonthBinder());

        YearMonth currentMonth = YearMonth.now();
        YearMonth firstMonth = currentMonth.minusMonths(MIN_MONTH_COUNT);
        YearMonth lastMonth = currentMonth.plusMonths(MAX_MONTH_COUNT);
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        calendarView.setup(firstMonth, lastMonth, firstDayOfWeek);
        calendarView.scrollToMonth(currentMonth);
    }

    private void initHolidayRecyclerView() {
        holidayRecyclerView = (RecyclerView) findViewById(R.id.holiday_recycler_view);

        holidayRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        holidayRecyclerView.setLayoutManager(layoutManager);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.calendar_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void updateToolbarSubtitle() {
        StringBuilder subTitleBuilder = new StringBuilder();

        List<String> hijriMonthsIngregorianMonth = hijriDates.stream()
                .map(date -> date.getHijri().getMonth().getNumber())
                .distinct()
                .collect(Collectors.toList());

        List<String> hijriYearsIngregorianMonth = hijriDates.stream()
                .map(date -> date.getHijri().getYear())
                .distinct()
                .collect(Collectors.toList());

        Iterator<String> hijriMonthIterator = hijriMonthsIngregorianMonth.iterator();
        Iterator<String> hijriYearIterator = hijriYearsIngregorianMonth.iterator();

        while (hijriMonthIterator.hasNext()) {
            String hijriMonth = getResources().getString(
                    getResources().getIdentifier("hijri_month_" + hijriMonthIterator.next(), "string", getPackageName()));

            subTitleBuilder.append(hijriMonth);
            if (hijriMonthIterator.hasNext()) {
                subTitleBuilder.append(" / ");
            }
        }

        subTitleBuilder.append(" ");
        while (hijriYearIterator.hasNext()) {
            subTitleBuilder.append(hijriYearIterator.next());
            if (hijriYearIterator.hasNext()) {
                subTitleBuilder.append("/");
            }
        }

        this.toolbar.setSubtitle(subTitleBuilder.toString());
    }

    private void selectDate(LocalDate date) {
        DateTimeFormatter selectedDateFormatter = DateTimeFormatter.ofPattern(UiUtils.GREGORIAN_READABLE_FORMAT);
        if (selectedDate != date) {
            LocalDate oldDate = selectedDate;
            selectedDate = date;
            if (oldDate != null) {
                calendarView.notifyDateChanged(oldDate);
            }
            calendarView.notifyDateChanged(date);

            AladhanDateType hijriDate = hijriDates.get(selectedDate.getDayOfMonth() - 1).getHijri();
            String hijriMonth = getResources().getString(
                    getResources().getIdentifier("hijri_month_" + hijriDate.getMonth().getNumber(), "string", getPackageName()));

            String readableHijriDate = UiUtils.formatHijriDate(Integer.parseInt(hijriDate.getDay()), hijriMonth, Integer.parseInt(hijriDate.getYear()));

            String title = StringUtils.capitalize(selectedDateFormatter.format(selectedDate)) + " / " + readableHijriDate;

            selectedDateTextView.setText(title);
            selectedDateTextView.setVisibility(View.VISIBLE);

            HijriHoliday holiday = HijriHoliday.getHoliday(Integer.parseInt(hijriDate.getDay()), Integer.parseInt(hijriDate.getMonth().getNumber()));

            if (holiday != null) {
                String hijriDay = getResources().getString(
                        getResources().getIdentifier(holiday.toString(), "string", getPackageName()));

                HolidayAdapter holidayAdapter = new HolidayAdapter(Collections.singletonList(hijriDay));
                holidayRecyclerView.setAdapter(holidayAdapter);
                holidayRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }
}
