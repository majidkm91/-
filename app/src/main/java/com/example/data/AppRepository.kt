package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userConfigDao: UserConfigDao,
    private val badgeDao: BadgeDao
) {
    val userConfigFlow: Flow<UserConfigEntity?> = userConfigDao.getConfig()
    val unlockedBadgesFlow: Flow<List<BadgeEntity>> = badgeDao.getAllBadges()

    suspend fun saveConfig(config: UserConfigEntity) {
        userConfigDao.insertConfig(config)
    }

    suspend fun resetQuitTime(newTimestamp: Long) {
        userConfigDao.resetQuitTime(newTimestamp)
        badgeDao.clearBadges()
    }

    suspend fun incrementCravingResisted() {
        userConfigDao.incrementCravingResisted()
    }

    suspend fun unlockBadge(badgeId: String) {
        badgeDao.insertBadge(BadgeEntity(id = badgeId, unlockedAt = System.currentTimeMillis()))
    }
}
