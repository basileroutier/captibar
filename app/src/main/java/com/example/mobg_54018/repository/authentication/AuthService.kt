package com.example.mobg_54018.repository.authentication

import android.util.Log
import com.example.mobg_54018.dto.User
import com.example.mobg_54018.model.UserService
import com.example.mobg_54018.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AuthService : AccountService {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun authenticate(user : User, onResult: (Throwable?) -> Unit) {
        if(user.email.isNullOrBlank() || user.password.isNullOrBlank()){
            onResult(Throwable("Email or password is empty"))
        } else {
            auth.signInWithEmailAndPassword(user.email!!, user.password!!)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        user.id = auth.currentUser?.uid
                        MainScope().launch (Dispatchers.Main){
                            user.groupe = UserRepository.getInstance().get(user.id!!)?.groupe
                            UserService.getInstance().loadUser(user)
                            onResult(null)
                        }
                    }else{
                        onResult(Throwable("Authentication failed"))
                    }
                }
        }
    }

    override fun register(user : User, onResult: (Throwable?) -> Unit) {
        if(user.email.isNullOrBlank() || user.password.isNullOrBlank()){
            onResult(Throwable("Email or password is empty"))
        } else {
            Log.d("AuthService", "Registering user ${user.email}")
            auth.createUserWithEmailAndPassword(user.email!!, user.password!!)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        user.id = auth.currentUser?.uid
                        createAccount(user) { error ->
                            if(error == null){
                                UserService.getInstance().loadUser(user)
                                onResult(null)
                            } else {
                                onResult(error)
                            }
                        }
                    } else {
                        onResult(it.exception)
                    }
                }
        }
    }

    override fun disconnect() {
        UserService.getInstance().destroyUser()
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        return auth.currentUser?.let {
            return User(it.uid, it.email)
        }
    }

    private fun createAccount(user: User, onResult: (Throwable?) -> Unit){
        UserRepository.getInstance().add(user) { error ->
            if(error== null){
                onResult(null)
            }else{
                onResult(error)
            }
        }
    }


}
