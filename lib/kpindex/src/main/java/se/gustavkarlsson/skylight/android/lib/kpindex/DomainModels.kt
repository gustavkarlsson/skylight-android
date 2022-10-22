package se.gustavkarlsson.skylight.android.lib.kpindex

import kotlinx.datetime.Instant

data class KpIndex(
    val value: Double,
    val timestamp: Instant,
)

data class KpIndexForecast(private val kpIndexes: List<KpIndex>) : List<KpIndex> by kpIndexes
