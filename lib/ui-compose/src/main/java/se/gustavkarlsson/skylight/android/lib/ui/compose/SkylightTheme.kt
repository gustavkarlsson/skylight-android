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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class SkylightColors(
    val material: Colors,
    val onSurfaceWeaker: Color,
    val progressLowest: Color,
    val progressMedium: Color,
    val progressHighest: Color,
    val bell: Color,
    val bookmark: Color,
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

private val darkPalette = SkylightColors(
    material = darkColors(
        primary = Color(0xFF0F9386),
        primaryVariant = Color(0xFF55C4B6),
        secondary = Color(0xFFFF4081),
        secondaryVariant = Color(0xFFFF79B0),
        onPrimary = Color.White,
        onSecondary = Color.White,
    ),
    onSurfaceWeaker = Color(0xB3FFFFFF),
    progressLowest = Color(0xFFF44336),
    progressMedium = Color(0xFFF0F436),
    progressHighest = Color(0xFF34B4E2),
    bell = Color(0xFFFFEE58),
    bookmark = Color(0xFF008000),
)

private val lightPalette = SkylightColors(
    material = lightColors(
        primary = Color(0xFF0F9386),
        primaryVariant = Color(0xFF55C4B6),
        secondary = Color(0xFFFF4081),
        secondaryVariant = Color(0xFFFF79B0),
        onPrimary = Color.White,
        onSecondary = Color.White,
    ),
    onSurfaceWeaker = Color(0xB3000000),
    progressLowest = Color(0xFFF44336),
    progressMedium = Color(0xFFF0F436),
    progressHighest = Color(0xFF34B4E2),
    bell = Color(0xFFFFEE58),
    bookmark = Color(0xFF008000),
)

private val LocalColors = staticCompositionLocalOf { lightPalette }

private val openSansCondensed = FontFamily(
    Font(R.font.open_sans_condensed_light, weight = FontWeight.Light),
    Font(R.font.open_sans_condensed_bold, weight = FontWeight.Bold),
    Font(R.font.open_sans_condensed_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
)

@Composable
fun SkylightTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkMode) darkPalette else lightPalette
    val systemUiController = rememberSystemUiController()
    CompositionLocalProvider(
        LocalColors provides colors,
    ) {
        SideEffect {
            systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = !darkMode)
        }
        MaterialTheme(
            colors = colors.material,
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

val Typography.chanceTitle: TextStyle
    get() = h3.copy(fontFamily = openSansCondensed)

val Typography.chanceSubtitle: TextStyle
    get() = subtitle1.copy(fontFamily = openSansCondensed, fontSize = 20.sp)

val Icons: Icons.Filled = Icons.Default
