package com.jn.filepickersphere.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.shape.MaterialShapeDrawable
import com.jn.filepickersphere.R
import com.jn.filepickersphere.databinding.FileItemBinding
import com.jn.filepickersphere.extensions.getColorByAttr
import com.jn.filepickersphere.filelist.common.mime.MimeTypeUtil
import com.jn.filepickersphere.filepicker.style.FileItemStyle
import com.jn.filepickersphere.models.FileModel
import java.util.Locale

object FileIcon {

    fun loadIcon(
        file: FileModel,
        binding: FileItemBinding,
        mimeType: String?,
        context: Context,
        fileItemStyle: FileItemStyle
    ) {
        if (mimeType != null && mimeType.isMimeTypeMedia()) {
            loadImage(file.path, binding, context)
        } else {
            loadIconByMimeType(file.isDirectory, mimeType, binding, context)
        }
        applyBackground(context, binding, mimeType?.isMimeTypeMedia() ?: false, fileItemStyle)
    }

    private fun loadImage(path: String, binding: FileItemBinding, context: Context) {
        val imageView = binding.iconFile
        binding.iconFile.clearColorFilter()

        Glide.with(context).load(path).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(50, 50).into(imageView)
    }

    private fun loadIconByMimeType(
        isDir: Boolean, mimeType: String?, binding: FileItemBinding, context: Context
    ) {
        val icFile = AppCompatResources.getDrawable(context, R.drawable.file_generic_icon)!!
        val icFolder = AppCompatResources.getDrawable(context, R.drawable.ic_folder)!!
        val tint = getColorPrimaryInverse(context)

        icFile.setTint(tint)
        icFolder.setTint(tint)

        val iconResourceId = mimeType?.let { MimeTypeUtil.getIconIdByMimeType(it) } ?: icFile
        val resource: Any = if (isDir) icFolder else iconResourceId

        binding.iconFile.scaleType = ImageView.ScaleType.CENTER
        binding.iconFile.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
        Glide.with(context).load(resource).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .apply(RequestOptions().placeholder(icFile)).into(binding.iconFile)

    }

    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "image/")
        val mimeType = this.lowercase(Locale.getDefault())
        return mediaMimeTypes.any { mimeType.startsWith(it) }
    }

    private fun applyBackground(
        context: Context,
        binding: FileItemBinding,
        isMedia: Boolean,
        fileItemStyle: FileItemStyle
    ) {
        val colorPrimary = context.getColorByAttr(com.google.android.material.R.attr.colorPrimary)

        val borderDrawable = MaterialShapeDrawable(fileItemStyle.fileIconBackground)
        val borderTint = if (isMedia) Color.TRANSPARENT else colorPrimary
        val iconFile = binding.iconFile

        if (fileItemStyle.autoApplyIconBackgroundTint) borderDrawable.setTint(borderTint)
        iconFile.background = borderDrawable
        iconFile.scaleType =
            if (isMedia) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.CENTER
        iconFile.shapeAppearanceModel = fileItemStyle.fileIconBackground


    }
}