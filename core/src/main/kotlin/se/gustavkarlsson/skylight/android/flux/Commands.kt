package se.gustavkarlsson.skylight.android.flux

sealed class SkylightCommand

object GetAuroraReportCommand : SkylightCommand()

data class AuroraReportStreamCommand(val stream: Boolean) : SkylightCommand()

data class ConnectivityStreamCommand(val stream: Boolean) : SkylightCommand()

object HideDialogCommand : SkylightCommand()

object SetLocationPermissionGrantedCommand : SkylightCommand()
