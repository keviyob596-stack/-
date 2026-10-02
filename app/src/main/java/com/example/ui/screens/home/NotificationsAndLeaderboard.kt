package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarWithFrame
import com.example.ui.screens.games.TabButton
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = SadeeqiWhite)
                }
                Text("🔔 مركز الإشعارات", color = SadeeqiYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            if (notifications.isNotEmpty()) {
                TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                    Text("قراءة الكل", color = SadeeqiYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = SadeeqiGray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لا توجد إشعارات جديدة", color = SadeeqiLightGray, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications) { notif ->
                    NotificationCard(notif = notif)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notif: NotificationEntity) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.isRead) SadeeqiDarkCard else SadeeqiDarkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (notif.isRead) SadeeqiDarkBorder else SadeeqiYellow.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (notif.type) {
                "COINS" -> "💰"
                "GIFT" -> "🎁"
                "FRIEND_REQ" -> "👥"
                "DAILY_REWARD" -> "🌟"
                else -> "📢"
            }
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notif.title, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(notif.message, color = SadeeqiLightGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun LeaderboardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: XP, 1: Wins, 2: Games

    val sortedUsers = when (selectedTab) {
        0 -> allUsers.sortedByDescending { it.xp }
        1 -> allUsers.sortedByDescending { it.wins }
        else -> allUsers.sortedByDescending { it.gamesPlayed }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = SadeeqiWhite)
            }
            Text("🏅 لوحة المتصدرين (Online)", color = SadeeqiYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SadeeqiDarkSurface, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            TabButton(text = "الخبرة XP ⚡", isSelected = selectedTab == 0, modifier = Modifier.weight(1f), onClick = { selectedTab = 0 })
            TabButton(text = "الفوز 🏆", isSelected = selectedTab == 1, modifier = Modifier.weight(1f), onClick = { selectedTab = 1 })
            TabButton(text = "المباريات 🎮", isSelected = selectedTab == 2, modifier = Modifier.weight(1f), onClick = { selectedTab = 2 })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sortedUsers.mapIndexed { index, user -> Pair(index + 1, user) }) { (rank, user) ->
                val rankBadge = when (rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "#$rank"
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (rank == 1) SadeeqiDarkSurface else SadeeqiDarkCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (rank == 1) SadeeqiYellow else SadeeqiDarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = rankBadge,
                                color = if (rank <= 3) SadeeqiYellow else SadeeqiGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = if (rank <= 3) 20.sp else 14.sp,
                                modifier = Modifier.width(36.dp)
                            )
                            AvatarWithFrame(displayName = user.displayName, frame = user.frame, size = 40)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(user.displayName, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Level ${user.level}", color = SadeeqiGray, fontSize = 11.sp)
                            }
                        }

                        val scoreText = when (selectedTab) {
                            0 -> "${user.xp} XP"
                            1 -> "${user.wins} فوز"
                            else -> "${user.gamesPlayed} لعبة"
                        }
                        Text(
                            text = scoreText,
                            color = SadeeqiYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
