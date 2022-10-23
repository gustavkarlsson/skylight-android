package se.gustavkarlsson.skylight.android.feature.main.view.linechart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import java.text.DecimalFormat

data class Label(
    val value: Double,
    val text: String,
    val color: Color,
    val style: TextStyle = TextStyle.Default,
    val rotationDegrees: Float = 0f,
) {
    init {
        require(value.isFinite()) { "value must be finite ($value)" }
    }
}

fun interface LabelFactory {
    fun create(visibleRange: ClosedRange<Double>): List<Label>
}

data class AutomaticLabelFactory(
    val targetCount: Double = 4.0,
    val baseInterval: Double = 1.0,
    val factor: Double = 2.0,
    val color: Color = Color.Black,
    val format: (Double) -> String = DecimalFormat("0.0#")::format,
) : LabelFactory {
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
    }

    override fun create(visibleRange: ClosedRange<Double>): List<Label> {
        val interval = findInterval(baseInterval, targetCount, factor, visibleRange.size)
        val valuesToDraw = generateValuesAtInterval(visibleRange, interval)
        return valuesToDraw.map { Label(it, format(it), color) }
    }
}

object EmptyLabelFactory : LabelFactory {
    override fun create(visibleRange: ClosedRange<Double>): List<Label> = emptyList()
}
