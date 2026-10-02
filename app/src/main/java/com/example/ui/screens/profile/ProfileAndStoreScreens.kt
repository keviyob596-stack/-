package com.example.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.InventoryItemEntity
import com.example.data.model.StoreItemEntity
import com.example.data.model.UserEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarWithFrame
import com.example.ui.components.OfflineCoinChip
import com.example.ui.components.OnlineCoinChip
import com.example.ui.components.SadeeqiButton
import com.example.ui.screens.friends.SendCoinsModal
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    onOpenStore: () -> Unit,
    onOpenAdmin: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val inventory by viewModel.userInventory.collectAsState()
    val transactions by viewModel.userTransactions.collectAsState()

    var showSendCoinsDialog by remember { mutableStateOf(false) }
    var selectedReceiver by remember { mutableStateOf<UserEntity?>(null) }
    var showSwitchUserDialog by remember { mutableStateOf(false) }

    val user = currentUser ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👤 حسابي الشخصي", color = SadeeqiYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Row {
                if (user.role == "ADMIN") {
                    Button(
                        onClick = onOpenAdmin,
                        colors = ButtonDefaults.buttonColors(containerColor = SadeeqiRedDark, contentColor = SadeeqiWhite),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("لوحة الأدمن", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
                IconButton(onClick = { showSwitchUserDialog = true }) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = "تبديل المستخدم", tint = SadeeqiYellow)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Profile Identity Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SadeeqiYellow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarWithFrame(
                    displayName = user.displayName,
                    frame = user.frame,
                    size = 72
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = user.displayName,
                    color = SadeeqiWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${user.username} • User ID: #${user.id}",
                    color = SadeeqiGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Level & XP Progress Bar
                Surface(
                    color = SadeeqiDarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المستوى: Level ${user.level}", color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${user.xp} / ${user.level * 500} XP", color = SadeeqiLightGray, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val progress = (user.xp % 500).toFloat() / 500f
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            color = SadeeqiYellow,
                            trackColor = SadeeqiDarkCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Balances View (Strict separation between Online and Offline coins)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiYellow.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🟡 Online Coins", color = SadeeqiYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${user.onlineCoins}", color = SadeeqiWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("للمتجر والتحويلات", color = SadeeqiGray, fontSize = 10.sp)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiRed.copy(alpha = 0.5f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔴 Offline Coins", color = SadeeqiRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${user.offlineCoins}", color = SadeeqiWhite, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            Text("للعب بدون نت فقط", color = SadeeqiGray, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats: Wins & Games
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆 الانتصارات", color = SadeeqiGray, fontSize = 11.sp)
                        Text("${user.wins}", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎮 المباريات", color = SadeeqiGray, fontSize = 11.sp)
                        Text("${user.gamesPlayed}", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👑 الإطار", color = SadeeqiGray, fontSize = 11.sp)
                        Text(user.frame, color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onOpenStore,
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(46.dp)
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("المتجر 🎁", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val otherUsers = allUsers.filter { it.id != user.id }
                    if (otherUsers.isNotEmpty()) {
                        selectedReceiver = otherUsers.first()
                        showSendCoinsDialog = true
                    } else {
                        viewModel.showMessage("لا يوجد مستخدمون آخرون للتحويل إليهم")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkCard, contentColor = SadeeqiYellow),
                border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiYellow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(46.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إرسال كوينز", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Equipped Items & Customization
        Text("🎨 تخصيص المظهر والتأثيرات", color = SadeeqiWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Frame customization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("إطار الصورة الشخصية", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("الحالي: ${user.frame}", color = SadeeqiYellow, fontSize = 12.sp)
                    }
                    Row {
                        Button(
                            onClick = { viewModel.equipItem("FRAME_ROYAL", "FRAME") },
                            colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiYellow),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("ملوكي 👑", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = { viewModel.equipItem("FRAME_FIRE", "FRAME") },
                            colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiRed),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("ناري 🔥", fontSize = 11.sp)
                        }
                    }
                }

                Divider(color = SadeeqiDarkBorder)

                // Sound customization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("صوت الدخول للقعدات", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("الحالي: ${user.entrySound}", color = SadeeqiYellow, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.equipItem("SOUND_VIP", "SOUND") },
                        colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiYellow),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("تفعيل VIP 🔊", fontSize = 11.sp)
                    }
                }

                Divider(color = SadeeqiDarkBorder)

                // Chat Effect customization
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("تأثير فقاعة الشات", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("الحالي: ${user.chatEffect}", color = SadeeqiYellow, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.equipItem("EFFECT_GOLD", "EFFECT") },
                        colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiYellow),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("بريق ذهبي ✨", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Transactions History
        Text("📜 سجل المعاملات المالية", color = SadeeqiWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            Surface(
                color = SadeeqiDarkCard,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "لا توجد معاملات بعد",
                    color = SadeeqiGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.take(5).forEach { tx ->
                    val isSent = tx.senderId == user.id
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isSent) "إرسال إلى: ${tx.receiverName}" else "استلام من: ${tx.senderName}",
                                    color = SadeeqiWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${tx.reason} • ${tx.transactionId}",
                                    color = SadeeqiGray,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "${if (isSent) "-" else "+"}${tx.amount} 🟡",
                                color = if (isSent) SadeeqiRed else SadeeqiYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout Button
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiRed.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تسجيل الخروج", fontWeight = FontWeight.Bold)
        }
    }

    if (showSendCoinsDialog && selectedReceiver != null) {
        SendCoinsModal(
            receiver = selectedReceiver!!,
            currentUserCoins = user.onlineCoins,
            onDismiss = { showSendCoinsDialog = false },
            onSend = { amount, reason ->
                viewModel.sendCoins(selectedReceiver!!.id, amount, reason)
                showSendCoinsDialog = false
            }
        )
    }

    if (showSwitchUserDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchUserDialog = false },
            containerColor = SadeeqiDarkCard,
            title = { Text("تبديل الحساب (تجربة وتطوير)", color = SadeeqiYellow, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    allUsers.forEach { u ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (u.id == user.id) SadeeqiYellow.copy(alpha = 0.2f) else SadeeqiDarkSurface)
                                .clickable {
                                    viewModel.switchUser(u.id)
                                    showSwitchUserDialog = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${u.displayName} (${u.role})", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${u.onlineCoins} 🟡", color = SadeeqiYellow, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSwitchUserDialog = false }) { Text("إغلاق", color = SadeeqiGray) }
            }
        )
    }
}

// -------------------------------------------------------------
// STORE SCREEN (BUY WITH ONLINE COINS, INVENTORY PERSISTENCE)
// -------------------------------------------------------------
@Composable
fun StoreScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val storeItems by viewModel.storeItems.collectAsState()
    val inventory by viewModel.userInventory.collectAsState()

    var selectedItemToBuy by remember { mutableStateOf<StoreItemEntity?>(null) }

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
                Column {
                    Text("🎁 متجر صديقي الرسمي", color = SadeeqiYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("الشراء حصرياً بـ Online Coins 🟡", color = SadeeqiGray, fontSize = 11.sp)
                }
            }
            OnlineCoinChip(amount = currentUser?.onlineCoins ?: 0L)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Store Items Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(storeItems) { item ->
                val userCount = inventory.find { it.itemKey == item.itemKey }?.count ?: 0
                StoreItemCard(
                    item = item,
                    ownedCount = userCount,
                    onBuy = { selectedItemToBuy = item }
                )
            }
        }
    }

    if (selectedItemToBuy != null) {
        val item = selectedItemToBuy!!
        val userCoins = currentUser?.onlineCoins ?: 0L
        val canAfford = userCoins >= item.price

        AlertDialog(
            onDismissRequest = { selectedItemToBuy = null },
            containerColor = SadeeqiDarkCard,
            title = {
                Text("شراء ${item.name} 🛍️", color = SadeeqiYellow, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(item.description, color = SadeeqiLightGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = SadeeqiDarkSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("السعر:", color = SadeeqiLightGray)
                            Text("${item.price} كوينز أونلاين 🟡", color = SadeeqiYellow, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (!canAfford) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("⚠️ رصيدك من Online Coins غير كافٍ! متاح: $userCoins", color = SadeeqiRed, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.buyStoreItem(item.itemKey)
                        selectedItemToBuy = null
                    },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                ) {
                    Text("تأكيد الشراء", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemToBuy = null }) {
                    Text("إلغاء", color = SadeeqiGray)
                }
            }
        )
    }
}

@Composable
fun StoreItemCard(
    item: StoreItemEntity,
    ownedCount: Int,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SadeeqiDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                color = SadeeqiWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.description,
                color = SadeeqiGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.price} 🟡",
                    color = SadeeqiYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (ownedCount > 0) {
                    Text(
                        text = "لديك: $ownedCount",
                        color = SadeeqiLightGray,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.fillMaxWidth().height(32.dp)
            ) {
                Text("شراء", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
