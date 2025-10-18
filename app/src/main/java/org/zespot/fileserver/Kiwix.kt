package org.zespot.fileserver

import java.io.File


fun StartKiwixServer(rep: String) {
    var command = arrayOf("")
    var context = StaticVars.getAppContext()

    val libPath = context.getApplicationInfo().nativeLibraryDir
    val filemane = "liblaunch-kiwix.so"
    val kiwixPath = libPath + "/" + filemane
    FSLog.log("kiwixPath =" + kiwixPath)
    var kiwixServe = File(kiwixPath)
    var enbableStartKiwix = true

    // Ps d'exécutable Kiwix
    if (!kiwixServe.exists()) {
        enbableStartKiwix = false
        FSLog.log("Pas d'exécutable Kiwix")
    }

    else {
        // Check zim dir exists
        var zimPath = rep + "/.zim/"
        FSLog.log("Zim path=$zimPath")
        var fzimPath = File(zimPath)

        // Pas de répertoire Zim
        if (!fzimPath.exists()) {
            enbableStartKiwix = false
            FSLog.log( "Pas de répertoire Kiwix")
        } else {
            if (File(zimPath).listFiles() == null) {
                FSLog.log("Pas de fichiers zim dans .zim")
                enbableStartKiwix = false
            } else {
                val files = File(zimPath).listFiles()
                if (files.isEmpty()) {
                    enbableStartKiwix = false
                } else {
                    val fileNames = arrayOfNulls<String>(files.size)
                    files?.mapIndexed { index, item ->
                        if (item?.name?.endsWith(".zim") ?: false) {
                            fileNames[index] = zimPath + item.name
                            FSLog.log("item = " + fileNames[index])
                        }
                        command =
                            arrayOf(
                                kiwixPath,
                                "-mb",
                                *fileNames,
                                "-p",
                                "8081",
                                "-z"
                            ) as Array<String>
                    }
                }
            }
        }
    }

        // Tout est OK, lancement de Kiwix
        if (enbableStartKiwix) {
            // Lancement de Kiwix
            enbableStartKiwix = false
            Thread {
                FSLog.log("Lancement de Kiwix")
                try {
                    val process = Runtime.getRuntime().exec(command)
                    val exitCode = process.waitFor()
                    val output = process.inputStream.bufferedReader().readText()
                    val erreur = process.errorStream.bufferedReader().readText()
                    FSLog.log("Kiwix Exit Code : $exitCode")
                    FSLog.log("Kiwix Output: $output")
                    FSLog.log("Kiwix Erreur: $erreur")
                    FSLog.log("Lancement de Kiwix")
                } catch (e: Exception) {
                    e.printStackTrace()
                    FSLog.log("Erreur au lancement de Kiwix : $e")
                }
            }.start()
        }
    }

//    @Composable
//    fun OldStartKiwixServer(langues: MutableState<String>) {
//        var isRunning = rememberSaveable { mutableStateOf(false) }
//        var aide = rememberSaveable { mutableStateOf(false) }
//        var command = arrayOf("")
//        val context = LocalContext.current
//        var hlptxt = dictionaries.getLocalizedString("DICO_menu_kiwix_hlp", langues.value)
//        var buttonText = dictionaries.getLocalizedString("DICO_menu_start_kiwix", langues.value)
//        var enbableStartKiwix = true
//        var btnColors = ButtonDefaults.buttonColors()
//
//        // Check if Kiwix is already runnin
//
//        if (isRunning.value) {
//            buttonText = dictionaries.getLocalizedString("DICO_menu_kiwix_running", langues.value)
//            enbableStartKiwix = false
//            btnColors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF90EE90), // Couleur de fond prédéfinie
//                contentColor = Color.Black // Couleur du texte prédéfinie
//            )
//        } else {
//
//            // Check Kiwix binay is installed
//            val libPath = context.getApplicationInfo().nativeLibraryDir
//            val filemane = "liblaunch-kiwix.so"
//            val kiwixPath = libPath + "/" + filemane
//            FSLog.log("kiwixPath =" + kiwixPath)
//            var kiwixServe = File(kiwixPath)
//            //println("Launch Kiwix, Is " + filemane + "Executable ? : " + kiwixServe.canExecute())
//            if (!kiwixServe.exists()) {
//                enbableStartKiwix = false
//                buttonText =
//                    dictionaries.getLocalizedString("DICO_menu_kiwix_notpresent", langues.value)
//                hlptxt = dictionaries.getLocalizedString(
//                    "DICO_menu_kiwix_not_presnet_hlp",
//                    langues.value
//                )
//            } else {
//                // Check zim dir exists
//                var rep = getRep()
//                //rep = rep.substringBeforeLast('/')
//                var zimPath = rep + "/.zim/"
//                Log.d("Thierry", "Zim path=$zimPath")
//                var fzimPath = File(zimPath)
//                if (!fzimPath.exists()) {
//                    enbableStartKiwix = false
//                    buttonText =
//                        dictionaries.getLocalizedString("DICO_menu_kiwix_nozimdir", langues.value)
//                    hlptxt = dictionaries.getLocalizedString(
//                        "DICO_menu_kiwix_nozimdir_hlp",
//                        langues.value
//                    ) + " (" + zimPath + ")"
//
//                } else {
//
//                    // Check if there is at least one zim file
//                    // Récupération des path des fichiers ZIM
//                    val files = File(zimPath).listFiles()
//                    if (files.isEmpty()) {
//                        enbableStartKiwix = false
//                        buttonText = dictionaries.getLocalizedString(
//                            "DICO_menu_kiwix_nozimfile",
//                            langues.value
//                        )
//                        hlptxt = dictionaries.getLocalizedString(
//                            "DICO_menu_kiwix_nozimfile_hlp",
//                            langues.value
//                        ) + " (" + zimPath + ")"
//                    } else {
//                        val fileNames = arrayOfNulls<String>(files.size)
//                        files?.mapIndexed { index, item ->
//                            if (item?.name?.endsWith(".zim") ?: false) {
//                                fileNames[index] = zimPath + item.name
//                                // Log.d("Thierry", "item = " + fileNames[index])
//                            }
//                            command = arrayOf(
//                                kiwixPath,
//                                "-mb",
//                                *fileNames,
//                                "-p",
//                                "8081",
//                                "-z"
//                            ) as Array<String>
//                        }
//                    }
//                }
//            }
//        }
//
//
//        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//            if (enbableStartKiwix) {
//                BoutonVertFonce(modifier = Modifier.weight(9f), buttonText, onClick = {
//                    // Lancement de Kiwix
//                    isRunning.value = true
//                    enbableStartKiwix = false
//                    Thread {
//                        Log.d("Thierry", "Lancement de Kiwix")
//                        try {
//                            val process = Runtime.getRuntime().exec(command)
//                            val exitCode = process.waitFor()
//                            val output = process.inputStream.bufferedReader().readText()
//                            val erreur = process.errorStream.bufferedReader().readText()
//                            println("Exit Code : $exitCode")
//                            println("Output: $output")
//                            println("Erreur: $erreur")
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            println("Erreur au lancement de Kiwix : $e")
//                        }
//                    }.start()
//                })
//            } else if (isRunning.value) BoutonVertPale(
//                modifier = Modifier.weight(9f),
//                buttonText,
//                onClick = {})
//            else BoutonGris(modifier = Modifier.weight(9f), buttonText, onClick = {})
//
//            Spacer(Modifier.width(4.dp))
//            BoutonAide(modifier = Modifier.weight(1f), if (aide.value) "X" else "?", onClick = {
//                aide.value = !aide.value
//            })
//        }
//        if (aide.value)
//            Text(hlptxt)
//    }
