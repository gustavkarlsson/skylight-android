package se.gustavkarlsson.skylight.android.initializers

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

internal fun Application.initThreeThen() {
    AndroidThreeTen.init(this)
}
