package org.zespot.fileserver

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.zespot.fileserver.repository.DataStoreManager
import java.io.File
import java.io.IOException


data class LanguageDictionary(var phrases: Map<String, Map<String, String>>) {
//    var currentLanguage: String = "en" // Langue par défaut
    var currentLanguage ="en"

    fun initLanguage(lang: String) {
        if (phrases.containsKey(lang)) {
            currentLanguage = lang
        } else {
            Log.d("Thierry","initLanguage : Language not found: $lang")
        }
    }

    fun getLocalizedString(key: String, language: String): String {
               val value = phrases[language]?.get(key)
        return if (value != null) {
            value
        } else {
            phrases["English"]?.get(key) ?: "Key not found"
        }
    }

    fun add(newphrases: Map<String, Map<String, String>>) {
        dictionaries.phrases+=     newphrases
    }

    fun getLanguageList(): List<String> {
        return phrases.keys.toList()
    }
}

@Composable
fun getLangue(): String {
    val lanState=DataStoreManager.getLanguage(LocalContext.current).collectAsState(initial = "en")
    return lanState.value
}

//val jsdictionaries = loadDictionariesFromJson( "dictionaries.json")
//val dictionaries = LanguageDictionary(jsdictionaries)
val jsdictionaries = loadDictionariesFromAssets()
val dictionaries = LanguageDictionary(jsdictionaries)

@Composable
fun addCustomLanguages()
{
    // Lecture des langues
    val configDir=File(getRep()+"/.config")
    if (configDir.exists() && configDir.isDirectory) {
        Log.d("Thierry", "Répertoire config présent")
        // Filtrer les fichiers qui commencent par "lang_" et se terminent par ".json"
        if (configDir.listFiles() != null) {
            val files = configDir.listFiles()
                .filter { it.isFile && it.name.startsWith("lang_") && it.name.endsWith(".json") }
                .map { file ->
                    // Extraire la partie * du nom de fichier
                    val prefixLength = "lang_".length
                    val suffixLength = ".json".length
                    val starPart =
                        file.name.substring(prefixLength, file.name.length - suffixLength)
                    Pair(file, starPart)
                }
            files.forEach { (file, starPart) ->
                Log.d("Thierry", "Fichier langue: ${file.name}, Partie *: $starPart")
                val jsonString = file.readText()
                val newdico =
                    Gson().fromJson(jsonString, Map::class.java) as Map<String, Map<String, String>>
                dictionaries.add(newdico)
            }
        }
    }
}

fun loadDictionariesFromJson(fileName: String): Map<String, Map<String, String>> {
    val context= StaticVars.getAppContext()
    val jsonString : String = context.assets.open(fileName).bufferedReader().use { it.readText() }
    val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
    return Gson().fromJson(jsonString, type)
}

fun loadDictionariesFromAssets(): Map<String, Map<String, String>> {
    val context = StaticVars.getAppContext()
    val dictionaries = mutableMapOf<String, Map<String, String>>()

    try {
        // Lister tous les fichiers dans le répertoire assets
        val fileNames = context.assets.list("")?.filter { it.startsWith("lang_") && it.endsWith(".json") } ?: emptyList()

        fileNames.forEach { fileName ->
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
            val languageDicts = Gson().fromJson<Map<String, Map<String, String>>>(jsonString, type)

            // Ajouter chaque dictionnaire de langue à la carte principale
            languageDicts.forEach { (lang, phrases) ->
                dictionaries[lang] = phrases
            }
        }
    } catch (e: IOException) {
        Log.e("Thierry", "Error loading dictionaries from assets", e)
    }

    return dictionaries
}
