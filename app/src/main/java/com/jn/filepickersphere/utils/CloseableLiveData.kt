package com.jn.filepickersphere.utils

import androidx.lifecycle.LiveData
import java.io.Closeable

abstract class CloseableLiveData<T> : LiveData<T>, Closeable {

    constructor()

    abstract override fun close()
}