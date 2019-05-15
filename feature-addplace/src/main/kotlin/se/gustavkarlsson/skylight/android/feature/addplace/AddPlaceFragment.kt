package se.gustavkarlsson.skylight.android.feature.addplace

import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.visibility
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_add_place.progressBar
import kotlinx.android.synthetic.main.fragment_add_place.searchResultRecyclerView
import kotlinx.android.synthetic.main.fragment_add_place.toolbarView
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.feature.base.BaseFragment
import se.gustavkarlsson.skylight.android.feature.base.showKeyboard

class AddPlaceFragment : BaseFragment(R.layout.fragment_add_place) {

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
		searchView.showKeyboard()
	}

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		searchView.setQueryTextChangeListener(viewModel::onSearchTextChanged)

		viewModel.searchResultItems
			.autoDisposable(scope)
			.subscribe { adapter.items = it }

		viewModel.isLoading
			.autoDisposable(scope)
			.subscribe(progressBar.visibility(View.INVISIBLE))

		viewModel.openSaveDialog
			.autoDisposable(scope)
			.subscribe {
				// FIXME show dialog
				Toast.makeText(requireContext(), "Showing dialog to save ${it.first}", Toast.LENGTH_LONG).show()
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
