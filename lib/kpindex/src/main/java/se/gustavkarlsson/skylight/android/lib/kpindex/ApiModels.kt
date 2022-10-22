package se.gustavkarlsson.skylight.android.lib.kpindex

import kotlinx.serialization.Serializable

@Serializable
internal data class KpIndexBody(val value: Double, val timestamp: Long)

@Serializable
internal data class KpIndexForecastBody(val kpIndexes: List<KpIndexBody>)
