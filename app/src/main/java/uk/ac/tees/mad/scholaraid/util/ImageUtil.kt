package uk.ac.tees.mad.scholaraid.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtil {
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun rotateBitmapIfRequired(context: Context, bitmap: Bitmap?, uri: Uri): Bitmap? {
        if (bitmap == null) return null

        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exif = ExifInterface(input)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                }

                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } ?: bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }


    private fun compressBitmap(bitmap: Bitmap?, quality: Int): ByteArray? {
        if (bitmap == null) return null

        return try {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            bitmap.recycle()
        }
    }

    suspend fun compressImage(context: Context, uri: Uri, maxSize: Int = 1024): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    // Decode image with options to reduce memory
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(inputStream, null, options)

                    // Calculate sample size
                    options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize)
                    options.inJustDecodeBounds = false

                    // Decode bitmap with new options
                    context.contentResolver.openInputStream(uri)?.use { newStream ->
                        var bitmap = BitmapFactory.decodeStream(newStream, null, options)

                        // Rotate bitmap if needed
                        bitmap = rotateBitmapIfRequired(context, bitmap, uri)

                        // Compress to byte array
                        compressBitmap(bitmap, 80) // 80% quality
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}