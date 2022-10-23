package se.gustavkarlsson.skylight.android.feature.main.view.linechart

import androidx.compose.ui.graphics.Color

val DefaultGridLine = StraightLineDrawer(Color.Black, 0f)

data class GridLine(
    val value: Double,
    val drawer: LineDrawer = DefaultGridLine,
) {
    init {
        require(value.isFinite()) { "value must be finite ($value)" }
    }
}

fun interface GridFactory {
    fun create(visibleRange: ClosedRange<Double>): List<GridLine>
}

data class AutomaticGridFactory(
    val targetCount: Double = 4.0,
    val baseInterval: Double = 1.0,
    val factor: Double = 2.0,
    val significantValues: Set<Double> = setOf(0.0),
    val drawer: LineDrawer = DefaultGridLine.copy(color = DefaultGridLine.color.copy(alpha = 0.25f)),
    val significantDrawer: LineDrawer = DefaultGridLine,
) : GridFactory {
    init {
        require(targetCount.isFinite() && targetCount > 0) {
            "targetCount must be a finite number > 0 ($targetCount)"
        }
        require(baseInterval.isFinite() && baseInterval > 0) {
            "baseInterval must be a finite number > 0 ($baseInterval)"
        }
        require(factor.isFinite() && factor > 1) {
            "factor must be a finite number > 1 ($factor)"
        }
        require(significantValues.all(Double::isFinite)) {
            "significantValues must be finite ($significantValues)"
        }
    }

    override fun create(visibleRange: ClosedRange<Double>): List<GridLine> {
        val interval = findInterval(baseInterval, targetCount, factor, visibleRange.size)
        val valuesToDraw = generateValuesAtInterval(visibleRange, interval)
        return valuesToDraw.map { GridLine(it, drawer) } + significantValues.map { GridLine(it, significantDrawer) }
    }
}

object EmptyGridFactory : GridFactory {
    override fun create(visibleRange: ClosedRange<Double>): List<GridLine> = emptyList()
}
