package com.jn.filepickersphere.models

import android.os.Parcelable
import androidx.annotation.WorkerThread
import com.jn.filepickersphere.utils.ParcelableParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.io.File
import java.nio.file.Path
import java.util.UUID
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlin.io.path.isDirectory
import kotlin.io.path.isHidden
import kotlin.io.path.name
import kotlin.io.path.pathString

/**
 * Represents a model for a file or directory, providing essential information about it.
 *
 * @property name The name of the file or directory.
 * @property path The path of the file or directory, serialized using [ParcelableParceler].
 * @property isDirectory Indicates whether the item is a directory.
 * @property fileExtension The extension of the file, empty for directories.
 * @property fileSize The size of the file in bytes. Zero for directories.
 * @property file The [java.io.File] representation of the file or directory.
 * @property isHidden Indicates whether the file or directory is hidden.
 *
 * @author Juan Nascimento (Ruan626Br)
 */
@Parcelize
data class FileModel(
    val name: String,
    val path: @WriteWith<ParcelableParceler> String,
    val isDirectory: Boolean,
    val fileExtension: String,
    val fileSize: Long,
    val file: File,
    val isHidden: Boolean

) : Parcelable

/**
 * Loads a [FileModel] from the current [Path]. This function should be called from a background thread
 * (e.g., using [WorkerThread]) as it involves file I/O operations.
 *
 * @return A [FileModel] representing the current file or directory.
 * @author Juan Nascimento (Ruan626Br)
 */
@WorkerThread
fun Path.loadFileModel(): FileModel {
    val file = toFile()
    val fileName = name
    val filePath = pathString
    val isDirectory = isDirectory()
    val fileExtension = extension
    val fileSize = fileSize()
    val isHidden = isHidden()

    return FileModel(fileName, filePath, isDirectory, fileExtension, fileSize, file, isHidden)
}