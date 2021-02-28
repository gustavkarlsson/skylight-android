package se.gustavkarlsson.skylight.android.lib.compose

import androidx.compose.runtime.Composable
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun SkylightTheme(content: @Composable () -> Unit) {
    MdcTheme(content = content)
}
