package com.jn.filepickersphere.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.google.android.material.R
import kotlin.math.roundToInt

@JvmInline
value class Color(@ColorInt val value: Int)

fun Int.asColor(): Color = Color(this)

val Color.alpha: Int
    @IntRange(from = 0, to = 255)
    get() = android.graphics.Color.alpha(value)


fun Color.withModulatedAlpha(@FloatRange(from = 0.0, to = 1.0) alphaModulation: Float): Color {
    val alpha = (alpha * alphaModulation).roundToInt()
    return ((alpha shl 24) or (value and 0x00FFFFFF)).asColor()
}

fun getColorPrimaryInverse(context: Context): Int {

    val attrs = intArrayOf(R.attr.colorPrimaryInverse)
    val typedArray = context.obtainStyledAttributes(attrs)
    val tint = typedArray.getColor(0, 0)
    typedArray.recycle()

    return tint
}