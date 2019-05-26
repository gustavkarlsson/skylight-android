package se.gustavkarlsson.skylight.android.feature.base

class DestinationRegistry {
	private val _destinations = mutableListOf<Destination>()

	@get:Synchronized
	val destinations: List<Destination>
		get() = _destinations.toList()

	@Synchronized
	fun register(destination: Destination) {
		val duplicatedDestination = _destinations.find { it.name == destination.name }
		require(duplicatedDestination == null) {
			"Duplicate destination names (${destination.name}) detected in " +
				"${destination::class.java.name} and " +
				"${duplicatedDestination!!::class.java.name} "
		}
		_destinations += destination
		_destinations.sortByDescending { it.priority }
	}
}
