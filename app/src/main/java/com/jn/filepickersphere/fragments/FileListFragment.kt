package com.jn.filepickersphere.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.jn.filepickersphere.R
import com.jn.filepickersphere.adapters.FileListAdapter
import com.jn.filepickersphere.databinding.FragmentFileListBinding
import com.jn.filepickersphere.extensions.getQuantityString
import com.jn.filepickersphere.extensions.parcelable
import com.jn.filepickersphere.filelist.FileItemSet
import com.jn.filepickersphere.filelist.FileListener
import com.jn.filepickersphere.filepicker.FilePickerCallbacks
import com.jn.filepickersphere.models.FileModel
import com.jn.filepickersphere.models.FilePickerModel
import com.jn.filepickersphere.utils.Constants
import com.jn.filepickersphere.utils.Failure
import com.jn.filepickersphere.utils.Loading
import com.jn.filepickersphere.utils.Stateful
import com.jn.filepickersphere.utils.Success
import com.jn.filepickersphere.viewmodels.FileListViewModel
import java.nio.file.Paths
import kotlin.io.path.pathString


class FileListFragment(
    private val filePickerCallbacks: FilePickerCallbacks? = null
) : Fragment(), FileListener {
    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!

    private var filePickerModel: FilePickerModel? = null
    private lateinit var adapter: FileListAdapter

    private lateinit var viewModel: FileListViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var topAppBar: MaterialToolbar

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val rootPath = filePickerModel?.pickOptions?.rootPath ?: Constants.DEFAULT_PATH
            viewModel.currentPath?.let { currentPath ->
                if (rootPath != currentPath.pathString) {
                    viewModel.navigateUp()
                } else {
                    requireActivity().supportFragmentManager.popBackStack(
                        TAG,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filePickerModel = arguments?.parcelable(ARG_FILE_PICKER_MODEL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        topAppBar = binding.topAppBar

        (activity as AppCompatActivity).setSupportActionBar(topAppBar)
        viewModel = ViewModelProvider(this)[FileListViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )

        topAppBar.setNavigationOnClickListener { onBackPressedCallback.handleOnBackPressed() }

        setupRecyclerview()
        observeFileViewModel()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun clearSelectedFiles() {
        viewModel.clearSelectedFiles()
    }

    override fun selectFile(file: FileModel, selected: Boolean) {
        filePickerCallbacks?.onFileSelectionChanged(file, selected)
        viewModel.selectFile(file, selected)
    }

    override fun selectFiles(files: FileItemSet, selected: Boolean) {
        viewModel.selectFiles(files, selected)
    }

    override fun openFile(file: FileModel) {

        if (file.isDirectory) {
            navigateTo(file.path)
            filePickerCallbacks?.onOpenFile(file)
        }
    }

    private fun observeFileViewModel() {
        viewModel.currentPathLiveData.observe(viewLifecycleOwner) { updateTopAppBar() }
        viewModel.fileListLiveData.observe(viewLifecycleOwner) { onFileListChanged(it) }
        viewModel.selectedFilesLiveData.observe(viewLifecycleOwner) { onSelectedFilesChanged(it) }

        val rootPath = filePickerModel?.pickOptions?.rootPath ?: Constants.DEFAULT_PATH
        viewModel.resetTo(Paths.get(rootPath))
    }

    private fun setupRecyclerview() {
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = FileListAdapter(this)
        adapter.pickOptions = filePickerModel?.pickOptions
        recyclerView.adapter = adapter

    }

    private fun updateTopAppBar() {
        val size = viewModel.selectedFiles.size
        topAppBar.title = getQuantityString(R.plurals.file_list_selected_count_format, size, size)
    }

    private fun onFileListChanged(stateful: Stateful<List<FileModel>>) {
        val files = if (stateful is Failure) null else stateful.value
        when (stateful) {
            is Failure -> topAppBar.subtitle = getString(R.string.error)
            is Loading -> topAppBar.subtitle = getString(R.string.loading)
            else -> topAppBar.subtitle = getSubtitle(files!!)
        }

        if (stateful !is Failure) {
            updateAdapterFileList()
        } else {
            adapter.clear()
        }
        if (stateful is Success) {
            viewModel.pendingState?.let { recyclerView.layoutManager!!.onRestoreInstanceState(it) }
        }

    }

    private fun getSubtitle(files: List<FileModel>): String {
        val directoryCount = files.count { it.isDirectory }
        val fileCount = files.size - directoryCount
        val directoryCountText = if (directoryCount > 0) {
            getQuantityString(
                R.plurals.file_list_subtitle_directory_count_format, directoryCount, directoryCount
            )
        } else {
            null
        }
        val fileCountText = if (fileCount > 0) {
            getQuantityString(
                R.plurals.file_list_subtitle_file_count_format, fileCount, fileCount
            )
        } else {
            null
        }
        return when {
            !directoryCountText.isNullOrEmpty() && !fileCountText.isNullOrEmpty() -> (directoryCountText + getString(
                R.string.file_list_subtitle_separator
            ) + fileCountText)

            !directoryCountText.isNullOrEmpty() -> directoryCountText
            !fileCountText.isNullOrEmpty() -> fileCountText
            else -> getString(R.string.empty)
        }
    }

    private fun onSelectedFilesChanged(files: FileItemSet) {
        filePickerCallbacks?.onSelectedFilesChanged(files.toList())
        updateTopAppBar()
        updateFab()
        if (::adapter.isInitialized) adapter.replaceSelectedFiles(files)
    }

    private fun updateAdapterFileList() {
        val files = viewModel.fileListStateful.value ?: return
        adapter.replaceList(files)
    }

    private fun updateFab() {
        val isExtended = if (filePickerModel?.pickOptions?.maxSelection != null){
            filePickerModel?.pickOptions?.maxSelection!! == viewModel.selectedFiles.size
        } else {
            true
        }

        binding.fabDone.isExtended = isExtended
        binding.fabDone.isClickable = isExtended

        binding.fabDone.setOnClickListener {
            if (isExtended) {
                filePickerCallbacks?.onAllFilesSelected(viewModel.selectedFiles.toList())
                requireActivity().supportFragmentManager.popBackStack(
                    TAG,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            }
        }

    }

    private fun navigateTo(path: String) {
        val state = recyclerView.layoutManager!!.onSaveInstanceState()
        val localOnly = filePickerModel?.pickOptions?.localOnly ?: false

        if (!localOnly)
            viewModel.navigateTo(state!!, Paths.get(path))

    }


    companion object {
        const val ARG_FILE_PICKER_MODEL = "FilePickerModel"
        const val TAG = "FileListFragment"

        fun newInstance(filePickerModel: FilePickerModel, pickerCallbacks: FilePickerCallbacks) =
            FileListFragment(pickerCallbacks).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_FILE_PICKER_MODEL, filePickerModel)
                }
            }
    }

}