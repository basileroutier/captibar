/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mobg_54018.fragment.detailbar

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobg_54018.R
import com.example.mobg_54018.databinding.FragmentBarDetailBinding
import com.example.mobg_54018.dto.Bar
import com.example.mobg_54018.fragment.BaseFragment
import com.example.mobg_54018.model.BarService
import com.example.mobg_54018.model.UserService
import com.example.mobg_54018.repository.BarRepository
import com.example.mobg_54018.util.LocationUtils
import com.example.mobg_54018.util.PermissionUtils
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson


@SuppressWarnings
/**
 * This class is used to display the detail of a bar.
 * It contains a map with the location of the bar
 * and a button to capture the bar
*/
class DetailBarFragment : BaseFragment() {

    private lateinit var detailBarViewModel : DetailBarViewModel
    private lateinit var barService : BarService
    private var alreadyLocationRequest : Boolean = false

    private lateinit var locationClient : FusedLocationProviderClient

    /**
     * This attribut used to get the location of the user when the tracker is enable
     * It check if the user is near the bar
     */
    private var locaCallBack : LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if(LocationUtils.locationOutOfTime(locationResult.lastLocation)) {
                Toast.makeText(requireContext(), "Une perte de localisation à eu lieu", Toast.LENGTH_SHORT).show()
                detailBarViewModel.barService.value?.canBeCaptured?.value = false
            } else {
                barService.checkUserLocationNearBar(locationResult.lastLocation)
            }
        }
    }

    /**
     * This attribut is used to request the permission to access the location of the user
     * If the permission is granted, we track the movement of the user
     * If the permission is permanently denied, we display a message to the user
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchLocationTracking()
        } else if(PermissionUtils.isLocationPermanentlyDenied(requireContext(), requireActivity())) {
            Toast.makeText(requireContext(),
                "Location is permanently denied. Impossible de valider le bar",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = FusedLocationProviderClient(requireActivity())
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val binding : FragmentBarDetailBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bar_detail, container, false)
        val application = requireNotNull(this.activity).application
        val arguments = DetailBarFragmentArgs.fromBundle(requireArguments())
        val bar = Gson().fromJson(arguments.bar, Bar::class.java)
        barService = BarService(bar, locationClient, BarRepository.getInstance(),requireContext())
        val viewModelFactory = DetailBarViewModelFactory(bar, barService , application)
        detailBarViewModel = ViewModelProvider(
                this, viewModelFactory)[DetailBarViewModel::class.java]


        checkPermission()

        detailBarViewModel.navigateToHomePage.observe(viewLifecycleOwner) {
            if (it == true) {
                this.findNavController()
                    .navigate(DetailBarFragmentDirections.actionDetailBarFragmentToHomePageFragment())
                detailBarViewModel.onNavigateToHomePageComplete()
            }
        }

        detailBarViewModel.barService.value?.canBeCaptured?.observe(viewLifecycleOwner, Observer {
            if(!detailBarViewModel.hasBeenCapturedByUser){
                detailBarViewModel.changeColorButton()
            }
        })

        detailBarViewModel.barService.value?.alreadyCaptured?.observe(viewLifecycleOwner) {
            if (it == true && !detailBarViewModel.hasBeenCapturedByUser) {
                Toast.makeText(requireContext(),
                    "Le bar a déjà été validé par une équipe",
                    Toast.LENGTH_SHORT).show()
                detailBarViewModel.changeColorButton(R.color.disabledButtonColor)
            }
        }


        detailBarViewModel.capturedBar.observe(viewLifecycleOwner) {
            if (it == true) {
                Toast.makeText(requireContext(),
                    "Bar capturé par l'équipe ${UserService.getInstanceUser()?.groupe}.\nBien joué !!!",
                    Toast.LENGTH_SHORT).show()
                detailBarViewModel.changeColorButton()
                detailBarViewModel.captureBarComplete()
            }
        }

        detailBarViewModel.onMapClick.observe(viewLifecycleOwner) {
            if (it == true) {
                detailBarViewModel.goToBarGoogleMap(requireContext())
                detailBarViewModel.onMapClickComplete()
            }
        }


        binding.detailBarViewModel = detailBarViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    /**
     * If the user has location enabled, we launch the location tracking,
     * if the user has permanently denied location, we show a toast,
     * if the user has denied location, we ask for permission
     */
    @SuppressLint("MissingPermission")
    private fun checkPermission(){
        if(PermissionUtils.isLocationEnabled(requireContext())) {
            launchLocationTracking()
        }else if(PermissionUtils.isLocationPermanentlyDenied(requireContext(), requireActivity())) {
            Toast.makeText(requireContext(), "Location is permanently denied. Impossible de valider le bar", Toast.LENGTH_SHORT).show()
        }else{
            enableMyLocation()
        }
    }

    /**
     * "If the user has granted the app permission to access their location, then enable the My
     * Location layer on the map."
     *
     * The first line of the function is a call to the `requestPermissionLauncher` object. This object
     * is a property of the `MapViewModel` class. It's a `PermissionLauncher` object that's been
     * configured to request the `ACCESS_FINE_LOCATION` permission
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Configure the map to track the user location
     * It check if the user is near the bar
     * And it launch the tracker
     */
    @SuppressLint("MissingPermission")
    private fun launchLocationTracking(){
        if(alreadyLocationRequest){
            return
        }
        val locationRequest = LocationRequest.create().apply {
            smallestDisplacement = 10.0f // Mise à jour seulement si la position a changé de plus de 10 mètres
            interval = 1000 // Mise à jour toutes les secondes
            fastestInterval = 1000 * 5 // 5 secondes
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        barService.userNearToBarLastLocation()
        locationClient.locationAvailability.addOnSuccessListener {
            if (it.isLocationAvailable) {
                locationClient.requestLocationUpdates(locationRequest, locaCallBack, null)
                alreadyLocationRequest = true
            }else{
                Toast.makeText(requireContext(), "Location is not available", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        locationClient.removeLocationUpdates(locaCallBack)
        alreadyLocationRequest = false
        detailBarViewModel.barService.value?.canBeCaptured?.value = false
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            detailBarViewModel.setGoogleMap(it)
            //detailBarViewModel.disableAllFunctionnality()
            detailBarViewModel.goToBarLocalisation(requireContext())
            addMarkerBarToMaps()
        }
    }

    /**
     * Get the address of the bar from the ViewModel
     * convert it to a LatLng object,
     * create a MarkerOptions object with the LatLng object, and then add the marker to the map
     */
    private fun addMarkerBarToMaps(){
        val latLngAddressBar = detailBarViewModel.bar.value?.let {
            LocationUtils.convertAdressToLatLng(it.address, requireContext())
        }
        val markerOptions = latLngAddressBar?.let {
            MarkerOptions()
                .position(it)
        }
        if (markerOptions != null) {
            detailBarViewModel.getGoogleMap()?.addMarker(markerOptions)
        }
    }

}
