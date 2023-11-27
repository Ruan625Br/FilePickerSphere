package com.jn.filepickersphere.viewmodels

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.jn.filepickersphere.filelist.FileItemSet
import com.jn.filepickersphere.filelist.common.trail.TrailLiveData
import com.jn.filepickersphere.filelist.fileItemSetOf
import com.jn.filepickersphere.livedata.FileListMapLiveData
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.models.PickOptions
import com.jn.filepickersphere.utils.CloseableLiveData
import com.jn.filepickersphere.utils.Stateful
import java.io.Closeable
import java.nio.file.Path

class FileListViewModel : ViewModel() {

    private val trailLiveData = TrailLiveData()
    val pendingState: Parcelable?
        get() = trailLiveData.value?.pendingSate

    private val _selectedFilesLiveData = MutableLiveData(fileItemSetOf())
    val selectedFilesLiveData: LiveData<FileItemSet>
        get() = _selectedFilesLiveData

    val selectedFiles: FileItemSet
        get() = _selectedFilesLiveData.value!!

    private val _pickOptionsLiveData = MutableLiveData<PickOptions?>()
    val pickOptionsLiveData: LiveData<PickOptions?>
        get() = _pickOptionsLiveData

    var pickOptions: PickOptions?
        get() = _pickOptionsLiveData.value
        set(value) {
            _pickOptionsLiveData.value = value
        }

    val currentPathLiveData = trailLiveData.map { it.currentPath }
    val currentPath: Path?
        get() = currentPathLiveData.value

    fun selectFile(file: FileModel, selected: Boolean) {
        selectFiles(fileItemSetOf(file), selected)
    }

    fun selectFiles(files: FileItemSet, selected: Boolean) {
        val selectedFiles = _selectedFilesLiveData.value
        if (selectedFiles === files) {
            if (!selected && selectedFiles.isNotEmpty()) {
                selectedFiles.clear()
            }
            return
        }
        var changed = false
        for (file in files) {
            changed = changed or if (selected) {
                selectedFiles!!.add(file)
            } else {
                selectedFiles!!.remove(file)
            }
        }
        if (changed) {
            _selectedFilesLiveData.postValue(selectedFiles)
        }
    }

    fun clearSelectedFiles() {
        val selectedFiles = _selectedFilesLiveData.value
        if (selectedFiles!!.isEmpty()) return

        selectedFiles.clear()
        _selectedFilesLiveData.postValue(selectedFiles)
    }

    fun navigateTo(lastState: Parcelable, path: Path) = trailLiveData.navigateTo(lastState, path)

    fun resetTo(path: Path) = trailLiveData.resetTo(path)

    fun navigateUp(): Boolean = trailLiveData.navigateUp()


    private val _fileListLiveData = FileListMapLiveData(currentPathLiveData)

    val fileListLiveData: LiveData<Stateful<List<FileModel>>>
        get() = _fileListLiveData
    val fileListStateful: Stateful<List<FileModel>>
        get() = _fileListLiveData.value!!

    fun reload() {
        _fileListLiveData.reload()
    }


}