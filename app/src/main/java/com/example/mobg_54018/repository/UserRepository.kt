package com.example.mobg_54018.repository

import androidx.lifecycle.LiveData
import com.example.mobg_54018.dto.User
import com.example.mobg_54018.repository.exception.RepositoryException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class UserRepository : Repository<String, User?> {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val users: MutableList<User> = ArrayList()

    companion object{
        const val dbName = "users"

        private var instance: UserRepository? = null
        fun getInstance(): UserRepository {
            if (instance == null) {
                instance = UserRepository()
            }
            return instance!!
        }
    }

    @Throws(RepositoryException::class)
    override fun add(item: User?, onResult: (Throwable?) -> Unit) {
        if (item?.id != null) {
            store.collection(dbName).document(item.id!!).set(item.hashMap()).addOnSuccessListener {
                onResult(null)
            }.addOnFailureListener {
                onResult(it)
            }
        }
    }

    @Throws(RepositoryException::class)
    override fun remove(key: String) {
        throw RepositoryException("Not implemented")
    }

    @Throws(RepositoryException::class)
    override suspend fun get(key: String): User? {
        return suspendCancellableCoroutine { continuation ->
            store.collection(dbName).document(key).get().addOnSuccessListener { result ->
                val user = result.toObject(User::class.java)
                user?.id = result.id
                continuation.resume(user!!) {}
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun getAll(): LiveData<List<User?>>{
        throw RepositoryException("Not implemented")
    }

    @Throws(RepositoryException::class)
    override operator fun contains(key: String): Boolean {
        throw RepositoryException("Not implemented")
    }
}