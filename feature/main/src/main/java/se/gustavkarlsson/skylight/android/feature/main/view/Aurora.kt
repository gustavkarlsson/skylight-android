package se.gustavkarlsson.skylight.android.feature.main.view

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.WindowManager
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
private const val minLineWidth = 10f
private const val maxLineWidth = 50f
private val validLineWidth = minLineWidth..maxLineWidth // FIXME base on dps
private val validTtlSeconds = 2f..10f
private val validHue = 120f..180f

@Composable
fun Aurora() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val frameTime = FrameTime
        var lines by remember { mutableStateOf(createLines()) }
        Canvas(Modifier.fillMaxSize()) {
            for (line in lines) {
                draw(line)
            }
            val newLines = lines.map { line ->
                if (line.ageSeconds > line.ttlSeconds) {
                    createLine(randomizeAge = false)
                } else {
                    line.copy(ageSeconds = line.ageSeconds + frameTime)
                }
            }
            lines = newLines
        }
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

private fun BoxWithConstraintsScope.createLines(): List<Line> {
    val lineCount = canvasWidth / minLineWidth.toInt()
    return List(lineCount) { createLine(randomizeAge = true) }
}

private fun BoxWithConstraintsScope.createLine(randomizeAge: Boolean): Line {
    val yVariance = (canvasHeight * 0.4f)
    val minLineHeight = (canvasHeight * 0.4f)
    val maxLineHeight = (canvasHeight - (yVariance / 2))
    val ttlSeconds = validTtlSeconds.random()
    return Line(
        x = (0..canvasWidth).random().toFloat(),
        y = (canvasHeight / 2) - (yVariance / 2) + (Random.nextFloat() * yVariance),
        width = validLineWidth.random(),
        height = (minLineHeight..maxLineHeight).random(),
        color = Color(ColorUtils.HSLToColor(floatArrayOf(validHue.random(), 1f, 0.65f))),
        ttlSeconds = ttlSeconds,
        ageSeconds = if (randomizeAge) (0f..ttlSeconds).random() else 0f,
    )
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
