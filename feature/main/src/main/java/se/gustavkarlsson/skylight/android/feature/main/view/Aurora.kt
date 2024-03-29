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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

@Composable
fun Aurora(
    modifier: Modifier = Modifier,
    flareDistance: Dp = 5.dp,
    flareWidthDps: ClosedRange<Dp> = 40.dp..80.dp,
    @FloatRange(from = 0.0, to = 1.0) altitudeVariation: Float = 0.3f,
    @FloatRange(from = 0.0, to = 1.0) angleVariation: Float = 0.1f,
    @FloatRange(from = 0.0, to = 1.0) heightVariation: Float = 0.6f,
    color1: Color = Color(0xFF4CFF86),
    color2: Color = Color(0xFF4CBFA6),
    ttlMillis: LongRange = 2000L..8000L,
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val renderer = Renderer(
            canvasWidth = constraints.maxWidth.toFloat(),
            canvasHeight = constraints.maxHeight.toFloat(),
            density = LocalDensity.current,
            distance = flareDistance,
            widthDps = flareWidthDps,
            altitudeVariation = altitudeVariation,
            angleVariation = angleVariation,
            heightVariation = heightVariation,
            colors = color1 to color2,
            ttlMillis = ttlMillis,
        )
        EveryFrame { timeMillis ->
            Canvas(Modifier.fillMaxSize()) {
                renderer.render(this, timeMillis)
            }
        }
    }
}

@Composable
private fun EveryFrame(content: @Composable (timeMillis: Long) -> Unit) {
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
    private val canvasHeight: Float,
    density: Density,
    distance: Dp,
    widthDps: ClosedRange<Dp>,
    altitudeVariation: Float,
    angleVariation: Float,
    private val heightVariation: Float,
    private val colors: Pair<Color, Color>,
    private val ttlMillis: LongRange,
) {
    init {
        distance.value.requirePositive("distance")
        widthDps.map { it.value }.requirePositiveAndAscending("widthDps")
        altitudeVariation.requireRatio("altitudeVariation")
        angleVariation.requireRatio("angleVariation")
        heightVariation.requireRatio("heightVariation")
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

    private val angles: ClosedRange<Float> = let {
        val offset = angleVariation * 90f
        -offset..offset
    }

    private fun createFlare(index: Int, timeMillis: Long): Flare {
        val indexRandom = Random(index)
        val ttlMillis = ttlMillis.random(indexRandom)
        val generation = timeMillis / ttlMillis
        val instanceSeed = 31 * index + generation
        val instanceRandom = Random(instanceSeed)
        val y = yRange.random(instanceRandom)
        val heights: ClosedRange<Float> = createHeights(y)
        return Flare(
            x = xRange.random(instanceRandom),
            y = y,
            width = widths.random(instanceRandom),
            height = heights.random(instanceRandom),
            angle = angles.random(instanceRandom),
            color = colors.random(instanceRandom),
            age = (timeMillis % ttlMillis).toFloat() / ttlMillis,
        )
    }

    private fun createHeights(y: Float): ClosedRange<Float> {
        val maxHeightBottom = y * 2
        val maxHeightTop = (canvasHeight - y) * 2
        val maxHeight = min(maxHeightBottom, maxHeightTop)
        val minHeightWhenCentered = (canvasHeight * (1 - heightVariation))
        val minHeight = minHeightWhenCentered.coerceAtMost(maxHeight)
        return (minHeight..maxHeight)
    }

    fun render(scope: DrawScope, timeMillis: Long) {
        repeat(count) { index ->
            val flare = createFlare(index, timeMillis)
            scope.draw(flare)
        }
    }

    private fun DrawScope.draw(flare: Flare) = with(flare) {
        val steps = arrayOf(
            0f to color.copy(alpha = color.alpha * fadeInOut(age)),
            1f to Color.Transparent,
        )
        val higherThanWide = height > width
        val smallestDimension = if (higherThanWide) width else height
        val (scaleY, scaleX) = if (higherThanWide) {
            (height / width) to 1.0f
        } else {
            1.0f to (width / height)
        }
        val downScaledStartY = y - ((height / 2) / scaleY)
        val downScaledEndY = y + ((height / 2) / scaleY)
        val downScaledWidth = width / scaleX
        val brush = Brush.radialGradient(
            *steps,
            center = Offset(x, y),
            radius = smallestDimension / 2,
        )
        rotate(angle, Offset(x, y)) {
            scale(scaleX, scaleY, Offset(x, y)) {
                drawLine(
                    brush,
                    start = Offset(x, downScaledStartY),
                    end = Offset(x, downScaledEndY),
                    strokeWidth = downScaledWidth,
                    blendMode = BlendMode.Screen,
                )
            }
        }
    }

    private fun fadeInOut(age: Float): Float {
        return abs((age + 0.5f) % 1f - 0.5f) / 0.5f
    }

    private data class Flare(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float,
        val angle: Float,
        val color: Color,
        val age: Float,
    )
}

private fun <T : Comparable<T>, R : Comparable<R>> ClosedRange<T>.map(block: (T) -> R): ClosedRange<R> {
    val start = block(start)
    val endInclusive = block(endInclusive)
    return start..endInclusive
}

private fun Float.requirePositive(name: String) {
    require(this > 0f) {
        "$name ($this) must be positive"
    }
}

private fun Float.requireRatio(name: String) {
    require(this in 0f..1f) {
        "$name ($this) must be a valid ratio (0.0-1.0)"
    }
}

private fun <T> ClosedRange<T>.requirePositiveAndAscending(name: String) where T : Comparable<T>, T : Number {
    require(start.toDouble() > 0.0 && start <= endInclusive) {
        "$name ($this) must be positive and ascending"
    }
}

private fun ClosedRange<Float>.random(random: Random): Float {
    val delta = endInclusive - start
    return start + (random.nextFloat() * delta)
}

private fun Pair<Color, Color>.random(random: Random): Color {
    val startArgb = first.toArgb()
    val endArgb = second.toArgb()
    val colorInt = ColorUtils.blendARGB(startArgb, endArgb, random.nextFloat())
    return Color(colorInt)
}
