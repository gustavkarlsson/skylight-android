package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ScreenBackground(
    content: @Composable () -> Unit,
) {
    SkylightTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Colors.background,
            content = content,
        )
    }
}
