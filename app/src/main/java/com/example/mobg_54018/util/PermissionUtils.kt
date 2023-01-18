package com.example.mobg_54018.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

/* It checks if the permission is granted or not. If not, it checks if the permission is permanently
denied or not */
class PermissionUtils {

    companion object {

        /**
         * Checks if the permission is granted or not.
         *
         * @param context The context of the activity or fragment.
         * @param permission The permission you want to check.
         * @return The return value is a boolean.
         */
        fun isPermissionGranted(context: Context, permission: String): Boolean {

            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * It checks if the permission is permanently denied or not.
         *
         * @param context Context of the activity from which you are calling this method.
         * @param activity The activity from which you are requesting the permission.
         * @param permission The permission you want to check.
         * @return Boolean
         */
        fun isPermissionPermentalyDenied(context: Context,activity: Activity, permission: String): Boolean {
            return (!isPermissionGranted(context, permission) && shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION))
        }

        /**
         * Checks if the location is enabled or not.
         *
         * @param context Context
         * @return A boolean value.
         */
        fun isLocationEnabled(context: Context) : Boolean{
            return (isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) || isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        /**
         * If the user has permanently denied the location permission, then show the user a dialog
         * explaining why the app needs the permission and then direct the user to the app settings
         * screen
         *
         * @param context Context of the activity
         * @param activity The activity that is requesting the permission.
         * @return A boolean value.
         */
        fun isLocationPermanentlyDenied(context: Context, activity: Activity) : Boolean{
            return isPermissionPermentalyDenied(context, activity, Manifest.permission.ACCESS_FINE_LOCATION) || isPermissionPermentalyDenied(context, activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        }

    }


}