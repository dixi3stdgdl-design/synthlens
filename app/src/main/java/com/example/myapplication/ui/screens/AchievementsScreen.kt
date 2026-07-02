package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.local.AchievementEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    achievements: List<AchievementEntity>,
    onBack: () -> Unit
) {
    val categories = achievements.groupBy { it.category }
    val unlockedCount = achievements.count { it.isUnlocked }
    val totalCount = achievements.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0F))
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "ACHIEVEMENTS",
                    color = Color(0xFF00FFD4),
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF00FFD4)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF0A0A0F)
            )
        )

        // Progress Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF12121A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROGRESS",
                        color = Color(0xFF00FFD4),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "$unlockedCount / $totalCount",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF00FFD4),
                    trackColor = Color(0xFF1A1A25)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${(if (totalCount > 0) unlockedCount * 100 / totalCount else 0)}% Complete",
                    color = Color(0xFF666680),
                    fontSize = 12.sp
                )
            }
        }

        // Achievement Categories
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            categories.forEach { (category, categoryAchievements) ->
                item {
                    Text(
                        text = category.uppercase(),
                        color = Color(0xFF666680),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(categoryAchievements) { achievement ->
                    AchievementCard(achievement = achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: AchievementEntity) {
    val backgroundColor = if (achievement.isUnlocked) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A2A1A),
                Color(0xFF121A12)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF12121A),
                Color(0xFF0A0A0F)
            )
        )
    }

    val borderColor = when {
        achievement.isUnlocked -> Color(0xFF00FFD4).copy(alpha = 0.3f)
        achievement.rarity == "Epic" -> Color(0xFFBB86FC).copy(alpha = 0.3f)
        achievement.rarity == "Rare" -> Color(0xFF03DAC6).copy(alpha = 0.2f)
        else -> Color(0xFF2A2A3A)
    }

    val progress = if (achievement.target > 0) {
        achievement.progress.toFloat() / achievement.target
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Text(
                    text = achievement.iconEmoji,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (achievement.isUnlocked) Color(0xFF00FFD4).copy(alpha = 0.1f)
                            else Color(0xFF1A1A25)
                        )
                        .wrapContentSize(Alignment.Center)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = achievement.name,
                            color = if (achievement.isUnlocked) Color(0xFF00FFD4) else Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (achievement.isUnlocked) {
                            Text(
                                text = "UNLOCKED",
                                color = Color(0xFF00FFD4),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = achievement.description,
                        color = Color(0xFF888899),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = when (achievement.rarity) {
                                "Epic" -> Color(0xFFBB86FC)
                                "Rare" -> Color(0xFF03DAC6)
                                else -> Color(0xFF00FFD4)
                            },
                            trackColor = Color(0xFF1A1A25)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${achievement.progress}/${achievement.target}",
                            color = Color(0xFF666680),
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rarity badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (achievement.rarity) {
                            "Epic" -> Color(0xFFBB86FC).copy(alpha = 0.2f)
                            "Rare" -> Color(0xFF03DAC6).copy(alpha = 0.2f)
                            else -> Color(0xFF333344).copy(alpha = 0.5f)
                        }
                    ) {
                        Text(
                            text = achievement.rarity.uppercase(),
                            color = when (achievement.rarity) {
                                "Epic" -> Color(0xFFBB86FC)
                                "Rare" -> Color(0xFF03DAC6)
                                else -> Color(0xFF666680)
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}