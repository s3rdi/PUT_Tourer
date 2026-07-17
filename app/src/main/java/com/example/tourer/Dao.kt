package com.example.tourer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tourer.model.Route
import com.example.tourer.model.RouteLog
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM route")
    fun getAllRoutes(): Flow<List<Route>>
}

@Dao
interface RouteLogDao {
    @Insert
    suspend fun insertLog(log: RouteLog)

    @Query("SELECT * FROM routeLogs WHERE routeId = :routeId ORDER BY timeInSeconds ASC")
    fun getLogsForRoute(routeId: Int): Flow<List<RouteLog>>


}