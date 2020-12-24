package ly.img.awesomebrushapplication.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ly.img.awesomebrushapplication.databinding.ItemPluginBinding

class PluginsAdapter(private val dataSet: List<PluginItem>, private val itemClickCallback: (PluginItem) -> Unit) :
    RecyclerView.Adapter<PluginsAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemPluginBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemPluginBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.binding.also {
            it.model = dataSet[position]
            it.root.setOnClickListener { onClickItem(viewHolder) }
        }.executePendingBindings()
    }

    private fun onClickItem(vh: ViewHolder) {
        vh.binding.model?.also {
            itemClickCallback(it)
        }
    }

    override fun getItemCount() = dataSet.size
}