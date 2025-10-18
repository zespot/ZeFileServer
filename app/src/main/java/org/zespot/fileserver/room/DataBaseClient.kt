package org.zespot.fileserver.room

import android.content.Context
import androidx.room.Room

class DataBaseClient(context: Context) {
    val appDataBase : AppDataBase = Room.databaseBuilder(
        context.applicationContext,
        AppDataBase::class.java,
        "Logs.db"
            ).build()
}