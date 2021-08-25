package com.example.pdf_scanner.utils

import android.content.Context
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import javax.inject.Inject


/**
 * Created by Nguyen Manh Tien
 */
class Download @Inject constructor(val context: Context) {
    fun downloadFile(
        url: String,
        dirPath: String,
        fileName: String,
        downloadListener: OnDownloadListener
    ): Int {
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .build()
        PRDownloader.initialize(context, config)

        val downloadId = PRDownloader.download(url, dirPath, fileName)
            .build()
            .start(downloadListener)

        return downloadId
    }
}