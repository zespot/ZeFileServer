package org.zespot.fileserver

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
public fun execInput() : String {
    var command by rememberSaveable { mutableStateOf("")}
    var res by rememberSaveable { mutableStateOf("")}
    Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
        OutlinedTextField(modifier = Modifier.weight(9f),
            value = command,
            onValueChange = { command = it },
            label = { Text("Type sh command") }
        )
        Button( modifier = Modifier.weight(1f),
            onClick = { res = shellExec(command)  }, contentPadding = PaddingValues(4.dp))
        {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Fermer")
        }
        if (res !="") {
            return res
        }
    }
    return ""
}

public fun shellExec(command: String)  : String {
    Log.d("TC", "Shell_exec commande reçue "+ command)
    val shell = command
    try {

            val process = Runtime.getRuntime().exec(shell)
        process.waitFor()
            //val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output2 = process.inputStream.bufferedReader().readText()
            val erreur2 = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            var res = "Code = "+exitCode.toString()
            if (! erreur2.toString().isEmpty()) res += "\n"+erreur2.toString()
            if (! output2.toString().isEmpty()) res += "\n"+output2.toString()
        Log.d("TC", res)
            return res
            // Ferme le lecteur
            //reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("TC", "Erreur lors de l'exécution de la commande shell", e)
        }
     catch (e: Exception) {
    Log.d("Thierry","Exec Shell  - Error :"+e.toString())
    e.printStackTrace()
    }
    return "Cannot execute command : "+command
    }

