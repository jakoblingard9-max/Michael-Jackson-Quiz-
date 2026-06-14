package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey val categoryId: String,
    val bestScore: Int,
    val maxScore: Int,
    val completedCount: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_badges")
data class UnlockedBadgeEntity(
    @PrimaryKey val badgeId: String,
    val title: String,
    val description: String,
    val category: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_scores")
    fun getAllScoresFlow(): Flow<List<QuizScoreEntity>>

    @Query("SELECT * FROM quiz_scores WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getScoreForCategory(categoryId: String): QuizScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScore(score: QuizScoreEntity)

    @Query("SELECT * FROM unlocked_badges")
    fun getAllBadgesFlow(): Flow<List<UnlockedBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun unlockBadge(badge: UnlockedBadgeEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_badges WHERE badgeId = :badgeId LIMIT 1)")
    suspend fun hasBadge(badgeId: String): Boolean

    @Query("DELETE FROM quiz_scores")
    suspend fun clearAllScores()

    @Query("DELETE FROM unlocked_badges")
    suspend fun clearAllBadges()
}

@Database(entities = [QuizScoreEntity::class, UnlockedBadgeEntity::class], version = 1, exportSchema = false)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}
