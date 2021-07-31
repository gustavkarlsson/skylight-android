package se.gustavkarlsson.skylight.android.feature.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.core.entities.TriggerLevel
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.*
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService

private val VIEW_MODE_ID = ServiceId("settingsViewModel")

@Parcelize
object SettingsScreen : Screen {
    override val type: Screen.Type get() = Screen.Type.Settings

    @Composable
    override fun Content(activity: AppCompatActivity, scope: CoroutineScope, tag: ServiceTag) {
        val viewModel = getOrRegisterService(VIEW_MODE_ID, tag) {
            SettingsComponent.build().viewModel()
        }
        val viewState by viewModel.state.collectAsLifecycleAwareState()
        Content(
            notificationLevelText = textRef(viewState.triggerLevelText),
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
        triggerLevelItems = emptyList(),
        onTriggerLevelChanged = {},
        onBackClicked = {},
        onAboutClicked = {},
    ) {}
}

@Composable
private fun Content(
    notificationLevelText: String,
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
                    contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.statusBars),
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.ArrowBack, contentDescription = null)
                        }
                    },
                    title = {
                        Text(stringResource(R.string.settings))
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                TriggerLevelItem(
                    notificationLevelText,
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
    // FIXME show how many places?
    ListItem(
        modifier = Modifier.clickable { showDialog = true },
        icon = {
            Icon(imageVector = Icons.Notifications, contentDescription = null)
        },
        secondaryText = {
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
        Text(text = stringResource(R.string.about))
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
        Text(text = stringResource(R.string.privacy_policy))
    }
}
