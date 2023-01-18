package com.example.mobg_54018.model

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import android.widget.Toast
import com.example.mobg_54018.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.error_connectivity_internet.view.*

/* It's a singleton class that extends BroadcastReceiver and listens to the connectivity changes */
class ConnectivityReceiver private constructor(val root: ActivityMainBinding) : BroadcastReceiver() {

        var isConnected: Boolean = false

        companion object{
            private var instance: ConnectivityReceiver? = null
            fun getInstance(): ConnectivityReceiver {
                if (instance == null) {
                    throw IllegalStateException("ConnectivityReceiver is not initialized")
                }
                return instance!!
            }

            fun loadInstance(root: ActivityMainBinding) : ConnectivityReceiver{
                instance = ConnectivityReceiver(root)
                return instance!!
            }
        }

        /**
         * It checks if the device is connected to the internet or not.
         *
         * @param context Context - The context in which the receiver is running.
         * @param intent The Intent to watch for.
         */
        @SuppressLint("ServiceCast", "MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            this.isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            changeVisibility()
        }


    /**
     * If the device is connected to the internet, hide the error layout, otherwise show it
     */
    private fun changeVisibility() {
        if (isConnected) {
            root.root.error_layout.visibility = View.GONE
        } else {
            root.root.error_layout.visibility = View.VISIBLE
        }
    }
}