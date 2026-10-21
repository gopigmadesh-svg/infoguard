package com.example.myapplication.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Custom Canvas helper to draw a security shield outline/fill
 */
fun DrawScope.drawShieldPath(
    color: Color,
    isStroke: Boolean = false,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.5f, h * 0.05f) // Top tip
        cubicTo(
            w * 0.75f, h * 0.05f,
            w * 0.95f, h * 0.12f,
            w * 0.95f, h * 0.25f
        )
        cubicTo(
            w * 0.95f, h * 0.60f,
            w * 0.70f, h * 0.85f,
            w * 0.50f, h * 0.98f
        )
        cubicTo(
            w * 0.30f, h * 0.85f,
            w * 0.05f, h * 0.60f,
            w * 0.05f, h * 0.25f
        )
        cubicTo(
            w * 0.05f, h * 0.12f,
            w * 0.25f, h * 0.05f,
            w * 0.50f, h * 0.05f
        )
        close()
    }

    if (isStroke) {
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    } else {
        drawPath(path = path, color = color)
    }
}

/**
 * Custom Canvas helper to draw a checkmark
 */
fun DrawScope.drawCheckmarkPath(
    color: Color,
    strokeWidth: Float = 6f
) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.28f, h * 0.50f)
        lineTo(w * 0.45f, h * 0.68f)
        lineTo(w * 0.73f, h * 0.35f)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

/**
 * Custom Canvas helper to draw a 4-point sparkle star
 */
fun DrawScope.drawSparkleStar(
    center: Offset,
    sizePx: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y - sizePx)
        quadraticTo(center.x, center.y, center.x + sizePx, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + sizePx)
        quadraticTo(center.x, center.y, center.x - sizePx, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - sizePx)
        close()
    }
    drawPath(path = path, color = color)
}

/**
 * Custom Canvas helper to draw a news document card sheet
 */
fun DrawScope.drawDocumentSheet(
    fillColor: Color,
    lineColor: Color
) {
    val w = size.width
    val h = size.height

    drawRoundRect(
        color = fillColor,
        topLeft = Offset(0f, 0f),
        size = Size(w, h),
        cornerRadius = CornerRadius(16f, 16f)
    )

    drawRoundRect(
        color = lineColor.copy(alpha = 0.2f),
        topLeft = Offset(w * 0.25f, h * 0.15f),
        size = Size(w * 0.5f, h * 0.28f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    val lineY1 = h * 0.52f
    val lineY2 = h * 0.66f
    val lineY3 = h * 0.80f

    drawLine(
        color = lineColor,
        start = Offset(w * 0.20f, lineY1),
        end = Offset(w * 0.80f, lineY1),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = lineColor,
        start = Offset(w * 0.20f, lineY2),
        end = Offset(w * 0.70f, lineY2),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = lineColor,
        start = Offset(w * 0.20f, lineY3),
        end = Offset(w * 0.50f, lineY3),
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
}

/**
 * Custom Canvas helper to draw AI Brain / Neural Network icon
 */
fun DrawScope.drawBrainIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    val pathLeft = Path().apply {
        moveTo(w * 0.50f, h * 0.18f)
        cubicTo(w * 0.30f, h * 0.15f, w * 0.15f, h * 0.30f, w * 0.15f, h * 0.50f)
        cubicTo(w * 0.15f, h * 0.65f, w * 0.25f, h * 0.80f, w * 0.45f, h * 0.85f)
        lineTo(w * 0.50f, h * 0.85f)
    }

    val pathRight = Path().apply {
        moveTo(w * 0.50f, h * 0.18f)
        cubicTo(w * 0.70f, h * 0.15f, w * 0.85f, h * 0.30f, w * 0.85f, h * 0.50f)
        cubicTo(w * 0.85f, h * 0.65f, w * 0.75f, h * 0.80f, w * 0.55f, h * 0.85f)
        lineTo(w * 0.50f, h * 0.85f)
    }

    val style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(path = pathLeft, color = color, style = style)
    drawPath(path = pathRight, color = color, style = style)

    drawLine(
        color = color,
        start = Offset(w * 0.50f, h * 0.22f),
        end = Offset(w * 0.50f, h * 0.80f),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.35f, h * 0.40f))
    drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.65f, h * 0.40f))
    drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.32f, h * 0.62f))
    drawCircle(color = color, radius = strokeWidth * 0.8f, center = Offset(w * 0.68f, h * 0.62f))
}

/**
 * Custom Canvas helper to draw Magnifying Glass Search Icon
 */
fun DrawScope.drawSearchIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    drawCircle(
        color = color,
        radius = w * 0.30f,
        center = Offset(w * 0.40f, h * 0.40f),
        style = Stroke(width = strokeWidth)
    )

    drawLine(
        color = color,
        start = Offset(w * 0.62f, h * 0.62f),
        end = Offset(w * 0.85f, h * 0.85f),
        strokeWidth = strokeWidth * 1.2f,
        cap = StrokeCap.Round
    )
}

/**
 * Custom Canvas helper to draw Security Lock Icon
 */
fun DrawScope.drawLockIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    val shacklePath = Path().apply {
        moveTo(w * 0.30f, h * 0.45f)
        lineTo(w * 0.30f, h * 0.30f)
        cubicTo(w * 0.30f, h * 0.12f, w * 0.70f, h * 0.12f, w * 0.70f, h * 0.30f)
        lineTo(w * 0.70f, h * 0.45f)
    }
    drawPath(
        path = shacklePath,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.20f, h * 0.42f),
        size = Size(w * 0.60f, h * 0.48f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = strokeWidth)
    )

    drawCircle(
        color = color,
        radius = strokeWidth * 1.2f,
        center = Offset(w * 0.50f, h * 0.62f)
    )
}

/**
 * Custom Canvas helper to draw Email Envelope Icon
 */
fun DrawScope.drawEmailIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.15f, h * 0.25f),
        size = Size(w * 0.70f, h * 0.50f),
        cornerRadius = CornerRadius(10f, 10f),
        style = Stroke(width = strokeWidth)
    )

    val flapPath = Path().apply {
        moveTo(w * 0.18f, h * 0.28f)
        lineTo(w * 0.50f, h * 0.55f)
        lineTo(w * 0.82f, h * 0.28f)
    }
    drawPath(
        path = flapPath,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

/**
 * Custom Canvas helper to draw User Person Profile Icon
 */
fun DrawScope.drawPersonIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    drawCircle(
        color = color,
        radius = w * 0.22f,
        center = Offset(w * 0.50f, h * 0.32f),
        style = Stroke(width = strokeWidth)
    )

    val shouldersPath = Path().apply {
        moveTo(w * 0.18f, h * 0.82f)
        cubicTo(w * 0.20f, h * 0.60f, w * 0.80f, h * 0.60f, w * 0.82f, h * 0.82f)
    }
    drawPath(
        path = shouldersPath,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

/**
 * Custom Canvas helper to draw Globe / Language Icon
 */
fun DrawScope.drawGlobeIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    drawCircle(
        color = color,
        radius = w * 0.40f,
        center = Offset(w * 0.50f, h * 0.50f),
        style = Stroke(width = strokeWidth)
    )

    drawLine(
        color = color,
        start = Offset(w * 0.10f, h * 0.50f),
        end = Offset(w * 0.90f, h * 0.50f),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )

    val ellipsePath = Path().apply {
        moveTo(w * 0.50f, h * 0.10f)
        cubicTo(w * 0.25f, h * 0.25f, w * 0.25f, h * 0.75f, w * 0.50f, h * 0.90f)
        cubicTo(w * 0.75f, h * 0.75f, w * 0.75f, h * 0.25f, w * 0.50f, h * 0.10f)
    }
    drawPath(
        path = ellipsePath,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

/**
 * Custom Canvas helper to draw Back Arrow Icon
 */
fun DrawScope.drawBackArrowIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.65f, h * 0.25f)
        lineTo(w * 0.35f, h * 0.50f)
        lineTo(w * 0.65f, h * 0.75f)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

/**
 * Custom Canvas helper to draw URL Link Icon
 */
fun DrawScope.drawLinkIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        moveTo(w * 0.35f, h * 0.65f)
        lineTo(w * 0.65f, h * 0.35f)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    drawCircle(
        color = color,
        radius = w * 0.18f,
        center = Offset(w * 0.30f, h * 0.70f),
        style = Stroke(width = strokeWidth)
    )
    drawCircle(
        color = color,
        radius = w * 0.18f,
        center = Offset(w * 0.70f, h * 0.30f),
        style = Stroke(width = strokeWidth)
    )
}

/**
 * Custom Canvas helper to draw History Counter Icon
 */
fun DrawScope.drawHistoryIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    val w = size.width
    val h = size.height

    drawCircle(
        color = color,
        radius = w * 0.35f,
        center = Offset(w * 0.50f, h * 0.50f),
        style = Stroke(width = strokeWidth)
    )

    val clockPath = Path().apply {
        moveTo(w * 0.50f, h * 0.25f)
        lineTo(w * 0.50f, h * 0.50f)
        lineTo(w * 0.68f, h * 0.50f)
    }
    drawPath(
        path = clockPath,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

/**
 * Custom Canvas helper to draw Shield Chart Analytics Icon
 */
fun DrawScope.drawAnalyticsShieldIcon(
    color: Color,
    strokeWidth: Float = 4f
) {
    drawShieldPath(color = color, isStroke = true, strokeWidth = strokeWidth)

    val w = size.width
    val h = size.height

    val chartPath = Path().apply {
        moveTo(w * 0.30f, h * 0.65f)
        lineTo(w * 0.45f, h * 0.52f)
        lineTo(w * 0.58f, h * 0.58f)
        lineTo(w * 0.72f, h * 0.38f)
    }
    drawPath(
        path = chartPath,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}
