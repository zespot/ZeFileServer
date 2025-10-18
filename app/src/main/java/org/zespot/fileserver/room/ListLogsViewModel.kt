package org.zespot.fileserver.room

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListLogsViewModel (val client : DataBaseClient) : ViewModel(){
    val _logs = mutableStateOf<List<Logs>>(emptyList())
    val logs : State<List<Logs>> = _logs

    init {
        viewModelScope.launch {
//            insert(Logs(0L, "12/02/2025 8:41", "Start server"))
//            insert(Logs(0L, "12/02/2025 9:10", "Stop server"))
            loadLogs()
        }
    }

     private suspend fun loadLogs() {
        withContext(Dispatchers.IO) {
            val logsFromBD = client.appDataBase.logsDao().getAllLogs()
            _logs.value = logsFromBD
        }
    }

    fun getLogs (){
        viewModelScope.launch {
            loadLogs()
        }
    }



    suspend fun insert (log: Logs) {
        withContext(Dispatchers.IO) {
            client.appDataBase.logsDao().insert(log)
        }
    }
     fun add (log: Logs) {
         viewModelScope.launch {
             client.appDataBase.logsDao().insert(log)
         }

    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ListLogsViewModel(
                    DataBaseClient(application.applicationContext),
                ) as T
            }
        }
    }
}