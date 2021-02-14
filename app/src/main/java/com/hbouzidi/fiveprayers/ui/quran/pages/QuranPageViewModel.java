package com.hbouzidi.fiveprayers.ui.quran.pages;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;

import java.util.List;

public class QuranPageViewModel extends AndroidViewModel {

    private final MutableLiveData<List<QuranPage>> pages;

    public QuranPageViewModel(@NonNull Application application) {
        super(application);
        pages = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    public MutableLiveData<List<QuranPage>> getPages() {
        return pages;
    }

    private void setLiveData(Context context) {
        List<QuranPage> allQuranPages = QuranParser.getInstance().getQuranPages(context);
        pages.postValue(allQuranPages);
    }
}