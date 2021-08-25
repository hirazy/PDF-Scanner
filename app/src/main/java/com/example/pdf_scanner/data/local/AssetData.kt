package com.example.pdf_scanner.data.local

import android.content.Context
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class AssetData @Inject constructor(private val context: Context) {
    private fun loadJSONFromAsset(filename: String): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = context.assets.open(filename)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

}