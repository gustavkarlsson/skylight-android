package se.gustavkarlsson.skylight.android.feature.main.view

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random


private const val FALLBACK_REFRESH_RATE = 60
private const val MAX_HUE = 360f
private const val minLineWidth = 10f
private const val maxLineWidth = 50f
private val validLineWidth = minLineWidth..maxLineWidth // FIXME base on dps

@Composable
fun Aurora() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val frameTime = FrameTime
        val factory = LineFactory(canvasWidth, canvasHeight)
        var lines by remember {
            val lineCount = canvasWidth / minLineWidth.toInt()
            val lines = List(lineCount) { factory.createLine(randomizeAge = true) }
            mutableStateOf(lines)
        }
        Canvas(Modifier.fillMaxSize()) {
            for (line in lines) {
                draw(line)
            }
            val newLines = lines.map { line ->
                if (line.ageSeconds > line.ttlSeconds) {
                    factory.createLine(randomizeAge = false)
                } else {
                    line.copy(ageSeconds = line.ageSeconds + frameTime)
                }
            }
            lines = newLines
        }
    }
}

private data class LineFactory(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
    private val color: Color = Color(0xFF4CFFA6),
    @FloatRange(from = 0.0, to = 1.0) private val hueRandomness: Float = 0.15f,
    @FloatRange(from = 0.0, to = 1.0) private val yRandomness: Float = 0.4f,
    @FloatRange(from = 0.0, to = 1.0) private val minHeightRatio: Float = 0.4f,
    @FloatRange(from = 0.0) private val minTtlSeconds: Float = 2f,
    @FloatRange(from = 0.0) private val maxTtlSeconds: Float = 8f,
) {
    fun createLine(randomizeAge: Boolean): Line {
        require(minTtlSeconds <= maxTtlSeconds) {
            "minTtlSeconds ($minTtlSeconds) must be <= maxTtlSeconds ($maxTtlSeconds)"
        }
        val yRandomnessPx = (canvasHeight * yRandomness)
        val minLineHeight = (canvasHeight * minHeightRatio)
        val maxLineHeight = (canvasHeight - (yRandomnessPx / 2))
        val ttlSeconds = (minTtlSeconds..maxTtlSeconds).random()
        return Line(
            x = (0..canvasWidth).random().toFloat(),
            y = (canvasHeight / 2) - (yRandomnessPx / 2) + (Random.nextFloat() * yRandomnessPx),
            width = validLineWidth.random(),
            height = (minLineHeight..maxLineHeight).random(),
            color = color.randomizeHue(hueRandomness),
            ttlSeconds = ttlSeconds,
            ageSeconds = if (randomizeAge) (0f..ttlSeconds).random() else 0f,
        )
    }
}

private val FrameTime: Float
    @Composable
    get() {
        val context = LocalContext.current
        val refreshRate = context.displayCompat?.refreshRate?.roundToInt() ?: FALLBACK_REFRESH_RATE
        return 1f / refreshRate
    }

private val Context.displayCompat: Display?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display
    } else {
        @Suppress("DEPRECATION")
        getSystemService<WindowManager>()?.defaultDisplay
    }

private val BoxWithConstraintsScope.canvasWidth: Int
    get() = constraints.maxWidth

private val BoxWithConstraintsScope.canvasHeight: Int
    get() = constraints.maxHeight

private fun ClosedFloatingPointRange<Float>.random(): Float {
    val delta = endInclusive - start
    return start + (Random.nextFloat() * delta)
}

private fun Color.randomizeHue(randomness: Float): Color {
    val inHsl = FloatArray(3)
    ColorUtils.RGBToHSL(
        (red * 255).roundToInt(),
        (green * 255).roundToInt(),
        (blue * 255).roundToInt(),
        inHsl,
    )
    val hueDelta = (randomness * MAX_HUE) / 2
    val validHue = (inHsl[0] - hueDelta)..(inHsl[0] + hueDelta)
    val outHsl = inHsl.copyOf()
    inHsl[0] = validHue.random() % MAX_HUE
    val colorInt = ColorUtils.HSLToColor(outHsl)
    return Color(colorInt)
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
