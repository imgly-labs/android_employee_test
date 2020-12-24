package ly.img.awesomebrushapplication.composer.renderer

import android.graphics.*
import ly.img.awesomebrushapplication.composer.Renderer

class BrushRenderer : Renderer<BrushRendererData> {
    private val path = Path()
    private val matrix = Matrix()
    private val brushStrokePaint = Paint()
        .apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

    override fun render(canvas: Canvas, data: BrushRendererData) {
        render(canvas, data, 0)
    }

    fun render(canvas: Canvas, brushRendererData: BrushRendererData, index: Int) {
        invalidate(brushRendererData, (index - 1).coerceAtLeast(0))
        matrix.setScale(canvas.width.toFloat(), canvas.height.toFloat())
        path.transform(matrix)
        brushStrokePaint.color = brushRendererData.color
        brushStrokePaint.strokeWidth = brushRendererData.strokeWidth * canvas.width
        canvas.drawPath(path, brushStrokePaint)
    }

    private fun invalidate(brushRendererData: BrushRendererData, startAt: Int = 0) {
        brushRendererData.nextPoint(startAt)?.also {
            path.reset()
            path.moveTo(it.x, it.y)
        }
        for (i in startAt + 1..brushRendererData.points.size) {
            pathTo(
                brushRendererData.nextPoint(i),
                brushRendererData.point(i),
                brushRendererData.lastPoint(i),
                brushRendererData.beforeLastPoint(i)
            )
        }
    }

    private fun pathTo(nextPoint: PointF?, point: PointF, lastPoint: PointF, beforeLastPoint: PointF) {
        val pointDx: Float
        val pointDy: Float
        if (nextPoint == null) {
            pointDx = (point.x - lastPoint.x) / SMOOTH_VAL
            pointDy = (point.y - lastPoint.y) / SMOOTH_VAL
        } else {
            pointDx = (nextPoint.x - lastPoint.x) / SMOOTH_VAL
            pointDy = (nextPoint.y - lastPoint.y) / SMOOTH_VAL
        }
        val lastPointDx = ((point.x - beforeLastPoint.x) / SMOOTH_VAL)
        val lastPointDy = ((point.y - beforeLastPoint.y) / SMOOTH_VAL)
        path.cubicTo(
            lastPoint.x + lastPointDx,
            lastPoint.y + lastPointDy,
            point.x - pointDx,
            point.y - pointDy,
            point.x,
            point.y
        )
    }

    companion object {
        private const val SMOOTH_VAL = 10f
    }
}