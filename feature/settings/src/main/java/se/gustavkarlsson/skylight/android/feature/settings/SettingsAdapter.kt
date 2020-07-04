package se.gustavkarlsson.skylight.android.feature.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_settings_trigger_level_item.view.*

internal class SettingsAdapter(
    private val viewModel: SettingsViewModel
) : RecyclerView.Adapter<ViewHolder>() {

    private var items: List<SettingsItem> = emptyList()

    fun setItems(newItems: List<SettingsItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = when (viewType) {
            TYPE_TITLE -> R.layout.layout_settings_title_item
            TYPE_TRIGGER_LEVEL -> R.layout.layout_settings_trigger_level_item
            else -> error("Unsupported view type: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val triggerLevelItem = items[position] as? SettingsItem.TriggerLevelItem
        if (triggerLevelItem != null) holder.itemView.bind(triggerLevelItem)
    }

    private fun View.bind(item: SettingsItem.TriggerLevelItem) {
        title.text = item.title.resolve(context)
        subtitle.text = item.subtitle.resolve(context)
        setOnClickListener {
            viewModel.onTriggerLevelItemClicked(item.place, item.triggerLevel)
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            SettingsItem.TitleItem -> TYPE_TITLE
            is SettingsItem.TriggerLevelItem -> TYPE_TRIGGER_LEVEL
        }
}

internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

private const val TYPE_TITLE = 1
private const val TYPE_TRIGGER_LEVEL = 2
