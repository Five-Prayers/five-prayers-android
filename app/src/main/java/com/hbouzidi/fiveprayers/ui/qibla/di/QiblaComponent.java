package com.hbouzidi.fiveprayers.ui.qibla.di;

import com.hbouzidi.fiveprayers.ui.qibla.CompassActivity;

import dagger.Subcomponent;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Subcomponent(modules = {QiblaModule.class})
public interface QiblaComponent {

    @Subcomponent.Factory
    interface Factory {
        QiblaComponent create();
    }

    void inject(CompassActivity compassActivity);
}
