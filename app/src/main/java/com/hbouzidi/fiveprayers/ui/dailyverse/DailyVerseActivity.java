package com.hbouzidi.fiveprayers.ui.dailyverse;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.os.ConfigurationCompat;

import com.hbouzidi.fiveprayers.FivePrayerApplication;
import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.quran.dto.Ayah;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;
import com.hbouzidi.fiveprayers.ui.BaseActivity;
import com.hbouzidi.fiveprayers.utils.CustomTypefaceSpan;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class DailyVerseActivity extends BaseActivity {

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        ((FivePrayerApplication) getApplicationContext())
                .defaultComponent
                .inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_verse);

        QuranParser quranParser = QuranParser.getInstance();
        Map<String, List<Ayah>> dailyVerses = quranParser.getDailyVerses(this);

        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        NumberFormat numberFormat = NumberFormat.getInstance();

        String systemLanguage = Objects.requireNonNull(ConfigurationCompat
                .getLocales(Resources.getSystem().getConfiguration())
                .get(0)).getLanguage();

        List<Ayah> todayVerse = dailyVerses.get(preferencesHelper.getDailyVerseKey());

        Ayah ayahInArabicLanguage = Objects.requireNonNull(todayVerse).stream()
                .filter((ayah -> ayah.getEdition().getLanguage().equals("ar")))
                .findFirst()
                .orElse(todayVerse.get(0));

        Ayah ayahInEnglishLanguage = Objects.requireNonNull(todayVerse).stream()
                .filter((ayah -> ayah.getEdition().getLanguage().equals("en")))
                .findFirst()
                .orElse(todayVerse.get(1));

        Ayah ayahSystemLanguage = Objects.requireNonNull(todayVerse).stream()
                .filter((ayah -> ayah.getEdition().getLanguage().equals(systemLanguage)))
                .findFirst()
                .orElse(null);

        TextView baslamaTextView = findViewById(R.id.baslama_text_view);
        baslamaTextView.setText(getIslamicPhrase("6"));

        TextView sadakaTextView = findViewById(R.id.sadaka_text_view);
        sadakaTextView.setText(getIslamicPhrase("S"));

        TextView separatorView = findViewById(R.id.ornaments_separator_text_view);
        separatorView.setText(getIslamicPhrase("XXXXXXX"));

        TextView arabicMetaTextView = findViewById(R.id.arabic_meta_text_view);
        arabicMetaTextView.setText(ayahInArabicLanguage.getSurah().getName() + " - " + numberFormat.format(ayahInArabicLanguage.getNumberInSurah()));

        TextView englishMetaTextView = findViewById(R.id.english_meta_text_view);
        englishMetaTextView.setText(ayahInArabicLanguage.getSurah().getEnglishName() + " - " + numberFormat.format(ayahInArabicLanguage.getNumberInSurah()));

        TextView ayaTextView = findViewById(R.id.aya_text_view);
        ayaTextView.setText(ayahInArabicLanguage.getText());

        TextView translationTextView = findViewById(R.id.translation_text_view);
        translationTextView.setText(ayahSystemLanguage != null ? ayahSystemLanguage.getText() : ayahInEnglishLanguage.getText());
    }

    private String getIslamicPhrase(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.aga_islamic_phrases);
        builder.append(text);
        builder.setSpan(new CustomTypefaceSpan(typeface), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(2.5f), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder.toString();
    }
}