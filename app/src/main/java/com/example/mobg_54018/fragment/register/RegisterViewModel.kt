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

package com.example.mobg_54018.fragment.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobg_54018.dto.User
import com.example.mobg_54018.repository.GroupRepository
import com.example.mobg_54018.repository.authentication.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/* It's a class that extends the ViewModel class and contains the logic for the registration process */
class RegisterViewModel : ViewModel()  {

    val user = MutableLiveData<User?>()
    val confirmPassword = MutableLiveData<String?>()

    private val _registerResult = MutableLiveData<String>()
    val registerResult : LiveData<String>
        get() = _registerResult

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin : LiveData<Boolean>
        get() = _navigateToLogin

    init {
        user.value = User()
    }

    private var authService : AuthService = AuthService()


    fun navigateToLogin() {
        _navigateToLogin.value = true
    }

    fun navigateToLoginComplete() {
        _navigateToLogin.value = false
    }

    /**
     * If the user's basic information is valid and the user's password advanced information is
     * valid, then register the user
     */
    fun register() {
            val user = user.value!!
            if(checkBasicInformation(user) && checkPasswordAdvancedInformation(user)){
                registerUser(user)
            }
    }

    /**
     * It registers a user in the database.
     *
     * @param user User is the user object that we created in the previous step.
     */
    private fun registerUser(user: User) {
        GlobalScope.launch(Dispatchers.IO){
            val color = GroupRepository().getRandomTeam()
            var errorRegi : String? = ""
            if(color != null) {
                user.groupe = color.name
                authService.register(user) { error ->
                    if (error == null) {
                        _registerResult.value = "success"
                    } else {
                        _registerResult.value = error.message
                    }
                }
            } else {
                _registerResult.value = "Register failed $errorRegi"
            }
        }
    }

    /**
     * It checks if the email is valid and if the password is not empty
     *
     * @param user User is the object that we will pass to the function.
     * @return A boolean
     */
    private fun checkBasicInformation(user: User) : Boolean{
        if(user.email.isNullOrBlank() || user.email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } == false){
            _registerResult.value = "L'email n'est pas valide"
            return false
        }
        if(user.password.isNullOrBlank()){
            _registerResult.value = "Le mot de passe n'est pas valide"
            return false
        }
        return true
    }

    /**
     * It checks if the password respects the security rules and if the password and the confirmation
     * password are the same
     *
     * @param user User is the user object that we created in the previous step.
     * @return a boolean.
     */
    private fun checkPasswordAdvancedInformation(user : User) : Boolean{
        if(!user.respectPasswordSecurity()){
            _registerResult.value = "Le mot de passe doit contenir au moins 6 caractères"
            return false
        }
        if(!user.confirmPassword(confirmPassword.value)){
            _registerResult.value = "Les mots de passe ne correspondent pas"
            return false
        }
        return true
    }

}