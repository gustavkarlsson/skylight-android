package se.gustavkarlsson.skylight.android.lib.ui.compose

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon

@Composable
fun MarkdownText(
    modifier: Modifier = Modifier,
    markdownText: String,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context)
        },
        update = { view ->
            Markwon.create(view.context).setMarkdown(view, markdownText)
        },
    )
}
