package com.example.mobg_54018.repository.authentication

import com.example.mobg_54018.dto.User

/* This is an interface that defines the methods that an AccountService must implement. */
interface AccountService {

    /**
     * This function takes a User and a function that takes a Throwable and returns Unit, and returns
     * Unit.
     *
     * @param user The user object that you want to authenticate.
     * @param onResult A callback function that will be called when the authentication is complete. The
     * callback function will be passed a Throwable object. If the authentication was successful, the
     * Throwable object will be null. If the authentication failed, the Throwable object will contain
     * the error message.
     */
    fun authenticate(user : User, onResult: (Throwable?) -> Unit)

    /**
     * This function takes a User object and a function that takes a Throwable and returns Unit.
     *
     * @param user The user object that you want to register.
     * @param onResult A callback function that will be called when the request is completed.
     */
    fun register(user : User, onResult: (Throwable?) -> Unit)

    /**
     * Disconnects the client from the server.
     */
    fun disconnect()

    /**
     * This function returns a User object or null.
     */
    fun getCurrentUser() : User?
}