package se.gustavkarlsson.skylight.android.lib.location

import kotlinx.coroutines.flow.Flow

interface LocationServiceStatusProvider {
    // FIXME change to enum values
    val locationServicesStatus: Flow<Boolean>
}
