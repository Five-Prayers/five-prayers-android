package com.hbouzidi.fiveprayers.ui.quran.pages;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.quran.dto.Page;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;

import java.io.IOException;
import java.util.List;

public class AyahsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Page>> pages;

    public AyahsViewModel(@NonNull Application application) throws IOException {
        super(application);
        pages = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    public MutableLiveData<List<Page>> getPages() {
        return pages;
    }

    private void setLiveData(Context context) throws IOException {
        List<Page> allPages = QuranParser.parsePagesFromAssets(context);
        pages.postValue(allPages);
    }
}