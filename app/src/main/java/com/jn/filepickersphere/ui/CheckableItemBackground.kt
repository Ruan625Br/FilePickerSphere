package com.jn.filepickersphere.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.jn.filepickersphere.R
import com.jn.filepickersphere.extensions.getColorByAttr
import com.jn.filepickersphere.extensions.shortAnimTime
import com.jn.filepickersphere.filepicker.style.FileItemStyle
import com.jn.filepickersphere.utils.asColor
import com.jn.filepickersphere.utils.withModulatedAlpha

object CheckableItemBackground {

    @SuppressLint("RestrictedApi")
    fun create(context: Context, fileItemStyle: FileItemStyle): Drawable {
        val typedValue = TypedValue()
        val resolved = context.theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSecondary, typedValue, true
        )
        val colorOnSecondary = if (resolved) typedValue.data else 0


        return AnimatedStateListDrawableCompat().apply {
            val shortAnimTime = context.shortAnimTime
            setEnterFadeDuration(shortAnimTime)
            setExitFadeDuration(shortAnimTime)
            val primaryColor =
                context.getColorByAttr(com.google.android.material.R.attr.colorPrimaryContainer)
            val checkedColor = primaryColor.asColor().withModulatedAlpha(fileItemStyle.fileSelectedOpacity).value

            val backgroundSelected =
                DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.bg_card_normal)!!)
            val background =
                DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.bg_card_normal)!!)

            DrawableCompat.setTint(backgroundSelected, checkedColor)
            DrawableCompat.setTint(background, colorOnSecondary)

            addState(intArrayOf(android.R.attr.state_checked), backgroundSelected)
            addState(intArrayOf(), background)

        }
    }

}

