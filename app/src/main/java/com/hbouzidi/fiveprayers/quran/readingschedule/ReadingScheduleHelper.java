package com.hbouzidi.fiveprayers.quran.readingschedule;

import android.content.Context;

import com.hbouzidi.fiveprayers.quran.dto.QuranQuarterMeta;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ReadingScheduleHelper {

    private final Context context;

    @Inject
    public ReadingScheduleHelper(Context context) {
        this.context = context;
    }

    public List<ReadingSchedule> createReadingSchedule(ReadingGoal readingGoal) {

        Map<Integer, List<Integer>> quranPageByQuarter = QuranParser.getInstance().getQuranPageByQuarter(context);
        List<QuranQuarterMeta> quranQuarterMetas = QuranParser.getInstance().getQuranQuarterMetas(context);

        int readingGoalTotalDays = readingGoal.getTotalDays();
        int rubuByDay = readingGoal.getRubu();

        List<ReadingSchedule> readingSchedule = new ArrayList<>();

        for (int i = 0; i < readingGoalTotalDays; i++) {
            int dayNumber = i + 1;
            int startQuarter = i * rubuByDay + 1;
            int endQuarter = i * rubuByDay + rubuByDay;
            int startPage = quranPageByQuarter.get(startQuarter).get(0);
            int endPage = quranPageByQuarter.get(endQuarter).get(quranPageByQuarter.get(endQuarter).size() - 1);
            int startAyahNumber = quranQuarterMetas.get(startQuarter - 1).getAyah();
            int startSurahNumber = quranQuarterMetas.get(startQuarter - 1).getSurah();

            ReadingSchedule dayReadingSchedule =
                    new ReadingSchedule(dayNumber, readingGoalTotalDays, startPage, startQuarter,
                            startAyahNumber, startSurahNumber, endPage, endQuarter, 0);

            readingSchedule.add(dayReadingSchedule);

            if (ReadingGoal.FOUR_TIMES_A_MONTH.equals(readingGoal) && i == ReadingGoal.FOUR_TIMES_A_MONTH.getTotalDays() - 2) {
                int cdayNumber = dayNumber + 1;
                int cstartQuarter = endQuarter + 1;
                int cendQuarter = 240;
                int cstartPage = quranPageByQuarter.get(cstartQuarter).get(0);
                int cendPage = quranPageByQuarter.get(cendQuarter).get(quranPageByQuarter.get(cendQuarter).size() - 1);

                int cstartAyahNumber = quranQuarterMetas.get(cstartQuarter - 1).getAyah();
                int cstartSurahNumber = quranQuarterMetas.get(cstartQuarter - 1).getSurah();

                readingSchedule.add(new ReadingSchedule(cdayNumber, readingGoalTotalDays, cstartPage,
                        cstartQuarter, cstartAyahNumber, cstartSurahNumber, cendPage, cendQuarter, 0));

                break;
            }
        }
        return readingSchedule;
    }
}
