package se.gustavkarlsson.skylight.android.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ioki.textref.TextRef
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.places.Place
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef

@ExperimentalCoroutinesApi
@FlowPreview
class SettingsFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("settingsViewModel") {
            SettingsComponent.build().viewModel()
        }
    }

    @ExperimentalFoundationApi
    @ExperimentalMaterialApi
    @Composable
    override fun ScreenContent() {
        val settings = viewModel.settingsItems.collectAsState(initial = emptyList())
        Content(
            settings = settings.value,
            onBackClicked = { navigator.closeScreen() },
            onTriggerLevelChanged = { place, triggerLevel -> viewModel.onTriggerLevelSelected(place, triggerLevel) },
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
@Preview
private fun PreviewContent() {
    Content(
        settings = emptyList(),
        onBackClicked = {},
        onTriggerLevelChanged = { _, _ -> },
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun Content(
    settings: List<SettingsItem>,
    onBackClicked: () -> Unit,
    onTriggerLevelChanged: (Place, TriggerLevel) -> Unit,
) {
    ScreenBackground {
        val alertDialogState = remember { mutableStateOf<AlertDialogData?>(null) }
        AlertDialog(
            state = alertDialogState.value,
            onDismiss = { alertDialogState.value = null },
            onTriggerLevelChanged = onTriggerLevelChanged,
        )
        Scaffold(
            modifier = Modifier.navigationBarsPadding(),
            topBar = {
                TopAppBar(onBackClicked = onBackClicked)
            },
        ) {
            SettingsItemList(
                settings = settings,
                onClickItem = { place, triggerLevel ->
                    alertDialogState.value = AlertDialogData(place, triggerLevel)
                },
            )
        }
    }
}

@Composable
private fun AlertDialog(
    state: AlertDialogData?,
    onDismiss: () -> Unit,
    onTriggerLevelChanged: (Place, TriggerLevel) -> Unit
) {
    if (state != null) {
        // TODO This looks quite ugly. It has built-in padding and extra padding for buttons
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (triggerLevel in TriggerLevel.values()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onTriggerLevelChanged(state.place, triggerLevel)
                                    onDismiss()
                                }
                                .padding(8.dp),
                        ) {
                            RadioButton(
                                selected = state.triggerLevel == triggerLevel,
                                onClick = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(textRef(textRef = triggerLevel.shortText))
                        }
                    }
                }
            },
            confirmButton = {},
        )
    }
}

@Composable
private fun TopAppBar(onBackClicked: () -> Unit) {
    TopAppBar(
        contentPadding = LocalWindowInsets.current.statusBars
            .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.ArrowBack, contentDescription = null)
            }
        },
        title = {
            Text(stringResource(R.string.settings))
        },
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun SettingsItemList(
    settings: List<SettingsItem>,
    onClickItem: (Place, TriggerLevel) -> Unit
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.navigationBars
            .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
    ) {
        stickyHeader {
            SettingsHeader()
        }
        items(settings) { item ->
            SettingsItem(item = item, onClick = onClickItem)
        }
    }
}

@Composable
private fun SettingsHeader() {
    Text(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        text = stringResource(R.string.pref_notifications_category_title),
        style = Typography.caption,
        color = Colors.secondary,
    )
}

@ExperimentalMaterialApi
@Composable
private fun SettingsItem(
    item: SettingsItem,
    onClick: (Place, TriggerLevel) -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable { onClick(item.place, item.triggerLevel) },
        secondaryText = { Text(textRef(item.subtitle)) },
    ) {
        Text(textRef(item.title))
    }
}

private data class AlertDialogData(val place: Place, val triggerLevel: TriggerLevel)

private val TriggerLevel.shortText: TextRef
    get() = when (this) {
        TriggerLevel.NEVER -> TextRef.stringRes(R.string.pref_notifications_entry_never_short)
        TriggerLevel.LOW -> TextRef.stringRes(R.string.pref_notifications_entry_low_short)
        TriggerLevel.MEDIUM -> TextRef.stringRes(R.string.pref_notifications_entry_medium_short)
        TriggerLevel.HIGH -> TextRef.stringRes(R.string.pref_notifications_entry_high_short)
    }
