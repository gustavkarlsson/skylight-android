package se.gustavkarlsson.skylight.android.feature.intro

import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.setPadding
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.toPaddingValues
import io.noties.markwon.Markwon
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.gustavkarlsson.skylight.android.feature.privacypolicy.R
import se.gustavkarlsson.skylight.android.lib.navigation.navigator
import se.gustavkarlsson.skylight.android.lib.ui.compose.AppBarHorizontalPadding
import se.gustavkarlsson.skylight.android.lib.ui.compose.ComposeScreenFragment
import se.gustavkarlsson.skylight.android.lib.ui.compose.Icons
import se.gustavkarlsson.skylight.android.lib.ui.compose.ScreenBackground
import se.gustavkarlsson.skylight.android.lib.ui.compose.TopAppBar

class PrivacyPolicyFragment : ComposeScreenFragment() {

    @Composable
    override fun ScreenContent() {
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
                    contentPadding = LocalWindowInsets.current.statusBars
                        .toPaddingValues(additionalHorizontal = AppBarHorizontalPadding),
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
        ) {
            val padding = with(LocalDensity.current) { 16.dp.toPx() }
            AndroidView(
                modifier = Modifier.navigationBarsPadding(),
                factory = { context ->
                    val scrollView = ScrollView(context)
                    scrollView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )

                    val textView = TextView(context)
                    textView.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                    )
                    textView.setPadding(padding.roundToInt())
                    textView.tag = "markdown"

                    scrollView.addView(textView)
                    scrollView
                },
                update = { scrollView ->
                    val textView = scrollView.findViewWithTag<TextView>("markdown")
                    Markwon.create(scrollView.context).setMarkdown(textView, text)
                }
            )
        }
    }
}
