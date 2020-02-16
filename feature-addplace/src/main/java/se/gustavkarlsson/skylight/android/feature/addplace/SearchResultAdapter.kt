package se.gustavkarlsson.skylight.android.feature.addplace

import android.text.SpannedString
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion

internal class SearchResultAdapter(
	private val viewModel: AddPlaceViewModel
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

	private var items: List<PlaceSuggestion> = emptyList()

	fun setItems(items: List<PlaceSuggestion>) {
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
		val suggestion = items[position]
		view.text = suggestion.createText()
		view.setOnClickListener { viewModel.onSuggestionClicked(suggestion) }
	}

	class ViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)
}

private fun PlaceSuggestion.createText(): SpannedString {
	val subTitle = createSubTitle()
	return buildSpannedString {
		bold { append(simpleName) }
		if (subTitle.isNotBlank()) {
			appendln()
			append(subTitle)
		}
	}
}

private fun PlaceSuggestion.createSubTitle() =
	fullName
		.removePrefix(simpleName)
		.dropWhile { !it.isLetterOrDigit() }
