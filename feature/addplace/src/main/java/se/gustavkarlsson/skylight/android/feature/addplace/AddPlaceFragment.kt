package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.ioki.textref.TextRef
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@ExperimentalCoroutinesApi
@FlowPreview
class AddPlaceFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("addPlaceViewModel") { AddPlaceComponent.build().viewModel() }
    }

    override fun onNewCreateDestroyScope(scope: CoroutineScope) {
        scope.launch {
            viewModel.navigateAway.collect {
                val target = arguments?.target
                if (target != null) {
                    navigator.setBackstack(target)
                } else {
                    navigator.closeScreen()
                }
            }
        }
    }
/*
// FIXME Add dialog

    @SuppressLint("InflateParams")
    private fun openSaveDialog(scope: CoroutineScope, placeSuggestion: PlaceSuggestion) {
        savePlaceDialog?.dismiss()
        val customView = layoutInflater.inflate(R.layout.layout_save_dialog, null)
        val editText = customView.placeNameEditText.apply {
            setText(placeSuggestion.simpleName)
            setSelection(0, placeSuggestion.simpleName.length)
        }
        val dialog = createDialog(customView) {
            val name = editText.text.toString().trim()
            scope.launch {
                viewModel.onSavePlaceClicked(name, placeSuggestion.location)
            }
        }
        savePlaceDialog = dialog
        dialog.show()
        dialog.initView(scope, editText)
        editText.requestFocus()
    }

    private fun createDialog(view: View, onClick: () -> Unit) =
        MaterialAlertDialogBuilder(view.context).apply {
            setView(view)
            setTitle(R.string.save_place)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.save) { _, _ -> onClick() }
        }.create()

    private fun AlertDialog.initView(scope: CoroutineScope, editText: TextInputEditText) {
        val positiveButton = getButton(AlertDialog.BUTTON_POSITIVE)
        val job = scope.launch {
            editText.textChanges()
                .map { it.isNotBlank() }
                .collect { positiveButton.isEnabled = it }
        }
        setOnDismissListener { job.cancel() }
    }
}
*/

    @ExperimentalMaterialApi
    @Composable
    override fun ScreenContent() {
        val state = viewModel.viewState.collectAsState(ViewState.default)
        Content(
            viewState = state.value,
            onBackClicked = { navigator.closeScreen() },
            onQueryChanged = { text -> viewModel.onSearchTextChanged(text) },
            onClickItem = { name, location -> viewModel.onSavePlaceClicked(name, location) },
            onSnackbarDismissed = { viewModel.onSnackbarDismissed() }
        )
    }
}

@ExperimentalMaterialApi
@Composable
@Preview
private fun Content(
    viewState: ViewState = ViewState.default,
    onBackClicked: () -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onClickItem: (name: String, Location) -> Unit = { _, _ -> },
    onSnackbarDismissed: () -> Unit = {},
) {
    ScreenBackground {
        Scaffold(
            topBar = { TopAppBar(viewState.query, onBackClicked, onQueryChanged) },
            snackbarHost = { state -> ErrorSnackbar(state, viewState.error, onSnackbarDismissed = onSnackbarDismissed) }
        ) {
            when (viewState) {
                is ViewState.Empty -> Empty()
                is ViewState.Searching -> Searching() // FIXME debounce searching state, or show progress bar instead?
                is ViewState.NoSuggestions -> NoSuggestions()
                is ViewState.Suggestions -> SuggestionsList(viewState.suggestions, onClickItem)
            }
        }
    }
}

@Composable
private fun TopAppBar(
    query: String,
    onBackClicked: () -> Unit,
    onQueryChanged: (String) -> Unit,
) {
    TopAppBar(
        contentPadding = LocalWindowInsets.current.statusBars
            .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = {
            val focusRequester = remember { FocusRequester() }
            SideEffect {
                focusRequester.requestFocus()
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = query,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.body1,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                },
                placeholder = { Text(stringResource(R.string.place_name)) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.secondary,
                ),
            )
        },
    )
}

@Composable
private fun ErrorSnackbar(
    hostState: SnackbarHostState,
    error: TextRef?,
    onSnackbarDismissed: () -> Unit,
) {
    val messageText = error?.let { textRef(it) }
    val dismissText = stringResource(R.string.dismiss)
    if (messageText != null) {
        LaunchedEffect(key1 = messageText) {
            hostState.showSnackbar(
                message = messageText,
                actionLabel = dismissText,
                duration = SnackbarDuration.Long,
            )
            onSnackbarDismissed()
        }
    }
    SnackbarHost(
        modifier = Modifier.navigationBarsWithImePadding(),
        hostState = hostState,
    ) { snackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            backgroundColor = MaterialTheme.colors.error,
            actionColor = MaterialTheme.colors.onError,
        )
    }
}

@Composable
private fun Empty() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .wrapContentSize(Alignment.Center),
        text = stringResource(R.string.search_for_place_to_save),
    )
}

@Composable
private fun Searching() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .wrapContentSize(Alignment.Center),
        text = stringResource(R.string.searching),
    )
}

@Composable
private fun NoSuggestions() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .wrapContentSize(Alignment.Center),
        text = stringResource(R.string.no_place_found),
    )
}

@ExperimentalMaterialApi
@Composable
private fun SuggestionsList(
    items: List<SuggestionItem>,
    onClickItem: (name: String, Location) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsWithImePadding(),
    ) {
        items(items) { item ->
            PlaceItem(item, onClick = onClickItem)
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun PlaceItem(
    item: SuggestionItem,
    onClick: (name: String, Location) -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable { onClick(item.saveName, item.location) },
        icon = { Icon(Icons.Default.Map, contentDescription = null) },
        secondaryText = {
            Text(
                text = item.secondaryText, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    ) {
        Text(
            text = item.primaryText, maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
