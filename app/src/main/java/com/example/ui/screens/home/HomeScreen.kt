package com.example.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToGames: () -> Unit,
    onNavigateToRooms: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onStartAutobus: () -> Unit,
    onStartQuiz: (String) -> Unit,
    onEnterRoom: (Long) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val activeAds by viewModel.activeAds.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val activeRooms by viewModel.activeRooms.collectAsState()
    val dailyRewards by viewModel.dailyRewards.collectAsState()
    val dailyMissions by viewModel.dailyMissions.collectAsState()
    val missionsProgress by viewModel.userMissionsProgress.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()

    val onlineFriends = allUsers.filter { it.isOnline && it.id != currentUser?.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Connectivity Status Bar
        ConnectivityStatusBar(
            isOffline = isOffline,
            onToggle = { viewModel.toggleOfflineMode() }
        )

        // 2. Main Header
        Surface(
            color = SadeeqiDarkCard,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, SadeeqiDarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Logo & User avatar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarWithFrame(
                        displayName = currentUser?.displayName ?: "ص",
                        frame = currentUser?.frame ?: "NONE",
                        size = 46
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "«صديقي»",
                                color = SadeeqiYellow,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = SadeeqiDarkSurface,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Lv.${currentUser?.level ?: 1}",
                                    color = SadeeqiWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = currentUser?.displayName ?: "أهلاً بك",
                            color = SadeeqiLightGray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Online & Offline Coins Chips + Notification Bell
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OnlineCoinChip(
                        amount = currentUser?.onlineCoins ?: 0L,
                        onClick = onNavigateToStore
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box {
                        IconButton(onClick = onNavigateToNotifications) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "الإشعارات",
                                tint = SadeeqiWhite
                            )
                        }
                        if (unreadNotifications > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(SadeeqiRed)
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadNotifications",
                                    color = SadeeqiWhite,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Internal Ad Banner (Managed by Admin)
        AdBannerView(
            ads = activeAds,
            onAdClick = { ad ->
                if (ad.linkUrl == "store") onNavigateToStore()
                else onNavigateToGames()
            }
        )

        // Offline coins reminder chip if user has offline coins
        if ((currentUser?.offlineCoins ?: 0L) > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfflineCoinChip(amount = currentUser?.offlineCoins ?: 0L)
                Text(
                    text = "العملات منفصلة: أونلاين وأوفلاين 🔒",
                    color = SadeeqiGray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Friends Online (Horizontal Scroll)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🟢 الأصدقاء المتصلون",
                    color = SadeeqiWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "عرض الكل",
                    color = SadeeqiYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToFriends() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (onlineFriends.isEmpty()) {
                Surface(
                    color = SadeeqiDarkCard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "لا يوجد أصدقاء متصلون حالياً، ادعُ أصدقاءك!",
                        color = SadeeqiGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(onlineFriends) { friend ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(64.dp)
                                .clickable { onNavigateToFriends() }
                        ) {
                            Box {
                                AvatarWithFrame(
                                    displayName = friend.displayName,
                                    frame = friend.frame,
                                    size = 48
                                )
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(SadeeqiOnlineGreen)
                                        .border(1.5.dp, SadeeqiBlack, CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = friend.displayName,
                                color = SadeeqiLightGray,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 5. Active Rooms (القعدات النشطة)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎤 القعدات النشطة",
                    color = SadeeqiWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "جميع القعدات",
                    color = SadeeqiYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRooms() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (activeRooms.isEmpty()) {
                Surface(
                    color = SadeeqiDarkCard,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "لا توجد قعدات مفتوحة، أنشئ قعدتك الأولى الآن!",
                        color = SadeeqiGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                activeRooms.take(2).forEach { room ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                viewModel.joinRoom(room.id)
                                onEnterRoom(room.id)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎤", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(room.title, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("المضيف: ${room.ownerName} • ${room.currentPlayersCount} لاعبين", color = SadeeqiGray, fontSize = 11.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    viewModel.joinRoom(room.id)
                                    onEnterRoom(room.id)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("انضمام", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 6. Quick Games (أتوبيس كومبليت، أسئلة، صح أم خطأ)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎮 الألعاب السريعة",
                    color = SadeeqiWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "المزيد",
                    color = SadeeqiYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToGames() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickGameTile(
                    title = "🚌 أتوبيس",
                    desc = "كومبليت كاملة",
                    modifier = Modifier.weight(1f),
                    onClick = onStartAutobus
                )
                QuickGameTile(
                    title = "🧠 أسئلة عامة",
                    desc = "مسابقة خيارات",
                    modifier = Modifier.weight(1f),
                    onClick = { onStartQuiz("GENERAL") }
                )
                QuickGameTile(
                    title = "✅ صح أم خطأ",
                    desc = "تحدي سرعة",
                    modifier = Modifier.weight(1f),
                    onClick = { onStartQuiz("TRUE_FALSE") }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 7. Daily Reward Card (Day 1..7 streak)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "🎁 المكافأة اليومية (Daily Reward)",
                color = SadeeqiWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiYellow.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجل دخولك يومياً واكسب حتى 50 كوينز!",
                            color = SadeeqiLightGray,
                            fontSize = 12.sp
                        )
                        Button(
                            onClick = { viewModel.claimDailyReward() },
                            colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("استلام اليوم 🎁", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 7 days streak row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = if (dailyRewards.isNotEmpty()) dailyRewards else (1..7).map {
                            DailyRewardEntity(it, it * 5L, it * 10)
                        }
                        days.forEach { reward ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SadeeqiDarkSurface)
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                                    .width(42.dp)
                            ) {
                                Text("يوم ${reward.dayNumber}", color = SadeeqiGray, fontSize = 9.sp)
                                Text("🟡", fontSize = 12.sp)
                                Text("+${reward.coins}", color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 8. Daily Missions (المهام اليومية)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "🎯 المهام اليومية (Daily Missions)",
                color = SadeeqiWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            dailyMissions.forEach { mission ->
                val prog = missionsProgress.find { it.missionId == mission.id }
                val current = prog?.currentCount ?: 0
                val isDone = current >= mission.targetCount
                val isClaimed = prog?.isClaimed ?: false

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mission.title, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(mission.description, color = SadeeqiGray, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            val frac = (current.toFloat() / mission.targetCount.toFloat()).coerceIn(0f, 1f)
                            LinearProgressIndicator(
                                progress = { frac },
                                color = SadeeqiYellow,
                                trackColor = SadeeqiDarkSurface,
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                        }

                        if (isClaimed) {
                            Text("تم الاستلام ✓", color = SadeeqiOnlineGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else if (isDone) {
                            Button(
                                onClick = { viewModel.claimMission(mission.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("استلام +${mission.rewardCoins} 🟡", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text("$current/${mission.targetCount}", color = SadeeqiLightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 9. Announcements
        if (announcements.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "📢 إعلانات الإدارة والمسابقات",
                    color = SadeeqiWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                announcements.forEach { ann ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(ann.title, color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(ann.content, color = SadeeqiLightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QuickGameTile(
    title: String,
    desc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, color = SadeeqiGray, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}
