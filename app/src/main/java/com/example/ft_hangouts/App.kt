package com.example.ft_hangouts

import android.app.Application
import android.content.Context
import android.os.Handler
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class App : Application() {
    private var foregroundActivityName: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA)
    private lateinit var dateTime: String

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun updateForeground(foreground: String) {
        foregroundActivityName = foreground
    }

    private fun isForeground(currClassName: String): Boolean {
        return foregroundActivityName == currClassName
    }

    fun showBackgroundTime(currClassName: String) {
        if (isForeground(currClassName))
            Toast.makeText(INSTANCE.applicationContext, dateTime, Toast.LENGTH_SHORT).show()
        else
            updateForeground(currClassName)
    }

    fun sendResume(currentTime: Long) {
        dateTime = dateFormat.format(currentTime)
    }

    companion object {
        lateinit var INSTANCE: App
    }
}

object BackgroundHelper {
    private val executor: ExecutorService = Executors.newFixedThreadPool(8)
    fun <T> execute(callable: Callable<T>) {
        executor.execute {
            callable.call()
        }
    }
}