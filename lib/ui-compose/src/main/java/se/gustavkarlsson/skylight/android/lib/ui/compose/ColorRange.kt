package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.ui.graphics.Color

data class ColorRange(val start: Color, val endInclusive: Color)

operator fun Color.rangeTo(that: Color): ColorRange {
    return ColorRange(this, that)
}
