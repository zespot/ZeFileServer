package org.zespot.fileserver.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities=[Logs::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun logsDao() : LogsDAO
}