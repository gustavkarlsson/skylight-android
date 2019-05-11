package se.gustavkarlsson.skylight.android.krate

sealed class Command {
	object RefreshAll : Command()
	object Bootstrap : Command()
	object SignalLocationPermissionGranted : Command()
	object SignalGooglePlayServicesInstalled : Command()
	object SignalFirstRunCompleted : Command()
	data class SelectPlace(val placeId: Int?) : Command()
}

