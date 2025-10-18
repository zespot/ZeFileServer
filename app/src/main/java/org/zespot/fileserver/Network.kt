package org.zespot.fileserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.compose.runtime.Composable
import java.lang.reflect.Method
import java.net.NetworkInterface
import java.util.Collections

fun isHotspotEnabled(context:Context): Boolean {
    val wifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    var dhcpIp = wifiManager.getDhcpInfo().serverAddress
    val method: Method = wifiManager.javaClass.getMethod(
        "getWifiApState"
    )
    method.isAccessible = true
    val invoke = method.invoke(wifiManager)
    println(invoke)
    if ( invoke == 13) return true
    return false
}

// Cf https://www.geeksforgeeks.org/how-to-check-internet-connection-in-kotlin/
fun isWifi(context: Context) : Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val activeNetwork = connectivityManager.getNetworkCapabilities(network)
    if (activeNetwork?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false) return true
    return false
}

// Lechat
fun getWifiIpAddress(context: Context): String? {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    val ipAddress = wifiInfo.ipAddress

    // Convertir l'adresse IP en un format lisible
    return if (ipAddress != 0) {
        val ipString = String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
        ipString
    } else {
        null
    }
}

// Le chat
fun getHotspotIpAddress(): String? {
    try {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            if (intf.name.lowercase().contains("ap") || intf.name.lowercase().contains("swlan")) {
                val addresses = Collections.list(intf.inetAddresses)
                for (addr in addresses) {
                    if (!addr.isLoopbackAddress) {
                        val ipAddress = addr.hostAddress
                        if (ipAddress != null && ipAddress.contains(".")) {
                            return ipAddress
                        }
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return null
}
@Composable
fun GwIpAddress(context: Context) : String {
    val wifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    return wifiManager.getDhcpInfo().gateway.toString()
}



///*
//Retourne l'adresse IP de wlan0 : wifi ou swlan0 : hotspot
// */
//fun WlanIpAddress(context: Context) : String? {
//    val interfaces = NetworkInterface.getNetworkInterfaces()
//    val list = interfaces.toList()
//    //go through the available interfaces
//    for ( iface in list) {
//        if ((iface.name.equals("wlan0")) || (iface.name.equals("swlan0"))  ) {
//            //look into the interface's ipaddresses (ipv4,ipv6)
//            var ip : String = iface.inetAddresses.toList().get(2).hostAddress.toString()
//            for (niface in iface.inetAddresses.toList()) {
//                var nip : String = niface.hostAddress.toString()
//                Log.d("Thierry",nip)
//                if (nip.startsWith("192")) return nip
//            }
//        }
//    }
//    return "No Wlan IP"
//}
//
//
//
//
//fun getIpAddress(context: Context) : String {
//    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
//    if (connectivityManager is ConnectivityManager) {
//        var link:LinkProperties =  connectivityManager.getLinkProperties(connectivityManager.activeNetwork) as LinkProperties
//
//        Log.d("Thierry", link.linkAddresses.toString())
//        return link.linkAddresses.toString()
//    }
//    return "IP adress cannot be read"
//}
//
// fun getLocalIpAddress(context: Context): String? {
//    try {
//        val wifiManager: WifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
//        return ipToString(wifiManager.connectionInfo.ipAddress)
//    } catch (ex: Exception) {
//        Log.e("IP Address", ex.toString())
//    }
//    return null
//}
//
//private fun ipToString(i: Int): String {
//    return (i and 0xFF).toString() + "." +
//            (i shr 8 and 0xFF) + "." +
//            (i shr 16 and 0xFF) + "." +
//            (i shr 24 and 0xFF)
//}
//
//







