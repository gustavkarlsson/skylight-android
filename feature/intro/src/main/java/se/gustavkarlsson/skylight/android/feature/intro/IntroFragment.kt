package se.gustavkarlsson.skylight.android.feature.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.systemBarsPadding
import kotlinx.android.synthetic.main.fragment_intro.*
import kotlinx.coroutines.launch
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground

internal class IntroFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("introViewModel") {
            IntroComponent.build().viewModel()
        }
    }

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

@Composable
@Preview
private fun Content(
    onPrivacyPolicyClicked: () -> Unit = {},
    onPickLocationClicked: () -> Unit = {},
    onUseMyLocationClicked: () -> Unit = {},
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
                style = MaterialTheme.typography.h4,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(id = R.string.intro_desc),
                style = MaterialTheme.typography.body1,
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
                ExtendedFloatingActionButton(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary,
                    text = { Text(stringResource(id = R.string.intro_pick_location)) },
                    onClick = onPickLocationClicked
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExtendedFloatingActionButton(
                    backgroundColor = MaterialTheme.colors.primary,
                    text = { Text(stringResource(id = R.string.intro_use_my_location)) },
                    onClick = onUseMyLocationClicked
                )
            }
        }
    }
}
