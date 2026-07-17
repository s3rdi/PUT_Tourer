package com.example.tourer

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tourer.model.Route
import com.example.tourer.model.RouteLog

@Database(entities = [Route::class, RouteLog::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routeDao() : RouteDao
    abstract fun routeLogDao() : RouteLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "route_db"
                )
                .createFromAsset("database/routes.db")
                .build().also { INSTANCE = it }
            }
    }
}