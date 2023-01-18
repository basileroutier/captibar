package com.example.mobg_54018.model


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.mobg_54018.dto.Bar
import com.example.mobg_54018.repository.BarRepository
import com.example.mobg_54018.util.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/* It checks if the user is near to the bar and if he can capture it */
class BarService(private var bar : Bar, private val locationClient : FusedLocationProviderClient, private val barRepository : BarRepository, context : Context) {

    var canBeCaptured : MutableLiveData<Boolean> = MutableLiveData(false)
    var alreadyCaptured : MutableLiveData<Boolean> = MutableLiveData(false)
    private val locationBar : Location = LocationUtils.convertAddressToLocation(bar.address, context)

    init {
        MainScope().launch(Dispatchers.Main) {
            alreadyCaptured.value = barRepository.isAlreadyCapturedCurrentHour(bar.id)
        }
    }

    /**
     * If the user hasn't already captured the bar, get the user's last location and check if they're
     * near the bar
     */
    @SuppressLint("MissingPermission")
    fun userNearToBarLastLocation(){
        if(alreadyCaptured.value == false){
            locationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location != null && !LocationUtils.locationOutOfTime(location)){
                    checkUserLocationNearBar(location)
                }
            }
        }
    }

    /**
     * If the user hasn't already captured the bar, check if the user is near the bar and update the
     * canBeCaptured LiveData accordingly
     *
     * @param location The location of the user
     */
    fun checkUserLocationNearBar(location : Location){
        if(alreadyCaptured.value == false){
            val isNear = isUserNearToBar(location)
            if(isNear!= canBeCaptured.value && !LocationUtils.locationOutOfTime(location)){
                canBeCaptured.value = isNear
            }
        }

    }

    /**
     * If the distance between the user's location and the bar's location is less than or equal to 50
     * meters, then return true, otherwise return false.
     *
     * @param location The location of the user.
     * @return A boolean value.
     */
    private fun isUserNearToBar(location : Location) : Boolean{
        return locationBar.distanceTo(location) <= 50
    }
}