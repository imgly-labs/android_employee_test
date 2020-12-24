package ly.img.awesomebrushapplication.plugins.brush

import android.graphics.Color
import android.graphics.PointF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.img.awesomebrushapplication.composer.renderer.BrushRendererData

class BrushRendererOptionsViewModel : ViewModel() {
    val strokeWidth = MutableLiveData<Int>().apply { value = 50 }
    val colorAlpha = MutableLiveData<Int>().apply { value = 255 }
    val colorRed = MutableLiveData<Int>()
    val colorGreen = MutableLiveData<Int>()
    val colorBlue = MutableLiveData<Int>()

    val previewBrushRendererData: LiveData<BrushRendererData> = MediatorLiveData<BrushRendererData>().apply {
        fun update() {
            value = BrushRendererData(
                color,
                width,
                mutableListOf(PointF(0f, 0f), PointF(0.9f, 0.5f), PointF(0f, 1f))
            )
        }
        addSource(colorRed) { update() }
        addSource(colorGreen) { update() }
        addSource(colorBlue) { update() }
        addSource(colorAlpha) { update() }
        addSource(strokeWidth) { update() }
        update()
    }
    val color: Int
        get() = Color.argb(colorAlpha.value ?: 0, colorRed.value ?: 0, colorGreen.value ?: 0, colorBlue.value ?: 0)

    val width: Float
        get() = (strokeWidth.value ?: 1) / 1000f
}