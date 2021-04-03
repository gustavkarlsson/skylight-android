package se.gustavkarlsson.skylight.android.feature.intro

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.startStopScope

@Parcelize
class IntroScreen(private val target: Backstack) : Screen {

    init {
        require(target.isNotEmpty()) { "Target backstack must not be empty" }
    }

    @IgnoredOnParcel
    override val name = ScreenName.Intro

    private val AppCompatActivity.viewModel: IntroViewModel
        get() = getOrRegisterService(this@IntroScreen, "introViewModel") {
            IntroComponent.build().viewModel()
        }

    @ExperimentalAnimationApi
    @Composable
    override fun AppCompatActivity.Content() {
        Content(
            onPrivacyPolicyClicked = { onPrivacyPolicyClicked() },
            onContinueClicked = { onContinueClicked() },
        )
    }

    private fun AppCompatActivity.onPrivacyPolicyClicked() {
        navigator.goTo(screens.privacyPolicy)
    }

    private fun AppCompatActivity.onContinueClicked() {
        startStopScope?.launch {
            PermissionsComponent.instance.locationPermissionRequester()
                .request(this@onContinueClicked)
            viewModel.registerScreenSeen()
            navigator.setBackstack(target)
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = stringResource(id = R.string.intro_title),
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
                onClick = onPrivacyPolicyClicked,
            )
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(
                visible = true,
                initiallyVisible = false,
                enter = slideInVertically(
                    initialOffsetY = { y -> y },
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                ) + fadeIn(),
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
