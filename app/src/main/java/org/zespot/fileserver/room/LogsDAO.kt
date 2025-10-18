package org.zespot.fileserver.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LogsDAO {

    @Insert
    suspend fun insert(logs:Logs)

    @Query("SELECT * from logs")
    fun getAllLogs() : List<Logs>
}