package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppConstants
import com.example.data.AppRepository
import com.example.data.BadgeDefinition
import com.example.data.HealthStage
import com.example.data.UserConfigEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class DashboardStats(
    val diffSeconds: Long = 0L,
    val days: Long = 0L,
    val hours: Long = 0L,
    val minutes: Long = 0L,
    val seconds: Long = 0L,
    val cigsAvoided: Int = 0,
    val moneySaved: Long = 0L,
    val lifeSavedHours: Double = 0.0,
    val nextGoalTitle: String = "",
    val nextGoalProgressPercent: Int = 0
)

class MainViewModel(
    private val repository: AppRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    private val _quoteIndex = MutableStateFlow(0)
    val currentQuote: StateFlow<String> = MutableStateFlow(AppConstants.quotes[0])

    private val _confettiTrigger = MutableStateFlow(0L)
    val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    val userConfig: StateFlow<UserConfigEntity> = repository.userConfigFlow
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserConfigEntity()
        )

    val unlockedBadges: StateFlow<List<com.example.data.BadgeEntity>> = repository.unlockedBadgesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        // Ensure default config exists
        viewModelScope.launch {
            repository.userConfigFlow.collect { current ->
                if (current == null) {
                    val initial = UserConfigEntity(
                        id = 1,
                        quitTimestamp = System.currentTimeMillis(),
                        cigsPerDay = 20,
                        costPerPack = 60000L,
                        packSize = 20,
                        cravingResistedCount = 0
                    )
                    repository.saveConfig(initial)
                }
            }
        }

        // Ticker for live second-by-second updates
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = System.currentTimeMillis()
                recalculateStats()
                delay(1000L)
            }
        }

        // Quote rotation every 12 seconds
        viewModelScope.launch {
            while (isActive) {
                delay(12000L)
                rotateQuote()
            }
        }
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun rotateQuote() {
        val nextIdx = (_quoteIndex.value + 1) % AppConstants.quotes.size
        _quoteIndex.value = nextIdx
        (currentQuote as MutableStateFlow).value = AppConstants.quotes[nextIdx]
    }

    fun triggerCelebration() {
        _confettiTrigger.value = System.currentTimeMillis()
    }

    fun saveConfig(quitTimestamp: Long, cigsPerDay: Int, costPerPack: Long, packSize: Int) {
        viewModelScope.launch {
            val current = userConfig.value
            val updated = current.copy(
                quitTimestamp = quitTimestamp,
                cigsPerDay = cigsPerDay.coerceAtLeast(1),
                costPerPack = costPerPack.coerceAtLeast(0L),
                packSize = packSize.coerceAtLeast(1)
            )
            repository.saveConfig(updated)
            recalculateStats()
        }
    }

    fun resetQuitTime() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repository.resetQuitTime(now)
            setSelectedTab(0)
            recalculateStats()
        }
    }

    fun onCravingResisted() {
        viewModelScope.launch {
            repository.incrementCravingResisted()
            triggerCelebration()
            recalculateStats()
        }
    }

    private fun recalculateStats() {
        val config = userConfig.value
        val now = _currentTime.value
        val quitTime = config.quitTimestamp
        val diffMs = (now - quitTime).coerceAtLeast(0L)
        val diffSecs = diffMs / 1000L
        val diffMins = diffSecs / 60L
        val diffHours = diffSecs / 3600.0
        val diffDays = (diffHours / 24.0).toLong()

        val costPerCig = if (config.packSize > 0) config.costPerPack.toDouble() / config.packSize else 0.0
        val cigsAvoided = ((diffSecs.toDouble() / 86400.0) * config.cigsPerDay).toInt()
        val moneySaved = (cigsAvoided * costPerCig).toLong()
        val lifeSavedMinutes = cigsAvoided * 11
        val lifeSavedHours = lifeSavedMinutes / 60.0

        // Determine next milestone
        var nextGoalTitle = "همه اهداف تکمیل شد!"
        var nextGoalPercent = 100
        for (stage in AppConstants.healthStages) {
            val percent = ((diffHours / stage.hours) * 100).toInt().coerceIn(0, 100)
            if (percent < 100) {
                nextGoalTitle = stage.title
                nextGoalPercent = percent
                break
            }
        }

        _stats.value = DashboardStats(
            diffSeconds = diffSecs,
            days = diffDays,
            hours = (diffHours.toLong() % 24),
            minutes = (diffMins % 60),
            seconds = (diffSecs % 60),
            cigsAvoided = cigsAvoided,
            moneySaved = moneySaved,
            lifeSavedHours = lifeSavedHours,
            nextGoalTitle = nextGoalTitle,
            nextGoalProgressPercent = nextGoalPercent
        )

        // Check badges
        checkBadges(diffHours, cigsAvoided, moneySaved, config.cravingResistedCount)
    }

    private fun checkBadges(passedHours: Double, cigsAvoided: Int, moneySaved: Long, cravingCount: Int) {
        val currentUnlocked = unlockedBadges.value.map { it.id }.toSet()
        for (b in AppConstants.allBadges) {
            if (currentUnlocked.contains(b.id)) continue

            var earned = false
            if (b.reqHours != null && passedHours >= b.reqHours) earned = true
            if (b.reqCigs != null && cigsAvoided >= b.reqCigs) earned = true
            if (b.reqMoney != null && moneySaved >= b.reqMoney) earned = true
            if (b.reqCravingResisted != null && cravingCount >= b.reqCravingResisted) earned = true

            if (earned) {
                viewModelScope.launch {
                    repository.unlockBadge(b.id)
                    triggerCelebration()
                }
            }
        }
    }
}

class MainViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
