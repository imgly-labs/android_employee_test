package ly.img.awesomebrushapplication.view

import android.graphics.PointF

class PathData(val color: Int, val strokeWidth: Float) {
    private val points = mutableListOf<PointF>()
    val isFirstPoint
        get() = points.isEmpty()

    //Next Point to draw (Last you get from onTouchEvent) or null if it is the last
    var nextPoint: PointF? = null

    // "Current point to draw. (The point before the last)
    val point
        get() = points.lastOrNull() ?: PointF(0f, 0f)

    // The point before `point`
    val lastPoint
        get() = if (points.size > 1) points[points.size - 2] else point

    // The point before lastPoint or lastPoint if there is not point before it.
    val beforeLastPoint
        get() = if (points.size > 2) points[points.size - 3] else lastPoint

    fun addPoint(point: PointF?) {
        nextPoint?.also { point ->
            points.add(point)
        }
        nextPoint = point
    }
}