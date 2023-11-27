package com.jn.filepickersphere.adapters

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jn.filepickersphere.ui.ListDiffer

abstract class ListAdapter<T, VH : RecyclerView.ViewHolder>(
    callback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {
    private val listDiffer = ListDiffer(AdapterListUpdateCallback(this), callback)

    val list: List<T>
        get() = listDiffer.list

    override fun getItemCount(): Int = list.size

    fun getItem(position: Int): T = list[position]

    final override fun getItemId(position: Int): Long = RecyclerView.NO_ID

    open fun refresh() {
        val list = listDiffer.list
        listDiffer.list = emptyList()
        listDiffer.list = list
    }

    open fun replace(list: List<T>, clear: Boolean) {
        if (clear) {
            listDiffer.list = emptyList()
        }
        listDiffer.list = list
    }

    open fun clear() {
        listDiffer.list = emptyList()
    }
}