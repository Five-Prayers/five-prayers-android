package com.hbouzidi.fiveprayers.quran.parser;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.quran.dto.Ayah;
import com.hbouzidi.fiveprayers.quran.dto.QuranPage;
import com.hbouzidi.fiveprayers.quran.dto.Surah;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.Map;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 28, maxSdk = 28, application = FakeFivePrayerApplication.class)
public class QuranParserTest {

    Context applicationContext;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();

    @Before
    public void before() {
        applicationContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void should_get_surahs() {
        QuranParser quranParser = QuranParser.getInstance();

        List<Surah> surahs = quranParser.getSurahs(applicationContext);

        Assertions.assertThat(surahs).isNotEmpty();
        Assertions.assertThat(surahs).hasSize(114);
    }

    @Test
    public void should_get_quran_pages() {
        QuranParser quranParser = QuranParser.getInstance();

        List<QuranPage> quranPages = quranParser.getQuranPages(applicationContext);

        Assertions.assertThat(quranPages).isNotEmpty();
        Assertions.assertThat(quranPages).hasSize(604);
    }

    @Test
    @Ignore("Work on local only")
    public void should_parse_daily_verse() {
        QuranParser quranParser = QuranParser.getInstance();

        Map<String, List<Ayah>> todayVerses = quranParser.getDailyVerses(applicationContext);

        Assertions.assertThat(todayVerses.keySet()).isNotEmpty();
        Assertions.assertThat(todayVerses.keySet()).hasSize(500);

        for (int i = 0; i < todayVerses.keySet().size(); i++) {
            Assertions.assertThat(todayVerses.get(String.valueOf(i))).isNotNull();
            Assertions.assertThat(todayVerses.get(String.valueOf(i))).hasSize(3);
        }
    }
}
