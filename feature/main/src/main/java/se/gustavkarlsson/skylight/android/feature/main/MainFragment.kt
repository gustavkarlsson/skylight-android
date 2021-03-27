package se.gustavkarlsson.skylight.android.feature.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ioki.textref.TextRef
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Banner
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.MultiColorLinearProgressIndicator
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchFieldState
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.navigationBarsWithIme
import se.gustavkarlsson.skylight.android.lib.ui.compose.onSurfaceWeaker
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@FlowPreview
@ExperimentalCoroutinesApi
class MainFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("mainViewModel") {
            MainComponent.build().viewModel()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshLocationPermission()
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @Composable
    override fun ScreenContent() {
        val state by viewModel.viewState.collectAsState()
        val scope = rememberCoroutineScope()
        val onBannerActionClicked: (BannerData.Event) -> Unit = { event ->
            when (event) {
                BannerData.Event.RequestLocationPermission -> requestLocationPermission(scope)
                BannerData.Event.OpenAppDetails -> openAppDetails()
            }
        }
        Content(
            viewState = state,
            onBannerActionClicked = onBannerActionClicked,
            onSettingsClicked = { navigator.goTo(screens.settings) },
            onAboutClicked = { navigator.goTo(screens.about) },
            onSearchFieldStateChanged = { viewModel.onSearchChanged(it) },
            onSearchResultClicked = { viewModel.onSearchResultClicked(it) },
            onRecentFavorited = {},
            onFavoriteRemoved = {},
        )
    }

    private fun requestLocationPermission(scope: CoroutineScope) {
        scope.launch {
            PermissionsComponent.instance.locationPermissionRequester().request(this@MainFragment)
        }
    }

    private fun openAppDetails() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        startActivity(intent)
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
@Preview
private fun PreviewContent() {
    Content(
        viewState = ViewState(
            toolbarTitleName = TextRef.EMPTY,
            chanceLevelText = TextRef.EMPTY,
            chanceSubtitleText = TextRef.EMPTY,
            errorBannerData = null,
            factorItems = emptyList(),
            search = SearchViewState.Closed,
        ),
        onBannerActionClicked = {},
        onSettingsClicked = {},
        onAboutClicked = {},
        onSearchFieldStateChanged = {},
        onSearchResultClicked = {},
        onRecentFavorited = {},
        onFavoriteRemoved = {},
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun Content(
    viewState: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onSettingsClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    onSearchFieldStateChanged: (SearchFieldState) -> Unit,
    onSearchResultClicked: (SearchResult) -> Unit,
    onRecentFavorited: (Place.Recent) -> Unit,
    onFavoriteRemoved: (Place.Favorite) -> Unit,
) {
    ScreenBackground {
        val topBarElevation = AppBarDefaults.TopAppBarElevation
        val topBarBackgroundColor = Colors.primarySurface
        Scaffold(
            topBar = {
                TopAppBar(
                    searchText = (viewState.search as? SearchViewState.Open)?.query,
                    title = textRef(viewState.toolbarTitleName),
                    onSettingsClicked = onSettingsClicked,
                    onAboutClicked = onAboutClicked,
                    onSearchFieldStateChanged = onSearchFieldStateChanged,
                    backgroundColor = topBarBackgroundColor,
                    elevation = topBarElevation,
                )
            },
        ) {
            MainContent(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                searchElevation = topBarElevation / 2,
                searchBackgroundColor = topBarBackgroundColor,
                viewState = viewState,
                onBannerActionClicked = onBannerActionClicked,
                onSearchResultClicked = onSearchResultClicked,
                onRecentFavorited = onRecentFavorited,
                onFavoriteRemoved = onFavoriteRemoved,
            )
        }
    }
}

@Composable
private fun TopAppBar(
    searchText: String?,
    title: String,
    onSettingsClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    onSearchFieldStateChanged: (SearchFieldState) -> Unit,
    backgroundColor: Color,
    elevation: Dp,
) {
    TopAppBar(
        backgroundColor = backgroundColor,
        elevation = elevation,
        contentPadding = LocalWindowInsets.current.statusBars
            .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
        title = {
            SearchField(
                modifier = Modifier.fillMaxWidth(),
                text = searchText.orEmpty(),
                unfocusedText = title,
                placeholderText = "Search", // FIXME,
                onStateChanged = onSearchFieldStateChanged,
            )
        },
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
            ) {
                Icon(Icons.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
            ) {
                DropdownMenuItem(onClick = onSettingsClicked) {
                    Text(stringResource(R.string.settings))
                }
                DropdownMenuItem(onClick = onAboutClicked) {
                    Text(stringResource(R.string.about))
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    viewState: ViewState,
    searchElevation: Dp,
    searchBackgroundColor: Color,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onSearchResultClicked: (SearchResult) -> Unit,
    onRecentFavorited: (Place.Recent) -> Unit,
    onFavoriteRemoved: (Place.Favorite) -> Unit,
) {
    Box {
        Column(modifier = modifier) {
            ErrorBanner(
                errorBannerData = viewState.errorBannerData,
                onBannerActionClicked = onBannerActionClicked,
            )
            Spacer(Modifier.weight(1.0f))
            CenterText(viewState = viewState)
            Spacer(Modifier.weight(1.0f))
            Cards(
                modifier = Modifier.fillMaxWidth(),
                items = viewState.factorItems,
            )
        }
        AnimatedVisibility(
            visible = viewState.search is SearchViewState.Open,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                elevation = searchElevation,
                color = searchBackgroundColor,
            ) {
                val focusManager = LocalFocusManager.current
                LazyColumn(
                    contentPadding = LocalWindowInsets.current.navigationBarsWithIme.toPaddingValues(),
                ) {
                    items((viewState.search as? SearchViewState.Open)?.searchResults.orEmpty()) { item ->
                        val itemModifier = if (item.selected) {
                            Modifier.background(MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
                        } else Modifier
                        ListItem(
                            modifier = itemModifier.clickable {
                                focusManager.clearFocus()
                                onSearchResultClicked(item)
                            },
                            icon = {
                                Icon(item.icon, contentDescription = null)
                            },
                            secondaryText = item.subtitle?.let { subtitle ->
                                {
                                    Text(
                                        text = textRef(subtitle),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            },
                        ) {
                            Text(
                                text = textRef(item.title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun ErrorBanner(
    errorBannerData: BannerData?,
    onBannerActionClicked: (BannerData.Event) -> Unit,
) {
    AnimatedVisibility(
        visible = errorBannerData != null,
        enter = slideInVertically(initialOffsetY = { y -> -y }),
        exit = slideOutVertically(targetOffsetY = { y -> -y })
    ) {
        if (errorBannerData != null) {
            Banner(
                backgroundColor = Colors.error,
                icon = {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = errorBannerData.icon,
                        contentDescription = null,
                    )
                },
                actions = {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Colors.onError,
                            disabledContentColor = Colors.onError
                                .copy(alpha = ContentAlpha.disabled),
                        ),
                        onClick = {
                            onBannerActionClicked(errorBannerData.buttonEvent)
                        },
                    ) {
                        Text(
                            text = textRef(errorBannerData.buttonText).toUpperCase(Locale.ROOT),
                        )
                    }
                },
            ) {
                Text(textRef(errorBannerData.message))
            }
        }
    }
}

@Composable
private fun CenterText(
    viewState: ViewState,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = textRef(textRef = viewState.chanceLevelText),
            style = Typography.h4,
        )
        Text(
            text = textRef(textRef = viewState.chanceSubtitleText),
            color = Colors.onSurfaceWeaker,
            style = Typography.body1,
        )
    }
}

@ExperimentalAnimationApi
@Composable
private fun Cards(
    modifier: Modifier,
    items: List<FactorItem>,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        var expandedIndex by remember { mutableStateOf<Int?>(null) }
        items.forEachIndexed { index, item ->
            val expanded = index == expandedIndex
            Card(
                item = item,
                expanded = expanded,
                onClick = {
                    expandedIndex = if (expanded) {
                        null
                    } else index
                },
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun Card(
    item: FactorItem,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    // TODO re-arrange when expanded
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.weight(0.35f),
                    text = textRef(item.title),
                    style = Typography.body1,
                )
                val getValueTextColor = item.valueTextColor
                Text(
                    modifier = Modifier.weight(0.35f),
                    text = textRef(item.valueText),
                    color = Colors.getValueTextColor(),
                    style = Typography.body1,
                )
                val progress = item.progress?.toFloat()
                val animatedProgress by animateFloatAsState(progress?.coerceAtLeast(0.02F) ?: 0F)
                val progressToRender = if (progress == null) null else animatedProgress
                MultiColorLinearProgressIndicator(
                    modifier = Modifier.weight(0.3f),
                    progress = progressToRender,
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            ) {
                Column {
                    // FIXME add formatting and make links clickable
                    Text(
                        text = textRef(item.descriptionText),
                        style = Typography.body2,
                    )
                    if (item.errorText != null) {
                        Text(
                            text = textRef(item.errorText),
                            style = Typography.body2,
                        )
                    }
                }
            }
        }
    }
}
