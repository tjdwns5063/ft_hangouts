package com.example.ft_hangouts

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.example.ft_hangouts.data.contact_database.ContactDatabase
import java.text.SimpleDateFormat
import java.util.*

class App : Application() {
    private var foregroundActivityName: String? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA)
    private lateinit var dateTime: String

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        ContactDatabase.INSTANCE = ContactDatabase.create(applicationContext)
    }

    private fun updateForeground(foregroundActivityName: String) {
        this.foregroundActivityName = foregroundActivityName
    }

    private fun isForeground(currClassName: String): Boolean {
        return foregroundActivityName == currClassName
    }

    fun showBackgroundTime(context: Context) {
        if (isForeground(context.javaClass.name))
            Toast.makeText(context, dateTime, Toast.LENGTH_SHORT).show()
        else
            updateForeground(context.javaClass.name)
    }

    fun sendResume(currentTime: Long) {
        dateTime = dateFormat.format(currentTime)
    }

    companion object {
        lateinit var INSTANCE: App
    }
}