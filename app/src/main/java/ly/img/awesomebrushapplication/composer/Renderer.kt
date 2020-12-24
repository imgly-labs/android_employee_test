package ly.img.awesomebrushapplication.composer

import android.graphics.Canvas

interface Renderer<T> {
    fun render(canvas: Canvas, data: T)
}