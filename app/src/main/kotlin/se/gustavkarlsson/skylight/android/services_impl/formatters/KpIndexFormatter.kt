package se.gustavkarlsson.skylight.android.services_impl.formatters

import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class KpIndexFormatter : SingleValueFormatter<KpIndex> {
	override fun format(value: KpIndex): CharSequence {
		val kpIndex = value.value ?: return "?"
		val whole = kpIndex.toInt()
		val part = kpIndex - whole
		val partString = parsePart(part)
		val wholeString = parseWhole(whole, partString)
		return wholeString + partString
	}

	private fun parsePart(part: Double): String {
		if (part < 0.15) {
			return ""
		}
		if (part < 0.5) {
			return "+"
		}
		return "-"
	}

	private fun parseWhole(whole: Int, partString: String): String {
		val wholeAdjusted = if (partString == "-") whole + 1 else whole
		return Integer.toString(wholeAdjusted)
	}
}
