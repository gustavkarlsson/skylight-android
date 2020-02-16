package se.gustavkarlsson.skylight.android.feature.addplace

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannedString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.layout_save_dialog.view.*
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
		val item = items[position]
		view.text = item.createText()
		view.setOnClickListener { openSaveDialog(view.context, item) }
	}

	// TODO dismiss on activity stopped
	@SuppressLint("InflateParams")
	private fun openSaveDialog(context: Context, placeSuggestion: PlaceSuggestion) {
		val customView = LayoutInflater.from(context).inflate(R.layout.layout_save_dialog, null)
		val editText = customView.placeNameEditText.apply {
			setText(placeSuggestion.simpleName)
			setSelection(0, placeSuggestion.simpleName.length)
		}
		val onClick = {
			val name = editText.text.toString().trim()
			viewModel.onSavePlaceClicked(name, placeSuggestion.location)
		}
		val dialog = createDialog(customView, onClick)
		dialog.show()
		dialog.initView(editText)
		editText.requestFocus()
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


private fun createDialog(
	view: View,
	onClick: () -> Unit
): AlertDialog =
	MaterialAlertDialogBuilder(view.context).apply {
		setView(view)
		setTitle(R.string.save_place)
		setNegativeButton(R.string.cancel, null)
		setPositiveButton(R.string.save) { _, _ -> onClick() }
	}.create()

private fun AlertDialog.initView(editText: TextInputEditText) {
	val positiveButton = getButton(AlertDialog.BUTTON_POSITIVE)
	val textChangeDisposable = editText.textChanges()
		.map(CharSequence::isNotBlank)
		.subscribe { positiveButton.isEnabled = it }
	setOnDismissListener { textChangeDisposable.dispose() }
}
