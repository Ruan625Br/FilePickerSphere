package com.jn.filepickersphere.utils

import com.jn.filepickersphere.provider.AppProvider

object SystemServices {
     val appClassLoader = AppProvider::class.java.classLoader
}