package com.jn.filepickersphere.filepicker.style

import com.google.android.material.shape.ShapeAppearanceModel
import com.jn.filepickersphere.utils.FileItemStyleDefaults

/**
 * Defines the styling options for an individual file item
 *
 *
 * @param fileIconBackground The background shape appearance model for the file icon.
 * @param autoApplyIconBackgroundTint Flag indicating whether to automatically apply tint to the file icon background.
 * @param fileSelectedOpacity The opacity of the file item when it is selected.
 */
data class FileItemStyle(
    val fileIconBackground: ShapeAppearanceModel = FileItemStyleDefaults.fileIconBackground,
    val autoApplyIconBackgroundTint: Boolean = true,
    val fileSelectedOpacity: Float = FileItemStyleDefaults.fileSelectedOpacity
)
