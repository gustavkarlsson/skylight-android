package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Place

class PlacesAdapter : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

	var items: List<Place> = emptyList()
		set(value) {
			val diff = DiffUtil.calculateDiff(DiffCallback(items, value))
			field = value
			diff.dispatchUpdatesTo(this)
		}

	override fun getItemCount() = items.size + 1

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.layout_place_item,
			parent,
			false
		) as TextView
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val text = if (position == 0) {
			"Current location" // FIXME string resource
		} else {
			items[position - 1].name
		}
		holder.view.text = text
	}

	class DiffCallback(
		private val oldItems: List<Place>,
		private val newItems: List<Place>
	) : DiffUtil.Callback() {

		override fun getOldListSize() = oldItems.size + 1

		override fun getNewListSize() = newItems.size + 1

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			if (oldItemPosition == 0 && newItemPosition == 0) return true
			if (oldItemPosition == 0 || newItemPosition == 0) return false
			val oldLocation = oldItems[oldItemPosition - 1].location
			val newLocation = newItems[newItemPosition - 1].location
			return oldLocation == newLocation
		}

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			if (oldItemPosition == 0 && newItemPosition == 0) return true
			if (oldItemPosition == 0 || newItemPosition == 0) return false
			val oldPlace = oldItems[oldItemPosition - 1]
			val newPlace = newItems[newItemPosition - 1]
			return oldPlace == newPlace
		}

	}

	class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)
}
