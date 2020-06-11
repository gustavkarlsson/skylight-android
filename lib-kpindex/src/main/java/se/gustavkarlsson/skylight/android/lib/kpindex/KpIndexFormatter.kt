package se.gustavkarlsson.skylight.android.lib.kpindex

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.services.Formatter

internal object KpIndexFormatter : Formatter<KpIndex> {
    override fun format(value: KpIndex): TextRef {
        val kpIndex = value.value
        val whole = kpIndex.toInt()
        val part = kpIndex - whole
        val partString =
            parsePart(
                part
            )
        val wholeString =
            parseWhole(
                whole,
                partString
            )
        return TextRef(wholeString + partString)
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
        return wholeAdjusted.toString()
    }
}
