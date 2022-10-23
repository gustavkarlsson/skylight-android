package se.gustavkarlsson.skylight.android.feature.main.view.linechart

data class Point(val x: Double, val y: Double) {
    init {
        require(x.isFinite()) { "x must be finite ($x)" }
        require(y.isFinite()) { "y must be finite ($y)" }
    }

    companion object {
        val ZERO = Point(0.0, 0.0)
    }
}

data class ValueRange(val minX: Double, val maxX: Double, val minY: Double, val maxY: Double) {
    init {
        require(minX.isFinite()) { "minX must be finite ($minX)" }
        require(maxX.isFinite()) { "maxX must be finite ($maxX)" }
        require(minY.isFinite()) { "minY must be finite ($minY)" }
        require(maxY.isFinite()) { "maxY must be finite ($maxY)" }
    }

    constructor(min: Point, max: Point) : this(min.x, max.x, min.y, max.y)

    constructor(center: Point, radius: Double) : this(
        minX = center.x - radius,
        maxX = center.x + radius,
        minY = center.y - radius,
        maxY = center.y + radius,
    )

    constructor(horizontalRange: ClosedRange<Double>, verticalRange: ClosedRange<Double>) : this(
        minX = horizontalRange.start,
        maxX = horizontalRange.endInclusive,
        minY = verticalRange.start,
        maxY = verticalRange.endInclusive,
    )

    val horizontalRange: ClosedRange<Double> get() = minX..maxX
    val verticalRange: ClosedRange<Double> get() = minY..maxY
}

fun ValueRange.growZeroSizeAxesTo(radius: Double): ValueRange {
    require(radius > 0) { "radius must be positive ($radius)" }
    val newHorizontalRange = horizontalRange.growZeroSizeTo(radius)
    val newVerticalRange = verticalRange.growZeroSizeTo(radius)
    return ValueRange(newHorizontalRange, newVerticalRange)
}

private fun ClosedRange<Double>.growZeroSizeTo(radius: Double): ClosedRange<Double> {
    return takeIf { it.start != it.endInclusive } ?: (start - radius)..(start + radius)
}

data class Line(val points: List<Point>, val drawer: LineDrawer)
