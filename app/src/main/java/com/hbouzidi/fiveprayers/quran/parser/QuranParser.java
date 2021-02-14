package com.hbouzidi.fiveprayers.quran.parser;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuranParser {

    private final static String TAG = "QuranParser";

    private static QuranParser INSTANCE = null;

    private List<Surah> surahList;
    private List<QuranPage> quranPages;

    private QuranParser() {
        surahList = new ArrayList<>();
        quranPages = new ArrayList<>();
    }

    public static QuranParser getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QuranParser();
        }
        return INSTANCE;
    }

    public List<Surah> getSurahs(Context context) {
        if (surahList.isEmpty()) {
            parseSurahsFromAssets(context);
        }
        return surahList;
    }

    public List<QuranPage> getQuranPages(Context context) {
        if (quranPages.isEmpty()) {
            parsePagesFromAssets(context);
        }
        return quranPages;
    }

    private void parseSurahsFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream ims;
            ims = assetManager.open("quran-surahs.json");

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(ims);

            Type type = new TypeToken<ArrayList<Surah>>() {
            }.getType();
            surahList = gson.fromJson(reader, type);

        } catch (IOException e) {
            Log.e(TAG, "cannot parse quran-surahs.json", e);
        }
    }

    private void parsePagesFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream ims = assetManager.open("quran-pages.json");
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(ims);

            Type type = new TypeToken<ArrayList<QuranPage>>() {
            }.getType();
            quranPages = gson.fromJson(reader, type);
        } catch (IOException e) {
            Log.e(TAG, "cannot parse quran-pages.json", e);
        }
    }
}
