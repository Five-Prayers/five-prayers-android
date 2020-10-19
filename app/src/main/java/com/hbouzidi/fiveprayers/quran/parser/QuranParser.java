package com.hbouzidi.fiveprayers.quran.parser;

import android.content.Context;
import android.content.res.AssetManager;

import com.hbouzidi.fiveprayers.quran.dto.QuranResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class QuranParser {

    public static QuranResponse parseQuranFromAssets(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream ims = assetManager.open("quran-uthmani.json");

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(ims);

        return gson.fromJson(reader, QuranResponse.class);
    }
}
