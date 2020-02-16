package se.gustavkarlsson.skylight.android.feature.addplace

import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.ioki.textref.TextRef
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_add_place.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.ui.BaseFragment
import se.gustavkarlsson.skylight.android.lib.ui.argument
import se.gustavkarlsson.skylight.android.lib.ui.extensions.fadeToVisible
import se.gustavkarlsson.skylight.android.lib.ui.extensions.showErrorSnackbar

internal class AddPlaceFragment : BaseFragment() {

	override val layoutId: Int = R.layout.fragment_add_place

	private val destination: NavItem? by argument()

	private val viewModel: AddPlaceViewModel by viewModel {
		parametersOf(destination.toOptional())
	}

	override val toolbar: Toolbar? get() = toolbarView

	private val searchView: SearchView
		get() = (toolbarView.menu.findItem(R.id.action_search).actionView as SearchView)

	private val adapter by lazy { SearchResultAdapter(viewModel) }

	private var errorSnackbarAndMessage: Pair<Snackbar, TextRef>? = null

	override fun initView() {
		toolbarView.inflateMenu(R.menu.add_place_menu)
		searchResultRecyclerView.adapter = adapter
		searchView.fixMargins()
		searchView.setIconifiedByDefault(false)
		searchView.queryHint = getString(R.string.place_name)
	}

	override fun onDestroyView() {
		errorSnackbarAndMessage?.first?.dismiss()
		errorSnackbarAndMessage = null
		super.onDestroyView()
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		searchView.setQueryTextChangeListener(viewModel::onSearchTextChanged)

		viewModel.placeSuggestions
			.autoDisposable(scope)
			.subscribe(adapter::setItems)

		viewModel.isEmptyVisible
			.autoDisposable(scope)
			.subscribe { visible ->
				emptyView.fadeToVisible(visible)
			}

		viewModel.isNoSuggestionsVisible
			.autoDisposable(scope)
			.subscribe { visible ->
				noSuggestionsView.fadeToVisible(visible)
			}

		viewModel.isSuggestionsVisible
			.autoDisposable(scope)
			.subscribe { visible ->
				searchResultRecyclerView.fadeToVisible(visible, View.INVISIBLE)
			}

		viewModel.errorMessages
			.autoDisposable(scope)
			.subscribe { message ->
				errorSnackbarAndMessage.handleNewMessage(message)
			}
	}

	private fun Pair<Snackbar, TextRef>?.handleNewMessage(message: TextRef) {
		val (oldSnackbar, oldMessage) = this ?: null to null
		if (oldSnackbar == null || !oldSnackbar.isShown || oldMessage != message) {
			oldSnackbar?.dismiss()
			errorSnackbarAndMessage = showErrorSnackbar(
				searchResultRecyclerView,
				message,
				Snackbar.LENGTH_LONG
			) to message
		}
	}
}

private fun SearchView.fixMargins() {
	val linearLayout = findViewById<LinearLayout>(R.id.search_edit_frame)
	val params = linearLayout.layoutParams
	(params as LinearLayout.LayoutParams).leftMargin = -32
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
