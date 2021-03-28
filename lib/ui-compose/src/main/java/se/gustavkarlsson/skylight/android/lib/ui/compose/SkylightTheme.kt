package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun SkylightTheme(content: @Composable () -> Unit) {
    MdcTheme(content = content)
}

val Colors: Colors
    @Composable
    get() = MaterialTheme.colors

val Shapes: Shapes
    @Composable
    get() = MaterialTheme.shapes

val Typography: Typography
    @Composable
    get() = MaterialTheme.typography

val Icons: Icons.Filled = Icons.Default

val Colors.onSurfaceWeaker: Color
    @Composable
    get() = colorResource(R.color.on_surface_weaker)

val Colors.onSurfaceDisabled: Color
    @Composable
    get() = colorResource(R.color.on_surface_disabled)

val Colors.onSurfaceDivider: Color
    @Composable
    get() = colorResource(R.color.on_surface_divider)

val Colors.progressLowest: Color
    get() = Color(0xFFF44336)

val Colors.progressMedium: Color
    get() = Color(0xFFF0F436)

val Colors.progressHighest: Color
    get() = Color(0xFF34B4E2)

val Colors.bell: Color
    get() = Color(0xFFFFEE58)

val Colors.heart: Color
    get() = Color(0xFFFF7043)
