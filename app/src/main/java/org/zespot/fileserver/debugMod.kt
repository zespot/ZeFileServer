package org.zespot.fileserver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


@Composable
fun debugMod(onDismiss: () -> Unit) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            shape = RoundedCornerShape(10.dp),

        ) {
            Column(
                modifier = Modifier.padding(5.dp).fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }
                Text("Debug info" ,fontWeight = FontWeight.Bold,         textAlign = TextAlign.Center,        fontSize = 20.sp,
                    modifier=Modifier.fillMaxWidth()
                    )
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    LogBouton()

                    Text(text = execInput())
                    if (StaticVars.getIsRooted())Text("IsDeviceRooted ="+StaticVars.getIsRooted())
                    if (StaticVars.getIsRootAquired())Text("IsRootAquired ="+StaticVars.getIsRootAquired())
                    Text("Nativlib="+context.getApplicationInfo().nativeLibraryDir.toString())
                    Text("HotSpot enabled :   " + isHotspotEnabled(LocalContext.current))
                    if (isHotspotEnabled(LocalContext.current))
                        Text("Hotspot  IP :   " + getHotspotIpAddress())
                    Text("Wifi enabled :   " + isWifi(LocalContext.current))
                    if (isWifi(LocalContext.current))
                        Text("Wlan IP :   " + getWifiIpAddress(LocalContext.current))
                 //   Text("Get IP adress " + getIpAddress(LocalContext.current))
                 //   Text("Gateway  server " + GwIpAddress(LocalContext.current))
                    Text("\nuname -a :\n"+shellExec("uname -a"))
                    Text("\nps -ef :\n"+shellExec("ps -ef"))
                }
            }
        }
    }
}