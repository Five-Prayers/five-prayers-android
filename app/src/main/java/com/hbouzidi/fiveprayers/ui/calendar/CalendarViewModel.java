package com.hbouzidi.fiveprayers.ui.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class CalendarViewModel extends AndroidViewModel {

    private MutableLiveData<LocalDate> selectedDate;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        selectedDate = new MutableLiveData<>();
    }

    public void setSelectedDate(LocalDate date) {
        selectedDate.setValue(date);
    }

    public MutableLiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}