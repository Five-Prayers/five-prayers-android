package com.hbouzidi.fiveprayers.ui.quran.index;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.database.ReadingScheduleRegistry;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class QuranScheduleIndexViewModel extends AndroidViewModel {

    private static final String TAG = "QuranScheduleViewModel";
    private final MutableLiveData<List<ReadingSchedule>> readingSchedules;
    private ReadingScheduleRegistry readingScheduleRegistry;

    @Inject
    public QuranScheduleIndexViewModel(@NonNull Application application, ReadingScheduleRegistry readingScheduleRegistry) {
        super(application);
        this.readingScheduleRegistry = readingScheduleRegistry;
        readingSchedules = new MutableLiveData<>();

        updateLiveData(application.getApplicationContext());
    }

    public void updateLiveData(Context context) {
        readingSchedules.postValue(readingScheduleRegistry.getReadingSchedule());
    }


    public MutableLiveData<List<ReadingSchedule>> getReadingSchedules() {
        return readingSchedules;
    }
}