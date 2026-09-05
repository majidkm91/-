package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppConstants
import com.example.data.BadgeDefinition
import com.example.data.BadgeEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.toPersianFormatted
import com.example.ui.theme.DarkBgBottom
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BadgesScreen(
    unlockedBadges: List<BadgeEntity>,
    onTriggerCelebration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedIds = remember(unlockedBadges) { unlockedBadges.map { it.id }.toSet() }
    var selectedBadge by remember { mutableStateOf<BadgeDefinition?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .testTag("badges_grid")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "تالار افتخارات",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "هر پیروزی شایسته جشن گرفتن است",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onTriggerCelebration,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldYellow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "جشن 🎊",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(AppConstants.allBadges, key = { it.id }) { badge ->
            val isUnlocked = unlockedIds.contains(badge.id)
            BadgeCard(
                badge = badge,
                isUnlocked = isUnlocked,
                onClick = { selectedBadge = badge }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Detail Dialog
    selectedBadge?.let { badge ->
        val isUnlocked = unlockedIds.contains(badge.id)
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = badge.icon, fontSize = 28.sp)
                    Text(
                        text = badge.title,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = badge.desc,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isUnlocked) "وضعیت: با موفقیت باز شده است ✔" else "وضعیت: هنوز قفل است 🔒",
                        color = if (isUnlocked) EmeraldLight else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isUnlocked) onTriggerCelebration()
                        selectedBadge = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUnlocked) GoldYellow else EmeraldPrimary
                    )
                ) {
                    Text(if (isUnlocked) "آتش‌بازی دوباره! 🎆" else "متوجه شدم", color = Color.Black)
                }
            },
            containerColor = DarkBgBottom,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun BadgeCard(
    badge: BadgeDefinition,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .alpha(if (isUnlocked) 1f else 0.45f),
        cornerRadius = 20.dp,
        backgroundColor = if (isUnlocked) GlassSurfaceElevated else Color(0x331E293B),
        borderColor = if (isUnlocked) GoldYellow.copy(alpha = 0.5f) else Color(0x1AFFFFFF),
        borderWidth = if (isUnlocked) 1.5.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = badge.icon,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                color = if (isUnlocked) TextPrimary else TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = badge.desc,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isUnlocked) "باز شده ✔" else "قفل 🔒",
                color = if (isUnlocked) GoldYellow else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
