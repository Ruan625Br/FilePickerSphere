package com.jn.filepickersphere.filelist

import com.jn.filepickersphere.models.FileModel

interface FileListener {

    fun clearSelectedFiles()

    fun selectFile(file: FileModel, selected: Boolean)

    fun selectFiles(files: FileItemSet, selected: Boolean)

    fun openFile(file: FileModel)

}