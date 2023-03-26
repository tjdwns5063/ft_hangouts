package com.example.ft_hangouts.database

import com.example.ft_hangouts.App

private lateinit var INSTANCE: ContactHelper

fun createDatabase(): ContactHelper {
    synchronized(ContactHelper::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = ContactHelper(App.INSTANCE)
        }
    }
    return INSTANCE
}
