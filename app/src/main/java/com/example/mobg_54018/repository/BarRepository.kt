package com.example.mobg_54018.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobg_54018.dto.Bar
import com.example.mobg_54018.model.UserService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resumeWithException

class BarRepository : Repository<String, Bar?>  {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()

    // object name bar
    companion object{
        const val dbName = "bars"
        const val dbBarDay = "barOfDay"
        const val dbBarCapture = "barCapture"

        private var instance: BarRepository? = null
        fun getInstance(): BarRepository {
            if (instance == null) {
                instance = BarRepository()
            }
            return instance!!
        }
    }

    /**
     * Add a bar to captured bar list
     * @return onResult : null if the bar is added to the list, exception otherwise
     */
    override fun add(item: Bar?, onResult: (Throwable?) -> Unit) {
        if(item != null){
            val mapBarCapture = hashMapBarCapture(item)
            store.collection(dbBarCapture).add(mapBarCapture).addOnSuccessListener {
                Log.d("BarRepository", "Bar added to the list ${item.captureNumbers}")
                item.captureNumbers++
                Log.d("BarRepository", "Bar added to the list ${item.captureNumbers}")

                store.collection(dbName).document(item.id).update("captureNumbers", item.captureNumbers).addOnSuccessListener {
                    onResult(null)
                }.addOnFailureListener { e ->
                    onResult(e)
                }
            }.addOnFailureListener {
                onResult(it)
            }
        }
    }

    override fun remove(key: String) {
        throw NotImplementedError()
    }

    override suspend fun get(key: String): Bar? {
        return suspendCancellableCoroutine { continuation ->
            var bar : Bar? = null
            store.collection(dbName).document(key).get().addOnSuccessListener { result ->
                bar = result.toObject(Bar::class.java)
                bar?.id = result.id
                continuation.resume(bar){}
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun getAll(): LiveData<List<Bar?>>{
        return suspendCancellableCoroutine { continuation ->
            val dataList = mutableListOf<Bar>()
            val query = store.collection(dbName).get()
            query.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val bar = document.toObject(Bar::class.java)
                        bar.id = document.id
                        dataList.add(bar)
                    }
                    continuation.resume(MutableLiveData(dataList)) {}
                } else {
                    continuation.resumeWithException(task.exception!!)
                }
            }
        }
    }

    /**
     * > This function checks if the current hour has already been captured for the given key
     *
     * @param key The key of the bar you want to check
     * @return A boolean value that indicates if the current hour has been captured or not.
     */
    suspend fun isAlreadyCapturedCurrentHour(key : String) : Boolean{
        return suspendCancellableCoroutine { continuation ->
            val todayString = getCurrentDateTime()
            store.collection(dbBarCapture).whereEqualTo("id", key).whereEqualTo("date", todayString).get().addOnSuccessListener{ result ->
                if(result != null) {
                    continuation.resume(!result.isEmpty){}
                }else{
                    continuation.resume(false){}
                }
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
    }

    override fun contains(key: String): Boolean {
        throw NotImplementedError()
    }

    /**
     * > This function returns a `Bar` object from the database, but it's a `suspend` function, so it
     * can be called from a `suspend` function
     *
     * @return A Bar object
     */
    suspend fun getBarOfDay() : Bar? {
        val idBar = suspendCancellableCoroutine { continuation ->
            store.collection(dbBarDay).get().addOnSuccessListener { result ->
                val barDay = result.documents[0].get("id") as String
                continuation.resume(barDay){}
            }.addOnFailureListener{
                continuation.resumeWithException(it)
            }
        }
        return get(idBar)
    }

    /**
     * It takes a bar as a parameter, and returns a hashmap with the bar's id, the user's team, and the
     * current date
     *
     * @param bar Bar is the bar object that we want to capture
     * @return A HashMap with the bar id, the team name and the current date.
     */
    private fun hashMapBarCapture(bar : Bar) : HashMap<String, String> {
        if(UserService.getInstanceUser()?.groupe.isNullOrBlank() || bar.id.isNullOrBlank()){
            throw Throwable("Team or bar is empty")
        } else {
            return hashMapOf(
                "id" to bar.id,
                "team" to UserService.getInstanceUser()!!.groupe!!,
                "date" to getCurrentDateTime()
            )
        }
    }

    /**
     * It returns the current date and time in the format yyyy-MM-dd : HH
     *
     * @return A string of the current date and time.
     */
    private fun getCurrentDateTime() : String {
        val today = Calendar.getInstance().time
        return SimpleDateFormat("yyyy-MM-dd : HH").format(today)
    }
}