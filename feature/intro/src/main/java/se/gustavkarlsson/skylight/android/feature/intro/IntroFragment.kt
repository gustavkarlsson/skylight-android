package se.gustavkarlsson.skylight.android.feature.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import de.halfbit.edgetoedge.Edge
import de.halfbit.edgetoedge.EdgeToEdgeBuilder
import kotlinx.android.synthetic.main.fragment_intro.*
import kotlinx.coroutines.CoroutineScope
import reactivecircus.flowbinding.android.view.clicks
import se.gustavkarlsson.skylight.android.lib.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator
import se.gustavkarlsson.skylight.android.lib.navigation.Screens
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.navigation.target
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.getOrRegisterService
import se.gustavkarlsson.skylight.android.lib.ui.extensions.bind

internal class IntroFragment : ComposeScreenFragment() {

    private val viewModel by lazy {
        getOrRegisterService("introViewModel") {
            IntroComponent.build().viewModel()
        }
    }

    fun setupEdgeToEdge(): EdgeToEdgeBuilder.() -> Unit = {
        title.fit { Edge.Top }
        myLocationButton.fit { Edge.Bottom }
    }

    fun bindView(scope: CoroutineScope) {
        privacyPolicyLink.clicks().bind(scope) {
            navigator.goTo(screens.privacyPolicy)
        }

        myLocationButton.clicks().bind(scope) {
            PermissionsComponent.instance.locationPermissionRequester()
                .request(this)
            viewModel.registerScreenSeen()
            navigator.closeScreenAndGoTo(screens.main)
        }

        pickLocationButton.clicks().bind(scope) {
            viewModel.registerScreenSeen()
            val target = requireNotNull(requireArguments().target)
            navigator.goTo(screens.addPlace(target))
        }
    }

    @Composable
    override fun ScreenContent() {
        MaterialTheme {
            Content(navigator, screens)
        }
    }
}

@Composable
private fun Content(navigator: Navigator, screens: Screens) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = stringResource(id = R.string.intro_title),
            style = MaterialTheme.typography.h4,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = R.string.intro_desc),
            style = MaterialTheme.typography.body1,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = AnnotatedString(stringResource(R.string.privacy_policy)),
            onClick = { navigator.goTo(screens.privacyPolicy) },
        )
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
        ) {

        }
    }
}
