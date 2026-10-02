package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class SadeeqiRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)

    // DAOs
    val userDao = db.userDao()
    val friendshipDao = db.friendshipDao()
    val blockDao = db.blockDao()
    val roomDao = db.roomDao()
    val messageDao = db.messageDao()
    val reactionDao = db.reactionDao()
    val storeDao = db.storeDao()
    val inventoryDao = db.inventoryDao()
    val economyDao = db.economyDao()
    val dailyRewardDao = db.dailyRewardDao()
    val dailyMissionDao = db.dailyMissionDao()
    val achievementDao = db.achievementDao()
    val questionDao = db.questionDao()
    val internalAdDao = db.internalAdDao()
    val announcementDao = db.announcementDao()
    val reportDao = db.reportDao()
    val banDao = db.banDao()
    val adminLogDao = db.adminLogDao()
    val notificationDao = db.notificationDao()

    // Current User Session State
    private val _currentUserId = MutableStateFlow<Long?>(1L) // Default logged in as admin for initial smooth exploration, or can switch
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    // Network / Offline state
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    fun setOfflineMode(offline: Boolean) {
        _isOfflineMode.value = offline
    }

    // Active User Flow
    val currentUserFlow: Flow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else userDao.getUserByIdFlow(id)
    }

    suspend fun login(emailOrUsername: String, password: String):Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(emailOrUsername) 
            ?: userDao.getUserByUsername(emailOrUsername)
        if (user == null) {
            return@withContext Result.failure(Exception("اسم المستخدم أو البريد الإلكتروني غير موجود"))
        }
        if (user.passwordHash != password) {
            return@withContext Result.failure(Exception("كلمة المرور غير صحيحة"))
        }
        if (user.isBanned) {
            return@withContext Result.failure(Exception("هذا الحساب محظور: ${user.banReason}"))
        }
        userDao.setOnlineStatus(user.id, true)
        _currentUserId.value = user.id
        Result.success(user)
    }

    suspend fun register(
        username: String,
        displayName: String,
        email: String,
        password: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            return@withContext Result.failure(Exception("يرجى ملء جميع الحقول المطلوبة"))
        }
        if (userDao.getUserByUsername(username) != null) {
            return@withContext Result.failure(Exception("اسم المستخدم مستخدم بالفعل"))
        }
        if (userDao.getUserByEmail(email) != null) {
            return@withContext Result.failure(Exception("البريد الإلكتروني مسجل بالفعل"))
        }

        val newUser = UserEntity(
            username = username.trim(),
            displayName = if (displayName.isBlank()) username.trim() else displayName.trim(),
            email = email.trim(),
            passwordHash = password,
            onlineCoins = 100, // Sign up gift
            offlineCoins = 50,
            role = if (email.contains("admin", ignoreCase = true)) "ADMIN" else "USER"
        )
        val id = userDao.insertUser(newUser)
        val inserted = userDao.getUserById(id) ?: newUser.copy(id = id)
        _currentUserId.value = id
        Result.success(inserted)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        _currentUserId.value?.let { uid ->
            userDao.setOnlineStatus(uid, false)
        }
        _currentUserId.value = null
    }

    fun switchUser(userId: Long) {
        _currentUserId.value = userId
    }

    // ECONOMY: Transfer Coins between users
    suspend fun sendCoins(
        receiverId: Long,
        amount: Long,
        reason: String = "تحويل شخصي"
    ): Result<String> = withContext(Dispatchers.IO) {
        val senderId = _currentUserId.value ?: return@withContext Result.failure(Exception("لم تقم بتسجيل الدخول"))
        if (senderId == receiverId) {
            return@withContext Result.failure(Exception("لا يمكنك إرسال كوينز لنفسك"))
        }
        if (amount <= 0) {
            return@withContext Result.failure(Exception("الكمية يجب أن تكون أكبر من صفر"))
        }

        val sender = userDao.getUserById(senderId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))
        val receiver = userDao.getUserById(receiverId) ?: return@withContext Result.failure(Exception("المستلم غير موجود"))

        if (sender.onlineCoins < amount) {
            return@withContext Result.failure(Exception("رصيدك من الـOnline Coins غير كافٍ! متاح: ${sender.onlineCoins}"))
        }

        // Check if blocked
        if (blockDao.isBlocked(senderId, receiverId)) {
            return@withContext Result.failure(Exception("لا يمكن إرسال الكوينز بسبب حظر متبادل"))
        }

        val txId = "TXN-${System.currentTimeMillis()}-${(1000..9999).random()}"

        // Atomic deduction & addition
        userDao.adjustOnlineCoins(senderId, -amount)
        userDao.adjustOnlineCoins(receiverId, amount)

        // Save transaction
        economyDao.insertTransaction(
            CoinTransactionEntity(
                transactionId = txId,
                senderId = senderId,
                senderName = sender.displayName,
                receiverId = receiverId,
                receiverName = receiver.displayName,
                amount = amount,
                currencyType = "ONLINE",
                reason = reason,
                status = "SUCCESS"
            )
        )

        // Notify receiver
        notificationDao.insertNotification(
            NotificationEntity(
                userId = receiverId,
                title = "استلمت كوينز جديدة! 💰",
                message = "أرسل لك ${sender.displayName} عدد $amount كوينز أونلاين.",
                type = "COINS"
            )
        )

        Result.success("تم إرسال $amount كوينز بنجاح! رقم المعاملة: $txId")
    }

    // STORE: Buy Item with Online Coins
    suspend fun buyStoreItem(itemKey: String): Result<String> = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value ?: return@withContext Result.failure(Exception("سجل الدخول أولاً"))
        val user = userDao.getUserById(userId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))
        val storeItem = storeDao.getStoreItemByKey(itemKey) ?: return@withContext Result.failure(Exception("العنصر غير موجود"))

        if (user.onlineCoins < storeItem.price) {
            return@withContext Result.failure(Exception("رصيدك من الـOnline Coins غير كافٍ! سعر العنصر: ${storeItem.price}"))
        }

        // Deduct coins
        userDao.adjustOnlineCoins(userId, -storeItem.price)

        // Add to inventory
        val existing = inventoryDao.getInventoryItem(userId, itemKey)
        if (existing != null) {
            inventoryDao.adjustCount(userId, itemKey, 1)
        } else {
            inventoryDao.insertOrUpdateInventory(
                InventoryItemEntity(userId = userId, itemKey = itemKey, count = 1, isEquipped = false)
            )
        }

        // Record transaction
        val txId = "BUY-${System.currentTimeMillis()}-${(100..999).random()}"
        economyDao.insertTransaction(
            CoinTransactionEntity(
                transactionId = txId,
                senderId = userId,
                senderName = user.displayName,
                receiverId = 0,
                receiverName = "متجر صديقي",
                amount = storeItem.price,
                currencyType = "ONLINE",
                reason = "شراء ${storeItem.name}",
                status = "SUCCESS"
            )
        )

        Result.success("تم شراء ${storeItem.name} بنجاح وإضافته إلى حقيبتك! 🎁")
    }

    // SEND GIFT (From inventory to friend or room)
    suspend fun sendGift(
        targetId: Long,
        conversationType: String, // "PRIVATE" or "ROOM"
        giftKey: String,
        giftName: String,
        giftIcon: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val senderId = _currentUserId.value ?: return@withContext Result.failure(Exception("سجل الدخول"))
        val sender = userDao.getUserById(senderId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))
        val inv = inventoryDao.getInventoryItem(senderId, giftKey)

        if (inv == null || inv.count <= 0) {
            return@withContext Result.failure(Exception("ليس لديك $giftName في حقيبتك! قم بشرائها من المتجر أولاً"))
        }

        // Deduct 1 gift from inventory
        inventoryDao.adjustCount(senderId, giftKey, -1)

        // Insert message
        messageDao.insertMessage(
            MessageEntity(
                conversationType = conversationType,
                targetId = targetId,
                senderId = senderId,
                senderName = sender.displayName,
                senderFrame = sender.frame,
                text = "أرسل لك هدية مميزة: $giftIcon $giftName!",
                giftType = giftKey,
                giftAmount = 1
            )
        )

        if (conversationType == "PRIVATE") {
            notificationDao.insertNotification(
                NotificationEntity(
                    userId = targetId,
                    title = "هدية جديدة! 🎁",
                    message = "أرسل لك ${sender.displayName} هدية $giftIcon $giftName!",
                    type = "GIFT"
                )
            )
        }

        // Mission progress
        recordMissionProgress(senderId, "SEND_GIFT", 1)

        Result.success("تم إرسال الهدية $giftIcon بنجاح!")
    }

    // DAILY REWARD CLAIM
    suspend fun claimDailyReward(): Result<DailyRewardEntity> = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value ?: return@withContext Result.failure(Exception("سجل الدخول"))
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        // Check if claimed today
        val claimedToday = dailyRewardDao.getClaimToday(userId, todayStr)
        if (claimedToday != null) {
            return@withContext Result.failure(Exception("لقد استلمت مكافأة اليوم بالفعل! عد غداً للمكافأة التالية."))
        }

        // Calculate streak day (1..7)
        val lastClaim = dailyRewardDao.getLastClaim(userId)
        val nextDay = if (lastClaim == null) 1 else ((lastClaim.dayNumber % 7) + 1)
        val reward = dailyRewardDao.getRewardForDay(nextDay) 
            ?: DailyRewardEntity(dayNumber = nextDay, coins = nextDay * 5L, xp = nextDay * 10)

        // Grant reward
        userDao.adjustOnlineCoins(userId, reward.coins)
        addXp(userId, reward.xp)

        // Record claim
        dailyRewardDao.insertClaim(
            DailyRewardClaimEntity(
                userId = userId,
                dayNumber = nextDay,
                claimDate = todayStr
            )
        )

        // Notification
        notificationDao.insertNotification(
            NotificationEntity(
                userId = userId,
                title = "مكافأة يومية 🌟",
                message = "مبروك! استلمت مكافأة اليوم $nextDay: +${reward.coins} كوينز و +${reward.xp} XP",
                type = "DAILY_REWARD"
            )
        )

        Result.success(reward)
    }

    // XP and Level Management
    suspend fun addXp(userId: Long, xpToAdd: Int) {
        val user = userDao.getUserById(userId) ?: return
        val newXp = user.xp + xpToAdd
        // Level calculation: Every level requires level * 500 XP
        val newLevel = maxOf(1, (newXp / 500) + 1)
        userDao.addXpAndSetLevel(userId, xpToAdd, newLevel)
    }

    // GAME RESULT AWARD
    suspend fun recordGameResult(
        isWin: Boolean,
        isOffline: Boolean,
        coinsWon: Long,
        xpEarned: Int
    ) = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value ?: return@withContext
        if (isOffline) {
            // Strictly Offline Coins
            userDao.adjustOfflineCoins(userId, coinsWon)
            addXp(userId, xpEarned / 2) // Half XP for offline games
        } else {
            // Online Coins
            userDao.adjustOnlineCoins(userId, coinsWon)
            addXp(userId, xpEarned)
        }

        if (isWin) {
            userDao.incrementWin(userId)
            recordMissionProgress(userId, "WIN_GAMES", 1)
        } else {
            userDao.incrementGamesPlayed(userId)
        }

        recordMissionProgress(userId, "PLAY_GAMES", 1)
    }

    // MISSION PROGRESS
    private suspend fun recordMissionProgress(userId: Long, missionType: String, delta: Int) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val missions = dailyMissionDao.getAllMissionsFlow().first().filter { it.missionType == missionType && it.active }
        for (m in missions) {
            val prog = dailyMissionDao.getProgress(userId, m.id, todayStr)
            val current = (prog?.currentCount ?: 0) + delta
            val completed = current >= m.targetCount
            val isClaimed = prog?.isClaimed ?: false

            dailyMissionDao.insertOrUpdateProgress(
                UserMissionProgressEntity(
                    id = prog?.id ?: 0,
                    userId = userId,
                    missionId = m.id,
                    currentCount = current,
                    isCompleted = completed,
                    isClaimed = isClaimed,
                    dateKey = todayStr
                )
            )
        }
    }

    // CLAIM MISSION REWARD
    suspend fun claimMissionReward(missionId: Long): Result<String> = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value ?: return@withContext Result.failure(Exception("سجل الدخول"))
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val prog = dailyMissionDao.getProgress(userId, missionId, todayStr) 
            ?: return@withContext Result.failure(Exception("لم تبدأ هذه المهمة بعد"))
        val mission = dailyMissionDao.getAllMissionsFlow().first().find { it.id == missionId }
            ?: return@withContext Result.failure(Exception("المهمة غير موجودة"))

        if (!prog.isCompleted) {
            return@withContext Result.failure(Exception("لم تكتمل المهمة بعد (${prog.currentCount}/${mission.targetCount})"))
        }
        if (prog.isClaimed) {
            return@withContext Result.failure(Exception("تم استلام مكافأة هذه المهمة بالفعل اليوم"))
        }

        // Update claimed
        dailyMissionDao.insertOrUpdateProgress(prog.copy(isClaimed = true))
        userDao.adjustOnlineCoins(userId, mission.rewardCoins)
        addXp(userId, mission.rewardXp)

        Result.success("تم استلام +${mission.rewardCoins} كوينز و +${mission.rewardXp} XP بنجاح!")
    }

    // ADMIN ACTIONS
    suspend fun adminModifyCoins(
        adminId: Long,
        targetUserId: Long,
        amount: Long, // positive or negative
        reason: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val admin = userDao.getUserById(adminId) ?: return@withContext Result.failure(Exception("غير مصرح"))
        if (admin.role != "ADMIN") return@withContext Result.failure(Exception("ليس لديك صلاحيات الأدمن"))

        val target = userDao.getUserById(targetUserId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))
        if (amount < 0 && target.onlineCoins + amount < 0) {
            return@withContext Result.failure(Exception("لا يمكن خصم مبلغ يجعل رصيد المستخدم سالباً! متاح: ${target.onlineCoins}"))
        }

        val beforeVal = "${target.onlineCoins}"
        userDao.adjustOnlineCoins(targetUserId, amount)
        val afterTarget = userDao.getUserById(targetUserId)
        val afterVal = "${afterTarget?.onlineCoins ?: 0}"

        // Log admin action
        adminLogDao.insertLog(
            AdminLogEntity(
                adminId = adminId,
                adminName = admin.displayName,
                action = if (amount >= 0) "إضافة كوينز" else "خصم كوينز",
                targetUser = target.username,
                details = "المبلغ: $amount",
                beforeVal = beforeVal,
                afterVal = afterVal,
                reason = reason
            )
        )

        Result.success("تم تعديل رصيد ${target.displayName} بنجاح! الرصيد الحالي: $afterVal")
    }

    suspend fun adminBanUser(
        adminId: Long,
        targetUserId: Long,
        reason: String,
        isPermanent: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        val admin = userDao.getUserById(adminId) ?: return@withContext Result.failure(Exception("غير مصرح"))
        if (admin.role != "ADMIN") return@withContext Result.failure(Exception("ليس لديك صلاحيات الأدمن"))
        val target = userDao.getUserById(targetUserId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))

        userDao.setBanStatus(targetUserId, true, reason)
        banDao.insertBan(
            BanEntity(
                userId = targetUserId,
                userEmail = target.email,
                userName = target.displayName,
                reason = reason,
                adminName = admin.displayName,
                isPermanent = isPermanent,
                status = "ACTIVE"
            )
        )

        adminLogDao.insertLog(
            AdminLogEntity(
                adminId = adminId,
                adminName = admin.displayName,
                action = "حظر مستخدم",
                targetUser = target.username,
                details = "حظر ${if (isPermanent) "نهائي" else "مؤقت"}",
                reason = reason
            )
        )

        Result.success("تم حظر المستخدم ${target.displayName} بنجاح")
    }

    suspend fun adminUnbanUser(adminId: Long, targetUserId: Long): Result<String> = withContext(Dispatchers.IO) {
        val admin = userDao.getUserById(adminId) ?: return@withContext Result.failure(Exception("غير مصرح"))
        if (admin.role != "ADMIN") return@withContext Result.failure(Exception("ليس لديك صلاحيات الأدمن"))
        val target = userDao.getUserById(targetUserId) ?: return@withContext Result.failure(Exception("المستخدم غير موجود"))

        userDao.setBanStatus(targetUserId, false, "")
        adminLogDao.insertLog(
            AdminLogEntity(
                adminId = adminId,
                adminName = admin.displayName,
                action = "فك حظر مستخدم",
                targetUser = target.username,
                details = "تم رفع الحظر",
                reason = "عفو إداري"
            )
        )

        Result.success("تم فك حظر ${target.displayName} بنجاح")
    }

    // Equip item (frame, badge, sound, effect)
    suspend fun equipItem(userId: Long, itemKey: String, type: String) = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: return@withContext
        when (type) {
            "FRAME" -> userDao.updateUser(user.copy(frame = if (itemKey == "FRAME_ROYAL") "ROYAL" else if (itemKey == "FRAME_FIRE") "FIRE" else "NONE"))
            "SOUND" -> userDao.updateUser(user.copy(entrySound = if (itemKey == "SOUND_VIP") "VIP" else "DEFAULT"))
            "EFFECT" -> userDao.updateUser(user.copy(chatEffect = if (itemKey == "EFFECT_GOLD") "GOLD_SPARKLE" else if (itemKey == "EFFECT_FIRE") "FIRE" else "NONE"))
        }
    }
}
