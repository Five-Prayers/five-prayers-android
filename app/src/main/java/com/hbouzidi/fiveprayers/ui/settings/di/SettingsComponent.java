package com.hbouzidi.fiveprayers.ui.settings.di;

import com.hbouzidi.fiveprayers.ui.settings.location.AutoCompleteTextPreferenceDialog;

import dagger.Subcomponent;

/**
 * @author Hicham Bouzidi Idrissi
 * Github : https://github.com/Five-Prayers/five-prayers-android
 * licenced under GPLv3 : https://www.gnu.org/licenses/gpl-3.0.en.html
 */
@Subcomponent
public interface SettingsComponent {

    @Subcomponent.Factory
    interface Factory {
        SettingsComponent create();
    }

    void inject(AutoCompleteTextPreferenceDialog autoCompleteTextPreferenceDialog);
}
