package com.hbouzidi.compassqibla

import android.hardware.Sensor
import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CompassQiblaViewModel : ViewModel() {

    private val _direction = MutableLiveData<QiblaDirection>()
    val direction get() = _direction

    private val _accuracy = MutableLiveData<Pair<Sensor?, Int>>()
    val accuracy get() = _accuracy

    fun updateCompassDirection(qiblaDirection: QiblaDirection) {
        viewModelScope.launch {
            _direction.value = qiblaDirection
        }
    }

    fun updateAccuracy(sensor: Sensor?, accuracy: Int) {
        _accuracy.value = Pair(sensor, accuracy)
    }
}