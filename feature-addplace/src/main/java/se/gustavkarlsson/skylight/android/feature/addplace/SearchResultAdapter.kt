package se.gustavkarlsson.skylight.android.feature.addplace

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

	private var items: List<SearchResultItem> = emptyList()

	fun setItems(items: List<SearchResultItem>) {
		this.items = items
		notifyDataSetChanged()
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

	class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)
}
