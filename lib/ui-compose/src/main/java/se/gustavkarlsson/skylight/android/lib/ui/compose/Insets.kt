package se.gustavkarlsson.skylight.android.lib.ui.compose

import com.google.accompanist.insets.Insets
import com.google.accompanist.insets.WindowInsets
import com.google.accompanist.insets.coerceEachDimensionAtLeast

val WindowInsets.navigationBarsWithIme: Insets
    get() = ime.coerceEachDimensionAtLeast(navigationBars)
