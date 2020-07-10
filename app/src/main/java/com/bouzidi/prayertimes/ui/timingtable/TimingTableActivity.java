package com.bouzidi.prayertimes.ui.timingtable;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.timings.ComplementaryTimingEnum;
import com.bouzidi.prayertimes.timings.DayPrayer;
import com.bouzidi.prayertimes.timings.PrayerEnum;
import com.bouzidi.prayertimes.preferences.PreferencesConstants;
import com.bouzidi.prayertimes.utils.TimingUtils;
import com.bouzidi.prayertimes.utils.UiUtils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.providers.TableDataRowBackgroundProvider;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class TimingTableActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private static final int TEXT_SIZE = 11;

    private TableView<String[]> tableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing_table);

        SharedPreferences sharedPreferences = getSharedPreferences(PreferencesConstants.LOCATION, MODE_PRIVATE);

        TimingTableViewModel timingTableViewModel = new ViewModelProvider(this).get(TimingTableViewModel.class);

        Toolbar toolbar = findViewById(R.id.calendar_toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(v -> finish());

        String toolBarTitle = getString(R.string.calendar_view_title) + " " + sharedPreferences.getString(PreferencesConstants.LAST_KNOWN_LOCALITY, "");
        ((Toolbar) findViewById(R.id.calendar_toolbar)).setTitle(toolBarTitle);

        tableView = findViewById(R.id.tableView);

        TextView dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setText(StringUtils.capitalize(UiUtils.formatShortDate(LocalDate.now())));

        createTableView();

        timingTableViewModel.getCalendar().observe(this, this::populateTableView);
    }

    private void populateTableView(List<DayPrayer> calendar) {
        String[][] data = new String[calendar.size()][COLUMN_COUNT];

        for (int index = 0; index < calendar.size(); index++) {
            DayPrayer dayPrayer = calendar.get(index);
            Map<PrayerEnum, LocalDateTime> timings = dayPrayer.getTimings();
            Map<ComplementaryTimingEnum, LocalDateTime> complementaryTiming = dayPrayer.getComplementaryTiming();

            data[index][0] = String.valueOf(dayPrayer.getGregorianDay());
            data[index][1] = getHijriShortDate(dayPrayer);
            data[index][2] = TimingUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.FAJR)));
            data[index][3] = TimingUtils.formatTiming(Objects.requireNonNull(complementaryTiming.get(ComplementaryTimingEnum.SUNRISE)));
            data[index][4] = TimingUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.DHOHR)));
            data[index][5] = TimingUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.ASR)));
            data[index][6] = TimingUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.MAGHRIB)));
            data[index][7] = TimingUtils.formatTiming(Objects.requireNonNull(timings.get(PrayerEnum.ICHA)));
        }

        SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(this, data);
        dataAdapter.setTextSize(TEXT_SIZE);
        tableView.setDataAdapter(dataAdapter);
    }

    private void createTableView() {
        String month = LocalDate.now().getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault());
        String[] TABLE_HEADERS = {
                StringUtils.capitalize(month),
                getResources().getString(R.string.hijri),
                getResources().getString(R.string.SHORT_FAJR),
                getResources().getString(R.string.SUNRISE),
                getResources().getString(R.string.SHORT_DHOHR),
                getResources().getString(R.string.SHORT_ASR),
                getResources().getString(R.string.SHORT_MAGHRIB),
                getResources().getString(R.string.SHORT_ICHA)
        };

        tableView.setColumnCount(COLUMN_COUNT);

        TableColumnWeightModel columnModel = new TableColumnWeightModel(COLUMN_COUNT);
        columnModel.setColumnWeight(1, 2);
        tableView.setColumnModel(columnModel);

        SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this, TABLE_HEADERS);
        headerAdapter.setTextSize(TEXT_SIZE);
        tableView.setHeaderAdapter(headerAdapter);

        tableView.setDataRowBackgroundProvider(new TodayRowColorProvider());
    }

    private String getHijriShortDate(DayPrayer dayPrayer) {
        String hijriMonth = getResources().getString(
                getResources().getIdentifier("hijri_month_" + dayPrayer.getHijriMonthNumber(), "string", getPackageName()));

        return UiUtils.formatShortHijriDate(
                dayPrayer.getHijriDay(),
                hijriMonth
        );
    }

    private class TodayRowColorProvider implements TableDataRowBackgroundProvider<String[]> {
        @Override
        public Drawable getRowBackground(final int rowIndex, final String[] dayPrayer) {
            int rowColor;
            Drawable drawable;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = getResources().getDrawable(R.drawable.shape_rectangle_solid, getTheme());
            } else {
                drawable = getResources().getDrawable(R.drawable.shape_rectangle_solid);
            }

            if (dayPrayer[0].equals(String.valueOf(LocalDate.now().getDayOfMonth()))) {
                return drawable;
            } else if (rowIndex % 2 == 0) {
                rowColor = getResources().getColor(R.color.dew);
            } else {
                rowColor = getResources().getColor(R.color.white);
            }
            return new ColorDrawable(rowColor);
        }
    }
}