package ly.img.awesomebrushapplication.plugins.brush

import androidx.fragment.app.Fragment
import ly.img.awesomebrushapplication.editor.BrushCanvas
import ly.img.awesomebrushapplication.plugins.RendererPlugin

class BrushRendererPlugin(private val touchHandler: BrushRendererTouchHandler, private val options: BrushRendererOptionsViewModel) :
    RendererPlugin,
    BrushCanvas.TouchCallback {

    override fun createOptionsFragment(): Fragment? {
        return BrushRendererOptionsFragment()
    }

    override fun onTouchStart(x: Float, y: Float) {
        touchHandler.touchStart(x, y, options.width, options.color)
    }

    override fun onTouchMove(x: Float, y: Float) {
        touchHandler.touchMove(x, y)
    }

    override fun onTouchEnd(x: Float, y: Float) {
        touchHandler.touchEnd(x, y)
    }
}