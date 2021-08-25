package com.example.wallpaper_app.base.util
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

val REQUEST_CODE_GALARY=2
val REQUEST_CODE_CAMERA=8
var mCurrentPhotoPath: String? = null;
fun Activity.requestPermissionsCamera(){
    if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                123
            )
        }
    }
}
fun Activity.openGalleryForImage() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(intent, REQUEST_CODE_GALARY)
}
fun Activity.openCamera() {

    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val file: File = createFile()

    val uri: Uri = FileProvider.getUriForFile(
        this,
        packageName+".fileprovider",
        file
    )
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
    startActivityForResult(intent, REQUEST_CODE_CAMERA)
}
@Throws(IOException::class)
fun Activity.createFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        mCurrentPhotoPath = absolutePath
    }
}