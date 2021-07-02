package se.gustavkarlsson.skylight.android.feature.intro

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService

@Parcelize
internal data class IntroScreen(private val target: Backstack) : Screen {

    init {
        require(target.isNotEmpty()) { "Target backstack must not be empty" }
    }

    @IgnoredOnParcel
    override val name = ScreenName.Intro

    private val AppCompatActivity.viewModel: IntroViewModel
        get() = getOrRegisterService(this@IntroScreen, "introViewModel") {
            IntroComponent.build().viewModel()
        }

    @Composable
    override fun AppCompatActivity.Content(scope: CoroutineScope) {
        Content(
            onPrivacyPolicyClicked = { onPrivacyPolicyClicked() },
            onContinueClicked = { onContinueClicked() },
        )
    }

    private fun AppCompatActivity.onPrivacyPolicyClicked() {
        navigator.goTo(screens.privacyPolicy)
    }

    private fun AppCompatActivity.onContinueClicked() {
        viewModel.registerScreenSeen()
        navigator.setBackstack(target)
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        onPrivacyPolicyClicked = {},
        onContinueClicked = {},
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun Content(
    onPrivacyPolicyClicked: () -> Unit,
    onContinueClicked: () -> Unit,
) {
    ScreenBackground {
        LargeDialog(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 32.dp)
                .systemBarsPadding(),
            image = {
                Image(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .width(200.dp)
                        .height(100.dp),
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = null,
                )
            },
            title = stringResource(R.string.intro_title),
            description = stringResource(R.string.intro_desc),
            primaryActionText = stringResource(R.string.intro_continue),
            onClickPrimaryAction = onContinueClicked,
            secondaryActionText = stringResource(R.string.privacy_policy),
            onClickSecondaryAction = onPrivacyPolicyClicked,
        )
    }
}
