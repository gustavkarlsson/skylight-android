package se.gustavkarlsson.skylight.android.feature.addplace

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

internal class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

	var items: List<SearchResultItem> = emptyList()
		set(value) {
			val diff = DiffUtil.calculateDiff(DiffCallback(items, value))
			field = value
			diff.dispatchUpdatesTo(this)
		}

	override fun getItemCount() = items.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(
			R.layout.layout_search_result_item,
			parent,
			false
		) as TextView
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val view = holder.view
		val item = items[position]
		view.text = item.text
		view.setOnClickListener { item.onClick() }
	}

	class DiffCallback(
		private val oldItems: List<SearchResultItem>,
		private val newItems: List<SearchResultItem>
	) : DiffUtil.Callback() {

		override fun getOldListSize() = oldItems.size

		override fun getNewListSize() = newItems.size

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			val oldItem = oldItems[oldItemPosition]
			val newItem = newItems[newItemPosition]
			return oldItem.text == newItem.text
		}

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
			areItemsTheSame(oldItemPosition, newItemPosition)

	}

	class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)


}
