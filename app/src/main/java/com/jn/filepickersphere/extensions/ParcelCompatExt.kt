package com.jn.filepickersphere.extensions

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

fun <E : Parcelable?, L : MutableList<E>> Parcel.readParcelableListCompat(
    list: L,
    classLoader: ClassLoader?
): L {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        return readParcelableList(list, classLoader) as L
    } else {
        val size = readInt()
        if (size == -1) {
            list.clear()
            return list
        }
        val listSize = list.size
        for (index in 0 until size) {
            @Suppress( "DEPRECATION", "UNCHECKED_CAST")
            val element = readParcelable<E>(classLoader) as E
            if (index < listSize) {
                list[index] = element
            } else {
                list += element
            }
        }
        if (size < listSize) {
            list.subList(size, listSize).clear()
        }
        return list
    }
}

fun <T : Parcelable?> Parcel.writeParcelableListCompat(value: List<T>?, flags: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        writeParcelableList(value, flags)
    } else {
        if (value == null) {
            writeInt(-1)
            return
        }
        writeInt(value.size)
        for (element in value) {
            writeParcelable(element, flags)
        }
    }
}
