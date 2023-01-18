package com.example.mobg_54018.util

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton

@BindingAdapter("buttonColor")
/**
 * Set the background color of a MaterialButton to a color resource.
 *
 * @param button MaterialButton - The button to change the color of
 * @param color The color to set the button to.
 */
fun setButtonColor(button: MaterialButton, @ColorRes color: Int) {
    button.setBackgroundColor(ContextCompat.getColor(button.context, color))
}