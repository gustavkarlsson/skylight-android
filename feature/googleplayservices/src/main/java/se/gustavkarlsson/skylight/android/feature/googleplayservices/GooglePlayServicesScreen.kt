package se.gustavkarlsson.skylight.android.feature.googleplayservices

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ErrorSnackbar
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.compose.collectAsLifecycleAwareState
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.getService

private val VIEW_MODEL_ID = ServiceId("googlePlayServicesViewModel")

@Parcelize
internal data class GooglePlayServicesScreen(private val target: Backstack) : Screen {

    init {
        require(target.isNotEmpty()) { "Target backstack must not be empty" }
    }

    @IgnoredOnParcel
    override val type: Screen.Type = Screen.Type.GooglePlayServices

    private val optionalViewModel get() = getService<GooglePlayServicesViewModel>(VIEW_MODEL_ID)

    override fun onCreateDestroyScope(activity: AppCompatActivity, scope: CoroutineScope) {
        scope.launch {
            optionalViewModel?.success?.collect {
                navigator.setBackstack(target)
            }
        }
    }

    @Composable
    override fun Content(activity: AppCompatActivity, tag: ServiceTag, scope: CoroutineScope) {
        val viewModel = getOrRegisterService(VIEW_MODEL_ID, tag) {
            GooglePlayServicesComponent.build().viewModel()
        }
        val errorSnackbarVisible = viewModel.error.collectAsLifecycleAwareState()
        Content(
            errorSnackbarVisible = errorSnackbarVisible.value,
            onInstallClicked = { viewModel.installGooglePlayServices(activity) },
            onErrorSnackbarDismissed = viewModel::clearError,
        )
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        errorSnackbarVisible = true,
        onInstallClicked = {},
        onErrorSnackbarDismissed = {},
    )
}

@Composable
private fun Content(
    errorSnackbarVisible: Boolean,
    onInstallClicked: () -> Unit,
    onErrorSnackbarDismissed: () -> Unit,
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Image(
                modifier = Modifier.fillMaxSize(0.3f),
                painter = painterResource(R.drawable.ic_google_play_store),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Colors.onBackground),
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.google_play_services_title),
                style = Typography.h5,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.google_play_services_desc),
                style = Typography.body1,
            )
            Spacer(modifier = Modifier.weight(1f))
            ErrorSnackbar(errorSnackbarVisible, onErrorSnackbarDismissed)
            ExtendedFloatingActionButton(
                backgroundColor = Colors.primary,
                text = { Text(stringResource(id = R.string.google_play_services_install)) },
                onClick = onInstallClicked,
            )
        }
    }
}

@Composable
private fun ErrorSnackbar(
    errorSnackbarVisible: Boolean,
    onDismissed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val messageText = stringResource(R.string.google_play_services_install_failed)
    val dismissText = stringResource(R.string.dismiss)
    LaunchedEffect(key1 = errorSnackbarVisible) {
        if (errorSnackbarVisible && snackbarHostState.currentSnackbarData == null) {
            snackbarHostState.showSnackbar(
                message = messageText,
                actionLabel = dismissText,
                duration = SnackbarDuration.Long,
            )
            onDismissed()
        }
    }
    SnackbarHost(hostState = snackbarHostState) { snackbarData ->
        ErrorSnackbar(snackbarData)
    }
}
