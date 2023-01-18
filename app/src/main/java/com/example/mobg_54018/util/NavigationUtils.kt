package com.example.mobg_54018.util

import com.example.mobg_54018.R

class Navigation private constructor(){

    companion object{
        private var instance: Navigation? = null

        fun getInstance(): Navigation {
            if (instance == null) {
                instance = Navigation()
            }
            return instance!!
        }

    }

    fun navigateTo(destination: Int) {
    }
}