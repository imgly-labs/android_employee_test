package ly.img.awesomebrushapplication.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

class PathRenderer(val pathData: PathData) {
    private val path = Path()
    private val brushStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    fun render(canvas: Canvas) {
        brushStrokePaint.color = pathData.color
        brushStrokePaint.strokeWidth = pathData.strokeWidth
        canvas.drawPath(path, brushStrokePaint)
    }

    fun updatePath() {
        if (pathData.isFirstPoint) {
            pathData.nextPoint?.also {
                path.moveTo(it.x, it.y)
            }
        } else {
            val pointDx: Float
            val pointDy: Float
            pathData.nextPoint.also { nextPoint ->
                if (nextPoint == null) {
                    pointDx = (pathData.point.x - pathData.lastPoint.x) / SMOOTH_VAL
                    pointDy = (pathData.point.y - pathData.lastPoint.y) / SMOOTH_VAL
                } else {
                    pointDx = (nextPoint.x - pathData.lastPoint.x) / SMOOTH_VAL
                    pointDy = (nextPoint.y - pathData.lastPoint.y) / SMOOTH_VAL
                }
            }
            val lastPointDx =
                ((pathData.point.x - pathData.beforeLastPoint.x) / SMOOTH_VAL)
                    .let { if (it == 0f) brushStrokePaint.strokeWidth else it }
            val lastPointDy =
                ((pathData.point.y - pathData.beforeLastPoint.y) / SMOOTH_VAL)
                    .let { if (it == 0f) brushStrokePaint.strokeWidth else it }
            path.cubicTo(
                pathData.lastPoint.x + lastPointDx,
                pathData.lastPoint.y + lastPointDy,
                pathData.point.x - pointDx,
                pathData.point.y - pointDy,
                pathData.point.x,
                pathData.point.y
            )
        }
    }

    companion object {
        private const val SMOOTH_VAL = 3
    }
}