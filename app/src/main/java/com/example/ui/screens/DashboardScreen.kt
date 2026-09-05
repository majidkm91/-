package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserConfigEntity
import com.example.ui.DashboardStats
import com.example.ui.components.GlassCard
import com.example.ui.components.toPersianDigits
import com.example.ui.components.toPersianFormatted
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.OrangeAlert
import com.example.ui.theme.PurpleMilestone
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    userConfig: UserConfigEntity,
    currentQuote: String,
    onRotateQuote: () -> Unit,
    onNavigateToSos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Hero Counter Card (Immersive UI rounded-[2.5rem], emerald-glow)
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_counter_card"),
            borderColor = GlassBorderHighlight,
            borderWidth = 1.2.dp,
            cornerRadius = 38.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Top-Right Live Status Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.12f))
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.25f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "وضعیت زنده • LIVE",
                        color = EmeraldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Ambient emerald glow inside hero card
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopStart)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(EmeraldPrimary.copy(alpha = 0.15f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "مدت زمان رهایی شما",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Big Hero Counter: "۱۲ روز"
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stats.days.toInt().toPersianFormatted(),
                            color = Color.White,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1.5).sp,
                            lineHeight = 66.sp
                        )
                        Text(
                            text = "روز",
                            color = EmeraldLight,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Sub-units row: Hours, Minutes, Seconds
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubUnitItem(
                            value = stats.hours.toInt().toPersianFormatted(),
                            label = "ساعت",
                            valueColor = Color.White
                        )
                        SubUnitItem(
                            value = stats.minutes.toInt().toPersianFormatted(),
                            label = "دقیقه",
                            valueColor = Color.White
                        )
                        SubUnitItem(
                            value = stats.seconds.toInt().toPersianFormatted(),
                            label = "ثانیه",
                            valueColor = EmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress bar: bg-white/5, fill bg-gradient-to-l from-emerald-500 to-teal-400
                    val progressFraction = (stats.nextGoalProgressPercent / 100f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(TealAccent, EmeraldPrimary)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "هدف: ${stats.nextGoalTitle}",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${stats.nextGoalProgressPercent.toInt().toPersianFormatted()}٪ پیشرفت",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Motivational Quote pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x1A000000))
                            .clickable { onRotateQuote() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(
                            targetState = currentQuote,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            modifier = Modifier.weight(1f),
                            label = "quote_anim"
                        ) { quote ->
                            Text(
                                text = "«$quote»",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        IconButton(
                            onClick = onRotateQuote,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "تغییر جمله انگیزشی",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bento Stats Grid (2x2 grid, rounded-3xl)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStatCard(
                iconEmoji = "💰",
                iconBgColor = AmberWarning.copy(alpha = 0.15f),
                title = "پس‌انداز شده",
                value = stats.moneySaved.toPersianFormatted(),
                unit = "تومان",
                modifier = Modifier.weight(1f)
            )

            BentoStatCard(
                iconEmoji = "🚭",
                iconBgColor = CyanAccent.copy(alpha = 0.15f),
                title = "سیگار دود نشده",
                value = stats.cigsAvoided.toPersianFormatted(),
                unit = "نخ",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BentoStatCard(
                iconEmoji = "⏳",
                iconBgColor = EmeraldPrimary.copy(alpha = 0.15f),
                title = "عمر بازیافته",
                value = stats.lifeSavedHours.toPersianFormatted(1),
                unit = "ساعت",
                modifier = Modifier.weight(1f)
            )

            BentoStatCard(
                iconEmoji = "🛡️",
                iconBgColor = PurpleMilestone.copy(alpha = 0.15f),
                title = "وسوسه مهار شده",
                value = userConfig.cravingResistedCount.toPersianFormatted(),
                unit = "بار",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Emergency Action (SOS Button, gradient rose-to-orange, rounded-[2rem])
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(RoseAlert, OrangeAlert)
                        )
                    )
                    .clickable { onNavigateToSos() }
                    .testTag("dashboard_sos_button")
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "🧘‍♂️", fontSize = 20.sp)
                    Text(
                        text = "مهار فوری وسوسه (SOS)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "«هر لحظه مقاومت، یک پیروزی بزرگ است»",
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}

@Composable
private fun SubUnitItem(
    value: String,
    label: String,
    valueColor: Color
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 1.dp)
        )
    }
}

@Composable
private fun BentoStatCard(
    iconEmoji: String,
    iconBgColor: Color,
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        cornerRadius = 24.dp,
        backgroundColor = GlassSurfaceElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(text = iconEmoji, fontSize = 18.sp)
            }

            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal
            )

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = unit,
                    color = TextSecondary.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}
