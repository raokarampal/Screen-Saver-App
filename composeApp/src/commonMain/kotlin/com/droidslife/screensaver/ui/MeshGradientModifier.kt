package com.droidslife.screensaver.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.pow

// Extension function to calculate distance between two points
private fun Offset.getDistance(): Float {
    return sqrt(x.pow(2) + y.pow(2))
}

/**
 * A modifier that draws a mesh gradient.
 *
 * @param points A 2D list of points and their associated colors.
 * @param resolutionX The number of points to sample on the x-axis.
 * @param resolutionY The number of points to sample on the y-axis.
 * @param debug Whether to draw debug information.
 */
fun Modifier.meshGradient(
    points: List<List<Pair<Offset, Color>>>,
    resolutionX: Int = 8,  // Significantly reduced from 12 to minimize grid visibility
    resolutionY: Int = 8,  // Significantly reduced from 12 to minimize grid visibility
    debug: Boolean = false
): Modifier = drawWithContent {
    // Draw the original content
    drawContent()

    // Draw the mesh gradient
    drawMeshGradient(points, resolutionX, resolutionY, debug)
}

/**
 * Draws a mesh gradient.
 *
 * @param points A 2D list of points and their associated colors.
 * @param resolutionX The number of points to sample on the x-axis.
 * @param resolutionY The number of points to sample on the y-axis.
 * @param debug Whether to draw debug information.
 */
private fun DrawScope.drawMeshGradient(
    points: List<List<Pair<Offset, Color>>>,
    resolutionX: Int,
    resolutionY: Int,
    debug: Boolean
) {
    // Calculate the center of the canvas for the oval gradient
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val center = Offset(centerX, centerY)

    // Calculate the maximum distance from center to corner (for oval gradient)
    val maxDistance = sqrt(centerX.pow(2) + centerY.pow(2))
    if (points.isEmpty() || points.any { it.isEmpty() }) return

    val rows = points.size
    val cols = points.maxOf { it.size }

    // Create a grid of vertices and colors
    val vertices = Array(rows) { r ->
        Array(cols) { c ->
            if (c < points[r].size) {
                val (offset, color) = points[r][c]
                Offset(offset.x * size.width, offset.y * size.height) to color
            } else {
                // If the point is missing, use the last point in the row
                val (offset, color) = points[r].last()
                Offset(offset.x * size.width, offset.y * size.height) to color
            }
        }
    }

    // Draw the mesh
    for (r in 0 until rows - 1) {
        for (c in 0 until cols - 1) {
            val stepX = 1f / resolutionX
            val stepY = 1f / resolutionY

            for (i in 0 until resolutionX) {
                for (j in 0 until resolutionY) {
                    val t1 = i * stepX
                    val t2 = j * stepY
                    val t3 = (i + 1) * stepX
                    val t4 = (j + 1) * stepY

                    // Bilinear interpolation for the four corners of the quad
                    val topLeft = this.interpolate(vertices[r][c], vertices[r][c + 1], vertices[r + 1][c], vertices[r + 1][c + 1], t1, t2)
                    val topRight = this.interpolate(vertices[r][c], vertices[r][c + 1], vertices[r + 1][c], vertices[r + 1][c + 1], t3, t2)
                    val bottomLeft = this.interpolate(vertices[r][c], vertices[r][c + 1], vertices[r + 1][c], vertices[r + 1][c + 1], t1, t4)
                    val bottomRight = this.interpolate(vertices[r][c], vertices[r][c + 1], vertices[r + 1][c], vertices[r + 1][c + 1], t3, t4)

                    // Draw the quad
                    drawVertices(
                        listOf(topLeft, topRight, bottomRight, bottomLeft),
                        debug
                    )
                }
            }
        }
    }
}

/**
 * Bilinear interpolation for a point in a quad.
 *
 * @param topLeft The top-left vertex and color.
 * @param topRight The top-right vertex and color.
 * @param bottomLeft The bottom-left vertex and color.
 * @param bottomRight The bottom-right vertex and color.
 * @param tx The x interpolation factor (0-1).
 * @param ty The y interpolation factor (0-1).
 * @return The interpolated vertex and color.
 */
private fun DrawScope.interpolate(
    topLeft: Pair<Offset, Color>,
    topRight: Pair<Offset, Color>,
    bottomLeft: Pair<Offset, Color>,
    bottomRight: Pair<Offset, Color>,
    tx: Float,
    ty: Float
): Pair<Offset, Color> {
    // Get the center of the canvas (defined in drawMeshGradient)
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val center = Offset(centerX, centerY)

    // Increase the fade radius for a more gradual edge transition
    // Using 0.9f instead of 0.8f makes the gradient extend further
    val maxDistance = sqrt(centerX.pow(2) + centerY.pow(2)) * 0.9f

    // Interpolate positions
    val top = Offset(
        lerp(topLeft.first.x, topRight.first.x, tx),
        lerp(topLeft.first.y, topRight.first.y, tx)
    )
    val bottom = Offset(
        lerp(bottomLeft.first.x, bottomRight.first.x, tx),
        lerp(bottomLeft.first.y, bottomRight.first.y, tx)
    )
    val position = Offset(
        lerp(top.x, bottom.x, ty),
        lerp(top.y, bottom.y, ty)
    )

    // Interpolate colors with a slight bias toward the average to reduce visible boundaries
    val topColor = lerpColor(topLeft.second, topRight.second, tx)
    val bottomColor = lerpColor(bottomLeft.second, bottomRight.second, tx)
    val color = lerpColor(topColor, bottomColor, ty)

    // Calculate distance from center for oval gradient effect
    val distanceFromCenter = (position - center).getDistance()
    val normalizedDistance = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)

    // Apply an extremely smooth oval gradient effect with no visible transitions
    // Use a higher-power curve for an even more natural oval shape with no visible edges
    val ovalFactor = 1f - normalizedDistance.pow(3) // Cubic curve for even smoother transition

    // Apply the oval gradient to the alpha channel with complete transparency at edges
    val minOpacity = 0.0f  // Complete transparency at the edges

    // Use a much more aggressive transition curve for the alpha to completely eliminate grid lines
    // This creates a super-smooth gradient with no visible borders between cells
    val adjustedAlpha = if (normalizedDistance > 0.65f) { // Expanded outer region for smoother transition
        // For the outer 35% of the gradient, use an even steeper fade to complete transparency
        lerp(0f, 0.2f, 1f - ((normalizedDistance - 0.65f) * 2.85f)) * color.alpha
    } else {
        // For the inner 65% of the gradient, use a more gradual fade but with lower maximum opacity
        lerp(minOpacity, 0.7f, ovalFactor) * color.alpha // Reduced maximum opacity from 0.9f to 0.7f
    }

    return position to color.copy(alpha = adjustedAlpha)
}

/**
 * Linear interpolation between two values.
 */
private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

/**
 * Linear interpolation between two colors.
 */
private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        lerp(start.red, end.red, fraction),
        lerp(start.green, end.green, fraction),
        lerp(start.blue, end.blue, fraction),
        lerp(start.alpha, end.alpha, fraction)
    )
}

/**
 * Draws a quad with the given vertices and colors.
 *
 * @param vertices The vertices and colors of the quad.
 * @param debug Whether to draw debug information.
 */
private fun DrawScope.drawVertices(
    vertices: List<Pair<Offset, Color>>,
    debug: Boolean
) {
    if (vertices.size != 4) return

    // Draw the quad as two triangles
    drawTriangle(
        vertices[0].first, vertices[1].first, vertices[2].first,
        vertices[0].second, vertices[1].second, vertices[2].second
    )
    drawTriangle(
        vertices[0].first, vertices[2].first, vertices[3].first,
        vertices[0].second, vertices[2].second, vertices[3].second
    )

    // Draw debug information if requested
    if (debug) {
        vertices.forEach { (pos, _) ->
            drawCircle(
                color = Color.Red,
                radius = 4f,
                center = pos
            )
        }

        for (i in 0 until 4) {
            val j = (i + 1) % 4
            drawLine(
                color = Color.Red,
                start = vertices[i].first,
                end = vertices[j].first,
                strokeWidth = 1f
            )
        }
    }
}

/**
 * Draws a triangle with the given vertices and colors.
 * Uses a gradient approach to create smoother transitions between triangles.
 */
private fun DrawScope.drawTriangle(
    p1: Offset, p2: Offset, p3: Offset,
    c1: Color, c2: Color, c3: Color
) {
    // Create a path for the triangle
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        lineTo(p3.x, p3.y)
        close()
    }

    // Calculate the center of the triangle
    val centerX = (p1.x + p2.x + p3.x) / 3f
    val centerY = (p1.y + p2.y + p3.y) / 3f

    // Calculate a blended color with extremely low transparency to completely eliminate grid visibility
    // Using a very low alpha value ensures no borders are visible between triangles
    val blendedColor = Color(
        (c1.red + c2.red + c3.red) / 3f,
        (c1.green + c2.green + c3.green) / 3f,
        (c1.blue + c2.blue + c3.blue) / 3f,
        (c1.alpha + c2.alpha + c3.alpha) / 3f * 0.5f // Drastically reduced transparency to eliminate borders
    )

    // Create a radial gradient brush with a much larger radius and smoother fade-out
    val brush = androidx.compose.ui.graphics.Brush.radialGradient(
        colors = listOf(
            blendedColor,
            blendedColor.copy(alpha = 0f) // Complete fade-out at edges to eliminate borders
        ),
        center = Offset(centerX, centerY),
        radius = maxOf(
            (p1 - Offset(centerX, centerY)).getDistance(),
            (p2 - Offset(centerX, centerY)).getDistance(),
            (p3 - Offset(centerX, centerY)).getDistance()
        ) * 2.0f // Greatly extend beyond the triangle to ensure no visible borders
    )

    // Draw the triangle with the gradient brush
    drawPath(
        path = path,
        brush = brush
    )
}
