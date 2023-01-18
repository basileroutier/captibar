package com.example.mobg_54018.dto

import java.util.*

open class Dto<K> protected constructor(key: K?) {
    var key: K
        protected set

    /**
     * Creates a new instance of `Dto` with the key of the data.
     *
     * @param key key of the data.
     */
    init {
        requireNotNull(key) { "Clé absente $key" }
        this.key = key
    }

    override fun hashCode(): Int {
        var hash = 7
        hash = 53 * hash + Objects.hashCode(key)
        return hash
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as Dto<*>
        return Objects.equals(key, other.key)
    }
}
