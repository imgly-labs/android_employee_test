package ly.img.awesomebrushapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import ly.img.awesomebrushapplication.view.BrushCanvas
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnSelectImage).setOnClickListener { onPressLoadImage() }
        /*

          == Layout ==

            We need some simple Layout here, use an xml layout or code to create it:
              * Load Image -> Select Image from Gallery.
              * Save Image -> Save Image to Gallery.
              * Color -> A Simply selector where you can select at min. 5 Colors. More Color's or a color-picker is also welcome.
              * Size -> Adjust brush size with a slider
              * Clear all -> Removes the Brush.

          ----------------------------------------------------------------------
         | HINT: The layout doesn't have to look good, but it should be usable. |
          ----------------------------------------------------------------------

          == Requirements ==
            Must have features
              * The result should be drawn in original Size, not in screen size!
                You can ignore OutOfMemory issues here, just use some smaller Image

              *

          == Things to consider ==
            Nice to have features, but keep you code open to implement this later
              * The user can make mistakes, so a history (undo redo) would be nice.
              * The image usually doesn't change while editing, but can be replaced in size.
         */


    }

    private fun onPressLoadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            intent.type = "image/*"
        } else {
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        startActivityForResult(intent, GALLERY_INTENT_RESULT)
    }

    private fun handleGalleryImage(uri: Uri) {
        findViewById<BrushCanvas>(R.id.brushCanvas).image =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
    }

    @MainThread
    private fun onPressSave() {
        TODO("saveBrushToGallery() on background thread.")
    }

    private fun onChangeColor(@ColorInt color: Int) {
        // ColorInt (8bit) Color is ok, do not wast time here.
    }

    private fun onSizeChanged(size: Float) {

    }

    @WorkerThread
    private fun saveBrushToGallery() {
        // Do not worry about memory there. (do not waste time it's another story)
        // ...simply use only test images that fit's in the memory.

        // Because it can take some time to create the brush, it would be nice to have a progress here.

        val bitmap: Bitmap = TODO("Create in Size of original image, not in Screen size!")
        val outputStream: OutputStream =
            TODO("Open an ScopedStorage OutputStream and save it in user gallery.")
        outputStream.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null && resultCode == Activity.RESULT_OK && requestCode == GALLERY_INTENT_RESULT) {
            val uri = data.data
            if (uri != null) {
                handleGalleryImage(uri)
            }
        }

    }

    companion object {
        const val GALLERY_INTENT_RESULT = 0
    }
}