package com.jn.filepickersphere.filepicker.style

import androidx.annotation.StyleRes

/**
 * Defines the styling options for the FilePickerSphere library.
 *
 * @param appTheme The resource ID of the application theme. Set to `null` to use the default FilePicker theme.
 * @param fileItemStyle The styling options for individual file items, such as file icon background and opacity.
 */
data class FilePickerStyle(
    @StyleRes val appTheme: Int? = null,
    val fileItemStyle: FileItemStyle = FileItemStyle()
)
