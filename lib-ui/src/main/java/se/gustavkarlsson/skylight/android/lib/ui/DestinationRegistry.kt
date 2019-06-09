package se.gustavkarlsson.skylight.android.lib.ui

class DestinationRegistry {
	private val _destinations = mutableListOf<Destination>()

	@get:Synchronized
	val destinations: List<Destination>
		get() = _destinations.toList()

	@Synchronized
	fun register(destination: Destination) {
		_destinations += destination
		_destinations.sortByDescending { it.priority }
	}
}
