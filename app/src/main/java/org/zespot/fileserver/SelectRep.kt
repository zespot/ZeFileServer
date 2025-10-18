package org.zespot.fileserver

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.zespot.fileserver.repository.DataStoreManager
import org.zespot.fileserver.ui.theme.Blanc
import org.zespot.fileserver.ui.theme.BoutonAide
import org.zespot.fileserver.ui.theme.BoutonRep
import java.io.File
import kotlin.system.exitProcess


@Composable
fun getRep(): String {
    val repState=DataStoreManager.getSharedRep(LocalContext.current).collectAsState(initial = "")
    return repState.value
}

// https://hahouari.medium.com/storage-access-framework-saf-in-jetpack-compose-69b440cef8fa
    @Composable
    fun SelectRep(langues: MutableState<String>) {
    var curentRep by rememberSaveable {  mutableStateOf("")  }
    var rep = remember { mutableStateOf<String?>("") }
    val context = LocalContext.current
    var newdir=""

    val activity = context as Activity
    val isDirectoryPicked = remember { mutableStateOf(false) }
    val aide = remember { mutableStateOf(false) }
    val dirPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { maybeUri ->
            maybeUri?.let { uri ->
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                if (checkUriPersisted(context.contentResolver, uri)) {
                    context.contentResolver.releasePersistableUriPermission(uri, flags)
                }
                context.contentResolver.takePersistableUriPermission(uri, flags)
                // Do Something
                isDirectoryPicked.value = true
                rep.value = getPathFromUri(uri)
                newdir=getPathFromUri(uri)?:""
                FSLog.log("Newdir=$newdir")
                //GlobalScope.launch {
                  runBlocking {
                    DataStoreManager.setSharedRep(context, rep.value)
                }
            }
        }
    )

    val repState = DataStoreManager.getSharedRep(context).collectAsState(initial = "")
     curentRep = repState.value


    Surface(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        //border = BorderStroke(1.dp, GrisClair),
        border = BorderStroke(1.dp, Blanc),
        color= Blanc
        ) {
        Column(
        //    modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Assure que les éléments sont placés de haut en bas
        ) {
            Text(
                dictionaries.getLocalizedString(
                    "DICO_menu_current_dir",
                    langues.value
                ) + curentRep ,
                modifier = Modifier.wrapContentSize(Alignment.Center)
                //,         fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoutonRep (
                    modifier = Modifier.weight(9f),
                    dictionaries.getLocalizedString("DICO_menu_chg_dir", langues.value),
                    onClick = { dirPickerLauncher.launch(Uri.EMPTY) })
                Spacer(Modifier.width(4.dp))
                BoutonAide(modifier = Modifier.weight(1f), if (aide.value) "X" else "?", onClick = {
                    aide.value = !aide.value
                })
            }

            if (aide.value)
                Text(dictionaries.getLocalizedString("DICO_menu_chg_dir_hlp", langues.value))

            if (isDirectoryPicked.value) {

                val intent = Intent(activity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val bundle = Bundle()
            runBlocking {
            delay(500) // 1000 millisecondes = 1 seconde
             }
                startActivity(context, intent, bundle)
                exitProcess(0)
            }
        }
    }
}

    fun checkUriPersisted(contentResolver: ContentResolver, uri: Uri): Boolean {
        return contentResolver.persistedUriPermissions.any { perm -> perm.uri == uri }
    }

    fun getPathFromUri(uri: Uri): String? {
        val path = uri.path ?: return null
        println("Uri path: $path")

        return when {
            path.startsWith("/tree/primary:") -> {
                "/sdcard/" + path.removePrefix("/tree/primary:")
            }

            path.matches(Regex("/tree/[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}:.*")) -> {
                val cleanedPath = path.removePrefix("/tree/")
                val parts = cleanedPath.split(":")
                if (parts.size >= 2) {
                    val sdCardId = parts[0]
                    val directoryPath = parts[1]
                    "/storage/$sdCardId/$directoryPath"
                } else {
                    null
                }
            }
            else -> null
        }
    }

// Initialise le répertoire par défaut, si celui qui est stocké n'existe pas
// le remplace par fileserverdir s'il est trouvé
@Composable
fun initRep(context: Context) {

    var hasBeeInit by rememberSaveable {  mutableStateOf(false)  }
    var curentRep by rememberSaveable {  mutableStateOf("")  }

    val hasBeeInit2 = remember { mutableStateOf(false) }
    if (hasBeeInit) return

    curentRep = DataStoreManager.getSharedRep(context).collectAsState(initial = "initrep").value
    if (curentRep!="initrep") {

        //var curentRep = repState.value
        FSLog.log("intRep() - curentRep=" + curentRep)
        val curentRepFd = File(curentRep)
        if (!(curentRepFd.exists() && curentRepFd.isDirectory)) {
            var searchedRep = searchForDirectory(context)
            FSLog.log("Searched REP=" + searchedRep)
            if (searchedRep != null) {
                curentRep = searchedRep
                LaunchedEffect(Unit) {
                    DataStoreManager.setSharedRep(context, curentRep)
                }
            }
        }
        hasBeeInit = true
    }
}

fun findDirectory(path: File?, currentDepth: Int = 0, maxDepth: Int = 3): String? {
    if (path == null || currentDepth > maxDepth) {
        return null
    }
    val targetDirName = "fileserverdir"
    try {
        val files = path.listFiles()
        files?.forEach { file ->
            if (file.isDirectory) {
                if (file.name.equals(targetDirName, ignoreCase = true)) {
                    Log.d("Thierry", "Directory found: ${file.absolutePath}")
                    return file.absolutePath
                }
                // Recherche récursive
                if (currentDepth < maxDepth) {
                    val foundPath = findDirectory(file, currentDepth + 1, maxDepth)
                    if (foundPath != null) {
                        return foundPath
                    }
                }
            }
        }
    } catch (e: SecurityException) {
        Log.e("Thierry", "Security exception: ${e.message}")
        e.printStackTrace()
    }
    return null
}

@Composable
fun searchForDirectory(context: Context): String? {
    // Obtenez les chemins des fichiers externes
    val externalStorageDirs = context.getExternalFilesDirs(null)
    for (dir in externalStorageDirs) {
        dir?.let {
            var currentPath = it
            // Remontez jusqu'à la racine du stockage
            while (currentPath != null) {
                    val result = findDirectory(currentPath)
                    if (result != null) {
                        return result
                }
                currentPath = currentPath.parentFile
            }
        }
    }
    // Si non trouvé sur les stockages externes, recherche sur le stockage interne
    val internalStorage = Environment.getDataDirectory().parentFile
    return findDirectory(internalStorage)
}