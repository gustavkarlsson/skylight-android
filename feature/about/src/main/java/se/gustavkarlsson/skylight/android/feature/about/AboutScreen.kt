package se.gustavkarlsson.skylight.android.feature.about

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
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
import com.google.accompanist.insets.toPaddingValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.navigation.screens
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.ClickableText
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar
import se.gustavkarlsson.skylight.android.lib.ui.compose.Typography
import se.gustavkarlsson.skylight.android.lib.ui.getOrRegisterService

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Parcelize
class AboutScreen(private val dummy: Unit = Unit) : Screen { // FIXME do we need dummy, or can this be an object?
    @IgnoredOnParcel
    override val name = ScreenName.About

    private val AppCompatActivity.viewModel: AboutViewModel
        get() = getOrRegisterService(this@AboutScreen, "aboutViewModel") {
            AboutComponent.build().viewModel()
        }

    @Composable
    override fun AppCompatActivity.Content() {
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
                    contentPadding = LocalWindowInsets.current.statusBars
                        .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                Image(
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp),
                    painter = painterResource(R.drawable.about_logo),
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
