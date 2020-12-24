package ly.img.awesomebrushapplication.plugins.brush

import android.graphics.PointF
import ly.img.awesomebrushapplication.composer.ImageComposer
import ly.img.awesomebrushapplication.composer.renderer.BrushRenderer
import ly.img.awesomebrushapplication.composer.renderer.BrushRendererData

class BrushRendererTouchHandler(private val imageComposer: ImageComposer) {
    private val renderer = BrushRenderer()
    private var data: BrushRendererData? = null
    fun touchStart(x: Float, y: Float, strokeWidth: Float = 0.05f, color: Int) {
        data = BrushRendererData(color, strokeWidth, mutableListOf())
        addPoint(x, y)
    }

    fun touchMove(x: Float, y: Float) {
        addPoint(x, y)
    }

    fun touchEnd(x: Float, y: Float) {
        addPoint(x, y)
        data?.also {
            if (it.points.isNotEmpty()) {
                imageComposer.add(it)
            }
        }
        data = null
    }

    private fun addPoint(x: Float, y: Float) {
        imageComposer.previewCanvas?.also { canvas ->
            data?.also {
                it.addPoint(PointF(x, y))
                renderer.render(canvas, it, it.points.size - 2)
                imageComposer.previewImage?.invalidateSelf()
            }
        }
    }
}