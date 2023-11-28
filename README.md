<p align="center"><img src="fastlane/metadata/android/en-US/images/icon.png" width="150"></p>
<h1 align="center"><b>FilePickerSphere</b></h1><p align="center"><span><b>FilePickerSphere</b>, a customizable and modern file picker</span></p>

<div align="center">

 <img alt="API" src="https://img.shields.io/badge/Api%2026+-50f270?logo=android&logoColor=black&style=for-the-badge"/></a>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/></a>
  <img alt="material" src="https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you"/></a>
  </br>
  </br>
    <a href="LICENSE">
        <img src="https://img.shields.io/github/license/ruan625br/filepickersphere.svg?color=CFBDFF&style=for-the-badge&logo=gitbook&logoColor=ebebf0&labelColor=23232F" alt="License">
    </a>
    <a href="https://github.com/Ruan625Br/FilePickerSphere/stargazers">
        <img src="https://img.shields.io/github/stars/Ruan625Br/filepickersphere.svg?color=cfbdff&style=for-the-badge&logo=apachespark&logoColor=ebebf0&labelColor=23232F" alt="Stars Count">
    </a>
    <a href="https://ko-fi.com/juannascimento/">
        <img src="https://img.shields.io/badge/Ko--fi-F16061?color=cfbdff&style=for-the-badge&logo=ko-fi&logoColor=black" alt="Ko-Fi">
    </a>   
</div>



---                 

## What is FilePickerSphere?
 
<div align="start">
    <img src="https://raw.githubusercontent.com/Ruan625Br/FileManagerSphere/master/fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="32%" alt="Screenshot 1" />
</div>

---

FilePickerSphere is an robust file management tool crafted to streamline and elevate your interaction with documents, media, and various file formats.
Built on [FileMangerSphere](https://github.com/Ruan625Br/FileManagerSphere)

---

## Features

- **Open-source:** Lightweight, clean, and secure.
- **Material Design:** Follows Material Design guidelines, with attention to detail.
- **Themes:** Customizable user interface colors, along with optional true black dark mode.
- **Well-implemented:** Built on the right foundations, including Java NIO File API and LiveData.

---

## Usage

To use `FilePickerSphereManager`, create an instance of the class by providing the necessary parameters. You can then customize its behavior using various builder methods before calling the `picker()` method to initiate the file picking process.

### Constructor

```kotlin
class FilePickerSphereManager(
    private val context: Context,
    private val bottomSheetViewMode: Boolean = true,
    private val filePickerCallbacks: FilePickerCallbacks? = null,
    private val filePickerModel: FilePickerModel? = null,
    @IdRes private val containerViewId: Int? = null
)
```

# Parameters

| Parameter         | Description                            | Default Value |
|-------------------|----------------------------------------|---------------|
| `context`         | The context in which the file picker will be used. | - |
| `bottomSheetViewMode` | A boolean flag indicating whether the file picker should be displayed as a bottom sheet. | `true` |
| `filePickerCallbacks` | Optional callbacks for handling file picker events. | `null` |
| `filePickerModel` | Optional configuration model for file picking behavior. | `null` |
| `containerViewId` | Optional container view ID for non-dialog mode. | `null` |


# Builder Methods

`callbacks(callbacks: FilePickerCallbacks): FilePickerSphereManager`

Sets the file picker callbacks.

`model(model: FilePickerModel): FilePickerSphereManager`

Sets the file picker configuration model.

`container(@IdRes containerViewId: Int): FilePickerSphereManager`

Sets the container view ID for non-dialog mode.


# File Picking
To initiate the file picking process, call the `picker()` method

This method checks for necessary configurations and displays the file picker accordingly.

# Notes

-  Ensure that the required configurations (callbacks and model) are set before calling the `picker()` method.
-  For non-dialog mode, provide a valid containerViewId using the `container() `
  method and set the `bottomSheetViewModel` to `false`

# Example

```kotlin

  // Define file picking options
    val options = PickOptions(
        mimeType = listOf(MimeType.IMAGE_PNG, MimeType.IMAGE_JPEG, MimeType.DIRECTORY, MimeType("value here")),
        localOnly = false,
        rootPath = "/storage/emulated/0/",
        maxSelection = 8
    )

 // Create and configure FilePickerSphereManager
    FilePickerSphereManager(this, true).callbacks(object : FilePickerCallbacks {
        override fun onFileSelectionChanged(file: FileModel, selected: Boolean) {
            Log.i("FilePickerSphere", "File clicked: ${file.name}\n Selected: $selected")
        }

        override fun onOpenFile(file: FileModel) {
            Log.i("FilePickerSphere", "Open file: ${file.name}")
        }

        override fun onSelectedFilesChanged(files: List<FileModel>) {
            // Handle selected files change
        }

        override fun onAllFilesSelected(files: List<FileModel>) {
            // Handle all files selected
        }
    }).container(R.id.fragment_container)
        .model(FilePickerModel(options))
        .picker()

```
---

## Authors

- [@Ruan625Br](https://www.github.com/Ruan625Br)

---

## Love my work?

<a href='https://ko-fi.com/juannascimento' target='_blank'><img height='35' style='border:0px;height:34px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' />

---

## License

    Copyright (C) 2023 Juan Nscimento

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
