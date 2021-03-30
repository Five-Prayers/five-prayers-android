package com.hbouzidi.fiveprayers;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.hbouzidi.fiveprayers.timings.calculations.CalculationMethodEnum;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@RunWith(AndroidJUnit4.class)
public class CalculationMethodInstrumentedTest {
    @Test
    public void useAppContext() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();


        @ArrayRes final String[] valuesArray = context.getResources().getStringArray(R.array.entryvalues_calculation_method_list_preference);
        @ArrayRes final String[] labelsArray = context.getResources().getStringArray(R.array.entryvalues_calculation_method_list_preference);

        CalculationMethodEnum[] calculationMethodEnums = CalculationMethodEnum.values();

        assertEquals(calculationMethodEnums.length, valuesArray.length);
        assertEquals(labelsArray.length, valuesArray.length);

        for (CalculationMethodEnum methodEnum : calculationMethodEnums) {
            String methodName = methodEnum.toString();

            assertTrue("Method not found in value array : " + methodName, Arrays.asList(valuesArray).contains(methodName));

            int shortMethodId = context.getResources().getIdentifier("short_method_" + methodName.toLowerCase(), "string", context.getPackageName());
            assertNotEquals("Short string not found " + methodName, 0, shortMethodId);

            int methodId = context.getResources().getIdentifier("method_" + methodName.toLowerCase(), "string", context.getPackageName());
            assertNotEquals("long string not found " + methodName, methodId);
        }
    }
}
