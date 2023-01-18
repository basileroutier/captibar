package com.example.mobg_54018.model

import com.example.mobg_54018.dto.User


/* "This is a singleton class that holds a reference to a User object."

The class is a singleton because it has a private constructor and a companion object that returns
the same instance of the class every time it's called */
class UserService private constructor() {
    companion object {
        private var instance: UserService? = null
        fun getInstance(): UserService {
            if (instance == null) {
                instance = UserService()
            }
            return instance!!
        }

        fun getInstanceUser(): User? {
            return getInstance().user
        }
    }
    var user: User? = null

    fun loadUser(user: User) {
        instance?.user = user
    }

    fun destroyUser() {
        instance?.user = null
    }
}