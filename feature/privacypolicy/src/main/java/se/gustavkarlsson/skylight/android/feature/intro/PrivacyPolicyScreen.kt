package se.gustavkarlsson.skylight.android.feature.intro

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.feature.privacypolicy.R
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag
import se.gustavkarlsson.skylight.android.lib.ui.compose.AutoMirroredIcons
import se.gustavkarlsson.skylight.android.lib.ui.compose.MarkdownText
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.core.R as CoreR

@Parcelize
object PrivacyPolicyScreen : Screen {
    override val type: Screen.Type get() = Screen.Type.PrivacyPolicy

    @Composable
    override fun Content(activity: AppCompatActivity, tag: ServiceTag) {
        var markdownText by remember { mutableStateOf("") }
        LaunchedEffect(key1 = null) {
            withContext(Dispatchers.IO) {
                val privacyPolicyMarkdown = activity.resources.openRawResource(R.raw.privacy_policy)
                    .bufferedReader()
                    .readText()
                markdownText = privacyPolicyMarkdown
            }
        }
        Content(
            markdownText = markdownText,
            onBackClicked = { navigator.closeScreen() },
        )
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        markdownText = "Line1\nLine2",
        onBackClicked = {},
    )
}

@Composable
private fun Content(
    markdownText: String,
    onBackClicked: () -> Unit,
) {
    ScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    windowInsets = WindowInsets.statusBars,
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(AutoMirroredIcons.ArrowBack, contentDescription = null)
                        }
                    },
                    title = {
                        Text(stringResource(CoreR.string.privacy_policy))
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            ) {
                MarkdownText(
                    modifier = Modifier.padding(16.dp),
                    markdownText = markdownText,
                )
                Spacer(
                    modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                )
            }
        }
    }
}
