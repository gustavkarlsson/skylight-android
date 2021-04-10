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

@Composable
fun Aurora() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val factory = LineFactory(canvasWidth.toFloat(), canvasHeight.toFloat(), LocalDensity.current)
        var lines by remember {
            val initialLines = factory.createInitialLines()
            mutableStateOf(initialLines, policy = neverEqualPolicy())
        }
        var elapsedTimeSeconds by remember { mutableStateOf(0f) }
        Canvas(Modifier.fillMaxSize()) {
            val newLines = lines.map { line ->
                if (line.ageSeconds > line.ttlSeconds) {
                    factory.createNewLine()
                } else {
                    line.incrementAgeSeconds(elapsedTimeSeconds)
                }
            }
            for (line in newLines) {
                draw(line)
            }
            lines = newLines
        }
        LaunchedEffect(key1 = null) {
            var lastTime = withFrameMillis { it }
            while (true) {
                val currentTime = withFrameMillis { it }
                val elapsedTimeMillis = currentTime - lastTime
                elapsedTimeSeconds = elapsedTimeMillis / 1000f
                lastTime = currentTime
            }
        }
    }
}

private fun Line.incrementAgeSeconds(increment: Float): Line = copy(ageSeconds = ageSeconds + increment)

private class LineFactory(
    canvasWidth: Float,
    canvasHeight: Float,
    density: Density,
    @FloatRange(from = 0.0) linesPerDp: Float = 0.1f,
    minLineWidthDp: Dp = 8.dp,
    maxLineWidthDp: Dp = 12.dp,
    @FloatRange(from = 0.0, to = 1.0) yRandomness: Float = 0.4f,
    @FloatRange(from = 0.0, to = 1.0) minHeightRatio: Float = 0.4f,
    color1: Color = Color(0xFF4CFFA6),
    color2: Color = Color(0xFF4CBFA6),
    @FloatRange(from = 0.0) minTtlSeconds: Float = 2f,
    @FloatRange(from = 0.0) maxTtlSeconds: Float = 8f,
) {
    init {
        require(linesPerDp > 0f) {
            "linesPerDp ($linesPerDp) must be positive"
        }
        require(minLineWidthDp >= 0.dp) {
            "minLineWidthDp ($minLineWidthDp) must be non-negative"
        }
        require(minLineWidthDp <= maxLineWidthDp) {
            "minLineWidthDp ($minLineWidthDp) must be <= maxLineWidthDp ($maxLineWidthDp)"
        }
        require(yRandomness in VALID_RATIO) {
            "yRandomness ($yRandomness) must be in $VALID_RATIO"
        }
        require(minHeightRatio in VALID_RATIO) {
            "minHeightRatio ($minHeightRatio) must be in $VALID_RATIO"
        }
        require(minTtlSeconds > 0f) {
            "minTtlSeconds ($minTtlSeconds) must be positive"
        }
        require(minTtlSeconds <= maxTtlSeconds) {
            "minTtlSeconds ($minTtlSeconds) must be <= maxTtlSeconds ($maxTtlSeconds)"
        }
    }

    private val lineCount = with(density) {
        val widthDp = canvasWidth.toDp()
        (linesPerDp * widthDp.value).toInt()
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
        val minPx = minLineWidthDp.toPx()
        val maxPx = maxLineWidthDp.toPx()
        minPx..maxPx
    }
    private val heightRange = let {
        val minLineHeight = (canvasHeight * minHeightRatio)
        val maxLineHeight = (canvasHeight - ((canvasHeight * yRandomness) / 2))
        minLineHeight..maxLineHeight
    }
    private val colorRange = color1..color2
    private val ttlSecondsRange = minTtlSeconds..maxTtlSeconds

    fun createInitialLines(): List<Line> = List(lineCount) { createLine(randomizeAge = true) }

    fun createNewLine(): Line = createLine(randomizeAge = false)

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

    private companion object {
        private val VALID_RATIO = 0f..1f
    }
}

private operator fun Color.rangeTo(that: Color): ColorRange {
    return ColorRange(this, that)
}

private data class ColorRange(val start: Color, val endInclusive: Color)

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

private fun ClosedFloatingPointRange<Float>.random(): Float {
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
