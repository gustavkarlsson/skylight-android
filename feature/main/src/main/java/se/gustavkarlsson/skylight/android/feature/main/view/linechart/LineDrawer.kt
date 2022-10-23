package se.gustavkarlsson.skylight.android.feature.main.view.linechart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

fun interface LineDrawer {
    fun DrawScope.drawLine(offsets: List<Offset>)
}

data class StraightLineDrawer(
    val color: Color,
    val strokeWidth: Float = 4f,
    val pointColor: Color = Color.Transparent,
    val pointSize: Float = (strokeWidth * 3).coerceAtLeast(10f),
    val cap: StrokeCap = StrokeCap.Butt,
    val pathEffect: PathEffect? = null,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DrawScope.DefaultBlendMode,
) : LineDrawer {
    override fun DrawScope.drawLine(offsets: List<Offset>) {
        drawPoints(
            points = offsets,
            pointMode = PointMode.Polygon,
            color = color,
            strokeWidth = strokeWidth,
            cap = cap,
            pathEffect = pathEffect,
            colorFilter = colorFilter,
            blendMode = blendMode,
        )
        if (pointColor.alpha > 0f) {
            drawPoints(
                points = offsets,
                pointMode = PointMode.Points,
                color = pointColor,
                strokeWidth = pointSize,
                cap = StrokeCap.Round,
                colorFilter = colorFilter,
                blendMode = blendMode,
            )
        }
    }
}

data class CurvedLineDrawer(
    val color: Color,
    val strokeWidth: Float = 4f,
    val pathEffect: PathEffect? = null,
    val cornerRadius: Float = Float.MAX_VALUE,
    val colorFilter: ColorFilter? = null,
    val blendMode: BlendMode = DrawScope.DefaultBlendMode,
) : LineDrawer {
    private val style = Stroke(
        width = strokeWidth,
        miter = 0f,
        pathEffect = if (pathEffect != null) {
            PathEffect.chainPathEffect(pathEffect, PathEffect.cornerPathEffect(cornerRadius))
        } else {
            PathEffect.cornerPathEffect(cornerRadius)
        },
    )

    override fun DrawScope.drawLine(offsets: List<Offset>) {
        if (offsets.isEmpty()) return
        val path = Path().apply {
            moveTo(offsets.first().x, offsets.first().y)
            for (offset in offsets.drop(1)) {
                lineTo(offset.x, offset.y)
            }
        }
        drawPath(path, color, alpha = 1f, style, colorFilter, blendMode)
    }
}

object NoLineDrawer : LineDrawer {
    override fun DrawScope.drawLine(offsets: List<Offset>) {}
}
