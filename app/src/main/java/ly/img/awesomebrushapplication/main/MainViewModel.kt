package ly.img.awesomebrushapplication.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ly.img.awesomebrushapplication.R
import ly.img.awesomebrushapplication.Shared
import ly.img.awesomebrushapplication.composer.BitmapStorage
import ly.img.awesomebrushapplication.composer.ImageComposer

class MainViewModel : ViewModel() {
    val imageComposer = ImageComposer(Shared.renderRegistryFactory)
        .also {
            it.dataChangedCallback = { update() }
        }
    val canUndo = MutableLiveData<Boolean>()
    val canRedo = MutableLiveData<Boolean>()
    val isBusy = MutableLiveData<Boolean>()
    val selected = MutableLiveData<String>()
    val pluginItems = listOf(PluginItem("brush", R.string.plugin_brush, R.drawable.ic_baseline_brush_24))
    private val scope = CoroutineScope(Job() + Dispatchers.Main)
    fun undo() {
        imageComposer.lastIndexToRender = (imageComposer.lastIndexToRender ?: imageComposer.dataCount) - 1
        update()
    }

    fun redo() {
        imageComposer.lastIndexToRender = imageComposer.lastIndexToRender?.plus(1)
        update()
    }

    fun selectPlugin(pluginItem: PluginItem) {
        selected.value = pluginItem.id
    }

    fun closeOptions() {
        selected.value = null
    }

    private fun update() {
        canUndo.value = imageComposer.lastIndexToRender ?: imageComposer.dataCount > 0
        canRedo.value = imageComposer.lastIndexToRender?.let { it < imageComposer.dataCount } ?: false
    }

    fun saveBrushToGallery(applicationContext: Context) {
        scope.launch {
            isBusy.value = true
            withContext(Dispatchers.IO) {
                imageComposer.render(applicationContext)?.also {
                    BitmapStorage.saveBitmap(applicationContext, it, "edited${System.currentTimeMillis()}")
                }
            }
            isBusy.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}