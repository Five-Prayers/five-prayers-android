package com.hbouzidi.fiveprayers.ui.quran.pages;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hbouzidi.fiveprayers.quran.dto.Page;
import com.hbouzidi.fiveprayers.quran.registry.QuranRegistry;

import java.util.List;

public class AyahsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Page>> pages;

    public AyahsViewModel(@NonNull Application application) {
        super(application);
        pages = new MutableLiveData<>();
        setLiveData(application.getApplicationContext());
    }

    public MutableLiveData<List<Page>> getPages() {
        return pages;
    }

    private void setLiveData(Context context) {
        QuranRegistry quranRegistry = QuranRegistry.getInstance(context);

        List<Page> allPages = quranRegistry.getAllPages();
        pages.postValue(allPages);
    }
}