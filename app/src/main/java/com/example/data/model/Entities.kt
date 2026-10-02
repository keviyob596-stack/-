package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val displayName: String,
    val email: String,
    val passwordHash: String,
    val avatarUrl: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val onlineCoins: Long = 100, // Starting bonus
    val offlineCoins: Long = 50,  // Separate offline coins
    val wins: Int = 0,
    val gamesPlayed: Int = 0,
    val frame: String = "NONE",     // e.g. "ROYAL", "GOLDEN", "FIRE", "NONE"
    val badge: String = "BEGINNER", // e.g. "CHAMPION", "FRIENDLY", "VETERAN"
    val entrySound: String = "DEFAULT", // "VIP", "ROYAL", "DEFAULT"
    val chatEffect: String = "NONE", // "GOLD_SPARKLE", "FIRE", "NONE"
    val isBanned: Boolean = false,
    val banReason: String = "",
    val role: String = "USER", // "ADMIN" or "USER"
    val createdAt: Long = System.currentTimeMillis(),
    val lastSeen: Long = System.currentTimeMillis(),
    val isOnline: Boolean = true
)

@Entity(tableName = "friendships")
data class FriendshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val friendId: Long,
    val status: String = "PENDING", // "ACCEPTED", "PENDING", "REJECTED"
    val requesterId: Long,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocks")
data class BlockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val blockedUserId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val code: String = "",
    val isPrivate: Boolean = false,
    val ownerId: Long,
    val ownerName: String,
    val currentPlayersCount: Int = 1,
    val maxPlayers: Int = 10,
    val activeGame: String = "",
    val isClosed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "room_members")
data class RoomMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    val userId: Long,
    val userName: String,
    val userAvatar: String = "",
    val userFrame: String = "NONE",
    val isMuted: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationType: String, // "PRIVATE" or "ROOM"
    val targetId: Long,           // friendId if PRIVATE, roomId if ROOM
    val senderId: Long,
    val senderName: String,
    val senderAvatar: String = "",
    val senderFrame: String = "NONE",
    val text: String,
    val giftType: String = "",    // "ROSE", "HEART", "CROWN", ""
    val giftAmount: Int = 0,
    val coinAmount: Long = 0,
    val effect: String = "NONE",
    val replyToId: Long? = null,
    val replyToText: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "message_reactions")
data class MessageReactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: Long,
    val userId: Long,
    val userName: String,
    val reactionEmoji: String // "❤️", "😂", "😮", "😢", "👍", "🔥"
)

@Entity(tableName = "store_items")
data class StoreItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemKey: String,
    val name: String,
    val type: String, // "GIFT", "SOUND", "FRAME", "EFFECT"
    val price: Long,
    val icon: String,
    val description: String,
    val active: Boolean = true
)

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val itemKey: String,
    val count: Int = 1,
    val isEquipped: Boolean = false
)

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: String,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long,
    val receiverName: String,
    val amount: Long,
    val currencyType: String = "ONLINE", // "ONLINE" or "OFFLINE"
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SUCCESS"
)

@Entity(tableName = "daily_rewards")
data class DailyRewardEntity(
    @PrimaryKey val dayNumber: Int,
    val coins: Long,
    val xp: Int
)

@Entity(tableName = "daily_reward_claims")
data class DailyRewardClaimEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dayNumber: Int,
    val claimDate: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_missions")
data class DailyMissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val targetCount: Int,
    val rewardCoins: Long,
    val rewardXp: Int,
    val missionType: String, // "PLAY_GAMES", "SEND_MESSAGES", "WIN_GAMES", "SEND_GIFT", "PLAY_WITH_FRIEND"
    val active: Boolean = true
)

@Entity(tableName = "user_mission_progress")
data class UserMissionProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val missionId: Long,
    val currentCount: Int = 0,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val dateKey: String = "" // YYYY-MM-DD
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val code: String,
    val title: String,
    val description: String,
    val requirementCount: Int,
    val rewardCoins: Long,
    val rewardXp: Int,
    val badgeCode: String
)

@Entity(tableName = "user_achievements")
data class UserAchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val achievementCode: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: String, // "AUTOBUS", "GENERAL", "FUNNY", "TRUE_FALSE", "FAST", "WHO_AM_I", "WHO_IS_LIAR", "GUESS_WORD"
    val category: String,
    val difficulty: String = "MEDIUM", // "EASY", "MEDIUM", "HARD"
    val question: String,
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String,
    val explanation: String = "",
    val active: Boolean = true
)

@Entity(tableName = "internal_ads")
data class InternalAdEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val buttonText: String = "عرض التفاصيل",
    val linkUrl: String = "",
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val startDate: Long = 0L,
    val endDate: Long = 0L
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reporterId: Long,
    val reporterName: String,
    val targetType: String, // "USER", "MESSAGE", "ROOM"
    val targetId: Long,
    val reason: String,
    val date: Long = System.currentTimeMillis(),
    val status: String = "OPEN" // "OPEN", "REVIEWING", "RESOLVED", "REJECTED"
)

@Entity(tableName = "bans")
data class BanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val userEmail: String,
    val userName: String,
    val reason: String,
    val adminName: String,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = 0L, // 0 = permanent
    val isPermanent: Boolean = true,
    val status: String = "ACTIVE" // "ACTIVE", "EXPIRED", "REVOKED"
)

@Entity(tableName = "admin_logs")
data class AdminLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminId: Long,
    val adminName: String,
    val action: String,
    val targetUser: String,
    val details: String,
    val beforeVal: String = "",
    val afterVal: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: String, // "FRIEND_REQ", "GIFT", "COINS", "ROOM_INVITE", "GAME_RESULT", "DAILY_REWARD", "ADMIN_ALERT"
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
