package ly.img.awesomebrushapplication.composer

import kotlin.reflect.KClass

typealias RendererRegistryFactory = () -> RendererRegistry

fun renderRegistry(lambda: RendererRegistry.Builder.() -> Unit) =
    RendererRegistry.Builder().apply(lambda).build()

class RendererRegistry private constructor(private val registry: MutableMap<KClass<out RendererData>, Lazy<Renderer<RendererData>>>) {
    class Builder {
        private val registry: MutableMap<KClass<out RendererData>, Lazy<Renderer<RendererData>>> = HashMap()
        inline fun <reified T : RendererData> register(renderer: Lazy<Renderer<T>>) {
            register(T::class, renderer)
        }

        fun <T : RendererData> register(dataClass: KClass<out T>, renderer: Lazy<Renderer<T>>) {
            registry[dataClass] = renderer as Lazy<Renderer<RendererData>>
        }

        fun build(): RendererRegistry {
            return RendererRegistry(registry)
        }
    }

    operator fun get(data: RendererData) = registry[data::class]?.value
}