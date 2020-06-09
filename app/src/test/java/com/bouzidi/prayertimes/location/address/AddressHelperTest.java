package com.bouzidi.prayertimes.location.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;

import com.bouzidi.prayertimes.exceptions.LocationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;


@RunWith(MockitoJUnitRunner.class)
public class AddressHelperTest {

    @Mock
    Context mockContext;
    @Mock
    SharedPreferences sharedPrefs;
    @Mock
    SharedPreferences.Editor mockEditor;

    @Before
    public void before() {
        this.sharedPrefs = Mockito.mock(SharedPreferences.class);
        this.mockContext = Mockito.mock(Context.class);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs);
        Mockito.when(mockContext.getSharedPreferences(anyString(), anyInt()).edit()).thenReturn(mockEditor);
    }

    @Test
    public void getAddressFromLocation_when_location_is_null() {
        TestObserver<Address> addressTestObserver = new TestObserver<>();
        Single<Address> addressSingle = AddressHelper.getAddressFromLocation(null, mockContext);

        addressSingle.subscribe(addressTestObserver);

        addressTestObserver.assertNotComplete();
        addressTestObserver.assertError(LocationException.class);
    }

    @Test
    public void getAddressFromLocation() {
        Mockito.when(sharedPrefs.getString("last_known_locality", null)).thenReturn("Colombes");
        Mockito.when(sharedPrefs.getString("last_known_country", null)).thenReturn("France");
        Mockito.when(sharedPrefs.getLong("last_known_latitude", 0)).thenReturn(0L);
        Mockito.when(sharedPrefs.getLong("last_known_longitude", 0)).thenReturn(0L);
        Mockito.when(sharedPrefs.getLong("last_known_longitude", 0)).thenReturn(0L);

        Address lastKnownAddress = AddressHelper.getLastKnownAddress(mockContext);

        assertNotNull(lastKnownAddress);
        //    assertEquals("Colombes", lastKnownAddress.getLocality());
        //  assertEquals("France", lastKnownAddress.getCountryName());
    }
}