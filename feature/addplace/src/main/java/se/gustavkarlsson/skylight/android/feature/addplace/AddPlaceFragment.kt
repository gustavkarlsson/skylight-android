package se.gustavkarlsson.skylight.android.feature.addplace

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.ioki.textref.TextRef
import com.jakewharton.rxbinding3.widget.textChanges
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_add_place.*
import kotlinx.android.synthetic.main.layout_save_dialog.view.*
import se.gustavkarlsson.skylight.android.lib.geocoder.PlaceSuggestion
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.ScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind
import se.gustavkarlsson.skylight.android.lib.ui.extensions.fadeToVisible
import se.gustavkarlsson.skylight.android.lib.ui.extensions.showSnackbar

class AddPlaceFragment : ScreenFragment() {

    override val layoutId: Int = R.layout.fragment_add_place

    private val viewModel by lazy {
        getOrRegisterService("addPlaceViewModel") { AddPlaceComponent.build().viewModel() }
    }

    override val toolbar: MaterialToolbar get() = toolbarView

    private val searchView: SearchView
        get() = (toolbarView.menu.findItem(R.id.action_search).actionView as SearchView)

    private val adapter by lazy { SearchResultAdapter(viewModel) }

    private var errorSnackbarAndMessage: Pair<Snackbar, TextRef>? = null

    private var savePlaceDialog: DialogInterface? = null

    override fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        toolbarView.fit { Edge.Top }
        searchResultRecyclerView.fit { Edge.Bottom }
    }

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
        savePlaceDialog?.dismiss()
        savePlaceDialog = null
        searchResultRecyclerView.adapter = null
        super.onDestroyView()
    }

    override fun bindData() {
        searchView.setQueryTextChangeListener(viewModel::onSearchTextChanged)

        viewModel.placeSuggestions.bind(this, adapter::setItems)

        viewModel.openSaveDialog.bind(this, ::openSaveDialog)

        viewModel.isEmptyVisible.bind(this) { visible ->
            emptyView.fadeToVisible(visible)
        }

        viewModel.isSearchingVisible.bind(this) { visible ->
            searchingView.fadeToVisible(visible)
        }

        viewModel.isNoSuggestionsVisible.bind(this) { visible ->
            noSuggestionsView.fadeToVisible(visible)
        }

        viewModel.isSuggestionsVisible.bind(this) { visible ->
            searchResultRecyclerView.fadeToVisible(visible, View.INVISIBLE)
        }

        viewModel.navigateAway.bind(this) {
            val target = arguments?.target
            if (target != null) {
                navigator.setBackstack(target)
            } else {
                navigator.closeScreen()
            }
        }

        viewModel.errorMessages.bind(this) { message ->
            errorSnackbarAndMessage.handleNewMessage(message)
        }
    }

    @SuppressLint("InflateParams")
    private fun openSaveDialog(placeSuggestion: PlaceSuggestion) {
        savePlaceDialog?.dismiss()
        val customView = layoutInflater.inflate(R.layout.layout_save_dialog, null)
        val editText = customView.placeNameEditText.apply {
            setText(placeSuggestion.simpleName)
            setSelection(0, placeSuggestion.simpleName.length)
        }
        val dialog = createDialog(customView) {
            val name = editText.text.toString().trim()
            viewModel.onSavePlaceClicked(name, placeSuggestion.location)
        }
        savePlaceDialog = dialog
        dialog.show()
        dialog.initView(editText)
        editText.requestFocus()
    }

    private fun Pair<Snackbar, TextRef>?.handleNewMessage(message: TextRef) {
        val (oldSnackbar, oldMessage) = this ?: null to null
        if (oldSnackbar == null || !oldSnackbar.isShown || oldMessage != message) {
            oldSnackbar?.dismiss()
            errorSnackbarAndMessage = showSnackbar(searchResultRecyclerView, message) {
                setIndefiniteDuration()
                setErrorStyle()
                setDismiss(R.string.dismiss)
            } to message
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

private fun createDialog(view: View, onClick: () -> Unit) =
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
