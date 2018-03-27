package se.gustavkarlsson.skylight.android.services_impl.formatters

import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class VisibilityFormatter : SingleValueFormatter<Visibility> {
	override fun format(value: Visibility): CharSequence {
		val clouds = value.cloudPercentage ?: return "?"
		val visibilityPercentage = 100 - clouds
		return Integer.toString(visibilityPercentage) + '%'
	}
}
