package com.example.mobg_54018.model

import android.location.Location
import com.google.android.gms.maps.GoogleMap

/* It's an interface that will be implemented by the MapsManager class */
interface Maps {

    fun setGoogleMap(googleMap: GoogleMap)

    fun getGoogleMap(): GoogleMap?

    /**
     * It sets up a default camera on a map of Brussels
     */
    fun setUpDefaultCameraOnBrusselsMap()

    /**
     * It takes a Location object and an optional Boolean value, and does something with them
     *
     * @param location The location to go to.
     * @param animation Boolean - If true, the map will animate to the new location.
     */
    fun goToLocalisation(location : Location, animation : Boolean = true)

    /**
     * This function enables the localisation of the user on the map.
     */
    @Suppress("MissingPermission")
    fun enableLocalisationMaps()

    /**
     * It disables all the functionnality of the application
     */
    fun disableAllFunctionnality()
}