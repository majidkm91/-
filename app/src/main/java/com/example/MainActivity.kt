package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.GlassCard
import com.example.ui.screens.BadgesScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HealthScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.SosScreen
import androidx.compose.foundation.border
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.ui.theme.DarkBgCanvas
import com.example.ui.theme.DarkNavBg
import com.example.ui.theme.GlassBorderHighlight
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember {
                    AppRepository(database.userConfigDao(), database.badgeDao())
                }
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(repository)
                )

                NafasePakApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NafasePakApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val userConfig by viewModel.userConfig.collectAsState()
    val currentQuote by viewModel.currentQuote.collectAsState()
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }

    // Enforce RTL for full Persian UX
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBgCanvas)
                .drawBehind {
                    // Radial gradient at top right: rgba(16, 185, 129, 0.12)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(EmeraldPrimary.copy(alpha = 0.13f), Color.Transparent),
                            center = Offset(size.width, 0f),
                            radius = size.width * 0.95f
                        ),
                        center = Offset(size.width, 0f),
                        radius = size.width * 0.95f
                    )
                    // Radial gradient at bottom left: rgba(244, 63, 94, 0.08)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(RoseAlert.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(0f, size.height),
                            radius = size.width * 0.85f
                        ),
                        center = Offset(0f, size.height),
                        radius = size.width * 0.85f
                    )
                }
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    AppTopBar(
                        onOpenSettings = { showSettingsDialog = true }
                    )
                },
                bottomBar = {
                    AppBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.setSelectedTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Crossfade(
                        targetState = selectedTab,
                        label = "tab_crossfade"
                    ) { tab ->
                        when (tab) {
                            0 -> DashboardScreen(
                                stats = stats,
                                userConfig = userConfig,
                                currentQuote = currentQuote,
                                onRotateQuote = { viewModel.rotateQuote() },
                                onNavigateToSos = { viewModel.setSelectedTab(2) }
                            )
                            1 -> HealthScreen(
                                quitTimestamp = userConfig.quitTimestamp,
                                currentTime = currentTime
                            )
                            2 -> SosScreen(
                                cravingResistedCount = userConfig.cravingResistedCount,
                                onCravingResisted = { viewModel.onCravingResisted() },
                                onResetQuitTime = { viewModel.resetQuitTime() }
                            )
                            3 -> BadgesScreen(
                                unlockedBadges = unlockedBadges,
                                onTriggerCelebration = { viewModel.triggerCelebration() }
                            )
                        }
                    }
                }
            }

            // Confetti overlay on celebration
            ConfettiEffect(trigger = confettiTrigger)

            // Settings Modal
            if (showSettingsDialog) {
                SettingsDialog(
                    initialConfig = userConfig,
                    onDismiss = { showSettingsDialog = false },
                    onSave = { quitTime, cigsDay, costPack, pSize ->
                        viewModel.saveConfig(quitTime, cigsDay, costPack, pSize)
                        showSettingsDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AppTopBar(
    onOpenSettings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "top_dot_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // w-10 h-10 rounded-2xl bg-emerald-500/20 flex items-center justify-center border border-emerald-500/30
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldPrimary.copy(alpha = 0.18f))
                    .border(1.dp, EmeraldPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight.copy(alpha = dotAlpha))
                )
            }

            Column {
                Text(
                    text = "نفس پاک",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Text(
                    text = "CLEAN BREATH PORTAL",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.2.sp
                )
            }
        }

        // w-10 h-10 rounded-full flex items-center justify-center bg-white/5 border border-white/10
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x12FFFFFF))
                .border(1.dp, Color(0x1FFFFFFF), CircleShape)
                .clickable { onOpenSettings() }
                .testTag("settings_top_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "تنظیمات",
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AppBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(DarkNavBg.copy(alpha = 0.95f))
                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(26.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    index = 0,
                    icon = Icons.Default.BarChart,
                    label = "داشبورد",
                    isSelected = selectedTab == 0,
                    testTag = "nav_dashboard",
                    onSelect = { onTabSelected(0) }
                )
                NavItem(
                    index = 1,
                    icon = Icons.Default.Favorite,
                    label = "سلامت",
                    isSelected = selectedTab == 1,
                    testTag = "nav_health",
                    onSelect = { onTabSelected(1) }
                )
                NavItem(
                    index = 2,
                    icon = Icons.Default.SelfImprovement,
                    label = "آرامش",
                    isSelected = selectedTab == 2,
                    testTag = "nav_sos",
                    onSelect = { onTabSelected(2) }
                )
                NavItem(
                    index = 3,
                    icon = Icons.Default.EmojiEvents,
                    label = "مدال‌ها",
                    isSelected = selectedTab == 3,
                    testTag = "nav_badges",
                    onSelect = { onTabSelected(3) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    index: Int,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onSelect: () -> Unit
) {
    val activeColor = EmeraldLight
    val inactiveColor = TextSecondary.copy(alpha = 0.55f)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) activeColor else inactiveColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
