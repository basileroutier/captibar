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

package com.example.mobg_54018.fragment.homepage

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobg_54018.dto.Bar
import com.example.mobg_54018.dto.User
import com.example.mobg_54018.model.UserService
import com.example.mobg_54018.repository.BarRepository
import com.example.mobg_54018.repository.UserRepository
import com.example.mobg_54018.repository.authentication.AuthService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


/**
 * It's a class that manages the data of the home page
*/
class HomePageViewModel(val database : FirebaseAuth, application : Application) : AndroidViewModel(application) {

    private val _authService = AuthService()

    private val _conceptGame = MutableLiveData<Boolean>()
    val conceptGame : LiveData<Boolean>
        get() = _conceptGame

    private val _signoutUser = MutableLiveData<Boolean>()
    val signoutUser : LiveData<Boolean>
        get() = _signoutUser

    
    private val _navigateToDetailBar= MutableLiveData<Bar?>()
    val navigateToDetailBar : LiveData<Bar?>
        get() = _navigateToDetailBar

    private val _navigateToMap = MutableLiveData<Boolean>()
    val navigateToMap : LiveData<Boolean>
        get() = _navigateToMap

    private val _loadMoreBar = MutableLiveData<Boolean>()
    val loadMoreBar : LiveData<Boolean>
        get() = _loadMoreBar

    private val _onBarOfDayClick = MutableLiveData<Boolean>()
    val onBarOfDayClick : LiveData<Boolean>
        get() = _onBarOfDayClick

    val currentUser = MutableLiveData<User>()

    var barOfDay : Bar? = null

    init{
        viewModelScope.launch {
            barOfDay = BarRepository.getInstance().getBarOfDay()
        }
    }

    fun onBarDetailClicked(bar : Bar) {
        _navigateToDetailBar.value = bar
    }

    fun onBarDetailClickedComplete() {
        _navigateToDetailBar.value = null
    }

    fun onBarLocalisationClicked(context: Context,address: String){
        _navigateToMap.value = true
        redirectUserToGoogleMap(context,address)
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

    fun onBarLocalisationClickedComplete() {
        _navigateToMap.value = false
    }

    fun conceptGameHome(){
        _conceptGame.value = true
    }

    fun conceptGameHomeComplete(){
        _conceptGame.value = false
    }

    fun onInfoIconUserClicked(){
        disconnect()
        _signoutUser.value = true
    }

    fun onInfoIconUserClickedComplete(){
        _signoutUser.value = false
    }

    /**
     * It disconnects the user from the app.
     */
    private fun disconnect(){
        _authService.disconnect()
        UserService.getInstance().destroyUser()
    }

    fun loadMoreBar(){
        _loadMoreBar.value = true
    }

    fun loadMoreBarComplete(){
        _loadMoreBar.value = false
    }

    /**
     * It retrieves the current user from the database.
     */
    fun retrieveUser() {
        val user = UserService.getInstanceUser()
        if (user == null) {
            viewModelScope.launch {
                val user = _authService.getCurrentUser()!!.id?.let {
                    UserRepository.getInstance().get(it)
                }
                if(user!=null){
                    UserService.getInstance().loadUser(user)
                    currentUser.value = user!!
                }

            }
        }
        else {
            currentUser.value = user!!
        }
    }

    fun onBarOfDayClick(){
        if(barOfDay==null)
            return

        _onBarOfDayClick.value = true
    }

    fun onBarOfDayClickComplete(){
        _onBarOfDayClick.value = false
    }

}