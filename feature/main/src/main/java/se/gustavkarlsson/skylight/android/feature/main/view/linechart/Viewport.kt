package se.gustavkarlsson.skylight.android.feature.main.view.linechart

fun interface ViewportFactory {
    fun create(valueRange: ValueRange): ValueRange
}

data class AutomaticViewportFactory(val zoom: Double = 0.9) : ViewportFactory {
    init {
        require(zoom.isFinite() && zoom > 0) {
            "zoom must be a finite number > 0 ($zoom)"
        }
    }

    override fun create(valueRange: ValueRange): ValueRange {
        val scale = 1 / zoom
        val scaledHorizontalRange = valueRange.horizontalRange.scaleAroundCenter(scale)
        val scaledVerticalRange = valueRange.verticalRange.scaleAroundCenter(scale)
        return ValueRange(scaledHorizontalRange, scaledVerticalRange)
    }

    private fun ClosedRange<Double>.scaleAroundCenter(scale: Double): ClosedRange<Double> {
        val center = (start + endInclusive) / 2
        val scaledSize = size * scale
        val offset = scaledSize / 2
        return (center - offset)..(center + offset)
    }
}

data class ManualViewportFactory(val viewport: ValueRange) : ViewportFactory {
    override fun create(valueRange: ValueRange): ValueRange = viewport
}
