package se.gustavkarlsson.skylight.android.feature.googleplayservices

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground

internal class GooglePlayServicesFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("googlePlayServicesViewModel") {
            GooglePlayServicesComponent.build().viewModel()
        }
    }

    @ExperimentalAnimationApi
    @Composable
    override fun ScreenContent() {
        val errorSnackbarVisible = viewModel.error.collectAsState(initial = false)
        Content(
            errorSnackbarVisible = errorSnackbarVisible.value,
            onInstallClicked = { viewModel.installGooglePlayServices(requireActivity()) },
            onErrorSnackbarDismissed = viewModel::clearError,
        )
    }

    override fun onNewCreateDestroyScope(scope: CoroutineScope) {
        scope.launch {
            viewModel.success.collect {
                val target = requireNotNull(requireArguments().target)
                navigator.setBackstack(target)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
@Preview
private fun Content(
    errorSnackbarVisible: Boolean = true,
    onInstallClicked: () -> Unit = {},
    onErrorSnackbarDismissed: () -> Unit = {},
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Image(
                modifier = Modifier
                    .fillMaxSize(0.3f)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.ic_google_play_store),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.google_play_services_title),
                style = MaterialTheme.typography.h5,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.google_play_services_desc),
                style = MaterialTheme.typography.body1,
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ErrorSnackbar(errorSnackbarVisible, onErrorSnackbarDismissed)
                ExtendedFloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    text = { Text(stringResource(id = R.string.google_play_services_install)) },
                    onClick = onInstallClicked,
                )
            }
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
        Snackbar(
            snackbarData = snackbarData,
            backgroundColor = MaterialTheme.colors.error,
            contentColor = MaterialTheme.colors.onError,
            actionColor = MaterialTheme.colors.onError,
        )
    }
}
