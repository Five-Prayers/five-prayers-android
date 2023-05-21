package com.hbouzidi.fiveprayers.ui.quran.index;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingGoal;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;
import com.hbouzidi.fiveprayers.utils.AlertHelper;
import com.hbouzidi.fiveprayers.utils.UiUtils;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.owl93.dpb.CircularProgressView;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranScheduleIndexFragment extends QuranBaseIndexFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private TextView mStartDateTextView;
    private TextView mNotificationTimeTextView;
    private TextView firstLineInfo;
    private TextView secondLineInfo;
    private CircularProgressView circularProgressView;
    private CircularProgressBar circularProgressBar;
    private LocalDate startDate;
    private LocalTime notificationTime;
    private ReadingGoal readingGoal;
    private List<ReadingSchedule> readingSchedules;
    private RecyclerView recyclerView;
    private TextView resetTextView;

    private TextView startDateTextView;
    private QuranScheduleIndexViewModel quranScheduleIndexViewModel;
    private View summaryConstraintLayout;

    public QuranScheduleIndexFragment() {
    }

    public static QuranScheduleIndexFragment newInstance() {
        return new QuranScheduleIndexFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_quran_schedule_index, container, false);
        recyclerView = rootView.findViewById(R.id.schedule_recycler_view);
        resetTextView = rootView.findViewById(R.id.reset_schedule_tv);
        startDateTextView = rootView.findViewById(R.id.start_date_tv);
        firstLineInfo = rootView.findViewById(R.id.info_line_one);
        secondLineInfo = rootView.findViewById(R.id.info_line_two);
        summaryConstraintLayout = rootView.findViewById(R.id.summary_constraint_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            circularProgressView = rootView.findViewById(R.id.schedule_progress_view);
        } else {
            circularProgressBar = rootView.findViewById(R.id.schedule_progress_bar);
        }

        QuranIndexViewModel quranIndexViewModel = viewModelFactory.create(QuranIndexViewModel.class);
        quranIndexViewModel.getQuranPages().observe(getViewLifecycleOwner(), quranPages -> this.quranPages = quranPages);

        startDate = LocalDate.now();
        notificationTime = LocalTime.now();

        quranScheduleIndexViewModel = viewModelFactory.create(QuranScheduleIndexViewModel.class);
        quranScheduleIndexViewModel.getReadingSchedules().observe(getViewLifecycleOwner(), readingSchedules1 -> {
            readingSchedules = readingSchedules1;

            if (readingSchedules.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                summaryConstraintLayout.setVisibility(View.GONE);
                buildFormDialog();
            } else {
                refreshScheduleIndex();
            }
        });

        return rootView;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        startDate = LocalDate.of(year, month + 1, dayOfMonth);
        mStartDateTextView.setText(UiUtils.formatReadableGregorianDate(startDate, FormatStyle.FULL));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        notificationTime = LocalTime.of(hourOfDay, minute);
        mNotificationTimeTextView.setText(UiUtils.formatTiming(LocalDateTime.of(LocalDate.now(), notificationTime)));
    }

    private void buildFormDialog() {
        TypedArray typedArray = requireContext().getTheme().obtainStyledAttributes(R.styleable.mainStyles);
        int topColor = typedArray.getColor(R.styleable.mainStyles_navigationBackgroundStartColor, ContextCompat.getColor(requireContext(), R.color.colorAccent));
        int topTitleColor = typedArray.getColor(R.styleable.mainStyles_textColorPrimary, ContextCompat.getColor(requireContext(), R.color.textColorPrimary));

        View formView = LayoutInflater.from(requireContext()).inflate(R.layout.quran_schedule_form, null);

        LovelyCustomDialog lovelyCustomDialog = new LovelyCustomDialog(requireContext())
                .setView(formView)
                .setTopColor(topColor)
                .setTitle(R.string.create_quran_daily_schedule)
                .setTopTitleColor(topTitleColor)
                .setTitleGravity(Gravity.CENTER)
                .setIcon(R.drawable.ic_al_quran_book)
                .setCancelable(true);

        Dialog formDialog = lovelyCustomDialog.show();

        mStartDateTextView = formView.findViewById(R.id.start_date_text_view);
        mStartDateTextView.setOnClickListener(this::showDatePickerDialog);
        mStartDateTextView.setText(UiUtils.formatReadableGregorianDate(LocalDate.now(), FormatStyle.FULL));

        mNotificationTimeTextView = formView.findViewById(R.id.notification_time_text_view);
        mNotificationTimeTextView.setOnClickListener(this::showTimePickerDialog);
        mNotificationTimeTextView.setText(UiUtils.formatTiming(LocalDateTime.of(LocalDate.now(), LocalTime.now())));

        Spinner spinner = formView.findViewById(R.id.reading_goal_spinner);
        String[] spinnerItems = {
                getString(R.string.common_once_a_month),
                getString(R.string.common_twice_a_month),
                getString(R.string.common_three_times_a_month),
                getString(R.string.common_four_times_a_month),
                getString(R.string.common_once_in_three_weeks),
                getString(R.string.common_once_in_two_month),
                getString(R.string.common_once_in_four_month)};

        ReadingGoal[] spinnerValues = {
                ReadingGoal.ONCE_A_MONTH,
                ReadingGoal.TWICE_A_MONTH,
                ReadingGoal.THREE_TIMES_A_MONTH,
                ReadingGoal.FOUR_TIMES_A_MONTH,
                ReadingGoal.ONCE_IN_THREE_WEEKS,
                ReadingGoal.ONCE_IN_TWO_MONTH,
                ReadingGoal.ONCE_IN_FOUR_MONTH,
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                R.layout.spinner_item, spinnerItems);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                readingGoal = spinnerValues[arg2];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Button dialogOkBtn = formView.findViewById(R.id.dialog_ok_btn);
        dialogOkBtn.setOnClickListener(v -> {
            initializeReadingSchedule();
            preferencesHelper.setReadingScheduleStartDateNotification(startDate);
            preferencesHelper.setReadingScheduleNotificationTime(notificationTime);
            refreshScheduleIndex();
            formDialog.dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    private void refreshScheduleIndex() {
        surahs = QuranParser.getInstance().getSurahs(requireContext());
        ScheduleListAdapter adapter = new ScheduleListAdapter(readingSchedules, surahs);
        adapter.setScheduleListener((pos, start, end) -> gotoSuraa(pos, start, end, surahs));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        OptionalInt first = IntStream.range(0, readingSchedules.size())
                .filter(i -> readingSchedules.get(i).getStatus() == 0)
                .findFirst();

        recyclerView.scrollToPosition(first.orElse(0));

        List<ReadingSchedule> remainingScheduleDays = readingSchedules.stream()
                .filter(readingSchedule -> readingSchedule.getStatus() == 0)
                .collect(Collectors.toList());

        int totalDays = readingSchedules.get(0).getTotalDays();
        firstLineInfo.setText(
                getText(R.string.reciting_total_days) + " : " + totalDays
                        + " - " +
                        getText(R.string.reciting_remaining_days) + " : " + remainingScheduleDays.size()
        );
        secondLineInfo.setText(
                getText(R.string.notification_time) + " : " + UiUtils.formatTiming(LocalDateTime.of(LocalDate.now(),
                        preferencesHelper.getReadingScheduleNotificationTime())));

        int finishedDays = totalDays - remainingScheduleDays.size();
        float progress = ((float) finishedDays / (float) totalDays) * 100;

        firstLineInfo.setVisibility(View.VISIBLE);
        secondLineInfo.setVisibility(View.VISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            circularProgressView.setProgress(progress);
            circularProgressView.setVisibility(View.VISIBLE);
        } else {
            circularProgressBar.setProgress(progress);
            circularProgressBar.setVisibility(View.VISIBLE);
        }

        recyclerView.setVisibility(View.VISIBLE);
        summaryConstraintLayout.setVisibility(View.VISIBLE);
        startDateTextView.setVisibility(View.VISIBLE);
        startDateTextView.setText(UiUtils.formatReadableGregorianDate(preferencesHelper.getReadingScheduleStartDateNotification(), FormatStyle.LONG));
        resetTextView.setVisibility(View.VISIBLE);
        resetTextView.setOnClickListener(v -> AlertHelper.displayDialogConformation(
                requireContext().getString(R.string.reset_quran_daily_schedule),
                requireContext().getString(R.string.reset_quran_daily_schedule_confirmation),
                requireContext(), v1 -> {
                    readingScheduleRegistry.deleteReadingSchedule();
                    quranScheduleIndexViewModel.updateLiveData(requireContext());
                }, true));
    }

    private void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(this);
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(this);
        newFragment.show(requireActivity().getSupportFragmentManager(), "timePicker");
    }

    private void initializeReadingSchedule() {
        readingScheduleRegistry.saveReadingSchedule(readingScheduleHelper.createReadingSchedule(readingGoal));
        readingSchedules = readingScheduleRegistry.getReadingSchedule();

        preferencesHelper.setReadingScheduleFrequency(readingGoal);
    }
}