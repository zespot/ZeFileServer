
import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun isDeviceRooted(): Boolean {
    return try {
        checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    } catch (e: Exception) {
        false
    }
}

private fun checkRootMethod1(): Boolean {
    return try {
        val buildTags = Build.TAGS
        buildTags != null && buildTags.contains("test-keys")
    } catch (e: Exception) {
        false
    }
}

private fun checkRootMethod2(): Boolean {
    return try {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        paths.any { path -> File(path).exists() }
    } catch (e: Exception) {
        false
    }
}

private fun checkRootMethod3(): Boolean {
    var process: Process? = null
    return try {
        process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
        val `in` = process.inputStream
        `in`.read() != -1
    } catch (e: Exception) {
        false
    } finally {
        process?.destroy()
    }
}
// Fonction pour demander les droits superutilisateur
fun requestRoot() {
    try {
        if (isDeviceRooted()) {
            // Demander les droits superutilisateur
            val process = Runtime.getRuntime().exec("su")
            process.outputStream.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Fonction pour vérifier si les droits superutilisateur ont été accordés
fun checkRoot(): Boolean {
    return try {
        if (isDeviceRooted()) {
            // Essayer d'exécuter une commande qui nécessite des droits root
            val process = Runtime.getRuntime().exec("su")
            val outputStream = process.outputStream
            val command = "ls /data\n"
            outputStream.write(command.toByteArray())
            outputStream.flush()
            outputStream.close()

            // Lire la sortie pour voir si la commande a réussi
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()
            reader.close()

            // Si la commande réussit, result ne sera pas null
            result != null
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}