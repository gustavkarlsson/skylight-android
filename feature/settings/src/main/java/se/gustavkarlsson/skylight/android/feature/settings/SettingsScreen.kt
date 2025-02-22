package se.gustavkarlsson.skylight.android.feature.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.AutoMirroredIcons
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import se.gustavkarlsson.skylight.android.lib.ui.compose.textRef
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService
import se.gustavkarlsson.skylight.android.core.R as CoreR

private val VIEW_MODE_ID = ServiceId("settingsViewModel")

@Parcelize
object SettingsScreen : Screen {
    override val type: Screen.Type get() = Screen.Type.Settings

    @Composable
    override fun Content(activity: AppCompatActivity, tag: ServiceTag) {
        val viewModel = getOrRegisterService(VIEW_MODE_ID, tag) {
            SettingsViewModelComponent.build().viewModel()
        }
        val viewState by viewModel.state.collectAsLifecycleAwareState()
        Content(
            notificationLevelText = textRef(viewState.triggerLevelText),
            placesWithTriggerLevelText = textRef(viewState.placesWithTriggerLevelText),
            triggerLevelItems = viewState.triggerLevelItems,
            onTriggerLevelChanged = { triggerLevel ->
                viewModel.onTriggerLevelChanged(triggerLevel)
            },
            onBackClicked = { navigator.closeScreen() },
            onAboutClicked = { navigator.goTo(screens.about) },
        ) { navigator.goTo(screens.privacyPolicy) }
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        notificationLevelText = "At low chance",
        placesWithTriggerLevelText = "2 places",
        triggerLevelItems = emptyList(),
        onTriggerLevelChanged = {},
        onBackClicked = {},
        onAboutClicked = {},
    ) {}
}

@Composable
private fun Content(
    notificationLevelText: String,
    placesWithTriggerLevelText: String,
    triggerLevelItems: List<TriggerLevelItem>,
    onTriggerLevelChanged: (TriggerLevel) -> Unit,
    onBackClicked: () -> Unit,
    onAboutClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
) {
    ScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets.statusBars,
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(AutoMirroredIcons.ArrowBack, contentDescription = null)
                        }
                    },
                    title = {
                        Text(stringResource(CoreR.string.settings))
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
            ) {
                TriggerLevelItem(
                    notificationLevelText,
                    placesWithTriggerLevelText,
                    triggerLevelItems,
                    onTriggerLevelChanged,
                )
                AboutItem(onClick = onAboutClicked)
                PrivacyPolicyItem(onClick = onPrivacyPolicyClicked)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TriggerLevelItem(
    triggerLevelText: String,
    placesWithTriggerLevelText: String,
    triggerLevelItems: List<TriggerLevelItem>,
    onTriggerLevelChanged: (TriggerLevel) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    if (showDialog) {
        SetTriggerLevelAlertDialog(
            items = triggerLevelItems,
            onTriggerLevelChanged = onTriggerLevelChanged,
            onDismiss = { showDialog = false },
        )
    }
    ListItem(
        modifier = Modifier.clickable { showDialog = true },
        icon = {
            Icon(imageVector = Icons.Notifications, contentDescription = null)
        },
        secondaryText = {
            Text(placesWithTriggerLevelText)
        },
        trailing = {
            Text(triggerLevelText)
        },
    ) {
        Text(text = stringResource(R.string.notify_title))
    }
}

@Composable
private fun SetTriggerLevelAlertDialog(
    items: List<TriggerLevelItem>,
    onTriggerLevelChanged: (TriggerLevel) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.notify_title))
        },
        text = {
            Column {
                for (item in items) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onTriggerLevelChanged(item.triggerLevel)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AboutItem(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        icon = {
            Icon(imageVector = Icons.Info, contentDescription = null)
        },
    ) {
        Text(text = stringResource(CoreR.string.about))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PrivacyPolicyItem(onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        icon = {
            Icon(imageVector = Icons.PrivacyTip, contentDescription = null)
        },
    ) {
        Text(text = stringResource(CoreR.string.privacy_policy))
    }
}
