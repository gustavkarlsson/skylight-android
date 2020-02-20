package se.gustavkarlsson.skylight.android.feature.addplace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_search_result_item.view.*

internal class SearchResultAdapter(
    private val viewModel: AddPlaceViewModel
) : RecyclerView.Adapter<ViewHolder>() {

    private var items: List<SuggestionItem> = emptyList()

    fun setItems(items: List<SuggestionItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_search_result_item,
            parent,
            false
        ) as ViewGroup
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.view
        val item = items[position]
        view.line1TextView.text = item.textLine1
        view.line2TextView.text = item.textLine2
        view.setOnClickListener { viewModel.onSuggestionClicked(item.suggestion) }
    }
}

internal class ViewHolder(val view: ViewGroup) : RecyclerView.ViewHolder(view)
