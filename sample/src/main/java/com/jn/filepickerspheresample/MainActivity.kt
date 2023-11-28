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
import com.jn.filepickersphere.filelist.common.mime.MimeType
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import com.jn.filepickersphere.filepicker.FilePickerSphereManager
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.models.FilePickerModel
import com.jn.filepickersphere.models.PickOptions


class MainActivity : AppCompatActivity() {

    private lateinit var btnPickerFiles: Button

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickerFiles()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPickerFiles = findViewById(R.id.btn_pick_files)

        if (checkPermission()) {
            pickerFiles()
        } else {
            requestPermissions()
        }

        btnPickerFiles.setOnClickListener { pickerFiles() }
    }

    private fun pickerFiles() {
        val options = PickOptions(
            mimeType = listOf(MimeType.IMAGE_PNG, MimeType.IMAGE_JPEG, MimeType.DIRECTORY, MimeType("value herre")),
            localOnly = false,
            rootPath = "/storage/emulated/0/",
            maxSelection = 8
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

            }
        }).container(R.id.fragment_container)
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
