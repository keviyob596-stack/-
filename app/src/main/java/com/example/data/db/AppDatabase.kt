package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        FriendshipEntity::class,
        BlockEntity::class,
        RoomEntity::class,
        RoomMemberEntity::class,
        MessageEntity::class,
        MessageReactionEntity::class,
        StoreItemEntity::class,
        InventoryItemEntity::class,
        CoinTransactionEntity::class,
        DailyRewardEntity::class,
        DailyRewardClaimEntity::class,
        DailyMissionEntity::class,
        UserMissionProgressEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        QuestionEntity::class,
        InternalAdEntity::class,
        AnnouncementEntity::class,
        ReportEntity::class,
        BanEntity::class,
        AdminLogEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendshipDao(): FriendshipDao
    abstract fun blockDao(): BlockDao
    abstract fun roomDao(): RoomDao
    abstract fun messageDao(): MessageDao
    abstract fun reactionDao(): ReactionDao
    abstract fun storeDao(): StoreDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun economyDao(): EconomyDao
    abstract fun dailyRewardDao(): DailyRewardDao
    abstract fun dailyMissionDao(): DailyMissionDao
    abstract fun achievementDao(): AchievementDao
    abstract fun questionDao(): QuestionDao
    abstract fun internalAdDao(): InternalAdDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun reportDao(): ReportDao
    abstract fun banDao(): BanDao
    abstract fun adminLogDao(): AdminLogDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sadeeqi_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
