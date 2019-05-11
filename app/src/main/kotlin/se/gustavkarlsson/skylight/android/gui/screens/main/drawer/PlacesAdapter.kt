package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.gustavkarlsson.skylight.android.R

class PlacesAdapter(
	private val closeDrawer: () -> Unit
) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

	var items: List<PlaceItem> = emptyList()
		set(value) {
			val diff = DiffUtil.calculateDiff(DiffCallback(items, value))
			field = value
			diff.dispatchUpdatesTo(this)
		}

	override fun getItemCount() = items.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.layout_place_item,
			parent,
			false
		) as TextView
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val view = holder.view
		val context = view.context
		val item = items[position]
		view.text = item.name.resolve(context)
		view.isSelected = item.isActive
		view.setOnClickListener {
			item.onClick()
			closeDrawer()
		}
	}

	class DiffCallback(
		private val oldItems: List<PlaceItem>,
		private val newItems: List<PlaceItem>
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