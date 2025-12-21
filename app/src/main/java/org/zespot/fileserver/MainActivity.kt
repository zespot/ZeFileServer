package org.zespot.fileserver


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import checkRoot
import isDeviceRooted
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.zespot.fileserver.repository.DataStoreManager
import org.zespot.fileserver.repository.DataStoreManager.getLanguage
import org.zespot.fileserver.room.ListLogsViewModel
import org.zespot.fileserver.room.Logs
import org.zespot.fileserver.ui.theme.Affiche_t1
import org.zespot.fileserver.ui.theme.ZeFileServerTheme
import org.zespot.fileserver.ui.theme.BoutonAide
import org.zespot.fileserver.ui.theme.BoutonContact
import org.zespot.fileserver.ui.theme.BoutonDebug
import org.zespot.fileserver.ui.theme.BoutonIsStarted
import org.zespot.fileserver.ui.theme.BoutonLaunch
import org.zespot.fileserver.ui.theme.BoutonStart
import org.zespot.fileserver.ui.theme.BoutonStatus
import org.zespot.fileserver.ui.theme.BoutonStop
import org.zespot.fileserver.ui.theme.BoutonVertRouge
import org.zespot.fileserver.ui.theme.Titre
import org.zespot.fileserver.ui.theme.TopBar
import org.zespot.fileserver.ui.theme.ligne
import requestRoot
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Date
import kotlin.system.exitProcess

// Test 2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this
        enableEdgeToEdge()
        StaticVars.initialize(this)


        if (isDeviceRooted() ) {
            StaticVars.setIsRooted(true)
            requestRoot()
            Log.d("Thierry","Device  isRooted"+ StaticVars.getIsRooted())
        }

        droitsFS(this)


            setContent {
                initRep(this)
                addCustomLanguages()
                //initRep(this)

                // Permet de sauver le choix du DarkTheme
                var isDarkThemeActivated by rememberSaveable {
                    mutableStateOf(false)
                }
                var debugMod by rememberSaveable {
                    mutableStateOf(false)
                }
                LaunchedEffect(Unit) {
                    DataStoreManager.isDarkThemeActivited(activity).collect {
                        isDarkThemeActivated = it
                    }
                }

                println("DarkMod=" + isDarkThemeActivated)

                ZeFileServerTheme(darkTheme = isDarkThemeActivated) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp, 18.dp),
                    ) { innerPadding ->
                        FileServer(
                            modifier = Modifier.padding(innerPadding),
                            isDarkThemeActivated = isDarkThemeActivated,
                            onDarkThemeToggle = { isActivated: Boolean ->
                                lifecycleScope.launch {
                                    DataStoreManager.setDarkThemeActivated(activity, isActivated)
                                }
                            },
                            debugMod = debugMod,
                            debugModToggle = { isActivated: Boolean ->
                                debugMod = !debugMod

                            },
                        )
                    }
                }
            }
        }

    @Composable
    fun FileServer(
        modifier: Modifier = Modifier,
        isDarkThemeActivated: Boolean,
        onDarkThemeToggle: (Boolean) -> Unit,
        navController: NavHostController = rememberNavController(),
        //cguOk: ManagePreferencesViewModel = viewModel(factory = ManagePreferencesViewModel.Factory),
        debugMod: Boolean,
        debugModToggle: (Boolean) -> Unit )
    {
        val context = LocalContext.current

        // Lecture de la lange dans le DataStore, enregistrement dans la DataClass
        LaunchedEffect(Unit) {
            val savedLanguage = getLanguage(context)
            Log.d("Thierry", "Fileserver Lecture langue dans DataStore ="+savedLanguage)
            dictionaries.initLanguage(savedLanguage.toString())
        }

        var isCguValidated by rememberSaveable { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            DataStoreManager.isCguValidated(context).collect {
                isCguValidated = it
                Log.d("Thierry", "Fileserver isCguValidated="+isCguValidated)
            }
        }

        if (  isCguValidated == false) {
            ValCGU()
        } else {
            Scaffold(
                topBar = {
                    TopBar(
                        navController = navController,
                        isDarkThemeActivated,
                        onDarkThemeToggle,
                        debugMod,
                        debugModToggle
                    )
                }) { innerPadding ->

                MainMenu(debugMod, Modifier.padding(innerPadding)  )
            }
        }
    }

    @Composable
    fun MainMenu(debugMod: Boolean, modifier: Modifier) {
        // Lecture de la langue dans la DataClass


        val langues = remember { mutableStateOf("English") }
        langues.value=getLangue()
        val langue = langues.value
        dictionaries.initLanguage(langue)
        Log.d("Thierry", "LANGUE : "+langue)
        Log.d("Thierry", "Titre  ="+dictionaries.getLocalizedString(langue, "DICO_titre"))
        Log.d("Thierry", "Change Dir   ="+dictionaries.getLocalizedString(langue, "DICO_menu_chg_dir"))


        var isRootGranted by remember { mutableStateOf(false) }
        if (isDeviceRooted() ) {
            StaticVars.setIsRooted(true)
            if (checkRoot()) {
                isRootGranted = true
                StaticVars.setIsRootAquired(true)
                val rootedport = "80"
                StaticVars.setPort(rootedport)
                Log.d("Thierry", "Device rooted, change port, new port is $rootedport")
            }
        }
        Log.d("Thierry","Device  isRooted"+ StaticVars.getIsRooted())
        Log.d("Thierry","Device  isRootAcquired = "+ StaticVars.getIsRootAquired())

        Addlogs("Mainenu")

        Column(modifier=modifier.padding(10.dp)) {

            Spacer(Modifier.height(12.dp))
            StartServer(langues)

            Spacer(Modifier.height(12.dp))
            Status(langues)

            connect2server(langues)

            Spacer(Modifier.height(12.dp))
            exitApp(langues)

            Spacer(Modifier.height(20.dp))
            ligne()

            Spacer(Modifier.height(12.dp))
            SelectRep(langues)

            GrandAccess(langues)

//            Spacer(Modifier.height(12.dp))
//            StartKiwixServer(langues)

            Spacer(Modifier.height(12.dp))
            var about = rememberSaveable { mutableStateOf(false) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showDebug by remember { mutableStateOf(false) }
                BoutonDebug(
                    modifier = Modifier.weight(9f),
                    dictionaries.getLocalizedString("DICO_menu_debug", langues.value),
                    { showDebug = true })
                if (showDebug) {
                    debugMod(onDismiss = { showDebug = false })
                }

                Spacer(Modifier.width(10.dp))
                    BoutonContact(
                        modifier = Modifier.weight(9f),
                        dictionaries.getLocalizedString("DICO_menu_about", langues.value),
                        onClick = { about.value =! about.value  })
            }
            if (about.value) {
                Text(dictionaries.getLocalizedString("DICO_menu_about_content", langues.value))
            }
        }
    }

    @Composable
    fun Status(langues: MutableState<String>) {
        var shoip = rememberSaveable { mutableStateOf(false) }
        var aide = rememberSaveable { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BoutonStatus (modifier = Modifier.weight(9f),
                dictionaries.getLocalizedString("DICO_menu_showip", langues.value),
                onClick = {
                shoip.value = !shoip.value
            })
            Spacer(Modifier.width(4.dp))
            BoutonAide (modifier = Modifier.weight(1f), if (aide.value)"X" else "?", onClick = {
                aide.value=!aide.value
            })
        }
        if (aide.value)
            Text(dictionaries.getLocalizedString("DICO_menu_showip_hlp", langues.value))
        if (shoip.value) ShowIP(langues)
    }

    @Composable
    fun ShowIP(langues: MutableState<String>) {
        val isHotspot = isHotspotEnabled(LocalContext.current)
        val isWifi = isWifi(LocalContext.current)
        Column  (modifier = Modifier
            .fillMaxSize()
            .padding(0.dp, 18.dp)
            .verticalScroll(rememberScrollState())) {

            if (isHotspot) {
                Text(
                    dictionaries.getLocalizedString("DICO_showip_AP", langues.value)
                           // + WlanIpAddress(LocalContext.current )
                            + getHotspotIpAddress()+":"
                            + StaticVars.getPort()
                )
            } else {
                Text(
                    dictionaries.getLocalizedString("DICO_showip_nothing", langues.value)
                )
            }
            if (isWifi) {
                Text("\n"+
                    dictionaries.getLocalizedString("DICO_showip_wifi", langues.value)
                            // + getLocalIpAddress(LocalContext.current)
                            + getWifiIpAddress(LocalContext.current)+":"
                            + StaticVars.getPort()
                )
            }
            Text(
                dictionaries.getLocalizedString("DICO_showip_local", langues.value)+" 127.0.0.1:"
                        +StaticVars.getPort()+"  or localhost:"+StaticVars.getPort()
            )
        }
    }

    @Composable
    fun ShowLogs(listLogsViewModel: ListLogsViewModel = viewModel(factory = ListLogsViewModel.Factory)) {
        val logs by listLogsViewModel.logs
        Addlogs("ShowLogs")
        listLogsViewModel.getLogs()
        LazyColumn(Modifier) {
            items(logs) { logs ->
                Text("${logs.date} ; ${logs.action}")
            }
        }
    }

    @Composable
    fun ValCGU() {
        var isCguValidated by rememberSaveable { mutableStateOf(false) }

        val context = LocalContext.current
        val cgu = context.getString(R.string.cgu)
        val titre = context.getString(R.string.titre)
        LaunchedEffect(Unit) {
            DataStoreManager.isCguValidated(context).collect {
                isCguValidated = it
                Log.d("Thierry", "Fileserver isCguValidated="+isCguValidated)
            }
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(12.dp, 15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            )
            {
                Titre(Modifier )
                Spacer(modifier = Modifier.height(20.dp))
                Affiche_t1("Accept General Conditions")
                Spacer(modifier = Modifier.height(10.dp))

                Text(text = cgu)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    //modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(text = "I agree", color = Color.Blue, textAlign = TextAlign.Right)

                    var boxState: Boolean by remember { mutableStateOf(false) }
                    Checkbox(checked = boxState ?: false, onCheckedChange = {
                        boxState = !boxState
                    })

                    Button(enabled = boxState ?: false, onClick = {
                        lifecycleScope.launch {
                            DataStoreManager.setCguValidated(context, boxState)
                        }

                    }) {
                        Text("Validate")
                    }

                    Button(onClick = {
                        System.exit(0)
                    }) {
                        Text("Exit")
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Composable
    fun Addlogs(
        action: String,
        listLogsViewModel: ListLogsViewModel = viewModel(factory = ListLogsViewModel.Factory)
    ) {
        val sdf = java.text.SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        listLogsViewModel.add(Logs(0L, currentDate, action))
    }

    private fun copyAssetFolder(
        assetManager: AssetManager,
        fromAssetPath: String,
        toPath: String
    ): Boolean {
        return try {
            val files = assetManager.list(fromAssetPath) ?: emptyArray()
            var res = true

            if (files.isEmpty()) {
                // If it's a file, it won't have any assets "inside" it.
                res = res and copyAsset(assetManager, fromAssetPath, toPath)
            } else {
                File(toPath).mkdirs()
                for (file in files) {
                    res = res and copyAssetFolder(
                        assetManager,
                        "$fromAssetPath/$file",
                        "$toPath/$file"
                    )
                }
            }
            res
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun deleteFolderRecursively(file: File): Boolean {
        return try {
            var res = true
            for (childFile in file.listFiles() ?: emptyArray()) {
                res = res and if (childFile.isDirectory) {
                    deleteFolderRecursively(childFile)
                } else {
                    childFile.delete()
                }
            }
            res and file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    private fun copyAsset(
        assetManager: AssetManager,
        fromAssetPath: String,
        toPath: String
    ): Boolean {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        return try {
            `in` = assetManager.open(fromAssetPath)
            File(toPath).createNewFile()
            out = FileOutputStream(toPath)
            copyFile(`in`, out)
            `in`.close()
            `in` = null
            out.flush()
            out.close()
            out = null
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    @Composable
    fun StartServer(langues: MutableState<String>) {
        var isRunning = rememberSaveable { mutableStateOf(false) }
        var aide = rememberSaveable { mutableStateOf(false) }
        val rep = getRep()
        val fdrep = File(rep)
        val context =LocalContext.current
        // Démarrage de NodeJS
        var hlptxt =dictionaries.getLocalizedString("DICO_menu_fs_hlp", langues.value)


        Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
            if (isRunning.value) {
                BoutonIsStarted  (modifier = Modifier.weight(9f),
                    text=dictionaries.getLocalizedString("DICO_menu_fs_running", langues.value),
                    onClick = {})
                    // isRunning.value=false

            } else if (!fdrep.exists()) {
                BoutonStart(Modifier.weight(9f),
                    dictionaries.getLocalizedString("DICO_menu_fs_repnotexists", langues.value ),
                    {})
                hlptxt=dictionaries.getLocalizedString("DICO_menu_fs_repnotexists_hlp", langues.value )
            }
            else
            {
                BoutonStart (
                    modifier = Modifier.weight(9f),
                    dictionaries.getLocalizedString("DICO_menu_start", langues.value),
                    onClick = {
                        isRunning.value = true
                        StaticVars.setIsRunning(true)

                        // Lancement de JS node
                        // ===================================

                        Log.d("NODEJS","1. Start Node , Check Root  = "+checkRoot()+ "\nport="+ StaticVars.getPort())


                        Log.d("NODEJS","1. Lancement de JS node")
                                val nodeDir =
                                    "${applicationContext.filesDir.absolutePath}/nodejs-project"
                                val nodeDirReference = File(nodeDir)
                                if (nodeDirReference.exists()) {
                                    deleteFolderRecursively(File(nodeDir))
                                }
                                // Copy the node project from assets into the application's data path.
                                copyAssetFolder(assets, "nodejs-project", nodeDir)
                                val libPath = context.getApplicationInfo().nativeLibraryDir


                                var command = arrayOf("")
                                val filemane = "liblaunch-node.so"
                                val nodePath = libPath + "/" + filemane

                                if (StaticVars.getIsRooted()) {
                                    Log.d("NODEJS","2. Lancement de JS node ROOT")
                                    command =
                                        arrayOf(
                                            "su",
                                            "-c",
                                            "$nodePath $nodeDir/http-server/bin/http-server $rep --no-dotfiles --port 80"
                                        ) as Array<String>
                                }
                                else {
                                    Log.d("NODEJS","3. Lancement de JS node pas ROOT")
                                    command =
                                        arrayOf(
                                            nodePath,
                                           "$nodeDir/http-server/bin/http-server",
                                           // Page version "$nodeDir/main.js",
                                            rep,
                                            "--no-dotfiles",
                                            "--port",
                                            StaticVars.getPort()
                                        ) as Array<String>
                                }
                        Thread {
                                val process = Runtime.getRuntime().exec(command, )
                                        val exitCode = process.waitFor()
                                        val output = process.inputStream.bufferedReader().readText()
                                        val erreur = process.errorStream.bufferedReader().readText()
                                    Log.d("NODEJS", "Node lancé root avec le PID=$process")
                            Log.d("NODEJS","Node Root Exit Code : $exitCode")
                            Log.d("NODEJS","Node Output: $output")
                            Log.d("NODEJS","Node Erreur: $erreur")
                            Log.d("NODEJS","Lancement de NOde")
                            }.start()
                        StartKiwixServer(rep)
                })
            }
            Spacer(Modifier.width(4.dp))
            BoutonAide (modifier = Modifier.weight(1f), if (aide.value)"X" else "?", onClick = {
                aide.value=!aide.value
            })
        }
        if (aide.value)
             Text(hlptxt)
    }


    @Composable
    fun connect2server(langues: MutableState<String>){
        var aide = rememberSaveable { mutableStateOf(false) }
        val context = LocalContext.current
        val isRunning = remember { StaticVars.isRunning }
        Log.d("Thierry","connect2server isRuuning = $isRunning" )
        if (isRunning.value) {
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BoutonLaunch (modifier = Modifier.weight(9f),
                dictionaries.getLocalizedString("DICO_menu_launch", langues.value),
                onClick = {
                    try {
                        val serverPort= StaticVars.getPort()
                    val intent = Intent(Intent.ACTION_VIEW, ("http://127.0.0.1:$serverPort").toUri())
                    if (intent.resolveActivity(context.packageManager) != null) {
                        val bundle = Bundle()
                        ContextCompat.startActivity(context, intent, bundle)
                    } } catch (e : Exception) {
                        FSLog.log("Erreur lancement ouverture url $e" )
                    }
                })

            Spacer(Modifier.width(4.dp))
            OutlinedButton(    contentPadding = PaddingValues(0.dp), modifier = Modifier.weight(1f),
                onClick = {
                    aide.value=!aide.value
                })
            {
                Text(if (aide.value)"X" else "?")
            }
        }
        if (aide.value)
            Text(dictionaries.getLocalizedString("DICO_menu_launch_hlp", langues.value))
    }
    }


    @Composable
    fun exitApp(langues: MutableState<String>){
        var aide = rememberSaveable { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BoutonStop (modifier = Modifier.weight(9f),
                dictionaries.getLocalizedString("DICO_menu_exit", langues.value),
                onClick = {
                    if (StaticVars.getIsRootAquired()) {
                        var command = arrayOf("")
                        val killnode= "pids=\$(ps -ef | grep '[n]ode' | sed 's/^[^ ]*//' | sed 's/^ *//' | sed 's/ .*//');for pid in \$pids; do su -c kill -9  \$pid; done"
                        command =
                            arrayOf(
                                "su",
                                "-c",
                                killnode
                            ) as Array<String>

                            try {
                                //  val command = "su -c \"pids=\\$(ps -ef | grep '[n]ode' | awk '{print \\$2}'); for pid in \\$pids; do kill -9 \\$pid; done\""
                                val process = Runtime.getRuntime().exec(command, )
                                val exitCode = process.waitFor()
                                val output = process.inputStream.bufferedReader().readText()
                                val erreur = process.errorStream.bufferedReader().readText()
                                Log.d("KILLNODE", "Killnode=$process")
                                Log.d("KILLNODE","Killnode: $exitCode")
                                Log.d("KILLNODE","Killnode $output")
                                Log.d("KILLNODE","Killnode Erreur: $erreur")
                                Log.d("KILLNODE","Node tué")
                                runBlocking {
                                    delay(500) // 1000 millisecondes = 1 seconde
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    exitProcess(0) })

            Spacer(Modifier.width(4.dp))
            OutlinedButton(    contentPadding = PaddingValues(0.dp), modifier = Modifier.weight(1f),
                onClick = {
                    aide.value=!aide.value
                })
            {
                 Text(if (aide.value)"X" else "?")
            }
        }
        if (aide.value)
            Text(dictionaries.getLocalizedString("DICO_menu_exit_hlp", langues.value))
    }
}

@Composable
fun GrandAccess(langues: MutableState<String>) {
    var askDroits: Boolean by remember { mutableStateOf(false) }
    var aide = rememberSaveable { mutableStateOf(false) }
    var droitsOk = false
    var hlptxt=dictionaries.getLocalizedString("DICO_grant_done_hlp", langues.value )
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) droitsOk = true
    else droitsOk = Environment.isExternalStorageManager()
    if (!droitsOk) {
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BoutonVertRouge(
                modifier = Modifier.weight(9f),
                dictionaries.getLocalizedString("DICO_grant_todo", langues.value),
                onClick = { askDroits = true })
            hlptxt = dictionaries.getLocalizedString("DICO_grant_done", langues.value)

//        } else {
//            BoutonVertPale(Modifier.weight(9f),dictionaries.getLocalizedString("DICO_grant_done", langues.value ),{})
//        }
            Spacer(Modifier.width(4.dp))
            OutlinedButton(
                contentPadding = PaddingValues(0.dp), modifier = Modifier.weight(1f),
                onClick = { aide.value = !aide.value })
            {
                Text(if (aide.value) "X" else "?")
            }

        }
        if (aide.value)
            Text(hlptxt)
        if (askDroits) {
            askDroits = false;droitsFS(context = LocalContext.current)
        }
    }
}


fun droitsFS(context: Context) {
    val REQUEST_CODE_READ_PERMISSION = 1001
    val activity = context as Activity

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_PERMISSION
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val uri = Uri.fromParts("package", context.packageName, null)
            Log.d("Thierry", "Request ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = uri
            context.startActivity(intent)
        }
    }

    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_CODE_READ_PERMISSION
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val uri = Uri.fromParts("package", context.packageName, null)
            Log.d("Thierry", "Request ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION")
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = uri
            context.startActivity(intent)
        }
    }

}

