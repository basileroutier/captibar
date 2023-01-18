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

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobg_54018.R
import com.example.mobg_54018.dto.Bar
import com.example.mobg_54018.model.BarService
import com.example.mobg_54018.model.Maps
import com.example.mobg_54018.model.MapsService
import com.example.mobg_54018.repository.BarRepository
import com.example.mobg_54018.repository.exception.RepositoryException
import com.example.mobg_54018.util.LocationUtils

/**
 * This class is used to display the detail of a bar.
 * It contains a map with the location of the bar
 * and a button to capture the bar
 */
class DetailBarViewModel(private val datasource : Bar, private val barServ : BarService, application : Application, mapsService: MapsService = MapsService()) : AndroidViewModel(application), Maps by mapsService {

    private val _bar : MutableLiveData<Bar> = MutableLiveData()
    val bar : LiveData<Bar>
        get() = _bar

    private val _navigateToHomePage = MutableLiveData<Boolean>()
    val navigateToHomePage : LiveData<Boolean>
        get() = _navigateToHomePage


    private val _barService = MutableLiveData<BarService>()
    val barService : LiveData<BarService>
        get() = _barService

    private val _capturedBar = MutableLiveData<Boolean>()
    val capturedBar : LiveData<Boolean>
        get() = _capturedBar

    private val _alreadyCapturedBar = MutableLiveData<Boolean>()
    val alreadyCapturedBar : LiveData<Boolean>
        get() = _alreadyCapturedBar

    private val _numberOfCaptures = MutableLiveData<String>()
    val numberOfCaptures : LiveData<String>
        get() = _numberOfCaptures

    private val _onMapClick = MutableLiveData<Boolean>()
    val onMapClick : LiveData<Boolean>
        get() = _onMapClick

    var hasBeenCapturedByUser = false

    @ColorRes
    var colorCapturedButton: MutableLiveData<Int> = MutableLiveData(R.color.disabledButtonColor)

    init {
        _bar.value = datasource
        _barService.value = barServ
        _numberOfCaptures.value = bar.value?.captureNumbers.toString()
    }


    fun onNavigateToHomePage() {
        _navigateToHomePage.value = true
    }

    fun onNavigateToHomePageComplete() {
        _navigateToHomePage.value = false
    }

    /**
     * It changes the color of the button depending on the state of the bar.
     */
    fun changeColorButton(){
        if(barServ.alreadyCaptured.value == false){
            if(barServ.canBeCaptured.value == true){
                colorCapturedButton.value = R.color.Yellow80OpaChartColor
            }else{
                colorCapturedButton.value = R.color.disabledButtonColor
            }
        }
    }


    fun changeColorButton(color : Int){
        colorCapturedButton.value = color
    }

    /**
     * If the bar can be captured, set the bar as captured, add the bar to the list of captured bars,
     * and set the captured bar to true
     */
    fun captureBar(){
        if(barServ.canBeCaptured.value == true) {
            hasBeenCapturedByUser = true
            barServ.canBeCaptured.value = false
            changeColorButton()
            barServ.alreadyCaptured.value = true
            _capturedBar.value = true
            addBarToCaptured()
        }
    }

    /**
     * If the bar is not null, add it to the repository
     */
    private fun addBarToCaptured(){
        if(bar != null){
            BarRepository.getInstance().add(bar.value!!){ result ->
                if(result!=null){
                    throw RepositoryException("Error while adding bar to captured ${result.message}")
                }else{
                    _numberOfCaptures.value = bar.value?.captureNumbers.toString()
                }
            }
        }
    }

    fun captureBarComplete(){
        _capturedBar.value = false
    }

    /**
     * This function takes a context as a parameter and uses it to convert the address of the bar to
     * a location. If the location is not null, it calls the goToLocalisation function with the
     * location and false as parameters
     *
     * @param context The context of the activity
     */
    fun goToBarLocalisation(context : Context){
        val location = bar.value?.let { LocationUtils.convertAddressToLocation(it.address, context) }
        if (location != null) {
            goToLocalisation(location, false)
        }
    }

    fun onMapClick(){
        _onMapClick.value = true
    }


    fun onMapClickComplete(){
        _onMapClick.value = false
    }

    fun goToBarGoogleMap(context : Context){
        bar.value?.address?.let { redirectUserToGoogleMap(context, it) }
    }


    /**
     * It takes a context and an address as parameters, creates a Uri object with the address, creates
     * an intent with the Uri object, and starts the activity
     *
     * @param context Context - The context of the activity that is calling the function.
     * @param address The address you want to redirect the user to.
     */
    private fun redirectUserToGoogleMap(context: Context, address : String){
        val geoUri = Uri.parse("geo:0,0?q=$address")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        context.startActivity(mapIntent)
    }
}