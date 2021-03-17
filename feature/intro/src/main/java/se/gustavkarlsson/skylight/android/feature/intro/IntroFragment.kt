package se.gustavkarlsson.skylight.android.feature.intro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import se.gustavkarlsson.skylight.android.lib.ui.compose.Colors
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
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
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
            onPickLocationClicked = ::onPickLocationClicked,
            onUseMyLocationClicked = ::onUseMyLocationClicked,
        )
    }

    private fun onPrivacyPolicyClicked() {
        navigator.goTo(screens.privacyPolicy)
    }

    private fun onPickLocationClicked() {
        viewModel.registerScreenSeen()
        val target = requireNotNull(requireArguments().target)
        navigator.goTo(screens.addPlace(target))
    }

    private fun onUseMyLocationClicked() {
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
        onPickLocationClicked = {},
        onUseMyLocationClicked = {},
    )
}

@ExperimentalAnimationApi
@Composable
private fun Content(
    onPrivacyPolicyClicked: () -> Unit,
    onPickLocationClicked: () -> Unit,
    onUseMyLocationClicked: () -> Unit,
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
            ) {
                AnimatedVisibility(
                    visible = true,
                    initiallyVisible = false,
                    enter = slideInHorizontally(
                        initialOffsetX = { x -> x },
                        animationSpec = spring(stiffness = 100f),
                    ),
                ) {
                    ExtendedFloatingActionButton(
                        backgroundColor = Colors.background,
                        contentColor = Colors.primary,
                        text = { Text(stringResource(id = R.string.intro_pick_location)) },
                        onClick = onPickLocationClicked,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AnimatedVisibility(
                    visible = true,
                    initiallyVisible = false,
                    enter = slideInHorizontally(
                        initialOffsetX = { x -> x },
                        animationSpec = spring(stiffness = 200f),
                    ),
                ) {
                    ExtendedFloatingActionButton(
                        backgroundColor = Colors.primary,
                        text = { Text(stringResource(id = R.string.intro_use_my_location)) },
                        onClick = onUseMyLocationClicked,
                    )
                }
            }
        }
    }
}
