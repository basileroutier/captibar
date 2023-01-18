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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mobg_54018.R
import com.example.mobg_54018.databinding.FragmentHomePageBinding
import com.example.mobg_54018.fragment.BaseFragment
import com.example.mobg_54018.fragment.popup.ConceptGame
import com.example.mobg_54018.fragment.recyclerview.BarAdapter
import com.example.mobg_54018.fragment.recyclerview.BarDetailListener
import com.example.mobg_54018.fragment.recyclerview.BarLocalisationListener
import com.example.mobg_54018.repository.BarRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * This class is the fragment that will be displayed when the user is connected.
 * It will display a list of bars and a button to display the bar of the day
*/
@SuppressWarnings
class HomePageFragment : BaseFragment() {

    private lateinit var homePageViewModel: HomePageViewModel
    private lateinit var binding: FragmentHomePageBinding
    private lateinit var barAdapter: BarAdapter

    /*
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_page, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = FirebaseAuth.getInstance()
        val viewModelFactory = HomePageViewModelFactory(dataSource, application)
        homePageViewModel =
            ViewModelProvider(
                this, viewModelFactory)[HomePageViewModel::class.java]

        homePageViewModel.retrieveUser()

        barAdapter = BarAdapter(
            BarDetailListener { bar ->
                val jsonBar = Gson().toJson(bar)
                this.findNavController().navigate(
                    HomePageFragmentDirections
                        .actionHomePageFragmentToDetailBarFragment(jsonBar))
                homePageViewModel.onBarDetailClicked(bar)
            },
            BarLocalisationListener { address ->
                homePageViewModel.onBarLocalisationClicked(requireContext(),address)
                homePageViewModel.onBarLocalisationClickedComplete()
            })

        binding.barListHomePage.adapter = barAdapter
        MainScope().launch(Dispatchers.Main) {
            val data  = withContext(Dispatchers.IO) {
                BarRepository.getInstance().getAll()
            }
            barAdapter.submitList(data.value)
        }


        homePageViewModel.navigateToDetailBar.observe(viewLifecycleOwner, Observer { bar ->
            bar?.let {
                Log.d("HomePageFragment", "barId: $bar")
                homePageViewModel.onBarDetailClickedComplete()
            }
        })
        homePageViewModel.loadMoreBar.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                /*listBarNo.addAll(
                    listOf(
                        Bar("zdez", "coucou", "test"),
                        Bar("dzdad", "coucou", "test"),
                        Bar("dezdzdad", "coucou", "test"),
                    )
                )
                listBar.value = listBarNo
                adapter.notifyItemRangeInserted(listBarNo.size - 3, 3)*/
                homePageViewModel.loadMoreBarComplete()
            }
        })

        // Display concept of the game
        homePageViewModel.conceptGame.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val showPopup = ConceptGame()
                showPopup.show(parentFragmentManager, "popup")
                homePageViewModel.conceptGameHomeComplete()
            }
        })

        // Disconnect User from Firebase
        homePageViewModel.signoutUser.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                // display popup concept game
                this.findNavController().navigate(HomePageFragmentDirections.actionHomePageFragmentToLoginFragment())
                homePageViewModel.onInfoIconUserClickedComplete()
            }
        })

        homePageViewModel.currentUser.observe(viewLifecycleOwner) {
            // TODO: on peut imaginer mettre le nom de la team quelque part
        }

        homePageViewModel.onBarOfDayClick.observe(viewLifecycleOwner){
            if(it && homePageViewModel.barOfDay != null){
                val jsonBar = Gson().toJson(homePageViewModel.barOfDay)
                this.findNavController().navigate(
                    HomePageFragmentDirections
                        .actionHomePageFragmentToDetailBarFragment(jsonBar))
                homePageViewModel.onBarOfDayClickComplete()
            }
        }

        binding.homePageViewModel = homePageViewModel
        binding.lifecycleOwner = this

        return binding.root
    }


}
