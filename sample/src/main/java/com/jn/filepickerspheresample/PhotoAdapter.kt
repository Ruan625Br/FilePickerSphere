package com.jn.filepickerspheresample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PhotoAdapter(
    private val photos: List<PhotoModel>,
    private val context: Context) : Adapter<PhotoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false))
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photoModel = photos[position]
        val fileModel = photoModel.file

        Glide.with(context).load(fileModel.path).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(100, 100).into(holder.imageView)
    }
}