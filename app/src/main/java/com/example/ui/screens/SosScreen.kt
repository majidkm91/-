package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.toPersianDigits
import com.example.ui.components.toPersianFormatted
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBgBottom
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

enum class BreathPhase(val label: String, val durationSecs: Int) {
    INHALE("دَم (عمیق)", 4),
    HOLD("حبس نفس", 7),
    EXHALE("بازدم (آرام)", 8)
}

@Composable
fun SosScreen(
    cravingResistedCount: Int,
    onCravingResisted: () -> Unit,
    onResetQuitTime: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showResetDialog by remember { mutableStateOf(false) }
    var showVictorySnackbar by remember { mutableStateOf(false) }

    // 4-7-8 Breathing Loop State
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var secondsRemainingInPhase by remember { mutableIntStateOf(BreathPhase.INHALE.durationSecs) }
    var completedCycles by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            for (phase in BreathPhase.values()) {
                currentPhase = phase
                for (s in phase.durationSecs downTo 1) {
                    secondsRemainingInPhase = s
                    delay(1000L)
                }
            }
            completedCycles++
        }
    }

    // Breathing Sphere visual scale interpolation
    val targetScale = when (currentPhase) {
        BreathPhase.INHALE -> 1.0f + (0.55f * (BreathPhase.INHALE.durationSecs - secondsRemainingInPhase + 1) / BreathPhase.INHALE.durationSecs)
        BreathPhase.HOLD -> 1.55f
        BreathPhase.EXHALE -> 1.55f - (0.55f * (BreathPhase.EXHALE.durationSecs - secondsRemainingInPhase + 1) / BreathPhase.EXHALE.durationSecs)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .testTag("sos_screen")
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Breathing Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 28.dp,
            borderColor = RoseAlert.copy(alpha = 0.3f),
            backgroundColor = GlassSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "آرام باش، این حس کاملاً موقتیه",
                    color = Color(0xFFFDA4AF),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "تمرین تنفس ضد اضطراب (۴-۷-۸): همگام با دایره نفس بکشید.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Breathing visual container
                Box(
                    modifier = Modifier
                        .size(210.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer static guide ring
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.2f), CircleShape)
                    )

                    // Outer pulse aura
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .scale(targetScale * 1.15f)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        EmeraldPrimary.copy(alpha = pulseAlpha * 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // Dynamic Breathing Circle
                    Box(
                        modifier = Modifier
                            .size(115.dp)
                            .scale(targetScale)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF6EE7B7),
                                        EmeraldPrimary,
                                        Color(0xFF047857)
                                    )
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentPhase.label,
                                color = Color(0xFF022C22),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = secondsRemainingInPhase.toPersianFormatted(),
                                color = Color(0xFF022C22),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${completedCycles.toPersianFormatted()} چرخه تنفس تکمیل شده",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Action Button: "وسوسه را شکست دادم! 💪"
        Button(
            onClick = {
                onCravingResisted()
                showVictorySnackbar = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldPrimary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("craving_resisted_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "💪", fontSize = 20.sp)
                Text(
                    text = "وسوسه را شکست دادم! ثبت پیروزی",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showVictorySnackbar) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = EmeraldPrimary.copy(alpha = 0.2f),
                borderColor = EmeraldPrimary,
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldLight
                    )
                    Text(
                        text = "آفرین بر اراده‌ات! یک پیروزی دیگر ثبت شد و به ریه‌هایت زندگی بخشیدی.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 3 Golden Rules Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp,
            backgroundColor = GlassSurfaceElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "💡 ۳ دستور طلایی برای عبور از موج وسوسه:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                RuleItem(
                    number = "۱",
                    title = "نوشیدن آب خنک",
                    description = "یک لیوان بزرگ آب خنک را جرعه‌جرعه و بسیار آرام بنوشید."
                )

                RuleItem(
                    number = "۲",
                    title = "تغییر محیط و شستشوی صورت",
                    description = "فوراً از جایی که هستید بلند شوید و دست و صورتتان را با آب سرد بشویید."
                )

                RuleItem(
                    number = "۳",
                    title = "یادآوری ذهنی",
                    description = "به خود بگویید: «من نیازی به سیگار ندارم؛ این فقط فریاد یک عادت در حال مرگه!»"
                )
            }
        }

        // Relapse reset prompt button
        TextButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.testTag("relapse_reset_button")
        ) {
            Text(
                text = "متاسفانه لغزش داشتم و کشیدم (تنظیم مجدد)",
                color = Color(0xFFF87171),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "تنظیم مجدد زمان ترک",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "آیا قصد دارید زمان ترک را به همین الان ریست کنید؟ اشکالی ندارد، لغزش پایان مسیر نیست، فقط یک تجربه است. قوی‌تر از قبل شروع کنید!",
                    color = TextSecondary,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetQuitTime()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseAlert)
                ) {
                    Text("ریست زمان ترک")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = DarkBgBottom,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun RuleItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(EmeraldPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toPersianDigits(),
                color = EmeraldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}
