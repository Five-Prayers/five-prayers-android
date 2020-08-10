package com.bouzidi.prayertimes.ui.quran.surahs;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bouzidi.prayertimes.quran.dto.Surah;
import com.bouzidi.prayertimes.quran.registry.QuranRegistry;

import java.util.List;

public class QuranViewModel extends AndroidViewModel {

    private MutableLiveData<List<Surah>> mSurahs;

    public QuranViewModel(@NonNull Application application) {
        super(application);
        mSurahs = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    public MutableLiveData<List<Surah>> getSurahs() {
        return mSurahs;
    }

    private void setLiveData(Context context) {
        QuranRegistry quranRegistry = QuranRegistry.getInstance(context);

        List<Surah> surahs = quranRegistry.getSurahs();

        quranRegistry.getAllPages();

        mSurahs.postValue(surahs);
    }
}