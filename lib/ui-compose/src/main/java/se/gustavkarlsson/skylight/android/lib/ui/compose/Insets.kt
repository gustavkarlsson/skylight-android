package se.gustavkarlsson.skylight.android.lib.ui.compose

import com.google.accompanist.insets.WindowInsets

// Workaround for https://github.com/google/accompanist/issues/239
val WindowInsets.navigationBarsWithIme: WindowInsets.Type
    get() = if (ime.isVisible) ime else navigationBars
