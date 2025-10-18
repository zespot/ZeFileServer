package org.zespot.fileserver

import android.content.Context
import androidx.compose.runtime.mutableStateOf

///var serverPort  = 8085


object StaticVars {
    // Variables privées
    private var port: String = "8080"
    private var isRooted: Boolean=false
    private var isRootAquired: Boolean=false
    private var language: String = "English"
    private var sharedDirectory: String = ""
    private var isNodeRunning: Boolean = false
    private var isKiwixRunning: Boolean = false
    private lateinit var appContext: Context


    // Getters
    fun getPort(): String = port
    fun getIsRooted(): Boolean = isRooted
    fun getIsRootAquired(): Boolean = isRootAquired
    fun getLanguage(): String = language
    fun getSharedDirectory(): String = sharedDirectory
    fun getIsNodeRunning(): Boolean = isNodeRunning
    fun getIsKiwixRunning(): Boolean = isKiwixRunning
    fun getAppContext(): Context = appContext

    val isRunning = mutableStateOf(false)
    fun getIsRunning(): Boolean = isRunning.value
    fun setIsRunning(isNd: Boolean) {
        isRunning.value = isNd
    }
    // Setters
    fun setPort(newport: String) {
        this.port = newport
    }
    fun setIsRooted(isRooted: Boolean) {
        this.isRooted = isRooted
    }
    fun setIsRootAquired(isRootAquired: Boolean) {
        this.isRootAquired = isRooted
    }
    fun setLanguage(language: String) {
        this.language = language
    }

    fun setSharedDirectory(sharedDir: String) {
        this.sharedDirectory = sharedDir
    }

    fun setIsKiwixRunning(isKw: Boolean) {
        this.isKiwixRunning = isKw
    }

    fun setIsNodeRunning(isNd: Boolean) {
        this.isNodeRunning = isNd
    }

    fun initialize(context: Context) {
        this.appContext = context.applicationContext
    }
}