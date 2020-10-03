package se.gustavkarlsson.skylight.android.feature.main

import kotlinx.coroutines.CoroutineScope
import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.places.Place

internal data class State(
    val streamScope: CoroutineScope? = null,
    val locationAccess: Access = Access.Unknown,
    val selectedPlace: Place,
    val selectedAuroraReport: LoadableAuroraReport = LoadableAuroraReport.LOADING,
)
