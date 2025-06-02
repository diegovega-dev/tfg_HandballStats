package es.diego.handballstats.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File

class PhotoPickerHelper(
    private val activity: ComponentActivity,
    private val onPhotoSelected: (Bitmap, String) -> Unit
) {
    private var currentPhotoPath: String = ""

    private val galleryLauncher = activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = uriToBitmap(it)
            bitmap?.let { bmp ->
                val savedPath = PhotoService.saveImageToInternalStorage(activity, bmp, generateFileName())
                onPhotoSelected(bmp, savedPath)
            }
        }
    }

    private val cameraLauncher = activity.registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            val savedPath = PhotoService.saveImageToInternalStorage(activity, bitmap, generateFileName())
            onPhotoSelected(bitmap, savedPath)
        }
    }

    fun pickFromGallery() {
        galleryLauncher.launch("image/*")
    }

    fun takePhoto() {
        val photoFile = createImageFile()
        val photoUri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.provider",
            photoFile
        )
        currentPhotoPath = photoFile.absolutePath
        cameraLauncher.launch(photoUri)
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return activity.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        }
    }

    private fun createImageFile(): File {
        val fileName = generateFileName()
        val storageDir = activity.getExternalFilesDir("photos")
        val file = File(storageDir, fileName)
        currentPhotoPath = file.absolutePath
        return file
    }

    private fun generateFileName(): String = "photo_${System.currentTimeMillis()}.jpg"
}
