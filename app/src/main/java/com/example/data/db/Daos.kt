package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY xp DESC")
    fun getAllUsersByXp(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY wins DESC")
    fun getAllUsersByWins(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY gamesPlayed DESC")
    fun getAllUsersByGames(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR displayName LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET onlineCoins = onlineCoins + :delta WHERE id = :userId")
    suspend fun adjustOnlineCoins(userId: Long, delta: Long)

    @Query("UPDATE users SET offlineCoins = offlineCoins + :delta WHERE id = :userId")
    suspend fun adjustOfflineCoins(userId: Long, delta: Long)

    @Query("UPDATE users SET xp = xp + :xpDelta, level = :newLevel WHERE id = :userId")
    suspend fun addXpAndSetLevel(userId: Long, xpDelta: Int, newLevel: Int)

    @Query("UPDATE users SET wins = wins + 1, gamesPlayed = gamesPlayed + 1 WHERE id = :userId")
    suspend fun incrementWin(userId: Long)

    @Query("UPDATE users SET gamesPlayed = gamesPlayed + 1 WHERE id = :userId")
    suspend fun incrementGamesPlayed(userId: Long)

    @Query("UPDATE users SET isBanned = :isBanned, banReason = :reason WHERE id = :userId")
    suspend fun setBanStatus(userId: Long, isBanned: Boolean, reason: String)

    @Query("UPDATE users SET isOnline = :isOnline, lastSeen = :time WHERE id = :userId")
    suspend fun setOnlineStatus(userId: Long, isOnline: Boolean, time: Long = System.currentTimeMillis())
}

@Dao
interface FriendshipDao {
    @Query("SELECT * FROM friendships WHERE (userId = :userId OR friendId = :userId) AND status = 'ACCEPTED'")
    fun getAcceptedFriendshipsFlow(userId: Long): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE friendId = :userId AND status = 'PENDING'")
    fun getPendingRequestsFlow(userId: Long): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE (userId = :u1 AND friendId = :u2) OR (userId = :u2 AND friendId = :u1) LIMIT 1")
    suspend fun getFriendship(u1: Long, u2: Long): FriendshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: FriendshipEntity): Long

    @Query("UPDATE friendships SET status = :status, updatedAt = :time WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, time: Long = System.currentTimeMillis())

    @Query("DELETE FROM friendships WHERE id = :id")
    suspend fun deleteFriendship(id: Long)
}

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks WHERE userId = :userId")
    fun getBlocksFlow(userId: Long): Flow<List<BlockEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM blocks WHERE (userId = :u1 AND blockedUserId = :u2) OR (userId = :u2 AND blockedUserId = :u1))")
    suspend fun isBlocked(u1: Long, u2: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: BlockEntity)

    @Query("DELETE FROM blocks WHERE userId = :userId AND blockedUserId = :targetId")
    suspend fun removeBlock(userId: Long, targetId: Long)
}

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms WHERE isClosed = 0 ORDER BY createdAt DESC")
    fun getActiveRoomsFlow(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms WHERE id = :roomId")
    fun getRoomFlow(roomId: Long): Flow<RoomEntity?>

    @Query("SELECT * FROM rooms WHERE id = :roomId")
    suspend fun getRoomById(roomId: Long): RoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity): Long

    @Update
    suspend fun updateRoom(room: RoomEntity)

    @Query("UPDATE rooms SET isClosed = 1 WHERE id = :roomId")
    suspend fun closeRoom(roomId: Long)

    @Query("SELECT * FROM room_members WHERE roomId = :roomId")
    fun getRoomMembersFlow(roomId: Long): Flow<List<RoomMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: RoomMemberEntity)

    @Query("DELETE FROM room_members WHERE roomId = :roomId AND userId = :userId")
    suspend fun removeMember(roomId: Long, userId: Long)

    @Query("UPDATE room_members SET isMuted = :isMuted WHERE roomId = :roomId AND userId = :userId")
    suspend fun setMemberMute(roomId: Long, userId: Long, isMuted: Boolean)
}

@Dao
interface MessageDao {
    @Query("""
        SELECT * FROM messages 
        WHERE conversationType = 'PRIVATE' 
        AND ((senderId = :userId AND targetId = :otherUserId) OR (senderId = :otherUserId AND targetId = :userId))
        ORDER BY timestamp ASC
    """)
    fun getPrivateMessagesFlow(userId: Long, otherUserId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationType = 'ROOM' AND targetId = :roomId ORDER BY timestamp ASC")
    fun getRoomMessagesFlow(roomId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: Long)

    @Query("UPDATE messages SET isRead = 1 WHERE targetId = :userId AND senderId = :otherUserId AND conversationType = 'PRIVATE'")
    suspend fun markMessagesAsRead(userId: Long, otherUserId: Long)

    @Query("SELECT COUNT(*) FROM messages")
    suspend fun getTotalMessagesCount(): Int
}

@Dao
interface ReactionDao {
    @Query("SELECT * FROM message_reactions WHERE messageId = :messageId")
    fun getReactionsForMessageFlow(messageId: Long): Flow<List<MessageReactionEntity>>

    @Query("SELECT * FROM message_reactions WHERE messageId = :messageId AND userId = :userId LIMIT 1")
    suspend fun getUserReaction(messageId: Long, userId: Long): MessageReactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReaction(reaction: MessageReactionEntity)

    @Query("DELETE FROM message_reactions WHERE messageId = :messageId AND userId = :userId")
    suspend fun removeReaction(messageId: Long, userId: Long)
}

@Dao
interface StoreDao {
    @Query("SELECT * FROM store_items WHERE active = 1")
    fun getActiveStoreItemsFlow(): Flow<List<StoreItemEntity>>

    @Query("SELECT * FROM store_items")
    fun getAllStoreItemsFlow(): Flow<List<StoreItemEntity>>

    @Query("SELECT * FROM store_items WHERE itemKey = :key LIMIT 1")
    suspend fun getStoreItemByKey(key: String): StoreItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoreItem(item: StoreItemEntity)

    @Update
    suspend fun updateStoreItem(item: StoreItemEntity)

    @Query("DELETE FROM store_items WHERE id = :id")
    suspend fun deleteStoreItem(id: Long)
}

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items WHERE userId = :userId")
    fun getUserInventoryFlow(userId: Long): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE userId = :userId AND itemKey = :itemKey LIMIT 1")
    suspend fun getInventoryItem(userId: Long, itemKey: String): InventoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateInventory(item: InventoryItemEntity)

    @Query("UPDATE inventory_items SET count = count + :delta WHERE userId = :userId AND itemKey = :itemKey")
    suspend fun adjustCount(userId: Long, itemKey: String, delta: Int)

    @Query("UPDATE inventory_items SET isEquipped = :isEquipped WHERE userId = :userId AND itemKey = :itemKey")
    suspend fun setEquipped(userId: Long, itemKey: String, isEquipped: Boolean)
}

@Dao
interface EconomyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity): Long

    @Query("SELECT * FROM coin_transactions WHERE senderId = :userId OR receiverId = :userId ORDER BY timestamp DESC")
    fun getUserTransactionsFlow(userId: Long): Flow<List<CoinTransactionEntity>>

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<CoinTransactionEntity>>

    @Query("SELECT COUNT(*) FROM coin_transactions")
    suspend fun getTotalTransactionsCount(): Int

    @Query("SELECT SUM(onlineCoins) FROM users")
    suspend fun getTotalOnlineCoinsInCirculation(): Long?
}

@Dao
interface DailyRewardDao {
    @Query("SELECT * FROM daily_rewards ORDER BY dayNumber ASC")
    fun getDailyRewardsConfigFlow(): Flow<List<DailyRewardEntity>>

    @Query("SELECT * FROM daily_rewards WHERE dayNumber = :dayNumber LIMIT 1")
    suspend fun getRewardForDay(dayNumber: Int): DailyRewardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReward(reward: DailyRewardEntity)

    @Query("SELECT * FROM daily_reward_claims WHERE userId = :userId AND claimDate = :today LIMIT 1")
    suspend fun getClaimToday(userId: Long, today: String): DailyRewardClaimEntity?

    @Query("SELECT * FROM daily_reward_claims WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastClaim(userId: Long): DailyRewardClaimEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(claim: DailyRewardClaimEntity)
}

@Dao
interface DailyMissionDao {
    @Query("SELECT * FROM daily_missions WHERE active = 1")
    fun getActiveMissionsFlow(): Flow<List<DailyMissionEntity>>

    @Query("SELECT * FROM daily_missions")
    fun getAllMissionsFlow(): Flow<List<DailyMissionEntity>>

    @Query("SELECT * FROM user_mission_progress WHERE userId = :userId AND dateKey = :dateKey")
    fun getUserMissionProgressFlow(userId: Long, dateKey: String): Flow<List<UserMissionProgressEntity>>

    @Query("SELECT * FROM user_mission_progress WHERE userId = :userId AND missionId = :missionId AND dateKey = :dateKey LIMIT 1")
    suspend fun getProgress(userId: Long, missionId: Long, dateKey: String): UserMissionProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: DailyMissionEntity)

    @Update
    suspend fun updateMission(mission: DailyMissionEntity)

    @Query("DELETE FROM daily_missions WHERE id = :id")
    suspend fun deleteMission(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserMissionProgressEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievementsFlow(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    fun getUserAchievementsFlow(userId: Long): Flow<List<UserAchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockAchievement(userAchievement: UserAchievementEntity)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE gameType = :gameType AND active = 1")
    fun getQuestionsByGameTypeFlow(gameType: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE gameType = :gameType AND active = 1")
    suspend fun getQuestionsByGameType(gameType: String): List<QuestionEntity>

    @Query("SELECT * FROM questions")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: Long)
}

@Dao
interface InternalAdDao {
    @Query("SELECT * FROM internal_ads WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getActiveAdsFlow(): Flow<List<InternalAdEntity>>

    @Query("SELECT * FROM internal_ads ORDER BY sortOrder ASC")
    fun getAllAdsFlow(): Flow<List<InternalAdEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAd(ad: InternalAdEntity): Long

    @Update
    suspend fun updateAd(ad: InternalAdEntity)

    @Query("DELETE FROM internal_ads WHERE id = :id")
    suspend fun deleteAd(id: Long)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements ORDER BY createdAt DESC")
    fun getAllAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Update
    suspend fun updateAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM reports ORDER BY date DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("UPDATE reports SET status = :status WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: Long, status: String)

    @Query("SELECT COUNT(*) FROM reports WHERE status = 'OPEN'")
    suspend fun getOpenReportsCount(): Int
}

@Dao
interface BanDao {
    @Query("SELECT * FROM bans ORDER BY startDate DESC")
    fun getAllBansFlow(): Flow<List<BanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBan(ban: BanEntity): Long

    @Query("UPDATE bans SET status = :status WHERE id = :id")
    suspend fun updateBanStatus(id: Long, status: String)
}

@Dao
interface AdminLogDao {
    @Query("SELECT * FROM admin_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<AdminLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AdminLogEntity): Long
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserNotificationsFlow(userId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadNotificationsCountFlow(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long)
}
