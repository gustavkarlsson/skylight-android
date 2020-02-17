package se.gustavkarlsson.skylight.android.feature.main.gui.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.gustavkarlsson.skylight.android.feature.main.R

internal class DrawerAdapter(
    private val viewModel: DrawerViewModel
) : RecyclerView.Adapter<ViewHolder>() {

    private var items: List<DrawerItem> = emptyList()

    fun setItems(items: List<DrawerItem>) {
        val diff = DiffUtil.calculateDiff(DiffCallback(this.items, items))
        this.items = items
        diff.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_drawer_item,
            parent,
            false
        ) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.view
        val context = view.context
        val item = items[position]
        view.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, 0, 0, 0)
        view.text = item.text.resolve(context)
        view.isSelected = item.isActive
        item.clickEvent?.let { event ->
            view.setOnClickListener {
                viewModel.onEvent(event)
            }
        }
        item.longClickEvent?.let { event ->
            view.setOnLongClickListener {
                viewModel.onEvent(event)
                true
            }
        }
    }

    class DiffCallback(
        private val oldItems: List<DrawerItem>,
        private val newItems: List<DrawerItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldLocation = oldItems[oldItemPosition].text
            val newLocation = newItems[newItemPosition].text
            return oldLocation == newLocation
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldPlace = oldItems[oldItemPosition]
            val newPlace = newItems[newItemPosition]
            return oldPlace == newPlace
        }
    }
}

internal class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)
