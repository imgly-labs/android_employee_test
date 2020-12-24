package ly.img.awesomebrushapplication

import ly.img.awesomebrushapplication.composer.RendererRegistryFactory
import ly.img.awesomebrushapplication.composer.renderRegistry
import ly.img.awesomebrushapplication.composer.renderer.BrushRenderer


object Shared {
    val renderRegistryFactory: RendererRegistryFactory = {
        renderRegistry {
            register(lazy { BrushRenderer() })
        }
    }
}