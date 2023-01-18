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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobg_54018.R
import com.example.mobg_54018.databinding.FragmentLoginBinding
import com.example.mobg_54018.fragment.AuthenticationFragment
import com.google.firebase.auth.FirebaseAuth

/**
 * This class is the fragment that will be displayed when the user is not connected.
 * It will display a login form and a button to register
 */
class LoginFragment : AuthenticationFragment() {


    /**
     * It creates the view of the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Bundle?
     * @return The binding.root is being returned.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = FirebaseAuth.getInstance()

        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]


        loginViewModel.navigateToRegister.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
                loginViewModel.navigateToRegisterComplete()
            }
        })

        loginViewModel.connection.observe(viewLifecycleOwner, Observer { connection ->
            if (connection.equals("success")) {
                this.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomePageFragment())
            }else if (connection != null) {
                if(connection.isNotEmpty()){
                    Toast.makeText(context, "Mauvais email ou mot de passe", Toast.LENGTH_SHORT).show()
                }
            }
        })


        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
