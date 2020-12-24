package ly.img.awesomebrushapplication.plugins

import androidx.fragment.app.Fragment

interface RendererPlugin {
    fun createOptionsFragment(): Fragment?
}