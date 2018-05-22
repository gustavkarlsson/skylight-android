package se.gustavkarlsson.skylight.android.flux

sealed class SkylightCommand

object GetAuroraReportCommand : SkylightCommand()

data class AuroraReportStreamCommand(val stream: Boolean) : SkylightCommand()

data class ConnectivityStreamCommand(val stream: Boolean) : SkylightCommand()

data class ShowDialogCommand(
	val titleResource: Int,
	val messageResource: Int
) : SkylightCommand()

object HideDialogCommand : SkylightCommand()
