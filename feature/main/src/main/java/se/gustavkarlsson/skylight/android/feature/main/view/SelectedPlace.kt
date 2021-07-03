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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.BannerData
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.ContentState
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.Event
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.FactorItem
import se.gustavkarlsson.skylight.android.feature.main.viewmodel.NotificationLevelItem
import se.gustavkarlsson.skylight.android.lib.ui.compose.Banner
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.MultiColorLinearProgressIndicator
import se.gustavkarlsson.skylight.android.lib.ui.compose.ToggleButtonState
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.chanceSubtitle
import se.gustavkarlsson.skylight.android.lib.ui.compose.chanceTitle
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef
import java.util.Locale

@Preview
@Composable
private fun PreviewSelectedPlace() {
    SelectedPlace(
        modifier = Modifier.fillMaxSize(),
        state = ContentState.PlaceSelected(
            chanceLevelText = TextRef.string("What chance?"),
            chanceSubtitleText = TextRef.string("subtitle"),
            errorBannerData = null,
            notificationsButtonState = ToggleButtonState.Enabled(checked = false),
            favoriteButtonState = ToggleButtonState.Enabled(checked = true),
            factorItems = listOf(
                FactorItem(
                    title = TextRef.string("Factor 1"),
                    valueText = TextRef.string("High"),
                    descriptionText = TextRef.string("Description..."),
                    valueTextColor = { primary },
                    progress = 0.7,
                    errorText = null,
                )
            ),
            onFavoriteClickedEvent = Event.Noop,
            notificationLevelItems = emptyList(),
        ),
        onBannerActionClicked = {},
        onEvent = {},
    )
}

@Composable
internal fun SelectedPlace(
    modifier: Modifier,
    state: ContentState.PlaceSelected,
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
        SetNotificationLevelAlertDialog(
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
            onFavoriteClicked = { onEvent(state.onFavoriteClickedEvent) },
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
                                    text = textRef(text).uppercase(Locale.ROOT),
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
private fun SetNotificationLevelAlertDialog(
    items: List<NotificationLevelItem>?,
    onEvent: (Event) -> Unit,
    onDismiss: () -> Unit,
) {
    if (items != null) {
        // TODO This looks quite ugly. It has built-in padding and extra padding for buttons
        AlertDialog(
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
        // TODO Add aurora?
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Card(
    item: FactorItem,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    // TODO re-arrange when expanded
    androidx.compose.material.Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(16.dp),
        ) {
            val (title, valueText, progressIndicator, description, error) = createRefs()
            Text(
                modifier = Modifier.constrainAs(title) {
                    linkTo(start = parent.start, top = parent.top, end = valueText.start, bottom = description.top)
                    width = Dimension.percent(0.35f)
                },
                text = textRef(item.title),
                style = Typography.body1,
            )

            val getValueTextColor = item.valueTextColor
            Text(
                modifier = Modifier.constrainAs(valueText) {
                    linkTo(start = title.end, end = progressIndicator.start)
                    centerVerticallyTo(title)
                    width = Dimension.percent(0.35f)
                },
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
                modifier = Modifier.constrainAs(progressIndicator) {
                    linkTo(start = valueText.end, end = parent.end)
                    centerVerticallyTo(title)
                    width = Dimension.percent(0.30f)
                },
                progress = renderProgress,
            )

            Box(
                modifier = Modifier.constrainAs(description) {
                    linkTo(start = parent.start, top = title.bottom, end = parent.end, bottom = error.top)
                    width = Dimension.fillToConstraints
                },
            ) {
                if (expanded) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = textRef(item.descriptionText),
                        style = Typography.body2,
                    )
                }
            }

            Box(
                modifier = Modifier.constrainAs(error) {
                    linkTo(start = parent.start, top = description.bottom, end = parent.end, bottom = parent.bottom)
                    width = Dimension.fillToConstraints
                },
            ) {
                if (expanded && item.errorText != null) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = textRef(item.errorText),
                        style = Typography.body2,
                        color = Colors.error,
                    )
                }
            }
        }
    }
}
