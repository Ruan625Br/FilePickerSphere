package com.jn.filepickersphere.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.utils.CloseableLiveData
import com.jn.filepickersphere.utils.Stateful
import java.io.Closeable
import java.nio.file.Path

class FileListMapLiveData(
    private val pathLiveData: LiveData<Path>,
) : MediatorLiveData<Stateful<List<FileModel>>>(), Closeable {
    private var liveData: CloseableLiveData<Stateful<List<FileModel>>>? = null

    init {
        addSource(pathLiveData) { updateSource() }
    }


    private fun updateSource() {
        liveData?.let {
            removeSource(it)
            it.close()
        }

        val liveData = FileListLiveData(pathLiveData.value!!)
        this.liveData = liveData
        addSource(liveData) { value = it }
    }

    fun reload() {
        (liveData as? FileListLiveData)?.loadValue()
    }

    override fun close() {
        liveData?.let {
            removeSource(it)
            it.close()
            this.liveData = null
        }
    }

}