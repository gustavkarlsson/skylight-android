package se.gustavkarlsson.skylight.android.lib.location

import android.app.Activity

interface LocationSettingsResolver {
    suspend fun resolve(activity: Activity) : Resolution
}

enum class Resolution {
    NotNeeded, Offered, Unavailable
}
