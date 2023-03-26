package com.example.ft_hangouts.database

import com.example.ft_hangouts.App
import java.util.concurrent.Callable
import java.util.concurrent.Executors

private lateinit var INSTANCE: ContactHelper

private val executor = Executors.newFixedThreadPool(8)
fun createDatabase(): ContactHelper {
    synchronized(ContactHelper::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = ContactHelper(App.INSTANCE)
        }
    }
    return INSTANCE
}

fun getAllItems(): List<Contact>? {
    val callable = Callable {
        INSTANCE.getAllItems()
    }
    val future = executor.submit(callable)
    return try {
        future.get()
    } catch (e: Exception) {
        null
    }
}

fun getItemById(rowId: Long): Contact? {
    val callable = Callable {
        INSTANCE.getItemById(rowId)
    }
    val future = executor.submit(callable)
    return try {
        future.get()
    } catch (e: Exception) {
        null
    }
}

fun addItem(contact: Contact): Long? {
    val callable = Callable {
        INSTANCE.addItem(contact)
    }
    val future = executor.submit(callable)
    return try {
        future.get()
    } catch (e: Exception) {
        null
    }
}

