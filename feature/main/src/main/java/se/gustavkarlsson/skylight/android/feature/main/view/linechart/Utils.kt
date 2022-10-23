package se.gustavkarlsson.skylight.android.feature.main.view.linechart

import kotlin.math.abs
import kotlin.math.roundToInt

fun findInterval(
    baseInterval: Double,
    targetCount: Double,
    factor: Double,
    size: Double,
): Double {
    val op: Double.(Double) -> Double = if (size < targetCount) {
        Double::div
    } else {
        Double::times
    }
    return findIntervalRecurse(baseInterval, targetCount, factor, op, size)
}

private tailrec fun findIntervalRecurse(
    interval: Double,
    targetCount: Double,
    factor: Double,
    op: Double.(Double) -> Double,
    size: Double,
): Double {
    val count = size / interval
    val newInterval = interval.op(factor)
    val newCount = size / newInterval
    return if (abs(count - targetCount) < abs(newCount - targetCount)) {
        interval
    } else {
        findIntervalRecurse(newInterval, targetCount, factor, op, size)
    }
}

fun generateValuesAtInterval(
    valueRange: ClosedRange<Double>,
    interval: Double,
): List<Double> = buildList {
    var valueToDraw = (valueRange.start / interval).roundToInt() * interval
    while (valueToDraw < valueRange.endInclusive) {
        add(valueToDraw)
        valueToDraw += interval
    }
}

val ClosedRange<Double>.size: Double get() = endInclusive - start
