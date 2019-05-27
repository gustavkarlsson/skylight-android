package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding2.widget.textChanges
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_add_place.searchResultRecyclerView
import kotlinx.android.synthetic.main.fragment_add_place.toolbarView
import kotlinx.android.synthetic.main.layout_save_dialog.view.placeNameEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment

internal class AddPlaceFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_add_place

	private val viewModel: AddPlaceViewModel by viewModel()

	override val toolbar: Toolbar?
		get() = toolbarView

	private val searchView: SearchView
		get() = (toolbarView.menu.findItem(R.id.action_search).actionView as SearchView)

	private val adapter = SearchResultAdapter()

	override fun initView() {
		toolbarView.inflateMenu(R.menu.add_place_menu)
		searchResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
		searchResultRecyclerView.adapter = adapter
		searchView.setIconifiedByDefault(false)
		searchView.queryHint = getString(R.string.search_for_place)
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		searchView.setQueryTextChangeListener(viewModel::onSearchTextChanged)

		viewModel.searchResultItems
			.autoDisposable(scope)
			.subscribe(adapter::setItems)

		viewModel.isLoading
			.autoDisposable(scope)
			.subscribe {
				// TODO no progress indicator yet.
				//  https://material.io/design/components/progress-indicators.html#implementation
			}

		viewModel.openSaveDialog
			.autoDisposable(scope)
			.subscribe(::openSaveDialog)
	}

	private fun openSaveDialog(dialogData: SaveDialogData) {
		val customView = layoutInflater.inflate(R.layout.layout_save_dialog, null)
		val editText = customView.placeNameEditText.apply {
			setText(dialogData.suggestedName)
			setSelection(dialogData.suggestedName.length)
		}
		MaterialAlertDialogBuilder(requireContext()).apply {
			setView(customView)
			setTitle(R.string.save_place)
			setNegativeButton(R.string.cancel, null)
			setPositiveButton(R.string.save) { _, _ -> dialogData.onSave(editText.text.toString()) }
		}.create().run {
			show()
			val positiveButton = getButton(AlertDialog.BUTTON_POSITIVE)
			val textChangeDisposable = editText.textChanges()
				.map(CharSequence::isNotBlank)
				.subscribe {
					positiveButton.isEnabled = it
				}
			setOnDismissListener { textChangeDisposable.dispose() }
		}
	}
}

private fun SearchView.setQueryTextChangeListener(callback: (String) -> Unit) {
	setOnQueryTextListener(object : SearchView.OnQueryTextListener {
		override fun onQueryTextSubmit(query: String) = false

		override fun onQueryTextChange(newText: String): Boolean {
			callback(newText)
			return true
		}
	})
}
