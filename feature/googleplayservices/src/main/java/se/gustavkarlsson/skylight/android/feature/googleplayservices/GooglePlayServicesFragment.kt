package se.gustavkarlsson.skylight.android.feature.googleplayservices

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
            onErrorSnackbarDismissClicked = viewModel::clearError,
        )
    }

    override fun onNewStartStopScope(scope: CoroutineScope) {
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
    onErrorSnackbarDismissClicked: () -> Unit = {},
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
                AnimatedVisibility(
                    visible = errorSnackbarVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    // FIXME Replace with scaffold/snackbarhost
                    Snackbar(
                        backgroundColor = MaterialTheme.colors.error,
                        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.error),
                        content = {
                            Text(text = stringResource(id = R.string.google_play_services_install_failed))
                        },
                        action = {
                            Text(
                                modifier = Modifier.clickable(onClick = onErrorSnackbarDismissClicked),
                                text = stringResource(id = R.string.dismiss),
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ExtendedFloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    text = { Text(stringResource(id = R.string.google_play_services_install)) },
                    onClick = onInstallClicked,
                )
            }
        }
    }
}
