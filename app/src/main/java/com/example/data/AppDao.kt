package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserConfigDao {
    @Query("SELECT * FROM user_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<UserConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: UserConfigEntity)

    @Query("UPDATE user_config SET quitTimestamp = :newQuitTime WHERE id = 1")
    suspend fun resetQuitTime(newQuitTime: Long)

    @Query("UPDATE user_config SET cravingResistedCount = cravingResistedCount + 1 WHERE id = 1")
    suspend fun incrementCravingResisted()
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeEntity)

    @Query("DELETE FROM badges")
    suspend fun clearBadges()
}
