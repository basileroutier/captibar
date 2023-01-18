package com.example.mobg_54018.dto

data class User (var id: String? = "",var email: String? = "", var password: String? = "", var groupe : String? = "") : Dto<String>(email) {

    /**
     * Check password security with min 6 length and not blank or empty
     */
    fun respectPasswordSecurity() : Boolean {
        return password.isNullOrBlank().not() && password!!.length > 6
    }

    /**
     * Check if "confirmPassword" is equals to "password"
     * Return true if equals
     */
    fun confirmPassword(password: String?) : Boolean {
        if(password != null){
            return this.password == password
        }
        return false
    }


    /**
     * It returns a HashMap with the email and groupe keys for the FirebaseStore
     *
     * @return A HashMap
     */
    fun hashMap() : HashMap<String, String> {
        if(email.isNullOrBlank() || password.isNullOrBlank()){
            throw Throwable("Email or password is empty")
        } else {
            return hashMapOf(
                "email" to email!!,
                "groupe" to groupe!!
            )
        }
    }

}