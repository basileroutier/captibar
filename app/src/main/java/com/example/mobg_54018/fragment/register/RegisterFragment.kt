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
import com.example.mobg_54018.databinding.FragmentRegisterLoginBinding
import com.example.mobg_54018.fragment.AuthenticationFragment
import com.google.firebase.auth.FirebaseAuth


/* It's a fragment that displays a form to register a user */
class RegisterFragment : AuthenticationFragment() {


    /**
     * We create a binding object, we create a view model, we observe the view model's registerResult
     * and navigateToLogin, and we return the binding object
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container The parent that this fragment's UI should be attached to.
     * @param savedInstanceState Bundle?
     * @return The root of the binding object.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRegisterLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register_login, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = FirebaseAuth.getInstance()
        val registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]


        registerViewModel.navigateToLogin.observe(viewLifecycleOwner, Observer { goBack ->
            if(goBack){

                findNavController().popBackStack(R.id.loginFragment, false)
               // findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                registerViewModel.navigateToLoginComplete()
            }
        })

        registerViewModel.registerResult.observe(viewLifecycleOwner, Observer { result ->
            result.let {
                if(it == "success"){
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                }else {
                    Toast.makeText(context, "Problèmes : $it", Toast.LENGTH_SHORT).show()
                }
            }
        })


        binding.registerViewModel = registerViewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
