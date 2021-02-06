package com.hbouzidi.fiveprayers.quran.parser;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.reflect.TypeToken;
import com.hbouzidi.fiveprayers.quran.dto.Page;
import com.google.gson.Gson;
import com.hbouzidi.fiveprayers.quran.dto.Surah;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuranParser {

    public static List<Surah> parseSurahsFromAssets(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream ims = assetManager.open("quran-surahs.json");

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(ims);

        Type type = new TypeToken<ArrayList<Surah>>(){}.getType();
        return gson.fromJson(reader, type);
    }

    public static List<Page> parsePagesFromAssets(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream ims = assetManager.open("quran-pages.json");

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(ims);

        Type type = new TypeToken<ArrayList<Page>>(){}.getType();
        return gson.fromJson(reader, type);
    }
}
