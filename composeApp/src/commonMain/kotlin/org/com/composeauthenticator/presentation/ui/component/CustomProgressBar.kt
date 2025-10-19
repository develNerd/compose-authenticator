package org.com.composeauthenticator.presentation.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomProgressBar(
    progressMultiplyingFactor: Float,
    secondsRemaining: String,
    modifier: Modifier = Modifier
) {
    // Constants for better readability and maintainability
    val strokeWidth = 6.dp
    val arcGap = 1.8f
    val fullCircle = 360f
    val arcSegmentDegrees = 120f
    
    // Arc phase boundaries
    val redPhaseEnd = arcSegmentDegrees // 0-120°
    val orangePhaseEnd = arcSegmentDegrees * 2 // 120-240°
    val greenPhaseEnd = fullCircle // 240-360°
    
    // Starting angles for each arc (offset by 90° to start at top, plus gap)
    val topOffset = -90f
    val redStartAngle = topOffset + arcGap / 2
    val orangeStartAngle = topOffset + arcSegmentDegrees + (arcGap / 2)
    val greenStartAngle = topOffset + (arcSegmentDegrees * 2) + (arcGap / 2)
    
    // Maximum sweep angle for each arc (accounting for gaps)
    val maxSweepAngle = arcSegmentDegrees - arcGap
    
    val stroke = with(LocalDensity.current) { Stroke(strokeWidth.toPx()) }
    val stroke2 = with(LocalDensity.current) { Stroke(1.dp.toPx()) }

    val progressBarProgress = remember(progressMultiplyingFactor) {
        derivedStateOf {
            fullCircle * progressMultiplyingFactor
        }
    }

    val progress by animateFloatAsState(
        targetValue = progressBarProgress.value,
        animationSpec = tween(100, easing = LinearEasing), // Smooth linear animation
        label = "progress"
    )

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            val innerRadius = (size.minDimension - stroke2.width) / 2
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )

            val arcSize = Size(innerRadius * 2, innerRadius * 2)

            if (progressBarProgress.value <= fullCircle && progressBarProgress.value > 0) {
                // Red arc (0-120 degrees) - Safe time at start
                drawArc(
                    color = Color.Red,
                    startAngle = redStartAngle,
                    sweepAngle = if (progressBarProgress.value < redPhaseEnd) 
                        (progress - arcGap).coerceAtLeast(0f) 
                    else maxSweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )

                // Orange arc (120-240 degrees) - Warning time
                drawArc(
                    color = Color(0xFFFFB74D), // Nice orange for warning
                    startAngle = orangeStartAngle,
                    sweepAngle = if (progressBarProgress.value <= redPhaseEnd) 0f 
                              else if (progressBarProgress.value > redPhaseEnd && progressBarProgress.value < orangePhaseEnd) 
                                  (progress - redPhaseEnd - arcGap).coerceAtLeast(0f)
                              else maxSweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )

                // Green arc (240-360 degrees) - Critical final countdown
                drawArc(
                    color = Color(0xFFE57373), // Red for critical time
                    startAngle = greenStartAngle,
                    sweepAngle = if (progressBarProgress.value <= orangePhaseEnd) 0f 
                                else (progress - orangePhaseEnd).coerceAtMost(maxSweepAngle),
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )
            }
        }
        
        Text(
            text = secondsRemaining,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 16.sp, // Increased from 14sp
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary // Use theme primary color instead of onSurface
        )
    }
}