package com.hbouzidi.fiveprayers.ui.invocations;

import com.hbouzidi.fiveprayers.R;
import com.hbouzidi.fiveprayers.quran.dto.Invocation;
import com.hbouzidi.fiveprayers.quran.parser.QuranParser;

import java.util.List;
import java.util.Map;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
public class EveningInvocationActivity extends InvocationActivity {

    @Override
    public Map<String, List<Invocation>> getInvocation() {
        QuranParser quranParser = QuranParser.getInstance();
        return quranParser.getEveningInvocations(getApplicationContext());
    }

    @Override
    public int getToolbarTitleResourceId() {
        return R.string.evening_invocations;
    }
}
