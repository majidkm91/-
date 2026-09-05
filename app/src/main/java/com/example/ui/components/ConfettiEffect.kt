package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Particle(
    val initialX: Float,
    val initialY: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    trigger: Long,
    modifier: Modifier = Modifier
) {
    if (trigger <= 0L) return

    val progress = remember(trigger) { Animatable(0f) }

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFF10B981), // Emerald
            Color(0xFF34D399), // Light Emerald
            Color(0xFFFBBF24), // Gold
            Color(0xFFF43F5E), // Rose
            Color(0xFF38BDF8), // Sky Blue
            Color(0xFFA855F7), // Purple
            Color(0xFFFFFFFF)  // White
        )
        List(70) {
            val angle = Random.nextDouble(Math.PI * 0.1, Math.PI * 0.9)
            val speed = Random.nextFloat() * 1200f + 600f
            Particle(
                initialX = 0.5f,
                initialY = 0.4f,
                vx = (Math.cos(angle) * speed).toFloat() * if (Random.nextBoolean()) 1f else -1f,
                vy = -(Math.sin(angle) * speed).toFloat(),
                size = Random.nextFloat() * 14f + 8f,
                color = colors.random(),
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val t = progress.value
            val gravity = 1800f * t * t
            val alpha = (1f - t).coerceIn(0f, 1f)

            particles.forEach { p ->
                val px = size.width * p.initialX + p.vx * t
                val py = size.height * p.initialY + p.vy * t + gravity
                val rot = p.rotationSpeed * t

                rotate(degrees = rot, pivot = Offset(px, py)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.size / 2f,
                            center = Offset(px, py)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(px - p.size / 2f, py - p.size / 4f),
                            size = Size(p.size, p.size * 0.6f)
                        )
                    }
                }
            }
        }
    }
}
