package com.example.tourer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourer.AppDatabase
import com.example.tourer.model.Route
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class RouteViewModel(application: Application) : AndroidViewModel(application) {
    private val routeDao = AppDatabase.getInstance(application).routeDao()
    private val routeLogDao = AppDatabase.getInstance(application).routeLogDao()
    private val _selectedFilter = MutableStateFlow("wszystkie")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()
    private val _selectedRouteId = MutableStateFlow<String?>(null)
    val selectedRouteId = _selectedRouteId.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun selectRoute(routeId: String?) {
        _selectedRouteId.value = routeId
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val routes: StateFlow<List<Route>> = routeDao.getAllRoutes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    private var timerJob: Job? = null
    private val _timeInSeconds = MutableStateFlow(0)
    val timeInSeconds: StateFlow<Int> = _timeInSeconds.asStateFlow()
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()
    private val _activeTimerRouteId = MutableStateFlow<String?>(null)
    val activeTimerRouteId: StateFlow<String?> = _activeTimerRouteId.asStateFlow()
    private val _activeTimerRouteName = MutableStateFlow<String?>(null)
    val activeTimerRouteName: StateFlow<String?> = _activeTimerRouteName.asStateFlow()
    private val _showStopConfirm = MutableStateFlow(false)
    val showStopConfirm: StateFlow<Boolean> = _showStopConfirm.asStateFlow()

    fun startTimer(routeId: String, routeName: String) {
        if (timerJob?.isActive == true)
            return

        _activeTimerRouteId.value = routeId
        _activeTimerRouteName.value = routeName
        _isTimerRunning.value = true
        _showStopConfirm.value = false

        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _timeInSeconds.value += 1
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
    }

    fun clickStop() {
        if (_showStopConfirm.value) {
            stopTimer()
        } else {
            pauseTimer()
            _showStopConfirm.value = true
        }
    }

    fun longStop(routeId: String?) {
        if (routeId != null) {
            val intId = routeId.toIntOrNull()
            if (intId != null) {
                viewModelScope.launch {
                    val log = com.example.tourer.model.RouteLog(
                        routeId = intId,
                        timeInSeconds = _timeInSeconds.value,
                        dateInMillis = System.currentTimeMillis()
                    )
                    routeLogDao.insertLog(log)
                }
            }
            stopTimer()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _timeInSeconds.value = 0
        _activeTimerRouteId.value = null
        _activeTimerRouteName.value = null
        _showStopConfirm.value = false
    }

    val formattedTime: StateFlow<String> = _timeInSeconds
        .map { totalSeconds ->
            val mins = totalSeconds / 60
            val secs = totalSeconds % 60
            String.format(java.util.Locale.getDefault(), "%02d:%02d", mins, secs)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "00:00"
        )

    fun getLogsForRoute(routeId: Int): kotlinx.coroutines.flow.Flow<List<com.example.tourer.model.RouteLog>> {
        return routeLogDao.getLogsForRoute(routeId)
    }
}