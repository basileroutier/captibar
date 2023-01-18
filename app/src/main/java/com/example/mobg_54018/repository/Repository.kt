package com.example.mobg_54018.repository

import androidx.lifecycle.LiveData
import com.example.mobg_54018.dto.Dto
import com.example.mobg_54018.repository.exception.RepositoryException


/* "This is an interface that defines a repository that can store and retrieve objects of type T that
have a key of type K."

The first thing to notice is that the interface is generic. It defines two type parameters, K and T.
K is the type of the key that is used to identify the object. T is the type of the object that is
stored in the repository */
interface Repository<K, T : Dto<K>?> {

    /**
     * This function adds an item to the repository
     *
     * @param item The item to be added to the repository.
     * @param onResult A callback function that will be called when the operation is complete.
     */
    @Throws(RepositoryException::class)
    fun add(item: T, onResult: (Throwable?) -> Unit)

    /**
     * Removes the mapping for the specified key from this map if present
     *
     * @param key The key of the item to remove.
     */
    @Throws(RepositoryException::class)
    fun remove(key: K)

    /**
     * `get` returns a `T` value, and throws a `RepositoryException` if it fails
     *
     * @param key The key of the item to get
     */
    @Throws(RepositoryException::class)
    suspend fun get(key: K): T

    /**
     * This function returns a LiveData object that contains a list of all the objects in the database
     */
    @Throws(RepositoryException::class)
    suspend fun getAll(): LiveData<List<T>>

    /**
     * It checks if the key is present in the map.
     *
     * @param key The key to check for.
     */
    @Throws(RepositoryException::class)
    operator fun contains(key: K): Boolean
}
