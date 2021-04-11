package se.gustavkarlsson.skylight.android.feature.main.view

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
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
    lineDistance: Dp = 5.dp,
    lineWidthDps: ClosedRange<Dp> = 40.dp..80.dp,
    @FloatRange(from = 0.0, to = 1.0) altitudeVariation: Float = 0.4f, // FIXME non-zero values can cause lines to render outside of canvas
    @FloatRange(from = 0.0, to = 1.0) angleVariation: Float = 0.1f,
    @FloatRange(from = 0.0, to = 1.0) minHeightRatio: Float = 0.4f,
    colors: ColorRange = Color(0xFF4CFF86)..Color(0xFF4CBFA6),
    ttlMillis: LongRange = 2000L..8000L,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val renderer = Renderer(
            canvasWidth = constraints.maxWidth.toFloat(),
            canvasHeight = constraints.maxHeight.toFloat(),
            density = LocalDensity.current,
            distance = lineDistance,
            widthDps = lineWidthDps,
            altitudeVariation = altitudeVariation,
            angleVariation = angleVariation,
            minHeightRatio = minHeightRatio,
            colors = colors,
            ttlMillis = ttlMillis,
        )
        EveryFrame { frameMillis ->
            Canvas(Modifier.fillMaxSize()) {
                renderer.render(this, frameMillis)
            }
        }
    }
}

@Composable
private fun EveryFrame(
    content: @Composable (frameMillis: Long) -> Unit,
) {
    var frameTime by remember { mutableStateOf(0L) }
    if (frameTime > 0) {
        content(frameTime)
    }
    LaunchedEffect(key1 = null) {
        while (true) {
            frameTime = withFrameMillis { it }
        }
    }
}

private class Renderer(
    canvasWidth: Float,
    canvasHeight: Float,
    density: Density,
    distance: Dp,
    widthDps: ClosedRange<Dp>,
    altitudeVariation: Float,
    angleVariation: Float,
    minHeightRatio: Float,
    private val colors: ColorRange,
    private val ttlMillis: LongRange,
) {
    init {
        distance.value.requirePositive("distance")
        widthDps.map { it.value }.requirePositiveAndAscending("widthDps")
        altitudeVariation.requireRatio("altitudeVariation")
        angleVariation.requireRatio("angleVariation")
        minHeightRatio.requireRatio("minHeightRatio")
        ttlMillis.requirePositiveAndAscending("ttlMillis")
    }

    private val count: Int = with(density) {
        val widthDp = canvasWidth.toDp()
        (widthDp / distance).toInt()
    }
    private val xRange: ClosedRange<Float> = 0f..canvasWidth
    private val yRange: ClosedRange<Float> = let {
        val center = canvasHeight / 2
        val delta = (canvasHeight * altitudeVariation) / 2
        val min = center - delta
        val max = center + delta
        min..max
    }
    private val widths: ClosedRange<Float> = with(density) {
        widthDps.map { it.toPx() }
    }
    private val heights: ClosedRange<Float> = let {
        val minHeight = (canvasHeight * minHeightRatio)
        val maxHeight = (canvasHeight - ((canvasHeight * altitudeVariation) / 2))
        minHeight..maxHeight
    }
    private val angles: ClosedRange<Float> = let {
        val offset = angleVariation * 90f
        -offset..offset
    }

    private fun createLine(index: Int, timeMillis: Long): Line {
        val indexRandom = Random(index)
        val ttlMillis = ttlMillis.random(indexRandom)
        val generation = timeMillis / ttlMillis
        val instanceSeed = 31 * index + generation
        val instanceRandom = Random(instanceSeed)
        return Line(
            x = xRange.random(instanceRandom),
            y = yRange.random(instanceRandom),
            width = widths.random(instanceRandom),
            height = heights.random(instanceRandom),
            angle = angles.random(instanceRandom),
            color = colors.random(instanceRandom),
            age = (timeMillis % ttlMillis).toFloat() / ttlMillis,
        )
    }

    fun render(scope: DrawScope, timeMillis: Long) {
        repeat(count) { index ->
            val line = createLine(index, timeMillis)
            scope.draw(line)
        }
    }
}

private fun <T : Comparable<T>, R : Comparable<R>> ClosedRange<T>.map(block: (T) -> R): ClosedRange<R> {
    val start = block(start)
    val endInclusive = block(endInclusive)
    return start..endInclusive
}

private fun <T> ClosedRange<T>.requirePositiveAndAscending(name: String) where T : Comparable<T>, T : Number {
    require(start.toDouble() > 0.0) {
        "$name ($this) must be positive"
    }
    require(start <= endInclusive) {
        "$name ($this) must be ascending"
    }
}

private fun <T : Number> T.requirePositive(name: String) {
    require(this.toDouble() > 0.0) {
        "$name ($this) must be positive"
    }
}

private fun <T : Number> T.requireRatio(name: String) {
    require(this.toDouble() in 0.0..1.0) {
        "$name ($this) must be within 0..1"
    }
}

private fun ColorRange.random(random: Random): Color {
    val startHsl = start.toHsl()
    val endHsl = endInclusive.toHsl()
    val outHsl = FloatArray(3)
    ColorUtils.blendHSL(startHsl, endHsl, random.nextFloat(), outHsl)
    val colorInt = ColorUtils.HSLToColor(outHsl)
    val newAlpha = (start.alpha + endInclusive.alpha) / 2
    return Color(colorInt).copy(alpha = newAlpha)
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

private fun ClosedRange<Float>.random(random: Random): Float {
    val delta = endInclusive - start
    return start + (random.nextFloat() * delta)
}

private fun DrawScope.draw(line: Line) = with(line) {
    val steps = arrayOf(
        0f to color.copy(alpha = color.alpha * fadeInOut(age)),
        1f to Color.Transparent,
    )
    val higherThanWide = height > width
    val smallestDimension = if (higherThanWide) width else height
    val (scaleY, scaleX) = if (higherThanWide) {
        (height / width) to 1.0f
    } else 1.0f to (width / height)
    val downScaledStartY = y - ((height / 2) / scaleY)
    val downScaledEndY = y + ((height / 2) / scaleY)
    val downScaledWidth = width / scaleX
    val brush = Brush.radialGradient(
        *steps,
        center = Offset(x, y),
        radius = smallestDimension / 2,
    )
    rotate(angle) {
        scale(scaleX, scaleY, Offset(x, y)) {
            drawLine(brush, start = Offset(x, downScaledStartY), end = Offset(x, downScaledEndY), downScaledWidth)
        }
    }
}

private fun fadeInOut(age: Float): Float {
    return abs((age + 0.5f) % 1f - 0.5f) / 0.5f
}

private data class Line(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val angle: Float,
    val color: Color,
    val age: Float,
)
