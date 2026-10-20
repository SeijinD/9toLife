package com.seijind.ninetolife.data.background

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import kotlin.math.max

private const val DirectoryName = "backgrounds"
private const val MaxEdgePx = 2160
private const val JpegQuality = 88

/**
 * Copies a picked photo into the app's files, downscaled and with EXIF rotation applied,
 * so the home screen never decodes a full-size camera image. Each import gets a new file
 * name so the UI reloads it.
 */
internal class BackgroundImageImporter(
    private val context: Context,
) {
    private val directory: File get() = File(context.filesDir, DirectoryName)

    /** Returns the stored file path, or null when the photo could not be read. Old photos are left to [deleteAllExcept]. */
    suspend fun import(uri: Uri): String? = withContext(context = Dispatchers.IO) {
        try {
            val bitmap = decodeScaled(uri = uri) ?: return@withContext null
            directory.mkdirs()
            val target = File(directory, "background_${System.currentTimeMillis()}.jpg")
            val isWritten = try {
                target.outputStream().use { output -> bitmap.compress(Bitmap.CompressFormat.JPEG, JpegQuality, output) }
            } catch (exception: IOException) {
                false
            } finally {
                bitmap.recycle()
            }
            if (!isWritten) {
                target.delete()
                return@withContext null
            }
            target.absolutePath
        } catch (exception: IOException) {
            null
        } catch (exception: SecurityException) {
            null
        }
    }

    /**
     * Removes every stored photo except [path]. Called with the saved setting, so a photo is deleted
     * only after the setting stopped pointing to it, whatever happened in between.
     */
    suspend fun deleteAllExcept(path: String?) {
        withContext(context = Dispatchers.IO) {
            directory.listFiles()?.filter { it.absolutePath != path }?.forEach { it.delete() }
        }
    }

    private fun decodeScaled(uri: Uri): Bitmap? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val scale = MaxEdgePx.toFloat() / max(info.size.width, info.size.height)
                if (scale < 1f) {
                    decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
                }
            }
        } else {
            decodeScaledLegacy(uri = uri)
        }

    /** API 26-27: no ImageDecoder, so sample down with BitmapFactory (EXIF rotation is not applied). */
    private fun decodeScaledLegacy(uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sampleSize = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MaxEdgePx) sampleSize *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }
}
