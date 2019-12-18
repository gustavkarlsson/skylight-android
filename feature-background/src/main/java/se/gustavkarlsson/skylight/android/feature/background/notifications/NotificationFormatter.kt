package se.gustavkarlsson.skylight.android.feature.background.notifications

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.feature.background.R
import se.gustavkarlsson.skylight.android.services.Formatter

internal class NotificationFormatter(
	private val chanceLevelFormatter: Formatter<ChanceLevel>
) : Formatter<Notification> {
	override fun format(value: Notification): TextRef {
		val lines = formatLines(value)
		val string = lines.joinToString("\n") { "%s" }
		return TextRef(string, *lines)
	}

	private fun formatLines(value: Notification): Array<TextRef> =
		value.data
			.sortedWith(comparator)
			.map {
				val chanceText = chanceLevelFormatter.format(it.chanceLevel)
				when (it.place) {
					Place.Current -> TextRef(R.string.x_at_your_current_location, chanceText)
					is Place.Custom -> TextRef(R.string.x_in_y, chanceText, it.place.name)
				}
			}.toTypedArray()
}

private val comparator =
	compareBy(::placeTypeComparable)
		.thenByDescending { it.chanceLevel }

private fun placeTypeComparable(placeWithChance: PlaceWithChance) =
	when (placeWithChance.place) {
		Place.Current -> -1
		is Place.Custom -> 1
	}
