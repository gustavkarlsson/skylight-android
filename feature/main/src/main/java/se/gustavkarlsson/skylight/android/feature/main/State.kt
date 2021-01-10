package se.gustavkarlsson.skylight.android.feature.main

import se.gustavkarlsson.skylight.android.lib.aurora.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.lib.permissions.Access
import se.gustavkarlsson.skylight.android.lib.places.Place

internal data class State(
    val locationAccess: Access = Access.Unknown,
    val selectedPlace: Place,
    val selectedAuroraReport: LoadableAuroraReport = LoadableAuroraReport.LOADING,
)
