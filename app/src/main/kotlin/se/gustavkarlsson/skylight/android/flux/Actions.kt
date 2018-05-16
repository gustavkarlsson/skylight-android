package se.gustavkarlsson.skylight.android.flux

sealed class Action

object GetAuroraReportAction : Action()

data class AuroraReportStreamAction(val stream: Boolean) : Action()

data class ConnectivityStreamAction(val stream: Boolean) : Action()
