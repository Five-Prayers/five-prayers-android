package com.hbouzidi.fiveprayers.timings.offline;

import static java.util.Calendar.DAY_OF_MONTH;

import android.location.Address;

import androidx.annotation.NonNull;

import com.aminography.primecalendar.PrimeCalendar;
import com.aminography.primecalendar.common.CalendarFactory;
import com.aminography.primecalendar.common.CalendarType;
import com.aminography.primecalendar.hijri.HijriCalendar;
import com.batoulapps.adhan.CalculationMethod;
import com.batoulapps.adhan.CalculationParameters;
import com.batoulapps.adhan.Coordinates;
import com.batoulapps.adhan.HighLatitudeRule;
import com.batoulapps.adhan.Madhab;
import com.batoulapps.adhan.PrayerTimes;
import com.batoulapps.adhan.SunnahTimes;
import com.batoulapps.adhan.data.DateComponents;
import com.hbouzidi.fiveprayers.common.ComplementaryTimingEnum;
import com.hbouzidi.fiveprayers.common.PrayerEnum;
import com.hbouzidi.fiveprayers.preferences.PreferencesConstants;
import com.hbouzidi.fiveprayers.preferences.PreferencesHelper;
import com.hbouzidi.fiveprayers.timings.DayPrayer;
import com.hbouzidi.fiveprayers.timings.TimingsPreferences;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDate;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanDateType;
import com.hbouzidi.fiveprayers.timings.aladhan.AladhanMonth;
import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;
import com.hbouzidi.fiveprayers.timings.calculations.LatitudeAdjustmentMethod;
import com.hbouzidi.fiveprayers.timings.calculations.SchoolAdjustmentMethod;
import com.hbouzidi.fiveprayers.utils.TimingUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Singleton
public class OfflineTimingsService {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public OfflineTimingsService(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public DayPrayer getPrayerTimings(LocalDate localDate, Address address, TimingsPreferences timingsPreferences) {
        DateComponents date = new DateComponents(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
        Coordinates coordinates = new Coordinates(address.getLatitude(), address.getLongitude());

        CalculationMethodEnum internalMethod = timingsPreferences.getMethod();
        SchoolAdjustmentMethod internalSchoolAdjustmentMethod = timingsPreferences.getSchoolAdjustmentMethod();
        LatitudeAdjustmentMethod latitudeAdjustmentMethod = timingsPreferences.getLatitudeAdjustmentMethod();
        int hijriAdjustment = timingsPreferences.getHijriAdjustment();
        Map<String, Integer> tuneMap = preferencesHelper.getTuneMap();

        HijriCalendar hijriCalendar = getHijriCalendar(localDate, hijriAdjustment);
        CalculationParameters params = getCalculationParameters(
                internalMethod,
                internalSchoolAdjustmentMethod,
                latitudeAdjustmentMethod,
                tuneMap,
                hijriCalendar);

        PrayerTimes prayerTimes = new PrayerTimes(coordinates, date, params);

        return createDayPrayer(localDate, address, internalMethod, hijriCalendar, prayerTimes);
    }

    public List<DayPrayer> getPrayerCalendar(Address address, int month, int year, TimingsPreferences timingsPreferences) {
        List<DayPrayer> dayPrayers = new ArrayList<>();

        for (int day = 1; day <= Month.of(month).length(Year.of(year).isLeap()); day++) {
            dayPrayers.add(getPrayerTimings(LocalDate.of(year, month, day), address, timingsPreferences));
        }

        return dayPrayers;
    }

    public List<AladhanDate> getHijriCalendar(int month, int year, int adjustment) {
        List<AladhanDate> aladhanDates = new ArrayList<>();

        for (int day = 1; day <= Month.of(month).length(Year.of(year).isLeap()); day++) {
            PrimeCalendar calendar = CalendarFactory.newInstance(CalendarType.CIVIL);
            calendar.set(year, month - 1, day);

            HijriCalendar hijriCalendar = calendar.toHijri();
            hijriCalendar.add(DAY_OF_MONTH, adjustment);

            AladhanDateType hijri = new AladhanDateType(hijriCalendar.getYear(), new AladhanMonth(hijriCalendar.getMonth() + 1), hijriCalendar.getDayOfMonth());
            AladhanDateType gregorian = new AladhanDateType(calendar.getYear(), new AladhanMonth(calendar.getMonth() + 1), calendar.getDayOfMonth());

            aladhanDates.add(new AladhanDate(hijri, gregorian));
        }

        return aladhanDates;
    }

    @NonNull
    private CalculationMethod getFromCalculationMethodEnum(CalculationMethodEnum calculationMethodEnum) {

        switch (calculationMethodEnum) {
            case MUSLIM_WORLD_LEAGUE:
                return CalculationMethod.MUSLIM_WORLD_LEAGUE;
            case EGYPTIAN_GENERAL_AUTHORITY_OF_SURVEY:
                return CalculationMethod.EGYPTIAN;
            case UNIVERSITY_OF_ISLAMIC_SCIENCES_KARACHI:
                return CalculationMethod.KARACHI;
            case UMM_AL_QURA_UNIVERSITY_MAKKAH:
                return CalculationMethod.UMM_AL_QURA;
            case GULF_REGION:
                return CalculationMethod.DUBAI;
            case ISLAMIC_SOCIETY_OF_NORTH_AMERICA:
                return CalculationMethod.NORTH_AMERICA;
            case KUWAIT:
                return CalculationMethod.KUWAIT;
            case QATAR:
                return CalculationMethod.QATAR;
            case MAJLIS_UGAMA_ISLAM_SINGAPURA:
                return CalculationMethod.SINGAPORE;

            default:
                return CalculationMethod.OTHER;
        }
    }

    @NonNull
    private HighLatitudeRule getFromLatitudeAdjustmentMethod(LatitudeAdjustmentMethod latitudeAdjustmentMethod) {

        switch (latitudeAdjustmentMethod) {
            case MIDDLE_OF_THE_NIGHT:
                return HighLatitudeRule.MIDDLE_OF_THE_NIGHT;
            case ONE_SEVENTH:
                return HighLatitudeRule.SEVENTH_OF_THE_NIGHT;
            default:
                return HighLatitudeRule.TWILIGHT_ANGLE;
        }
    }

    @NonNull
    private DayPrayer createDayPrayer(LocalDate localDate, Address address, CalculationMethodEnum internalMethod, HijriCalendar hijriCalendar, PrayerTimes prayerTimes) {
        long epochSecond = TimingUtils.getTimestampsFromLocalDate(localDate, ZoneId.systemDefault());

        Map<PrayerEnum, LocalDateTime> timings = new LinkedHashMap<>(5);
        Map<ComplementaryTimingEnum, LocalDateTime> complementaryTiming = new LinkedHashMap<>(4);

        DayPrayer dayPrayer = new DayPrayer(TimingUtils.formatDateForAdhanAPI(localDate), epochSecond, address.getLocality(), address.getCountryName(),
                hijriCalendar.getDayOfMonth(),
                hijriCalendar.getMonth() + 1,
                hijriCalendar.getYear(),
                localDate.getDayOfMonth(),
                localDate.getMonthValue(),
                localDate.getYear());

        dayPrayer.setCalculationMethodEnum(internalMethod);

        LocalDateTime fajrTime = TimingUtils.convertToLocalDateTime(prayerTimes.fajr, ZoneId.systemDefault());
        LocalDateTime maghribTime = TimingUtils.convertToLocalDateTime(prayerTimes.maghrib, ZoneId.systemDefault());

        timings.put(PrayerEnum.FAJR, fajrTime);
        timings.put(PrayerEnum.DHOHR, TimingUtils.convertToLocalDateTime(prayerTimes.dhuhr, ZoneId.systemDefault()));
        timings.put(PrayerEnum.ASR, TimingUtils.convertToLocalDateTime(prayerTimes.asr, ZoneId.systemDefault()));
        timings.put(PrayerEnum.MAGHRIB, maghribTime);
        timings.put(PrayerEnum.ICHA, TimingUtils.convertToLocalDateTime(prayerTimes.isha, ZoneId.systemDefault()));

        SunnahTimes sunnahTimes = new SunnahTimes(prayerTimes);

        complementaryTiming.put(ComplementaryTimingEnum.SUNRISE, TimingUtils.convertToLocalDateTime(prayerTimes.sunrise, ZoneId.systemDefault()));
        complementaryTiming.put(ComplementaryTimingEnum.SUNSET, maghribTime);
        complementaryTiming.put(ComplementaryTimingEnum.DOHA, TimingUtils.convertToLocalDateTime(prayerTimes.sunrise, ZoneId.systemDefault()).plusMinutes(TimingUtils.DOHA_INTERVAL));
        complementaryTiming.put(ComplementaryTimingEnum.MIDNIGHT, TimingUtils.convertToLocalDateTime(sunnahTimes.middleOfTheNight, ZoneId.systemDefault()));
        complementaryTiming.put(ComplementaryTimingEnum.LAST_THIRD_OF_THE_NIGHT, TimingUtils.getLastThirdOfTheNight(fajrTime, maghribTime));

        dayPrayer.setTimings(timings);
        dayPrayer.setComplementaryTiming(complementaryTiming);
        dayPrayer.setTimezone(TimingUtils.getDefaultTimeZone());
        dayPrayer.setLatitude(address.getLatitude());
        dayPrayer.setLongitude(address.getLongitude());
        return dayPrayer;
    }

    @NonNull
    private CalculationParameters getCalculationParameters(CalculationMethodEnum internalMethod, SchoolAdjustmentMethod internalSchoolAdjustmentMethod, LatitudeAdjustmentMethod latitudeAdjustmentMethod, Map<String, Integer> tuneMap, HijriCalendar hijriCalendar) {
        int RAMADAN_MONTH_INDEX = 8;
        int RAMADAN_ICHA_ADJUSTMENT = 30;

        CalculationParameters params;
        CalculationMethod calculationMethod = getFromCalculationMethodEnum(internalMethod);

        if (calculationMethod.equals(CalculationMethod.OTHER)) {
            params = CalculationMethod.OTHER.getParameters();
            params.fajrAngle = Double.parseDouble(internalMethod.getFajrAngle());
            params.ishaAngle = Double.parseDouble(internalMethod.getIchaAngle());
        } else {
            params = calculationMethod.getParameters();
        }

        params.madhab = SchoolAdjustmentMethod.HANAFI.equals(internalSchoolAdjustmentMethod) ? Madhab.HANAFI : Madhab.SHAFI;
        params.highLatitudeRule = getFromLatitudeAdjustmentMethod(latitudeAdjustmentMethod);

        params.methodAdjustments.fajr = tuneMap.get(PreferencesConstants.FAJR_TIMING_ADJUSTMENT);
        params.methodAdjustments.dhuhr = tuneMap.get(PreferencesConstants.DOHR_TIMING_ADJUSTMENT);
        params.methodAdjustments.asr = tuneMap.get(PreferencesConstants.ASR_TIMING_ADJUSTMENT);
        params.methodAdjustments.maghrib = tuneMap.get(PreferencesConstants.MAGHREB_TIMING_ADJUSTMENT);

        params.methodAdjustments.isha =
                (CalculationMethod.UMM_AL_QURA.equals(calculationMethod) && hijriCalendar.getMonth() == RAMADAN_MONTH_INDEX) ?
                        RAMADAN_ICHA_ADJUSTMENT + tuneMap.get(PreferencesConstants.ICHA_TIMING_ADJUSTMENT) : tuneMap.get(PreferencesConstants.ICHA_TIMING_ADJUSTMENT);
        return params;
    }

    @NonNull
    private HijriCalendar getHijriCalendar(LocalDate localDate, int hijriAdjustment) {
        PrimeCalendar calendar = CalendarFactory.newInstance(CalendarType.CIVIL);
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());

        HijriCalendar hijriCalendar = calendar.toHijri();
        hijriCalendar.add(DAY_OF_MONTH, hijriAdjustment);
        return hijriCalendar;
    }
}
