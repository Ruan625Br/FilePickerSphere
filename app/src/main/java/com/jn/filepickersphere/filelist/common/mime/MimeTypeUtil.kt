package com.jn.filepickersphere.filelist.common.mime

import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File

object MimeTypeUtil {

    fun getMimeType(uri: Uri? = null, mPath: String? = null): String? {
        val path = if (uri == null) mPath else uri.path
        val default = "vnd.android.document/directory"
        val file = File(path!!)
        if (file.isDirectory) return default
        val lastDotIndex = path.lastIndexOf(".")
        if (lastDotIndex != -1) {
            val extension = path.substring(lastDotIndex + 1)
            val mimeTypeMap = MimeTypeMap.getSingleton()
            return mimeTypeMap.getMimeTypeFromExtension(extension.lowercase())
        }
        return default
    }

    fun getIconIdByMimeType(mimeType: String): Int {
        val mimeTypeObj = MimeType(mimeType)
        val icon: MimeTypeIcon = mimeTypeObj.icon

        return icon.resourceId

    }

}