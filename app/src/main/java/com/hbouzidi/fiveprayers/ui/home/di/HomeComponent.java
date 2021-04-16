package com.hbouzidi.fiveprayers.ui.home.di;

import com.hbouzidi.fiveprayers.ui.home.HomeFragment;

import dagger.Subcomponent;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Subcomponent(modules = {HomeModule.class})
public interface HomeComponent {

    @Subcomponent.Factory
    interface Factory {
        HomeComponent create();
    }

    void inject(HomeFragment homeFragment);
}
