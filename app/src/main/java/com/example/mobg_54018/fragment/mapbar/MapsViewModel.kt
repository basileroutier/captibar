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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobg_54018.model.Maps
import com.example.mobg_54018.model.MapsService


/**
 * It's a ViewModel that implements the Maps interface, and it has a LiveData that is set to true when
 * the user clicks on the localisate me button
*/

class MapsViewModel(mapsService : MapsService = MapsService()) : ViewModel() , Maps by mapsService  {

    private val _onLocalisateMeClicked : MutableLiveData<Boolean> = MutableLiveData()
    val onLocalisateMeClicked : LiveData<Boolean>
        get() = _onLocalisateMeClicked



    fun onLocalisateMeClicked() {
        _onLocalisateMeClicked.value = true
    }

    fun onLocalisateMeClickedComplete() {
        _onLocalisateMeClicked.value = false
    }


}