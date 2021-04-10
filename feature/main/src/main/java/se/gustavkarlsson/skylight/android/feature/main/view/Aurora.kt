package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.random.Random
import se.gustavkarlsson.skylight.android.lib.ui.compose.ColorRange
import se.gustavkarlsson.skylight.android.lib.ui.compose.rangeTo

@Composable
fun Aurora(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.0) linesPerDp: Float = 0.1f,
    lineWidthRange: ClosedRange<Dp> = 8.dp..12.dp,
    @FloatRange(from = 0.0, to = 1.0) lineYRandomness: Float = 0.4f,
    @FloatRange(from = 0.0, to = 1.0) minLineHeightRatio: Float = 0.4f,
    colorRange: ColorRange = Color(0xFF4CFFA6)..Color(0xFF4CBFA6),
    lineTtlSecondsRange: ClosedRange<Float> = 2f..8f,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val lineFactory = LineFactory(
            canvasWidth = canvasWidth.toFloat(),
            canvasHeight = canvasHeight.toFloat(),
            density = LocalDensity.current,
            countPerDp = linesPerDp,
            widthDpRange = lineWidthRange,
            yRandomness = lineYRandomness,
            minHeightRatio = minLineHeightRatio,
            colorRange = colorRange,
            ttlSecondsRange = lineTtlSecondsRange,
        )
        var lines by remember {
            val initialLines = lineFactory.createInitial()
            mutableStateOf(initialLines, policy = neverEqualPolicy())
        }
        EveryFrame { frameMillis ->
            Canvas(Modifier.fillMaxSize()) {
                val newLines = lines.map { line ->
                    if (line.ageSeconds > line.ttlSeconds) {
                        lineFactory.createNew()
                    } else {
                        val incrementSeconds = frameMillis / 1000f
                        line.incrementAgeSeconds(incrementSeconds)
                    }
                }
                for (line in newLines) {
                    draw(line)
                }
                lines = newLines
            }
        }
    }
}

@Composable
private fun EveryFrame(
    content: @Composable (frameMillis: Long) -> Unit,
) {
    var frameTime by remember { mutableStateOf(0L) }
    content(frameTime)
    LaunchedEffect(key1 = null) {
        var lastTime = withFrameMillis { it }
        while (true) {
            val currentTime = withFrameMillis { it }
            frameTime = currentTime - lastTime
            lastTime = currentTime
        }
    }
}

private fun Line.incrementAgeSeconds(increment: Float): Line = copy(ageSeconds = ageSeconds + increment)

private class LineFactory(
    canvasWidth: Float,
    canvasHeight: Float,
    density: Density,
    countPerDp: Float,
    widthDpRange: ClosedRange<Dp>,
    yRandomness: Float,
    minHeightRatio: Float,
    private val colorRange: ColorRange,
    private val ttlSecondsRange: ClosedRange<Float>,
) {
    init {
        countPerDp.requirePositive("countPerDp")
        widthDpRange.map { it.value }.requirePositiveIncreasing("widthDpRange")
        yRandomness.requireRatio("yRandomness")
        minHeightRatio.requireRatio("minHeightRatio")
        ttlSecondsRange.requirePositiveIncreasing("ttlSecondsRange")
    }

    private val count = with(density) {
        val widthDp = canvasWidth.toDp()
        (countPerDp * widthDp.value).toInt()
    }
    private val xRange = 0f..canvasWidth
    private val yRange = let {
        val center = canvasHeight / 2
        val delta = (canvasHeight * yRandomness) / 2
        val min = center - delta
        val max = center + delta
        min..max
    }
    private val widthRange = with(density) {
        widthDpRange.map { it.toPx() }
    }
    private val heightRange = let {
        val minHeight = (canvasHeight * minHeightRatio)
        val maxHeight = (canvasHeight - ((canvasHeight * yRandomness) / 2))
        minHeight..maxHeight
    }

    fun createInitial(): List<Line> = List(count) { createLine(randomizeAge = true) }

    fun createNew(): Line = createLine(randomizeAge = false)

    private fun createLine(randomizeAge: Boolean): Line {
        val ttlSeconds = ttlSecondsRange.random()
        return Line(
            x = xRange.random(),
            y = yRange.random(),
            width = widthRange.random(),
            height = heightRange.random(),
            color = colorRange.random(),
            ttlSeconds = ttlSeconds,
            ageSeconds = if (randomizeAge) (0f..ttlSeconds).random() else 0f,
        )
    }
}

private fun <T : Comparable<T>, R : Comparable<R>> ClosedRange<T>.map(block: (T) -> R): ClosedRange<R> {
    val start = block(start)
    val endInclusive = block(endInclusive)
    return start..endInclusive
}

private fun ClosedRange<Float>.requirePositiveIncreasing(name: String) {
    require(start > 0f) {
        "$name ($this) must be increasing"
    }
    require(start <= endInclusive) {
        "$name ($this) must be increasing"
    }
}

private fun Float.requirePositive(name: String) {
    require(this > 0f) {
        "$name ($this) must be positive"
    }
}

private fun Float.requireRatio(name: String) {
    require(this in 0f..1f) {
        "$name ($this) must be within 0..1"
    }
}

private fun ColorRange.random(): Color {
    val startHsl = start.toHsl()
    val endHsl = endInclusive.toHsl()
    val hsl = FloatArray(3)
    ColorUtils.blendHSL(startHsl, endHsl, Random.nextFloat(), hsl)
    val colorInt = ColorUtils.HSLToColor(hsl)
    return Color(colorInt)
}

private fun Color.toHsl(): FloatArray {
    val hsl = FloatArray(3)
    ColorUtils.RGBToHSL(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        hsl,
    )
    return hsl
}

private val BoxWithConstraintsScope.canvasWidth: Int
    get() = constraints.maxWidth

private val BoxWithConstraintsScope.canvasHeight: Int
    get() = constraints.maxHeight

private fun ClosedRange<Float>.random(): Float {
    val delta = endInclusive - start
    return start + (Random.nextFloat() * delta)
}

private fun DrawScope.draw(line: Line) = with(line) {
    val steps = arrayOf(
        0f to Color.Transparent,
        0.5f to color.copy(alpha = getAlpha(ageSeconds, ttlSeconds)),
        1f to Color.Transparent,
    )
    val startY = y - (height / 2)
    val endY = y + (height / 2)
    val brush = Brush.verticalGradient(
        *steps,
        startY = startY,
        endY = endY,
    )
    drawLine(brush, start = Offset(x, startY), end = Offset(x, endY), width)
}

private fun getAlpha(age: Float, ttl: Float): Float {
    val hm = 0.5f * ttl
    return abs((age + hm) % ttl - hm) / hm
}

private data class Line(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Color,
    val ttlSeconds: Float,
    val ageSeconds: Float,
)
