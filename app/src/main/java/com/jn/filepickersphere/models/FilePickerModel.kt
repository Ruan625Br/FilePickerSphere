package com.jn.filepickersphere.models

import android.os.Parcelable
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import kotlinx.parcelize.Parcelize

/**
 * Represents the configuration and callbacks for a file picker.
 *
 * @param pickOptions Options for file picking.
 */
@Parcelize
data class FilePickerModel(
    val pickOptions: PickOptions,
) : Parcelable
