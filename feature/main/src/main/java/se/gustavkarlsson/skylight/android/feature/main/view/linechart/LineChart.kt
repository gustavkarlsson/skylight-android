@file:OptIn(ExperimentalTextApi::class)

package se.gustavkarlsson.skylight.android.feature.main.view.linechart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TEXT_TO_CHART_PADDING_PX = 10
private val pointXAxisComparator = compareBy<Point> { it.x }

// FIXME support RTL?
// FIXME zoom/scale?
@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    lines: List<Line>,
    viewportFactory: ViewportFactory = AutomaticViewportFactory(),
    xLabelFactory: LabelFactory = AutomaticLabelFactory(),
    yLabelFactory: LabelFactory = AutomaticLabelFactory(),
    horizontalGridFactory: GridFactory = AutomaticGridFactory(),
    verticalGridFactory: GridFactory = AutomaticGridFactory(),
    contentPadding: PaddingValues = PaddingValues(start = 48.dp, top = 24.dp, end = 24.dp, bottom = 48.dp),
) {
    val textMeasurer = rememberTextMeasurer()
    val (pointsByLine, valueRange) = remember(lines) {
        val pointsByLine = lines.associateWith { line ->
            line.points.sortedWith(pointXAxisComparator)
        }
        val allPointsSorted = pointsByLine.values.flatten().sortedWith(pointXAxisComparator)
        val allYs = allPointsSorted.map { it.y }
        val valueRange = if (allPointsSorted.isNotEmpty()) {
            ValueRange(
                minX = allPointsSorted.first().x,
                maxX = allPointsSorted.last().x,
                minY = allYs.min(),
                maxY = allYs.max(),
            )
        } else {
            null
        }
        pointsByLine to valueRange
    }
    val viewport = remember(viewportFactory, valueRange) {
        if (valueRange != null) {
            viewportFactory.create(valueRange).growZeroSizeAxesTo(1.0)
        } else {
            ValueRange(Point.ZERO, 1.0)
        }
    }
    val xLabels = remember(xLabelFactory, viewport.horizontalRange) {
        xLabelFactory.create(viewport.horizontalRange).filter { it.value in viewport.horizontalRange }
    }
    val yLabels = remember(yLabelFactory, viewport.verticalRange) {
        yLabelFactory.create(viewport.verticalRange).filter { it.value in viewport.verticalRange }
    }
    val horizontalGridLines = remember(horizontalGridFactory, viewport.verticalRange) {
        horizontalGridFactory.create(viewport.verticalRange).filter { it.value in viewport.verticalRange }
    }
    val verticalGridLines = remember(verticalGridFactory, viewport.horizontalRange) {
        verticalGridFactory.create(viewport.horizontalRange).filter { it.value in viewport.horizontalRange }
    }
    Canvas(modifier = modifier.defaultMinSize(200.dp, 200.dp)) {
        val chartScope = ChartScope(
            drawScope = this,
            pointsByLine = pointsByLine,
            textMeasurer = textMeasurer,
            xLabels = xLabels,
            yLabels = yLabels,
            horizontalGridLines = horizontalGridLines,
            verticalGridLines = verticalGridLines,
            viewport = viewport,
        )
        inset(
            left = contentPadding.calculateLeftPadding(layoutDirection).toPx(),
            top = contentPadding.calculateTopPadding().toPx(),
            right = contentPadding.calculateRightPadding(layoutDirection).toPx(),
            bottom = contentPadding.calculateBottomPadding().toPx(),
        ) {
            clipRect {
                withChart(chartScope) {
                    drawHorizontalGridLines()
                    drawVerticalGridLines()
                    drawLines()
                }
            }
            withChart(chartScope) {
                drawXLabels(contentPadding.calculateBottomPadding().toPx())
                drawYLabels(contentPadding.calculateLeftPadding(layoutDirection).toPx())
            }
        }
    }
}

private data class ChartScope(
    private val drawScope: DrawScope,
    val pointsByLine: Map<Line, List<Point>>,
    val textMeasurer: TextMeasurer,
    val xLabels: List<Label>,
    val yLabels: List<Label>,
    val horizontalGridLines: List<GridLine>,
    val verticalGridLines: List<GridLine>,
    val viewport: ValueRange,
) : DrawScope by drawScope {
    val toXCoordinate: Double.() -> Float by lazy {
        val min = viewport.minX
        val max = viewport.maxX
        val range = max - min
        val k = size.width / range
        val m = -(min * k)
        return@lazy { ((k * this) + m).toFloat() }
    }
    val toYCoordinate: Double.() -> Float by lazy {
        // min and max are flipped, because draw coordinate system has Y inverted
        val min = viewport.maxY
        val max = viewport.minY
        val range = max - min
        val k = size.height / range
        val m = -(min * k)
        return@lazy { ((k * this) + m).toFloat() }
    }
    val valueToOffset: Point.() -> Offset = { Offset(x.toXCoordinate(), y.toYCoordinate()) }
}

private inline fun DrawScope.withChart(chartScope: ChartScope, block: ChartScope.() -> Unit) {
    chartScope.copy(drawScope = this).run(block)
}

private fun ChartScope.drawHorizontalGridLines() {
    for (line in horizontalGridLines) {
        val y = line.value.toYCoordinate()
        val start = Offset(0f, y)
        val end = Offset(size.width, y)
        with(line.drawer) {
            drawLine(listOf(start, end))
        }
    }
}

private fun ChartScope.drawVerticalGridLines() {
    for (line in verticalGridLines) {
        val x = line.value.toXCoordinate()
        val start = Offset(x, 0f)
        val end = Offset(x, size.height)
        with(line.drawer) {
            drawLine(listOf(start, end))
        }
    }
}

private fun ChartScope.drawLines() {
    for ((line, sortedPoints) in pointsByLine) {
        val offsets = sortedPoints.map(valueToOffset)
        with(line.drawer) {
            drawLine(offsets)
        }
    }
}

private fun ChartScope.drawXLabels(maxSizePx: Float) {
    // FIXME adjust position and max size based on rotation
    for (label in xLabels) {
        val measuredText = textMeasurer.measure(
            text = AnnotatedString(label.text),
            style = label.style,
            overflow = TextOverflow.Ellipsis,
            constraints = Constraints(maxHeight = (maxSizePx - TEXT_TO_CHART_PADDING_PX).toInt()),
        )
        val textSize = measuredText.size
        val xCoordinate = label.value.toXCoordinate()
        val x = xCoordinate - (textSize.width / 2f)
        val y = (size.height + TEXT_TO_CHART_PADDING_PX)
        rotate(label.rotationDegrees, pivot = Offset(xCoordinate, size.height + (textSize.height / 2f))) {
            drawText(measuredText, label.color, Offset(x, y))
        }
    }
}

private fun ChartScope.drawYLabels(maxSizePx: Float) {
    // FIXME adjust position and max size based on rotation
    for (label in yLabels) {
        val measuredText = textMeasurer.measure(
            text = AnnotatedString(label.text),
            style = label.style,
            overflow = TextOverflow.Ellipsis,
            constraints = Constraints(maxWidth = (maxSizePx - TEXT_TO_CHART_PADDING_PX).toInt()),
        )
        val textSize = measuredText.size
        val x = (-textSize.width - TEXT_TO_CHART_PADDING_PX).toFloat()
        val yCoordinate = label.value.toYCoordinate()
        val y = yCoordinate - (textSize.height / 2f)
        rotate(label.rotationDegrees, pivot = Offset(-(textSize.width / 2f), yCoordinate)) {
            drawText(measuredText, label.color, Offset(x, y))
        }
    }
}

@Preview
@Composable
fun PreviewAnimatedLineChart() {
    MaterialTheme(colors = lightColors()) {
        Surface {
            LineChart(
                modifier = Modifier.size(480.dp, 240.dp),
                lines = listOf(
                    Line(
                        points = listOf(
                            Point(-3.0, 0.0),
                            Point(-2.0, -2.0),
                            Point(0.0, 2.0),
                            Point(2.0, -2.0),
                            Point(3.0, -1.0),
                            Point(4.5, -2.0),
                            Point(5.5, -1.0),
                        ),
                        drawer = CurvedLineDrawer(
                            color = Color.Green,
                            strokeWidth = 3f,
                        ),
                    ),
                    Line(
                        points = listOf(
                            Point(-4.0, 2.0),
                            Point(-3.0, -1.0),
                            Point(-2.0, 0.0),
                            Point(1.0, 1.0),
                            Point(3.0, 5.0),
                            Point(4.0, 4.0),
                        ),
                        drawer = StraightLineDrawer(
                            color = Color.Blue,
                            strokeWidth = 2f,
                            pointColor = Color.Red,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
                        ),
                    ),
                ),
                viewportFactory = animateRandomViewport(),
                contentPadding = PaddingValues(start = 48.dp, bottom = 48.dp, top = 24.dp, end = 24.dp),
            )
        }
    }
}

@Composable
private fun animateRandomViewport(): ViewportFactory {
    val radius = remember { Animatable(8f) }
    val centerX = remember { Animatable(0f) }
    val centerY = remember { Animatable(0f) }
    LaunchedEffect(null) {
        launch {
            while (true) radius.animateTo(1 + (Random.nextFloat() * 6), spring(stiffness = 10f))
        }
        launch {
            while (true) centerX.animateTo(-2 + (Random.nextFloat() * 4), spring(stiffness = 10f))
        }
        launch {
            while (true) centerY.animateTo(-2 + (Random.nextFloat() * 4), spring(stiffness = 10f))
        }
    }
    val viewport = ValueRange(Point(centerX.value.toDouble(), centerY.value.toDouble()), radius.value.toDouble())
    return ManualViewportFactory(viewport)
}

@Preview
@Composable
fun PreviewEmptyLineChart() {
    MaterialTheme(colors = lightColors()) {
        Surface {
            LineChart(
                modifier = Modifier.size(480.dp, 240.dp),
                lines = emptyList(),
            )
        }
    }
}
