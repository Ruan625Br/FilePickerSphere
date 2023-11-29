package com.jn.filepickerspheresample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.jn.filepickersphere.filelist.common.mime.MimeType
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import com.jn.filepickersphere.filepicker.FilePickerSphereManager
import com.jn.filepickersphere.filepicker.style.FileItemStyle
import com.jn.filepickersphere.filepicker.style.FilePickerStyle
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.models.FilePickerModel
import com.jn.filepickersphere.models.PickOptions
import com.jn.filepickersphere.utils.SphereThemes


class MainActivity : AppCompatActivity() {

    private lateinit var btnPickerPhotos: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearList: Button
    private lateinit var textInputEditTextRootPath: TextInputEditText
    private lateinit var textInputEditTextMaxSelection: TextInputEditText
    private lateinit var textInputCornerSize: TextInputEditText
    private lateinit var switchLocalOnly: MaterialSwitch
    private lateinit var autoCompleteTextView: MaterialAutoCompleteTextView
    private lateinit var toggleGroupCornerFamily: MaterialButtonToggleGroup


    private lateinit var adapter: PhotoAdapter
    private val photoList = mutableListOf<PhotoModel>()
    private var theme: Int? = null
    private var cornerFamily = CornerFamily.ROUNDED


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
        textInputCornerSize = findViewById(R.id.text_input_corner_size)
        switchLocalOnly = findViewById(R.id.switch_local_only)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        toggleGroupCornerFamily = findViewById(R.id.toggle_group_corner_family)

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
        setupTheme()
        setupToggleGroupCornerFamily()
    }

    private fun pickerFiles() {

        val maxSelection = if (textInputEditTextMaxSelection.text.isNullOrBlank()) {
            null
        } else {
            textInputEditTextMaxSelection.text.toString().toInt()
        }

        val rootPath = if (textInputEditTextRootPath.text.isNullOrBlank()) {
            "/storage/emulated/0/"
        } else {
            textInputEditTextRootPath.text.toString()
        }
        val options = PickOptions(
            mimeType = listOf(MimeType.IMAGE_PNG, MimeType.IMAGE_JPEG),
            localOnly = switchLocalOnly.isChecked,
            rootPath = rootPath,
            maxSelection = maxSelection
        )

        val cornerSize = if (textInputCornerSize.text.isNullOrBlank()){
            30f
        } else {
            textInputCornerSize.text.toString().toFloat()
        }

        val fileIconBackground = ShapeAppearanceModel.builder()
            .setAllCorners(cornerFamily, cornerSize)
            .build()


        val fileItemStyle = FileItemStyle(fileIconBackground)

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
            .model(FilePickerModel(options))
            .style(FilePickerStyle(theme, fileItemStyle))
            .picker()

    }

    private fun setupTheme() {
        autoCompleteTextView.setText(getString(R.string.theme_default), false)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            theme = when (position) {
                0 -> SphereThemes.PINK_THEME
                1 -> SphereThemes.GREEN_THEME_LIGHT
                2 -> SphereThemes.GREEN_THEME_DARK
                3 -> SphereThemes.BLUE_THEME_LIGHT
                4 -> SphereThemes.BLUE_THEME_DARK
                5 -> SphereThemes.RED_THEME_LIGHT
                6 -> SphereThemes.RED_THEME_DARK
                7 -> SphereThemes.DYNAMIC_COLORS
                else -> null
            }
        }
    }

    private fun setupToggleGroupCornerFamily() {
        toggleGroupCornerFamily.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                cornerFamily = when (checkedId) {
                    R.id.btn_corner_family_rounded -> CornerFamily.ROUNDED

                    R.id.btn_corner_family_cut -> CornerFamily.CUT
                    else -> CornerFamily.CUT
                }
            }
        }
    }

    private fun setupRecyclerview() {
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
