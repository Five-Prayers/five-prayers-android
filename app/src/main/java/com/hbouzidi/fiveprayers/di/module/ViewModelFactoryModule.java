package com.hbouzidi.fiveprayers.di.module;

import androidx.lifecycle.ViewModelProvider;

import com.hbouzidi.fiveprayers.di.factory.viewmodel.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Module
public abstract class ViewModelFactoryModule {


    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelProviderFactory);

}
