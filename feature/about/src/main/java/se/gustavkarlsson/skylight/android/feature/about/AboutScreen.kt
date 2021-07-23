package se.gustavkarlsson.skylight.android.feature.about

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService

private val VIEW_MODE_ID = ServiceId("aboutViewModel")

@Parcelize
object AboutScreen : Screen {
    override val name get() = ScreenName.About

    @Composable
    override fun AppCompatActivity.Content(tag: ServiceTag, scope: CoroutineScope) {
        val viewModel = getOrRegisterService(VIEW_MODE_ID, tag) {
            AboutComponent.build().viewModel()
        }
        val text = viewModel.detailsText.resolve(this)
        Content(
            text = text,
            onBackClicked = { navigator.closeScreen() },
            onPrivacyPolicyClicked = { navigator.goTo(screens.privacyPolicy) },
        )
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        text = "Line1\nLine2",
        onBackClicked = {},
        onPrivacyPolicyClicked = {},
    )
}

@Composable
private fun Content(
    text: String,
    onBackClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
) {
    ScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    contentPadding = rememberInsetsPaddingValues(LocalWindowInsets.current.statusBars),
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.ArrowBack, contentDescription = null)
                        }
                    },
                    title = {
                        Text(stringResource(R.string.about))
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                Image(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp),
                    painter = painterResource(R.drawable.app_logo),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = Typography.h4,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = text, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                ClickableText(
                    text = stringResource(R.string.privacy_policy),
                    onClick = onPrivacyPolicyClicked,
                )
            }
        }
    }
}
