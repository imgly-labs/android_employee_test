package ly.img.awesomebrushapplication.composer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri

class DataRenderer(private val rendererRegistry: RendererRegistry) {
    fun render(context: Context, uri: Uri, data: List<RendererData>): Bitmap? =
        BitmapStorage.loadBitmap(context, uri)?.also {
            render(Canvas(it), data)
        }

    fun render(canvas: Canvas, data: List<RendererData>) {
        data.forEach {
            rendererRegistry[it]?.render(canvas, it)
        }
    }
}