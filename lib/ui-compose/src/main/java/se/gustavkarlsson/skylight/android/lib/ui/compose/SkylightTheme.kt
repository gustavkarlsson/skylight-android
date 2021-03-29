package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// FIXME add colors? If so remove from colors.xml
// <color name="bars">@android:color/transparent</color>
private fun darkPalette() = darkColors(
    primary = Color(0xFF0F9386),
    primaryVariant = Color(0xFF55C4B6),
    secondary = Color(0xFFFF4081),
    secondaryVariant = Color(0xFFFF79B0),
    background = Color(0xFF121212),
    surface = Color(0xFF161616),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black,
)

// FIXME add colors? If so remove from colors.xml
// <color name="bars">#52000000</color>
private fun lightPalette() = lightColors(
    primary = Color(0xFF0F9386),
    primaryVariant = Color(0xFF55C4B6),
    secondary = Color(0xFFFF4081),
    secondaryVariant = Color(0xFFFF79B0),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White,
)

// FIXME add typo
/*
<style name="AppTheme.TextTitleLarge" parent="TextAppearance.MaterialComponents.Headline4">
    <item name="android:textColor">?android:attr/textColor</item>
    <item name="autoSizeTextType">uniform</item>
    <item name="autoSizeMaxTextSize">40sp</item>
    <item name="android:lines">1</item>
</style>

<style name="AppTheme.TextTitleSmall" parent="TextAppearance.MaterialComponents.Headline5">
    <item name="android:textColor">?android:attr/textColor</item>
    <item name="autoSizeTextType">uniform</item>
    <item name="autoSizeMaxTextSize">24sp</item>
    <item name="android:lines">1</item>
</style>

<style name="AppTheme.TextBody1" parent="TextAppearance.MaterialComponents.Body1">
    <item name="android:textColor">?android:attr/textColorPrimary</item>
</style>

<style name="AppTheme.TextBody2" parent="TextAppearance.MaterialComponents.Body2">
    <item name="android:textColor">?android:attr/textColorSecondary</item>
</style>
*/
@Composable
fun SkylightTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkMode) darkPalette() else lightPalette()
    MaterialTheme(
        colors = colors,
        content = content,
    )
}

// FIXME integrate extra colors properly
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
    get() = if (isSystemInDarkTheme()) {
        Color(0xB3FFFFFF)
    } else {
        Color(0xB3000000)
    }

val Colors.onSurfaceDisabled: Color
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0x61FFFFFF)
    } else {
        Color(0x61000000)
    }

val Colors.onSurfaceDivider: Color
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0x52FFFFFF)
    } else {
        Color(0x52000000)
    }

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
