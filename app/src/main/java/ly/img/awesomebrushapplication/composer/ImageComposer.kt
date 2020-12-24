package ly.img.awesomebrushapplication.composer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri

class ImageComposer(private val rendererRegistryFactory: RendererRegistryFactory) {
    var previewImage: BitmapDrawable? = null
        private set
    var previewCanvas: Canvas? = null
        private set
    private var image: Bitmap? = null
    private var rendererData: MutableList<RendererData> = mutableListOf()
        set(value) {
            field = value
            dataChangedCallback?.invoke()
        }
    private var uri: Uri? = null
    private val dataRenderer = DataRenderer(rendererRegistryFactory())
    var dataChangedCallback: (() -> Unit)? = null
    fun load(context: Context, uri: Uri, data: List<RendererData>? = null) {
        BitmapStorage.loadBitmap(context, uri, 1920)?.also { bmp ->
            this.uri = uri
            image = bmp
            createPreview(context, bmp.width, bmp.height)
            load(data ?: listOf())
        }
    }

    private fun createPreview(context: Context, width: Int, height: Int) {
        previewImage = BitmapDrawable(context.resources, Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565))
            .also { drawable ->
                drawable.setBounds(0, 0, drawable.bitmap.width, drawable.bitmap.height)
                previewCanvas = Canvas(drawable.bitmap)
            }
    }

    private fun load(data: List<RendererData>) {
        lastIndexToRender = null
        this.rendererData = data.toMutableList()
        invalidatePreviewImage()
    }

    private val filteredData: List<RendererData>
        get() = rendererData.take(lastIndexToRender ?: rendererData.size)

    fun add(data: RendererData) {
        lastIndexToRender?.also {
            rendererData = filteredData.toMutableList()
            _lastIndexToRender = null
        }
        rendererData.add(data)
        dataChangedCallback?.invoke()
    }

    val dataCount
        get() = rendererData.size

    fun render(context: Context): Bitmap? =
        uri?.let { uri ->
            DataRenderer(rendererRegistryFactory())
                .render(context, uri, filteredData)
        }

    private fun invalidatePreviewImage() {
        previewCanvas?.also { canvas ->
            image?.also {
                canvas.drawBitmap(it, 0f, 0f, null)
            }
            dataRenderer.render(canvas, filteredData)
        }
    }

    private var _lastIndexToRender: Int? = null
    var lastIndexToRender: Int?
        get() = _lastIndexToRender
        set(value) {
            value?.coerceIn(0, rendererData.size)
                .also {
                    if (it != _lastIndexToRender) {
                        _lastIndexToRender = it
                        invalidatePreviewImage()
                    }
                }
        }
}