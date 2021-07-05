package se.gustavkarlsson.skylight.android.feature.intro

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.noties.markwon.Markwon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import se.gustavkarlsson.skylight.android.feature.privacypolicy.R
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar

@Parcelize
object PrivacyPolicyScreen : Screen {
    override val name get() = ScreenName.PrivacyPolicy

    @Composable
    override fun AppCompatActivity.Content(scope: CoroutineScope) {
        var text by remember { mutableStateOf("") }
        LaunchedEffect(key1 = null) {
            withContext(Dispatchers.IO) {
                val privacyPolicyMarkdown = resources.openRawResource(R.raw.privacy_policy)
                    .bufferedReader()
                    .readText()
                text = privacyPolicyMarkdown
            }
        }
        Content(
            text = text,
            onBackClicked = { navigator.closeScreen() }
        )
    }
}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        text = "Line1\nLine2",
        onBackClicked = {},
    )
}

@Composable
private fun Content(
    text: String,
    onBackClicked: () -> Unit,
) {
    ScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    contentPadding = rememberInsetsPaddingValues(
                        insets = LocalWindowInsets.current.statusBars,
                        additionalStart = AppBarHorizontalPadding,
                        additionalEnd = AppBarHorizontalPadding,
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(Icons.ArrowBack, contentDescription = null)
                        }
                    },
                    title = {
                        Text(stringResource(R.string.privacy_policy))
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
            ) {
                AndroidView(
                    modifier = Modifier.padding(16.dp),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        Markwon.create(view.context).setMarkdown(view, text)
                    }
                )
                Spacer(
                    modifier = Modifier.padding(rememberInsetsPaddingValues(LocalWindowInsets.current.navigationBars))
                )
            }
        }
    }
}
