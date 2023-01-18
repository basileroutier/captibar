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

package com.example.mobg_54018.fragment.mapbar

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.mobg_54018.R
import com.example.mobg_54018.databinding.FragmentMapsBinding
import com.example.mobg_54018.fragment.BaseFragment
import com.example.mobg_54018.model.BarClusterRender
import com.example.mobg_54018.model.BarItem
import com.example.mobg_54018.repository.BarRepository
import com.example.mobg_54018.util.LocationUtils
import com.example.mobg_54018.util.PermissionUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class MapsFragment : BaseFragment() {

    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: MapsViewModel
    private lateinit var clusterManager: ClusterManager<BarItem>
    private lateinit var clusterRenderer : BarClusterRender

    /* It's an attribut that checks if the user has granted the permission to access the location. */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            goToCurrentLocation()
        }
    }

    /**
     * We're creating a binding object that will be used to inflate the fragment views
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent that this fragment's UI should be attached to.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
     * is the state.
     * @return The binding object is being returned.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_maps, container, false)
        val application = requireNotNull(this.activity).application
        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]

        binding.mapViewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    /**
     * We get the mapFragment from the layout, we set the googleMap in the viewModel, we set the
     * default camera on the map, we init the map points, we observe the onLocalisateMeClicked event
     * and we check the permission
     *
     * @param view View, savedInstanceState: Bundle?
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
     * is the state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync{
            viewModel.setGoogleMap(it)
            viewModel.setUpDefaultCameraOnBrusselsMap()

            MainScope().launch(Dispatchers.Main) {
                initMapPoints()
            }


            if(PermissionUtils.isLocationEnabled(requireContext())) {
                goToCurrentLocation()
            }


            viewModel.onLocalisateMeClicked.observe(viewLifecycleOwner) { onLocalisateMeClicked ->
                if (onLocalisateMeClicked) {
                    checkPermission()
                    viewModel.onLocalisateMeClickedComplete()
                }
            }


        }
    }

    /**
     * It disables the localisation of the user on the map.
     */
    @SuppressLint("MissingPermission")
    private fun disableFunctionnalityGoogleMap(){
        viewModel.enableLocalisationMaps()
    }

    /**
     * It disables the Google Map's functionnality, then it gets the last location of the user and
     * sends it to the viewModel
     */
    @SuppressLint("MissingPermission")
    private fun goToCurrentLocation(){
        super.startLocationUpdates()
        disableFunctionnalityGoogleMap()
        super.fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                viewModel.goToLocalisation(location)
            }else{
                Toast.makeText(requireContext(), "Impossible de récupérer votre position", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * If location is enabled, go to current location. If location is permanently denied, show a toast.
     * If location is not enabled, ask for permission
     */
    private fun checkPermission(){
        if(PermissionUtils.isLocationEnabled(requireContext())) {
            goToCurrentLocation()
        }else if(PermissionUtils.isLocationPermanentlyDenied(requireContext(), requireActivity())) {
            Toast.makeText(requireContext(), "Location is permanently denied", Toast.LENGTH_SHORT).show()
        }else{
            enableMyLocation()
        }
    }

    /**
     * We initialise the cluster map, setup the cluster map, and then add a cluster map item
     */
    private suspend fun initMapPoints(){
        initialiseClusterMap()
        setupClusterMap()
        addClusterMapItem()
    }

    /**
     * It creates a cluster manager for the map, then creates a custom renderer for the cluster
     * manager, and finally sets the renderer for the cluster manager
     *
     * @return The return type is a LiveData<List<Bar>>.
     */
    private fun initialiseClusterMap(){
        if(viewModel.getGoogleMap() == null){
            return
        }
        val map = viewModel.getGoogleMap()!!
        // Créez un gestionnaire de regroupement pour la carte
        clusterManager = ClusterManager<BarItem>(requireContext(), map)

        // Ajoutez un renderer personnalisé pour dessiner les marqueurs regroupés
        clusterRenderer = BarClusterRender(requireContext(), map, clusterManager)
        clusterManager.renderer = clusterRenderer
    }

    /**
     * It sets up the cluster manager for the map.
     *
     * @return A boolean value.
     */
    private fun setupClusterMap(){
        if(viewModel.getGoogleMap() == null){
            return
        }
        val map = viewModel.getGoogleMap()!!
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        clusterManager.setAnimation(false);
        clusterManager.cluster()

        clusterManager.setOnClusterClickListener { cluster ->
            // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
            // inside of bounds, then animate to center of the bounds.
            val builder = LatLngBounds.builder()
            for (item in cluster.items) {
                builder.include(item.position)
            }
            val bounds = builder.build()

            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            true
        }
    }

    /**
     * We get all the bars from the database, then we check if the address is not null or blank, if
     * it's not, we convert the address to a LatLng object, then we create a BarItem object with the
     * LatLng object and the name and address of the bar, and finally we add the BarItem object to the
     * clusterManager
     */
    private suspend fun addClusterMapItem(){
        val allBar = BarRepository.getInstance().getAll()
        for(bar in allBar.value!!){
            if(!bar?.address.isNullOrBlank()){
                val latLngBar = LocationUtils.convertAdressToLatLng(bar!!.address, requireContext())
                val barItem = BarItem(LatLng(latLngBar.latitude, latLngBar.longitude), bar.name, bar.address)
                clusterManager.addItem(barItem)
            }
        }
    }
}
