package com.hbouzidi.fiveprayers.ui.timingtable.di;

import com.hbouzidi.fiveprayers.ui.timingtable.TimingTableBaseFragment;

import dagger.Subcomponent;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Subcomponent(modules = {TimingTableModule.class})
public interface TimingTableComponent {

    @Subcomponent.Factory
    interface Factory {
        TimingTableComponent create();
    }

    void inject(TimingTableBaseFragment timingTableBaseFragment);
}
