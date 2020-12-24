package ly.img.awesomebrushapplication.composer

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

internal object BitmapStorage {
    fun loadBitmap(context: Context, uri: Uri, maxWidth: Int = Int.MAX_VALUE): Bitmap? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            decodeScaledBitmapWithTargetSize(context, uri, maxWidth)
        } else {
            decodeScaledBitmap(context, uri, maxWidth)
        }
    }

    fun saveBitmap(context: Context, bitmap: Bitmap, filename: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.jpg")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)?.let {
                context.contentResolver.openOutputStream(it)
            }
        } else {
            FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "$filename.jpg"))
        }?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun decodeScaledBitmapWithTargetSize(context: Context, image: Uri, maxWidth: Int): Bitmap {
        val header = ImageDecoder.OnHeaderDecodedListener { decoder, info, _ ->
            val size = info.size
            val requiredWidth = minOf(maxWidth, size.width)
            decoder.isMutableRequired = true
            decoder.setTargetSize(requiredWidth, (size.height * (requiredWidth / size.width.toDouble())).toInt())
        }
        return ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, image), header)
    }

    private fun decodeScaledBitmap(context: Context, image: Uri, minWidth: Int): Bitmap? {
        var options = BitmapFactory.Options()
        context.contentResolver.openInputStream(image)?.use {
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(it, null, options)
        }
        val targetWidth = min(minWidth, options.outWidth)
        val sourceWidth = options.outWidth
        val sampleSize = calculateSampleSize(sourceWidth, targetWidth)
        options = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inDensity = sourceWidth
            inTargetDensity = targetWidth * sampleSize
        }
        return context.contentResolver.openInputStream(image)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
            ?.apply {
                density = context.resources.displayMetrics.densityDpi
            }
    }

    private fun calculateSampleSize(currentWidth: Int, requiredWidth: Int): Int {
        var inSampleSize = 1
        if (currentWidth > requiredWidth) {
            while (currentWidth / 2 / inSampleSize >= requiredWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}