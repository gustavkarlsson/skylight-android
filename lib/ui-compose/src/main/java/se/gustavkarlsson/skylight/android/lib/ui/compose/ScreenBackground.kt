package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun ScreenBackground(
    content: @Composable () -> Unit,
) {
    SkylightTheme {
        ProvideWindowInsets {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Colors.background,
                content = content,
            )
        }
    }
}
