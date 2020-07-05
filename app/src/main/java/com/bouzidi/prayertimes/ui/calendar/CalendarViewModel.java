package com.bouzidi.prayertimes.ui.calendar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.threeten.bp.LocalDate;

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