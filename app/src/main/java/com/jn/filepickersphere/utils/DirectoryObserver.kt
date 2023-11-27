package com.jn.filepickersphere.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit

class DirectoryObserver(private val path: Path, private val onChange: () -> Unit) {
    private var watchService: WatchService? = null
    private var closed = false
    private var job: Job? = null

    init {
        startObserving()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startObserving() {
        job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val directory = path
                watchService = FileSystems.getDefault().newWatchService()
                directory.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE
                )
                while (!closed) {
                    val key = watchService?.poll(1, TimeUnit.SECONDS)
                    key?.let {
                        for (event in key.pollEvents()) {
                            when (event.kind()) {
                                StandardWatchEventKinds.ENTRY_DELETE,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_CREATE -> {
                                    withContext(Dispatchers.Main) {
                                        onChange.invoke()
                                    }
                                }
                            }
                        }
                        key.reset()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                watchService?.close()
            }
        }
    }

    fun close() {
        closed = true
        job?.cancel()
        watchService?.close()
    }
}
