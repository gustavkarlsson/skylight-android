package se.gustavkarlsson.skylight.android.feature.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.IconToggleButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
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
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Banner
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.MultiColorLinearProgressIndicator
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.SearchField
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
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
        viewModel.onEvent(Event.RefreshLocationPermission)
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
            onAboutClicked = { navigator.goTo(screens.about) },
            onEvent = { event -> viewModel.onEvent(event) },
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
            notificationsButtonState = ToggleButtonState.Enabled(checked = false),
            favoriteButtonState = ToggleButtonState.Enabled(checked = true),
            factorItems = emptyList(),
            search = SearchViewState.Closed,
            onFavoritesClickedEvent = Event.Noop,
            notificationLevelItems = emptyList(),
        ),
        onBannerActionClicked = {},
        onAboutClicked = {},
        onEvent = {},
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
private fun Content(
    viewState: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
) {
    ScreenBackground {
        val topBarElevation = AppBarDefaults.TopAppBarElevation
        val topBarBackgroundColor = Colors.primarySurface
        Scaffold(
            topBar = {
                TopAppBar(
                    searchText = (viewState.search as? SearchViewState.Open)?.query,
                    title = textRef(viewState.toolbarTitleName),
                    onAboutClicked = onAboutClicked,
                    onEvent = onEvent,
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
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun TopAppBar(
    searchText: String?,
    title: String,
    onAboutClicked: () -> Unit,
    onEvent: (Event) -> Unit,
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
                onStateChanged = { state -> onEvent(Event.SearchChanged(state)) },
            )
        },
        actions = {
            var menuExpanded by remember { mutableStateOf(false) }
            IconButton(
                onClick = { menuExpanded = !menuExpanded },
            ) {
                Icon(Icons.MoreVert, contentDescription = null)
            }
            // FIXME change to icon?
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                },
            ) {
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
    onEvent: (Event) -> Unit,
) {
    Box {
        ConstraintLayout(modifier = modifier) {
            val (errorBanner, placeButtons, centerText, cards) = createRefs()

            ErrorBanner(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .constrainAs(errorBanner) {
                        linkTo(parent.start, parent.top, parent.end, centerText.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    },
                errorBannerData = viewState.errorBannerData,
                onBannerActionClicked = onBannerActionClicked,
            )

            var showDialog by remember { mutableStateOf(false) }
            AlertDialog(
                items = if (showDialog) viewState.notificationLevelItems else null,
                onDismiss = { showDialog = false },
                onEvent = onEvent,
            )
            PlaceButtons(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 8.dp)
                    .constrainAs(placeButtons) {
                        bottom.linkTo(cards.top)
                        end.linkTo(parent.end)
                    },
                notificationsButtonState = viewState.notificationsButtonState,
                favoriteButtonState = viewState.favoriteButtonState,
                onNotificationsClicked = { showDialog = true },
                onFavoriteClicked = { onEvent(viewState.onFavoritesClickedEvent) },
            )

            CenterText(
                modifier = Modifier.constrainAs(centerText) {
                    linkTo(parent.start, errorBanner.bottom, parent.end, cards.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                viewState = viewState,
            )

            Cards(
                modifier = Modifier
                    .padding(16.dp, 8.dp, 16.dp, 16.dp)
                    .constrainAs(cards) {
                        linkTo(parent.start, centerText.bottom, parent.end, parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    },
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
                                onEvent(item.selectEvent)
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
    modifier: Modifier = Modifier,
    errorBannerData: BannerData?,
    onBannerActionClicked: (BannerData.Event) -> Unit,
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = errorBannerData != null,
            enter = slideInVertically(initialOffsetY = { y -> -y }),
            exit = slideOutVertically(targetOffsetY = { y -> -y })
        ) {
            if (errorBannerData != null) {
                Banner(
                    backgroundColor = Colors.error,
                    icon = {
                        Crossfade(targetState = errorBannerData.icon) { icon ->
                            Icon(
                                modifier = Modifier.size(40.dp),
                                imageVector = icon,
                                contentDescription = null,
                            )
                        }
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
                            Crossfade(targetState = errorBannerData.buttonText) { text ->
                                Text(
                                    text = textRef(text).toUpperCase(Locale.ROOT),
                                )
                            }
                        }
                    },
                ) {
                    Text(
                        modifier = Modifier.animateContentSize(spring(stiffness = Spring.StiffnessLow)),
                        text = textRef(errorBannerData.message),
                    )
                }
            }
        }
    }
}
@Composable
private fun AlertDialog(
    items: List<NotificationLevelItem>?,
    onEvent: (Event) -> Unit,
    onDismiss: () -> Unit,
) {
    if (items != null) {
        // TODO This looks quite ugly. It has built-in padding and extra padding for buttons
        androidx.compose.material.AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (item in items) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(item.selectEvent)
                                    onDismiss()
                                }
                                .padding(8.dp),
                        ) {
                            RadioButton(
                                selected = item.selected,
                                onClick = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(textRef(textRef = item.text))
                        }
                    }
                }
            },
            confirmButton = {},
        )
    }
}


@ExperimentalAnimationApi
@Composable
private fun PlaceButtons(
    modifier: Modifier = Modifier,
    notificationsButtonState: ToggleButtonState,
    favoriteButtonState: ToggleButtonState,
    onNotificationsClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        AnimatedVisibility(visible = notificationsButtonState.visible) {
            IconToggleButton(
                checked = notificationsButtonState.checked,
                onCheckedChange = { onNotificationsClicked() },
            ) {
                val icon = if (notificationsButtonState.checked) {
                    Icons.Notifications
                } else Icons.NotificationsNone
                Icon(icon, tint = Color.Yellow, contentDescription = null) // FIXME color
            }
        }
        AnimatedVisibility(visible = favoriteButtonState.visible) {
            IconToggleButton(
                checked = favoriteButtonState.checked,
                onCheckedChange = { onFavoriteClicked() },
            ) {
                val icon = if (favoriteButtonState.checked) {
                    Icons.Favorite
                } else Icons.FavoriteBorder
                Icon(icon, tint = Color.Red, contentDescription = null) // FIXME color
            }
        }
    }
}

@Composable
private fun CenterText(
    modifier: Modifier = Modifier,
    viewState: ViewState,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
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
        modifier = modifier,
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
        ConstraintLayout(
            modifier = Modifier.padding(16.dp),
            constraintSet = cardConstraints(expanded),
        ) {
            Text(
                modifier = Modifier.layoutId("title"),
                text = textRef(item.title),
                style = Typography.body1,
            )

            val getValueTextColor = item.valueTextColor
            Text(
                modifier = Modifier.layoutId("valueText"),
                text = textRef(item.valueText),
                color = Colors.getValueTextColor(),
                style = Typography.body1,
            )

            val actualProgress = item.progress?.toFloat()
            val targetProgress = actualProgress?.coerceAtLeast(0.02F) ?: 0F
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
            )
            val renderProgress = if (actualProgress == null) null else animatedProgress
            MultiColorLinearProgressIndicator(
                modifier = Modifier.layoutId("progressIndicator"),
                progress = renderProgress,
            )

            if (expanded) {
                Text(
                    modifier = Modifier.layoutId("description"),
                    text = textRef(item.descriptionText),
                    style = Typography.body2,
                )

                Box(modifier = Modifier.layoutId("error")) {
                    if (item.errorText != null) {
                        Text(
                            text = textRef(item.errorText),
                            style = Typography.body2,
                            color = Colors.error,
                        )
                    }
                }
            }
        }
    }
}

private fun cardConstraints(expanded: Boolean): ConstraintSet {
    return ConstraintSet {
        val title = createRefFor("title")
        val valueText = createRefFor("valueText")
        val progressIndicator = createRefFor("progressIndicator")
        val description = createRefFor("description")
        val error = createRefFor("error")

        if (expanded) {
            constrain(title) {
                linkTo(parent.start, parent.top, valueText.start, description.top)
                width = Dimension.percent(0.35f)
            }
            constrain(valueText) {
                linkTo(title.end, parent.top, progressIndicator.start, description.top)
                width = Dimension.percent(0.35f)
            }
            constrain(progressIndicator) {
                linkTo(valueText.end, parent.top, parent.end, description.top)
                width = Dimension.percent(0.30f)
            }
            constrain(description) {
                linkTo(parent.start, title.bottom, parent.end, error.top)
                width = Dimension.fillToConstraints
            }
            constrain(error) {
                linkTo(parent.start, description.bottom, parent.end, parent.bottom)
                width = Dimension.fillToConstraints
            }
        } else {
            constrain(title) {
                linkTo(parent.start, parent.top, valueText.start, parent.bottom)
                width = Dimension.percent(0.35f)
            }
            constrain(valueText) {
                linkTo(title.end, parent.top, progressIndicator.start, parent.bottom)
                width = Dimension.percent(0.35f)
            }
            constrain(progressIndicator) {
                linkTo(valueText.end, parent.top, parent.end, parent.bottom)
                width = Dimension.percent(0.30f)
            }
        }
    }
}
