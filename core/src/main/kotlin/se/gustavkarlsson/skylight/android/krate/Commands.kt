package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.Place

sealed class SkylightCommand

object GetAuroraReportCommand : SkylightCommand()

data class AuroraReportStreamCommand(val stream: Boolean) : SkylightCommand()

data class SettingsStreamCommand(val stream: Boolean) : SkylightCommand()

object BootstrapCommand : SkylightCommand()

object SignalLocationPermissionGranted : SkylightCommand()

object SignalGooglePlayServicesInstalled : SkylightCommand()

object SignalFirstRunCompleted : SkylightCommand()

data class SelectPlaceCommand(val place: Place?) : SkylightCommand()
