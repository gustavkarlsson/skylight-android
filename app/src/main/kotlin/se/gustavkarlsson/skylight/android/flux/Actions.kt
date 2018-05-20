package se.gustavkarlsson.skylight.android.flux

sealed class SkylightAction

object GetAuroraReportAction : SkylightAction()

data class AuroraReportStreamAction(val stream: Boolean) : SkylightAction()

data class ConnectivityStreamAction(val stream: Boolean) : SkylightAction()

data class ShowDialogAction(
	val titleResource: Int,
	val messageResource: Int
) : SkylightAction()

object HideDialogAction : SkylightAction()