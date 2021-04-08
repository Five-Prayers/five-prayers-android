package com.hbouzidi.fiveprayers.ui.calendar.di;

import com.hbouzidi.fiveprayers.ui.calendar.CalendarActivity;

import dagger.Subcomponent;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Subcomponent
public interface CalendarComponent {

    @Subcomponent.Factory
    interface Factory {
        CalendarComponent create();
    }

    void inject(CalendarActivity calendarActivity);
}
