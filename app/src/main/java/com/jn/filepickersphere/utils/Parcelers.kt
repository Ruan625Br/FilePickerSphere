
package com.jn.filepickersphere.utils

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler

object ParcelableParceler : Parceler<Any?> {
    @Suppress("DEPRECATION")
    override fun create(parcel: Parcel): Any? = parcel.readParcelable(SystemServices.appClassLoader)

    override fun Any?.write(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(this as Parcelable?, flags)
    }
}