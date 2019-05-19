package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.gustavkarlsson.skylight.android.R

class DrawerAdapter : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

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
		view.text = item.name.resolve(context)
		view.isSelected = item.isActive
		view.setOnClickListener {
			item.onClick()
		}
		view.setOnLongClickListener {
			item.onLongClick()
			true
		}
	}

	class DiffCallback(
		private val oldItems: List<DrawerItem>,
		private val newItems: List<DrawerItem>
	) : DiffUtil.Callback() {

		override fun getOldListSize() = oldItems.size

		override fun getNewListSize() = newItems.size

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			val oldLocation = oldItems[oldItemPosition].name
			val newLocation = newItems[newItemPosition].name
			return oldLocation == newLocation
		}

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			val oldPlace = oldItems[oldItemPosition]
			val newPlace = newItems[newItemPosition]
			return oldPlace == newPlace
		}

	}

	class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)
}
