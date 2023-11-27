package com.jn.filepickersphere.filepicker

import com.jn.filepickersphere.models.FileModel


/**
 * Contains callbacks for file picking interactions.
 */
interface FilePickerCallbacks {

    /**
     * Called when the selection state of a file changes.
     *
     * @param file The file for which the selection state changed.
     * @param selected Indicates whether the file is now selected.
     */
    fun onFileSelectionChanged(file: FileModel, selected: Boolean)

    /**
     * Called when a file is opened.
     *
     * @param file The file that was opened.
     */
    fun onOpenFile(file: FileModel)

    /**
     * Called when the set of selected files changes.
     *
     * @param files The list of currently selected files.
     */
    fun onSelectedFilesChanged(files: List<FileModel>)

    /**
     * Called when all necessary files have been selected.
     *
     * @param files The list of all selected files.
     */
    fun onAllFilesSelected(files: List<FileModel>)
}
