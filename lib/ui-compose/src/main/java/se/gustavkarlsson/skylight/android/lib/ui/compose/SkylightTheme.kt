package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun SkylightTheme(content: @Composable () -> Unit) {
    MdcTheme(content = content)
}

val Colors.onSurfaceWeaker: Color
    @Composable
    get() = colorResource(R.color.on_surface_weaker)

val Colors.onSurfaceDisabled: Color
    @Composable
    get() = colorResource(R.color.on_surface_disabled)

val Colors.onSurfaceDivider: Color
    @Composable
    get() = colorResource(R.color.on_surface_divider)
