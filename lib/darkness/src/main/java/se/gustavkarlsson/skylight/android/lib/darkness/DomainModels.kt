package se.gustavkarlsson.skylight.android.lib.darkness

import java.util.function.IntFunction
import kotlin.time.Instant

data class Darkness(
    val sunZenithAngle: Double,
    val timestamp: Instant,
)

data class DarknessForecast(private val darknesses: List<Darkness>) : List<Darkness> by darknesses {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun <T : Any?> toArray(generator: IntFunction<Array<out T>>): Array<out T> {
        @Suppress("DEPRECATION")
        return super.toArray(generator)
    }
}
