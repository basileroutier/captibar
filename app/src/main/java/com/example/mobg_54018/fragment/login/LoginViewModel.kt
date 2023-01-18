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

package com.example.mobg_54018.fragment.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobg_54018.dto.User
import com.example.mobg_54018.repository.authentication.AuthService
import kotlinx.coroutines.launch

/**
 * It's a class that manages the data of the login page
 */
class LoginViewModel : ViewModel() {

    val user = MutableLiveData<User?>()

    private val _navigateToRegister = MutableLiveData<Boolean>()
    val navigateToRegister : LiveData<Boolean>
        get() = _navigateToRegister

    private val _connection = MutableLiveData<String?>()

    val connection : LiveData<String?>
        get() = _connection

    init {
        user.value = User()
    }
    private var authService : AuthService = AuthService()

    /**
     * The function launches a coroutine in the viewModelScope, which is a scope that is tied to the
     * lifecycle of the viewModel. The coroutine then calls the authenticate function of the
     * authService, which is a suspend function. The authenticate function takes a callback as a
     * parameter, which is called when the authentication is complete. The callback takes an error as a
     * parameter, which is null if the authentication was successful. If the error is null, the
     * connection value is set to "success", otherwise it is set to the error message
     */
    fun login() {
        viewModelScope.launch {
            if(user.value != null) {
                authService.authenticate(user.value!!) {
                    error ->
                    if(error == null) {
                        _connection.value = "success"
                    } else {
                        _connection.value = error.message
                    }
                }
            }
        }
    }

    fun navigateToRegister(){
        _navigateToRegister.value = true
    }

    fun navigateToRegisterComplete(){
        _navigateToRegister.value = false
    }

}