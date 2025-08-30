package se.gustavkarlsson.skylight.android.lib.kpindex

import java.util.function.IntFunction
import kotlin.time.Instant

data class KpIndex(
    val value: Double,
    val timestamp: Instant,
)

data class KpIndexForecast(private val kpIndexes: List<KpIndex>) : List<KpIndex> by kpIndexes {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun <T : Any?> toArray(generator: IntFunction<Array<out T>>): Array<out T> {
        @Suppress("DEPRECATION")
        return super.toArray(generator)
    }
}
