package se.gustavkarlsson.skylight.android

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

internal fun Application.initThreeThen() {
    AndroidThreeTen.init(this)
}
