package com.example.ui.screens.friends

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.MessageEntity
import com.example.data.model.UserEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarWithFrame
import com.example.ui.components.SadeeqiButton
import com.example.ui.screens.games.TabButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(
    viewModel: MainViewModel,
    onOpenChat: (friendUserId: Long) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val friendsList by viewModel.friendsList.collectAsState()
    val pendingRequests by viewModel.pendingFriendRequests.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: My Friends, 1: Requests, 2: Discover/Add
    var showSendCoinsDialog by remember { mutableStateOf<UserEntity?>(null) }
    var searchUserQuery by remember { mutableStateOf("") }

    // Map friend IDs to UserEntity
    val myFriendUsers = friendsList.mapNotNull { f ->
        val otherId = if (f.userId == currentUser?.id) f.friendId else f.userId
        allUsers.find { it.id == otherId }
    }

    val requestUsers = pendingRequests.mapNotNull { req ->
        val requester = allUsers.find { it.id == req.requesterId }
        requester?.let { Pair(req.id, it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
    ) {
        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👥 قائمة الأصدقاء", color = SadeeqiYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Surface(
                color = SadeeqiDarkSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder)
            ) {
                Text(
                    text = "${myFriendUsers.size} صديق",
                    color = SadeeqiWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SadeeqiDarkSurface, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            TabButton(
                text = "أصدقائي (${myFriendUsers.size})",
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 0 }
            )
            TabButton(
                text = "الطلبات (${requestUsers.size})",
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 1 }
            )
            TabButton(
                text = "إضافة أصدقاء 🔍",
                isSelected = selectedTab == 2,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 2 }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                // My Friends List
                if (myFriendUsers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = SadeeqiGray, modifier = Modifier.size(50.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("ليس لديك أصدقاء بعد", color = SadeeqiLightGray, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("ابحث عن لاعبين ووسع دائرة صداقاتك!", color = SadeeqiGray, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(myFriendUsers) { friend ->
                            FriendItemCard(
                                user = friend,
                                onChat = { onOpenChat(friend.id) },
                                onSendCoins = { showSendCoinsDialog = friend },
                                onBlock = { viewModel.blockUser(friend.id) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Friend Requests
                if (requestUsers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد طلبات صداقة معلقة حالياً", color = SadeeqiGray)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(requestUsers) { (reqId, sender) ->
                            FriendRequestCard(
                                sender = sender,
                                onAccept = { viewModel.acceptFriendRequest(reqId) },
                                onReject = { viewModel.rejectFriendRequest(reqId) }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Search & Add Users
                Column(modifier = Modifier.fillMaxSize()) {
                    OutlinedTextField(
                        value = searchUserQuery,
                        onValueChange = { searchUserQuery = it },
                        placeholder = { Text("ابحث باسم المستخدم أو البريد...", color = SadeeqiGray, fontSize = 13.sp) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    val discoverUsers = allUsers.filter {
                        it.id != currentUser?.id &&
                                (searchUserQuery.isBlank() || it.username.contains(searchUserQuery, true) || it.displayName.contains(searchUserQuery, true))
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(discoverUsers) { user ->
                            val isFriend = myFriendUsers.any { it.id == user.id }
                            DiscoverUserCard(
                                user = user,
                                isFriend = isFriend,
                                onAddFriend = { viewModel.sendFriendRequest(user.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSendCoinsDialog != null) {
        SendCoinsModal(
            receiver = showSendCoinsDialog!!,
            currentUserCoins = currentUser?.onlineCoins ?: 0L,
            onDismiss = { showSendCoinsDialog = null },
            onSend = { amount, reason ->
                viewModel.sendCoins(showSendCoinsDialog!!.id, amount, reason)
                showSendCoinsDialog = null
            }
        )
    }
}

@Composable
fun FriendItemCard(
    user: UserEntity,
    onChat: () -> Unit,
    onSendCoins: () -> Unit,
    onBlock: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                AvatarWithFrame(displayName = user.displayName, frame = user.frame, size = 46)
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (user.isOnline) SadeeqiOnlineGreen else SadeeqiGray)
                        .align(Alignment.BottomEnd)
                        .border(1.5.dp, SadeeqiDarkCard, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.displayName,
                    color = SadeeqiWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (user.isOnline) "🟢 متصل الآن" else "⚫ غير متصل",
                        color = if (user.isOnline) SadeeqiOnlineGreen else SadeeqiGray,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Level ${user.level}",
                        color = SadeeqiYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onSendCoins) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = "إرسال كوينز", tint = SadeeqiYellow)
                }
                IconButton(onClick = onChat) {
                    Icon(Icons.Default.ChatBubble, contentDescription = "دردشة", tint = SadeeqiWhite)
                }
            }
        }
    }
}

@Composable
fun FriendRequestCard(
    sender: UserEntity,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithFrame(displayName = sender.displayName, frame = sender.frame, size = 44)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = sender.displayName, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "@${sender.username}", color = SadeeqiGray, fontSize = 11.sp)
                }
            }

            Row {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("قبول", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = onReject,
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("رفض", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DiscoverUserCard(
    user: UserEntity,
    isFriend: Boolean,
    onAddFriend: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithFrame(displayName = user.displayName, frame = user.frame, size = 44)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = user.displayName, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "@${user.username} • Level ${user.level}", color = SadeeqiGray, fontSize = 11.sp)
                }
            }

            if (isFriend) {
                Surface(
                    color = SadeeqiDarkSurface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "صديق ✓",
                        color = SadeeqiYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            } else {
                Button(
                    onClick = onAddFriend,
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SEND COINS MODAL (SECURITY CHECKS, CONFIRMATION, REASON)
// -------------------------------------------------------------
@Composable
fun SendCoinsModal(
    receiver: UserEntity,
    currentUserCoins: Long,
    onDismiss: () -> Unit,
    onSend: (amount: Long, reason: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var reasonText by remember { mutableStateOf("هدية صداقة") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmStep by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SadeeqiDarkCard,
        title = {
            Text("إرسال Online Coins 💰", color = SadeeqiYellow, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = SadeeqiDarkSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("المستلم: ${receiver.displayName} (@${receiver.username})", color = SadeeqiWhite, fontWeight = FontWeight.Bold)
                        Text("رصيدك الحالي المتاح: $currentUserCoins كوينز أونلاين 🟡", color = SadeeqiYellow, fontSize = 12.sp)
                    }
                }

                if (!showConfirmStep) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it.filter { char -> char.isDigit() }
                            errorMessage = null
                        },
                        label = { Text("كمية الكوينز", color = SadeeqiGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("سبب الإرسال / ملاحظة", color = SadeeqiGray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SadeeqiYellow,
                            unfocusedBorderColor = SadeeqiDarkBorder,
                            focusedTextColor = SadeeqiWhite,
                            unfocusedTextColor = SadeeqiWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(errorMessage!!, color = SadeeqiRed, fontSize = 12.sp)
                    }
                } else {
                    // Confirmation View
                    Surface(
                        color = SadeeqiYellow.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiYellow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("تأكيد العملية النهائية:", color = SadeeqiYellow, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("المبلغ: $amountText كوينز أونلاين", color = SadeeqiWhite)
                            Text("المستلم: ${receiver.displayName}", color = SadeeqiLightGray, fontSize = 12.sp)
                            Text("الملاحظة: $reasonText", color = SadeeqiGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("⚠️ هذه العملية نهائية وسيتم تسجيلها في سجل المعاملات.", color = SadeeqiLightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showConfirmStep) {
                Button(
                    onClick = {
                        val amount = amountText.toLongOrNull() ?: 0L
                        if (amount <= 0) {
                            errorMessage = "يرجى إدخال مبلغ صحيح أكبر من صفر"
                        } else if (amount > currentUserCoins) {
                            errorMessage = "رصيدك غير كافٍ! متاح: $currentUserCoins"
                        } else {
                            showConfirmStep = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                ) {
                    Text("متابعة", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        val amount = amountText.toLongOrNull() ?: 0L
                        onSend(amount, reasonText)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                ) {
                    Text("تأكيد الإرسال الآن 💸", fontWeight = FontWeight.Bold)
                }
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
// PRIVATE CHAT SCREEN (REALTIME MESSAGES, REACTIONS, GIFTS)
// -------------------------------------------------------------
@Composable
fun PrivateChatScreen(
    otherUserId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val otherUser = allUsers.find { it.id == otherUserId }

    val messages by viewModel.repository.messageDao.getPrivateMessagesFlow(
        currentUser?.id ?: 0L,
        otherUserId
    ).collectAsState(initial = emptyList())

    var messageText by remember { mutableStateOf("") }
    var showGiftModal by remember { mutableStateOf(false) }
    var showCoinsModal by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val emojis = listOf("❤️", "😂", "😮", "😢", "👍", "🔥")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
    ) {
        // Chat Header
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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = SadeeqiWhite)
                    }
                    AvatarWithFrame(
                        displayName = otherUser?.displayName ?: "ص",
                        frame = otherUser?.frame ?: "NONE",
                        size = 38
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = otherUser?.displayName ?: "محادثة خاصة",
                            color = SadeeqiWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (otherUser?.isOnline == true) "🟢 متصل" else "⚫ غير متصل",
                            color = if (otherUser?.isOnline == true) SadeeqiOnlineGreen else SadeeqiGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Row {
                    IconButton(onClick = { showCoinsModal = true }) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = "تحويل كوينز", tint = SadeeqiYellow)
                    }
                    IconButton(onClick = { showGiftModal = true }) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "إرسال هدية", tint = SadeeqiYellow)
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.id
                PrivateMessageItem(
                    msg = msg,
                    isMe = isMe,
                    onReact = { emoji -> viewModel.addReaction(msg.id, emoji) }
                )
            }
        }

        // Bottom Input
        Surface(
            color = SadeeqiDarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Quick emoji picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    emojis.forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clickable {
                                    messageText += emoji
                                }
                                .padding(4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("اكتب رسالتك...", color = SadeeqiGray, fontSize = 13.sp) },
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
                                viewModel.sendPrivateMessage(otherUserId, messageText)
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
    }

    if (showGiftModal && otherUser != null) {
        com.example.ui.screens.rooms.RoomSendGiftDialog(
            viewModel = viewModel,
            onDismiss = { showGiftModal = false },
            onSend = { key, name, icon ->
                coroutineScope.launch {
                    val res = viewModel.repository.sendGift(otherUserId, "PRIVATE", key, name, icon)
                    res.onSuccess {
                        SoundManager.playVictory()
                        viewModel.showMessage(it)
                    }.onFailure {
                        SoundManager.playAlert()
                        viewModel.showMessage(it.message ?: "فشل الإرسال")
                    }
                    showGiftModal = false
                }
            }
        )
    }

    if (showCoinsModal && otherUser != null) {
        SendCoinsModal(
            receiver = otherUser,
            currentUserCoins = currentUser?.onlineCoins ?: 0L,
            onDismiss = { showCoinsModal = false },
            onSend = { amount, reason ->
                viewModel.sendCoins(otherUser.id, amount, reason)
                showCoinsModal = false
            }
        )
    }
}

@Composable
fun PrivateMessageItem(
    msg: MessageEntity,
    isMe: Boolean,
    onReact: (String) -> Unit
) {
    var showReactionsBar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) SadeeqiYellow.copy(alpha = 0.95f) else SadeeqiDarkCard
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = if (msg.effect != "NONE") 1.5.dp else 1.dp,
                color = if (msg.effect == "GOLD_SPARKLE") SadeeqiYellow else if (msg.effect == "FIRE") SadeeqiRed else SadeeqiDarkBorder
            ),
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clickable { showReactionsBar = !showReactionsBar }
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = msg.text,
                    color = if (isMe) SadeeqiBlack else SadeeqiWhite,
                    fontSize = 14.sp
                )
            }
        }

        // Reaction Bar on click
        if (showReactionsBar) {
            Surface(
                color = SadeeqiDarkSurface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                    listOf("❤️", "😂", "😮", "😢", "👍", "🔥").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .clickable {
                                    onReact(emoji)
                                    showReactionsBar = false
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
