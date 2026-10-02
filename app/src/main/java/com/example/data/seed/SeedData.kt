package com.example.data.seed

import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SeedData {
    suspend fun populateInitialDataIfEmpty(db: AppDatabase) = withContext(Dispatchers.IO) {
        val userDao = db.userDao()
        if (userDao.getAllUsers().isNotEmpty()) {
            return@withContext
        }

        // 1. Seed Users (Admin + Community Users)
        val admin = UserEntity(
            id = 1,
            username = "admin",
            displayName = "إدارة صديقي",
            email = "admin@sadeeqi.app",
            passwordHash = "admin123",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            level = 10,
            xp = 5000,
            onlineCoins = 99999,
            offlineCoins = 1000,
            wins = 88,
            gamesPlayed = 120,
            frame = "ROYAL",
            badge = "ADMIN_BADGE",
            entrySound = "ROYAL",
            chatEffect = "GOLD_SPARKLE",
            role = "ADMIN"
        )
        val user1 = UserEntity(
            id = 2,
            username = "ahmed_hero",
            displayName = "أحمد البطل ⚡",
            email = "ahmed@sadeeqi.app",
            passwordHash = "123456",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
            level = 6,
            xp = 2400,
            onlineCoins = 450,
            offlineCoins = 120,
            wins = 24,
            gamesPlayed = 40,
            frame = "GOLDEN",
            badge = "CHAMPION",
            entrySound = "VIP",
            chatEffect = "FIRE"
        )
        val user2 = UserEntity(
            id = 3,
            username = "sara_gamer",
            displayName = "سارة المحترفة 👑",
            email = "sara@sadeeqi.app",
            passwordHash = "123456",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330",
            level = 8,
            xp = 3800,
            onlineCoins = 820,
            offlineCoins = 200,
            wins = 45,
            gamesPlayed = 70,
            frame = "ROYAL",
            badge = "QUEEN",
            entrySound = "ROYAL",
            chatEffect = "GOLD_SPARKLE"
        )
        val user3 = UserEntity(
            id = 4,
            username = "karim_joker",
            displayName = "كريم جوكر 🃏",
            email = "karim@sadeeqi.app",
            passwordHash = "123456",
            avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61",
            level = 4,
            xp = 1100,
            onlineCoins = 220,
            offlineCoins = 80,
            wins = 12,
            gamesPlayed = 25,
            frame = "NONE",
            badge = "FRIENDLY",
            isOnline = false
        )
        val user4 = UserEntity(
            id = 5,
            username = "yasmin_star",
            displayName = "ياسمين روز 🌸",
            email = "yasmin@sadeeqi.app",
            passwordHash = "123456",
            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
            level = 5,
            xp = 1900,
            onlineCoins = 380,
            offlineCoins = 150,
            wins = 19,
            gamesPlayed = 35,
            frame = "FIRE",
            badge = "POPULAR"
        )

        userDao.insertUser(admin)
        userDao.insertUser(user1)
        userDao.insertUser(user2)
        userDao.insertUser(user3)
        userDao.insertUser(user4)

        // 2. Friendships
        val friendshipDao = db.friendshipDao()
        friendshipDao.insertFriendship(FriendshipEntity(userId = 1, friendId = 2, status = "ACCEPTED", requesterId = 2))
        friendshipDao.insertFriendship(FriendshipEntity(userId = 1, friendId = 3, status = "ACCEPTED", requesterId = 3))
        friendshipDao.insertFriendship(FriendshipEntity(userId = 1, friendId = 5, status = "PENDING", requesterId = 5))

        // 3. Store Items
        val storeDao = db.storeDao()
        val storeItems = listOf(
            StoreItemEntity(itemKey = "GIFT_ROSE_20", name = "20 وردة حمراء", type = "GIFT", price = 30, icon = "🌹", description = "أرسل باقة ورود حمراء لأصدقائك في الدردشة والقعدات"),
            StoreItemEntity(itemKey = "GIFT_HEART_10", name = "10 قلوب دافئة", type = "GIFT", price = 25, icon = "❤️", description = "عبر عن محبتك بإرسال قلوب مميزة للغرف والمحادثات"),
            StoreItemEntity(itemKey = "GIFT_CROWN_5", name = "5 تيجان ملوكية", type = "GIFT", price = 50, icon = "👑", description = "هدية الفخامة والملوك، هدية استثنائية لأعز أصدقائك"),
            StoreItemEntity(itemKey = "SOUND_VIP", name = "صوت دخول VIP", type = "SOUND", price = 30, icon = "🔊", description = "مؤثر صوتي رنان يعلن دخولك المميز إلى كل قعدة"),
            StoreItemEntity(itemKey = "FRAME_ROYAL", name = "إطار العرش الملوكي", type = "FRAME", price = 100, icon = "👑", description = "إطار ذهبي فخم يزين صورتك الشخصية في كل مكان"),
            StoreItemEntity(itemKey = "FRAME_FIRE", name = "إطار اللهب الناري", type = "FRAME", price = 80, icon = "🔥", description = "إطار مشتعل بالحماس والروح التنافسية العالية"),
            StoreItemEntity(itemKey = "EFFECT_GOLD", name = "تأثير النجوم الذهبية", type = "EFFECT", price = 50, icon = "✨", description = "تأثير بريق ذهبي ساحر يرافق جميع رسائلك في الشات"),
            StoreItemEntity(itemKey = "EFFECT_FIRE", name = "تأثير وهج النار", type = "EFFECT", price = 40, icon = "🔥", description = "تأثير ناري حماسي يبرز رسائلك بقوة")
        )
        storeItems.forEach { storeDao.insertStoreItem(it) }

        // 4. Initial Inventory for Admin and Users
        val inventoryDao = db.inventoryDao()
        inventoryDao.insertOrUpdateInventory(InventoryItemEntity(userId = 1, itemKey = "GIFT_ROSE_20", count = 50))
        inventoryDao.insertOrUpdateInventory(InventoryItemEntity(userId = 1, itemKey = "GIFT_HEART_10", count = 30))
        inventoryDao.insertOrUpdateInventory(InventoryItemEntity(userId = 1, itemKey = "GIFT_CROWN_5", count = 20))
        inventoryDao.insertOrUpdateInventory(InventoryItemEntity(userId = 1, itemKey = "FRAME_ROYAL", count = 1, isEquipped = true))
        inventoryDao.insertOrUpdateInventory(InventoryItemEntity(userId = 1, itemKey = "SOUND_VIP", count = 1, isEquipped = true))
        inventoryDao.insertOrUpdateInventory(InventoryItemEntity(userId = 1, itemKey = "EFFECT_GOLD", count = 1, isEquipped = true))

        // 5. Daily Rewards Config
        val dailyRewardDao = db.dailyRewardDao()
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 1, coins = 5, xp = 10))
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 2, coins = 10, xp = 20))
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 3, coins = 15, xp = 30))
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 4, coins = 20, xp = 40))
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 5, coins = 25, xp = 50))
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 6, coins = 30, xp = 60))
        dailyRewardDao.insertDailyReward(DailyRewardEntity(dayNumber = 7, coins = 50, xp = 100))

        // 6. Daily Missions
        val missionDao = db.dailyMissionDao()
        val missions = listOf(
            DailyMissionEntity(title = "العب 3 ألعاب", description = "شارك في 3 ألعاب أونلاين أو بدون إنترنت", targetCount = 3, rewardCoins = 25, rewardXp = 50, missionType = "PLAY_GAMES"),
            DailyMissionEntity(title = "العب مع صديق", description = "انضم إلى قعدة والعب مع صديق متصل", targetCount = 1, rewardCoins = 20, rewardXp = 40, missionType = "PLAY_WITH_FRIEND"),
            DailyMissionEntity(title = "أرسل 10 رسائل", description = "تفاعل في المحادثات الخاصة أو القعدات", targetCount = 10, rewardCoins = 15, rewardXp = 30, missionType = "SEND_MESSAGES"),
            DailyMissionEntity(title = "اربح مباراتين", description = "حقق المركز الأول أو الفوز مرتين", targetCount = 2, rewardCoins = 35, rewardXp = 70, missionType = "WIN_GAMES"),
            DailyMissionEntity(title = "أرسل هدية", description = "أسعد صديقاً بهدية ورود أو قلوب", targetCount = 1, rewardCoins = 20, rewardXp = 30, missionType = "SEND_GIFT")
        )
        missions.forEach { missionDao.insertMission(it) }

        // 7. Achievements
        val achievementDao = db.achievementDao()
        val achievements = listOf(
            AchievementEntity(code = "FIRST_WIN", title = "🏆 أول فوز", description = "حقق فوزك الأول في أي لعبة", requirementCount = 1, rewardCoins = 50, rewardXp = 100, badgeCode = "BADGE_FIRST_WIN"),
            AchievementEntity(code = "PLAY_10", title = "🎮 العب 10 ألعاب", description = "خض 10 مباريات حماسية", requirementCount = 10, rewardCoins = 100, rewardXp = 200, badgeCode = "BADGE_GAMER"),
            AchievementEntity(code = "WIN_50", title = "👑 اربح 50 لعبة", description = "أثبت براعتك وتوج بطلاً في 50 مباراة", requirementCount = 50, rewardCoins = 500, rewardXp = 1000, badgeCode = "BADGE_ROYAL"),
            AchievementEntity(code = "SEND_100_MSG", title = "💬 أرسل 100 رسالة", description = "شارك وتحدث بـ 100 رسالة في التطبيق", requirementCount = 100, rewardCoins = 80, rewardXp = 150, badgeCode = "BADGE_CHATTER"),
            AchievementEntity(code = "SEND_20_GIFTS", title = "🎁 أرسل 20 هدية", description = "وزع الفرح وأرسل 20 هدية لأصدقائك", requirementCount = 20, rewardCoins = 150, rewardXp = 300, badgeCode = "BADGE_GENEROUS"),
            AchievementEntity(code = "ADD_10_FRIENDS", title = "👥 أضف 10 أصدقاء", description = "كوّن صداقات جديدة وأضف 10 أصدقاء", requirementCount = 10, rewardCoins = 100, rewardXp = 200, badgeCode = "BADGE_SOCIAL")
        )
        achievements.forEach { achievementDao.insertAchievement(it) }

        // 8. Rooms
        val roomDao = db.roomDao()
        val r1 = roomDao.insertRoom(
            RoomEntity(
                title = "قعدة الضحك والوناسة 🎤",
                description = "مرحباً بالجميع! مسابقات وسوالف وتحديات ألعاب خفيفة.",
                code = "1001",
                ownerId = 2,
                ownerName = "أحمد البطل ⚡",
                currentPlayersCount = 3,
                maxPlayers = 10,
                activeGame = "أتوبيس كومبليت"
            )
        )
        val r2 = roomDao.insertRoom(
            RoomEntity(
                title = "تحدي الأذكياء والأسئلة العامة 🧠",
                description = "قعدة معلومات عامة وتحدي سرعة بين المحترفين.",
                code = "1002",
                ownerId = 3,
                ownerName = "سارة المحترفة 👑",
                currentPlayersCount = 4,
                maxPlayers = 8,
                activeGame = "أسئلة عامة"
            )
        )
        roomDao.insertMember(RoomMemberEntity(roomId = r1, userId = 2, userName = "أحمد البطل ⚡", userFrame = "GOLDEN"))
        roomDao.insertMember(RoomMemberEntity(roomId = r1, userId = 4, userName = "كريم جوكر 🃏", userFrame = "NONE"))
        roomDao.insertMember(RoomMemberEntity(roomId = r2, userId = 3, userName = "سارة المحترفة 👑", userFrame = "ROYAL"))
        roomDao.insertMember(RoomMemberEntity(roomId = r2, userId = 5, userName = "ياسمين روز 🌸", userFrame = "FIRE"))

        // 9. Messages
        val messageDao = db.messageDao()
        val m1 = messageDao.insertMessage(
            MessageEntity(
                conversationType = "PRIVATE",
                targetId = 1,
                senderId = 2,
                senderName = "أحمد البطل ⚡",
                senderFrame = "GOLDEN",
                text = "يا هلا ومية مرحبا في صديقي! جاهز لتحدي أتوبيس كومبليت اليوم؟ 🚌",
                effect = "FIRE",
                timestamp = System.currentTimeMillis() - 3600000
            )
        )
        messageDao.insertMessage(
            MessageEntity(
                conversationType = "PRIVATE",
                targetId = 2,
                senderId = 1,
                senderName = "إدارة صديقي",
                senderFrame = "ROYAL",
                text = "أهلاً يا بطل! بالتأكيد، جهز نفسك لأسئلة الحروف الصعبة! 🎯",
                effect = "GOLD_SPARKLE",
                timestamp = System.currentTimeMillis() - 1800000
            )
        )

        // Room messages
        messageDao.insertMessage(
            MessageEntity(
                conversationType = "ROOM",
                targetId = r1,
                senderId = 2,
                senderName = "أحمد البطل ⚡",
                senderFrame = "GOLDEN",
                text = "نورتوا القعدة يا شباب! اللعبة هتبدأ كمان شوية!",
                timestamp = System.currentTimeMillis() - 1200000
            )
        )

        // Message reactions
        val reactionDao = db.reactionDao()
        reactionDao.insertReaction(MessageReactionEntity(messageId = m1, userId = 1, userName = "إدارة صديقي", reactionEmoji = "🔥"))
        reactionDao.insertReaction(MessageReactionEntity(messageId = m1, userId = 3, userName = "سارة", reactionEmoji = "👍"))

        // 10. Ads
        val adDao = db.internalAdDao()
        adDao.insertAd(
            InternalAdEntity(
                title = "🏆 بطولة أبطال صديقي للألعاب",
                description = "تنافس في ألعاب أتوبيس كومبليت والأسئلة واحصل على جوائز تصل إلى 5,000 كوينز!",
                buttonText = "سجل الآن مجاناً",
                linkUrl = "championship",
                sortOrder = 1,
                isActive = true
            )
        )
        adDao.insertAd(
            InternalAdEntity(
                title = "✨ متجر الهدايا والإطارات الملوكية",
                description = "جدد مظهرك في القعدات والدردشة مع إطار العرش الملوكي وتأثيرات الشات الذهبية!",
                buttonText = "تصفح المتجر",
                linkUrl = "store",
                sortOrder = 2,
                isActive = true
            )
        )

        // 11. Announcements
        val announcementDao = db.announcementDao()
        announcementDao.insertAnnouncement(
            AnnouncementEntity(
                title = "🎉 انطلاق النسخة الرسمية لتطبيق صديقي!",
                content = "أهلاً بكم في مجتمع صديقي للألعاب والتواصل، استمتعوا باللعب مع أصدقائكم أو بدون إنترنت مع البوتات الذكية!",
                date = "اليوم"
            )
        )

        // 12. Questions Bank
        val questionDao = db.questionDao()

        // Questions for General Knowledge (أسئلة عامة)
        val generalQuestions = listOf(
            QuestionEntity(gameType = "GENERAL", category = "تاريخ وجغرافيا", question = "ما هي عاصمة دولة مصر؟", optionA = "الإسكندرية", optionB = "القاهرة", optionC = "الجيزة", optionD = "أسوان", correctAnswer = "القاهرة", explanation = "القاهرة هي العاصمة الرسمية والسياسية لجمهورية مصر العربية."),
            QuestionEntity(gameType = "GENERAL", category = "علوم وطبيعة", question = "ما هو أطول نهر في العالم؟", optionA = "نهر النيل", optionB = "نهر الأمازون", optionC = "نهر الدانوب", optionD = "نهر الفرات", correctAnswer = "نهر النيل", explanation = "نهر النيل في قارة أفريقيا يعد أطول نهر عالمياً."),
            QuestionEntity(gameType = "GENERAL", category = "فلك وفضاء", question = "ما هو أقرب كوكب إلى الشمس؟", optionA = "الزهرة", optionB = "عطارد", optionC = "المريخ", optionD = "الأرض", correctAnswer = "عطارد", explanation = "عطارد هو الكوكب الأصغر والأقرب للشمس."),
            QuestionEntity(gameType = "GENERAL", category = "لغة وثقافة", question = "كم عدد حروف اللغة العربية؟", optionA = "26 حرفاً", optionB = "28 حرفاً", optionC = "30 حرفاً", optionD = "24 حرفاً", correctAnswer = "28 حرفاً", explanation = "تتكون الأبجدية العربية من 28 حرفاً أصلياً."),
            QuestionEntity(gameType = "GENERAL", category = "تاريخ إسلامي", question = "من هو أول الخلفاء الراشدين؟", optionA = "عمر بن الخطاب", optionB = "أبو بكر الصديق", optionC = "عثمان بن عفان", optionD = "علي بن أبي طالب", correctAnswer = "أبو بكر الصديق", explanation = "تولى أبو بكر الصديق الخلافة بعد وفاة النبي صلى الله عليه وسلم.")
        )
        generalQuestions.forEach { questionDao.insertQuestion(it) }

        // Questions for True/False (صح أم خطأ)
        val tfQuestions = listOf(
            QuestionEntity(gameType = "TRUE_FALSE", category = "علوم", question = "الحوت الأزرق هو أكبر كائن حي يعيش على كوكب الأرض.", optionA = "صح", optionB = "خطأ", correctAnswer = "صح", explanation = "الحوت الأزرق يعد أضخم حيوان معروف وجد على الأرض عبر العصور."),
            QuestionEntity(gameType = "TRUE_FALSE", category = "جغرافيا", question = "سور الصين العظيم يمكن رؤيته بوضوح من سطح القمر بالعين المجردة.", optionA = "صح", optionB = "خطأ", correctAnswer = "خطأ", explanation = "هذه أسطورة شائعة، حيث لا يمكن تمييزه من الفضاء دون أجهزة تكبير."),
            QuestionEntity(gameType = "TRUE_FALSE", category = "طبيعة", question = "الضوء يتحرك أسرع بكثير من الصوت.", optionA = "صح", optionB = "خطأ", correctAnswer = "صح", explanation = "سرعة الضوء حوالي 300,000 كم/ث بينما الصوت حوالي 343 م/ث في الهواء."),
            QuestionEntity(gameType = "TRUE_FALSE", category = "تاريخ", question = "تم بناء أهرامات الجيزة في عصر الدولة الحديثة بمصر القديمة.", optionA = "صح", optionB = "خطأ", correctAnswer = "خطأ", explanation = "شيدت أهرامات الجيزة في عهد الدولة القديمة (الأسرة الرابعة).")
        )
        tfQuestions.forEach { questionDao.insertQuestion(it) }

        // Fast Reflexes / Math (أسرع إجابة)
        val fastQuestions = listOf(
            QuestionEntity(gameType = "FAST", category = "حساب سريع", question = "ما حاصل: 12 × 4 + 8 ؟", optionA = "52", optionB = "56", optionC = "60", optionD = "48", correctAnswer = "56", explanation = "12 × 4 = 48 + 8 = 56."),
            QuestionEntity(gameType = "FAST", category = "حساب سريع", question = "نصف الرقم 150 مخصوماً منه 25 يساوي:", optionA = "40", optionB = "50", optionC = "60", optionD = "75", correctAnswer = "50", explanation = "75 - 25 = 50."),
            QuestionEntity(gameType = "FAST", category = "بديهة سريعة", question = "كم عدد الأيام في 7 أسابيع كاملة؟", optionA = "42", optionB = "49", optionC = "56", optionD = "35", correctAnswer = "49", explanation = "7 × 7 = 49 يوماً.")
        )
        fastQuestions.forEach { questionDao.insertQuestion(it) }

        // Funny Riddles (أسئلة مضحكة وخفيفة)
        val funnyQuestions = listOf(
            QuestionEntity(gameType = "FUNNY", category = "فوازير", question = "ما هو الشيء الذي يمشي ويقف وليس له أرجل؟", optionA = "الساعة", optionB = "القطار", optionC = "السيارة", optionD = "الماء", correctAnswer = "الساعة", explanation = "الساعة تعمل وتقف بعقاربها دون أن تمشي على أرجل!"),
            QuestionEntity(gameType = "FUNNY", category = "فوازير", question = "شيء كلما أخذت منه كَبُر وزاد حجمه؟", optionA = "الحفرة", optionB = "العمر", optionC = "البحر", optionD = "الجبل", correctAnswer = "الحفرة", explanation = "الحفرة كلما رفعت منها تراباً زاد اتساعها!"),
            QuestionEntity(gameType = "FUNNY", category = "فوازير", question = "ما هو الشيء الذي يتحدث جميع لغات العالم دون أن يدرسها؟", optionA = "صدى الصوت", optionB = "الراديو", optionC = "الهاتف", optionD = "المترجم", correctAnswer = "صدى الصوت", explanation = "صدى الصوت يردد كل ما تقوله بأي لغة كانت!")
        )
        funnyQuestions.forEach { questionDao.insertQuestion(it) }

        // Who am I (مين أنا؟)
        val whoAmIQuestions = listOf(
            QuestionEntity(gameType = "WHO_AM_I", category = "شخصيات", question = "رحالة ومستكشف عربي شهير ولد في طنجة وقضى ثلاثين عاماً في السفر وطاف بلاد المغرب والشرق وأفريقيا، من أنا؟", optionA = "ابن بطوطة", optionB = "ابن خلدون", optionC = "ابن سينا", optionD = "الإدريسي", correctAnswer = "ابن بطوطة", explanation = "ابن بطوطة صاحب كتاب تحفة النظار في غرائب الأمصار."),
            QuestionEntity(gameType = "WHO_AM_I", category = "شخصيات", question = "طبيب وفيلسوف مسلم لُقب بالشيخ الرئيس وألف كتاب 'القانون في الطب'، من أنا؟", optionA = "ابن سينا", optionB = "الرازي", optionC = "الفارابي", optionD = "ابن الهيثم", correctAnswer = "ابن سينا", explanation = "ابن سينا ألف كتاب القانون في الطب الذي درس لقرون بأوروبا.")
        )
        whoAmIQuestions.forEach { questionDao.insertQuestion(it) }

        // Who is the liar (مين الكذاب)
        val whoIsLiar = listOf(
            QuestionEntity(gameType = "WHO_IS_LIAR", category = "كشف الكذب", question = "ثلاث حقائق وشائعة واحدة كاذبة، اكتشف الشائعة الكاذبة:", optionA = "الفيل لا يستطيع القفز", optionB = "النعامة تدفن رأسها خوفاً في الرمال", optionC = "النمل لا ينام أبداً نوماً عميقاً", optionD = "قلب الجمبري يقع في رأسه", correctAnswer = "النعامة تدفن رأسها خوفاً في الرمال", explanation = "النعامة لا تدفن رأسها خوفاً بل تضعه قرب الأرض لتقليب بيضها وتفحص الخطر!"),
            QuestionEntity(gameType = "WHO_IS_LIAR", category = "كشف الكذب", question = "أي من هذه العبارات غير صحيحة (كذبة)؟", optionA = "الذهب لا يصدأ أبداً", optionB = "الموز ينمو على أشجار خشبية صلبة", optionC = "الماء يغلي عند 100 مئوية بمستوى سطح البحر", optionD = "الماس أصلب مادة طبيعية", correctAnswer = "الموز ينمو على أشجار خشبية صلبة", explanation = "الموز ينمو على نباتات عشبية عملاقة وليس أشجاراً خشبية!")
        )
        whoIsLiar.forEach { questionDao.insertQuestion(it) }

        // Guess word (خمن الكلمة)
        val guessWord = listOf(
            QuestionEntity(gameType = "GUESS_WORD", category = "ألعاب لغوية", question = "رتب الحروف التالية لتكوين اسم عاصمة عربية: [ر - ق - ه - ا - ا - ل]", optionA = "القاهرة", optionB = "الرياض", optionC = "الدوحة", optionD = "المنامة", correctAnswer = "القاهرة", explanation = "الحروف تكون كلمة: القاهرة"),
            QuestionEntity(gameType = "GUESS_WORD", category = "ألعاب لغوية", question = "حيوان سريع جداً من 5 حروف يبدأ بحرف الفاء:", optionA = "الفهد", optionB = "الفيل", optionC = "الفأر", optionD = "الفقمة", correctAnswer = "الفهد", explanation = "الفهد أسرع حيوان بري على وجه الأرض.")
        )
        guessWord.forEach { questionDao.insertQuestion(it) }

        // 13. Notifications for user
        val notificationDao = db.notificationDao()
        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "مكافأة التسجيل والترحيب 🎁",
                message = "حصلت على 100 كوينز أونلاين و 50 كوينز أوفلاين كهدية ترحيبية!",
                type = "COINS"
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                userId = 1,
                title = "طلب صداقة جديد 👥",
                message = "أرسلت ياسمين روز طلباً لمصادقتك في تطبيق صديقي.",
                type = "FRIEND_REQ"
            )
        )
    }
}
