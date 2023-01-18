package com.example.mobg_54018.dto

/**
 * Data Transfer Object for a Bar.
 */
data class Bar(var id : String="",var name: String="", var address : String="", var description : String = "", var captureNumbers : Int = 0) : Dto<String>(id)
