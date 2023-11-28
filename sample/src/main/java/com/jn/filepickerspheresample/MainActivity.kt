package com.jn.filepickerspheresample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.jn.filepickersphere.filelist.common.mime.MimeType
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import com.jn.filepickersphere.filepicker.FilePickerSphereManager
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.models.FilePickerModel
import com.jn.filepickersphere.models.PickOptions


class MainActivity : AppCompatActivity() {

    private lateinit var btnPickerPhotos: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearList: Button
    private lateinit var textInputEditTextRootPath: TextInputEditText
    private lateinit var textInputEditTextMaxSelection: TextInputEditText
    private lateinit var switchLocalOnly: MaterialSwitch

    private lateinit var adapter: PhotoAdapter
    private val photoList = mutableListOf<PhotoModel>()


    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickerFiles()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPickerPhotos = findViewById(R.id.btn_pick_photos)
        recyclerView = findViewById(R.id.recyclerView)
        btnClearList = findViewById(R.id.btn_clear_list)
        textInputEditTextRootPath = findViewById(R.id.text_input_root_path)
        textInputEditTextMaxSelection = findViewById(R.id.text_input_max_selection)
        switchLocalOnly = findViewById(R.id.switch_local_only)

        if (!checkPermission()) {
            requestPermissions()
        }

        btnPickerPhotos.setOnClickListener { pickerFiles() }
        btnClearList.setOnClickListener {
            val itemCount = photoList.size
            photoList.clear()
            adapter.notifyItemRangeRemoved(0, itemCount)
        }
        setupRecyclerview()
    }

    private fun pickerFiles() {

        val maxSelection = if (textInputEditTextMaxSelection.text.isNullOrBlank()){
            null
        } else{
            textInputEditTextMaxSelection.text.toString().toInt()
        }

        val rootPath = if (textInputEditTextRootPath.text.isNullOrBlank()){
            "/storage/emulated/0/"
        } else{
            textInputEditTextRootPath.text.toString()
        }
        val options = PickOptions(
            mimeType = listOf(MimeType.IMAGE_PNG, MimeType.IMAGE_JPEG),
            localOnly = switchLocalOnly.isChecked,
            rootPath = rootPath,
            maxSelection = maxSelection
        )

        FilePickerSphereManager(this, true).callbacks(object : FilePickerCallbacks {
            override fun onFileSelectionChanged(file: FileModel, selected: Boolean) {
                Log.i("FilePickerSphere", "File clicked: ${file.name}\n Selected: $selected")
            }

            override fun onOpenFile(file: FileModel) {
                Log.i("FilePickerSphere", "Open file: ${file.name}")
            }

            override fun onSelectedFilesChanged(files: List<FileModel>) {
            }

            override fun onAllFilesSelected(files: List<FileModel>) {
                files.forEach {
                    photoList.add(PhotoModel(it))
                }
                adapter.notifyItemRangeInserted(photoList.size, files.size)
            }
        })
            //.container(R.id.fragment_container)
            .model(FilePickerModel(options))
            .picker()

        /* FilePickerSphereManager(
             this, false, object : FilePickerCallbacks {
                 override fun onSelectFile(file: FileModel, selected: Boolean) {
                     Log.i("FilePickerSphere", "File clicked: ${file.name}\n Selected: $selected")
                 }

                 override fun onOpenFile(file: FileModel) {
                     Log.i("FilePickerSphere", "Open file: ${file.name}")
                 }
             }
         ).picker()*/
    }

    private fun setupRecyclerview(){
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = PhotoAdapter(photoList, this)
        recyclerView.adapter = adapter
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readWritePermission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            arePermissionsGranted(readWritePermission)
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        } else {
            val readWritePermission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            readWritePermission.forEach {
                requestPermissionsLauncher.launch(it)
            }
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}
