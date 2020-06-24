package com.bouzidi.prayertimes.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.bouzidi.prayertimes.R;
import com.bouzidi.prayertimes.location.SearchHelper;
import com.bouzidi.prayertimes.location.address.AddressHelper;
import com.bouzidi.prayertimes.location.photon.Feature;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AutoCompleteTextPreferenceDialog extends PreferenceDialogFragmentCompat {

    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 500;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    private final AutoCompleteTextPreference preference;
    private AutoCompleteLoading mEditText;
    private boolean isSelectedText;

    public AutoCompleteTextPreferenceDialog(AutoCompleteTextPreference preference) {
        this.preference = preference;
        this.isSelectedText = false;

        final Bundle b = new Bundle();
        b.putString(ARG_KEY, preference.getKey());
        setArguments(b);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nexus 7 needs the keyboard hiding explicitly.
        // A flag on the activity in the manifest doesn't
        // apply to the dialog, so needs to be in code:
        Window window = requireActivity().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        mEditText = new AutoCompleteLoading(context);

        autoSuggestAdapter = new AutoSuggestAdapter(context, android.R.layout.simple_dropdown_item_1line);

        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        progressBar.setBackgroundColor(0xFF6DE3D1);
        progressBar.setIndeterminate(true);
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));

        mEditText.setThreshold(3);
        mEditText.setPaddingRelative(20, 30, 30, 20);
        mEditText.setHint(preference.getText());
        mEditText.setLoadingIndicator(progressBar);
        mEditText.setAdapter(autoSuggestAdapter);
        mEditText.setOnItemClickListener(
                (parent, view, position, id) -> {
                    isSelectedText = true;

                    AutoSuggestAdapter adapter = (AutoSuggestAdapter) mEditText.getAdapter();
                    Address address = adapter.getAddress(position);
                    AddressHelper.updateUserPreferences(context, address);
                });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
                isSelectedText = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }
        });

        handler = new Handler(msg -> {
            if (msg.what == TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(mEditText.getText())) {
                    retrieveData(mEditText.getText().toString());
                }
            }
            return false;
        });

        return mEditText;
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        if (mEditText != null && dialog != null) {
            // In order to prevent dialog from closing we setup its onLickListener this late
            Button neutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            if (neutralButton != null) {
                neutralButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
            }
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            String textValue = mEditText.getText().toString();
            if (preference.callChangeListener(textValue) && isSelectedText) {
                preference.setText(textValue);
            }
        }
    }

    private void retrieveData(String str) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                SearchHelper.searchForLocation(str, 5)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Feature>>() {
                            @Override
                            public void onSuccess(List<Feature> features) {
                                ArrayList<String> stringList = new ArrayList<>(5);
                                ArrayList<Address> addresses = new ArrayList<>(5);
                                for (Feature feature : features) {
                                    String locality = feature.getProperties().getName();
                                    String country = feature.getProperties().getCountry();
                                    boolean isPlaceType = feature.getProperties().getOsmKey().equals("place");

                                    if (isPlaceType && StringUtils.isNotBlank(locality) && StringUtils.isNotBlank(country)) {
                                        Address address = new Address(Locale.getDefault());
                                        address.setLocality(locality);
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(locality);
                                        builder.append(", ");
                                        if (feature.getProperties().getState() != null) {
                                            builder.append(feature.getProperties().getState());
                                            address.setSubLocality(feature.getProperties().getState());
                                            builder.append(", ");
                                        }

                                        builder.append(country);
                                        address.setCountryName(country);
                                        address.setLongitude(feature.getGeometry().getCoordinates().get(0));
                                        address.setLatitude(feature.getGeometry().getCoordinates().get(1));

                                        stringList.add(builder.toString());
                                        addresses.add(address);
                                    }
                                }

                                if (stringList.size() > 0) {
                                    autoSuggestAdapter.setData(stringList, addresses);
                                    autoSuggestAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            }
                        }));
    }
}
