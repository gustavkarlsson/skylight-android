package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.flow.StateFlow

interface LocationServiceStatusProvider {
    val locationServicesStatus: StateFlow<LocationServiceStatus>
}

enum class LocationServiceStatus {
    Enabled, Disabled
}
