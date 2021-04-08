package com.hbouzidi.fiveprayers.ui.home.di;


import androidx.lifecycle.ViewModel;

import com.hbouzidi.fiveprayers.di.factory.viewmodel.ViewModelKey;
import com.hbouzidi.fiveprayers.ui.home.HomeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module
public abstract class HomeModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    public abstract ViewModel bindsHomeViewModel(HomeViewModel homeViewModel);
}
