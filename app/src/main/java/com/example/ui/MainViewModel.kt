package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.SadeeqiRepository
import com.example.data.seed.SeedData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = SadeeqiRepository(application)
    private val db = AppDatabase.getInstance(application)

    init {
        viewModelScope.launch {
            SeedData.populateInitialDataIfEmpty(db)
        }
    }

    // State flows
    val currentUser = repository.currentUserFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val isOfflineMode = repository.isOfflineMode

    val activeAds = repository.internalAdDao.getActiveAdsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val announcements = repository.announcementDao.getActiveAnnouncementsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allUsers = repository.userDao.getAllUsersFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val activeRooms = repository.roomDao.getActiveRoomsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val storeItems = repository.storeDao.getActiveStoreItemsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val userInventory = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.inventoryDao.getUserInventoryFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.economyDao.getUserTransactionsFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyRewards = repository.dailyRewardDao.getDailyRewardsConfigFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val dailyMissions = repository.dailyMissionDao.getActiveMissionsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val userMissionsProgress = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            repository.dailyMissionDao.getUserMissionProgressFlow(user.id, todayStr)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements = repository.achievementDao.getAllAchievementsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val userAchievements = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.achievementDao.getUserAchievementsFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(0)
        else repository.notificationDao.getUnreadNotificationsCountFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notifications = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.notificationDao.getUserNotificationsFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friendsList = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.friendshipDao.getAcceptedFriendshipsFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingFriendRequests = currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList())
        else repository.friendshipDao.getPendingRequestsFlow(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin data flows
    val allTransactions = repository.economyDao.getAllTransactionsFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val adminLogs = repository.adminLogDao.getAllLogsFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allReports = repository.reportDao.getAllReportsFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allBans = repository.banDao.getAllBansFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allQuestions = repository.questionDao.getAllQuestionsFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val allAds = repository.internalAdDao.getAllAdsFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // UI Feedback Message
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    fun showMessage(msg: String) {
        _uiMessage.value = msg
    }

    fun clearMessage() {
        _uiMessage.value = null
    }

    fun toggleOfflineMode() {
        val next = !repository.isOfflineMode.value
        repository.setOfflineMode(next)
        SoundManager.playAlert()
        showMessage(if (next) "تم التحويل إلى وضع اللعب بدون إنترنت 🔴" else "تمت العودة إلى الوضع المتصل أونلاين 🟢")
    }

    fun switchUser(userId: Long) {
        repository.switchUser(userId)
        SoundManager.playClick()
    }

    fun login(emailOrUsername: String, pass: String) {
        viewModelScope.launch {
            val res = repository.login(emailOrUsername, pass)
            res.onSuccess {
                SoundManager.playVictory()
                showMessage("أهلاً بك مجدداً يا ${it.displayName}! 👋")
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "خطأ في تسجيل الدخول")
            }
        }
    }

    fun register(username: String, displayName: String, email: String, pass: String) {
        viewModelScope.launch {
            val res = repository.register(username, displayName, email, pass)
            res.onSuccess {
                SoundManager.playVictory()
                showMessage("تم إنشاء الحساب بنجاح! مرحباً بك في صديقي 🌟")
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "فشل إنشاء الحساب")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            showMessage("تم تسجيل الخروج بنجاح")
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            val res = repository.claimDailyReward()
            res.onSuccess {
                SoundManager.playCoins()
                showMessage("تم استلام مكافأة اليوم بنجاح: +${it.coins} كوينز و +${it.xp} XP! 🎁")
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "فشل استلام المكافأة")
            }
        }
    }

    fun claimMission(missionId: Long) {
        viewModelScope.launch {
            val res = repository.claimMissionReward(missionId)
            res.onSuccess {
                SoundManager.playCoins()
                showMessage(it)
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "فشل استلام مكافأة المهمة")
            }
        }
    }

    fun buyStoreItem(itemKey: String) {
        viewModelScope.launch {
            val res = repository.buyStoreItem(itemKey)
            res.onSuccess {
                SoundManager.playCoins()
                showMessage(it)
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "فشل الشراء")
            }
        }
    }

    fun sendCoins(receiverId: Long, amount: Long, reason: String) {
        viewModelScope.launch {
            val res = repository.sendCoins(receiverId, amount, reason)
            res.onSuccess {
                SoundManager.playCoins()
                showMessage(it)
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "فشل إرسال الكوينز")
            }
        }
    }

    fun equipItem(itemKey: String, type: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.equipItem(user.id, itemKey, type)
            SoundManager.playClick()
            showMessage("تم تفعيل العنصر بنجاح! ✨")
        }
    }

    // Room operations
    fun createRoom(title: String, desc: String, isPrivate: Boolean, code: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val newRoom = RoomEntity(
                title = title,
                description = desc,
                isPrivate = isPrivate,
                code = code,
                ownerId = user.id,
                ownerName = user.displayName,
                currentPlayersCount = 1
            )
            val roomId = repository.roomDao.insertRoom(newRoom)
            repository.roomDao.insertMember(
                RoomMemberEntity(
                    roomId = roomId,
                    userId = user.id,
                    userName = user.displayName,
                    userFrame = user.frame
                )
            )
            SoundManager.playVictory()
            showMessage("تم إنشاء القعدة بنجاح! 🎤")
        }
    }

    fun joinRoom(roomId: Long) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val room = repository.roomDao.getRoomById(roomId) ?: return@launch
            repository.roomDao.insertMember(
                RoomMemberEntity(
                    roomId = roomId,
                    userId = user.id,
                    userName = user.displayName,
                    userFrame = user.frame
                )
            )
            repository.roomDao.updateRoom(room.copy(currentPlayersCount = room.currentPlayersCount + 1))
            SoundManager.playEntrySound(user.entrySound)
            showMessage("انضممت إلى قعدة: ${room.title}")
        }
    }

    fun leaveRoom(roomId: Long) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.roomDao.removeMember(roomId, user.id)
            val room = repository.roomDao.getRoomById(roomId)
            if (room != null && room.currentPlayersCount > 1) {
                repository.roomDao.updateRoom(room.copy(currentPlayersCount = room.currentPlayersCount - 1))
            }
            showMessage("غادرت القعدة")
        }
    }

    fun sendRoomMessage(roomId: Long, text: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (text.isBlank()) return@launch
            repository.messageDao.insertMessage(
                MessageEntity(
                    conversationType = "ROOM",
                    targetId = roomId,
                    senderId = user.id,
                    senderName = user.displayName,
                    senderFrame = user.frame,
                    text = text.trim(),
                    effect = user.chatEffect
                )
            )
            SoundManager.playClick()
        }
    }

    fun sendPrivateMessage(otherUserId: Long, text: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (text.isBlank()) return@launch
            repository.messageDao.insertMessage(
                MessageEntity(
                    conversationType = "PRIVATE",
                    targetId = otherUserId,
                    senderId = user.id,
                    senderName = user.displayName,
                    senderFrame = user.frame,
                    text = text.trim(),
                    effect = user.chatEffect
                )
            )
            SoundManager.playClick()
        }
    }

    fun addReaction(messageId: Long, emoji: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val existing = repository.reactionDao.getUserReaction(messageId, user.id)
            if (existing != null) {
                if (existing.reactionEmoji == emoji) {
                    repository.reactionDao.removeReaction(messageId, user.id)
                } else {
                    repository.reactionDao.insertReaction(existing.copy(reactionEmoji = emoji))
                }
            } else {
                repository.reactionDao.insertReaction(
                    MessageReactionEntity(
                        messageId = messageId,
                        userId = user.id,
                        userName = user.displayName,
                        reactionEmoji = emoji
                    )
                )
            }
            SoundManager.playClick()
        }
    }

    // Friend operations
    fun sendFriendRequest(targetUserId: Long) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val existing = repository.friendshipDao.getFriendship(user.id, targetUserId)
            if (existing != null) {
                showMessage("يوجد طلب أو صداقة قائمة بالفعل")
                return@launch
            }
            repository.friendshipDao.insertFriendship(
                FriendshipEntity(
                    userId = user.id,
                    friendId = targetUserId,
                    status = "PENDING",
                    requesterId = user.id
                )
            )
            repository.notificationDao.insertNotification(
                NotificationEntity(
                    userId = targetUserId,
                    title = "طلب صداقة جديد 👥",
                    message = "أرسل لك ${user.displayName} طلب صداقة",
                    type = "FRIEND_REQ"
                )
            )
            SoundManager.playClick()
            showMessage("تم إرسال طلب الصداقة بنجاح!")
        }
    }

    fun acceptFriendRequest(friendshipId: Long) {
        viewModelScope.launch {
            repository.friendshipDao.updateStatus(friendshipId, "ACCEPTED")
            SoundManager.playVictory()
            showMessage("أصبحتم أصدقاء الآن! 🎉")
        }
    }

    fun rejectFriendRequest(friendshipId: Long) {
        viewModelScope.launch {
            repository.friendshipDao.deleteFriendship(friendshipId)
            showMessage("تم رفض طلب الصداقة")
        }
    }

    fun blockUser(targetUserId: Long) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.blockDao.insertBlock(BlockEntity(userId = user.id, blockedUserId = targetUserId))
            SoundManager.playAlert()
            showMessage("تم حظر المستخدم بنجاح")
        }
    }

    fun reportTarget(targetType: String, targetId: Long, reason: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.reportDao.insertReport(
                ReportEntity(
                    reporterId = user.id,
                    reporterName = user.displayName,
                    targetType = targetType,
                    targetId = targetId,
                    reason = reason
                )
            )
            SoundManager.playClick()
            showMessage("تم إرسال البلاغ للإدارة للمراجعة، شكراً لتعاونك! 🛡️")
        }
    }

    // Mark notifications read
    fun markAllNotificationsRead() {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.notificationDao.markAllAsRead(user.id)
        }
    }

    // Admin Operations
    fun adminModifyCoins(targetUserId: Long, amount: Long, reason: String) {
        viewModelScope.launch {
            val admin = currentUser.value ?: return@launch
            val res = repository.adminModifyCoins(admin.id, targetUserId, amount, reason)
            res.onSuccess {
                SoundManager.playCoins()
                showMessage(it)
            }.onFailure {
                SoundManager.playAlert()
                showMessage(it.message ?: "فشل تعديل الرصيد")
            }
        }
    }

    fun adminBanUser(targetUserId: Long, reason: String, isPermanent: Boolean) {
        viewModelScope.launch {
            val admin = currentUser.value ?: return@launch
            val res = repository.adminBanUser(admin.id, targetUserId, reason, isPermanent)
            res.onSuccess {
                SoundManager.playAlert()
                showMessage(it)
            }.onFailure {
                showMessage(it.message ?: "فشل الحظر")
            }
        }
    }

    fun adminUnbanUser(targetUserId: Long) {
        viewModelScope.launch {
            val admin = currentUser.value ?: return@launch
            val res = repository.adminUnbanUser(admin.id, targetUserId)
            res.onSuccess {
                showMessage(it)
            }.onFailure {
                showMessage(it.message ?: "فشل فك الحظر")
            }
        }
    }

    fun adminAddQuestion(q: QuestionEntity) {
        viewModelScope.launch {
            repository.questionDao.insertQuestion(q)
            showMessage("تم إضافة السؤال إلى بنك الأسئلة بنجاح")
        }
    }

    fun adminDeleteQuestion(qId: Long) {
        viewModelScope.launch {
            repository.questionDao.deleteQuestion(qId)
            showMessage("تم حذف السؤال")
        }
    }

    fun adminSaveAd(ad: InternalAdEntity) {
        viewModelScope.launch {
            if (ad.id == 0L) repository.internalAdDao.insertAd(ad)
            else repository.internalAdDao.updateAd(ad)
            showMessage("تم حفظ الإعلان بنجاح")
        }
    }

    fun adminDeleteAd(adId: Long) {
        viewModelScope.launch {
            repository.internalAdDao.deleteAd(adId)
            showMessage("تم حذف الإعلان")
        }
    }
}
