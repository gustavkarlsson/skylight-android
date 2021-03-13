package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ListItem
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import se.gustavkarlsson.skylight.android.lib.ui.compose.ErrorSnackbar
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
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

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @Composable
    override fun ScreenContent() {
        val state = viewModel.viewState.collectAsState(ViewState())
        Content(
            viewState = state.value,
            onBackClicked = { navigator.closeScreen() },
            onQueryChanged = { text -> viewModel.onSearchTextChanged(text) },
            onSaveClicked = { name, location -> viewModel.onSavePlaceClicked(name, location) },
            onSnackbarDismissed = { viewModel.onSnackbarDismissed() }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
@Preview
private fun Content(
    viewState: ViewState = ViewState(),
    onBackClicked: () -> Unit = {},
    onQueryChanged: (String) -> Unit = {},
    onSaveClicked: (name: String, Location) -> Unit = { _, _ -> },
    onSnackbarDismissed: () -> Unit = {},
) {
    ScreenBackground {
        val dialogData = remember { mutableStateOf<DialogData?>(null) }
        val updateDialogData = { name: String, location: Location ->
            dialogData.value = DialogData(name, location)
        }
        AlertDialog(
            data = dialogData.value,
            onDismiss = { dialogData.value = null },
            onTextChanged = updateDialogData,
            onSaveClicked = onSaveClicked,
        )
        Scaffold(
            topBar = { TopAppBar(viewState.query, onBackClicked, onQueryChanged) },
            snackbarHost = { state -> ErrorSnackbar(state, viewState.error, onSnackbarDismissed = onSnackbarDismissed) }
        ) {
            MainContent(
                suggestions = viewState.suggestions,
                searching = viewState.searching,
                onClickItem = updateDialogData,
            )
        }
    }
}

// TODO This looks ugly. Padding doesn't work for text field. Use custom dialog instead?
@Composable
private fun AlertDialog(
    data: DialogData?,
    onDismiss: () -> Unit,
    onTextChanged: (name: String, Location) -> Unit,
    onSaveClicked: (name: String, Location) -> Unit,
) {
    if (data != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(stringResource(R.string.save_place))
            },
            text = {
                val focusRequester = remember { FocusRequester() }
                val focus = remember { mutableStateOf(true) }
                if (focus.value) {
                    SideEffect {
                        focusRequester.requestFocus()
                    }
                }
                val textFieldValue = remember {
                    mutableStateOf(TextFieldValue(text = data.name, selection = TextRange(0, data.name.length)))
                }
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = textFieldValue.value,
                    onValueChange = { value ->
                        focus.value = false
                        textFieldValue.value = value
                        onTextChanged(value.text, data.location)
                    },
                )
            },
            confirmButton = {
                TextButton(
                    enabled = data.name.isNotBlank(),
                    onClick = {
                        onSaveClicked(data.name, data.location)
                        onDismiss()
                    },
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

private data class DialogData(
    val name: String,
    val location: Location,
)

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
                textStyle = Typography.body1,
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
                    cursorColor = Colors.secondary,
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
        ErrorSnackbar(snackbarData)
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun MainContent(
    suggestions: List<SuggestionItem>?,
    searching: Boolean,
    onClickItem: (String, Location) -> Unit,
) {
    when {
        suggestions == null -> Empty()
        suggestions.isEmpty() -> NoSuggestions()
        else -> SuggestionsList(
            items = suggestions,
            onClickItem = onClickItem,
        )
    }
    AnimatedVisibility(
        visible = searching,
        enter = fadeIn(animationSpec = tween(durationMillis = 1000, delayMillis = 2000)),
        exit = fadeOut(animationSpec = tween()),
    ) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
    val ime = LocalWindowInsets.current.ime
    val navigationBars = LocalWindowInsets.current.navigationBars
    val insets = if (ime.isVisible) {
        ime
    } else navigationBars
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = insets.toPaddingValues(),
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
        modifier = Modifier.clickable { onClick(item.suggestedName, item.location) },
        icon = { Icon(Icons.Default.Map, contentDescription = null) },
        secondaryText = if (item.secondaryText.isNotBlank()) {
            { SecondaryText(item.secondaryText) }
        } else null
    ) {
        PrimaryText(item.primaryText)
    }
}

@Composable
private fun PrimaryText(text: String) {
    Text(
        text = text, maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun SecondaryText(text: String) {
    Text(
        text = text, maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
