package se.gustavkarlsson.skylight.android.feature.intro

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.LargeDialog
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService

private val VIEW_MODEL_ID = ServiceId("introViewModel")

@Parcelize
internal data class IntroScreen(private val target: Backstack) : Screen {

    @IgnoredOnParcel
    override val type: Screen.Type = Screen.Type.Intro

    @Composable
    override fun Content(activity: AppCompatActivity, tag: ServiceTag) {
        val viewModel = getOrRegisterService(VIEW_MODEL_ID, tag) {
            IntroComponent.build().viewModel()
        }
        Content(
            onPrivacyPolicyClicked = {
                navigator.goTo(screens.privacyPolicy)
            },
            onContinueClicked = {
                viewModel.registerScreenSeen()
                navigator.setBackstack(target)
            },
        )
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
                Spacer(modifier = Modifier.height(64.dp))
                Image(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    painter = painterResource(R.drawable.app_logo),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(32.dp))
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
