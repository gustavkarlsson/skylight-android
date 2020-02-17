package se.gustavkarlsson.skylight.android.feature.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_settings_item.view.*

internal class SettingsAdapter(
    private val viewModel: SettingsViewModel
) : RecyclerView.Adapter<ViewHolder>() {

    private var items: List<TriggerLevelItem> = emptyList()

    fun setItems(newItems: List<TriggerLevelItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_settings_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val view = holder.itemView
        view.title.text = item.title.resolve(view.context)
        view.subtitle.text = item.subtitle.resolve(view.context)
        view.setOnClickListener {
            viewModel.onItemClicked(item.place)
        }
    }
}

internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
