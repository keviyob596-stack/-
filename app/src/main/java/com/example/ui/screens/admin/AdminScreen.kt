package com.example.ui.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.model.InternalAdEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.UserEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarWithFrame
import com.example.ui.components.SadeeqiButton
import com.example.ui.screens.games.TabButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val adminLogs by viewModel.adminLogs.collectAsState()
    val reports by viewModel.allReports.collectAsState()
    val bans by viewModel.allBans.collectAsState()
    val questions by viewModel.allQuestions.collectAsState()
    val ads by viewModel.allAds.collectAsState()

    var selectedSection by remember { mutableStateOf(0) }
    // 0: Dashboard, 1: Users, 2: Questions, 3: Ads, 4: Reports & Bans, 5: Logs

    if (currentUser?.role != "ADMIN") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SadeeqiBlack),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Security, contentDescription = null, tint = SadeeqiRed, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("منطقة محظورة!", color = SadeeqiRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("هذه اللوحة مخصصة لإدارة تطبيق صديقي فقط.", color = SadeeqiGray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                ) {
                    Text("الرجوع", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
    ) {
        // Admin Header
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
                    Text("🛡️ لوحة تحكم الإدارة (Admin)", color = SadeeqiYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("مدير النظام: ${currentUser?.displayName}", color = SadeeqiGray, fontSize = 11.sp)
                }
            }
            Surface(
                color = SadeeqiRedDark,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "صلاحيات كاملة",
                    color = SadeeqiWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Sections Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SadeeqiDarkSurface, RoundedCornerShape(10.dp))
                .padding(3.dp)
        ) {
            TabButton("الإحصائيات", isSelected = selectedSection == 0, modifier = Modifier.weight(1f)) { selectedSection = 0 }
            TabButton("المستخدمين", isSelected = selectedSection == 1, modifier = Modifier.weight(1f)) { selectedSection = 1 }
            TabButton("بنك الأسئلة", isSelected = selectedSection == 2, modifier = Modifier.weight(1.1f)) { selectedSection = 2 }
            TabButton("الإعلانات", isSelected = selectedSection == 3, modifier = Modifier.weight(1f)) { selectedSection = 3 }
            TabButton("السجلات", isSelected = selectedSection == 5, modifier = Modifier.weight(1f)) { selectedSection = 5 }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedSection) {
            0 -> AdminDashboardView(allUsers, allTransactions, reports, ads)
            1 -> AdminUsersView(allUsers, viewModel)
            2 -> AdminQuestionsView(questions, viewModel)
            3 -> AdminAdsView(ads, viewModel)
            5 -> AdminLogsView(adminLogs)
        }
    }
}

@Composable
fun AdminDashboardView(
    users: List<UserEntity>,
    transactions: List<com.example.data.model.CoinTransactionEntity>,
    reports: List<com.example.data.model.ReportEntity>,
    ads: List<InternalAdEntity>
) {
    val totalCoins = users.sumOf { it.onlineCoins }
    val onlineCount = users.count { it.isOnline }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("📊 إحصائيات النظام الفورية", color = SadeeqiWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("إجمالي الأعضاء", "${users.size}", "👥", modifier = Modifier.weight(1f))
            StatCard("المتصلون الآن", "$onlineCount", "🟢", modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("الكوينز المتداولة", "$totalCoins 🟡", "💰", modifier = Modifier.weight(1f))
            StatCard("المعاملات المالية", "${transactions.size}", "📜", modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard("البلاغات المفتوحة", "${reports.count { it.status == "OPEN" }}", "🚨", modifier = Modifier.weight(1f))
            StatCard("الإعلانات النشطة", "${ads.count { it.isActive }}", "📢", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = SadeeqiGray, fontSize = 12.sp)
                Text(icon, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = SadeeqiYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminUsersView(
    users: List<UserEntity>,
    viewModel: MainViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var userToEditCoins by remember { mutableStateOf<UserEntity?>(null) }
    var userToBan by remember { mutableStateOf<UserEntity?>(null) }

    val filtered = users.filter {
        it.username.contains(searchQuery, true) || it.displayName.contains(searchQuery, true) || it.email.contains(searchQuery, true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث بالاسم أو اليوزر أو الإيميل...", color = SadeeqiGray, fontSize = 12.sp) },
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
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(filtered) { user ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (user.isBanned) SadeeqiRed else SadeeqiDarkBorder),
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
                            AvatarWithFrame(displayName = user.displayName, frame = user.frame, size = 42)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(user.displayName, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("@${user.username} • ${user.email}", color = SadeeqiGray, fontSize = 11.sp)
                                Text("رصيد: ${user.onlineCoins} 🟡 | Level ${user.level}", color = SadeeqiYellow, fontSize = 11.sp)
                            }
                        }

                        Row {
                            Button(
                                onClick = { userToEditCoins = user },
                                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text("تعديل رصيد", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            if (user.isBanned) {
                                Button(
                                    onClick = { viewModel.adminUnbanUser(user.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiOnlineGreen, contentColor = SadeeqiBlack),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("فك حظر", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { userToBan = user },
                                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiDarkSurface, contentColor = SadeeqiRed),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("حظر", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (userToEditCoins != null) {
        AdminModifyCoinsDialog(
            user = userToEditCoins!!,
            onDismiss = { userToEditCoins = null },
            onConfirm = { amount, reason ->
                viewModel.adminModifyCoins(userToEditCoins!!.id, amount, reason)
                userToEditCoins = null
            }
        )
    }

    if (userToBan != null) {
        AdminBanDialog(
            user = userToBan!!,
            onDismiss = { userToBan = null },
            onConfirm = { reason, perm ->
                viewModel.adminBanUser(userToBan!!.id, reason, perm)
                userToBan = null
            }
        )
    }
}

@Composable
fun AdminModifyCoinsDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, reason: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var isDeduct by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("مكافأة إدارية") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SadeeqiDarkCard,
        title = { Text("تعديل كوينز: ${user.displayName}", color = SadeeqiYellow, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("الرصيد الحالي: ${user.onlineCoins} كوينز أونلاين 🟡", color = SadeeqiLightGray, fontSize = 13.sp)

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { isDeduct = false },
                        colors = ButtonDefaults.buttonColors(containerColor = if (!isDeduct) SadeeqiYellow else SadeeqiDarkSurface, contentColor = if (!isDeduct) SadeeqiBlack else SadeeqiWhite),
                        modifier = Modifier.weight(1f)
                    ) { Text("إضافة (+)") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { isDeduct = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isDeduct) SadeeqiRed else SadeeqiDarkSurface, contentColor = SadeeqiWhite),
                        modifier = Modifier.weight(1f)
                    ) { Text("خصم (-)") }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("المبلغ", color = SadeeqiGray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("السبب (يسجل في تقرير التدقيق)", color = SadeeqiGray) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    if (amt > 0) {
                        val finalAmt = if (isDeduct) -amt else amt
                        onConfirm(finalAmt, reason)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
            ) {
                Text("تأكيد العملية", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = SadeeqiGray) }
        }
    )
}

@Composable
fun AdminBanDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (reason: String, isPermanent: Boolean) -> Unit
) {
    var reason by remember { mutableStateOf("مخالفة شروط مجتمع صديقي") }
    var isPermanent by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SadeeqiDarkCard,
        title = { Text("حظر حساب: ${user.displayName}", color = SadeeqiRed, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("سبب الحظر", color = SadeeqiGray) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حظر نهائي دائم", color = SadeeqiWhite)
                    Switch(checked = isPermanent, onCheckedChange = { isPermanent = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reason, isPermanent) },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiRed, contentColor = SadeeqiWhite)
            ) {
                Text("تأكيد الحظر", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء", color = SadeeqiGray) }
        }
    )
}

@Composable
fun AdminQuestionsView(
    questions: List<QuestionEntity>,
    viewModel: MainViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("بنك الأسئلة (${questions.size} سؤال)", color = SadeeqiWhite, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة سؤال", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(questions) { q ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(q.question, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("اللعبة: ${q.gameType} | الإجابة الصحيحة: ${q.correctAnswer}", color = SadeeqiYellow, fontSize = 11.sp)
                        }
                        IconButton(onClick = { viewModel.adminDeleteQuestion(q.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = SadeeqiRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var qText by remember { mutableStateOf("") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var correct by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("معلومات عامة") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SadeeqiDarkCard,
            title = { Text("إضافة سؤال جديد", color = SadeeqiYellow, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("نص السؤال") })
                    OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("الخيار أ") })
                    OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("الخيار ب") })
                    OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("الخيار ج") })
                    OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("الخيار د") })
                    OutlinedTextField(value = correct, onValueChange = { correct = it }, label = { Text("الإجابة الصحيحة بالضبط") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qText.isNotBlank() && correct.isNotBlank()) {
                            viewModel.adminAddQuestion(
                                QuestionEntity(
                                    gameType = "GENERAL",
                                    category = category,
                                    question = qText,
                                    optionA = optA,
                                    optionB = optB,
                                    optionC = optC,
                                    optionD = optD,
                                    correctAnswer = correct
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                ) { Text("حفظ السؤال") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun AdminAdsView(
    ads: List<InternalAdEntity>,
    viewModel: MainViewModel
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("الإعلانات الداخلية (${ads.size})", color = SadeeqiWhite, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إعلان جديد", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(ads) { ad ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ad.title, color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(ad.description, color = SadeeqiGray, fontSize = 11.sp, maxLines = 1)
                            Text("الزر: ${ad.buttonText} | نشط: ${if (ad.isActive) "نعم ✓" else "لا ✗"}", color = SadeeqiYellow, fontSize = 10.sp)
                        }
                        IconButton(onClick = { viewModel.adminDeleteAd(ad.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = SadeeqiRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var btnText by remember { mutableStateOf("عرض التفاصيل") }
        var linkUrl by remember { mutableStateOf("games") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = SadeeqiDarkCard,
            title = { Text("إنشاء إعلان جديد", color = SadeeqiYellow, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان الإعلان") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("الوصف") })
                    OutlinedTextField(value = btnText, onValueChange = { btnText = it }, label = { Text("نص الزر") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.adminSaveAd(
                                InternalAdEntity(
                                    title = title,
                                    description = desc,
                                    buttonText = btnText,
                                    linkUrl = linkUrl,
                                    isActive = true
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                ) { Text("حفظ الإعلان") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
fun AdminLogsView(logs: List<com.example.data.model.AdminLogEntity>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("سجل التدقيق الإداري (Audit Logs)", color = SadeeqiWhite, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد سجلات حالياً", color = SadeeqiGray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                items(logs) { log ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("بواسطة: ${log.adminName}", color = SadeeqiGray, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("الهدف: ${log.targetUser} | ${log.details}", color = SadeeqiWhite, fontSize = 12.sp)
                            if (log.reason.isNotBlank()) {
                                Text("السبب: ${log.reason}", color = SadeeqiLightGray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
