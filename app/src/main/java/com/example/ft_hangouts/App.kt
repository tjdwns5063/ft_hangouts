package com.example.ft_hangouts

import android.app.Application
import android.content.Context
import com.example.ft_hangouts.database.ContactHelper

class App : Application() {

    init {
        INSTANCE = this
        contactDbHelper = ContactHelper(INSTANCE)
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        lateinit var INSTANCE: App
        lateinit var contactDbHelper: ContactHelper
    }
}