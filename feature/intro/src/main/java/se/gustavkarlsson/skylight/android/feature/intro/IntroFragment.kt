package se.gustavkarlsson.skylight.android.feature.intro

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography

internal class IntroFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("introViewModel") {
            IntroComponent.build().viewModel()
        }
    }

    @ExperimentalAnimationApi
    @Composable
    override fun ScreenContent() {
        Content(
            onPrivacyPolicyClicked = ::onPrivacyPolicyClicked,
            onContinueClicked = ::onContinueClicked,
        )
    }

    private fun onPrivacyPolicyClicked() {
        navigator.goTo(screens.privacyPolicy)
    }

    private fun onContinueClicked() {
        startStopScope?.launch {
            PermissionsComponent.instance.locationPermissionRequester()
                .request(this@IntroFragment)
            viewModel.registerScreenSeen()
            navigator.closeScreenAndGoTo(screens.main)
        }
    }
}

@ExperimentalAnimationApi
@Composable
@Preview
private fun PreviewContent() {
    Content(
        onPrivacyPolicyClicked = {},
        onContinueClicked = {},
    )
}

@ExperimentalAnimationApi
@Composable
private fun Content(
    onPrivacyPolicyClicked: () -> Unit,
    onContinueClicked: () -> Unit,
) {
    ScreenBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = stringResource(id = R.string.intro_title),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = Typography.h4,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.intro_desc),
                style = Typography.body1,
            )
            Spacer(modifier = Modifier.height(16.dp))
            ClickableText(
                text = stringResource(R.string.privacy_policy),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onPrivacyPolicyClicked,
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    initiallyVisible = false,
                    enter = slideInVertically(
                        initialOffsetY = { y -> y },
                        animationSpec = spring(stiffness = 200f),
                    ),
                ) {
                    ExtendedFloatingActionButton(
                        backgroundColor = Colors.primary,
                        text = { Text(stringResource(id = R.string.intro_continue)) },
                        onClick = onContinueClicked,
                    )
                }
            }
        }
    }
}
