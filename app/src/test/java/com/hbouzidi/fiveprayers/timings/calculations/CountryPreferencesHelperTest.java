package com.hbouzidi.fiveprayers.timings.calculations;

import android.location.Address;

import com.hbouzidi.fiveprayers.FakeFivePrayerApplication;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Locale;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = FakeFivePrayerApplication.class)
public class CountryPreferencesHelperTest {

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_null() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setLocality("Paris");
        address.setAddressLine(1, "Île-de-France");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.getDefault());
    }

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_FR() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setLocality("Paris");
        address.setAddressLine(1, "Île-de-France");
        address.setCountryCode("FR");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.MOSQUEE_DE_PARIS_FRANCE);
    }

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_FR_and_not_IDF() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setLocality("Paris");
        address.setAddressLine(1, "not_IDF");
        address.setCountryCode("FR");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.MOSQUEE_DE_PARIS_FRANCE);
    }

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_Unknown() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setAddressLine(1, "not_IDF");
        address.setCountryCode("KK");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.getDefault());
    }

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_MA() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setAddressLine(1, "not_IDF");
        address.setCountryCode("MA");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.MOROCCAN_MINISTRY_OF_ISLAMIC_AFFAIRS);
    }

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_TN() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setAddressLine(1, "not_IDF");
        address.setCountryCode("TN");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.TUNISIAN_MINISTRY_OF_RELIGIOUS_AFFAIRS);
    }

    @Test
    public void getCalculationMethodByAddress_when_country_code_is_DZ() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(40.2);
        address.setLongitude(-2.789);
        address.setAddressLine(1, "not_IDF");
        address.setCountryCode("DZ");

        Assertions.assertThat(CountryCalculationMethod.getCalculationMethodByAddress(address))
                .isEqualTo(CalculationMethodEnum.ALGERIAN_MINISTRY_OF_RELIGIOUS_AFFAIRS_AND_WAKFS);
    }
}