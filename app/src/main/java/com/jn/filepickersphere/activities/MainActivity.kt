package com.jn.filepickersphere.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import com.jn.filepickersphere.R
import com.jn.filepickersphere.filelist.FileItemSet
import com.jn.filepickersphere.filelist.FileListener
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import com.jn.filepickersphere.fragments.FileListFragment
import com.jn.filepickersphere.models.FileModel

class MainActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
    }
}