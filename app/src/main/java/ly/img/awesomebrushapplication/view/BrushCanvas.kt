package ly.img.awesomebrushapplication.view


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ly.img.awesomebrushapplication.R
import kotlin.math.min
import kotlin.random.Random


class BrushCanvas @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var image: Bitmap
    private var pathRenderer = mutableListOf<PathRenderer>()

    init {
        setWillNotDraw(false)
        image = BitmapFactory.decodeResource(resources, R.drawable.landscape)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?) =
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    pathRenderer.add(
                        PathRenderer(
                            PathData(
                                color = Color.argb(
                                    255,
                                    155 + Random.nextInt(100),
                                    155 + Random.nextInt(100),
                                    155 + Random.nextInt(100)
                                ),
                                strokeWidth = Random.nextFloat() * 10 + 2
                            )
                        )
                    )
                    pathRenderer.last().pathData.addPoint(PointF(it.x, it.y))
                }
                MotionEvent.ACTION_MOVE -> {
                    pathRenderer.last().pathData.addPoint(PointF(it.x, it.y))
                }
                MotionEvent.ACTION_UP -> {
                    pathRenderer.last().pathData.addPoint(null)
                }
            }
            pathRenderer.last().updatePath()
            invalidate()
            true
        } ?: false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val scale = min(height.toFloat() / image.height, width.toFloat() / image.width)
        canvas.scale(scale, scale)
        canvas.drawBitmap(image, 0f, 0f, null)
        canvas.restore()
        pathRenderer.forEach {
            it.render(canvas)
        }
    }
}