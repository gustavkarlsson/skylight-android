package se.gustavkarlsson.skylight.android.feature.main.view

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.core.graphics.ColorUtils
import kotlin.math.abs
import kotlin.random.Random

private const val minLineWidth = 10f
private const val maxLineWidth = 50f
private val validLineWidth = minLineWidth..maxLineWidth // FIXME base on dps
private val validTtl = 100..700
private val validHue = 120f..180f

@Composable
fun Aurora() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        var lines by remember {
            val lineCount = canvasWidth / minLineWidth.toInt()
            val list = List(lineCount) { createLine() }
            mutableStateOf(list)
        }
        Canvas(Modifier.fillMaxSize()) {
            for (line in lines) {
                line.draw(this)
            }
            val updatedLines = lines.map { line ->
                if (line.age > line.ttl) {
                    createLine()
                } else {
                    line.copy(age = line.age + 1)
                }
            }
            lines = updatedLines
        }
    }
}

private fun BoxWithConstraintsScope.createLine(): Line {
    val random = Random.Default
    val yVariance = (canvasHeight * 0.4f)
    val minLineHeight = (canvasHeight * 0.4f)
    val maxLineHeight = (canvasHeight - (yVariance / 2))
    val colorHsl = floatArrayOf(validHue.random(), 1f, 0.65f)
    return Line(
        x = (0..canvasWidth).random().toFloat(),
        y = (canvasHeight / 2) - (yVariance / 2) + (random.nextFloat() * yVariance),
        width = validLineWidth.random(),
        height = (minLineHeight..maxLineHeight).random(),
        color = Color(ColorUtils.HSLToColor(colorHsl)),
        ttl = validTtl.random()
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

private data class Line(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Color,
    val ttl: Int,
    val age: Int = 0,
) {

    fun draw(scope: DrawScope) {
        val colorEdge = color.copy(alpha = 0f)
        val colorCenter = color.copy(alpha = fade(age, ttl))
        val steps = arrayOf(
            0f to colorEdge,
            0.5f to colorCenter,
            1f to colorEdge,
        )
        val startY = y - (height / 2)
        val endY = y + (height / 2)
        val brush = Brush.verticalGradient(
            *steps,
            startY = startY,
            endY = endY,
        )

        val path = Path().apply {
            moveTo(x, startY)
            lineTo(x, endY)
            close()
        }
        scope.drawPath(path, brush, style = Stroke(width))
    }
}

private fun fade(age: Int, ttl: Int): Float {
    val hm = 0.5f * ttl
    return abs((age + hm) % ttl - hm) / hm
}
