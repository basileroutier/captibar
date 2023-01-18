package com.example.mobg_54018.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mobg_54018.R
import com.google.firebase.auth.FirebaseAuth

/* This class is an abstract class that extends the Fragment class. It has a property called auth that
is an instance of FirebaseAuth. It also has a method called onResume that checks if the user is
logged in. If the user is logged in, the user is navigated to the home page */
abstract class AuthenticationFragment : Fragment() {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * If the user is logged in, navigate to the home page
     */
    override fun onResume() {
        super.onResume()
        if (auth.currentUser != null) {
            findNavController().navigate(
                R.id.homePageFragment
            )
        }
    }

}