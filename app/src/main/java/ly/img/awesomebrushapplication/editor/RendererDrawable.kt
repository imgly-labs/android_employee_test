package ly.img.awesomebrushapplication.editor

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import ly.img.awesomebrushapplication.composer.DataRenderer
import ly.img.awesomebrushapplication.composer.RendererData

class RendererDrawable(private val renderer: DataRenderer, var data: List<RendererData>) : Drawable() {
    override fun draw(canvas: Canvas) {
        renderer.render(canvas, data)
    }

    override fun setAlpha(alpha: Int) {
/* no-op */
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
/* no-op */
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}