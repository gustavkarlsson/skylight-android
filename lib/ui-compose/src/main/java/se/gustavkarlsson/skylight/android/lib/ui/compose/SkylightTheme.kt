package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.lightColors
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class SkylightColors(
    val material: Colors,
    val onSurfaceWeaker: Color,
) {
    val primary: Color get() = material.primary
    val primaryVariant: Color get() = material.primaryVariant
    val secondary: Color get() = material.secondary
    val secondaryVariant: Color get() = material.secondaryVariant
    val background: Color get() = material.background
    val surface: Color get() = material.surface
    val error: Color get() = material.error
    val onPrimary: Color get() = material.onPrimary
    val onSecondary: Color get() = material.onSecondary
    val onBackground: Color get() = material.onBackground
    val onSurface: Color get() = material.onSurface
    val onError: Color get() = material.onError
    val isLight: Boolean get() = material.isLight
    val primarySurface: Color get() = material.primarySurface
}

// background linked to theme.xml
private val darkPalette = SkylightColors(
    material = darkColors(
        background = Color(0xFF121212),
        primary = Color(0xFF0F9386),
        primaryVariant = Color(0xFF55C4B6),
        secondary = Color(0xFFFF4081),
        secondaryVariant = Color(0xFFFF79B0),
        onPrimary = Color.White,
        onSecondary = Color.White,
    ),
    onSurfaceWeaker = Color(0x8AFFFFFF),
)

// background linked to theme.xml
private val lightPalette = SkylightColors(
    material = lightColors(
        background = Color(0xFFFFFFFF),
        primary = Color(0xFF0F9386),
        primaryVariant = Color(0xFF55C4B6),
        secondary = Color(0xFFFF4081),
        secondaryVariant = Color(0xFFFF79B0),
        onPrimary = Color.White,
        onSecondary = Color.White,
    ),
    onSurfaceWeaker = Color(0x8A000000),
)

private val LocalColors = staticCompositionLocalOf { lightPalette }

private val skylightTypography = Typography().run {
    val newH6 = h6.copy(fontSize = h6.fontSize * 0.9)
    copy(h6 = newH6)
}

@Composable
fun SkylightTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkMode) darkPalette else lightPalette
    CompositionLocalProvider(
        LocalColors provides colors,
    ) {
        MaterialTheme(
            colors = colors.material,
            typography = skylightTypography,
            content = content,
        )
    }
}

val Colors: SkylightColors
    @Composable
    get() = LocalColors.current

val Shapes: Shapes
    @Composable
    get() = MaterialTheme.shapes

val Typography: Typography
    @Composable
    get() = MaterialTheme.typography

val Icons: Icons.Filled = Icons.Default
val AutoMirroredIcons: Icons.AutoMirrored.Filled = Icons.AutoMirrored.Default
