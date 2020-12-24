package ly.img.awesomebrushapplication.composer.renderer


import android.graphics.PointF
import ly.img.awesomebrushapplication.composer.RendererData

data class BrushRendererData(val color: Int, val strokeWidth: Float, val points: MutableList<PointF> = mutableListOf()) : RendererData {
    fun addPoint(point: PointF) {
        points.add(point)
    }

    //Next Point to draw (Last you get from onTouchEvent) or null if it is the last
    fun nextPoint(index: Int) = points.getOrNull(index)

    // "Current point to draw. (The point before the last)
    fun point(index: Int) = points.getOrNull(index - 1) ?: PointF(0f, 0f)

    // The point before `point`
    fun lastPoint(index: Int) = points.getOrNull(index - 2) ?: point(index)

    // The point before lastPoint or lastPoint if there is not point before it.
    fun beforeLastPoint(index: Int) = points.getOrNull(index - 3) ?: lastPoint(index)
}