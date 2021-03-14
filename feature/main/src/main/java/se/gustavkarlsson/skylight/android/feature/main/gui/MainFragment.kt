package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DrawerDefaults.ScrimOpacity
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ioki.textref.TextRef
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.feature.main.DrawerComponent
import se.gustavkarlsson.skylight.android.feature.main.MainComponent
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Banner
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.onSurfaceDisabled
import se.gustavkarlsson.skylight.android.lib.ui.compose.onSurfaceDivider
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

    private val drawerViewModel by lazy {
        getOrRegisterService("drawerViewModel") {
            DrawerComponent.build().viewModel()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshLocationPermission()
    }

    // TODO Close drawer on back button

    @ExperimentalFoundationApi
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
        val drawerItems by drawerViewModel.drawerItems.collectAsState(initial = emptyList())
        Content(
            viewState = state,
            drawerItems = drawerItems,
            onBannerActionClicked = onBannerActionClicked,
            onSettingsClicked = { navigator.goTo(screens.settings) },
            onAboutClicked = { navigator.goTo(screens.about) },
            onDrawerItemClick = { event ->
                when (event) {
                    DrawerClickEvent.AddPlaceClicked -> navigator.goTo(screens.addPlace())
                    is DrawerClickEvent.PlaceClicked -> drawerViewModel.onEvent(event)
                }
            },
            onDrawerItemLongClick = { event ->
                // FIXME show dialog
                drawerViewModel.onEvent(event)
            },
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

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
@Preview
private fun Content(
    viewState: ViewState = ViewState(
        toolbarTitleName = TextRef.EMPTY,
        chanceLevelText = TextRef.EMPTY,
        chanceSubtitleText = TextRef.EMPTY,
        errorBannerData = null,
        factorItems = emptyList(),
    ),
    drawerItems: List<DrawerItem> = emptyList(),
    onBannerActionClicked: (BannerData.Event) -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onAboutClicked: () -> Unit = {},
    onDrawerItemClick: (DrawerClickEvent) -> Unit = {},
    onDrawerItemLongClick: (DrawerLongClickEvent) -> Unit = {},
) {
    ScreenBackground {
        val scope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = textRef(viewState.toolbarTitleName),
                    onMenuClicked = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    onSettingsClicked = onSettingsClicked,
                    onAboutClicked = onAboutClicked,
                )
            },
            drawerScrimColor = Color.Black.copy(alpha = ScrimOpacity),
            drawerContent = {
                Drawer(
                    modifier = Modifier.systemBarsPadding(),
                    drawerItems = drawerItems,
                    onItemClick = { event ->
                        onDrawerItemClick(event)
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    },
                    onItemLongClick = { event ->
                        onDrawerItemLongClick(event)
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    },
                )
            },
        ) {
            MainContent(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                viewState = viewState,
                onBannerActionClicked = onBannerActionClicked,
            )
        }
    }
}

@Composable
private fun TopAppBar(
    title: String,
    onSettingsClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    onMenuClicked: () -> Unit,
) {
    TopAppBar(
        contentPadding = LocalWindowInsets.current.statusBars
            .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
        navigationIcon = {
            IconButton(
                onClick = onMenuClicked,
            ) {
                Icon(Icons.Menu, contentDescription = null)
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                overflow = TextOverflow.Ellipsis,
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

@ExperimentalFoundationApi
@Composable
private fun Drawer(
    modifier: Modifier = Modifier,
    drawerItems: List<DrawerItem>,
    onItemClick: (DrawerClickEvent) -> Unit,
    onItemLongClick: (DrawerLongClickEvent) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.places),
            style = Typography.h5,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(16.dp)
                .background(Colors.onSurfaceDivider),
        )
        LazyColumn {
            items(drawerItems) { item ->
                DrawerItem(
                    item = item,
                    onItemClick = onItemClick,
                    onItemLongClick = onItemLongClick,
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun DrawerItem(
    item: DrawerItem,
    onItemClick: (DrawerClickEvent) -> Unit,
    onItemLongClick: (DrawerLongClickEvent) -> Unit
) {
    // FIXME show item.isActive
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (item.clickEvent != null) {
                        onItemClick(item.clickEvent)
                    }
                },
                onLongClick = {
                    if (item.longClickEvent != null) {
                        onItemLongClick(item.longClickEvent)
                    }
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = item.icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(textRef(item.text))
    }
}

@ExperimentalAnimationApi
@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    viewState: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
) {
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
}

@ExperimentalAnimationApi
@Composable
private fun ErrorBanner(
    errorBannerData: BannerData?,
    onBannerActionClicked: (BannerData.Event) -> Unit
) {
    AnimatedVisibility(visible = errorBannerData != null) {
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
                            text = textRef(errorBannerData.buttonText).toUpperCase(),
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
    // FIXME re-arrange when expanded
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
                val progress = item.progress?.toFloat()?.coerceAtLeast(0.02F) ?: 0F
                val animatedProgress by animateFloatAsState(progress)
                LinearProgressIndicator(
                    modifier = Modifier.weight(0.3f),
                    color = item.progressColor,
                    backgroundColor = Colors.onSurfaceDisabled,
                    progress = animatedProgress,
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
            ) {
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
