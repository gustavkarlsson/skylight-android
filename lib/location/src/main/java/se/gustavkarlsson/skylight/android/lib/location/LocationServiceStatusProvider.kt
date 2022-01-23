package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.flow.Flow

interface LocationServiceStatusProvider {
    val locationServiceStatus: Flow<LocationServiceStatus>
}

enum class LocationServiceStatus {
    Enabled, Disabled
}
