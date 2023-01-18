package com.example.mobg_54018

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.mobg_54018.databinding.ActivityMainBinding
import com.example.mobg_54018.model.ConnectivityReceiver
import com.example.mobg_54018.model.UserService
import com.example.mobg_54018.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    /**
     * A function that is called when the activity is created.
     *
     * @param savedInstanceState The Bundle that contains the data it most recently supplied in
     * onSaveInstanceState(Bundle) if activity was reinitialized after being previously shut down.
     * Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        getCurrentUserConnected()
        val navController = this.findNavController(R.id.myNavHostFragment)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        displayBottomNavigationOnBaseFragment(navController)
        showNoInternetConnection()

    }


    /**
     * If the destination is the login or register fragment, hide the bottom navigation, otherwise show
     * it.
     *
     * @param navController The NavController that will be used to navigate to the destination.
     */
    private fun displayBottomNavigationOnBaseFragment(navController: NavController) {
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }



    /**
     * It registers a broadcast receiver to listen for network changes.
     */
    private fun showNoInternetConnection() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val receiver = ConnectivityReceiver.loadInstance(binding)
        registerReceiver(receiver, filter)
    }

    /**
     * It gets the current user connected.
     *
     * @return The current user connected
     */
    private fun getCurrentUserConnected(){
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            return
        }

        val userAuth = UserService.getInstanceUser()
        if(userAuth == null){
            MainScope().launch (Dispatchers.Main){
                UserRepository.getInstance().get(auth.currentUser!!.uid)?.let {
                    UserService.getInstance().loadUser(it)
                }
            }
        }
    }

}


