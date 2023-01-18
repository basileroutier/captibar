package com.example.mobg_54018.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobg_54018.dto.Colors
import com.example.mobg_54018.repository.exception.RepositoryException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


class GroupRepository : Repository<String, Colors?>{

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val groups: ArrayList<Colors?> = ArrayList()

    private companion object{
        const val dbName = "teams"
        private var instance: GroupRepository? = null
        fun getInstance(): GroupRepository {
            if (instance == null) {
                instance = GroupRepository()
            }
            return instance!!
        }
    }

    override fun add(item: Colors?, onResult: (Throwable?) -> Unit) {
        onResult(RepositoryException("Not implemented"))
    }

    override fun remove(key: String) {
        TODO("Not yet implemented")
    }

    override suspend fun get(key: String): Colors? {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): LiveData<List<Colors?>>{
        return suspendCancellableCoroutine { continuation ->
            val dataList = mutableListOf<Colors>()
            val query = FirebaseFirestore.getInstance().collection(dbName).get()
            query.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        dataList.add(document.toObject(Colors::class.java))
                    }
                    continuation.resume(MutableLiveData(dataList)) {}
                } else {
                    continuation.resumeWithException(task.exception!!)
                }
            }
        }
    }

    override fun contains(key: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * > This function returns a random team from the list of teams
     *
     * @return A random team from the database
     */
    suspend fun getRandomTeam() : Colors?{
        val teams = this.getAll().value
        val random = (0 until teams?.size!!).random()
        return teams[random]
    }




}