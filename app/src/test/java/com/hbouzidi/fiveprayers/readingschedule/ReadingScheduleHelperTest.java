package com.hbouzidi.fiveprayers.readingschedule;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingGoal;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingSchedule;
import com.hbouzidi.fiveprayers.quran.readingschedule.ReadingScheduleHelper;

import junit.framework.TestCase;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@RunWith(RobolectricTestRunner.class)
@Config(minSdk = 28, maxSdk = 28, application = FakeFivePrayerApplication.class)
public class ReadingScheduleHelperTest extends TestCase {

    Context applicationContext;

    @Rule
    public MockitoRule initRule = MockitoJUnit.rule();

    @Before
    public void before() {
        applicationContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void test_calculate_reading_scheduler_ONCE_IN_FOUR_MONTH() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.ONCE_IN_FOUR_MONTH);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.ONCE_IN_FOUR_MONTH.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(ReadingGoal.ONCE_IN_FOUR_MONTH.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }

    @Test
    public void test_calculate_reading_scheduler_ONCE_IN_TWO_MONTH() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.ONCE_IN_TWO_MONTH);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.ONCE_IN_TWO_MONTH.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(ReadingGoal.ONCE_IN_TWO_MONTH.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }

    @Test
    public void test_calculate_reading_scheduler_ONCE_A_MONTH() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.ONCE_A_MONTH);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.ONCE_A_MONTH.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartAyahNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartSurahNumber()).isEqualTo(1);

        Assertions.assertThat(readingSchedule.get(19).getDayNumber()).isEqualTo(20);
        Assertions.assertThat(readingSchedule.get(19).getStartPage()).isEqualTo(381);
        Assertions.assertThat(readingSchedule.get(19).getStartAyahNumber()).isEqualTo(56);
        Assertions.assertThat(readingSchedule.get(19).getStartSurahNumber()).isEqualTo(27);

        Assertions.assertThat(readingSchedule.get(24).getDayNumber()).isEqualTo(25);
        Assertions.assertThat(readingSchedule.get(24).getStartPage()).isEqualTo(481);
        Assertions.assertThat(readingSchedule.get(24).getStartSurahNumber()).isEqualTo(41);
        Assertions.assertThat(readingSchedule.get(24).getStartAyahNumber()).isEqualTo(47);

        Assertions.assertThat(readingSchedule.get(28).getDayNumber()).isEqualTo(29);
        Assertions.assertThat(readingSchedule.get(28).getStartPage()).isEqualTo(561);
        Assertions.assertThat(readingSchedule.get(28).getStartSurahNumber()).isEqualTo(67);
        Assertions.assertThat(readingSchedule.get(28).getStartAyahNumber()).isEqualTo(1);

        Assertions.assertThat(readingSchedule.get(ReadingGoal.ONCE_A_MONTH.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }

    @Test
    public void test_calculate_reading_scheduler_ONCE_IN_THREE_WEEKS() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.ONCE_IN_THREE_WEEKS);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.ONCE_IN_THREE_WEEKS.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(ReadingGoal.ONCE_IN_THREE_WEEKS.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }

    @Test
    public void test_calculate_reading_scheduler_TWICE_A_MONTH() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.TWICE_A_MONTH);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.TWICE_A_MONTH.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(ReadingGoal.TWICE_A_MONTH.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }

    @Test
    public void test_calculate_reading_scheduler_THREE_TIMES_A_MONTH() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.THREE_TIMES_A_MONTH);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.THREE_TIMES_A_MONTH.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(ReadingGoal.THREE_TIMES_A_MONTH.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }

    @Test
    public void test_calculate_reading_scheduler_FOUR_TIMES_A_MONTH() {

        ReadingScheduleHelper readingScheduleHelper = new ReadingScheduleHelper(applicationContext);

        List<ReadingSchedule> readingSchedule = readingScheduleHelper.createReadingSchedule(ReadingGoal.FOUR_TIMES_A_MONTH);

        Assertions.assertThat(readingSchedule).isNotEmpty();
        Assertions.assertThat(readingSchedule).hasSize(ReadingGoal.FOUR_TIMES_A_MONTH.getTotalDays());
        Assertions.assertThat(readingSchedule.get(0).getDayNumber()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(0).getStartPage()).isEqualTo(1);
        Assertions.assertThat(readingSchedule.get(ReadingGoal.FOUR_TIMES_A_MONTH.getTotalDays() - 1).getEndPage()).isEqualTo(604);
    }
}