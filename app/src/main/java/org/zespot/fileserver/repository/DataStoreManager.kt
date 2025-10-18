package org.zespot.fileserver.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreManager {

    private val CGU_VALIDATED = booleanPreferencesKey(name = "cgu_validated")
    private val DARK_THEME_KEY = booleanPreferencesKey(name = "dark_theme")
    private val SHARED_REP_KEY = stringPreferencesKey(name = "shared_rep")
    private val LANGUAGE_KEY = stringPreferencesKey(name = "language")

    fun getSharedRep(context: Context): Flow<String> {
        val rep=context.dataStore.data.map { preferences ->
            preferences[SHARED_REP_KEY] ?: "/sdcard/fileserverdir"}
        //Log.d("Thierry", "getSharedRep  Rep = "+rep)
        return context.dataStore.data.map { preferences ->
            preferences[SHARED_REP_KEY] ?: "/sdcard/fileserverdir"
        }
    }
    suspend fun setSharedRep(context: Context, rep: String?) {
        Log.d("Thierry", "setSharedRep Enregistrement Rep = "+rep)
        context.dataStore.edit { preferences ->
            if (rep != null) preferences[SHARED_REP_KEY] = rep
        }
    }

     fun getLanguage(context: Context): Flow<String>  {
        val language=context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"}
        //Log.d("Thierry", "getLanguage   = "+language)
        return context.dataStore.data.map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }
    }

    suspend fun setLanguage(context: Context, language: String?) {
        Log.d("Thierry", "setLanguage Enregistrement langue = "+language)
        context.dataStore.edit { preferences ->
            if (language != null) preferences[LANGUAGE_KEY] = language
        }
    }


    fun isDarkThemeActivited(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }
    }

    suspend fun setDarkThemeActivated(context: Context, isActivated: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = isActivated
        }
    }
    fun isCguValidated(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[CGU_VALIDATED] ?: false
        }
    }

    suspend fun setCguValidated(context: Context, isActivated: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[CGU_VALIDATED] = isActivated
        }
    }


}