package com.jn.filepickersphere.models

import android.os.Parcelable
import com.jn.filepickersphere.filelist.common.mime.MimeType
import com.jn.filepickersphere.utils.Constants
import kotlinx.parcelize.Parcelize


/**
 * Represents options for file picking, specifying the criteria for selecting files or directories.
 *
 * @param mimeType The MIME type(s) associated with the files to be selected.
 *                 It can be a single MIME type or a list of MIME types.
 * @param localOnly Indicates whether the selection is limited to local files only.
 * @param rootPath The root path for file picking. Default is [Constants.DEFAULT_PATH].
 * @param maxSelection The maximum number of files that can be selected. Set to `null` for unlimited selection.
 *
 * @see MimeType Represents the MIME type(s) associated with the files.
 */
@Parcelize
data class PickOptions(
    val mimeType: List<MimeType>,
    val localOnly: Boolean,
    val rootPath: String = Constants.DEFAULT_PATH,
    val maxSelection: Int? = null,
) : Parcelable
