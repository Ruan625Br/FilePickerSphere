package com.jn.filepickersphere.adapters

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jn.filepickersphere.R
import com.jn.filepickersphere.databinding.FileItemBinding
import com.jn.filepickersphere.extensions.getDimension
import com.jn.filepickersphere.extensions.getDimensionPixelSize
import com.jn.filepickersphere.extensions.layoutInflater
import com.jn.filepickersphere.filelist.FileItemSet
import com.jn.filepickersphere.filelist.FileListener
import com.jn.filepickersphere.filelist.common.mime.MimeType
import com.jn.filepickersphere.filelist.fileItemSetOf
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.ui.CheckableItemBackground
import com.jn.filepickersphere.filelist.common.mime.MimeTypeUtil
import com.jn.filepickersphere.models.PickOptions
import com.jn.filepickersphere.utils.FileFormatUtils
import com.jn.filepickersphere.utils.FileIcon

class FileListAdapter(
    private val listener: FileListener
) : ListAdapter<FileModel, FileListAdapter.ViewHolder>(CALLBACK) {


    private val selectedFiles = fileItemSetOf()
    private val filePositionMap = mutableMapOf<String, Int>()
    var pickOptions: PickOptions? = null
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        FileItemBinding.inflate(parent.context.layoutInflater, parent, false)
    ).apply {
        applyStyle(binding)
        binding.itemFile.background = CheckableItemBackground.create(binding.itemFile.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        throw UnsupportedOperationException()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        val file = getItem(position)
        val binding = holder.binding
        val selected = file in selectedFiles
        val path = file.path
        val mimeType = MimeTypeUtil.getMimeType(mPath = path)
        val context = binding.root.context

        binding.itemFile.isChecked = selected
        if (payloads.isNotEmpty()) {
            return
        }

        FileIcon.loadIcon(file, binding, mimeType, context)

        binding.fileTitle.text = file.name
        binding.fileSize.text = FileFormatUtils.formatFileSize(file.fileSize)
        binding.fileDate.text = FileFormatUtils.formatDateFromFile(path, true)

        binding.itemFile.setOnClickListener {
            if (selectedFiles.isEmpty()) {
                listener.openFile(file)
            } else {
                selectFile(file)
            }
        }
        binding.itemFile.setOnLongClickListener {
            if (selectedFiles.isEmpty()) {
                selectFile(file)
            } else {
                listener.openFile(file)
            }
            true
        }

        binding.iconFile.setOnClickListener {
            selectFile(file)
        }
    }

    class ViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root)


    fun replaceSelectedFiles(files: FileItemSet) {
        val changedFiles = fileItemSetOf()
        val iterator = selectedFiles.iterator()

        while (iterator.hasNext()) {
            val file = iterator.next()
            if (file !in files) {
                iterator.remove()
                changedFiles.add(file)
            }
        }

        for (file in files) {
            if (file !in selectedFiles) {
                selectedFiles.add(file)
                changedFiles.add(file)
            }
        }

        for (file in changedFiles) {
            val position = filePositionMap[file.path]
            position?.let { notifyItemChanged(it, PAYLOAD_STATE_CHANGED) }
        }
    }

    private fun selectFile(file: FileModel) {
        if (!isFileSelectable(file)) {
            return
        }
        val selected = file in selectedFiles
        applyMaxSelectionCheck({
            listener.selectFile(file, false)
        }, {
            listener.selectFile(file, !selected)
        })

    }


    fun selectAllFiles() {
        val files = fileItemSetOf()
        for (index in 0 until itemCount) {
            val file = getItem(index)
            if (isFileSelectable(file)) {
                files.add(file)
            }
        }
        listener.selectFiles(files, true)
    }

    private fun isFileSelectable(file: FileModel): Boolean {
        val pickOptions = pickOptions ?: return true
        val mimeType = MimeTypeUtil.getMimeType(mPath = file.path)?.let { MimeType(it) }

        Log.i("FileListAdapter", "MimeTYpe: ${mimeType?.value} required: ${pickOptions.mimeType}")
        return if (pickOptions.pickDirectory) {
            file.isDirectory
        } else {
            mimeType?.let {
                it in pickOptions.mimeType
            } ?: false
        }
    }

    fun replaceList(list: List<FileModel>) {
        super.replace(list, true)
        rebuildFilePositionMap()
    }

    private fun rebuildFilePositionMap() {
        filePositionMap.clear()
        for (index in 0 until itemCount) {
            val file = getItem(index)
            filePositionMap[file.path] = index
        }
    }

    private fun applyStyle(binding: FileItemBinding) {
        binding.root.context.let { ctx ->
            binding.itemFile.radius = ctx.getDimension(R.dimen.corner_radius_base)
            binding.layoutBase.layoutParams = binding.layoutBase.layoutParams.apply {
                if (this is ViewGroup.MarginLayoutParams) {
                    val padding = ctx.getDimensionPixelSize(R.dimen.spacing_tiny)
                    setMargins(padding, padding, padding, padding)
                }
            }
        }
    }

    private inline fun applyMaxSelectionCheck(onMaximum: () -> Unit, action: () -> Unit) {
        pickOptions?.let {
            if (it.maxSelection != null && it.maxSelection <= selectedFiles.size) {
                onMaximum.invoke()
            } else {
                action.invoke()
            }
        } ?: action.invoke()
    }

    companion object {
        private val PAYLOAD_STATE_CHANGED = Any()

        private val CALLBACK = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem.path == newItem.path
            }

            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean {
                return oldItem == newItem
            }

        }

    }


}