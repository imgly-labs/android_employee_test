package ly.img.awesomebrushapplication.editor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min


class BrushCanvas @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    interface TouchCallback {
        fun onTouchStart(x: Float, y: Float)
        fun onTouchMove(x: Float, y: Float)
        fun onTouchEnd(x: Float, y: Float)
    }

    private val position = PointF()
    private var scale = 1f
    private val tmpPoint = PointF()
    var touchCallback: TouchCallback? = null

    var image: Drawable? = null
        set(value) {
            field?.callback = null
            field = value
            field?.callback = this
            scaleImage()
        }

    init {
        setWillNotDraw(false)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scaleImage()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && transformTouchToNormalizedPoint(event.x, event.y, tmpPoint) != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchCallback?.onTouchStart(tmpPoint.x, tmpPoint.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    touchCallback?.onTouchMove(tmpPoint.x, tmpPoint.y)
                }
                MotionEvent.ACTION_UP -> {
                    touchCallback?.onTouchEnd(tmpPoint.x, tmpPoint.y)
                }
            }
        }
        return true
    }

    override fun invalidateDrawable(drawable: Drawable) {
        super.invalidateDrawable(drawable)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(position.x, position.y)
        canvas.scale(scale, scale)
        image?.draw(canvas)
        canvas.restore()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        image?.callback = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        image?.callback = this
    }

    private fun scaleImage() {
        image?.bounds?.also { bounds ->
            val srcRect = RectF(bounds)
            Matrix().also {
                it.reset()
                it.setRectToRect(srcRect, RectF(0f, 0f, width.toFloat(), height.toFloat()), Matrix.ScaleToFit.START)
                it.mapRect(srcRect)
            }
            scale = min(width.toFloat() / bounds.width().toFloat(), height.toFloat() / bounds.height().toFloat())
            position.set((width - srcRect.width()) / 2f, (height - srcRect.height()) / 2f)
        }
        invalidate()
    }

    private fun transformTouchToNormalizedPoint(x: Float, y: Float, outPoint: PointF): PointF? {
        return image?.bounds?.let { bounds ->
            outPoint.set(
                (x - position.x) * bounds.width() / (bounds.width() * scale) / bounds.width(),
                (y - position.y) * bounds.height() / (bounds.height() * scale) / bounds.height()
            )
            outPoint
        } ?: outPoint.let {
            it.set(-1f, -1f)
            null
        }
    }
}