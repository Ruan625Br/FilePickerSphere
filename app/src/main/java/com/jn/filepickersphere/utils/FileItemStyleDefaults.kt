package com.jn.filepickersphere.utils

import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.jn.filepickersphere.filepicker.style.FileItemStyle

/**
 * Default values for [FileItemStyle].
 */
object FileItemStyleDefaults {

    val fileIconBackground = ShapeAppearanceModel.builder()
        .setAllCorners(CornerFamily.ROUNDED, 30f)
        .build()
    const val fileSelectedOpacity: Float = 0.28f
}