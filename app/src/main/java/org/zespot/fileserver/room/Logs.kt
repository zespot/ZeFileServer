package org.zespot.fileserver.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Logs(
    @PrimaryKey(autoGenerate = true) val id : Long =0L,
    var date : String,
    val action : String
)
