package es.diego.handballstats.services

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object PhotoService {

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {
        val directory = File(context.filesDir, "photos")
        if (!directory.exists()) directory.mkdir()

        val file = File(directory, fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return file.absolutePath
    }

    fun deleteImage(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.delete()
    }
}
