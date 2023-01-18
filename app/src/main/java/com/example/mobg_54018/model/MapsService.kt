package com.example.mobg_54018.model

import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/* It's a wrapper for the GoogleMap object */
class MapsService : Maps{

    private var googleMap: GoogleMap? = null

    override fun setGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun getGoogleMap(): GoogleMap? {
        return googleMap
    }

    override fun setUpDefaultCameraOnBrusselsMap() {
        if(googleMap != null) {
            setUpDefaultCameraOnUserLocation(LatLng(50.8503, 4.3517))
        }
    }

    private fun setUpDefaultCameraOnUserLocation(location : LatLng) {
        if(googleMap != null) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 13f)
            googleMap?.moveCamera(cameraUpdate)
        }
    }

    override fun goToLocalisation(location : Location, animation : Boolean){
        if(location != null){
            val currentLocation = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 15f)
            if(animation){
                googleMap?.animateCamera(cameraUpdate)
            }else{
                googleMap?.moveCamera(cameraUpdate)
            }
        }
    }

    @Suppress("MissingPermission")
    override fun enableLocalisationMaps(){
        if(googleMap != null) {
            googleMap!!.isMyLocationEnabled = true
            googleMap!!.uiSettings?.isMyLocationButtonEnabled = false
        }
    }

    override fun disableAllFunctionnality(){
        if(googleMap != null) {
            googleMap!!.uiSettings?.isZoomControlsEnabled = false
            googleMap!!.uiSettings?.isCompassEnabled = false
            googleMap!!.uiSettings?.isMapToolbarEnabled = false
            googleMap!!.uiSettings?.isRotateGesturesEnabled = false
            googleMap!!.uiSettings?.isScrollGesturesEnabled = false
            googleMap!!.uiSettings?.isTiltGesturesEnabled = false
            googleMap!!.uiSettings?.isZoomGesturesEnabled = false
        }
    }

}