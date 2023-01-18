package com.example.mobg_54018.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobg_54018.R
import com.example.mobg_54018.util.PermissionUtils
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.auth.FirebaseAuth

/* This class is an abstract class that extends the Fragment class. It has a property called auth that
is an instance of FirebaseAuth. It also has an onResume method that sets the screen orientation to
portrait and checks if the user is logged in. If the user is not logged in, it navigates to the
loginFragment */
abstract class BaseFragment : Fragment() {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var locationCallback: LocationCallback
    lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var alreadyLocationRequest : Boolean = false



    /**
     * It forces the screen to be in portrait mode.
     * If the user is not logged in, navigate to the login page
     */
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (auth.currentUser == null) {
                findNavController().navigate(
                    R.id.loginFragment
                )
        }

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.detailBarFragment) {
                if(!alreadyLocationRequest) {
                    startLocationUpdates()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.detailBarFragment) {
                stopLocationUpdates()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                val location: Location = locationResult.lastLocation
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(){
        if(alreadyLocationRequest) return
        alreadyLocationRequest = true
        val locationRequest = LocationRequest.create().apply {
            interval = 1000 * 10 // Mise à jour toutes les secondes
            fastestInterval = 1000 * 5 // 5 secondes
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())

    }

    private fun stopLocationUpdates(){
        if(!alreadyLocationRequest) return
        alreadyLocationRequest = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}
