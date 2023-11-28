package com.jn.filepickersphere.filepicker

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.jn.filepickersphere.extensions.activity
import com.jn.filepickersphere.fragments.FileListFragment
import com.jn.filepickersphere.fragments.FilePickerBottomSheetFragment
import com.jn.filepickersphere.models.FilePickerModel

class FilePickerSphereManager(
    private val context: Context,
    private val bottomSheetViewMode: Boolean = true,
    private val filePickerCallbacks: FilePickerCallbacks? = null,
    private val filePickerModel: FilePickerModel? = null,
    @IdRes
    private val containerViewId: Int? = null
) {

    fun callbacks(callbacks: FilePickerCallbacks): FilePickerSphereManager {
        return copy(filePickerCallbacks = callbacks)
    }

    fun model(model: FilePickerModel): FilePickerSphereManager {
        return copy(filePickerModel = model)
    }

    fun container(@IdRes containerViewId: Int): FilePickerSphereManager {
        return copy(containerViewId = containerViewId)
    }

    fun picker() {
        requireNotNull(filePickerCallbacks) { "FilePickerCallbacks must be set." }
        requireNotNull(filePickerModel) { "FilePickerModel must be set." }

        if (bottomSheetViewMode) {
            val filePickerDialogFragment = FilePickerBottomSheetFragment.newInstance(filePickerModel, filePickerCallbacks)
            filePickerDialogFragment.show(findFragmentManager(), FilePickerBottomSheetFragment.TAG)
        } else {
            requireNotNull(containerViewId) { "ContainerViewId must be set." }

            val fileListFragment = FileListFragment.newInstance(filePickerModel, filePickerCallbacks)
            findFragmentManager().beginTransaction()
                .replace(containerViewId, fileListFragment, FileListFragment.TAG)
                .addToBackStack(FileListFragment.TAG)
                .commit()
        }
    }

    private fun findFragmentManager(): FragmentManager {
        return (context.activity as? FragmentActivity)?.supportFragmentManager
            ?: throw IllegalStateException("Cannot find FragmentManager.")
    }

    private fun copy(
        context: Context = this.context,
        dialogViewMode: Boolean = this.bottomSheetViewMode,
        filePickerCallbacks: FilePickerCallbacks? = this.filePickerCallbacks,
        filePickerModel: FilePickerModel? = this.filePickerModel,
        @IdRes
        containerViewId: Int? = this.containerViewId
    ): FilePickerSphereManager {
        return FilePickerSphereManager(
            context,
            dialogViewMode,
            filePickerCallbacks,
            filePickerModel,
            containerViewId
        )
    }
}
