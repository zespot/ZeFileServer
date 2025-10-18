package org.zespot.fileserver

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.zespot.fileserver.ui.theme.Noir

object FSLog {
    var StrLog: String = ""

    fun addFSlog(log: String) {
        Log.d("Thierry", "FSlog : ajout du log : $log")
        StrLog += "\n$log"
        Log.d("Thierry", "FSlog : STRLOG =  : $StrLog")
    }

    fun getFSlog(): String {
        return StrLog
    }

    fun clearFSlog() {
        StrLog = ""
    }

    fun log(l: String) {
        Log.d("Thierry", l)
        addFSlog(l)
    }

}

@Composable
fun LogBouton() {
    var show = rememberSaveable { mutableStateOf(false) }

    Button(
        modifier = Modifier.padding(0.dp),
        onClick = { show.value = !show.value },
        border = BorderStroke(1.dp, Noir),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Couleur de fond prédéfinie
            contentColor = Color.Black // Couleur du texte prédéfinie
        )
    ) {
        Text("Show Log")
    }
    if (show.value) {
        Text(FSLog.StrLog)
    }
}