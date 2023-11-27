package com.jn.filepickersphere.extensions

import java.io.IOException
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.Path

@Throws(IOException::class)
fun Path.newDirectoryStream(): DirectoryStream<Path> = Files.newDirectoryStream(this)