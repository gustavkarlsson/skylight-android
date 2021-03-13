package se.gustavkarlsson.skylight.android.lib.ui.compose

import dev.chrisbanes.accompanist.insets.Insets
import dev.chrisbanes.accompanist.insets.WindowInsets


// Workaround for https://github.com/google/accompanist/issues/239
val WindowInsets.navigationBarsWithIme: Insets
    get() = if (ime.isVisible) ime else navigationBars
