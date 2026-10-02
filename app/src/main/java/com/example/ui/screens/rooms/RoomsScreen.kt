package com.example.ui.screens.rooms

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.MessageEntity
import com.example.data.model.RoomEntity
import com.example.data.model.RoomMemberEntity
import kotlinx.coroutines.launch
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarWithFrame
import com.example.ui.components.SadeeqiButton
import com.example.ui.theme.*

@Composable
fun RoomsScreen(
    viewModel: MainViewModel,
    onEnterRoom: (roomId: Long) -> Unit
) {
    val rooms by viewModel.activeRooms.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRooms = rooms.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.ownerName.contains(searchQuery, ignoreCase = true)
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🎤 القعدات النشطة",
                    color = SadeeqiYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "انضم للقعدة ودردش والعب مع الأصدقاء",
                    color = SadeeqiGray,
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = {
                    SoundManager.playClick()
                    showCreateDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إنشاء قعدة", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث عن اسم القعدة أو المضيف...", color = SadeeqiGray, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SadeeqiYellow) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SadeeqiYellow,
                unfocusedBorderColor = SadeeqiDarkBorder,
                focusedTextColor = SadeeqiWhite,
                unfocusedTextColor = SadeeqiWhite,
                focusedContainerColor = SadeeqiDarkCard,
                unfocusedContainerColor = SadeeqiDarkCard
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredRooms.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MicNone, contentDescription = null, tint = SadeeqiGray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لا توجد قعدات نشطة حالياً", color = SadeeqiLightGray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("كن أول من ينشئ قعدة وادعُ أصدقاءك!", color = SadeeqiGray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRooms) { room ->
                    RoomCardItem(
                        room = room,
                        onJoin = {
                            viewModel.joinRoom(room.id)
                            onEnterRoom(room.id)
                        }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateRoomDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, isPrivate, code ->
                viewModel.createRoom(title, desc, isPrivate, code)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun RoomCardItem(
    room: RoomEntity,
    onJoin: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoin() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SadeeqiDarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎤",
                    fontSize = 26.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = room.title,
                        color = SadeeqiWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (room.isPrivate) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Lock, contentDescription = "خاصة", tint = SadeeqiYellow, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "المضيف: ${room.ownerName}",
                    color = SadeeqiGray,
                    fontSize = 12.sp
                )
                if (room.activeGame.isNotBlank()) {
                    Text(
                        text = "اللعبة الحالية: ${room.activeGame}",
                        color = SadeeqiYellow,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = SadeeqiDarkSurface,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, tint = SadeeqiYellow, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${room.currentPlayersCount}/${room.maxPlayers}",
                            color = SadeeqiWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("دخول", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, isPrivate: Boolean, code: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SadeeqiDarkCard,
        title = {
            Text("إنشاء قعدة جديدة 🎤", color = SadeeqiYellow, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم القعدة", color = SadeeqiGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SadeeqiYellow,
                        unfocusedBorderColor = SadeeqiDarkBorder,
                        focusedTextColor = SadeeqiWhite,
                        unfocusedTextColor = SadeeqiWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("الوصف أو الموضوع", color = SadeeqiGray) },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SadeeqiYellow,
                        unfocusedBorderColor = SadeeqiDarkBorder,
                        focusedTextColor = SadeeqiWhite,
                        unfocusedTextColor = SadeeqiWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("قعدة خاصة برمز مرور", color = SadeeqiWhite, fontSize = 13.sp)
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SadeeqiYellow,
                            checkedTrackColor = SadeeqiYellowDark
                        )
                    )
                }
                if (isPrivate) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("رمز القعدة (PIN)", color = SadeeqiGray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SadeeqiYellow,
                            unfocusedBorderColor = SadeeqiDarkBorder,
                            focusedTextColor = SadeeqiWhite,
                            unfocusedTextColor = SadeeqiWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, desc, isPrivate, code)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
            ) {
                Text("تأكيد الإنشاء", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = SadeeqiGray)
            }
        }
    )
}

// -------------------------------------------------------------
// ROOM DETAIL SCREEN (INSIDE ROOM CHAT, PLAYERS, GIFTS, SOUNDS)
// -------------------------------------------------------------
@Composable
fun RoomDetailScreen(
    roomId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val room by viewModel.repository.roomDao.getRoomFlow(roomId).collectAsState(initial = null)
    val members by viewModel.repository.roomDao.getRoomMembersFlow(roomId).collectAsState(initial = emptyList())
    val messages by viewModel.repository.messageDao.getRoomMessagesFlow(roomId).collectAsState(initial = emptyList())

    var messageText by remember { mutableStateOf("") }
    var showGiftSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val isOwner = currentUser?.id == room?.ownerId

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
    ) {
        // Top Bar
        Surface(
            color = SadeeqiDarkCard,
            border = androidx.compose.foundation.BorderStroke(0.5.dp, SadeeqiDarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        viewModel.leaveRoom(roomId)
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "خروج", tint = SadeeqiWhite)
                    }
                    Column {
                        Text(
                            text = room?.title ?: "القعدة",
                            color = SadeeqiWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${members.size} أعضاء متصلين",
                            color = SadeeqiOnlineGreen,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        currentUser?.entrySound?.let { SoundManager.playEntrySound(it) }
                    }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "صوت الدخول", tint = SadeeqiYellow)
                    }
                    IconButton(onClick = { showGiftSheet = true }) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "إرسال هدية", tint = SadeeqiYellow)
                    }
                }
            }
        }

        // Active Members Horizontal Scroll
        Surface(
            color = SadeeqiDarkSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(members) { member ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(60.dp)
                    ) {
                        AvatarWithFrame(
                            displayName = member.userName,
                            frame = member.userFrame,
                            size = 40
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = member.userName,
                            color = SadeeqiLightGray,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Room Chat Messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.id
                RoomMessageItem(msg = msg, isMe = isMe)
            }
        }

        // Bottom Input Row
        Surface(
            color = SadeeqiDarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("اكتب في القعدة...", color = SadeeqiGray, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SadeeqiYellow,
                        unfocusedBorderColor = SadeeqiDarkBorder,
                        focusedTextColor = SadeeqiWhite,
                        unfocusedTextColor = SadeeqiWhite,
                        focusedContainerColor = SadeeqiDarkSurface,
                        unfocusedContainerColor = SadeeqiDarkSurface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendRoomMessage(roomId, messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SadeeqiYellow)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "إرسال", tint = SadeeqiBlack)
                }
            }
        }
    }

    if (showGiftSheet) {
        RoomSendGiftDialog(
            viewModel = viewModel,
            onDismiss = { showGiftSheet = false },
            onSend = { giftKey, name, icon ->
                coroutineScope.launch {
                    val res = viewModel.repository.sendGift(roomId, "ROOM", giftKey, name, icon)
                    res.onSuccess {
                        SoundManager.playVictory()
                        viewModel.showMessage(it)
                    }.onFailure {
                        SoundManager.playAlert()
                        viewModel.showMessage(it.message ?: "فشل إرسال الهدية")
                    }
                    showGiftSheet = false
                }
            }
        )
    }
}

@Composable
fun RoomMessageItem(msg: MessageEntity, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            AvatarWithFrame(displayName = msg.senderName, frame = msg.senderFrame, size = 32)
            Spacer(modifier = Modifier.width(6.dp))
        }

        val hasGoldEffect = msg.effect == "GOLD_SPARKLE"
        val hasFireEffect = msg.effect == "FIRE"

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) SadeeqiYellow.copy(alpha = 0.95f) else SadeeqiDarkCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = if (hasGoldEffect || hasFireEffect) 1.5.dp else 1.dp,
                color = if (hasGoldEffect) SadeeqiYellow else if (hasFireEffect) SadeeqiRed else SadeeqiDarkBorder
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (!isMe) {
                    Text(
                        text = msg.senderName,
                        color = SadeeqiYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = msg.text,
                    color = if (isMe) SadeeqiBlack else SadeeqiWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RoomSendGiftDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSend: (key: String, name: String, icon: String) -> Unit
) {
    val inventory by viewModel.userInventory.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SadeeqiDarkCard,
        title = { Text("إرسال هدية للقعدة 🎁", color = SadeeqiYellow, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("اختر هدية من حقيبتك لإهدائها لجميع الحاضرين:", color = SadeeqiLightGray, fontSize = 12.sp)

                val gifts = listOf(
                    Triple("GIFT_ROSE_20", "وردة حمراء", "🌹"),
                    Triple("GIFT_HEART_10", "قلب دافئ", "❤️"),
                    Triple("GIFT_CROWN_5", "تاج ملوكي", "👑")
                )

                gifts.forEach { (key, name, icon) ->
                    val userCount = inventory.find { it.itemKey == key }?.count ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SadeeqiDarkSurface)
                            .clickable { onSend(key, name, icon) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Text("متاح: $userCount", color = if (userCount > 0) SadeeqiYellow else SadeeqiGray, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = SadeeqiGray) }
        }
    )
}
