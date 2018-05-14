package se.gustavkarlsson.skylight.android.actions

sealed class Action

object GetAuroraReportAction : Action()

object StreamAuroraReportsAction : Action()

object StreamConnectivityAction : Action()
