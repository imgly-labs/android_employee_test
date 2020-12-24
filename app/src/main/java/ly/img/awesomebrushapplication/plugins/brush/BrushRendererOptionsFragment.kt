package ly.img.awesomebrushapplication.plugins.brush

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ly.img.awesomebrushapplication.Shared
import ly.img.awesomebrushapplication.composer.DataRenderer
import ly.img.awesomebrushapplication.databinding.FragmentBrushOptionsBinding
import ly.img.awesomebrushapplication.editor.RendererDrawable

class BrushRendererOptionsFragment : Fragment() {
    private lateinit var viewModel: BrushRendererOptionsViewModel
    private var binding: FragmentBrushOptionsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity().viewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireContext().applicationContext as Application)
        )[BrushRendererOptionsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentBrushOptionsBinding.inflate(inflater, container, false)
            .also { binding ->
                this.binding = binding
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawable = RendererDrawable(DataRenderer(Shared.renderRegistryFactory()), listOf())
        binding?.also { binding ->
            binding.vm = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
            binding.vColorPreview.setBackgroundDrawable(drawable)
        }

        viewModel.previewBrushRendererData.observe(viewLifecycleOwner) {
            drawable.data = listOf(it)
            drawable.invalidateSelf()
        }
    }
}