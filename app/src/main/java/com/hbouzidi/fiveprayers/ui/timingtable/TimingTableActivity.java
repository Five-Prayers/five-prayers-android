package com.hbouzidi.fiveprayers.ui.timingtable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;

import com.evrencoskun.tableview.TableView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.TableViewAdapter;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.Cell;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.ColumnHeader;
import com.hbouzidi.fiveprayers.ui.timingtable.tableview.model.RowHeader;
import com.hbouzidi.fiveprayers.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TimingTableActivity extends AppCompatActivity {

    private TableView mTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_table);

        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);

        TimingTableViewModel timingTableViewModel = new ViewModelProvider(this).get(TimingTableViewModel.class);

        mTableView = findViewById(R.id.tableview);

        // Force direction to LTR until Table view RTL support released
        ConstraintLayout tableConstraintLayout = findViewById(R.id.table_constraint_layout);
        ViewCompat.setLayoutDirection(tableConstraintLayout, ViewCompat.LAYOUT_DIRECTION_LTR);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> finish());

        String toolBarTitle = getString(R.string.calendar_view_title) + " " + sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_LOCALITY, "");
        ((TextView) findViewById(R.id.timing_table_toolbar_title)).setText(toolBarTitle);

        TextView dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setText(StringUtils.capitalize(UiUtils.formatShortDate(LocalDate.now())));

        timingTableViewModel.getCalendar().observe(this, this::initializeTableView);
    }


    private String getHijriShortDate(DayPrayer dayPrayer) {
        String hijriMonth = getResources().getString(
                getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", getPackageName()));

        return UiUtils.formatShortHijriDate(
                dayPrayer.getHijriDay(),
                hijriMonth
        );
    }

    private void initializeTableView(List<DayPrayer> calendar) {
        String month = LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());

        TableViewAdapter tableViewAdapter = new TableViewAdapter(StringUtils.capitalize(month), getApplicationContext());

        mTableView.setAdapter(tableViewAdapter);
        mTableView.setHasFixedWidth(true);
        tableViewAdapter.setAllItems(getColumnHeaderList(), getRowHeaderList(calendar), getCellList(calendar));

        mTableView.getSelectionHandler().setSelectedRowPosition(LocalDate.now().getDayOfMonth() - 1);
        mTableView.scrollToRowPosition(LocalDate.now().getDayOfMonth() - 1, 2);
    }

    @NonNull
    private List<RowHeader> getRowHeaderList(List<DayPrayer> calendar) {
        List<RowHeader> rowHeaders = new ArrayList<>();

        for (int index = 0; index < calendar.size(); index++) {
            DayPrayer dayPrayer = calendar.get(index);

            rowHeaders.add(new RowHeader(String.valueOf(dayPrayer.getGregorianDay()), String.valueOf(dayPrayer.getGregorianDay())));
        }

        return rowHeaders;
    }

    @NonNull
    private List<ColumnHeader> getColumnHeaderList() {
        ColumnHeader[] columnHeaders = {
                new ColumnHeader(getResources().getString(R.string.hijri), getResources().getString(R.string.hijri)),
                new ColumnHeader(getResources().getString(R.string.FAJR), getResources().getString(R.string.FAJR)),
                new ColumnHeader(getResources().getString(R.string.SUNRISE), getResources().getString(R.string.SUNRISE)),
                new ColumnHeader(getResources().getString(R.string.DHOHR), getResources().getString(R.string.DHOHR)),
                new ColumnHeader(getResources().getString(R.string.ASR), getResources().getString(R.string.ASR)),
                new ColumnHeader(getResources().getString(R.string.MAGHRIB), getResources().getString(R.string.MAGHRIB)),
                new ColumnHeader(getResources().getString(R.string.ICHA), getResources().getString(R.string.ICHA))
        };

        return Arrays.asList(columnHeaders);
    }

    @NonNull
    private List<List<Cell>> getCellList(List<DayPrayer> calendar) {
        List<List<Cell>> list = new ArrayList<>();

        for (int index = 0; index < calendar.size(); index++) {
            List<Cell> cellList = new ArrayList<>();
            DayPrayer dayPrayer = calendar.get(index);
            Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();
            Map<ComplementaryTimingEnum, LocalDateTime> complementaryTiming = dayPrayer.getComplementaryTiming();

            cellList.add(new Cell(index + "-0", getHijriShortDate(dayPrayer)));
            cellList.add(new Cell(index + "-1", UiUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.FAJR)))));
            cellList.add(new Cell(index + "-2", UiUtils.formatTiming(Objects.requireNonNull(complementaryTiming.get(ComplementaryTimingEnum.SUNRISE)))));
            cellList.add(new Cell(index + "-3", UiUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.DHOHR)))));
            cellList.add(new Cell(index + "-4", UiUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.ASR)))));
            cellList.add(new Cell(index + "-5", UiUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.MAGHRIB)))));
            cellList.add(new Cell(index + "-6", UiUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.ICHA)))));

            list.add(cellList);
        }

        return list;
    }
}