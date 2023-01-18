package com.example.mobg_54018.util

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng

/* It converts an address to a location or a LatLng */
class LocationUtils {

    companion object {

        private const val LOCATION_EXPIRATION_TIME = 1000 * 60  // 1 minute

        /**
         * It takes an address and a context, and returns a Location object with the latitude and
         * longitude of the address
         *
         * @param address The address you want to convert to a location.
         * @param context The context of the activity.
         * @return A Location object
         */
        fun convertAddressToLocation(address: String, context: Context): Location {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(address, 1)
            val location = Location("")
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                location.latitude = address.latitude
                location.longitude = address.longitude
            }
            return location
        }

        /**
         * It takes an address as a string and returns a LatLng object
         *
         * @param address The address you want to convert to LatLng
         * @param context The context of the activity.
         * @return LatLng
         */
        fun convertAdressToLatLng(address: String, context: Context) : LatLng {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocationName(address, 1)
            var latLng = LatLng(0.0, 0.0)
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                latLng = LatLng(address.latitude, address.longitude)
            }
            return latLng
        }

        /**
         * It converts a location to an address.
         *
         * @param location Location - The location object that you want to convert to an address.
         * @param context Context
         * @return A string
         */
        fun convertLocationToAddress(location: Location, context: Context) : String {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            var address = ""
            if (addresses.isNotEmpty()) {
                address = addresses[0].getAddressLine(0)
            }
            return address
        }

        /**
         * If the current time minus the location time is greater than the location expiration time,
         * then the location is out of time
         *
         * @param location The location object that we want to check if it's out of time.
         * @return A boolean value.
         */
        fun locationOutOfTime(location: Location) : Boolean {
            return location.time + LOCATION_EXPIRATION_TIME < System.currentTimeMillis()
        }
    }

}