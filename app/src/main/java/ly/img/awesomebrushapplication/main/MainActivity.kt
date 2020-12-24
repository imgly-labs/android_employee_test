package ly.img.awesomebrushapplication.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ly.img.awesomebrushapplication.R
import ly.img.awesomebrushapplication.databinding.ActivityMainBinding
import ly.img.awesomebrushapplication.editor.BrushCanvas
import ly.img.awesomebrushapplication.plugins.RendererPlugin
import ly.img.awesomebrushapplication.plugins.brush.BrushRendererOptionsViewModel
import ly.img.awesomebrushapplication.plugins.brush.BrushRendererPlugin
import ly.img.awesomebrushapplication.plugins.brush.BrushRendererTouchHandler


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[MainViewModel::class.java]
        binding.vm = viewModel
        binding.rcvPlugins.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.rcvPlugins.adapter = PluginsAdapter(viewModel.pluginItems) {
            viewModel.selectPlugin(it)
        }
        viewModel.selected.observe(this) {
            activatePlugin(it)
        }
        binding.brushCanvas.image = viewModel.imageComposer.previewImage
        binding.lifecycleOwner = this
    }

    private fun activatePlugin(id: String?) {
        if (id == null) {
            supportFragmentManager.findFragmentById(R.id.container)?.also { fragment ->
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        } else {
            val plugin: RendererPlugin? = when (id) {
                else -> BrushRendererPlugin(
                    BrushRendererTouchHandler(viewModel.imageComposer),
                    ViewModelProvider(
                        this,
                        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    )[BrushRendererOptionsViewModel::class.java]
                )
            }
            plugin?.also {
                if (it is BrushCanvas.TouchCallback) {
                    binding.brushCanvas.touchCallback = it
                }
                plugin.createOptionsFragment()?.also { fragment ->
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                }
            }
            BrushRendererPlugin(
                BrushRendererTouchHandler(viewModel.imageComposer),
                ViewModelProvider(
                    this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                )[BrushRendererOptionsViewModel::class.java]
            ).also { plugin ->
                binding.brushCanvas.touchCallback = plugin
                plugin.createOptionsFragment()?.also { fragment ->
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_load -> onPressLoadImage()
            R.id.action_save -> viewModel.saveBrushToGallery(applicationContext)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onPressLoadImage() {
        val intent = Intent(Intent.ACTION_PICK)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            intent.type = "image/*"
        } else {
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        startActivityForResult(intent, GALLERY_INTENT_RESULT)
    }

    private fun handleGalleryImage(uri: Uri) {
        viewModel.imageComposer.load(applicationContext, uri)
        binding.brushCanvas.image = viewModel.imageComposer.previewImage
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == GALLERY_INTENT_RESULT) {
            val uri = data.data
            if (uri != null) {
                handleGalleryImage(uri)
            }
        }
    }

    companion object {
        const val GALLERY_INTENT_RESULT = 0
    }
}