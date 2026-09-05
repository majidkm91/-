package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppConstants
import com.example.data.HealthStage
import com.example.ui.components.GlassCard
import com.example.ui.components.toPersianDigits
import com.example.ui.components.toPersianFormatted
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HealthScreen(
    quitTimestamp: Long,
    currentTime: Long,
    modifier: Modifier = Modifier
) {
    val passedMs = (currentTime - quitTimestamp).coerceAtLeast(0L)
    val passedHours = passedMs / 3600000.0

    // Compute completed count
    val completedCount = AppConstants.healthStages.count { passedHours >= it.hours }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("health_timeline_list")
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "روند بازسازی بدن",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "بهبودهای فیزیولوژیک بر اساس سازمان بهداشت جهانی (WHO)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${completedCount.toPersianFormatted()} از ${AppConstants.healthStages.size.toPersianFormatted()} مرحله",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(AppConstants.healthStages, key = { it.id }) { stage ->
            HealthStageCard(stage = stage, passedHours = passedHours)
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HealthStageCard(
    stage: HealthStage,
    passedHours: Double
) {
    val progressFloat = (passedHours / stage.hours).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progressFloat, label = "health_prog")
    val percentInt = (progressFloat * 100).toInt()
    val isDone = progressFloat >= 1f

    val remainingHours = (stage.hours - passedHours).coerceAtLeast(0.0)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp,
        backgroundColor = if (isDone) GlassSurfaceElevated else GlassSurfaceElevated.copy(alpha = 0.6f),
        borderColor = if (isDone) GlassBorderHighlight else Color(0x1AFFFFFF),
        borderWidth = if (isDone) 1.5.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stage.iconEmoji, fontSize = 20.sp)
                    Text(
                        text = stage.title,
                        color = if (isDone) EmeraldLight else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isDone) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "تکمیل شد ✔",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text(
                        text = "${percentInt.toPersianFormatted()}٪",
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stage.desc,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isDone) EmeraldPrimary else CyanAccent,
                trackColor = Color(0x33334155),
                strokeCap = StrokeCap.Round
            )

            if (!isDone && remainingHours > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                val remainingText = if (remainingHours < 1.0) {
                    val remainingMins = (remainingHours * 60).toInt().coerceAtLeast(1)
                    "حدود ${remainingMins.toPersianFormatted()} دقیقه تا دستیابی"
                } else if (remainingHours < 48) {
                    val remainingH = remainingHours.toInt()
                    "حدود ${remainingH.toPersianFormatted()} ساعت تا دستیابی"
                } else {
                    val remainingDays = (remainingHours / 24).toInt()
                    "حدود ${remainingDays.toPersianFormatted()} روز تا دستیابی"
                }
                Text(
                    text = remainingText,
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
