package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.ioki.textref.TextRef
import java.util.Locale
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.FactorItem
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.NotificationLevelItem
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.SearchViewState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ViewState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Banner
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.MultiColorLinearProgressIndicator
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.chanceSubtitle
import se.gustavkarlsson.skylight.android.lib.ui.compose.chanceTitle
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@Preview
@Composable
private fun PreviewSelectedPlace() {
    SelectedPlace(
        modifier = Modifier,
        state = ViewState(
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
        onEvent = {},
    )
}

@Composable
internal fun SelectedPlace(
    modifier: Modifier,
    state: ViewState,
    onBannerActionClicked: (BannerData.Event) -> Unit,
    onEvent: (Event) -> Unit
) {
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
            errorBannerData = state.errorBannerData,
            onBannerActionClicked = onBannerActionClicked,
        )

        var showDialog by remember { mutableStateOf(false) }
        AlertDialog(
            items = if (showDialog) state.notificationLevelItems else null,
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
            notificationsButtonState = state.notificationsButtonState,
            favoriteButtonState = state.favoriteButtonState,
            onNotificationsClicked = { showDialog = true },
            onFavoriteClicked = { onEvent(state.onFavoritesClickedEvent) },
        )

        CenterText(
            modifier = Modifier.constrainAs(centerText) {
                linkTo(parent.start, errorBanner.bottom, parent.end, cards.top)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            title = textRef(textRef = state.chanceLevelText),
            subtitle = textRef(textRef = state.chanceSubtitleText),
        )

        Cards(
            modifier = Modifier
                .padding(16.dp, 8.dp, 16.dp, 16.dp)
                .constrainAs(cards) {
                    linkTo(parent.start, centerText.bottom, parent.end, parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
            items = state.factorItems,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ErrorBanner(
    modifier: Modifier,
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlaceButtons(
    modifier: Modifier,
    notificationsButtonState: ToggleButtonState,
    favoriteButtonState: ToggleButtonState,
    onNotificationsClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
) {
    Row(modifier = modifier) {
        AnimatedVisibility(visible = notificationsButtonState.visible) {
            IconToggleButton(
                checked = notificationsButtonState.checked,
                onCheckedChange = { onNotificationsClicked() },
            ) {
                val icon = if (notificationsButtonState.checked) {
                    Icons.Notifications
                } else Icons.NotificationsNone
                Icon(icon, tint = Colors.bell, contentDescription = null)
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
                Icon(icon, tint = Colors.heart, contentDescription = null)
            }
        }
    }
}

@Composable
private fun CenterText(
    modifier: Modifier,
    title: String,
    subtitle: String,
) {
    Box(modifier) {
        Aurora(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = Typography.chanceTitle,
            )
            Text(
                text = subtitle,
                color = Colors.onSurfaceWeaker,
                style = Typography.chanceSubtitle,
            )
        }
    }
}

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

@Composable
private fun Card(
    item: FactorItem,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    // TODO re-arrange when expanded
    androidx.compose.material.Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(onClick = onClick),
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(16.dp),
            constraintSet = cardConstraints(expanded, item.errorText != null),
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

private fun cardConstraints(expanded: Boolean, showError: Boolean): ConstraintSet {
    return ConstraintSet {
        val title = createRefFor("title")
        val valueText = createRefFor("valueText")
        val progressIndicator = createRefFor("progressIndicator")
        val description = createRefFor("description")
        val error = createRefFor("error")

        if (expanded) {
            constrain(title) {
                linkTo(parent.start, valueText.start)
                width = Dimension.percent(0.35f)
            }
            constrain(valueText) {
                linkTo(title.end, progressIndicator.start)
                centerVerticallyTo(title)
                width = Dimension.percent(0.35f)
            }
            constrain(progressIndicator) {
                linkTo(valueText.end, parent.end)
                centerVerticallyTo(title)
                width = Dimension.percent(0.30f)
            }
            constrain(description) {
                top.linkTo(title.bottom, margin = 8.dp)
                linkTo(parent.start, parent.end)
                width = Dimension.fillToConstraints
            }
            if (showError) {
                constrain(error) {
                    top.linkTo(description.bottom, margin = 8.dp)
                    linkTo(parent.start, parent.end)
                    width = Dimension.fillToConstraints
                }
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
