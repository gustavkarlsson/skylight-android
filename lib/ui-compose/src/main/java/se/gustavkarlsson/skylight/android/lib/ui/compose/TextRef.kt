package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ioki.textref.TextRef

@Composable
fun textRef(textRef: TextRef): String {
    return textRef.resolve(LocalContext.current)
}
