package com.example.ui.screens.games

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.games.AutobusDictionary
import com.example.data.model.QuestionEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarWithFrame
import com.example.ui.components.SadeeqiButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GamesHubScreen(
    viewModel: MainViewModel,
    onStartAutobus: (isOffline: Boolean) -> Unit,
    onStartQuiz: (gameType: String, isOffline: Boolean) -> Unit
) {
    val isOffline by viewModel.isOfflineMode.collectAsState()
    var selectedTab by remember { mutableStateOf(if (isOffline) 1 else 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
    ) {
        // Mode Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SadeeqiDarkSurface, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            TabButton(
                text = "🌐 ألعاب أونلاين",
                isSelected = selectedTab == 0,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 0 }
            )
            TabButton(
                text = "🔴 بدون نت (14 لعبة + بوتات)",
                isSelected = selectedTab == 1,
                modifier = Modifier.weight(1.3f),
                onClick = { selectedTab = 1 }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0 && isOffline) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔴 أنت حالياً في وضع عدم الاتصال", color = SadeeqiRed, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("انتقل لتبويب 'بدون نت' للعب مع البوتات بدون استهلاك بيانات أو أعد تفعيل الاتصال.", color = SadeeqiGray, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.toggleOfflineMode() },
                        colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack)
                    ) {
                        Text("الرجوع إلى وضع أونلاين 🟢", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Autobus Complete Featured Card
                FeaturedGameCard(
                    title = "🚌 أتوبيس كومبليت الكاملة",
                    badge = "اللعبة الأكثر شعبية",
                    description = "اكتب أسماء الأولاد والبنات والحيوانات والبلاد بالحرف المختار قبل انتهاء الوقت واضغط 'خلصت'!",
                    reward = if (selectedTab == 0) "+50 Online Coins" else "+30 Offline Coins",
                    onClick = {
                        SoundManager.playClick()
                        onStartAutobus(selectedTab == 1)
                    }
                )
            }

            val gamesList = if (selectedTab == 0) {
                listOf(
                    GameItem("🧠 أسئلة عامة", "GENERAL", "اختبر معلوماتك العامة في مسابقة سرعة متعددة الخيارات", "4 خيارات"),
                    GameItem("✅ صح أم خطأ", "TRUE_FALSE", "معلومات سريعة وعجائب وغرائب حول العالم، احكم بصح أم خطأ", "سريعة"),
                    GameItem("⚡ أسرع إجابة", "FAST", "تحدي الحساب والبديهة السريعة لأصحاب الردود الخاطفة", "تحدي سرعة"),
                    GameItem("😂 أسئلة مضحكة وفوازير", "FUNNY", "فوازير طريفة وأسئلة ذكاء خفيفة الظل", "ضحك وفرفشة"),
                    GameItem("🎭 مين أنا؟", "WHO_AM_I", "تعرف على الشخصية التاريخية أو المشهورة من التلميحات", "تخمين"),
                    GameItem("🕵️ مين الكذاب", "WHO_IS_LIAR", "أربع حقائق وشائعة واحدة مضللة، اكتشف الكذبة!", "كشف الخداع"),
                    GameItem("🔤 خمن الكلمة", "GUESS_WORD", "رتب الحروف المبعثرة واكشف الكلمة السرية", "كلمات")
                )
            } else {
                // 14 Offline Games
                listOf(
                    GameItem("🧠 أسئلة عامة (ضد البوت)", "GENERAL", "نافس البوتات الذكية في بنك المعلومات المحلي", "Easy/Hard"),
                    GameItem("✅ صح أم خطأ (بدون نت)", "TRUE_FALSE", "حكم على الحقائق مع بوتات الذكاء الاصطناعي", "فوري"),
                    GameItem("⚡ أسرع إجابة (رياضيات)", "FAST", "تحدي الحساب الذهني الخاطف دون نت", "ذكاء"),
                    GameItem("😂 فوازير صديقي", "FUNNY", "ألغاز خفيفة وبوتات تنافسية", "تسلية"),
                    GameItem("🎭 مين أنا؟ (بدون نت)", "WHO_AM_I", "خمن الشخصيات العربية والعالمية", "معلومات"),
                    GameItem("🕵️ مين الكذاب", "WHO_IS_LIAR", "اكتشف الشائعة الكاذبة قبل البوت", "تحدي"),
                    GameItem("🔤 خمن الكلمة", "GUESS_WORD", "فصل الحروف واكتشف العواصم والحيوانات", "تفكير"),
                    GameItem("🧩 ألغاز صديقي الذكية", "FUNNY", "ألغاز منطقية بدون إنترنت", "تركيز"),
                    GameItem("🔢 تحديات حسابية", "FAST", "معادلات رياضية سريعة لحصد كوينز أوفلاين", "حساب"),
                    GameItem("🧠 اختبار الذاكرة الخاطفة", "GENERAL", "احفظ وأجب سريعاً", "ذاكرة"),
                    GameItem("🖼️ خمن الصورة والرمز", "GUESS_WORD", "رموز وأمثال شعبية", "ثقافة"),
                    GameItem("🎲 التحدي العشوائي", "GENERAL", "مزيج أسئلة عشوائية من كل التصنيفات", "عشوائي"),
                    GameItem("🎯 تحدي النقاط التراكمي", "FAST", "اجمع أكبر عدد نقاط متتالية دون خطأ", "ماراثون")
                )
            }

            items(gamesList) { game ->
                GameListCard(
                    item = game,
                    isOffline = selectedTab == 1,
                    onPlay = {
                        SoundManager.playClick()
                        onStartQuiz(game.type, selectedTab == 1)
                    }
                )
            }
        }
    }
}

data class GameItem(val title: String, val type: String, val desc: String, val tag: String)

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) SadeeqiYellow else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) SadeeqiBlack else SadeeqiLightGray,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun FeaturedGameCard(
    title: String,
    badge: String,
    description: String,
    reward: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, SadeeqiYellow),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = SadeeqiYellow,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badge,
                        color = SadeeqiBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = reward,
                    color = SadeeqiYellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = SadeeqiWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = SadeeqiGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ابدأ اللعب الآن 🎮", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun GameListCard(
    item: GameItem,
    isOffline: Boolean,
    onPlay: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        color = SadeeqiWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = SadeeqiDarkSurface,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, SadeeqiGray.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = item.tag,
                            color = SadeeqiLightGray,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.desc,
                    color = SadeeqiGray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOffline) SadeeqiRedDark else SadeeqiYellow,
                    contentColor = if (isOffline) SadeeqiWhite else SadeeqiBlack
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("لعب", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// AUTOBUS COMPLETE FULL PLAYABLE GAME
// -------------------------------------------------------------
@Composable
fun AutobusGameScreen(
    isOffline: Boolean,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentLetter by remember { mutableStateOf(AutobusDictionary.getRandomLetter()) }
    var timeLeft by remember { mutableStateOf(45) }
    var isGameOver by remember { mutableStateOf(false) }

    // User inputs
    var boy by remember { mutableStateOf("") }
    var girl by remember { mutableStateOf("") }
    var animal by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var obj by remember { mutableStateOf("") }
    var plant by remember { mutableStateOf("") }
    var food by remember { mutableStateOf("") }

    // Bot difficulty
    var botDifficulty by remember { mutableStateOf("MEDIUM") }

    // Bot inputs (Simulated typing)
    var botBoy by remember { mutableStateOf("") }
    var botGirl by remember { mutableStateOf("") }
    var botAnimal by remember { mutableStateOf("") }
    var botCountry by remember { mutableStateOf("") }
    var botObject by remember { mutableStateOf("") }
    var botPlant by remember { mutableStateOf("") }
    var botFood by remember { mutableStateOf("") }

    // Results
    var userScore by remember { mutableStateOf(0) }
    var botScore by remember { mutableStateOf(0) }
    var coinsWon by remember { mutableStateOf(0L) }
    var xpWon by remember { mutableStateOf(0) }

    // Timer countdown
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (timeLeft > 0 && !isGameOver) {
                delay(1000)
                timeLeft--
                // Bot gradually writes answers
                if (timeLeft == 38) botBoy = AutobusDictionary.getBotAnswer(currentLetter, "ولد", botDifficulty)
                if (timeLeft == 32) botGirl = AutobusDictionary.getBotAnswer(currentLetter, "بنت", botDifficulty)
                if (timeLeft == 26) botAnimal = AutobusDictionary.getBotAnswer(currentLetter, "حيوان", botDifficulty)
                if (timeLeft == 20) botCountry = AutobusDictionary.getBotAnswer(currentLetter, "بلد", botDifficulty)
                if (timeLeft == 14) botObject = AutobusDictionary.getBotAnswer(currentLetter, "جماد", botDifficulty)
                if (timeLeft == 8) botPlant = AutobusDictionary.getBotAnswer(currentLetter, "نبات", botDifficulty)
                if (timeLeft == 3) botFood = AutobusDictionary.getBotAnswer(currentLetter, "أكل", botDifficulty)
            }
            if (timeLeft <= 0 && !isGameOver) {
                isGameOver = true
            }
        }
    }

    // End Game calculation
    fun finishGame() {
        if (isGameOver) return
        isGameOver = true

        // Ensure bot has finished answers if user finished early
        if (botBoy.isEmpty()) botBoy = AutobusDictionary.getBotAnswer(currentLetter, "ولد", botDifficulty)
        if (botGirl.isEmpty()) botGirl = AutobusDictionary.getBotAnswer(currentLetter, "بنت", botDifficulty)
        if (botAnimal.isEmpty()) botAnimal = AutobusDictionary.getBotAnswer(currentLetter, "حيوان", botDifficulty)
        if (botCountry.isEmpty()) botCountry = AutobusDictionary.getBotAnswer(currentLetter, "بلد", botDifficulty)
        if (botObject.isEmpty()) botObject = AutobusDictionary.getBotAnswer(currentLetter, "جماد", botDifficulty)
        if (botPlant.isEmpty()) botPlant = AutobusDictionary.getBotAnswer(currentLetter, "نبات", botDifficulty)
        if (botFood.isEmpty()) botFood = AutobusDictionary.getBotAnswer(currentLetter, "أكل", botDifficulty)

        var uScore = 0
        var bScore = 0

        fun evaluateCategory(uAns: String, bAns: String, cat: String): Pair<Int, Int> {
            val uVal = AutobusDictionary.validateAnswer(currentLetter, cat, uAns)
            val bVal = AutobusDictionary.validateAnswer(currentLetter, cat, bAns)
            return if (uVal && bVal) {
                if (uAns.trim().equals(bAns.trim(), ignoreCase = true)) Pair(5, 5) // Duplicate = 5 each
                else Pair(10, 10)
            } else if (uVal && !bVal) {
                Pair(10, 0)
            } else if (!uVal && bVal) {
                Pair(0, 10)
            } else Pair(0, 0)
        }

        val rBoy = evaluateCategory(boy, botBoy, "ولد")
        val rGirl = evaluateCategory(girl, botGirl, "بنت")
        val rAnimal = evaluateCategory(animal, botAnimal, "حيوان")
        val rCountry = evaluateCategory(country, botCountry, "بلد")
        val rObj = evaluateCategory(obj, botObject, "جماد")
        val rPlant = evaluateCategory(plant, botPlant, "نبات")
        val rFood = evaluateCategory(food, botFood, "أكل")

        uScore = rBoy.first + rGirl.first + rAnimal.first + rCountry.first + rObj.first + rPlant.first + rFood.first
        bScore = rBoy.second + rGirl.second + rAnimal.second + rCountry.second + rObj.second + rPlant.second + rFood.second

        userScore = uScore
        botScore = bScore

        val isWin = uScore >= bScore
        coinsWon = if (isWin) (if (isOffline) 35L else 60L) else (if (isOffline) 10L else 20L)
        xpWon = if (isWin) 100 else 40

        coroutineScope.launch {
            viewModel.repository.recordGameResult(isWin, isOffline, coinsWon, xpWon)
            if (isWin) SoundManager.playVictory() else SoundManager.playAlert()
        }
    }

    LaunchedEffect(isGameOver) {
        if (isGameOver && userScore == 0 && botScore == 0) {
            finishGame()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = SadeeqiWhite)
            }
            Text(
                text = "🚌 أتوبيس كومبليت ${if (isOffline) "(أوفلاين)" else "(أونلاين)"}",
                color = SadeeqiYellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            // Timer Badge
            Surface(
                color = if (timeLeft < 10) SadeeqiRed else SadeeqiDarkSurface,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (timeLeft < 10) SadeeqiRed else SadeeqiYellow)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = SadeeqiWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$timeLeft ث", color = SadeeqiWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chosen Letter Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
            border = androidx.compose.foundation.BorderStroke(2.dp, SadeeqiYellow),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("الحرف المختار للجولة:", color = SadeeqiGray, fontSize = 13.sp)
                    Text("اكتب جميع الإجابات التي تبدأ بحرف:", color = SadeeqiWhite, fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SadeeqiYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$currentLetter",
                        color = SadeeqiBlack,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isGameOver) {
            // Category Input Fields
            AutobusInputField(label = "اسم ولد", value = boy, onValueChange = { boy = it })
            AutobusInputField(label = "اسم بنت", value = girl, onValueChange = { girl = it })
            AutobusInputField(label = "حيوان", value = animal, onValueChange = { animal = it })
            AutobusInputField(label = "بلد / مدينة", value = country, onValueChange = { country = it })
            AutobusInputField(label = "جماد", value = obj, onValueChange = { obj = it })
            AutobusInputField(label = "نبات", value = plant, onValueChange = { plant = it })
            AutobusInputField(label = "أكل / وجبة", value = food, onValueChange = { food = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Finished Button
            Button(
                onClick = { finishGame() },
                colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("خلصت! (إنهاء الجولة وحساب النقاط) 🏁", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else {
            // GAME RESULTS SCREEN
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (userScore >= botScore) SadeeqiYellow else SadeeqiRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (userScore >= botScore) "🎉 مبروووك الفوز! انت بطل الحروف 🏆" else "حظاً أوفر في الجولة القادمة! 🤖",
                        color = if (userScore >= botScore) SadeeqiYellow else SadeeqiWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("أنت", color = SadeeqiYellow, fontWeight = FontWeight.Bold)
                            Text("$userScore نقطة", color = SadeeqiWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Text("ضد", color = SadeeqiGray, modifier = Modifier.align(Alignment.CenterVertically))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("البوت المنافس 🤖", color = SadeeqiLightGray)
                            Text("$botScore نقطة", color = SadeeqiLightGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        color = SadeeqiDarkSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text("المكافأة: +$coinsWon ${if (isOffline) "كوينز أوفلاين 🔴" else "Online Coins 🟡"}", color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("+$xpWon XP ⚡", color = SadeeqiWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // Table comparison
                    Text("مقارنة الإجابات بحرف '$currentLetter':", color = SadeeqiGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    AnswerRow(cat = "ولد", user = boy, bot = botBoy)
                    AnswerRow(cat = "بنت", user = girl, bot = botGirl)
                    AnswerRow(cat = "حيوان", user = animal, bot = botAnimal)
                    AnswerRow(cat = "بلد", user = country, bot = botCountry)
                    AnswerRow(cat = "جماد", user = obj, bot = botObject)
                    AnswerRow(cat = "نبات", user = plant, bot = botPlant)
                    AnswerRow(cat = "أكل", user = food, bot = botFood)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            currentLetter = AutobusDictionary.getRandomLetter()
                            timeLeft = 45
                            isGameOver = false
                            boy = ""; girl = ""; animal = ""; country = ""; obj = ""; plant = ""; food = ""
                            botBoy = ""; botGirl = ""; botAnimal = ""; botCountry = ""; botObject = ""; botPlant = ""; botFood = ""
                            userScore = 0; botScore = 0
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("جولة جديدة 🔄", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AutobusInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = SadeeqiGray, fontSize = 13.sp) },
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
    }
}

@Composable
fun AnswerRow(cat: String, user: String, bot: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$cat:", color = SadeeqiLightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("أنت: ${if (user.isBlank()) "—" else user}", color = SadeeqiYellow, fontSize = 12.sp)
        Text("البوت: ${if (bot.isBlank()) "—" else bot}", color = SadeeqiGray, fontSize = 12.sp)
    }
}

// -------------------------------------------------------------
// QUIZ GAME SCREEN (GENERAL, TRUE_FALSE, FAST, FUNNY, WHO_AM_I, ETC.)
// -------------------------------------------------------------
@Composable
fun QuizGameScreen(
    gameType: String,
    isOffline: Boolean,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var userScore by remember { mutableStateOf(0) }
    var botScore by remember { mutableStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(15) }

    LaunchedEffect(gameType) {
        val list = viewModel.repository.questionDao.getQuestionsByGameType(gameType)
        questions = if (list.isNotEmpty()) list.shuffled() else listOf(
            QuestionEntity(
                gameType = gameType,
                category = "معلومات عامة",
                question = "ما هي عاصمة دولة مصر؟",
                optionA = "القاهرة",
                optionB = "الإسكندرية",
                optionC = "الجيزة",
                optionD = "أسوان",
                correctAnswer = "القاهرة"
            )
        )
    }

    // Timer
    LaunchedEffect(currentIndex, isAnswerRevealed, isQuizFinished) {
        if (!isAnswerRevealed && !isQuizFinished && questions.isNotEmpty()) {
            timerSeconds = 15
            while (timerSeconds > 0 && !isAnswerRevealed) {
                delay(1000)
                timerSeconds--
            }
            if (timerSeconds <= 0 && !isAnswerRevealed) {
                // Time up
                isAnswerRevealed = true
                SoundManager.playAlert()
            }
        }
    }

    fun submitAnswer(ans: String) {
        if (isAnswerRevealed) return
        selectedOption = ans
        isAnswerRevealed = true

        val q = questions.getOrNull(currentIndex) ?: return
        val isCorrect = ans == q.correctAnswer
        if (isCorrect) {
            userScore += 10
            SoundManager.playVictory()
        } else {
            SoundManager.playAlert()
        }

        // Bot answer simulation
        if (Math.random() < 0.7) {
            botScore += 10
        }
    }

    fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            selectedOption = null
            isAnswerRevealed = false
        } else {
            isQuizFinished = true
            val isWin = userScore >= botScore
            val coins = if (isWin) (if (isOffline) 25L else 50L) else (if (isOffline) 10L else 15L)
            val xp = if (isWin) 80 else 30
            coroutineScope.launch {
                viewModel.repository.recordGameResult(isWin, isOffline, coins, xp)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SadeeqiBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowForward, contentDescription = "رجوع", tint = SadeeqiWhite)
            }
            Text(
                text = "مسابقة الأسئلة ${if (isOffline) "(بدون نت)" else "(أونلاين)"}",
                color = SadeeqiYellow,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                color = SadeeqiDarkSurface,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "$timerSeconds ث",
                    color = if (timerSeconds < 5) SadeeqiRed else SadeeqiYellow,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SadeeqiYellow)
            }
        } else if (!isQuizFinished) {
            val q = questions[currentIndex]

            // Question Progress & Scoreboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("السؤال ${currentIndex + 1} من ${questions.size}", color = SadeeqiGray, fontSize = 12.sp)
                Row {
                    Text("نقاطك: $userScore", color = SadeeqiYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("البوت: $botScore", color = SadeeqiLightGray, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, SadeeqiDarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        color = SadeeqiYellow.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = q.category,
                            color = SadeeqiYellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = q.question,
                        color = SadeeqiWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            val options = if (q.optionC.isBlank() && q.optionD.isBlank()) {
                listOf(q.optionA, q.optionB)
            } else {
                listOf(q.optionA, q.optionB, q.optionC, q.optionD).filter { it.isNotBlank() }
            }

            options.forEach { opt ->
                val isSelected = selectedOption == opt
                val isCorrect = opt == q.correctAnswer
                val buttonColor = if (isAnswerRevealed) {
                    if (isCorrect) SadeeqiOnlineGreen else if (isSelected) SadeeqiRed else SadeeqiDarkCard
                } else {
                    if (isSelected) SadeeqiYellow else SadeeqiDarkCard
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = buttonColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) SadeeqiYellow else SadeeqiDarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { submitAnswer(opt) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            color = if (isAnswerRevealed && (isCorrect || isSelected)) SadeeqiWhite else SadeeqiWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswerRevealed && isCorrect) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SadeeqiWhite)
                        } else if (isAnswerRevealed && isSelected && !isCorrect) {
                            Icon(Icons.Default.Cancel, contentDescription = null, tint = SadeeqiWhite)
                        }
                    }
                }
            }

            if (isAnswerRevealed) {
                Spacer(modifier = Modifier.height(12.dp))
                if (q.explanation.isNotBlank()) {
                    Surface(
                        color = SadeeqiDarkSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "💡 معلومة: ${q.explanation}",
                            color = SadeeqiLightGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Button(
                    onClick = { nextQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(if (currentIndex < questions.size - 1) "السؤال التالي ⬅️" else "عرض النتيجة النهائية 🏆", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Finished Quiz Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SadeeqiDarkCard),
                border = androidx.compose.foundation.BorderStroke(2.dp, SadeeqiYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (userScore >= botScore) "🎉 انتصرت في المسابقة! 🏆" else "انتهت المسابقة، حظاً أوفر! 👏",
                        color = SadeeqiYellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("مجموع نقاطك: $userScore", color = SadeeqiWhite, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text("نقاط البوت: $botScore", color = SadeeqiGray, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = SadeeqiDarkSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("تمت إضافة المكافأة إلى حسابك:", color = SadeeqiLightGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+${if (userScore >= botScore) (if (isOffline) 25 else 50) else 10} ${if (isOffline) "كوينز أوفلاين 🔴" else "Online Coins 🟡"}  |  +${if (userScore >= botScore) 80 else 30} XP ⚡",
                                color = SadeeqiYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = SadeeqiYellow, contentColor = SadeeqiBlack),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("العودة للألعاب", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
