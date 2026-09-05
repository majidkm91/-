package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserConfigEntity
import com.example.ui.components.toPersianDigits
import com.example.ui.components.toPersianFormatted
import com.example.ui.theme.DarkBgBottom
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsDialog(
    initialConfig: UserConfigEntity,
    onDismiss: () -> Unit,
    onSave: (quitTimestamp: Long, cigsPerDay: Int, costPerPack: Long, packSize: Int) -> Unit
) {
    var quitTimestamp by remember { mutableLongStateOf(initialConfig.quitTimestamp) }
    var cigsPerDay by remember { mutableIntStateOf(initialConfig.cigsPerDay) }
    var costPerPack by remember { mutableLongStateOf(initialConfig.costPerPack) }
    var packSize by remember { mutableIntStateOf(initialConfig.packSize) }

    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "⚙️ تنظیمات ترک سیگار",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "اطلاعات را وارد کنید تا آمار دقیق دریافت کنید.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .testTag("settings_dialog_content"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Quit Date selector section
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "تاریخ و زمان ترک سیگار:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurfaceElevated)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dateFormat.format(Date(quitTimestamp)).toPersianDigits(),
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Quick date presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DatePresetChip(
                            label = "همین الان",
                            isSelected = Math.abs(quitTimestamp - System.currentTimeMillis()) < 60000,
                            onClick = { quitTimestamp = System.currentTimeMillis() },
                            modifier = Modifier.weight(1f)
                        )
                        DatePresetChip(
                            label = "۱ روز قبل",
                            isSelected = false,
                            onClick = { quitTimestamp = System.currentTimeMillis() - 86400000L },
                            modifier = Modifier.weight(1f)
                        )
                        DatePresetChip(
                            label = "۳ روز قبل",
                            isSelected = false,
                            onClick = { quitTimestamp = System.currentTimeMillis() - 3 * 86400000L },
                            modifier = Modifier.weight(1f)
                        )
                        DatePresetChip(
                            label = "۱ هفته قبل",
                            isSelected = false,
                            onClick = { quitTimestamp = System.currentTimeMillis() - 7 * 86400000L },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Cigarettes Per Day
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "تعداد نخ در روز:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (cigsPerDay > 1) cigsPerDay-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "کاهش", tint = TextPrimary)
                        }

                        Text(
                            text = "${cigsPerDay.toPersianFormatted()} نخ",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        IconButton(
                            onClick = { cigsPerDay++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "افزایش", tint = TextPrimary)
                        }
                    }
                }

                // Cost Per Pack (Tomans)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "قیمت هر پاکت (تومان):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (costPerPack >= 10000L) costPerPack -= 5000L },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "کاهش قیمت", tint = TextPrimary)
                        }

                        Text(
                            text = "${costPerPack.toPersianFormatted()} تومان",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        IconButton(
                            onClick = { costPerPack += 5000L },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "افزایش قیمت", tint = TextPrimary)
                        }
                    }

                    // Cost quick presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PricePresetChip(
                            price = 40000L,
                            isSelected = costPerPack == 40000L,
                            onClick = { costPerPack = 40000L },
                            modifier = Modifier.weight(1f)
                        )
                        PricePresetChip(
                            price = 60000L,
                            isSelected = costPerPack == 60000L,
                            onClick = { costPerPack = 60000L },
                            modifier = Modifier.weight(1f)
                        )
                        PricePresetChip(
                            price = 90000L,
                            isSelected = costPerPack == 90000L,
                            onClick = { costPerPack = 90000L },
                            modifier = Modifier.weight(1f)
                        )
                        PricePresetChip(
                            price = 120000L,
                            isSelected = costPerPack == 120000L,
                            onClick = { costPerPack = 120000L },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Cigarettes in a pack
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "تعداد سیگار در یک پاکت:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (packSize > 1) packSize-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "کاهش ظرفیت پاکت", tint = TextPrimary)
                        }

                        Text(
                            text = "${packSize.toPersianFormatted()} عددی",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        IconButton(
                            onClick = { packSize++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "افزایش ظرفیت پاکت", tint = TextPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(quitTimestamp, cigsPerDay, costPerPack, packSize)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text(text = "ذخیره اطلاعات", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "انصراف", color = TextSecondary)
            }
        },
        containerColor = DarkBgBottom,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun DatePresetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) EmeraldPrimary else Color(0x22FFFFFF))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PricePresetChip(
    price: Long,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) EmeraldPrimary else Color(0x22FFFFFF))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${(price / 1000).toInt().toPersianFormatted()}ک",
            color = if (isSelected) Color.Black else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
