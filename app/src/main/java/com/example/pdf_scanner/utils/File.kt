package com.example.pdf_scanner.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.pdf_scanner.BuildConfig
import java.io.File
import javax.inject.Inject


/**
 * Created by Nguyen Manh Tien
 */
class FileUtil @Inject constructor(val context: Context) : FileSource {
    override fun getRootFolder(): String {
        return "${context.getExternalFilesDir(null)?.absolutePath}/${BuildConfig.APPLICATION_ID}"
    }

    override fun open(filePath: String) {
        val file = File(filePath)
        val uri: Uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file)
        val mime: String? = context.contentResolver.getType(uri)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(uri, mime)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

interface FileSource {
    fun getRootFolder(): String
    fun open(filePath: String)
}