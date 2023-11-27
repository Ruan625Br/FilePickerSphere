package com.jn.filepickersphere.filelist

import android.os.Parcel
import android.os.Parcelable
import com.jn.filepickersphere.extensions.readParcelableListCompat
import com.jn.filepickersphere.extensions.writeParcelableListCompat
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.utils.LinkedMapSet

class FileItemSet() : LinkedMapSet<String, FileModel>(FileModel::path), Parcelable {
    constructor(parcel: Parcel) : this() {
        addAll(parcel.readParcelableListCompat())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelableListCompat(toList(), flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FileItemSet> {
        override fun createFromParcel(parcel: Parcel): FileItemSet = FileItemSet(parcel)

        override fun newArray(size: Int): Array<FileItemSet?> = arrayOfNulls(size)
    }
}

fun fileItemSetOf(vararg files: FileModel) = FileItemSet().apply { addAll(files) }